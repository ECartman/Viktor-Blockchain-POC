/*
 *
 *  Copyright © 2019 Eduardo Vindas Cordoba. All rights reserved.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.aeongames.blockchain.test;

import com.aeongames.blockchain.base.MerkleTree.MerkleTree2;

import static com.aeongames.logger.LoggingHelper.getDefaultLogger;

import java.util.ArrayList;
import java.util.logging.Level;

import com.aeongames.blockchain.common.RandomTransactionCreator;
import com.aeongames.blockchain.common.TestTransaction;
import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

public class MerkleTreeTest {
     RandomTransactionCreator creator;
     
    public MerkleTreeTest() {
    }

    @Before
    public void setUp() {
        System.out.println("Setting Up the test ");
        creator = new RandomTransactionCreator();
    }

    @After
    public void tearDown() {
        //nothing
        System.out.println("Test done");
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void TestHash() {
        try {
            getDefaultLogger().log(Level.FINE, "Running Merkle Test");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            fail("Exception Happend");
        }
       dotest();
    }

    /*****************************************************/


    private void dotest() {
        ArrayList<TestTransaction> result =creator.createrandomlist();
        MerkleTree2 tree = new MerkleTree2(result);
        tree.getRootHash();
        tree.printTreeByStructure();
        MerkleTree2 comfirmationTreefor = tree.getConfirmationTreeFor(result.get(40));
        comfirmationTreefor.printTreeByStructure();
        System.out.println("after do while");

    }

}
