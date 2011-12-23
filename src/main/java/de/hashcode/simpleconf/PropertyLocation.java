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
