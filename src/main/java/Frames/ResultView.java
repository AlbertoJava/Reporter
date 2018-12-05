package Frames;

import javax.swing.*;
import java.awt.*;

public class ResultView extends JScrollPane {
    private JTextArea textArea = new JTextArea();

    public ResultView() {
        setViewportView(textArea);
        setBorder(BorderFactory.createTitledBorder("This is ScrollPanel"));
        setPreferredSize(new Dimension(300,100));
    }

    public void setText (String text){
        textArea.setText(text);
    }
    public void append (String text){
        textArea.append(text);
    }

}
