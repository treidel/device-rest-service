package com.fancypants.rest.app.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.device.container.DeviceContainer;
import com.fancypants.device.service.DeviceService;
import com.fancypants.rest.app.assembler.DeviceResourceAssembler;
import com.fancypants.rest.app.resource.DeviceResource;
import com.fancypants.rest.domain.Device;
import com.fancypants.rest.mapping.DeviceEntityMapper;
import com.fancypants.rest.mapping.DeviceMapper;

@Controller
@RequestMapping("/app/device")
@Secured("ROLE_USER")
public class DeviceController {

	private final DeviceResourceAssembler deviceResourceAssembler = new DeviceResourceAssembler();
	@Autowired
	private DeviceService deviceService;
	@Autowired
	private DeviceContainer deviceContainer;
	@Autowired
	private DeviceMapper deviceMapper;
	@Autowired
	private DeviceEntityMapper deviceEntityMapper;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<DeviceResource> getDevice() {
		// get the device
		DeviceEntity deviceEntity = deviceContainer.getDeviceEntity();
		// map the device
		Device device = deviceMapper.convert(deviceEntity);
		DeviceResource resource = deviceResourceAssembler.toResource(device);
		return new ResponseEntity<DeviceResource>(resource, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public HttpEntity<DeviceResource> putDevice(@RequestBody Device device)
			throws AbstractServiceException {
		// map the device
		DeviceEntity deviceEntity = deviceEntityMapper.convert(device);
		// do the update
		deviceService.updateDevice(deviceEntity);
		DeviceResource resource = deviceResourceAssembler.toResource(device);
		return new ResponseEntity<DeviceResource>(resource, HttpStatus.OK);
	}

}
