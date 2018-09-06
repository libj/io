/* Copyright (c) 2018 lib4j
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

package org.lib4j.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Objects;

public class DecodingReader extends Reader {
  private static final int bufferLen = 5;
  private final InputStream reader;
  private final Charset charset;

  // FIXME: Implement other charsets
  public DecodingReader(final InputStream reader, final Charset charset) {
    Objects.requireNonNull(reader);
    this.reader = reader;
    this.charset = charset;
    if (charset != StandardCharsets.UTF_8)
      throw new UnsupportedCharsetException(charset.name());
  }

  private final CharBuffer buf = CharBuffer.allocate(bufferLen);
  private boolean flush = false;

  private int abort(final CharBuffer buf, final int ch) {
    buf.rewind();
    flush = true;
    return ch;
  }

  @Override
  public int read() throws IOException {
    final int ch0 = flush && buf.hasRemaining() ? buf.get() : reader.read();
    if (ch0 != '\\' || ch0 == -1)
      return ch0;

    int i = 0;
    if (flush && buf.hasRemaining())
      while (buf.hasRemaining())
        buf.array()[i++] = buf.get();

    buf.position(i);
    flush = false;
    int ch1 = 0;
    while (buf.hasRemaining() && (ch1 = reader.read()) != -1)
      buf.put((char)ch1);

    if (ch1 == -1) {
      buf.limit(buf.position());
      return abort(buf, ch0);
    }

    buf.position(0);
    if (buf.get() != 'u')
      return abort(buf, ch0);

    while (buf.hasRemaining()) {
      final char ch2 = buf.get();
      if (ch2 < '0' || '9' < ch2 && ch2 < 'A' || 'F' < ch2 && ch2 < 'a' || 'f' < ch2)
        return abort(buf, ch0);
    }

    buf.position(1);
    return (char)Integer.parseInt(buf, 0, bufferLen - 1, 16);
  }

  @Override
  public int read(final char[] cbuf, final int off, final int len) throws IOException {
    int i = 0;
    for (int ch; i < len && (ch = read()) != -1; ++i)
      cbuf[off + i] = (char)ch;

    return i;
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }
}