/* Copyright (c) 2022 LibJ
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Random;

/**
 * An {@link InputStream} that generates a stream of random bytes.
 */
public class RandomInputStream extends InputStream {
  private final Random random;
  private volatile boolean closed;
  private final int length;
  private int pos;

  /**
   * Creates a new {@link RandomInputStream} with the provided {@code length} and {@link Random} instance.
   *
   * @param length The length of the stream.
   * @param random The {@link Random} instance to be used for generation of random bytes.
   * @throws NullPointerException If {@code random} is null.
   * @throws IllegalArgumentException If {@code length} is negative.
   */
  public RandomInputStream(final int length, final Random random) {
    this.length = assertNotNegative(length);
    this.random = Objects.requireNonNull(random);
  }

  /**
   * Creates a new {@link RandomInputStream} with the provided {@code length}.
   *
   * @param length The length of the stream.
   * @throws IllegalArgumentException If {@code length} is negative.
   */
  public RandomInputStream(final int length) {
    this(length, new Random());
  }

  private void assertNotClosed() throws IOException {
    if (closed)
      throw new IOException("Input stream closed");
  }

  @Override
  public int read() throws IOException {
    assertNotClosed();
    if (pos++ >= length)
      return -1;

    final int val = random.nextInt() % 256;
    return val < 0 ? -val : val;
  }

  @Override
  public int read(final byte[] b) throws IOException {
    return read(b, 0, b.length);
  }

  private void nextBytes(final byte[] b, final int off, final int len) {
    for (int i = off; i < len;) // [N]
      for (int rnd = random.nextInt(), n = Math.min(len - i, Integer.SIZE / Byte.SIZE); n-- > 0; rnd >>= Byte.SIZE) // [N]
        b[i++] = (byte)rnd;
  }

  @Override
  public int read(final byte[] b, final int off, int len) throws IOException {
    assertNotClosed();
    if (pos >= length)
      return -1;

    nextBytes(b, off, len = Math.min(len, available()));
    pos += len;
    return len;
  }

  @Override
  public long skip(long n) throws IOException {
    assertNotClosed();
    if (pos >= length)
      return 0;

    pos += n;
    return n;
  }

  @Override
  public void close() {
    closed = true;
  }

  @Override
  public int available() {
    return length - pos;
  }
}