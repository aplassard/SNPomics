package org.cchmc.bmi.snpomics.cli.arguments;

import java.util.Date;
import java.util.GregorianCalendar;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

public class DateConverter implements IStringConverter<Date> {

	@Override
	public Date convert(String value) {
		String[] fields = value.split("-");
		if (fields.length != 3)
			throw new ParameterException("Date must be specified as YYYY-MM-DD");
		GregorianCalendar cal = new GregorianCalendar(Integer.parseInt(fields[0]),
				Integer.parseInt(fields[1])-1, Integer.parseInt(fields[2]));
		return cal.getTime();
	}

}
