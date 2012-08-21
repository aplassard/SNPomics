package org.cchmc.bmi.snpomics.cli.arguments;

import java.io.File;
import java.util.Date;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.IStringConverterFactory;

public class ConverterFactory implements IStringConverterFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <T> Class<? extends IStringConverter<T>> getConverter(Class<T> forType) {
		/*
		 * There's a nasty little bug in JDK 1.6 regarding the conversion of generics.
		 * See: http://bugs.sun.com/view_bug.do?bug_id=6548436
		 * 
		 * The workaround is to cast to Object, then back down to Class<IStringConverter> etc.
		 * 
		 * Note that the compiler used by Eclipse doesn't have this problem
		 */
		if (forType.equals(File.class)) 
			return (Class<? extends IStringConverter<T>>)(Object) FileConverter.class;
		if (forType.equals(Date.class))
			return (Class<? extends IStringConverter<T>>)(Object) DateConverter.class;
		return null;
	}

}
