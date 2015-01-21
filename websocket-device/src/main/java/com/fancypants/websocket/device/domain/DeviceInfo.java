package com.fancypants.websocket.device.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceInfo {

	public static final String MODEL_ATTRIBUTE = "model";
	public static final String SOFTWAREVERSION_ATTRIBUTE = "software-version";
	public static final String HARDWAREVERSION_ATTRIBUTE = "hardware-version";

	private final String model;
	private final String softwareVersion;
	private final String hardwareVersion;

	@JsonCreator
	public DeviceInfo(@JsonProperty(MODEL_ATTRIBUTE) String model,
			@JsonProperty(SOFTWAREVERSION_ATTRIBUTE) String softwareVersion,
			@JsonProperty(HARDWAREVERSION_ATTRIBUTE) String hardwareVersion) {
		this.model = model;
		this.softwareVersion = softwareVersion;
		this.hardwareVersion = hardwareVersion;
	}

	public String getModel() {
		return model;
	}

	public String getSoftwareVersion() {
		return softwareVersion;
	}

	public String getHardwareVersion() {
		return hardwareVersion;
	}
}
