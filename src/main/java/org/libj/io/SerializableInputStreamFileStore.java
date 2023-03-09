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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A {@link SerializableStreamStore} for {@link InputStream}s that stores deserialized data in temp files. The temp files are
 * deleted upon invocation of {@link #close()}, as well as via {@link File#deleteOnExit()} in all else cases.
 */
public class SerializableInputStreamFileStore extends FileOutputStream implements SerializableStreamStore<InputStream> {
  private final File file;

  SerializableInputStreamFileStore() throws IOException {
    this(File.createTempFile("serializable_stream_", "_store"));
  }

  private SerializableInputStreamFileStore(final File file) throws IOException {
    super(file);
    this.file = file;
    file.deleteOnExit();
  }

  @Override
  public InputStream consume() throws IOException {
    super.close();
    return new FileInputStream(file);
  }

  @Override
  public void close() throws IOException {
    file.delete();
  }
}