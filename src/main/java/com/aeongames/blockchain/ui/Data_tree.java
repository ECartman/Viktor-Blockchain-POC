/*
 * 
 * Copyright © 2008-2011,2019 Eduardo Vindas Cordoba. All rights reserved.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/**
 * Data_tree.java
 *
 * Created on 16/03/2010, 02:05:05 PM
 */
package com.aeongames.blockchain.ui;

import com.aeongames.edi.utils.visual.RestrictedTreeSelectionModel;
import com.aeongames.expediente.Expediente;
import com.aeongames.expediente.FileRecord;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 *
 * @author Eduardo Vindas C <eduardo.vindas@hp.com>
 */
public final class Data_tree extends javax.swing.JTree {

    ArrayList<Expediente<FileRecord>> lista;
    public static final ImageIcon OPEN
            // /com/aeongames/edi/utils/visual/resources
            = Data_tree.class.getResource("/com/aeongames/edi/utils/visual/resources/document-open.png") == null ? null
            : new javax.swing.ImageIcon(Data_tree.class.getResource("/com/aeongames/edi/utils/visual/resources/document-open.png"));
    public static final ImageIcon CLOSE
            = Data_tree.class.getResource("/com/aeongames/edi/utils/visual/resources/folder.png") == null ? null
            : new javax.swing.ImageIcon(Data_tree.class.getResource("/com/aeongames/edi/utils/visual/resources/folder.png"));
    public static final ImageIcon BULLET
            = Data_tree.class.getResource("/com/aeongames/edi/utils/visual/resources/bullet.png") == null ? null
            : new javax.swing.ImageIcon(Data_tree.class.getResource("/com/aeongames/edi/utils/visual/resources/bullet.png"));
    private static boolean isbusy = false;

    public Data_tree() {
        TreeModel tmp = getDefaultTreeModel();
        ((DefaultMutableTreeNode) tmp.getRoot()).removeAllChildren();
        ((DefaultMutableTreeNode) tmp.getRoot()).add(new DefaultMutableTreeNode("No Records"));
        this.setModel(tmp);
        setrenderer();
    }

    public Data_tree(ArrayList<Expediente<FileRecord>> list) {
        //this.setModel(getDefaultTreeModel());
        setrenderer();
        this.lista = list;
        settree();
    }

    public void setlist(ArrayList<Expediente<FileRecord>> list) {
        this.lista = list;
        settree();
    }

    public void settree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Porfolio");
        DefaultTreeModel model = new DefaultTreeModel(root);
        this.setModel(model);
        lista.forEach(expediente -> {
            DefaultMutableTreeNode exp = new DefaultMutableTreeNode(expediente);
            exp.setUserObject(expediente);
            root.add(exp);
            model.nodesWereInserted(root, new int[]{root.getIndex(exp)});
            DefaultMutableTreeNode inchain = new DefaultMutableTreeNode("In Blockchain");
            exp.add(inchain);
            model.nodesWereInserted(exp, new int[]{exp.getIndex(inchain)});
            expediente.getRecords().forEach(record -> {
                DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(record);
                inchain.add(newnode);
                model.nodesWereInserted(inchain, new int[]{inchain.getIndex(newnode)});
                newnode = null;
            });
            DefaultMutableTreeNode outchain = new DefaultMutableTreeNode("Pending Transactions");
            exp.add(outchain);
            model.nodesWereInserted(exp, new int[]{exp.getIndex(outchain)});
            expediente.getUncommitedRecords().forEach(record -> {
                DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(record);
                outchain.add(newnode);
                model.nodesWereInserted(outchain, new int[]{outchain.getIndex(newnode)});
                newnode = null;
            });
        });
        model.reload();
        if (!((DefaultMutableTreeNode) getModel().getRoot()).isLeaf()) {
            for (int toex = 0; toex < ((DefaultMutableTreeNode) getModel().getRoot()).getChildCount(); toex++) {
                expandPath(new javax.swing.tree.TreePath(((DefaultMutableTreeNode) ((DefaultMutableTreeNode) getModel().getRoot()).getChildAt(toex)).getPath()));
            }
        } else {
            setModel(null);
        }
    }

    private void setrenderer() {
        setShowsRootHandles(true);
        setEditable(false);
        setRootVisible(false);
        javax.swing.tree.DefaultTreeCellRenderer renderer = new javax.swing.tree.DefaultTreeCellRenderer();
        if (CLOSE != null) {
            renderer.setClosedIcon(CLOSE);
        }
        if (OPEN != null) {
            renderer.setOpenIcon(OPEN);
        }
        if (BULLET != null) {
            renderer.setLeafIcon(BULLET);
        }
        setCellRenderer(renderer);
        setSelectionModel(new RestrictedTreeSelectionModel());
    }

    /**
     * this is az method to implement on later date when data is read from SQL
     *
     * @return
     */
    /*
    @NotImplementedYet
    public boolean willhaveData() {
        boolean responce = false;
        try {
            responce = Manage_sql_list.containsDataType(GetDatabase(), this_type);
        } catch (ClassNotFoundException | SQLException ex) {
            ComponentLogger.LogWarningError(this.getClass(), "willhaveData", "Errpr Reading Database if will have data", ex);
        }
        return responce;
    }*/

 /*
    @NotImplementedYet
    public String GetDatabase() {
        String db = null;
        try {
            db = Configuration.gatherSetting("database");
        } catch (IOException ex) {
        }
        if (db == null) {
            db = DB_ticket_manager.getFile();
            Configuration.SetProperty("database", DB_ticket_manager.getFile());
            Configuration.RecordChanges();
        }
        return db;
    }
     */
 /*
    @NotImplementedYet
    public void fin() {
        savestatus();
    }*/

 /*
    @NotImplementedYet
    private synchronized void savestatus() {
        if (!isbusy) {
            isbusy = true;
            savethread savetree = new savethread();
            savetree.start();
        }
    }
     */
 /*
    private class savethread extends Thread {

        boolean issucess = true;

        public savethread() {
            super("Treesaver");
        }

        @Override
        public void run() {
            try {
                LinkedList<Lista> lista;
                lista = Manage_sql_list.getGroupsByType(GetDatabase(), this_type);
                int cont = 0;
                boolean success = true;
                while (!lista.isEmpty()) {
                    success = success && lista.poll().setStatus(GetDatabase(),
                            isExpanded(new javax.swing.tree.TreePath(
                                            ((DefaultMutableTreeNode) ((DefaultMutableTreeNode) getModel().getRoot())
                                            .getChildAt(cont)).getPath())));
                    cont++;
                }
                lista.clear();
                lista = null;
                issucess = success;
            } catch (ClassNotFoundException | SQLException|NullPointerException ex) {
                issucess = false;
            }
            release();
        }

        public void release() {
            isbusy = false;
        }

        public boolean wassuccess() {
            return issucess;
        }
    }
    
     */
}
