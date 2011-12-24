# simpleconf

**simple configuration for java applications**

Simpleconf is a simple method of configuring your java (web) app. It reads java properties files and exposes the contained properties as java system properties. It makes it possible to deploy a default set of properties with an application, but also makes it possible to override and extend the set of properties per deployed instance.

## Usage

### Web Application

For usage in a web application, simpleconf brings with it a ServletContextListener, which by default reads a file from the war file/classpath, and then looks for an instance specific file provided by a system property at startup.

#### Configuration
TODO
- default locations
- custom locations
### Java Application

When using it in a plain old java app, you can roll your own behaviour very easily. TODO: example

#### Configuration
TODO
- default locations
- custom locations

## Properties file formats

Simpleconf can handle both plain java properties files and xml properties files. With xml properties files, you can specify a character encoding for the properties.

## Dependencies

simpleconf has been designed to have as little dependencies as possible. Right now there are no dependencies for using it in a web app.

### Logging

Logging is done via java.util.logging. That way we don't force a logging framework upon users. But we can recommend using slf4j with it's excellent jul-over-slf4j bridge.
