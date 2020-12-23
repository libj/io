/* Copyright (c) 2006 LibJ
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

import static org.libj.util.function.Throwing.*;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.libj.util.StringPaths;

/**
 * Utility functions for operations pertaining to {@link File} and {@link Path}.
 */
public final class FileUtil {
  private static File CWD;
  private static File TEMP_DIR;
  private static volatile ConcurrentMap<Path,List<DirectoryStream.Filter<? super Path>>> deleteOnExit;
  private static final AtomicBoolean deleteOnExitMutex = new AtomicBoolean();

  private static void deleteOnExit(final Path path, final DirectoryStream.Filter<? super Path> filter) {
    if (!deleteOnExitMutex.get()) {
      synchronized (deleteOnExitMutex) {
        if (!deleteOnExitMutex.get()) {
          deleteOnExit = new ConcurrentHashMap<>();
          Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
              for (final Map.Entry<Path,List<DirectoryStream.Filter<? super Path>>> entry : deleteOnExit.entrySet()) {
                final Path path = entry.getKey();
                for (final DirectoryStream.Filter<? super Path> filter : entry.getValue()) {
                  try {
                    deleteAll0(path, filter);
                  }
                  catch (final IOException e) {
                    e.printStackTrace();
                  }
                }
              }
            }
          });

          deleteOnExitMutex.set(true);
        }
      }
    }

    List<DirectoryStream.Filter<? super Path>> filters = deleteOnExit.get(path);
    if (filters == null)
      deleteOnExit.put(path, filters = new ArrayList<>(1));

    filters.add(filter != null ? filter : anyStreamFilter);
  }

  /**
   * Returns a new {@link File} with the given {@code pathname} if the
   * {@code pathname} exists, otherwise {@code null}.
   *
   * @param pathname The pathname.
   * @return A new {@link File} with the given {@code pathname} if the
   *         {@code pathname} exists, otherwise {@code null}.
   */
  public static File existsOrNull(final String pathname) {
    final File file = new File(pathname);
    return file.exists() ? file : null;
  }

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

  private static final DirectoryStream.Filter<Path> anyStreamFilter = p -> true;

  private static void deleteAll0(final Path path, final DirectoryStream.Filter<? super Path> filter) throws IOException {
    if (Files.isDirectory(path)) {
      try (final DirectoryStream<Path> stream = Files.newDirectoryStream(path, filter)) {
        for (final Path entry : stream) {
          if (Files.isDirectory(entry))
            deleteAll0(entry, filter);
          else
            Files.delete(entry);
        }
      }
    }

    Files.delete(path);
  }

  /**
   * Register a path to be recursively deleted when the JVM exits. When executed
   * on exit, only the paths that pass the {@code filter} will be deleted.
   * <p>
   * <i><b>Note:</b> Filtering will be performed at the time the JVM exists (not
   * at the time when this method is called).</i>
   *
   * @param path The path to delete recursively.
   * @param filter The filter of paths to delete, or {@code null} to match all
   *          paths.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code path} is null.
   */
  public static void deleteAllOnExit(final Path path, final DirectoryStream.Filter<? super Path> filter) throws IOException {
    final File file = path.toFile();
    if (file.isDirectory())
      deleteOnExit(path, filter);
    else if (filter != null && filter.accept(path))
      file.deleteOnExit();
  }

  /**
   * Register a path to be recursively deleted when the JVM exits. When executed
   * on exit, only the paths that pass the {@code filter} will be deleted.
   * <p>
   * <i><b>Note:</b> Filtering will be performed at the time the JVM exists (not
   * at the time when this method is called).</i>
   *
   * @param file The file to delete recursively.
   * @param filter The filter of paths to delete, or {@code null} to match all
   *          paths.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code file} is null.
   */
  public static void deleteAllOnExit(final File file, final DirectoryStream.Filter<? super Path> filter) throws IOException {
    final Path path = file.toPath();
    if (file.isDirectory())
      deleteOnExit(path, filter);
    else if (filter != null && filter.accept(path))
      file.deleteOnExit();
  }

  /**
   * Register a path to be recursively deleted when the JVM exits.
   *
   * @param path The path to delete recursively.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If the {@code path} is null.
   */
  public static void deleteAllOnExit(final Path path) throws IOException {
    deleteAllOnExit(path, anyStreamFilter);
  }

  /**
   * Register a path to be recursively deleted when the JVM exits.
   *
   * @param file The file to delete recursively.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If the {@code file} is null.
   */
  public static void deleteAllOnExit(final File file) throws IOException {
    deleteAllOnExit(file, anyStreamFilter);
  }

  /**
   * Delete a path recursively. Only the paths that pass the {@code filter} will
   * be deleted.
   *
   * @param path The path to delete recursively.
   * @param filter The filter of paths to delete, or {@code null} to match all
   *          paths.
   * @return {@code true} if and only if the file or directory was successfully
   *         deleted; {@code false} otherwise.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If the {@code path} is null.
   */
  public static boolean deleteAll(final Path path, final DirectoryStream.Filter<? super Path> filter) throws IOException {
    deleteAll0(path, filter != null ? filter : anyStreamFilter);
    return !Files.exists(path);
  }

  /**
   * Delete a path recursively.
   *
   * @param path The path to delete recursively.
   * @return {@code true} if and only if the file or directory was successfully
   *         deleted; {@code false} otherwise.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If the {@code path} is null.
   */
  public static boolean deleteAll(final Path path) throws IOException {
    deleteAll0(path, anyStreamFilter);
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
   *           replaced because the {@link StandardCopyOption#REPLACE_EXISTING}
   *           option is not specified <i>(optional specific exception)</i>.
   * @throws DirectoryNotEmptyException The
   *           {@link StandardCopyOption#REPLACE_EXISTING} option is specified
   *           but the target path could not be deleted <i>(optional specific
   *           exception)</i>.
   * @throws SecurityException In the case of the default provider, and a
   *           security manager is installed, the
   *           {@link SecurityManager#checkRead(String) checkRead} method is
   *           invoked to check read access to the source file, the
   *           {@link SecurityManager#checkWrite(String) checkWrite} is invoked
   *           to check write access to the target file. If a symbolic link is
   *           copied the security manager is invoked to check
   *           {@link LinkPermission}{@code ("symbolic")}.
   * @throws NullPointerException If {@code source}, {@code target}, or
   *           {@code options} is null.
   * @see Files#copy(Path,Path,CopyOption...)
   */
  public static Path copyAll(final Path source, final Path target, final CopyOption ... options) throws IOException {
    if (Files.isRegularFile(source))
      return Files.copy(source, target, options);

    if (Files.exists(target) && options != null)
      for (int i = 0; i < options.length; ++i)
        if (options[i] == StandardCopyOption.REPLACE_EXISTING && !deleteAll(target))
          throw new DirectoryNotEmptyException(target.toString());

    Files.walk(source).forEach(rethrow(path -> {
      final Path resolved = target.resolve(source.relativize(path));
      if (Files.isRegularFile(path))
        Files.copy(path, resolved, options);
      else if (!Files.exists(resolved))
        Files.createDirectory(resolved);
    }));

    return target;
  }

  /**
   * Returns a {@link File} having a path that is common to the argument
   * {@code files}.
   *
   * @param files The files.
   * @return A {@link File} having a path that is common to the argument
   *         {@code files}.
   * @throws IllegalArgumentException If {@code files.length == 0}.
   * @throws NullPointerException If {@code files} is null.
   */
  public static File commonality(final File ... files) {
    if (files.length == 0)
      throw new IllegalArgumentException("files.length == 0");

    if (files.length > 1) {
      final String[] canons = new String[files.length];
      canons[0] = StringPaths.canonicalize(files[0].getPath());
      int length = canons[0].length();
      for (int i = 1; i < files.length; ++i) {
        canons[i] = StringPaths.canonicalize(files[i].getPath());
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
   * file not including the last dot and extension, if present.
   *
   * @param file The {@link File}.
   * @return The "short name" of {@code file}.
   * @throws NullPointerException If {@code file} is null.
   */
  public static String getShortName(final File file) {
    final String name = file.getName();
    final int index = name.lastIndexOf('.');
    return index == -1 ? name : name.substring(0, index);
  }

  private FileUtil() {
  }
}