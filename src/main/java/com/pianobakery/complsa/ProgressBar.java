package com.pianobakery.complsa;

import javax.swing.*;
import java.awt.event.*;

public class ProgressBar extends JDialog {
    private JPanel contentPane;
    private JButton buttonCancel;
    private JProgressBar progressBar1;
    private JTextField textField1;
    private Boolean cancel;

    public void setProgressBarValue(int value) {
        this.progressBar1.setValue(value);
    }

    public void setProgressBarMax(int value) {
        this.progressBar1.setMaximum(value);
    }

    public void setTextField(String value) {
        this.textField1.setEditable(false);
        this.textField1.setText(value);
    }

    public Boolean getButtonCancel() {
        return cancel;
    }

    public ProgressBar() {
        cancel = false;
        progressBar1.setMinimum(0);
        progressBar1.setMaximum(100);
        progressBar1.setValue(50);
        setAlwaysOnTop(true);
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonCancel);
        //pack();
        //setVisible(true);




        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Action Cancel");
                onCancel();
            }
        });

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
        System.out.println("Cancel");
        cancel = true;
// add your code here if necessary
        dispose();
    }

    /*public static void main(String[] args) {

        ProgressBar dialog = new ProgressBar();
        dialog.pack();
        dialog.setVisible(true);
        //System.exit(0);
    }*/
}
