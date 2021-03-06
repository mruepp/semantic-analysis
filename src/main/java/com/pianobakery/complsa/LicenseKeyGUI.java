/*
 * License Key GUI file, for license input, validation, activation and display details.
 */
package com.pianobakery.complsa;

import com.license4j.ActivationStatus;
import com.license4j.License;
import com.license4j.LicenseValidator;
import com.license4j.ValidationStatus;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * LicenseKeyGUI.
 *
 */
public class LicenseKeyGUI extends javax.swing.JDialog {

    /**
     * MODIFY BELOW THIS LINE ACCORDING TO YOUR PRODUCT AND LICENSE DETAILS.
     * **********************************************************************
     *
     * You should modify following variables according to your product and
     * license details. Required variables must be defined, all others optional
     * and depends on your licensing implementation.
     */
    /* REQUIRED. There is a unique public key for each product. */
    private final String publicKey
            = "30819f300d06092a864886f70d010101050003818d003081893032301006\n" +
            "072a8648ce3d02002EC311215SHA512withECDSA106052b81040006031e0\n" +
            "00417a21a003475e781d554439b059dd2a73e8c0745760f14fa9f36bb50G\n" +
            "02818100c070ca58eaa4c30c545b9fa8b403619639f7a26234dc40f6d00f\n" +
            "217c1d24f64b815cc5dc68511e8c2b6ed91761e259c60b773156a11cb530\n" +
            "68336b5639de80b0ba16beb6d06c53a8105cad092b51c3ac7ec586bd086b\n" +
            "49dabdbbb047922a23e103RSA4102413SHA512withRSA2b8cf3ca676f4b3\n" +
            "368ed51e3e78cd2c57155d8ebc680bef4dbfa2ec3d7cd5f510203010001\n";

    /* REQUIRED. It is used in license key validation. */
    private final String internalString = "semanticanalyzer-string";

    private final String trialLicKey = "4T9FR-FIAI5-S9RE6-VL7FK-APHEM";

    /**
     * OPTIONAL. If you are using name and company in license validation, you
     * should also add name and company input fields for user to input; then set
     * these two variables.
     */
    private final String nameforValidation = null;
    private final String companyforValidation = null;

    /**
     * OPTIONAL. If you generated a node-locked license key. Details on integer
     * value is explained in user guide. 0 means no hardware ID validation.
     */
    private final int hardwareIDMethod = 0;

    /* REQUIRED. The product ID is used to validate activated license text. */
    private final String productID = "complsa-id";

    /**
     * OPTIONAL. If you are using edition and version validation, you should
     * define this product's edition and version here.
     */
    private final String productEdition = null;
    private final String productVersion = null;

    /**
     * OPTINAL. If it is null, new Date() is called and used. If you want to use
     * another custom date/time source, you can define date here.
     */
    private final Date currentDate = null;

    /**
     * OPTIONAL. This variable is used to check for valid maintenance period. If
     * you defined a maintenance period in license then it will be valid for
     * versions before maintenance period expires.
     */
    private final Date currentVersionReleaseDate = null;
    /**
     * ********************************************************************
     * MODIFY ABOVE THIS LINE ACCORDING TO YOUR PRODUCT AND LICENSE DETAILS.
     */
    /**
     * This is the file in which license key is saved. It is in user's home
     * directory on disk. You may save it on database or in any other file if
     * you like.
     */
    private final String licenseKeyFileOnDisk = System.getProperty("user.home") + File.separator + "SemanticAnalyzer-Lic-Key.lic";

    /**
     * This is the file in which activated license text is saved. It is in
     * user's home directory on disk. You may save it on database or in any
     * other file if you like.
     */
    private final String licenseTextFileOnDisk = System.getProperty("user.home") + File.separator + "SemanticAnalyzer-Lic-Text.lic";

    /* License object; validate method return this object. */
    private License licenseObject;

    /* LicenseKeyGUI constructor. */
    public LicenseKeyGUI(JFrame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocationRelativeTo(parent);

        /**
         * We hide activate button first, then will show only if license key is
         * valid but not activated.
         */
        activatejButton.setVisible(false);
        useTrialButton.setVisible(true);
    }

    @Override
    public void dispose() {
        /**
         * We easily override dispose() method here to exit main application if
         * license is not valid. Maybe you can display an informative error
         * message before exiting application.
         */
        if (licenseObject != null && licenseObject.getValidationStatus() != ValidationStatus.LICENSE_VALID) {
            System.exit(-1);
        }

        super.dispose();
    }

    /**
     * Method to check for license key or license text on disk.
     *
     * First it checks for activated license text on disk. If license text file
     * is not found on disk, then it checks for license key file on disk.
     * Finally it updates GUI fields and returns license object.
     */
    public License checkLicense() {
        try {
            /**
             * We use Apache commons-io (FileUtils class) to easily read string
             * from file.
             */
            String licenseTextString = FileUtils.readFileToString(new File(licenseTextFileOnDisk));

            licenseObject = LicenseValidator.validate(
                    licenseTextString,
                    publicKey,
                    productID,
                    productEdition,
                    productVersion,
                    currentDate,
                    currentVersionReleaseDate
            );

            updateGUIFieldsWithLicenseObject();

            return licenseObject;

        } catch (IOException ex) {
            Logger.getLogger(LicenseKeyGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            String licenseKeyString = FileUtils.readFileToString(new File(licenseKeyFileOnDisk));

            licenseObject = LicenseValidator.validate(
                    licenseKeyString,
                    publicKey,
                    internalString,
                    nameforValidation,
                    companyforValidation,
                    hardwareIDMethod
            );

            updateGUIFieldsWithLicenseObject();

            return licenseObject;

        } catch (IOException ex) {
            Logger.getLogger(LicenseKeyGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        /**
         * If there is no license key file or license text file, then it returns
         * null here.
         */
        return null;
    }

    /* This method updates fields on window with license object. */
    private void updateGUIFieldsWithLicenseObject() {
        if (licenseObject != null) {
            switch (licenseObject.getValidationStatus()) {
                case LICENSE_VALID:
                    if (licenseObject.isActivationCompleted()) {
                        licenseStatusjTextField.setText("VALID - ACTIVATED");
                        licenseStatusjTextField.setForeground(Color.BLUE);
                        setVisible(false);
                        useTrialButton.setVisible(false);
                        activatejButton.setVisible(false);
                    } else {
                        licenseStatusjTextField.setText("VALID - NOT ACTIVATED");
                        licenseExpirationDatejTextField.setText("Days Left for Activation: " + licenseObject.getLicenseActivationDaysRemaining(null));
                        licenseStatusjTextField.setForeground(Color.red);
                        licenseExpirationDatejTextField.setForeground(Color.red);

                        /**
                         * License key is valid, but it is not activated so show
                         * "activate" button.
                         */
                        activatejButton.setVisible(true);
                        useTrialButton.setVisible(false);
                    }

                    break;
                default:
                    licenseStatusjTextField.setText(licenseObject.getValidationStatus().toString());
                    licenseStatusjTextField.setForeground(Color.red);
            }

            /**
             * Since we use a license key with enabled activation and activation
             * return type is a license text, we can check if license text is
             * null. If it is not null, then set user information fields from
             * license text.
             */
            if (licenseObject.getLicenseText() != null) {
                if (licenseObject.getLicenseText().getLicenseExpireDate() != null) {
                    licenseExpirationDatejTextField.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(licenseObject.getLicenseText().getLicenseExpireDate()));

                    /* If license expire date is before current date, then set expiration date field color red. */
                    if (licenseObject.getLicenseText().getLicenseExpireDate().before(new Date())) {
                        licenseExpirationDatejTextField.setForeground(Color.red);
                    } else {
                        licenseExpirationDatejTextField.setForeground(Color.BLUE);
                    }
                }

                /* Here set user information fields if available in license. */
                namejTextField.setText(licenseObject.getLicenseText().getUserFullName() != null ? licenseObject.getLicenseText().getUserFullName() : "");
                emailjTextField.setText(licenseObject.getLicenseText().getUserEMail() != null ? licenseObject.getLicenseText().getUserEMail() : "");
                companyjTextField.setText(licenseObject.getLicenseText().getUserCompany() != null ? licenseObject.getLicenseText().getUserCompany() : "");
            } else {
                /**
                 * License Text is null, means it is not activated. So we clear
                 * GUI fields.
                 */
                namejTextField.setText("");
                emailjTextField.setText("");
                companyjTextField.setText("");
            }
        } else {
            licenseStatusjTextField.setText("NO LICENSE AVAILABLE");
            licenseStatusjTextField.setForeground(Color.red);

            /**
             * No license; clear GUI fields if some information left from
             * previous license...
             */
            namejTextField.setText("");
            emailjTextField.setText("");
            companyjTextField.setText("");
        }
    }

    /**
     * Following "Generated Code" is generated by Netbeans form editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        licenseExpirationDatejTextField = new javax.swing.JTextField();
        licenseStatusjTextField = new javax.swing.JTextField();
        closejButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        namejTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        emailjTextField = new javax.swing.JTextField();
        companyjTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        changeProductKeyjButton = new javax.swing.JButton();
        activatejButton = new javax.swing.JButton();
        useTrialButton = new javax.swing.JButton();
        buyButton = new javax.swing.JButton();
        progressjLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Licensing");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Product License Information"));

        jLabel1.setText("License Status:");

        jLabel2.setText("License Expiration Date:");

        licenseExpirationDatejTextField.setEditable(false);

        licenseStatusjTextField.setEditable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(licenseExpirationDatejTextField)
                                        .addComponent(licenseStatusjTextField))
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(licenseStatusjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(licenseExpirationDatejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );

        closejButton.setText("Close");
        closejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closejButtonActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Product is Licensed to"));

        jLabel4.setText("Name:");

        namejTextField.setEditable(false);

        jLabel5.setText("e-mail:");

        emailjTextField.setEditable(false);

        companyjTextField.setEditable(false);

        jLabel6.setText("Company:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(87, 87, 87)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(namejTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(emailjTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(companyjTextField, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(namejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(emailjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel6)
                                        .addComponent(companyjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );

        changeProductKeyjButton.setText("Change Product Key");
        changeProductKeyjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeProductKeyjButtonActionPerformed(evt);
            }
        });

        useTrialButton.setText("Use 30 Day Trial");
        useTrialButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                useTrialKeyButtonActionPerformed(e);
            }
        });

        buyButton.setText("Buy License");
        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buyButtonActionPerformed(e);
            }
        });

        activatejButton.setText("Activate");
        activatejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                activatejButtonActionPerformed(evt);
            }
        });

        progressjLabel.setForeground(Color.red);
        progressjLabel.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()

                                    .addComponent(progressjLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                                    .addGap(18, 18, 18)
                                    .addComponent(buyButton)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(useTrialButton)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(activatejButton)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(changeProductKeyjButton)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(closejButton))
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(closejButton)
                                        .addComponent(changeProductKeyjButton)
                                        .addComponent(activatejButton)
                                        .addComponent(progressjLabel)
                                        .addComponent(useTrialButton)
                                        .addComponent(buyButton))
                                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {useTrialButton, activatejButton, changeProductKeyjButton, closejButton});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void changeProductKeyjButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeProductKeyjButtonActionPerformed
        /**
         * Basically we show an input dialog to get license key. It will be
         * better to use a JDialog with required license key input fields.
         */
        String key = JOptionPane.showInputDialog(null, "Enter License Key", "License Key", JOptionPane.QUESTION_MESSAGE);

        if (key != null) {
            key = key.trim(); // the license key (trim it because user may copy/paste with spaces etc)

            /**
             * First validate license and get a temporary license object to
             * check for validation status later.
             */
            License temporaryLicenseObject = LicenseValidator.validate(
                    key,
                    publicKey,
                    internalString,
                    nameforValidation,
                    companyforValidation,
                    hardwareIDMethod
            );

            /**
             * If given license key is valid, then save it on disk, and update
             * GUI fields.
             */
            if (temporaryLicenseObject.getValidationStatus() == ValidationStatus.LICENSE_VALID) {
                licenseObject = temporaryLicenseObject;

                try {
                    /**
                     * We use Apache commons-io (FileUtils class) to easily save
                     * string to file.
                     */
                    FileUtils.writeStringToFile(new File(licenseKeyFileOnDisk), key);

                    /**
                     * Since license key is changed delete license text file if
                     * exists on disk left from previous license key.
                     */
                    FileUtils.deleteQuietly(new File(licenseTextFileOnDisk));
                } catch (IOException ex) {
                    Logger.getLogger(LicenseKeyGUI.class.getName()).log(Level.SEVERE, null, ex);
                }

                updateGUIFieldsWithLicenseObject();
            } else {
                /**
                 * If given license is not valid, display an error message.
                 */
                JOptionPane.showMessageDialog(null, "License error: " + temporaryLicenseObject.getValidationStatus(), "License Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_changeProductKeyjButtonActionPerformed

    private void buyButtonActionPerformed(ActionEvent evt) {
        System.out.println("Buying");
        try {
            openWebpage(new URI("http://www.oyonoko.com/semanticsearch/buy"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void  useTrialKeyButtonActionPerformed(ActionEvent evt){

        /**
         * First validate license and get a temporary license object to
         * check for validation status later.
         */
        License temporaryLicenseObject = LicenseValidator.validate(
                trialLicKey,
                publicKey,
                internalString,
                nameforValidation,
                companyforValidation,
                hardwareIDMethod
        );

        /**
         * If given license key is valid, then save it on disk, and update
         * GUI fields.
         */
        if (temporaryLicenseObject.getValidationStatus() == ValidationStatus.LICENSE_VALID) {
            licenseObject = temporaryLicenseObject;

            try {
                /**
                 * We use Apache commons-io (FileUtils class) to easily save
                 * string to file.
                 */
                FileUtils.writeStringToFile(new File(licenseKeyFileOnDisk), trialLicKey);

                /**
                 * Since license key is changed delete license text file if
                 * exists on disk left from previous license key.
                 */
                FileUtils.deleteQuietly(new File(licenseTextFileOnDisk));
            } catch (IOException ex) {
                Logger.getLogger(LicenseKeyGUI.class.getName()).log(Level.SEVERE, null, ex);
            }

            updateGUIFieldsWithLicenseObject();


            /**
             * We use a SwingWorker here because it will connect to license server
             * for activation, and it may take 2-3 seconds.
             */
            SwingWorker<License, Void> worker = new SwingWorker<License, Void>() {
                @Override
                protected void done() {
                    try {
                        /**
                         * Again we get license object to a temporary object to
                         * check for ActivationStatus.
                         */
                        License temporaryLicenseObject = (License) get();

                        /**
                         * If it is successfully activated save on disk and update
                         * GUI fields.
                         */
                        if (temporaryLicenseObject.getActivationStatus() == ActivationStatus.ACTIVATION_COMPLETED) {
                            licenseObject = temporaryLicenseObject;

                            try {
                                /**
                                 * We use Apache commons-io (FileUtils class) to
                                 * easily save string to file.
                                 *
                                 * licenseObject.getLicenseString() method returns
                                 * activated license string.
                                 */
                                FileUtils.writeStringToFile(new File(licenseTextFileOnDisk), licenseObject.getLicenseString());
                            } catch (IOException ex) {
                                Logger.getLogger(LicenseKeyGUI.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            updateGUIFieldsWithLicenseObject();
                        } else {
                            /**
                             * If activation cannot be completed, display an error
                             * message.
                             */
                            JOptionPane.showMessageDialog(null, "License activation error: " + temporaryLicenseObject.getActivationStatus(), "Activation Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(LicenseKeyGUI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(LicenseKeyGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    progressjLabel.setText("");

                    /**
                     * Activation progress is complete, enable buttons again.
                     */
                    activatejButton.setEnabled(true);
                    changeProductKeyjButton.setEnabled(true);
                    JOptionPane.showMessageDialog(null,"Please restart to enable the license", "Restart...", JOptionPane.INFORMATION_MESSAGE);
                }

                @Override
                protected License doInBackground() {
                    /**
                     * Since example licenses are on Online.License4J the method
                     * below will activate on Online.License4J when autoActivate
                     * method is called without a license server address.
                     */
                    return LicenseValidator.autoActivate(licenseObject);

                    /**
                     * If you want to test your own "Auto License Generation and
                     * Activation Server" you should give its address as argument
                     * like below.
                     */
                    //return LicenseValidator.autoActivate(licenseObject, "http://YourServer.com/algas/autoactivate");
                }
            };
            worker.execute();

            progressjLabel.setText("Activating ...");

            /**
             * It is good to disable "activate" and "change product key" buttons
             * while activation is in progress.
             */
            activatejButton.setEnabled(false);
            changeProductKeyjButton.setEnabled(false);



        } else {
            /**
             * If given license is not valid, display an error message.
             */
            JOptionPane.showMessageDialog(null, "License error: " + temporaryLicenseObject.getValidationStatus(), "License Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void closejButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closejButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closejButtonActionPerformed

    private void activatejButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_activatejButtonActionPerformed
        /**
         * We use a SwingWorker here because it will connect to license server
         * for activation, and it may take 2-3 seconds.
         */
        SwingWorker<License, Void> worker = new SwingWorker<License, Void>() {
            @Override
            protected void done() {
                try {
                    /**
                     * Again we get license object to a temporary object to
                     * check for ActivationStatus.
                     */
                    License temporaryLicenseObject = (License) get();

                    /**
                     * If it is successfully activated save on disk and update
                     * GUI fields.
                     */
                    if (temporaryLicenseObject.getActivationStatus() == ActivationStatus.ACTIVATION_COMPLETED) {
                        licenseObject = temporaryLicenseObject;

                        try {
                            /**
                             * We use Apache commons-io (FileUtils class) to
                             * easily save string to file.
                             *
                             * licenseObject.getLicenseString() method returns
                             * activated license string.
                             */
                            FileUtils.writeStringToFile(new File(licenseTextFileOnDisk), licenseObject.getLicenseString());
                        } catch (IOException ex) {
                            Logger.getLogger(LicenseKeyGUI.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        updateGUIFieldsWithLicenseObject();
                    } else {
                        /**
                         * If activation cannot be completed, display an error
                         * message.
                         */
                        JOptionPane.showMessageDialog(null, "License activation error: " + temporaryLicenseObject.getActivationStatus(), "Activation Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(LicenseKeyGUI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(LicenseKeyGUI.class.getName()).log(Level.SEVERE, null, ex);
                }

                progressjLabel.setText("");

                /**
                 * Activation progress is complete, enable buttons again.
                 */
                activatejButton.setEnabled(true);
                changeProductKeyjButton.setEnabled(true);
                JOptionPane.showMessageDialog(null, "Please restart to enable the license","Restart...", JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            protected License doInBackground() {
                /**
                 * Since example licenses are on Online.License4J the method
                 * below will activate on Online.License4J when autoActivate
                 * method is called without a license server address.
                 */
                return LicenseValidator.autoActivate(licenseObject);

                /**
                 * If you want to test your own "Auto License Generation and
                 * Activation Server" you should give its address as argument
                 * like below.
                 */
                //return LicenseValidator.autoActivate(licenseObject, "http://YourServer.com/algas/autoactivate");
            }
        };
        worker.execute();

        progressjLabel.setText("Activating ...");

        /**
         * It is good to disable "activate" and "change product key" buttons
         * while activation is in progress.
         */
        activatejButton.setEnabled(false);
        changeProductKeyjButton.setEnabled(false);
    }//GEN-LAST:event_activatejButtonActionPerformed

    public static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton activatejButton;
    private javax.swing.JButton buyButton;
    private javax.swing.JButton useTrialButton;
    private javax.swing.JButton changeProductKeyjButton;
    private javax.swing.JButton closejButton;
    private javax.swing.JTextField companyjTextField;
    private javax.swing.JTextField emailjTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField licenseExpirationDatejTextField;
    private javax.swing.JTextField licenseStatusjTextField;
    private javax.swing.JTextField namejTextField;
    private javax.swing.JLabel progressjLabel;
    // End of variables declaration//GEN-END:variables
}
