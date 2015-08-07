package com.pianobakery.complsa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by michael on 07.08.15.
 */
public class InfoPane extends JFrame {

    private JPanel rootPanel;
    private JTextArea infoTextArea;
    private JButton OKButton;
    private JFrame frame;
    private String title;
    private String text;


    public InfoPane(String title, String text) throws HeadlessException {
        super(title);
        frame = new JFrame("Title");
        this.text = text;
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

    }



    public void showGui() {

        frame.setContentPane(this.rootPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setTitle(title);
        Dimension thePanelDim = new Dimension(600,800);
        this.rootPanel.setPreferredSize(thePanelDim);

        int frameWidth = 600;
        int frameHeight = 800;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((int) screenSize.getWidth() / 2 - frameWidth / 2, (int) screenSize.getHeight() / 4 - frameHeight / 4, frameWidth, frameHeight);
        frame.pack();
        frame.setVisible(true);
        infoTextArea.setText(text);
        infoTextArea.setCaretPosition(0);

    }


}
