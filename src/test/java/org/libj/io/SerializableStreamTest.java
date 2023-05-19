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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

abstract class SerializableStreamTest<T extends AutoCloseable> {
  abstract T serialize(final String inputString, final int serializationBufferSize, final Class<? extends SerializableStreamStore<T>> tempStore);
  abstract String deserialize(final ObjectInputStream ois) throws ClassNotFoundException, IOException;

  @SafeVarargs
  final void test(final Class<? extends SerializableStreamStore<T>> ... serializationTempStores) throws Exception {
    for (final Class<? extends SerializableStreamStore<T>> tempStore : serializationTempStores) // [A]
      for (int chunkSize = 1; chunkSize < Short.MAX_VALUE; chunkSize *= 2) // [N]
        test(chunkSize, tempStore);
  }

  private void test(final int serializationBufferSize, final Class<? extends SerializableStreamStore<T>> tempStore) throws Exception, ClassNotFoundException {
    final String inputString = createPayload();
    final byte[] bytes;
    try (
      final T sis = serialize(inputString, serializationBufferSize, tempStore);
      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      final ObjectOutputStream oos = new ObjectOutputStream(baos);
    ) {
      oos.writeObject(sis);
      bytes = baos.toByteArray();
    }

    final String outputString;
    try (final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
      outputString = deserialize(ois);
    }
    assertEquals(serializationBufferSize + " " + tempStore, inputString, outputString);
  }

  private static String createPayload() {
    final StringBuilder b = new StringBuilder();
    for (int i = 0; i < 500; i++) // [N]
      b.append(UUID.randomUUID().toString()).append('\n');

    return b.toString();
  }
}