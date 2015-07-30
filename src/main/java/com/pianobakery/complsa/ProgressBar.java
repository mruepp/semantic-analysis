package com.pianobakery.complsa;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

public class ProgressBar extends JDialog {
    private JPanel contentPane;
    private JButton buttonCancel;
    private JProgressBar progressBar1;
    private JLabel textField;
    private Boolean cancel;


    final static Logger logger = Logger.getLogger(MainGui.class);




    public void setProgressBarValue(int value) {
        this.progressBar1.setValue(value);
    }

    public void setProgressBarIndeterminate(boolean value) {
        this.progressBar1.setIndeterminate(value);
    }

    public void setProgressBarMax(int value) {
        this.progressBar1.setMaximum(value);
    }

    public void setTextField(String value) {
        this.textField.setText(value);
    }

    public Boolean getButtonCancel() {
        return cancel;
    }

    public ProgressBar(boolean withCancelButton) {
        cancel = false;
        progressBar1.setMinimum(0);
        progressBar1.setMaximum(100);
        progressBar1.setValue(100);
        setAlwaysOnTop(false);
        //contentPane.setSize(200,50);
        setContentPane(contentPane);
        setModal(true);

        if(withCancelButton) {

            getRootPane().setDefaultButton(buttonCancel);
            buttonCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    logger.debug("Action Cancel");
                    onCancel();
                }
            });

        } else {
            buttonCancel.setVisible(false);
        }



// call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }


    private void onCancel() {
        logger.debug("Cancel");
        cancel = true;

        dispose();
    }




}
