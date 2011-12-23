package de.hashcode.simpleconf;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A configuration location.
 * 
 * @param <T>
 *            Data type for {@link #getInputStream(Object)}
 * @author Ole Langbehn (ole.langbehn@googlemail.com) (initial creation)
 */
public interface PropertyLocation<T> {
	/**
	 * Returns the configuration as a stream.
	 * 
	 * @throws IOException
	 *             if an input stream can't be returned.
	 */
	@CheckForNull
	InputStream getInputStream(@Nullable final T data) throws IOException;

	/**
	 * Returns the name of this location.
	 */
	@Nonnull
	String getName();

	/**
	 * Returns the source of this location.
	 */
	@Nonnull
	String getSource();

	/**
	 * Returns if the properties are in xml format.
	 */
	boolean isXmlProperties();
}
