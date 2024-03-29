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

import com.aeongames.blockchain.TODOS.NotImplementedYet;
import com.aeongames.blockchain.TODOS.SubjectToChange;
import com.aeongames.blockchain.base.Block;
import com.aeongames.blockchain.base.BlockchainPool;
import com.aeongames.blockchain.base.common.ByteUtils;
import com.aeongames.expediente.FileRecord;
import com.aeongames.expediente.Person;
import com.aeongames.expediente.SerializablePersona;
import com.aeongames.logger.LoggingHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.pushingpixels.substance.api.SubstanceCortex;
import org.pushingpixels.substance.api.SubstanceSlices.FocusKind;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public final class ViktorUI extends javax.swing.JFrame {

    public static final String PROPSFILE = "/com/aeongames/blockchain/resources/Blockchainprops.properties";
    public static final ImageIcon logo = ViktorUI.class.getResource("/com/aeongames/edi/utils/visual/resources/logoico.png") == null
                    ? null : new javax.swing.ImageIcon(
                            ViktorUI.class.getResource("/com/aeongames/edi/utils/visual/resources/logoico.png"));
    /**
     * person pool for the application wide use.... this thing... should be a
     * structure used... more refined. but doing this way for the sake of
     * finishing this POC some day soon.
     *
     * setting as final as it is a global REFERENCE ! it can be clear, it can be
     * moved but it needs to point to the same structure in the Heap. my dude...
     *
     * TODO: think if a different structure is required or need to be stored
     * somewhere else.
     *
     */
    private final BlockchainPool<FileRecord> mypool;
    private final ArrayList<Person> PersonList;
    private final PersonModel PersonsModel;
    public static final String PERSONFILE = "testuser.json";

    /**
     * Creates new form ViktorUI
     */
    public ViktorUI() {
        PersonList = new ArrayList<>();
        PersonsModel = new PersonModel(PersonList);
        initComponents();
        //TODO: SEND TO PARALLEL COMPUTATION! this SHOULDNT BE DONE ON EDT!!!!
        loaddata();
        mypool = new BlockchainPool<>();
        expedientePanel1.setPool(mypool);
        StartChainPool();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MainImgPanel = new com.aeongames.edi.utils.visual.ImagePanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        chaingraph = new com.aeongames.blockchain.ui.neatScrollPanel();
        jPanel2 = new javax.swing.JPanel();
        jAeonTabPane1 = new com.aeongames.edi.utils.visual.JAeonTabPane();
        imagePanel1 = new com.aeongames.edi.utils.visual.ImagePanel();
        personManagement1 = new com.aeongames.blockchain.ui.PersonManagement();
        imagePanel2 = new com.aeongames.edi.utils.visual.ImagePanel();
        expedientePanel1 = new com.aeongames.blockchain.ui.ExpedientePanel();
        imagePanel3 = new com.aeongames.edi.utils.visual.ImagePanel();
        translucentpanel1 = new com.aeongames.edi.utils.visual.translucentpanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtchain = new com.aeongames.edi.utils.visual.TranslucentTextPane();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtoutputpane = new com.aeongames.edi.utils.visual.TranslucentTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("ViktorBlockChain POC.");
        setIconImage(logo.getImage());
        setMinimumSize(new java.awt.Dimension(800, 600));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Chain"));
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(223, 500));

        jScrollPane1.setOpaque(false);
        jScrollPane1.getViewport().setOpaque(false);

        chaingraph.setOpaque(false);
        chaingraph.setLayout(new javax.swing.BoxLayout(chaingraph, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane1.setViewportView(chaingraph);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
        );

        jPanel2.setOpaque(false);

        javax.swing.GroupLayout imagePanel1Layout = new javax.swing.GroupLayout(imagePanel1);
        imagePanel1.setLayout(imagePanel1Layout);
        imagePanel1Layout.setHorizontalGroup(
            imagePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(personManagement1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        imagePanel1Layout.setVerticalGroup(
            imagePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(personManagement1, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
        );

        jAeonTabPane1.addTab("Person Management", imagePanel1);

        javax.swing.GroupLayout imagePanel2Layout = new javax.swing.GroupLayout(imagePanel2);
        imagePanel2.setLayout(imagePanel2Layout);
        imagePanel2Layout.setHorizontalGroup(
            imagePanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(expedientePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        imagePanel2Layout.setVerticalGroup(
            imagePanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imagePanel2Layout.createSequentialGroup()
                .addComponent(expedientePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jAeonTabPane1.addTab("Records Management", imagePanel2);

        jScrollPane2.getViewport().setOpaque(false);
        jScrollPane2.setOpaque(false);
        jScrollPane2.setViewportView(txtchain);

        jButton1.setText("Print the Chain!");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout translucentpanel1Layout = new javax.swing.GroupLayout(translucentpanel1);
        translucentpanel1.setLayout(translucentpanel1Layout);
        translucentpanel1Layout.setHorizontalGroup(
            translucentpanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(translucentpanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(translucentpanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(translucentpanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(0, 651, Short.MAX_VALUE)))
                .addContainerGap())
        );
        translucentpanel1Layout.setVerticalGroup(
            translucentpanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(translucentpanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addGap(22, 22, 22))
        );

        javax.swing.GroupLayout imagePanel3Layout = new javax.swing.GroupLayout(imagePanel3);
        imagePanel3.setLayout(imagePanel3Layout);
        imagePanel3Layout.setHorizontalGroup(
            imagePanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imagePanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(translucentpanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        imagePanel3Layout.setVerticalGroup(
            imagePanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imagePanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(translucentpanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jAeonTabPane1.addTab("BlockChainPrinter", imagePanel3);

        jPanel3.setOpaque(false);

        jScrollPane3.setOpaque(false);
        jScrollPane3.getViewport().setOpaque(false);

        txtoutputpane.setEditable(false);
        jScrollPane3.setViewportView(txtoutputpane);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 805, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 96, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jAeonTabPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jAeonTabPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        javax.swing.GroupLayout MainImgPanelLayout = new javax.swing.GroupLayout(MainImgPanel);
        MainImgPanel.setLayout(MainImgPanelLayout);
        MainImgPanelLayout.setHorizontalGroup(
            MainImgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainImgPanelLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        MainImgPanelLayout.setVerticalGroup(
            MainImgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainImgPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainImgPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    if(mypool!=null){
        StringWriter Stringdata= new StringWriter();
        PrintWriter writter = new PrintWriter(Stringdata);
         mypool.getChain().forEach(mablock -> {
                writter.println("*********************************************************************************");
                writter.println(String.format("BlockHash::%s", mablock.getHash().toString()));
                writter.println(String.format("BlockTimeIndex::%d", mablock.getIndex()));
                ByteBuffer[] signatures = mablock.getBlockAuthoritativeSignature();
                for (ByteBuffer signature : signatures) {
                    writter.println(String.format("\tBlockAcceptaceSignature::%s", ByteUtils.ByteArrayToString(signature)));
                    signature.rewind();
                }
                writter.println(String.format("BlockPrevious::%s", mablock.getPreviousHash().toString()));
                ZonedDateTime time = Instant.ofEpochMilli(mablock.getTimeStamp()).atZone(ZoneId.of("America/Costa_Rica"));//.atZone(ZoneId.of("GMT-06:00"));
                writter.println(String.format("BlockCreationTime::%s", time));
                writter.println("*********************************************************************************");
                mablock.getBlockTransactions().forEach(trasc -> {
                 writter.println(String.format("\tBlockTransaction::%s", trasc.toString()));
                });
                writter.println("*********************************************************************************");
            });
         Stringdata.flush();
        txtchain.setText(Stringdata.toString());
    }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.LookAndFeelInfo[] avails = javax.swing.UIManager.getInstalledLookAndFeels();
            for (javax.swing.UIManager.LookAndFeelInfo info : avails) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            LoggingHelper.getAClassLogger("UI").log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        SwingUtilities.invokeLater(() -> {
            LookAndFeel laf = new SubstanceGraphiteGlassLookAndFeel();
            try {
                javax.swing.UIManager.setLookAndFeel(laf);
            } catch (UnsupportedLookAndFeelException ex) {
                LoggingHelper.getAClassLogger("UI").log(Level.SEVERE, null, ex);
            }
            // Configure the main skin
            // SubstanceCortex.GlobalScope.setSkin(new );
            SubstanceCortex.GlobalScope.setFocusKind(FocusKind.NONE);
            JFrame.setDefaultLookAndFeelDecorated(true);

            // Create the main frame
            ViktorUI userint = new ViktorUI();

            // And increase the height of the title pane to play nicer with additional
            // content that we are displaying in that area.
            // SubstanceCortex.WindowScope.setPreferredTitlePaneHeight(userint, 40);
            // Set initial size, center in screen, configure to exit the app on clicking the
            // close button
            userint.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            userint.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.aeongames.edi.utils.visual.ImagePanel MainImgPanel;
    private com.aeongames.blockchain.ui.neatScrollPanel chaingraph;
    private com.aeongames.blockchain.ui.ExpedientePanel expedientePanel1;
    private com.aeongames.edi.utils.visual.ImagePanel imagePanel1;
    private com.aeongames.edi.utils.visual.ImagePanel imagePanel2;
    private com.aeongames.edi.utils.visual.ImagePanel imagePanel3;
    private com.aeongames.edi.utils.visual.JAeonTabPane jAeonTabPane1;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private com.aeongames.blockchain.ui.PersonManagement personManagement1;
    private com.aeongames.edi.utils.visual.translucentpanel translucentpanel1;
    private com.aeongames.edi.utils.visual.TranslucentTextPane txtchain;
    private com.aeongames.edi.utils.visual.TranslucentTextPane txtoutputpane;
    // End of variables declaration//GEN-END:variables

    /**
     * TODO: Ensure this is done outside the EDT. and THEN notify the EDT that
     * is ready and... whatever we have the EU doing is done.
     */
    private void loaddata() {
        try {
            LoadUsers(PERSONFILE).forEach(PersonList::add);
            PersonsModel.donotify();
            personManagement1.setPersonModel(PersonsModel);
            expedientePanel1.setPersonModel(PersonsModel);
        } catch (IOException | CertificateException ex) {
            LoggingHelper.getAClassLogger("UI").log(Level.SEVERE, null, ex);
            notifyError("Unable to load Person Lists From file", ex);
        }
    }

    /**
     * not yet created. this needs to do something cool.
     *
     * @param Message
     * @param The_error
     */
    @SubjectToChange
    @NotImplementedYet
    private void notifyError(String Message, Throwable The_error) {

    }

    public static final ArrayList<Person> LoadUsers() throws IOException, CertificateException {
        SerializablePersona[] persons = null;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //CAUTION THE Resource REFERED HERE IS only ON TEST SCENARIOS!!!
        InputStream resource = ViktorUI.class.getResourceAsStream("/com/aeongames/expediente/resources/testuser.json");
        JsonReader reader = gson.newJsonReader(new InputStreamReader(resource));
        JsonObject root = gson.fromJson(reader, JsonObject.class);
        JsonElement json = root.get("List");
        if (!json.isJsonNull()) {
            persons = gson.fromJson(json, SerializablePersona[].class);
        }
        ArrayList<Person> tmp = new ArrayList<>();
        if (persons != null) {
            for (SerializablePersona p : persons) {
                tmp.add(p.convertToPersonClass());
                System.out.println(p);
            }
        }

        return tmp.isEmpty() ? null : tmp;
    }

    public static final ArrayList<Person> LoadUsers(String Path) throws IOException, CertificateException {
        SerializablePersona[] persons = null;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        InputStream resource = new FileInputStream(Path);
        JsonReader reader = gson.newJsonReader(new InputStreamReader(resource));
        JsonObject root = gson.fromJson(reader, JsonObject.class);
        JsonElement json = root.get("List");
        if (!json.isJsonNull()) {
            persons = gson.fromJson(json, SerializablePersona[].class);
        }
        ArrayList<Person> tmp = new ArrayList<>();
        if (persons != null) {
            for (SerializablePersona p : persons) {
                tmp.add(p.convertToPersonClass());
                System.out.println(p);
            }
        }

        return tmp.isEmpty() ? null : tmp;
    }

    //TODO::::
    private void updateChainGraphic() {
        chaingraph.removeAll();
        mypool.getChain().forEach(bloque -> {
            JPanel p = createPanelFor(bloque);
            chaingraph.add(p);
        });

        chaingraph.revalidate();
        revalidate();
        chaingraph.repaint();
        repaint();
    }

    private JPanel createPanelFor(Block bloque) {
        return new Blockpanel(bloque);
    }

    public void StartChainPool() {
        BlockchainPool.debugging = true;
        mypool.RegisterListener(new ChaintoPaneListener(txtoutputpane));
        mypool.RegisterListener(new blockAddedUIListener() {
            @Override
            public void BlockAdded(BlockchainPool<FileRecord> CallerPool, List<FileRecord> CommitedTransactions) {
                //  if(CallerPool!=mypool) Throw new no matcherror
                updateChainGraphic();
            }
        });
        mypool.start();
        //TODO: bring Down at some point? 
        //mypool.BringDown();
    }

}
