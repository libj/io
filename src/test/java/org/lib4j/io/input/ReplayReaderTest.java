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

import static org.junit.Assert.*;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;

import org.junit.Test;

public class ReplayReaderTest {
  @Test
  @SuppressWarnings("resource")
  public void test() throws IOException {
    final char[] array = new char[26];
    for (int i = 0; i < array.length; i++)
      array[i] = (char)('a' + i);

    final Reader in = new ReplayReader(new CharArrayReader(array));
    assertEquals('a', in.read());

    final char[] bytes = new char[3];
    in.read(bytes);
    assertArrayEquals(new char[] {'b', 'c', 'd'}, bytes);

    in.mark(20);
    assertEquals(3, in.skip(3));
    in.read(bytes);
    assertArrayEquals(new char[] {'h', 'i', 'j'}, bytes);

    in.reset();
    in.read(bytes);
    assertArrayEquals(new char[] {'e', 'f', 'g'}, bytes);
    assertEquals(3, in.skip(3));

    assertEquals(16, in.skip(40));
    in.close();

    assertEquals('a', in.read());

    in.read(bytes);
    assertArrayEquals(new char[] {'b', 'c', 'd'}, bytes);

    assertEquals(3, in.skip(3));
    in.read(bytes);
    assertArrayEquals(new char[] {'h', 'i', 'j'}, bytes);

    assertEquals(3, in.skip(3));
    in.read(bytes);
    assertArrayEquals(new char[] {'n', 'o', 'p'}, bytes);

    in.reset();
    in.read(bytes);
    assertArrayEquals(new char[] {'e', 'f', 'g'}, bytes);

    in.close();
    assertEquals('a', in.read());

    in.read(bytes);
    assertArrayEquals(new char[] {'b', 'c', 'd'}, bytes);
  }
}