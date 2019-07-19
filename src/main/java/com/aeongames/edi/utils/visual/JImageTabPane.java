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
package com.aeongames.edi.utils.visual;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.HashMap;

/**
 * this class is designed to show a image instead of the ye old boring
 * background whenever there is not tabs begin display do note this class is
 * design to draw a image whenever there is no other thing on the tab pane as
 * per such a Tab DUH so if there are tabs will draw only the background color
 *
 * @author Eduardo Vindas C
 */
public class JImageTabPane extends javax.swing.JTabbedPane {

    /**
     * variable to determine a policy where allow the image from a ImagePanel to
     * scale the image to a smaller size only used for when you want to show a
     * image up to its original size
     */
    public static final int SCALE_SMALL_ONLY = 0;
    /**
     * variable to determine a policy where allow the image from a ImagePanel to
     * scale the image to the size required to show on the panel but with
     * respecting the aspect ratio of the image, also will be center
     */
    public static final int SCALE_ALWAYS = 1;
    /**
     * will scale the image to use ALL the space of the panel will not try to
     * keep the ratio will not keep the aspect will fill the hold panel space.
     * this thought is not a good idea.
     */
    public static final int SCALE_USE_ALL_SPACE = 2;
    /**
     * the policy to use to resize and or print the image the default is Scale
     * Small Only
     */
    private int scale_policy = SCALE_SMALL_ONLY;
    /**
     * the default image location we use on our Image panel when none is
     * provided.
     * com.aeongames.blockchain.resources
     */
    private static final String LOGO = "/com/aeongames/blockchain/resources/logo.png";
    /**
     * the image to be show or process.
     */
    private Image img;

    /**
     * creates a new instance of the JImageTabPane tab pane using the Aeongames
     * Logo
     */
    public JImageTabPane() {
        super();
        this.img = java.awt.Toolkit.getDefaultToolkit().getImage(this.getClass().getResource(LOGO));
//        bgcolor = javax.swing.UIManager.getDefaults().getColor("Panel.background");
        java.awt.Dimension size = new java.awt.Dimension(img.getWidth(this), img.getHeight(this));
//        setPreferredSize(size);
//        setMinimumSize(size);
        setSize(size);
//        super.setBackground(bgcolor);
        this.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    /**
     * creates a new instance of the JImageTabPane tab pane using the selected
     * image parsed by parameter.
     *
     * @param <code>java.awt.Image</code> to display, the image to display on
     * the pane
     * @param todisplay
     */
    public JImageTabPane(Image todisplay) {
        super();
        if (todisplay != null) {
            this.img = todisplay;
        } else {
            this.img = java.awt.Toolkit.getDefaultToolkit().getImage(this.getClass().getResource(LOGO));
        }
        java.awt.Dimension size = new java.awt.Dimension(img.getWidth(this), img.getHeight(this));
//        setPreferredSize(size);
//        setMinimumSize(size);
        setSize(size);
//        bgcolor = javax.swing.UIManager.getDefaults().getColor("Panel.background");
//        super.setBackground(bgcolor);
        this.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        HashMap<RenderingHints.Key, Object> tmp = new HashMap<>();
        tmp.put(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
//        tmp.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        tmp.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        RenderingHints rh = new RenderingHints(tmp);
        g2.setRenderingHints(rh);
        if (this.getTabCount() == 0) {
            switch (scale_policy) {
                case (SCALE_SMALL_ONLY):
                    paint_to_size(g);
                    break;
                case (SCALE_ALWAYS):
                    paint_respect_ratio(g);
                    break;
                case (SCALE_USE_ALL_SPACE):
                    paint_default(g);
                    break;
                default:
                    //meh lets set paint to size as default if somthing is wrongle set...
                    paint_to_size(g);
                    break;
            }
        } else {
            setBackground(getBackground());
        }
    }

    /**
     * sets the policy for resize the background image acceptable parameters
     * SCALE_ALWAYS SCALE_USE_ALL_SPACE SCALE_SMALL_ONLY
     *
     * @throws IllegalArgumentException if a invalid parameter is sent
     */
    public void setbackground_policy(int policy) {
        //check if the param is valid
        if (policy == SCALE_ALWAYS
                || policy == SCALE_SMALL_ONLY
                || policy == SCALE_USE_ALL_SPACE) {
            scale_policy = policy;
        } else {
            throw new java.lang.IllegalArgumentException("the value " + policy + " is invalid");
        }
    }

    /**
     * returns the current policy
     */
    public int getBacgroundScalePolicy() {
        return scale_policy;
    }

    /**
     * changes the image to be painted.
     *
     * @param todisplay
     */
    protected void changeImage(Image todisplay) {
        if (todisplay != null) {
            this.img = todisplay;
        }
    }

    /**
     * paints the image from size 1x1 to the image actual size that of course we
     * will respect the image ratio so the image will be show as it should with
     * not forced size also will center the image
     */
    private void paint_to_size(Graphics g) {
        if (img.getWidth(null) > -1 && img.getWidth(null) < this.getWidth() && img.getHeight(null) < this.getHeight()) {
            int Width = (this.getWidth() / 2) - img.getWidth(null) / 2;
            int Height = (this.getHeight() / 2) - img.getHeight(null) / 2;
            g.drawImage(img, Width, Height, img.getWidth(null), img.getHeight(null), null);
        } else {
            paint_respect_ratio(g);
        }
    }

    /**
     * paints the image from size 1x1 to whatever is possible without error (yet
     * unknown to me) use with caution also id the image is expanded to much
     * might eventually look... no good... it respect the image ratio BTW
     */
    private void paint_respect_ratio(Graphics g) {
        //ok now we want to keep the image ratio so lets try the new aproach
        int[] size = ImageUtils.keep_ratio_for_size(this.getWidth(), this.getHeight(), img);
        g.drawImage(img, size[2], size[3], size[0], size[1], null);
    }

    /**
     * sets the image on the panel but stretch to the PANEL size so this will
     * not respect the ratio will fill the hold panel.
     */
    private void paint_default(Graphics g) {
        g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
    }
}
