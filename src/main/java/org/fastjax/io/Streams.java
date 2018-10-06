/* Copyright (c) 2006 FastJAX
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

package org.fastjax.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import org.fastjax.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility functions for operations pertaining to {@link InputStream} and
 * {@link OutputStream}.
 */
public final class Streams {
  private static final Logger logger = LoggerFactory.getLogger(Streams.class);
  private static final int DEFAULT_SOCKET_BUFFER_SIZE = 65536;

  /**
   * Write a 2-byte {@code short} value to {@code out} in big-endian encoding.
   *
   * @param out The {@code OutputStream}.
   * @param s The {@code short} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is {@code null}.
   */
  public static void writeShort(final OutputStream out, final short s) throws IOException {
    writeShort(out, s, true);
  }

  /**
   * Write a 2-byte {@code short} value to {@code out}.
   *
   * @param out The {@code OutputStream}.
   * @param s The {@code short} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is {@code null}.
   */
  public static void writeShort(final OutputStream out, final short s, final boolean isBigEndian) throws IOException {
    Bytes.toBytes(s, out, isBigEndian);
  }

  /**
   * Write a 2-byte {@code char} value to {@code out} in big-endian encoding.
   *
   * @param out The {@code OutputStream}.
   * @param c The {@code char} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is {@code null}.
   */
  public static void writeChar(final OutputStream out, final char c) throws IOException {
    writeChar(out, c, true);
  }

  /**
   * Write a 2-byte {@code char} value to {@code out}.
   *
   * @param out The {@code OutputStream}.
   * @param c The {@code char} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is {@code null}.
   */
  public static void writeChar(final OutputStream out, final char c, final boolean isBigEndian) throws IOException {
    Bytes.toBytes(c, out, isBigEndian);
  }

  /**
   * Write a 4-byte {@code int} value to {@code out} in big-endian encoding.
   *
   * @param out The {@code OutputStream}.
   * @param i The {@code int} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is {@code null}.
   */
  public static void writeInt(final OutputStream out, final int i) throws IOException {
    writeInt(out, i, true);
  }

  /**
   * Write a 4-byte {@code int} value to {@code out}.
   *
   * @param out The {@code OutputStream}.
   * @param i The {@code int} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is {@code null}.
   */
  public static void writeInt(final OutputStream out, final int i, final boolean isBigEndian) throws IOException {
    Bytes.toBytes(i, out, isBigEndian);
  }

  /**
   * Write a 8-byte {@code long} value to {@code out} in big-endian encoding.
   *
   * @param out The {@code OutputStream}.
   * @param l The {@code long} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is {@code null}.
   */
  public static void writeLong(final OutputStream out, final long l) throws IOException {
    writeLong(out, l, true);
  }

  /**
   * Write a 8-byte {@code long} value to {@code out}.
   *
   * @param out The {@code OutputStream}.
   * @param l The {@code long} value to write.
   * @param isBigEndian If {@code true}, bytes will be written in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is {@code null}.
   */
  public static void writeLong(final OutputStream out, final long l, final boolean isBigEndian) throws IOException {
    Bytes.toBytes(l, out, isBigEndian);
  }

  /**
   * Write a 4-byte {@code float} value to {@code out}, in the representation of
   * the specified floating-point value according to the
   * <a href="https://en.wikipedia.org/wiki/IEEE_754">IEEE 754</a>
   * floating-point "single format" bit layout.
   *
   * @param out The {@code OutputStream}.
   * @param f The {@code float} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is {@code null}.
   */
  public static void writeFloat(final OutputStream out, final float f) throws IOException {
    writeInt(out, Float.floatToIntBits(f));
  }

  /**
   * Write a 8-byte {@code double} value to {@code out}, in the representation
   * of the specified floating-point value according to the
   * <a href="https://en.wikipedia.org/wiki/IEEE_754">IEEE 754</a>
   * floating-point "double format" bit layout.
   *
   * @param out The {@code OutputStream}.
   * @param d The {@code double} value to write.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code out} is {@code null}.
   */
  public static void writeDouble(final OutputStream out, final double d) throws IOException {
    writeLong(out, Double.doubleToLongBits(d));
  }

  /**
   * Returns a {@code short} value constructed from the 2-byte big-endian
   * representation read from {@code in}.
   *
   * @param in The {@code InputStream}.
   * @return A {@code short} value constructed from the 2-byte big-endian
   *         representation read from {@code in}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is {@code null}.
   */
  public static short readShort(final InputStream in) throws IOException {
    return readShort(in, true);
  }

  /**
   * Returns a {@code short} value constructed from the 2-byte
   * {@code isBigEndian} representation read from {@code in}.
   *
   * @param in The {@code InputStream}.
   * @return A {@code short} value constructed from the 2-byte
   *         {@code isBigEndian} representation read from {@code in}.
   * @param isBigEndian If {@code true}, bytes will be read in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is {@code null}.
   */
  public static short readShort(final InputStream in, final boolean isBigEndian) throws IOException {
    if (isBigEndian)
      return (short)(((in.read() & 0xFF) << 8) | (in.read() & 0xFF));

    return (short)((in.read() & 0xFF) | (in.read() & 0xFF) << 8);
  }

  /**
   * Returns a {@code char} value constructed from the 2-byte unsigned
   * big-endian representation read from {@code in}.
   *
   * @param in The {@code InputStream}.
   * @return A {@code short} value constructed from the 2-byte unsigned
   *         big-endian representation read from {@code in}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is {@code null}.
   */
  public static char readChar(final InputStream in) throws IOException {
    return readChar(in, true);
  }

  /**
   * Returns a {@code char} value constructed from the 2-byte unsigned
   * {@code isBigEndian} representation read from {@code in}.
   *
   * @param in The {@code InputStream}.
   * @return A {@code short} value constructed from the 2-byte unsigned
   *         {@code isBigEndian} representation read from {@code in}.
   * @param isBigEndian If {@code true}, bytes will be read in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is {@code null}.
   */
  public static char readChar(final InputStream in, final boolean isBigEndian) throws IOException {
    if (isBigEndian)
      return (char)((in.read() & 0xFF) << 8 | (in.read() & 0xFF));

    return (char)((in.read() & 0xFF) | (in.read() & 0xFF) << 8);
  }

  /**
   * Returns an {@code int} value constructed from the 4-byte big-endian
   * representation read from {@code in}.
   *
   * @param in The {@code InputStream}.
   * @return An {@code int} value constructed from the 4-byte big-endian
   *         representation read from {@code in}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is {@code null}.
   */
  public static int readInt(final InputStream in) throws IOException {
    return readInt(in, true);
  }

  /**
   * Returns an {@code int} value constructed from the 4-byte
   * {@code isBigEndian} representation read from {@code in}.
   *
   * @param in The {@code InputStream}.
   * @return An {@code int} value constructed from the 4-byte
   *         {@code isBigEndian} representation read from {@code in}.
   * @param isBigEndian If {@code true}, bytes will be read in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is {@code null}.
   */
  public static int readInt(final InputStream in, final boolean isBigEndian) throws IOException {
    if (isBigEndian)
      return ((in.read() & 0xFF) << 24) | ((in.read() & 0xFF) << 16) | ((in.read() & 0xFF) << 8) | (in.read() & 0xFF);

    return in.read() & 0xFF | (in.read() & 0xFF) << 8 | (in.read() & 0xFF) << 16 | (in.read() & 0xFF) << 24;
  }

  /**
   * Returns a {@code long} value constructed from the 8-byte big-endian
   * representation read from {@code in}.
   *
   * @param in The {@code InputStream}.
   * @return A {@code long} value constructed from the 8-byte big-endian
   *         representation read from {@code in}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is {@code null}.
   */
  public static long readLong(final InputStream in) throws IOException {
    return readLong(in, true);
  }

  /**
   * Returns a {@code long} value constructed from the 8-byte
   * {@code isBigEndian} representation read from {@code in}.
   *
   * @param in The {@code InputStream}.
   * @return A {@code long} value constructed from the 8-byte
   *         {@code isBigEndian} representation read from {@code in}.
   * @param isBigEndian If {@code true}, bytes will be read in big-endian
   *          encoding. If {@code false}, in little-endian.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is {@code null}.
   */
  public static long readLong(final InputStream in, final boolean isBigEndian) throws IOException {
    if (isBigEndian)
      return (in.read() & 0xFFl) << 56 | (in.read() & 0xFFl) << 48 | (in.read() & 0xFFl) << 40 | (in.read() & 0xFFl) << 32 | (in.read() & 0xFFl) << 24 | (in.read() & 0xFFl) << 16 | (in.read() & 0xFFl) << 8 | (in.read() & 0xFFl);

    return (in.read() & 0xFFl) | (in.read() & 0xFFl) << 8 | (in.read() & 0xFFl) << 16 | (in.read() & 0xFFl) << 24 | (in.read() & 0xFFl) << 32 | (in.read() & 0xFFl) << 40 | (in.read() & 0xFFl) << 48 | (in.read() & 0xFFl) << 56;
  }

  /**
   * Reads all bytes from the input stream and returns the resulting buffer
   * array. This method blocks until all contents have been read, end of
   * file is detected, or an exception is thrown.
   * <p>
   * If the InputStream <code>in</code> is <code>null</code>, then null
   * is returned; otherwise, a byte[] of at least size 0 will be returned.
   *
   * @param in The input stream to read from.
   * @return The byte[] containing all bytes that were read from the
   *         InputStream <code>in</code> until an end of file is detected.
   * @throws IOException If the first byte cannot be read for any reason
   *           other than the end of the file, if the input stream has been closed, or
   *           if some other I/O error occurs.
   * @throws NullPointerException If {@code in} is {@code null}.
   * @see java.io.InputStream#read(byte[])
   */
  public static byte[] readBytes(final InputStream in) throws IOException {
    if (in == null)
      return null;

    final ByteArrayOutputStream buffer = new ByteArrayOutputStream(DEFAULT_SOCKET_BUFFER_SIZE);
    final byte[] data = new byte[DEFAULT_SOCKET_BUFFER_SIZE];
    for (int length; (length = in.read(data)) != -1; buffer.write(data, 0, length));
    return buffer.toByteArray();
  }

  /**
   * Returns a synchronous merged {@code InputStream} receiving its input from
   * the array of {@code streams} input streams. Data is received from each
   * input stream in sequential order -- i.e. the first stream is read first,
   * advancing to the second only once the first has been read fully. The order
   * the input streams are read is the order in which they are provided in the
   * {@code streams} argument.
   *
   * @param streams The streams to merge.
   * @return A merged {@code InputStream} receiving its input from the array of
   *         {@code streams} input streams.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code streams.length == 0}.
   */
  public static InputStream merge(final InputStream ... streams) throws IOException {
    return merge(true, streams);
  }

  /**
   * Returns an asynchronously merged {@code InputStream} receiving its input
   * from the array of {@code streams} input streams. Data is received from each
   * input stream asynchronously, and is written to the merged stream in the
   * order data becomes available to read.
   *
   * @param streams The streams to merge.
   * @return A merged {@code InputStream} receiving its input from the array of
   *         {@code streams} input streams.
   * @throws IOException If an I/O error has occurred.
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
      for (int i = 0; i < streams.length; ++i)
        Streams.pipe(streams[i], pipedOut, false, sync, p -> {
          try {
            pipedOut.close();
          }
          catch (final IOException e) {
            logger.debug(e.getMessage(), e);
          }
        });

      return pipedIn;
    }
  }

  /**
   * Pipe the {@code src} input stream to the {@code snk} output stream in the
   * current thread.
   *
   * @param src The source {@code InputStream}.
   * @param snk The sink {@code OutputStream}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code src} or {@code snk} are
   *           {@code null}.
   */
  public static void pipe(final InputStream src, final OutputStream snk) throws IOException {
    pipe(src, snk, false, true, null);
  }

  /**
   * Asynchronously pipe the {@code src} input stream to the {@code snk} output
   * stream. This method will spawn a dedicated thread to pipe the data.
   *
   * @param src The source {@code InputStream}.
   * @param snk The sink {@code OutputStream}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code src} or {@code snk} are {@code null}.
   */
  public static void pipeAsync(final InputStream src, final OutputStream snk) throws IOException {
    pipeAsync(src, snk, null);
  }

  /**
   * Asynchronously pipe the {@code src} input stream to the {@code snk} output
   * stream. This method will spawn a dedicated thread to pipe the data.
   *
   * @param src The source {@code InputStream}.
   * @param snk The sink {@code OutputStream}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code src} or {@code snk} are {@code null}.
   */
  public static void pipeAsync(final InputStream src, final OutputStream snk, final Consumer<IOException> onThreadExit) throws IOException {
    pipe(src, snk, false, false, onThreadExit);
  }

  /**
   * Tee the {@code src} input stream to the {@code snk} output stream and the
   * returned input stream in the current thread.
   *
   * @param src The source {@code InputStream}.
   * @param snk The sink {@code OutputStream}.
   * @return The {@code InputStream} teed from {@code src}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code src} or {@code snk} are
   *           {@code null}.
   */
  public static InputStream tee(final InputStream src, final OutputStream snk) throws IOException {
    return pipe(src, snk, true, true, null);
  }

  /**
   * Asynchronously tee the {@code src} input stream to the {@code snk} output
   * stream and the returned input stream. This method will spawn a dedicated
   * thread to tee the data.
   *
   * @param src The source {@code InputStream}.
   * @param snk The sink {@code OutputStream}.
   * @return The {@code InputStream} teed from {@code src}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code src} or {@code snk} are
   *           {@code null}.
   */
  public static InputStream teeAsync(final InputStream src, final OutputStream snk) throws IOException {
    return teeAsync(src, snk, null);
  }

  /**
   * Asynchronously tee the {@code src} input stream to the {@code snk} output
   * stream and the returned input stream. This method will spawn a dedicated
   * thread to tee the data.
   *
   * @param src The source {@code InputStream}.
   * @param snk The sink {@code OutputStream}.
   * @param onThreadExit Consumer function that will be called when the thread
   *          exits, either (1) due to IOException (in which case the exception
   *          will be passed to the consumer instance), or (2) due to regular
   *          completion (in which case {@code null} will be passed to the
   *          consumer instance).
   * @return The {@code InputStream} teed from {@code src}.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code src} or {@code snk} are
   *           {@code null}.
   */
  public static InputStream teeAsync(final InputStream src, final OutputStream snk, final Consumer<IOException> onThreadExit) throws IOException {
    return pipe(src, snk, true, false, onThreadExit);
  }

  private static InputStream pipe(final InputStream src, final OutputStream snk, final boolean tee, final boolean sync, final Consumer<IOException> onExit) throws IOException {
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
      Streams.pipe(src, snk, pipedOut, DEFAULT_SOCKET_BUFFER_SIZE, onExit);
    }
    else {
      new Thread(tee ? "tee" : "pipe") {
        @Override
        public void run() {
          Streams.pipe(src, snk, pipedOut, DEFAULT_SOCKET_BUFFER_SIZE, onExit);
        }
      }.start();
    }

    return pipedIn;
  }

  private static void pipe(final InputStream src, final OutputStream snk, final PipedOutputStream pipedOut, final int bufferSize, final Consumer<IOException> onExit) {
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
        while ((len = src.read(bytes)) != -1);
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