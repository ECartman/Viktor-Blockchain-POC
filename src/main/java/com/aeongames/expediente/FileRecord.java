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
package com.aeongames.expediente;

import com.aeongames.blockchain.base.common.ByteUtils;
import com.aeongames.blockchain.base.common.Hash;
import com.aeongames.blockchain.base.transactions.ITransaction;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class FileRecord extends Record {
    // patds are alredy immutable. so we are fine. 
   private final Path FileRecordUsed;
   private final String Name;
   private final String Description;
   private final byte[] fileRecordSignature;

    public FileRecord(Person owner, Person requester, Path FileRecord,
            String TransactionIdentName,
            String Descript) throws FileNotFoundException, IOException {
        //for time sake and due we want immutability lets allow this at first call... TODO: do something more... refined. 
        super(owner, requester, Hash.of(Files.readAllBytes(FileRecord)).toByteArray());
        if (!Files.exists(Objects.requireNonNull(FileRecord), LinkOption.NOFOLLOW_LINKS)) {
            throw new FileNotFoundException(String.format("The File at \"%s\" does not exists", FileRecord.toString()));
        } else {
            FileRecordUsed=FileRecord;
        }
        this.Name=TransactionIdentName;
        this.Description=Descript;
        fileRecordSignature=null;
    }
    
    public FileRecord(Person owner, Person requester, Path FileRecord,
            String TransactionIdentName,
            String Descript,
            byte[] signaturebits) throws FileNotFoundException, IOException {
        //for time sake and due we want immutability lets allow this at first call... TODO: do something more... refined. 
        super(owner, requester, Hash.of(Files.readAllBytes(FileRecord)).toByteArray());
        if (!Files.exists(Objects.requireNonNull(FileRecord), LinkOption.NOFOLLOW_LINKS)) {
            throw new FileNotFoundException(String.format("The File at \"%s\" does not exists", FileRecord.toString()));
        } else {
            FileRecordUsed=FileRecord;
        }
        this.Name=TransactionIdentName;
        this.Description=Descript;
        fileRecordSignature=signaturebits;
    }
    
    /**
     * exceptional Scenario
     * @param r
     * @param TransactionIdentName
     * @param Descript
     */
    public FileRecord(Record r,String TransactionIdentName,
            String Descript){
        super(r.Owner, r.TransactionRequester, r.getTransactionBytes());
         this.Name=TransactionIdentName;
        this.Description=Descript;
        FileRecordUsed=null;
        fileRecordSignature=null;
    }

    /**
     * @return the FileRecordUsed remember this value is immutable. 
     */
    public Path getFileRecordUsed() {
        return FileRecordUsed;
    }
    
    @Override
    public String toString() {
        StringBuilder b= new StringBuilder();
        b.append("FileRecord->");
        b.append(this.Name);
        b.append("\n Description: ");
         b.append(this.Description);
          b.append("\n File Path:");
          try{
          b.append(this.FileRecordUsed.toAbsolutePath().toString());
          }catch(Exception e){
              b.append("Unable to read Path.");
          }
          b.append(String.format("%s::%s",Owner.toString(),ByteUtils.ByteArrayToString(getTransactionBytes())));
          return b.toString();
    }
    
    /**
     * attempts to do a clone of this object. HOWEVER it might fail if it fails returns null. 
     * @return 
     */
    @Override
    public ITransaction requestclone() {
       try {
           //this does a shallow copy of the owner and Requester but a deep of the data. it should work just fine? hopefully
           return new FileRecord(Owner,TransactionRequester,FileRecordUsed,Name,Description);
       } catch (IOException ex) {
           return null;
       }
    }

    /**
     * @return the Name
     */
    public String getName() {
        return Name;
    }

    /**
     * @return the Description
     */
    public String getDescription() {
        return Description;
    }
}
