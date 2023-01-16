/* Copyright (c) 2023 LibJ
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

import java.io.IOException;

import org.junit.Test;

public class UnsynchronizedStringReaderTest {
  private static final String testString = "This is a test string";

  @Test
  public void testConstructor() {
    assertTrue("Used in tests", true);
  }

  @Test
  public void testClose() {
    try (final UnsynchronizedStringReader sr = new UnsynchronizedStringReader(testString)) {
      sr.close();
      final char[] buf = new char[10];
      sr.read(buf, 0, 2);
      fail("Close failed");
    }
    catch (final IOException e) {
    }
  }

  @Test
  public void testMarkI() throws IOException {
    try (final UnsynchronizedStringReader sr = new UnsynchronizedStringReader(testString)) {
      sr.skip(5);
      sr.mark(0);
      sr.skip(5);
      sr.reset();
      final char[] buf = new char[10];
      sr.read(buf, 0, 2);
      assertTrue("Failed to return to mark", new String(buf, 0, 2).equals(testString.substring(5, 7)));
    }
  }

  @Test
  public void testMarkSupported() {
    try (final UnsynchronizedStringReader sr = new UnsynchronizedStringReader(testString)) {
      assertTrue("markSupported returned false", sr.markSupported());
    }
  }

  @Test
  public void testRead() throws IOException {
    try (final UnsynchronizedStringReader sr = new UnsynchronizedStringReader(testString)) {
      final int r = sr.read();
      assertEquals("Failed to read char", 'T', r);
    }

    try (final UnsynchronizedStringReader sr = new UnsynchronizedStringReader(new String(new char[] {'\u8765'}))) {
      assertTrue("Wrong double byte char", sr.read() == '\u8765');
    }
  }

  @Test
  public void testRead$CII() throws IOException {
    // Test for method int java.io.StringReader.read(char [], int, int)
    try (final UnsynchronizedStringReader sr = new UnsynchronizedStringReader(testString)) {
      final char[] buf = new char[testString.length()];
      final int r = sr.read(buf, 0, testString.length());
      assertTrue("Failed to read chars", r == testString.length());
      assertTrue("Read chars incorrectly", new String(buf, 0, r).equals(testString));
    }
  }

  @Test
  public void testReady() {
    try (final UnsynchronizedStringReader sr = new UnsynchronizedStringReader(testString)) {
      assertTrue("Steam not ready", sr.ready());
      sr.close();
      sr.ready();
      fail("Expected IOException not thrown in read()");
    }
    catch (final IOException e) {
    }
  }

  @Test
  public void testReset() throws IOException {
    try (final UnsynchronizedStringReader sr = new UnsynchronizedStringReader(testString)) {
      sr.skip(5);
      sr.mark(0);
      sr.skip(5);
      sr.reset();
      final char[] buf = new char[10];
      sr.read(buf, 0, 2);
      assertTrue("Failed to reset properly", new String(buf, 0, 2).equals(testString.substring(5, 7)));
    }
  }

  @Test
  public void testSkipJ() throws IOException {
    try (final UnsynchronizedStringReader sr = new UnsynchronizedStringReader(testString)) {
      sr.skip(5);
      final char[] buf = new char[10];
      sr.read(buf, 0, 2);
      assertTrue("Failed to skip properly", new String(buf, 0, 2).equals(testString.substring(5, 7)));
    }
  }
}