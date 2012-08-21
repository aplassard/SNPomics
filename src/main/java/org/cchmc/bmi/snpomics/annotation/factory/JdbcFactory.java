package org.cchmc.bmi.snpomics.annotation.factory;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.cchmc.bmi.snpomics.Genome;
import org.cchmc.bmi.snpomics.ReferenceMetadata;
import org.cchmc.bmi.snpomics.SnpomicsEngine;
import org.cchmc.bmi.snpomics.annotation.importer.GenomicSequenceImporter;
import org.cchmc.bmi.snpomics.annotation.importer.JdbcImporter;
import org.cchmc.bmi.snpomics.annotation.importer.TranscriptImporter;
import org.cchmc.bmi.snpomics.annotation.loader.GenomicSequenceLoader;
import org.cchmc.bmi.snpomics.annotation.loader.JdbcLoader;
import org.cchmc.bmi.snpomics.annotation.loader.TranscriptLoader;
import org.cchmc.bmi.snpomics.annotation.reference.GenomicSequenceAnnotation;
import org.cchmc.bmi.snpomics.annotation.reference.ReferenceAnnotation;
import org.cchmc.bmi.snpomics.annotation.reference.TranscriptAnnotation;
import org.cchmc.bmi.snpomics.exception.AnnotationNotFoundException;
import org.cchmc.bmi.snpomics.util.StringUtils;

/**
 * An AnnotationFactory that utilizes JDBC as a backend.  The credentials for the
 * database are typically specified in a Properties object with the following keys:
 * 
 * <ul>
 *  <li><b>jdbc.drivers</b>: A colon-separated list of Driver classes to use 
 *  (<code>org.postgresql.Driver</code> or <code>com.mysql.jdbc.Driver</code>)</li>
 *  <li><b>jdbc.url</b>: The JDBC URL specifying the database (<code>jdbc:postgresql:[database]</code>
 *  or <code>jdbc:mysql://[host][:port]/[database]</code>)</li>
 *  <li><b>jdbc.username</b>: Optional - if not specified, no user authentication will occur</li>
 *  <li><b>jdbc.password</b>: Optional - only used if jdbc.username is specified</li>
 * </ul>
 * @author dexzb9
 *
 */
public class JdbcFactory extends AnnotationFactory {
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ReferenceAnnotation> JdbcLoader<T> getLoader(
			Class<T> cls, String version) throws AnnotationNotFoundException {
		JdbcLoader<?> loader = null;
		@SuppressWarnings("rawtypes")
		ReferenceMetadata<?> rmd = new ReferenceMetadata(cls, genome, version);
		if (!loaderCache.containsKey(cls)) {
			if (cls == TranscriptAnnotation.class)
				loader = new TranscriptLoader();
			if (cls == GenomicSequenceAnnotation.class)
				loader = new GenomicSequenceLoader();
			if (loader == null)
				throw new AnnotationNotFoundException(cls.getCanonicalName());
			loader.setConnection(connection);
			loaderCache.put(cls, loader);
		}
		loader = loaderCache.get(cls);
		loader.setTableName(tableNames.get(rmd));
		return (JdbcLoader<T>) loader;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends ReferenceAnnotation> JdbcImporter<T> getImporter(ReferenceMetadata<T> ref) {
		if (ref.getAnnotationClass() == TranscriptAnnotation.class)
			return (JdbcImporter<T>) new TranscriptImporter();
		if (ref.getAnnotationClass() == GenomicSequenceAnnotation.class)
			return (JdbcImporter<T>) new GenomicSequenceImporter();
		return null;
	}

	/**
	 * Creates a factory that creates its own JDBC connection from the parameters in the Snpomics
	 * properties
	 */
	public JdbcFactory() {
		versions = new HashMap<Class<? extends ReferenceAnnotation>, List<ReferenceMetadata<?>>>();
		defaults = new HashMap<Class<? extends ReferenceAnnotation>, ReferenceMetadata<?>>();
		tableNames = new HashMap<ReferenceMetadata<?>, String>();
		loaderCache = new HashMap<Class<? extends ReferenceAnnotation>, JdbcLoader<?>>();
		initialize();
	}
	
	/**
	 * Create a factory that utilizes the specified JDBC connection.  Note that the factory will
	 * take ownership of this connection - in particular, the Factory will close the connection
	 * when the program ends
	 * @param cxn
	 */
	public JdbcFactory(Connection cxn) {
		versions = new HashMap<Class<? extends ReferenceAnnotation>, List<ReferenceMetadata<?>>>();
		defaults = new HashMap<Class<? extends ReferenceAnnotation>, ReferenceMetadata<?>>();
		tableNames = new HashMap<ReferenceMetadata<?>, String>();
		loaderCache = new HashMap<Class<? extends ReferenceAnnotation>, JdbcLoader<?>>();
		initialize(cxn);
	}
	
	/**
	 * Creates a connection to the JDBC backend based on the values in the global Snpomics properties
	 * @return
	 */
	public boolean initialize() {
		//This code is nearly verbatim from Horstmann & Cornell's Core Java, Volume 2, p196 (7th ed)
		String drivers = SnpomicsEngine.getProperty("jdbc.drivers");
		if (drivers != null) System.setProperty("jdbc.drivers", drivers);
		String url = SnpomicsEngine.getProperty("jdbc.url");
		String username = SnpomicsEngine.getProperty("jdbc.username");
		String password = SnpomicsEngine.getProperty("jdbc.password");
		curGenome = null;
		try {
			Connection c;
			if (username == null)
				c = DriverManager.getConnection(url);
			else
				c = DriverManager.getConnection(url, username, password);
			return initialize(c);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean initialize(Connection cxn) {
		curGenome = null;
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				//Ignore it - nothing to be done at this point anyway
			}
			Runtime.getRuntime().removeShutdownHook(connectionCloser);
		}
		connection = cxn;
		connectionCloser = new Thread(new ShutdownRunnable(connection));
		Runtime.getRuntime().addShutdownHook(connectionCloser);
		return true;
	}
	
	@Override
	public boolean initializeEmptyBackend() {
		Statement stat = null;
		try {
			stat = connection.createStatement();
			stat.executeUpdate("CREATE TABLE genomes ("+
					"id varchar(30) NOT NULL, "+
					"organism varchar(60) NOT NULL, "+
					"taxid int(10) UNSIGNED NOT NULL, "+
					"sourceURI varchar(255) NOT NULL, "+
					"translateTable smallint(3) UNSIGNED NOT NULL, "+
					"altTranslateTable smallint(3) UNSIGNED NOT NULL, "+
					"altTranslateChroms varchar(255) NOT NULL, "+
					"PRIMARY KEY (id))");
			stat.executeUpdate("CREATE TABLE `references` ("+
					"genome varchar(30) NOT NULL, "+
					"javaclass varchar(255) NOT NULL, "+
					"version varchar(255) NOT NULL, "+
					"tablename varchar(40) NOT NULL, "+
					"updated date NOT NULL, "+
					"linktemplate varchar(255), "+
					"sourceURI varchar(255) NOT NULL, "+
					"defaultVersion smallint(1) NOT NULL, "+
					"PRIMARY KEY(genome,javaclass,version))");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e) {}
		}
		return true;
	}

	@Override
	public Set<Genome> getAvailableGenomes() {
		Statement stat = null;
		ResultSet rs = null;
		Set<Genome> result = new HashSet<Genome>();
		try {
			stat = connection.createStatement();
			rs = stat.executeQuery("SELECT * FROM genomes");
			while (rs.next()) {
				result.add(createGenomeFromRS(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return result;
		} finally {
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e) {}
		}
		return result;
	}

	@Override
	public void setGenome(String genome) {
		this.genome = genome;
		curGenome = null;
		loadAnnotationTables();
	}
	
	@Override
	public Genome getGenome() {
		PreparedStatement stat = null;
		ResultSet rs = null;
		if (curGenome == null) {
			try {
				stat = connection.prepareStatement("SELECT * FROM genomes WHERE id=?");
				stat.setString(1, genome);
				rs = stat.executeQuery();
				if (rs.next()) {
					curGenome = createGenomeFromRS(rs);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return curGenome;
			} finally {
				try {
					if (stat != null)
						stat.close();
				} catch (SQLException e) {}
			}
		}
		return curGenome;
	}

	@Override
	public void createGenome(Genome newGenome) {
		PreparedStatement stat = null;
		try {
			stat = connection.prepareStatement("INSERT INTO genomes SET "+
					"id=?, organism=?, taxid=?, sourceURI=?, translateTable=?, "+
					"altTranslateTable=?, altTranslateChroms=?");
			stat.setString(1, newGenome.getName());
			stat.setString(2, newGenome.getOrganism());
			stat.setInt(3, newGenome.getTaxId());
			stat.setString(4, newGenome.getSourceLink().toString());
			stat.setInt(5, newGenome.getTransTableId());
			stat.setInt(6, newGenome.getAltTransTableId());
			stat.setString(7, StringUtils.join(",", newGenome.getAltTransChromosomes()));
			stat.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e) {}
		}
		setGenome(newGenome.getName());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ReferenceAnnotation> List<ReferenceMetadata<T>> getAvailableVersions(Class<T> cls) {
		//Can't just return versions.get(cls) because of type-checking - so
		//the rest of this is blatantly unsafe, "fool the compiler" type stuff
		List<ReferenceMetadata<T>> result = new ArrayList<ReferenceMetadata<T>>();
		for (ReferenceMetadata<?> rmd : versions.get(cls))
			result.add((ReferenceMetadata<T>)rmd);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ReferenceAnnotation> ReferenceMetadata<T> getDefaultVersion(Class<T> cls) {
		return (ReferenceMetadata<T>)defaults.get(cls);
	}

	@Override
	public void setDefaultVersion(Class<? extends ReferenceAnnotation> cls, String version) {
		for (ReferenceMetadata<?> rmd : versions.get(cls))
			if (rmd.getVersion().equals(version))
				defaults.put(cls, rmd);
	}

	@Override
	public void makeVersionPermanentDefault(Class<? extends ReferenceAnnotation> cls, String version) {
		setDefaultVersion(cls, version);
		PreparedStatement stat = null;
		try {
			stat = connection.prepareStatement("UPDATE `references` SET defaultVersion=(version=?) WHERE genome=? AND javaclass=?");
			stat.setString(1, version);
			stat.setString(2, genome);
			stat.setString(3, cls.getCanonicalName());
			stat.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e) {}
		}
	}
	
	@Override
	public boolean importData(Reader input, ReferenceMetadata<? extends ReferenceAnnotation> ref) {
		//Ignore the default flag!
		ref.setDefault(false);
		//Add ref to our cache of rmds
		if (!versions.containsKey(ref.getAnnotationClass()))
			versions.put(ref.getAnnotationClass(), new ArrayList<ReferenceMetadata<?>>());
		versions.get(ref.getAnnotationClass()).add(ref);
		
		//Set up the references entry and import
		JdbcImporter<?> importer = getImporter(ref);
		//Generate a table name and verify that it's not already used;
		String tableName = "";
		boolean isNameUnused = false;
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			stat = connection.prepareStatement("SHOW TABLES LIKE ?");
			do {
				tableName = UUID.randomUUID().toString();
				stat.setString(1, tableName);
				rs = stat.executeQuery();
				isNameUnused = !rs.next();
				rs.close();
			} while (!isNameUnused);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e) {}
		}
		stat = null;
		
		//Update the references table
		try {
			stat = connection.prepareStatement("INSERT INTO `references` SET "+
					"genome=?, javaclass=?, version=?, tablename=?, "+
					"updated=?, linktemplate=?, sourceURI=?, defaultVersion=?");
			stat.setString(1, ref.getGenome());
			stat.setString(2, ref.getAnnotationClass().getCanonicalName());
			stat.setString(3, ref.getVersion());
			stat.setString(4, tableName);
			if (ref.getUpdateDate() != null)
				stat.setDate(5, new java.sql.Date(ref.getUpdateDate().getTime()));
			else
				stat.setNull(5, java.sql.Types.DATE);
			stat.setString(6, ref.getLinkTemplate());
			stat.setString(7, ref.getSource().toString());
			stat.setBoolean(8, ref.isDefault());
			stat.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e) {}
		}
		
		//And actually import
		importer.setConnection(connection);
		importer.setTableName(tableName);
		return importer.importAnnotations(input);
	}

	private void loadAnnotationTables() {
		versions.clear();
		defaults.clear();
		tableNames.clear();
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			stat = connection.prepareStatement("SELECT * FROM `references` WHERE genome=?");
			stat.setString(1, genome);
			rs = stat.executeQuery();
			while (rs.next()) {
				ReferenceMetadata<?> rmd = createMetadataFromRS(rs);
				if (!versions.containsKey(rmd.getAnnotationClass()))
					versions.put(rmd.getAnnotationClass(), new ArrayList<ReferenceMetadata<?>>());
				versions.get(rmd.getAnnotationClass()).add(rmd);
				if (rmd.isDefault())
					defaults.put(rmd.getAnnotationClass(), rmd);
				tableNames.put(rmd, rs.getString("tablename"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e) {}
		}
	}

	private static Genome createGenomeFromRS(ResultSet rs) {
		Genome g;
		try {
			g = new Genome(rs.getString("id"),
					  rs.getString("organism"),
					  rs.getInt("taxid"),
					  rs.getString("sourceURI"));
			g.setTransTableId(rs.getInt("translateTable"));
			g.setAltTransTableId(rs.getInt("altTranslateTable"));
			for (String ch : rs.getString("altTranslateChroms").split(","))
				g.addAltTransChromosome(ch);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return g;
	}
	
	@SuppressWarnings("unchecked")
	private static ReferenceMetadata<?> createMetadataFromRS(ResultSet rs) {
		try {
			Class<? extends ReferenceAnnotation> cls = (Class<? extends ReferenceAnnotation>) Class.forName(rs.getString("javaclass"));
			@SuppressWarnings("rawtypes")
			ReferenceMetadata<?> rmd = new ReferenceMetadata(cls, rs.getString("genome"), rs.getString("version"));
			rmd.setUpdateDate(rs.getDate("updated"));
			rmd.setLinkTemplate(rs.getString("linktemplate"));
			rmd.setSource(rs.getString("sourceURI"));
			rmd.setDefault(rs.getBoolean("defaultVersion"));
			return rmd;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {}
	}
	
	class ShutdownRunnable implements Runnable {
		private final Connection cxn;
		public ShutdownRunnable(final Connection cxn) {
			this.cxn = cxn;
		}
		@Override
		public void run() {
			try {
				cxn.close();
			} catch (Exception e) {				
			}
		}
	}
	
	private Connection connection;
	private Thread connectionCloser;
	private String genome;
	private Genome curGenome;
	private Map<Class<? extends ReferenceAnnotation>, List<ReferenceMetadata<?>>> versions;
	private Map<Class<? extends ReferenceAnnotation>, ReferenceMetadata<?>> defaults;
	private Map<ReferenceMetadata<?>, String> tableNames;
	private Map<Class<? extends ReferenceAnnotation>, JdbcLoader<?>> loaderCache;
}
