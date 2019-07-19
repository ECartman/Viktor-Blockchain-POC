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
package com.aeongames.blockchain.base;

import com.aeongames.blockchain.TODOS.NotImplementedYet;
import com.aeongames.blockchain.TODOS.SubjectToChange;
import com.aeongames.blockchain.base.common.Hash;
import com.aeongames.blockchain.base.transactions.ITransaction;
import com.aeongames.blockchain.consensus.pbft.pbftClient;
import com.aeongames.blockchain.consensus.pbft.pbftServerCommunication;
import com.aeongames.crypto.signature.SignatureProvider;
import com.aeongames.logger.LoggingHelper;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import redis.clients.jedis.JedisPool;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 * @param <T> the specific type of Transaction that this pool will handle.
 */
public class BlockchainPool<T extends ITransaction> extends Thread {

    public static boolean debugging = true, verbose = false;
    /**
     * if we have been ask to close.
     */
    private volatile boolean halt = false;

    /**
     * the sleep timer for this thread
     */
    private final long sleeptime = 700;
    /**
     * the pool of transactions to add into the blockchain
     */
    private final ConcurrentLinkedDeque<T> TransactionsPool;
    /**
     * a map of "tainted" transactions (possibly illegal ones); however we will
     * attempt to add them later but after the amount of attempts
     * <taint_tolerance> we will remove them from the queue and tell whomever
     * want to know that we CANNOT add those transactions
     */
    private final HashMap<T, Integer> Tainted;
    /**
     * the tolerance to taint values
     */
    private final int taint_tolerance = 3;//3 strikes chanse to add them.
    /**
     * notificator thread
     */
    private final ReentrantLock NotificatorLock;
    /**
     * TransactionsPool Lock
     */
    private final ReentrantLock PoolLock;
    /**
     * chain is not final (immutable inside this class we need to handle with
     * care so we need to be caution to Not provide ANYONE a reference to Chain.
     * Chain must be ONLY handled on this class any exposure of this object
     * leaks information and might be considered a vulnerability. therefore make
     * sure to only expose copy or clone references to the Chain.
     *
     * read only references? might suffice. but be cautions with Shallow
     * Copy's... check remarks on the methods @BlockChain class.
     */
    private BlockChain<T> Chain = null;
    /**
     * the object we use to connect or notify changes to the consensus mechanism
     */
    private final pbftClient conectionClient;
    /**
     * TODO: move outside? & replace the simulation object of the consensus
     * server
     */
    private pbftServerCommunication communicationSimulation;
    /**
     * tell us if we should notify Async or Sync.
     */
    private boolean direct = true;
    /**
     * authority Certificate.
     */
    private final HashMap<Hash, X509Certificate> Authoritys;
    /**
     * a list of listeners that will await for changes on this pool
     */
    private final ArrayList<PoolChangeListener<T>> PoolListeners;
    /**
     * paralel notificator.
     */
    private BlockCPoolNotifierThread PoolNotificatorThread;

    public BlockchainPool(boolean ParalelNotification) {
        this();
        direct = ParalelNotification;
        PoolNotificatorThread = new BlockCPoolNotifierThread(this, NotificatorLock);
    }

    public BlockchainPool() {
        super("BlockChainPoolThread");
        //we want this thread to be IMPORTANT and ensure if all other thread 
        //die the JVM do not die. 
        this.setDaemon(false);
        TransactionsPool = new ConcurrentLinkedDeque<>();
        Authoritys = new HashMap<>();
        Tainted = new HashMap<>();
        PoolListeners = new ArrayList<>();
        conectionClient = new pbftClient();
        X509Certificate certificate = initServerCertificate(conectionClient);
        //a hash of a hash... sure why not... 
        Authoritys.put(Hash.of(certificate.getSignature()), certificate);
        NotificatorLock = new ReentrantLock(true);
        PoolLock = new ReentrantLock(true);
    }

    /**
     * this might need to be moved outside.
     */
    private X509Certificate initServerCertificate(pbftClient conectionClient) {
        X509Certificate public_cert = null;
        //only for test on production there is a need for a more refined code to check null & the sort...
        try {
            /********************************************/
            Provider Provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
            //this should not be required. however if it happends lets do the work the caller should had done
            if (Provider == null) {
                //a exception might happend here... extremely unlikely  tho but may happend.
                //a class exception that BouncyCastle was not loaded. if it happends well game over man.
                Provider = new BouncyCastleProvider();
                Security.addProvider(Provider);
            }
            /********************************************/
            KeyStore ks = KeyStore.getInstance("PKCS12", Provider);
            try (InputStream is = this.getClass().getResourceAsStream("/com/aeongames/blockchain/resources/server01.p12")) {
                ks.load(is, "server01".toCharArray());
            }
            public_cert = (X509Certificate) ks.getCertificate("server01");
            SignatureProvider provider = new SignatureProvider(ks, Optional.of("server01".toCharArray()), "server01", "server01");
            initserverEmulator(conectionClient.getPool(), provider);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException ex) {
            LoggingHelper.getDefaultLogger().log(Level.SEVERE, null, ex);
        }
        return public_cert;
    }

    private void initserverEmulator(JedisPool pool, SignatureProvider serverProvider) {
        communicationSimulation = new pbftServerCommunication(pool, serverProvider);
        communicationSimulation.StartThreads();
    }

    public boolean queueTransaction(T transaction) {
        boolean result=false;
        PoolLock.lock();
        try {
            if(!TransactionsPool.contains(Objects.requireNonNull(transaction, "The Transaction cannot be null")))
             result= TransactionsPool.add(transaction);
        } finally {
            PoolLock.unlock();
        }
        return result;
    }

    /**
     * gather a copy Shallow of the block chain. objects should be immutable
     * enough to be safe to use... otherwise let me know.... hehe
     *
     * @return a list
     */
    public List<Block> getChain() {
        return Chain.getBlocklistCopy();
    }

    @Override
    public void run() {
        while (!halt || !TransactionsPool.isEmpty()) {
            processqueue();
            //TODO: remove debugging code
            /**
             * ***************************************************
             */
            if (debugging) {
//                System.out.println(String.format("contains values on the queue?: %s", !TransactionsPool.isEmpty() ? "yes" : "no"));
//                if (!TransactionsPool.isEmpty() && verbose) {
//                    TransactionsPool.forEach((t) -> {
//                        System.out.println(t.toString());
//                    });
//                }
//                System.out.println(String.format("contains Tainted values?: %s", !Tainted.isEmpty() ? "yes" : "no"));
                if(!Tainted.isEmpty())
                 LoggingHelper.getDefaultLogger().log(Level.SEVERE, "There Are Tainted Values!!!!");
            }
            /**
             * ***************************************************
             */
            if (TransactionsPool.isEmpty() || TransactionsPool.size() < BlockChain.DEFAULT_TRANSACTIONS_PER_BLOCK) {
                try {
                    Thread.sleep(sleeptime);
                } catch (InterruptedException ex) {
                    LoggingHelper.getDefaultLogger().log(Level.SEVERE, "unable to sleep", ex);
                }
            }
        }
    }

    /**
     * this will be run to process the blocks. this is intended to run on a
     * thread.
     */
    private synchronized void processqueue() {
        if (TransactionsPool.size() >= BlockChain.DEFAULT_TRANSACTIONS_PER_BLOCK) {
            //notes: we could take several aproaches here. we could take the transactions as many and add then to a single 
            //block. but size might be inconsistent. 
            //another aproach and to avoid problems we cound add only powers of 2  (see merkle vulnerability) 
            // another aproach is to use the limit we set arbitrary at BlockChain.DEFAULT_TRANSACTIONS_PER_BLOCK/
            //lets takes the later. 
            if (Chain == null) {
                LoggingHelper.getDefaultLogger().log(
                        Level.FINE, String
                                .format("initializing the blockchain Object expected transactions to add %d, and how many avaiable on the queue: %d",
                                        BlockChain.DEFAULT_TRANSACTIONS_PER_BLOCK,
                                        TransactionsPool.size()));
                Chain = new BlockChain<>(conectionClient);
            }
            ArrayList<T> transactionsToCommit = new ArrayList<>(BlockChain.DEFAULT_TRANSACTIONS_PER_BLOCK);
            PoolLock.lock();
            try {
                for (int i = 0; i < BlockChain.DEFAULT_TRANSACTIONS_PER_BLOCK; i++) {
                    transactionsToCommit.add(TransactionsPool.poll());
                }
            } finally {
                PoolLock.unlock();
            }
            BlockchainResponse applied = null;
            try {
                //IndexOutOfBoundsException should never happend if YOU changed the DEFAULT_TRANSACTIONS_PER_BLOCK MAY happend but look into the code.to be sure
                //TODO: consider to do a copy of the list here rather than on the chainitself
                applied = Chain.PropouseBlock(Collections.unmodifiableList(transactionsToCommit), Collections.unmodifiableMap(Authoritys));
            } catch (InterruptedException ex) {
                LoggingHelper.getDefaultLogger().log(Level.SEVERE, "unable to wait for the Transaction to be commited! ", ex);
            }
            //if applied is null then there is a error and therefore we dont even need to handle the tainted.
            if (applied != null) {
                switch (applied.getResolution()) {
                    case REJECTED_NOTACCEPTED:
                        //they were not accepted why? 
                        //something is wrong on the transaction that they know and we might not
                        //lets tag as tainted transactions
                        //maybe we need a better method as this taint the hold block worth
                        transactionsToCommit.forEach((t) -> {
                            Integer count = Tainted.putIfAbsent(t, 1);
                            if (count != null) {
                                Tainted.put(t, count++);
                            }
                        });
                        NotifyNotAccepted(Collections.unmodifiableList(transactionsToCommit));
                        break;
                    case REJECTED_INVALIDSIGN:
                        NotifyInvalidSignature(Collections.unmodifiableList(transactionsToCommit));
                        break;
                    case DOUBLESPENT:
                        //some transactions are duplicated AND CANNOT be added again. 
                        //remove the NON doublespent transactions
                        transactionsToCommit.removeAll(applied.getRelatedTransactions());
                        //notify invalid transactions were queued or were alredy added 
                        NotifyDoubleSpent(applied.getRelatedTransactions());
                        break;
                    case ACCEPTED:
                        //clear the list if transactions to commit as we are done
                        NotifyAccepted(Collections.unmodifiableList(transactionsToCommit));
                        transactionsToCommit.clear();
                        //we are done nothing to do. maybe send a notification
                        break;
                    default:
                        throw new UnsupportedOperationException(String.format("Unexpected Value: %s", applied.getResolution().name()));
                }
            }
            PoolLock.lock();
            try {
                //add whatever is left to be added;
                TransactionsPool.addAll(transactionsToCommit);
            } finally {
                PoolLock.unlock();
            }
        } else {
            //there are not enought transactions yet. 
            Tick();
            LoggingHelper.getDefaultLogger().log(
                    Level.FINE, String
                            .format("not enought transactions for a block need at least %d there are %d",
                                    BlockChain.DEFAULT_TRANSACTIONS_PER_BLOCK,
                                    TransactionsPool.size()));
        }

    }

    public synchronized boolean hasTainted() {
        return !Tainted.isEmpty();
    }

    @NotImplementedYet
    @SubjectToChange
    @SuppressWarnings("unchecked")
    public synchronized Map<T, Integer> getTainted() {
        Map<T, Integer> copyoftainted = null;
        if (!Tainted.isEmpty()) {
            //shallow copy. 
            copyoftainted = Collections.unmodifiableMap((Map<T, Integer>) Tainted.clone());
        }
        return copyoftainted;
    }

    public synchronized void ClearTainted() {
        if (!Tainted.isEmpty()) {
            Tainted.clear();
        }
    }

    @SubjectToChange
    public synchronized void BringDown() {
        //this kills the chain.
        if (PoolNotificatorThread != null) {
            PoolNotificatorThread.halt();
        }
        communicationSimulation.Halt();
        //TODO: remove listeners
        //TODO: Persist the chain and clear the blockchain
        //TODO: a lot more
        this.halt = true;
    }

    private void NotifyNotAccepted(List<T> unmodifiableList) {
        if (direct) {
            try {
                NotificatorLock.lock();
                PoolListeners.forEach((PoolListener) -> {
                    try {
                        PoolListener.NotifyNotAccepted(this, unmodifiableList);
                    } catch (Exception err) {
                        //the idiot caused a error. idiot! 
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                "You Idiot did not handle a Exception you have doom us all",
                                err);
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                String.format("Listener that Caused The issue: %s",
                                        PoolListener.getClass().getName()));
                    }
                });
            } finally {
                NotificatorLock.unlock();
            }
        } else {
            PoolNotificatorThread.addEventToqueue(new ModificationEvent(BlockchainResponse.Response.ACCEPTED, unmodifiableList));
        }
    }

    private void NotifyInvalidSignature(List<T> unmodifiableList) {
        if (direct) {
            try {
                NotificatorLock.lock();
                PoolListeners.forEach((PoolListener) -> {
                    try {
                        PoolListener.NotifyInvalidSignature(this, unmodifiableList);
                    } catch (Exception err) {
                        //the idiot caused a error. idiot! 
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                "You Idiot did not handle a Exception you have doom us all",
                                err);
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                String.format("Listener that Caused The issue: %s",
                                        PoolListener.getClass().getName()));
                    }
                });
            } finally {
                NotificatorLock.unlock();
            }
        } else {
            PoolNotificatorThread.addEventToqueue(new ModificationEvent(BlockchainResponse.Response.REJECTED_INVALIDSIGN, unmodifiableList));
        }
    }

    private void NotifyDoubleSpent(List<? extends ITransaction> relatedTransactions) {
        if (direct) {
            try {
                NotificatorLock.lock();
                PoolListeners.forEach((PoolListener) -> {
                    try {
                        PoolListener.NotifyDoubleSpent(this, relatedTransactions);
                    } catch (Exception err) {
                        //the idiot caused a error. idiot! 
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                "You Idiot did not handle a Exception you have doom us all",
                                err);
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                String.format("Listener that Caused The issue: %s",
                                        PoolListener.getClass().getName()));
                    }
                });
            } finally {
                NotificatorLock.unlock();
            }
        } else {
            PoolNotificatorThread.addEventToqueue(new ModificationEvent(BlockchainResponse.Response.DOUBLESPENT, relatedTransactions));
        }
    }

    private void NotifyAccepted(List<T> unmodifiableList) {
        if (direct) {
            try {
                NotificatorLock.lock();
                PoolListeners.forEach((PoolListener) -> {
                    try {
                        PoolListener.NotifyAccepted(this, unmodifiableList);
                    } catch (Exception err) {
                        //the idiot caused a error. idiot! 
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                "You Idiot did not handle a Exception you have doom us all",
                                err);
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                String.format("Listener that Caused The issue: %s",
                                        PoolListener.getClass().getName()));
                    }
                });
            } finally {
                NotificatorLock.unlock();
            }
        } else {
            PoolNotificatorThread.addEventToqueue(new ModificationEvent(BlockchainResponse.Response.ACCEPTED, unmodifiableList));
        }
    }

    private void Tick() {
        if (direct) {
            try {
                NotificatorLock.lock();
                PoolListeners.forEach((PoolListener) -> {
                    try {
                        PoolListener.Tick(this);
                    } catch (Exception err) {
                        //the idiot caused a error. idiot! 
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                "You Idiot did not handle a Exception you have doom us all",
                                err);
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                String.format("Listener that Caused The issue: %s",
                                        PoolListener.getClass().getName()));
                    }
                });
            } finally {
                NotificatorLock.unlock();
            }
        } else {
            PoolNotificatorThread.addEventToqueue(new ModificationEvent());
        }
    }

    /**
     * register a new listener for this pool
     *
     * @param newListener
     * @return true if able to add false otherwise
     */
    public boolean RegisterListener(PoolChangeListener<T> newListener) {
        boolean responce = false;
        if (newListener != null) {
            NotificatorLock.lock();
            try {
                responce = this.PoolListeners.add(newListener);
            } finally {
                NotificatorLock.unlock();
            }
        }
        return responce;
    }

    /**
     * remove the provided listener
     *
     * @param removelistener
     * @return removes the selected listener.
     */
    public boolean UnReggisterListener(PoolChangeListener<T> removelistener) {
        boolean responce = false;
        if (removelistener != null) {
            NotificatorLock.lock();
            try {
                responce = this.PoolListeners.remove(removelistener);
            } finally {
                NotificatorLock.unlock();
            }
        }
        return responce;
    }

    private class ModificationEvent {

        private final BlockchainResponse.Response responcekind;
        private final List<T> Transactions;
        private final List<? extends ITransaction> DoubleSpentTransactions;

        @SuppressWarnings("unchecked")
        private ModificationEvent(BlockchainResponse.Response resp, List<? extends ITransaction> Transactions) {
            this.responcekind = resp;
            if (resp != BlockchainResponse.Response.DOUBLESPENT) {
                this.Transactions = (List<T>) Transactions;
                this.DoubleSpentTransactions = null;
            } else {
                this.DoubleSpentTransactions = Transactions;
                this.Transactions = null;
            }
        }

        private ModificationEvent() {
            this.responcekind = null;
            this.Transactions = null;
            this.DoubleSpentTransactions = null;
        }

    }

    private class BlockCPoolNotifierThread extends Thread {

        private final long sleeptime = 600;
        private final BlockchainPool<T> poolref;
        private final ConcurrentLinkedDeque<ModificationEvent> Eventqueue;
        private volatile boolean Halted = false;
        private final ReentrantLock NotificatorLock;
        private final ReentrantLock ThreadQLock;

        BlockCPoolNotifierThread(BlockchainPool<T> ref, ReentrantLock NotifierLock) {
            super("PoolNotifierThread");
            poolref = ref;
            NotificatorLock = NotifierLock;
            ThreadQLock = new ReentrantLock(true);
            Eventqueue = new ConcurrentLinkedDeque<>();
        }

        boolean addEventToqueue(ModificationEvent event) {
            boolean added = false;
            ThreadQLock.lock();
            try {
                added = Eventqueue.add(event);
            } finally {
                ThreadQLock.unlock();
            }
            return added;
        }

        @Override
        public void run() {
            while (!Halted || !Eventqueue.isEmpty()) {
                ModificationEvent value;
                ThreadQLock.lock();
                try {
                    value = Eventqueue.poll();
                } finally {
                    ThreadQLock.unlock();
                }
                if (value != null) {
                    switch (value.responcekind) {
                        case ACCEPTED:
                            processAccepted(value.Transactions);
                            break;
                        case DOUBLESPENT:
                            processDoublespend(value.DoubleSpentTransactions);
                            break;
                        case REJECTED_INVALIDSIGN:
                            processRejectINV(value.Transactions);
                            break;
                        case REJECTED_NOTACCEPTED:
                            processRejectNOTACCEPTED(value.Transactions);
                            break;
                        default:
                            tick();
                            break;
                    }
                }
                if (Eventqueue.isEmpty()) {
                    try {
                        Thread.sleep(sleeptime);
                    } catch (InterruptedException ex) {
                        LoggingHelper.getDefaultLogger().log(Level.SEVERE, "unable to sleep", ex);
                    }
                }
            }
        }

        private void processAccepted(List<T> Transactions) {
            NotificatorLock.lock();
            try {
                PoolListeners.forEach((PoolListener) -> {
                    try {
                        PoolListener.NotifyNotAccepted(poolref, Transactions);
                    } catch (Exception err) {
                        //the idiot caused a error. idiot!
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                "You Idiot did not handle a Exception you have doom us all",
                                err);
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                String.format("Listener that Caused The issue: %s",
                                        PoolListener.getClass().getName()));
                    }
                });
            } finally {
                NotificatorLock.unlock();
            }
        }

        private void tick() {
            NotificatorLock.lock();
            try {
                PoolListeners.forEach((PoolListener) -> {
                    try {
                        PoolListener.Tick(poolref);
                    } catch (Exception err) {
                        //the idiot caused a error. idiot!
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                "You Idiot did not handle a Exception you have doom us all",
                                err);
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                String.format("Listener that Caused The issue: %s",
                                        PoolListener.getClass().getName()));
                    }
                });
            } finally {
                NotificatorLock.unlock();
            }
        }

        public void halt() {
            Halted = true;
        }

        private void processDoublespend(List<? extends ITransaction> DoubleSpentTransactions) {
            try {
                NotificatorLock.lock();
                PoolListeners.forEach((PoolListener) -> {
                    try {
                        PoolListener.NotifyDoubleSpent(poolref, DoubleSpentTransactions);
                    } catch (Exception err) {
                        //the idiot caused a error. idiot! 
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                "You Idiot did not handle a Exception you have doom us all",
                                err);
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                String.format("Listener that Caused The issue: %s",
                                        PoolListener.getClass().getName()));
                    }
                });
            } finally {
                NotificatorLock.unlock();
            }
        }

        private void processRejectINV(List<T> Transactions) {
            try {
                NotificatorLock.lock();
                PoolListeners.forEach((PoolListener) -> {
                    try {
                        PoolListener.NotifyInvalidSignature(poolref, Transactions);
                    } catch (Exception err) {
                        //the idiot caused a error. idiot! 
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                "You Idiot did not handle a Exception you have doom us all",
                                err);
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                String.format("Listener that Caused The issue: %s",
                                        PoolListener.getClass().getName()));
                    }
                });
            } finally {
                NotificatorLock.unlock();
            }
        }

        private void processRejectNOTACCEPTED(List<T> Transactions) {
            try {
                NotificatorLock.lock();
                PoolListeners.forEach((PoolListener) -> {
                    try {
                        PoolListener.NotifyNotAccepted(poolref, Transactions);
                    } catch (Exception err) {
                        //the idiot caused a error. idiot! 
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                "You Idiot did not handle a Exception you have doom us all",
                                err);
                        LoggingHelper.getAClassLogger(
                                PoolChangeListener.class.getName()
                        ).log(Level.SEVERE,
                                String.format("Listener that Caused The issue: %s",
                                        PoolListener.getClass().getName()));
                    }
                });
            } finally {
                NotificatorLock.unlock();
            }
        }

    }

}
