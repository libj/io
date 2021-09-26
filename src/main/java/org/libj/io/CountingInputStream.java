/* Copyright (c) 2019 LibJ
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

import static org.libj.lang.Assertions.*;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An {@link InputStream} that counts the number of bytes read.
 */
public class CountingInputStream extends FilterInputStream {
  protected long count;
  private long mark = -1;

  /**
   * Creates a new {@link CountingInputStream} wrapping the specified
   * {@link InputStream}.
   *
   * @param in The output stream to be wrapped.
   * @throws IllegalArgumentException If {@code in} is null.
   */
  public CountingInputStream(final InputStream in) {
    super(assertNotNull(in));
  }

  /**
   * Returns the number of bytes read.
   *
   * @return The number of bytes read.
   */
  public long getCount() {
    return count;
  }

  @Override
  public int read() throws IOException {
    final int ch = in.read();
    if (ch != -1)
      ++count;

    return ch;
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    final int read = in.read(b, off, len);
    if (read > 0)
      count += read;

    return read;
  }

  @Override
  public long skip(final long n) throws IOException {
    final long skip = in.skip(n);
    count += skip;
    return skip;
  }

  @Override
  public int available() throws IOException {
    return in.available();
  }

  @Override
  public boolean markSupported() {
    return in.markSupported();
  }

  @Override
  @SuppressWarnings("sync-override")
  public void mark(final int readlimit) {
    synchronized (in) {
      in.mark(readlimit);
      mark = count;
    }
  }

  @Override
  @SuppressWarnings("sync-override")
  public void reset() throws IOException {
    synchronized (in) {
      if (!in.markSupported())
        throw new IOException("Mark not supported");

      if (mark == -1)
        throw new IOException("Mark not set");

      in.reset();
      count = mark;
    }
  }

  @Override
  public void close() throws IOException {
    in.close();
  }
}