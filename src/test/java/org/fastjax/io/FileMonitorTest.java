/* Copyright (c) 2009 FastJAX
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

import java.io.File;

import org.fastjax.io.FileMonitor;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileMonitorTest implements FileMonitor.FileEventListener {
  private static final Logger logger = LoggerFactory.getLogger(FileMonitorTest.class);

  @Override
  public void onModify(final File file) {
    logger.info(file.getName() + " modified.");
  }

  @Override
  public void onDelete(final File file) {
    logger.info(file.getName() + " deleted.");
  }

  @Test
  @Ignore
  public void testFileMonitor() throws InterruptedException {
    final File testFile = new File("/tmp/file-monitor-test");
    testFile.deleteOnExit();

    final FileMonitor monitor = new FileMonitor(testFile, 5000);
    monitor.addListener(this);
    monitor.start();
    Thread.sleep(1000);
    logger.info("main done!");
  }
}