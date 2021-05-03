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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Test;

public class CountingOutputStreamTest {
  private static final int numTests = 100;

  private static void assertEquals(final int expected, final OutputStream cbos) {
    Assert.assertEquals(expected, cbos instanceof CountingOutputStream ? ((CountingOutputStream)cbos).getCount() : ((CountingBufferedOutputStream)cbos).getCount());
  }

  private static void testWriteByte(final Supplier<OutputStream> supplier) throws IOException {
    for (int i = 0; i < numTests; ++i) {
      final int expectedCount = (int)(Math.random() * Short.MAX_VALUE);
      try (final OutputStream cbos = supplier.get()) {
        for (int j = 0; j < expectedCount; ++j)
          cbos.write((int)(Math.random() * Integer.MAX_VALUE));

        assertEquals(expectedCount, cbos);
      }
    }
  }

  @Test
  public void testWriteByte() throws IOException {
    testWriteByte(() -> new CountingOutputStream(new ByteArrayOutputStream()));
    testWriteByte(() -> new CountingBufferedOutputStream(new ByteArrayOutputStream()));
  }

  private static void testWriteBytes(final Supplier<OutputStream> supplier) throws IOException {
    for (int i = 0; i < numTests; ++i) {
      final int expectedCount = (int)(Math.random() * Short.MAX_VALUE);
      try (final OutputStream cbos = supplier.get()) {
        cbos.write(new byte[expectedCount]);
        assertEquals(expectedCount, cbos);
      }
    }
  }

  @Test
  public void testWriteBytes() throws IOException {
    testWriteBytes(() -> new CountingOutputStream(new ByteArrayOutputStream()));
    testWriteBytes(() -> new CountingBufferedOutputStream(new ByteArrayOutputStream()));
  }

  private static void testWriteBytesOffLen(final Supplier<OutputStream> supplier) throws IOException {
    for (int i = 0; i < numTests; ++i) {
      final int expectedCount = (int)(Math.random() * Short.MAX_VALUE);
      try (final OutputStream cbos = supplier.get()) {
        cbos.write(new byte[expectedCount], 0, expectedCount);
        assertEquals(expectedCount, cbos);
      }
    }
  }

  @Test
  public void testWriteBytesOffLen() throws IOException {
    testWriteBytesOffLen(() -> new CountingOutputStream(new ByteArrayOutputStream()));
    testWriteBytesOffLen(() -> new CountingBufferedOutputStream(new ByteArrayOutputStream()));
  }
}