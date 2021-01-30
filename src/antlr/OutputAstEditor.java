package antlr;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;

public class OutputAstEditor implements CaretListener {
    private JTextArea textArea = new JTextArea(20, 30);
    private JScrollPane scroll = new JScrollPane(textArea,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    public OutputAstEditor() {
        super();
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        textArea.addCaretListener(this);
    }

    public void caretUpdate(CaretEvent arg0) {
        currentTestCase.getOutput().setScript(getText());
        listCases.updateUI();
    }

    public void setText(String text) {
        this.textArea.setText(text);
    }

    public String getText() {
        return this.textArea.getText();
    }

    public JScrollPane getView() {
        return this.scroll;
    }
}