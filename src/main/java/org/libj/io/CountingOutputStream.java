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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A {@link FilterOutputStream} that counts the number of bytes written.
 */
public class CountingOutputStream extends FilterOutputStream {
  protected long count;

  /**
   * Creates a new {@link CountingOutputStream} wrapping the specified
   * {@link OutputStream}.
   *
   * @param out The output stream to be wrapped.
   * @throws IllegalArgumentException If {@code out} is null.
   */
  public CountingOutputStream(final OutputStream out) {
    super(assertNotNull(out));
  }

  /**
   * Returns the number of bytes written.
   *
   * @return The number of bytes written.
   */
  public long getCount() {
    return count;
  }

  @Override
  public void write(final byte[] b, final int off, final int len) throws IOException {
    out.write(b, off, len);
    count += len;
  }

  @Override
  public void write(final byte[] b) throws IOException {
    out.write(b);
    count += b.length;
  }

  @Override
  public void write(final int b) throws IOException {
    out.write(b);
    ++count;
  }
}