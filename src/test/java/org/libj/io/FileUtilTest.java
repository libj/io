/* Copyright (c) 2008 LibJ
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

import java.io.File;

import org.junit.Test;

public class FileUtilTest {
  @Test
  public void testGetCwd() {
    final File cwd = FileUtil.getCwd();
    assertNotNull(cwd);
    assertSame(cwd, FileUtil.getCwd());
  }

  @Test
  public void testGetTempDir() {
    final File tmp = FileUtil.getTempDir();
    assertNotNull(tmp);
    assertSame(tmp, FileUtil.getTempDir());
  }

  @Test
  public void testCommonality() {
    try {
      FileUtil.commonality();
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals(new File("foo"), FileUtil.commonality(new File("foo")));
    assertEquals(new File("/"), FileUtil.commonality(new File("/foo"), new File("/bar")));
    assertEquals(new File(""), FileUtil.commonality(new File("foo"), new File("bar")));

    final File[] files = {
      new File("/var/lib/foo/bar/hi.txt"),
      new File("/var/lib/foo/bar/a/b/c.txt"),
      new File("/var/lib/foo/welcome.txt"),
      new File("/var/lib/foo/some/dir.txt"),
      new File("/var/lib/foo/bar/a/b/hello.txt")
    };

    assertEquals(new File("/var/lib/foo"), FileUtil.commonality(files));
  }

  @Test
  public void testGetShortName() {
    assertEquals("", FileUtil.getShortName(new File("")));
    assertEquals("share", FileUtil.getShortName(new File("file:///usr/share/../share.txt")));
    assertEquals("lib", FileUtil.getShortName(new File("file:///usr/share/../share/../lib")));
    assertEquals("var", FileUtil.getShortName(new File("/usr/share/../share/../lib/../../var.old")));
    assertEquals("var", FileUtil.getShortName(new File("/usr/share/../share/../lib/../../var/")));
    assertEquals("resolv", FileUtil.getShortName(new File("/etc/resolv.conf")));
    assertEquals("name", FileUtil.getShortName(new File("name")));
  }

  @Test
  public void testExistsOrNull() {
    assertNull(FileUtil.existsOrNull("foo/bar"));
    assertNotNull(FileUtil.existsOrNull("target"));
  }
}