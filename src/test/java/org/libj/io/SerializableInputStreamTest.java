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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.junit.Test;

public class SerializableInputStreamTest extends SerializableStreamTest<InputStream> {
  @Test
  public void test() throws Exception {
    test(SerializableInputStreamFileStore.class, SerializableInputStreamMemoryStore.class);
  }

  @Override
  InputStream serialize(final String inputString, final int serializationBufferSize, final Class<? extends SerializableStreamStore<InputStream>> tempStore) {
    return new SerializableInputStream(new ByteArrayInputStream(inputString.getBytes()), serializationBufferSize, tempStore);
  }

  @Override
  String deserialize(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
    try (
      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      final SerializableInputStream in = (SerializableInputStream)ois.readObject();
    ) {
      final byte[] buf = new byte[2048];
      for (int read = 0; (read = in.read(buf)) > 0; baos.write(buf, 0, read)); // [X]
      return new String(baos.toByteArray());
    }
  }
}