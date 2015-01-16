package com.fancypants.websocket.container;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value="websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionContainer {

	private boolean identified = false;

	public boolean isIdentified() {
		return identified;
	}

	public void setIdentified(boolean identified) {
		this.identified = identified;
	}
	
}
