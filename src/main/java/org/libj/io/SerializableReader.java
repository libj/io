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

import static org.libj.lang.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * An {@link Reader} that makes another {@link Reader} {@linkplain Serializable serializable}.
 */
public class SerializableReader extends Reader implements Serializable {
  private static final int DEFAULT_SERIALIZATION_CHUNK_SIZE = 2048;
  private static final Class<SerializableReaderMemoryStore> DEFAULT_SERIALIZABLE_STREAM_STORE = SerializableReaderMemoryStore.class;

  private transient Reader stream;
  private transient int serializationBufferSize;
  private transient Class<? extends SerializableStreamStore<Reader>> serializableStreamStoreClass;
  private transient SerializableStreamStore<Reader> serializableStreamStore;

  /**
   * Creates a new {@link SerializableReader} with the provided {@link Reader}, which uses
   * {@value #DEFAULT_SERIALIZATION_CHUNK_SIZE} as the chunk size, and {@link SerializableReaderMemoryStore} as the
   * {@linkplain SerializableStreamStore serializable stream store}.
   *
   * @param stream The stream to serialize.
   * @throws NullPointerException If {@code stream} is null.
   */
  public SerializableReader(final Reader stream) {
    this.stream = Objects.requireNonNull(stream, "stream is null");
    this.serializationBufferSize = DEFAULT_SERIALIZATION_CHUNK_SIZE;
    this.serializableStreamStoreClass = DEFAULT_SERIALIZABLE_STREAM_STORE;
  }

  /**
   * Creates a new {@link SerializableReader} with the provided {@link Reader}, {@code serializationBufferSize} as the chunk size,
   * and {@link SerializableReaderMemoryStore} as the {@linkplain SerializableStreamStore serializable stream store}.
   *
   * @param stream The stream to serialize.
   * @param serializationBufferSize The buffer size to be used when serializing.
   * @throws NullPointerException If {@code stream} is null.
   * @throws IllegalArgumentException If {@code bufferSize} is not a positive value.
   */
  public SerializableReader(final Reader stream, final int serializationBufferSize) {
    this.stream = Objects.requireNonNull(stream, "stream is null");
    this.serializationBufferSize = assertPositive(serializationBufferSize, "bufferSize must be positive");
    this.serializableStreamStoreClass = DEFAULT_SERIALIZABLE_STREAM_STORE;
  }

  /**
   * Creates a new {@link SerializableReader} with the provided {@link Reader}, {@code serializationBufferSize} as the chunk size,
   * and {@code serializableStreamStore} as the {@linkplain SerializableStreamStore serializable stream store}.
   *
   * @param stream The stream to serialize.
   * @param serializationBufferSize The buffer size to be used when serializing.
   * @param serializableStreamStore The {@link SerializableStreamStore} class providing the serialization store.
   * @throws NullPointerException If {@code stream} or {@code tempStore} is null.
   * @throws IllegalArgumentException If {@code bufferSize} is not a positive value.
   */
  public SerializableReader(final Reader stream, final int serializationBufferSize, final Class<? extends SerializableStreamStore<Reader>> serializableStreamStore) {
    this.stream = Objects.requireNonNull(stream, "stream is null");
    this.serializationBufferSize = assertPositive(serializationBufferSize, "bufferSize must be positive");
    this.serializableStreamStoreClass = Objects.requireNonNull(serializableStreamStore, "tempStore is null");
  }

  private void writeObject(final ObjectOutputStream out) throws IOException {
    out.writeInt(serializationBufferSize);
    out.writeObject(serializableStreamStoreClass);
    try (final InputStream in = new ReaderInputStream(stream, Charset.defaultCharset())) {
      final byte[] buf = new byte[serializationBufferSize];
      for (int r; (r = in.read(buf)) > 0; out.writeInt(r), out.write(buf, 0, r)); // [X]
      out.writeInt(0);
    }
  }

  @SuppressWarnings("unchecked")
  private void readObject(final ObjectInputStream in) throws IOException {
    serializationBufferSize = in.readInt();
    try {
      serializableStreamStoreClass = (Class<? extends SerializableStreamStore<Reader>>)in.readObject();
      serializableStreamStore = serializableStreamStoreClass.getDeclaredConstructor().newInstance();
    }
    catch (final Exception e) {
      throw new IllegalStateException(e);
    }

    final OutputStream out = new WriterOutputStream((Writer)serializableStreamStore, Charset.defaultCharset(), serializationBufferSize, true);
    int r0 = in.readInt();
    if (r0 > 0) {
      int r1;
      byte[] buf = new byte[r0];
      do {
        buf = buf.length >= r0 ? buf : new byte[r0];
        r1 = in.read(buf, 0, r0);
        out.write(buf, 0, r1);
        if ((r0 -= r1) == 0)
          r0 = in.readInt();
      }
      while (r0 > 0);
    }

    stream = serializableStreamStore.consume();
  }

  @Override
  public int read() throws IOException {
    return stream.read();
  }

  @Override
  public int read(final char[] cbuf) throws IOException {
    return stream.read(cbuf);
  }

  @Override
  public int read(final char[] cbuf, final int off, final int len) throws IOException {
    return stream.read(cbuf, off, len);
  }

  @Override
  public boolean markSupported() {
    return stream.markSupported();
  }

  @Override
  public void mark(final int readlimit) throws IOException {
    stream.mark(readlimit);
  }

  @Override
  public long skip(final long n) throws IOException {
    return stream.skip(n);
  }

  @Override
  public void reset() throws IOException {
    stream.reset();
  }

  @Override
  public void close() throws IOException {
    if (stream != null)
      stream.close();

    if (serializableStreamStore != null)
      serializableStreamStore.close();
  }
}