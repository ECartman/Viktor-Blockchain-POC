/*
 * 
 *   Copyright � � 2019 Eduardo Vindas Cordoba. All rights reserved.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import static com.aeongames.crypto.signature.DevicesHelper.getdevices;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardNotPresentException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

/**
 * reference read
 * https://docs.oracle.com/javase/9/security/pkcs11-reference-guide1.htm#JSSEC-GUID-99785B51-50D8-458E-AA2C-755749F1E39E
 * ...
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class DigitalSignatureHelper {

    /**
     * here we define de Smart card reader provider. this is EXTREMELY likely to
     * change in a future release of JDK due the shit Oracle is doing to JAVA so
     * HAVE THIS INTO ACCOUNT
     */
    public static final String PKCS_SEC_PROVIDER = "SunPKCS11";
    public static final String FILTER_KEY = "filter";
    public static String PKCS_SEC_PROVIDER_LOADED = PKCS_SEC_PROVIDER;
    /**
     * this value is internal resource. a future enhancement can be added to read
     * resource from outside of the application (a file on the underline filesystem)
     */
    private static final String CONF_FILE = "/com/aeongames/blockchain/resources/Pkcs11.properties";

    protected static boolean checkSmartCardIsPresent() {
        List<CardTerminal> terminals = getdevices();
        boolean result = false;
        if (terminals != null) {
            for (CardTerminal terminal : terminals) {
                try {
                    if (terminal.isCardPresent()) {
                        Card card = null;
                        CardChannel channel = null;
                        try {
                            card = terminal.connect("*");
                            channel = card.getBasicChannel();
                            ResponseAPDU r = channel.transmit(new CommandAPDU(DevicesHelper.DEFAULTCLA,
                                    DevicesHelper.GET_CHALLENGE, DevicesHelper.NOPARAM, DevicesHelper.NOPARAM,
                                    DevicesHelper.EXPECTED_RESPONSE_BYTES));
                            if (r.getSW() != DevicesHelper.INSTRUCTION_NOT_SUPPORTED && r.getBytes() != null) {
                                // String hex = ByteArrayToString(r.getBytes());
                                result = true;
                            }
                        } catch (CardNotPresentException err) {
                            if (channel != null) {
                                try {
                                    channel.close();
                                } catch (CardException ex) {
                                    // log?
                                }
                            }
                            if (card != null) {
                                try {
                                    card.disconnect(false);
                                } catch (CardException ex) {
                                    // log?
                                }
                            }
                        } catch (CardException generalErr) {
                            if (channel != null) {
                                try {
                                    channel.close();
                                } catch (CardException ex) {
                                    // log?
                                }
                            }
                            if (card != null) {
                                try {
                                    card.disconnect(false);
                                } catch (CardException ex) {
                                    // log?
                                }
                            }
                        }
                    }
                } catch (CardException ex) {
                }
            }
        }
        return result;
    }

    /**
     * this method will load the PKCS_SEC_PROVIDER (likely the default PKCS11
     * provider) and will configure with the CONF_FILE ( a internal Resource)
     *
     * @throws SecurityException if a smart card is not detected.
     * @throws IOException       if a IO or Security error is detected
     */
    public static void LoadSmartCardPKCSProvider() throws IOException {
        LoadSmartCardPKCSProvider(Optional.of(Boolean.TRUE));
    }

    /**
     * this method will load the PKCS_SEC_PROVIDER (likely the default PKCS11
     * provider) and will configure with the CONF_FILE ( a internal Resource)
     *
     * @param FailNotDetected a Optional Parameter. this parameter CAN be null if so
     *                        its handle as if it were "false" this parameter
     *                        defines a option to "fast fail" this method will
     *                        thrown a exception if the smart card expected is not
     *                        detected instead of loading the provider. this however
     *                        might be detrimental if you expect it to be set up at
     *                        some other point during the app lifecycle so you might
     *                        want to set as false or null if you desire to use as
     *                        "load the provider the smart card is not plugged yet"
     * @throws SecurityException if a smart card is not
     *                           detected.
     * @throws IOException       if a IO or Security error is detected
     */
    public static void LoadSmartCardPKCSProvider(Optional<Boolean> FailNotDetected) throws IOException {
        Provider p = Security.getProvider(PKCS_SEC_PROVIDER);
        Properties withinProperties = loadInternalProperties(CONF_FILE, DigitalSignatureHelper.class, false);
        if (!checkSmartCardIsPresent()) {
            if (Objects.nonNull(FailNotDetected) && FailNotDetected.isPresent() && FailNotDetected.get()) {
                throw new SecurityException("unable to Register the provider: no SmartCard Detected.");
            }
        }
        p = p.configure(getConfigurations(withinProperties));
        int result = Security.addProvider(p);
        if (result != -1) {
            PKCS_SEC_PROVIDER_LOADED = p.getName();
            // already registered... do nothing
            //removing filter here, i noted the filter thing 
            //is the Smartcard Reader brand rather than the Smartcard itself.
            // throw new SecurityException("unable to Register the provider");
        }
    }

    public static KeyStore getKeystore(char pin[])
            throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        Provider p = Security.getProvider(PKCS_SEC_PROVIDER_LOADED);
        KeyStore KS = KeyStore.getInstance("PKCS11", p);
        KS.load(null, pin);
        return KS;
    }

    /**
     *
     * @param Path      the internal resource to read
     * @param classfrom the class calling to read
     * @param XML       if the read file is XML or props file
     * @return
     * @throws java.io.IOException is error reading happends.
     */
    protected static synchronized Properties loadInternalProperties(String Path, Class<?> classfrom, boolean XML)
            throws java.io.IOException {
        Properties p = new Properties();
        InputStream resource = classfrom.getResourceAsStream(Path);
        if (resource != null) {
            if (!XML) {
                p.load(resource);
            } else {
                p.loadFromXML(resource);
            }
        } else {
            throw new IOException("Cannot Load The Resource");
        }
        return p;
    }

    /**
     *
     * @param My_properties the properties to load into a string
     * @return a String with a representation of this properties.
     * @throws NullPointerException if My_properties is null
     */
    protected static synchronized String getConfigurations(Properties My_properties) {
        Objects.requireNonNull(My_properties, "properties are null");
        StringWriter writter = new StringWriter();
        // the following is required so the Provider knows is a "in memory settings"
        writter.write("--\n");
        My_properties.forEach((key, value) -> {
            if (!FILTER_KEY.equals(key)) {
                writter.write(String.format("%s=%s\n", key.toString(), value.toString()));
            }
        });
        return writter.toString();
    }

    public static Signature getSignatureObject_toSign(KeyStore ks, String alias, String Algorithm,
            KeyStore.PasswordProtection SmartCardPin)
            throws UnrecoverableEntryException, KeyStoreException, NoSuchAlgorithmException, InvalidKeyException {
        Signature SignatureObject;
        // Key key = ks.getKey(settings.getProperty("SignatureKeyAlias"),pin);--> this
        // does the job but is not a instance
        KeyStore.PrivateKeyEntry pkey;// -->private key this key is NOT in memory
        boolean isPrivateKeyEntry = ks.entryInstanceOf(alias, KeyStore.PrivateKeyEntry.class);
        if (isPrivateKeyEntry) {
            pkey = (KeyStore.PrivateKeyEntry) ks.getEntry(alias, SmartCardPin);
            SignatureObject = Signature.getInstance(Algorithm, ks.getProvider());
            if (pkey != null) {
                SignatureObject.initSign(pkey.getPrivateKey());
            } else {
                throw new InvalidKeyException("Unable To Read the Private key");
            }
        } else {
            throw new InvalidKeyException("Unable To Read the Private key");
        }
        return SignatureObject;
    }

    public static Signature getSignatureObject_toVerify(KeyStore ks, String Algorithm, Certificate Certificate)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Signature SignatureObject;
        // Key key = ks.getKey(settings.getProperty("SignatureKeyAlias"),pin);--> this
        // does the job but is not a instance
        SignatureObject = Signature.getInstance(Algorithm, ks.getProvider());
        SignatureObject.initVerify(Certificate);
        return SignatureObject;
    }

    public static Signature getSignatureObject_toVerify(Provider SecProvider, String Algorithm, Certificate Certificate)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Signature SignatureObject;
        SignatureObject = Signature.getInstance(Algorithm, SecProvider);
        SignatureObject.initVerify(Certificate);
        return SignatureObject;
    }

    public static void unloadSmartCardPKCSProvider() {
        Provider p = Security.getProvider(PKCS_SEC_PROVIDER_LOADED);
        p.clear();
        Security.removeProvider(PKCS_SEC_PROVIDER_LOADED);
        PKCS_SEC_PROVIDER_LOADED = PKCS_SEC_PROVIDER;
    }
}
