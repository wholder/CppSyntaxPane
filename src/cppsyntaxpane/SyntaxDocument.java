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
package cppsyntaxpane;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;

/**
 * A document that supports being highlighted.  The document maintains an
 * internal List of all the Tokens.  The Tokens are updated using
 * a Lexer, passed to it during construction.
 *
 * @author Ayman Al-Sairafi, Hanns Holger Rutz
 */
public class SyntaxDocument extends PlainDocument {
  public static final String CAN_UNDO = "can-undo";
  public static final String CAN_REDO = "can-redo";

  private Lexer               lexer;
  private List<Token>         tokens;
  private CompoundUndoManager undo;

  private final PropertyChangeSupport propSupport;
  private boolean canUndoState = false;
  private boolean canRedoState = false;

  public SyntaxDocument (Lexer lexer) {
    super();
    putProperty(PlainDocument.tabSizeAttribute, 4);
    this.lexer = lexer;
    undo = new CompoundUndoManager(this);    // Listen for undo and redo events
    propSupport = new PropertyChangeSupport(this);
  }

  /*
   * Parse the entire document and return list of tokens that do not already
   * exist in the tokens list.  There may be overlaps, and replacements,
   * which we will cleanup later.
   *
   * @return list of tokens that do not exist in the tokens field
   */
  private void parse () {
    // if we have no lexer, then we must have no tokens...
    if (lexer == null) {
      tokens = null;
      return;
    }
    List<Token> toks = new ArrayList<>(getLength() / 10);
    long ts = System.nanoTime();
    int len = getLength();
    try {
      Segment seg = new Segment();
      getText(0, getLength(), seg);
      lexer.parse(seg, 0, toks);
    } catch (BadLocationException ex) {
      log.log(Level.SEVERE, null, ex);
    } finally {
      if (log.isLoggable(Level.FINEST)) {
        log.finest(String.format("Parsed %d in %d ms, giving %d tokens\n",
          len, (System.nanoTime() - ts) / 1000000, toks.size()));
      }
      tokens = toks;
    }
  }

  @Override
  protected void fireChangedUpdate (DocumentEvent e) {
    parse();
    super.fireChangedUpdate(e);
  }

  @Override
  protected void fireInsertUpdate (DocumentEvent e) {
    parse();
    super.fireInsertUpdate(e);
  }

  @Override
  protected void fireRemoveUpdate (DocumentEvent e) {
    parse();
    super.fireRemoveUpdate(e);
  }

  /**
   * This class is used to iterate over tokens between two positions
   */
  class TokenIterator implements ListIterator<Token> {

    int start;
    int end;
    int ndx = 0;

    @SuppressWarnings("unchecked")
    private TokenIterator (int start, int end) {
      this.start = start;
      this.end = end;
      if (tokens != null && !tokens.isEmpty()) {
        Token token = new Token(TokenType.COMMENT, start, end - start);
        ndx = Collections.binarySearch((List) tokens, token);
        // we will probably not find the exact token...
        if (ndx < 0) {
          // so, start from one before the token where we should be...
          // -1 to get the location, and another -1 to go back..
          ndx = (-ndx - 1 - 1 < 0) ? 0 : (-ndx - 1 - 1);
          Token t = tokens.get(ndx);
          // if the prev token does not overlap, then advance one
          if (t.end() <= start) {
            ndx++;
          }

        }
      }
    }

    @Override
    public boolean hasNext () {
      if (tokens == null) {
        return false;
      }
      if (ndx >= tokens.size()) {
        return false;
      }
      Token t = tokens.get(ndx);
      return t.start < end;
    }

    @Override
    public Token next () {
      return tokens.get(ndx++);
    }

    @Override
    public void remove () {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasPrevious () {
      if (tokens == null) {
        return false;
      }
      if (ndx <= 0) {
        return false;
      }
      Token t = tokens.get(ndx);
      return t.end() > start;
    }

    @Override
    public Token previous () {
      return tokens.get(ndx--);
    }

    @Override
    public int nextIndex () {
      return ndx + 1;
    }

    @Override
    public int previousIndex () {
      return ndx - 1;
    }

    @Override
    public void set (Token e) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void add (Token e) {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Returns an iterator of tokens between p0 and p1.
   *
   * @param start start position for getting tokens
   * @param end   position for last token
   * @return Iterator for tokens that overall with range from start to end
   */
  Iterator<Token> getTokens (int start, int end) {
    return new TokenIterator(start, end);
  }

  /**
   * Finds the token at a given position.  May return null if no token is
   * found (whitespace skipped) or if the position is out of range:
   */
  public Token getTokenAt (int pos) {
    if (tokens == null || tokens.isEmpty() || pos > getLength()) {
      return null;
    }
    Token tok = null;
    Token tKey = new Token(TokenType.DEFAULT, pos, 1);
    @SuppressWarnings("unchecked")
    int ndx = Collections.binarySearch((List) tokens, tKey);
    if (ndx < 0) {
      // so, start from one before the token where we should be...
      // -1 to get the location, and another -1 to go back..
      ndx = (-ndx - 1 - 1 < 0) ? 0 : (-ndx - 1 - 1);
      Token t = tokens.get(ndx);
      if ((t.start <= pos) && (pos <= t.end())) {
        tok = t;
      }
    } else {
      tok = tokens.get(ndx);
    }
    return tok;
  }

  /**
   * This is used to return the other part of a paired token in the document.
   * A paired part has token.pairValue <> 0, and the paired token will
   * have the negative of t.pairValue.
   * This method properly handles nestings of same pairValues, but overlaps
   * are not checked.
   * if the document does not contain a paired token, then null is returned.
   *
   * @return the other pair's token, or null if nothing is found.
   */
  public Token getPairFor (Token t) {
    if (t == null || t.pairValue == 0) {
      return null;
    }
    Token p = null;
    int ndx = tokens.indexOf(t);
    // w will be similar to a stack. The openners weght is added to it
    // and the closers are subtracted from it (closers are already negative)
    int w = t.pairValue;
    int direction = (t.pairValue > 0) ? 1 : -1;
    boolean done = false;
    int v = Math.abs(t.pairValue);
    while (!done) {
      ndx += direction;
      if (ndx < 0 || ndx >= tokens.size()) {
        break;
      }
      Token current = tokens.get(ndx);
      if (Math.abs(current.pairValue) == v) {
        w += current.pairValue;
        if (w == 0) {
          p = current;
          done = true;
        }
      }
    }

    return p;
  }

  // public boolean isDirty() { return dirty; }

  void setCanUndo (boolean value) {
    if (canUndoState != value) {
      // System.out.println("canUndo = " + value);
      canUndoState = value;
      propSupport.firePropertyChange(CAN_UNDO, !value, value);
    }
  }

  void setCanRedo (boolean value) {
    if (canRedoState != value) {
      // System.out.println("canRedo = " + value);
      canRedoState = value;
      propSupport.firePropertyChange(CAN_REDO, !value, value);
    }
  }

  public void addPropertyChangeListener (String property, PropertyChangeListener listener) {
    // System.out.println("ADD " + property + " " + listener.hashCode() + " / " + this.hashCode());
    propSupport.addPropertyChangeListener(property, listener);
  }

  public void removePropertyChangeListener (String property, PropertyChangeListener listener) {
    // System.out.println("REM " + property + " " + listener.hashCode() + " / " + this.hashCode());
    propSupport.removePropertyChangeListener(property, listener);
  }

  /**
   * Performs an undo action, if possible
   */
  public void doUndo () {
    if (undo.canUndo()) {
      undo.undo();
      parse();
    }
  }

  public boolean canUndo () {
    return canUndoState; // undo.canUndo();
  }

  /**
   * Performs a redo action, if possible.
   */
  public void doRedo () {
    if (undo.canRedo()) {
      undo.redo();
      parse();
    }
  }

  public boolean canRedo () {
    return canRedoState; // undo.canRedo();
  }

  /**
   * Returns a matcher that matches the given pattern on the entire document
   *
   * @return matcher object
   */
  public Matcher getMatcher (Pattern pattern) {
    return getMatcher(pattern, 0, getLength());
  }

  /**
   * Returns a matcher that matches the given pattern in the part of the
   * document starting at offset start.  Note that the matcher will have
   * offset starting from <code>start</code>
   *
   * @return matcher that <b>MUST</b> be offset by start to get the proper
   * location within the document
   */
  public Matcher getMatcher (Pattern pattern, int start) {
    return getMatcher(pattern, start, getLength() - start);
  }

  /**
   * Returns a matcher that matches the given pattern in the part of the
   * document starting at offset start and ending at start + length.
   * Note that the matcher will have
   * offset starting from <code>start</code>
   *
   * @return matcher that <b>MUST</b> be offset by start to get the proper location within the document
   */
  private Matcher getMatcher (Pattern pattern, int start, int length) {
    Matcher matcher = null;
    if (getLength() == 0) {
      return null;
    }
    if (start >= getLength()) {
      return null;
    }
    try {
      if (start < 0) {
        start = 0;
      }
      if (start + length > getLength()) {
        length = getLength() - start;
      }
      Segment seg = new Segment();
      getText(start, length, seg);
      matcher = pattern.matcher(seg);
    } catch (BadLocationException ex) {
      log.log(Level.SEVERE, "Requested offset: " + ex.offsetRequested(), ex);
    }
    return matcher;
  }

  /**
   * Returns the number of lines in this document
   */
  public int getLineCount () {
    Element e = getDefaultRootElement();
    return e.getElementCount();
  }

  /**
   * Returns the line number at given position.  The line numbers are zero based
   */
  public int getLineNumberAt (int pos) {
    return getDefaultRootElement().getElementIndex(pos);
  }

  @Override
  public String toString () {
    return "SyntaxDocument(" + lexer + ", " + ((tokens == null) ? 0 : tokens.size()) + " tokens)@" +
      hashCode();
  }

  /**
   * We override this here so that the replace is treated as one operation
   * by the undomanager
   */
  @Override
  public void replace (int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
    remove(offset, length);
    undo.startCombine();
    insertString(offset, text, attrs);
  }

  // our logger instance...
  private static final Logger log = Logger.getLogger(SyntaxDocument.class.getName());
}
