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

import org.junit.Ignore;
import org.junit.Test;

public class UnicodeReaderTest {
  private static void test(final String expected, final String test) throws IOException {
    try (final Reader reader = new UnicodeReader(new ByteArrayInputStream(test.getBytes()))) {
      final String actual = Math.random() < 0.5 ? Readers.readFully(reader) : Readers.readFully(reader, 1 + (int)(Math.random() * 50));
      assertEquals(expected, actual);
    }
  }

  @Test
  public void test() {
    for (int i = 0; i < 8; i++) {
      final int f = i * 4;
      System.err.print(f + " ");
    }

    System.err.println();
    for (int i = 0; i < 8; i++) {
      final int g = 4 * (i % 2 == 0 ? (i + 1) * ((i + 1) % 2) : (i - 1) * (i % 2));
      System.err.print(g + " ");
    }
  }

  @Test
  @SuppressWarnings({"resource", "unused"})
  public void testExceptions() {
    try {
      new UnicodeReader(null);
      fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }
  }

  @Test
  public void testUtf8() throws IOException {
    test("o", "\\u6F");
    test("\0" + "6", "\\u006");
    test("Foo ¿", "\\u46\\u6F\\u6F\\u20\\uBF");
    test("bar ð", "\\u62\\u61\\u72\\u20\\uF0\\u9D\\u8C\\u86");
    test("baz â qux", "\\u62\\u61\\u7a\\u20\\ue2\\u98\\u83\\u20\\u71\\u75\\u78");
    test("\\\"ceba", "\\\"ceba");
    test("\\\\/", "\\\\/");

    test("Hello World", "\\u48\\u65\\u6C\\u6C\\u6F World");

    test("H4ello World", "\\u484\\u65\\u6C\\u6C\\u6F World");
    test("\\u0ello World", "\\u0\\u65\\u6C\\u6C\\u6F World");
    test("\\uello World", "\\u\\u65\\u6C\\u6C\\u6F World");
    test("\\ello World", "\\\\u65\\u6C\\u6C\\u6F World");

    test("Hell\\u0", "\\u48\\u65\\u6C\\u6C\\u0");
    test("Hell\\u", "\\u48\\u65\\u6C\\u6C\\u");
    test("Hell\\", "\\u48\\u65\\u6C\\u6C\\");

    test("He\\u6lo", "\\u48\\u65\\u6\\u6C\\u6F");
    test("He\\ulo", "\\u48\\u65\\u\\u6C\\u6F");
    test("He\\u0lo", "\\u48\\u65\\u0\\u6C\\u6F");
    test("He\\ulo", "\\u48\\u65\\u\\u6C\\u6F");
    test("He\\lo", "\\u48\\u65\\\\u6C\\u6F");
  }

  @Test
  public void testUtf16be() throws IOException {
    test("o", "\\u006F");
    test("\0" + "6", "\\u00006");
    test("Foo ¿", "\\u0046\\u006F\\u006F\\u0020\\u00BF");
    test("bar ð", "\\u0062\\u0061\\u0072\\u0020\\u00F0\\u009D\\u008C\\u0086");
    test("baz â qux", "\\u0062\\u0061\\u007a\\u0020\\u00e2\\u0098\\u0083\\u0020\\u0071\\u0075\\u0078");
    test("\\\"ceba", "\\\"ceba");
    test("\\\\/", "\\\\/");

    test("hello © ȼ", "\\u0068\\u0065\\u006c\\u006c\\u006f\\u0020\\u00a9\\u0020\\u023c"); // utf-16be

    test("Hello World", "\\u0048\\u0065\\u006C\\u006C\\u006F World");

    test("H4ello World", "\\u484\\u0065\\u006C\\u006C\\u006F World");
    test("\\u0ello World", "\\u0\\u0065\\u006C\\u006C\\u006F World");
    test("\\uello World", "\\u\\u0065\\u006C\\u006C\\u006F World");
    test("\\ello World", "\\\\u65\\u006C\\u006C\\u006F World");

    test("Hell\\u0", "\\u0048\\u0065\\u006C\\u006C\\u0");
    test("Hell\\u", "\\u0048\\u0065\\u006C\\u006C\\u");
    test("Hell\\", "\\u0048\\u0065\\u006C\\u006C\\");

    test("He\\u6lo", "\\u0048\\u0065\\u6\\u006C\\u006F");
    test("He\\ulo", "\\u0048\\u0065\\u\\u006C\\u006F");
    test("He\\u0lo", "\\u0048\\u0065\\u0\\u006C\\u006F");
    test("He\\ulo", "\\u0048\\u0065\\u\\u006C\\u006F");
    test("He\\lo", "\\u0048\\u0065\\\\u6C\\u006F");
  }

  @Test
  public void testUtf16le() throws IOException {
    test("o", "\\ufffe\\u6F00");
    test("\0" + "6", "\\ufffe\\u00006");
    test("Foo ¿", "\\ufffe\\u4600\\u6F00\\u6F00\\u2000\\uBF00");
    test("bar ð", "\\ufffe\\u6200\\u6100\\u7200\\u2000\\uF000\\u9D00\\u8C00\\u8600");
    test("baz â qux", "\\ufffe\\u6200\\u6100\\u7a00\\u2000\\ue200\\u9800\\u8300\\u2000\\u7100\\u7500\\u7800");
    test("\\\"ceba", "\\ufffe\\\"ceba");
    test("\\\\/", "\\ufffe\\\\/");

    test("hello © ȼ", "\\ufffe\\u6800\\u6500\\u6c00\\u6c00\\u6f00\\u2000\\ua900\\u2000\\u3c02"); // utf-16le

    test("Hello World", "\\ufffe\\u4800\\u6500\\u6C00\\u6C00\\u6F00 World");

    test("H4ello World", "\\ufffe\\u484\\u6500\\u6C00\\u6C00\\u6F00 World");
    test("\\u0ello World", "\\ufffe\\u0\\u6500\\u6C00\\u6C00\\u6F00 World");
    test("\\uello World", "\\ufffe\\u\\u6500\\u6C00\\u6C00\\u6F00 World");
    test("\\ello World", "\\ufffe\\\\u65\\u6C00\\u6C00\\u6F00 World");

    test("Hell\\u0", "\\ufffe\\u4800\\u6500\\u6C00\\u6C00\\u0");
    test("Hell\\u", "\\ufffe\\u4800\\u6500\\u6C00\\u6C00\\u");
    test("Hell\\", "\\ufffe\\u4800\\u6500\\u6C00\\u6C00\\");

    test("He\\u6lo", "\\ufffe\\u4800\\u6500\\u6\\u6C00\\u6F00");
    test("He\\ulo", "\\ufffe\\u4800\\u6500\\u\\u6C00\\u6F00");
    test("He\\u0lo", "\\ufffe\\u4800\\u6500\\u0\\u6C00\\u6F00");
    test("He\\ulo", "\\ufffe\\u4800\\u6500\\u\\u6C00\\u6F00");
    test("He\\lo", "\\ufffe\\u4800\\u6500\\\\u6C\\u6F00");
  }

  @Test
  public void testUtf32be() throws IOException {
    test("hello © ȼ", "\\u00000068\\u00000065\\u0000006c\\u0000006c\\u0000006f\\u00000020\\u000000a9\\u00000020\\u0000023c");
  }

  @Test
  public void testUtf32le() throws IOException {
    test("hello © ȼ", "\\ufffe0000\\u68000000\\u65000000\\u6c000000\\u6c000000\\u6f000000\\u20000000\\ua9000000\\u20000000\\u3c020000");
  }

  @Test
  public void testUtf16mix() throws IOException {
    test("hello © ȼhello © ȼ", "\\ufffe\\ufffe0000\\u68000000\\u65\\u6c00\\u6c\\u6f000000\\u2000\\ua900\\u20000000\\u3c02\\ufeff\\u0000feff\\u00000068\\u0065\\u006c\\u6c\\u00006f\\u00000020\\ua9\\u000020\\u0000023c"); // utf-16le & utf-16be
  }
}