package cppsyntaxpane.actions;

import cppsyntaxpane.SyntaxDocument;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public abstract class AbstractUndoRedoAction extends DefaultSyntaxAction {
  private JEditorPane editor;
  SyntaxDocument doc;

  private final String property;

  AbstractUndoRedoAction (String property, String key) {
    super(key);
    this.property = property;
  }

  private PropertyChangeListener propListener = e -> setEnabled(updateState());

  abstract protected boolean updateState ();

  private void removeDocument () {
    if (doc != null) {
      doc.removePropertyChangeListener(property, propListener);
      doc = null;
    }
  }

  private void setDocument (SyntaxDocument newDoc) {
    if (doc != null) {
      throw new IllegalStateException();
    }
    doc = newDoc;
    doc.addPropertyChangeListener(property, propListener);
    setEnabled(updateState());
  }

  private PropertyChangeListener docListener = e -> {
    removeDocument();
    Object newDoc = e.getNewValue();
    if (newDoc instanceof SyntaxDocument) {
      setDocument((SyntaxDocument) newDoc);
    }
  };

  @Override
  public void install (JEditorPane editor) {
    if (this.editor != null) {
      throw new IllegalStateException();
    }
    this.editor = editor;
    editor.addPropertyChangeListener("document", docListener);
  }

  @Override
  public void deinstall (JEditorPane editor) {
    super.deinstall(editor);
    if (this.editor != editor) {
      throw new IllegalStateException();
    }
    editor.removePropertyChangeListener("document", docListener);
    removeDocument();
    this.editor = null;
  }
}