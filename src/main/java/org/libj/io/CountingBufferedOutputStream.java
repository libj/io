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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * A {@link BufferedOutputStream} that counts the number of bytes written.
 *
 * @implNote This class is not thread safe.
 */
public class CountingBufferedOutputStream extends BufferedOutputStream {
  protected long count;

  /**
   * Creates a new {@link CountingBufferedOutputStream} wrapping the specified {@link OutputStream}, with a buffer size of
   * {@code 8192}.
   *
   * @param out The output stream to be wrapped.
   * @throws NullPointerException If {@code out} is null.
   */
  public CountingBufferedOutputStream(final OutputStream out) {
    super(Objects.requireNonNull(out));
  }

  /**
   * Creates a new {@link CountingBufferedOutputStream} wrapping the specified {@link OutputStream}, with the provided buffer
   * {@code size}.
   *
   * @param out The output stream to be wrapped.
   * @param size The buffer size.
   * @throws NullPointerException If {@code out} is null.
   * @throws IllegalArgumentException if {@code size <= 0}.
   */
  public CountingBufferedOutputStream(final OutputStream out, final int size) {
    super(Objects.requireNonNull(out), size);
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
  @SuppressWarnings("sync-override")
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
  @SuppressWarnings("sync-override")
  public void write(final int b) throws IOException {
    out.write(b);
    ++count;
  }
}