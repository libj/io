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

package org.lib4j.io.scanner;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.safris.commons.util.HashTree;

public final class InputStreamScanner extends Thread {
  private final InputStream in;
  private List<HashTree.Node<ScannerHandler>> currentNodes;

  public InputStreamScanner(final InputStream in, final HashTree<ScannerHandler> handlers) {
    super(InputStreamScanner.class.getSimpleName());
    this.in = in;
    currentNodes = handlers != null ? handlers.getChildren() : null;
  }

  private boolean onMatch(final String line, final List<HashTree.Node<ScannerHandler>> nodes) throws IOException {
    boolean match = false;
    for (final HashTree.Node<ScannerHandler> node : nodes) {
      if (node.getValue() != null) {
        if (line.matches(node.getValue().getMatch())) {
          match = true;
          node.getValue().match(line);
          if (node.hasChildren())
            currentNodes = node.getChildren();
        }
      }
      else {
        for (final HashTree.Node<ScannerHandler> child : node.getChildren())
          onMatch(line, child.getChildren());
      }
    }

    return match;
  }

  @Override
  public void run() {
    String line = "";
    try {
      char ch = 0;
      while ((ch = (char)in.read()) != -1) {
        if (ch != '\n') {
          if (ch != ' ' || line.length() != 0)
            line += ch;
        }
        else {
          line = "";
        }

        if (currentNodes == null)
          continue;

        if (onMatch(line, currentNodes))
          line = "";
      }
    }
    catch (final Exception e) {
      if ("Pipe broken".equals(e.getMessage()))
        return;

      throw new RuntimeException(e);
    }
    finally {
      try {
        notifyAll();
      }
      catch (final IllegalMonitorStateException e) {
      }
    }
  }
}