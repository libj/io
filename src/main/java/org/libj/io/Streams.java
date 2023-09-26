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
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import org.libj.util.ArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility functions for operations pertaining to {@link InputStream} and {@link OutputStream}.
 */
public final class Streams {
  private static final Logger logger = LoggerFactory.getLogger(Streams.class);
  public static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;

  /**
   * Write a 2-byte {@code short} value to the specified {@link OutputStream} in big-endian encoding.
   *
   * @param out The {@link OutputStream} to which to write.
   * @param s The {@code short} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeShort(final OutputStream out, final short s) throws IOException {
    writeShort(out, s, true);
  }

  /**
   * Write a 2-byte {@code short} value to the specified {@link OutputStream}.
   *
   * @param out The {@link OutputStream} to which to write.
   * @param s The {@code short} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeShort(final OutputStream out, final short s, final boolean isBigEndian) throws IOException {
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
   * Write a 2-byte {@code short} value to the specified {@link OutputStream} in big-endian encoding.
   *
   * @param out The {@link OutputStream} to which to write.
   * @param s The {@code short} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeShort(final DataOutput out, final short s) throws IOException {
    writeShort(out, s, true);
  }

  /**
   * Write a 2-byte {@code short} value to the specified {@link OutputStream}.
   *
   * @param out The {@link OutputStream} to which to write.
   * @param s The {@code short} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeShort(final DataOutput out, final short s, final boolean isBigEndian) throws IOException {
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
   * Write a 2-byte {@code char} value to the specified {@link OutputStream} in big-endian encoding.
   *
   * @param out The {@link OutputStream} to which to write.
   * @param c The {@code char} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeChar(final OutputStream out, final char c) throws IOException {
    writeChar(out, c, true);
  }

  /**
   * Write a 2-byte {@code char} value to the specified {@link OutputStream}.
   *
   * @param out The {@link OutputStream} to which to write.
   * @param c The {@code char} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeChar(final OutputStream out, final char c, final boolean isBigEndian) throws IOException {
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
   * Write a 2-byte {@code char} value to the {@code out} is null in big-endian encoding.
   *
   * @param out The {@link DataOutput} to which to write.
   * @param c The {@code char} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeChar(final DataOutput out, final char c) throws IOException {
    writeChar(out, c, true);
  }

  /**
   * Write a 2-byte {@code char} value to the {@code out} is null.
   *
   * @param out The {@link DataOutput} to which to write.
   * @param c The {@code char} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeChar(final DataOutput out, final char c, final boolean isBigEndian) throws IOException {
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
   * Write a 4-byte {@code int} value to the specified {@link OutputStream} in big-endian encoding.
   *
   * @param out The {@link OutputStream} to which to write.
   * @param i The {@code int} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeInt(final OutputStream out, final int i) throws IOException {
    writeInt(out, i, true);
  }

  /**
   * Write a 4-byte {@code int} value to the specified {@link OutputStream}.
   *
   * @param out The {@link OutputStream} to which to write.
   * @param i The {@code int} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeInt(final OutputStream out, final int i, final boolean isBigEndian) throws IOException {
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
   * Write a 4-byte {@code int} value to the {@code out} is null in big-endian encoding.
   *
   * @param out The {@link DataOutput} to which to write.
   * @param i The {@code int} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeInt(final DataOutput out, final int i) throws IOException {
    writeInt(out, i, true);
  }

  /**
   * Write a 4-byte {@code int} value to the {@code out} is null.
   *
   * @param out The {@link DataOutput} to which to write.
   * @param i The {@code int} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeInt(final DataOutput out, final int i, final boolean isBigEndian) throws IOException {
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
   * Write a 8-byte {@code long} value to the specified {@link OutputStream} in big-endian encoding.
   *
   * @param out The {@link OutputStream} to which to write.
   * @param l The {@code long} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeLong(final OutputStream out, final long l) throws IOException {
    writeLong(out, l, true);
  }

  /**
   * Write a 8-byte {@code long} value to the specified {@link OutputStream}.
   *
   * @param out The {@link OutputStream} to which to write.
   * @param l The {@code long} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeLong(final OutputStream out, final long l, final boolean isBigEndian) throws IOException {
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
   * Write a 8-byte {@code long} value to the {@code out} is null in big-endian encoding.
   *
   * @param out The {@link DataOutput} to which to write.
   * @param l The {@code long} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeLong(final DataOutput out, final long l) throws IOException {
    writeLong(out, l, true);
  }

  /**
   * Write a 8-byte {@code long} value to the {@code out} is null.
   *
   * @param out The {@link DataOutput} to which to write.
   * @param l The {@code long} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeLong(final DataOutput out, final long l, final boolean isBigEndian) throws IOException {
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
   * Write a 4-byte {@code float} value to the specified {@link OutputStream}, in the representation of the specified floating-point
   * value according to the <a href="https://en.wikipedia.org/wiki/IEEE_754">IEEE 754</a> floating-point "single format" bit layout.
   *
   * @param out The {@link OutputStream} to which to write.
   * @param f The {@code float} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeFloat(final OutputStream out, final float f) throws IOException {
    writeInt(out, Float.floatToIntBits(f));
  }

  /**
   * Write a 8-byte {@code double} value to the specified {@link OutputStream}, in the representation of the specified floating-point
   * value according to the <a href="https://en.wikipedia.org/wiki/IEEE_754">IEEE 754</a> floating-point "double format" bit layout.
   *
   * @param out The {@link OutputStream} to which to write.
   * @param d The {@code double} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is null.
   */
  public static void writeDouble(final OutputStream out, final double d) throws IOException {
    writeLong(out, Double.doubleToLongBits(d));
  }

  /**
   * Returns the bytes read from the provided {@link InputStream} until the next occurrence of the provided {@code char}, or the end
   * of the stream is encountered.
   *
   * @param in The {@link InputStream} to be read.
   * @param ch The {@code char} until which to read.
   * @return The bytes read from the provided {@link InputStream} until the next occurrence of the provided {@code char}, or the end
   *         of the stream is encountered.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is null.
   */
  public static byte[] readUntil(final InputStream in, final char ch) throws IOException {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    for (int c; (c = in.read()) != ch && c != -1;) // [ST]
      out.write(c);

    return out.toByteArray();
  }

  /**
   * Returns a {@code short} value constructed from the 2-byte big-endian representation read from {@code in}.
   *
   * @param in The {@link InputStream} from which to read.
   * @return A {@code short} value constructed from the 2-byte big-endian representation read from {@code in}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is null.
   */
  public static short readShort(final InputStream in) throws IOException {
    return readShort(in, true);
  }

  /**
   * Returns a {@code short} value constructed from the 2-byte {@code isBigEndian} representation read from {@code in}.
   *
   * @param in The {@link InputStream} from which to read.
   * @return A {@code short} value constructed from the 2-byte {@code isBigEndian} representation read from {@code in}.
   * @param isBigEndian If {@code true}, bytes will be read in big-endian encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is null.
   */
  public static short readShort(final InputStream in, final boolean isBigEndian) throws IOException {
    if (isBigEndian)
      return (short)(((in.read() & 0xFF) << 8) | (in.read() & 0xFF));

    return (short)((in.read() & 0xFF) | (in.read() & 0xFF) << 8);
  }

  /**
   * Returns a {@code char} value constructed from the 2-byte unsigned big-endian representation read from {@code in}.
   *
   * @param in The {@link InputStream} from which to read.
   * @return A {@code short} value constructed from the 2-byte unsigned big-endian representation read from {@code in}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is null.
   */
  public static char readChar(final InputStream in) throws IOException {
    return readChar(in, true);
  }

  /**
   * Returns a {@code char} value constructed from the 2-byte unsigned {@code isBigEndian} representation read from {@code in}.
   *
   * @param in The {@link InputStream} from which to read.
   * @return A {@code short} value constructed from the 2-byte unsigned {@code isBigEndian} representation read from {@code in}.
   * @param isBigEndian If {@code true}, bytes will be read in big-endian encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is null.
   */
  public static char readChar(final InputStream in, final boolean isBigEndian) throws IOException {
    if (isBigEndian)
      return (char)((in.read() & 0xFF) << 8 | (in.read() & 0xFF));

    return (char)((in.read() & 0xFF) | (in.read() & 0xFF) << 8);
  }

  /**
   * Returns an {@code int} value constructed from the 4-byte big-endian representation read from {@code in}.
   *
   * @param in The {@link InputStream} from which to read.
   * @return An {@code int} value constructed from the 4-byte big-endian representation read from {@code in}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is null.
   */
  public static int readInt(final InputStream in) throws IOException {
    return readInt(in, true);
  }

  /**
   * Returns an {@code int} value constructed from the 4-byte {@code isBigEndian} representation read from {@code in}.
   *
   * @param in The {@link InputStream} from which to read.
   * @return An {@code int} value constructed from the 4-byte {@code isBigEndian} representation read from {@code in}.
   * @param isBigEndian If {@code true}, bytes will be read in big-endian encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is null.
   */
  public static int readInt(final InputStream in, final boolean isBigEndian) throws IOException {
    if (isBigEndian)
      return ((in.read() & 0xFF) << 24) | ((in.read() & 0xFF) << 16) | ((in.read() & 0xFF) << 8) | (in.read() & 0xFF);

    return in.read() & 0xFF | (in.read() & 0xFF) << 8 | (in.read() & 0xFF) << 16 | (in.read() & 0xFF) << 24;
  }

  /**
   * Returns a {@code long} value constructed from the 8-byte big-endian representation read from {@code in}.
   *
   * @param in The {@link InputStream} from which to read.
   * @return A {@code long} value constructed from the 8-byte big-endian representation read from {@code in}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is null.
   */
  public static long readLong(final InputStream in) throws IOException {
    return readLong(in, true);
  }

  /**
   * Returns a {@code long} value constructed from the 8-byte {@code isBigEndian} representation read from {@code in}.
   *
   * @param in The {@link InputStream} from which to read.
   * @return A {@code long} value constructed from the 8-byte {@code isBigEndian} representation read from {@code in}.
   * @param isBigEndian If {@code true}, bytes will be read in big-endian encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is null.
   */
  public static long readLong(final InputStream in, final boolean isBigEndian) throws IOException {
    if (isBigEndian)
      return (in.read() & 0xFFL) << 56 | (in.read() & 0xFFL) << 48 | (in.read() & 0xFFL) << 40 | (in.read() & 0xFFL) << 32 | (in.read() & 0xFFL) << 24 | (in.read() & 0xFFL) << 16 | (in.read() & 0xFFL) << 8 | (in.read() & 0xFFL);

    return (in.read() & 0xFFL) | (in.read() & 0xFFL) << 8 | (in.read() & 0xFFL) << 16 | (in.read() & 0xFFL) << 24 | (in.read() & 0xFFL) << 32 | (in.read() & 0xFFL) << 40 | (in.read() & 0xFFL) << 48 | (in.read() & 0xFFL) << 56;
  }

  /**
   * Reads all bytes from the provided {@link InputStream} and returns the resulting buffer array. This method blocks until all
   * contents have been read, end of file is detected, or an exception is thrown.
   *
   * @param in The {@link InputStream} from which to read.
   * @return The {@code byte[]} containing all bytes that were read from the provided {@link InputStream} {@code in} until an end of
   *         file is detected.
   * @throws IOException If the first byte cannot be read for any reason other than the end of the file, if the {@link InputStream}
   *           has been closed, or if some other I/O error occurs.
   * @throws NullPointerException If {@code in} is null.
   * @see InputStream#read(byte[])
   */
  public static byte[] readBytes(final InputStream in) throws IOException {
    return readBytes(in, Integer.MAX_VALUE);
  }

  /**
   * Reads up to {@code maxLength} bytes from the provided {@link InputStream} and returns the resulting buffer array. This method
   * blocks until all contents have been read, end of file is detected, or an exception is thrown.
   *
   * @param in The {@link InputStream} from which to read.
   * @param maxLength The maximum number of bytes to read.
   * @return The {@code byte[]} containing all bytes that were read from the provided {@link InputStream} {@code in} until an end of
   *         file is detected.
   * @throws IOException If the first byte cannot be read for any reason other than the end of the file, if the {@link InputStream}
   *           has been closed, or if some other I/O error occurs.
   * @throws NullPointerException If {@code in} is null.
   * @throws IllegalArgumentException If {@code maxLength} is not a positive value.
   * @see InputStream#read(byte[])
   */
  public static byte[] readBytes(final InputStream in, final int maxLength) throws IOException {
    final int bufferSize = Math.min(DEFAULT_SOCKET_BUFFER_SIZE, maxLength);
    final byte[] data = new byte[bufferSize];
    int len = 0;
    int pos = 0;
    for (; (len = bufferSize - pos) > 0 && (len = in.read(data, pos, len)) != -1; pos += len); // [X]

    if (pos == 0)
      return ArrayUtil.EMPTY_ARRAY_BYTE;

    if (len == -1)
      return Arrays.copyOf(data, pos);

    if (pos == maxLength || (len = in.read()) == -1)
      return data;

    final ByteArrayOutputStream buf = new ByteArrayOutputStream(Math.min(DEFAULT_SOCKET_BUFFER_SIZE * 4, maxLength));
    buf.write(data);
    buf.write(len);
    ++pos;

    while ((len = Math.min(DEFAULT_SOCKET_BUFFER_SIZE, maxLength - pos)) > 0 && (len = in.read(data, 0, len)) != -1) {
      buf.write(data, 0, Math.min(len, maxLength - pos));
      if ((pos += len) == maxLength)
        break;
    }

    return buf.toByteArray();
  }

  /**
   * Returns a synchronous merged {@link InputStream} receiving its input from the array of {@code streams} {@link InputStream}s. Data
   * is received from each {@link InputStream} in sequential order -- i.e. the first stream is read first, advancing to the second
   * only once the first has been read fully. The order the {@link InputStream}s are read is the order in which they are provided in
   * the {@code streams} argument.
   *
   * @param streams The streams to merge.
   * @return A merged {@link InputStream} receiving its input from the array of {@code streams} {@link InputStream}s.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code streams} is null, or if any members of {@code streams} is null.
   * @throws IllegalArgumentException If {@code streams.length == 0}.
   */
  public static InputStream merge(final InputStream ... streams) throws IOException {
    return merge(true, streams);
  }

  /**
   * Returns an asynchronously merged {@link InputStream} receiving its input from the array of {@code streams} {@link InputStream}s.
   * Data is received from each {@link InputStream} asynchronously, and is written to the merged stream in the order data becomes
   * available to read.
   *
   * @param streams The streams to merge.
   * @return A merged {@link InputStream} receiving its input from the array of {@code streams} {@link InputStream}s.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code streams} is null, or if any members of {@code streams} is null.
   * @throws IllegalArgumentException If {@code streams.length == 0}.
   */
  public static InputStream mergeAsync(final InputStream ... streams) throws IOException {
    return merge(false, streams);
  }

  private static InputStream merge(final boolean sync, final InputStream ... streams) throws IOException {
    if (streams.length == 0)
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
      for (int i = 0, i$ = streams.length; i < i$; ++i) { // [A]
        pipe(streams[i], pipedOut, false, sync, p -> {
          try {
            pipedOut.close();
          }
          catch (final IOException e) {
            if (logger.isDebugEnabled()) { logger.debug(e.getMessage(), e); }
          }
        });
      }

      return pipedIn;
    }
  }

  /**
   * Pipe the {@code src} {@link InputStream} to the {@code snk} {@link OutputStream} in the current thread.
   *
   * @param src The source {@link InputStream}.
   * @param snk The sink {@link OutputStream}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code src} is null.
   */
  public static void pipe(final InputStream src, final OutputStream snk) throws IOException {
    pipe(src, snk, false, true, null);
  }

  /**
   * Asynchronously pipe the {@code src} {@link InputStream} to the {@code snk} {@link OutputStream}. This method will spawn a
   * dedicated thread to pipe the data.
   *
   * @param src The source {@link InputStream}.
   * @param snk The sink {@link OutputStream}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code src} is null.
   */
  public static void pipeAsync(final InputStream src, final OutputStream snk) throws IOException {
    pipeAsync(src, snk, null);
  }

  /**
   * Asynchronously pipe the {@code src} {@link InputStream} to the {@code snk} {@link OutputStream}. This method will spawn a
   * dedicated thread to pipe the data.
   *
   * @param src The source {@link InputStream}.
   * @param snk The sink {@link OutputStream}.
   * @param onThreadExit Consumer function that will be called when the thread exits, either (1) due to IOException (in which case the
   *          exception will be passed to the consumer instance), or (2) due to regular completion (in which case null will be passed
   *          to the consumer instance).
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code src} is null.
   */
  public static void pipeAsync(final InputStream src, final OutputStream snk, final Consumer<? super IOException> onThreadExit) throws IOException {
    pipe(src, snk, false, false, onThreadExit);
  }

  /**
   * Tee the {@code src} {@link InputStream} to the {@code snk} {@link OutputStream} and the returned {@link InputStream} in the
   * current thread.
   *
   * @param src The source {@link InputStream}.
   * @param snk The sink {@link OutputStream}.
   * @return The {@link InputStream} teed from {@code src}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code src} is null.
   */
  public static InputStream tee(final InputStream src, final OutputStream snk) throws IOException {
    return pipe(src, snk, true, true, null);
  }

  /**
   * Asynchronously tee the {@code src} {@link InputStream} to the {@code snk} {@link OutputStream} and the returned
   * {@link InputStream}. This method will spawn a dedicated thread to tee the data.
   *
   * @param src The source {@link InputStream}.
   * @param snk The sink {@link OutputStream}.
   * @return The {@link InputStream} teed from {@code src}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code src} is null.
   */
  public static InputStream teeAsync(final InputStream src, final OutputStream snk) throws IOException {
    return teeAsync(src, snk, null);
  }

  /**
   * Asynchronously tee the {@code src} {@link InputStream} to the {@code snk} {@link OutputStream} and the returned
   * {@link InputStream}. This method will spawn a dedicated thread to tee the data.
   *
   * @param src The source {@link InputStream}.
   * @param snk The sink {@link OutputStream}.
   * @param onThreadExit Consumer function that will be called when the thread exits, either (1) due to IOException (in which case the
   *          exception will be passed to the consumer instance), or (2) due to regular completion (in which case {@code null} will be
   *          passed to the consumer instance).
   * @return The {@link InputStream} teed from {@code src}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code src} is null.
   */
  public static InputStream teeAsync(final InputStream src, final OutputStream snk, final Consumer<? super IOException> onThreadExit) throws IOException {
    return pipe(src, snk, true, false, onThreadExit);
  }

  private static InputStream pipe(final InputStream src, final OutputStream snk, final boolean tee, final boolean sync, final Consumer<? super IOException> onExit) throws IOException {
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

  /**
   * Reads all bytes from the {@code src} {@link InputStream} and writes the bytes to the {@code snk} {@link OutputStream} in the
   * order that they are read. On return, the {@code src} {@link InputStream} will be at end of stream. This method does not close
   * either stream.
   * <p>
   * This method may block indefinitely reading from the {@link InputStream}, or writing to the {@link OutputStream}. The behavior for
   * the case where the {@link InputStream} and/or {@link OutputStream} is <i>asynchronously closed</i>, or the thread interrupted
   * during the transfer, is highly {@link InputStream} and {@link OutputStream} specific, and therefore not specified.
   * <p>
   * If an I/O error occurs reading from the {@link InputStream} or writing to the {@link OutputStream}, then it may do so after some
   * bytes have been read or written. Consequently the {@link InputStream} may not be at end of stream and one, or both, streams may
   * be in an inconsistent state. It is strongly recommended that both streams be promptly closed if an I/O error occurs.
   *
   * @param src The source {@link InputStream}.
   * @param snk The sink {@link OutputStream}.
   * @return The number of bytes transferred.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code src} or {@code snk} is null.
   */
  public static long transferTo(final InputStream src, final OutputStream snk) throws IOException {
    long total = 0;
    final byte[] buffer = new byte[DEFAULT_SOCKET_BUFFER_SIZE];
    for (int read; (read = src.read(buffer, 0, DEFAULT_SOCKET_BUFFER_SIZE)) >= 0; total += read) // [X]
      snk.write(buffer, 0, read);

    return total;
  }

  /**
   * Returns {@code true} if the {@link InputStream}s contain equal data (by reading from the streams) and {@code false} otherwise.
   * Consequently, if both {@link InputStream}s are {@code null}, {@code true} is returned. Otherwise, if the first argument is not
   * {@code
   * null}, equality is determined by calling the {@link Object#equals equals} method of the first argument with the second argument
   * of this method. Otherwise, {@code false} is returned.
   *
   * @param a An {@link InputStream} to compare with {@code b} for equality.
   * @param b An {@link InputStream} to compare with {@code a} for equality.
   * @return {@code true} if the {@link InputStream}s contain equal data (by reading from the streams) and {@code false} otherwise.
   * @throws IOException If an I/O error has occurred.
   */
  public static Integer equal(final InputStream a, final InputStream b) throws IOException {
    if (a == null) {
      if (b == null)
        return -1;

      return null;
    }
    else if (b == null) {
      return null;
    }

    final ReadableByteChannel ch1 = Channels.newChannel(a);
    final ReadableByteChannel ch2 = Channels.newChannel(b);

    final ByteBuffer buf1 = ByteBuffer.allocateDirect(1024);
    final ByteBuffer buf2 = ByteBuffer.allocateDirect(1024);

    int total = 0;
    try {
      while (true) {
        final int n1 = ch1.read(buf1);
        final int n2 = ch2.read(buf2);

        if (n1 == -1 || n2 == -1)
          return n1 == n2 ? total : null;

        buf1.flip();
        buf2.flip();

        for (int i = 0; i < Math.min(n1, n2); ++i) // [N]
          if (buf1.get() != buf2.get())
            return null;

        total += n1;
        buf1.compact();
        buf2.compact();
      }

    }
    finally {
      if (a != null)
        a.close();

      if (b != null)
        b.close();
    }
  }

  private Streams() {
  }
}