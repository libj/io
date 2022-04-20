/* Copyright (c) 2019 LibJ
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

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Utility functions for operations pertaining to {@link Charset}.
 */
public final class Charsets {
  /**
   * Look up a {@link Charset} by the specified name. This method is the equivalent of {@link Charset#forName(String)
   * Charset.forName(String)}, but returns {@code null} in case of {@link IllegalCharsetNameException} or
   * {@link UnsupportedCharsetException}.
   *
   * @param charsetName The name of the requested charset; may be either a canonical name or an alias
   * @return A charset object for the named charset, or {@code null} if the specified name is null or in case of
   *         {@link IllegalCharsetNameException} or {@link UnsupportedCharsetException}.
   */
  public static Charset lookup(final String charsetName) {
    if (charsetName == null)
      return null;

    try {
      return Charset.forName(charsetName);
    }
    catch (final IllegalCharsetNameException | UnsupportedCharsetException e) {
      return null;
    }
  }

  private Charsets() {
  }
}