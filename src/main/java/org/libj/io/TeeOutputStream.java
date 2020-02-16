/* Copyright (c) 2006 LibJ
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
import java.io.OutputStream;

/**
 * {@link OutputStream} that delegates its method calls to an array of output
 * streams.
 */
public class TeeOutputStream extends OutputStream {
  private final OutputStream[] streams;

  /**
   * Construct a new {@link TeeOutputStream} with the specified
   * {@link OutputStream} instances.
   * <p>
   * Streams will be written to in the order of the provided array.
   *
   * @param streams The streams to which this stream's method calls will be
   *          delegated.
   * @throws IllegalArgumentException If {@code streams} is empty, or if any
   *           stream in the {@code streams} array is null.
   * @throws NullPointerException If {@code streams} is null.
   */
  public TeeOutputStream(final OutputStream ... streams) {
    if (streams.length == 0)
      throw new IllegalArgumentException("Empty array");

    for (int i = 0; i < streams.length; ++i)
      if (streams[i] == null)
        throw new IllegalArgumentException("member at index " + i + " is null");

    this.streams = streams;
  }

  @Override
  public void write(final int b) throws IOException {
    IOException exception = null;
    for (int i = 0; i < streams.length; ++i) {
      try {
        streams[i].write(b);
      }
      catch (final IOException e) {
        if (exception == null)
          exception = e;
        else
          exception.addSuppressed(e);
      }
    }

    if (exception != null)
      throw exception;
  }

  @Override
  public void write(final byte[] b) throws IOException {
    IOException exception = null;
    for (int i = 0; i < streams.length; ++i) {
      try {
        streams[i].write(b);
      }
      catch (final IOException e) {
        if (exception == null)
          exception = e;
        else
          exception.addSuppressed(e);
      }
    }

    if (exception != null)
      throw exception;
  }

  @Override
  public void write(final byte[] b, final int off, final int len) throws IOException {
    IOException exception = null;
    for (int i = 0; i < streams.length; ++i) {
      try {
        streams[i].write(b, off, len);
      }
      catch (final IOException e) {
        if (exception == null)
          exception = e;
        else
          exception.addSuppressed(e);
      }
    }

    if (exception != null)
      throw exception;
  }

  @Override
  public void flush() throws IOException {
    IOException exception = null;
    for (int i = 0; i < streams.length; ++i) {
      try {
        streams[i].flush();
      }
      catch (final IOException e) {
        if (exception == null)
          exception = e;
        else
          exception.addSuppressed(e);
      }
    }

    if (exception != null)
      throw exception;
  }

  @Override
  public void close() throws IOException {
    IOException exception = null;
    for (int i = 0; i < streams.length; ++i) {
      try {
        streams[i].close();
      }
      catch (final IOException e) {
        if (exception == null)
          exception = e;
        else
          exception.addSuppressed(e);
      }
    }

    if (exception != null)
      throw exception;
  }
}