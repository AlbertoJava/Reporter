package Frames;

import Utilz.SqlProperties;

import javax.swing.*;
import java.awt.*;
import java.util.Map;


public class MyFrame extends JFrame {
    private ProccessesPanelTab proccessesPanel;
    private ResultView resultView = new ResultView();

    public MyFrame(String title, Map<SqlProperties, Boolean> statusMap) {
        super(title);
        //Setlayout manager
        setLayout(new BorderLayout());
        //Add swing components to content pane
        Container c = getContentPane();
        c.add(resultView, BorderLayout.CENTER);
        proccessesPanel = new ProccessesPanelTab();
        c.add(proccessesPanel, BorderLayout.WEST);
    }

    public ResultView getTextArea() {
        return resultView;
    }

    public ProccessesPanelTab getProccessesPanel() {
        return proccessesPanel;
    }
}
