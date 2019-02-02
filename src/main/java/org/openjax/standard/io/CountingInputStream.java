/* Copyright (c) 2019 OpenJAX
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

package org.openjax.standard.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * An {@code InputStream} that counts the number of bytes read.
 */
public class CountingInputStream extends FilterInputStream {
  private long count;
  private long mark = -1;

  /**
   * Creates a new {@code CountingInputStream} wrapping the specified
   * {@code InputStream}.
   *
   * @param in The output stream to be wrapped.
   * @throws NullPointerException If {@code in} is null.
   */
  public CountingInputStream(final InputStream in) {
    super(Objects.requireNonNull(in));
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
    final int result = in.read();
    if (result != -1)
      ++count;

    return result;
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    final int result = in.read(b, off, len);
    if (result > 0)
      count += result;

    return result;
  }

  @Override
  public long skip(final long n) throws IOException {
    final long result = in.skip(n);
    count += result;
    return result;
  }

  @Override
  public synchronized void mark(final int readlimit) {
    in.mark(readlimit);
    mark = count;
  }

  @Override
  public synchronized void reset() throws IOException {
    if (!in.markSupported())
      throw new IOException("Mark not supported");

    if (mark == -1)
      throw new IOException("Mark not set");

    in.reset();
    count = mark;
  }
}