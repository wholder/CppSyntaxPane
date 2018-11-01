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

import java.awt.*;
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
  private int   fontStyle;

  SyntaxStyle (Color color, int fontStyle) {
    super();
    this.color = color;
    this.fontStyle = fontStyle;
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
   *                      expanded as a space character.
   * @param startOffset - starting offset of the text in the document >= 0
   * @return x
   */
  int drawText (Segment segment, int x, int y, Graphics graphics, TabExpander e, int startOffset) {
    graphics.setFont(graphics.getFont().deriveFont(fontStyle));
    graphics.setColor(color);
    return Utilities.drawTabbedText(segment, x, y, graphics, e, startOffset);
  }
}
