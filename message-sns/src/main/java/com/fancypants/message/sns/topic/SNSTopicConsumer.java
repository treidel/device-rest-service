package com.fancypants.message.sns.topic;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AbortedException;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.util.json.JSONObject;
import com.fancypants.message.exception.AbstractMessageException;
import com.fancypants.message.topic.TopicConsumer;

public class SNSTopicConsumer implements TopicConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(SNSTopicConsumer.class);
	private static final int AMAZON_SQS_WAIT_TIME_IN_SECS = 20;
	private static final int AMAZON_SQS_MAX_MESSAGES_RX = 10;

	private final AmazonSNSClient snsClient;
	private final AmazonSQSClient sqsClient;
	private final String subscriptionARN;
	private final String sqsURL;
	private final Handler handler;
	private final Thread thread;
	private boolean running = false;

	public SNSTopicConsumer(AmazonSNSClient snsClient, AmazonSQSClient sqsClient, String subscriptionARN, String sqsURL,
			Handler handler) {
		LOG.trace("SNSTopicConsumer enter {}={} {}={} {}={} {}={} {}={}", "snsClient", snsClient, "sqsClient",
				sqsClient, "subscriptionARN", subscriptionARN, "sqsURL", sqsURL, "handler", handler);
		this.snsClient = snsClient;
		this.sqsClient = sqsClient;
		this.subscriptionARN = subscriptionARN;
		this.sqsURL = sqsURL;
		this.handler = handler;
		// create the handler thread
		this.thread = new Thread(new ReceiverThreadHandler(), sqsURL);
		LOG.trace("SNSTopicConsumer exit");
	}

	public void start() throws AbstractMessageException {
		LOG.trace("start enter");
		// set to running
		this.running = true;
		// start the thread
		this.thread.start();
		LOG.trace("start exit");
	}

	@Override
	public void close() {
		LOG.trace("close enter");
		// tell the looper we're done
		running = false;
		try {
			// first delete the subscription
			snsClient.unsubscribe(this.subscriptionARN);
		} catch (Exception e) {
			LOG.error("exception={}", e);
		}
		try {
			// interrupt the thread
			this.thread.interrupt();
			// wait for the thread to exit
			this.thread.join();
		} catch (InterruptedException e) {
			LOG.error("join={}", e);
		}
		try {
			// now delete the queue
			sqsClient.deleteQueue(sqsURL);
		} catch (Exception e) {
			LOG.error("exception={}", e);
		}
		LOG.trace("close exit");
	}

	private class ReceiverThreadHandler implements Runnable {

		@Override
		public void run() {
			LOG.trace("ReceiverThreadHandler.run enter");
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqsURL);
			receiveMessageRequest.setWaitTimeSeconds(AMAZON_SQS_WAIT_TIME_IN_SECS);
			receiveMessageRequest.setMaxNumberOfMessages(AMAZON_SQS_MAX_MESSAGES_RX);
			while (true == running) {
				try {
					// pull pending messages
					ReceiveMessageResult receiveMessageResult = sqsClient.receiveMessage(receiveMessageRequest);
					// setup the list of messages to ack
					List<DeleteMessageBatchRequestEntry> deleteMessages = new ArrayList<>(
							receiveMessageResult.getMessages().size());
					for (Message message : receiveMessageResult.getMessages()) {
						LOG.debug("received message={}", message);
						// parse out the actual message
						JSONObject jsonObject = new JSONObject(message.getBody());
						// now provide the message inside
						handler.handle(jsonObject.getString("Message"));
						// schedule for deletion
						DeleteMessageBatchRequestEntry deleteMessageEntry = new DeleteMessageBatchRequestEntry(
								message.getMessageId(), message.getReceiptHandle());
						deleteMessages.add(deleteMessageEntry);
					}
					// ack all messages (if there were any)
					if (0 != deleteMessages.size()) {
						sqsClient.deleteMessageBatch(sqsURL, deleteMessages);
					}
				} catch (AbortedException e) {
					LOG.debug("aborted blocking waiting for message");
				} catch (Exception e) {
					LOG.error("exception={}", e);
				}
			}
			LOG.trace("run exit");
		}
	}
}
