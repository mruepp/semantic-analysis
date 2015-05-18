package com.pianobakery.complsa;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Created by michael on 14.05.15.
 */
public class importParse {
    //private static DefaultListModel listModel;
    private JPanel importDocPanel;
    private JList impDocList;
    private DefaultListModel listModel;
    private JButton addDocumentButton;
    private JButton removeDocumentButton;
    private JButton nextButton;
    private JScrollPane scrollPane;
    private File[] files;


    //Getter and Setter
    public DefaultListModel getListModel() {
        return listModel;
    }

    public void setListModel(DefaultListModel listModel) {
        this.listModel = listModel;
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public JList getImpDocList() {
        return impDocList;
    }

    public void setImpDocList(JList impDocList) {
        this.impDocList = impDocList;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainImport");
        frame.setContentPane(new importParse().importDocPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.pack();
        int frameWidth = 800;
        int frameHeight = 600;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((int) screenSize.getWidth()/2 - frameWidth/2, (int) screenSize.getHeight()/4 - frameHeight/4, frameWidth, frameHeight);
        frame.setVisible(true);

    }

    public importParse() {

        addDocumentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setMultiSelectionEnabled(true);
                          int whatChoose =  chooser.showOpenDialog(null);

                    if(whatChoose == JFileChooser.APPROVE_OPTION ) {
                        files = chooser.getSelectedFiles();


                        for(File f : files){
                            listModel.addElement(f.toString ());
                        }


                        System.out.println("Dateipfad ist: " + Arrays.toString(files));
                    }


                } catch(Exception ex){
                    JOptionPane.showMessageDialog(null, "Falsche Eingabe");
                }

            }


        });


        removeDocumentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {


                if(impDocList.getSelectedIndices().length > 0) {
                    int[] selectedIndices = impDocList.getSelectedIndices();
                    System.out.println("Remove selected Indices: " + Arrays.toString(selectedIndices));
                    for (int i = selectedIndices.length - 1; i >= 0; i--) {
                        listModel.removeElementAt(selectedIndices[i]);
                    }
                }

                } catch(Exception ex){
                    JOptionPane.showMessageDialog(null, "Falsche Eingabe");

                }
                System.out.println("Elements still there: " + listModel.toString());
            }
        });
    }


    private void createUIComponents() {
        this.setListModel(new DefaultListModel());
        System.out.println("Liste ist: " + getListModel().toString());

        this.setImpDocList(new JList(this.getListModel()));
        this.getImpDocList().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.getImpDocList().setSelectedIndex(0);
        //this.getImpDocList().addListSelectionListener(this);
        this.getImpDocList().setVisibleRowCount(12);


        System.out.println("Jlist ist: " + this.getImpDocList().toString());

        // TODO: place custom component creation code here
    }
}




