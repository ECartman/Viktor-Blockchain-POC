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

import com.aeongames.blockchain.base.transactions.ITransaction;
import java.util.List;
import java.util.Objects;

public final class BlockchainResponse {

    public static final BlockchainResponse REJECTED_NOTACCEPTED = new BlockchainResponse(Response.REJECTED_NOTACCEPTED),
            REJECTED_INVALIDSIGN = new BlockchainResponse(Response.REJECTED_INVALIDSIGN),
            ACCEPTED = new BlockchainResponse(Response.ACCEPTED),
            DOUBLESPENT = new BlockchainResponse(Response.DOUBLESPENT);

    static final BlockchainResponse DOUBLESPENT(final List<? extends ITransaction> RelatedTransactions){
       return  new BlockchainResponse(Response.DOUBLESPENT,RelatedTransactions);
    }
    
    private final List<? extends ITransaction> RelatedTransactions;
    private final Response Myresolution;

    private BlockchainResponse(Response Response) {
        RelatedTransactions = null;
        Myresolution = Response;
    }

    private BlockchainResponse() {
        throw new IllegalAccessError();
    }

    private BlockchainResponse(Response Response, List<? extends ITransaction> relatedt) {
        RelatedTransactions = relatedt;
        Myresolution = Response;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.getResolution());
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
        final BlockchainResponse other = (BlockchainResponse) obj;
        return this.getResolution() == other.getResolution();
    }
    
    

    public enum Response {
        REJECTED_NOTACCEPTED,
        REJECTED_INVALIDSIGN,
        ACCEPTED,
        DOUBLESPENT;
    }

    /**
     * @return the Myresolution
     */
    public Response getResolution() {
        return Myresolution;
    }

    /**
     * @return the RelatedTransactions
     */
    public List<? extends ITransaction> getRelatedTransactions() {
        return RelatedTransactions;
    }
}
