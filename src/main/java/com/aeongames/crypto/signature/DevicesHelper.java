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
package com.aeongames.crypto.signature;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.util.List;
import java.util.stream.Collectors;

public class DevicesHelper {

    //https://stackoverflow.com/questions/30550899/what-is-the-structure-of-an-application-protocol-data-unit-apdu-command-and-re/30552223
    //00 - class byte (CLA, 00 means "inter-industry command sent to logical channel 0")
    //https://cardwerk.com/smart-card-standard-iso7816-4-section-5-basic-organizations/
    //https://www.win.tue.nl/pinpasjc/docs/GPCardSpec_v2.2.pdf
    //http://javacard.vetilles.com/2008/04/28/jc101-11c-attacks-on-smart-cards/
    /**
     * 0X // or 0000 second byte. please read the info on
     * https://cardwerk.com/smart-card-standard-iso7816-4-section-5-basic-organizations/
     *
     */
    public static final int DEFAULTCLA = 0b0000_0000;//-->this shit here is a byte with 0 represented in a binary way so we can study deeper later my dude.
    public static final int GET_CHALLENGE = 0x84;/*Instruction byte*/
    public static final int NOPARAM = 0b0;
    public static final int EXPECTED_RESPONSE_BYTES = 0x08;
    /**
     *  https://cardwerk.com/smart-card-standard-iso7816-4-section-5-basic-organizations/
     * see table Table 12
     * currently and for this test we will check IF we can get the CHALLENGE.
     * so lets see if the response is valid
     */
    public static final int INSTRUCTION_NOT_SUPPORTED=0x6D00;
    public static final int NO_FURTHER_QUALIFICATIONS=0x9000;

    public static List<CardTerminal> getdevices() {
        List<CardTerminal> terminals;
        TerminalFactory factory = TerminalFactory.getDefault();
        try {
            terminals = factory.terminals().list();
            terminals = terminals.stream().filter(terminal -> terminal != null && terminal.getName() != null).collect(Collectors.toList());
            return terminals;
        } catch (CardException e) {
            return null;
        }
    }
    
    public static boolean hasCard(CardTerminal terminal) throws CardException{
        return terminal.isCardPresent();
    }
}
