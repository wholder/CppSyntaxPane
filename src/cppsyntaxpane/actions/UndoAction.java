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

import cppsyntaxpane.SyntaxDocument;

import javax.swing.text.JTextComponent;

/**
 * Undo action
 */
@SuppressWarnings("unused")
public class UndoAction extends AbstractUndoRedoAction {
  public UndoAction () {
    super(SyntaxDocument.CAN_UNDO, "UNDO");
  }

  @Override
  protected boolean updateState () {
    return doc.canUndo();
  }

  @Override
  public void actionPerformed (JTextComponent target, SyntaxDocument sDoc) {
    if (sDoc != null) {
      sDoc.doUndo();
    }
  }
}
