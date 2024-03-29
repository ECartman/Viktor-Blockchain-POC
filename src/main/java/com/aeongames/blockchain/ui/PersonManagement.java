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

import com.aeongames.blockchain.TODOS.SubjectToChange;
import static com.aeongames.crypto.signature.DevicesHelper.getdevices;
import com.aeongames.crypto.signature.DigitalSignatureHelper;
import com.aeongames.edi.utils.File.properties_File;
import javax.swing.text.AbstractDocument;
import com.aeongames.edi.utils.text.DocumentDelimiterFilter;
import com.aeongames.edi.utils.text.LabelText;
import com.aeongames.expediente.Person;
import com.aeongames.expediente.SerializablePersona;
import com.aeongames.expediente.Sex;
import com.aeongames.logger.LoggingHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.awt.Color;
import java.awt.Frame;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.security.auth.login.FailedLoginException;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

/**
 *
 * to be fair i don't like most of they way this handle. but time is getting
 * spent don't want to focus on UI, rather than the Server-blockchain...
 *
 * @SubjectToChange
 * @author Eduardo <cartman@aeongames.com>
 */
public class PersonManagement extends javax.swing.JPanel {

    private static final String PROPSFILE = "/com/aeongames/blockchain/resources/Blockchainprops.properties";
    private PersonModel PersonL;
    private Person currentedi = null;
    private X509Certificate ProposedCertificate = null;
    private final HashMap<String, JTextComponent> cardmap = new HashMap<>();
    private final Pattern pattern = Pattern.compile("^[0-9]+$");
    private final Random rndrnd = new Random();
    private final byte[] RAndomTestBytes;

    /**
     * Creates new form PersonManagement
     */
    public PersonManagement() {
        initComponents();
        cardmap.put("sn", txtsn);
        cardmap.put("givenName", txtname);
        cardmap.put("serialNumber", txtcri);
        RAndomTestBytes = new byte[255];

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtcri = new com.aeongames.edi.utils.visual.translucenttextfield();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtdate = new com.github.lgooddatepicker.components.DatePicker();
        txtsex = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtname = new com.aeongames.edi.utils.visual.translucenttextfield();
        jLabel5 = new javax.swing.JLabel();
        commit = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        btconfirmcert = new javax.swing.JButton();
        btcertread = new javax.swing.JButton();
        txtoutput = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtsn = new com.aeongames.edi.utils.visual.translucenttextfield();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtaddress = new com.aeongames.edi.utils.visual.TranslucentTextArea();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Plists = new javax.swing.JList<>();
        addnew = new javax.swing.JButton();

        setOpaque(false);

        jPanel2.setOpaque(false);

        jLabel1.setText("CR ID:");

        ((AbstractDocument)txtcri.getDocument()).setDocumentFilter(new DocumentDelimiterFilter(10,true,true));
        txtcri.setEnabled(false);

        jLabel2.setText("Name:");

        jLabel3.setText("BirthDate:");

        txtdate.setEnabled(false);

        txtsex.setModel(new javax.swing.DefaultComboBoxModel<>(Sex.getSexOptions()));
        txtsex.setEnabled(false);

        jLabel4.setText("Sex:");

        txtname.setEnabled(false);

        jLabel5.setText("Address:");

        commit.setText("Include/Update");
        commit.setEnabled(false);
        commit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commitActionPerformed(evt);
            }
        });

        jLabel6.setText("Signature Certificate:");

        btconfirmcert.setText("Confirm Certificate");
        btconfirmcert.setEnabled(false);
        btconfirmcert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btconfirmcertActionPerformed(evt);
            }
        });

        btcertread.setText("Read From Certificate");
        btcertread.setEnabled(false);
        btcertread.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcertreadActionPerformed(evt);
            }
        });

        txtoutput.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtoutput.setText("ready...");

        jLabel7.setText("Last Name");

        txtsn.setEnabled(false);

        jScrollPane2.setOpaque(false);
        jScrollPane2.getViewport().setOpaque(false);

        txtaddress.setColumns(20);
        txtaddress.setRows(2);
        txtaddress.setEnabled(false);
        jScrollPane2.setViewportView(txtaddress);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtoutput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtdate, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                                        .addGap(110, 110, 110))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(txtname, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(txtcri, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(btcertread)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel7)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtsn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtsex, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(jScrollPane2)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 228, Short.MAX_VALUE)
                                        .addComponent(commit)
                                        .addGap(9, 9, 9))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btconfirmcert)
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addGap(37, 37, 37))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtcri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btcertread))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtsn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtsex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(btconfirmcert))
                .addGap(112, 112, 112)
                .addComponent(commit)
                .addGap(33, 33, 33)
                .addComponent(txtoutput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.setOpaque(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Registered Persons"));
        jPanel1.setOpaque(false);

        jScrollPane1.setOpaque(false);

        Plists.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        Plists.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PlistsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(Plists);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        addnew.setText("New Person");
        addnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addnewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addnew, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addnew))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void PlistsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PlistsMouseClicked
        if (evt.getClickCount() >= 2 && Plists.isEnabled()) {
            if (Plists.getSelectedIndex() != -1 && Plists.getSelectedValue() != null) {
                clean(true);
                currentedi = Plists.getSelectedValue();
                enableforedit();
            }
        }
    }//GEN-LAST:event_PlistsMouseClicked

    private void btcertreadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcertreadActionPerformed
        ReadFromSmartcard();
    }//GEN-LAST:event_btcertreadActionPerformed

    private void addnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addnewActionPerformed
        clean(true);
        enableforedit();
    }//GEN-LAST:event_addnewActionPerformed

    private void commitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commitActionPerformed
        Commit();
        clean(false);
        enabledforSelection();
    }//GEN-LAST:event_commitActionPerformed

    private void btconfirmcertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btconfirmcertActionPerformed
        DoCertTest();
    }//GEN-LAST:event_btconfirmcertActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<Person> Plists;
    private javax.swing.JButton addnew;
    private javax.swing.JButton btcertread;
    private javax.swing.JButton btconfirmcert;
    private javax.swing.JButton commit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private com.aeongames.edi.utils.visual.TranslucentTextArea txtaddress;
    private com.aeongames.edi.utils.visual.translucenttextfield txtcri;
    private com.github.lgooddatepicker.components.DatePicker txtdate;
    private com.aeongames.edi.utils.visual.translucenttextfield txtname;
    private javax.swing.JLabel txtoutput;
    private javax.swing.JComboBox<Sex> txtsex;
    private com.aeongames.edi.utils.visual.translucenttextfield txtsn;
    // End of variables declaration//GEN-END:variables

    void setPersonList(final ArrayList<Person> PersonList) {
        //this should only be set once. should we deny to set it more than a single time? hmmm not sure... 
        //TODO: analize check which is the best way to adapt. 
        if (PersonL == null) {
            PersonL = new PersonModel(PersonList);
            Plists.setModel(PersonL);
        } else {
            PersonL.donotify();
        }
    }
    
    void setPersonModel(final PersonModel Personmod) {
        //this should only be set once. should we deny to set it more than a single time? hmmm not sure... 
        //TODO: analize check which is the best way to adapt. 
        if (PersonL == null) {
            PersonL = Personmod;
            Plists.setModel(PersonL);
        } else {
            PersonL.donotify();
        }
    }

    private void enableforedit() {
        Plists.setEnabled(false);
        setedition(true);
        if (currentedi != null) {
            StringBuilder t = new StringBuilder();
            for (byte b : currentedi.getCRID()) {
                t.append(b);
            }
            txtcri.setText(t.toString());
            txtcri.setEditable(false);//--> this value cannot be changed!
            txtname.setText(currentedi.getName());
            txtaddress.setText(currentedi.getDomicilio());
            txtdate.setDate(LocalDate.ofEpochDay(currentedi.getDay_of_birth()));
            txtsex.setSelectedItem(currentedi.getPersonSex());
            this.ProposedCertificate = currentedi.getCertificate();
        }
    }

    private void enabledforSelection() {
        if (Plists.getModel().getSize() > 0) {
            Plists.setEnabled(true);
        }
        currentedi = null;
        setedition(false);
        commit.setEnabled(false);
    }

    private void setedition(boolean flag) {
        txtcri.setEnabled(flag);
        txtcri.setEditable(flag);
        txtname.setEnabled(flag);
        txtname.setEditable(flag);
        txtdate.setEnabled(flag);
        txtsn.setEditable(flag);
        txtsn.setEnabled(flag);
        txtsex.setEnabled(flag);
        txtaddress.setEnabled(flag);
        txtaddress.setEditable(flag);
        btcertread.setEnabled(flag);
        btconfirmcert.setEnabled(flag);
        btcertread.setEnabled(flag);
        commit.setEnabled(flag);

    }

    private boolean checkSmartCard() {
        List<CardTerminal> terminals = getdevices();
        boolean hasSmartCard = false;
        if (terminals != null) {
            for (CardTerminal terminal : terminals) {
                try {
                    hasSmartCard = terminal.isCardPresent();
                    if (hasSmartCard) {
                        break;
                    }
                } catch (CardException ex) {
                }
            }
        }
        terminals = null;
        return hasSmartCard;
    }

    private KeyStore getSignatureKeystore() {
        KeyStore toreturn = null;
        PinDialog dialog = new PinDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        if (dialog.getpin() != null) {
            try {
                DigitalSignatureHelper.LoadSmartCardPKCSProvider();
                toreturn = DigitalSignatureHelper.getKeystore(dialog.getpin());
            } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException ex) {
                LoggingHelper.getAClassLogger("UI").log(java.util.logging.Level.SEVERE, "error Detected", ex);
                Throwable error = ex;
                boolean consummed = false;
                while (error != null) {
                    if (error instanceof FailedLoginException) {
                        consummed = true;
                        seterrorMessage("Wrong Password on the Smart Card!");
                        break;
                    } else {
                        error = error.getCause();
                    }
                }
                if (!consummed) {
                    seterrorMessage("Error reading the smart card.");
                }
            }
        }
        return toreturn;
    }

    private void ReadFromSmartcard() {
        boolean hasSmartCard = checkSmartCard();
        if (hasSmartCard) {
            KeyStore ks = getSignatureKeystore();
            if (ks != null) {
                try {
                    properties_File settings = new properties_File(PROPSFILE);
                    ProposedCertificate = (X509Certificate) ks.getCertificate(settings.getProperty("SignatureKeyAlias"));
                    //TODO: dreadful remove this Joption pane shit from my sight... 
                    ImageIcon e = null;
                    if (((Frame) SwingUtilities.getWindowAncestor(this)).getIconImage() != null) {
                        e = new ImageIcon(((Frame) SwingUtilities.getWindowAncestor(this)).getIconImage());
                    }
                    boolean confirmed = true;//--> add the data if no info is available at all. 
                    if (currentedi != null || (!txtcri.getText().trim().isEmpty()
                            || !txtname.getText().trim().isEmpty()
                            || !txtsn.getText().trim().isEmpty())) {
                        confirmed = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                                "import all values from Smart Card?",
                                "SmartCard import",
                                JOptionPane.YES_NO_OPTION,
                                HEIGHT, e) == JOptionPane.YES_OPTION;
                    }
                    if (confirmed) {
                        X500Name x500name = new JcaX509CertificateHolder(ProposedCertificate).getSubject();
                        for (RDN rdn : x500name.getRDNs()) {
                            for (AttributeTypeAndValue attribute : rdn.getTypesAndValues()) {
                                JTextComponent field = cardmap.get(RFC4519Style.INSTANCE.oidToDisplayName(attribute.getType()));
                                if (field != txtcri) {
                                    if (attribute.getValue() != null && field != null) {
                                        field.setText(attribute.getValue().toString());
                                    }
                                } else {
                                    //if this person is loaded from file should not be updated. the CRID is to be unique & final 
                                    //if error ppl need to go to whomever can fix from the resouce ie. Tribunal supremo de Elecciones
                                    // or IT of the database.
                                    String t = attribute.getValue().toString().replaceAll("[^\\d]", "").trim();
                                    if (currentedi == null) {
                                        field.setText(t);
                                    } else if (!txtcri.getText().trim().equals(t)) {
                                        LoggingHelper.getAClassLogger("UI").log(java.util.logging.Level.WARNING, "DISCREPANCY DETECTED! ");
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException | KeyStoreException | CertificateException ex) {
                    LoggingHelper.getAClassLogger("UI").log(java.util.logging.Level.SEVERE, "error Detected", ex);
                    Throwable error = ex;
                    boolean consummed = false;
                    while (error != null) {
                        if (error instanceof FailedLoginException) {
                            consummed = true;
                            seterrorMessage("Wrong Password on the Smart Card!");
                            break;
                        } else {
                            error = error.getCause();
                        }
                    }
                    if (!consummed) {
                        seterrorMessage("Error reading the smart card.");
                    }
                }
            }
        } else {
            seterrorMessage("SmartCard not detected! ");
        }
    }

    private void clean(boolean clearerroroutput) {
        currentedi = null;
        ProposedCertificate = null;
        if (clearerroroutput) {
            CleanOuputField();
        }
        txtaddress.setText("");
        txtcri.setText("");
        txtdate.clear();
        txtname.setText("");
        txtsex.setSelectedItem(Sex.UNDEFINED);
        txtsn.setText("");
    }

    /**
     * one of two thing needs to happen. either check the edited person
     * information is as valid we can. and otherwise check the information enter
     * is not duplicated. at the least the CRID TODO: ADD MORE CHECKUPS
     */
    @SubjectToChange
    private void Commit() {
        String crid = txtcri.getText().trim();
        boolean error = !pattern.matcher(crid).find();
        txtname.setText((txtname.getText().trim() + " " + txtsn.getText().trim()).trim());
        error |= txtname.getText().trim().isEmpty();
        error |= txtaddress.getText().trim().isEmpty();
        error |= txtdate.getDate() == null;
        if (error) {
            seterrorMessage("invalid data");
        } else {
            CleanOuputField();
            if (currentedi != null) {
                if (!PersonL.getthelist().contains(currentedi)) {
                    LoggingHelper.getAClassLogger("UI").log(java.util.logging.Level.SEVERE,
                            "the Person is Corrupted?!");
                    seterrorMessage("unable to Save this person");
                } else {
                    //here we have that currentedi is IN the list alredy, but currentedi is outdated 
                    //update and persist it. 
                    currentedi.setDomicilio(txtaddress.getText().trim());
                    currentedi.setName(txtname.getText().trim());
                    currentedi.setPersonSex(txtsex.getItemAt(txtsex.getSelectedIndex()));
                    try {
                        PersistPersons();
                        seterrorMessage("Person updated");
                    } catch (IOException ex) {
                        LoggingHelper.getAClassLogger("UI").log(java.util.logging.Level.SEVERE,
                                "Error Serializing the Person List", ex);
                        seterrorMessage("Error Serializing the Person List");
                    }
                }
            } else {
                if (safe_new_one(crid)) {
                    seterrorMessage("Person added");
                }
            }
        }
    }

    private void PersistPersons() throws IOException {
        List<SerializablePersona> topersist = PersonL.getthelist().stream()
                .map(pertoconvert -> {
                    try {
                        return SerializablePersona.toSerializablePersona(pertoconvert);
                    } catch (CertificateEncodingException e) {
                        LoggingHelper.getAClassLogger("UI").log(java.util.logging.Level.SEVERE,
                                "Error Serializing the Object", e);
                    }
                    return null;
                })
                .collect(Collectors.toList());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String json = gson.toJson(topersist);

        System.out.println(json);
        JsonObject root = new JsonObject();
        root.add("List", gson.toJsonTree(topersist));
        JsonWriter writer = gson.newJsonWriter(new FileWriter(ViktorUI.PERSONFILE));
        System.out.println(gson.toJson(root.toString()));
        gson.toJson(root, writer);
        writer.flush();
    }

    private void seterrorMessage(String errormessage) {
        LabelText.wrapLabelText(txtoutput, errormessage);
        System.out.println(txtoutput.getText());
        txtoutput.setForeground(Color.red);
    }

    private void CleanOuputField() {
        txtoutput.setText("");
        txtoutput.setForeground(UIManager.getColor("Label.foreground"));
    }

    private boolean safe_new_one(String crid) {
        boolean sucess = false;
        //so currentedi is null. we need to check 
        //a similar one does not exist alredy. soooooo 
        //to do so we going to do a comparaitve. begin by creating currentedi
        byte[] id_array = new byte[crid.length()];//each character should be a numeric. 
        for (int i = 0; i < id_array.length; i++) {
            id_array[i] = (byte) Character.getNumericValue(crid.charAt(i));

        }
        currentedi = new Person(id_array,
                txtname.getText().trim(),
                (Sex) txtsex.getSelectedItem(),
                txtdate.getDate().toEpochDay(),
                txtaddress.getText().trim(),
                ProposedCertificate);//ProposedCertificate CAN be null. its fine. 
        if (!PersonL.getthelist().contains(currentedi)) {
            PersonL.getthelist().add(currentedi);
            PersonL.donotify();
            try {
                PersistPersons();
                sucess = true;
            } catch (IOException ex) {
                LoggingHelper.getAClassLogger("UI").log(java.util.logging.Level.SEVERE,
                        "Error Serializing the Person List", ex);
                seterrorMessage("Error Serializing the Person List");
            }
        } else {
            seterrorMessage("duplicate detected");
        }
        return sucess;
    }

    private void DoCertTest() {
        boolean hasSmartCard = checkSmartCard();
        if (hasSmartCard) {
            KeyStore ks = getSignatureKeystore();
            if (ks != null) {
                properties_File settings;
                try {
                    settings = new properties_File(PROPSFILE);

                    Signature RSASHA = DigitalSignatureHelper.getSignatureObject_toSign(ks,
                            settings.getProperty("SignatureKeyAlias"),
                            settings.getProperty("signmethod"),
                            new KeyStore.PasswordProtection("".toCharArray()));
                    if (RSASHA != null) {//--> should not be
                        rndrnd.nextBytes(RAndomTestBytes);
                        RSASHA.update(RAndomTestBytes);
                        byte[] signedvalue = RSASHA.sign();
                        RSASHA = DigitalSignatureHelper.getSignatureObject_toVerify(ks.getProvider(),
                                settings.getProperty("signmethod"),
                                ProposedCertificate);
                        RSASHA.update(RAndomTestBytes);
                       if(RSASHA.verify(signedvalue)) seterrorMessage("Certificate Confirmed.");
                    }
                } catch (IOException | UnrecoverableEntryException | KeyStoreException | NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
                    LoggingHelper.getAClassLogger("UI").log(java.util.logging.Level.SEVERE,
                            "unable to Confirm Signature", ex);
                    seterrorMessage("unable to Confirm Signature");
                }
            }
        } else {
            seterrorMessage("SmartCard not detected! ");
        }

    }

    
}
