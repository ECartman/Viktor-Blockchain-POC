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
package com.aeongames.expediente;

import com.aeongames.crypto.signature.DigitalSignatureHelper;
import com.aeongames.crypto.signature.SmartCardChecker;
import com.aeongames.edi.utils.File.properties_File;
import com.aeongames.expediente.TestDataCreator.PersonCreator;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class PersonaCreator {
    
    public PersonaCreator() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //adminSicH
     @Test
     public void PersistanceTest() {
         try {
            DigitalSignatureHelper.LoadSmartCardPKCSProvider();
        } catch (IOException ex) {
            Logger.getLogger(SmartCardChecker.class.getName()).log(Level.SEVERE, null, ex);
            //fail the test... this should not happen on our test.
            fail("an Exeption Happend.");
        }
        // Load the key store PUT YOUT PIN HERE
        char[] pin = "<><><><>".toCharArray();
        try {
            KeyStore ks = DigitalSignatureHelper.getKeystore(pin);
            properties_File settings = new properties_File("/com/aeongames/blockchain/resources/Blockchainprops.properties");
            assertTrue(ks.containsAlias(settings.getProperty("SignatureKeyAlias")));// the Signature alias MUST be there
            X509Certificate Cert = (X509Certificate) ks.getCertificate(settings.getProperty("SignatureKeyAlias"));
            assertNotNull("Certificate information is non null", Cert);
            byte tmp[]={1,1,3,3,7,0,3,1,2};
            Person tmpPerson= new Person(tmp,"Eduardo Vindas Cordoba", Sex.MALE, LocalDate.of(1987,11,29).toEpochDay(),"calle 10",Cert);
            System.out.println(tmpPerson);
              ArrayList<Person> persons = new ArrayList<>();
              persons.add(tmpPerson);
            byte tmp2[]={1,1,3,3,3,4,3,1,2};
            Person personal= new Person(tmp2,"Maria Ortega Cordoba", Sex.FEMALE, LocalDate.of(1874,11,03).toEpochDay(),"calle 4",null);
            persons.add(personal);
              PersonCreator.getandsafe(persons);
            ArrayList<Person> loadedusers = PersonCreator.LoadUsers("testuser.json");
            System.out.println();

        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException ex) {
            Logger.getLogger(SmartCardChecker.class.getName()).log(Level.SEVERE, null, ex);
            fail("exception detected. this should not be");
        } 
     
     }
}
