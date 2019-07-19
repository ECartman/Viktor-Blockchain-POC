/*
 * 
 *   Copyright � � 2019 Eduardo Vindas Cordoba. All rights reserved.
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

import java.nio.ByteBuffer;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public interface ITransaction {

    /**
     * gather a ByteBuffer that underline contains the Bytes of the object this
     * method is the prefer method to gather the Object bytes note
     * getTransactionBytes should never be used besides by this method as this
     * method should be safer and more efficient.
     *
     * @return a(hopefully ReadOnlyBuffer) that wraps the bytes for this
     * transaction
     */
    public default ByteBuffer getTransactionByteBuffer() {
        ByteBuffer bytes = ByteBuffer.wrap(getTransactionBytes()).asReadOnlyBuffer();
        bytes.rewind();
        return bytes;
    }

    /**
     * gathers all the information of this transaction that is relevant to
     * ensure is a Transaction. to be used to be digested. this is assumed the
     * return information is IMMUTABLE and cannot be changed. this function is
     * intended to return a UNIQUE byte array that makes this transaction unique
     * and the value returned is not to be reused ANYWARE else other than to
     * calculate the transaction Hash. and the return value is to be a copy and
     * not used or modified in ANY WAY.
     *
     * internal use only DO NOT USE! fo anything else if not for hash
     * calculation please use getTransactionByteBuffer instead
     *
     * @return the bytes for this Object
     */
    byte[] getTransactionBytes();

    /**
     * creates a immutable Abstract (and horrible XD ) implementation of the
     * ITransaction interface.
     *
     * @return a immutable implementation of this ITransaction note this is not
     * a instance of child class
     */
    public default ITransaction getImmutableClone() {
        ITransaction cpy = requestclone();
       //ensure the implementor of requestclone does a deep copy. otherwise lets do ourselves
        if (cpy == this||cpy.getTransactionBytes()== this.getTransactionBytes()) {
            byte[] tmp = getTransactionBytes();
            final byte[] datacopy = Arrays.copyOf(tmp, tmp.length);
            cpy = new ITransaction() {
                private final byte[] content = datacopy;

                @Override
                public final byte[] getTransactionBytes() {
                    return content;
                }

                @Override
                public boolean canCertificateConfirm() {
                    return false;
                }

                @Override
                public X509Certificate[] getConfirmationCertificates() {
                    return null;
                }

                @Override
                public ByteBuffer getSignedTransaction(int index) {
                    return null;
                }

                @Override
                public final boolean equals(Object obj) {
                    if (Objects.nonNull(obj)) {
                        if (obj instanceof ITransaction) {
                            return Arrays.equals(((ITransaction) obj).getTransactionBytes(), getTransactionBytes());
                        }
                    }
                    return false;
                }

                @Override
                public final int hashCode() {
                    return Arrays.hashCode(getTransactionBytes());
                }

                @Override
                public ITransaction requestclone() {
                    return getImmutableClone();
                }
            };
        }
        return cpy;
    }

    public boolean canCertificateConfirm();

    public X509Certificate[] getConfirmationCertificates();

    public ByteBuffer getSignedTransaction(int index);

    /**
     * intended to create a copy of this Object that is instance of the class
     * that this is implemented on.
     *
     * @return
     */
    public ITransaction requestclone();

}
