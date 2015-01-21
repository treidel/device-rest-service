package com.fancypants.websocket.app.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientInfo {

	public static final String TYPE_ATTRIBUTE = "type";
	public static final String VERSION_ATTRIBUTE = "version";

	private final String type;
	private final String version;

	@JsonCreator
	public ClientInfo(@JsonProperty(TYPE_ATTRIBUTE) String type,
			@JsonProperty(VERSION_ATTRIBUTE) String version) {
		this.type = type;
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public String getSoftwareVersion() {
		return version;
	}
}
