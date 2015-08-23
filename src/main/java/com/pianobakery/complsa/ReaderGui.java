package com.pianobakery.complsa;


import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private JLabel beforeSelLabel;
    private JLabel selLabel;
    private JLabel afterSelLabel;
    private JLabel selChunkLabel;
    private JLabel afterChunkLabel;
    private JLabel beforeChunkLabel;
    private JLabel selFullDocLabel;
    private JButton formatTextButton;
    private JTextField textField1;
    private static String[] viewTypeCombo = {"Result Splitscreen","Document Splitscreen","Document"};
    private MainGui theMainGui;
    private String[] searchTerms;
    private List<JTextPane> allTextPanes = new ArrayList<JTextPane>();


    private DocSearchModel theModel;
    private JFrame frame;


    public void setBeforeSelLabel(String text) {
        this.beforeSelLabel.setText(text);
    }

    public void setSelLabel(String text) {
        this.selLabel.setText(text);
    }

    public void setAfterSelLabel(String text) {
        this.afterSelLabel.setText(text);
    }

    public void setSelChunkLabel(String text) {
        this.selChunkLabel.setText(text);
    }

    public void setAfterChunkLabel(String text) {
        this.afterChunkLabel.setText(text);
    }

    public void setBeforeChunkLabel(String text) {
        this.beforeChunkLabel.setText(text);
    }

    public void setSelFullDocLabel(String text) {
        this.selFullDocLabel.setText(text);
    }

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

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        //frame.setMaximumSize(screenSize);
        Dimension thePanelDim = new Dimension(1024,700);
        logger.debug("Screen height: " + screenSize.height);
        logger.debug("Screen width: " + screenSize.width);

        if (screenSize.height < thePanelDim.height) {
            logger.debug("Screen height is smaller");
            thePanelDim.setSize(800, screenSize.height);
        }
        if (screenSize.width < thePanelDim.width) {
            logger.debug("Screen width is smaller");
            thePanelDim.setSize(screenSize.width,thePanelDim.height);
        }
        thePanelDim.setSize(1024,screenSize.height/1.5);

        //frame.setMinimumSize(thePanelDim);
        frame.setLocationRelativeTo(null);
        this.rootPanel.setMaximumSize(screenSize);
        this.rootPanel.setPreferredSize(thePanelDim);
        //this.rootPanel.setMinimumSize(thePanelDim);

        int frameWidth = 1024;
        int frameHeight = 700;

        frame.setBounds((int) screenSize.getWidth() / 2 - frameWidth / 2, (int) screenSize.getHeight() / 4 - frameHeight / 4, frameWidth, frameHeight);
        frame.pack();
        frame.setVisible(true);


        /*frame.addWindowFocusListener(new WindowAdapter() {
            public void windowGainedFocus(WindowEvent e) {
                logger.debug("Window gained focus");
                buttonPanel.requestFocusInWindow();
            }

        });*/

        frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                logger.debug("Window gained focus");
                buttonPanel.requestFocusInWindow();

            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                logger.debug("Window lost focus");
            }
        });



        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke('W', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "CTRL + W");
        frame.getRootPane().getActionMap().put("CTRL + W", closeWindow());

        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),"CTRL + S");
        frame.getRootPane().getActionMap().put("CTRL + S", toggleViewAction());

        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke('T', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),"CTRL + T");
        frame.getRootPane().getActionMap().put("CTRL + T", formatFontAction());


        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(38, 0),"UP");
        frame.getRootPane().getActionMap().put("UP", selUpAction());

        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(40, 0),"DOWN");
        frame.getRootPane().getActionMap().put("DOWN", selDownAction());

        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(37, 0),"LEFT");
        frame.getRootPane().getActionMap().put("LEFT", docUpAction());

        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(39, 0),"RIGHT");
        frame.getRootPane().getActionMap().put("RIGHT", docDownAction());




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
                    selectionUpMethod();


                }
            });


        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectionDownMethod();

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
                documentUpMethod();


            }
        });

        documentDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                documentDownMethod();

            }
        });

        toggleViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                toggleView();


            }
        });

        highlightSelectedTermsCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setHighliter(highlightSelectedTermsCheckBox.isSelected());
            }
        });

        formatTextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                formatFontMethod();

            }});

        if (searchTerms != null) {
            setHighliter(highlightSelectedTermsCheckBox.isSelected());
        }

        Font font = new Font("Arial",Font.PLAIN, 12);
        logger.debug("Default Font : " + font);
        beforeText.setFont(font);
        selectedText.setFont(font);
        afterText.setFont(font);

        beforeDocText.setFont(font);
        selectedDocText.setFont(font);
        afterDocText.setFont(font);

        fullTextPane.setFont(font);


    }

    private Action formatFontAction() {
        return new AbstractAction("formatFont") {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                formatFontMethod();
            }
        };
    }

    private Action closeWindow() {
        return new AbstractAction("Closing Window") {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Und tschüss");
                setFrameVisible(false);
            }
        };
    }

    private Action toggleViewAction() {
        return new AbstractAction("Toggle View") {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                toggleView();
            }
        };
    }

    private Action docUpAction() {
        return new AbstractAction("Docup") {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                documentUpMethod();
            }
        };
    }

    private Action docDownAction() {
        return new AbstractAction("Docdown") {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                documentDownMethod();
            }
        };
    }

    private Action selUpAction() {
        return new AbstractAction("Selup") {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                selectionUpMethod();
            }
        };
    }

    private Action selDownAction() {
        return new AbstractAction("Seldown") {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                selectionDownMethod();
            }
        };
    }


    private void formatFontMethod() {
        JFontChooser fontChooser = new JFontChooser();
        int result = fontChooser.showDialog(null);
        if (result == JFontChooser.OK_OPTION) {
            Font font = fontChooser.getSelectedFont();
            logger.debug("Selected Font : " + font);
            beforeText.setFont(font);
            selectedText.setFont(font);
            afterText.setFont(font);

            beforeDocText.setFont(font);
            selectedDocText.setFont(font);
            afterDocText.setFont(font);

            fullTextPane.setFont(font);


        }


    }

    public void selectionUpMethod() {
        int outrow = theMainGui.getSelectedDocTableRow();
        logger.debug("The Row: " + outrow);
        if (outrow != -1 || outrow >= 1) {
            theMainGui.setSelectedDocTableRow(outrow - 1);
        }

    }

    public void selectionDownMethod() {
        int inrow = theMainGui.getSelectedDocTableRow();

        if (inrow != -1 || inrow > theModel.getRowCount() - 1) {
            theMainGui.setSelectedDocTableRow(inrow + 1);
        }

    }

    public void documentUpMethod() {
        theMainGui.setDocReaderContent(-1);
        setHighliter(highlightSelectedTermsCheckBox.isSelected());
    }

    public void documentDownMethod() {
        theMainGui.setDocReaderContent(1);
        setHighliter(highlightSelectedTermsCheckBox.isSelected());

    }

    private void toggleView(){

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
