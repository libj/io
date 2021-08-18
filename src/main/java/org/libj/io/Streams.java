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

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import org.libj.lang.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility functions for operations pertaining to {@link InputStream} and
 * {@link OutputStream}.
 */
public final class Streams {
  private static final Logger logger = LoggerFactory.getLogger(Streams.class);
  static final int DEFAULT_SOCKET_BUFFER_SIZE = 65536;

  /**
   * Write a 2-byte {@code short} value to the specified {@link OutputStream} in
   * big-endian encoding.
   *
   * @param out The {@link OutputStream}.
   * @param s The {@code short} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeShort(final OutputStream out, final short s) throws IOException {
    writeShort(out, s, true);
  }

  /**
   * Write a 2-byte {@code short} value to the specified {@link OutputStream}.
   *
   * @param out The {@link OutputStream}.
   * @param s The {@code short} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeShort(final OutputStream out, final short s, final boolean isBigEndian) throws IOException {
    Assertions.assertNotNull(out);
    if (isBigEndian) {
      out.write((byte)((s >> 8) & 0xff));
      out.write((byte)(s & 0xff));
    }
    else {
      out.write((byte)(s & 0xff));
      out.write((byte)((s >> 8) & 0xff));
    }
  }

  /**
   * Write a 2-byte {@code short} value to the specified {@link OutputStream} in
   * big-endian encoding.
   *
   * @param out The {@link OutputStream}.
   * @param s The {@code short} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeShort(final DataOutput out, final short s) throws IOException {
    writeShort(out, s, true);
  }

  /**
   * Write a 2-byte {@code short} value to the specified {@link OutputStream}.
   *
   * @param out The {@link OutputStream}.
   * @param s The {@code short} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeShort(final DataOutput out, final short s, final boolean isBigEndian) throws IOException {
    Assertions.assertNotNull(out);
    if (isBigEndian) {
      out.write((byte)((s >> 8) & 0xff));
      out.write((byte)(s & 0xff));
    }
    else {
      out.write((byte)(s & 0xff));
      out.write((byte)((s >> 8) & 0xff));
    }
  }

  /**
   * Write a 2-byte {@code char} value to the specified {@link OutputStream} in
   * big-endian encoding.
   *
   * @param out The {@link OutputStream}.
   * @param c The {@code char} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeChar(final OutputStream out, final char c) throws IOException {
    writeChar(out, c, true);
  }

  /**
   * Write a 2-byte {@code char} value to the specified {@link OutputStream}.
   *
   * @param out The {@link OutputStream}.
   * @param c The {@code char} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeChar(final OutputStream out, final char c, final boolean isBigEndian) throws IOException {
    Assertions.assertNotNull(out);
    if (isBigEndian) {
      out.write((byte)((c >> 8) & 0xff));
      out.write((byte)(c & 0xff));
    }
    else {
      out.write((byte)(c & 0xff));
      out.write((byte)((c >> 8) & 0xff));
    }
  }

  /**
   * Write a 2-byte {@code char} value to the {@code out} is null in
   * big-endian encoding.
   *
   * @param out The {@link DataOutput}.
   * @param c The {@code char} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeChar(final DataOutput out, final char c) throws IOException {
    writeChar(out, c, true);
  }

  /**
   * Write a 2-byte {@code char} value to the {@code out} is null.
   *
   * @param out The {@link DataOutput}.
   * @param c The {@code char} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeChar(final DataOutput out, final char c, final boolean isBigEndian) throws IOException {
    Assertions.assertNotNull(out);
    if (isBigEndian) {
      out.write((byte)((c >> 8) & 0xff));
      out.write((byte)(c & 0xff));
    }
    else {
      out.write((byte)(c & 0xff));
      out.write((byte)((c >> 8) & 0xff));
    }
  }

  /**
   * Write a 4-byte {@code int} value to the specified {@link OutputStream} in
   * big-endian encoding.
   *
   * @param out The {@link OutputStream}.
   * @param i The {@code int} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeInt(final OutputStream out, final int i) throws IOException {
    writeInt(out, i, true);
  }

  /**
   * Write a 4-byte {@code int} value to the specified {@link OutputStream}.
   *
   * @param out The {@link OutputStream}.
   * @param i The {@code int} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeInt(final OutputStream out, final int i, final boolean isBigEndian) throws IOException {
    Assertions.assertNotNull(out);
    if (isBigEndian) {
      out.write((byte)((i >> 24) & 0xff));
      out.write((byte)((i >> 16) & 0xff));
      out.write((byte)((i >> 8) & 0xff));
      out.write((byte)(i & 0xff));
    }
    else {
      out.write((byte)(i & 0xff));
      out.write((byte)((i >> 8) & 0xff));
      out.write((byte)((i >> 16) & 0xff));
      out.write((byte)((i >> 24) & 0xff));
    }
  }

  /**
   * Write a 4-byte {@code int} value to the {@code out} is null in
   * big-endian encoding.
   *
   * @param out The {@link DataOutput}.
   * @param i The {@code int} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeInt(final DataOutput out, final int i) throws IOException {
    writeInt(out, i, true);
  }

  /**
   * Write a 4-byte {@code int} value to the {@code out} is null.
   *
   * @param out The {@link DataOutput}.
   * @param i The {@code int} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeInt(final DataOutput out, final int i, final boolean isBigEndian) throws IOException {
    Assertions.assertNotNull(out);
    if (isBigEndian) {
      out.write((byte)((i >> 24) & 0xff));
      out.write((byte)((i >> 16) & 0xff));
      out.write((byte)((i >> 8) & 0xff));
      out.write((byte)(i & 0xff));
    }
    else {
      out.write((byte)(i & 0xff));
      out.write((byte)((i >> 8) & 0xff));
      out.write((byte)((i >> 16) & 0xff));
      out.write((byte)((i >> 24) & 0xff));
    }
  }

  /**
   * Write a 8-byte {@code long} value to the specified {@link OutputStream} in
   * big-endian encoding.
   *
   * @param out The {@link OutputStream}.
   * @param l The {@code long} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeLong(final OutputStream out, final long l) throws IOException {
    writeLong(out, l, true);
  }

  /**
   * Write a 8-byte {@code long} value to the specified {@link OutputStream}.
   *
   * @param out The {@link OutputStream}.
   * @param l The {@code long} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeLong(final OutputStream out, final long l, final boolean isBigEndian) throws IOException {
    Assertions.assertNotNull(out);
    if (isBigEndian) {
      out.write((byte)((l >> 56) & 0xff));
      out.write((byte)((l >> 48) & 0xff));
      out.write((byte)((l >> 40) & 0xff));
      out.write((byte)((l >> 32) & 0xff));
      out.write((byte)((l >> 24) & 0xff));
      out.write((byte)((l >> 16) & 0xff));
      out.write((byte)((l >> 8) & 0xff));
      out.write((byte)(l & 0xff));
    }
    else {
      out.write((byte)(l & 0xff));
      out.write((byte)((l >> 8) & 0xff));
      out.write((byte)((l >> 16) & 0xff));
      out.write((byte)((l >> 24) & 0xff));
      out.write((byte)((l >> 32) & 0xff));
      out.write((byte)((l >> 40) & 0xff));
      out.write((byte)((l >> 48) & 0xff));
      out.write((byte)((l >> 56) & 0xff));
    }
  }

  /**
   * Write a 8-byte {@code long} value to the {@code out} is null in
   * big-endian encoding.
   *
   * @param out The {@link DataOutput}.
   * @param l The {@code long} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeLong(final DataOutput out, final long l) throws IOException {
    writeLong(out, l, true);
  }

  /**
   * Write a 8-byte {@code long} value to the {@code out} is null.
   *
   * @param out The {@link DataOutput}.
   * @param l The {@code long} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeLong(final DataOutput out, final long l, final boolean isBigEndian) throws IOException {
    Assertions.assertNotNull(out);
    if (isBigEndian) {
      out.write((byte)((l >> 56) & 0xff));
      out.write((byte)((l >> 48) & 0xff));
      out.write((byte)((l >> 40) & 0xff));
      out.write((byte)((l >> 32) & 0xff));
      out.write((byte)((l >> 24) & 0xff));
      out.write((byte)((l >> 16) & 0xff));
      out.write((byte)((l >> 8) & 0xff));
      out.write((byte)(l & 0xff));
    }
    else {
      out.write((byte)(l & 0xff));
      out.write((byte)((l >> 8) & 0xff));
      out.write((byte)((l >> 16) & 0xff));
      out.write((byte)((l >> 24) & 0xff));
      out.write((byte)((l >> 32) & 0xff));
      out.write((byte)((l >> 40) & 0xff));
      out.write((byte)((l >> 48) & 0xff));
      out.write((byte)((l >> 56) & 0xff));
    }
  }

  /**
   * Write a 4-byte {@code float} value to the specified {@link OutputStream},
   * in the representation of the specified floating-point value according to
   * the <a href="https://en.wikipedia.org/wiki/IEEE_754">IEEE 754</a>
   * floating-point "single format" bit layout.
   *
   * @param out The {@link OutputStream}.
   * @param f The {@code float} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeFloat(final OutputStream out, final float f) throws IOException {
    writeInt(out, Float.floatToIntBits(f));
  }

  /**
   * Write a 8-byte {@code double} value to the specified {@link OutputStream},
   * in the representation of the specified floating-point value according to
   * the <a href="https://en.wikipedia.org/wiki/IEEE_754">IEEE 754</a>
   * floating-point "double format" bit layout.
   *
   * @param out The {@link OutputStream}.
   * @param d The {@code double} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the {@code out} is null.
   */
  public static void writeDouble(final OutputStream out, final double d) throws IOException {
    writeLong(out, Double.doubleToLongBits(d));
  }

  /**
   * Returns a {@code short} value constructed from the 2-byte big-endian
   * representation read from {@code in}.
   *
   * @param in The {@link InputStream}.
   * @return A {@code short} value constructed from the 2-byte big-endian
   *         representation read from {@code in}.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code in} is null.
   */
  public static short readShort(final InputStream in) throws IOException {
    return readShort(in, true);
  }

  /**
   * Returns a {@code short} value constructed from the 2-byte
   * {@code isBigEndian} representation read from {@code in}.
   *
   * @param in The {@link InputStream}.
   * @return A {@code short} value constructed from the 2-byte
   *         {@code isBigEndian} representation read from {@code in}.
   * @param isBigEndian If {@code true}, bytes will be read in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code in} is null.
   */
  public static short readShort(final InputStream in, final boolean isBigEndian) throws IOException {
    Assertions.assertNotNull(in);
    if (isBigEndian)
      return (short)(((in.read() & 0xFF) << 8) | (in.read() & 0xFF));

    return (short)((in.read() & 0xFF) | (in.read() & 0xFF) << 8);
  }

  /**
   * Returns a {@code char} value constructed from the 2-byte unsigned
   * big-endian representation read from {@code in}.
   *
   * @param in The {@link InputStream}.
   * @return A {@code short} value constructed from the 2-byte unsigned
   *         big-endian representation read from {@code in}.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code in} is null.
   */
  public static char readChar(final InputStream in) throws IOException {
    return readChar(in, true);
  }

  /**
   * Returns a {@code char} value constructed from the 2-byte unsigned
   * {@code isBigEndian} representation read from {@code in}.
   *
   * @param in The {@link InputStream}.
   * @return A {@code short} value constructed from the 2-byte unsigned
   *         {@code isBigEndian} representation read from {@code in}.
   * @param isBigEndian If {@code true}, bytes will be read in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code in} is null.
   */
  public static char readChar(final InputStream in, final boolean isBigEndian) throws IOException {
    Assertions.assertNotNull(in);
    if (isBigEndian)
      return (char)((in.read() & 0xFF) << 8 | (in.read() & 0xFF));

    return (char)((in.read() & 0xFF) | (in.read() & 0xFF) << 8);
  }

  /**
   * Returns an {@code int} value constructed from the 4-byte big-endian
   * representation read from {@code in}.
   *
   * @param in The {@link InputStream}.
   * @return An {@code int} value constructed from the 4-byte big-endian
   *         representation read from {@code in}.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code in} is null.
   */
  public static int readInt(final InputStream in) throws IOException {
    return readInt(in, true);
  }

  /**
   * Returns an {@code int} value constructed from the 4-byte
   * {@code isBigEndian} representation read from {@code in}.
   *
   * @param in The {@link InputStream}.
   * @return An {@code int} value constructed from the 4-byte
   *         {@code isBigEndian} representation read from {@code in}.
   * @param isBigEndian If {@code true}, bytes will be read in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code in} is null.
   */
  public static int readInt(final InputStream in, final boolean isBigEndian) throws IOException {
    Assertions.assertNotNull(in);
    if (isBigEndian)
      return ((in.read() & 0xFF) << 24) | ((in.read() & 0xFF) << 16) | ((in.read() & 0xFF) << 8) | (in.read() & 0xFF);

    return in.read() & 0xFF | (in.read() & 0xFF) << 8 | (in.read() & 0xFF) << 16 | (in.read() & 0xFF) << 24;
  }

  /**
   * Returns a {@code long} value constructed from the 8-byte big-endian
   * representation read from {@code in}.
   *
   * @param in The {@link InputStream}.
   * @return A {@code long} value constructed from the 8-byte big-endian
   *         representation read from {@code in}.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code in} is null.
   */
  public static long readLong(final InputStream in) throws IOException {
    return readLong(in, true);
  }

  /**
   * Returns a {@code long} value constructed from the 8-byte
   * {@code isBigEndian} representation read from {@code in}.
   *
   * @param in The {@link InputStream}.
   * @return A {@code long} value constructed from the 8-byte
   *         {@code isBigEndian} representation read from {@code in}.
   * @param isBigEndian If {@code true}, bytes will be read in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code in} is null.
   */
  public static long readLong(final InputStream in, final boolean isBigEndian) throws IOException {
    Assertions.assertNotNull(in);
    if (isBigEndian)
      return (in.read() & 0xFFL) << 56 | (in.read() & 0xFFL) << 48 | (in.read() & 0xFFL) << 40 | (in.read() & 0xFFL) << 32 | (in.read() & 0xFFL) << 24 | (in.read() & 0xFFL) << 16 | (in.read() & 0xFFL) << 8 | (in.read() & 0xFFL);

    return (in.read() & 0xFFL) | (in.read() & 0xFFL) << 8 | (in.read() & 0xFFL) << 16 | (in.read() & 0xFFL) << 24 | (in.read() & 0xFFL) << 32 | (in.read() & 0xFFL) << 40 | (in.read() & 0xFFL) << 48 | (in.read() & 0xFFL) << 56;
  }

  /**
   * Reads all bytes from the provided {@link InputStream} and returns the
   * resulting buffer array. This method blocks until all contents have been
   * read, end of file is detected, or an exception is thrown.
   *
   * @param in The {@link InputStream} from which to read.
   * @return The {@code byte[]} containing all bytes that were read from the
   *         provided {@link InputStream} {@code in} until an end of file is
   *         detected.
   * @throws IOException If the first byte cannot be read for any reason other
   *           than the end of the file, if the input stream has been closed, or
   *           if some other I/O error occurs.
   * @throws IllegalArgumentException If {@code in} is null.
   * @see InputStream#read(byte[])
   */
  public static byte[] readBytes(final InputStream in) throws IOException {
    Assertions.assertNotNull(in);
    final ByteArrayOutputStream buf = new ByteArrayOutputStream(DEFAULT_SOCKET_BUFFER_SIZE);
    final byte[] data = new byte[DEFAULT_SOCKET_BUFFER_SIZE];
    for (int length; (length = in.read(data)) != -1; buf.write(data, 0, length));
    return buf.toByteArray();
  }

  /**
   * Returns a synchronous merged {@link InputStream} receiving its input from
   * the array of {@code streams} input streams. Data is received from each
   * input stream in sequential order -- i.e. the first stream is read first,
   * advancing to the second only once the first has been read fully. The order
   * the input streams are read is the order in which they are provided in the
   * {@code streams} argument.
   *
   * @param streams The streams to merge.
   * @return A merged {@link InputStream} receiving its input from the array of
   *         {@code streams} input streams.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If @{@code streams} is null, or if
   *           {@code streams.length == 0}.
   */
  public static InputStream merge(final InputStream ... streams) throws IOException {
    return merge(true, streams);
  }

  /**
   * Returns an asynchronously merged {@link InputStream} receiving its input
   * from the array of {@code streams} input streams. Data is received from each
   * input stream asynchronously, and is written to the merged stream in the
   * order data becomes available to read.
   *
   * @param streams The streams to merge.
   * @return A merged {@link InputStream} receiving its input from the array of
   *         {@code streams} input streams.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code streams.length == 0}.
   */
  public static InputStream mergeAsync(final InputStream ... streams) throws IOException {
    return merge(false, streams);
  }

  private static InputStream merge(final boolean sync, final InputStream ... streams) throws IOException {
    if (Assertions.assertNotNull(streams).length == 0)
      throw new IllegalArgumentException("streams.length == 0");

    if (streams.length == 1)
      return streams[0];

    final CountDownLatch latch = new CountDownLatch(streams.length + 1);
    try (final PipedOutputStream pipedOut = new PipedOutputStream() {
      @Override
      public void close() throws IOException {
        latch.countDown();
        if (latch.getCount() == 0) {
          super.close();
        }
      }
    }) {
      final InputStream pipedIn = new PipedInputStream(pipedOut, DEFAULT_SOCKET_BUFFER_SIZE);
      for (int i = 0; i < streams.length; ++i) {
        pipe(streams[i], pipedOut, false, sync, p -> {
          try {
            pipedOut.close();
          }
          catch (final IOException e) {
            if (logger.isDebugEnabled())
              logger.debug(e.getMessage(), e);
          }
        });
      }

      return pipedIn;
    }
  }

  /**
   * Pipe the {@code src} input stream to the {@code snk} output stream in the
   * current thread.
   *
   * @param src The source {@link InputStream}.
   * @param snk The sink {@link OutputStream}.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code src} or {@code snk} is null.
   */
  public static void pipe(final InputStream src, final OutputStream snk) throws IOException {
    pipe(src, snk, false, true, null);
  }

  /**
   * Asynchronously pipe the {@code src} input stream to the {@code snk} output
   * stream. This method will spawn a dedicated thread to pipe the data.
   *
   * @param src The source {@link InputStream}.
   * @param snk The sink {@link OutputStream}.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code src} or {@code snk} is null.
   */
  public static void pipeAsync(final InputStream src, final OutputStream snk) throws IOException {
    pipeAsync(src, snk, null);
  }

  /**
   * Asynchronously pipe the {@code src} input stream to the {@code snk} output
   * stream. This method will spawn a dedicated thread to pipe the data.
   *
   * @param src The source {@link InputStream}.
   * @param snk The sink {@link OutputStream}.
   * @param onThreadExit Consumer function that will be called when the thread
   *          exits, either (1) due to IOException (in which case the exception
   *          will be passed to the consumer instance), or (2) due to regular
   *          completion (in which case null will be passed to the consumer
   *          instance).
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code src} or {@code snk} is null.
   */
  public static void pipeAsync(final InputStream src, final OutputStream snk, final Consumer<? super IOException> onThreadExit) throws IOException {
    pipe(src, snk, false, false, onThreadExit);
  }

  /**
   * Tee the {@code src} input stream to the {@code snk} output stream and the
   * returned input stream in the current thread.
   *
   * @param src The source {@link InputStream}.
   * @param snk The sink {@link OutputStream}.
   * @return The {@link InputStream} teed from {@code src}.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code src} or {@code snk} is null.
   */
  public static InputStream tee(final InputStream src, final OutputStream snk) throws IOException {
    return pipe(src, snk, true, true, null);
  }

  /**
   * Asynchronously tee the {@code src} input stream to the {@code snk} output
   * stream and the returned input stream. This method will spawn a dedicated
   * thread to tee the data.
   *
   * @param src The source {@link InputStream}.
   * @param snk The sink {@link OutputStream}.
   * @return The {@link InputStream} teed from {@code src}.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code src} or {@code snk} is null.
   */
  public static InputStream teeAsync(final InputStream src, final OutputStream snk) throws IOException {
    return teeAsync(src, snk, null);
  }

  /**
   * Asynchronously tee the {@code src} input stream to the {@code snk} output
   * stream and the returned input stream. This method will spawn a dedicated
   * thread to tee the data.
   *
   * @param src The source {@link InputStream}.
   * @param snk The sink {@link OutputStream}.
   * @param onThreadExit Consumer function that will be called when the thread
   *          exits, either (1) due to IOException (in which case the exception
   *          will be passed to the consumer instance), or (2) due to regular
   *          completion (in which case {@code null} will be passed to the
   *          consumer instance).
   * @return The {@link InputStream} teed from {@code src}.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code src} or {@code snk} is null.
   */
  public static InputStream teeAsync(final InputStream src, final OutputStream snk, final Consumer<? super IOException> onThreadExit) throws IOException {
    return pipe(src, snk, true, false, onThreadExit);
  }

  private static InputStream pipe(final InputStream src, final OutputStream snk, final boolean tee, final boolean sync, final Consumer<? super IOException> onExit) throws IOException {
    Assertions.assertNotNull(src);
    Assertions.assertNotNull(snk);
    final PipedOutputStream pipedOut;
    final InputStream pipedIn;
    if (tee) {
      pipedOut = new PipedOutputStream();
      pipedIn = new PipedInputStream(pipedOut, DEFAULT_SOCKET_BUFFER_SIZE);
    }
    else {
      pipedOut = null;
      pipedIn = null;
    }

    if (sync) {
      pipe(src, snk, pipedOut, DEFAULT_SOCKET_BUFFER_SIZE, onExit);
    }
    else {
      new Thread(tee ? "tee" : "pipe") {
        @Override
        public void run() {
          pipe(src, snk, pipedOut, DEFAULT_SOCKET_BUFFER_SIZE, onExit);
        }
      }.start();
    }

    return pipedIn;
  }

  private static void pipe(final InputStream src, final OutputStream snk, final PipedOutputStream pipedOut, final int bufferSize, final Consumer<? super IOException> onExit) {
    int len;
    final byte[] bytes = new byte[bufferSize];
    try {
      if (pipedOut != null) {
        if (snk != null) {
          while ((len = src.read(bytes)) != -1) {
            pipedOut.write(bytes, 0, len);
            snk.write(bytes, 0, len);

            pipedOut.flush();
            snk.flush();
          }

          pipedOut.close();
          snk.flush();
        }
        else {
          while ((len = src.read(bytes)) != -1) {
            pipedOut.write(bytes, 0, len);
            pipedOut.flush();
          }

          pipedOut.close();
        }
      }
      else if (snk != null) {
        while ((len = src.read(bytes)) != -1) {
          snk.write(bytes, 0, len);
          snk.flush();
        }

        snk.flush();
      }
      else {
        while (src.read(bytes) != -1);
      }

      if (onExit != null)
        onExit.accept(null);
    }
    catch (final IOException e) {
      if (onExit != null)
        onExit.accept(e);
    }
  }

  private Streams() {
  }
}