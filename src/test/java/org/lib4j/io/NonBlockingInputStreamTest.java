/* Copyright (c) 2006 FastJAX
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

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.Assert;
import org.junit.Test;
import org.lib4j.io.NonBlockingInputStream;

public class NonBlockingInputStreamTest {
  @Test
  @SuppressWarnings("resource")
  public void testInputStream() throws Exception {
    final PipedOutputStream out = new PipedOutputStream();
    final NonBlockingInputStream in = new NonBlockingInputStream(new PipedInputStream(out), 7);
    final int sleepTime = 100;

    // write 1 byte, and read it
    out.write('!');
    out.flush();
    Thread.sleep(sleepTime);
    Assert.assertEquals(in.read(), '!');

    // write 5 bytes, and read 5 bytes
    out.write("\"#$%&".getBytes());
    out.flush();
    Thread.sleep(sleepTime);
    Assert.assertEquals(in.read(), '"');
    Assert.assertEquals(in.read(), '#');
    Assert.assertEquals(in.read(), '$');
    Assert.assertEquals(in.read(), '%');
    Assert.assertEquals(in.read(), '&');

    // write 9 bytes, (first 2 will be lost) then read 1 byte
    out.write("'()*+,-./".getBytes());
    out.flush();
    Thread.sleep(sleepTime);
    Assert.assertEquals(in.read(), ')');
    Assert.assertEquals(in.getLostBytesCount(), 2);

    // write 2 bytes, and read 1, loosing 1 more byte
    out.write("01".getBytes());
    out.flush();
    Thread.sleep(sleepTime);
    Assert.assertEquals(in.read(), '+');
    Assert.assertEquals(in.getLostBytesCount(), 3);

    // write 2 bytes, and read 1, loosing 1 more byte
    out.write("23".getBytes());
    out.flush();
    Thread.sleep(sleepTime);
    Assert.assertEquals(in.read(), '-');
    Assert.assertEquals(in.getLostBytesCount(), 4);

    // write 3 bytes, and read 1, loosing 2 more bytes
    out.write("456".getBytes());
    out.flush();
    Thread.sleep(sleepTime);
    Assert.assertEquals(in.read(), '0');
    Assert.assertEquals(in.getLostBytesCount(), 6);

    // write 3 bytes, and read 7 bytes, loosing 2 bytes, and further ahead of the writing stream
    out.write("789".getBytes());
    out.flush();
    Thread.sleep(sleepTime);
    Assert.assertEquals(in.read(), '3');
    Assert.assertEquals(in.read(), '4');
    Assert.assertEquals(in.read(), '5');
    Assert.assertEquals(in.read(), '6');
    Assert.assertEquals(in.read(), '7');
    Assert.assertEquals(in.read(), '8');
    Assert.assertEquals(in.read(), '9');
    Assert.assertEquals(in.read(), 0);
    Assert.assertEquals(in.read(), 0);
    Assert.assertEquals(in.read(), 0);
    Assert.assertEquals(in.read(), 0);
    Assert.assertEquals(in.read(), 0);
    Assert.assertEquals(in.getLostBytesCount(), 8);

    // wirte 1 byte, try to read 2
    out.write(":".getBytes());
    out.flush();
    Thread.sleep(sleepTime);
    Assert.assertEquals(in.read(), ':');
    Assert.assertEquals(in.read(), 0);

    // write 13 bytes, and read 8
    out.write(";<=>?@ABCDEFG".getBytes());
    out.flush();
    Thread.sleep(sleepTime);
    Assert.assertEquals(in.read(), 'A');
    Assert.assertEquals(in.read(), 'B');
    Assert.assertEquals(in.read(), 'C');
    Assert.assertEquals(in.read(), 'D');
    Assert.assertEquals(in.read(), 'E');
    Assert.assertEquals(in.read(), 'F');
    Assert.assertEquals(in.read(), 'G');
    Assert.assertEquals(in.read(), 0);
    Assert.assertEquals(in.getLostBytesCount(), 14);

    // close the stream, and read -1 byte
    out.close();
    Thread.sleep(sleepTime);
    Assert.assertEquals(in.read(), -1);
    in.close();
  }
}