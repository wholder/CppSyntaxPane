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
package cppsyntaxpane;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import cppsyntaxpane.actions.SyntaxAction;
import cppsyntaxpane.components.SyntaxComponent;

/**
 * The DefaultSyntaxKit is the main entry to SyntaxPane.  To use the package, just
 * set the EditorKit of the EditorPane to a new instance of this class.
 * <p>
 * You need to pass a proper lexer to the class.
 *
 * @author ayman, Hanns Holger Rutz
 */
public class DefaultSyntaxKit extends DefaultEditorKit implements ViewFactory {
  private static final Color  caretColor = new Color(0x000000);
  private static final Color  selectionColor = new Color(0x99CCFF);
  private static final String[] components = {"cppsyntaxpane.components.PairsMarker", "cppsyntaxpane.components.LineNumbersRuler"};
  private final Lexer lexer;
  private final Map<JEditorPane, List<SyntaxComponent>> editorComponents = new WeakHashMap<>();

  static {
    initKit();
  }

  /**
   * Creates a new Kit for the given language
   */
  public DefaultSyntaxKit (Lexer lexer) {
    super();
    this.lexer = lexer;
  }

  @Override
  public ViewFactory getViewFactory () {
    return this;
  }

  @Override
  public View create (Element element) {
    return new SyntaxView(element);
  }

  /**
   * Adds UI components to the pane
   *
   * @param editorPane a component to install this kit for
   */
  public void addComponents (JEditorPane editorPane) {
    // install the components to the editor:
    for (String c : components) {
      installComponent(editorPane, c);
    }
  }

  /**
   * Creates a SyntaxComponent of the the given class name and installs it on the pane
   */
  private void installComponent (JEditorPane pane, String className) {
    try {
      Class<?> compClass = Class.forName(className);
      SyntaxComponent comp = (SyntaxComponent) compClass.newInstance();
      comp.install(pane);
      editorComponents.computeIfAbsent(pane, k -> new ArrayList<>());
      editorComponents.get(pane).add(comp);
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void read (Reader in, Document doc, int pos)
    throws IOException, BadLocationException {
    super.read(in, doc, pos);
  }

  private static final int  CMD = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
  private static final int  SHIFT = InputEvent.SHIFT_MASK;
  private static final int  CTRL = InputEvent.CTRL_MASK;

  private static KeyStroke getKey (int keyCode, int modifier) {
    return KeyStroke.getKeyStroke(keyCode, modifier);
  }

  private JMenuItem getMenuItem (JEditorPane ePane, String menuText, String actionCode, String actionClass, KeyStroke key) {
    Action action = ePane.getActionMap().get(actionCode);
    JMenuItem menuItem = new JMenuItem(action);
    menuItem.setText(menuText);
    if (actionClass != null) {
      try {
        ActionMap actionMap = ePane.getActionMap();
        String actionName = "cppsyntaxpane.actions." + actionClass;
        SyntaxAction synAction = (SyntaxAction) Class.forName(actionName).newInstance();
        synAction.putValue(Action.NAME, menuText);
        synAction.install(ePane);
        menuItem.setAction(synAction);
        actionMap.put(actionCode, synAction);
      } catch (InstantiationException ex) {
        throw new IllegalArgumentException("Cannot create class: " + actionClass + ". Ensure it has default constructor.", ex);
      } catch (IllegalAccessException | ClassNotFoundException | ClassCastException ex) {
        throw new IllegalArgumentException("Cannot create class: " + actionClass, ex);
      }
    }
    if (key != null) {
      menuItem.setAccelerator(key);
      InputMap inMap = ePane.getInputMap();
      inMap.put(key, actionCode);
    }
    return menuItem;
  }

  public JMenu getEditMenu (JEditorPane ePane) {
    JMenu menu = new JMenu("Edit");
    menu.add(getMenuItem(ePane, "Cut",         "cut-to-clipboard",      null,                   getKey(KeyEvent.VK_X, CMD)));
    menu.add(getMenuItem(ePane, "Copy",        "copy-to-clipboard",     null,                   getKey(KeyEvent.VK_C, CMD)));
    menu.add(getMenuItem(ePane, "Paste",       "paste-from-clipboard",  null,                   getKey(KeyEvent.VK_V, CMD)));
    menu.addSeparator();
    menu.add(getMenuItem(ePane, "Select All",  "select-all",            null,                   getKey(KeyEvent.VK_A, CMD)));
    menu.addSeparator();
    menu.add(getMenuItem(ePane, "Undo",        "undo",                  "UndoAction",           getKey(KeyEvent.VK_Z, CMD)));
    menu.add(getMenuItem(ePane, "Redo",        "redo",                  "RedoAction",           getKey(KeyEvent.VK_Z, CMD + SHIFT)));
    menu.addSeparator();
    menu.add(getMenuItem(ePane, "Indent",      "indent",                "IndentAction",         getKey(KeyEvent.VK_TAB, 0)));
    menu.add(getMenuItem(ePane, "Unindent",    "unindent",              "UnindentAction",       getKey(KeyEvent.VK_TAB, SHIFT)));
    menu.addSeparator();
    menu.add(getMenuItem(ePane, "Find",        "find",                  "FindReplaceAction",    getKey(KeyEvent.VK_F, CMD)));
    menu.add(getMenuItem(ePane, "Find Next",   "find-next",             "FindNextAction",       getKey(KeyEvent.VK_G, CMD)));
    menu.addSeparator();
    menu.add(getMenuItem(ePane, "Goto Line Number", "goto-line",        "GotoLineAction",       getKey(KeyEvent.VK_G, CTRL)));
    menu.add(getMenuItem(ePane, "Toggle Comments", "toggle-comments",   "ToggleCommentsAction", getKey(KeyEvent.VK_SLASH, CTRL)));
    return menu;
  }

  /**
   * Installs the View on the given EditorPane.  This is called by Swing and
   * can be used to do anything you need on the JEditorPane control.  Here
   * I set some default Actions.
   */
  @Override
  public void install (JEditorPane editorPane) {
    super.install(editorPane);
    editorPane.setFont(new Font("Courier New", Font.PLAIN, 12));
    editorPane.setCaretColor(caretColor);
    editorPane.setSelectionColor(selectionColor);
    addComponents(editorPane);    // {} pair marking, etc.
  }

  @Override
  public void deinstall (JEditorPane editorPane) {
    for (SyntaxComponent c : editorComponents.get(editorPane)) {
      c.deinstall(editorPane);
    }
    editorComponents.clear();
    editorPane.getInputMap().clear();
    ActionMap m = editorPane.getActionMap();
    for (Object key : editorPane.getActionMap().keys()) {
      Action a = m.get(key);
      if (a instanceof SyntaxAction) {
        ((SyntaxAction) a).deinstall(editorPane);
      }
    }
    m.clear();
  }

  /**
   * This is called by Swing to create a Document for the JEditorPane document
   * This may be called before you actually get a reference to the control.
   * We use it here to create a proper lexer and pass it to the
   * SyntaxDocument we return.
   */
  @Override
  public Document createDefaultDocument () {
    return new SyntaxDocument(lexer);
  }

  /**
   * This is called to initialize the list of <code>Lexer</code>s we have.
   * You can call  this at initialization, or it will be called when needed.
   * The method will also add the appropriate EditorKit classes to the
   * corresponding ContentType of the JEditorPane.  After this is called,
   * you can simply call the editor.setContentType("text/java") on the
   * control and you will be done.
   */
  private synchronized static void initKit () {
    JEditorPane.registerEditorKitForContentType("text/cpp", "cppsyntaxpane.syntaxkits.CppSyntaxKit");
  }

  @Override
  public String getContentType () {
    return "text/" + this.getClass().getSimpleName().replace("SyntaxKit", "").toLowerCase();
  }
}