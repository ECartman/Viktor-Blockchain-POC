/*
 * 
 *   Copyright (c)© 2019 Eduardo Vindas Cordoba. All rights reserved.
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
package com.aeongames.blockchain.consensus.pbft;

import static com.aeongames.blockchain.consensus.pbft.pbftvars.*;
import com.aeongames.blockchain.consensus.pbft.replica.BlockReplica;
import com.aeongames.blockchain.consensus.pbft.replica.BlockReplicaEncoder;
import com.aeongames.blockchain.consensus.pbft.replica.BlockReplicaTransport;
import com.aeongames.crypto.signature.SignatureProvider;
import static com.aeongames.logger.LoggingHelper.getDefaultLogger;
import com.gmail.woodyc40.pbft.DefaultReplicaMessageLog;
import com.gmail.woodyc40.pbft.ReplicaRequestKey;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.exceptions.JedisExhaustedPoolException;

/**
 * this class is a pbft communicator
 * TODO: make sure this class when ask to halt to Close in a graceful manner. as the connections currently has no
 * way to close "gracefully"
 */
public class pbftServerCommunication {

    private boolean expent = false;
    private final ListenerRegister[] listenerThread; 
    private final WaitTimeout[] timeoutThread;
    private final CountDownLatch readyLatch;
    private final BlockReplicaEncoder ReplicaEncoder = new BlockReplicaEncoder();
    private final BlockReplicaTransport channel;

    public pbftServerCommunication(JedisPool pool, SignatureProvider serverProvider) {
        readyLatch = new CountDownLatch(REPLICA_COUNT);
        listenerThread = new ListenerRegister[REPLICA_COUNT];
        timeoutThread = new WaitTimeout[REPLICA_COUNT];
        channel = new BlockReplicaTransport(pool, REPLICA_COUNT);
        //create the simulated servers.
        for (int i = 0; i < REPLICA_COUNT; i++) {
            DefaultReplicaMessageLog log = new DefaultReplicaMessageLog(100, 100, 200);
            final BlockReplica replica = new BlockReplica(i, TOLERANCE,
                    TIMEOUT_MS, log,
                    ReplicaEncoder,
                    channel,
                    serverProvider
            );
            listenerThread[i] = new ListenerRegister(pool, replica);
            timeoutThread[i] = new WaitTimeout(TIMEOUT_MS, replica);
        }
    }

    public void StartThreads() {
        //the simulation is daemon meaning that when the jvm closes all others threads
        //this one will be killed or forced to close. 
        for (Thread thread : listenerThread) {
            thread.setDaemon(true);
            thread.start();
        }
        for (Thread thread : timeoutThread) {
            thread.setDaemon(true);
            thread.start();
        }
        try {
            readyLatch.await();
        } catch (InterruptedException ignored) {
            getDefaultLogger().log(Level.SEVERE, null, ignored);
        }
        //to change if this is to run async.
        expent = true;
    }
    
    public void Halt(){
        for (WaitTimeout thread : timeoutThread) {
            thread.halt();
        }
    }

    private static int currentid = 0;

    private static final class WaitTimeout extends Thread {
        private volatile boolean halt=false;
        private final long timeout;
        BlockReplica replica;
        
        public synchronized boolean halt(){
            halt=true;
            return this.isAlive();
        }
        
        public synchronized boolean isHaltRequested(){
            return halt;
        }

        WaitTimeout(long timeout, BlockReplica replicaObject) {
            super(String.format("Replica_WaitTimeout<%d>", currentid));
            currentid = currentid == Integer.MAX_VALUE ? 0 : ++currentid;
            this.timeout = timeout;
            this.replica = Objects.requireNonNull(replicaObject, "the Replica object Must be non null");
        }

        @Override
        public void run() {
            while (!isHaltRequested()) {
                long minTime = timeout;
                for (ReplicaRequestKey key : replica.activeTimers()) {
                    long waitTime = replica.checkTimeout(key);
                    if (waitTime > 0 && waitTime < minTime) {
                        minTime = waitTime;
                    }
                }
                try {
                    Thread.sleep(minTime);
                } catch (InterruptedException e) {
                    Logger.getLogger(pbftServerCommunication.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }

    /**
     * this one does not seem to need to be a daemon thread... it only register listeners. 
     */
    private final class ListenerRegister extends Thread {
        private  final ArrayList<Exception> DetectedErrors=new ArrayList<>();
        private static final String BASENAME = "replica-";
        private final Jedis jedis;
        private final String channel;
        private final BlockReplica replica;

        public ListenerRegister(JedisPool pool, BlockReplica replica) throws JedisConnectionException, JedisException, JedisExhaustedPoolException {
            super(BASENAME + replica.replicaId());
            this.channel = BASENAME + replica.replicaId();
            this.replica = Objects.requireNonNull(replica, "Replica cannot be null");
            this.jedis = Objects.requireNonNull(pool.getResource(), "the pool cannot be null");
        }

        @Override
        public void run() {
            boolean alredydiscouted=false;
            try (jedis) {
                JedisPubSub listener = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        replica.handleIncomingMessage(message);
                    }
                };
                readyLatch.countDown();
                alredydiscouted=true;
                jedis.subscribe(listener, channel);
            } catch (JedisConnectionException err) {
                DetectedErrors.add(err);
                Logger.getLogger(pbftServerCommunication.class.getName()).log(Level.SEVERE, null, err);
            }catch(RuntimeException err){
                DetectedErrors.add(err);
                Logger.getLogger(pbftServerCommunication.class.getName()).log(Level.SEVERE, "unexpected error trigger", err);
            }finally{
                //fixing deadlock condition.
                //only do this if not counted down before.
                if(!alredydiscouted){
                    readyLatch.countDown(); 
                }
            }
        }
    }

    /**
     * tell if this communication emulation has finish.
     *
     * @return the expent status
     */
    public boolean isExpent() {
        return expent;
    }

}
