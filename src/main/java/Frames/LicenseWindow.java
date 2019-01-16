package Frames;

import Controller.Controller;
import Utilz.BaseConstants;
import Utilz.Printer;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import static Utilz.Printer.printRowToMonitor;

public class LicenseWindow extends JFrame {
    private JLabel textLabel = new JLabel("<html>The period of use of the software has expired. Enter the new key.<br>Contact e-mail: albtutanota@tutanota.com<html>");
    private JLabel logginLabel = new JLabel(" Please, enter here new  key: ");
    private JTextField logginText = new JTextField(60);
    private JButton logginButton   = new JButton("Enter key");




    public LicenseWindow() throws HeadlessException {
        super ("License window");
        setSize(new Dimension(400,150));
        setLayout(new BorderLayout());
        Container c= getContentPane();
        c.add(textLabel,BorderLayout.NORTH);
        textLabel.setHorizontalAlignment(0);
        c.add(logginLabel,BorderLayout.WEST);
        c.add(logginText,BorderLayout.CENTER);
        c.add (logginButton,BorderLayout.SOUTH);
        //c.add(new JLabel("Contact e-mail: albtutanota@tutanota.com"),BorderLayout.SOUTH);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        logginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printRowToMonitor("Button clicked!!!");
                if (Controller.checkKey(logginText.getText())){
                    saveKey(logginText.getText());
                    Controller.init(BaseConstants.isIsZip());
                }
                dispose();
            }
        });

    }

    private void saveKey(String text) {
        if (BaseConstants.isIsZip()) {
            String zipFilePath = BaseConstants.getInstance().getZipFileSQL();
            FileHeader fHeader = null;
            ZipFile zipFile = null;
            try {
                zipFile = new ZipFile(zipFilePath);
                if (zipFile.isEncrypted()) {
                    zipFile.setPassword(BaseConstants.getInstance().getZipPsw());
                }
                fHeader = zipFile.getFileHeader(BaseConstants.getLiesencePath());
                zipFile.removeFile(fHeader);

            } catch (ZipException e) {
                e.printStackTrace(); Printer.saveLogFile(e); ;
            }
            try (InputStream is = new ByteArrayInputStream(text.getBytes("utf-8"))) {
                ZipParameters zp = new ZipParameters();
                zp.setSourceExternalStream(true);
                zp.setFileNameInZip(fHeader.getFileName());
                zp.setPassword(BaseConstants.getInstance().getZipPsw());
                zipFile.addStream(is, zp);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace(); Printer.saveLogFile(e); ;
            } catch (IOException e) {
                e.printStackTrace(); Printer.saveLogFile(e); ;
            } catch (ZipException e) {
                e.printStackTrace(); Printer.saveLogFile(e); ;
            }
        }
        else{
            try (BufferedWriter bfw = new BufferedWriter(new FileWriter(BaseConstants.getLiesencePath())))
            {
                bfw.write(text);
            } catch (FileNotFoundException e) {
                e.printStackTrace(); Printer.saveLogFile(e); ;
            } catch (IOException e) {
                e.printStackTrace(); Printer.saveLogFile(e); ;
            }

        }
    }


}
