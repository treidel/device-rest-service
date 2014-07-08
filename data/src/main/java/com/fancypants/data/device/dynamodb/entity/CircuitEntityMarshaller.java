package com.fancypants.data.device.dynamodb.entity;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.dynamodbv2.datamodeling.JsonMarshaller;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;

public class CircuitEntityMarshaller extends JsonMarshaller<Set<CircuitEntity>> {

	@Autowired
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Set<CircuitEntity> unmarshall(Class<Set<CircuitEntity>> clazz,
			String obj) {
		Set<CircuitEntity> circuitEntities = new HashSet<CircuitEntity>();
		try {
			// parse the json into pieces
			JsonNode rootNode = objectMapper.readTree(obj);
			for (JsonNode childNode : rootNode) {
				JsonParser parser = new TreeTraversingParser(childNode, objectMapper);
				CircuitEntity circuitEntity = parser
						.readValueAs(CircuitEntity.class);
				circuitEntities.add(circuitEntity);
				parser.close();
			}
			return circuitEntities;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
