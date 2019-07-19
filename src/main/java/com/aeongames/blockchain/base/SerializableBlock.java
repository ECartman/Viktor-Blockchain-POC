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

import com.aeongames.blockchain.base.common.Hash;
import com.aeongames.blockchain.base.transactions.ITransaction;
import com.aeongames.blockchain.base.transactions.SerializableTransaction;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * a Block that can be recorded and saved. this is intended for transportation
 * between servers.
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class SerializableBlock {

    /**
     * @return the RelatedBlock
     */
    public Block getRelatedBlock() {
        return RelatedBlock;
    }

    /**
     * @param RelatedBlock the RelatedBlock to set
     */
    public void setRelatedBlock(Block RelatedBlock) {
        this.RelatedBlock = RelatedBlock;
    }

    public static final int CURRENTVERSION = 0b01;//32 bits should ve enought to represent a version

    /**
     * an array that contains the current existent Authorities.
     */
    private Hash RegisteredAuthorities[];
    /**
     * this buffer contains the block proof of authority signature (the
     * authority that approve this block) and or, the list of accepting
     * signatures. to meet BFT we need at least 1/3 of signatures. but that is
     * not relevant here have it on mind tho.
     */
    private ByteBuffer blockAuthoritySignature[];
    /**
     * this Merkle ROOT to confirm the block hash and ensure all transactions on
     * the list are accurate. and has not been edited.
     */
    private Hash BlockHash;
    /**
     * the previous Hash for the last block in the chain. remember that you need
     * to be cautions a fork on the list MIGHT exists.
     */
    private Hash previousHash;
    /**
     * the transactions that are part of this block.
     */
    private ArrayList<SerializableTransaction> BlockTransactions;
    /**
     * this is a timestamp of the creation time of the block.
     */
    private long timeStamp = -1; //as number of milliseconds since 1/1/1970 (epoch).
    /**
     * this value will store the ID or index of the block in relation to the
     * Genesis block (that is consider to be index 0)
     */
    private BigInteger index;
    /**
     * version of this block relative to the BlockChain. this is intended to
     * ensure data is know on the way it works and the way data is presented and
     * ensure what metadata might be present.
     */
    private int Version;

    @Nullable
    private transient Block RelatedBlock;
    
    /**
     * @return the RegisteredAuthorities
     */
    public Hash[] getRegisteredAuthorities() {
        return RegisteredAuthorities;
    }

    /**
     * @param RegisteredAuthorities the RegisteredAuthorities to set
     */
    public void setRegisteredAuthorities(Hash[] RegisteredAuthorities) {
        this.RegisteredAuthorities = RegisteredAuthorities;
    }

    /**
     * @return the blockAuthoritativeSignature
     */
    public ByteBuffer[] getBlockAuthoritativeSignature() {
        return blockAuthoritySignature;
    }

    /**
     * @param blockAuthoritativeSignature the blockAuthoritativeSignature to set
     */
    public void setBlockAuthoritativeSignature(ByteBuffer[] blockAuthoritativeSignature) {
        if(RegisteredAuthorities!=null&&RegisteredAuthorities.length==blockAuthoritativeSignature.length) {
            this.blockAuthoritySignature = blockAuthoritativeSignature;
        }else{
            throw new IndexOutOfBoundsException("size does not match");
        }
    }

    /**
     * @return the BlockHash
     */
    public Hash getBlockHash() {
        return BlockHash;
    }

    /**
     * @param BlockHash the BlockHash to set
     */
    public void setBlockHash(Hash BlockHash) {
        this.BlockHash = BlockHash;
    }

    /**
     * @return the previousHash
     */
    public Hash getPreviousHash() {
        return previousHash;
    }

    /**
     * @param previousHash the previousHash to set
     */
    public void setPreviousHash(Hash previousHash) {
        this.previousHash = previousHash;
    }

    /**
     * @return the BlockTransactions
     */
    public ArrayList<SerializableTransaction> getBlockTransactions() {
        return BlockTransactions;
    }

    /**
     * @param BlockTransactions the BlockTransactions to set
     */
    public void setBlockTransactions(ArrayList<SerializableTransaction> BlockTransactions) {
        this.BlockTransactions = BlockTransactions;
    }

    /**
     * @return the timeStamp
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @return the index
     */
    public BigInteger getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(BigInteger index) {
        this.index = index;
    }

    /**
     * @return the Version
     */
    public int getVersion() {
        return Version;
    }

    /**
     * @param Version the Version to set
     */
    public void setVersion(int Version) {
        this.Version = Version;
    }

    public static final SerializableBlock ToSerializableBlock(Block block) {
        SerializableBlock ser = new SerializableBlock();
        ser.setVersion(block.getVersion());
        ser.setIndex(block.getIndex());
        ser.setTimeStamp(block.getTimeStamp());
        ser.setRegisteredAuthorities(block.getRegisteredAuthorities());
        ser.setPreviousHash(block.getPreviousHash());
        List<? extends ITransaction> tmp = block.getBlockTransactions();
        ArrayList<SerializableTransaction> BlockTransactions = new ArrayList<>(tmp.size());
        tmp.forEach((blockTransaction) -> {
            SerializableTransaction t = new SerializableTransaction();
            byte[] arr = new byte[blockTransaction.getTransactionByteBuffer().remaining()];
            blockTransaction.getTransactionByteBuffer().get(arr);
            t.setTransactionBytes(arr);
            BlockTransactions.add(t);
        });
        ser.setBlockAuthoritativeSignature(block.getBlockAuthoritativeSignature());
        ser.setBlockTransactions(BlockTransactions);
        //consider if this is ok to transport...
        //ser.setBlockHash(block.getMerkrootHash());
        //this is a composed value and is a hash o several values. this one is intended for verification of the transaction.
        //tho it can be composed.
        ser.setBlockHash(block.getHash());
        ser.setRelatedBlock(block);
        return ser;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.BlockHash);
        hash = 37 * hash + Objects.hashCode(this.previousHash);
        hash = 37 * hash + Objects.hashCode(this.BlockTransactions);
        hash = 37 * hash + (int) (this.timeStamp ^ (this.timeStamp >>> 32));
        hash = 37 * hash + Objects.hashCode(this.index);
        hash = 37 * hash + this.Version;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SerializableBlock other = (SerializableBlock) obj;
        if (this.timeStamp != other.timeStamp) {
            return false;
        }
        if (this.Version != other.Version) {
            return false;
        }
        if (!Objects.equals(this.BlockHash, other.BlockHash)) {
            return false;
        }
        if (!Objects.equals(this.previousHash, other.previousHash)) {
            return false;
        }
        if (!Objects.equals(this.BlockTransactions, this.BlockTransactions)) {
            return false;
        }
        return Objects.equals(this.index, other.index);
    }
    
    

    /**
     * intended for confirmation or testing. for production needs to be checked
     * against the chain.
     *
     * @param block
     * @return
     */
    public static final Block DeSerializeBlock(SerializableBlock block) {
        Block b;
        b = new Block(block.getPreviousHash(), block.getIndex(),
                //this one intended to do a copy as we are not sure of how secure is use THE list
                new ArrayList<>(block.getBlockTransactions()),
                block.getRegisteredAuthorities(),
                block.getTimeStamp(),
                block.getVersion());
        if(block.getBlockAuthoritativeSignature()!=null) {
            for (ByteBuffer byteBuffer : block.getBlockAuthoritativeSignature()) {
                b.registerAuthoritativeSignature(byteBuffer);
            }
        }
        if(!block.BlockHash.equals(b.getHash())){
            //LOG BLOCK IS CORRUPT
        }
        return b;
        //Block(Block previousBlock, final ArrayList<? extends ITransaction> Transactions, final Hash Authorities[], final long PreviousTime, final int version)
    }

}
