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

import com.fancypants.rest.device.assembler.DeviceResourceAssembler;
import com.fancypants.rest.device.resource.DeviceResource;
import com.fancypants.rest.domain.Device;
import com.fancypants.rest.exception.AbstractServiceException;
import com.fancypants.rest.request.DeviceContainer;
import com.fancypants.rest.service.DeviceService;

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

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public HttpEntity<DeviceResource> postDevice(@RequestBody Device device) throws AbstractServiceException {
		// do the creation
		deviceService.createDevice(device);
		
		DeviceResource resource = deviceResourceAssembler.toResource(device);
		ResponseEntity<DeviceResource> response = new ResponseEntity<DeviceResource>(
				resource, HttpStatus.CREATED);
		return response;
	}

	@RequestMapping(method = RequestMethod.GET)
	@Secured("ROLE_USER")
	@ResponseBody
	public HttpEntity<DeviceResource> getDevice() {
		Device device = deviceContainer.getDevice();
		DeviceResource resource = deviceResourceAssembler.toResource(device);
		return new ResponseEntity<DeviceResource>(resource, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT)
	@Secured("ROLE_USER")
	@ResponseBody
	public HttpEntity<DeviceResource> putDevice(@RequestBody Device device) throws AbstractServiceException {

		// do the update 
		deviceService.updateDevice(device);
		
		DeviceResource resource = deviceResourceAssembler.toResource(device);
		return new ResponseEntity<DeviceResource>(resource, HttpStatus.OK);
	}

}
