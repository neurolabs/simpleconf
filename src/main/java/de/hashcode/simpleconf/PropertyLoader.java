/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.hashcode.simpleconf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;

/**
 * Loads properties from one or more {@link PropertyLocation}s and writes them
 * to system properties.
 * 
 * @author Dennis Brakhane <brakhane@googlemail.com> (initial creation)
 * @see #loadProperties(Collection, Object)
 */
public final class PropertyLoader {

	private static final Logger LOG = Logger
			.getLogger("de.hashcode.simpleconf.PropertyLoader");

	/**
	 * Non instantiable.
	 */
	private PropertyLoader() {
	}

	/**
	 * Convenience method. Identical with
	 * <code>loadProperties( Collections.singleton( location ), data )</code>.
	 * 
	 * @param <T>
	 *            Data type for {@link PropertyLocation}
	 * @param location
	 *            The location
	 * @param data
	 *            The data for the location
	 * @throws IOException
	 *             if there's a problem while reading the properties.
	 */
	public static <T> void loadProperties(
			@Nonnull final PropertyLocation<T> location, @Nullable final T data)
			throws IOException {
		loadProperties(Collections.singleton(location), data);
	}

	/**
	 * Reads the properties and writes them to system properties. Existing
	 * system properties are not overridden.
	 * 
	 * @param <T>
	 *            Data type for {@link PropertyLocation}
	 * @param locations
	 *            The locations from which should be read.
	 * @param data
	 *            Data for the locations
	 * @throws IOException
	 *             if there's a problem while reading the properties.
	 */
	public static <T> void loadProperties(
			@Nonnull final Collection<? extends PropertyLocation<T>> locations,
			@Nullable final T data) throws IOException {
		final Properties properties = new Properties();

		for (final PropertyLocation<T> location : locations) {
			properties.putAll(read(location, data));
		}
		if (properties.isEmpty()) {
			LOG.warning("No property file found at any possible location, not processing any properties");
		} else {
			writeToSystemProperties(properties);
		}
	}

	private static void writeToSystemProperties(
			@Nonnull final Map<Object, Object> properties) {
		for (final Entry<Object, Object> entry : properties.entrySet()) {
			final String keyAsString = String.valueOf(entry.getKey());
			final String valueAsString = String.valueOf(entry.getValue());
			final String existingValue = System.getProperty(keyAsString);
			if (existingValue == null) {
				LOG.log(Level.FINE, String.format(
						"Setting System Property '%s' to value '%s'.",
						keyAsString, valueAsString));
				System.setProperty(keyAsString, valueAsString);
			} else {
				LOG.log(Level.INFO,
						String.format(
								"Not setting System Property '%s' to value '%s', because it is already set to '%s'.",
								keyAsString, valueAsString, existingValue));
			}
		}
	}

	@Nonnull
	private static <T> Properties read(
			@Nonnull final PropertyLocation<T> location, @Nullable final T data)
			throws IOException {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE,
					String.format(
							"Trying to read application properties from %s, source '%s'.",
							location.getName(), location.getSource()));
		}

		final InputStream inputStream = location.getInputStream(data);
		final Properties properties = new Properties();

		if (inputStream == null) {
			return properties;
		}

		try {
			if (location.isXmlProperties()) {
				properties.loadFromXML(inputStream);
			} else {
				properties.load(inputStream);
			}
			if (LOG.isLoggable(Level.FINE)) {
				LOG.log(Level.FINE, String.format(
						"Read application properties from %s, source '%s'.",
						location.getName(), location.getSource()));
			}
			inputStream.close();
		} catch (final IOException e) {
			LOG.log(Level.SEVERE,
					String.format("IO Error on file from %s.",
							location.getName()));
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		return properties;
	}

}
