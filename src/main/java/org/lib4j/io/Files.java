/* Copyright (c) 2006 lib4j
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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lib4j.util.Collections;
import org.lib4j.util.Paths;

public final class Files {
  private static File CWD;
  private static File TEMP_DIR;

  public static File getCwd() {
    return CWD == null ? CWD = new File("").getAbsoluteFile() : CWD;
  }

  public static File getTempDir() {
    return TEMP_DIR == null ? TEMP_DIR = new File(System.getProperty("java.io.tmpdir")) : TEMP_DIR;
  }

  private static final DirectoryStream.Filter<Path> anyFilter = new DirectoryStream.Filter<>() {
    @Override
    public boolean accept(final Path entry) {
      return true;
    }
  };

  private static final class DirectoryFileFilter implements FileFilter {
    private final FileFilter original;

    public DirectoryFileFilter(final FileFilter original) {
      this.original = original;
    }

    @Override
    public boolean accept(final File pathname) {
      return original.accept(pathname) || pathname.isDirectory();
    }
  }

  private static void delete(final Path path, final boolean onExit) throws IOException {
    if (onExit)
      java.nio.file.Files.newOutputStream(path, StandardOpenOption.DELETE_ON_CLOSE);
    else
      java.nio.file.Files.delete(path);
  }

  private static void deleteAll(final Path path, final DirectoryStream.Filter<Path> filter, final boolean onExit) throws IOException {
    if (java.nio.file.Files.isDirectory(path)) {
      try (final DirectoryStream<Path> stream = java.nio.file.Files.newDirectoryStream(path, filter)) {
        for (final Path entry : stream) {
          if (java.nio.file.Files.isDirectory(entry))
            deleteAll(entry, filter, onExit);
          else
            delete(entry, onExit);
        }
      }
    }

    delete(path, onExit);
  }

  public static void deleteAllOnExit(final Path path, final DirectoryStream.Filter<Path> filter) throws IOException {
    deleteAll(path, filter, true);
  }

  public static boolean deleteAll(final Path path, final DirectoryStream.Filter<Path> filter) throws IOException {
    deleteAll(path, filter, false);
    return !java.nio.file.Files.exists(path);
  }

  public static void deleteAllOnExit(final Path path) throws IOException {
    deleteAll(path, anyFilter, true);
  }

  public static boolean deleteAll(final Path path) throws IOException {
    deleteAll(path, anyFilter, false);
    return !java.nio.file.Files.exists(path);
  }

  public static List<File> listAll(final File directory) {
    if (!directory.isDirectory())
      return null;

    List<File> outer = Collections.asCollection(new ArrayList<File>(), directory.listFiles());
    final List<File> files = new ArrayList<>(outer);
    for (List<File> inner; outer.size() != 0;) {
      inner = new ArrayList<>();
      for (final File file : outer)
        if (file.isDirectory())
          inner.addAll(Arrays.asList(file.listFiles()));

      files.addAll(inner);
      outer = inner;
    }

    return files;
  }

  public static List<File> listAll(final File directory, final FileFilter fileFilter) {
    if (!directory.isDirectory())
      return null;

    final FileFilter directoryFilter = new DirectoryFileFilter(fileFilter);
    List<File> outer = Collections.asCollection(new ArrayList<File>(), directory.listFiles(directoryFilter));
    final List<File> files = new ArrayList<>(outer);
    for (List<File> inner; outer.size() != 0;) {
      inner = new ArrayList<>();
      for (final File file : outer)
        if (file.isDirectory())
          inner.addAll(Arrays.asList(file.listFiles(directoryFilter)));

      files.addAll(inner);
      outer = inner;
    }

    final List<File> result = new ArrayList<>();
    for (final File file : files)
      if (fileFilter.accept(file))
        result.add(file);

    return result;
  }

  /**
   * Copy a file or directory from <code>from</code> to <code>to</code>.
   *
   * @param from <code>File</code> to copy from.
   * @param to <code>File</code> to copy to.
   *
   * @exception IOException If there is an error handling either the from file, or the to file.
   */
  public static void copy(final File from, final File to) throws IOException {
    if (from.isFile()) {
      copyFile(from, to);
    }
    else if (from.isDirectory()) {
      if (to.isFile())
        throw new IllegalArgumentException("trying to copy a directory to a file");

      if (!to.exists() && !to.mkdirs())
        throw new IOException("Unable to create destination directory: " + to.getAbsolutePath());

      final List<File> files = Files.listAll(from.getAbsoluteFile());
      for (final File file : files) {
        final String relativePath = Paths.relativePath(from.getAbsolutePath(), file.getAbsolutePath());
        final File toFile = new File(to, relativePath);
        if (file.isFile())
          copyFile(file, toFile);
        else if (file.isDirectory())
          toFile.mkdir();
        else
          throw new IllegalArgumentException(file.getAbsolutePath() + " does not exist");
      }
    }
    else {
      throw new IllegalArgumentException("from does not exist");
    }
  }

  private static void copyFile(final File from, final File to) throws IOException {
    try (final FileInputStream in = new FileInputStream(from);
      final FileChannel sourceChannel = in.getChannel();
      final FileOutputStream out = new FileOutputStream(to);
      final FileChannel destinationChannel = out.getChannel();
    ) {
      sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
    }
  }

  public static String relativePath(final File dir, final File file) {
    // FIXME: Should this be getAbsolutePath() instead?
    return dir != null && file != null ? Paths.relativePath(dir.getPath(), file.getPath()) : null;
  }

  public static File commonality(final File[] files) throws IOException {
    if (files == null || files.length == 0)
      return null;

    if (files.length > 1) {
      int length = Integer.MAX_VALUE;
      for (final File file : files)
        if (file.getCanonicalPath().length() < length)
          length = file.getCanonicalPath().length();

      for (int i = 0; i < length; i++)
        for (int j = 1; j < files.length; j++)
          if (files[0].getCanonicalPath().charAt(i) != files[j].getCanonicalPath().charAt(i))
            return new File(files[0].getCanonicalPath().substring(0, i));
    }

    return files[0];
  }

  public static String getShortName(final File file) {
    final String name = file.getName();
    final int index = name.indexOf('.');
    return index == -1 ? name : name.substring(0, index);
  }

  private Files() {
  }
}