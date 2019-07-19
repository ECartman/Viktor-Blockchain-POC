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

import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

/**
 * this is a representation of a person. this is intended as immutable
 * information and not to change however we know it is possible for a persona to
 * change details of his live such as name and it seems even sex so this is not
 * a final value in reality. we could set in store if they change to create a
 * new one. however we make a decision to set the immutable value as the CRID
 * and for it to be unique. as this one is the true immutable one. and others
 * will be changeable but require to be on record. this due as for today even if
 * you change your sex there are fundamental biological aspects that will follow
 * you and the medical history needs to be known
 *
 * this object is Mutable. however some of its data is not. so we can use to
 * "singularize values"
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class Person {

    /**
     * this variable will store the CRID (cedula) value. since the current CRID
     * is mere numeric this should sufice. if it change it should be fine if we
     * use UTF-8 representation as well. but might require tweaking some other
     * places. this value IS NOT to be exposed as an array but as a copy or read
     * only values
     */
    private final byte CRID[];

    /**
     * the current name of this person. TODO: separate this name to several
     * parts (first, last name etc)
     */
    private String Name;
    
    private X509Certificate Person_Certificate;

    /**
     * the value for this person sex. this value needs to be one of the two
     * acceptable values (at least initially) as for medical reasons.
     */
    private Sex PersonSex;
    /**
     * person birthday
     */
    private final long day_of_birth;

    /**
     * private string domicilio
     */
    private String Domicilio;

    /**
     * this is not intended to be used... EVER
     *
     * @throws IllegalAccessException
     */
    private Person() throws IllegalAccessException {
        throw new IllegalAccessException("Wrong Constructor mate");
    }

    /**
     * creates a new instance of person.please note that the values are check
 for null but are not check if they are correct. is the caller task to
 ensure the values are "real world valid" for example the CRID is a # with
 AT least 9 characters (numeric) and the first digit must be between 1 to
 9. however this code does not seek or check those are meet. whomever
 called this class should already be sure those are alredy filtered.
     *
     * @param CRID
     * @param Name
     * @param PersonSex
     * @param day_of_birth
     * @param Domicilio
     * @param cert the person certificate Can be null
     */
    public Person(byte[] CRID, String Name, Sex PersonSex, long day_of_birth, String Domicilio,X509Certificate cert) {
        this.CRID = Arrays.copyOfRange(Objects.requireNonNull(CRID, "The ID cannot be null"), 0, Objects.requireNonNull(CRID, "The ID cannot be null").length);
        this.Name = Objects.requireNonNull(Name, "the name Cannot be null");
        this.PersonSex = Objects.requireNonNullElse(PersonSex, Sex.UNDEFINED);//--> if Sex is null will be asumed an UNDEFINED. 
        this.day_of_birth = day_of_birth;
        this.Domicilio = Objects.requireNonNull(Domicilio, "the Address Cannot be null");
        this.Person_Certificate=cert;
    }

    public Person(byte[] CRID, String Name, Sex PersonSex, LocalDate date, String Domicilio) {
        this(CRID, Name, PersonSex, date.toEpochDay(), Domicilio,null);
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
     * updates the name of this person the current name is recorded on this
     * person history and then updates
     *
     * @param Name the Name to set
     */
    public void setName(String Name) {
        Objects.requireNonNull(Name, "the name Cannot be null");
        this.Name = Name;
    }

    /**
     * @param PersonSex the PersonSex to set
     */
    public void setPersonSex(Sex PersonSex) {
        Objects.requireNonNull(PersonSex, "the new Sex cannot be null");
        this.PersonSex = PersonSex;
    }

    /**
     * @param Domicilio the Domicilio to set
     */
    public void setDomicilio(String Domicilio) {
        this.Domicilio = Objects.requireNonNull(Domicilio, "the new address cannot be null");
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Arrays.hashCode(this.CRID);
        hash = 89 * hash + (int) (this.day_of_birth ^ (this.day_of_birth >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (Objects.isNull(obj)) {
            return false;
        }
        if (!(obj instanceof Person)) {
            return false;
        }
        final Person other = (Person) obj;
        if (this.day_of_birth != other.day_of_birth) {
            return false;
        }
        return Arrays.equals(this.CRID, other.CRID);
    }

    public byte[] getCRID() {
        return Arrays.copyOf(CRID,CRID.length);
    }
    
    public X509Certificate getCertificate(){
        return Person_Certificate;
    }

    @Override
    public String toString() {
        StringBuilder b= new StringBuilder();
        for (byte c : CRID) {
            b.append(c);
        }
      return  String.format("%s::%s", getName(),b.toString());
    }
    
    

}
