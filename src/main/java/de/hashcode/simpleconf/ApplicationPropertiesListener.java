package de.hashcode.simpleconf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.UnhandledException;

/**
 * Reads some properties files on context startup and provides these properties
 * via system properties. First off, a file provided via a system property is
 * read. This way properties are overridable per server.
 * 
 * @author Ole Langbehn (ole.langbehn@googlemail.com) (initial creation)
 */
public class ApplicationPropertiesListener implements ServletContextListener {

	private static final Logger LOG = Logger
			.getLogger("de.hashcode.simpleconf");

	/**
	 * Types of configuration locations.
	 * 
	 * @author Ole Langbehn (ole.langbehn@googlemail.com) (initial creation)
	 */
	private enum LocationType {
		CLASSPATH, SYSTEM_PROPERTY;
	}

	/**
	 * The different configuration locations defining the place where to look
	 * and provide a method of access.
	 * 
	 * @author Ole Langbehn (ole.langbehn@googlemail.com) (initial creation)
	 */
	protected enum DefaultLocation implements
			PropertyLocation<ServletContextEvent> {
		CLASSPATH_PLAIN("classpath", LocationType.CLASSPATH,
				"/WEB-INF/application.properties"), CLASSPATH_XML("classpath",
				LocationType.CLASSPATH, "/WEB-INF/applicationProperties.xml"), SYSTEM_PROPERTY(
				"system property", LocationType.SYSTEM_PROPERTY,
				"application.properties.path");

		private final String name;
		private final LocationType type;
		private final String source;

		private DefaultLocation(@Nonnull final String name,
				@Nonnull final LocationType type, @Nonnull final String source) {
			this.name = name;
			this.type = type;
			this.source = source;
		}

		@Override
		@CheckForNull
		public InputStream getInputStream(
				@Nullable final ServletContextEvent event)
				throws FileNotFoundException {
			switch (this.type) {
			case CLASSPATH:
				if (event == null) {
					throw new IllegalStateException(
							"Can't read from classpath without a servlet context.");
				}
				return classpathToStream(this.source, event);
			case SYSTEM_PROPERTY:
				final String path = System.getProperty(this.source);
				if (path == null) {
					return null;
				}
				return pathToStream(path);
			default:
				return null;
			}
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public String getSource() {
			return this.source;
		}

		@CheckForNull
		private InputStream classpathToStream(@Nullable final String path,
				@Nonnull final ServletContextEvent event) {
			if (path != null) {
				return event.getServletContext().getResourceAsStream(path);
			}
			return null;
		}

		@CheckForNull
		private InputStream pathToStream(@Nonnull final String path)
				throws FileNotFoundException {
			final File file = new File(path);
			if (file.exists() && file.isFile() && file.canRead()) {
				return new FileInputStream(file);
			}
			throw new FileNotFoundException(
					"Configured Properties file cannot be found: "
							+ file.getAbsolutePath());
		}

		@Override
		public boolean isXmlProperties() {
			switch (this.type) {
			case CLASSPATH:
				return endsWithXml(this.source);
			case SYSTEM_PROPERTY:
				final String path = System.getProperty(this.source);
				if (path == null) {
					return false;
				}
				return endsWithXml(path);
			default:
				return false;
			}
		}

		private boolean endsWithXml(@Nonnull final String string) {
			return string.toLowerCase().endsWith(".xml");
		}

	}

	private final List<? extends PropertyLocation<ServletContextEvent>> locations;

	public ApplicationPropertiesListener() {
		this.locations = Arrays.asList(DefaultLocation.values());
	}

	public ApplicationPropertiesListener(
			@Nonnull final PropertyLocation<ServletContextEvent>... locations) {
		this.locations = Arrays.asList(locations);
	}

	@Override
	public void contextDestroyed(@Nonnull final ServletContextEvent event) {
		// nothing to do
	}

	@Override
	public void contextInitialized(@Nullable final ServletContextEvent event) {
		try {
			PropertyLoader.loadProperties(this.locations, event);
		} catch (final Exception e) {
			logAndRethrow(e);
		}
	}

	private void logAndRethrow(@Nonnull final Throwable e) {
		LOG.log(Level.SEVERE, "Error during initialization", e);
		throw e instanceof RuntimeException ? (RuntimeException) e
				: new UnhandledException(e);
	}
}
