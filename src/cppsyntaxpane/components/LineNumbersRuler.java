/*
 * Copyright 2008 Ayman Al-Sairafi ayman.alsairafi@gmail.com
 * Copyright 2013-2014 Hanns Holger Rutz.
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
package cppsyntaxpane.components;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import cppsyntaxpane.SyntaxDocument;
import cppsyntaxpane.SyntaxView;
import cppsyntaxpane.actions.ActionUtils;
import cppsyntaxpane.actions.gui.GotoLineDialog;

/**
 * This class will display line numbers for a related text component. The text
 * component must use the same line height for each line.
 * <p>
 * This class was designed to be used as a component added to the row header
 * of a JScrollPane.
 * <p>
 * Original code from http://tips4java.wordpress.com/2009/05/23/text-component-line-number/
 *
 * @author Rob Camick
 * <p>
 * Revised for cppsyntaxpane
 * @author Ayman Al-Sairafi, Hanns Holger Rutz
 */
@SuppressWarnings("unused")
public class LineNumbersRuler extends JPanel implements CaretListener, DocumentListener, PropertyChangeListener, SyntaxComponent {
  private static final int    DEFAULT_R_MARGIN = 7;
  private static final int    DEFAULT_L_MARGIN = 5;
  private static final int    MINIMUM_DISPLAY_DIGITS = 2;
  private static final Color  foreColor = new Color(0x333300);
  private static final Color  backColor = new Color(0xEEEEFF);
  private static final Color  lineColor = new Color(0xCCCCEE);
  private Status status;
  private final static int    MAX_HEIGHT = Integer.MAX_VALUE - 1000000;
  //  Text component this TextTextLineNumber component is in sync with
  private JEditorPane         editor;
  //  Keep history information to reduce the number of times the component needs to be repainted
  private int                 lastDigits;
  private int                 lastHeight;
  private int                 lastLine;
  private MouseListener       mouseListener;
  // The formatting to use for displaying numbers.  Use in String.format(numbersFormat, line)
  private String numbersFormat = "%3d";

  /**
   * Returns the JScrollPane that contains this EditorPane, or null if no
   * JScrollPane is the parent of this editor
   */
  private JScrollPane getScrollPane (JTextComponent editorPane) {
    Container parent = editorPane.getParent();
    while (parent != null) {
      if (parent instanceof JScrollPane) {
        return (JScrollPane) parent;
      }
      parent = parent.getParent();
    }
    return null;
  }

  @Override
  public void install (JEditorPane editor) {
    this.editor = editor;
    setForeground(foreColor);
    setBackground(backColor);
    setBorder(BorderFactory.createEmptyBorder(0, DEFAULT_L_MARGIN, 0, DEFAULT_R_MARGIN));
    setFont(editor.getFont());
    Insets ein = editor.getInsets();
    if (ein.top != 0 || ein.bottom != 0) {
      Insets curr = getInsets();
      setBorder(BorderFactory.createEmptyBorder(ein.top, curr.left, ein.bottom, curr.right));
    }
    editor.getDocument().addDocumentListener(this);
    editor.addCaretListener(this);
    editor.addPropertyChangeListener(this);
    JScrollPane sp = getScrollPane(editor);
    if (sp != null) {
      sp.setRowHeaderView(this);
    }
    mouseListener = new MouseAdapter() {
      @Override
      public void mouseClicked (MouseEvent e) {
        GotoLineDialog.showForEditor(editor);
      }
    };
    addMouseListener(mouseListener);
    setPreferredWidth(false);    // required for toggle-lines to correctly repaint
    status = Status.INSTALLING;
  }

  @Override
  public void deinstall (JEditorPane editor) {
    removeMouseListener(mouseListener);
    status = Status.DEINSTALLING;
    editor.getDocument().removeDocumentListener(this);
    editor.removeCaretListener(this);
    editor.removePropertyChangeListener(this);
    JScrollPane sp = getScrollPane(editor);
    if (sp != null) {
      sp.setRowHeaderView(null);
    }
  }

  /**
   * Calculate the width needed to display the maximum line number
   */
  private void setPreferredWidth (boolean force) {
    int lines = ActionUtils.getLineCount(editor);
    int digits = Math.max(String.valueOf(lines).length(), MINIMUM_DISPLAY_DIGITS);
    //  Update sizes when number of digits in the line number changes
    if (force || lastDigits != digits) {
      lastDigits = digits;
      numbersFormat = "%" + digits + "d";
      FontMetrics fontMetrics = getFontMetrics(getFont());
      int width = fontMetrics.charWidth('0') * digits;
      Insets insets = getInsets();
      int preferredWidth = insets.left + insets.right + width;
      Dimension d = getPreferredSize();
      d.setSize(preferredWidth, MAX_HEIGHT);
      setPreferredSize(d);
      setSize(d);
    }
  }

  /**
   * Draw the line numbers
   */
  @Override
  public void paintComponent (Graphics g) {
    super.paintComponent(g);
    FontMetrics fontMetrics = getFontMetrics(getFont());
    Insets insets = getInsets();
    int currentLine;
    currentLine = ActionUtils.getLineNumber(editor, editor.getCaretPosition());
    int lh = fontMetrics.getHeight();
    int maxLines = ActionUtils.getLineCount(editor);
    SyntaxView.setRenderingHits((Graphics2D) g);
    Rectangle clip = g.getClip().getBounds();
    int topLine = (int) (clip.getY() / lh);
    int bottomLine = Math.min(maxLines, (int) (clip.getHeight() + lh - 1) / lh + topLine + 1);
    for (int line = topLine; line < bottomLine; line++) {
      String lineNumber = String.format(numbersFormat, line + 1);
      int y = line * lh + insets.top;
      int yt = y + fontMetrics.getAscent();
      if (line == currentLine) {
        g.setColor(lineColor);
        g.fillRect(0, y /* - lh + fontMetrics.getDescent() - 1 */, getWidth(), lh);
        g.setColor(getForeground());
        g.drawString(lineNumber, insets.left, yt);
      } else {
        g.drawString(lineNumber, insets.left, yt);
      }
    }
  }

  //
//  Implement CaretListener interface
//
  @Override
  public void caretUpdate (CaretEvent e) {
    //  Get the line the caret is positioned on
    int caretPosition = editor.getCaretPosition();
    Element root = editor.getDocument().getDefaultRootElement();
    int currentLine = root.getElementIndex(caretPosition);
    //  Need to repaint so the correct line number can be highlighted
    if (lastLine != currentLine) {
      repaint();
      lastLine = currentLine;
    }
  }

  //
//  Implement DocumentListener interface
//
  @Override
  public void changedUpdate (DocumentEvent e) {
    documentChanged();
  }

  @Override
  public void insertUpdate (DocumentEvent e) {
    documentChanged();
  }

  @Override
  public void removeUpdate (DocumentEvent e) {
    documentChanged();
  }

  /*
   *  A document change may affect the number of displayed lines of text.
   *  Therefore the lines numbers will also change.
   */
  private void documentChanged () {
    //  Preferred size of the component has not been updated at the time
    //  the DocumentEvent is fired
    SwingUtilities.invokeLater(() -> {
      int preferredHeight = editor.getPreferredSize().height;
      //  Document change has caused a change in the number of lines.
      //  Repaint to reflect the new line numbers
      if (lastHeight != preferredHeight) {
        setPreferredWidth(false);
        repaint();
        lastHeight = preferredHeight;
      }
    });
  }

  /**
   * Implement PropertyChangeListener interface
   */
  @Override
  public void propertyChange (PropertyChangeEvent evt) {
    String prop = evt.getPropertyName();
    if (prop.equals("document")) {
      if (evt.getOldValue() instanceof SyntaxDocument) {
        SyntaxDocument syntaxDocument = (SyntaxDocument) evt.getOldValue();
        syntaxDocument.removeDocumentListener(this);
      }
      if (evt.getNewValue() instanceof SyntaxDocument && status.equals(Status.INSTALLING)) {
        SyntaxDocument syntaxDocument = (SyntaxDocument) evt.getNewValue();
        syntaxDocument.addDocumentListener(this);
        setPreferredWidth(false);
        repaint();
      }
    } else if (prop.equals("font") && evt.getNewValue() instanceof Font) {
      setFont((Font) evt.getNewValue());
      setPreferredWidth(true);
    }
  }
}
