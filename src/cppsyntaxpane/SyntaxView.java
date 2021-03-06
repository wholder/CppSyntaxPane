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
import java.util.Iterator;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.ViewFactory;

public class SyntaxView extends PlainView {

  private SyntaxStyle                 DEFAULT_STYLE = SyntaxStyles.getInstance().getStyle(TokenType.DEFAULT);
  private static final SyntaxStyles   styles = SyntaxStyles.getInstance();
  private static RenderingHints       sysHints;

  static {
    sysHints = null;
    try {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      @SuppressWarnings("unchecked")
      Map<RenderingHints.Key, ?> map = (Map<RenderingHints.Key, ?>) toolkit.getDesktopProperty("awt.font.desktophints");
      sysHints = new RenderingHints(map);
    } catch (Throwable th) {
      th.printStackTrace();
    }
  }

  /**
   * Construct a new view using the given configuration and prefix given
   */
  SyntaxView (Element element) {
    super(element);
  }

  @Override
  protected int drawUnselectedText (Graphics graphics, int x, int y, int p0, int p1) {
    setRenderingHits((Graphics2D) graphics);
    Font saveFont = graphics.getFont();
    Color saveColor = graphics.getColor();
    SyntaxDocument doc = (SyntaxDocument) getDocument();
    Segment segment = getLineBuffer();
    try {
      // Colour the parts
      Iterator<Token> it = doc.getTokens(p0, p1);
      int start = p0;
      while (it.hasNext()) {
        Token tok = it.next();
        // if there is a gap between the next token start and where we
        // should be starting (spaces not returned in tokens), then draw
        // it in the default type
        if (start < tok.start) {
          doc.getText(start, tok.start - start, segment);
          x = DEFAULT_STYLE.drawText(segment, x, y, graphics, this, start);
        }
        // t and s are the actual start and length of what we should
        // put on the screen.  assume these are the whole token....
        int l = tok.length;
        int s = tok.start;
        // ... unless the token starts before p0:
        if (s < p0) {
          // token is before what is requested. adgust the length and s
          l -= (p0 - s);
          s = p0;
        }
        // if token end (s + l is still the token end pos) is greater
        // than p1, then just put up to p1
        if (s + l > p1) {
          l = p1 - s;
        }
        doc.getText(s, l, segment);
        x = styles.drawText(segment, x, y, graphics, this, tok);
        start = tok.end();
      }
      // now for any remaining text not tokenized:
      if (start < p1) {
        doc.getText(start, p1 - start, segment);
        x = DEFAULT_STYLE.drawText(segment, x, y, graphics, this, start);
      }
    } catch (BadLocationException ex) {
      ex.printStackTrace();
    } finally {
      graphics.setFont(saveFont);
      graphics.setColor(saveColor);
    }
    return x;
  }

  @Override
  protected int drawSelectedText (Graphics graphics, int x, int y, int p0, int p1) {
    return drawUnselectedText(graphics, x, y, p0, p1);
  }

  /**
   * Sets the Rendering Hints o nthe Graphics.  This is used so that
   * any painters can set the Rendering Hits to match the view.
   */
  public static void setRenderingHits (Graphics2D g2d) {
    g2d.addRenderingHints(sysHints);
  }

  @Override
  protected void updateDamage (javax.swing.event.DocumentEvent changes, Shape a, ViewFactory f) {
    super.updateDamage(changes, a, f);
    java.awt.Component host = getContainer();
    host.repaint();
  }
}
