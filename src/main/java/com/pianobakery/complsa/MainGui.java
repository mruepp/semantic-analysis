package com.pianobakery.complsa;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by michael on 16.05.15.
 */
public class MainGui {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JButton newButton;
    private JButton selectButton;
    private JList impDocList;
    private JButton addDocumentsButton;
    private JButton removeDocumentsButton;
    private JButton impNextButton;
    private JPanel impDocPanel;
    private JTextField wDirText;
    private JCheckBox withMetadataCheckBox;
    private JCheckBox splitParagraphsCheckBox;
    private JCheckBox translateToCheckBox;
    private JComboBox comboBox1;
    private DefaultListModel listModel;
    private File[] files;
    private File wDir;




    //Getter and Setter
    public JList getImpDocList() {
        return impDocList;
    }

    public void setImpDocList(JList impDocList) {
        this.impDocList = impDocList;
    }

    public JTabbedPane getTabbedPane1() {
        return tabbedPane1;
    }

    public void setTabbedPane1(JTabbedPane tabbedPane1) {
        this.tabbedPane1 = tabbedPane1;
    }

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

    public JTextField getwDirText() {
        return wDirText;
    }

    public void setwDirText(JTextField wDirText) {
        this.wDirText = wDirText;
    }

    public File getwDir() {
        return wDir;
    }

    public void setwDir(File wDir) {
        this.wDir = wDir;
    }

    //Main
    public static void main(String[] args) {
        JFrame frame = new JFrame("MainGui");
        frame.setContentPane(new MainGui().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        int frameWidth = 1024;
        int frameHeight = 800;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((int) screenSize.getWidth()/2 - frameWidth/2, (int) screenSize.getHeight()/4 - frameHeight/4, frameWidth, frameHeight);
        frame.setVisible(true);
    }




    //Main Gui
    public MainGui() {
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    JFrame frame = new JFrame();
                    JFileChooser chooser = new JFileChooser();

                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
                    chooser.setDialogTitle("Create Working Folder");
                    chooser.setFileHidingEnabled(Boolean.TRUE);
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setAcceptAllFileFilterUsed(false);
                    chooser.setDialogType(JFileChooser.SAVE_DIALOG);

                    frame.getContentPane().add(chooser);

                    //int whatChoose =  chooser.showSaveDialog(null);
                    //Disable Save as Dialog
                    ArrayList<JPanel> jpanels = new ArrayList<JPanel>();

                    for(Component c : chooser.getComponents()){
                        if( c instanceof JPanel ){
                            jpanels.add((JPanel)c);
                        }
                    }

                    jpanels.get(0).getComponent(0).setVisible(false);

                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    //frame.setVisible(false);
                    chooser.setApproveButtonText("Choose");
                    int whatChoose =  chooser.showSaveDialog(null);
                    //int whatChoose = chooser.getApp


                            System.out.println("getSelectedFileNew() : " +  chooser.getSelectedFile());

                    if(whatChoose == JFileChooser.APPROVE_OPTION ) {
                        System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                        System.out.println("getSelectedFile() : " +  chooser.getSelectedFile());
                        String text = chooser.getCurrentDirectory().toString();

                        System.out.println("WDir is: " + text);

                        wDirText.setText(text);

                        wDir = chooser.getCurrentDirectory();

                        System.out.println("Dateipfad ist: " + Arrays.toString(files));


                        System.out.println("Dateipfad ist: " + Arrays.toString(files));
                    }


                } catch(Exception ex){
                    JOptionPane.showMessageDialog(null, "Falsche Eingabe");
                }


            }
        });
        selectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {

                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
                    chooser.setDialogTitle("Create Working Folder");
                    chooser.setFileHidingEnabled(Boolean.TRUE);
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setAcceptAllFileFilterUsed(false);
                    int whatChoose =  chooser.showOpenDialog(null);

                    if(whatChoose == JFileChooser.APPROVE_OPTION ) {
                        System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                        System.out.println("getSelectedFile() : " +  chooser.getSelectedFile());
                        String text = chooser.getSelectedFile().toString();

                        System.out.println("WDir is: " + text);

                        wDirText.setText(text);

                        wDir = chooser.getSelectedFile();



                    }


                } catch(Exception ex){
                    JOptionPane.showMessageDialog(null, "Falsche Eingabe");
                }

            }
        });
        addDocumentsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
                    chooser.setDialogTitle("Add Documents");
                    chooser.setMultiSelectionEnabled(true);
                    int whatChoose =  chooser.showOpenDialog(null);

                    if(whatChoose == JFileChooser.APPROVE_OPTION ) {
                        files = chooser.getSelectedFiles();
                        System.out.println("Dateipfad ist: " + Arrays.toString(files));
                        for(File f : files){
                            listModel.addElement(f.toString ());
                        }

                    }


                } catch(Exception ex){
                    JOptionPane.showMessageDialog(null, "Falsche Eingabe");
                }

            }
        });
        removeDocumentsButton.addActionListener(new ActionListener() {
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
        impNextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {




                    if (wDir != null && files != null) {


                        for (File f : files){

                            Parser parser = new Parser();
                            try {
                                parser.parseDocToXhtml(f, wDir);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            } catch (SAXException e1) {
                                e1.printStackTrace();
                            } catch (TikaException e1) {
                                e1.printStackTrace();
                            }
                            //parser.setwDir(wDir);
                            //parser.setFiles(files);
                            //System.out.println(parser);

                        }


                        //JOptionPane.showMessageDialog(null, parser.parseDocsToXhtml());


                    }
                    else if (wDir == null && files == null) {
                        JOptionPane.showMessageDialog(null, "Kein Working Folder und Dokumente ausgewählt!");
                    }
                    else if (wDir == null){
                        JOptionPane.showMessageDialog(null, "Kein Working Folder ausgewählt!");
                    }
                    else if (files == null){
                        JOptionPane.showMessageDialog(null, "Keine Dokumente Folder ausgewählt!");
                    }







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
    }
}
