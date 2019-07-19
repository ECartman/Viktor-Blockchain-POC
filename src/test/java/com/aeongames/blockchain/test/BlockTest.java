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
package com.aeongames.blockchain.test;

import com.aeongames.blockchain.base.common.Hash;
import com.aeongames.blockchain.base.Block;
import com.aeongames.blockchain.base.SerializableBlock;
import com.aeongames.blockchain.common.RandomTransactionCreator;
import com.aeongames.blockchain.common.TestTransaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;

import org.junit.*;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class BlockTest {
    RandomTransactionCreator creator;
    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        System.out.println("Setting Up the test ");
        creator = new RandomTransactionCreator();
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void Block_Test() {
        ArrayList<TestTransaction> result = creator.createrandomlist();
        Hash[] v={Hash.of(creator.createTestOBjectwithRandomhash().getTransactionByteBuffer())};
        Block TestGenesisBlock= new Block( Hash.of(creator.createTestOBjectwithRandomhash().getTransactionByteBuffer()), result,v);
        //System.out.println(TestGenesisBlock.toString());
       SerializableBlock testblock= SerializableBlock.ToSerializableBlock(TestGenesisBlock);
       Gson gson = new GsonBuilder().setPrettyPrinting().create();
       String json = gson.toJson(testblock);
       System.out.println(json);
        SerializableBlock test2= gson.fromJson(json,SerializableBlock.class);
        Assert.assertTrue(testblock.equals(test2));
    }

}
