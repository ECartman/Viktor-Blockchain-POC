/*
 * 
 *   Copyright � 2019 Eduardo Vindas Cordoba. All rights reserved.
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

import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * the name "Expediente" is the best to use english does not seem to have a
 * great way to translate this concept for instance a medical record is not the
 * same a medical record might refer to a single entry on a book of medical
 * records an "Expediente" is not singular to "Medical" but is more general
 * information. and can apply to other scenarios. now for this PoC this will
 * apply a example in medical records however the idea is for this to be general
 * enough so can be used on other kinds of records.
 *
 * a possible translation COULD be portfolio refer to
 * https://en.wikipedia.org/wiki/Portfolio
 *
 * TODO: add Listening or callbacks to Pool managers IF implemented.
 * TODO: GENERICS are missing
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class SerializableExpediente {

    /**
     * the owner or patient of this record;
     */
    private final SerializablePersona Owner;

    /**
     * the Transactions or Records of changes done to this portfolio.this is
     * used to compile the portfolio information. and the medical (or other)
     * records of "things" done.
     *
     * @return
     */
    //TODO: do some Serialization or Deserialization on Records. 
    //private final ArrayList<Record> Records;
    public final Expediente CreateExpediente() throws CertificateException {
        return new Expediente(getOwner(), new ArrayList<>());
    }

    public static final SerializableExpediente SerializableExpediente(Expediente exp) throws CertificateEncodingException {
        return new SerializableExpediente(SerializablePersona.toSerializablePersona(exp.getOwner()), exp.getRecords());
    }

    /**
     * to be used by Loaders from databases. Please ensure this is not used to
     * create records that are not commit as they will not be added.
     *
     * @param Owner
     * @param Records
     */
    public SerializableExpediente(SerializablePersona Owner, ArrayList<Record> Records) {
        this.Owner = Objects.requireNonNull(Owner, "the Owner MUST be non null");
        if (!Objects.requireNonNull(Records, "The records cannot be a null value").isEmpty()) {
            //SEE TODO'S
            // this.Records = Records;
        } else {
            throw new IllegalArgumentException("the Records cannot be a Empty list");
        }
    }

    /**
     * used when creating a new Portfolio
     *
     * @param Owner
     */
    public SerializableExpediente(SerializablePersona Owner) {
        this.Owner = Owner;
        //this.Records = new ArrayList<>();
    }

    /**
     * @return the Owner
     */
    public Person getOwner() throws CertificateException {
        return Owner.convertToPersonClass();
    }

    //TODO: Update
    public synchronized ArrayList<Record> getRecords() {
        return new ArrayList<>();
    }

}
