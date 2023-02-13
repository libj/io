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

package org.libj.io;

import java.io.IOException;
import java.io.Reader;

import org.libj.lang.Assertions;

/**
 * An <b>unsynchronized</b> character stream whose source is a string.
 *
 * @see java.io.StringReader
 */
public class UnsynchronizedStringReader extends Reader {
  private String str;
  private final int length;
  private int next = 0;
  private int mark = 0;

  /**
   * Creates a new {@link UnsynchronizedStringReader}.
   *
   * @param s String providing the character stream.
   * @throws NullPointerException If {@code s} is null.
   */
  public UnsynchronizedStringReader(final String s) {
    this.str = s;
    this.length = s.length();
  }

  /** Check to make sure that the stream has not been closed */
  private void ensureOpen() throws IOException {
    if (str == null)
      throw new IOException("Stream closed");
  }

  /**
   * Reads a single character.
   *
   * @return The character read, or -1 if the end of the stream has been reached.
   * @throws IOException If an I/O error has occurred.
   */
  @Override
  public int read() throws IOException {
    ensureOpen();
    return next >= length ? -1 : str.charAt(next++);
  }

  /**
   * Reads characters into a portion of an array.
   * <p>
   * If {@code len} is zero, then no characters are read and {@code 0} is returned; otherwise, there is an attempt to read at least
   * one character. If no character is available because the stream is at its end, the value {@code -1} is returned; otherwise, at
   * least one character is read and stored into {@code cbuf}.
   *
   * @param cbuf {@inheritDoc}
   * @param off {@inheritDoc}
   * @param len {@inheritDoc}
   * @return {@inheritDoc}
   * @throws IOException If an I/O error has occurred.
   */
  @Override
  public int read(final char[] cbuf, final int off, final int len) throws IOException {
    ensureOpen();
    Assertions.assertBoundsOffsetCount("cbuf.length", cbuf.length, "off", off, "len", len);
    if (len == 0)
      return 0;

    if (next >= length)
      return -1;

    final int n = Math.min(length - next, len);
    str.getChars(next, next += n, cbuf, off);
    return n;
  }

  /**
   * Skips characters. If the stream is already at its end before this method is invoked, then no characters are skipped and zero is
   * returned.
   * <p>
   * The {@code n} parameter may be negative, even though the {@link #skip(long)} method of the {@link Reader} superclass throws an
   * exception in this case. Negative values of {@code n} cause the stream to skip backwards. Negative return values indicate a skip
   * backwards. It is not possible to skip backwards past the beginning of the string.
   * <p>
   * If the entire string has been read or skipped, then this method has no effect and always returns {@code 0}.
   *
   * @param n {@inheritDoc}
   * @return {@inheritDoc}
   * @throws IOException {@inheritDoc}
   */
  @Override
  public long skip(final long n) throws IOException {
    ensureOpen();
    if (next >= length)
      return 0;

    // Bound skip by beginning and end of the source
    final long r = Math.max(-next, Math.min(length - next, n));
    next += r;
    return r;
  }

  /**
   * Tells whether this stream is ready to be read.
   *
   * @return {@code true} if the next {@link #read()} is guaranteed not to block for input.
   * @throws IOException If the stream is closed.
   */
  @Override
  public boolean ready() throws IOException {
    ensureOpen();
    return true;
  }

  /**
   * Tells whether this stream supports the mark() operation, which it does.
   */
  @Override
  public boolean markSupported() {
    return true;
  }

  /**
   * Marks the present position in the stream. Subsequent calls to reset() will reposition the stream to this point.
   *
   * @param readAheadLimit Limit on the number of characters that may be read while still preserving the mark. Because the stream's
   *          input comes from a string, there is no actual limit, so this argument must not be negative, but is otherwise ignored.
   * @throws IllegalArgumentException If {@code readAheadLimit < 0}.
   * @throws IOException If an I/O error has occurred.
   */
  @Override
  public void mark(final int readAheadLimit) throws IOException {
    if (readAheadLimit < 0)
      throw new IllegalArgumentException("Read-ahead limit < 0");

    ensureOpen();
    mark = next;
  }

  /**
   * Resets the stream to the most recent mark, or to the beginning of the string if it has never been marked.
   *
   * @throws IOException If an I/O error has occurred.
   */
  @Override
  public void reset() throws IOException {
    ensureOpen();
    next = mark;
  }

  /**
   * Closes the stream and releases any system resources associated with it. Once the stream has been closed, further
   * {@link #read()}, {@link #ready()}, {@link #mark(int)}, or {@link #reset()} invocations will throw an {@link IOException}.
   * Closing a previously closed stream has no effect. This method will block while there is another thread blocking on the reader.
   */
  @Override
  public void close() {
    str = null;
  }
}