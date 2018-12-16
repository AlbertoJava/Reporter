import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class View extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTabbedPane tabbedPane1;

    public View() {

        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        jSplitPane.setRightComponent(new JButton("Right"));
        jSplitPane.setLeftComponent(new JButton("Left"));
        add(jSplitPane);
/*
        //setContentPane(contentPane);
        *//*модальность - блоктрует/неблокирует другие окна приложения*//*
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);*/
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        View dialog = new View();
        dialog.pack();
        dialog.setVisible(true);
        //System.exit(0);
        System.out.println("dsfsdf");
    }

}
