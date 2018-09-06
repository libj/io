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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

import org.junit.Test;

public class DecodingReaderTest {
  private static void testUtf8(final String expected, final String test) throws IOException {
    try (final Reader reader = new DecodingReader(new ByteArrayInputStream(test.getBytes()), StandardCharsets.UTF_8)) {
      final String actual = Math.random() < 0.5 ? Readers.readFully(reader) : Readers.readFully(reader, 1 + (int)(Math.random() * 50));
      assertEquals(expected, actual);
    }
  }

  @Test
  @SuppressWarnings({"resource", "unused"})
  public void testExceptions() {
    try {
      new DecodingReader(null, StandardCharsets.UTF_8);
      fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    try {
      new DecodingReader(new ByteArrayInputStream("".getBytes()), StandardCharsets.UTF_16);
      fail("Expected UnsupportedCharsetException");
    }
    catch (final UnsupportedCharsetException e) {
    }
  }

  @Test
  public void testFooBar() throws IOException {
    testUtf8("Foo ©", "\\u0046\\u006F\\u006F\\u0020\\u00A9");
    testUtf8("bar ð", "\\u0062\\u0061\\u0072\\u0020\\u00F0\\u009D\\u008C\\u0086");
    testUtf8("baz â qux", "\\u0062\\u0061\\u007a\\u0020\\u00e2\\u0098\\u0083\\u0020\\u0071\\u0075\\u0078");
    testUtf8("\\\"beca", "\\\"beca");
    testUtf8("\\\\/", "\\\\/");
  }

  @Test
  public void testHelloWorld() throws IOException {
    testUtf8("Hello World", "\\u0048\\u0065\\u006C\\u006C\\u006F World");

    testUtf8("\\u004ello World", "\\u004\\u0065\\u006C\\u006C\\u006F World");
    testUtf8("\\u00ello World", "\\u00\\u0065\\u006C\\u006C\\u006F World");
    testUtf8("\\u0ello World", "\\u0\\u0065\\u006C\\u006C\\u006F World");
    testUtf8("\\uello World", "\\u\\u0065\\u006C\\u006C\\u006F World");
    testUtf8("\\ello World", "\\\\u0065\\u006C\\u006C\\u006F World");

    testUtf8("Hell\\u006", "\\u0048\\u0065\\u006C\\u006C\\u006");
    testUtf8("Hell\\u00", "\\u0048\\u0065\\u006C\\u006C\\u00");
    testUtf8("Hell\\u0", "\\u0048\\u0065\\u006C\\u006C\\u0");
    testUtf8("Hell\\u", "\\u0048\\u0065\\u006C\\u006C\\u");
    testUtf8("Hell\\", "\\u0048\\u0065\\u006C\\u006C\\");

    testUtf8("He\\u006lo", "\\u0048\\u0065\\u006\\u006C\\u006F");
    testUtf8("He\\u00lo", "\\u0048\\u0065\\u00\\u006C\\u006F");
    testUtf8("He\\u0lo", "\\u0048\\u0065\\u0\\u006C\\u006F");
    testUtf8("He\\ulo", "\\u0048\\u0065\\u\\u006C\\u006F");
    testUtf8("He\\lo", "\\u0048\\u0065\\\\u006C\\u006F");
  }
}