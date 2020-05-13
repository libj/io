/* Copyright (c) 2016 LibJ
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
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility functions for operations pertaining to {@link Reader}.
 */
public final class Readers {
  private static final Logger logger = LoggerFactory.getLogger(Readers.class);

  /**
   * Returns a string of the remaining contents from the specified
   * {@link Reader}.
   *
   * @param reader The {@link Reader}.
   * @return A string of the remaining contents from the specified
   *         {@link Reader}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If the specified {@link Reader} is null.
   */
  public static String readFully(final Reader reader) throws IOException {
    return readFully(reader, new StringBuilder()).toString();
  }

  /**
   * Reads the remaining contents from the specified {@link Reader} into the
   * provided {@link StringBuilder}.
   *
   * @param reader The {@link Reader}.
   * @param builder The {@link StringBuilder} into which the specified
   *          {@link Reader} is to be read.
   * @return The provided {@link StringBuilder}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If the specified {@code reader} or
   *           {@code builder} is null.
   */
  public static StringBuilder readFully(final Reader reader, final StringBuilder builder) throws IOException {
    for (int ch; (ch = reader.read()) != -1; builder.append((char)ch));
    return builder;
  }

  /**
   * Returns a string of the remaining contents from the specified
   * {@link Reader}.
   *
   * @param reader The {@link Reader}.
   * @param bufferSize The size of the read buffer to use when reading.
   * @return A string of the remaining contents from the specified
   *         {@link Reader}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If the specified {@link Reader} is null.
   * @throws IllegalArgumentException If {@code bufferSize} is negative.
   */
  public static String readFully(final Reader reader, final int bufferSize) throws IOException {
    return readFully(reader, new StringBuilder(), bufferSize).toString();
  }

  /**
   * Reads the remaining contents from the specified {@link Reader} into the
   * provided {@link StringBuilder}.
   *
   * @param reader The {@link Reader}.
   * @param builder The {@link StringBuilder} into which the specified
   *          {@link Reader} is to be read.
   * @param bufferSize The size of the read buffer to use when reading.
   * @return The provided {@link StringBuilder}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If the specified {@code reader} or
   *           {@code builder} is null.
   * @throws IllegalArgumentException If {@code bufferSize} is negative.
   */
  public static StringBuilder readFully(final Reader reader, final StringBuilder builder, final int bufferSize) throws IOException {
    if (bufferSize <= 0)
      throw new IllegalArgumentException("Buffer size (" + bufferSize + ") must be greater than 0");

    final char[] cbuf = new char[bufferSize];
    for (int size; (size = reader.read(cbuf)) > 0; builder.append(cbuf, 0, size));
    return builder;
  }

  private Readers() {
  }
}