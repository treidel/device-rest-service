package com.fancypants.test.websocket.device.cases;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;

import com.fancypants.data.repository.DeviceRepository;
import com.fancypants.rest.domain.RawRecord;
import com.fancypants.test.data.values.DeviceValues;
import com.fancypants.test.stream.TestStreamScanMe;
import com.fancypants.test.websocket.device.config.WebsocketDeviceTestConfig;
import com.fancypants.test.websocket.device.context.KeyStoreApplicationContextInitializer;
import com.fancypants.test.websocket.util.StompSimulator;
import com.fancypants.websocket.device.config.WSDeviceWebConfig;
import com.fancypants.websocket.device.config.WSDeviceWebSecurityConfig;
import com.fancypants.websocket.device.config.WSDeviceWebSocketConfig;
import com.fancypants.websocket.device.domain.DeviceInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { WSDeviceWebSecurityConfig.class, WSDeviceWebSocketConfig.class,
		WSDeviceWebConfig.class, WebsocketDeviceTestConfig.class,
		TestStreamScanMe.class }, initializers = { KeyStoreApplicationContextInitializer.class })
@WebAppConfiguration
@IntegrationTest
public class WebsocketDeviceTests {
	private static final URI TEST_URI = URI.create("wss://localhost:8083/stomp");

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebSocketClient websocketClient;

	@PostConstruct
	public void init() throws Exception {
		// cleanup + init database
		deviceRepository.delete(DeviceValues.DEVICEENTITY.getDevice());
		deviceRepository.save(DeviceValues.DEVICEENTITY);
	}

	@Test
	public void registrationTest() throws Exception {
		// create the simulator
		StompSimulator simulator = new StompSimulator();
		// add a connection action
		simulator.add(new StompSimulator.ConnectionAction());

		// serialize the device info
		DeviceInfo deviceInfo = new DeviceInfo("PROTOTYPE", "1.0", "1.0");
		String deviceInfoJSON = objectMapper.writeValueAsString(deviceInfo);

		// add a registration action
		simulator.add(new StompSimulator.SendAction("/registration", deviceInfoJSON));
		// add a disconnect action
		// simulator.add(new StompSimulator.DisconnectAction());

		// HTTP headers
		WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
		// connect
		websocketClient.doHandshake(simulator, headers, TEST_URI);

		// wait for the simulator to finish
		simulator.block(5, TimeUnit.HOURS);

		// make sure it completed as expected
		Assert.assertTrue(simulator.finished());
	}

	@Test
	public void messageTest() throws Exception {
		// serialize the records
		String rawRecordsJSON = objectMapper.writeValueAsString(RawRecord.TEST.RECORDS);

		// create the simulator
		StompSimulator simulator = new StompSimulator();
		// add a connection action
		simulator.add(new StompSimulator.ConnectionAction());

		// serialize the device info
		DeviceInfo deviceInfo = new DeviceInfo("PROTOTYPE", "1.0", "1.0");
		String deviceInfoJSON = objectMapper.writeValueAsString(deviceInfo);

		// add a send action
		simulator.add(new StompSimulator.SendAction("/registration", deviceInfoJSON));
		// add a send action
		simulator.add(new StompSimulator.SendAction("/records", rawRecordsJSON, UUID.randomUUID().toString()));

		// HTTP headers
		WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

		// connect
		websocketClient.doHandshake(simulator, headers, TEST_URI);

		// wait for the simulator to finish
		simulator.block(5, TimeUnit.HOURS);

		// make sure it completed as expected
		Assert.assertTrue(simulator.finished());
	}
}
