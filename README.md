# LibJ IO

[![Build Status](https://travis-ci.org/libj/io.svg?1)](https://travis-ci.org/libj/io)
[![Coverage Status](https://coveralls.io/repos/github/libj/io/badge.svg?1)](https://coveralls.io/github/libj/io)
[![Javadocs](https://www.javadoc.io/badge/org.libj/io.svg?1)](https://www.javadoc.io/doc/org.libj/io)
[![Released Version](https://img.shields.io/maven-central/v/org.libj/io.svg?1)](https://mvnrepository.com/artifact/org.libj/io)
![Snapshot Version](https://img.shields.io/nexus/s/org.libj/io?label=maven-snapshot&server=https%3A%2F%2Foss.sonatype.org)

IO is a supplementary extension to the `java.io` and `java.nio` packages.

## Classes

* **[FastFiles](src/main/java/org.libj/io/FastFiles.java)**: Utility functions for operations pertaining to `java.io.File` and `java.nio.file.Path`.
* **[Readers](src/main/java/org.libj/io/Readers.java)**: Utility functions for operations pertaining to `java.io.Reader`.
* **[ReplayInputStream](src/main/java/org.libj/io/ReplayInputStream.java)**: Implementation of a FilterInputStream that allows its content to be re-read.
* **[ReplayReader](src/main/java/org.libj/io/ReplayReader.java)**: Implementation of a FilterReader that allows its content to be re-read.
* **[Streams](src/main/java/org.libj/io/Streams.java)**: Utility functions for operations pertaining to `java.io.InputStream` and `java.io.OutputStream`.
* **[TeeOutputStream](src/main/java/org.libj/io/TeeOutputStream.java)**: `java.io.OutputStream` that propagates its method calls to an array of output streams.
* **[UnicodeReader](src/main/java/org.libj/io/UnicodeReader.java)**: A `java.io.Reader` for decoding streams of escaped unicode encoded strings.

## Contributing

Pull requests are welcome. For major changes, please [open an issue](../../issues) first to discuss what you would like to change.

Please make sure to update tests as appropriate.

### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.