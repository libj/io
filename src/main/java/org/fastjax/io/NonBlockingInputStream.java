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

package org.fastjax.io;

import java.io.IOException;
import java.io.InputStream;

public class NonBlockingInputStream extends InputStream {
  private final InputStream in;
  private final byte[] buffer;
  private final int tempBufferSize;
  private volatile int writeAhead;
  private volatile int writeIndex = 0;
  private volatile int readIndex = 0;
  private volatile IOException ioException;
  private boolean eof = false;
  private int lost = 0;

  public NonBlockingInputStream(final InputStream in, final int bufferSize) {
    if (bufferSize == 0)
      throw new IllegalArgumentException("bufferSize cannot be 0");

    this.in = in;
    this.buffer = new byte[bufferSize];
    this.tempBufferSize = (int)Math.round(Math.log(bufferSize) / Math.log(2));
    this.writeAhead = bufferSize;
    new ReaderThread().start();
  }

  @Override
  public int read() throws IOException {
    if (ioException != null)
      throw ioException;

    if (eof)
      return -1;

    if (writeAhead == buffer.length)
      return 0;

    final int value;
    synchronized (buffer) {
      value = buffer[readIndex];
      if (++readIndex == buffer.length)
        readIndex = 0;

      if (++writeAhead == buffer.length)
        writeAhead = buffer.length;
    }

    return value;
  }

  public int getLostBytesCount() {
    return lost;
  }

  private final class ReaderThread extends Thread {
    public ReaderThread() {
      setName(NonBlockingInputStream.this.getClass().getSimpleName() + "$" + getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()));
      setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    public void run() {
      int length = 0;
      final byte[] bytes = new byte[tempBufferSize];
      try {
        while ((length = in.read(bytes)) != -1) {
          writeAhead -= length;

          if (buffer.length <= writeIndex + length) {
            System.arraycopy(bytes, 0, buffer, writeIndex, buffer.length - writeIndex);
            System.arraycopy(bytes, buffer.length - writeIndex, buffer, 0, writeIndex = bytes.length - buffer.length + writeIndex);
          }
          else {
            System.arraycopy(bytes, 0, buffer, writeIndex, length);
            writeIndex += length;
          }

          if (writeAhead <= 0) {
            lost += -1 * writeAhead;
            writeAhead = 0;
            readIndex = writeIndex;
          }
        }

        eof = true;
      }
      catch (final IOException e) {
        ioException = e;
      }
    }
  }
}