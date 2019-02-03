package Frames;

import javax.swing.*;
import java.awt.*;

public class ResultView extends JScrollPane {
    private JTextArea textArea = new JTextArea();

    public ResultView() {
        //textArea.setEnabled(false);
        setViewportView(textArea);
        setBorder(BorderFactory.createTitledBorder("Task result panel"));
        setPreferredSize(new Dimension(300,100));
        textArea.setColumns(400);
    }

    public void setText (String text){
        textArea.setText(text);
    }
    public String getText(){
        return textArea.getText();
    }
    public void append (String text){
        textArea.append(text);
    }

}
