package com.aeongames.blockchain.consensus.pbft.client;

import com.aeongames.blockchain.base.SerializableBlock;
import static com.aeongames.blockchain.consensus.pbft.client.JSONTYPES.*;
import com.gmail.woodyc40.pbft.ClientEncoder;
import com.gmail.woodyc40.pbft.message.ClientRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class BlockClientEncoder implements ClientEncoder<SerializableBlock, String> {
    @Override
    public String encodeRequest(ClientRequest<SerializableBlock> request) {
        SerializableBlock op = request.operation();
        long timestamp = request.timestamp();
        String clientId = request.client().clientId();

        JsonObject root = new JsonObject();
        root.addProperty(JSONOBJECTTYPE, JSONOBJECTTYPE_REQUEST);
        JsonObject operation = new JsonObject();
        Gson gson = new GsonBuilder().create();
        operation.add(JSONBBLOCK,gson.toJsonTree(op));
        root.add(JSONOP, operation);
        root.addProperty(JSONOPTIME, timestamp);
        root.addProperty(JSONOPCLIENT, clientId);
        return root.toString();
    }
}
