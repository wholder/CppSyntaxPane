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

import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import cppsyntaxpane.SyntaxDocument;

/**
 * IndentAction is used to replace Tabs with spaces.  If there is selected
 * text, then the lines spanning the selection will be shifted
 * right by one tab-width space character.
 * <p>
 * Since this is also used as an abbreviation completion action,
 * Abbreviiations are processed by this event.
 * <p>
 * FIXME:  Move the abbreviation expansion to an ActionUtils proc
 *
 * @author Ayman Al-Sairafi
 */
@SuppressWarnings("unused")
public class IndentAction extends DefaultSyntaxAction {

  public IndentAction () {
    super("insert-tab");
  }

  @Override
  public void actionPerformed (JTextComponent target, SyntaxDocument sDoc) {
    String selected = target.getSelectedText();
    if (selected != null) {
      String[] lines = ActionUtils.getSelectedLines(target);
      int start = target.getSelectionStart();
      StringBuilder sb = new StringBuilder();
      for (String line : lines) {
        sb.append(ActionUtils.getTab(target));
        sb.append(line);
        sb.append('\n');
      }
      target.replaceSelection(sb.toString());
      target.select(start, start + sb.length());
    } else {
      // If no text select, insert tab
      int caretPos = target.getCaretPosition();
      Document doc = target.getDocument();
      try {
        doc.insertString(caretPos, "\t", null);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}
