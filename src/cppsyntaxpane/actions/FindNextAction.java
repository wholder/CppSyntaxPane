package cppsyntaxpane.actions;

import javax.swing.text.JTextComponent;

import cppsyntaxpane.SyntaxDocument;

/**
 * This class performs a Find Next operation by using the current pattern
 */
@SuppressWarnings("unused")
public class FindNextAction extends DefaultSyntaxAction {

  public FindNextAction () {
    super("find-next");
  }

  @Override
  public void actionPerformed (JTextComponent target, SyntaxDocument sdoc) {
    DocumentSearchData dsd = DocumentSearchData.getFromEditor(target);
    if (dsd != null) {
      if (!dsd.doFindNext(target)) {
        dsd.msgNotFound(target);
      }
    }
  }
}
