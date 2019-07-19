/*
 * 
 *   Copyright � 2019 Eduardo Vindas Cordoba. All rights reserved.
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

import com.aeongames.blockchain.base.BlockchainPool;
import com.aeongames.blockchain.base.PoolChangeListener;
import com.aeongames.blockchain.base.common.ByteUtils;
import com.aeongames.blockchain.base.transactions.ITransaction;
import com.aeongames.blockchain.common.PoolFileRecordTerminalListener;
import com.aeongames.edi.utils.File.properties_File;
import static com.aeongames.expediente.TestDataCreator.PersonCreator.LoadUsers;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class BlockChainExpedienteTest {

    public BlockChainExpedienteTest() {
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
    public void testJsonLoader() {
        try {
            ArrayList<Person> list = LoadUsers();
        } catch (IOException ex) {
            Logger.getLogger(BlockChainExpedienteTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException e) {
            Logger.getLogger(BlockChainExpedienteTest.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Test
    public void testExpedientesCreation() {
        BlockchainPool<FileRecord> mypool = new BlockchainPool<>();
        final ArrayList<FileRecord> transactions = new ArrayList<>();
        BlockchainPool.debugging = true;
        properties_File settings = null;
        try {
            settings = new properties_File("/com/aeongames/expediente/resources/test.properties");
        } catch (IOException ex) {
            fail();
        }
        ArrayList<Person> list = null;
        try {
            list = LoadUsers();
        } catch (IOException ex) {
            Logger.getLogger(BlockChainExpedienteTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException e) {
            Logger.getLogger(BlockChainExpedienteTest.class.getName()).log(Level.SEVERE, null, e);
        }
        Expediente<FileRecord> myExpediente= new Expediente<>(list.get(0), new FileRecord(Record.CreateGenesisforExpediente(list.get(0), list.get(1)), null, null));
        mypool.RegisterListener(new PoolFileRecordTerminalListener(myExpediente));
        staller staller = new staller(new StallerObject(), Integer.parseInt(settings.getProperty("filescount")));
        mypool.RegisterListener(staller);
        try {
            final Person Person01 = list.get(0), Person02 = list.get(1);
            Files.list(Path.of(settings.getProperty("testfilespath"))).forEach(path -> {
                try {
                    FileRecord record = new FileRecord(Person01, Person02, path,null,null);
                    myExpediente.addRecord(record);
                    transactions.add(record);
                } catch (IOException ex) {
                    Logger.getLogger(BlockChainExpedienteTest.class.getName()).log(Level.SEVERE, null, ex);
                    fail();
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(BlockChainExpedienteTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        mypool.start();
        staller.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException err) {
        }
        myExpediente.toString();
        transactions.forEach((transaction) -> {
            mypool.queueTransaction(transaction);
        });
        staller.getstallerlock().stall();
        Assert.assertTrue("the test took too long to finish", staller.isfinish());
        mypool.BringDown();
        printchain(mypool);
    }

    private void printchain(BlockchainPool<FileRecord> mypool) {
        mypool.getChain().forEach(mablock -> {
                System.out.println("*********************************************************************************");
                System.out.println(String.format("BlockHash::%s", mablock.getHash().toString()));
                System.out.println(String.format("BlockTimeIndex::%d", mablock.getIndex()));
                ByteBuffer[] signatures = mablock.getBlockAuthoritativeSignature();
                for (ByteBuffer signature : signatures) {
                    System.out.println(String.format("\tBlockAcceptaceSignature::%s", ByteUtils.ByteArrayToString(signature)));
                    signature.rewind();
                }
                System.out.println(String.format("BlockPrevious::%s", mablock.getPreviousHash().toString()));
                ZonedDateTime time = Instant.ofEpochMilli(mablock.getTimeStamp()).atZone(ZoneId.of("America/Costa_Rica"));//.atZone(ZoneId.of("GMT-06:00"));
                System.out.println(String.format("BlockCreationTime::%s", time));
                System.out.println("*********************************************************************************");
                mablock.getBlockTransactions().forEach(trasc -> {
                 System.out.println(String.format("\tBlockTransaction::%s", trasc.toString()));
                });
                System.out.println("*********************************************************************************");
            });
    }
}

class staller extends Thread implements PoolChangeListener<FileRecord> {

    private final Duration waititme = Duration.of(100, ChronoUnit.SECONDS);
    private final int ammountawaited;
    private int remaiding;
    private final StallerObject stallerObject;

    public staller(StallerObject locker, int count) {
        ammountawaited = count;
        remaiding = ammountawaited;
        stallerObject = locker;
    }

    @Override
    public void run() {
        Instant before = Instant.now();
        try {
            staller.sleep(waititme.toMillis());
        } catch (InterruptedException ex) {
        }
        Instant after = Instant.now();
        System.out.println(Duration.between(before, after).toSeconds());
        stallerObject.unstall();
    }

    public boolean isfinish() {
        return remaiding == 0;
    }

    public StallerObject getstallerlock() {
        return stallerObject;
    }

    @Override
    public synchronized void NotifyAccepted(BlockchainPool<FileRecord> CallerPool, List<FileRecord> CommitedTransactions) {
        remaiding -= CommitedTransactions.size();
        System.out.println(String.format("awaiting for %d of %d", remaiding, ammountawaited));
        if (remaiding == 0) {
            stallerObject.unstall();
        }
    }

    @Override
    public synchronized void NotifyDoubleSpent(BlockchainPool<FileRecord> CallerPool, List<? extends ITransaction> DoubleSpentTransactions) {
        remaiding -= DoubleSpentTransactions.size();
        System.out.println(String.format("awaiting for %d of %d", remaiding, ammountawaited));
        if (remaiding == 0) {
            stallerObject.unstall();
        }
    }

    @Override
    public synchronized void NotifyNotAccepted(BlockchainPool<FileRecord> CallerPool, List<FileRecord> AttemptedTransactions) {
        remaiding -= AttemptedTransactions.size();
        System.out.println(String.format("awaiting for %d of %d", remaiding, ammountawaited));
        if (remaiding == 0) {
            stallerObject.unstall();
        }
    }

    @Override
    public void NotifyInvalidSignature(BlockchainPool<FileRecord> CallerPool, List<FileRecord> AttemptedTransactions) {
    }

    @Override
    public void Tick(BlockchainPool<FileRecord> CallerPool) {
    }
}

class StallerObject {

    private boolean stall = true;

    public synchronized void stall() {
        // Wait until message is
        // available.
        while (stall) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        stall = true;
        notifyAll();
    }

    public synchronized boolean unstall() {
        if (!stall) {
            return false;
        }
        // Toggle status.
        stall = false;
        notifyAll();
        return true;
    }
}
