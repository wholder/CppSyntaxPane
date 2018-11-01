import cppsyntaxpane.DefaultSyntaxKit;
import cppsyntaxpane.lexers.CppLexer;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

public class TestEditor extends JFrame {
  private transient Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

  private TestEditor () {
    setLayout(new BorderLayout());
    DefaultSyntaxKit synKit = new DefaultSyntaxKit(new CppLexer());
    JEditorPane codePane = new JEditorPane();
    synKit.addComponents(codePane);
    JScrollPane scroll = new JScrollPane(codePane);
    add(scroll, BorderLayout.CENTER);
    doLayout();
    codePane.setContentType("text/cpp");
    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    JMenu editMenu = synKit.getEditMenu(codePane);
    menuBar.add(editMenu);
    boolean windows = System.getProperty("os.name").toLowerCase().contains("win");
    codePane.setFont(new Font(windows ? "Consolas" : "Menlo", Font.PLAIN, 12));
    codePane.setEditable(true);
    Document doc = codePane.getDocument();
    doc.putProperty(PlainDocument.tabSizeAttribute, 4);
    codePane.setText(
      "#include <stdio.h>\n\n" +
      "/*\n" +
      " *  Block comment\n" +
      " */\n\n" +
      "void main () {\n" +
      "  // Line comment\n" +
      "  for (int ii = 0; ii < 2; ii++) {\n" +
      "    int jj = ii + 1;\n" +
      "  }\n" +
      "}\n"
    );
    // Add window close handler
    addWindowListener(new WindowAdapter() {
      public void windowClosing (WindowEvent ev) {
        System.exit(0);
      }
    });
    // Track window resize/move events and save in prefs
    addComponentListener(new ComponentAdapter() {
      public void componentMoved (ComponentEvent ev)  {
        Rectangle bounds = ev.getComponent().getBounds();
        prefs.putInt("window.x", bounds.x);
        prefs.putInt("window.y", bounds.y);
      }
      public void componentResized (ComponentEvent ev)  {
        Rectangle bounds = ev.getComponent().getBounds();
        prefs.putInt("window.width", bounds.width);
        prefs.putInt("window.height", bounds.height);
      }
    });
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setSize(prefs.getInt("window.width", 400), prefs.getInt("window.height", 300));
    setLocation(prefs.getInt("window.x", 10), prefs.getInt("window.y", 10));
    setVisible(true);
  }

  public static void main (String[] args) {
    new TestEditor();
  }
}
