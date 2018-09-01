package org.lib4j.io;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class StreamsTest {
  @Test
  public void testReadBytes() throws IOException {
    final InputStream in = new ByteArrayInputStream("abc".getBytes());
    assertArrayEquals(new byte[] {'a', 'b', 'c'}, Streams.readBytes(in));
  }
}