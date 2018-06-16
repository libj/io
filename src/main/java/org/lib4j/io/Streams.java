/* Copyright (c) 2006 lib4j
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

package org.lib4j.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.lib4j.io.output.TeeOutputStream;

public final class Streams {
  private static final int DEFAULT_SOCKET_BUFFER_SIZE = 65536;

  public static void writeByte(final OutputStream out, final byte b) throws IOException {
    out.write(b);
  }

  public static void writeShort(final OutputStream out, final short s) throws IOException {
    out.write(s & 0xFF);
    out.write((s >> 8) & 0xFF);
  }

  public static void writeChar(final OutputStream out, final char c) throws IOException {
    out.write(c & 0xFF);
    out.write((c >> 8) & 0xFF);
  }

  public static void writeInt(final OutputStream out, final int i) throws IOException {
    out.write(i & 0xFF);
    out.write((i >> 8) & 0xFF);
    out.write((i >> 16) & 0xFF);
    out.write((i >> 24) & 0xFF);
  }

  public static void writeLong(final OutputStream out, final long l) throws IOException {
    out.write((int)(l & 0xFF));
    out.write((int)((l >> 8) & 0xFF));
    out.write((int)((l >> 16) & 0xFF));
    out.write((int)((l >> 24) & 0xFF));
    out.write((int)((l >> 32) & 0xFF));
    out.write((int)((l >> 40) & 0xFF));
    out.write((int)((l >> 48) & 0xFF));
    out.write((int)((l >> 56) & 0xFF));
  }

  public static void writeFloat(final OutputStream out, final float f) throws IOException {
    writeInt(out, Float.floatToIntBits(f));
  }

  public static void writeDouble(final OutputStream out, final double d) throws IOException {
    writeLong(out, Double.doubleToLongBits(d));
  }

  public static int readInt(final InputStream in) throws IOException {
    return in.read() & 0xFF | (in.read() & 0xFF) << 8 | (in.read() & 0xFF) << 16 | (in.read() & 0xFF) << 24;
  }

  public static long readLong(final InputStream in) throws IOException {
    return in.read() & 0xFF | (in.read() & 0xFF) << 8 | (in.read() & 0xFF) << 16 | (in.read() & 0xFF) << 24 | (in.read() & 0xFF) << 32 | (in.read() & 0xFF) << 40 | (in.read() & 0xFF) << 48 | (in.read() & 0xFF) << 56;
  }

  /**
   * Reads all bytes from the input stream and returns the resulting buffer
   * array. This method blocks until all contents have been read, end of
   * file is detected, or an exception is thrown.
   *
   * <p> If the InputStream <code>in</code> is <code>null</code>, then null
   * is returned; otherwise, a byte[] of at least size 0 will be returned.
   *
   * @param      in   the input stream to read from.
   * @return     the byte[] containing all bytes that were read from the
   *             InputStream <code>in</code> until an end of file is detected.
   * @exception  IOException  If the first byte cannot be read for any reason
   * other than the end of the file, if the input stream has been closed, or
   * if some other I/O error occurs.
   * @see        java.io.InputStream#read(byte[])
   */
  public static byte[] readBytes(final InputStream in) throws IOException {
    if (in == null)
      return null;

    final ByteArrayOutputStream buffer = new ByteArrayOutputStream(DEFAULT_SOCKET_BUFFER_SIZE);
    final byte[] data = new byte[DEFAULT_SOCKET_BUFFER_SIZE];
    int length = -1;
    while ((length = in.read(data)) != -1)
      buffer.write(data, 0, length);

    return buffer.toByteArray();
  }

  public static OutputStream tee(final OutputStream src, final InputStream snkIn, final OutputStream snkOut) throws IOException {
    pipe(snkIn, src, false, true);
    return new TeeOutputStream(src, snkOut);
  }

  public static OutputStream teeAsync(final OutputStream src, final InputStream snkIn, final OutputStream snkOut) throws IOException {
    pipe(snkIn, src, false, false);
    return new TeeOutputStream(src, snkOut);
  }

  public static InputStream tee(final InputStream src, final OutputStream snk) throws IOException {
    return pipe(src, snk, true, true);
  }

  public static InputStream teeAsync(final InputStream src, final OutputStream snk) throws IOException {
    return pipe(src, snk, true, false);
  }

  public static void pipe(final OutputStream src, final InputStream snk) throws IOException {
    pipe(snk, src, false, true);
  }

  public static void pipeAsync(final OutputStream src, final InputStream snk) throws IOException {
    pipe(snk, src, false, false);
  }

  public static void pipe(final InputStream src, final OutputStream snk) throws IOException {
    pipe(src, snk, false, true);
  }

  public static void pipeAsync(final InputStream src, final OutputStream snk) throws IOException {
    pipe(src, snk, false, false);
  }

  public static InputStream merge(final InputStream a, final InputStream b) throws IOException {
    final PipedOutputStream pipedOut = new PipedOutputStream();
    final InputStream pipedIn = new PipedInputStream(pipedOut, DEFAULT_SOCKET_BUFFER_SIZE);

    Streams.pipeAsync(a, pipedOut);
    Streams.pipeAsync(b, pipedOut);

    return pipedIn;
  }

  private static InputStream pipe(final InputStream src, final OutputStream snk, final boolean tee, final boolean sync) throws IOException {
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
      Streams.pipe(src, snk, pipedOut, DEFAULT_SOCKET_BUFFER_SIZE);
    }
    else {
      new Thread(tee ? "tee" : "pipe") {
        @Override
        public void run() {
          Streams.pipe(src, snk, pipedOut, DEFAULT_SOCKET_BUFFER_SIZE);
        }
      }.start();
    }

    return pipedIn;
  }

  private static boolean dismissException(final IOException e) {
    return "Write end dead".equals(e.getMessage()) || "Broken pipe".equals(e.getMessage()) || "Pipe broken".equals(e.getMessage()) || "Stream closed".equals(e.getMessage()) || "Pipe closed".equals(e.getMessage()) || "Bad file number".equals(e.getMessage());
  }

  private static void pipe(final InputStream src, final OutputStream snk, final PipedOutputStream pipedOut, final int bufferSize) {
    int length;
    final byte[] bytes = new byte[bufferSize];
    try {
      if (pipedOut != null) {
        if (snk != null) {
          while ((length = src.read(bytes)) != -1) {
            pipedOut.write(bytes, 0, length);
            snk.write(bytes, 0, length);

            pipedOut.flush();
            snk.flush();
          }

          pipedOut.flush();
          snk.flush();
        }
        else {
          while ((length = src.read(bytes)) != -1) {
            pipedOut.write(bytes, 0, length);
            pipedOut.flush();
          }

          pipedOut.flush();
        }
      }
      else if (snk != null) {
        while ((length = src.read(bytes)) != -1) {
          snk.write(bytes, 0, length);
          snk.flush();
        }

        snk.flush();
      }
      else {
        while ((length = src.read(bytes)) != -1);
      }
    }
    catch (final IOException e) {
      if (dismissException(e))
        return;

      e.printStackTrace();
    }
  }

  private Streams() {
  }
}