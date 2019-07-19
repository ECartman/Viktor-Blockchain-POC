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
package com.aeongames.blockchain.common;

import com.aeongames.blockchain.base.common.Hash;
import java.util.ArrayList;
import java.util.Random;

/**
 * this is a test class that creates transactions with random data. 
 * i tired of copy paste the same code on every test. (but likely this is the last one 
 * so a waste of time doing this? lol) 
 * @author Eduardo <cartman@aeongames.com>
 */
public class RandomTransactionCreator {

    private final Random rnd;

    public RandomTransactionCreator() {
        System.out.println("Setting Up Random Transaction Creator");
        //for TEST ONLY this shit is not for security & shit... 
        rnd = new Random();
    }

    public TestTransaction createTestOBjectwithRandomhash() {
        return new TestTransaction(CreateRandomHash());
    }

    private byte[] CreateRandomHash() {
        byte[] data = new byte[Hash.HASHSIZE];
        rnd.nextBytes(data);
        return data;
    }

    public ArrayList<TestTransaction> createrandomlist() {
        ArrayList<TestTransaction> returnliust = new ArrayList<>();
        for (int count = 0; count < 50; count++) {
            returnliust.add(createTestOBjectwithRandomhash());
        }
        return returnliust;
    }
}

