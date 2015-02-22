package com.fancypants.test.websocket.app.cases;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.sockjs.client.SockJsClient;

import com.fancypants.data.repository.DeviceRepository;
import com.fancypants.message.topic.TopicManager;
import com.fancypants.message.topic.TopicProducer;
import com.fancypants.test.data.values.DeviceValues;
import com.fancypants.test.data.values.HourlyRecordValues;
import com.fancypants.test.websocket.app.config.WebsocketAppTestConfig;
import com.fancypants.test.websocket.app.util.StompSimulator;
import com.fancypants.websocket.app.config.WebSecurityConfig;
import com.fancypants.websocket.app.config.WebSocketConfig;
import com.fancypants.websocket.app.config.WebSocketSecurityConfig;
import com.fancypants.websocket.app.domain.ClientInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { WebSecurityConfig.class,
		WebSocketConfig.class, WebSocketSecurityConfig.class,
		WebsocketAppTestConfig.class })
@WebAppConfiguration
@IntegrationTest
public class WebsocketAppTests {
	private static final URI TEST_URI = URI.create("ws://localhost:8080/stomp");

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private DeviceValues values;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private TopicManager topicManager;

	@Autowired
	private SockJsClient sockjsClient;

	private String clientInfoJSON;

	@PostConstruct
	public void init() throws Exception {
		// cleanup + init database
		deviceRepository.delete(DeviceValues.DEVICEENTITY.getDevice());
		deviceRepository.save(DeviceValues.DEVICEENTITY);

		// create the client info body
		ClientInfo info = new ClientInfo("test", "1.0");
		clientInfoJSON = mapper.writeValueAsString(info);

		// initialize the message broker
		topicManager.topicCreate(DeviceValues.DEVICEENTITY.getDevice());

	}

	@Test
	public void registrationTest() throws Exception {
		// create the simulator
		StompSimulator simulator = new StompSimulator();
		// add a connection action
		simulator.add(new StompSimulator.ConnectionAction());
		// add a registration action
		simulator.add(new StompSimulator.RegisterAction(clientInfoJSON));
		// add a disconnect action
		simulator.add(new StompSimulator.DisconnectAction());

		// HTTP headers
		WebSocketHttpHeaders headers = new WebSocketHttpHeaders(
				createAuthenticationHeaders());
		// connect
		sockjsClient.doHandshake(simulator, headers, TEST_URI);

		// wait for the simulator to finish
		simulator.block(5, TimeUnit.HOURS);

		// make sure it completed as expected
		Assert.assertTrue(simulator.finished());
	}

	@Test
	public void subscriptionTest() throws Exception {
		// create the simulator
		StompSimulator simulator = new StompSimulator();
		// add a connection action
		simulator.add(new StompSimulator.ConnectionAction());
		// add a registration action
		simulator.add(new StompSimulator.RegisterAction(clientInfoJSON));
		// add a subscription action
		simulator.add(new StompSimulator.SubscriptionAction("/topic/"
				+ DeviceValues.DEVICEENTITY.getDevice(), UUID.randomUUID()
				.toString()));

		// HTTP headers
		WebSocketHttpHeaders headers = new WebSocketHttpHeaders(
				createAuthenticationHeaders());
		// connect
		sockjsClient.doHandshake(simulator, headers, TEST_URI);

		// wait for the simulator to finish
		simulator.block(5, TimeUnit.HOURS);

		// make sure it completed as expected
		Assert.assertTrue(simulator.finished());
	}

	@Test
	public void notificationTest() throws Exception {
		// create the topic publisher
		TopicProducer producer = topicManager
				.topicProducer(DeviceValues.DEVICEENTITY.getDevice());
		// create the simulator
		StompSimulator simulator = new StompSimulator();
		// add a connection action
		simulator.add(new StompSimulator.ConnectionAction());
		// add a registration action
		simulator.add(new StompSimulator.RegisterAction(clientInfoJSON));
		// add a subscription action
		String subscriptionId = UUID.randomUUID().toString();
		simulator.add(new StompSimulator.SubscriptionAction("/topic/"
				+ DeviceValues.DEVICEENTITY.getDevice(), subscriptionId));
		// add a notification action
		simulator.add(new StompSimulator.NotificationAction(subscriptionId));

		// HTTP headers
		WebSocketHttpHeaders headers = new WebSocketHttpHeaders(
				createAuthenticationHeaders());
		// connect
		sockjsClient.doHandshake(simulator, headers, TEST_URI);

		// send a notification
		String json = mapper.writeValueAsString(HourlyRecordValues.RECORD1);
		producer.sendMessage(json);

		// wait for the simulator to finish
		simulator.block(5, TimeUnit.HOURS);

		// make sure it completed as expected
		Assert.assertTrue(simulator.finished());
	}

	private HttpHeaders createAuthenticationHeaders() {
		HttpHeaders headers = new HttpHeaders();
		String auth = DeviceValues.DEVICEENTITY.getDevice() + ":"
				+ DeviceValues.DEVICEENTITY.getSerialNumber();
		String authInBase64 = new String(Base64.encodeBase64(auth.getBytes()));
		headers.add(HttpHeaders.AUTHORIZATION, "Basic " + authInBase64);
		return headers;
	}
}
