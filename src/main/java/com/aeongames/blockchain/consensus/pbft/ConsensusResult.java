/*
 *
 *   Copyright © ï¿½ 2019 Eduardo Vindas Cordoba. All rights reserved.
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

import com.aeongames.blockchain.base.common.ByteUtils;
import java.io.Serializable;
import java.util.Objects;

/**
 * this class is a simple POJO that contains and, transports and represent the
 * AcceptanceSignature of consensus information
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class ConsensusResult implements Serializable, Cloneable {

    private static final long serialVersionUID = 3308638179879024814L;
    /**
     * the block that was accepted/rejected
     */
    private final String BlockHash,
            /**
             * the acceptance signature
             */
            AcceptanceSignature,
            /**
             * whom did the signning.
             */
            SignatureOwner;

    /**
     * sets the value of a AcceptanceSignature of the analyze of a acceptance or
     * denial of a new block
     *
     * @param signer the one who signed this block
     * @param BlockHash the hash of the signed block
     * @param result the signature of this block
     */
    public ConsensusResult(String signer, String BlockHash, String result) {
        this.BlockHash = BlockHash;
        this.AcceptanceSignature = result;
        this.SignatureOwner = signer;
    }

    /**
     *
     * returns the AcceptanceSignature of the analysis of consensus
     *
     * @return the AcceptanceSignature of analysis
     */
    public String result() {
        return this.AcceptanceSignature;
    }

    /**
     *
     * returns the AcceptanceSignature of the analysis of consensus
     *
     * @return the AcceptanceSignature of analysis
     */
    public String getAcceptanceSignature() {
        return this.AcceptanceSignature;
    }

    /**
     *
     * returns the AcceptanceSignature of the analysis of consensus
     *
     * @return the AcceptanceSignature bytes of analysis
     */
    public byte[] getAcceptanceSignaturebytes() {
        if (this.AcceptanceSignature != null) {
            return ByteUtils.HexToBytes(this.AcceptanceSignature);
        }else{
            return null;
        }
    }

    public String RelatedBlock() {
        return BlockHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        ConsensusResult that = (ConsensusResult) o;
        return this.AcceptanceSignature.equals(that.AcceptanceSignature) && 
               this.BlockHash.equals(that.BlockHash) && 
                this.SignatureOwner.equals(that.SignatureOwner) ;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.AcceptanceSignature);
        hash = 97 * hash + Objects.hashCode(this.BlockHash);
        hash = 97 * hash + Objects.hashCode(this.SignatureOwner);
        return hash;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        //super.clone();//--> odd now java recomends using this unstable shit? wtf!i will need to read the 
        //JDK... OPENJDK implementation to make sure is safe. for the time begin do a manual copy. 
        return copyof();
    }

    protected ConsensusResult copyof() {
        return new ConsensusResult(this.SignatureOwner, this.BlockHash, this.AcceptanceSignature);
    }

    public String getAcceptersignature() {
       return  this.SignatureOwner;
    }

}
