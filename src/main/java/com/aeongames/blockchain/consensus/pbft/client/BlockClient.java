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

import com.aeongames.blockchain.base.SerializableBlock;
import com.aeongames.blockchain.consensus.pbft.ConsensusResult;
import com.gmail.woodyc40.pbft.ClientEncoder;
import com.gmail.woodyc40.pbft.ClientTransport;
import com.gmail.woodyc40.pbft.DefaultClient;
import com.gmail.woodyc40.pbft.message.DefaultClientReply;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import static com.aeongames.blockchain.consensus.pbft.client.JSONTYPES.*;

/**
 * @author Eduardo <cartman@aeongames.com>
 */
public class BlockClient extends DefaultClient<SerializableBlock, ConsensusResult, String> {

    public BlockClient(String clientId, int tolerance,
            long timeoutMs,
            ClientEncoder<SerializableBlock, String> encoder,
            ClientTransport<String> transport) {
        super(clientId, tolerance, timeoutMs, encoder, transport);
    }

    public void handleIncomingMessage(String data) {
        System.out.println(String.format("Client %s RECV: %s", this.clientId(), data));
        Gson gson = new Gson();
        JsonObject root = gson.fromJson(data, JsonObject.class);
        ConsensusResult additionResult=null;
        String type = root.get(JSONOBJECTTYPE).getAsString();
        if (JSONOBJECTTYPE_REPLY.equals(type)) {
            int viewNumber = root.get(JSONOBJECTVIEWNUM).getAsInt();
            long timestamp = root.get(JSONOBJECTTIMESTAMP).getAsLong();
            int replicaId = root.get(JSONOBJECTREPLICAID).getAsInt();
            JsonElement Result = root.get(JSONOBJECTRESULT);
            if (!Result.isJsonNull()) {
               // JsonObject resultobj = Result.getAsJsonObject();
                Gson gson2 = new GsonBuilder().create();
                additionResult = gson2.fromJson(Result, ConsensusResult.class);
            }

            DefaultClientReply<ConsensusResult> reply = new DefaultClientReply<>(
                    viewNumber,
                    timestamp,
                    this,
                    replicaId,
                    additionResult);
            this.recvReply(reply);
        } else {
            throw new IllegalArgumentException("Unrecognized type: " + type);
        }
    }
}
