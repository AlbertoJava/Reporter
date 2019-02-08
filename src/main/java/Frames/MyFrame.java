package Frames;

import Utilz.SqlProperties;


import javax.swing.*;
import java.awt.*;
import java.util.Map;


public class MyFrame extends JFrame {
    private ProccessesPanelTab proccessesPanel;
    private ResultView resultView = new ResultView();
    private JTextArea description = new JTextArea();

    public   MyFrame (String title, Map<SqlProperties,Boolean> statusMap){
        super(title);
        //Setlayout manager
        setLayout(new BorderLayout());
        //Add swing components to content pane
        Container c = getContentPane();
        proccessesPanel=new ProccessesPanelTab();
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        JSplitPane right = setHorizontalSplitPanel();
        jSplitPane.setResizeWeight(0.5);
        jSplitPane.setRightComponent(right);
        jSplitPane.setLeftComponent(proccessesPanel);
        BorderLayout bl = new BorderLayout();
        c.setLayout(bl);
        c.add(jSplitPane,BorderLayout.CENTER);
        c.add(new JLabel("Contact e-mail: albtutanota@tutanota.com"),BorderLayout.SOUTH);


      }

       public ProccessesPanelTab getProccessesPanel() {
        return proccessesPanel;
    }
       public ResultView getTextArea() { return resultView;}
       public JTextArea getDescriptionArea(){return description;}

        private JSplitPane setHorizontalSplitPanel(){
            JSplitPane jSplitPaneInternal = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
            jSplitPaneInternal.setTopComponent(resultView);
            description.setLineWrap(true);
            description.setPreferredSize(new Dimension(300,100));
            JScrollPane jsc = new JScrollPane();
            jsc.setViewportView(description);
            jSplitPaneInternal.setBottomComponent(jsc);
            return jSplitPaneInternal;

        }
    private JPanel setRightSplitPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(resultView,BorderLayout.NORTH);
        //description.setLineWrap(true);
        panel.add(description,BorderLayout.SOUTH);
        return panel;
    }
}
