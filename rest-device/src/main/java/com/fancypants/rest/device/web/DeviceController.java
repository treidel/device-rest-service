package com.fancypants.rest.device.web;

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
import com.fancypants.rest.device.assembler.DeviceResourceAssembler;
import com.fancypants.rest.device.resource.DeviceResource;
import com.fancypants.rest.domain.Device;
import com.fancypants.rest.mapping.DeviceEntityMapper;
import com.fancypants.rest.mapping.DeviceMapper;

@Controller
@RequestMapping("/device")
@Secured("ROLE_ADMIN")
public class DeviceController {

	@Autowired
	private DeviceResourceAssembler deviceResourceAssembler;
	@Autowired
	private DeviceService deviceService;
	@Autowired
	private DeviceContainer deviceContainer;
	@Autowired
	private DeviceMapper deviceMapper;
	@Autowired
	private DeviceEntityMapper deviceEntityMapper;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public HttpEntity<DeviceResource> postDevice(@RequestBody Device device)
			throws AbstractServiceException {
		// map the device
		DeviceEntity deviceEntity = deviceEntityMapper.convert(device);
		// do the creation
		deviceService.createDevice(deviceEntity);
		// wrap as a resource
		DeviceResource resource = deviceResourceAssembler.toResource(device);
		ResponseEntity<DeviceResource> response = new ResponseEntity<DeviceResource>(
				resource, HttpStatus.CREATED);
		return response;
	}

	@RequestMapping(method = RequestMethod.GET)
	@Secured("ROLE_USER")
	@ResponseBody
	public HttpEntity<DeviceResource> getDevice() {
		// get the entity
		DeviceEntity deviceEntity = deviceContainer.getDeviceEntity();
		// map it to the REST form
		Device device = deviceMapper.convert(deviceEntity);
		// wrap as a resource
		DeviceResource resource = deviceResourceAssembler.toResource(device);
		return new ResponseEntity<DeviceResource>(resource, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT)
	@Secured("ROLE_USER")
	@ResponseBody
	public HttpEntity<DeviceResource> putDevice(@RequestBody Device device)
			throws AbstractServiceException {
		// map the device
		DeviceEntity deviceEntity = deviceEntityMapper.convert(device);
		// do the update
		deviceService.updateDevice(deviceEntity);
		// wrap as a resource
		DeviceResource resource = deviceResourceAssembler.toResource(device);
		return new ResponseEntity<DeviceResource>(resource, HttpStatus.OK);
	}

}
