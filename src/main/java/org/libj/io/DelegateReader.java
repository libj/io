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
import java.nio.CharBuffer;
import java.util.Objects;

/**
 * A {@link DelegateReader} contains some other input stream, which it uses as its basic source of data, possibly transforming the
 * data along the way or providing additional functionality. The class {@link DelegateReader} itself simply overrides all methods of
 * {@link Reader} with versions that pass all requests to the contained input stream. Subclasses of {@link DelegateReader} may
 * further override some of these methods and may also provide additional methods and fields.
 * <p>
 * This class differentiates itself from {@link java.io.FilterReader} by not locking any of its method calls.
 */
public class DelegateReader extends Reader {
  /** The target reader to be filtered. */
  protected Reader in;

  /**
   * Creates a {@link DelegateReader} by assigning the argument {@code in} to the field {@code this.in} so as to remember it for later
   * use.
   *
   * @param in The underlying input stream, or {@code null} if this instance is to be created without an underlying stream.
   * @throws NullPointerException If {@code in} is null.
   */
  public DelegateReader(final Reader in) {
    this.in = Objects.requireNonNull(in);
  }

  /**
   * Creates a {@link DelegateReader} with a null {@code in}.
   */
  protected DelegateReader() {
  }

  /**
   * Tells whether this stream is ready to be read.
   * <p>
   * This method simply performs {@link #ready() in.ready()} and returns the result.
   *
   * @return {@code true} if the next {@link #read()} is guaranteed not to block for input, {@code false} otherwise. Note that
   *         returning false does not guarantee that the next read will block.
   * @throws IOException If an I/O error has occurred.
   * @see DelegateReader#in
   */
  @Override
  public boolean ready() throws IOException {
    return super.ready();
  }

  /**
   * Reads the next byte of data from this input stream. The value byte is returned as an {@code int} in the range {@code 0} to
   * {@code 255}. If no byte is available because the end of the stream has been reached, the value {@code -1} is returned. This
   * method blocks until input data is available, the end of the stream is detected, or an exception is thrown.
   * <p>
   * This method simply performs {@link #read() in.read()} and returns the result.
   *
   * @return The next byte of data, or {@code -1} if the end of the stream is reached.
   * @throws IOException If an I/O error has occurred.
   * @see DelegateReader#in
   */
  @Override
  public int read() throws IOException {
    return in.read();
  }

  /**
   * Reads characters into an array. This method will block until some input is available, an I/O error occurs, or the end of the
   * stream is reached.
   * <p>
   * This method simply performs the call {@link #read(char[],int,int) read(b, 0, b.length)} and returns the result. It is important
   * that it does <i>not</i> do {@link #read(char[]) in.read(b)} instead; certain subclasses of {@link DelegateReader} depend on the
   * implementation strategy actually used.
   *
   * @param cbuf Destination buffer.
   * @return The number of characters read, or {@code -1} if the end of the stream has been reached.
   * @throws IOException If an I/O error has occurred.
   * @see DelegateReader#read(char[], int, int)
   */
  @Override
  public int read(final char[] cbuf) throws IOException {
    return read(cbuf, 0, cbuf.length);
  }

  /**
   * Reads up to {@code len} bytes of data from this input stream into an array of bytes. If {@code len} is not zero, the method
   * blocks until some input is available; otherwise, no bytes are read and {@code 0} is returned.
   * <p>
   * This method simply performs {@link #read(char[],int,int) in.read(b, off, len)} and returns the result.
   *
   * @param cbuf Destination buffer.
   * @param off Offset at which to start storing characters.
   * @param len Maximum number of characters to read.
   * @return The number of characters read, or {@code -1} if the end of the stream has been reached.
   * @throws NullPointerException If {@code b} is null.
   * @throws IndexOutOfBoundsException If {@code off} is negative, {@code len} is negative, or {@code len} is greater than
   *           {@code b.length - off}.
   * @throws IOException If an I/O error has occurred.
   * @see DelegateReader#in
   */
  @Override
  public int read(final char[] cbuf, final int off, final int len) throws IOException {
    return in.read(cbuf, off, len);
  }

  /**
   * Attempts to read characters into the specified character buffer. The buffer is used as a repository of characters as-is: the only
   * changes made are the results of a put operation. No flipping or rewinding of the buffer is performed.
   * <p>
   * This method simply performs {@link #read(CharBuffer) in.readCharBuffer)} and returns the result.
   *
   * @param target The buffer to read characters into.
   * @return The number of characters added to the buffer, or {@code -1} if this source of characters is at its end.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If target is null.
   * @throws java.nio.ReadOnlyBufferException If target is a read only buffer.
   */
  @Override
  public int read(final CharBuffer target) throws IOException {
    return super.read(target);
  }

  /**
   * Skips over and discards {@code n} bytes of data from the input stream. The {@link #skip(long)} method may, for a variety of
   * reasons, end up skipping over some smaller number of bytes, possibly {@code 0}. The actual number of bytes skipped is returned.
   * <p>
   * This method simply performs {@link #skip(long) in.skip(n)}.
   *
   * @param n The number of bytes to be skipped.
   * @return The actual number of bytes skipped.
   * @throws IOException If the stream does not support seek, or if some other I/O error occurs.
   */
  @Override
  public long skip(final long n) throws IOException {
    return in.skip(n);
  }

  /**
   * Closes this input stream and releases any system resources associated with the stream. This method simply performs
   * {@link #close() in.close()}.
   *
   * @throws IOException If an I/O error has occurred.
   * @see DelegateReader#in
   */
  @Override
  public void close() throws IOException {
    in.close();
  }

  /**
   * Marks the current position in this input stream. A subsequent call to the {@link #reset()} method repositions this stream at the
   * last marked position so that subsequent reads re-read the same bytes.
   * <p>
   * The {@code readlimit} argument tells this input stream to allow that many bytes to be read before the mark position gets
   * invalidated.
   * <p>
   * This method simply performs {@link #mark(int) in.mark(readlimit)}.
   *
   * @param readlimit The maximum limit of bytes that can be read before the mark position becomes invalid.
   * @throws IOException If the delegated stream does not support mark(), or if some other I/O error occurs.
   * @see DelegateReader#in
   * @see DelegateReader#reset()
   */
  @Override
  public void mark(final int readlimit) throws IOException {
    in.mark(readlimit);
  }

  /**
   * Repositions this stream to the position at the time the {@code mark} method was last called on this input stream.
   * <p>
   * This method simply performs {@link #reset() in.reset()}.
   * <p>
   * Stream marks are intended to be used in situations where you need to read ahead a little to see what's in the stream. Often this
   * is most easily done by invoking some general parser. If the stream is of the type handled by the parse, it just chugs along
   * happily. If the stream is not of that type, the parser should toss an exception when it fails. If this happens within readlimit
   * bytes, it allows the outer code to reset the stream and try another parser.
   *
   * @throws IOException If the stream has not been marked or if the mark has been invalidated.
   * @see DelegateReader#in
   * @see DelegateReader#mark(int)
   */
  @Override
  public void reset() throws IOException {
    in.reset();
  }

  /**
   * Tests if this input stream supports the {@link #mark(int)} and {@link #reset()} methods. This method simply performs
   * {@link #markSupported() in.markSupported()}.
   *
   * @return {@code true} if this stream type supports the {@link #mark(int)} and {@link #reset()} methods; {@code false} otherwise.
   * @see DelegateReader#in
   * @see Reader#mark(int)
   * @see Reader#reset()
   */
  @Override
  public boolean markSupported() {
    return in.markSupported();
  }
}