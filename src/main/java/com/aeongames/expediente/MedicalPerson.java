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

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class MedicalPerson extends Person {
    
    /**
     * blood type.
     */
    private bloodtype personblood = bloodtype.Unknown;

    /**
     * if any we assume the most likely this will not be used this will contain
     * all the previous names registered for this person.
     */
    private final ArrayList<String> previousNames = new ArrayList<>(1);
    
    
    
    public MedicalPerson(byte[] CRID, String Name, Sex PersonSex, long day_of_birth, String Domicilio,X509Certificate cert) {
        super(CRID, Name, PersonSex, day_of_birth, Domicilio,cert);
    }
    
    public MedicalPerson(byte[] CRID, String Name, Sex PersonSex, long day_of_birth, String Domicilio,X509Certificate cert, bloodtype type) {
        super(CRID, Name, PersonSex, day_of_birth, Domicilio,cert);
        this.personblood = Objects.requireNonNullElse(type, bloodtype.Unknown);
    }

    /**
     * @return the personblood
     */
    public bloodtype getPersonblood() {
        return personblood;
    }

    /**
     * updates the name of this person the current name is recorded on this
     * person history and then updates
     *
     * @param Name the Name to set
     */
    @Override
    public void setName(String Name) {
        Objects.requireNonNull(Name, "the name Cannot be null");
        previousNames.add(getName());
        super.setName(Name);
    }

    /**
     * @param personblood the personblood to set
     */
    public void setPersonblood(bloodtype personblood) {
        this.personblood = Objects.requireNonNull(personblood, "the new bloodtype cannot be null");
    }

    /**
     *
     * TODO: implement something to avoid compiler warning instead of suppressing the warning
     * returns null if there are no names prior to the current one.
     *
     * @return a list of names this person had before. null if the current name
     * is the only one has had
     */
    @SuppressWarnings("unchecked")
    public ArrayList<String> getPreviousNames() {
        if (!previousNames.isEmpty()) {
            //since this is a list of immutable's we dont care the way this is done 
            //on the background. this is sufficient. 
            return (ArrayList<String>) previousNames.clone();
        }
        return null;
    }

}
