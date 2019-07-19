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

import com.aeongames.blockchain.base.common.BinaryMath;
import com.aeongames.blockchain.base.common.Hash;

import static com.aeongames.logger.LoggingHelper.getDefaultLogger;

import java.util.Arrays;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Assert;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/**
 * @author cartman
 */
public class HashTest {

    /**
     * Unix output color
     */
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    public HashTest() {
    }

    @Before
    public void setUp() {
        System.out.println("pre test");
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
        System.out.println(ANSI_RESET);
        try {
            getDefaultLogger().log(Level.FINE, "Running Test");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            fail("Exception Happend");
        }
        final byte[] ZERO_ARRAY = new byte[0];
        // ensure the array is zero filled.
        //Arrays.fill(ZERO_ARRAY, (byte) 0);
        Hash kk = Hash.of(ZERO_ARRAY);
        System.out.println(kk.toString());
        Hash x = Hash.of(new byte[]{23, 1, 8, 11, 7, 24, 28, 10, 9, 5, 31, 6, 2, 26, 20, 27, 14, 3, 29, 19, 32, 30, 13, 16, 0, 17, 25, 4, 12, 21, 22, 18});
        System.out.println(x.toString());
        Hash k = new Hash(x.toString());
        System.out.println(k.toBigInteger().toString(16));
    }


    @Test
    public void TestPow() {
        System.out.println(ANSI_RESET);
        try {
            getDefaultLogger().log(Level.FINE, "Running Test: pow");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            fail("Exception Happend");
        }
        int test_Binary= 0x80000000;
         System.out.println(test_Binary);
        System.out.println(Integer.toUnsignedLong(test_Binary));
        System.out.println(String.format("Testing value: %s", Long.toBinaryString(Integer.toUnsignedLong(test_Binary))));
        System.out.println(String.format("Testing value: %s", Integer.toBinaryString(test_Binary)));
        System.out.println((long) test_Binary);
        System.out.println(String.format("Testing value: %s", Long.toBinaryString(test_Binary)));
        Assert.assertTrue(String.format("Fail to check if %d is power of 2", test_Binary), BinaryMath.is_power_of_two(test_Binary));


    }

    @Test
    public void Testlogof2() {
        int test_value= 2;
        System.out.println(test_value);
        System.out.println(Integer.toUnsignedLong(test_value));
        System.out.println(String.format("Testing value decimal: %d", test_value));
        System.out.println(String.format("Testing value Binary: %s", Integer.toBinaryString(test_value)));
        for (int i = test_value; i <= 335510; i++) {
            if(!BinaryMath.is_power_of_two(i)) {
                System.out.println(String.format("Testing value: %d result: %d", i, BinaryMath.GetTreeDepthNeeded(i)));
                if(BinaryMath.GetTreeDepthNeeded(i)>20){
                    i+=10;
                }
            }else{
                System.out.println(String.format("Testing value: %d is power of 2 exact:: %d",i,BinaryMath.GetTreeDepthNeeded(i)));
            }
        }
        for(int filler=1;filler<100;filler=filler+2)
            System.out.println(String.format("Filling %d and %d",filler-1,filler));
    }
}
