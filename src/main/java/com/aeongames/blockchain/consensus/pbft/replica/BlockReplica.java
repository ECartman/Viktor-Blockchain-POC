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
package com.aeongames.blockchain.consensus.pbft.replica;

import com.aeongames.blockchain.base.Block;
import com.aeongames.blockchain.base.SerializableBlock;

import static com.aeongames.blockchain.base.common.ByteUtils.ByteArrayToString;
import static com.aeongames.blockchain.base.common.ByteUtils.HexToBytes;
import com.aeongames.blockchain.consensus.pbft.ConsensusResult;
import static com.aeongames.blockchain.consensus.pbft.client.JSONTYPES.*;

import com.aeongames.crypto.signature.SignatureProvider;
import static com.aeongames.logger.LoggingHelper.getDefaultLogger;
import com.gmail.woodyc40.pbft.*;
import com.gmail.woodyc40.pbft.message.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.security.*;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlockReplica extends DefaultReplica<SerializableBlock, ConsensusResult, String> {

    private final SignatureProvider SignatureKeyStore;

    public BlockReplica(int replicaId,
            int tolerance,
            long timeout,
            ReplicaMessageLog log,
            ReplicaEncoder<SerializableBlock, ConsensusResult, String> encoder,
            ReplicaTransport<String> transport,
            SignatureProvider SecurityStore) {
        super(replicaId, tolerance, timeout, log, encoder, (ReplicaRequest<SerializableBlock> request) -> {
            if (request != null && request.operation() != null ) {
                if(request.operation().getBlockHash()!=null) {
                    return request.operation().getBlockHash().toByteArray();
                }else{
                    return null;
                }
            } else {
                return null;
            }
        }, transport);
        SignatureKeyStore= Objects.requireNonNull(SecurityStore,"Invalid key store");
    }

    @Override
    public ConsensusResult compute(SerializableBlock operation) {
        String hexResult = null;
        synchronized (SignatureKeyStore) {
            if (confirmBlockValidity(operation)) {
                try {
                    hexResult = ByteArrayToString(SignatureKeyStore.signData(operation.getBlockHash().toByteArray()));
                } catch (SignatureException e) {
                    Logger.getLogger(BlockReplica.class.getName()).log(Level.SEVERE, null, e);
                } catch (Exception err) {
                    Logger.getLogger(BlockReplica.class.getName()).log(Level.SEVERE, null, err);
                }
                if (hexResult == null) {
                    getDefaultLogger().log(Level.SEVERE, "somethething went wrong?");
                }
            }else{
                hexResult = "";
            }
        }
        return new ConsensusResult(ByteArrayToString(SignatureKeyStore.getCertSignature()),operation.getBlockHash().toString(), hexResult);
    }

/*
     @Override
    public ConsensusResult compute(SerializableBlock operation) {
        //TODO IMPLEMENT MORE NICELY
        confirmBlockValidity(operation);
        char[] pin = "<smartcard pin>".toCharArray();
        String hexResult=null;
        synchronized (BlockReplica.class) {
            try {
                KeyStore ks = DigitalSignatureHelper.getKeystore(pin);
                properties_File settings = new properties_File("/com/aeongames/blockchain/resources/Blockchainprops.properties");
                String algorithm = "SHA256withRSA";
                Signature RSASHA = DigitalSignatureHelper.getSignatureObject_toSign(ks, settings.getProperty("SignatureKeyAlias"), algorithm, new KeyStore.PasswordProtection(pin));
                RSASHA.update(operation.getBlockHash().toByteArray());
                hexResult = ByteArrayToString(RSASHA.sign());
            } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | UnrecoverableEntryException | InvalidKeyException | SignatureException e) {
                Logger.getLogger(BlockReplica.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return new ConsensusResult(hexResult);
    }*/


    /**
     * check if the provided block is valid.
     * @param operation
     * @return
     */
    private boolean confirmBlockValidity(SerializableBlock operation) {
        //Please note this will not verify against a real BlockChain... for now.
        Block tmpblock= SerializableBlock.DeSerializeBlock(operation);
        //TODO: check valid timeframe
        if(Instant.ofEpochMilli(tmpblock.getTimeStamp()).isAfter(
       Instant.now()
        ))
            return false;
        //TODO: check Valid signatures
        //TODO:CHECK VALID INDEX
        //TODO: Implement more checks. in general before accepting a block.
        return tmpblock.getHash().equals(operation.getBlockHash());
    }

    private static ReplicaRequest<SerializableBlock> readRequest(JsonObject root) {
        JsonElement operation = root.get(JSONOP);
        SerializableBlock block = null;
        if (!operation.isJsonNull()) {
            JsonObject operationObject = operation.getAsJsonObject();
            Gson gson = new GsonBuilder().create();
            block = gson.fromJson(operationObject.get(JSONBBLOCK), SerializableBlock.class);
        }
        long timestamp = root.get(JSONOPTIME).getAsLong();
        String clientId = root.get(JSONOPCLIENT).getAsString();

        return new DefaultReplicaRequest<>(block, timestamp, clientId);
    }

    private static ReplicaPrePrepare<SerializableBlock> readPrePrepare(JsonObject root) {
        int viewNumber = root.get("view-number").getAsInt();
        long seqNumber = root.get("seq-number").getAsLong();
        byte[] digest = HexToBytes(root.get("digest").getAsString());
        ReplicaRequest<SerializableBlock> request = readRequest(root);

        return new DefaultReplicaPrePrepare<>(
                viewNumber,
                seqNumber,
                digest,
                request);
    }

    private static ReplicaPrepare readPrepare(JsonObject root) {
        int viewNumber = root.get("view-number").getAsInt();
        long seqNumber = root.get("seq-number").getAsLong();
        byte[] digest = HexToBytes(root.get("digest").getAsString());
        int replicaId = root.get("replica-id").getAsInt();

        return new DefaultReplicaPrepare(
                viewNumber,
                seqNumber,
                digest,
                replicaId);
    }

    private static ReplicaCommit readCommit(JsonObject root) {
        int viewNumber = root.get("view-number").getAsInt();
        long seqNumber = root.get("seq-number").getAsLong();
        byte[] digest = HexToBytes(root.get("digest").getAsString());
        int replicaId = root.get("replica-id").getAsInt();

        return new DefaultReplicaCommit(
                viewNumber,
                seqNumber,
                digest,
                replicaId);
    }

    private static ReplicaCheckpoint readCheckpoint(JsonObject root) {
        long lastSeqNumber = root.get("last-seq-number").getAsLong();
        byte[] digest = HexToBytes(root.get("digest").getAsString());
        int replicaId = root.get("replica-id").getAsInt();

        return new DefaultReplicaCheckpoint(
                lastSeqNumber,
                digest,
                replicaId);
    }

    private static ReplicaViewChange readViewChange(JsonObject root) {
        int newViewNumber = root.get("new-view-number").getAsInt();
        long lastSeqNumber = root.get("last-seq-number").getAsLong();

        Collection<ReplicaCheckpoint> checkpointProofs = new ArrayList<>();
        JsonArray checkpointProofsArray = root.get("checkpoint-proofs").getAsJsonArray();
        for (JsonElement checkpoint : checkpointProofsArray) {
            checkpointProofs.add(readCheckpoint(checkpoint.getAsJsonObject()));
        }

        Map<Long, Collection<ReplicaPhaseMessage>> preparedProofs = new HashMap<>();
        JsonArray preparedProofsArray = root.get("prepared-proofs").getAsJsonArray();
        for (JsonElement element : preparedProofsArray) {
            JsonObject proof = element.getAsJsonObject();
            long seqNumber = proof.get("seq-number").getAsLong();

            Collection<ReplicaPhaseMessage> messages = new ArrayList<>();
            JsonArray messagesArray = proof.get("messages").getAsJsonArray();
            for (JsonElement message : messagesArray) {
                String type = message.getAsJsonObject().get(JSONOBJECTTYPE).getAsString();
                if ("PRE-PREPARE".equals(type)) {
                    messages.add(readPrePrepare(message.getAsJsonObject()));
                } else if ("PREPARE".equals(type)) {
                    messages.add(readPrepare(message.getAsJsonObject()));
                }
            }

            preparedProofs.put(seqNumber, messages);
        }
        int replicaId = root.get("replica-id").getAsInt();

        return new DefaultReplicaViewChange(
                newViewNumber,
                lastSeqNumber,
                checkpointProofs,
                preparedProofs,
                replicaId);
    }

    private static ReplicaNewView readNewView(JsonObject root) {
        int newViewNumber = root.get("new-view-number").getAsInt();

        Collection<ReplicaViewChange> viewChangeProofs = new ArrayList<>();
        JsonArray viewChangesArray = root.get("view-change-proofs").getAsJsonArray();
        for (JsonElement element : viewChangesArray) {
            viewChangeProofs.add(readViewChange(element.getAsJsonObject()));
        }

        Collection<ReplicaPrePrepare<?>> preparedProofs = new ArrayList<>();
        JsonArray preparedArray = root.get("prepared-proofs").getAsJsonArray();
        for (JsonElement element : preparedArray) {
            preparedProofs.add(readPrePrepare(element.getAsJsonObject()));
        }

        return new DefaultReplicaNewView(
                newViewNumber,
                viewChangeProofs,
                preparedProofs);
    }

    public void handleIncomingMessage(String data) {
        synchronized (System.out) {
            System.out.println(String.format("Rep<%s> %d RECV",Thread.currentThread().getName(), this.replicaId()));
        }
        Gson gson = new Gson();
        JsonObject root = gson.fromJson(data, JsonObject.class);

        String type = root.get(JSONOBJECTTYPE).getAsString();
        synchronized (System.out) {
            System.out.println(String.format("\tRep<%s> Kind: %s",Thread.currentThread().getName(), type));
        }
        if (null == type) {
            throw new IllegalArgumentException("Unrecognized type: NULL ");
        } else {
            switch (type) {
                case JSONOBJECTTYPE_REQUEST:
                    ReplicaRequest<SerializableBlock> request = readRequest(root);
                    this.recvRequest(request);
                    break;
                case JSONOBJECTTYPE_PREPRE:
                    ReplicaPrePrepare<SerializableBlock> prePrepare = readPrePrepare(root);
                    this.recvPrePrepare(prePrepare);
                    break;
                case JSONOBJECTTYPE_PRE:
                    ReplicaPrepare prepare = readPrepare(root);
                    this.recvPrepare(prepare);
                    break;
                case JSONOBJECTTYPE_COMMIT:
                    ReplicaCommit commit = readCommit(root);
                    this.recvCommit(commit);
                    break;
                case JSONOBJECTTYPE_CHECK:
                    ReplicaCheckpoint checkpoint = readCheckpoint(root);
                    this.recvCheckpoint(checkpoint);
                    break;
                case JSONOBJECTTYPE_VIEWCHNG:
                    ReplicaViewChange viewChange = readViewChange(root);
                    this.recvViewChange(viewChange);
                    break;
                case JSONOBJECTTYPE_NEWVIEW:
                    ReplicaNewView newView = readNewView(root);
                    this.recvNewView(newView);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized type: " + type);
            }
        }
    }
}
