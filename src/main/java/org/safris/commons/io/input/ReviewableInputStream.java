/* Copyright (c) 2016 lib4j
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

package org.safris.commons.io.input;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReviewableInputStream extends InputStream {
  private final InputStream in;

  public ReviewableInputStream(final InputStream in, final int size) {
    this.in = new BufferedInputStream(in, size);
    this.in.mark(Integer.MAX_VALUE);
  }

  public ReviewableInputStream(final InputStream in) {
    this.in = new BufferedInputStream(in);
    this.in.mark(Integer.MAX_VALUE);
  }

  @Override
  public int read() throws IOException {
    return in.read();
  }

  @Override
  public int read(final byte[] b) throws IOException {
    return in.read(b);
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    return in.read(b, off, len);
  }

  @Override
  public long skip(final long n) throws IOException {
    return in.skip(n);
  }

  @Override
  public int available() throws IOException {
    return in.available();
  }

  @Override
  public void close() throws IOException {
    in.reset();
  }

  @Override
  public synchronized void mark(int readlimit) {
  }

  @Override
  public synchronized void reset() throws IOException {
  }

  @Override
  public boolean markSupported() {
    return false;
  }

  public void destroy() throws IOException {
    in.close();
  }
}