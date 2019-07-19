/*
 * 
 *   Copyright © 2019 Eduardo Vindas Cordoba. All rights reserved.
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

import com.aeongames.blockchain.base.BlockchainPool;
import com.aeongames.blockchain.base.PoolChangeListener;
import com.aeongames.blockchain.base.common.ByteUtils;
import com.aeongames.blockchain.base.transactions.ITransaction;
import com.aeongames.expediente.FileRecord;
import com.aeongames.logger.LoggingHelper;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class ChaintoPaneListener implements PoolChangeListener<FileRecord> {

    private final JTextPane underlinePane;
    public static final Color ACCEPTED_COLOR = Color.GREEN;
    public static final Color ERROR_COLOR = Color.RED;
    public static final Color WARNING_COLOR = Color.YELLOW;
    public static final Color DEFAULT_COLOR = UIManager.getColor("TextPane.foreground");

    public ChaintoPaneListener(JTextPane txtoutputpane) {
        underlinePane=txtoutputpane;
    }

    @Override
    public void NotifyAccepted(BlockchainPool<FileRecord> CallerPool, List<FileRecord> CommitedTransactions) {
        try {
            //this is almost 100% ensured to run on a non EDT therefore we need to be mindful of the threading.
            SwingUtilities.invokeAndWait(() -> {
                appendToPane(underlinePane,"One or more Transactions got accepted to the Chain",ACCEPTED_COLOR);
                appendToPane(underlinePane,"Accepted Transactions:",ACCEPTED_COLOR);
                CommitedTransactions.stream().map((Record) -> String.format("\tTransaction: %s\n\t\t%s", Record.getName(),
                        ByteUtils.ByteArrayToString(Record.getTransactionByteBuffer()))).forEachOrdered((msg) -> {
                            appendToPane(underlinePane,msg,ACCEPTED_COLOR);
                });
            });
        } catch (InterruptedException | InvocationTargetException ex) {
             LoggingHelper.getAClassLogger("UI").log(Level.SEVERE, "Error Updating the UI", ex);
        }
    }

    @Override
    public void NotifyDoubleSpent(BlockchainPool<FileRecord> CallerPool, List<? extends ITransaction> DoubleSpentTransactions) {
        try {
            //this is almost 100% ensured to run on a non EDT therefore we need to be mindful of the threading.
            SwingUtilities.invokeAndWait(() -> {
                appendToPane(underlinePane,"Duplicate Record Detected, it has been denied",WARNING_COLOR);
                appendToPane(underlinePane,"denied Transactions:",WARNING_COLOR);
                DoubleSpentTransactions.stream().map((Record) -> String.format("\tTransaction: %s\n\t\t%s",
                        (Record instanceof FileRecord)? ((FileRecord)Record).getName():"unknown Record",
                        Record!=null?ByteUtils.ByteArrayToString(Record.getTransactionByteBuffer()):""
                )).forEachOrdered((msg) -> {
                            appendToPane(underlinePane,msg,WARNING_COLOR);
                });
            });
        } catch (InterruptedException | InvocationTargetException ex) {
             LoggingHelper.getAClassLogger("UI").log(Level.SEVERE, "Error Updating the UI", ex);
        }
    }

    @Override
    public void NotifyNotAccepted(BlockchainPool<FileRecord> CallerPool, List<FileRecord> denied) {
        try {
            //this is almost 100% ensured to run on a non EDT therefore we need to be mindful of the threading.
            SwingUtilities.invokeAndWait(() -> {
                appendToPane(underlinePane,"Transactions where denied to the Chain",ERROR_COLOR);
                appendToPane(underlinePane,"denied Transactions:",ERROR_COLOR);
                denied.stream().map((Record) -> String.format("\tTransaction: %s\n\t\t%s", Record.getName(),
                        ByteUtils.ByteArrayToString(Record.getTransactionByteBuffer()))).forEachOrdered((msg) -> {
                            appendToPane(underlinePane,msg,ERROR_COLOR);
                });
            });
        } catch (InterruptedException | InvocationTargetException ex) {
             LoggingHelper.getAClassLogger("UI").log(Level.SEVERE, "Error Updating the UI", ex);
        }
    }

    @Override
    public void NotifyInvalidSignature(BlockchainPool<FileRecord> CallerPool, List<FileRecord> AttemptedTransactions) {
        try {
            //this is almost 100% ensured to run on a non EDT therefore we need to be mindful of the threading.
            SwingUtilities.invokeAndWait(() -> {
                appendToPane(underlinePane,"Transactions where denied due they are invalid the Chain",ERROR_COLOR);
                appendToPane(underlinePane,"denied Transactions:",ERROR_COLOR);
                AttemptedTransactions.stream().map((Record) -> String.format("\tTransaction: %s\n\t\t%s", Record.getName(),
                        ByteUtils.ByteArrayToString(Record.getTransactionByteBuffer()))).forEachOrdered((msg) -> {
                            appendToPane(underlinePane,msg,ERROR_COLOR);
                });
            });
        } catch (InterruptedException | InvocationTargetException ex) {
             LoggingHelper.getAClassLogger("UI").log(Level.SEVERE, "Error Updating the UI", ex);
        }
    }

    @Override
    public void Tick(BlockchainPool<FileRecord> CallerPool) {
        //we dont care for ticks the commented code was  for testing mate. 
//        try {
//            //this is almost 100% ensured to run on a non EDT therefore we need to be mindful of the threading.
//            SwingUtilities.invokeAndWait(() -> {
//                appendToPane(underlinePane,"clock tick... clock tock... time is moving forward",DEFAULT_COLOR);
//                
//            });
//        } catch (InterruptedException | InvocationTargetException ex) {
//             LoggingHelper.getAClassLogger("UI").log(Level.SEVERE, "Error Updating the UI", ex);
//        }
    }

    private void appendToPane(JTextPane tp, String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, UIManager.getDefaults().getFont("TextPane.font").getFamily());
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_LEFT);
        int len = tp.getDocument().getLength();
        try {
            tp.getDocument().insertString(len, String.format("%s\n", msg), aset);

        } catch (BadLocationException ex) {
        }
        tp.setCaretPosition(tp.getDocument().getLength());
    }

}
