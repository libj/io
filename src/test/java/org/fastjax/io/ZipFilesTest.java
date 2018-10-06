/* Copyright (c) 2018 FastJAX
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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.fastjax.util.ClassLoaders;
import org.junit.Test;

public class ZipFilesTest {
  private static final File extractDir = new File("target/generated-test-resources/extract");

  static {
    extractDir.mkdirs();
  }

  private static URL findJar(final String name) {
    for (final URL url : ClassLoaders.getClassPath()) {
      if (url.toString().contains("/" + name + "-") && url.toString().endsWith(".jar"))
        return url;
    }

    throw new NoSuchElementException(name);
  }

  @Test
  public void testExtractAll() throws IOException, URISyntaxException, ZipException {
    final URL url = findJar("junit");
    final File destDir = new File(extractDir, "all");
    ZipFiles.extract(new ZipFile(new File(url.toURI())), destDir);
    assertEquals(4, destDir.list().length);
  }

  @Test
  public void testExtractSome() throws IOException, URISyntaxException, ZipException {
    final URL url = findJar("junit");
    final File destDir = new File(extractDir, "meta");
    ZipFiles.extract(new ZipFile(new File(url.toURI())), destDir, new Predicate<ZipEntry>() {
      @Override
      public boolean test(final ZipEntry t) {
        return t.getName().startsWith("META-INF/");
      }
    });

    assertEquals(1, destDir.list().length);
    assertTrue(new File(destDir, "META-INF/MANIFEST.MF").exists());
  }
}