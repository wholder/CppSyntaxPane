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
package cppsyntaxpane.actions;

import java.awt.Point;
import javax.swing.JComboBox;
import javax.swing.MutableComboBoxModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import cppsyntaxpane.SyntaxDocument;

/**
 * Various utility methods to work on JEditorPane and its SyntaxDocument
 * for use by Actions
 *
 * @author Ayman Al-Sairafi
 */
public class ActionUtils {
  // This is used internally to avoid NPE if we have no Strings
  private final static String[] EMPTY_STRING_ARRAY = new String[0];
  // This is used to quickly create Strings of at most 16 spaces (using substring)
  private final static String SPACES = "                ";

  private ActionUtils () {
  }

  /**
   * Return the lines that span the selection (split as an array of Strings)
   * if there is no selection then current line is returned.
   * <p>
   * Note that the strings returned will not contain the terminating line feeds
   * If the document is empty, then an empty string array is returned.  So
   * you can always iterate over the returned array without a null check
   * <p>
   * The text component will then have the full lines set as selection
   *
   * @return String[] of lines spanning selection / or line containing dot
   */
  static String[] getSelectedLines (JTextComponent target) {
    String[] lines;
    try {
      PlainDocument pDoc = (PlainDocument) target.getDocument();
      int start = pDoc.getParagraphElement(target.getSelectionStart()).getStartOffset();
      int end;
      if (target.getSelectionStart() == target.getSelectionEnd()) {
        end = pDoc.getParagraphElement(target.getSelectionEnd()).getEndOffset();
      } else {
        // if more than one line is selected, we need to subtract one from the end
        // so that we do not select the line with the caret and no selection in it
        end = pDoc.getParagraphElement(target.getSelectionEnd() - 1).getEndOffset();
      }
      target.select(start, end);
      lines = pDoc.getText(start, end - start).split("\n");
      target.select(start, end);
    } catch (BadLocationException ex) {
      ex.printStackTrace();
      lines = EMPTY_STRING_ARRAY;
    }
    return lines;
  }

  /**
   * A helper function that will return the SyntaxDocument attached to the
   * given text component.  Return null if the document is not a
   * SyntaxDocument, or if the text component is null
   */
  public static SyntaxDocument getSyntaxDocument (JTextComponent component) {
    if (component == null) {
      return null;
    }
    Document doc = component.getDocument();
    if (doc instanceof SyntaxDocument) {
      return (SyntaxDocument) doc;
    } else {
      return null;
    }
  }

  /**
   * Gets the Line Number at the give position of the editor component.
   * The first line number is ZERO
   *
   * @return line number
   */
  public static int getLineNumber (JTextComponent editor, int pos) {
    if (getSyntaxDocument(editor) != null) {
      SyntaxDocument sdoc = getSyntaxDocument(editor);
      return sdoc.getLineNumberAt(pos);
    } else {
      Document doc = editor.getDocument();
      return doc.getDefaultRootElement().getElementIndex(pos);
    }
  }

  /**
   * Get the closest position within the document of the component that
   * has given line and column.
   *
   * @param line   the first being 1
   * @param column the first being 1
   * @return the closest position for the text component at given line and
   * column
   */
  private static int getDocumentPosition (JTextComponent editor, int line, int column) {
    int lineHeight = editor.getFontMetrics(editor.getFont()).getHeight();
    int charWidth = editor.getFontMetrics(editor.getFont()).charWidth('m');
    int y = line * lineHeight;
    int x = column * charWidth;
    Point pt = new Point(x, y);
    return editor.viewToModel(pt);
  }

  public static int getLineCount (JTextComponent pane) {
    SyntaxDocument sdoc = getSyntaxDocument(pane);
    if (sdoc != null) {
      return sdoc.getLineCount();
    }
    int count = 0;
    int p = pane.getDocument().getLength() - 1;
    if (p > 0) {
      count = getLineNumber(pane, p);
    }
    return count;
  }

  /**
   * Insert the given item into the combo box, and set it as first selected
   * item.  If the item already exists, it is removed, so there are no
   * duplicates.
   *
   * @param item the item to insert. if it's null, then nothing is inserted
   */
  public static void insertIntoCombo (JComboBox combo, Object item) {
    if (item == null) {
      return;
    }
    MutableComboBoxModel model = (MutableComboBoxModel) combo.getModel();
    if (model.getSize() == 0) {
      model.insertElementAt(item, 0);
      return;
    }
    Object o = model.getElementAt(0);
    if (o.equals(item)) {
      return;
    }
    model.removeElement(item);
    model.insertElementAt(item, 0);
    combo.setSelectedIndex(0);
  }

  /**
   * Return the TabStop property for the given text component, or 0 if not
   * used
   */
  private static int getTabSize (JTextComponent text) {
    Integer tabs = (Integer) text.getDocument().getProperty(PlainDocument.tabSizeAttribute);
    return (null == tabs) ? 0 : tabs;
  }

  /**
   * Sets the caret position of the given target to the given line and column
   *
   * @param line   the first being 1
   * @param column the first being 1
   */
  public static void setCaretPosition (JTextComponent target, int line, int column) {
    int p = getDocumentPosition(target, line, column);
    target.setCaretPosition(p);
  }

  /**
   * Return a string with number of spaces equal to the tab-stop of the TextComponent
   */
  static String getTab (JTextComponent target) {
    return SPACES.substring(0, getTabSize(target));
  }
}
