/* Copyright (c) 2008 lib4j
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

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class FilesTest {
  @Test
  public void testGetShortName() {
    Assert.assertEquals("", Files.getShortName(new File("")));
    Assert.assertEquals("share", Files.getShortName(new File("file:///usr/share/../share.txt")));
    Assert.assertEquals("lib", Files.getShortName(new File("file:///usr/share/../share/../lib")));
    Assert.assertEquals("var", Files.getShortName(new File("/usr/share/../share/../lib/../../var.old")));
    Assert.assertEquals("var", Files.getShortName(new File("/usr/share/../share/../lib/../../var/")));
    Assert.assertEquals("resolv", Files.getShortName(new File("/etc/resolv.conf")));
    Assert.assertEquals("name", Files.getShortName(new File("name")));
  }
}