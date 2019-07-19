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

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public enum Sex {
    MALE("Male", (byte) 0b01),
    FEMALE("Female", (byte) 0b10),
    //whatever floats your boat mate.
    UNDEFINED("Unknown", (byte) 0b11111111, 'X');

    private final char Sex_Character;
    private final String SexDescriptor;
    /**
     * lets allow ppl to have their way if the want to be
     * non binary... personally i don't care, however this is limited to a byte
     * of combinations -128 and a maximum value of 127 (inclusive). so and
     * therefore if that shit about begin all kinds fo furry and alien etc
     * defines a "sex" we might need to define something beyond a byte... ... to
     * be fair i accept the fact there might be more than 2 sex. but some ...
     * definitions are just idiotic. but i digress.
     */
    private final byte SexBinID;

    private Sex(String SexDescriptor, byte DescriptorID) {
        this.SexBinID = DescriptorID;
        this.SexDescriptor = SexDescriptor;
        this.Sex_Character = Character.toUpperCase(SexDescriptor.charAt(0));
    }

    private Sex(String SexDescriptor, byte DescriptorID, char Sex_Character) {
        this.SexBinID = DescriptorID;
        this.SexDescriptor = SexDescriptor;
        this.Sex_Character = Character.toUpperCase(Sex_Character);
    }

    private Sex() {
        throw new IllegalCallerException("this class cannot be instantiated this way");
    }

    /**
     * @return the SexDescriptor
     */
    public String getSexDescriptor() {
        return SexDescriptor;
    }

    /**
     * @return the SexBinID
     */
    public byte getSexBinID() {
        return SexBinID;
    }

    @Override
    public String toString() {
        //return String.format("SexBinaryCode:%d SexChar:%s SexName:%s", getSexBinID(), getSex_Character(), getSexDescriptor());
        return getSexDescriptor();
    }

    /**
     * @return the Sex_Character
     */
    public char getSex_Character() {
        return Sex_Character;
    }
    
    public static Sex[] getSexOptions(){
      return new Sex[]{ MALE,FEMALE,UNDEFINED};   
    }
    
}
