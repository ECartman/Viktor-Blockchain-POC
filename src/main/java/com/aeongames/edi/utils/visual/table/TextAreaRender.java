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
/**
 * TextAreaRender.java
 *  based on subclass at journalview.java
 * Created on November 5, 2008, 3:56 PM
 * Created on 25/04/2011, 11:49:59 AM
 */
package com.aeongames.edi.utils.visual.table;

/**
 *
 * @author Eduardo Vindas C <eduardo.vindas@hp.com>
 */
public class TextAreaRender extends javax.swing.JTextArea
        implements javax.swing.table.TableCellRenderer {

    public TextAreaRender() {
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    @Override
    public java.awt.Component getTableCellRendererComponent(javax.swing.JTable jTable,
            Object obj, boolean isSelected, boolean hasFocus, int row,
            int column) {
        setText((String) obj);
        return this;
    }
    
}
