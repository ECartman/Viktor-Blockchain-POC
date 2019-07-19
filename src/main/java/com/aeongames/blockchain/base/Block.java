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

import static com.aeongames.blockchain.base.common.ByteUtils.to_byte_buff;
import com.aeongames.blockchain.base.MerkleTree.MerkleTree2;
import com.aeongames.blockchain.base.common.Hash;
import com.aeongames.blockchain.base.transactions.ITransaction;
import com.aeongames.logger.LoggingHelper;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class Block {

    public static final int CURRENTVERSION = 0b01;//32 bits should ve enought to represent a version

    /**
     * an array that contains the current existent Authorities.
     * this is a hash of a certificate
     */
    private final Hash RegisteredAuthorities[];
    /**
     * this buffer contains the block proof of authority signature (the
     * authority that approve this block) and or, the list of accepting
     * signatures. to meet BFT we need at least 1/3 of signatures. but that is
     * not relevant here have it on mind tho.
     */
    private final ByteBuffer blockAuthoritativeSignature[];
    /**
     * this Merkle tree is the object that will be created in order to calculate
     * test, confirm the block hash and ensure all transactions on the list are
     * accurate. and has not been edited.
     */
    private final MerkleTree2 BlockHash;
    /**
     * the previous Hash for the last block in the chain. remember that you need
     * to be cautions a fork on the list MIGHT exists.
     */
    private final Hash previousHash;
    /**
     * the transactions that are part of this block.
     */
    private final List<? extends ITransaction> BlockTransactions;
    /**
     * this is a timestamp of the creation time of the block.
     */
    private long timeStamp = -1; //as number of milliseconds since 1/1/1970 (epoch).
    /**
     * this value will store the ID or index of the block in relation to the
     * Genesis block (that is consider to be index 0)
     */
    private final BigInteger index;
    /**
     * version of this block relative to the BlockChain. this is intended to
     * ensure data is know on the way it works and the way data is presented and
     * ensure what metadata might be present.
     */
    private final int Version;

    /**
     * the default private contructor this contructor is not intended to be
     * called. now Java by itself might take care of this however there is
     * something called "reflections" that can force the default contructor to
     * be called. it is important for us to specify this contructor and send a
     * error. as we dont desire new instance with "default" values on heap.
     * therefore we define and throw a exeption.
     */
    private Block() {
        throw new RuntimeException("Illegal Contruction");
    }

    /**
     * creates a new instance of a Block.the block will target the provided
     * previousBlock the previousBlock will not be used besides of gathering
     * <code>previousBlock.getHash()</code> the timestamp will not be set at
     * this time. BE CAREFUL this constructor is intended for new blocks!
     *
     * @param previousBlock the previous block to link to this block
     * @param Transactions the transactions for this block
     * @param Authorities the authorities that CAN sign blocks at the point of
     * creation of this block this value will copied inside as we DO NOT want a
     * reference as that might open a vulnerability. however there is a trade
     * off. (performance due copy)
     */
    public Block(final Block previousBlock, final List<? extends ITransaction> Transactions, Hash Authorities[]) {
        this.Version = CURRENTVERSION;
        this.timeStamp = -1;
        Objects.requireNonNull(previousBlock.getIndex(), "The previous block is not Commited or invalid This block cannot be created from a uncommited or invalid block");
        index = previousBlock.getIndex().add(BigInteger.ONE);
        this.BlockTransactions = Objects.requireNonNull(Transactions, "the Merkle Tree Cannot be Null");
        if (BlockTransactions.isEmpty()) {
            throw new IllegalArgumentException("the transaction lust is empty");
        }
        previousHash = Objects.requireNonNull(previousBlock, "the Previous Block Cannot be null").getHash();
        if (previousHash.equals(Hash.INVALID)) {
            throw new IllegalArgumentException("The previousHash is invalid");
        }
        BlockHash = new MerkleTree2(Transactions);
        RegisteredAuthorities = Arrays.copyOf(Objects.requireNonNull(Authorities, "the Authorities Cannot be null"), Authorities.length);
        blockAuthoritativeSignature = new ByteBuffer[RegisteredAuthorities.length];
    }

    /**
     * creates a new instance of a Block.the block will contain the information
     * for the loaded block
     * <strong>intended to create the Genesis block</strong>
     *
     * @param previousBlock the previous block to load the previous hash.
     * @param Transactions the transactions of this block
     * @param Authorities the authorities that CAN sign blocks at the point of
     * creation of this block
     */
    public Block(final Hash previousBlock, final List<? extends ITransaction> Transactions, final Hash Authorities[]) {
        this.Version = CURRENTVERSION;
        index = BigInteger.ZERO;//--> this is for a gensis block! 
        this.timeStamp = -1;
        this.BlockTransactions = Objects.requireNonNull(Transactions, "the Merkle Tree Cannot be Null");
        if (BlockTransactions.isEmpty()) {
            throw new IllegalArgumentException("the transaction lust is empty");
        }
        previousHash = Objects.requireNonNull(previousBlock, "the Previous Block Hash Cannot be null");
        if (previousHash.equals(Hash.INVALID)) {
            throw new IllegalArgumentException("The previousHash is invalid");
        }
        BlockHash = new MerkleTree2(Transactions);
        RegisteredAuthorities = Arrays.copyOf(Objects.requireNonNull(Authorities, "the Authorities Cannot be null"), Authorities.length);
        blockAuthoritativeSignature = new ByteBuffer[RegisteredAuthorities.length];
    }

    /**
     * creates a new instance of a Block.the block will contain the information
     * for the loaded block
     * <strong>this is intended to load block from storage</strong>
     *
     * @param previousBlock the previous block to load the previous hash.
     * @param Transactions the transactions of this block
     * @param PreviousTime the time when this block was created
     * @param version the version of this block (the version of the chain that
     * @param Authorities the authorities that CAN sign blocks at the point of
     * creation of this block created this block)
     */
    public Block(Block previousBlock, final List<? extends ITransaction> Transactions, final Hash Authorities[], final long PreviousTime, final int version) {
        if (version <= -1) {
            throw new IllegalArgumentException("The version is invalid");
        }
        this.Version = version;
        Objects.requireNonNull(previousBlock.getIndex(), "The previous block is not Commited or invalid This block cannot be created from a uncommited or invalid block");
        index = previousBlock.getIndex().add(BigInteger.ONE);
        this.timeStamp = PreviousTime;// this might change to a more acurrate time representation.
        this.BlockTransactions = Objects.requireNonNull(Transactions, "the Transactions CANNOT be Null");
        if (BlockTransactions.isEmpty()) {
            throw new IllegalArgumentException("the transaction list is empty");
        }
        this.previousHash = Objects.requireNonNull(previousBlock, "the Previous Block Hash Cannot be null").getHash();
        if (previousHash.equals(Hash.INVALID)) {
            throw new IllegalArgumentException("The previousHash is invalid");
        }
        this.BlockHash = new MerkleTree2(Transactions);
        RegisteredAuthorities = Arrays.copyOf(Objects.requireNonNull(Authorities, "the Authorities Cannot be null"), Authorities.length);
        blockAuthoritativeSignature = new ByteBuffer[RegisteredAuthorities.length];
    }

    /**
     * creates a new instance of a Block.the block will contain the information
     * for the loaded block
     * <strong>this is intended to load block from storage</strong>
     * <strong>TESTING or cloning ONLY</strong>
     *
     * @param previousHash the previous block to load the previous hash.
     * @param Transactions the transactions of this block
     * @param PreviousTime the time when this block was created
     * @param version the version of this block (the version of the chain that
     * @param Authorities the authorities that CAN sign blocks at the point of
     * creation of this block created this block)
     */
    Block(Hash previousHash, BigInteger index, final List<? extends ITransaction> Transactions, final Hash Authorities[], final long PreviousTime, final int version) {
        if (version <= -1) {
            throw new IllegalArgumentException("The version is invalid");
        }
        this.Version = version;
        this.index = Objects.requireNonNullElse(index, BigInteger.ZERO);
        this.timeStamp = PreviousTime;// this might change to a more accurate time representation.
        this.BlockTransactions = Objects.requireNonNull(Transactions, "the Transactions CANNOT be Null");
        if (BlockTransactions.isEmpty()) {
            throw new IllegalArgumentException("the transaction list is empty");
        }
        this.previousHash = Objects.requireNonNull(previousHash, "the Previous Block Hash Cannot be null");
        if (previousHash.equals(Hash.INVALID)) {
            throw new IllegalArgumentException("The previousHash is invalid");
        }
        this.BlockHash = new MerkleTree2(Transactions);
        RegisteredAuthorities = Arrays.copyOf(Objects.requireNonNull(Authorities, "the Authorities Cannot be null"), Authorities.length);
        blockAuthoritativeSignature = new ByteBuffer[RegisteredAuthorities.length];
    }

    @Override
    public String toString() {
        StringBuilder auths = new StringBuilder(), authssigns = new StringBuilder();
        for (int i = 0; i < RegisteredAuthorities.length; i++) {
            auths.append("\n\t\tAuthority::");
            auths.append(i);
            auths.append("{");
            auths.append(RegisteredAuthorities[i].toString()).append("}\n\t");
            authssigns.append("\n\t\tsignature::");
            authssigns.append(i);
            authssigns.append("{");
            authssigns.append(blockAuthoritativeSignature[i] == null ? "<Null signature>" : Hash.of(blockAuthoritativeSignature[i]).toString());
            authssigns.append("}\n\t");
        }
        String info = String.format("Block{"
                + "\n\timeStamp=%s"
                + "\n\tindex=%s"
                + "\n\ttimestamp=%s"
                + "\n\tPreviousHash=%s"
                + "\n\tBlockHash=%s"
                + "\n\tBlockversion=%s"
                + "\n\tRegistered Authorities={%s}"
                + "\n\tSignatures={%s}"
                + "\n\tTransactions={%s}"
                + "}",
                timeStamp,
                index,
                getTimeStamp(),
                previousHash.toString(),
                BlockHash.getRootHash().toString(),
                Version,
                auths.toString(),
                authssigns.toString(),
                TransactionHelper.Print(getBlockTransactions()));

        return info;
    }

    /**
     * calculate a JAVA hash for this object. PLEASE note this is NOT the hash
     * for this block this is the hash that is used internally for java to store
     * and tell one object from another NO NOT USE as part of the Block chain as
     * this is vulnerable hash (merely 32 bit) this is susceptible to birthday
     * attack
     *
     * PLEASE AVOID USING THIS METHOND UNLESS IS INTEDED FOR HASHTABLES OR
     * COLLECTIONS
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hash(this.BlockHash, this.previousHash, this.getBlockTransactions());
        hash = 47 * hash + Arrays.hashCode(RegisteredAuthorities);
        hash = 47 * hash + Objects.hashCode(index);
        hash = 47 * hash + (int) (this.getTimeStamp() ^ (this.getTimeStamp() >>> 32));
        hash = 47 * hash + this.getVersion();
        return hash;
    }

    /**
     * ensures 2 blocks are not equals. NOTE: the equals takes the hashes and
     * compares them and also the transactions it is possible for 2 blocks to
     * have the same transactions and be different. on this cases YOU MUST CHECK
     * THE INDIVIDUAL TRANSACTIONS AGAINST The hold block and the chain itself.
     *
     * @param o
     * @return
     */
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (Objects.isNull(o) || getClass() != o.getClass()) {
            return false;
        }
        final Block oblock = (Block) o;

        return Objects.equals(BlockHash, oblock.BlockHash)
                && Objects.equals(previousHash, oblock.previousHash)
                && Objects.equals(getBlockTransactions(), oblock.getBlockTransactions());
        //in reality the blocks are diferent one from the other by the others fields time stap is irrelevant. 
        //&& timeStamp == oblock.timeStamp;
    }

    /**
     * gets the index of this block on the BlockChain if already commit to the
     * chain. if not this value will be null.
     *
     * @return
     */
    public final BigInteger getIndex() {
        return index;
    }

    /**
     * gets the TimeStamp for this block if already assign otherwise will return
     * -1
     *
     * @return
     */
    public final long getTimeStamp() {
        return timeStamp;
    }

    /**
     * returns the Block hash. that consist of hashing block timestamp. index,
     * version, previousHash And the merkle tree root.
     *
     * @throws NullPointerException if the previous hash is null OR index is
     * null or the merkle tree is null. (unlikely)
     * @throws RuntimeException if hash underline function is not supported
     * @return
     */
    public final Hash getHash() {
        ArrayList<Hash> tmp = new ArrayList<>(6);//at least 6
        boolean noerrs = tmp.addAll(Arrays.asList(RegisteredAuthorities))
                && tmp.add(Hash.of(to_byte_buff(timeStamp)))
                && tmp.add(Hash.of(to_byte_buff(index.toByteArray())))
                && tmp.add(Hash.of(to_byte_buff(Version)))
                && tmp.add(previousHash)
                && tmp.add(BlockHash.getRootHash());
        if (!noerrs || previousHash == null) {
            throw new NullPointerException("Error calculating Hash");
        }
        return Hash.merge(tmp.toArray(new Hash[0]));
    }

    /**
     * returns the previous block hash
     *
     * @return
     */
    public final Hash getPreviousHash() {
        return previousHash;
    }

    /**
     * @return the Version
     */
    public final int getVersion() {
        return Version;
    }

    /**
     * TODO: consider returning a copy not a reference.
     *
     * @return the RegisteredAuthorities
     */
    public final Hash[] getRegisteredAuthorities() {
        return RegisteredAuthorities;
    }

    /**
     * TODO: consider returning a copy not a reference.
     *
     * @return the blockAuthoritativeSignature
     */
    public final ByteBuffer[] getBlockAuthoritativeSignature() {
        return blockAuthoritativeSignature;
    }

    /**
     * we assume the signature is valid we will just add it. we also assume this
     * is a safe ByteBuffer (read only one)
     *
     * @param signature
     * @return
     */
    public final boolean registerAuthoritativeSignature(ByteBuffer signature) {
        for (int i = 0; i < blockAuthoritativeSignature.length; i++) {
            if (blockAuthoritativeSignature[i] == null || blockAuthoritativeSignature[i].equals(signature)) {
                blockAuthoritativeSignature[i] = signature;
                return true;
            }
        }
        //the list is alredy filled. 
        return false;
    }

    /**
     * TODO: deep copy! 
     * shallow but non modifiable copy 
     * creates and provides a copy of the transactions of this block
     * 
     *
     * @return the BlockTransactions
     */
    public List<? extends ITransaction> getBlockTransactions() {
        // this does the copy... shallow but a copy... 
        List<? extends ITransaction> returnlist = Collections.unmodifiableList(BlockTransactions);
        return returnlist;
    }

    Hash getMerkrootHash() {
        return BlockHash.getRootHash();
    }

    public List<? extends ITransaction> containsAny(List<? extends ITransaction> transactions) {
        List<? extends ITransaction> foundTransactions
                = transactions.stream()
                        .filter(trans -> BlockTransactions.stream()
                        .anyMatch(blocktran -> ((ITransaction) blocktran).equals(trans)))
                        .collect(Collectors.toList());
        return foundTransactions;
    }

    /**
     * sets the timestamp. 
     * note that the provided value SHOULD be 
     * @param timestamp 
     */
    public void setTimeStap(Optional<Instant> timestamp) {
        if (timeStamp == -1) {
            if (Objects.requireNonNullElse(timestamp, Optional.empty()).isPresent()) {
                timeStamp = timestamp.get().getEpochSecond();
            }else{
                //asume whomever call use with null or empty want to setup the current time 
                timeStamp=java.time.Clock.systemUTC().millis();
            }
        }else{
            LoggingHelper.getDefaultLogger().log(Level.WARNING,"the timestamp is alredy set. someone called setTimeStap at a wrong point.");
        }
    }

    /**
     * makes a Deep immutable Copy of this block. althought some values are reference. the 
     * non final ones are deep copied (basically the transaction) HOWEVER 
     * please be aware that the transactions copy only contain the Data and nothing more. 
     * for "equals" please use the transactions themselves and compare to the external ones as
     * the external ones might not equalize with this deep copy. 
     * @return 
     * CAREFUL this WILL return a list of  List<anonymus ITransaction>
     * @see ITransaction getImmutableClone() so you CANNOT be warrantee this will work as you might expect
     */
    Block getaDeepcopy() {
     ArrayList<ITransaction> clonedtransaction=new ArrayList<>(BlockTransactions.size());
     BlockTransactions.forEach((BlockTransaction) -> {
         clonedtransaction.add(BlockTransaction.getImmutableClone());
        });
      Block cloned= new Block( previousHash, index, clonedtransaction, RegisteredAuthorities, timeStamp, Version);
        for (ByteBuffer byteBuffer : cloned.getBlockAuthoritativeSignature()) {
            cloned.registerAuthoritativeSignature(byteBuffer);
        }
        return cloned;
    }
}
