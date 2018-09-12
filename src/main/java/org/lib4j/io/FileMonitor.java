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

package org.lib4j.io;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

// FIXME: Need to change to to use atomic API
public final class FileMonitor {
  public static interface FileEventListener {
    public void onModify(final File file);
    public void onDelete(final File file);
  }

  private final File file;
  private final int interval;
  private volatile long lastModifiedTime = 0;
  private volatile boolean deleted = false;
  private volatile boolean kill = false;
  private final Set<FileEventListener> listeners = new HashSet<>();

  public FileMonitor(final File file, final int interval) {
    if (file == null)
      throw new IllegalArgumentException("file == null");

    this.file = file;
    this.interval = interval;
  }

  public void addListener(final FileEventListener listener) {
    listeners.add(listener);
  }

  public void start() {
    if (!(deleted = !file.exists()))
      lastModifiedTime = file.lastModified();

    final FileMonitorRunner monitorThread = new FileMonitorRunner();
    Thread mainThread = null;
    final Set<Thread> threads = Thread.getAllStackTraces().keySet();
    for (final Thread thread : threads) {
      if ("main".equals(thread.getName())) {
        mainThread = thread;
        break;
      }
    }

    new FileMonitorKiller(mainThread, monitorThread).start();
    monitorThread.start();
  }

  private final class FileMonitorRunner extends Thread {
    @Override
    public void run() {
      try {
        while (true) {
          if (kill)
            break;

          long modifiedTime;
          if (!deleted && (deleted = !file.exists())) {
            new FileDeletedNotifier().start();
          }
          else if (lastModifiedTime < (modifiedTime = file.lastModified())) {
            lastModifiedTime = modifiedTime;
            deleted = false;
            new FileModifiedNotifier().start();
          }

          synchronized (this) {
            wait(interval);
          }

          if (kill)
            break;
        }
      }
      catch (final InterruptedException e) {
      }
    }
  }

  private final class FileMonitorKiller extends Thread {
    private final Thread criticalThread;
    private final Thread dependentThread;

    public FileMonitorKiller(final Thread criticalThread, final Thread dependentThread) {
      if (criticalThread == null)
        throw new IllegalArgumentException("criticalThread == null");

      if (dependentThread == null)
        throw new IllegalArgumentException("dependentThread == null");

      this.criticalThread = criticalThread;
      this.dependentThread = dependentThread;
    }

    @Override
    public void run() {
      try {
        criticalThread.join();
      }
      catch (final InterruptedException e) {
        throw new RuntimeException(e);
      }

      kill = true;
      synchronized (dependentThread) {
        dependentThread.notify();
      }
    }
  }

  private final class FileModifiedNotifier extends Thread {
    @Override
    public void run() {
      for (final FileEventListener listener : listeners)
        listener.onModify(file);
    }
  }

  private final class FileDeletedNotifier extends Thread {
    @Override
    public void run() {
      for (final FileEventListener listener : listeners)
        listener.onDelete(file);
    }
  }
}