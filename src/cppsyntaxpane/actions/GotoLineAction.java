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

import cppsyntaxpane.actions.gui.GotoLineDialog;

import javax.swing.text.JTextComponent;

import cppsyntaxpane.SyntaxDocument;

/**
 * This actions displays the GotoLine dialog
 */
@SuppressWarnings("unused")
public class GotoLineAction extends DefaultSyntaxAction {

  public GotoLineAction () {
    super("GOTO_LINE");
  }

  @Override
  public void actionPerformed (JTextComponent target, SyntaxDocument sdoc) {
    GotoLineDialog.showForEditor(target);
  }
}
