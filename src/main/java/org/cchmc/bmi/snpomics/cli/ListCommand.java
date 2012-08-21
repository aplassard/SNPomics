package org.cchmc.bmi.snpomics.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.cchmc.bmi.snpomics.Genome;
import org.cchmc.bmi.snpomics.OutputField;
import org.cchmc.bmi.snpomics.ReferenceMetadata;
import org.cchmc.bmi.snpomics.SnpomicsEngine;
import org.cchmc.bmi.snpomics.annotation.factory.AnnotationFactory;
import org.cchmc.bmi.snpomics.annotation.reference.AnnotationType;
import org.cchmc.bmi.snpomics.annotation.reference.ReferenceAnnotation;
import org.cchmc.bmi.snpomics.cli.arguments.ListArguments;
import org.cchmc.bmi.snpomics.reader.InputIterator;
import org.cchmc.bmi.snpomics.util.StringUtils;
import org.cchmc.bmi.snpomics.writer.VariantWriter;

public class ListCommand {
	
	public static void run(AnnotationFactory factory, ListArguments args) {
		switch (args.toList.get(0)) {
		case GENOMES : listGenomes(factory); break;
		case REFERENCES : listReferences(factory, false); break;
		case DEFAULT_REFERENCES : listReferences(factory, true); break;
		case ANNOTATIONS : listAnnotations(); break;
		case PARAMETERS : listParameters(); break;
		case READERS : listReaders(); break;
		case WRITERS : listWriters(); break;
		}		
	}

	private static void listGenomes(AnnotationFactory factory) {
		System.out.println("Name	Organism	TaxID	URL	TranslationTable (Alt/Chroms)");
		for (Genome g : factory.getAvailableGenomes()) {
			StringBuilder sb = new StringBuilder();
			sb.append(g.getName());
			sb.append("\t");
			sb.append(g.getOrganism());
			sb.append("\t");
			sb.append(g.getTaxId());
			sb.append("\t");
			sb.append(g.getSourceLink());
			sb.append("\t");
			sb.append(g.getTransTableId());
			if (!g.getAltTransChromosomes().isEmpty()) {
				sb.append(" (");
				sb.append(g.getAltTransTableId());
				sb.append("/");
				sb.append(StringUtils.join(",", g.getAltTransChromosomes()));
				sb.append(")");
			}
			System.out.println(sb.toString());
		}
	}
	
	private static void listReferences(AnnotationFactory factory, boolean onlyDefault) {
		System.out.println("Type	Version	Default	Source URL	Updated");
		Map<String, Class<? extends ReferenceAnnotation>> refs = SnpomicsEngine.getAnnotations();
		for (String key : refs.keySet()) {
			if (onlyDefault)
				printRMD(key, factory.getDefaultVersion(refs.get(key)));
			else {
				for (ReferenceMetadata<?> r : factory.getAvailableVersions(refs.get(key)))
					printRMD(key, r);
			}
		}
	}
	
	private static void printRMD(String type, ReferenceMetadata<?> rmd) {
		StringBuilder sb = new StringBuilder();
		sb.append(type);
		sb.append("\t");
		sb.append(rmd.getVersion());
		sb.append("\t");
		if (rmd.isDefault())
			sb.append("*");
		sb.append("\t");
		sb.append(rmd.getSource());
		sb.append("\t");
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(rmd.getUpdateDate());
		sb.append(cal.get(Calendar.YEAR));
		sb.append("-");
		sb.append(String.format("%02d", cal.get(Calendar.MONTH)+1));
		sb.append("-");
		sb.append(String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)));
		System.out.println(sb.toString());
	}
	
	private static void listAnnotations() {
		System.out.println("Name	Description	References	Groups");
		Map<String, OutputField> fields = SnpomicsEngine.getAllowedOutput();
		for (OutputField f : fields.values()) {
			ArrayList<String> refs = new ArrayList<String>();
			for (Class<? extends ReferenceAnnotation> cls : f.getReferences())
				refs.add(cls.getAnnotation(AnnotationType.class).value());
			if (refs.isEmpty()) refs.add("<none>");
			List<String> groups = f.getGroups();
			System.out.println(f.getName()+"\t"+
					f.getDescription()+"\t"+
					StringUtils.join(",", refs)+"\t"+
					(groups.isEmpty() ? "<none>" : StringUtils.join(",", groups)));
		}
	}
	
	private static void listParameters() {
		Properties prop = SnpomicsEngine.getProperties();
		try {
			prop.store(System.out, "Snpomics Parameters");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void listReaders() {
		System.out.println("Name	Extension	Description");
		Map<String, Class<? extends InputIterator>> inputs = SnpomicsEngine.getReaders();
		for (Class<? extends InputIterator> cls : inputs.values()) {
			try {
				InputIterator it = cls.newInstance();
				System.out.println(it.name()+"\t"+it.preferredExtension()+"\t"+it.description());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void listWriters() {
		System.out.println("Name	Extension	Description");
		Map<String, Class<? extends VariantWriter>> outputs = SnpomicsEngine.getWriters();
		for (Class<? extends VariantWriter> cls : outputs.values()) {
			try {
				VariantWriter it = cls.newInstance();
				System.out.println(it.name()+"\t"+it.preferredExtension()+"\t"+it.description());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

}
