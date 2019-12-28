/* Copyright (c) 2019 LibJ
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

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.junit.Test;

public class ReaderInputStreamTest {
  private static final String TEST_STRING = "\u00e0 peine arriv\u00e9s nous entr\u00e2mes dans sa chambre";
  private static final Random random = new Random();
  private static final String LARGE_TEST_STRING;

  static {
    final StringBuilder builder = new StringBuilder();
    for (int i = 0; i < 100; ++i)
      builder.append(TEST_STRING);

    LARGE_TEST_STRING = builder.toString();
  }

  private static void testWithSingleByteRead(final String testString, final Charset charset) throws IOException {
    final byte[] bytes = testString.getBytes(charset);
    try (final ReaderInputStream in = new ReaderInputStream(new StringReader(testString), charset)) {
      for (int i = 0; i < bytes.length; ++i) {
        final int ch = in.read();
        assertTrue(ch >= 0);
        assertTrue(ch <= 255);
        assertEquals(bytes[i], (byte)ch);
      }

      assertEquals(-1, in.read());
    }
  }

  private static void testWithBufferedRead(final String testString, final Charset charset) throws IOException {
    final byte[] expected = testString.getBytes(charset);
    try (final ReaderInputStream in = new ReaderInputStream(new StringReader(testString), charset)) {
      final byte[] buffer = new byte[128];
      for (int offset = 0;;) {
        int bufferOffset = random.nextInt(64);
        final int bufferLength = random.nextInt(64);
        int read = in.read(buffer, bufferOffset, bufferLength);
        if (read == -1) {
          assertEquals(offset, expected.length);
          break;
        }

        assertTrue(read <= bufferLength);
        for (; read > 0; ++offset, ++bufferOffset, --read) {
          assertTrue(offset < expected.length);
          assertEquals(expected[offset], buffer[bufferOffset]);
        }
      }
    }
  }

  @Test
  public void testUTF8WithSingleByteRead() throws IOException {
    testWithSingleByteRead(TEST_STRING, StandardCharsets.UTF_8);
  }

  @Test
  public void testLargeUTF8WithSingleByteRead() throws IOException {
    testWithSingleByteRead(LARGE_TEST_STRING, StandardCharsets.UTF_8);
  }

  @Test
  public void testUTF8WithBufferedRead() throws IOException {
    testWithBufferedRead(TEST_STRING, StandardCharsets.UTF_8);
  }

  @Test
  public void testLargeUTF8WithBufferedRead() throws IOException {
    testWithBufferedRead(LARGE_TEST_STRING, StandardCharsets.UTF_8);
  }

  @Test
  public void testUTF16WithSingleByteRead() throws IOException {
    testWithSingleByteRead(TEST_STRING, StandardCharsets.UTF_16);
  }

  @Test
  public void testCharsetMismatchInfiniteLoop() throws IOException {
    final char[] inputChars = {(char)0xE0, (char)0xB2, (char)0xA0};
    // final Charset charset = Charset.forName("UTF-8"); // works
    final Charset charset = StandardCharsets.US_ASCII; // infinite loop
    try (final ReaderInputStream in = new ReaderInputStream(new CharArrayReader(inputChars), charset)) {
      while (in.read() != -1);
    }
  }
}