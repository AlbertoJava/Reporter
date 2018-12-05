package Frames;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class DetailsPanel extends JPanel {
    private EventListenerList listenerList = new EventListenerList();


    public DetailsPanel() {
        Dimension size=getPreferredSize();
        size.width=250;
        setPreferredSize(size);
        setBorder(BorderFactory.createTitledBorder("Personal Details"));
        JLabel nameLabel = new JLabel("Name: ");
        JLabel occupationLabel = new JLabel("Occupation: ");

        JTextField nameField = new JTextField(10);
        JTextField occupationField = new JTextField(10);

        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String occupation = occupationField.getText();

                String  text = name + ": " + occupation + "\n";
                fireDetailEvent(new DetailEvent(this,text));
            }
        });

        setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();

        /// First Column
        gc.anchor = GridBagConstraints.LINE_END;//выровнять в право в столбце
        gc.weightx=0.5;
        gc.weighty=0.5;

        gc.gridx=0;
        gc.gridy=0;
        add(nameLabel,gc);

        gc.gridx=0;
        gc.gridy=1;
        add (occupationLabel,gc);

        ////Second column
        gc.anchor=GridBagConstraints.LINE_START;//выравнять в лево в столбце
        gc.gridx=1;
        gc.gridy=0;
        add(nameField,gc);

        gc.gridx=1;
        gc.gridy=1;
        add(occupationField,gc);

        //Final Row
        gc.weighty=10;

        gc.anchor=GridBagConstraints.FIRST_LINE_START;
        gc.gridx=1;
        gc.gridy=2;
        add (addBtn,gc);

    }
        /*пары - 1. - ссылка на класс, 2. сам объект */
    public void fireDetailEvent(DetailEvent event){
        add(new JTextArea("Test text area"));
        Object[] listeners = listenerList.getListenerList();
        for (int i=0;i<listeners.length;i+=2){
            if (listeners[i]== DetailListener.class){
                ((DetailListener)listeners[i+1]).detailEventOccurred(event);
            }
        }
    }

    public void addDetailListener (DetailListener detailListener){
        listenerList.add(DetailListener.class,detailListener);

    }
    public void removeDetailListener (DetailListener listener){
        listenerList.remove(DetailListener.class,listener);

    }

}
