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
package com.aeongames.blockchain.test;

import com.aeongames.blockchain.base.BlockchainPool;
import com.aeongames.blockchain.base.PoolChangeListener;
import com.aeongames.blockchain.base.common.ByteUtils;
import com.aeongames.blockchain.base.transactions.ITransaction;
import com.aeongames.blockchain.common.PoolTerminalListener;
import com.aeongames.blockchain.common.RandomTransactionCreator;
import com.aeongames.blockchain.common.TestTransaction;
import com.aeongames.logger.LoggingHelper;
import java.nio.ByteBuffer;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.junit.*;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class BlockChainPoolTest {

    RandomTransactionCreator creator;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        System.out.println("Setting Up the test ");
        creator = new RandomTransactionCreator();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void Block_Pool_Test() {
        System.out.println("Starting the Pool Test");
        ArrayList<TestTransaction> transactions = creator.createrandomlist();
        staller staller = new staller(new StallerObject(), transactions.size());
        try {
            //so the TEST is here let see how bad we did. 
            BlockchainPool<TestTransaction> mypool = new BlockchainPool<>();
            BlockchainPool.debugging = true;
            mypool.RegisterListener(new PoolTerminalListener());
            mypool.RegisterListener(staller);
            mypool.start();
            staller.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException err) {
            }
            transactions.forEach((transaction) -> {
                mypool.queueTransaction(transaction);
            });
            staller.getstallerlock().stall();
            Assert.assertTrue("the test took too long to finish", staller.isfinish());
            mypool.BringDown();
            System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\///////////////////////////////////////////");
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
        } catch (Throwable errorfound) {
            LoggingHelper.getAClassLogger("BlockChainPoolTest").log(Level.SEVERE, "Error Running Pool Test", errorfound);
        }
        System.out.println("Finishing the Pool Test");
    }

    private class staller extends Thread implements PoolChangeListener<TestTransaction> {

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
        public synchronized void NotifyAccepted(BlockchainPool<TestTransaction> CallerPool, List<TestTransaction> CommitedTransactions) {
            remaiding -= CommitedTransactions.size();
            System.out.println(String.format("awaiting for %d of %d", remaiding, ammountawaited));
            if (remaiding == 0) {
                stallerObject.unstall();
            }
        }

        @Override
        public synchronized void NotifyDoubleSpent(BlockchainPool<TestTransaction> CallerPool, List<? extends ITransaction> DoubleSpentTransactions) {
            remaiding -= DoubleSpentTransactions.size();
            System.out.println(String.format("awaiting for %d of %d", remaiding, ammountawaited));
            if (remaiding == 0) {
                stallerObject.unstall();
            }
        }

        @Override
        public synchronized void NotifyNotAccepted(BlockchainPool<TestTransaction> CallerPool, List<TestTransaction> AttemptedTransactions) {
            remaiding -= AttemptedTransactions.size();
            System.out.println(String.format("awaiting for %d of %d", remaiding, ammountawaited));
            if (remaiding == 0) {
                stallerObject.unstall();
            }
        }

        @Override
        public void NotifyInvalidSignature(BlockchainPool<TestTransaction> CallerPool, List<TestTransaction> AttemptedTransactions) {
        }

        @Override
        public void Tick(BlockchainPool<TestTransaction> CallerPool) {
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

}
