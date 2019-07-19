package com.aeongames.blockchain.consensus.pbft.replica;


import com.gmail.woodyc40.pbft.ReplicaTransport;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class BlockReplicaTransport implements ReplicaTransport<String> {
      private final JedisPool pool;
    private final int replicas;

    public BlockReplicaTransport(JedisPool ch, int replicas) {
        this.pool = ch;
        this.replicas = replicas;
    }

    @Override
    public int countKnownReplicas() {
        return this.replicas;
    }

    @Override
    public IntStream knownReplicaIds() {
        return IntStream.range(0, this.replicas);
    }

    private static String toChannel(int replicaId) {
        return "replica-" + replicaId;
    }

    @Override
    public void sendMessage(int replicaId, String request) {
        synchronized (System.out) {
            System.out.println(String.format("Replica SEND -> %d: %s", replicaId, request));
        }

        String channel = toChannel(replicaId);
        try (Jedis jedis = this.pool.getResource()) {
            jedis.publish(channel, request);
        }
    }

    @Override
    public void multicast(String data, int... ignoredReplicas) {
        Set<Integer> ignored = new HashSet<>(ignoredReplicas.length);
        for (int id : ignoredReplicas) {
            ignored.add(id);
        }

        for (int i = 0; i < this.replicas; i++) {
            if (!ignored.contains(i)) {
                this.sendMessage(i, data);
            }
        }
    }

    @Override
    public void sendReply(String clientId, String reply) {
        synchronized (System.out) {
            System.out.println(String.format("Replica SEND -> %s: %s", clientId, reply));
        }
        try (Jedis jedis = this.pool.getResource()) {
            jedis.publish(clientId, reply);
        }
    }
}
