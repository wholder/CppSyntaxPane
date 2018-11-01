/*
 * Copyright 2008 Ayman Al-Sairafi ayman.alsairafi@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License
 *       at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cppsyntaxpane;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.text.Segment;
import javax.swing.text.TabExpander;
import javax.swing.text.Utilities;

/**
 * This class represents the Style for a TokenType.  This class is responsible
 * for actually drawing a Token on the View.
 *
 * @author Ayman Al-Sairafi
 */
final class SyntaxStyle {

  private Color color;
  private int fontStyle;

  SyntaxStyle (Color color, int fontStyle) {
    super();
    this.color = color;
    this.fontStyle = fontStyle;
  }

  SyntaxStyle (String str) {
    String[] parts = str.split("\\s*,\\s*");
    if (parts.length != 2) {
      throw new IllegalArgumentException("style not correct format: " + str);
    }
    this.color = new Color(Integer.decode(parts[0]));
    this.fontStyle = Integer.decode(parts[1]);
  }

  private int getFontStyle () {
    return fontStyle;
  }

  private Color getColor () {
    return color;
  }

  /**
   * Draw text.  This can directly call the Utilities.drawTabbedText.
   * Sub-classes can override this method to provide any other decorations.
   *
   * @param segment     - the source of the text
   * @param x           - the X origin >= 0
   * @param y           - the Y origin >= 0
   * @param graphics    - the graphics context
   * @param e           - how to expand the tabs. If this value is null, tabs will be
   *                    expanded as a space character.
   * @param startOffset - starting offset of the text in the document >= 0
   * @return x
   */
  int drawText (Segment segment, int x, int y, Graphics graphics, TabExpander e, int startOffset) {
    graphics.setFont(graphics.getFont());
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int a = fontMetrics.getAscent();
    int h = a + fontMetrics.getDescent();
    int w = Utilities.getTabbedTextWidth(segment, fontMetrics, 0, e, startOffset);
    int rX = x - 1;
    int rY = y - a;
    int rW = w + 2;
    if ((getFontStyle() & 0x10) != 0) {
      graphics.setColor(Color.decode("#EEEEEE"));
      graphics.fillRect(rX, rY, rW, h);
    }
    graphics.setColor(getColor());
    x = Utilities.drawTabbedText(segment, x, y, graphics, e, startOffset);
    if ((getFontStyle() & 0x8) != 0) {
      graphics.setColor(Color.RED);
      graphics.drawRect(rX, rY, rW, h);
    }
    return x;
  }
}
