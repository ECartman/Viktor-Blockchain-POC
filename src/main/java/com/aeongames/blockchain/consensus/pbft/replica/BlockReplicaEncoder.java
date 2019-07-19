package com.aeongames.blockchain.consensus.pbft.replica;

import com.aeongames.blockchain.base.SerializableBlock;
import com.aeongames.blockchain.consensus.pbft.ConsensusResult;
import static com.aeongames.blockchain.base.common.ByteUtils.ByteArrayToString;
import static com.aeongames.blockchain.consensus.pbft.client.JSONTYPES.*;
import com.gmail.woodyc40.pbft.ReplicaEncoder;
import com.gmail.woodyc40.pbft.message.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class BlockReplicaEncoder implements ReplicaEncoder<SerializableBlock, ConsensusResult, String> {
    private static void writeRequest(JsonObject root, ReplicaRequest<SerializableBlock> request) {
        SerializableBlock op = request.operation();
        long timestamp = request.timestamp();
        String clientId = request.clientId();

        JsonObject operation = new JsonObject();
        Gson gson = new GsonBuilder().create();
        operation.add(JSONBBLOCK,gson.toJsonTree(op));
        root.add(JSONOP, operation);
        root.addProperty(JSONOPTIME, timestamp);
        root.addProperty(JSONOPCLIENT, clientId);
    }

    @Override
    public String encodeRequest(ReplicaRequest<SerializableBlock> request) {
        JsonObject root = new JsonObject();
        root.addProperty(JSONOBJECTTYPE, JSONOBJECTTYPE_REQUEST);
        writeRequest(root, request);

        return root.toString();
    }

    private static void writePhaseMessage(JsonObject root, ReplicaPhaseMessage message) {
        root.addProperty("view-number", message.viewNumber());
        root.addProperty("seq-number", message.seqNumber());
        //vulnerability or caveat, sending bytes encoded does not guarantee the RIGHT data will be sent. also there are
        //several issues to consider. this is plain stupid. lets fix this line
        //root.addProperty("digest", new String(message.digest(), StandardCharsets.UTF_8));
        root.addProperty("digest", ByteArrayToString(message.digest()));
    }

    private static JsonObject writePrePrepare(ReplicaPrePrepare<SerializableBlock> prePrepare) {
        JsonObject root = new JsonObject();
        root.addProperty(JSONOBJECTTYPE, JSONOBJECTTYPE_PREPRE);
        writePhaseMessage(root, prePrepare);
        writeRequest(root, prePrepare.request());

        return root;
    }

    @Override
    public String encodePrePrepare(ReplicaPrePrepare<SerializableBlock> prePrepare) {
        return writePrePrepare(prePrepare).toString();
    }

    private static JsonObject writePrepare(ReplicaPrepare prepare) {
        JsonObject root = new JsonObject();
        root.addProperty(JSONOBJECTTYPE, JSONOBJECTTYPE_PRE);
        writePhaseMessage(root, prepare);
        root.addProperty(JSONOBJECTREPLICAID, prepare.replicaId());

        return root;
    }

    @Override
    public String encodePrepare(ReplicaPrepare prepare) {
        return writePrepare(prepare).toString();
    }

    @Override
    public String encodeCommit(ReplicaCommit commit) {
        JsonObject root = new JsonObject();
        root.addProperty(JSONOBJECTTYPE, JSONOBJECTTYPE_COMMIT);
        writePhaseMessage(root, commit);
        root.addProperty(JSONOBJECTREPLICAID, commit.replicaId());

        return root.toString();
    }

    @Override
    public String encodeReply(ReplicaReply<ConsensusResult> reply) {
        JsonObject root = new JsonObject();
        root.addProperty(JSONOBJECTTYPE,JSONOBJECTTYPE_REPLY);
        root.addProperty(JSONOBJECTVIEWNUM, reply.viewNumber());
        root.addProperty(JSONOBJECTTIMESTAMP, reply.timestamp());
        root.addProperty("client-id", reply.clientId());
        root.addProperty(JSONOBJECTREPLICAID, reply.replicaId());
        JsonObject operation = new JsonObject();
        Gson gson = new GsonBuilder().create();
        root.add(JSONOBJECTRESULT,gson.toJsonTree(reply.result()));
        //root.addProperty(JSONOBJECTRESULT, reply.result().result());
        //root.addProperty(JSONOBJECTBLOCKID, reply.result().RelatedBlock());

        return root.toString();
    }

    private static JsonObject writeCheckpoint(ReplicaCheckpoint checkpoint) {
        JsonObject root = new JsonObject();
        root.addProperty(JSONOBJECTTYPE, JSONOBJECTTYPE_CHECK);
        root.addProperty("last-seq-number", checkpoint.lastSeqNumber());
        //root.addProperty("digest", new String(checkpoint.digest(), StandardCharsets.UTF_8));
        root.addProperty("digest", ByteArrayToString(checkpoint.digest()));
        root.addProperty(JSONOBJECTREPLICAID, checkpoint.replicaId());
        return root;
    }

    @Override
    public String encodeCheckpoint(ReplicaCheckpoint checkpoint) {
        return writeCheckpoint(checkpoint).toString();
    }

    @SuppressWarnings("unchecked")
    private static JsonObject writeViewChange(ReplicaViewChange viewChange) {
        JsonObject root = new JsonObject();
        root.addProperty(JSONOBJECTTYPE, JSONOBJECTTYPE_VIEWCHNG);
        root.addProperty("new-view-number", viewChange.newViewNumber());
        root.addProperty("last-seq-number", viewChange.lastSeqNumber());
        JsonArray checkpointProofs = new JsonArray();
        viewChange.checkpointProofs().forEach((checkpoint) -> {
            checkpointProofs.add(writeCheckpoint(checkpoint));
        });
        root.add("checkpoint-proofs", checkpointProofs);
        JsonArray preparedProofs = new JsonArray();
        viewChange.preparedProofs().entrySet().stream().map((entry) -> {
            JsonObject proof = new JsonObject();
            proof.addProperty("seq-number", entry.getKey());
            JsonArray messages = new JsonArray();
            entry.getValue().forEach((message) -> {
                if (message instanceof ReplicaPrePrepare) {
                    messages.add(writePrePrepare((ReplicaPrePrepare<SerializableBlock>) message));
                } else if (message instanceof ReplicaPrepare) {
                    messages.add(writePrepare((ReplicaPrepare) message));
                }
            });
            proof.add("messages", messages);
            return proof;
        }).forEachOrdered((proof) -> {
            preparedProofs.add(proof);
        });
        root.add("prepared-proofs", preparedProofs);
        root.addProperty(JSONOBJECTREPLICAID, viewChange.replicaId());

        return root;
    }

    @Override
    public String encodeViewChange(ReplicaViewChange viewChange) {
        return writeViewChange(viewChange).toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String encodeNewView(ReplicaNewView newView) {
        JsonObject root = new JsonObject();
        root.addProperty(JSONOBJECTTYPE,JSONOBJECTTYPE_NEWVIEW);
        root.addProperty("new-view-number", newView.newViewNumber());
        JsonArray viewChangeProofs = new JsonArray();
        newView.viewChangeProofs().forEach((viewChange) -> {
            viewChangeProofs.add(writeViewChange(viewChange));
        });
        root.add("view-change-proofs", viewChangeProofs);
        JsonArray preparedProofs = new JsonArray();
        newView.preparedProofs().forEach((prePrepare) -> {
            preparedProofs.add(writePrePrepare((ReplicaPrePrepare<SerializableBlock>) prePrepare));
        });
        root.add("prepared-proofs", preparedProofs);

        return root.toString();
    }
}
