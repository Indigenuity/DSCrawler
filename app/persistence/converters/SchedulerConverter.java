package persistence.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import datadefinitions.Scheduler;

@Converter(autoApply=true)
public class SchedulerConverter  implements AttributeConverter<Scheduler, Integer>{

	@Override
	public Integer convertToDatabaseColumn(Scheduler wp) {
		return wp.getId();
	}

	@Override
	public Scheduler convertToEntityAttribute(Integer id) {
		return Scheduler.getTypeFromId(id);
	}

}