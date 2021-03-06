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
package cppsyntaxpane.components;

import cppsyntaxpane.actions.ActionUtils;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JEditorPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

import cppsyntaxpane.SyntaxDocument;
import cppsyntaxpane.Token;

/**
 * This class highlights any pairs of the given language.  Pairs are defined
 * with the Token.pairValue.
 *
 * @author Ayman Al-Sairafi
 */
@SuppressWarnings("unused")
public class PairsMarker implements CaretListener, SyntaxComponent, PropertyChangeListener {
  private static Color markerColor = new Color(0xFFBB77);
  private JTextComponent pane;
  private Markers.SimpleMarker marker;
  private Status status;

  public PairsMarker () {
    this.marker = new Markers.SimpleMarker(markerColor);
  }

  @Override
  public void caretUpdate (CaretEvent e) {
    removeMarkers();
    int pos = e.getDot();
    SyntaxDocument doc = ActionUtils.getSyntaxDocument(pane);
    if (doc != null) {
      Token token = doc.getTokenAt(pos);
      if (token != null && token.pairValue != 0) {
        Markers.markToken(pane, token, marker);
        Token other = doc.getPairFor(token);
        if (other != null) {
          Markers.markToken(pane, other, marker);
        }
      }
    }
  }

  /**
   * Remove all the highlights from the editor pane.  This should be called
   * when the editor-kit is removed.
   */
  private void removeMarkers () {
    Markers.removeMarkers(pane, marker);
  }

  @Override
  public void install (JEditorPane editor) {
    pane = editor;
    pane.addCaretListener(this);
    status = Status.INSTALLING;
  }

  @Override
  public void deinstall (JEditorPane editor) {
    status = Status.DEINSTALLING;
    pane.removeCaretListener(this);
    removeMarkers();
  }

  @Override
  public void propertyChange (PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("document")) {
      pane.removeCaretListener(this);
      if (status.equals(Status.INSTALLING)) {
        pane.addCaretListener(this);
        removeMarkers();
      }
    }
  }
}
