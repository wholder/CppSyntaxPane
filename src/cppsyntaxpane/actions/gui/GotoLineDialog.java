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
import java.lang.ref.WeakReference;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import cppsyntaxpane.actions.ActionUtils;
import cppsyntaxpane.util.SwingUtils;

/**
 * A simple dialog to prompt for a line number and go to it
 *
 * @author Ayman Al-Sairafi
 */
public class GotoLineDialog extends javax.swing.JDialog implements EscapeListener {

  private static final String PROPERTY_KEY = "GOTOLINE_DIALOG";
  private final WeakReference<JTextComponent> text;
  private JButton   okButton;
  private JComboBox comboBox;


  /**
   * Creates new form GotoLineDialog
   */
  private GotoLineDialog (JTextComponent text) {
    super(SwingUtilities.getWindowAncestor(text), ModalityType.APPLICATION_MODAL);
    initComponents();
    this.text = new WeakReference<>(text);
    setLocationRelativeTo(text.getRootPane());
    getRootPane().setDefaultButton(okButton);
    text.getDocument().putProperty(PROPERTY_KEY, this);
    SwingUtils.addEscapeListener(this);
  }

  /**
   * This method is called from within the constructor to
   * initialize the form.
   */
  private void initComponents () {
    JPanel panel = new JPanel();
    panel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
    panel.setLayout(new GridLayout(1, 2));
    comboBox = new JComboBox();
    okButton = new JButton();
    panel.add(comboBox);
    panel.add(okButton);
    setTitle("Goto Line");
    setModal(true);
    setName("");
    setResizable(false);
    comboBox.setEditable(true);
    comboBox.addActionListener(this::jCmbLineNumbersActionPerformed);
    okButton.setAction(comboBox.getAction());
    okButton.setText("Go");
    okButton.addActionListener(evt -> jBtnOkActionPerformed());
    add(panel);
    pack();
  }

  private void setTextPos () {
    Object line = comboBox.getSelectedItem();
    if (line != null) {
      try {
        int lineNr = Integer.parseInt(line.toString()) - 1;
        ActionUtils.insertIntoCombo(comboBox, line);
        ActionUtils.setCaretPosition(text.get(), lineNr, 0);
        setVisible(false);
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Invalid Number: " + line, "Number Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void jCmbLineNumbersActionPerformed (java.awt.event.ActionEvent evt) {
    if (evt.getActionCommand().equals("comboBoxEdited")) {
      setTextPos();
    }
  }

  private void jBtnOkActionPerformed () {
    setTextPos();
  }

  /**
   * Create or return the GotoLine dialog for a given ext component
   */
  public static void showForEditor (JTextComponent text) {
    GotoLineDialog dlg;
    if (text.getDocument().getProperty(PROPERTY_KEY) == null) {
      dlg = new GotoLineDialog(text);
    } else {
      dlg = (GotoLineDialog) text.getDocument().getProperty(PROPERTY_KEY);
    }
    dlg.comboBox.requestFocusInWindow();
    dlg.setVisible(true);

  }

  @Override
  public void escapePressed () {
    setVisible(false);
  }
}
