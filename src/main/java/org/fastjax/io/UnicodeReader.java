/* Copyright (c) 2018 FastJAX
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

package org.fastjax.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Objects;

/**
 * A {@code Reader} for decoding streams of escaped unicode encoded strings
 * (i.e. {@code "\\u48\\u65\\u6C\\u6C\\u6F"} -&gt; {@code "Hello"}). The
 * {@code UnicodeReader} supports:
 * <ul>
 * <li>{@code UTF-8}, {@code UTF-16be}, {@code UTF-16le}, {@code UTF-32be} and
 * {@code UTF-32le} encoded strings, and detects BOM (Byte Order Mark)
 * characters to identify the encoding.</li>
 *
 * <li>Streams can contain mixed unicode encodings:
 *
 * <pre>{@code "\\u48\\u0065\\u0000006C\\ufffe\\u6C00\\ufffe0000\\u6F000000"}
 * -&gt; {@code "Hello"})</pre></li>
 *
 * <li>Streams can contain mixed {@code UTF} multibyte characters, as well as
 * escaped unicode sequences of Latin1 {@code ISO 8859-1} bytes (i.e. intermixed
 * {@code UTF} multibyte characters are preserved).</li>
 *
 * <li>Malformed unicode sequences are returned in their original unicode
 * encoded form:
 *
 * <pre>{@code "\\u48\\u6\\u48"} -&gt; {@code "H\\u6H"}</pre></li>
 *
 * <li>If no BOM is detected, streams are assumed to be in big-endian encoding.</li>
 * </ul>
 */
public class UnicodeReader extends Reader {
  /**
   * Returns the little-endian bit shift amount for the given {@code i}.
   * <p>
   * This function defines the following series:
   *
   * <pre>4 0 12 8 20 16 28 24</pre>
   *
   * @param i The unicode character index plus 2.
   * @return The little-endian bit shift amount for the given {@code i}.
   */
  private static int shift(final int i) {
    return 4 * (i % 2 == 0 ? (i - 1) * ((i - 1) % 2) : (i - 3) * (i % 2));
  }

  private final InputStream reader;
  private final int[] buf = new int[9];

  private int limit = buf.length;
  private int index = 0;
  private int next = -2;

  private boolean le16 = false;
  private boolean le32 = false;
  private boolean flush = false;

  public UnicodeReader(final InputStream reader) {
    Objects.requireNonNull(reader);
    this.reader = reader;
  }

  /**
   * Abort reading a unicode sequence in the event of a malformed encoding.
   *
   * @param ch The char to return.
   * @return The {@code ch} param.
   */
  private int abort(final int ch) {
    index = 0;
    flush = true;
    return ch;
  }

  /**
   * {@inheritDoc}
   * <p>
   * This method automatically detects and replaces escaped sequences into their
   * corresponding UTF character. The BOM (Byte Order Mark) is used to signify
   * the encoding format of the characters that follow. BOM characters are
   * consumed (i.e. the following character is returned, if it is also not a
   * BOM).
   */
  @Override
  public int read() throws IOException {
    while (true) {
      final int ch0;
      if (flush && index < limit) {
        ch0 = buf[index++];
      }
      else if (next != -2) {
        ch0 = next;
        next = -2;
      }
      else {
        ch0 = reader.read();
      }

      if (ch0 != '\\')
        return ch0;

      int i = 0;
      if (flush)
        while (index < limit)
          buf[i++] = buf[index++];

      index = i;
      int ch1 = reader.read();
      buf[index++] = ch1;
      if (ch1 != 'u') {
        limit = index;
        return abort(ch0);
      }

      while (index < buf.length && (next = ch1 = reader.read()) != -1 && ('0' <= ch1 && ch1 <= '9' || 'a' <= ch1 && ch1 <= 'f' || 'A' <= ch1 && ch1 <= 'F'))
        buf[index++] = ch1;

      if (index == buf.length)
        next = -2;

      limit = index;
      index = 1;

      final int end = (flush = limit % 2 == 0) ? limit - 1 : limit;
      if (end == 1)
        return abort(ch0);

      int value = 0;
      if (le16 && end == 5 || le32 && end == 9) {
        while (index < end) {
          final int ch2 = buf[index++];
          if ('0' <= ch2 && ch2 <= '9')
            value += (ch2 - '0') << shift(index);
          else if ('a' <= ch2 && ch2 <= 'f')
            value += (10 + ch2 - 'a') << shift(index);
          else if ('A' <= ch2 && ch2 <= 'F')
            value += (10 + ch2 - 'A') << shift(index);
          else
            return abort(ch0);
        }
      }
      else {
        while (index < end) {
          final int ch2 = buf[index++];
          if ('0' <= ch2 && ch2 <= '9')
            value = (value << 4) + ch2 - '0';
          else if ('a' <= ch2 && ch2 <= 'f')
            value = (value << 4) + 10 + ch2 - 'a';
          else if ('A' <= ch2 && ch2 <= 'F')
            value = (value << 4) + 10 + ch2 - 'A';
          else
            return abort(ch0);
        }
      }

      if (value == 0xfffe) {
        le16 = !le16;
        continue;
      }

      if (value == 0xfffe0000) {
        le32 = !le32;
        continue;
      }

      if (value == 0xfeff || value == 0x0000feff)
        continue;

      return value;
    }
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