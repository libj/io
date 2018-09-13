/* Copyright (c) 2016 FastJAX
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
import java.io.Reader;

public final class Readers {
  public static String readFully(final Reader reader) throws IOException {
    final StringBuilder builder = new StringBuilder();
    for (int ch; (ch = reader.read()) != -1; builder.append((char)ch));
    return builder.toString();
  }

  public static String readFully(final Reader reader, final int bufferSize) throws IOException {
    if (bufferSize <= 0)
      throw new IllegalArgumentException("bufferSize <= 0: " + bufferSize);

    final StringBuilder builder = new StringBuilder();
    final char[] cbuf = new char[bufferSize];
    for (int size; (size = reader.read(cbuf)) > 0; builder.append(cbuf, 0, size));
    return builder.toString();
  }

  private Readers() {
  }
}