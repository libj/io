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
import java.io.OutputStream;

public class TeeOutputStream extends OutputStream {
  private final OutputStream out1;
  private final OutputStream out2;

  public TeeOutputStream(final OutputStream out1, final OutputStream out2) {
    this.out1 = out1;
    this.out2 = out2;
  }

  @Override
  public void write(final int b) throws IOException {
    out1.write(b);
    out2.write(b);
    if ((char)b == '\n') {
      out1.flush();
      out2.flush();
    }
  }
}