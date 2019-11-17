/* Copyright (c) 2019 LibJ
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.libj.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Objects;

import org.libj.util.Assertions;

/**
 * Implementation of {@link InputStream} that reads a character stream from a
 * {@link Reader}, and transforms it to a byte stream using a specified charset
 * encoding. The characters are transformed via the {@link CharsetEncoder},
 * which guarantees that all charset encodings supported by the JRE are handled
 * correctly. In particular for charsets such as UTF-16, the implementation
 * ensures that one and only one byte order marker is produced.
 * <p>
 * Reads from the source {@link Reader} are buffered, because it is not possible
 * to predict the number of characters needed to be read from the {@link Reader}
 * to satisfy a read request on the {@link ReaderInputStream}. Therefore, in
 * general, there is no need to wrap the underlying {@link Reader} with a
 * {@link java.io.BufferedReader}.
 * <p>
 * The {@link ReaderInputStream} implements the inverse transformation of
 * {@link java.io.InputStreamReader}. The following example illustrates how
 * reading from {@code in2} would return the same byte sequence as reading from
 * {@code in} (provided that the initial byte sequence is legal with respect to
 * the charset encoding):
 *
 * <pre>
 * InputStream in = ...
 * Charset cs = ...
 * InputStreamReader reader = new InputStreamReader(in, cs);
 * ReaderInputStream in2 = new ReaderInputStream(reader, cs);
 * </pre>
 *
 * The {@link ReaderInputStream} implements the same transformation as
 * {@link java.io.OutputStreamWriter}, except that the control flow is reversed
 * -- both classes transform a character stream into a byte stream, but
 * {@link java.io.OutputStreamWriter} pushes data <i>to</i> the underlying
 * stream, while {@link ReaderInputStream} pulls data <i>from</i> the underlying
 * stream.
 * <p>
 * Given the fact that the {@link Reader} class doesn't provide any way to
 * predict whether the next read operation will block or not, it is not possible
 * to provide a meaningful implementation of the {@link InputStream#available()}
 * method. A call to this method will always return 0. Also, this class does not
 * support {@link InputStream#mark(int)}.
 * <p>
 * The {@link ReaderInputStream} is not thread safe.
 */
public class ReaderInputStream extends InputStream {
  private static final int DEFAULT_BUFFER_SIZE = 1024;

  private final Reader reader;
  private final CharsetEncoder encoder;

  /**
   * CharBuffer used as input for the decoder. It should be reasonably large as
   * we read data from the underlying Reader into this buffer.
   */
  private final CharBuffer encoderIn;

  /**
   * ByteBuffer used as output for the decoder. This buffer can be small as it
   * is only used to transfer data from the decoder to the buffer provided by
   * the caller.
   */
  private final ByteBuffer encoderOut;

  private CoderResult lastCoderResult;
  private boolean endOfInput;

  /**
   * Construct a new {@link ReaderInputStream} with the specified
   * {@link Reader}, {@link CharsetEncoder}, and buffer size.
   *
   * @param reader The target {@link Reader}.
   * @param encoder The charset encoder.
   * @param bufferSize The size of the input buffer in number of characters.
   * @throws NullPointerException If {@code reader} or {@code encoder} is null.
   * @throws IllegalArgumentException If {@code bufferSize} is negative.
   */
  public ReaderInputStream(final Reader reader, final CharsetEncoder encoder, final int bufferSize) {
    this.reader = Objects.requireNonNull(reader);
    this.encoder = Objects.requireNonNull(encoder);
    this.encoderIn = CharBuffer.allocate(bufferSize);
    this.encoderIn.flip();
    this.encoderOut = ByteBuffer.allocate(128);
    this.encoderOut.flip();
  }

  /**
   * Construct a new {@link ReaderInputStream} with the specified {@link Reader}
   * and {@link CharsetEncoder}.
   *
   * @param reader The target {@link Reader}.
   * @param encoder The charset encoder.
   * @throws NullPointerException If {@code reader} or {@code encoder} is null.
   */
  public ReaderInputStream(final Reader reader, final CharsetEncoder encoder) {
    this(reader, encoder, DEFAULT_BUFFER_SIZE);
  }

  /**
   * Construct a new {@link ReaderInputStream} with the specified
   * {@link Reader}, {@link Charset}, and buffer size. Characters from the
   * specified reader that do not map or are invalid to the provided encoder
   * will be replaced.
   *
   * @param reader The target {@link Reader}.
   * @param charset The charset encoding.
   * @param bufferSize The size of the input buffer in number of characters.
   * @throws NullPointerException If {@code reader} or {@code charset} is null.
   * @throws IllegalArgumentException If {@code bufferSize} is negative.
   */
  public ReaderInputStream(final Reader reader, final Charset charset, final int bufferSize) {
    this(reader, charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE), bufferSize);
  }

  /**
   * Construct a new {@link ReaderInputStream} with the specified
   * {@link Reader}, {@link Charset}, and a default input buffer size of 1024
   * characters. Characters from the specified reader that do not map or are
   * invalid to the provided encoder will be replaced.
   *
   * @param reader The target {@link Reader}.
   * @param charset The charset encoding.
   * @throws NullPointerException If {@code reader} or {@code charset} is null.
   */
  public ReaderInputStream(final Reader reader, final Charset charset) {
    this(reader, charset, DEFAULT_BUFFER_SIZE);
  }

  /**
   * Construct a new {@link ReaderInputStream} with the specified
   * {@link Reader}, {@link Charset}, and buffer size. Characters from the
   * specified reader that do not map or are invalid to the provided encoder
   * will be replaced.
   *
   * @param reader The target {@link Reader}.
   * @param charsetName The name of the charset encoding.
   * @param bufferSize The size of the input buffer in number of characters.
   * @throws NullPointerException If {@code reader} is null.
   * @throws IllegalCharsetNameException If the given charset name is illegal.
   * @throws IllegalArgumentException If {@code bufferSize} is negative.
   */
  public ReaderInputStream(final Reader reader, final String charsetName, final int bufferSize) {
    this(reader, Charset.forName(charsetName), bufferSize);
  }

  /**
   * Construct a new {@link ReaderInputStream} with the specified
   * {@link Reader}, {@link Charset}, and a default input buffer size of 1024
   * characters. Characters from the specified reader that do not map or are
   * invalid to the provided encoder will be replaced.
   *
   * @param reader The target {@link Reader}.
   * @param charsetName The name of the charset encoding.
   * @throws NullPointerException If {@code reader} is null.
   * @throws IllegalCharsetNameException If the given charset name is illegal.
   */
  public ReaderInputStream(final Reader reader, final String charsetName) {
    this(reader, charsetName, DEFAULT_BUFFER_SIZE);
  }

  /**
   * Fills the internal char buffer from the reader.
   *
   * @throws IOException If an I/O error has occurred.
   */
  private void fillBuffer() throws IOException {
    if (!endOfInput && (lastCoderResult == null || lastCoderResult.isUnderflow())) {
      encoderIn.compact();
      final int position = encoderIn.position();
      final int ch = reader.read(encoderIn.array(), position, encoderIn.remaining());
      if (ch == -1)
        endOfInput = true;
      else
        encoderIn.position(position + ch);

      encoderIn.flip();
    }

    encoderOut.compact();
    lastCoderResult = encoder.encode(encoderIn, encoderOut, endOfInput);
    encoderOut.flip();
  }

  /**
   * Read the specified number of bytes into an array.
   *
   * @param b The buffer into which the data is read.
   * @param off The start offset in array <code>b</code> at which the data is
   *          written.
   * @param len The maximum number of bytes to read.
   * @return The total number of bytes read into the buffer, or {@code -1} if
   *         there is no more data because the end of the stream has been
   *         reached.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If the specified array is null.
   * @throws IndexOutOfBoundsException If {@code off} is negative, {@code len}
   *           is negative, or {@code len} is greater than
   *           {@code b.length - off}.
   */
  @Override
  public int read(final byte[] b, int off, int len) throws IOException {
    Assertions.assertBoundsOffsetCount(b.length, off, len, "length", "off", "len");
    if (len == 0)
      return 0;

    int read = 0;
    while (len > 0) {
      if (encoderOut.hasRemaining()) {
        final int count = Math.min(encoderOut.remaining(), len);
        encoderOut.get(b, off, count);
        off += count;
        len -= count;
        read += count;
      }
      else {
        fillBuffer();
        if (endOfInput && !encoderOut.hasRemaining())
          break;
      }
    }

    return read == 0 && endOfInput ? -1 : read;
  }

  /**
   * Read the specified number of bytes into an array.
   *
   * @param b The buffer into which the data is read.
   * @return The total number of bytes read into the buffer, or {@code -1} if
   *         there is no more data because the end of the stream has been
   *         reached.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If the specified array is null.
   */
  @Override
  public int read(final byte[] b) throws IOException {
    return read(b, 0, b.length);
  }

  /**
   * Read a single byte.
   *
   * @return either the byte read or <code>-1</code> if the end of the stream
   *         has been reached
   * @throws IOException if an I/O error occurs
   */
  @Override
  public int read() throws IOException {
    while (true) {
      if (encoderOut.hasRemaining())
        return encoderOut.get() & 0xFF;

      fillBuffer();
      if (endOfInput && !encoderOut.hasRemaining())
        return -1;
    }
  }

  /**
   * Close this stream and the underlying {@link Reader}.
   *
   * @throws IOException If an I/O error has occurred.
   */
  @Override
  public void close() throws IOException {
    reader.close();
  }
}