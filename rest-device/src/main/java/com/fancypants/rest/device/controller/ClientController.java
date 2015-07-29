package com.fancypants.rest.device.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.device.service.DeviceService;
import com.fancypants.rest.device.assembler.DeviceResourceAssembler;
import com.fancypants.rest.device.resource.DeviceResource;
import com.fancypants.rest.domain.Device;
import com.fancypants.rest.mapping.DeviceMapper;

@Controller
@RequestMapping("/devices")
public class ClientController {

	private static final Logger LOG = LoggerFactory.getLogger(ClientController.class);

	@Autowired
	private DeviceResourceAssembler deviceResourceAssembler;
	@Autowired
	private DeviceService deviceService;
	@Autowired
	private DeviceMapper deviceMapper;

	@RequestMapping(method = RequestMethod.GET, value = "/{deviceId}")
	@ResponseBody
	public HttpEntity<DeviceResource> getDevice(@PathVariable("deviceId") String deviceId)
			throws AbstractServiceException {
		LOG.trace("getDevice enter {}={}", "deviceId", deviceId);
		// query for the device
		DeviceEntity deviceEntity = deviceService.findDevice(deviceId);
		if (null == deviceEntity) {
			LOG.debug("device {} not found", deviceId);
			return new ResponseEntity<DeviceResource>(HttpStatus.NOT_FOUND);
		}
		// map it to the REST form
		Device device = deviceMapper.convert(deviceEntity);
		// wrap as a resource
		DeviceResource resource = deviceResourceAssembler.toResource(device);
		LOG.trace("getDevice exit {}", resource);
		return new ResponseEntity<DeviceResource>(resource, HttpStatus.OK);
	}
}
