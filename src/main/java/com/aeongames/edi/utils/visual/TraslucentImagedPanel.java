/*
 *  Copyright © 2008-2012 Eduardo Vindas Cordoba. All rights reserved.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.aeongames.edi.utils.visual;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

/**
 *
 * @author Eduardo Jose Vindas Cordoba <cartman@aeongames.com>
 */
public class TraslucentImagedPanel extends ImagePanel {

    public static final int DEFAULTARC = 0;
    public static final int DEFTRANSPARENCY = 190;
    public static final int MIN_TRANSPARENCY=0,MAX_TRANSPARENCY=255;
    public static final Color DEFCOL = new Color(219, 229, 241, DEFTRANSPARENCY); //r,g,b,alpha
    private int arcWidth = DEFAULTARC, arcHeight = DEFAULTARC;
    private int trasparency = DEFTRANSPARENCY;
    private Color ppColor = DEFCOL;

    public TraslucentImagedPanel() {
        super();
        super.setOpaque(false);
        setcolor(getBackground());
    }

    public TraslucentImagedPanel(Image todisplay) {
        super(todisplay);
        super.setOpaque(false);
        setcolor(getBackground());
    }
    
    private TraslucentImagedPanel(Image todisplay, float Imagealpha) {
        super(todisplay,Imagealpha);
        super.setOpaque(false);
        setcolor(getBackground());
    }

    /**
     * unlike the original implementation on this case if you set opaque or not will result in either begin completely opaque or 
     * complete transparent on the internal alpha level. so it will not call the parent implementation. however the result 
     * will appear to be the same
     * NOTE: on this case it does not affect the image! 
     * @param isOpaque 
     */
    @Override
    public void setOpaque(boolean isOpaque) {
        if (isOpaque) {
            setPanelTrasparency(MAX_TRANSPARENCY);
        }else{
            setPanelTrasparency(MIN_TRANSPARENCY);
        }
    }
    
    public void setPanelTrasparency(int Trasparency) {
        if (Trasparency >= MIN_TRANSPARENCY && Trasparency <= MAX_TRANSPARENCY) {
            trasparency = Trasparency;
            setcolor(ppColor);
        }
        repaint();
    }

    public int getpanelAlpha() {
        return trasparency;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color temp = g.getColor();
        g.setColor(ppColor);
        g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), arcWidth, arcHeight);
        g.setColor(temp);
    }

    public void setuniformarc(int arc) {
        if (arc > 0) {
            arcWidth = arcHeight = arc;
        }
    }

    /**
     * @param arcWidth the arcWidth to set
     */
    public void setArcWidth(int arcWidth) {
        if (arcWidth > 0) {
            this.arcWidth = arcWidth;
        }
    }

    /**
     * @param arcHeight the arcHeight to set
     */
    public void setArcHeight(int arcHeight) {
        if (arcHeight > 0) {
            this.arcHeight = arcHeight;
        }
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (bg != null) {
            setcolor(bg);
        } else {
            setcolor(DEFCOL);
        }
    }

    public final void setcolor(Color col) {
        if (col != null) {
            int r = col.getRed();
            int g = col.getGreen();
            int b = col.getBlue();
            ppColor = new Color(r, g, b, trasparency);
        }
        this.repaint();
    }
}
