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
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.Segment;
import javax.swing.text.TabExpander;

/**
 * The Styles to use for each TokenType.  The defaults are created here, and
 * then the resource META-INF/services/syntaxstyles.properties is read and
 * merged.  You can also pass a properties instance and merge your prefered
 * styles into the default styles.
 * <p>
 * Text is drawn by forwarding the drawText request to the SyntaxStyle for the
 * that matches the given TokenType
 *
 * @author Ayman Al-Sairafi
 */
class SyntaxStyles {
  private static Map<TokenType, SyntaxStyle> styles = new HashMap<>();
  private static SyntaxStyles instance = new SyntaxStyles();
  private static SyntaxStyle  DEFAULT_STYLE = new SyntaxStyle(Color.BLACK, Font.PLAIN);

  private SyntaxStyles () {
    // These are the various Attributes for each TokenType.
    styles.put(TokenType.valueOf("KEYWORD2"), new SyntaxStyle(new Color(0x3333ee), Font.BOLD + Font.ITALIC));
    styles.put(TokenType.valueOf("STRING"),   new SyntaxStyle(new Color(0xcc6600), Font.PLAIN));
    styles.put(TokenType.valueOf("TYPE2"),    new SyntaxStyle(new Color(0x000000), Font.BOLD));
    styles.put(TokenType.valueOf("COMMENT"),  new SyntaxStyle(new Color(0x339933), Font.ITALIC));
    styles.put(TokenType.valueOf("KEYWORD"),  new SyntaxStyle(new Color(0x3333ee), Font.PLAIN));
    styles.put(TokenType.valueOf("NUMBER"),   new SyntaxStyle(new Color(0x999933), Font.BOLD));
    styles.put(TokenType.valueOf("OPERATOR"), new SyntaxStyle(new Color(0x000000), Font.PLAIN));
    styles.put(TokenType.valueOf("TYPE"),     new SyntaxStyle(new Color(0x000000), Font.ITALIC));
    styles.put(TokenType.valueOf("DEFAULT"),  new SyntaxStyle(new Color(0x000000), Font.PLAIN));
  }

  // Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD + Font.ITALIC
  /**
   * Returns the default singleton
   */
  static SyntaxStyles getInstance () {
    return instance;
  }

  /**
   * Returns the style for the given TokenType
   */
  SyntaxStyle getStyle (TokenType type) {
    if (styles != null && styles.containsKey(type)) {
      return styles.get(type);
    } else {
      return DEFAULT_STYLE;
    }
  }

  /**
   * Draws the given Token.  This will simply find the proper SyntaxStyle for
   * the TokenType and then asks the proper Style to draw the text of the
   * Token.
   */
  int drawText (Segment segment, int x, int y, Graphics graphics, TabExpander e, Token token) {
    SyntaxStyle s = getStyle(token.type);
    return s.drawText(segment, x, y, graphics, e, token.start);
  }
}
