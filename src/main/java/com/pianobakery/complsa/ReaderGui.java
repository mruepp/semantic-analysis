package com.pianobakery.complsa;


import org.apache.log4j.Logger;

import javax.smartcardio.Card;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.pianobakery.complsa.IndexWrapper;

/**
 * Created by michael on 27.07.15.
 */
public class ReaderGui {
    private JPanel rootPanel;
    private JPanel cardPanel;
    private JPanel buttonPanel;
    private JPanel splitSelPanel;
    private JPanel splitDocPanel;
    private JPanel fullDocPanel;


    private JTextPane beforeText;
    private JTextPane selectedText;
    private JTextPane afterText;
    private JTextPane beforeDocText;
    private JTextPane selectedDocText;
    private JTextPane afterDocText;
    private JTextPane fullTextPane;

    private JButton prevButton;
    private JButton nextButton;

    private JButton toggleViewButton;
    private JComboBox viewType;

    private JButton documentUpButton;
    private JButton documentDownButton;

    private JCheckBox highlightSelectedTermsCheckBox;
    private static String[] viewTypeCombo = {"Result Splitscreen","Document Splitscreen","Document"};
    private MainGui theMainGui;
    private String[] searchTerms;
    private List<JTextPane> allTextPanes = new ArrayList<JTextPane>();


    private DocSearchModel theModel;
    private JFrame frame;


    final static Logger logger = Logger.getLogger(MainGui.class);

    public void enableChunkButtons(boolean enabled) {

        documentUpButton.setEnabled(enabled);
        documentDownButton.setEnabled(enabled);

    }


    public void setSearchTerms(String[] aTerms) {
        this.searchTerms = aTerms;
        setHighliter(highlightSelectedTermsCheckBox.isSelected());

    }

    public boolean getFrameVisible() {
        return frame.isVisible();
    }
    public void setFrameVisible(boolean aBool) {
        frame.setVisible(aBool);

    }

    public void showGui() {
        frame = new JFrame("ReaderGui");
        frame.setContentPane(this.rootPanel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Semantic Text Reader");
        Dimension thePanelDim = new Dimension(800,1024);
        this.rootPanel.setPreferredSize(thePanelDim);

        int frameWidth = 800;
        int frameHeight = 1024;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((int) screenSize.getWidth() / 2 - frameWidth / 2, (int) screenSize.getHeight() / 4 - frameHeight / 4, frameWidth, frameHeight);
        frame.pack();
        frame.setVisible(true);
        allTextPanes.add(beforeText);
        allTextPanes.add(selectedText);
        allTextPanes.add(afterText);
        allTextPanes.add(beforeDocText);
        allTextPanes.add(selectedDocText);
        allTextPanes.add(afterDocText);
        allTextPanes.add(fullTextPane);


    }

    public ReaderGui(DocSearchModel theModel, MainGui theMainGui) {
            this.theModel = theModel;
            this.theMainGui = theMainGui;

        if (theModel != null) {
            theMainGui.setDocReaderContent(0);
        }


            prevButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int outrow = theMainGui.getSelectedDocTableRow();
                    logger.debug("The Row: " + outrow);
                    if (outrow != -1 || outrow >= 1) {
                        theMainGui.setSelectedDocTableRow(outrow - 1);
                    }

                }
            });



        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int inrow = theMainGui.getSelectedDocTableRow();

                if (inrow != -1 || inrow > theModel.getRowCount() - 1) {
                    theMainGui.setSelectedDocTableRow(inrow + 1);
                }

            }
        });

        viewType.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                CardLayout c1 = new CardLayout();
                c1 = (CardLayout) (cardPanel.getLayout());
                int sel = viewType.getSelectedIndex();
                logger.debug("Pane Index: " + sel);
                logger.debug("C1 Class: " + c1.getClass().toString());
                switch (sel) {

                    case 0:
                        c1.show(cardPanel, "splitSelPanel");
                        enableChunkButtons(false);
                        logger.debug("Case0");
                        break;
                    case 1:
                        c1.show(cardPanel, "splitDocPanel");
                        enableChunkButtons(true);
                        logger.debug("Case1");
                        break;
                    case 2:
                        c1.show(cardPanel, "fullDocPanel");
                        enableChunkButtons(false);
                        logger.debug("Case2");
                        break;
                    default:
                        c1.show(cardPanel, "splitSelPanel");
                        enableChunkButtons(false);
                        logger.debug("Casedef");
                        break;

                }
                logger.debug("reval");
                cardPanel.revalidate();

            }
        });

        documentUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                theMainGui.setDocReaderContent(-1);
                setHighliter(highlightSelectedTermsCheckBox.isSelected());


            }
        });

        documentDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                theMainGui.setDocReaderContent(1);
                setHighliter(highlightSelectedTermsCheckBox.isSelected());

            }
        });

        toggleViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                CardLayout c1 = new CardLayout();
                c1 = (CardLayout) (cardPanel.getLayout());
                int sel = viewType.getSelectedIndex();
                logger.debug("Pane Index: " + sel);
                logger.debug("C1 Class: " + c1.getClass().toString());
                if (sel < 2) {
                    sel = sel + 1;
                } else {
                    sel = 0;
                }
                switch (sel) {

                    case 0:
                        c1.show(cardPanel, "splitSelPanel");
                        logger.debug("Case0");
                        break;
                    case 1:
                        c1.show(cardPanel, "splitDocPanel");
                        logger.debug("Case1");
                        break;
                    case 2:
                        c1.show(cardPanel, "fullDocPanel");
                        logger.debug("Case2");
                        break;
                    default:
                        c1.show(cardPanel, "splitSelPanel");
                        logger.debug("Casedef");
                        break;

                }
                viewType.setSelectedIndex(sel);
                logger.debug("reval");
                cardPanel.revalidate();

            }
        });

        highlightSelectedTermsCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setHighliter(highlightSelectedTermsCheckBox.isSelected());
            }
        });

        if (searchTerms != null) {
            setHighliter(highlightSelectedTermsCheckBox.isSelected());
        }


    }


    public void setBeforeText(String beforeText) {
        this.beforeText.setText(beforeText);
        this.beforeText.setCaretPosition(0);
    }

    public void setSelectedText(String selectedText) {
        this.selectedText.setText(selectedText);
        this.selectedText.setCaretPosition(0);
    }

    public void setAfterText(String afterText) {
        this.afterText.setText(afterText);
        this.afterText.setCaretPosition(0);
    }

    public void setBeforeDocText(String beforeDocText) {
        this.beforeDocText.setText(beforeDocText);
        this.beforeDocText.setCaretPosition(0);
    }

    public void setSelectedDocText(String selectedDocText) {
        this.selectedDocText.setText(selectedDocText);
        this.selectedDocText.setCaretPosition(0);
    }

    public void setAfterDocText(String afterDocText) {
        this.afterDocText.setText(afterDocText);
        this.afterDocText.setCaretPosition(0);
    }

    public void setHighliter(boolean setIt) {

        for (JTextPane aPane : allTextPanes) {
            if(setIt) {
                Highlighter highlighter = aPane.getHighlighter();
                Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
                highlighter.removeAllHighlights();
                String text = aPane.getText();

                for (String aWord : searchTerms) {
                    List<IndexWrapper> indexes = findIndexesInTextForKeyWord(text, aWord);

              /*  int p0 = text.indexOf(aWord);
                int p1 = p0 + aWord.length();*/
                    logger.debug("Indexes found: " + indexes.size());
                    for (IndexWrapper anIndex : indexes) {

                        try {
                            highlighter.addHighlight(anIndex.getStart(), anIndex.getEnd(), painter );
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }

                    }

                }

            }else {
                Highlighter highlighter = aPane.getHighlighter();
                highlighter.removeAllHighlights();
            }


        }





    }

    public List<IndexWrapper>findIndexesInTextForKeyWord(String text, String keyword) {

        String regex = "(?i)\\b"+keyword+"\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        List<IndexWrapper> wrappers = new ArrayList<IndexWrapper>();

        while(matcher.find() == true){
            int end = matcher.end();
            int start = matcher.start();
            IndexWrapper wrapper = new IndexWrapper(start, end);
            wrappers.add(wrapper);
        }
        return wrappers;
    }


    public void setDocumentText(String docText) {
        this.fullTextPane.setText(docText);
        this.fullTextPane.setCaretPosition(0);
    }

    public void setViewPane(int thePane) {
        CardLayout c1 = new CardLayout();
        c1 = (CardLayout) (cardPanel.getLayout());
        int sel = viewType.getSelectedIndex();
        logger.debug("Pane Index: " + sel);
        logger.debug("C1 Class: " + c1.getClass().toString());
        switch (thePane) {

            case 0:
                c1.show(cardPanel, "splitSelPanel");
                enableChunkButtons(false);
                logger.debug("Case0");
                break;
            case 1:
                c1.show(cardPanel, "splitDocPanel");
                enableChunkButtons(true);
                logger.debug("Case1");
                break;
            case 2:
                c1.show(cardPanel, "fullDocPanel");
                enableChunkButtons(false);
                logger.debug("Case2");
                break;
            default:
                c1.show(cardPanel, "splitSelPanel");
                enableChunkButtons(false);
                logger.debug("Casedef");
                break;

        }
        logger.debug("reval");
        cardPanel.revalidate();

    }

    public void disableComponents() {
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);

        toggleViewButton.setEnabled(false);
        viewType.setEnabled(false);

    }

    private void createUIComponents() {

        this.viewType = new JComboBox(viewTypeCombo);



        // TODO: place custom component creation code here
    }
}
