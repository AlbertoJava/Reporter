package Frames;

import Utilz.SqlProperties;


import javax.swing.*;
import java.awt.*;
import java.util.Map;


public class MyFrame extends JFrame {
    private DetailsPanel detailsPanel;
    private ProccessesPanel proccessesPanel;
    private TestPanel testPanel;
    private ResultView resultView = new ResultView();
    //private JTextArea textArea = new JTextArea();

    public ResultView getTextArea() {
        return resultView;
    }

    public   MyFrame (String title, Map<SqlProperties,Boolean> statusMap){
        super(title);
        //Setlayout manager
        setLayout(new BorderLayout());
        //Add swing components to content pane
        Container c = getContentPane();
        c.add(resultView,BorderLayout.CENTER);
        proccessesPanel=new ProccessesPanel();
        c.add(proccessesPanel, BorderLayout.WEST);
      }

       public ProccessesPanel getProccessesPanel() {
        return proccessesPanel;
    }
}
