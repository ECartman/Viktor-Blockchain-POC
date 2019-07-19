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
package com.aeongames.edi.utils.text;

import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * this class is intended for crate a Document filter that twill delimiter the
 * amount of characters that can be enter on a Document as example a Document on
 * a text field component
 *
 * @version 1.0
 * @author Eduardo Vindas C
 */
public class DocumentDelimiterFilter extends DocumentFilter {

    private boolean Enforce = true,
                    TextTrim=false;
    private int maxCharacters;

    /**
     *
     * will create a delimiter of the parameter max characters
     *
     * @param maxChars
     */
    public DocumentDelimiterFilter(int maxChars) {
        this(maxChars, true);
    }

    /**
     *
     * will create a delimiter of the parameter max characters
     *
     * @param maxChars
     * @param enforce set if it is required to enforce the limit
     */
    public DocumentDelimiterFilter(int maxChars, boolean enforce) {
        maxCharacters = maxChars;
        Enforce = enforce;
    }
    
    
        /**
     *
     * will create a delimiter of the parameter max characters
     *
     * @param maxChars
     * @param trim_text
     * @param enforce set if it is required to enforce the limit
     */
    public DocumentDelimiterFilter(int maxChars,boolean trim_text, boolean enforce) {
        maxCharacters = maxChars;
        Enforce = enforce;
        TextTrim=trim_text;
    }

    /**
     * {@inheritDoc } <p> This rejects the entire insertion if it would make the
     * contents too long. so we disallow and sent a beep to the pc however if
     * the enforce variable is set to false the rule will be bypassed, however a
     * warning might be called.
     */
    @Override
    public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
        int lenght=TextTrim?str.trim().length():str.length();
        boolean disallow = (fb.getDocument().getLength() +lenght) > maxCharacters;
        if (disallow) {
            Toolkit.getDefaultToolkit().beep();
        }
        if (!disallow || !Enforce) {
            super.insertString(fb, offs, TextTrim?str.trim():str, a);
        }
    }

    /**
     * {@inheritDoc } <p> this rejects the entire replacement if it would make
     * the contents too long. Another option would be to truncate the
     * replacement string so the contents would be exactly maxCharacters in
     * length. unless the enforce is false.
     */
    @Override
    public void replace(FilterBypass fb, int offs, int length, String str,
            AttributeSet a) throws BadLocationException {
        if (str != null) {
            boolean disallow = (fb.getDocument().getLength() + str.length() - length) > maxCharacters;
            if (disallow) {
                Toolkit.getDefaultToolkit().beep();
            }
            if (!disallow || !Enforce) {
                super.replace(fb, offs, length, str, a);
            }
        } else {
            super.replace(fb, offs, length, str, a);
        }
    }
}
