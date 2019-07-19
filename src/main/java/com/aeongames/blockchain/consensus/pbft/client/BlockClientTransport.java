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
package com.aeongames.blockchain.consensus.pbft.client;
import com.aeongames.logger.LoggingHelper;
import com.gmail.woodyc40.pbft.ClientTransport;

import java.util.logging.Level;
import java.util.stream.IntStream;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;



public class BlockClientTransport implements ClientTransport<String> {
    private final JedisPool pool;
    private final int replicas;

    public BlockClientTransport(JedisPool ch, int replicas) {
        this.pool = ch;
        this.replicas = replicas;
    }

    @Override
    public IntStream knownReplicaIds() {
        return IntStream.range(0, this.replicas);
    }

    @Override
    public int countKnownReplicas() {
        return this.replicas;
    }

    private static String toChannel(int replicaId) {
        return "replica-" + replicaId;
    }

    @Override
    public void sendRequest(int replicaId, String request) {
        System.out.println(String.format("Client SEND -> %d: %s", replicaId, request));
        if(!this.pool.isClosed()) {
            try (Jedis jedis = this.pool.getResource()) {
                jedis.publish(toChannel(replicaId), request);
            } catch (Throwable err) {
                LoggingHelper.getDefaultLogger().log(Level.SEVERE, "error", err);
            }
        }else{
            //we cannot send the request we are not connected!
            LoggingHelper.getDefaultLogger().log(Level.SEVERE, "error: not Connected!");
            throw new RuntimeException("unable to send a request due the Pool is closed");
        }
    }

    @Override
    public void multicastRequest(String request) {
        for (int i = 0; i < this.replicas; i++) {
            this.sendRequest(i, request);
        }
    }
}
