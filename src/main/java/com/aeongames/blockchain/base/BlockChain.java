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
package com.aeongames.blockchain.base;

import com.aeongames.blockchain.TODOS.NotImplementedYet;
import com.aeongames.blockchain.TODOS.SubjectToChange;
import com.aeongames.blockchain.base.common.Hash;
import com.aeongames.blockchain.base.transactions.ITransaction;
import com.aeongames.blockchain.consensus.pbft.pbftClient;
import static com.aeongames.blockchain.base.common.BinaryMath.twoPower;
import com.aeongames.blockchain.base.common.ByteUtils;
import static com.aeongames.blockchain.base.common.ByteUtils.to_byte_buff;
import com.aeongames.blockchain.consensus.pbft.ConsensusResult;
import com.aeongames.crypto.signature.SignatureProvider;
import com.aeongames.logger.LoggingHelper;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * this is a IN MEMORY representation of the block chain please have in mind
 * this is not intended to contain LONG or STRONG chains.this class is made so
 * it can represent the chain on a POC in the future changes needs to be done in
 * order to contain Partial blocks from the chain and the chain becomes
 * persistence once it reaches a certain size, age, or other criteria. the
 * underline way of persistence is up to the needs of the implementation IT
 * COULD be a ye olde SQL a NONSQL or other new generation storage. remember tho
 * the chain should be immutable (nothing once commit can be removed. if FORCED
 * it will become inconsistent and the hold or partial chain Might become
 * corrupted. TODO:: ensure or make sure that the transaction list are final and
 * immutable. maybe implement our own linked or array imutable list. that cannot
 * be modified & objects are immutable or uneditable.
 *
 * @author Eduardo <cartman@aeongames.com>
 * @param <T> the underline kind of data that implements the ITransaction
 * interface
 */
public final class BlockChain<T extends ITransaction> {

    /**
     * this will have the max amount of blocks that will be in memory loaded.
     * this is intended mostly for future use and ensure the block is not too
     * heavy in memory.
     */
    public static final int MAXBLOCKS = Integer.MAX_VALUE;

    /**
     * the Default allowed amount of transactions for the block.
     */
    public static final int DEFAULT_TRANSACTIONS_PER_BLOCK = (int) twoPower(1);

    public static final Hash GEN_PRE_HASH = Hash.HASHOFZERO;//--> 0 hash or e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
    /**
     * **********************************************
     */

    /**
     * this will be the structure that will hold the chain data. why a linked
     * list? although a linkedlist is slower than an Array list (contiguous
     * memory structure) it has some limitations and performance trade due the
     * fact that when more and more data is added the underline array must grow
     * and re add the items. this is negative for the desire and expected
     * behaviour. also the LinkedList represent better the way our data will
     * exist, move and so on. another aspect is that this list is just a list of
     * references. in the end it does NOT contain the actual blocks just
     * references to them that exist on HEAP memory (or in disk if storage
     * backed.) this value is final however the content MIGHT be ever changing
     */
    private final LinkedList<Block> UderlineChain;
    /**
     * this value tell us if this block current state is persisted (safe to
     * unload) TODO: implement persistance.
     */
    private boolean isStored;

    /**
     * the communication channel used to transmit the request for new blocks
     * into this chain, they need to be aproved and signed by an authoratha.
     *
     */
    private final pbftClient CommunicationClient;

    /**
     * to use later TODO: use
     */
    public final int ammount_transactions = DEFAULT_TRANSACTIONS_PER_BLOCK;

    private BlockChain() {
        throw new IllegalCallerException("this constructor call is ilegal");
    }

    /**
     * for future implementation this one is is intended to "de serialize the
     * stored chain into a new instance of a chain"
     *
     * @param CommunicationClient
     * @param PersistedChain
     */
    @NotImplementedYet
    protected BlockChain(pbftClient CommunicationClient, Object PersistedChain) {
        this(CommunicationClient);
        Objects.requireNonNull(PersistedChain, "The loaded chain cannot be null");
        throw new UnsupportedOperationException("Not Avaiable yet!");
    }

    /**
     * creates a new BlockChain and initialize the variables required.
     *
     * @param CommunicationClient the channel used to send to the byzantine
     * generals the info and request their approval.
     */
    /*package protected*/ BlockChain(pbftClient CommunicationClient) {
        this.CommunicationClient = Objects.requireNonNull(CommunicationClient,
                "The Communication Client cannot be null");
        isStored = false;
        UderlineChain = new LinkedList<>();
    }

    /**
     * propose a new block to be added into the chain.at this point there is yet
     * no warranted to succeed please not thought this will held the Execution
     * thread until this is finish.
     *
     *
     * @throws java.lang.InterruptedException if something interrupted the
     * process or the code did not complete correctly.
     * @SubjectToChange in the future we might allow a listener to be register
     * and do this in/as background task.
     *
     * @param Transactions a list of transaction from we will trust YOU WILL NOT
     * MODIFY AND YOU ARE THE OWNER if NOT please make sure to pass an
     * un-modifiable COPY of the list using {@link Collections#unmodifiableList}
     * (unmodifiableList does not copy the values so you need to ensure is a
     * copy where YOU HAVE THE CONTROL) i hope this is clear...
     * @param Authoritys<AuthorityHash, X509Certificate> a map containing the
     * Hash of the signature of the certificate and the certificate itself.
     * TODO: change the return to a code to support notification of different
     * types of results
     * @return true if able to submit the new block. false if a double spend, or
     * the new block is deny to be added.
     */
    @SubjectToChange
    public synchronized BlockchainResponse PropouseBlock(final List<T> Transactions, Map<Hash, X509Certificate> Authoritys) throws InterruptedException {
        if (Objects.requireNonNull(Transactions, "Transactions Cannot be null").size() != ammount_transactions) {
            throw new IndexOutOfBoundsException(
                    String.format("The Transaction Amount is not allowed requires:%d", ammount_transactions));
        }
        if (Objects.requireNonNull(Authoritys).size() <= 0) {
            throw new IndexOutOfBoundsException("we need at least one Authority to be Register for this block");
        }
        final Block propoused_block;
        final Block previousBlock =UderlineChain.isEmpty()?null:UderlineChain.getLast();
        //***********************************TODO: CONSIDER: *********************/
        //I am a idiot, fortunate this was documented  this comment happen on the test up... so we do need to waste performance cuz i am a idiot
        // and if i fall here no doubt a bunch more of idiots will NOT send a immutable list... better safe than sorry.
        //***************************************************************
        // make a copy of Transactions here if in the future the caller misbehave. i
        // don't trust them but doing a copy is
        // costly and we do not desire to add extra overhead to the code.
        // check if there is a duplicate transaction on the last N blocks on the chain.
        //do a shallow copy
        List<T> Transactions2=new ArrayList<>(Transactions);
       if (Objects.isNull(previousBlock)) {
            propoused_block = new Block(GEN_PRE_HASH, Transactions2, Authoritys.keySet().toArray(new Hash[0]));
        } else {
            List<? extends ITransaction> invalid = DoubleSpendDetected(Transactions2);
            if (invalid!=null &&!invalid.isEmpty()) {
                return BlockchainResponse.DOUBLESPENT(invalid);
            }
            propoused_block = new Block(previousBlock, Transactions2, Authoritys.keySet().toArray(new Hash[0]));
        }
        // at this point the block exists but is not trusted by the authorities nor have
        // they made aware of their existence.
        // therefore we might here add a thread and do the BFT and return with the
        // promise of notify our caller. at later date <kek>
        // or stall the code and send the request on THIS thread.
        // note any other thread that MIGHT be requesting to add blocks will be stalled
        // awaiting.
        // we will take the later as is easy and works for this POC. however i consider
        // the former to be a good option. but require more code to check
        // no duplicates are begin requested
        //propoused_block.setTimeStap(Optional.of(java.time.Clock.systemUTC().instant()));
        propoused_block.setTimeStap(Optional.empty());
        getCommunicationClient().pbftClient_execution(propoused_block);
        if (!CommunicationClient.isCompleted()) {
            throw new InterruptedException("it seems something went wrong.");
        }
        if (!CommunicationClient.getResult().RelatedBlock().equals(propoused_block.getHash().toString())
                || getCommunicationClient().getResult().result() == null
                || getCommunicationClient().getResult().result().equals("")) {
            return BlockchainResponse.REJECTED_NOTACCEPTED;
        }
        //confirm signature
        ConsensusResult testResult = getCommunicationClient().getResult();

        //for this POC we will accept for ANY sucess to pass. however after we upgrade the 
        //PBFT we should check all auths to sign. (PAXOS)? 
        //we should CHECK that the required signature matches but for this 
        /*enhancement: 
            lets take a look to whom responded (look at the signature) and then check it it match
            if it does not o MAYBE it might be from someone else? so we will loop. 
            this should be look into a removed as i dont think looping all certificates is required. 
         */
        X509Certificate cert = null;
        if (testResult.getAcceptersignature() != null) {
            cert = Authoritys.get(Hash.of(ByteUtils.HexToBytes(testResult.getAcceptersignature())));
        }
        boolean found = false;
        if (cert != null) {
            found = isValidSignature(cert, propoused_block, testResult);
        }
        //ok so it was not found or the signer does not match...
        //did the signer miss the mark? no problem lets loop and see. 
        if (!found) {
             LoggingHelper.getDefaultLogger().log(Level.WARNING,"no Register Signer Detected! looping.");
            for (X509Certificate Authoritycert : Authoritys.values()) {
                found = isValidSignature(Authoritycert, propoused_block, testResult);
                if (found) {
                    //goit exit the loop
                    break;
                }
            }
            if (!found) {
                return BlockchainResponse.REJECTED_INVALIDSIGN;
            }
        }
        //ok, lets add the acceptance signature to the block and add it. 
        propoused_block.registerAuthoritativeSignature(to_byte_buff(testResult.getAcceptanceSignaturebytes()));
        //yay accepted ok. lets add it into the chain and return true. 
        UderlineChain.add(propoused_block);
        //great now is part of the chain. 
        //at this point if there are other nodes. and they accepted they have or should had accepted it and add is just like we do. 
        return BlockchainResponse.ACCEPTED;
    }

    private synchronized List<? extends ITransaction> DoubleSpendDetected(List<T> transactions) {
        Block block;
        Iterator<Block> blockIterator = UderlineChain.descendingIterator();
        while (blockIterator.hasNext()) {
            // TODO: add a level of tolerance for example after N blocks do not check accept
            // the risk.
            block = blockIterator.next();
            List<? extends ITransaction> tmps = block.containsAny(transactions);
            if (tmps != null && !tmps.isEmpty()) {
                return tmps;
            }
        }
        return null;
    }

    /**
     * returns a really Shallow Copy of the block chain. TODO: use a REAL copy
     * of the chain to dis-allow ANY kind of Modifications
     *
     * @return
     */
    public List<Block> getBlocklistCopy() {
        return Collections.unmodifiableList(UderlineChain);
    }

    /**
     * due the fact the blocks have a FEW items that can be changed for example
     * add values to the signatures. change or add time.... also we want to
     * ensure this CHAIN IS ABOSLUTELY unchangeable. to do so we better just
     * return a Copy of the data. so we can rest asure due the nature of some of
     * the data we stored we don't desire this to be edited later on
     *
     * @param Object_to_Clone the list to clone.
     * @return
     */
    public List<Block> getDeepImmutableClone(List<Block> Object_to_Clone) {
        return Object_to_Clone.stream().map(Block::getaDeepcopy).collect(Collectors.toUnmodifiableList());
    }

    /**
     * this method is not implement we need a method to persist and send info to
     * a database OR other storage.
     *
     * @param StoragePath
     */
    @NotImplementedYet
    public void Persist(Path StoragePath) {
         LoggingHelper.getDefaultLogger().log(Level.SEVERE,"Calling something that is not ready");
        throw new UnsupportedOperationException("Not Avaiable yet!");
    }

    /**
     * @return the CommunicationClient
     */
    public pbftClient getCommunicationClient() {
        return CommunicationClient;
    }

    private boolean isValidSignature(X509Certificate cert, Block propoused_block, ConsensusResult testResult) {
        boolean isvalid = false;
        Provider Provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        //this should not be required. however if it happends lets do the work the caller should had done
        if (Provider == null) {
            //a exception might happend here... extremely unlikely  tho but may happend. 
            //a class exception that BouncyCastle was not loaded. if it happends well game over man. 
            Provider = new BouncyCastleProvider();
            Security.addProvider(Provider);
        }
        Signature RSASHA;
        try {
            RSASHA = SignatureProvider.VerifyData(Provider, Optional.empty(), cert);
            RSASHA.update(propoused_block.getHash().toByteArray());
            isvalid = RSASHA.verify(testResult.getAcceptanceSignaturebytes());
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            LoggingHelper.getDefaultLogger().log(Level.SEVERE, null, ex);
        }
        return isvalid;
    }

}
