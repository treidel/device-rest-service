package com.fancypants.rest.app.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fancypants.rest.domain.CurrentRecord;
import com.fancypants.rest.domain.PowerConsumptionRecord;
import com.fancypants.rest.request.DeviceContainer;
import com.fancypants.rest.service.RecordService;
import com.fancypants.rest.usage.MonthlyDateIntervalGenerator;
import com.fancypants.rest.usage.UsageSummarizer;

@Service
public class UsageService {

	@Autowired
	private RecordService recordService;

	@Autowired
	private DeviceContainer deviceContainer;

	@Autowired
	private UsageSummarizer summarizer;

	@Autowired
	private MonthlyDateIntervalGenerator monthlyItervalGenerator;

	public Set<PowerConsumptionRecord> getMonthlyRecords() {
		// query all records
		List<CurrentRecord> currentRecords = recordService
				.findAllRecordsForDevice();
		// summarize
		Set<PowerConsumptionRecord> powerRecords = summarizer.summarizeRecords(
				currentRecords, monthlyItervalGenerator);
		// return the set
		return powerRecords;
	}
}
