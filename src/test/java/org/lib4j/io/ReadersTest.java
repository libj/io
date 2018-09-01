package org.lib4j.io;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

public class ReadersTest {
  @Test
  public void testReadBytes() throws IOException {
    final Reader in = new StringReader("abc");
    assertEquals("abc", Readers.readFully(in));
  }
}