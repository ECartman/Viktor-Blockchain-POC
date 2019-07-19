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

import java.io.IOException;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.aeongames.crypto.signature.DigitalSignatureHelper;
import com.aeongames.crypto.signature.SmartCardChecker;
import com.aeongames.edi.utils.File.properties_File;
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
public class expedienteTest {
    
    public expedienteTest() {
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
    
    @Test
    public void PersonTest(){
        try {
            DigitalSignatureHelper.LoadSmartCardPKCSProvider();
        } catch (IOException ex) {
            Logger.getLogger(SmartCardChecker.class.getName()).log(Level.SEVERE, null, ex);
            //fail the test... this should not happen on our test.
            fail("an Exeption Happend.");
        }
        // Load the key store TODO: EDIT IF TESTING! 
        char[] pin = "<>".toCharArray();
        try {
            KeyStore ks = DigitalSignatureHelper.getKeystore(pin);
            properties_File settings = new properties_File("/com/aeongames/blockchain/resources/Blockchainprops.properties");
            assertTrue(ks.containsAlias(settings.getProperty("SignatureKeyAlias")));// the Signature alias MUST be there
            X509Certificate Cert = (X509Certificate) ks.getCertificate(settings.getProperty("SignatureKeyAlias"));
            assertNotNull("Certificate information is non null", Cert);
            byte tmp[]={1,1,3,3,7,0,3,1,2};
            Person tmpPerson= new Person(tmp,"Eduardo Vindas Cordoba", Sex.MALE, LocalDate.of(1987,11,29).toEpochDay(),"calle 10",Cert);
            System.out.println(tmpPerson);

        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException ex) {
            Logger.getLogger(SmartCardChecker.class.getName()).log(Level.SEVERE, null, ex);
            fail("exception detected. this should not be");
        }

    }

    @Test
    public void ExpedienteCreationTest(){
          byte tmp[]={1,1,3,3,7,0,3,1,2};
      Person tmpPerson= new Person(tmp,"Eduardo Vindas Cordoba", Sex.MALE, LocalDate.of(1987,11,29).toEpochDay(),"calle 10",null);
      System.out.println(tmpPerson);
      //this should fail:
        try {
            new Expediente<>(tmpPerson, new FileRecord(Record.CreateGenesisforExpediente(tmpPerson, tmpPerson), null, null));
            fail("the creation of a Expediente did not fail when the requester is the same  as the owner");
        }catch(IllegalArgumentException err){
        }
        byte tmp2[]={1,1,3,3,3,4,3,1,2};
        Person personal= new Person(tmp2,"Maria Ortega Cordoba", Sex.FEMALE, LocalDate.of(1874,11,03).toEpochDay(),"calle 4",null);
        Expediente<FileRecord> myExpediente= new Expediente<>(tmpPerson, new FileRecord(Record.CreateGenesisforExpediente(tmpPerson, personal), null, null));
        assertNotNull(myExpediente);
        //this should be true as we have not commited the changes to a chain... hell this is not even in the chain yet.
        assertTrue(myExpediente.hasuncommitedRedords());
        try {
            myExpediente.addRecord(new FileRecord(tmpPerson, personal, Paths.get("build.gradle"),null,null));
        } catch (IOException e) {
            //for this test this should not fail. but we digress. it is posible.
            //
            fail(e.getMessage());
        }
        myExpediente.toString();
    }
}
