package Frames;

import Utilz.SqlProperties;


import javax.swing.*;
import java.awt.*;
import java.util.Map;


public class MyFrame extends JFrame {
    private DetailsPanel detailsPanel;
    private ProccessesPanelTab proccessesPanel;
    private TestPanel testPanel;
    private ResultView resultView = new ResultView();
    //private JTextArea textArea = new JTextArea();


    public   MyFrame (String title, Map<SqlProperties,Boolean> statusMap){
        super(title);
        //Setlayout manager
        setLayout(new BorderLayout());
        //Add swing components to content pane
        Container c = getContentPane();
        proccessesPanel=new ProccessesPanelTab();
  /*      c.add(resultView,BorderLayout.CENTER);
        c.add(proccessesPanel, BorderLayout.WEST);*/
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        jSplitPane.setRightComponent(resultView);
        jSplitPane.setLeftComponent(proccessesPanel);
        BorderLayout bl = new BorderLayout();
        c.setLayout(bl);
        c.add(jSplitPane,BorderLayout.CENTER);
        c.add(new JLabel("Contact e-mail: albtutanota@tutanota.com"),BorderLayout.SOUTH);


      }

       public ProccessesPanelTab getProccessesPanel() {
        return proccessesPanel;
    }

    public ResultView getTextArea() {

        return resultView;
    }
}
