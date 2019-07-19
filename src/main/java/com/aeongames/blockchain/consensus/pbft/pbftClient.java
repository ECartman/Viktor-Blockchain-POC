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
package com.aeongames.blockchain.consensus.pbft;

import com.aeongames.blockchain.base.Block;
import com.aeongames.blockchain.base.SerializableBlock;
import com.aeongames.blockchain.consensus.pbft.client.BlockClient;
import com.aeongames.blockchain.consensus.pbft.client.BlockClientEncoder;
import com.aeongames.blockchain.consensus.pbft.client.BlockClientTransport;
import static com.aeongames.blockchain.consensus.pbft.pbftvars.NAME;
import static com.aeongames.blockchain.consensus.pbft.pbftvars.REPLICA_COUNT;
import static com.aeongames.blockchain.consensus.pbft.pbftvars.TIMEOUT_MS;
import static com.aeongames.blockchain.consensus.pbft.pbftvars.TOLERANCE;
import com.gmail.woodyc40.pbft.Client;
import com.gmail.woodyc40.pbft.ClientTicket;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO: make sure this class when ask to halt to Close in a graceful manner. as the connections currently has no
 *   way to close "gracefully"
 * TODO: this class require to be Refactored to be have better performance. this
 * is just an adaptation from the PBFT from github with some changes. i do start
 * to like his implementation of the PBFT. but his example is too limited on
 * some aspects that i don't like this class is a a "Client" for the PBFT
 * scheme. however it is not intended to be part of the user client. it is a
 * client for start the PBFT transmission and consensus of the block chain.
 */
public class pbftClient {

    private final JedisPool pool;
    private ReentrantLock resultWritter;
    private ConsensusResult result = null;
    private final Client<SerializableBlock, ConsensusResult, String> client;

    public pbftClient() {
        this(null);
    }

    public pbftClient(JedisPool thePoolTouse) {
        this.resultWritter = new ReentrantLock(true);
        pool = Objects.requireNonNullElse(thePoolTouse, new JedisPool());
        client = setupClient(pool);
    }

    public synchronized void pbftClient_execution(Block block) {
        setresult(null);
        Set<ClientTicket<SerializableBlock, ConsensusResult>> tickets = new HashSet<>();
        SerializableBlock operation = SerializableBlock.ToSerializableBlock(block);
        ClientTicket<SerializableBlock, ConsensusResult> ticket = client.sendRequest(operation);
        tickets.add(ticket);
        ticket.result().thenAccept(AuthResult -> {
            NotifyResult(AuthResult);
        });
        WaitTimeout<SerializableBlock, ConsensusResult, String> thread = new WaitTimeout<>(TIMEOUT_MS, client, tickets);
        thread.start();
        try {
            awaitForCompletion(thread);
        } catch (InterruptedException ex) {
            Logger.getLogger(pbftClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * sets the current client result with a copy of the provided results if
     * null the results are set to null
     *
     * @param thereference null or the value to copy as the result for this
     * client
     */
    private void setresult(ConsensusResult thereference) {
        resultWritter.lock();
        try {
            result = thereference == null ? null : thereference.copyof();
        } finally {
            resultWritter.unlock();
        }
    }

    /**
     * gets a copy of the results.
     *
     * @return
     */
    public ConsensusResult getResult() {
        return result.copyof();
    }

    private void NotifyResult(final ConsensusResult result) {
        setresult(result);
    }

    public boolean isCompleted() {
        boolean iscompleted = false;
        resultWritter.lock();
        try {
            iscompleted = this.result != null;
        } finally {
            resultWritter.unlock();
        }
        return iscompleted;
    }

    private void awaitForCompletion(WaitTimeout<SerializableBlock, ConsensusResult, String> thread)
            throws InterruptedException {
        thread.join();
    }

    private static Client<SerializableBlock, ConsensusResult, String> setupClient(JedisPool pool) {
        BlockClientEncoder clientEncoder = new BlockClientEncoder();
        BlockClientTransport clientTransport = new BlockClientTransport(pool, REPLICA_COUNT);
        currentClientid = currentClientid == Integer.MAX_VALUE ? 0 : ++currentClientid;
        BlockClient client = new BlockClient(String.format("%sClient-%d", NAME, currentClientid), TOLERANCE, TIMEOUT_MS,
                clientEncoder, clientTransport);

        CountDownLatch readyLatch = new CountDownLatch(1);
        Thread thread = new Thread(() -> {
            try (Jedis jedis = pool.getResource()) {
                String channel = client.clientId();
                JedisPubSub listener = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        client.handleIncomingMessage(message);
                    }
                };

                readyLatch.countDown();
                jedis.subscribe(listener, channel);
            }
        });
        thread.setName(String.format("%sClient-%d", NAME, currentClientid));
        thread.setDaemon(true);
        thread.start();

        try {
            readyLatch.await();
        } catch (InterruptedException ignored) {

        }
        return client;
    }

    private static int currentid = 0;
    private static int currentClientid = 0;

    private final class WaitTimeout<O, R, T> extends Thread {

        private final Collection<ClientTicket<O, R>> tickets;
        Client<O, R, T> Client;
        private final long timeout;

        WaitTimeout(long timeout, Client<O, R, T> client, Collection<ClientTicket<O, R>> tickets) {
            super(String.format("Client_WaitTimeout<%d>", currentid));
            currentid = currentid == Integer.MAX_VALUE ? 0 : ++currentid;
            this.tickets = tickets;
            this.timeout = timeout;
            this.Client = Objects.requireNonNull(client, "the client object Must be non null");
        }

        @Override
        public void run() {
            int completed = 0;
            while (true) {
                long minTime = timeout;
                for (ClientTicket<O, R> ticket : tickets) {
                    long sleepTime = determineSleepTime(timeout, ticket);
                    if (sleepTime > 0 && sleepTime < minTime) {
                        minTime = sleepTime;
                    }
                    if (ticket.result().isDone()) {
                        completed++;
                        continue;
                    }
                    Client.checkTimeout(ticket);
                }
                if (completed == tickets.size()) {
                    break;
                }
                try {
                    Thread.sleep(minTime);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private static long determineSleepTime(long timeout, ClientTicket<?, ?> ticket) {
        long elapsed = System.currentTimeMillis() - ticket.dispatchTime();
        return Math.max(0, timeout - elapsed);
    }

    /**
     * @return the pool
     */
    public synchronized JedisPool getPool() {
        return pool;
    }
}
