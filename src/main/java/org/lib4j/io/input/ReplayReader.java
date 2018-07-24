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

package org.lib4j.io.input;

import java.io.CharArrayWriter;
import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class ReplayReader extends FilterReader {
  protected class ReadbackCharArrayWriter extends CharArrayWriter {
    private int length;
    private int mark;

    public ReadbackCharArrayWriter(final int size) {
      super(size);
    }

    public ReadbackCharArrayWriter() {
      super();
    }

    @Override
    public CharArrayWriter append(final char c) {
      final CharArrayWriter writer = super.append(c);
      ++length;
      return writer;
    }

    @Override
    public CharArrayWriter append(final CharSequence csq) {
      final CharArrayWriter writer = super.append(csq);
      length += csq.length();
      return writer;
    }

    @Override
    public CharArrayWriter append(final CharSequence csq, final int start, final int end) {
      final CharArrayWriter writer = super.append(csq, start, end);
      length += end - start;
      return writer;
    }

    @Override
    public void write(final int c) {
      super.write(c);
      ++length;
    }

    @Override
    public void write(final char[] c, final int off, final int len) {
      super.write(c, off, len);
      length += len;
    }

    @Override
    public void write(final String str, final int off, final int len) {
      super.write(str, off, len);
      length += len;
    }

    @Override
    public void write(final char[] cbuf) throws IOException {
      super.write(cbuf);
      length += cbuf.length;
    }

    @Override
    public void write(final String str) throws IOException {
      super.write(str);
      length += str.length();
    }

    public int read() {
      if (!isReadable())
        throw new IllegalStateException("Reader has not been closed");

      return count == length ? -1 : buf[count++];
    }

    public int read(final char[] b) {
      return read(b, 0, b.length);
    }

    public int read(final char[] b, final int off, final int len) {
      if (!isReadable())
        throw new IllegalStateException("Reader has not been closed");

      final int check = length - count - len;
      final int length = check < 0 ? len + check : len;
      System.arraycopy(buf, count, b, off, length);
      count += length;
      return length;
    }

    public long skip(final long n) {
      if (!isReadable())
        throw new IllegalStateException("Reader has not been closed");

      if (n <= 0)
        return 0;

      final long check = length - count - n;
      final long length = check < 0 ? n + check : n;
      count += length;
      return length;
    }

    public void mark() {
      mark = count;
    }

    @Override
    public void reset() {
      length = count;
      count = mark;
    }

    public boolean isReadable() {
      return length != count;
    }

    @Override
    public void close() {
      count = 0;
    }
  }

  protected final ReadbackCharArrayWriter buffer;

  public ReplayReader(final Reader in, final int size) {
    super(in);
    this.buffer = new ReadbackCharArrayWriter(size);
  }

  public ReplayReader(final Reader in) {
    super(in);
    this.buffer = new ReadbackCharArrayWriter();
  }

  @Override
  public int read() throws IOException {
    if (buffer.isReadable())
      return buffer.read();

    final int by = in.read();
    if (by != -1)
      buffer.write(by);

    return by;
  }

  @Override
  public int read(final char[] b) throws IOException {
    if (buffer.isReadable())
      return buffer.read(b);

    final int by = in.read(b);
    if (by > 0)
      buffer.write(b, 0, by);

    return by;
  }

  @Override
  public int read(final char[] b, final int off, final int len) throws IOException {
    if (buffer.isReadable())
      return buffer.read(b, off, len);

    final int by = in.read(b, off, len);
    if (by > 0)
      buffer.write(b, off, by);

    return by;
  }

  @Override
  public long skip(final long n) throws IOException {
    if (buffer.isReadable())
      return buffer.skip(n);

    for (int i = 0; i < n; i++)
      if (read() == -1)
        return i;

    return n;
  }

  @Override
  public void mark(final int readlimit) {
    buffer.mark();
  }

  @Override
  public boolean markSupported() {
    return true;
  }

  @Override
  public void reset() throws IOException {
    buffer.reset();
  }

  @Override
  public void close() throws IOException {
    in.close();
    buffer.close();
  }
}