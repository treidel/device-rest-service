package com.fancypants.usage.summarizer;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.data.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.entity.EnergyConsumptionRecordId;

@Component
public class EnergyConsumptionSummarizer implements
		Summarizer<EnergyConsumptionRecordId, EnergyConsumptionRecordEntity>,
		Serializable {

	private static final long serialVersionUID = -6315384857284705447L;

	@Override
	public EnergyConsumptionRecordEntity summarize(
			EnergyConsumptionRecordId id,
			EnergyConsumptionRecordEntity entity1,
			EnergyConsumptionRecordEntity entity2) {
		// create the set to hold the new usage
		Map<Integer, Float> usage = new TreeMap<Integer, Float>();
		// go through all possible circuits and sum their usage
		for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
			Float usage1 = entity1.getEnergy(i);
			Float usage2 = entity2.getEnergy(i);
			// paranoia here - both usage should normally be available
			if ((null != usage1) && (null != usage2)) {
				usage.put(i, usage1 + usage2);
			} else if (null != usage1) {
				usage.put(i, usage1);
			} else if (null != usage2) {
				usage.put(i, usage2);
			}
		}
		// create the entity
		return new EnergyConsumptionRecordEntity(id, usage);
	}
}
