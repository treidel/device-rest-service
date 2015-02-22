package com.fancypants.test.websocket.app.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompDecoder;
import org.springframework.messaging.simp.stomp.StompEncoder;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class StompSimulator extends TextWebSocketHandler {

	private static final StompEncoder ENCODER = new StompEncoder();
	private static final StompDecoder DECODER = new StompDecoder();

	private final List<Action> actions = new LinkedList<>();
	private Action current = null;
	private Semaphore semaphore = new Semaphore(0);

	public void add(Action action) {
		// add the action to the list
		actions.add(action);
	}

	public synchronized boolean finished() {
		return (null == current);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session)
			throws Exception {
		Assert.assertTrue(0 < actions.size());
		// pull the first action
		current = actions.remove(0);
		// send the message
		sendMessage(session);
	}

	@Override
	protected synchronized void handleTextMessage(WebSocketSession session,
			TextMessage message) throws Exception {
		// convert to a buffer
		ByteBuffer buffer = ByteBuffer.wrap(message.asBytes());
		// decode to stomp
		List<Message<byte[]>> messages = DECODER.decode(buffer);
		Assert.assertTrue(1 == messages.size());
		// decode
		StompHeaderAccessor accessor = StompHeaderAccessor
				.wrap(messages.get(0));
		// validate
		current.validateResponse(accessor);
		// dohh
		checkForEndOfTest(session);
	}

	public void block(int timeout, TimeUnit unit) {
		try {
			semaphore.tryAcquire(timeout, unit);
		} catch (InterruptedException e) {
			Assert.fail(e.getMessage());
		}
		Assert.assertTrue(0 == actions.size());
	}

	private synchronized void sendMessage(WebSocketSession session)
			throws IOException {
		// encode a STOMP message
		StompHeaderAccessor accessor = current.createRequest();
		// check if this action generates a message, if not we wait for a
		// response
		if (null != accessor) {
			accessor.setSessionId(session.getId());
			String body = current.createBody();
			byte[] messageBytes = ENCODER.encode(accessor.toMap(),
					body.getBytes());
			// wrap as a websocket message
			TextMessage message = new TextMessage(messageBytes);
			// send message
			session.sendMessage(message);
			// see if we need to wait for a response
			if (false == current.expectResponse()) {
				// self explanatory
				checkForEndOfTest(session);
			}
		} else {
			// mandatory to be looking for a response
			Assert.assertTrue(true == current.expectResponse());
		}
	}

	public void checkForEndOfTest(WebSocketSession session) throws IOException {
		// see if this was the last action
		if (false == actions.isEmpty()) {
			// get the next action
			current = actions.remove(0);
			// send the next message
			sendMessage(session);
		} else {
			// clear the current action to show we're done
			current = null;
			// force-close the session
			session.close();
			// wait to allow the server to process anything we sent
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				Assert.fail();
			}
			// release the semaphore to allow the test to complete
			semaphore.release();
		}
	}

	public interface Action {

		boolean expectResponse();

		void validateResponse(StompHeaderAccessor accessor);

		StompHeaderAccessor createRequest();

		String createBody();
	}

	public static class ConnectionAction implements Action {

		@Override
		public void validateResponse(StompHeaderAccessor accessor) {
			Assert.assertTrue(StompCommand.CONNECTED.equals(accessor
					.getCommand()));
		}

		@Override
		public StompHeaderAccessor createRequest() {
			return StompHeaderAccessor.create(StompCommand.CONNECT);
		}

		@Override
		public String createBody() {
			return "";
		}

		@Override
		public boolean expectResponse() {
			return true;
		}
	};

	public static class DisconnectAction implements Action {

		@Override
		public void validateResponse(StompHeaderAccessor accessor) {
			Assert.fail();
		}

		@Override
		public StompHeaderAccessor createRequest() {
			StompHeaderAccessor request = StompHeaderAccessor
					.create(StompCommand.DISCONNECT);
			return request;
		}

		@Override
		public String createBody() {
			return "";
		}

		@Override
		public boolean expectResponse() {
			return false;
		}
	};

	public static class RegisterAction implements Action {

		private final String body;

		public RegisterAction(String body) {
			this.body = body;
		}

		@Override
		public void validateResponse(StompHeaderAccessor accessor) {
			Assert.fail();
		}

		@Override
		public StompHeaderAccessor createRequest() {
			StompHeaderAccessor accessor = StompHeaderAccessor
					.create(StompCommand.SEND);
			accessor.addNativeHeader(
					StompHeaderAccessor.STOMP_DESTINATION_HEADER,
					"/registration");
			return accessor;
		}

		@Override
		public String createBody() {
			return body;
		}

		@Override
		public boolean expectResponse() {
			return false;
		}
	};

	public static class SubscriptionAction implements Action {

		private final String destination;
		private final String subscriptionId;

		public SubscriptionAction(String destination, String subscriptionId) {
			this.destination = destination;
			this.subscriptionId = subscriptionId;
		}

		@Override
		public void validateResponse(StompHeaderAccessor accessor) {
			Assert.fail();
		}

		@Override
		public StompHeaderAccessor createRequest() {
			StompHeaderAccessor accessor = StompHeaderAccessor
					.create(StompCommand.SUBSCRIBE);
			accessor.addNativeHeader(
					StompHeaderAccessor.STOMP_DESTINATION_HEADER, destination);
			accessor.setSubscriptionId(subscriptionId);
			return accessor;
		}

		@Override
		public String createBody() {
			return "";
		}

		@Override
		public boolean expectResponse() {
			return false;
		}
	};

	public static class NotificationAction implements Action {

		private final String subscriptionId;

		public NotificationAction(String subscriptionId) {
			this.subscriptionId = subscriptionId;
		}

		@Override
		public boolean expectResponse() {
			return true;
		}

		@Override
		public void validateResponse(StompHeaderAccessor accessor) {
			Assert.assertTrue(true == StompCommand.MESSAGE.equals(accessor
					.getCommand()));
			Assert.assertTrue(true == subscriptionId.equals(accessor
					.getSubscriptionId()));
		}

		@Override
		public StompHeaderAccessor createRequest() {
			return null;
		}

		@Override
		public String createBody() {
			return null;
		}

	}
}
