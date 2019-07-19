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
package com.aeongames.blockchain.base;

import java.math.BigInteger;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class blockindexer {

    private static BigInteger currentIndex= BigInteger.ZERO,
                              CommitedIndex = BigInteger.ZERO;

    synchronized static BigInteger getnextIndex() {
        BigInteger returnvalue=CommitedIndex;
        currentIndex=CommitedIndex.add(BigInteger.ONE);
        return returnvalue;
    }
    
     synchronized static void commitCurrentValue() {
        CommitedIndex=currentIndex;
    }

}
