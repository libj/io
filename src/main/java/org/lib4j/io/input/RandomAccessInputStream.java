/* Copyright (c) 2012 lib4j
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

package org.lib4j.io.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public final class RandomAccessInputStream extends InputStream {
  private final RandomAccessFile file;
  private long mark = 0;

  public RandomAccessInputStream(final File file) throws FileNotFoundException {
    this.file = new RandomAccessFile(file, "r");
  }

  @Override
  public int available() throws IOException {
    final long availableBytes = file.length() - file.getFilePointer();
    if (availableBytes > 0x7fffffffl)
      return 0x7fffffff;

    return (int)availableBytes;
  }

  @Override
  public void close() throws IOException {
    file.close();
  }

  @Override
  public void mark(final int readlimit) {
    try {
      this.mark = file.getFilePointer();
    }
    catch (final IOException e) {
      this.mark = -1;
    }
  }

  @Override
  public boolean markSupported() {
    return true;
  }

  @Override
  public int read() throws IOException {
    return file.read();
  }

  @Override
  public int read(final byte[] b) throws IOException {
    return file.read(b);
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    return file.read(b, off, len);
  }

  @Override
  public void reset() throws IOException {
    if (mark < 0)
      throw new IOException("Invalid mark position");

    file.seek(mark);
  }

  @Override
  public long skip(final long n) throws IOException {
    final long position = file.getFilePointer();
    try {
      file.seek(n + position);
    }
    catch (final IOException e) {
      if (!"Negative seek offset".equals(e.getMessage()))
        throw e;
    }

    return file.getFilePointer() - position;
  }
}