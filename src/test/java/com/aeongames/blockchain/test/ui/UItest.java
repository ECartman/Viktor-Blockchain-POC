/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aeongames.blockchain.test.ui;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.aeongames.blockchain.ui.ViktorUI;
import com.aeongames.logger.LoggingHelper;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.security.Security;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import static org.junit.Assert.fail;
import org.pushingpixels.substance.api.SubstanceCortex;
import org.pushingpixels.substance.api.SubstanceSlices;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 *
 * @author edvindas
 */
public class UItest {

    public UItest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
    }

    @After
    public void tearDown() {
    }

    @Test
    public void TestUI() {
        final ReentrantLock lock = new ReentrantLock();
        /** ensure REDIS is running */
        try (JedisPool pool = new JedisPool()) {
            try (Jedis jeditest = pool.getResource()) {
                System.out.println(jeditest.isConnected());
            }
        } catch (Exception err) {
            LoggingHelper.getAClassLogger("UI").log(Level.SEVERE, "Redis not Running ", err);
            fail("Redis not Running ");
        }
        /* Create and display the form */
        try {
            SwingUtilities.invokeAndWait(() -> {
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
                try {
                    ViktorUI userint = new ViktorUI();

                    // And increase the height of the title pane to play nicer with additional
                    // content that we are displaying in that area.
                    // SubstanceCortex.WindowScope.setPreferredTitlePaneHeight(userint, 40);
                    // Set initial size, center in screen, configure to exit the app on clicking the
                    // close button
                    userint.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    userint.addWindowListener(new WindowListener(){
                    
                        @Override
                        public void windowOpened(WindowEvent e) {
                            
                        }
                    
                        @Override
                        public void windowIconified(WindowEvent e) {
                            
                        }
                    
                        @Override
                        public void windowDeiconified(WindowEvent e) {
                            
                        }
                    
                        @Override
                        public void windowDeactivated(WindowEvent e) {
                            
                        }
                    
                        @Override
                        public void windowClosing(WindowEvent e) {
                            //lock.unlock();
                        }
                    
                        @Override
                        public void windowClosed(WindowEvent e) {
                            lock.unlock();
                        }
                    
                        @Override
                        public void windowActivated(WindowEvent e) {
                            
                        }
                    });
                    userint.setVisible(true);
                    lock.lock();
                } catch (Exception ex) {
                    LoggingHelper.getAClassLogger("UI").log(Level.SEVERE, "Severe Error Happend or was not handled! ",
                            ex);
                    UItest.this.notifyAll();
                    fail("Severe Error Happend or was not handled!");
                    lock.unlock();
                }
            });
            lock.lock();
        } catch (InvocationTargetException | InterruptedException e) {
            LoggingHelper.getAClassLogger("UI").log(Level.SEVERE, "Severe Error Happend or was not handled! ", e);
            fail("Severe Error Happend or was not handled!");
        }
        lock.unlock();
    }
}
