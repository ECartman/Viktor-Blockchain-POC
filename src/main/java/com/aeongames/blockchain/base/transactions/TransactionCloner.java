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
package com.aeongames.blockchain.base.transactions;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public final class TransactionCloner {

    /**
     * returns a copy of the provided Object. please note the cloned value 
     * MIGHT be shallow copy. 
     * @param <T> the type of value. 
     * @param transaction the transaction to clone
     * @return a <Strong>possible Shallow copy</strong> of the provided transactions
     */
    @SuppressWarnings("unchecked")
    public static <T extends ITransaction> T CloneTransaction(T transaction) {
        T value = (T) transaction.requestclone();
        return value;
    }
    
    /**
     * creates a deep copy and check if the Class matches 
     * @param <T> the kind of transaction
     * @param transaction the transaction to copy
     * @return a deep copy of the provided object OR
     * null if the Transaction class cannot create a Deep Copy 
     */
    @SuppressWarnings("unchecked")
    public static <T extends ITransaction> T DeepCloneTransaction(T transaction) {
        T result=null;
        ITransaction value =  transaction.getImmutableClone();
        if(transaction.getClass().isInstance(value)){
           result =(T) value;
        }
        return result;
    }

}
