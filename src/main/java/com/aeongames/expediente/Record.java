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
package com.aeongames.expediente;

import com.aeongames.blockchain.TODOS.NotImplementedYet;
import com.aeongames.blockchain.TODOS.SubjectToChange;
import com.aeongames.blockchain.base.common.ByteUtils;
import com.aeongames.blockchain.base.transactions.ITransaction;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class Record implements ITransaction { 
    protected final Person Owner;//->a reference from the portfolio. 
    protected final Person TransactionRequester;//->the medic, lawer, goberment official. etc. 
    private byte transactionsignatures[][];//-> not used.... need to use it.... 
    /**
     * this is the information that will represent the Record exchange. 
     * for the Poc we might do something simple. however this is thought out for the implementer 
     * to do as desire to meet the needs of the records needs.
     */
    private final byte[] RecordData;//--> the data of the record. 
    
    private Record(){
        throw new IllegalArgumentException("illegal creation");
    }
    
    public static Record CreateGenesisforExpediente(Person Owner, Person requester){
       return new Record(Owner, requester, String.format("Genesis done at %s",LocalDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).getBytes(StandardCharsets.UTF_8));
    }
    
    protected Record(Person owner,Person requester,byte[] recorddata){
        this.Owner=Objects.requireNonNull(owner,"the Owner cannot be null");
        this.TransactionRequester=Objects.requireNonNull(requester,"the requester cannot be null");
        if(Owner.equals(TransactionRequester)) throw new IllegalArgumentException("The owner cannot be the Transaction requester");
        this.RecordData = Arrays.copyOfRange(Objects.requireNonNull(recorddata, "The data cannot be null"), 0, Objects.requireNonNull(recorddata, "The data cannot be null").length);
    }
   

    @Override
    public synchronized byte[] getTransactionBytes() {
     return Arrays.copyOfRange(RecordData, 0, RecordData.length);   
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.Owner);
        hash = 73 * hash + Objects.hashCode(this.TransactionRequester);
        hash = 73 * hash + Arrays.hashCode(this.RecordData);
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
        if (!(obj instanceof Record)) {
            return false;
        }
        final Record other = (Record) obj;
        if (!Objects.equals(this.Owner, other.Owner)) {
            return false;
        }
        if (!Objects.equals(this.TransactionRequester, other.TransactionRequester)) {
            return false;
        }
        return Arrays.equals(this.RecordData, other.RecordData);
    }

    @Override
    public ITransaction requestclone() {
        //this does a shallow copy of the owner and Requester but a deep of the data. it should work just fine? hopefully 
        return new Record(this.Owner,this.TransactionRequester,Arrays.copyOf(RecordData, RecordData.length));
    }


    @Override
    public String toString() {
         return String.format("Record->%s::%s",Owner.toString(),ByteUtils.ByteArrayToString(RecordData));
    }
    @Override
    @SubjectToChange
    public boolean canCertificateConfirm() {
        return transactionsignatures != null && transactionsignatures.length > 0;
    }

    @Override
    @NotImplementedYet
    public X509Certificate[] getConfirmationCertificates() {
        return null;//TODO. the signature Certificates might be required to persist
    }

    @Override
    @SubjectToChange
    public ByteBuffer getSignedTransaction(int index) {
        if (transactionsignatures != null && transactionsignatures.length > index) {
            return ByteBuffer.wrap(transactionsignatures[index]).asReadOnlyBuffer();
        }
        throw new IndexOutOfBoundsException("the index request is beyond the bounts or the signature object is null");
    }
    
}
