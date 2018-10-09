# IO

**Java API Extensions for IO**

IO is a supplementary extension to the `java.io` and `java.nio` packages.

## Classes

* **[FastFiles](src/main/java/org/fastjax/io/FastFiles.java)**: Utility functions for operations pertaining to `java.io.File` and `java.nio.file.Path`.
* **[Readers](src/main/java/org/fastjax/io/Readers.java)**: Utility functions for operations pertaining to `java.io.Reader`.
* **[ReplayInputStream](src/main/java/org/fastjax/io/ReplayInputStream.java)**: Implementation of a FilterInputStream that allows its content to be re-read.
* **[ReplayReader](src/main/java/org/fastjax/io/ReplayReader.java)**: Implementation of a FilterReader that allows its content to be re-read.
* **[Streams](src/main/java/org/fastjax/io/Streams.java)**: Utility functions for operations pertaining to `java.io.InputStream` and `java.io.OutputStream`.
* **[TeeOutputStream](src/main/java/org/fastjax/io/TeeOutputStream.java)**: `java.io.OutputStream` that propagates its method calls to an array of output streams.
* **[UnicodeReader](src/main/java/org/fastjax/io/UnicodeReader.java)**: A `java.io.Reader` for decoding streams of escaped unicode encoded strings.

### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.