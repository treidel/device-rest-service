package com.fancypants.websocket.app.subscription;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import com.fancypants.message.exception.AbstractMessageException;
import com.fancypants.message.topic.TopicConsumer;
import com.fancypants.message.topic.TopicManager;

@Component
public class SubscriptionManager {
	private static final Logger LOG = LoggerFactory.getLogger(SubscriptionManager.class);

	private static final Map<String, SessionState> SESSIONS = new ConcurrentHashMap<>();

	@Autowired
	private TopicManager topicManager;

	@Autowired
	private AbstractSubscribableChannel clientOutboundChannel;

	@Bean
	public ApplicationListener<SessionConnectedEvent> sessionConnectedEventListener() {
		LOG.trace("sessionConnectedEventListener enter");
		ApplicationListener<SessionConnectedEvent> listener = new SessionConnectedListener();
		LOG.trace("sessionConnectedEventListener exit", "listener", listener);
		return listener;
	}

	@Bean
	public ApplicationListener<SessionDisconnectEvent> sessionDisconnectedEventListener() {
		LOG.trace("sessionDisconnectedEventListener enter");
		ApplicationListener<SessionDisconnectEvent> listener = new SessionDisconnectedListener();
		LOG.trace("sessionDisconnectedEventListener exit", "listener", listener);
		return listener;
	}

	@Bean
	public ApplicationListener<SessionSubscribeEvent> sessionSubscribeEventListener() {
		LOG.trace("sessionSubscribeEventListener enter");
		ApplicationListener<SessionSubscribeEvent> listener = new SessionSubscribeListener();
		LOG.trace("sessionSubscribeEventListener exit", "listener", listener);
		return listener;
	}

	@Bean
	public ApplicationListener<SessionUnsubscribeEvent> sessionUnsubscribeEventListener() {
		LOG.trace("sessionUnsubscribeEventListener enter");
		ApplicationListener<SessionUnsubscribeEvent> listener = new SessionUnsubscribeListener();
		LOG.trace("sessionUnsubscribeEventListener exit", "listener", listener);
		return listener;
	}

	private class SessionState {

		private Map<String, SubscriptionState> subscriptions = new ConcurrentHashMap<>();

		public void addSubscription(SubscriptionState subscriptionState) {
			subscriptions.put(subscriptionState.getSubscriptionId(), subscriptionState);
		}

		public SubscriptionState removeSubscription(String subscriptionId) {
			return subscriptions.remove(subscriptionId);
		}

		public void cleanup() {
			LOG.trace("SessionState.cleanup enter");
			for (Map.Entry<String, SubscriptionState> entry : subscriptions.entrySet()) {
				entry.getValue().cleanup();
			}
			LOG.trace("SessionState.cleanup exit");
		}
	}

	private class SubscriptionState {
		private final TopicConsumer topicConsumer;
		private final String subscriptionId;

		public SubscriptionState(TopicConsumer topicConsumer, String subscriptionId) {
			LOG.trace("SubscriptionState.SubscriptionState enter", "topicConsumer", topicConsumer, "subscriptionId",
					subscriptionId);
			this.topicConsumer = topicConsumer;
			this.subscriptionId = subscriptionId;
			LOG.trace("SubscriptionState.SubscriptionState exit");
		}

		public String getSubscriptionId() {
			return subscriptionId;
		}

		public void cleanup() {
			LOG.trace("SubscriptionState.cleanup enter");
			// close the topic
			topicConsumer.close();
			LOG.trace("SubscriptionState.cleanup exit");
		}
	}

	private class SessionConnectedListener implements ApplicationListener<SessionConnectedEvent> {

		@Override
		public void onApplicationEvent(SessionConnectedEvent event) {
			LOG.trace("SessionConnectedListener.onApplicationEvent enter", "event", event);
			// decode the message
			StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
			// create the session state
			SESSIONS.put(accessor.getSessionId(), new SessionState());
			LOG.trace("SessionConnectedListener.onApplicationEvent exit");

		}

	}

	private class SessionDisconnectedListener implements ApplicationListener<SessionDisconnectEvent> {

		@Override
		public void onApplicationEvent(SessionDisconnectEvent event) {
			LOG.trace("SessionDisconnectedListener.onApplicationEvent enter", "event", event);
			// decode the message
			StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
			// remove the session state
			SessionState state = SESSIONS.remove(accessor.getSessionId());
			if (null != state) {
				// clean up the state
				state.cleanup();
			}
			LOG.trace("SessionDisconnectedListener.onApplicationEvent exit");
		}

	}

	private class SessionSubscribeListener implements ApplicationListener<SessionSubscribeEvent> {

		@Override
		public void onApplicationEvent(SessionSubscribeEvent event) {
			LOG.trace("SessionSubscribeListener.onApplicationEvent enter", "event", event);
			// decode the message
			StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
			// make sure we got a subscription id
			Assert.notNull(accessor.getSubscriptionId());
			// get the session state
			SessionState sessionState = SESSIONS.get(accessor.getSessionId());
			Assert.notNull(sessionState);
			// extract the topic
			String topic = accessor.getUser().getName();
			try {
				// setup the listener
				TopicConsumer.Handler listener = new TopicListener(accessor.getDestination(), accessor.getSessionId(),
						accessor.getSubscriptionId());
				// create the topic consumer
				TopicConsumer topicConsumer = topicManager.topicConsumer(topic, listener);
				// start the consumer
				topicConsumer.start();
				// now create the subscription state
				SubscriptionState subscriptionState = new SubscriptionState(topicConsumer,
						accessor.getSubscriptionId());
				// store the state
				sessionState.addSubscription(subscriptionState);
			} catch (AbstractMessageException e) {
				LOG.warn("unable to add topic consumer", e);
				throw new IllegalStateException(e);
			}
		}
	}

	private class SessionUnsubscribeListener implements ApplicationListener<SessionUnsubscribeEvent> {

		@Override
		public void onApplicationEvent(SessionUnsubscribeEvent event) {
			LOG.trace("SessionUnsubscribeListener.onApplicationEvent enter", "event", event);
			// decode the message
			StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
			// get the session state
			SessionState sessionState = SESSIONS.get(accessor.getSessionId());
			Assert.notNull(sessionState);
			// get the subscription state
			SubscriptionState subscriptionState = sessionState.removeSubscription(accessor.getSubscriptionId());
			if (null != subscriptionState) {
				subscriptionState.cleanup();
			}
		}
	}

	private class TopicListener implements TopicConsumer.Handler {

		private final String destination;
		private final String sessionId;
		private final String subscriptionId;

		public TopicListener(String destination, String sessionId, String subscriptionId) {
			this.destination = destination;
			this.sessionId = sessionId;
			this.subscriptionId = subscriptionId;
		}

		@Override
		public void handle(String body) {
			LOG.trace("TopicListener.handle enter", "body", body);
			// create a stomp message
			StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.MESSAGE);
			accessor.setDestination(destination);
			accessor.setSessionId(sessionId);
			accessor.setSubscriptionId(subscriptionId);
			// create the message
			Message<byte[]> message = MessageBuilder.createMessage(body.getBytes(), accessor.getMessageHeaders());
			// send it
			clientOutboundChannel.send(message);
			LOG.trace("TopicListener.handle exit");
		}
	}
}
