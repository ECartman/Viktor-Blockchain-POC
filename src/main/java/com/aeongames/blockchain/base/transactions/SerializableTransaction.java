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
package com.aeongames.blockchain.base.transactions;

import com.aeongames.blockchain.TODOS.NotImplementedYet;
import java.nio.ByteBuffer;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class SerializableTransaction implements ITransaction {

    private byte TransactionBytes[];
    private byte transactionsignatures[][];

    @Override
    public byte[] getTransactionBytes() {
        return TransactionBytes;
    }

    /**
     * @param TransactionBytes the TransactionBytes to set
     */
    public void setTransactionBytes(byte[] TransactionBytes) {
        this.TransactionBytes = TransactionBytes;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Arrays.hashCode(this.TransactionBytes);
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
        final SerializableTransaction other = (SerializableTransaction) obj;
        return Arrays.equals(this.TransactionBytes, other.TransactionBytes);
    }

    @Override
    public ITransaction requestclone() {
        SerializableTransaction tmp = new SerializableTransaction();
        tmp.TransactionBytes = Arrays.copyOf(this.TransactionBytes, 0);
        return tmp;
    }

    @Override
    public boolean canCertificateConfirm() {
        return transactionsignatures != null && transactionsignatures.length > 0;
    }

    @Override
    @NotImplementedYet
    public X509Certificate[] getConfirmationCertificates() {
        return null;//TODO. the signature Certificates might be required to persist
    }

    @Override
    public ByteBuffer getSignedTransaction(int index) {
        if (transactionsignatures != null && transactionsignatures.length > index) {
            return ByteBuffer.wrap(transactionsignatures[index]).asReadOnlyBuffer();
        }
        throw new IndexOutOfBoundsException("the index request is beyond the bounts or the signature object is null");
    }

}
