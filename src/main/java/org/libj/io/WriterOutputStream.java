/* Copyright (c) 2023 LibJ
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
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.libj.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Objects;

/**
 * An {@link OutputStream} implementation that writes a byte stream to a {@link Writer} using a specified charset encoding (i.e. the
 * inverse transformation of {@link java.io.OutputStreamWriter}). The stream is transformed using a {@link CharsetDecoder} object,
 * guaranteeing that all charset encodings supported by the JRE are handled correctly.
 * <p>
 * Input data is transformed by the {@link WriterOutputStream} via the {@link CharsetDecoder} with a fixed size buffer. By default,
 * the buffer is flushed only when it overflows or when {@link #flush()} or {@link #close()} is called, and the
 * {@link WriterOutputStream} can also be instructed to flush the buffer after each write operation. In this case, all available
 * data is written immediately to the underlying {@link Writer}.
 *
 * @see ReaderInputStream
 */
public class WriterOutputStream extends OutputStream {
  private static final int DEFAULT_BUFFER_SIZE = 1024;

  private final Writer writer;
  private final CharsetDecoder decoder;
  private final boolean flushImmediately;

  /** {@link ByteBuffer} used as input for the decoder. */
  private final ByteBuffer decoderIn;

  /** {@link CharBuffer} used as output for the decoder. */
  private final CharBuffer decoderOut;

  /**
   * Creates a new {@link WriterOutputStream} with the target {@link Writer}, {@link CharsetDecoder}, {@code bufferSize} and
   * {@code flushImmediately} flag.
   *
   * @param writer The target {@link Writer}.
   * @param decoder The {@link CharsetDecoder}.
   * @param bufferSize the size of the output buffer in number of characters
   * @param flushImmediately If {@code true} the output buffer will be flushed after each write operation. If {@code false}, the
   *          output buffer will only be flushed when it overflows or when {@link #flush()} or {@link #close()} is called.
   * @throws IllegalArgumentException If the capacity is a negative integer.
   * @throws NullPointerException If the provided {@link Writer} or {@link CharsetDecoder} is null.
   */
  public WriterOutputStream(final Writer writer, final CharsetDecoder decoder, final int bufferSize, final boolean flushImmediately) {
    checkIbmJdkWithBrokenUTF16(decoder.charset());
    this.writer = Objects.requireNonNull(writer);
    this.decoder = decoder;
    this.flushImmediately = flushImmediately;
    this.decoderIn = ByteBuffer.allocate(bufferSize);
    this.decoderOut = CharBuffer.allocate(bufferSize);
  }

  /**
   * Creates a new {@link WriterOutputStream} with the target {@link Writer}, {@link CharsetDecoder}, and a default output buffer
   * size of {@value #DEFAULT_BUFFER_SIZE} characters. The output buffer will only be flushed when it overflows or when
   * {@link #flush()} or {@link #close()} is called.
   *
   * @param writer The target {@link Writer}.
   * @param decoder The {@link CharsetDecoder}.
   * @throws NullPointerException If the provided {@link Writer} or {@link CharsetDecoder} is null.
   */
  public WriterOutputStream(final Writer writer, final CharsetDecoder decoder) {
    this(writer, decoder, DEFAULT_BUFFER_SIZE, false);
  }

  /**
   * Creates a new {@link WriterOutputStream} with the target {@link Writer}, {@link Charset}, {@code bufferSize} and
   * {@code flushImmediately} flag.
   *
   * @param writer The target {@link Writer}.
   * @param charset The {@link Charset}.
   * @param bufferSize the size of the output buffer in number of characters
   * @param flushImmediately If {@code true} the output buffer will be flushed after each write operation. If {@code false}, the
   *          output buffer will only be flushed when it overflows or when {@link #flush()} or {@link #close()} is called.
   * @throws IllegalArgumentException If the capacity is a negative integer.
   * @throws NullPointerException If the provided {@link Writer} or {@link Charset} is null.
   */
  public WriterOutputStream(final Writer writer, final Charset charset, final int bufferSize, final boolean flushImmediately) {
    this(writer, charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith("?"), bufferSize, flushImmediately);
  }

  /**
   * Creates a new {@link WriterOutputStream} with the target {@link Writer}, {@link Charset}, and a default output buffer size of
   * {@value #DEFAULT_BUFFER_SIZE} characters. The output buffer will only be flushed when it overflows or when {@link #flush()} or
   * {@link #close()} is called.
   *
   * @param writer The target {@link Writer}.
   * @param charset The {@link Charset}.
   * @throws NullPointerException If the provided {@link Writer} or {@link Charset} is null.
   */
  public WriterOutputStream(final Writer writer, final Charset charset) {
    this(writer, charset, DEFAULT_BUFFER_SIZE, false);
  }

  /**
   * Creates a new {@link WriterOutputStream} with the target {@link Writer}, {@code charsetName}, {@code bufferSize} and
   * {@code flushImmediately} flag.
   *
   * @param writer The target {@link Writer}.
   * @param charsetName The charset name.
   * @param bufferSize the size of the output buffer in number of characters
   * @param flushImmediately If {@code true} the output buffer will be flushed after each write operation. If {@code false}, the
   *          output buffer will only be flushed when it overflows or when {@link #flush()} or {@link #close()} is called.
   * @throws IllegalCharsetNameException If the given charset name is illegal.
   * @throws IllegalArgumentException If the capacity is a negative integer.
   * @throws NullPointerException If the provided {@link Writer} is null.
   */
  public WriterOutputStream(final Writer writer, final String charsetName, final int bufferSize, final boolean flushImmediately) throws IllegalCharsetNameException {
    this(writer, Charset.forName(charsetName), bufferSize, flushImmediately);
  }

  /**
   * Creates a new {@link WriterOutputStream} with the target {@link Writer}, {@code charsetName}, and a default output buffer size of
   * {@value #DEFAULT_BUFFER_SIZE} characters. The output buffer will only be flushed when it overflows or when {@link #flush()} or
   * {@link #close()} is called.
   *
   * @param writer The target {@link Writer}.
   * @param charsetName The charset name.
   * @throws IllegalCharsetNameException If the given charset name is illegal.
   * @throws NullPointerException If the provided {@link Writer} is null.
   */
  public WriterOutputStream(final Writer writer, final String charsetName) throws IllegalCharsetNameException {
    this(writer, charsetName, DEFAULT_BUFFER_SIZE, false);
  }

  /**
   * Creates a new {@link WriterOutputStream} with the target {@link Writer}, the {@linkplain Charset#defaultCharset() default
   * charset}, and a default output buffer size of {@value #DEFAULT_BUFFER_SIZE} characters. The output buffer will only be flushed
   * when it overflows or when {@link #flush()} or {@link #close()} is called.
   *
   * @param writer the target {@link Writer}
   * @throws NullPointerException If the provided {@link Writer} is null.
   */
  @Deprecated
  public WriterOutputStream(final Writer writer) {
    this(writer, Charset.defaultCharset(), DEFAULT_BUFFER_SIZE, false);
  }

  @Override
  public void write(final byte[] b, int off, int len) throws IOException {
    for (int r; len > 0; len -= r, off += r) {
      r = Math.min(len, decoderIn.remaining());
      decoderIn.put(b, off, r);
      processInput(false);
    }

    if (flushImmediately)
      flushBuffer();
  }

  @Override
  public void write(final byte[] b) throws IOException {
    write(b, 0, b.length);
  }

  @Override
  public void write(final int b) throws IOException {
    decoderIn.put((byte)b);
    processInput(false);

    if (flushImmediately)
      flushBuffer();
  }

  /**
   * Flush the stream, writing and flushing all remaining content accumulated in the output buffer to the underlying {@link Writer}.
   *
   * @throws IOException If an I/O error has occurred.
   */
  @Override
  public void flush() throws IOException {
    flushBuffer();
    writer.flush();
  }

  /**
   * Close the stream, first writing and flushing all remaining content accumulated in the output buffer to the underlying
   * {@link Writer}.
   *
   * @throws IOException If an I/O error has occurred.
   */
  @Override
  public void close() throws IOException {
    processInput(true);
    flushBuffer();
    writer.close();
  }

  protected void flushBuffer() throws IOException {
    final int pos = decoderOut.position();
    if (pos > 0) {
      writer.write(decoderOut.array(), 0, pos);
      decoderOut.rewind();
    }
  }

  /**
   * Decode the contents of the input ByteBuffer into a CharBuffer.
   *
   * @param endOfInput indicates end of input
   * @throws IOException if an I/O error occurs.
   */
  private void processInput(final boolean endOfInput) throws IOException {
    decoderIn.flip(); // Prepare decoderIn for reading
    for (CoderResult coderResult;;) {
      coderResult = decoder.decode(decoderIn, decoderOut, endOfInput);
      if (coderResult.isOverflow()) {
        flushBuffer();
      }
      else if (coderResult.isUnderflow()) {
        break;
      }
      else {
        // The decoder is configured to replace malformed input and unmappable characters, so we should not get here.
        throw new IOException("Unexpected coder result");
      }
    }

    decoderIn.compact(); // Discard the bytes that have been read
  }

  /**
   * Check if the JDK in use properly supports the given charset.
   *
   * @param charset The charset to check the support for.
   */
  private static void checkIbmJdkWithBrokenUTF16(final Charset charset) {
    if (!"UTF-16".equals(charset.name()))
      return;

    final String TEST_STRING_2 = "v\u00e9s";
    final byte[] bytes = TEST_STRING_2.getBytes(charset);

    final CharsetDecoder charsetDecoder2 = charset.newDecoder();
    final ByteBuffer bb2 = ByteBuffer.allocate(16);
    final CharBuffer cb2 = CharBuffer.allocate(TEST_STRING_2.length());
    try {
      for (int i = 0, len = bytes.length; i < len; ++i) {
        bb2.put(bytes[i]);
        bb2.flip();
        charsetDecoder2.decode(bb2, cb2, i == len - 1);

        bb2.compact();
      }

      cb2.rewind();
      if (TEST_STRING_2.equals(cb2.toString()))
        return;
    }
    catch (final IllegalArgumentException e) {
    }

    throw new UnsupportedOperationException("UTF-16 requested when running on an IBM JDK with broken UTF-16 support. Please find a JDK that supports UTF-16 if you intend to use UTF-16 with WriterOutputStream");
  }
}