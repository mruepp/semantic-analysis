package com.pianobakery.complsa;

import com.license4j.License;
import com.license4j.ValidationStatus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;
import pitt.search.semanticvectors.Search;
import pitt.search.semanticvectors.*;
import pitt.search.semanticvectors.vectors.*;
import pitt.search.semanticvectors.vectors.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;





/**
 * Created by michael ruepp on 16.05.15.
 */
public class MainGui {
    private static JMenuBar menuBar;
    private static JMenu menu, submenu;
    private static JMenuItem menuItem;
    private static JMenuItem newProjFolderMenuItem;
    private static JRadioButtonMenuItem rbMenuItem;
    private static JCheckBoxMenuItem cbMenuItem;

    private JTabbedPane tabbedPane1;
    private JPanel setupPanel;
    private JPanel searchDocs;
    private JPanel mainPanel;

    private JButton newFolderButton;
    private JButton selectFolderButton;
    private JButton trainCorpButton;
    private JButton addTopicCorpusButton;
    private JButton removeTopicCorpusButton;
    private JButton downloadModelButton;
    private JButton updateIndexButton;
    private JButton removeIndexButton;
    private JButton searchButton;
    private JButton impSearchCorpButton;
    private JButton removeSearchCorpButton;

    private JCheckBox addCorpRecursiveCheckBox;
    private JCheckBox createChunksCheckBox;
    private JCheckBox impSearchCorpRecursiveCheckBox;
    private JCheckBox splitSearchCorpCheckBox;

    private JTextField wDirText;
    private JTextField amountOfSentencesPerTextField;
    private JTextField posIndRadiusTextField;
    private JTextField amountSearchCorpSent;

    private JComboBox selectTrainCorp;
    private JComboBox termComboBox;
    private JComboBox indexTypeComboBox;
    private JComboBox searchCorpComboBox;

    private JComboBox selectIndexTypeComboBox;
    private JComboBox selectTermweightComboBox;
    private Map<String, List<String>> searchModelList = new LinkedHashMap<String, List<String>>();

    private JLabel langModelsText;

    private JTextArea searchTextArea;

    private JRadioButton searchSearchCorpRadioButton;
    private JRadioButton searchTopCorpRadioButton;

    private JTextField noOfSearchResultsText;
    private JRadioButton selTextRadioButton;
    private JLabel algTextField;
    private JRadioButton selDocRadioButton;
    private JButton selectDocumentButton;
    private JTextArea metadataTextField;
    private JTable termSearchResTable;
    private JTable docSearchResTable;
    private JLabel searchDocValue;
    private JButton openSearchDocumentButton;
    private JScrollPane docTablePane;
    private JScrollPane termTablePane;
    private JButton showsSelFileTerms;

    private String[] docSearchTitles;
    private DocSearchModel docSearchResModel;
    private String[] termSearchTitles;
    private DefaultTableModel termSearchResModel;

    private ReaderGui reader;
    private ReaderGui searchDocReader;
    private int docSelCounter;
    private List<File> selDocdirContent;

    private String searchFileString;



    //private DefaultListModel listModel;

    private File[] files;
    private File wDir;
    private HashMap<String, File> trainCorp;
    private HashMap<String, File> trainSentModels;
    private HashMap<String, File> indexFilesModel;
    private HashMap<String, File> searchCorpusModel;

    private static MainGui maingui;
    private static JFrame frame;
    private static String topicFolder = "TopicCorp";
    private static String searchFolder = "SearchCorp";
    private static String trainModelFolder = "TrainModels";
    private static String modelUrl = "http://opennlp.sourceforge.net/models-1.5";
    private static String[] indexType= {"Standard","LSA", "Positional"};
    private static String[] termweights= {"None", "IDF", "LOGENTROPY", "SQRT"};
    private static String emptyTable = "No Search Result...";


    private static JMenuItem newAction = new JMenuItem("New Folder");
    private static JMenuItem openAction = new JMenuItem("Choose Folder");
    //private static JMenuItem downloadAction = new JMenuItem("Download Language Model");
    private static JMenuItem exitAction = new JMenuItem("Exit");

    private static JMenuItem cutAction = new JMenuItem("Cut");
    private static JMenuItem copyAction = new JMenuItem("Copy");
    private static JMenuItem pasteAction = new JMenuItem("Paste");

    private static JMenuItem addCorpFolderAction = new JMenuItem("Add Folder");
    private static JMenuItem remCorpFolderAction = new JMenuItem("Remove Folder");
    private static JMenuItem updateIndexAction = new JMenuItem("Update Index");
    private static JMenuItem remCorpIndexAction = new JMenuItem("Remove Corpus Index");
    private static JMenuItem trainSemAction = new JMenuItem("Train Semantics");

    private static JMenuItem addSearchCorpFolderAction = new JMenuItem("Add Folder");
    private static JMenuItem remSearchCorpFolderAction = new JMenuItem("Remove Folder");

    private static JMenuItem openReaderAction = new JMenuItem("Open Reader");
    private static JMenuItem closeReaderAction = new JMenuItem("Close Reader");
    private static JMenuItem searchAction = new JMenuItem("Search");

    private static JMenuItem helpAction = new JMenuItem("Help");
    private static JMenuItem licenseAction = new JMenuItem("License");
    private static JMenuItem aboutAction = new JMenuItem("About");


    //private static File openFolder = new File(System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "complsaTestData");
    private static File openFolder = new File(System.getProperty("user.home"));

    final static Logger logger = Logger.getLogger(MainGui.class);

    private LicenseKeyGUI licenseKeyGUI;



    //Getter and Setter
    public JTabbedPane getTabbedPane1() {
        return tabbedPane1;
    }

    public void setTabbedPane1(JTabbedPane tabbedPane1) {
        this.tabbedPane1 = tabbedPane1;
    }


    public int getSelectedDocTableRow() {



        int row = docSearchResTable.getSelectedRow();
        logger.debug("Row Selected: " + row);

        if (row > docSearchResModel.getRowCount() - 1 || row < 0) {
            return -1;
        }

        return row;

    }

    public void setSelectedDocTableRow(int theRow) {

        logger.debug("Row Set: " + theRow);

        if (theRow < 0 || theRow >= docSearchResModel.getRowCount()) {
            return;
        }

        if (docSearchResTable.getRowCount() != 0) {
            docSearchResTable.setRowSelectionInterval(theRow,theRow);
        }

    }

    public String[]getSelectedTermTableWords() {
        List<String> theStrings = new ArrayList<String>();

        int[] theRows = termSearchResTable.getSelectedRows();
        if (theRows.length != 0) {
            for (int aRow : theRows) {
                //String aString = (String)termSearchResTable.getValueAt(aRow,1);
                int modelRow = termSearchResTable.convertRowIndexToModel(aRow);
                String aString = (String)termSearchResTable.getModel().getValueAt(modelRow,1);
                theStrings.add(aString);
            }

        }
        String[] theStringArr = new String[theStrings.size()];
        theStringArr = theStrings.toArray(theStringArr);

        return theStringArr;




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




    //Runtime Parameter
    public void runtimeParameters() {
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        List<String> aList = bean.getInputArguments();
        long heapSize = Runtime.getRuntime().totalMemory();
        logger.info("Heap Size = " + heapSize);
        long heapSizeMax = Runtime.getRuntime().maxMemory();
        System.out.println("Heap Size max= " + heapSize);

        for (int i = 0; i < aList.size(); i++) {
            logger.info("Runtime Infos: " + aList.get(i));
        }
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
        JMenu trainingMenu = new JMenu("Corpus");
        JMenu searchMenu = new JMenu("Search");
        JMenu viewMenu = new JMenu("View");
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(trainingMenu);
        menuBar.add(searchMenu);

        menuBar.add(helpMenu);

        newAction.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        openAction.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        exitAction.setAccelerator(KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

        cutAction.setAccelerator(KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        copyAction.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        pasteAction.setAccelerator(KeyStroke.getKeyStroke('V', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

        addCorpFolderAction.setAccelerator(KeyStroke.getKeyStroke('1', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        updateIndexAction.setAccelerator(KeyStroke.getKeyStroke('2', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        trainSemAction.setAccelerator(KeyStroke.getKeyStroke('3', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

        addSearchCorpFolderAction.setAccelerator(KeyStroke.getKeyStroke('4', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        openReaderAction.setAccelerator(KeyStroke.getKeyStroke('R', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        closeReaderAction.setAccelerator(KeyStroke.getKeyStroke('W', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        searchAction.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));


        fileMenu.add(newAction);
        fileMenu.add(openAction);
        fileMenu.addSeparator();
        //fileMenu.add(downloadAction);
        fileMenu.addSeparator();
        fileMenu.add(exitAction);


        editMenu.add(cutAction);
        editMenu.add(copyAction);
        editMenu.add(pasteAction);
        //editMenu.addSeparator();


        trainingMenu.add(addCorpFolderAction);
        trainingMenu.add(remCorpFolderAction);
        trainingMenu.addSeparator();
        trainingMenu.add(updateIndexAction);
        trainingMenu.add(remCorpIndexAction);
        trainingMenu.addSeparator();
        trainingMenu.add(trainSemAction);


        trainingMenu.add(addSearchCorpFolderAction);
        trainingMenu.add(remSearchCorpFolderAction);

        searchMenu.add(searchAction);
        searchMenu.add(openReaderAction);
        searchMenu.add(closeReaderAction);


        helpMenu.add(helpAction);
        helpMenu.add(licenseAction);
        helpMenu.addSeparator();
        helpMenu.add(aboutAction);



        // Create and add CheckButton as a menu item to one of the drop down
        // menu
        //JCheckBoxMenuItem checkAction = new JCheckBoxMenuItem("Check Action");
        // Create and add Radio Buttons as simple menu items to one of the drop
        // down menu
        /*JRadioButtonMenuItem radioAction1 = new JRadioButtonMenuItem(
                "Radio Button1");
        JRadioButtonMenuItem radioAction2 = new JRadioButtonMenuItem(
                "Radio Button2");
                */
        // Create a ButtonGroup and add both radio Button to it. Only one radio
        // button in a ButtonGroup can be selected at a time.
        //ButtonGroup bg = new ButtonGroup();
        //bg.add(radioAction1);
        //bg.add(radioAction2);
        //editMenu.add(radioAction1);
        //editMenu.add(radioAction2);


        // Add a listener to the New menu item. actionPerformed() method will
        // invoked, if user triggred this menu item
        newAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                maingui.newFolderMethod();
            }
        });
        openAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                maingui.selectFolderMethod();
            }
        });
        /*downloadAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                maingui.downloadModelMethod();
            }
        });*/
        exitAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });

        addCorpFolderAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maingui.addTopicCorpusMethod();
            }
        });
        remCorpFolderAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maingui.removeTopicCorpusMethod();
            }
        });
        updateIndexAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maingui.updateIndexMethod();
            }
        });
        remCorpIndexAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maingui.removeIndexMethod();
            }
        });
        trainSemAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maingui.trainCorpMethod();
            }
        });

        addSearchCorpFolderAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maingui.impSearchCorpMethod();
            }
        });
        remSearchCorpFolderAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maingui.removeSearchCorpMethod();
            }
        });

        searchAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maingui.searchMethod();
            }
        });
        openReaderAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maingui.openReaderMethod();
            }
        });
        closeReaderAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maingui.closeReaderMethod();
            }
        });

        helpAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maingui.helpMethod();
            }
        });

        licenseAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maingui.licenseMethod();
            }
        });

        aboutAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maingui.aboutMethod();
            }
        });





        return menuBar;
    }

    //Main
    public static void main(String[] args) {
        // take the menu bar off the jframe
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        // set the name of the application menu item
        //System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Semantic Analysis");

        // set the look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }


        //StartUI
        frame = new JFrame("MainGui");
        maingui = new MainGui();


        frame.setContentPane(maingui.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setTitle("Semantic Search");
        int frameWidth = 1280;
        int frameHeight = 800;
        frame.setMinimumSize(new Dimension(frameWidth, frameHeight));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((int) screenSize.getWidth() / 2 - frameWidth / 2, (int) screenSize.getHeight() / 4 - frameHeight / 4, frameWidth, frameHeight);
        JMenuBar menu = MenuExp();
        frame.setJMenuBar(menu);


        //Sets Button as always selected
        //JRootPane rootPane = SwingUtilities.getRootPane(maingui.searchButton);
        //rootPane.setDefaultButton(maingui.searchButton);

        frame.setVisible(true);



    }

    //Main Gui Constructor
    public MainGui() {

        runtimeParameters();
        trainCorp = new HashMap<String, File>();
        trainSentModels = new HashMap<String, File>();
        indexFilesModel = new HashMap<String, File>();
        searchCorpusModel = new HashMap<String, File>();
        selDocdirContent = new ArrayList<File>();
        langModelsText.setText("None");
        posIndRadiusTextField.setEnabled(false);
        //Disable all Components as long as wDir is not set.
        enableUIElements(false);

        ButtonGroup selSearchGroup =new ButtonGroup();
        selSearchGroup.add(searchSearchCorpRadioButton);
        selSearchGroup.add(searchTopCorpRadioButton);

        ButtonGroup searchSelGroup = new ButtonGroup();
        searchSelGroup.add(selTextRadioButton);
        searchSelGroup.add(selDocRadioButton);

        licenseKeyGUI = new LicenseKeyGUI(frame, true);

        //Added to get the docSearchTable the focus when opening the Reader without selecting something so up down button will work
        frame.addWindowFocusListener(new WindowAdapter() {
            public void windowGainedFocus(WindowEvent e) {
                docSearchResTable.requestFocusInWindow();
            }
        });

        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "CTRL + S");
        frame.getRootPane().getActionMap().put("CTRL + S", runSearch());


        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        //Needs to get a white background in the termtableview (only in windows)
        termTablePane.getViewport().setBackground(Color.WHITE);

        //Project Page
        //Project Folder
        newFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newFolderMethod();


            }
        });

        selectFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFolderMethod();


            }
        });


        //Download Language Models
        downloadModelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                downloadModelMethod();


            }
        });


        //Add-Remove Topic Corpus
        addTopicCorpusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addTopicCorpusMethod();



            }
        });

        selectTrainCorp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                frame.setTitle("Selected Training Corpus: " + selectTrainCorp.getSelectedItem() + " and " + "Sel. Search Corpus: " + searchCorpComboBox.getSelectedItem());
                algTextField.setText("Knowledge Corpus: " + selectTrainCorp.getSelectedItem());

                try {
                    updateIndexFileFolder();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        removeTopicCorpusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) throws ArrayIndexOutOfBoundsException {
                removeTopicCorpusMethod();


            }

        });


        //Update and remove Index
        updateIndexButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateIndexMethod();


            }
        });

        removeIndexButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeIndexMethod();

            }
        });


        //Train Semantic
        trainCorpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trainCorpMethod();


            }
        });

        indexTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                if (indexTypeComboBox.getSelectedIndex() == 2) {
                    logger.debug("Enable indexType Combo with Index: " + indexTypeComboBox.getSelectedIndex());

                    posIndRadiusTextField.setEnabled(true);
                    return;
                }
                logger.debug("Disable indexType Combo with Index: " + indexTypeComboBox.getSelectedIndex());
                posIndRadiusTextField.setEnabled(false);

            }
        });


        //Import and remove Search Corpora
        impSearchCorpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                impSearchCorpMethod();

            }
        });

        searchCorpComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setTitle("Selected Training Corpus: " + selectTrainCorp.getSelectedItem() + " and " + "Sel. Search Corpus: " + searchCorpComboBox.getSelectedItem());
            }
        });

        removeSearchCorpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) throws ArrayIndexOutOfBoundsException {
                removeSearchCorpMethod();

            }

        });




        //Search Page
        //Choose Index Type
        selectIndexTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (searchModelList.isEmpty()) {
                    return;
                }

                if (searchModelList.get(selectIndexTypeComboBox.getSelectedItem()) == null) {
                    return;
                }

                List<String> theList = searchModelList.get(selectIndexTypeComboBox.getSelectedItem().toString());
                selectTermweightComboBox.removeAllItems();
                for (String aTFItem : theList) {
                    selectTermweightComboBox.addItem(aTFItem);
                }

                getSelectedSearchModelFiles();


            }
        });

        selectTermweightComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (searchModelList.isEmpty()) {
                    return;
                }

                if (searchModelList.get(selectIndexTypeComboBox.getSelectedItem()) == null) {
                    return;
                }
                getSelectedSearchModelFiles();
            }
        });


        //Select Search Type
        selTextRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectDocumentButton.setEnabled(false);
                searchTextArea.setEnabled(true);
                //searchDocValue.setText("nothing selected");

            }
        });

        selDocRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectDocumentButton.setEnabled(true);
                searchTextArea.setText(null);
                searchTextArea.setEnabled(false);

            }
        });

        selectDocumentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importSearchFile();
                openSearchDocumentButton.setEnabled(true);

            }
        });

        openSearchDocumentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (searchDocReader == null) {
                    searchDocReader = getReaderLater(null, maingui);
                    searchDocReader.setSearchTerms(getSelectedTermTableWords());
                } else if (!searchDocReader.getFrameVisible()) {
                    searchDocReader.setFrameVisible(true);
                    searchDocReader.setSearchTerms(getSelectedTermTableWords());
                }
                searchDocReader.setDocumentText(searchFileString);
                searchDocReader.setSelFullDocLabel(searchDocValue.toString());
                searchDocReader.setViewPane(2);
                searchDocReader.disableComponents();
                searchDocReader.setSearchTerms(getSelectedTermTableWords());

            }
        });

        //Search Button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchMethod();



            }


        });

        //Table Listeners
        docSearchResTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table = (JTable) me.getSource();
                Point p = me.getPoint();
                int row = docSearchResTable.rowAtPoint(p);
                if (docSearchResModel == null || docSearchResModel.getRowCount() == 0 || docSearchResModel.getDocFile(0).getFileName().equals(emptyTable)){
                    return;
                }

                switch (me.getClickCount()) {
                    case 1:
                        if (row == -1) {
                            break;
                        }
                        if (docSearchResTable.getRowCount() > 0) {
                            logger.debug("Single click Doc: " + ((DocSearchModel) docSearchResTable.getModel()).getDocSearchFile(row).getFile().toString());
                            fillMetaDataField(((DocSearchModel) docSearchResTable.getModel()).getDocSearchFile(row).getFile());
                            if (reader != null) {
                                reader.setSearchTerms(getSelectedTermTableWords());
                            }
                            if (searchDocReader != null) {
                                searchDocReader.setSearchTerms(getSelectedTermTableWords());
                            }
                            setSelReaderContent();
                            setDocReaderContent(0);
                            if (reader != null) {
                                reader.setSearchTerms(getSelectedTermTableWords());
                            }
                            if (searchDocReader != null) {
                                searchDocReader.setSearchTerms(getSelectedTermTableWords());
                            }

                        }
                        break;
                    case 2:
                        if (row == -1) {
                            break;
                        }
                        if (docSearchResTable.getRowCount() > 0) {
                            logger.debug("Double click Doc: " + ((DocSearchModel) docSearchResTable.getModel()).getDocSearchFile(row).getFile().toString());
                            fillMetaDataField(((DocSearchModel) docSearchResTable.getModel()).getDocSearchFile(row).getFile());

                            if (reader == null) {
                                reader = getReaderLater((DocSearchModel) docSearchResTable.getModel(), maingui);
                                reader.setSearchTerms(getSelectedTermTableWords());
                            } else if (!reader.getFrameVisible()) {
                                reader.setFrameVisible(true);
                                reader.setSearchTerms(getSelectedTermTableWords());
                            }
                            setSelReaderContent();
                            setDocReaderContent(0);
                            if (reader != null) {
                                reader.setSearchTerms(getSelectedTermTableWords());
                            }
                            if (searchDocReader != null) {
                                searchDocReader.setSearchTerms(getSelectedTermTableWords());
                            }
                        }
                        break;
                }

            }
        });

        termSearchResTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table = (JTable) me.getSource();
                Point p = me.getPoint();
                int row = docSearchResTable.rowAtPoint(p);
                if (termSearchResTable.getModel() == null || termSearchResTable.getRowCount() == 0 || termSearchResTable.getModel().getValueAt(row, 1).equals(emptyTable)){
                    return;
                }
                switch (me.getClickCount()) {
                    case 1:
                        logger.debug("Single click Term: " + termSearchResTable.getModel().getValueAt(row, 1));
                        logger.debug("Selected Terms: " + Arrays.toString(getSelectedTermTableWords()));
                        if (reader != null) {
                            reader.setSearchTerms(getSelectedTermTableWords());

                        }
                        if (searchDocReader != null) {
                            searchDocReader.setSearchTerms(getSelectedTermTableWords());
                        }
                        break;
                    case 2:
                        logger.debug("Double click Term: " + termSearchResTable.getModel().getValueAt(row, 1));
                        if (reader != null) {
                            reader.setSearchTerms(getSelectedTermTableWords());
                        }
                        if (searchDocReader != null) {
                            searchDocReader.setSearchTerms(getSelectedTermTableWords());
                        }
                        break;

                }

            }
        });

        docSearchResTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (docSearchResModel == null || docSearchResModel.getRowCount() == 0 || docSearchResModel.getDocFile(0).getFileName().equals(emptyTable)) {
                    return;
                }
                if (docSearchResTable.getSelectedRow() > -1) {
                    // print first column value from selected row
                    logger.debug("KeyboardSelection: " + ((DocSearchModel) docSearchResTable.getModel()).getDocSearchFile(docSearchResTable.convertRowIndexToModel(docSearchResTable.getSelectedRow())).getFile().toString());
                    fillMetaDataField(((DocSearchModel) docSearchResTable.getModel()).getDocSearchFile(docSearchResTable.convertRowIndexToModel(docSearchResTable.getSelectedRow())).getFile());
                    setSelReaderContent();
                    setDocReaderContent(0);
                    if (reader != null) {
                        reader.setSearchTerms(getSelectedTermTableWords());
                    }
                    if (searchDocReader != null) {
                        searchDocReader.setSearchTerms(getSelectedTermTableWords());
                    }


                }
            }
        });

        termSearchResTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (termSearchResTable.getModel() == null || termSearchResTable.getRowCount() == 0 || termSearchResTable.getModel().getValueAt(0, 1).equals(emptyTable)){
                    return;
                }
                if (termSearchResTable.getSelectedRow() > -1) {
                    // print first column value from selected row
                    if (reader != null) {
                        reader.setSearchTerms(getSelectedTermTableWords());
                    }
                    if (searchDocReader != null) {
                        searchDocReader.setSearchTerms(getSelectedTermTableWords());
                    }


                }

            }
        });

    }



    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        /**
         * COPY THIS METHOD.
         *
         *
         * We will check license here in "formWindowOpened" so that license
         * window will be displayed after user will see main product window.
         *
         * Depending license status, we will display license window.
         *
         * If license on disk is not valid, display license window. ALSO it is
         * good to disable some features or menu items like below; so that user
         * will not be able to use product without a valid license. OR software
         * may be directly closed with an error.
         */
        License license = licenseKeyGUI.checkLicense();

        if (license != null) {
            if (license.getValidationStatus() == ValidationStatus.LICENSE_VALID) {
                /**
                 * License is valid, so run your software product.
                 */

                /**
                 * But If license require activation, check if license is
                 * activated. If license is not activated check the activation
                 * period. If allowed activation period is expired but user
                 * still did not complete activation, display license GUI for
                 * user to complete activation.
                 */
                if (license.isActivationRequired() && license.getLicenseActivationDaysRemaining(null) == 0) {
                    JOptionPane.showMessageDialog(null, "Your license activation period is over, activate on the next window.", "License Activation", JOptionPane.INFORMATION_MESSAGE);

                    // This is an example, and we just disable main file menu.
                    newAction.setEnabled(false);
                    openAction.setEnabled(false);
                    newFolderButton.setEnabled(false);
                    selectFolderButton.setEnabled(false);

                    licenseKeyGUI.setVisible(true);
                }
            } else {
                /**
                 * If license status is not valid, display message to display
                 * license status; and disable some software features etc.
                 */
                JOptionPane.showMessageDialog(null, "Your license is not valid (" + license.getValidationStatus() + ")", "License Error", JOptionPane.INFORMATION_MESSAGE);

                // This is an example, and we just disable main file menu.
                newAction.setEnabled(false);
                openAction.setEnabled(false);
                newFolderButton.setEnabled(false);
                selectFolderButton.setEnabled(false);

                licenseKeyGUI.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(null, "There is no valid license installed. Please buy one or activate Trial Mode.", "License Error", JOptionPane.INFORMATION_MESSAGE);

            // This is an example, and we just disable main file menu.
            newAction.setEnabled(false);
            openAction.setEnabled(false);
            newFolderButton.setEnabled(false);
            selectFolderButton.setEnabled(false);

            licenseKeyGUI.setVisible(true);
        }
    }//GEN-LAST:event_formWindowOpened




    //ActionListener Methods
    public void newFolderMethod(){
        clearSelections();
        createNewProjectFolder();
        try {
            addExistingSentModelsToMap();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            loadTopicCorp();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            loadSearchCorp();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
       /* try {
           updateIndexFileFolder();

        } catch (IOException e1) {
            e1.printStackTrace();
        }*/

    }

    public void selectFolderMethod() {
        clearSelections();
        chooseNewProjectFolder();
        try {
            addExistingSentModelsToMap();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            loadTopicCorp();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            loadSearchCorp();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        /*try {
            updateIndexFileFolder();
        } catch (IOException e1) {
            e1.printStackTrace();
        }*/
    }

    public void downloadModelMethod() {
        logger.debug("Download Models");

        try {
            if (testURL(modelUrl)) {
                downloadModelTaskWithBar(getProgressBarWithTitleLater("Download Model Files...",true));
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(null, "Connection Timeout - check your Internet connection");
        }
    }

    public void addTopicCorpusMethod() {

        logger.debug("Numeric?: " + StringUtils.isNumeric(amountOfSentencesPerTextField.getText()));
        logger.debug("Chunk selected: " + createChunksCheckBox.isSelected());


        if (createChunksCheckBox.isSelected() && !StringUtils.isNumeric(amountOfSentencesPerTextField.getText())) {

            JOptionPane.showMessageDialog(null, "Enter Number");
            return;

        }
        if (trainSentModels.size() == 0) {
            JOptionPane.showMessageDialog(null, "Download Sentence Models first");
            return;
        }


        File folder = chooseAddCorpusFolder();

        if (folder != null) {

            File newDir = new File(wDir + File.separator + topicFolder + File.separator + folder.getName());
            logger.debug("Corpus Folder: " + folder.toString());
            logger.debug("Import Folder: " + newDir.toString());
            logger.debug("Working Folder : " + wDir.toString());
            logger.debug("Corpus Folder recursive is: " + addCorpRecursiveCheckBox.isSelected());

            //Create Corpus Folder
            if (!newDir.exists()) {

                logger.debug("Creating directory: " + newDir);
                boolean result = false;

                try {
                    FileUtils.forceMkdir(newDir);
                    addRemoveItemToTopicSearchBoxTaskWithBar(getProgressBarWithTitleLater("Please wait...", false), newDir, true, true);
                    //addRemoveItemToTopicBox(newDir, true, true);
                    result = true;
                } catch (SecurityException se) {
                    JOptionPane.showMessageDialog(null, "No permission or File Exists");

                    //return Boolean.FALSE;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if (result) {
                    logger.debug("DIR created");
                }
            } else {

                int result = JOptionPane.showConfirmDialog(new JFrame(), "Folder exists, add to your Topic Corpus? You have to re-train!");
                logger.debug("DIR not created");
                if (result == JOptionPane.NO_OPTION | result == JOptionPane.CANCEL_OPTION) {
                    return;
                }


            }

            //Run import
            if (folder != null) {
                addTopicCorpTaskWithBar(getProgressBarWithTitleLater("Add Topic Corpus", true), folder, newDir, addCorpRecursiveCheckBox.isSelected(), Integer.parseInt(amountOfSentencesPerTextField.getText()), createChunksCheckBox.isSelected());

            }

        }

    }

    public void removeTopicCorpusMethod() {


        File theFile = trainCorp.get(selectTrainCorp.getSelectedItem());

        int result = JOptionPane.showConfirmDialog(null, "Do you want to continue?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.NO_OPTION || result == JOptionPane.CANCEL_OPTION) {
            return;
        }

        if (theFile != null) {





                addRemoveItemToTopicSearchBoxTaskWithBar(getProgressBarWithTitleLater("Please wait...", false), theFile, false, true);
               //addRemoveItemToTopicBox(theFile, false, true);
                //updateIndexFileFolder();



        } else if (theFile == null) {

            try {
                selectTrainCorp.removeItemAt(0);
                System.out.printf("Items of selectTrainingCorp: " + selectTrainCorp.getItemAt(0));
                try {
                    updateIndexFileFolder();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            } catch (ArrayIndexOutOfBoundsException e2) {
                JOptionPane.showMessageDialog(null, "Keine Topic Corps mehr vorhanden");
            }

        }


    }

    public void updateIndexMethod() {
        if (selectTrainCorp.getSelectedItem() == null) {
            logger.debug("Corpus selection = null");
            return;
        }
        int wordRadius = 0;
        ProgressBar bar;
        bar = getProgressBarWithTitleLater("Train on selected Corpus", false);
        bar.setProgressBarIndeterminate(true);

        logger.debug("Train the Selected Corpus");


        logger.debug("Selected Corpus: " + selectTrainCorp.getSelectedItem().toString());
        logger.debug("Selected Corpus Path: " + trainCorp.get(selectTrainCorp.getSelectedItem().toString()));
        File corpDir = trainCorp.get(selectTrainCorp.getSelectedItem().toString());
        trainTopicCorpTaskWithBar(bar, corpDir, wordRadius, indexTypeComboBox.getSelectedIndex(), termComboBox.getSelectedItem().toString(), true);

    }

    public void removeIndexMethod() {
        if (selectTrainCorp.getSelectedItem() == null) {
            logger.debug("Corpus selection = null");
            return;
        }
        File corpDir = trainCorp.get(selectTrainCorp.getSelectedItem().toString());
        File indexFolderParent = new File(wDir + File.separator + SemanticParser.getLucIndexParentDirName());
        File indexFolder = new File(wDir + File.separator + SemanticParser.getLucIndexParentDirName() + File.separator + corpDir.getName());

        logger.debug("The Indexfolder to be deleted: " + indexFolder);
        boolean isChild = false;
        try {
            isChild = isSubDirectory(indexFolderParent, indexFolder);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if (isChild && indexFolder.exists()) {
            try {
                FileUtils.deleteDirectory(indexFolder);

            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null, "Unable to delete Index Folder");
                return;
            }
            JOptionPane.showMessageDialog(null, "Index deleted");
        } else {
            JOptionPane.showMessageDialog(null, "Unable to delete, no Index existing");
        }


    }

    public void trainCorpMethod() {

        File theIndexFileFolder = new File(wDir + File.separator + SemanticParser.getLucIndexParentDirName() + File.separator + trainCorp.get(selectTrainCorp.getSelectedItem()).getName().toString());
        logger.debug("The Index File Folder Path: " + theIndexFileFolder);

        if (!theIndexFileFolder.exists()) {
            JOptionPane.showMessageDialog(null, "Update Index first");
            return;
        }


        int wordRadius = 0;


        if (indexTypeComboBox.getSelectedIndex() == 2 && (posIndRadiusTextField.getText().isEmpty() || !StringUtils.isNumeric(posIndRadiusTextField.getText()) || Integer.parseInt(posIndRadiusTextField.getText()) == 0)) {


            JOptionPane.showMessageDialog(null, "Enter Number");
            return;

        } else if (indexTypeComboBox.getSelectedIndex() == 2 && StringUtils.isNumeric(posIndRadiusTextField.getText())) {
            wordRadius = Integer.parseInt(posIndRadiusTextField.getText());
            logger.debug("Position Radius Field is: " + posIndRadiusTextField.getText());
        }

        ProgressBar bar;
        bar = getProgressBarWithTitleLater("Train on selected Corpus", false);
        bar.setProgressBarIndeterminate(true);

        logger.debug("Train the Selected Corpus");

        if (selectTrainCorp.getSelectedItem() == null) {
            logger.debug("Corpus selection = null");
            return;
        }
        logger.debug("Selected Corpus: " + selectTrainCorp.getSelectedItem().toString());
        logger.debug("Selected Corpus Path: " + trainCorp.get(selectTrainCorp.getSelectedItem().toString()));
        File corpDir = trainCorp.get(selectTrainCorp.getSelectedItem().toString());


        trainTopicCorpTaskWithBar(bar, corpDir, wordRadius, indexTypeComboBox.getSelectedIndex(), termComboBox.getSelectedItem().toString(), false);

    }

    public void impSearchCorpMethod() {

        logger.debug("Numeric?: " + StringUtils.isNumeric(amountSearchCorpSent.getText()));
        logger.debug("Chunk selected: " + splitSearchCorpCheckBox.isSelected());


        if (splitSearchCorpCheckBox.isSelected() && !StringUtils.isNumeric(amountSearchCorpSent.getText())) {

            JOptionPane.showMessageDialog(null, "Enter Number");
            return;

        }


        File folder = chooseAddCorpusFolder();

        if (folder != null) {

            File newDir = new File(wDir + File.separator + searchFolder + File.separator + folder.getName());
            logger.debug("Corpus Folder: " + folder.toString());
            logger.debug("Import Folder: " + newDir.toString());
            logger.debug("Working Folder : " + wDir.toString());
            logger.debug("Corpus Folder recursive is: " + impSearchCorpRecursiveCheckBox.isSelected());

            //Create Corpus Folder
            if (!newDir.exists()) {

                logger.debug("Creating directory: " + newDir);
                boolean result = false;

                try {
                    FileUtils.forceMkdir(newDir);
                    addRemoveItemToTopicSearchBoxTaskWithBar(getProgressBarWithTitleLater("Please wait...", false), newDir, true, false);
                    //addRemoveItemToTopicBox(newDir, true, false);
                    result = true;
                } catch (SecurityException se) {
                    JOptionPane.showMessageDialog(null, "No permission or File Exists");

                    //return Boolean.FALSE;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if (result) {
                    logger.debug("DIR created");
                }
            } else {

                int result = JOptionPane.showConfirmDialog(new JFrame(), "Folder exists, add to your Search Corpus?");
                logger.debug("DIR not created");
                if (result == JOptionPane.NO_OPTION | result == JOptionPane.CANCEL_OPTION) {
                    return;
                }


            }

            //Run import
            if (folder != null) {
                addTopicCorpTaskWithBar(getProgressBarWithTitleLater("Add Topic Corpus", true), folder, newDir, addCorpRecursiveCheckBox.isSelected(), Integer.parseInt(amountSearchCorpSent.getText()),splitSearchCorpCheckBox.isSelected());

            }

        }

    }

    public void removeSearchCorpMethod() {
        File theFile = searchCorpusModel.get(searchCorpComboBox.getSelectedItem());

        int result = JOptionPane.showConfirmDialog(null, "Do you want to continue?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.NO_OPTION || result == JOptionPane.CANCEL_OPTION) {
            return;
        }

        if (theFile != null) {
            addRemoveItemToTopicSearchBoxTaskWithBar(getProgressBarWithTitleLater("Please wait...", false), theFile, false, false);
            //addRemoveItemToTopicBox(theFile, false, false);
        } else if (theFile == null) {

            try {
                searchCorpComboBox.removeItemAt(0);
                System.out.printf("Items of select Search Corps: " + searchCorpComboBox.getItemAt(0));

            } catch (ArrayIndexOutOfBoundsException e2) {
                JOptionPane.showMessageDialog(null, "Keine Search Corps mehr vorhanden");
            }

        }

    }

    public void openReaderMethod() {


        if (docSearchResModel == null || docSearchResModel.getRowCount() == 0 || docSearchResModel.getDocFile(0).getFileName().equals(emptyTable)){
            JOptionPane.showMessageDialog(null, "Unable to open Text - No Search Result");
            return;
        }

        if (docSearchResTable.getSelectedRow() == -1){
            //docSearchResTable.setRowSelectionInterval(0, 0);
            docSearchResTable.changeSelection(0,0,false,false);
        }



        if (docSearchResTable.getRowCount() > 0) {


            if (reader == null) {
                reader = getReaderLater((DocSearchModel) docSearchResTable.getModel(), maingui);
                reader.setSearchTerms(getSelectedTermTableWords());
            } else if (!reader.getFrameVisible()) {
                reader.setFrameVisible(true);
                reader.setSearchTerms(getSelectedTermTableWords());
            }
            setSelReaderContent();
            setDocReaderContent(0);
            if (reader != null) {
                reader.setSearchTerms(getSelectedTermTableWords());
            }
            if (searchDocReader != null) {
                searchDocReader.setSearchTerms(getSelectedTermTableWords());
            }
        }



    }

    public void closeReaderMethod() {
        if (reader != null && reader.getFrameVisible()) {
            reader.setFrameVisible(false);
        }
    }

    public void searchMethod() {

        if (!searchAction.isEnabled()) {
            return;
        }

        if (searchTextArea.getText().isEmpty() && selTextRadioButton.isSelected()) {
            JOptionPane.showMessageDialog(null, "Enter Search Terms");
            return;
        }

        if ((searchFileString == null || searchFileString.isEmpty()) && selDocRadioButton.isSelected()) {
            JOptionPane.showMessageDialog(null, "Import Search Document");
            return;
        }
        if (searchTopCorpRadioButton.isSelected() && (selectIndexTypeComboBox.getItemCount() == 0 || selectTrainCorp.getItemCount() == 0)) {
            JOptionPane.showMessageDialog(null, "Import Training Corpus and train it");
            return;
        }
        if (searchSearchCorpRadioButton.isSelected() && searchCorpComboBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(null, "Import Search Corpus first");
            return;
        }


        if (searchCorpComboBox.getItemCount() != 0 && searchSearchCorpRadioButton.isSelected() && selectIndexTypeComboBox.getItemCount() != 0) {

            if (selDocRadioButton.isSelected()) {
                logger.debug("run Doc search on Search Corpus");
                ProgressBar bar = getProgressBarWithTitleLater("Search Document Similarities...", false);
                //File corpDir = new File(wDir + File.separator + searchFolder + File.separator + selectTrainCorp.getSelectedItem());
                compareCorpDocsWithSearchDocTaskWithBar(bar);
            } else if (selTextRadioButton.isSelected()) {
                logger.debug("run Text search on Search Docs");
                ProgressBar bar1 = getProgressBarWithTitleLater("Search Text Similarities...", false);
                //File corpDir = new File(wDir + File.separator + searchFolder + File.separator + selectTrainCorp.getSelectedItem());
                compareCorpDocsWithSearchDocTaskWithBar(bar1);

            }


        } else if ((selectTrainCorp.getItemCount() != 0) && searchTopCorpRadioButton.isSelected() && selectIndexTypeComboBox.getItemCount() != 0) {

            if (selDocRadioButton.isSelected()) {
                logger.debug("Run Doc search on Topic Corpus");
                ProgressBar bar = getProgressBarWithTitleLater("Calculate Terms...", false);
                searchDocInTopicCorpTaskWithBar(bar);

                ProgressBar bar2 = getProgressBarWithTitleLater("Calculate Terms...", false);
                searchTermInTopicCorpTaskWithBar(bar2);

                //searchTermInTopicCorp();

            } else if (selTextRadioButton.isSelected()) {
                logger.debug("Run Text search on Topic Corp");
                ProgressBar bar = getProgressBarWithTitleLater("Calculate Terms...", false);
                searchDocInTopicCorpTaskWithBar(bar);

                ProgressBar bar2 = getProgressBarWithTitleLater("Calculate Terms...", false);
                searchTermInTopicCorpTaskWithBar(bar2);
                //searchTermInTopicCorp();
            }

        }


    }

    public void helpMethod() {

        //Get file from resources folder

        InfoPane thePane = getInfoPaneLater("Licenses", Disclaimer.allLicenses());

    }

    public void licenseMethod() {

        //Get file from resources folder

        licenseKeyGUI.setVisible(true);

    }

    public void aboutMethod() {

        //Get file from resources folder

        InfoPane thePane = getInfoPaneLater("Licenses", Disclaimer.allLicenses());

    }


    private Action runSearch() {
        return new AbstractAction("The Search") {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("The Search");
                searchMethod();
            }
        };
    }

    //ProjectFolder
    public void createNewProjectFolder() {

        try {
            JFrame frame = new JFrame();
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(openFolder);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            //chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
            chooser.setDialogTitle("Create Folder");
            chooser.setFileHidingEnabled(Boolean.TRUE);
            chooser.setMultiSelectionEnabled(false);
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            chooser.setSelectedFile(new File("Workingfile"));
            frame.getContentPane().add(chooser);
            chooser.setApproveButtonText("Choose");

            //Disable Save as
            ArrayList<JPanel> jpanels = new ArrayList<JPanel>();
            for (Component c : chooser.getComponents()) {
                if (c instanceof JPanel) {
                    jpanels.add((JPanel) c);
                }

            }
            jpanels.get(0).getComponent(0).setVisible(false);
            frame.pack();
            frame.setLocationRelativeTo(null);

            int whatChoose = chooser.showSaveDialog(null);
            if (whatChoose == JFileChooser.APPROVE_OPTION) {
                File selFile = chooser.getSelectedFile();
                File currDir = chooser.getCurrentDirectory();
                Path parentDir = Paths.get(chooser.getCurrentDirectory().getParent());
                String parentDirName = parentDir.getFileName().toString();

                logger.debug("Chooser SelectedFile: " + selFile.toString());
                logger.debug("getCurrentDirectory(): " + currDir.toString());
                logger.debug("Chooser parentdir: " + parentDir);
                logger.debug("Parentdirname: " + parentDirName);

                if (selFile.getName().equals(parentDirName)) {
                    wDir = currDir;
                } else {
                    wDir = chooser.getSelectedFile();
                }

                logger.debug("WDIR is: " + wDir.toString());
                wDirText.setText(wDir.toString());
                enableUIElements(true);
            }


        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Falsche Eingabe");
            logger.debug("Exeption: " + ex.toString());
        }
    }

    public void chooseNewProjectFolder() {

        try {

            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(openFolder);
            //chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
            chooser.setDialogTitle("Choose Folder");
            chooser.setFileHidingEnabled(Boolean.TRUE);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setMultiSelectionEnabled(false);
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setApproveButtonText("Choose");
            int whatChoose = chooser.showOpenDialog(null);

            if (whatChoose == JFileChooser.APPROVE_OPTION) {
                String text = chooser.getSelectedFile().toString();
                wDirText.setText(text);
                wDir = chooser.getSelectedFile();
                logger.debug("WDir is: " + wDir.toString());
                logger.debug("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                logger.debug("getSelectedFile() : " + chooser.getSelectedFile());
                enableUIElements(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Falsche Eingabe");
        }


    }

    public void importSearchFile() {
        searchDocValue.setText("loading...");

        try {
            File selected;
            searchFileString = "";
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
            chooser.setDialogTitle("Choose Search File");
            chooser.setFileHidingEnabled(Boolean.TRUE);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setMultiSelectionEnabled(false);
            chooser.setAcceptAllFileFilterUsed(false);
            int whatChoose = chooser.showOpenDialog(null);
            if (whatChoose == JFileChooser.APPROVE_OPTION) {
                selected = chooser.getSelectedFile();
                logger.debug("AddCorpDir is: " + selected.toString());
                logger.debug("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                logger.debug("getSelectedFile() : " + chooser.getSelectedFile());
                enableUIElements(true);
                if (selected.exists()) {

                    Parser parser = new Parser(selected);
                    try {
                        parser.parseDocToPlainText();

                        searchFileString = Utilities.removeQuoteFromString(parser.getPlainText());
                        logger.debug("The Search File: " + searchFileString);
                        searchDocValue.setText(selected.getParentFile().getName() + File.separator + selected.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (TikaException e) {
                        e.printStackTrace();
                    }
                }

            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Falsche Eingabe");
        }


    }

    public File chooseAddCorpusFolder() {

        try {

            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
            chooser.setDialogTitle("Create Working Folder");
            chooser.setFileHidingEnabled(Boolean.TRUE);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setMultiSelectionEnabled(false);
            chooser.setAcceptAllFileFilterUsed(false);
            int whatChoose = chooser.showOpenDialog(null);
            File selected;
            if (whatChoose == JFileChooser.APPROVE_OPTION) {
                selected = chooser.getSelectedFile();
                logger.debug("AddCorpDir is: " + selected.toString());
                logger.debug("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                logger.debug("getSelectedFile() : " + chooser.getSelectedFile());
                enableUIElements(true);

                    return selected;


            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Falsche Eingabe");
        }

        return null;
    }

    //Enable and Disable UI Conditionally until wDir is set
    private void enableUIElements(boolean enabled) {
        //enable all Components as long as wDir is not set.

        addTopicCorpusButton.setEnabled(enabled);
        removeTopicCorpusButton.setEnabled(enabled);
        selectTrainCorp.setEnabled(enabled);
        trainCorpButton.setEnabled(enabled);
        addCorpRecursiveCheckBox.setEnabled(enabled);
        createChunksCheckBox.setEnabled(enabled);
        amountOfSentencesPerTextField.setEnabled(enabled);
        downloadModelButton.setEnabled(enabled);
        updateIndexButton.setEnabled(enabled);
        termComboBox.setEnabled(enabled);
        indexTypeComboBox.setEnabled(enabled);
        removeIndexButton.setEnabled(enabled);
        searchTextArea.setEnabled(enabled);
        selectIndexTypeComboBox.setEnabled(enabled);
        impSearchCorpButton.setEnabled(enabled);
        impSearchCorpRecursiveCheckBox.setEnabled(enabled);
        splitSearchCorpCheckBox.setEnabled(enabled);
        amountSearchCorpSent.setEnabled(enabled);
        searchCorpComboBox.setEnabled(enabled);
        removeSearchCorpButton.setEnabled(enabled);
        searchSearchCorpRadioButton.setEnabled(enabled);
        searchTopCorpRadioButton.setEnabled(enabled);
        searchButton.setEnabled(enabled);
        selectTermweightComboBox.setEnabled(enabled);
        noOfSearchResultsText.setEnabled(enabled);
        selTextRadioButton.setEnabled(enabled);
        selDocRadioButton.setEnabled(enabled);

        //downloadAction.setEnabled(enabled);

        cutAction.setEnabled(enabled);
        copyAction.setEnabled(enabled);
        pasteAction.setEnabled(enabled);

        addCorpFolderAction.setEnabled(enabled);
        remCorpFolderAction.setEnabled(enabled);
        updateIndexAction.setEnabled(enabled);
        remCorpIndexAction.setEnabled(enabled);
        trainSemAction.setEnabled(enabled);

        addSearchCorpFolderAction.setEnabled(enabled);
        remSearchCorpFolderAction.setEnabled(enabled);

        searchAction.setEnabled(enabled);
        openReaderAction.setEnabled(enabled);
        closeReaderAction.setEnabled(enabled);


    }

    //Manual UI Construction
    private void createUIComponents() {

        this.indexTypeComboBox = new JComboBox(indexType);
        this.termComboBox = new JComboBox(termweights);



        //docSearchTitles = new String[]{"%Similarity","Path","Show"};
        docSearchResModel = new DocSearchModel();
        docSearchResTable = new JTable(docSearchResModel);
        docSearchResTable.setShowHorizontalLines(false);
        docSearchResTable.setShowVerticalLines(true);
        docSearchResTable.setFillsViewportHeight(true);
        docSearchResTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        docSearchResTable.setShowGrid(false);
        docSearchResTable.setGridColor(Color.DARK_GRAY);
        docSearchResTable.setAutoscrolls(true);
        docSearchResTable.getColumn("%Similarities:").setPreferredWidth(100);
        docSearchResTable.getColumn("%Similarities:").setWidth(25);
        docSearchResTable.getColumn("Filename:").setPreferredWidth(600);
        docSearchResTable.getColumn("Filename:").setWidth(100);
        docSearchResTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        docSearchResTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);






        termSearchTitles = new String[]{"%Similarities:","Terms:"};
        termSearchResModel = new DefaultTableModel(termSearchTitles, 0 ){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        termSearchResTable = new JTable(termSearchResModel);
        termSearchResTable.setShowVerticalLines(true);
        termSearchResTable.setShowHorizontalLines(false);
        termSearchResTable.setFillsViewportHeight(true);
        termSearchResTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        termSearchResTable.setShowGrid(false);
        termSearchResTable.setGridColor(Color.DARK_GRAY);
        termSearchResTable.setAutoscrolls(true);
        termSearchResTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        termSearchResTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        termSearchResTable.getColumnModel().getColumn(0).setWidth(80);
        termSearchResTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        termSearchResTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        termSearchResTable.getColumnModel().getColumn(1).setWidth(120);





        //docSearchResTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        //docSearchResTable.getColumnModel().getColumn(1).sizeWidthToFit();











    }

    public void addTopicCorpTaskWithBar(ProgressBar bar, File folder, File newDir,boolean sel, int amSent, boolean areChunks) {

        addTopicCorpTask task = new addTopicCorpTask(bar, folder, newDir, sel, amSent, areChunks);
        logger.debug("Runs");
        task.execute();
    }

    class addTopicCorpTask extends SwingWorker<Integer, Integer> {
        private ProgressBar bar;
        private File folder;
        private File newDir;
        private boolean sel;
        private int amSent;
        private Collection<File> files = null;
        private boolean chunks;

        public addTopicCorpTask(ProgressBar aBar, File aFolder, File aNewDir, boolean aSel, int amSent, boolean areChunks) {
            this.bar = aBar;
            this.folder = aFolder;
            this.newDir = aNewDir;
            this.sel = aSel;
            this.amSent = amSent;
            this.chunks = areChunks;

        }

        @Override
        public Integer doInBackground() {
            logger.debug("Folder: " + folder.toString());
            logger.debug("Folder newDir: " + newDir.toString());
            logger.debug("Runs");
            //int amSent = Integer.parseInt(amountOfSentencesPerTextField.getText());
            logger.debug("amSent=" + amSent);

            //boolean chunks = createChunksCheckBox.isSelected();


            if (sel) {
                files = FileUtils.listFiles(folder, FileFileFilter.FILE, DirectoryFileFilter.DIRECTORY);
            }else if (!sel){
                files = FileUtils.listFiles(folder, FileFileFilter.FILE, FalseFileFilter.FALSE);
            }


            /*for (File aFile : files) {
                logger.debug("A Filda: " + aFile.toString());
            }
            logger.debug("Create Chunks: " + chunks + " and how much: " + amSent);
            */

            Integer amount = files.size();
            bar.setProgressBarMax(amount);
            bar.setTextField("");


            for (File file : files ) {
                bar.setTextField(file.getName().toString());

                Parser ps = new Parser(file);
                Boolean result = null;
                try {
                    result = ps.saveDocToWorkingDirFolder(newDir, chunks, amSent, trainSentModels);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (result == false) {
                    logger.debug("Error happened");

                }
                amount--;
                bar.setProgressBarValue(amount);
                if (bar.getButtonCancel()) break;

            }
            //trainCorp.put(folder.getName(), folder);
            //selectTrainCorp.addItem(folder.getName().toString());
            //logger.debug("TrainCorp Map: " + trainCorp.get(folder.getName()));
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            logger.debug("Done");
            Toolkit.getDefaultToolkit().beep();
            bar.dispose();
            JOptionPane.showMessageDialog(null, "Import completed");

        }
    }

    public ProgressBar getProgressBarWithTitleLater(String title, boolean withCancelButton){
        final ProgressBar bar = new ProgressBar(withCancelButton);
        bar.setTitle(title);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                bar.setLocationRelativeTo(frame);
                bar.setProgressBarValue(0);
                bar.pack();
                bar.setVisible(true);

            }

        });
        return bar;
    }

    public ReaderGui getReaderLater(DocSearchModel aModel, MainGui maingui){
        final ReaderGui reader = new ReaderGui(aModel,maingui);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {


                reader.showGui();


            }

        });
        return reader;
    }

    public InfoPane getInfoPaneLater(String title, String text) {
        final InfoPane infoPane = new InfoPane(title,text);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {


                infoPane.showGui();


            }

        });
        return infoPane;

    }

    public void setDocReaderContent(int addRem) {

        File theSelFile;
        File theParentFolder;

        if (addRem == 0) {
            if (selDocdirContent.size() != 0) {
                selDocdirContent.clear();
            }

            docSelCounter = -1;
            int row = docSearchResTable.convertRowIndexToModel(docSearchResTable.getSelectedRow());
            logger.debug("Selected Doc row: " + row + " RowCount: " + docSearchResTable.getRowCount());

            DocSearchModel theModel = (DocSearchModel) docSearchResTable.getModel();
            theSelFile = theModel.getDocSearchFile(row).getFile();
            theParentFolder = new File(theSelFile.getParent());


           File[] dirContentArray = theParentFolder.listFiles(new FileFilter() {
               @Override
               public boolean accept(File pathname) {
                   if (pathname.isFile()) {
                       return true;
                   } else {
                   return false;
               }

           }});

            logger.debug("The Selected Doc File: " + theModel.getDocSearchFile(row).getFile());
            logger.debug("File Array: " + dirContentArray.length);

            selDocdirContent = new ArrayList<File>(Arrays.asList(dirContentArray));

            logger.debug("File List: " + selDocdirContent.toString());
            docSelCounter = selDocdirContent.indexOf(theSelFile);
            logger.debug("Docselcounter: " + docSelCounter);

            if((reader != null) && (docSelCounter > 0 ) && (docSelCounter < (selDocdirContent.size() -1))) {
                reader.setBeforeDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter - 1)));
                reader.setSelectedDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter)));
                reader.setAfterDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter + 1)));

                reader.setBeforeChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter - 1), wDir));
                reader.setSelChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter), wDir));
                reader.setAfterChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter + 1), wDir));
            } else if (docSelCounter == 0 && reader != null) {
                reader.setBeforeDocText("Reached Beginning of Text");;
                reader.setSelectedDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter)));
                reader.setAfterDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter + 1)));

                reader.setBeforeChunkLabel("None");
                reader.setSelChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter), wDir));
                reader.setAfterChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter + 1), wDir));
            } else if ((docSelCounter == (selDocdirContent.size() - 1)) && (reader != null)) {
                reader.setBeforeDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter - 1)));
                reader.setSelectedDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter)));
                reader.setAfterDocText("Reached End of Text");

                reader.setBeforeChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter - 1), wDir));
                reader.setSelChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter), wDir));
                reader.setAfterChunkLabel("None");
            }

        } else if(addRem == -1) {
            if (docSelCounter == 0) {
                return;
            }
            docSelCounter = docSelCounter - 1;
            logger.debug("Docselcounter: " + docSelCounter);
            if((reader != null) && (docSelCounter > 0 ) && (docSelCounter < (selDocdirContent.size() -1))) {
                reader.setBeforeDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter - 1)));
                reader.setSelectedDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter)));
                reader.setAfterDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter + 1)));

                reader.setBeforeChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter - 1), wDir));
                reader.setSelChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter), wDir));
                reader.setAfterChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter + 1), wDir));
            } else if (docSelCounter == 0 && reader != null) {
                reader.setBeforeDocText("Reached Beginning of Text");;
                reader.setSelectedDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter)));
                reader.setAfterDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter + 1)));

                reader.setBeforeChunkLabel("None");
                reader.setSelChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter), wDir));
                reader.setAfterChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter + 1), wDir));
            } else if ((docSelCounter == (selDocdirContent.size() - 1)) && (reader != null)) {
                reader.setBeforeDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter - 1)));
                reader.setSelectedDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter)));
                reader.setAfterDocText("Reached End of Text");

                reader.setBeforeChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter - 1), wDir));
                reader.setSelChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter), wDir));
                reader.setAfterChunkLabel("None");
            }


        } else if (addRem == 1) {
            if (docSelCounter == selDocdirContent.size() - 1) {
                return;
            }

            docSelCounter = docSelCounter + 1;
            logger.debug("Docselcounter: " + docSelCounter);
            if((reader != null) && (docSelCounter > 0 ) && (docSelCounter < (selDocdirContent.size() -1))) {
                reader.setBeforeDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter - 1)));
                reader.setSelectedDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter)));
                reader.setAfterDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter + 1)));

                reader.setBeforeChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter - 1), wDir));
                reader.setSelChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter), wDir));
                reader.setAfterChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter + 1), wDir));
            } else if (docSelCounter == 0 && reader != null) {
                reader.setBeforeDocText("Reached Beginning of Text");;
                reader.setSelectedDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter)));
                reader.setAfterDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter + 1)));

                reader.setBeforeChunkLabel("None");
                reader.setSelChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter), wDir));
                reader.setAfterChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter + 1), wDir));
            } else if ((docSelCounter == (selDocdirContent.size() - 1)) && (reader != null)) {
                reader.setBeforeDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter - 1)));
                reader.setSelectedDocText(Utilities.readFileToString(selDocdirContent.get(docSelCounter)));
                reader.setAfterDocText("Reached End of Text");

                reader.setBeforeChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter - 1), wDir));
                reader.setSelChunkLabel(Utilities.getRelFileName(selDocdirContent.get(docSelCounter), wDir));
                reader.setAfterChunkLabel("None");
            }

        }
        if (reader != null) {
            reader.setSearchTerms(getSelectedTermTableWords());
        }



    }

    public void setSelReaderContent() {

        int row = docSearchResTable.convertRowIndexToModel(docSearchResTable.getSelectedRow());
        logger.debug("Selected row: " + row + " RowCount: " + docSearchResTable.getRowCount());
        DocSearchModel theModel = (DocSearchModel) docSearchResTable.getModel();
        logger.debug("The Selected Doc File: " + theModel.getDocSearchFile(row).getFile());


        if((reader != null) && (row > 0 ) && (row < (docSearchResTable.getRowCount() -1))) {
            reader.setBeforeText(Utilities.readFileToString(theModel.getDocSearchFile(row - 1).getFile()));
            reader.setSelectedText(Utilities.readFileToString(theModel.getDocSearchFile(row).getFile()));
            reader.setAfterText(Utilities.readFileToString(theModel.getDocSearchFile(row + 1).getFile()));

            reader.setBeforeSelLabel(Utilities.getRelFileName(theModel.getDocSearchFile(row - 1).getFile(), wDir));
            reader.setSelLabel(Utilities.getRelFileName(theModel.getDocSearchFile(row).getFile(), wDir));
            reader.setAfterSelLabel(Utilities.getRelFileName(theModel.getDocSearchFile(row + 1).getFile(), wDir));
        } else if (row == 0 && reader != null) {
            reader.setBeforeText("Reached Beginning of Text");
            reader.setSelectedText(Utilities.readFileToString(theModel.getDocSearchFile(row).getFile()));
            reader.setAfterText(Utilities.readFileToString(theModel.getDocSearchFile(row + 1).getFile()));

            reader.setBeforeSelLabel("None");
            reader.setSelLabel(Utilities.getRelFileName(theModel.getDocSearchFile(row).getFile(), wDir));
            reader.setAfterSelLabel(Utilities.getRelFileName(theModel.getDocSearchFile(row + 1).getFile(), wDir));
        } else if ((row == (docSearchResTable.getRowCount() - 1)) && (reader != null)) {
            reader.setBeforeText(Utilities.readFileToString(theModel.getDocSearchFile(row - 1).getFile()));
            reader.setSelectedText(Utilities.readFileToString(theModel.getDocSearchFile(row).getFile()));
            reader.setAfterText("Reached End of Text");

            reader.setBeforeSelLabel(Utilities.getRelFileName(theModel.getDocSearchFile(row - 1).getFile(), wDir));
            reader.setSelLabel(Utilities.getRelFileName(theModel.getDocSearchFile(row).getFile(), wDir));
            reader.setAfterSelLabel("None");
        }

        if (reader != null) {

            File theSelFile = theModel.getDocSearchFile(row).getFile();
            File theParentFolder = new File(theSelFile.getParent());
            File[] dirContentArray = theParentFolder.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File current, String name) {

                    return new File(current, name).isFile();
                }

        });
            logger.debug("File Array: " + dirContentArray.toString());

            List <File> theFileList = new ArrayList<File>(Arrays.asList(dirContentArray));

            logger.debug("File List: " + theFileList.toString());

            StringBuilder theComplete = new StringBuilder();

            for (File aFile : theFileList) {

                String aString = Utilities.readFileToString(aFile);

                theComplete.append(aString + System.getProperty("line.separator"));
        }
            reader.setDocumentText(theComplete.toString());
            reader.setSelFullDocLabel(Utilities.getRelFileName(theParentFolder, wDir));
        }
        if (reader != null) {
            reader.setSearchTerms(getSelectedTermTableWords());
        }

    }

    /*
    public void addRemoveItemToTopicBox(File aFile, Boolean add, boolean isTopicCorp) throws IOException {

        if (isTopicCorp) {
            File theFile = trainCorp.get(aFile.getName());
            if (add && theFile == null) {
                trainCorp.put(aFile.getName(), aFile);
                selectTrainCorp.addItem(aFile.getName());
                logger.debug("Added Item to Map: " + trainCorp.get(aFile.getName()).toString());
                logger.debug("Added Item to Map - Mapsize: " + trainCorp.size());
            } else if (!add && theFile != null){

                logger.debug("Removed Item from Map: " + aFile.toString());
                logger.debug("Removed Item from Map - Mapsize: " + trainCorp.size());

                File parent = new File(wDir + File.separator + topicFolder);
                File indexParent = new File (wDir + File.separator + SemanticParser.getLucIndexParentDirName());
                File indexFolder = new File (indexParent + File.separator + aFile.getName());
                File indexFileParent = new File (wDir + File.separator + SemanticParser.getLuceneIndexFilesFolder() );
                File indexFileFolder = new File (indexFileParent + File.separator + aFile.getName());

                logger.debug("Parentfolder: " + parent.toString());
                logger.debug("Lucene Index Parent Folder: " + indexParent.toString() + " and Index Folder Path: " + indexFolder.toString());
                logger.debug("Lucene Index File Parent Folder: " + indexFileParent.toString() + " and Index File Folder Path: " + indexFileFolder.toString());

                boolean isIt = isSubDirectory(parent, aFile);
                boolean isIndexIt = isSubDirectory(indexParent, indexFolder);
                boolean isIndexFileIt = isSubDirectory(indexFileParent, indexFileFolder);

                int result = JOptionPane.showConfirmDialog(null, "Do you want to continue?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (isIt && result == JOptionPane.YES_OPTION && isIndexIt && isIndexFileIt) {

                    System.out.printf("Delete this Path: " + aFile.toString());

                    try {
                        FileUtils.deleteDirectory(aFile);
                        FileUtils.deleteDirectory(indexFolder);
                        FileUtils.deleteDirectory(indexFileFolder);


                    }catch (IOException e1) {

                        JOptionPane.showMessageDialog(null, "Unable to delete Folder");


                    }

                    trainCorp.remove(aFile.getName());
                    selectTrainCorp.removeItem(aFile.getName());
                    JOptionPane.showMessageDialog(null, "Deleted...");
                } else if (!isIt || result == JOptionPane.NO_OPTION) {
                    logger.debug("No delete, because cancel:" + aFile.toString());
                }
            }

        } else if (!isTopicCorp) {
            File theFile = searchCorpusModel.get(aFile.getName());
            if (add && theFile == null) {
                searchCorpusModel.put(aFile.getName(), aFile);
                searchCorpComboBox.addItem(aFile.getName());
                logger.debug("Added Item to Map: " + searchCorpusModel.get(aFile.getName()).toString());
                logger.debug("Added Item to Map - Mapsize: " + searchCorpusModel.size());
            } else if (!add && theFile != null){

                logger.debug("Removed Item from Map: " + aFile.toString());
                logger.debug("Removed Item from Map - Mapsize: " + searchCorpusModel.size());

                File parent = new File(wDir + File.separator + searchFolder);
                File indexParent = new File (wDir + File.separator + SemanticParser.getLucIndexParentDirName());
                File indexFolder = new File (indexParent + File.separator + aFile.getName());
                File indexFileParent = new File (wDir + File.separator + SemanticParser.getLuceneIndexFilesFolder() );
                File indexFileFolder = new File (indexFileParent + File.separator + aFile.getName());

                logger.debug("Parentfolder: " + parent.toString());
                logger.debug("Lucene Index Parent Folder: " + indexParent.toString() + " and Index Folder Path: " + indexFolder.toString());
                logger.debug("Lucene Index File Parent Folder: " + indexFileParent.toString() + " and Index File Folder Path: " + indexFileFolder.toString());

                boolean isIt = isSubDirectory(parent, aFile);
                boolean isIndexIt = isSubDirectory(indexParent, indexFolder);
                boolean isIndexFileIt = isSubDirectory(indexFileParent, indexFileFolder);

                int result = JOptionPane.showConfirmDialog(null, "Do you want to continue?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (isIt && result == JOptionPane.YES_OPTION && isIndexIt && isIndexFileIt) {

                    System.out.printf("Delete this Path: " + aFile.toString());


                    try {
                        FileUtils.deleteDirectory(aFile);


                    }catch (IOException e1) {

                        JOptionPane.showMessageDialog(null, "Unable to delete Folder");

                    }

                    searchCorpusModel.remove(aFile.getName());
                    searchCorpComboBox.removeItem(aFile.getName());
                    JOptionPane.showMessageDialog(null, "Deleted...");
                } else if (!isIt || result == JOptionPane.NO_OPTION) {
                    logger.debug("No delete, because cancel:" + aFile.toString());
                }
            }

        }


    }
    */

    public void addRemoveItemToTopicSearchBoxTaskWithBar(ProgressBar bar,File aFile, Boolean add, boolean isTopicCorp) {


        addRemoveItemToTopicSearchBoxTask task = new addRemoveItemToTopicSearchBoxTask(bar,aFile,add,isTopicCorp);
        logger.debug("Runs");
        task.execute();

    }

    class addRemoveItemToTopicSearchBoxTask extends SwingWorker<Integer, Integer> {
        private ProgressBar bar;
        private File aFile;
        private Boolean add;
        private boolean isTopicCorp;


        public addRemoveItemToTopicSearchBoxTask(ProgressBar aBar, File aFile, Boolean add, boolean isTopicCorp) {
            this.bar = aBar;
            this.aFile = aFile;
            this.add = add;
            this.isTopicCorp = isTopicCorp;
        }

        @Override
        public Integer doInBackground() {
            boolean isIt = false;
            boolean isIndexIt = false;
            boolean isIndexFileIt = false;
            bar.setProgressBarIndeterminate(true);

            if (isTopicCorp) {
                File theFile = trainCorp.get(aFile.getName());


                if (add && theFile == null) {
                    trainCorp.put(aFile.getName(), aFile);
                    selectTrainCorp.addItem(aFile.getName());
                    logger.debug("Added Item to Map: " + trainCorp.get(aFile.getName()).toString());
                    logger.debug("Added Item to Map - Mapsize: " + trainCorp.size());
                } else if (!add && theFile != null){

                    logger.debug("Removed Item from Map: " + aFile.toString());
                    logger.debug("Removed Item from Map - Mapsize: " + trainCorp.size());

                    File parent = new File(wDir + File.separator + topicFolder);
                    File indexParent = new File (wDir + File.separator + SemanticParser.getLucIndexParentDirName());
                    File indexFolder = new File (indexParent + File.separator + aFile.getName());
                    File indexFileParent = new File (wDir + File.separator + SemanticParser.getLuceneIndexFilesFolder() );
                    File indexFileFolder = new File (indexFileParent + File.separator + aFile.getName());

                    logger.debug("Parentfolder: " + parent.toString());
                    logger.debug("Lucene Index Parent Folder: " + indexParent.toString() + " and Index Folder Path: " + indexFolder.toString());
                    logger.debug("Lucene Index File Parent Folder: " + indexFileParent.toString() + " and Index File Folder Path: " + indexFileFolder.toString());


                    try {
                        isIt = isSubDirectory(parent, aFile);
                        isIndexIt = isSubDirectory(indexParent, indexFolder);
                        isIndexFileIt = isSubDirectory(indexFileParent, indexFileFolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    //int result = JOptionPane.showConfirmDialog(null, "Do you want to continue?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                    //if (isIt && result == JOptionPane.YES_OPTION && isIndexIt && isIndexFileIt)
                    if (isIt && isIndexIt && isIndexFileIt){

                        System.out.printf("Delete this Path: " + aFile.toString());

                        try {
                            FileUtils.deleteDirectory(aFile);
                            FileUtils.deleteDirectory(indexFolder);
                            FileUtils.deleteDirectory(indexFileFolder);

                        }catch (IOException e1) {
                            JOptionPane.showMessageDialog(null, "Unable to delete Folder");
                        }

                        trainCorp.remove(aFile.getName());
                        selectTrainCorp.removeItem(aFile.getName());
                        bar.dispose();
                        JOptionPane.showMessageDialog(null, "Deleted...");
                    }
                }

            } else if (!isTopicCorp) {
                File theFile = searchCorpusModel.get(aFile.getName());
                if (add && theFile == null) {
                    searchCorpusModel.put(aFile.getName(), aFile);
                    searchCorpComboBox.addItem(aFile.getName());
                    logger.debug("Added Item to Map: " + searchCorpusModel.get(aFile.getName()).toString());
                    logger.debug("Added Item to Map - Mapsize: " + searchCorpusModel.size());
                } else if (!add && theFile != null){

                    logger.debug("Removed Item from Map: " + aFile.toString());
                    logger.debug("Removed Item from Map - Mapsize: " + searchCorpusModel.size());

                    File parent = new File(wDir + File.separator + searchFolder);
                    File indexParent = new File (wDir + File.separator + SemanticParser.getLucIndexParentDirName());
                    File indexFolder = new File (indexParent + File.separator + aFile.getName());
                    File indexFileParent = new File (wDir + File.separator + SemanticParser.getLuceneIndexFilesFolder() );
                    File indexFileFolder = new File (indexFileParent + File.separator + aFile.getName());

                    logger.debug("Parentfolder: " + parent.toString());
                    logger.debug("Lucene Index Parent Folder: " + indexParent.toString() + " and Index Folder Path: " + indexFolder.toString());
                    logger.debug("Lucene Index File Parent Folder: " + indexFileParent.toString() + " and Index File Folder Path: " + indexFileFolder.toString());

                    try {
                        isIt = isSubDirectory(parent, aFile);
                        isIndexIt = isSubDirectory(indexParent, indexFolder);
                        isIndexFileIt = isSubDirectory(indexFileParent, indexFileFolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    //int result = JOptionPane.showConfirmDialog(null, "Do you want to continue?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    //if (isIt && result == JOptionPane.YES_OPTION && isIndexIt && isIndexFileIt)
                    if (isIt && isIndexIt && isIndexFileIt) {

                        System.out.printf("Delete this Path: " + aFile.toString());

                        try {
                            FileUtils.deleteDirectory(aFile);
                        }catch (IOException e1) {
                            JOptionPane.showMessageDialog(null, "Unable to delete Folder");
                        }

                        searchCorpusModel.remove(aFile.getName());
                        searchCorpComboBox.removeItem(aFile.getName());
                        bar.dispose();
                        JOptionPane.showMessageDialog(null, "Deleted...");

                    }
                }

            }

           return null;

        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            bar.dispose();
            try {
                updateIndexFileFolder();
            } catch (IOException e) {
                e.printStackTrace();
            }


            logger.debug("Done");
            //Toolkit.getDefaultToolkit().beep();

            //JOptionPane.showMessageDialog(null, "Import completed");

        }
    }

    public void addExistingSentModelsToMap() throws IOException {

        trainSentModels.clear();
        File theFolder = new File(wDir + File.separator + trainModelFolder);
        File[] theModels = theFolder.listFiles();
        if (theModels != null) {

            for (File aFile : theModels){
                String ext = FilenameUtils.getExtension(aFile.toString());
                String filename = FilenameUtils.getBaseName(aFile.toString());


                if (ext.equals("bin")) {
                    String langPre = filename.split("-")[0];
                    String langSuf = filename.split("-")[1];

                    if (langSuf.equals("sent")){
                        trainSentModels.put(langPre, aFile);
                        logger.debug("Put to TrainSentModel:" + trainSentModels.get(langPre));
                    }

                }

            }

            if (!trainSentModels.isEmpty()){
                langModelsText.setText(String.valueOf(trainSentModels.keySet()));
            }else {
                langModelsText.setText("None");
            }

        } else {
            langModelsText.setText("None");
        }

    }

    public void updateIndexFileFolder() throws IOException {

        if (trainCorp.isEmpty()) {
            indexFilesModel.clear();
            searchModelList.clear();
            selectIndexTypeComboBox.removeAllItems();
            selectTermweightComboBox.removeAllItems();
            return;
        }


        indexFilesModel.clear();
        searchModelList.clear();
        selectIndexTypeComboBox.removeAllItems();
        selectTermweightComboBox.removeAllItems();

        File theCorpFolder = trainCorp.get(selectTrainCorp.getSelectedItem().toString());
        File indexFileParent = new File (wDir + File.separator + SemanticParser.getLuceneIndexFilesFolder() );
        File indexFileFolder = new File (indexFileParent + File.separator + theCorpFolder.getName());



        logger.debug("The CorpFolder: " + indexFileFolder);
        File[] theIndexFiles = null;
        theIndexFiles = indexFileFolder.listFiles();

        if (theIndexFiles != null) {

            for (File aFile : theIndexFiles){
                String ext = FilenameUtils.getExtension(aFile.toString());
                String filename = FilenameUtils.getBaseName(aFile.toString());



                if (ext.equals("bin")) {
                    String indexType = filename.split("-")[0];
                    String termDocType = filename.split("-")[1];
                    String termWeightType = filename.split("-")[2];

                    logger.debug("Index File Names: " + indexType + " " + termDocType + " " + termWeightType);

                    indexFilesModel.put(filename, aFile);

                    if(termDocType.equals("term")) {
                        //logger.debug("Get Index in Searchmodellist: " + searchModelList.get(indexType).toString());

                        if (searchModelList.get(indexType) == null) {
                            List<String> theTFList = new ArrayList<>();
                            theTFList.add(termWeightType);
                            searchModelList.put(indexType, theTFList);
                        } else if (!searchModelList.get(indexType).contains(termWeightType)) {
                            logger.debug("searchmodellist: " + searchModelList.get(indexType).toString());
                            searchModelList.get(indexType).add(termWeightType);


                        }
                        logger.debug("The Map: " + searchModelList.toString());
                        logger.debug("The List: " + searchModelList.get(indexType).toString());


                    logger.debug("Put to indexFilesModel:" + indexFilesModel.get(filename));


                    }


                }



            }
            for (String theIndex : searchModelList.keySet()) {
                selectIndexTypeComboBox.addItem(theIndex);
            }

            logger.debug("The TF List: " + searchModelList.toString());
            logger.debug(("The selected Object " + selectIndexTypeComboBox.getSelectedItem().toString()));
            List<String> theItem = searchModelList.get(selectIndexTypeComboBox.getSelectedItem().toString());
            logger.debug("The Selected List: " + searchModelList.get(selectIndexTypeComboBox.getSelectedItem().toString()));
            logger.debug("The Selected ListVariable: " + theItem.toString());


        }



    }

    public File[] getSelectedSearchModelFiles() {

        String theIndexType;
        String theTFType;
        List<File> theFiles = new ArrayList<>();


        if (selectTermweightComboBox.getSelectedItem() != null && selectIndexTypeComboBox.getSelectedItem() != null) {

            String theTermSearchString = new String(selectIndexTypeComboBox.getSelectedItem() + "-term-" + selectTermweightComboBox.getSelectedItem());
            String theDocSearchString = new String(selectIndexTypeComboBox.getSelectedItem() + "-doc-" + selectTermweightComboBox.getSelectedItem());

            if (!theTermSearchString.isEmpty() && !theDocSearchString.isEmpty()){


                theFiles.add(indexFilesModel.get(theTermSearchString));
                theFiles.add(indexFilesModel.get(theDocSearchString));
                logger.debug("Return Files: " + theFiles.toString());
                return theFiles.toArray(new File[theFiles.size()]);
            }






        }
        return null;


    }

    public void loadTopicCorp() throws IOException {

        selectTrainCorp.removeAllItems();
        trainCorp.clear();
        File theFolder = new File(wDir + File.separator + topicFolder);
        File[] theModels = theFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });

        if (theModels != null) {

            for (File aFile : theModels) {

                addRemoveItemToTopicSearchBoxTaskWithBar(getProgressBarWithTitleLater("Please wait...", false), aFile, true, true);
                //addRemoveItemToTopicBox(aFile, true, true);

            }

        }
    }

    public void loadSearchCorp() throws IOException {

        searchCorpComboBox.removeAllItems();
        searchCorpusModel.clear();
        File theFolder = new File(wDir + File.separator + searchFolder);
        File[] theModels = theFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });

        if (theModels != null) {

            for (File aFile : theModels) {

                addRemoveItemToTopicSearchBoxTaskWithBar(getProgressBarWithTitleLater("Please wait...", false), aFile, true, false);
                //addRemoveItemToTopicBox(aFile, true, false);

            }

        }
    }

    public boolean isSubDirectory(File base, File child) throws IOException {
        base = base.getCanonicalFile();
        child = child.getCanonicalFile();

        File parentFile = child;
        while (parentFile != null) {
            if (base.equals(parentFile)) {
                return true;
            }
            parentFile = parentFile.getParentFile();
        }
        return false;
    }

    public void downloadModelTaskWithBar(ProgressBar bar) {


        downloadModelTask task = new downloadModelTask(bar);
        logger.debug("Runs");
        task.execute();

    }

    class downloadModelTask extends SwingWorker<Integer, Integer> {
        private ProgressBar bar;


        public downloadModelTask(ProgressBar aBar) {
            this.bar = aBar;


        }

        @Override
        public Integer doInBackground() {

            logger.debug("Runs");

            File modelFolder = new File(wDir + File.separator + trainModelFolder);

            if (!modelFolder.exists()) {
                modelFolder.mkdir();
            }
            bar.setProgressBarIndeterminate(true);

            Document doc = null;
            try {
                try {
                    doc = Jsoup.connect(modelUrl).timeout(10 * 1000).get();

                    //logger.debug("The Doc" + doc.html().toString());
                    Elements links = doc.select("a[href]");


                    for (Element link : links) {

                        String srcUrl = link.attr("abs:href");

                        logger.debug("A File: " + srcUrl);

                        String fileName = FilenameUtils.getName(srcUrl);
                        String ext = FilenameUtils.getExtension(srcUrl);
                        String langPre = fileName.split("-")[0];

                        File outPath = new File(wDir + File.separator + trainModelFolder + File.separator + fileName);

                        boolean exists = outPath.exists();

                        logger.debug("Filename: " + fileName + " with ext: " + ext + " and exists?: " + exists + " and Lang: " + langPre);
                        bar.setTextField(fileName);

                        if (fileName != null && !fileName.isEmpty() && ext.equals("bin") && !exists) {
                            URL down = new URL(srcUrl);
                            InputStream in = down.openStream();
                            OutputStream out = new BufferedOutputStream(new FileOutputStream(outPath));


                            for (int b; (b = in.read()) != -1; ) {
                                out.write(b);
                            }
                            out.close();
                            in.close();

                        }

                        if (bar.getButtonCancel()) {
                            addExistingSentModelsToMap();
                            break;
                        }



                    }
                    addExistingSentModelsToMap();
                } catch (SocketTimeoutException | SocketException e0) {
                    JOptionPane.showMessageDialog(null, "Connection Timeout - check Internet connection");
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            if (trainSentModels.isEmpty()) {
                JOptionPane.showMessageDialog(null, "There is an error with the page. Check http://opennlp.sourceforge.net/models-1.5/");
            }
            logger.debug("Done");
            Toolkit.getDefaultToolkit().beep();
            bar.dispose();
            JOptionPane.showMessageDialog(null, "Import completed");

        }
    }

    public boolean testURL(String aUrl) {
        String strUrl = aUrl;

        try {
            URL url = new URL(strUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();

            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            }


        } catch (IOException e) {
            System.err.println("Error creating HTTP connection");
            JOptionPane.showMessageDialog(null, "Bad Connection, check Internet");

        }
        return false;
    }

    public void trainTopicCorpTaskWithBar(ProgressBar bar, File aCorpDir, int aPosIndexRadius, int indexType, String termweightType, boolean onlylucene) {

        trainTopicCorp task = new trainTopicCorp(bar, aCorpDir, aPosIndexRadius, indexType, termweightType, onlylucene);
        logger.debug("Runs");

           task.execute();
    }

    class trainTopicCorp extends SwingWorker<Integer, Integer> {
        private ProgressBar bar;
        private File theCorpDir;
        private int thePosIndexRadius;
        private int theIndexType;
        private String theTermweightType;
        private boolean theOnlyLucene;

        public trainTopicCorp(ProgressBar aBar, File aCorpDir, int aPosIndexRadius, int indexType, String termweightType, boolean onlylucene) {
            this.bar = aBar;
            this.theCorpDir = aCorpDir;
            this.thePosIndexRadius = aPosIndexRadius;
            this.theIndexType = indexType;
            this.theTermweightType = termweightType;
            this.theOnlyLucene = onlylucene;

        }

        @Override
        public Integer doInBackground() {

            SemanticParser sp = new SemanticParser(wDir,theCorpDir,thePosIndexRadius, bar);

            bar.setProgressBarIndeterminate(true);

            if (theOnlyLucene) {

                    boolean success = sp.createLuceneIndexCorp();
                    if (success) {
                        logger.info("success lucene");

                    }

                } else {
                    boolean success2 = sp.buildSemanticIndex(theIndexType,theTermweightType);

                    if (success2) {
                        logger.info("success semantic");

                    }

                }



            return null;
        }
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            try {
                updateIndexFileFolder();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            logger.debug("Done");
            Toolkit.getDefaultToolkit().beep();
            bar.dispose();
            JOptionPane.showMessageDialog(null, "Training completed");

        }
    }

    public void clearSelections() {
        trainCorp.clear();
        trainSentModels.clear();
        searchCorpusModel.clear();
        indexFilesModel.clear();
        searchCorpComboBox.removeAllItems();
        selectIndexTypeComboBox.removeAllItems();
        selectTermweightComboBox.removeAllItems();

    }

    public void searchDocInTopicCorpTaskWithBar(ProgressBar bar) {
        if (!StringUtils.isNumeric(noOfSearchResultsText.getText())) {

            JOptionPane.showMessageDialog(null, "Enter Number of Search Results");
            return;

        }

        searchDocInTopicCorpTask taskDoc = new searchDocInTopicCorpTask(bar);
        logger.debug("Runs");
        taskDoc.execute();


    }

    class searchDocInTopicCorpTask extends SwingWorker<Void, Void> {
        private ProgressBar bar;



        public searchDocInTopicCorpTask(ProgressBar aBar) {
            this.bar = aBar;



        }

        @Override
        public Void doInBackground() {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bar.setProgressBarIndeterminate(true);

            if (docSearchResModel.getRowCount() != 0) {
                docSearchResModel.resetModel();
            }

            File termvectorfile = getSelectedSearchModelFiles()[0];
            File docvectorfile = getSelectedSearchModelFiles()[1];

            String theContents = null;

            if (selDocRadioButton.isSelected() && !searchFileString.isEmpty()) {
                theContents = searchFileString;

            } else if (selTextRadioButton.isSelected()) {
                theContents = searchTextArea.getText();


            }


            File theIndexFileFolder = new File(wDir + File.separator + SemanticParser.getLucIndexParentDirName() + File.separator + trainCorp.get(selectTrainCorp.getSelectedItem()).getName().toString());
            List<String> theWords = Utilities.getWords(theContents);

            ArrayList<String> arguments = new ArrayList<String>();
            arguments.add("-luceneindexpath");
            arguments.add(theIndexFileFolder.toString());
            arguments.add("-numsearchresults");
            arguments.add(noOfSearchResultsText.getText());
            //arguments.add("-vectortype");
            //arguments.add("-dimension");
            //arguments.add("-seedlength");
            //arguments.add("-minfrequency");
            //arguments.add("-maxnonalphabetchars");
            arguments.add("-termweight");
            arguments.add(((String)selectTermweightComboBox.getSelectedItem()));
            //arguments.add("-docindexing");
            //arguments.add("incremental");
            //arguments.add("-trainingcycles");
            //arguments.add(Integer.toString(amTraining));
            //arguments.add("-termtermvectorsfile");
            //arguments.add(termtermvectorfile.toString());
            arguments.add("-queryvectorfile");
            arguments.add(termvectorfile.toString());
            arguments.add("-searchvectorfile");
            arguments.add(docvectorfile.toString());
            //arguments.add("Abraham");
            //arguments.add("Isaac");




            for (String aWord : theWords) {
                arguments.add(aWord);
            }

            String[] args = new String[arguments.size()];
            args = arguments.toArray(args);

            List<SearchResult> theResult;
            FlagConfig flagConfig;
            try {
                flagConfig = FlagConfig.getFlagConfig(args);
                theResult = Search.runSearch(flagConfig);
            } catch (IllegalArgumentException e) {
                throw e;
            }




            if (theResult.size() > 0) {
                logger.info("Search output follows ...\n");
                for (SearchResult result: theResult) {

                    File theFile = new File(result.getObjectVector().getObject().toString());
                    double percent = result.getScore() * 100;
                    String theScore = new DecimalFormat("#.###").format(percent);

                    DocSearchFile theEntry = new DocSearchFile(theScore, theFile, new File(wDir.toString() + File.separator + topicFolder));

                    System.out.println(result.toSimpleString());
                    logger.debug("ObjectVector: " + result.getObjectVector().getObject().toString());
                    logger.debug("Score: " + result.getScore());
                    logger.debug("toString: " + result.toString());

                    docSearchResModel.addDocFile(theEntry);
                }


            } else {
                DocSearchFile theEntry = new DocSearchFile(" ",new File(emptyTable), new File(""));
                docSearchResModel.addDocFile(theEntry);
                //JOptionPane.showMessageDialog(null, "No Results");
            }


            return null;

        }
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {


            logger.debug("Done");
            //Toolkit.getDefaultToolkit().beep();
            bar.dispose();
            //JOptionPane.showMessageDialog(null, "Training completed");

        }
    }


    public void searchTermInTopicCorpTaskWithBar(ProgressBar bar) {
        if (!StringUtils.isNumeric(noOfSearchResultsText.getText())) {

            JOptionPane.showMessageDialog(null, "Enter Number of Search Results");
            return;

        }
        searchTermInTopicCorpTask taskTerm = new searchTermInTopicCorpTask(bar);
        logger.debug("Runs");
        taskTerm.execute();


    }

    class searchTermInTopicCorpTask extends SwingWorker<Void, Void> {
        private ProgressBar bar;



        public searchTermInTopicCorpTask(ProgressBar aBar) {
            this.bar = aBar;



        }

        @Override
        public Void doInBackground() {

            bar.setProgressBarIndeterminate(true);

            if (termSearchResModel.getRowCount() != 0) {
                termSearchResModel.setRowCount(0);
            }



            File termvectorfile = getSelectedSearchModelFiles()[0];
            File docvectorfile = getSelectedSearchModelFiles()[1];

            String theContents = null;

            if (selDocRadioButton.isSelected() && !searchFileString.isEmpty()) {
                theContents = searchFileString;

            } else if (selTextRadioButton.isSelected()) {
                theContents = searchTextArea.getText();


            }

            File theIndexFileFolder = new File(wDir + File.separator + SemanticParser.getLucIndexParentDirName() + File.separator + trainCorp.get(selectTrainCorp.getSelectedItem()).getName().toString());
            List<String> theWords = Utilities.getWords(theContents);

            logger.debug("Numeric?: " + StringUtils.isNumeric(noOfSearchResultsText.getText()));


            ArrayList<String> arguments = new ArrayList<String>();
            arguments.add("-termweight");
            arguments.add((String)selectTermweightComboBox.getSelectedItem());
            arguments.add("-luceneindexpath");
            arguments.add(theIndexFileFolder.toString());
            arguments.add("-numsearchresults");
            arguments.add(noOfSearchResultsText.getText());
            arguments.add("-queryvectorfile");
            arguments.add(termvectorfile.toString());
            //arguments.add("-vectortype");
            //arguments.add("-dimension");
            //arguments.add("-seedlength");
            //arguments.add("-minfrequency");
            //arguments.add("-maxnonalphabetchars");

            //arguments.add("-docindexing");
            //arguments.add("incremental");
            //arguments.add("-trainingcycles");
            //arguments.add(Integer.toString(amTraining));
            //arguments.add("-termtermvectorsfile");
            //arguments.add(termtermvectorfile.toString());

            //arguments.add("-searchvectorfile");
            //arguments.add(termvectorfile.toString());
            //arguments.add(searchTextArea.getText().toString());

            for (String aWord : theWords) {
                arguments.add(aWord);
            }


            String[] args = new String[arguments.size()];
            args = arguments.toArray(args);

            List<SearchResult> theResult;
            FlagConfig flagConfig;
            try {
                flagConfig = FlagConfig.getFlagConfig(args);
                theResult = Search.runSearch(flagConfig);
            } catch (IllegalArgumentException e) {
                //System.err.println(usageMessage);
                throw e;
            }


            if (theResult.size() > 0) {
                logger.info("Search output follows ...\n");
                for (SearchResult result: theResult) {

                    System.out.println(result.toSimpleString());
                    logger.debug("ObjectVector: " + result.getObjectVector().getObject().toString());
                    logger.debug("Score: " + result.getScore());
                    logger.debug("toString: " + result.toString());
                    double percent = result.getScore() * 100;
                    String theScore = new DecimalFormat("#.###").format(percent);

                    termSearchResModel.addRow(new Object[]{theScore, result.getObjectVector().getObject().toString()});
                }

            } else {
                termSearchResModel.addRow(new Object[]{null, emptyTable});
            }

            return null;
        }
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {





            logger.debug("Done");
            //Toolkit.getDefaultToolkit().beep();
            bar.dispose();
            //JOptionPane.showMessageDialog(null, "Training completed");

        }
    }


    public void compareCorpDocsWithSearchDocTaskWithBar(ProgressBar bar) {

        if (!StringUtils.isNumeric(noOfSearchResultsText.getText())) {

            JOptionPane.showMessageDialog(null, "Enter Number of Search Results");
            return;

        }


        if (docSearchResModel.getRowCount() != 0) {
            docSearchResModel.resetModel();
        }
        if (termSearchResModel.getRowCount() != 0) {
            termSearchResModel.setRowCount(0);
        }

        compareCorpDocsWithSearchDocTask task = new compareCorpDocsWithSearchDocTask(bar);
        logger.debug("Runs");
        task.execute();

    }

    class compareCorpDocsWithSearchDocTask extends SwingWorker<Void, Void> {
        private ProgressBar bar;


        public compareCorpDocsWithSearchDocTask(ProgressBar aBar) {
            this.bar = aBar;


        }

        @Override
        public Void doInBackground() throws IOException{
            bar.setProgressBarIndeterminate(true);

            File theIndexFileFolder = new File(wDir + File.separator + SemanticParser.getLucIndexParentDirName() + File.separator + trainCorp.get(selectTrainCorp.getSelectedItem()).getName().toString());
            File termvectorfile = getSelectedSearchModelFiles()[0];
            //File docvectorfile = getSelectedSearchModelFiles()[1];

            ArrayList<String> arguments = new ArrayList<String>();
            arguments.add("-termweight");
            arguments.add(((String)selectTermweightComboBox.getSelectedItem()));
            arguments.add("-luceneindexpath");
            arguments.add(theIndexFileFolder.toString());
            arguments.add("-numsearchresults");
            arguments.add(noOfSearchResultsText.getText());
            arguments.add("-queryvectorfile");
            arguments.add(termvectorfile.toString());
            arguments.add("-searchvectorfile");
            arguments.add(termvectorfile.toString());

            if (docSearchResModel.getRowCount() != 0) {
                docSearchResModel.resetModel();
            }

            if (termSearchResModel.getRowCount() != 0) {
                termSearchResModel.setRowCount(0);
            }

            //String[] args = new String[arguments.size()];
            //args = arguments.toArray(args);

            //List<SearchResult> theResult;
            //FlagConfig flagConfig;
            //flagConfig = FlagConfig.getFlagConfig(args);


            //Get Selected Train Corpus


            //Get Selected Search Corpus:
            File searchCorpDir = searchCorpusModel.get(searchCorpComboBox.getSelectedItem().toString());

           IOFileFilter filter = new IOFileFilter() {
               @Override
               public boolean accept(File file) {
                   if (file.exists() && !file.isHidden()) {
                       return true;
                   } else {
                       return false;
                   }
               }

               @Override
               public boolean accept(File file, String s) {
                   if (s.equals(".metadata.txt")) {
                       return false;
                   }else {
                       return true;
                   }
               }
           };

            Collection<File> theFiles = FileUtils.listFiles(searchCorpDir, filter, TrueFileFilter.INSTANCE);

            logger.debug("The selected SearchCorp Folder: " + searchCorpDir);
            logger.debug("The Collection: " + theFiles.toString());


            List<SearchResult> theCompResult = new ArrayList<SearchResult>();
            List<String> theSearchInputWordlist = new ArrayList<String>();
            List<SearchResult> theTermResult = new ArrayList<SearchResult>();

            if (selDocRadioButton.isSelected() && !searchFileString.isEmpty()) {

                logger.debug("Compare Doc with Search Corpus");

                theSearchInputWordlist = Utilities.getWords(searchFileString);


                for (File aFile : theFiles) {


                    String theDocString = Utilities.removeQuoteFromString(Utilities.readFileToString(aFile));

                    logger.debug("The File: " + aFile.toString());

                    ArrayList<String> allArgs = new ArrayList<String>(arguments);
                    logger.debug("AllArgs " + allArgs.toString());


                    allArgs.add("\"" + searchFileString + "\"");
                    allArgs.add("\"" + theDocString + "\"");

                    String[] args = new String[allArgs.size()];
                    args = allArgs.toArray(args);

                    FlagConfig flagConfig;
                    flagConfig = FlagConfig.getFlagConfig(args);
                    logger.debug("Remaining Args: " + flagConfig.remainingArgs[0]);


                    System.out.println(aFile.toString());

                    double theScore = 0;
                    try {
                        theScore = runCompareTerms(flagConfig);
                        logger.debug("The Score " + theScore);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (!Double.isNaN(theScore)) {
                        ObjectVector theObVec = new ObjectVector(aFile, null);
                        SearchResult theSerRes = new SearchResult(theScore, theObVec);
                        theCompResult.add(theSerRes);

                    }



                }


            } else if (selTextRadioButton.isSelected() && !searchTextArea.getText().isEmpty()){

                logger.debug("Compare Text with Search Corpus");
                logger.debug("Compare Doc with Search Corpus");

                String theSearchString = Utilities.removeQuoteFromString(searchTextArea.getText());

                theSearchInputWordlist = Utilities.getWords(theSearchString);



                for (File aFile : theFiles) {

                    String theDocString = Utilities.removeQuoteFromString(Utilities.readFileToString(aFile));
                    logger.debug("The File: " + aFile.toString());

                    ArrayList<String> allArgs = new ArrayList<String>(arguments);
                    logger.debug("AllArgs " + allArgs.toString());


                    allArgs.add("\"" + theSearchString + "\"");
                    allArgs.add("\"" + theDocString + "\"");

                    String[] args = new String[allArgs.size()];
                    args = allArgs.toArray(args);

                    FlagConfig flagConfig;
                    flagConfig = FlagConfig.getFlagConfig(args);
                    logger.debug("Remaining Args: " + flagConfig.remainingArgs[0]);


                    System.out.println(aFile.toString());

                    double theScore = 0;
                    try {
                        theScore = runCompareTerms(flagConfig);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (!Double.isNaN(theScore)) {
                        logger.debug("The Score " + theScore);
                        ObjectVector theObVec = new ObjectVector(aFile, null);
                        SearchResult theSerRes = new SearchResult(theScore, theObVec);
                        theCompResult.add(theSerRes);

                    }



                }

            }


            if (theCompResult.size() > 0) {
                logger.info("Search output follows ... " + theCompResult.size());

                Comparator<SearchResult> comp = (r1, r2) -> Double.compare(r1.getScore(),r2.getScore());
                Collections.sort(theCompResult,Collections.reverseOrder(comp));

                //theCompResult.stream().forEach(r -> logger.debug("the r:" + r.getScore()));

                logger.debug("The Sorted List: " + theCompResult.toString());

                int numSearchRes = Integer.parseInt(noOfSearchResultsText.getText());
                List<SearchResult> theTrimmedList = null;

                if (theCompResult.size() > numSearchRes) {
                    theTrimmedList = theCompResult.subList(0, numSearchRes);

                } else {
                    theTrimmedList = theCompResult;
                }

                logger.debug("The Trimmed List: " + theTrimmedList.size());
                logger.debug("The Comp List: " + theCompResult.size());



                for (SearchResult result : theTrimmedList) {

                    File theFile = new File(result.getObjectVector().getObject().toString());
                    double percent = result.getScore() * 100;
                    String theScore = new DecimalFormat("#.###").format(percent);

                    DocSearchFile theEntry = new DocSearchFile(theScore, theFile, new File(wDir.toString() + File.separator + searchFolder));

                    logger.debug(result.toSimpleString());
                    logger.debug("ObjectVector: " + result.getObjectVector().getObject().toString());
                    logger.debug("Score: " + result.getScore());
                    logger.debug("toString: " + result.toString());

                    docSearchResModel.addDocFile(theEntry);



                }

                ArrayList<String> allTermArgs = new ArrayList<String>(arguments);
                logger.debug("AllTermArgs " + allTermArgs.toString());

                for (String aWord : theSearchInputWordlist) {
                    allTermArgs.add(aWord);
                }

                String[] termArgs = new String[allTermArgs.size()];
                termArgs = allTermArgs.toArray(termArgs);


                FlagConfig flagConfig;
                flagConfig = FlagConfig.getFlagConfig(termArgs);
                logger.debug("Remaining Term Args: " + flagConfig.remainingArgs[0]);

                try {
                    theTermResult = Search.runSearch(flagConfig);
                } catch (IllegalArgumentException e) {
                    //System.err.println(usageMessage);
                    throw e;
                }


                if (theTermResult.size() > 0) {
                    logger.info(" Term Search output follows ...\n");
                    for (SearchResult result: theTermResult) {

                        System.out.println(result.toSimpleString());
                        logger.debug("Term ObjectVector: " + result.getObjectVector().getObject().toString());
                        logger.debug("Term Score: " + result.getScore());
                        logger.debug("Term toString: " + result.toString());
                        double percent = result.getScore() * 100;
                        String theScore = new DecimalFormat("#.###").format(percent);

                        termSearchResModel.addRow(new Object[]{theScore, result.getObjectVector().getObject().toString()});
                    }

                } else {
                    termSearchResModel.addRow(new Object[]{null, emptyTable});
                }



            } else {
                DocSearchFile theEntry = new DocSearchFile(" ",new File(emptyTable), new File(""));
                docSearchResModel.addDocFile(theEntry);
                termSearchResModel.addRow(new Object[]{null, emptyTable});
                //JOptionPane.showMessageDialog(null, "No Results");
            }

            return null;
        }

        public double runCompareTerms(FlagConfig flagConfig) throws IOException {
            String[] args = flagConfig.remainingArgs;
            logger.debug("Remaining Args: " + args.length);

            LuceneUtils luceneUtils = null;


            VectorStoreRAM vecReader =null, elementalVecReader=null, semanticVecReader=null, predicateVecReader=null;
            if (flagConfig.searchtype().equals(Search.SearchType.BOUNDPRODUCT) || flagConfig.searchtype().equals(Search.SearchType.BOUNDMINIMUM) || flagConfig.searchtype().equals(Search.SearchType.INTERSECTION) || flagConfig.searchtype().equals(Search.SearchType.BOUNDPRODUCTSUBSPACE))
            {
                elementalVecReader = new VectorStoreRAM(flagConfig);
                semanticVecReader = new VectorStoreRAM(flagConfig);
                predicateVecReader = new VectorStoreRAM(flagConfig);
                elementalVecReader.initFromFile(flagConfig.elementalvectorfile());
                semanticVecReader.initFromFile(flagConfig.semanticvectorfile());
                predicateVecReader.initFromFile(flagConfig.predicatevectorfile());

            } else {

                vecReader = new VectorStoreRAM(flagConfig);
                vecReader.initFromFile(VectorStoreUtils.getStoreFileName(flagConfig.queryvectorfile(), flagConfig));
                logger.info(String.format(
                        "Using RAM cache of vectors from file: %s\n", flagConfig.queryvectorfile()));
            }

            if (!flagConfig.luceneindexpath().isEmpty()) {
                try {
                    luceneUtils = new LuceneUtils(flagConfig);
                } catch (IOException e) {
                    logger.info("Couldn't open Lucene index at " + flagConfig.luceneindexpath());
                }
            }
            if (luceneUtils == null) {
                logger.info("No Lucene index for query term weighting, "
                        + "so all query terms will have same weight.\n");
            }

            Vector vec1=null;
            Vector vec2=null;

            if (flagConfig.searchtype().equals(Search.SearchType.BOUNDPRODUCT))
            {
                vec1 = CompoundVectorBuilder.getBoundProductQueryVectorFromString(
                        flagConfig, elementalVecReader, semanticVecReader, predicateVecReader, luceneUtils, args[0]);
                vec2 = CompoundVectorBuilder.getBoundProductQueryVectorFromString(
                        flagConfig, elementalVecReader, semanticVecReader, predicateVecReader, luceneUtils, args[1]);

            } else  if (flagConfig.searchtype().equals(Search.SearchType.BOUNDPRODUCTSUBSPACE))
            {
                ArrayList<pitt.search.semanticvectors.vectors.Vector> vecs1 = CompoundVectorBuilder.getBoundProductQuerySubspaceFromString(
                        flagConfig, elementalVecReader, semanticVecReader, predicateVecReader, args[0]);
                vec2 = CompoundVectorBuilder.getBoundProductQueryVectorFromString(
                        flagConfig, elementalVecReader, semanticVecReader, predicateVecReader, luceneUtils, args[1]);



                return VectorUtils.compareWithProjection(vec2, vecs1);

            }
            else  if (flagConfig.searchtype().equals(Search.SearchType.INTERSECTION))
            {

                vec1 = CompoundVectorBuilder.getBoundProductQueryIntersectionFromString(
                        flagConfig, elementalVecReader, semanticVecReader, predicateVecReader, luceneUtils, args[0]);

                vec2 = CompoundVectorBuilder.getBoundProductQueryIntersectionFromString(
                        flagConfig, elementalVecReader, semanticVecReader, predicateVecReader, luceneUtils, args[1]);



                return vec1.measureOverlap(vec2);

            }
            else {
                vec1 = CompoundVectorBuilder.getQueryVectorFromString(
                        vecReader, luceneUtils, flagConfig, args[0]);
                vec2 = CompoundVectorBuilder.getQueryVectorFromString(
                        vecReader, luceneUtils, flagConfig, args[1]);


            }



            return vec1.measureOverlap(vec2);
        }




        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            logger.debug("Done");
            Toolkit.getDefaultToolkit().beep();
            bar.dispose();
            //JOptionPane.showMessageDialog(null, "Training completed");

        }
    }


    public void fillMetaDataField(File aFile) {
        File metaFile = new File(aFile.getParentFile().toString() + File.separator + ".metadata.txt");
        String metadata;


        if (!metaFile.exists()) {
            return;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(metaFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                try {
                    line = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            metadata = sb.toString();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        logger.debug("Metadata: " + metadata);

        metadataTextField.setText(metadata);
        metadataTextField.setCaretPosition(0);

    }


    //TODO Implement OCR

    //TODO Implement Buy button

    //TODO Implement Help and Buy Open Internet Browser

    //TODO Implement update check

    //TODO Improve Reader, save default Text Format and Size, allow to mark and to export text as pdf

    //TODO Export/Import search Results

    //TODO Search History

    //TODO open recent Project

    //TODO Implement Project File and Defaults




    public void searchMatchTermOfSelDocTaskWithBar(ProgressBar bar) {

        if (!StringUtils.isNumeric(noOfSearchResultsText.getText())) {

            JOptionPane.showMessageDialog(null, "Enter Number of Search Results");
            return;

        }
        searchMatchTermOfSelDocTask taskMatch = new searchMatchTermOfSelDocTask(bar);
        logger.debug("Search Matches");
        taskMatch.execute();


    }

    class searchMatchTermOfSelDocTask extends SwingWorker<Void, Void> {
        private ProgressBar bar;



        public searchMatchTermOfSelDocTask(ProgressBar aBar) {
            this.bar = aBar;



        }

        @Override
        public Void doInBackground() {

            bar.setProgressBarIndeterminate(true);

            if (termSearchResModel.getRowCount() != 0) {
                termSearchResModel.setRowCount(0);
            }



            File termvectorfile = getSelectedSearchModelFiles()[0];
            File docvectorfile = getSelectedSearchModelFiles()[1];
            logger.debug("termfile: " + termvectorfile);
            logger.debug("docfile: " + docvectorfile);

            //String theContents = null;

            /*if (selDocRadioButton.isSelected() && !searchFileString.isEmpty()) {
                theContents = searchFileString;

            } else if (selTextRadioButton.isSelected()) {
                theContents = searchTextArea.getText();


            }*/

            //File theIndexFileFolder = new File(wDir + File.separator + SemanticParser.getLucIndexParentDirName() + File.separator + trainCorp.get(selectTrainCorp.getSelectedItem()).getName().toString());
            //List<String> theWords = Utilities.getWords(theContents);

            //logger.debug("Numeric?: " + StringUtils.isNumeric(noOfSearchResultsText.getText()));

            int row = docSearchResTable.convertRowIndexToModel(docSearchResTable.getSelectedRow());
            logger.debug("The Matchterm row: " + row);
            DocSearchModel theModel = (DocSearchModel)docSearchResTable.getModel();
            File theFile = theModel.getDocFile(row).getFile();
            logger.debug("The Matchterm File: " + theFile.toString());



            ArrayList<String> arguments = new ArrayList<String>();
            //arguments.add("-luceneindexpath");
            //arguments.add(theIndexFileFolder.toString());
            //arguments.add("-numsearchresults");
            //arguments.add(noOfSearchResultsText.getText());
            arguments.add("-queryvectorfile");
            arguments.add(docvectorfile.toString());
            arguments.add("-searchvectorfile");
            arguments.add(termvectorfile.toString());
            arguments.add("-matchcase");
            arguments.add(theFile.toString());
            //arguments.add("-vectortype");
            //arguments.add("-dimension");
            //arguments.add("-seedlength");
            //arguments.add("-minfrequency");
            //arguments.add("-maxnonalphabetchars");
            //arguments.add("-termweight");
            //arguments.add(termweight);
            //arguments.add("-docindexing");
            //arguments.add("incremental");
            //arguments.add("-trainingcycles");
            //arguments.add(Integer.toString(amTraining));
            //arguments.add("-termtermvectorsfile");
            //arguments.add(termtermvectorfile.toString());


            //arguments.add(searchTextArea.getText().toString());

            /*for (String aWord : theWords) {
                arguments.add(aWord);
            }*/


            String[] args = new String[arguments.size()];
            args = arguments.toArray(args);

            //List<SearchResult> theResult;
            FlagConfig flagConfig;
            try {
                flagConfig = FlagConfig.getFlagConfig(args);

            } catch (IllegalArgumentException e) {
                //System.err.println(usageMessage);
                throw e;
            }

            try {
                Search.main(args);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*
            theResult = Search.runSearch(flagConfig);
            logger.debug("The Match Result: " + theResult.size());

            if (theResult.size() > 0) {
                logger.info("Search output follows ...\n");
                for (SearchResult result: theResult) {

                    System.out.println(result.toSimpleString());
                    logger.debug("ObjectVector: " + result.getObjectVector().getObject().toString());
                    logger.debug("Score: " + result.getScore());
                    logger.debug("toString: " + result.toString());
                    double percent = result.getScore() * 100;
                    String theScore = new DecimalFormat("#.###").format(percent);

                    termSearchResModel.addRow(new Object[]{theScore, result.getObjectVector().getObject().toString()});
                }

            } else {
                termSearchResModel.addRow(new Object[]{null, "No Search Results..."});
            }
            */
            return null;
        }
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {

            logger.debug("Done Term of selected File");
            //Toolkit.getDefaultToolkit().beep();
            bar.dispose();
            //JOptionPane.showMessageDialog(null, "Training completed");

        }
    }

    public void matchTerm() {

        if (!StringUtils.isNumeric(noOfSearchResultsText.getText())) {

            JOptionPane.showMessageDialog(null, "Enter Number of Search Results");
            return;

        }

        //bar.setProgressBarIndeterminate(true);

        if (termSearchResModel.getRowCount() != 0) {
            termSearchResModel.setRowCount(0);
        }



        File termvectorfile = getSelectedSearchModelFiles()[0];
        File docvectorfile = getSelectedSearchModelFiles()[1];
        logger.debug("termfile: " + termvectorfile);
        logger.debug("docfile: " + docvectorfile);

        /*String theContents = null;

            if (selDocRadioButton.isSelected() && !searchFileString.isEmpty()) {
                theContents = searchFileString;

            } else if (selTextRadioButton.isSelected()) {
                theContents = searchTextArea.getText();


            }
        */
        File theIndexFileFolder = new File(wDir + File.separator + SemanticParser.getLucIndexParentDirName() + File.separator + trainCorp.get(selectTrainCorp.getSelectedItem()).getName().toString());
        //List<String> theWords = Utilities.getWords(theContents);

        //logger.debug("Numeric?: " + StringUtils.isNumeric(noOfSearchResultsText.getText()));

        int row = docSearchResTable.convertRowIndexToModel(docSearchResTable.getSelectedRow());
        logger.debug("The Matchterm row: " + row);
        DocSearchModel theModel = (DocSearchModel)docSearchResTable.getModel();
        File theFile = theModel.getDocFile(row).getFile();
        logger.debug("The Matchterm File: " + theFile.toString());



        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add("-luceneindexpath");
        arguments.add(theIndexFileFolder.toString());
        arguments.add("-numsearchresults");
        arguments.add(noOfSearchResultsText.getText());
        arguments.add("-queryvectorfile");
        arguments.add(docvectorfile.toString());
        arguments.add("-searchvectorfile");
        arguments.add(termvectorfile.toString());
        arguments.add("-matchcase");
        arguments.add(theFile.toString());



        //arguments.add("-vectortype");
        //arguments.add("-dimension");
        //arguments.add("-seedlength");
        //arguments.add("-minfrequency");
        //arguments.add("-maxnonalphabetchars");
        //arguments.add("-termweight");
        //arguments.add(termweight);
        //arguments.add("-docindexing");
        //arguments.add("incremental");
        //arguments.add("-trainingcycles");
        //arguments.add(Integer.toString(amTraining));
        //arguments.add("-termtermvectorsfile");
        //arguments.add(termtermvectorfile.toString());


        //arguments.add(searchTextArea.getText().toString());

            /*for (String aWord : theWords) {
                arguments.add(aWord);
            }*/


        String[] args = new String[arguments.size()];
        args = arguments.toArray(args);

        for (String aarg : args) {
            logger.debug("The Args: " + aarg);
        }


        List<SearchResult> theResult;
        FlagConfig flagConfig;
        try {
            flagConfig = FlagConfig.getFlagConfig(args);
            theResult = Search.runSearch(flagConfig);
        } catch (IllegalArgumentException e) {
            //System.err.println(usageMessage);
            throw e;
        }

        try {
            pitt.search.semanticvectors.Search.main(args);
        } catch (IOException e) {
            e.printStackTrace();
        }


        theResult = Search.runSearch(flagConfig);
            logger.debug("The Match Result: " + theResult.size());

            if (theResult.size() > 0) {
                logger.info("Search output follows ...\n");
                for (SearchResult result: theResult) {

                    System.out.println(result.toSimpleString());
                    logger.debug("ObjectVector: " + result.getObjectVector().getObject().toString());
                    logger.debug("Score: " + result.getScore());
                    logger.debug("toString: " + result.toString());
                    double percent = result.getScore() * 100;
                    String theScore = new DecimalFormat("#.###").format(percent);

                    termSearchResModel.addRow(new Object[]{theScore, result.getObjectVector().getObject().toString()});
                }

            } else {
                termSearchResModel.addRow(new Object[]{null, "No Search Results..."});
            }


    }




}
