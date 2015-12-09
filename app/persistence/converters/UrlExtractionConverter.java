package persistence.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import datadefinitions.UrlExtraction;

@Converter(autoApply=true)
public class UrlExtractionConverter implements AttributeConverter<UrlExtraction, Integer>{

	@Override
	public Integer convertToDatabaseColumn(UrlExtraction element) {
//		System.out.println("general id : " + wp.getId());
		return element.getId();
	}

	@Override
	public UrlExtraction convertToEntityAttribute(Integer id) {
		
		return UrlExtraction.getTypeFromId(id);
	}

}