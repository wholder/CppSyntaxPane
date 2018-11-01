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
package cppsyntaxpane.actions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.JTextComponent;

import cppsyntaxpane.SyntaxDocument;

/**
 * This action will toggle comments on or off on selected whole lines.
 *
 * @author Ayman Al-Sairafi, Hanns Holger Rutz
 */
@SuppressWarnings("unused")
public class ToggleCommentsAction extends DefaultSyntaxAction {

  private Pattern lineCommentPattern = null;

  /**
   * creates new JIndentAction.
   * Initial Code contributed by ser... AT mail.ru
   */
  public ToggleCommentsAction () {
    super("toggle-comment");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void actionPerformed (JTextComponent target, SyntaxDocument sDoc) {
    String lineCommentStart = "//";
    if (lineCommentPattern == null) {
      lineCommentPattern = Pattern.compile("(^\\s*)(" + lineCommentStart + "\\s?)(.*)");
    }
    String[] lines = ActionUtils.getSelectedLines(target);
    int start = target.getSelectionStart();
    StringBuilder toggled = new StringBuilder();
    boolean allComments = true;
    for (String line : lines) {
      Matcher m1 = lineCommentPattern.matcher(line);
      if (!m1.find()) {
        allComments = false;
        break;
      }
    }
    for (String line : lines) {
      if (allComments) {
        Matcher m1 = lineCommentPattern.matcher(line);
        m1.find();
        toggled.append(m1.replaceFirst("$1$3"));
      } else {
        toggled.append(lineCommentStart);
        toggled.append(' ');
        toggled.append(line);
      }
      toggled.append('\n');
    }
    target.replaceSelection(toggled.toString());
    target.select(start, start + toggled.length());
  }
}
