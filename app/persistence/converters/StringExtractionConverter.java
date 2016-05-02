package persistence.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import datadefinitions.StringExtraction;

@Converter(autoApply=true)
public class StringExtractionConverter implements AttributeConverter<StringExtraction, Integer>{

	@Override
	public Integer convertToDatabaseColumn(StringExtraction element) {
//		System.out.println("general id : " + wp.getId());
//		return element.getId();
		return 0;
	}
 
	@Override
	public StringExtraction convertToEntityAttribute(Integer id) {
		
		return StringExtraction.EMAIL_ADDRESS;
	}

}