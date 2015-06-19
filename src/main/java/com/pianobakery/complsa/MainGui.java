package com.pianobakery.complsa;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.plaf.FileChooserUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
    private JButton newFolderButton;
    private JButton selectFolderButton;
    private JList impDocList;
    private JButton addTopicCorpusButton;
    private JButton removeTopicCorpusButton;
    private JButton impNextButton;
    private JPanel setupPanel;
    private JTextField wDirText;
    private JCheckBox withMetadataCheckBox;
    private JCheckBox splitParagraphsCheckBox;
    private JCheckBox translateToCheckBox;
    private JComboBox comboBox1;
    private JComboBox selectTrainCorp;
    private JPanel searchDocs;
    private JButton removeCorpusButton;
    private DefaultListModel listModel;
    private File[] files;
    private File wDir;

    private static JMenuBar menuBar;
    private static JMenu menu, submenu;
    private static JMenuItem menuItem;
    private static JMenuItem newProjFolderMenuItem;
    private static JRadioButtonMenuItem rbMenuItem;
    private static JCheckBoxMenuItem cbMenuItem;
    private static MainGui maingui;




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
        maingui = new MainGui();

        frame.setContentPane(maingui.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        int frameWidth = 1024;
        int frameHeight = 800;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((int) screenSize.getWidth()/2 - frameWidth/2, (int) screenSize.getHeight()/4 - frameHeight/4, frameWidth, frameHeight);
        JMenuBar menu = MenuExp();
        frame.setJMenuBar(menu);
        frame.setVisible(true);

    }


    //Main Gui Constructor
    public MainGui() {




        //Disable all Components as long as wDir is not set.
        enableUIElements(false);


        newFolderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createNewProjectFolder();

            }
        });

        selectFolderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseNewProjectFolder();


            }
        });

        addTopicCorpusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    AddCorpusDialog dialog = new AddCorpusDialog();
                    dialog.showDialog();

                    /*JFileChooser chooser = new JFileChooser();
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

                    }*/


                } catch(Exception ex){
                    JOptionPane.showMessageDialog(null, "Falsche Eingabe");
                }

            }
        });
        removeTopicCorpusButton.addActionListener(new ActionListener() {
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
                            parser.parseDocToXhtml(f);
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



    //Menubar
    public static JMenuBar MenuExp() {



            // Creates a menubar for a JFrame
            JMenuBar menuBar = new JMenuBar();

            // Add the menubar to the frame
            //setJMenuBar(menuBar);

            // Define and add two drop down menu to the menubar
            JMenu fileMenu = new JMenu("Project");
            JMenu editMenu = new JMenu("Edit");
            menuBar.add(fileMenu);
            menuBar.add(editMenu);

            // Create and add simple menu item to one of the drop down menu
            JMenuItem newAction = new JMenuItem("New Folder");
            JMenuItem openAction = new JMenuItem("Choose Folder");
            JMenuItem exitAction = new JMenuItem("Exit");
            JMenuItem cutAction = new JMenuItem("Cut");
            JMenuItem copyAction = new JMenuItem("Copy");
            JMenuItem pasteAction = new JMenuItem("Paste");

            // Create and add CheckButton as a menu item to one of the drop down
            // menu
            JCheckBoxMenuItem checkAction = new JCheckBoxMenuItem("Check Action");
            // Create and add Radio Buttons as simple menu items to one of the drop
            // down menu
            JRadioButtonMenuItem radioAction1 = new JRadioButtonMenuItem(
                    "Radio Button1");
            JRadioButtonMenuItem radioAction2 = new JRadioButtonMenuItem(
                    "Radio Button2");
            // Create a ButtonGroup and add both radio Button to it. Only one radio
            // button in a ButtonGroup can be selected at a time.
            ButtonGroup bg = new ButtonGroup();
            bg.add(radioAction1);
            bg.add(radioAction2);
            fileMenu.add(newAction);
            fileMenu.add(openAction);
            fileMenu.add(checkAction);
            fileMenu.addSeparator();
            fileMenu.add(exitAction);
            editMenu.add(cutAction);
            editMenu.add(copyAction);
            editMenu.add(pasteAction);
            editMenu.addSeparator();
            editMenu.add(radioAction1);
            editMenu.add(radioAction2);
            // Add a listener to the New menu item. actionPerformed() method will
            // invoked, if user triggred this menu item
            newAction.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    maingui.createNewProjectFolder();
                    System.out.println("You have clicked on the new action");
                }
            });
            openAction.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    maingui.chooseNewProjectFolder();
                    System.out.println("You have clicked on the new action");
                }
            });
            exitAction.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    System.exit(0);
                    System.out.println("You have clicked on the new action");
                }
            });

            return menuBar;
    }




    //ProjectFolder
    public void createNewProjectFolder(){

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
            chooser.setSelectedFile(new File("Workingfile"));
            frame.getContentPane().add(chooser);
            chooser.setApproveButtonText("Choose");

            //Disable Save as
            ArrayList<JPanel> jpanels = new ArrayList<JPanel>();
            for(Component c : chooser.getComponents()){
                if( c instanceof JPanel ){
                    jpanels.add((JPanel)c);
                }

            }
            jpanels.get(0).getComponent(0).setVisible(false);
            frame.pack();
            frame.setLocationRelativeTo(null);

            int whatChoose =  chooser.showSaveDialog(null);
            if(whatChoose == JFileChooser.APPROVE_OPTION ) {
                String path = chooser.getCurrentDirectory().toString();
                wDirText.setText(path);
                wDir = chooser.getCurrentDirectory();
                System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                System.out.println("getSelectedFile() : " +  chooser.getSelectedFile());
                System.out.println("WDir is: " + wDir.toString());
                enableUIElements(true);
            }


            } catch(Exception ex){
            JOptionPane.showMessageDialog(null, "Falsche Eingabe");
            }
    }

    public void chooseNewProjectFolder(){

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
                String text = chooser.getSelectedFile().toString();
                wDirText.setText(text);
                wDir = chooser.getSelectedFile();
                System.out.println("WDir is: " + wDir.toString());
                System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                System.out.println("getSelectedFile() : " +  chooser.getSelectedFile());
                enableUIElements(true);
            }
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, "Falsche Eingabe");
        }


    }


    //Enable and Disable UI Conditionally until wDir is set
    private void enableUIElements(boolean enabled) {
        //enable all Components as long as wDir is not set.

        addTopicCorpusButton.setEnabled(enabled);
        removeTopicCorpusButton.setEnabled(enabled);
        selectTrainCorp.setEnabled(enabled);
        removeCorpusButton.setEnabled(enabled);
        impNextButton.setEnabled(enabled);

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
