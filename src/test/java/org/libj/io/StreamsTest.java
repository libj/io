/* Copyright (c) 2018 LibJ
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.libj.util.ArrayUtil;

public class StreamsTest {
  @Test
  public void testWriteReadChar() throws IOException {
    for (int i = 0; i < 100; ++i) { // [N]
      final boolean isBigEndian = Math.random() < .5;
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final char value = (char)new Random().nextInt();
      Streams.writeChar(out, value, isBigEndian);
      assertEquals(value, Streams.readChar(new ByteArrayInputStream(out.toByteArray()), isBigEndian));
    }
  }

  @Test
  public void testWriteReadShort() throws IOException {
    for (int i = 0; i < 100; ++i) { // [N]
      final boolean isBigEndian = Math.random() < .5;
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final short value = (short)new Random().nextInt();
      Streams.writeShort(out, value, isBigEndian);
      assertEquals(value, Streams.readShort(new ByteArrayInputStream(out.toByteArray()), isBigEndian));
    }
  }

  @Test
  public void testWriteReadInt() throws IOException {
    for (int i = 0; i < 100; ++i) { // [N]
      final boolean isBigEndian = Math.random() < .5;
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final int value = new Random().nextInt();
      Streams.writeInt(out, value, isBigEndian);
      assertEquals(value, Streams.readInt(new ByteArrayInputStream(out.toByteArray()), isBigEndian));
    }
  }

  @Test
  public void testWriteReadLong() throws IOException {
    for (int i = 0; i < 100; ++i) { // [N]
      final boolean isBigEndian = Math.random() < .5;
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final long value = new Random().nextLong();
      Streams.writeLong(out, value, isBigEndian);
      assertEquals(value, Streams.readLong(new ByteArrayInputStream(out.toByteArray()), isBigEndian));
    }
  }

  private static byte[] createRandomBytes(final int length) {
    final byte[] bytes = new byte[length];
    final Random random = new Random();
    for (int i = 0, i$ = bytes.length; i < i$; ++i) // [A]
      bytes[i] = (byte)random.nextInt();

    return bytes;
  }

  private static void assertBytesEqual(final byte[] expected, final byte[] actual, final int len) {
    if (len == 0)
      assertSame(ArrayUtil.EMPTY_ARRAY_BYTE, actual);
    else
      for (int i = 0; i < Math.min(expected.length, len); ++i) // [N]
        assertEquals(expected[i], actual[i]);
  }

  public void testReadBytes(final int len) throws IOException {
    final byte[] bytes = createRandomBytes(len);
    try (final InputStream in = new ByteArrayInputStream(bytes)) {
//      assertBytesEqual(bytes, Streams.readBytes(in), len);
    }

    try (final InputStream in = new ByteArrayInputStream(bytes)) {
//      assertBytesEqual(bytes, Streams.readBytes(in, len), len);
    }

    if (len > 0) {
      try (final InputStream in = new ByteArrayInputStream(bytes)) {
//        assertBytesEqual(bytes, Streams.readBytes(in, 0), 0);
      }

      try (final InputStream in = new ByteArrayInputStream(bytes)) {
//        assertBytesEqual(bytes, Streams.readBytes(in, len - 1), len - 1);
      }

      try (final InputStream in = new ByteArrayInputStream(bytes)) {
        assertBytesEqual(bytes, Streams.readBytes(in, len / 2), len / 2);
      }
    }

    try (final InputStream in = new ByteArrayInputStream(bytes)) {
      assertBytesEqual(bytes, Streams.readBytes(in, len + 1), len + 1);
    }

    try (final InputStream in = new ByteArrayInputStream(bytes)) {
      assertBytesEqual(bytes, Streams.readBytes(in, len * 2), len * 2);
    }
  }

  @Test
  public void testReadBytesEmpty() throws IOException {
    testReadBytes(0);
  }

  @Test
  public void testReadBytesSmall() throws IOException {
    testReadBytes(Byte.MAX_VALUE);
  }

  @Test
  public void testReadBytesMedium() throws IOException {
    testReadBytes(Streams.DEFAULT_SOCKET_BUFFER_SIZE);
  }

  @Test
  public void testReadBytesLarge() throws IOException {
    testReadBytes(Short.MAX_VALUE * 2);
  }

  @Test
  public void testReadBytesHuge() throws IOException {
    testReadBytes(Short.MAX_VALUE * 16);
  }

  @Test
  public void testReadBytesMaxLength() throws IOException {
    for (int i = 1; i < 17; ++i) { // [N]
      final byte[] bytes = createRandomBytes(i * i * i * i * i);
      for (int j = 1; j < Short.MAX_VALUE * Short.MAX_VALUE; j *= 2) { // [N]
        try (final InputStream in = new ByteArrayInputStream(bytes)) {
          assertArrayEquals(bytes.length < j ? bytes : Arrays.copyOf(bytes, j), Streams.readBytes(in, j));
        }
      }
    }
  }

  @Test
  public void testPipe() throws IOException {
    final byte[] bytes = createRandomBytes(Streams.DEFAULT_SOCKET_BUFFER_SIZE);
    try (
      final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
    ) {
      Streams.pipe(in, out);
      assertArrayEquals(bytes, out.toByteArray());
    }
  }

  @Test
  public void testPipeAsync() throws InterruptedException, IOException {
    final byte[] bytes = createRandomBytes(Streams.DEFAULT_SOCKET_BUFFER_SIZE);
    try (
      final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
    ) {
      Streams.pipeAsync(in, out);
      Thread.sleep(100);
      assertArrayEquals(bytes, out.toByteArray());
    }
  }

  @Test
  public void testTee() throws IOException {
    final byte[] bytes = createRandomBytes(Streams.DEFAULT_SOCKET_BUFFER_SIZE);
    try (
      final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final InputStream tee = Streams.tee(in, out);
    ) {
      assertArrayEquals(bytes, out.toByteArray());

      final byte[] teeBytes = Streams.readBytes(tee);
      assertArrayEquals(bytes, teeBytes);
    }
  }

  @Test
  public void testTeeAsync() throws InterruptedException, IOException {
    final byte[] bytes = createRandomBytes(Streams.DEFAULT_SOCKET_BUFFER_SIZE);
    try (
      final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final InputStream tee = Streams.tee(in, out);
    ) {
      Thread.sleep(100);
      assertArrayEquals(bytes, out.toByteArray());

      final byte[] teeBytes = Streams.readBytes(tee);
      assertArrayEquals(bytes, teeBytes);
    }
  }

  @Test
  public void testMerge() throws IOException {
    final int len = Streams.DEFAULT_SOCKET_BUFFER_SIZE / 4;
    final byte[] bytes = createRandomBytes(len);
    try (
      final ByteArrayInputStream in1 = new ByteArrayInputStream(bytes);
      final ByteArrayInputStream in2 = new ByteArrayInputStream(bytes);
      final ByteArrayInputStream in3 = new ByteArrayInputStream(bytes);
      final ByteArrayInputStream in4 = new ByteArrayInputStream(bytes);
      final InputStream merged = Streams.merge(in1, in2, in3, in4);
    ) {
      final byte[] mergedBytes = Streams.readBytes(merged);
      assertEquals(len * 4, mergedBytes.length);
    }
  }

  private class LatchedByteArrayInputStream extends ByteArrayInputStream {
    private volatile CountDownLatch latch;

    private LatchedByteArrayInputStream(final byte[] buf, final CountDownLatch latch) {
      super(buf);
      this.latch = latch;
    }

    @Override
    public synchronized int read() {
      final int n = super.read();
      if (n == -1 && latch != null) {
        latch.countDown();
        latch = null;
      }

      return n;
    }

    @Override
    public synchronized int read(final byte[] b, final int off, final int len) {
      final int n = super.read(b, off, len);
      if (n == -1 && latch != null) {
        latch.countDown();
        latch = null;
      }

      return n;
    }
  }

  @Test
  public void testMergeAsync() throws InterruptedException, IOException {
    final int len = Streams.DEFAULT_SOCKET_BUFFER_SIZE / 2;
    final byte[] bytes = createRandomBytes(len);
    final CountDownLatch latch = new CountDownLatch(4);
    try (
      final ByteArrayInputStream in1 = new LatchedByteArrayInputStream(bytes, latch);
      final ByteArrayInputStream in2 = new LatchedByteArrayInputStream(bytes, latch);
      final ByteArrayInputStream in3 = new LatchedByteArrayInputStream(bytes, latch);
      final ByteArrayInputStream in4 = new LatchedByteArrayInputStream(bytes, latch);
    ) {
      final InputStream merged = Streams.mergeAsync(in1, in2, in3, in4);
      Executors.newSingleThreadExecutor().execute(() -> {
        try {
          Thread.sleep(500);
          final byte[] mergedBytes = Streams.readBytes(merged);
          assertEquals(len * 4, mergedBytes.length);
        }
        catch (final InterruptedException e) {
          throw new RuntimeException(e);
        }
        catch (final IOException e) {
          if (e.getMessage().equals("Write end dead"))
            System.err.println(e.getMessage());
          else
            throw new UncheckedIOException(e);
        }
      });
      latch.await();
    }
  }
}