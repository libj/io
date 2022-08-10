/* Copyright (c) 2016 LibJ
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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

public class ReplayInputStreamTest {
  @Test
  public void test() throws IOException {
    final byte[] array = new byte[26];
    for (int i = 0; i < array.length; ++i) // [A]
      array[i] = (byte)('a' + i);

    final ReplayInputStream in = new ReplayInputStream(new ByteArrayInputStream(array));
    try {
      in.buffer.reset(-1);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    // Read 1
    assertEquals('a', in.read());
    try {
      in.buffer.reset(2);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    // Read 3
    final byte[] bytes = new byte[3];
    assertEquals(3, in.read(bytes));
    assertArrayEquals(new byte[] {'b', 'c', 'd'}, bytes);

    // Go back to the beginning and read 3
    in.buffer.reset(0);
    assertEquals(3, in.read(bytes));
    assertArrayEquals(new byte[] {'a', 'b', 'c'}, bytes);

    // Go back 1 step and read 3
    in.buffer.reset(0);
    in.buffer.reset(2);
    assertEquals(3, in.read(bytes));
    assertArrayEquals(new byte[] {'c', 'd', 'e'}, bytes);

    // Go back 1 step and read 3
    in.buffer.reset(4);
    assertEquals(3, in.read(bytes));
    assertArrayEquals(new byte[] {'e', 'f', 'g'}, bytes);

    in.mark(20);
    try {
      in.skip(-1);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }
    assertEquals(3, in.skip(3));
    assertEquals(3, in.read(bytes));
    assertArrayEquals(new byte[] {'k', 'l', 'm'}, bytes);

    in.reset();
    assertEquals(3, in.read(bytes));
    assertArrayEquals(new byte[] {'h', 'i', 'j'}, bytes);
    assertEquals(3, in.skip(3));

    assertEquals(13, in.skip(40));

    in.buffer.reset(in.buffer.size() + in.buffer.available() - 1);
    assertEquals('z', in.read());

    in.buffer.reset(in.buffer.size() + in.buffer.available() - 1);
    assertEquals(1, in.read(bytes));
    assertArrayEquals(new byte[] {'z', 'i', 'j'}, bytes);

    assertEquals(-1, in.read());

    in.buffer.reset(0);

    assertEquals('a', in.read());

    assertEquals(3, in.read(bytes));
    assertArrayEquals(new byte[] {'b', 'c', 'd'}, bytes);

    assertEquals(3, in.skip(3));
    assertEquals(3, in.read(bytes));
    assertArrayEquals(new byte[] {'h', 'i', 'j'}, bytes);

    assertEquals(3, in.skip(3));
    assertEquals(3, in.read(bytes));
    assertArrayEquals(new byte[] {'n', 'o', 'p'}, bytes);

    in.reset();
    assertEquals(3, in.read(bytes));
    assertArrayEquals(new byte[] {'h', 'i', 'j'}, bytes);

    in.buffer.reset(0);

    assertEquals('a', in.read());

    assertEquals(3, in.read(bytes));
    assertArrayEquals(new byte[] {'b', 'c', 'd'}, bytes);

    in.buffer.reset(0);
    assertEquals('a', in.read());

    assertEquals(3, in.read(bytes));
    assertArrayEquals(new byte[] {'b', 'c', 'd'}, bytes);

    in.buffer.reset(in.buffer.size() - 1);
    assertEquals(3, in.read(bytes));
    assertArrayEquals(new byte[] {'d', 'e', 'f'}, bytes);

    in.buffer.reset(in.buffer.size() + in.buffer.available() - 1);
    assertEquals('z', in.read());

    in.buffer.reset(in.buffer.size() + in.buffer.available() - 1);
    assertEquals(1, in.read(bytes));
    assertArrayEquals(new byte[] {'z', 'e', 'f'}, bytes);

    assertEquals(-1, in.read());
  }
}