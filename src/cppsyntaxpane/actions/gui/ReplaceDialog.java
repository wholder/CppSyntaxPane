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
package cppsyntaxpane.actions.gui;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import cppsyntaxpane.actions.ActionUtils;
import cppsyntaxpane.actions.DocumentSearchData;
import cppsyntaxpane.components.Markers;
import cppsyntaxpane.util.SwingUtils;

/**
 * A Find and Replace Dialog.  The dialog will also act as a listener to
 * Document changes so that all highlights are updated if the document is
 * changed.
 *
 * @author Ayman Al-Sairafi
 */
public class ReplaceDialog extends JDialog implements EscapeListener {
  private static Markers.SimpleMarker SEARCH_MARKER = new Markers.SimpleMarker(Color.YELLOW);
  private final JTextComponent        textComponent;
  private final DocumentSearchData    dsd;
  private JButton       nextButton;
  private JButton       replaceAllBbutton;
  private JCheckBox     ignoreCase;
  private JCheckBox     useRegex;
  private JComboBox     findBox;
  private JComboBox     replaceBox;
  private JToggleButton highlightButton;

  /**
   * Creates new form FindDialog
   */
  public ReplaceDialog (JTextComponent textComponent, DocumentSearchData dsd) {
    super(SwingUtilities.getWindowAncestor(textComponent), ModalityType.MODELESS);
    this.textComponent = textComponent;
    this.dsd = dsd;
    initComponents();
    textComponent.addCaretListener(ev -> updateHighlights());
    setLocationRelativeTo(textComponent.getRootPane());
    getRootPane().setDefaultButton(nextButton);
    SwingUtils.addEscapeListener(this);
    replaceAllBbutton.setEnabled(textComponent.isEditable() && textComponent.isEnabled());
  }


  @Override
  public void escapePressed () {
    setVisible(false);
  }

  /**
   * updates the highlights in the document when it is updated.
   * This is called by the DocumentListener methods
   */
  private void updateHighlights () {
    Markers.removeMarkers(textComponent, SEARCH_MARKER);
    if (highlightButton.isSelected()) {
      Markers.markAll(textComponent, dsd.getPattern(), SEARCH_MARKER);
    }
  }

  private void showRegexpError (PatternSyntaxException ex) throws HeadlessException {
    JOptionPane.showMessageDialog(this, "Regexp error: " + ex.getMessage(), "Regular Expression Error", JOptionPane.ERROR_MESSAGE);
    findBox.requestFocus();
  }

  /**
   * update the finder object with data from our UI
   */
  private void updateFinder () {
    String regex = (String) findBox.getSelectedItem();
    try {
      dsd.setPattern(regex,
        useRegex.isSelected(),
        ignoreCase.isSelected());
      ActionUtils.insertIntoCombo(findBox, regex);
    } catch (PatternSyntaxException e) {
      showRegexpError(e);
    }
  }

  private GridBagConstraints getGbc (int x, int y) {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = x;
    gbc.gridy = y;
    //gbc.anchor = (x == 0) ? GridBagConstraints.WEST : GridBagConstraints.EAST;
    gbc.fill = (x == 0) ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
    gbc.weightx = (new double[] {0.1, 0.7, 0.2})[x];
    gbc.ipady = 2;
    return gbc;
  }

  /**
   * This method is called from within the constructor to
   * initialize the form.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents () {
    JLabel findLabel = new JLabel();
    findLabel.setDisplayedMnemonic('F');
    findLabel.setLabelFor(findBox);
    findLabel.setText("Find");

    JLabel replaceLabel = new JLabel();
    replaceLabel.setDisplayedMnemonic('R');
    replaceLabel.setLabelFor(replaceBox);
    replaceLabel.setText("Replace");

    nextButton = new JButton();
    nextButton.setMnemonic('N');
    nextButton.setText("Next");
    nextButton.addActionListener(evt -> nextActionPerformed());

    JButton prevButton = new JButton();
    prevButton.setMnemonic('P');
    prevButton.setText("Previous");
    prevButton.addActionListener(evt -> prevActionPerformed());

    replaceAllBbutton = new JButton();
    replaceAllBbutton.setMnemonic('H');
    replaceAllBbutton.setText("Replace All");
    replaceAllBbutton.addActionListener(evt -> replaceAllActionPerformed());

    highlightButton = new JToggleButton();
    highlightButton.setText("Highlight");
    highlightButton.addActionListener(evt -> highlightActionPerformed());

    JButton replaceButton = new JButton();
    replaceButton.setText("Replace");
    replaceButton.addActionListener(evt -> replaceActionPerformed());

    JCheckBox wrapCheck = new JCheckBox();
    wrapCheck.setMnemonic('W');
    wrapCheck.setText("Wrap around");
    wrapCheck.setToolTipText("Wrap to beginning when end is reached");

    useRegex = new JCheckBox();
    useRegex.setMnemonic('R');
    useRegex.setText("Regular Expression");
    useRegex.setToolTipText("Search using a regular expression to metch text");

    ignoreCase = new JCheckBox();
    ignoreCase.setMnemonic('I');
    ignoreCase.setText("Ignore Case");

    replaceBox = new JComboBox();
    replaceBox.setEditable(true);

    findBox = new JComboBox();
    findBox.setEditable(true);

    setTitle("Find and Replace");
    setName("");
    setResizable(false);

    JPanel fields = new JPanel();
    fields.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    fields.setLayout(new GridBagLayout());
    fields.add(findLabel,         getGbc(0, 0));
    fields.add(findBox,           getGbc(1, 0));
    fields.add(nextButton,        getGbc(2, 0));

    fields.add(replaceLabel,      getGbc(0, 1));
    fields.add(replaceBox,        getGbc(1, 1));
    fields.add(prevButton,        getGbc(2, 1));

    fields.add(new JLabel(),      getGbc(0, 2));
    fields.add(wrapCheck,         getGbc(1, 2));
    fields.add(replaceButton,     getGbc(2, 2));

    fields.add(new JLabel(),      getGbc(0, 3));
    fields.add(useRegex,          getGbc(1, 3));
    fields.add(replaceAllBbutton, getGbc(2, 3));

    fields.add(new JLabel(),      getGbc(0, 4));
    fields.add(ignoreCase,        getGbc(1, 4));
    fields.add(highlightButton,   getGbc(2, 4));

    fields.setPreferredSize(new Dimension(450, fields.getPreferredSize().height));
    add(fields);
    pack();
  }

  private void nextActionPerformed() {
    try {
      updateFinder();
      if (!dsd.doFindNext(textComponent)) {
        dsd.msgNotFound(textComponent);
      }
      textComponent.requestFocusInWindow();
    } catch (PatternSyntaxException ex) {
      showRegexpError(ex);
    }
  }

  private void replaceAllActionPerformed() {
    try {
      updateFinder();
      String replaceText = (String) replaceBox.getSelectedItem();
      if (replaceText == null) {
        replaceText = "";
      }
      if (!useRegex.isSelected()) {
        replaceText = Matcher.quoteReplacement(replaceText);
      }
      ActionUtils.insertIntoCombo(replaceBox, replaceText);
      highlightButton.setSelected(false);
      dsd.doReplaceAll(textComponent, replaceText);
      textComponent.requestFocusInWindow();
    } catch (PatternSyntaxException ex) {
      showRegexpError(ex);
    }
  }

  private void highlightActionPerformed() {
    updateFinder();
    updateHighlights();
  }

  private void prevActionPerformed() {
    updateFinder();
    dsd.doFindPrev(textComponent);
  }

  private void replaceActionPerformed() {
    highlightButton.setSelected(false);
    String replacement = replaceBox.getSelectedItem() == null ? "" : replaceBox.getSelectedItem().toString();
    dsd.doReplace(textComponent, replacement);
  }
}
