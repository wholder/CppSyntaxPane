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

import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

import cppsyntaxpane.SyntaxDocument;

/**
 * The DefaultSyntaxAction.  You can extend this class or implement the interface
 * SyntaxAction to create your own actions.
 *
 * @author Ayman Al-Sairafi
 */
abstract public class DefaultSyntaxAction extends TextAction implements SyntaxAction {

  DefaultSyntaxAction (String actionName) {
    super(actionName);
    putValue(NAME, actionName);
  }

  @Override
  public void install (JEditorPane editor) {
  }

  @Override
  public void deinstall (JEditorPane editor) {
    // nothing
  }

  @Override
  public void actionPerformed (ActionEvent e) {
    JTextComponent text = getTextComponent(e);
    SyntaxDocument sdoc = ActionUtils.getSyntaxDocument(text);
    if (text != null) {
      actionPerformed(text, sdoc);
    }
  }

  /**
   * Convenience method that will be called if the Action is performed on a
   * JTextComponent.  SyntaxActions should generally override this method.
   *  @param target (non-null JTextComponent from Action.getSource
   * @param sDoc   (SyntaxDOcument of the text component, could be null)
   */
  public void actionPerformed (JTextComponent target, SyntaxDocument sDoc) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
