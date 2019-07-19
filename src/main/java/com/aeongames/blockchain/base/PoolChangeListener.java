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

import com.aeongames.blockchain.TODOS.SubjectToChange;
import com.aeongames.blockchain.base.transactions.ITransaction;
import java.util.EventListener;
import java.util.List;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 * @param <T> the type od Transactions
 */
public interface PoolChangeListener<T extends ITransaction> extends EventListener {

    public void NotifyAccepted(BlockchainPool<T> CallerPool, List<T> CommitedTransactions);

    //TODO: this needs to be T instead of ?... maybe? 
    @SubjectToChange
    public void NotifyDoubleSpent(BlockchainPool<T> CallerPool, List<? extends ITransaction> DoubleSpentTransactions);

    public void NotifyNotAccepted(BlockchainPool<T> CallerPool, List<T> Denylist);

    public void NotifyInvalidSignature(BlockchainPool<T> CallerPool, List<T> AttemptedTransactions);
    
    public void Tick(BlockchainPool<T> CallerPool);

}
