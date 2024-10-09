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
import java.util.Objects;

/**
 * {@link OutputStream} that delegates its method calls to an array of output streams.
 */
public class TeeOutputStream extends OutputStream {
  private final OutputStream stream;
  private final OutputStream[] streams;
  private final int len;

  /**
   * Construct a new {@link TeeOutputStream} with the specified {@link OutputStream} instances.
   * <p>
   * Streams will be written to in the order of the provided array.
   *
   * @param stream The first stream to which this stream's method calls will be delegated.
   * @param streams The other streams to which this stream's method calls will be delegated.
   * @throws NullPointerException If {@code stream} or {@code streams} is null, or if any stream in the {@code streams} array is null.
   */
  public TeeOutputStream(final OutputStream stream, final OutputStream ... streams) {
    this.stream = Objects.requireNonNull(stream, "member at index 0 is null");;
    this.streams = streams;
    this.len = streams.length;
    for (int i = 0; i < len; ++i) // [A]
      Objects.requireNonNull(streams[i], "member at index " + (i + 1) + " is null");
  }

  @Override
  public void write(final int b) throws IOException {
    IOException exception = null;
    try {
      stream.write(b);
    }
    catch (final IOException e) {
      exception = e;
    }

    for (int i = 0; i < len; ++i) { // [A]
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
    try {
      stream.write(b);
    }
    catch (final IOException e) {
      exception = e;
    }

    for (int i = 0; i < len; ++i) { // [A]
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
    try {
      stream.write(b, off, len);
    }
    catch (final IOException e) {
      exception = e;
    }

    for (int i = 0; i < len; ++i) { // [A]
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
    try {
      stream.flush();
    }
    catch (final IOException e) {
      exception = e;
    }

    for (int i = 0; i < len; ++i) { // [A]
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
    try {
      stream.close();
    }
    catch (final IOException e) {
      exception = e;
    }

    for (int i = 0; i < len; ++i) { // [A]
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