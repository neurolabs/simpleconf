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
import java.io.StringReader;
import java.util.Arrays;

import org.apache.commons.io.input.ReaderInputStream;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link PropertyLoader} class.
 * 
 * @author Ole Langbehn (ole.langbehn@googlemail.com)
 * 
 */
public class PropertyLoaderTest {

	private static final String XML_FOO_AND_FOOBAR = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">"
			+ "<properties>"
			+ "<entry key=\"foo\">bar</entry>"
			+ "<entry key=\"foobar\">baz</entry>"
			+ "</properties>";
	private static final String PLAIN_FOO_AND_FOOBAR = "foo=bar\nfoobar=baz";

	@Before
	public void clearPropertiesUnderTest() {
		System.clearProperty("foo");
		System.clearProperty("foobar");
	}

	private static final class TestPropertyLocation implements
			PropertyLocation<Void> {

		private final boolean isXml;
		private final String content;

		public TestPropertyLocation(boolean isXml, String content) {
			this.isXml = isXml;
			this.content = content;
		}

		@Override
		public InputStream getInputStream(Void data) throws IOException {
			return new ReaderInputStream(new StringReader(content));
		}

		@Override
		public String getName() {
			return "test";
		}

		@Override
		public String getSource() {
			return "test";
		}

		@Override
		public boolean isXmlProperties() {
			return isXml;
		}
	}

	@Test
	public void testCorrectPropertiesImportOrder() {
		try {
			PropertyLoader.loadProperties(Arrays
					.asList(new TestPropertyLocation[] {
							new TestPropertyLocation(false,
									PLAIN_FOO_AND_FOOBAR),
							new TestPropertyLocation(false,
									"foobar=overwritten") }), null);
		} catch (IOException e) {
			fail("Exception while testing reading plain format");
		}
		assertThat(System.getProperty("foo"), equalTo("bar"));
		assertThat(System.getProperty("foobar"), equalTo("overwritten"));
	}

	@Test
	public void testExistingPropertiesAreNotOverwritten() {
		System.setProperty("foo", "existing");
		internalLoadProperties(true, XML_FOO_AND_FOOBAR);

		assertThat(System.getProperty("foo"), equalTo("existing"));
	}

	@Test
	public void testXmlFormat() {
		internalTestFormat(true, XML_FOO_AND_FOOBAR);
	}

	@Test
	public void testPlainFormat() {
		internalTestFormat(false, PLAIN_FOO_AND_FOOBAR);
	}

	private void internalTestFormat(boolean isXml, String content) {
		internalLoadProperties(isXml, content);
		assertThat(System.getProperty("foo"), equalTo("bar"));
		assertThat(System.getProperty("foobar"), equalTo("baz"));
	}

	private void internalLoadProperties(boolean isXml, String content) {
		try {
			PropertyLoader.loadProperties(new TestPropertyLocation(isXml,
					content), null);

		} catch (IOException e) {
			fail("Exception while testing reading plain format");
		}
	}
}
