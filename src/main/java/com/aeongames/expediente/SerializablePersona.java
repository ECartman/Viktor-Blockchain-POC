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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class SerializablePersona {

    /**
     * this variable will store the CRID (cedula) value. since the current CRID
     * is mere numeric this should suffice. if it change it should be fine if we
     * use UTF-8 representation as well. but might require tweaking some other
     * places. this value IS NOT to be exposed as an array but as a copy or read
     * only values
     */
    private byte CRID[];
    /**
     * the current name of this person. TODO: separate this name to several
     * parts (first, last name etc)
     */
    private String Name;

    /**
     * the value for this person sex. this value needs to be one of the two
     * acceptable values (at least initially) as for medical reasons.
     */
    private Sex PersonSex;
    /**
     * person birthday
     */
    private long day_of_birth;

    /**
     * private string domicilio
     */
    private String Domicilio;

    private byte[] Person_Certificate;

    /**
     * creates a new instance of person. please note that the values are check
     * for null but are not check if they are correct. is the caller task to
     * ensure the values are "real world valid" for example the CRID is a # with
     * AT least 9 characters (numeric) and the first digit must be between 1 to
     * 9. however this code does not seek or check those are meet. whomever
     * called this class should already be sure those are alredy filtered.
     *
     * @param CRID
     * @param Name
     * @param PersonSex
     * @param day_of_birth
     * @param Domicilio
     */
    private SerializablePersona(byte[] CRID, String Name, Sex PersonSex, long day_of_birth, String Domicilio, byte[] cert) {
        this.CRID = Arrays.copyOfRange(Objects.requireNonNull(CRID, "The ID cannot be null"), 0, Objects.requireNonNull(CRID, "The ID cannot be null").length);
        this.Name = Objects.requireNonNull(Name, "the name Cannot be null");
        this.PersonSex = Objects.requireNonNullElse(PersonSex, Sex.UNDEFINED);//--> if Sex is null will be asumed an UNDEFINED. 
        this.day_of_birth = day_of_birth;
        this.Domicilio = Objects.requireNonNull(Domicilio, "the Address Cannot be null");
        this.Person_Certificate = cert;
    }

    /**
     * @return the CRID
     */
    public byte[] getCRID() {
        return CRID;
    }

    /**
     * @return the Name
     */
    public String getName() {
        return Name;
    }

    /**
     * @return the PersonSex
     */
    public Sex getPersonSex() {
        return PersonSex;
    }

    /**
     * @return the day_of_birth
     */
    public long getDay_of_birth() {
        return day_of_birth;
    }

    /**
     * @return the Domicilio
     */
    public String getDomicilio() {
        return Domicilio;
    }

    /**
     * converts this instance to Person instance.
     *
     * @return
     * @throws java.security.cert.CertificateException if unable to read the Certificate from bytes
     */
    public Person convertToPersonClass() throws CertificateException {
        X509Certificate ReadCert=null;
        if(Person_Certificate!=null) {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            try (InputStream in = new ByteArrayInputStream(Person_Certificate)) {
                ReadCert = (X509Certificate) certFactory.generateCertificate(in);
            } catch (IOException ex) {
                throw new CertificateException(ex);
            }
        }
        return new Person(CRID, Name, PersonSex, day_of_birth, Domicilio,ReadCert);
    }

    public static SerializablePersona toSerializablePersona(Person p) throws CertificateEncodingException {
        return new SerializablePersona(p.getCRID(), p.getName(), p.getPersonSex(), p.getDay_of_birth(), p.getDomicilio(),p.getCertificate()!=null?p.getCertificate().getEncoded():null);
    }

}
