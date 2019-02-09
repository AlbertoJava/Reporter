package Frames;



import javax.swing.*;
import java.awt.*;

public class StartUpWindow extends JFrame {
    private JTextArea textLabel = new JTextArea();



    public StartUpWindow(String message) throws HeadlessException {
        super ("Start up window");
        setSize(new Dimension(400,150));
        setLayout(new BorderLayout());
        Container c= getContentPane();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        textLabel.setWrapStyleWord(true);
        textLabel.setLineWrap(true);
        textLabel.setEditable(false);
        textLabel.setText(message);
        textLabel.setBackground(getBackground());
        c.add(textLabel,BorderLayout.NORTH);

    }
    public void setMessage (String message){
        textLabel.setText(message);
    }


}
