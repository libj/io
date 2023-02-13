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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * This class implements a FilterInputStream that allows its content to be re-read. With each call to its read methods, content is
 * written to an underlying buffer that automatically grows. This implementation supports a maximum re-readable buffer length of
 * {@link Integer#MAX_VALUE}.
 */
public class ReplayInputStream extends DelegateInputStream {
  /**
   * A byte array output stream with readback APIs.
   */
  protected class ReadbackByteArrayOutputStream extends ByteArrayOutputStream {
    private int total;
    private int mark;

    /**
     * Creates a new {@link ReadbackByteArrayOutputStream} with the specified initial size.
     *
     * @param initialSize An int specifying the initial buffer size.
     * @throws IllegalArgumentException If {@code initialSize} is negative.
     */
    public ReadbackByteArrayOutputStream(final int initialSize) {
      super(initialSize);
    }

    /**
     * Creates a new {@link ReadbackByteArrayOutputStream}.
     */
    public ReadbackByteArrayOutputStream() {
      super();
    }

    /**
     * Returns the buffer where data is stored.
     *
     * @return The buffer where data is stored.
     */
    public byte[] buf() {
      return buf;
    }

    @Override
    @SuppressWarnings("sync-override")
    public void write(final int c) {
      if (closed)
        return;

      super.write(c);
      ++total;
    }

    @Override
    @SuppressWarnings("sync-override")
    public void write(final byte[] c, final int off, final int len) {
      if (closed)
        return;

      super.write(c, off, len);
      total += len;
    }

    /**
     * Reads a single byte from the buffer.
     *
     * @return The byte read, as an integer in the range 0 to 255, or -1 if the end of the buffer has been reached.
     */
    public int read() {
      return closed || count >= total ? -1 : buf[count++];
    }

    /**
     * Reads bytes into an array.
     *
     * @param b Destination buffer.
     * @return The number of bytes read, or -1 if the end of the stream has been reached.
     * @throws NullPointerException If {@code b} is null.
     */
    public int read(final byte[] b) {
      return read(b, 0, b.length);
    }

    /**
     * Reads bytes into a portion of an array.
     *
     * @param b Destination buffer.
     * @param off Offset at which to start storing bytes.
     * @param len Maximum number of bytes to read.
     * @return The number of bytes read, or -1 if the end of the stream has been reached.
     * @throws IndexOutOfBoundsException If {@code off} is negative, or {@code len} is negative, or {@code len} is greater than
     *           {@code b.length - off}.
     * @throws NullPointerException If {@code b} is null.
     */
    public int read(final byte[] b, final int off, final int len) {
      if (closed || count >= total)
        return -1;

      final int delta = len - available() - 1;
      final int length = 0 < delta ? len - delta : len;
      System.arraycopy(buf, count, b, off, length);
      count += length;
      return length;
    }

    /**
     * Skips bytes.
     *
     * @param n The number of bytes to skip.
     * @return The number of bytes actually skipped.
     * @throws IllegalArgumentException If {@code n} is negative.
     */
    public long skip(final long n) {
      if (n < 0)
        throw new IllegalArgumentException("Skip value is negative: " + n);

      return skip0(n);
    }

    /**
     * Skips bytes.
     *
     * @param n The number of bytes to skip.
     * @return The number of bytes actually skipped.
     */
    private long skip0(final long n) {
      if (closed || count >= total || n <= 0)
        return 0;

      final long check = total - count - n;
      final long length = check < 0 ? n + check : n;
      count += length;
      return length;
    }

    /**
     * Returns the number of bytes available to read from the buffer.
     *
     * @return The number of bytes available to read from the buffer.
     */
    public int available() {
      return closed ? 0 : total - count;
    }

    /**
     * Marks the present position in the stream. Subsequent calls to {@link #reset()} will attempt to reposition the stream to this
     * point.
     */
    public void mark() {
      mark = count;
    }

    /**
     * Resets the buffer to the position value of the argument.
     *
     * @param p The position to reset to.
     */
    private void reset0(final int p) {
      if (p > count)
        skip0(p - count);

      count = p;
    }

    /**
     * Resets the buffer to the position value of the argument.
     *
     * @param p The position to reset to.
     * @throws IllegalArgumentException If {@code p} is negative, or if {@code p} exceeds the buffer length.
     */
    public void reset(final int p) {
      if (p < 0)
        throw new IllegalArgumentException("Position (" + p + ") must be non-negative");

      if (total < p)
        throw new IllegalArgumentException("Position (" + p + ") must be less than the buffer length (" + total + ")");

      reset0(p);
    }

    /**
     * Resets the buffer to the position previously marked by {@link #mark()}.
     */
    @Override
    @SuppressWarnings("sync-override")
    public void reset() {
      reset0(mark);
    }

    /**
     * Close the stream, and release the buffer.
     */
    @Override
    public void close() {
      buf = null;
    }
  }

  protected final ReadbackByteArrayOutputStream buffer;
  private volatile boolean closed;

  /**
   * Creates a new {@link ReplayInputStream} using the specified {@link InputStream} as its source, and the provided initial size
   * for the re-readable buffer.
   *
   * @param in An InputStream object providing the underlying stream.
   * @param initialSize An int specifying the initial buffer size of the re-readable buffer.
   * @throws NullPointerException If {@code in} is null.
   * @throws IllegalArgumentException If {@code initialSize} is negative.
   */
  public ReplayInputStream(final InputStream in, final int initialSize) {
    super(Objects.requireNonNull(in));
    this.buffer = new ReadbackByteArrayOutputStream(initialSize);
  }

  /**
   * Creates a new {@link ReplayInputStream} using the specified {@link InputStream} as its source, and default initial size of 32
   * for the re-readable buffer.
   *
   * @param in A Reader object providing the underlying stream.
   * @throws NullPointerException If {@code in} is null.
   */
  public ReplayInputStream(final InputStream in) {
    super(Objects.requireNonNull(in));
    this.buffer = new ReadbackByteArrayOutputStream();
  }

  /**
   * Reads a single byte. If the stream's position was previously reset resulting in the buffer having a byte available to be
   * re-read, the byte will be re-read from the underlying buffer. Otherwise, a byte will be read from the underlying stream, in
   * which case this method will block until a byte is available, an I/O error occurs, or the end of the stream is reached.
   *
   * @throws IOException If an I/O error has occurred.
   * @return The byte read, as an integer in the range 0 to 255, or -1 if the end of the stream has been reached.
   */
  @Override
  public int read() throws IOException {
    if (buffer.available() > 0)
      return buffer.read();

    if (closed)
      return -1;

    final int ch = in.read();
    if (ch != -1)
      buffer.write(ch);

    return ch;
  }

  /**
   * Reads bytes into an array. If the stream's position was previously reset resulting in the buffer having bytes available to be
   * re-read, the available bytes will be re-read from the underlying buffer. The remaining bytes will be read from the underlying
   * stream, in which case this method will block bytes are available, an I/O error occurs, or the end of the stream is reached.
   *
   * @param b Destination buffer.
   * @return The number of bytes read, or -1 if the end of the stream has been reached.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code b} is null.
   */
  @Override
  public int read(final byte[] b) throws IOException {
    return read(b, 0, b.length);
  }

  /**
   * Reads bytes into a portion of an array. If the stream's position was previously reset resulting in the buffer having bytes
   * available to be re-read, the available bytes will be re-read from the underlying buffer. The remaining bytes will be read from
   * the underlying stream, in which case this method will block bytes are available, an I/O error occurs, or the end of the stream
   * is reached.
   *
   * @param b Destination buffer.
   * @param off Offset at which to start storing bytes.
   * @param len Maximum number of bytes to read.
   * @return The number of bytes read, or -1 if the end of the stream has been reached.
   * @throws IOException If an I/O error has occurred.
   * @throws IndexOutOfBoundsException If {@code off} is negative, or {@code len} is negative, or {@code len} is greater than
   *           {@code b.length - off}.
   * @throws NullPointerException If {@code b} is null.
   */
  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    int avail = buffer.available();
    if (avail >= len)
      return buffer.read(b, off, len);

    if (avail > 0) {
      buffer.read(b, off, avail);
      for (int ch; avail < b.length && (ch = read()) != -1; b[off + avail++] = (byte)ch)
        ;
      return avail;
    }

    if (closed)
      return -1;

    final int ch = in.read(b, off, len);
    if (ch > 0)
      buffer.write(b, off, ch);

    return ch;
  }

  /**
   * Skips bytes. If the stream's position was previously reset resulting in the buffer having bytes available to be re-read, the
   * available bytes will first be skipped in the underlying buffer. The remaining bytes will be read from the underlying stream,
   * written to the buffer, and skipped, in which case this method will block bytes are available, an I/O error occurs, or the end
   * of the stream is reached.
   *
   * @param n The number of bytes to skip.
   * @return The number of bytes actually skipped.
   * @throws IllegalArgumentException If {@code n} is negative.
   * @throws IOException If an I/O error has occurred.
   */
  @Override
  public long skip(final long n) throws IOException {
    if (n < 0)
      throw new IllegalArgumentException("Skip value is negative: " + n);

    int avail = buffer.available();
    if (avail >= n)
      return buffer.skip(n);

    if (avail > 0) {
      buffer.skip(avail);
      while (avail++ < n && read() != -1)
        ;
      return avail;
    }

    while (read() != -1 && ++avail < n)
      ;
    return avail;
  }

  /**
   * Returns an estimate of the number of bytes that can be read (or skipped over) from this input stream without blocking by the
   * next caller of a method for this input stream. If the stream's position was previously reset resulting in the buffer having
   * bytes available to be re-read, the available bytes will contribute to the total returned by this method. The remaining estimate
   * is delegated to the available() method of the underlying stream.
   *
   * @return An estimate of the number of bytes that can be read (or skipped over) from this input stream without blocking.
   * @throws IOException If an I/O error has occurred.
   */
  @Override
  public int available() throws IOException {
    return buffer.available() + in.available();
  }

  /**
   * Marks the present position in the stream. Subsequent calls to {@link #reset()} will attempt to reposition the stream to this
   * point.
   *
   * @param readlimit This argument is ignored.
   */
  @Override
  public void mark(final int readlimit) {
    in.mark(readlimit);
    buffer.mark();
  }

  /**
   * Tells whether this stream supports the {@link #mark(int)} operation, which is always {@code true}.
   *
   * @return {@code true}.
   */
  @Override
  public boolean markSupported() {
    return true;
  }

  /**
   * Resets the stream to a location previously marked with the {@link #mark(int)} method.
   */
  @Override
  public void reset() {
    buffer.reset();
  }

  /**
   * Closes the stream and releases any system resources associated with it. Once the stream has been closed, further
   * {@link #read()}, {@link #available()}, {@link #mark(int)}, {@link #reset()}, or {@link #skip(long)} invocations will throw an
   * {@link IOException}. Closing a previously closed stream has no effect.
   *
   * @throws IOException If an I/O error has occurred.
   */
  @Override
  public void close() throws IOException {
    buffer.close();
    in.close();
    closed = true;
  }
}