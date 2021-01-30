package antlr;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import java.awt.*;

public class InputStringEditor {
    public InputStringEditor() {
        super();

        this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        this.addCaretListener(this);
    }

    public void caretUpdate(CaretEvent arg0) {
        currentTestCase.getInput().setScript(getText());
        listCases.updateUI();
    }
}
