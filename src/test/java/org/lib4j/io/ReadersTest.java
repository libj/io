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

package org.lib4j.io;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;
import org.lib4j.util.Strings;

public class ReadersTest {
  @Test
  public void testReadBytes() throws IOException {
    final String string = Strings.getRandomAlphaNumericString(64);
    assertEquals(string, Readers.readFully(new StringReader(string)));
    for (int i = 1; i < 100; ++i)
      assertEquals(string, Readers.readFully(new StringReader(string), i));
  }
}