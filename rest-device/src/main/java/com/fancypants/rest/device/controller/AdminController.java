package com.fancypants.rest.device.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.device.service.DeviceService;
import com.fancypants.rest.device.assembler.DeviceResourceAssembler;
import com.fancypants.rest.device.resource.DeviceResource;
import com.fancypants.rest.domain.Device;
import com.fancypants.rest.mapping.DeviceEntityMapper;
import com.fancypants.rest.mapping.DeviceMapper;

@Controller
@RequestMapping("/admin")
@Secured("ROLE_ADMIN")
public class AdminController {

	@Autowired
	private DeviceResourceAssembler deviceResourceAssembler;
	@Autowired
	private DeviceService deviceService;
	@Autowired
	private DeviceMapper deviceMapper;
	@Autowired
	private DeviceEntityMapper deviceEntityMapper;

	@RequestMapping(method = RequestMethod.POST, value = "/devices")
	@ResponseBody
	public ResponseEntity<Void> postDevice(@RequestBody Device device)
			throws AbstractServiceException {
		// map the device
		DeviceEntity deviceEntity = deviceEntityMapper.convert(device);
		// do the creation
		deviceService.createDevice(deviceEntity);
		// create the response
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(linkTo(
				methodOn(getClass()).getDevice(device.getName())).toUri());
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/devices/{deviceId}")
	@ResponseBody
	public HttpEntity<DeviceResource> getDevice(
			@PathVariable("deviceId") String deviceId)
			throws AbstractServiceException {
		// query for the device
		DeviceEntity deviceEntity = deviceService.getDevice(deviceId);
		// map it to the REST form
		Device device = deviceMapper.convert(deviceEntity);
		// wrap as a resource
		DeviceResource resource = deviceResourceAssembler.toResource(device);
		return new ResponseEntity<DeviceResource>(resource, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/devices/{deviceId}")
	@ResponseBody
	public HttpEntity<DeviceResource> putDevice(
			@PathVariable("deviceId") String deviceId,
			@RequestBody Device device) throws AbstractServiceException {
		// map the device
		DeviceEntity deviceEntity = deviceEntityMapper.convert(device);
		// do the update
		deviceService.updateDevice(deviceId, deviceEntity);
		// wrap as a resource
		DeviceResource resource = deviceResourceAssembler.toResource(device);
		return new ResponseEntity<DeviceResource>(resource, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/devices/{deviceId}")
	@ResponseBody
	public ResponseEntity<Void> putDevice(
			@PathVariable("deviceId") String deviceId)
			throws AbstractServiceException {
		// delete the device
		deviceService.deleteDevice(deviceId);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
