/*
 * 
 *   Copyright © 2019 Eduardo Vindas Cordoba. All rights reserved.
 *  
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 * 
 */
package com.aeongames.blockchain.ui;

import com.aeongames.expediente.FileRecord;
import com.aeongames.expediente.Person;
import com.aeongames.logger.LoggingHelper;
import java.nio.file.Paths;
import java.util.logging.Level;
import javax.swing.JFileChooser;

import com.aeongames.crypto.signature.DigitalSignatureHelper;
import com.aeongames.edi.utils.File.properties_File;
import com.aeongames.edi.utils.text.LabelText;
import com.aeongames.expediente.Expediente;
import java.awt.Color;
import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.util.AbstractMap;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class RecordP extends javax.swing.JPanel {

    private javax.swing.JLabel txtoutput;
    private PersonModel PersonL;

    /**
     * Creates new form RecordP
     */
    public RecordP() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtrecordname = new com.aeongames.edi.utils.visual.translucenttextfield();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtdescript = new com.aeongames.edi.utils.visual.TranslucentTextArea();
        jLabel4 = new javax.swing.JLabel();
        txtfile = new com.aeongames.edi.utils.visual.translucenttextfield();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtowner = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        txtrequester = new javax.swing.JComboBox<>();
        inchain = new javax.swing.JCheckBox();

        setOpaque(false);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setOpaque(false);

        jLabel1.setText("New Record");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jLabel1)
                .addGap(2, 2, 2))
        );

        jPanel2.setOpaque(false);

        jLabel2.setText("Name:");

        jLabel3.setText("Description:");

        jScrollPane1.getViewport().setOpaque(false);
        jScrollPane1.setOpaque(false);

        txtdescript.setColumns(20);
        txtdescript.setRows(2);
        jScrollPane1.setViewportView(txtdescript);

        jLabel4.setText("Record File:");

        txtfile.setEditable(false);

        jButton1.setText("select File");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel5.setText("Afected:");

        txtowner.setToolTipText("");
        txtowner.setEnabled(false);
        txtowner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtownerexpPersChanged(evt);
            }
        });

        jLabel6.setText("Medic/Agent:");

        txtrequester.setEnabled(false);
        txtrequester.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtrequesterexpPersChanged(evt);
            }
        });

        inchain.setText("commited to the chain");
        inchain.setEnabled(false);
        inchain.setOpaque(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtfile, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jScrollPane1))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(37, 37, 37)
                            .addComponent(txtrecordname, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtowner, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtrequester, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(inchain))
                .addGap(21, 21, 21))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtrecordname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(txtrequester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtowner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inchain)
                .addGap(8, 8, 8))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtownerexpPersChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtownerexpPersChanged

    }//GEN-LAST:event_txtownerexpPersChanged

    private void txtrequesterexpPersChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtrequesterexpPersChanged

    }//GEN-LAST:event_txtrequesterexpPersChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        if (chooser.showOpenDialog((Frame) SwingUtilities.getWindowAncestor(this)) == JFileChooser.APPROVE_OPTION) {
            txtfile.setText(chooser.getSelectedFile().toPath().toAbsolutePath().toString());
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    void setPersonModel(final PersonModel Personmod) {
        //this should only be set once. should we deny to set it more than a single time? hmmm not sure... 
        //TODO: analize check which is the best way to adapt. 
        if (PersonL == null) {
            PersonL = Personmod;
            txtrequester.setModel(PersonL.getcopy());
            txtowner.setModel(PersonL.getcopy());
            txtrequester.setEnabled(true);
            txtowner.setEnabled(true);
        } else {
            //PersonL.donotify();
        }
    }

    public boolean isready() {
        return !txtfile.getText().trim().isEmpty()
                && !txtrecordname.getText().trim().isEmpty()
                && !txtdescript.getText().trim().isEmpty()
                && txtowner.getSelectedItem() != null
                && txtrequester.getSelectedItem() != null;
    }

    public FileRecord asRecord(KeyStore ks) {
        if (isready()) {
            try {
                return new FileRecord(txtowner.getItemAt(txtowner.getSelectedIndex()),
                        txtrequester.getItemAt(txtrequester.getSelectedIndex()),
                        Paths.get(txtfile.getText().trim()),
                        txtrecordname.getText().trim(),
                        txtdescript.getText().trim(), ks != null ? cert(ks, txtowner.getItemAt(txtowner.getSelectedIndex()), Paths.get(txtfile.getText().trim())) : null);
            } catch (IOException ex) {
                LoggingHelper.getAClassLogger("UI").log(Level.SEVERE, "Error detected attempting to Crate a File Record.!");
            }
        }
        return null;
    }

    AbstractMap.SimpleImmutableEntry<Boolean, String> checkreadynessfor(Expediente<FileRecord> SelectedExpediente) {
        try {
            return SelectedExpediente.CheckRecord(new FileRecord(txtowner.getItemAt(txtowner.getSelectedIndex()),
                    txtrequester.getItemAt(txtrequester.getSelectedIndex()),
                    Paths.get(txtfile.getText().trim()),
                    txtrecordname.getText().trim(),
                    txtdescript.getText().trim(), null));
        } catch (IOException ex) {
            return new AbstractMap.SimpleImmutableEntry<>(false, "Testing the Readyness");
        }
    }

    private byte[] cert(KeyStore ks, Person person, Path File) {
        try {
            return DoCertTest(ks, person, Files.readAllBytes(File));
        } catch (IOException ex) {
            return null;
        }
    }

    private byte[] DoCertTest(KeyStore ks, Person person, byte[] Data) {
        byte[] signedvalue = null;
        if (person.getCertificate() != null) {
            if (ks != null) {
                properties_File settings;
                try {
                    settings = new properties_File(ViktorUI.PROPSFILE);
                    Signature RSASHA = DigitalSignatureHelper.getSignatureObject_toSign(ks,
                            settings.getProperty("SignatureKeyAlias"),
                            settings.getProperty("signmethod"),
                            new KeyStore.PasswordProtection("".toCharArray()));
                    if (RSASHA != null) {//--> should not be
                        RSASHA.update(Data);
                        signedvalue = RSASHA.sign();
                        RSASHA = DigitalSignatureHelper.getSignatureObject_toVerify(ks.getProvider(),
                                settings.getProperty("signmethod"),
                                person.getCertificate());
                        RSASHA.update(Data);
                        if (RSASHA.verify(signedvalue)) {
                            SetOutputMessage("Certificate Confirmed.");
                            return signedvalue;
                        }
                    }
                } catch (IOException | UnrecoverableEntryException | KeyStoreException | NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
                    LoggingHelper.getAClassLogger("UI").log(java.util.logging.Level.SEVERE,
                            "unable to Confirm Signature", ex);
                    SetOutputMessage("unable to Confirm Signature");
                }
            }
        }
        return signedvalue;
    }

    private void SetOutputMessage(String message) {
        LabelText.wrapLabelText(txtoutput, message);
        txtoutput.setForeground(Color.red);
    }

    public void setoutputcomponent(JLabel compt) {
        txtoutput = compt;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox inchain;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private com.aeongames.edi.utils.visual.TranslucentTextArea txtdescript;
    private com.aeongames.edi.utils.visual.translucenttextfield txtfile;
    private javax.swing.JComboBox<Person> txtowner;
    private com.aeongames.edi.utils.visual.translucenttextfield txtrecordname;
    private javax.swing.JComboBox<Person> txtrequester;
    // End of variables declaration//GEN-END:variables

}