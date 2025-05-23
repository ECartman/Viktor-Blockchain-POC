/*
 * 
 * Copyright © 2010-2024 Eduardo Vindas Cordoba. All rights reserved.
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
 * LabelText.java
 *
 * Created on 03/11/2010, 03:47:39 PM
 */
package com.aeongames.edi.utils.text;

import java.awt.Component;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JLabel;

/**
 *
 * @author Eduardo Vindas C
 * @version 1.8
 */
public class LabelText {

    /**
     * this method is designed to Wrap the text provided on a label avoiding it
     * to force the label to resize. so how does it work, it takes and test the
     * JLabel (font and usable space ) then measure the string to use and
     * determine if it is too big for the assign space if so will remove the
     * additional text and add &quot;...&quot; to the end of this string and set
     * the tool tip text as the required string <br>
     * <strong>WARNINGS</strong>:<br> this method <strong>DOES NOT support HTML
     * text wrap!!</strong><br> this implementation <strong>is not Thread
     * Safe!</strong>
     *
     * @param label, the label to add the text into
     * @param text, the text to be added or to be contained on the label
     * this object will not be edited or changed.
     */
    public static void wrapLabelText(final JLabel label, final String text) {
        final String finalstring = getTrimmedtoComponentsize(text, label);
        if (EventQueue.isDispatchThread()) {
            label.setText(finalstring);
        } else {
            /*
             * we should be more carefully to disallow this on this method, i
             * will allow for now as there are many clases that might expect
             * help to call AWT
             */
            java.awt.EventQueue.invokeLater(() -> {
                label.setText(finalstring);
            });
        }
    }

    /**
     * this method is designed to Wrap the text provided on a label avoiding it
     * to force the label to resize. so how does it work, it takes and test the
     * JLabel (font and usable space ) then measure the string to use and
     * determine if it is too big for the assign space if so will remove the
     * additional text and add &quot;...&quot; to the end of this string and set
     * the tool tip text as the required string <br>
     * <strong>WARNINGS</strong>:<br> this method <strong>DOES NOT support HTML
     * text wrap!!</strong><br> this implementation is sync-ed and <strong>the
     * caller will wait until the call is done.</strong> when not run from Event
     * Dispatched Thread.
     *
     * @param label
     * @param text
     */
    public static void SyncwrapLabelText(final JLabel label, final String text) {
        final String finalstring = getTrimmedtoComponentsize(text, label);
        if (EventQueue.isDispatchThread()) {
            label.setText(finalstring);
        } else {
            try {
                java.awt.EventQueue.invokeAndWait(() -> {
                    label.setText(finalstring);
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                java.awt.EventQueue.invokeLater(() -> {
                    label.setText(finalstring);
                });
            }
        }
    }
    
    private static final String FILLER_TEXT="...";
    /**
     * this method will compute the amount of text that the provided label is
     * able to fit and display. and will return a string trimmed to the size of
     * what can be displayed. this method will not alter the original string
     * provided.
     *
     * @param data the string data that will be computer if is possible to fit.
     * @param UIComponent the label that will display the data.
     * @return String that will contain a trimmed string with the possible data
     * displayable if the string is too long will also calculate space to add
     * &quot;...&quot;
     */
    public static String getTrimmedtoComponentsize(final String data, final Component UIComponent) {
        if (data != null && UIComponent != null) {
            java.awt.FontMetrics fm = UIComponent.getFontMetrics(UIComponent.getFont());
            int containerWidth = UIComponent.getWidth();
            StringBuilder trial = new StringBuilder();
            StringBuilder real = new StringBuilder();
            if (!data.trim().equals("")) {
                int limit = (containerWidth - javax.swing.SwingUtilities.computeStringWidth(fm, FILLER_TEXT));
                for (int location = 0; location < data.length(); location++) {
                    char letter = data.charAt(location);
                    trial.append(letter);
                    int trialWidth = javax.swing.SwingUtilities.computeStringWidth(fm,
                            trial.toString());
                    if (trialWidth >= limit) {
                        if (location + 1 >= data.length()) {
                            //this is the last char and no more is avaial so add ... is not required.
                            break;
                        } else if (trialWidth > limit) {
                            if (real.length() > 0) {
                                real.deleteCharAt(real.length() - 1);
                            }
                        }
                        real.append(FILLER_TEXT);
                        break;
                    }
                    real.append(letter);
                }
            }
            trial.delete(0, trial.length());
            return real.toString();
        } else {
            throw new NullPointerException("Null values are not allowed!");
        }
    }
}
