/*
 * 
 *   Copyright � 2019 Eduardo Vindas Cordoba. All rights reserved.
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

import com.aeongames.blockchain.base.BlockchainPool;
import com.aeongames.expediente.Expediente;
import com.aeongames.expediente.Person;
import com.aeongames.logger.LoggingHelper;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.UIManager;

import java.security.cert.CertificateException;
import javax.security.auth.login.FailedLoginException;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import java.util.List;
import java.util.AbstractMap.SimpleImmutableEntry;

import static com.aeongames.crypto.signature.DevicesHelper.getdevices;
import com.aeongames.crypto.signature.DigitalSignatureHelper;
import com.aeongames.edi.utils.text.LabelText;
import com.aeongames.expediente.FileRecord;
import java.awt.Color;
import java.awt.Frame;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class ExpedientePanel extends javax.swing.JPanel {

    private final ArrayList<RecordP> recordslist;
    private final ArrayList<Expediente<FileRecord>> lista;
    private BlockchainPool<FileRecord> mypool;
    private Expediente<FileRecord> SelectedExpediente = null;
    private PersonModel PersonL;

    /**
     * Creates new form Expediente
     */
    public ExpedientePanel() {
        initComponents();
        lista = new ArrayList<>();
        recordslist = new ArrayList<>();
        //data_tree1.setlist(lista);
    }

    void setPool(BlockchainPool<FileRecord> pool) {
        this.mypool = pool;
        mypool.RegisterListener(new blockAddedUIListener() {
            @Override
            //TODO: for better perfomance code instead of interate for all the records 
            //We should have a Map (hashmap or tablemap) that relate the records vs their transactions)
            public void BlockAdded(BlockchainPool<FileRecord> CallerPool, List<FileRecord> CommitedTransactions) {
                lista.forEach(expediente -> {
                    expediente.commit(CommitedTransactions);
                });
                data_tree1.setlist(lista);
            }
        });

    }

    public ArrayList<Expediente<FileRecord>> getExpedientes() {
        return lista;
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
        jScrollPane2 = new javax.swing.JScrollPane();
        data_tree1 = new com.aeongames.blockchain.ui.Data_tree();
        btexpediente = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtrequester = new javax.swing.JComboBox<>();
        txtowner = new javax.swing.JComboBox<>();
        txtoutput = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btrecord = new javax.swing.JButton();
        btaddrecord = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        RecordPanel = new com.aeongames.blockchain.ui.neatScrollPanel();

        setOpaque(false);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Record List"));
        jPanel2.setOpaque(false);

        jScrollPane2.setOpaque(false);
        jScrollPane2.getViewport().setOpaque(false);

        data_tree1.setOpaque(false);
        data_tree1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                data_tree1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(data_tree1);

        btexpediente.setText("New Record List");
        btexpediente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btexpedienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
            .addComponent(btexpediente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(btexpediente))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Record List"));
        jPanel5.setOpaque(false);

        jLabel1.setText("Owner:");

        jLabel2.setText("Requester:");

        txtrequester.setEnabled(false);
        txtrequester.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expPersChanged(evt);
            }
        });

        txtowner.setToolTipText("");
        txtowner.setEnabled(false);
        txtowner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expPersChanged(evt);
            }
        });

        txtoutput.setText("Ready");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtoutput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtowner, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtrequester, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtowner, txtrequester});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(txtrequester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtowner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtoutput, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtowner, txtrequester});

        jPanel3.setOpaque(false);

        btrecord.setText("Save Record");
        btrecord.setEnabled(false);
        btrecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btrecordActionPerformed(evt);
            }
        });

        btaddrecord.setText("Add new Record");
        btaddrecord.setEnabled(false);
        btaddrecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddrecordActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.setToolTipText("Clear non Commited Records");
        btclear.setEnabled(false);
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        jPanel4.setOpaque(false);

        jScrollPane3.setOpaque(false);
        jScrollPane3.getViewport().setOpaque(false);

        RecordPanel.setOpaque(false);
        RecordPanel.setLayout(new javax.swing.BoxLayout(RecordPanel, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane3.setViewportView(RecordPanel);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btaddrecord)
                .addGap(38, 38, 38)
                .addComponent(btclear)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btrecord)
                .addContainerGap())
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btaddrecord, btrecord});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btrecord)
                    .addComponent(btaddrecord)
                    .addComponent(btclear))
                .addGap(2, 2, 2))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btexpedienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btexpedienteActionPerformed
        clear();
        Clearoutput();
        txtowner.setEnabled(true);
        txtrequester.setEnabled(true);
        toglebuttons(true);
    }//GEN-LAST:event_btexpedienteActionPerformed

    private void expPersChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expPersChanged
        if (SelectedExpediente == null) {
            //unsaved=true;
            SetOutputMessage("New Record List is not yet saved");
        }
    }//GEN-LAST:event_expPersChanged

    private void btaddrecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddrecordActionPerformed
        addblankrecord();
    }//GEN-LAST:event_btaddrecordActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        //now if this was loaded from the tree we need to add all the records back...
        //TODO: LOAD FROM SelectedExpediente

        recordslist.clear();
        RecordPanel.removeAll();
    }//GEN-LAST:event_btclearActionPerformed

    private void btrecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btrecordActionPerformed
        boolean Errordetected = false, created = false;
        if (txtowner.getSelectedItem() != null
                && (SelectedExpediente != null || txtrequester.getSelectedItem() != null)) {
            if (SelectedExpediente == null) {
                created = true;
                // since the SelectedExpediente should be null here means weneed to build a expediente from ground up
                SelectedExpediente = new Expediente<>(txtowner.getItemAt(txtowner.getSelectedIndex()), new FileRecord(
                        FileRecord.CreateGenesisforExpediente(txtowner.getItemAt(txtowner.getSelectedIndex()),
                                txtrequester.getItemAt(txtrequester.getSelectedIndex())), "Genesis", "Genesis transaction for this Person"));
            }

            for (RecordP tentative : recordslist) {
                if (!tentative.isready()) {
                    Errordetected = true;
                    SetOutputMessage("Error: information is missing");
                    break;
                }
                SimpleImmutableEntry<Boolean, String> readyness = tentative.checkreadynessfor(SelectedExpediente);
                if (!readyness.getKey()) {
                    Errordetected = true;
                    SetOutputMessage(readyness.getValue());
                    break;
                }
            }
            try {
                if (!Errordetected) {
                    KeyStore keystore = gatherKeystore();
                    recordslist.forEach((tentative) -> {
                        SimpleImmutableEntry<Boolean, String> addresult = SelectedExpediente.addRecord(tentative.asRecord(keystore));
                        if (!addresult.getKey()) {
                            String ermessage = String.format("Fail to Include: %s due:%s", tentative.getName(), addresult.getValue());
                            SetOutputMessage(ermessage);
                        }
                    });
                    if (!lista.contains(SelectedExpediente)) {
                        lista.add(SelectedExpediente);
                    }
                    data_tree1.setlist(lista);
                    data_tree1.revalidate();
                    data_tree1.repaint();
                    SelectedExpediente.getUncommitedRecords().forEach(uncommited -> {
                        if (mypool.queueTransaction(uncommited)) {
                            LoggingHelper.getAClassLogger("UI").log(java.util.logging.Level.INFO, "transaction queued");
                        }
                    });

                    //----------------------------------------------
                    clear();
                    Clearoutput();
                    toglebuttons(false);
                    //----------------------------------------------
                } else if (created) {
                    SelectedExpediente = null;
                }
            } catch (Exception err) {
                //TODO: HANDLE THIS ERROR BETTER
                LoggingHelper.getAClassLogger("UI").log(Level.WARNING, "likely the patienet is the same as the medic this is not allow check it out", err);
                SetOutputMessage("Error: please check the Selected Persons");
                if (created) {
                    SelectedExpediente = null;
                }
            }
        } else {
            SetOutputMessage("Error: information is missing");
        }
    }//GEN-LAST:event_btrecordActionPerformed

    @SuppressWarnings("unchecked")
    private void data_tree1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_data_tree1MouseClicked
        if (evt.getClickCount() >= 2) {
            if (data_tree1.getSelectionPaths() != null && data_tree1.getSelectionPaths().length != 0) {
                for (TreePath selectionPath : data_tree1.getSelectionPaths()) {
                    for (Object mrx : selectionPath.getPath()) {
                        if (mrx instanceof DefaultMutableTreeNode) {
                            if (((DefaultMutableTreeNode) mrx).getUserObject() instanceof Expediente) {
                                clear();
                                Clearoutput();
                                txtowner.setEnabled(false);
                                txtrequester.setEnabled(false);
                                this.SelectedExpediente = (Expediente<FileRecord>) ((DefaultMutableTreeNode) mrx).getUserObject();
                                txtowner.setSelectedItem(SelectedExpediente.getOwner());
                                toglebuttons(true);
                                addblankrecord();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_data_tree1MouseClicked

    void setPersonModel(final PersonModel Personmod) {
        //this should only be set once. should we deny to set it more than a single time? hmmm not sure... 
        //TODO: analize check which is the best way to adapt. 
        if (PersonL == null) {
            PersonL = Personmod;
            //Plists.setModel(PersonL);
            txtrequester.setModel(PersonL.getcopy());
            txtowner.setModel(PersonL.getcopy());
        } else {
            //PersonL.donotify();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.aeongames.blockchain.ui.neatScrollPanel RecordPanel;
    private javax.swing.JButton btaddrecord;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btexpediente;
    private javax.swing.JButton btrecord;
    private com.aeongames.blockchain.ui.Data_tree data_tree1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel txtoutput;
    private javax.swing.JComboBox<Person> txtowner;
    private javax.swing.JComboBox<Person> txtrequester;
    // End of variables declaration//GEN-END:variables

    private void clear() {
        recordslist.clear();
        SelectedExpediente = null;
        recordslist.clear();
        RecordPanel.removeAll();
        txtowner.setSelectedIndex(-1);
        txtrequester.setSelectedIndex(-1);
        toglebuttons(false);
    }

    private void Clearoutput() {
        txtoutput.setText("");
        txtoutput.setForeground(UIManager.getColor("Label.foreground"));
    }

    private void SetOutputMessage(String Message) {
        LabelText.wrapLabelText(txtoutput, Message);
       //System.out.println(txtoutput.getText());
        txtoutput.setForeground(Color.red);
    }

    private void toglebuttons(boolean enabled) {
        btaddrecord.setEnabled(enabled);
        btrecord.setEnabled(enabled);
        btclear.setEnabled(enabled);
    }

    private KeyStore gatherKeystore() {
        ImageIcon e = null;
        if (((Frame) SwingUtilities.getWindowAncestor(this)).getIconImage() != null) {
            e = new ImageIcon(((Frame) SwingUtilities.getWindowAncestor(this)).getIconImage());
        }
        boolean proeed = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                "Digital Sign The transaction?",
                "Digital Sign",
                JOptionPane.YES_NO_OPTION,
                HEIGHT, e) == JOptionPane.YES_OPTION;
        KeyStore ks = null;
        if (proeed) {
            boolean hasSmartCard = checkSmartCard();
            if (hasSmartCard) {
                ks = getSignatureKeystore();
            }
        }
        return ks;
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
                        SetOutputMessage("Wrong Password on the Smart Card!");
                        break;
                    } else {
                        error = error.getCause();
                    }
                }
                if (!consummed) {
                    SetOutputMessage("Error reading the smart card.");
                }
            }
        }
        return toreturn;
    }

    private void addblankrecord() {
        if (recordslist.size() < 8) {
            RecordP tmp = new RecordP();
            tmp.setoutputcomponent(txtoutput);
            tmp.setPersonModel(PersonL);
            recordslist.add(tmp);
            RecordPanel.add(tmp);
            revalidate();
            repaint();
        } else {
            //--> no particular reason. i just want to avoid problems doing testing. but we COULD allow more. 
            LoggingHelper.getAClassLogger("UI").log(Level.WARNING, "no more Records can be added, POC limit!");

        }

    }

}