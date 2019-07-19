/*
 * 
 *   Copyright ? 2019 Eduardo Vindas Cordoba. All rights reserved.
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class neatScrollPanel extends JPanel implements Scrollable {

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        if (getSize() != null) {
            return getSize();
        } else {
            return getPreferredSize();
        }
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.VERTICAL) {
            Component comp = null;
            int pix = 10;
            if (direction > 0) {
                try {
                    do {
                        comp = getComponentAt(getWidth() / 2, visibleRect.y + visibleRect.height + pix);
                        pix--;
                        if (pix == 0 && comp == null) {
                            break;
                        }
                    } while (comp == null);
                    if (comp == this) {
                        return 10;
                    } else if (comp != null) {
                        int tosumy = comp.getBounds().y + comp.getBounds().height;
                        tosumy = tosumy - (visibleRect.y + visibleRect.height);
                        if (tosumy > 0) {
                            return tosumy;
                        } else {
                            return 10;
                        }
                    }
                } catch (Exception e) {
                }
            } else {
                try {
                    pix = 3;
                    do {
                        comp = getComponentAt(getWidth() / 2, visibleRect.y - pix);
                        pix++;
                        if (pix == 13 && comp == null) {
                            break;
                        }
                    } while (comp == null);
                    if (comp == this) {
                        return 10;
                    } else if (comp != null) {
                        int tosumy = comp.getBounds().y;
                        tosumy = visibleRect.y - tosumy;
                        if (tosumy > 0) {
                            return tosumy;
                        } else {
                            return 10;
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        return 10;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.VERTICAL) {
            Component comp = null;
            int pix = 10;
            if (direction > 0) {
                try {
                    do {
                        comp = getComponentAt(getWidth() / 2, visibleRect.y + visibleRect.height + pix);
                        pix--;
                        if (pix == 0 && comp == null) {
                            break;
                        }
                    } while (comp == null);
                    if (comp == this) {
                        return 10;
                    } else if (comp != null) {
                        int tosumy = comp.getBounds().y + comp.getBounds().height;
                        tosumy = tosumy - (visibleRect.y + visibleRect.height);
                        if (tosumy > 0) {
                            return tosumy;
                        } else {
                            return 10;
                        }
                    }
                } catch (Exception e) {
                }
            } else {
                try {
                    pix = 3;
                    do {
                        comp = getComponentAt(getWidth() / 2, visibleRect.y - pix);
                        pix++;
                        if (pix == 13 && comp == null) {
                            break;
                        }
                    } while (comp == null);
                    if (comp == this) {
                        return 10;
                    } else if (comp != null) {
                        int tosumy = comp.getBounds().y;
                        tosumy = visibleRect.y - tosumy;
                        if (tosumy > 0) {
                            return tosumy;
                        } else {
                            return 10;
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        return 10;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
    


}
