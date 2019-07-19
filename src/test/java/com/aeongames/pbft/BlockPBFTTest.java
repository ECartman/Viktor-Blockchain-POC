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
package com.aeongames.pbft;

import com.aeongames.blockchain.base.Block;
import com.aeongames.blockchain.base.common.Hash;
import com.aeongames.blockchain.common.RandomTransactionCreator;
import com.aeongames.blockchain.common.TestTransaction;
import com.aeongames.blockchain.consensus.pbft.pbftServerCommunication;
import com.aeongames.crypto.signature.SignatureProvider;
import com.aeongames.blockchain.consensus.pbft.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.*;
import redis.clients.jedis.JedisPool;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class BlockPBFTTest {
    RandomTransactionCreator creator;
    private Block TestGenesisBlock;
    private final SignatureProvider server[]=new SignatureProvider[3];
    
    public BlockPBFTTest() {
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
        System.out.println("LOADED");
        System.out.println("Setting Up the test ");
        creator = new RandomTransactionCreator();
        ArrayList<TestTransaction> result = creator.createrandomlist();
        Hash[] v={Hash.of(creator.createTestOBjectwithRandomhash().getTransactionByteBuffer())};
        TestGenesisBlock= new Block( Hash.of(creator.createTestOBjectwithRandomhash().getTransactionByteBuffer()), result,v);
        TestGenesisBlock.setTimeStap(Optional.empty());
        try {
            //only for test on production there is a need for a more refined code to check null & the sort...
            KeyStore ks =KeyStore.getInstance("PKCS12",BouncyCastleProvider.PROVIDER_NAME);
            InputStream is=this.getClass().getResourceAsStream("/com/aeongames/blockchain/resources/server01.p12");
            Assert.assertNotNull(is);
            ks.load(is, "server01".toCharArray());
            is.close();
            server[0] =new SignatureProvider(ks,Optional.of("server01".toCharArray()),"server01","server01");
            ks = KeyStore.getInstance("PKCS12",BouncyCastleProvider.PROVIDER_NAME);
            is=this.getClass().getResourceAsStream("/com/aeongames/blockchain/resources/server02.p12");
            Assert.assertNotNull(is);
            ks.load( is, "server02".toCharArray());
            server[1] =new SignatureProvider(ks,Optional.of("server02".toCharArray()),"server02","server02");
            ks = KeyStore.getInstance("PKCS12",BouncyCastleProvider.PROVIDER_NAME);
            is=this.getClass().getResourceAsStream("/com/aeongames/blockchain/resources/server03.p12");
            Assert.assertNotNull(is);
            ks.load(is, "server03".toCharArray());
            server[2] =new SignatureProvider(ks,Optional.of("server03".toCharArray()),"server03","server03");
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | NoSuchProviderException ex) {
            Logger.getLogger(BlockPBFTTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }

    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     public void TestPBFT() {
     //test the notification of a genesis block to 3 nodes.
     System.out.println("creating client");
     pbftClient client=new pbftClient();
     System.out.println("creating servers");
     setupReplicas(client.getPool(),server);
     System.out.println("setting and sending a block");
     client.pbftClient_execution(TestGenesisBlock);
     Assert.assertTrue("the test is not completed",client.isCompleted());
                 synchronized (System.out) {
                System.out.println("---------------------------------------------------------");
                System.out.println("---------------------------------------------------------");
                System.out.println(TestGenesisBlock.getHash().toString() + " = " + client.getResult().RelatedBlock());
                System.out.println("---------------------------------------------------------");
                System.out.println("---------------------------------------------------------");
            }
     //test another 
     client.pbftClient_execution(TestGenesisBlock);
     Assert.assertTrue("the test is not completed",client.isCompleted());
     synchronized (System.out) {
        System.out.println("---------------------------------------------------------");
        System.out.println("---------------------------------------------------------");
        System.out.println(TestGenesisBlock.getHash().toString() + " = " + client.getResult().RelatedBlock());
        System.out.println("---------------------------------------------------------");
        System.out.println("---------------------------------------------------------");
    }
     //done
     }
     
     
     // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     public void TestPBFT2() {
     //test the notification of a genesis block to 3 nodes.
     System.out.println("creating client");
     pbftClient client=new pbftClient();
     System.out.println("creating servers");
     setupReplicas(client.getPool(),server);
     System.out.println("setting and sending a block");
         ArrayList<TestTransaction> result =creator.createrandomlist();
         Hash[] v={Hash.of(creator.createTestOBjectwithRandomhash().getTransactionByteBuffer())};
         TestGenesisBlock= new Block( Hash.of(creator.createTestOBjectwithRandomhash().getTransactionByteBuffer()), result,v);
         TestGenesisBlock.setTimeStap(Optional.of(Instant.MAX));
     client.pbftClient_execution(TestGenesisBlock);
     Assert.assertTrue("the test is not completed",client.isCompleted());
    //done
     }


    private static void setupReplicas(JedisPool pool, SignatureProvider[] providers) {  
        pbftServerCommunication communicationSimulation= new pbftServerCommunication(pool,providers[0]);
        communicationSimulation.StartThreads();
        System.out.println(communicationSimulation.isExpent()?"is Expent":"not Expent");
    }

}
