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

import static org.fastjax.util.function.Throwing.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkPermission;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
   * Copy a source path to a target path recursively with the {@code options}
   * parameter specifying how the copy is performed. If the source path is a
   * directory, this method traverses all child paths, creates child
   * directories, and applies the {@code options} parameter to the
   * {@link Files#copy(Path,Path,CopyOption...)} operation applied to each child
   * file. If the source path is a file, this method delegate to
   * {@link Files#copy(Path,Path,CopyOption...)}.
   *
   * @param source The source path to copy from.
   * @param target The target path to copy to.
   * @param options Options specifying how the copy should be done.
   * @return The path to the target file.
   * @throws IOException If an I/O error has occurred.
   * @throws UnsupportedOperationException If the array contains a copy option
   *           that is not supported.
   * @throws FileAlreadyExistsException If the target file exists but cannot be
   *           replaced because the {@code REPLACE_EXISTING} option is not
   *           specified <i>(optional specific exception)</i>.
   * @throws DirectoryNotEmptyException The {@code REPLACE_EXISTING} option is
   *           specified but the target path could not be deleted <i>(optional
   *           specific exception)</i>.
   * @throws SecurityException In the case of the default provider, and a
   *           security manager is installed, the
   *           {@link SecurityManager#checkRead(String) checkRead} method is
   *           invoked to check read access to the source file, the
   *           {@link SecurityManager#checkWrite(String) checkWrite} is invoked
   *           to check write access to the target file. If a symbolic link is
   *           copied the security manager is invoked to check
   *           {@link LinkPermission}{@code ("symbolic")}.
   * @throws NullPointerException If {@code source} or {@code target} is null.
   * @see Files#copy(Path,Path,CopyOption...)
   */
  public static Path copyAll(final Path source, final Path target, final CopyOption ... options) throws IOException {
    if (Files.isRegularFile(source))
      return Files.copy(source, target, options);

    if (Files.exists(target) && options != null)
      for (int i = 0; i < options.length; ++i)
        if (options[i] == StandardCopyOption.REPLACE_EXISTING && !deleteAll(target))
          throw new DirectoryNotEmptyException(target.toString());

    Files.walk(source).forEach(rethrow(s -> {
      final Path t = target.resolve(source.relativize(s));
      if (Files.isRegularFile(s))
        Files.copy(s, t, options);
      else if (!Files.exists(t))
        Files.createDirectory(t);
    }));

    return target;
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