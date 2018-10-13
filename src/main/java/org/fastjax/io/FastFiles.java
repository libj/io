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

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.fastjax.util.Paths;

/**
 * Utility functions for operations pertaining to {@link File} and {@link Path}.
 */
public final class FastFiles {
  private static File CWD;
  private static File TEMP_DIR;

  /**
   * Returns the current working directory.
   *
   * @return The current working directory.
   */
  public static File getCwd() {
    return CWD == null ? CWD = new File("").getAbsoluteFile() : CWD;
  }

  /**
   * Returns the default path the JVM uses to store temporary files.
   *
   * @return The default path the JVM uses to store temporary files.
   */
  public static File getTempDir() {
    return TEMP_DIR == null ? TEMP_DIR = new File(System.getProperty("java.io.tmpdir")) : TEMP_DIR;
  }

  private static final DirectoryStream.Filter<Path> anyStreamFilter = new DirectoryStream.Filter<Path>() {
    @Override
    public boolean accept(final Path entry) {
      return true;
    }
  };

  private static void delete(final Path path, final boolean onExit) throws IOException {
    if (onExit)
      Files.newOutputStream(path, StandardOpenOption.DELETE_ON_CLOSE);
    else
      Files.delete(path);
  }

  private static void deleteAll(final Path path, final DirectoryStream.Filter<Path> filter, final boolean onExit) throws IOException {
    if (Files.isDirectory(path)) {
      try (final DirectoryStream<Path> stream = Files.newDirectoryStream(path, filter)) {
        for (final Path entry : stream) {
          if (Files.isDirectory(entry))
            deleteAll(entry, filter, onExit);
          else
            delete(entry, onExit);
        }
      }
    }

    delete(path, onExit);
  }

  /**
   * Register a path to be recursively deleted when the JVM exits. When executed
   * on exit, only the paths that pass the {@code filter} will be deleted.
   *
   * @param path The path to delete recursively.
   * @param filter The filter of paths to delete, or null to match all
   *          paths.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code path} is null.
   */
  public static void deleteAllOnExit(final Path path, final DirectoryStream.Filter<Path> filter) throws IOException {
    deleteAll(path, filter != null ? filter : anyStreamFilter, true);
  }

  /**
   * Delete a path recursively. Only the paths that pass the {@code filter} will
   * be deleted.
   *
   * @param path The path to delete recursively.
   * @param filter The filter of paths to delete, or null to match all
   *          paths.
   * @return {@code true} if and only if the file or directory was successfully
   *         deleted; {@code false} otherwise.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code path} is null.
   */
  public static boolean deleteAll(final Path path, final DirectoryStream.Filter<Path> filter) throws IOException {
    deleteAll(path, filter != null ? filter : anyStreamFilter, false);
    return !Files.exists(path);
  }

  /**
   * Register a path to be recursively deleted when the JVM exits.
   *
   * @param path The path to delete recursively.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code path} is null.
   */
  public static void deleteAllOnExit(final Path path) throws IOException {
    deleteAll(path, anyStreamFilter, true);
  }

  /**
   * Delete a path recursively.
   *
   * @param path The path to delete recursively.
   * @return {@code true} if and only if the file or directory was successfully
   *         deleted; {@code false} otherwise.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code path} is null.
   */
  public static boolean deleteAll(final Path path) throws IOException {
    deleteAll(path, anyStreamFilter, false);
    return !Files.exists(path);
  }

  /**
   * Returns a {@code File} having a path that is common to the argument
   * {@code files}.
   *
   * @param files The files.
   * @return A {@code File} having a path that is common to the argument
   *         {@code files}.
   * @throws IllegalArgumentException If {@code files.length == 0}.
   */
  public static File commonality(final File ... files) {
    if (files.length == 0)
      throw new IllegalArgumentException("files.length == 0");

    if (files.length > 1) {
      final String[] canons = new String[files.length];
      canons[0] = Paths.canonicalize(files[0].getPath());
      int length = canons[0].length();
      for (int i = 1; i < files.length; ++i) {
        canons[i] = Paths.canonicalize(files[i].getPath());
        if (canons[i].length() < length)
          length = canons[i].length();
      }

      for (int i = 0; i < length; ++i) {
        final char ch = canons[0].charAt(i);
        for (int j = 1; j < files.length; ++j)
          if (ch != canons[j].charAt(i))
            return new File(canons[0].substring(0, i));
      }
    }

    return files[0];
  }

  /**
   * Returns the "short name" of {@code file}. The "short name" is the name of a
   * file not including the dot and extension, if present.
   *
   * @param file The {@code File}.
   * @return The "short name" of {@code file}.
   * @throws NullPointerException If {@code file} is null.
   */
  public static String getShortName(final File file) {
    final String name = file.getName();
    final int index = name.indexOf('.');
    return index == -1 ? name : name.substring(0, index);
  }

  private FastFiles() {
  }
}