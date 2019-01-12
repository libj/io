# OpenJAX Standard IO

> Java API Extensions for IO

[![Build Status](https://travis-ci.org/openjax/standard-io.png)](https://travis-ci.org/openjax/standard-io)

IO is a supplementary extension to the `java.io` and `java.nio` packages.

## Classes

* **[FastFiles](src/main/java/org/openjax/standard/io/FastFiles.java)**: Utility functions for operations pertaining to `java.io.File` and `java.nio.file.Path`.
* **[Readers](src/main/java/org/openjax/standard/io/Readers.java)**: Utility functions for operations pertaining to `java.io.Reader`.
* **[ReplayInputStream](src/main/java/org/openjax/standard/io/ReplayInputStream.java)**: Implementation of a FilterInputStream that allows its content to be re-read.
* **[ReplayReader](src/main/java/org/openjax/standard/io/ReplayReader.java)**: Implementation of a FilterReader that allows its content to be re-read.
* **[Streams](src/main/java/org/openjax/standard/io/Streams.java)**: Utility functions for operations pertaining to `java.io.InputStream` and `java.io.OutputStream`.
* **[TeeOutputStream](src/main/java/org/openjax/standard/io/TeeOutputStream.java)**: `java.io.OutputStream` that propagates its method calls to an array of output streams.
* **[UnicodeReader](src/main/java/org/openjax/standard/io/UnicodeReader.java)**: A `java.io.Reader` for decoding streams of escaped unicode encoded strings.

### JavaDocs

JavaDocs are available [here](https://standard.openjax.org/io/apidocs/).

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.