/*
 *
 * Copyright © 2008-2012 Eduardo Vindas Cordoba. All rights reserved.
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * a panel that will display a image within.
 *
 * @author Eduardo Vindas C
 */
public class ImagePanel extends javax.swing.JPanel {

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
     * will Not Scale the image, but will instead use the image as A texture to
     * be used to Paint the background of the Panel.
     */
    public static final int NO_SCALABLE_TEXTURE = 3;
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
    private static final String logo = "/com/aeongames/blockchain/resources/logo.png";
    /**
     * the image to be show or process.
     */
    private Image ORGimg = null;
    /**
     * the image to be show or process.
     */
    private Image img;
    /**
     * the image transparency level.
     */
    private float translucent = 1.0f;
    /**
     * if the Texture paint enabled. this 2 settings allow us to know if the texture should be repeated on X and or on Y axis
     */
    private boolean repeat_X=true,repeat_Y=true;
    /**
     * minimal transparency
     */
    private static final float MINTRASPT = 0.20f;

    /**
     * the Image Panel is a normal Swing panel that just change the way it draws
     * the background instead of the silly and boring color will draw a image
     * whenever is required or wanted to be implemented.
     */
    public ImagePanel() {
        readDefault();
        set();
    }

    /**
     * the Image Panel is a normal Swing panel that just change the way it draws
     * the background instead of the silly and boring color will draw a image
     * whenever is required or wanted to be implemented.
     */
    public ImagePanel(Image todisplay) {
        if (todisplay != null) {
            img = todisplay;
        } else {
            readDefault();
        }
        set();
    }

    /**
     * the Image Panel is a normal Swing panel that just change the way it draws
     * the background instead of the silly and boring color will draw a image
     * whenever is required or wanted to be implemented.
     * @param todisplay
     * @param alpha
     */
    public ImagePanel(Image todisplay, float alpha) {
        if (todisplay != null) {
            img = todisplay;
        } else {
            readDefault();
        }
        if (alpha >= MINTRASPT && alpha < 1.0f) {
            translucent = alpha;
            config();
        }
        set();
    }

    /**
     * sets the dimensions for this panel according to the image.
     */
    private void set() {
        java.awt.Dimension size = new java.awt.Dimension(img.getWidth(this), img.getHeight(this));
        setSize(size);
        //        setLayout(null);
    }

    /**
     * configures the image to be printed as transparency on the panel if
     * required, otherwise just set the image.
     */
    private void config() {
        if (translucent >= MINTRASPT && translucent < 1.0f) {
            if (ORGimg == null || !(ORGimg instanceof BufferedImage)) {
                ORGimg = ImageUtils.toBufferedImage(img);
            }
            BufferedImage aimg = new BufferedImage(((BufferedImage) ORGimg).getWidth(), ((BufferedImage) ORGimg).getHeight(), BufferedImage.TRANSLUCENT);
            Graphics2D g2 = aimg.createGraphics();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, translucent));
            g2.drawImage(((BufferedImage) ORGimg), null, 0, 0);
            // let go of all system resources in this Graphics  
            g2.dispose();
            img = aimg;
        } else if (ORGimg != null) {
            img = ORGimg;
        }
    }

    /**
     * read and sets the default image for the panel.
     */
    private void readDefault() {
        try {
            this.img = ImageIO.read(this.getClass().getResource(logo));
        } catch (IOException ex) {
            this.img = java.awt.Toolkit.getDefaultToolkit().getImage(this.getClass().getResource(logo));
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
                || policy == SCALE_USE_ALL_SPACE
                || policy == NO_SCALABLE_TEXTURE) {
            scale_policy = policy;
        } else {
            throw new java.lang.IllegalArgumentException("the value " + policy + " is invalid");
        }
    }

    /**
     * this method changes the image of the panel.
     *
     * @param todisplay the image to display
     * @return boolean determine whenever or not the change were successful or
     * not if the image is the same as before this will return false.
     */
    public final boolean changeImage(Image todisplay) {
        boolean result = false;
        if (todisplay != null) {
            if (img != todisplay) {
                img = todisplay;
                ORGimg = null;
                config();
            }
            result = true;
        } else {
            ORGimg = null;
            readDefault();
        }
        repaint();
        return result;
    }

    public final void setImageTrasparency(float trasparency) {
        if (trasparency >= MINTRASPT && trasparency <= 1.0f) {
            translucent = trasparency;
            config();
            repaint();
        }
    }

    /**
     * returns the alpha Level of the Image that is printed on this panel.
     *
     * @return Alpha Level of the image Begin printed on this panel.
     */
    public final float getImageAlphaLevel() {
        return translucent;
    }

    /**
     * returns the default background for a image panel that is the Logo image.
     */
    protected final void returntodefault() {
        readDefault();
        ORGimg = null;
        translucent = 1.0f;
        repaint();
    }

    /**
     * returns the current policy
     */
    public final int getBacgroundScalePolicy() {
        return scale_policy;
    }
    
    public final void setRepeatX(boolean repeat){
        repeat_X=repeat;
    }
    
    public final void setRepeatY(boolean repeat){
        repeat_Y=repeat;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //we should add code for set this as an option instad of always apply
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//       ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING,
//                RenderingHints.VALUE_RENDER_QUALITY);
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
            case NO_SCALABLE_TEXTURE:
                paintsTexture(g);
                break;
            default:
                //meh lets set paint to size as default if somthing is wrong...
                paint_to_size(g);
                break;
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
            g.drawImage(img, Width, Height, img.getWidth(null), img.getHeight(null), this);
        } else {
            paint_respect_ratio(g);
        }
    }

    /**
     * paints the image from size 1x1 to whatever is possible without error (yet
     * unknown to me) use with caution also if the image is expanded to much
     * might eventually look... stretch... it respect the image ratio BTW
     */
    private void paint_respect_ratio(Graphics g) {
        //ok now we want to keep the image ratio so lets try the new aproach
        int[] size = ImageUtils.keep_ratio_for_size(this.getWidth(), this.getHeight(), img);
        g.drawImage(img, size[2], size[3], size[0], size[1], this);
    }

    /**
     * sets the image on the panel but stretch to the PANEL size so this will
     * not respect the ratio will fill the hold panel.
     */
    private void paint_default(Graphics g) {
        g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
    }

    /**
     * draws the image all over the Panel as we want a textured Panel.
     *
     * @param g
     */
    private void paintsTexture(Graphics g) {
        if (!(img instanceof BufferedImage)) {
            ORGimg = ImageUtils.toBufferedImage(img);
            img = ORGimg;
        }
        Paint tempaint = ((Graphics2D) g).getPaint();
        TexturePaint textpaint = new TexturePaint((BufferedImage) img, new Rectangle(0, 0, ((BufferedImage) img).getWidth(), ((BufferedImage) img).getHeight()));
        ((Graphics2D) g).setPaint(textpaint);
        int Xupto=getWidth();
        if(!repeat_X){
          Xupto=((BufferedImage) img).getWidth();
        }
        int yupto=getHeight();
        if(!repeat_Y){
          yupto=((BufferedImage) img).getHeight();
        }
        ((Graphics2D) g).fillRect(0, 0, Xupto, yupto);
        ((Graphics2D) g).setPaint(tempaint);
    }

    /**
     * provides the image Dimension. the Dimensions are generated each time this
     * method is called.
     */
    public Dimension getImageSize() {
        if (img != null) {
            return new Dimension(img.getWidth(null) == -1 ? 0 : img.getWidth(null),
                    img.getHeight(null) == -1 ? 0 : img.getHeight(null));

        } else {
            return new Dimension();
        }
    }
}
