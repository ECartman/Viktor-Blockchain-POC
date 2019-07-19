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
package com.aeongames.blockchain;

import com.aeongames.blockchain.ui.ViktorUI;
import com.aeongames.logger.LoggingHelper;
import java.security.Security;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.pushingpixels.substance.api.SubstanceCortex;
import org.pushingpixels.substance.api.SubstanceSlices;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class application {

    /*
    this static block is required in order to ensure we can call all and with unlimited access to 
    crypto functions. java in its idiocity or fear choose to limit cripto in one point. (it was related to 
    limitation in some countries. but this is old and obsolete ideas) 
    this app will be compiled against Java 10+ however if require for older please read and review 
    https://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
     */
    static {
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.LookAndFeelInfo[] avails = javax.swing.UIManager.getInstalledLookAndFeels();
            for (javax.swing.UIManager.LookAndFeelInfo info : avails) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            LoggingHelper.getAClassLogger("UI").log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        SwingUtilities.invokeLater(() -> {
            LookAndFeel laf = new SubstanceGraphiteGlassLookAndFeel();
            try {
                javax.swing.UIManager.setLookAndFeel(laf);
            } catch (UnsupportedLookAndFeelException ex) {
                LoggingHelper.getAClassLogger("UI").log(Level.SEVERE, null, ex);
            }
            // Configure the main skin
            // SubstanceCortex.GlobalScope.setSkin(new );
            SubstanceCortex.GlobalScope.setFocusKind(SubstanceSlices.FocusKind.NONE);
            JFrame.setDefaultLookAndFeelDecorated(true);

            // Create the main frame
            ViktorUI userint = new ViktorUI();

            // And increase the height of the title pane to play nicer with additional
            // content that we are displaying in that area.
            // SubstanceCortex.WindowScope.setPreferredTitlePaneHeight(userint, 40);
            // Set initial size, center in screen, configure to exit the app on clicking the
            // close button
            userint.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            userint.setVisible(true);
        });
    }

}
