/*
 * 
 * Copyright © 2008-2011 Eduardo Vindas Cordoba. All rights reserved.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/*
 * TabCloseComp.java
 *
 * Created on 13/10/2010, 02:23:38 PM
 */
package com.aeongames.edi.utils.visual;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 *  this class is designed in order to be used as a Tab component in order to enhance
 * the functionality of the tabs, we want to add a Close button functionality to the tabs.
 * @author Eduardo Vindas C <eduardo.vindas@hp.com>
 */
public class TabCloseComp extends javax.swing.JPanel {

    private static final long serialVersionUID = -8021913239435307813L;
    protected float gap = 3.3f;
    protected Icon TabCIcon = null;
    protected JTabbedPane mainpane;
    protected Color XColor = Color.BLACK;

    private TabCloseComp() {
    }

    /**
     * Creates new form TabCloseComp
     * this requires a JtabbedPane to be parsed by a parameter.
     */
    public TabCloseComp(JTabbedPane pane) {
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        mainpane = pane;
        initComponents();
        check_condition();
    }

    public TabCloseComp(JTabbedPane pane, Color X_Color) {
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        mainpane = pane;
        if (X_Color != null) {
            XColor = X_Color;
        }
        initComponents();
        check_condition();
    }

    public TabCloseComp(JTabbedPane pane, Icon icon) {
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        mainpane = pane;
        TabCIcon = icon;
        initComponents();
        check_condition();
    }

    public TabCloseComp(JTabbedPane pane, Color X_Color, Icon icon) {
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        mainpane = pane;
        if (X_Color != null) {
            XColor = X_Color;
        }
        TabCIcon = icon;
        initComponents();
        check_condition();
    }

    public void setgap(float newgap) {
        gap = newgap;
    }

    public final float getgap(){
        return gap;
    }

    public void setIcon(Icon icon) {
        TabCIcon = icon;
        Iconlb.setIcon(TabCIcon);
        check_condition();
    }

    private void check_condition() {
        if (TabCIcon == null) {
            Iconlb.setVisible(false);
        } else {
            Iconlb.setVisible(true);
        }
    }

    public Icon getICon() {
        return TabCIcon;
    }

    public void setXColor(Color X_Color) {
        XColor = X_Color;
    }

    public final Color getColorX() {
        return XColor;
    }

    public void changepane(JTabbedPane pane) {
        mainpane = pane;
    }

    public void setTittle(String text) {
        LbTittle.setText(text.trim());
    }

    public final String getTittle() {
        return LbTittle.getText();

    }

    public final void update() {
        int index_to_process = mainpane.indexOfTabComponent(this);
        setTittle(mainpane.getTitleAt(index_to_process));
        setIcon(mainpane.getIconAt(index_to_process));
    }

    protected boolean close() {
        int index_to_delete = mainpane.indexOfTabComponent(this);
        if (index_to_delete != -1) {
            mainpane.removeTabAt(index_to_delete);
            return true;
        } else {
            return false;
        }
    }

    private class TabButton extends JButton implements ActionListener {

        public TabButton() {
            setToolTipText("close this tab");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
//            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(TabButton.this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            close();
        }

//        //we don't want to update UI for this button
        @Override
        public void updateUI() {
        }
        //paint the cross

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.setStroke(new BasicStroke(2));
//                g2.translate(1, 1);
            } else {
                g2.setStroke(new BasicStroke(3));
            }
            g2.setColor(XColor);
            if (getModel().isRollover()) {
                g2.setColor(Color.RED);
            }
            float init = 0 + gap;
            float presize = getWidth() - gap;
            g2.draw(new Line2D.Float(init, init, presize, presize));
            g2.draw(new Line2D.Float(presize, init, init, presize));
            g2.dispose();

        }
    }
    private final static MouseListener buttonMouseListener = new MouseAdapter() {

        @Override
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Iconlb = new javax.swing.JLabel();
        LbTittle = new javax.swing.JLabel();
        jButton1 = new TabButton();

        setOpaque(false);

        Iconlb.setIcon(TabCIcon);

        jButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton1.setFocusable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Iconlb, javax.swing.GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(LbTittle, javax.swing.GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Iconlb, javax.swing.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(LbTittle, javax.swing.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE)
                .addGap(0, 0, 0))
            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Iconlb;
    private javax.swing.JLabel LbTittle;
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables
}
