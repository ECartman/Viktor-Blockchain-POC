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

import com.aeongames.edi.utils.File.properties_File;
import static com.aeongames.logger.LoggingHelper.getClassLoggerForMe;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

public final class SignatureProvider {

    private final KeyStore UnderlineStore;
    /**
     * remove this on a production implementation and use a listener or a
     * "safer" method. of course and unless you use a secure store and server
     * (ie. a smartcard or HSM provider yet i still thinking about removing this
     * value as a hold...
     *
     * transient should be... redundant and un required as this class is not to
     * be serialized. but it serves 2 reasons a documentation reason (lets
     * others know is not to be recorded on any way. and tells other code (if it
     * were to do some reflections or for example GSON ) to skip this value. and
     * leave it alone so in the end it is indeed useful refer
     * https://docs.microsoft.com/en-us/windows/win32/api/dpapi/nf-dpapi-cryptprotectmemory
     * oh ye this is java...oh no ORACLE IS killing me !!!!! AAAHHHHH
     *
     */
    private transient final char accesskey[];
    /**
     * this will not be implemented now however for a future reference i think
     * it will be better to use a callback or better known in the java circle as
     * "events" or plain ans simple interfaces to begin called back and react to
     * a event on this case the thing is if we don't have the access key, and
     * the provider dont want is to record we will be required to nag each time
     * we need to use the key and access the store. this is safe but...
     * problematic for instance if this is intended to be a SERVER application
     * we will desire to better store the key on a smartCard and use a static
     * key on a some place safe on the server either using safe locations of
     * memory or something safe.
     */
    //TODO: USE/IMPLEMENT
    private /*callback*/ ArrayList<EventListener> RegisteredListeners = null;//-->unused for the time begin. but leaving it for future use.

    private final String CertificateAlias;
    public final static String DEFAULT_ALGORITHM;

    static {
        String loadedmethod;
        try {
            properties_File settings = new properties_File("/com/aeongames/blockchain/resources/Blockchainprops.properties");
            loadedmethod = settings.getProperty("signmethod").trim();
        } catch (IOException ex) {
            loadedmethod = "SHA256withRSA";
        }
        DEFAULT_ALGORITHM = loadedmethod;
    }
    private final String algorithm = DEFAULT_ALGORITHM;
    private final String KeyAlias;

    private SignatureProvider() {
        throw new IllegalCallerException();
    }

    public SignatureProvider(KeyStore CryptoStore, Optional<char[]> Password, String KeyAlias, String CertificateAlias) throws KeyStoreException {
        this.UnderlineStore = Objects.requireNonNull(CryptoStore, "The Crypto Store is null, this is not allowed");
        if (UnderlineStore.size() <= 0) {
            throw new KeyStoreException("the Store does not have enough data");
        }
        char[] tmp = (Password == null ? null : Password.orElse(null));
        if (tmp != null) {
            accesskey = Arrays.copyOf(tmp,tmp.length);
        } else {
            accesskey = null;
        }
        this.CertificateAlias = CertificateAlias;
        this.KeyAlias = KeyAlias;
    }

    public boolean supportSignature() {
        boolean result = false;
        for (Object sign : UnderlineStore.getProvider().keySet().toArray()) {
            if (sign.toString().startsWith("Signature.")) {
                if (sign.toString().toUpperCase().contains(DEFAULT_ALGORITHM.toUpperCase())) {
                    result = true;
                }
            }
        }
        return result;
    }

    public synchronized byte[] signData(byte DataToSign[]) throws SignatureException {
        Objects.requireNonNull(DataToSign, "the data cannot be null");
        byte[] signature = null;
        try {
            Signature RSASHA = DigitalSignatureHelper.getSignatureObject_toSign(UnderlineStore, KeyAlias, algorithm, new KeyStore.PasswordProtection(accesskey));
            RSASHA.update(DataToSign);
            signature = (RSASHA.sign());
        } catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableEntryException | InvalidKeyException | SignatureException e) {
            getClassLoggerForMe().log(Level.SEVERE, null, e);
            throw new SignatureException("Error Signing the data", e);
        }
        return signature;
    }

    public synchronized byte[] getCertSignature() {
        try {
            Certificate Certificate = UnderlineStore.getCertificate(CertificateAlias);
            if (Certificate instanceof X509Certificate) {
                  return  ((X509Certificate)Certificate).getSignature();
            }
        } catch (KeyStoreException err) {
            getClassLoggerForMe().log(Level.SEVERE, null, err);
        }
        return null;
    }

    public static Signature VerifyData(Provider SecProvider, Optional<String> Algorithm, Certificate Certificate)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Signature SignatureObject;
        SignatureObject = Signature.getInstance(Objects.requireNonNullElse(Algorithm, Optional.of(DEFAULT_ALGORITHM)).orElse(DEFAULT_ALGORITHM), SecProvider);
        SignatureObject.initVerify(Certificate);
        return SignatureObject;
    }

}
