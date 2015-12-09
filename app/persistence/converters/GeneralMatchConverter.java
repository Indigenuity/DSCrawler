package persistence.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import datadefinitions.GeneralMatch;

@Converter(autoApply=true)
public class GeneralMatchConverter  implements AttributeConverter<GeneralMatch, Integer>{

	@Override
	public Integer convertToDatabaseColumn(GeneralMatch wp) {
//		System.out.println("general id : " + wp.getId());
		return wp.getId();
	}

	@Override
	public GeneralMatch convertToEntityAttribute(Integer id) {
		
		return GeneralMatch.getTypeFromId(id);
	}

}