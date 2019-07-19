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
package com.aeongames.crypto.signature;

import static com.aeongames.blockchain.base.common.ByteUtils.ByteArrayToString;
import static com.aeongames.crypto.signature.DevicesHelper.getdevices;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.FailedLoginException;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardNotPresentException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import com.aeongames.edi.utils.File.properties_File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class SmartCardChecker {
    private static final String MAPIN="<<enter the pin to this variable>>";
    public SmartCardChecker() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * this test will list the installed hardware(smartcards) and test if able
     * to send a challenge (if so means we can use its crypto task .......
     * hopefully...
     *
     * @throws CardException
     */
    @Test
    public void TestSmartCards() throws CardException {
        List<CardTerminal> terminals = getdevices();
        if (terminals != null) {
            for (CardTerminal terminal : terminals) {
                try {
                    System.out.println(String.format("Device Name: %s has Smart Card ready: %s", terminal.getName(), terminal.isCardPresent() ? "Yes" : "No"));
                    if (terminal.isCardPresent()) {
                        try {
                            Card card = terminal.connect("*");
                            System.out.println("Card: " + card); //--> problem. the underline CARD ID cannot be read unless native code is added... not gonna do that.
                            CardChannel channel = card.getBasicChannel();
                            //https://stackoverflow.com/questions/30550899/what-is-the-structure-of-an-application-protocol-data-unit-apdu-command-and-re/30552223
                            //00 - class byte (CLA, 00 means "inter-industry command sent to logical channel 0")
                            //https://cardwerk.com/smart-card-standard-iso7816-4-section-5-basic-organizations/
                            //https://www.win.tue.nl/pinpasjc/docs/GPCardSpec_v2.2.pdf
                            //http://javacard.vetilles.com/2008/04/28/jc101-11c-attacks-on-smart-cards/
                            /**
                             * 0X // or 0000 second byte. please read the info
                             * on
                             * https://cardwerk.com/smart-card-standard-iso7816-4-section-5-basic-organizations/
                             *
                             */
                            final int DEFAULTCLA = 0b0000_0000;//-->this shit here is a byte with 0 represented in a binary way so we can study deeper later my dude.
                            final int GET_CHALLENGE = 0x84;/*Instruction byte*/
                            final int NOPARAM = 0b0;
                            final int EXPECTED_RESPONSE_BYTES = 0x08;
                            ResponseAPDU r = channel.transmit(new CommandAPDU(DEFAULTCLA, GET_CHALLENGE, NOPARAM, NOPARAM, EXPECTED_RESPONSE_BYTES));
                            /**
                             * https://cardwerk.com/smart-card-standard-iso7816-4-section-5-basic-organizations/
                             * see table Table 12 currently and for this test we
                             * will check IF we can get the CHALLENGE. so lets
                             * see if the response is valid
                             */
                            final int INSTRUCTION_NOT_SUPPORTED = 0x6D00;
                            //if we cannot assert that this test is false means the card is not ready for other tests.
                            //maybe the card driver is not correct. or the card is not setup. not our problem.
                            assertFalse(r.toString(),r.getSW() == INSTRUCTION_NOT_SUPPORTED);
                            String hex = ByteArrayToString(r.getBytes());
                            System.out.println("Response: " + hex);
                            // disconnect card:
                            card.disconnect(false);
                        } catch (CardNotPresentException err) {
                            //card was removed? or something fail!
                            //fail the test... this should not happen on our test.
                            assertNull("an Exeption Happend.", err);
                            //throw err;
                        } catch (CardException generalErr) {
                            //card fail for some reason, Corrupt or wrong driver!?
                            //fail the test... this should not happen on our test.
                            assertNull("an Exeption Happend.", generalErr);
                            //throw generalErr;
                        }
                    }
                } catch (CardException ex) {
                    //fail the test... this should not happen on our test.
                    assertNull("an Exeption Happend.", ex);
                }
            }
        }
        System.out.println("Test Finish.");
    }

    /**
     * this test will attempt to access the Smart card with a wrong pin and
     * fail. if it fail with other criteria other than FailedLoginException this
     * test will fail. if the authentication is successful it will fail the test
     * as the test is intended to have a FailedLoginException
     */
    @Test
    public void TestWrongPinForSmartCard() {
        boolean expectederrorHappend = false;
        //run this test alone as it will fail as even if we send the wrong pin
        //if the smartcard is alredy logged on it will allow us to use the smart card. dunno how long the smart card leave
        //the session open. and the needful is not exposed.
        try {
            DigitalSignatureHelper.LoadSmartCardPKCSProvider();
        } catch (IOException ex) {
            Logger.getLogger(SmartCardChecker.class.getName()).log(Level.SEVERE, null, ex);
            //fail the test... this should not happen on our test.
            fail("an Exeption Happend.");
        }
        // this pin is wrong lets see if it fails.
        char[] pin = "12345".toCharArray();
        try {
            KeyStore ks = DigitalSignatureHelper.getKeystore(pin);
            System.out.println(ks);
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException ex) {
            Throwable theone = ex;
            while (!(theone instanceof FailedLoginException)) {
                theone = theone.getCause();
                if (theone == null) {
                    break;
                }
            }
            expectederrorHappend = (theone instanceof FailedLoginException);
            //log only if the error is not the one expected
            if (!expectederrorHappend) {
                Logger.getLogger(SmartCardChecker.class.getName()).log(Level.SEVERE, null, ex);
                fail("unexpected Exception");
            }
        }
        assertTrue(expectederrorHappend);
    }

    @Test
    public void TestSmartCardCrypto() {
        try {
            DigitalSignatureHelper.LoadSmartCardPKCSProvider();
        } catch (IOException ex) {
            Logger.getLogger(SmartCardChecker.class.getName()).log(Level.SEVERE, null, ex);
            //fail the test... this should not happen on our test.
            fail("an Exeption Happend.");
        }
        // Load the key store  TODO: PUT TEST PIN: 
        char[] pin = MAPIN.toCharArray();
        try {
            KeyStore ks = DigitalSignatureHelper.getKeystore(pin);
            properties_File settings = new properties_File("/com/aeongames/blockchain/resources/Blockchainprops.properties");
            assertTrue(ks.containsAlias(settings.getProperty("SignatureKeyAlias")));// the Signature alias MUST be there
            X509Certificate tmp = (X509Certificate) ks.getCertificate(settings.getProperty("SignatureKeyAlias"));
            System.out.println(tmp);
            byte signedinfo[] = null;
            String algorithm="SHA256withRSA";
            assertNotNull("Certificate information is non null", tmp);
            try {
                Signature RSASHA = DigitalSignatureHelper.getSignatureObject_toSign(ks,settings.getProperty("SignatureKeyAlias"),algorithm,new KeyStore.PasswordProtection(pin));
                if (RSASHA != null) {//--> should not be
                    RSASHA.update(Files.readAllBytes(Paths.get("build.gradle")));
                    signedinfo=RSASHA.sign();
                    System.out.println(ByteArrayToString(signedinfo));
                }else{
                    fail("Something went wrong the Signer is null!");
                }
            } catch (UnrecoverableEntryException | InvalidKeyException | SignatureException ex) {
                Logger.getLogger(SmartCardChecker.class.getName()).log(Level.SEVERE, null, ex);
                fail("exception detected. this should not be");
            }
            try {
                Signature RSASHA = DigitalSignatureHelper.getSignatureObject_toVerify(ks,algorithm, tmp);
                RSASHA.update(Files.readAllBytes(Paths.get("build.gradle")));
               boolean result= RSASHA.verify(signedinfo);
               assertTrue(result);
            } catch (InvalidKeyException ex) {
                Logger.getLogger(SmartCardChecker.class.getName()).log(Level.SEVERE, null, ex);
                fail("exception detected. this should not be");
            } catch (SignatureException ex) {
                Logger.getLogger(SmartCardChecker.class.getName()).log(Level.SEVERE, null, ex);
            }
             
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException ex) {
            Logger.getLogger(SmartCardChecker.class.getName()).log(Level.SEVERE, null, ex);
            fail("exception detected. this should not be");
        }

    }
}
