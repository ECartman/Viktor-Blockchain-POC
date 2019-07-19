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

import com.aeongames.blockchain.base.BlockchainPool;
import com.aeongames.blockchain.base.PoolChangeListener;
import com.aeongames.blockchain.base.transactions.ITransaction;
import java.util.List;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class PoolTerminalListener implements PoolChangeListener<TestTransaction> {

    @Override
    public synchronized void NotifyAccepted(BlockchainPool<TestTransaction> CallerPool, List<TestTransaction> CommitedTransactions) {
        printthreadinfo("NotifyAccepted", CallerPool);
        printlist(1, CommitedTransactions);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized void NotifyDoubleSpent(BlockchainPool<TestTransaction> CallerPool, List<? extends ITransaction> DoubleSpentTransactions) {
        printthreadinfo("NotifyDoubleSpent", CallerPool);
        printlist(1, (List<TestTransaction>) DoubleSpentTransactions);
    }

    @Override
    public synchronized void NotifyNotAccepted(BlockchainPool<TestTransaction> CallerPool, List<TestTransaction> AttemptedTransactions) {
        printthreadinfo("NotifyNotAccepted", CallerPool);
        printlist(1, AttemptedTransactions);
    }

    @Override
    public synchronized void NotifyInvalidSignature(BlockchainPool<TestTransaction> CallerPool, List<TestTransaction> AttemptedTransactions) {
        printthreadinfo("NotifyInvalidSignature", CallerPool);
        printlist(1, AttemptedTransactions);
    }

    @Override
    public synchronized void Tick(BlockchainPool<TestTransaction> CallerPool) {
        printthreadinfo("Tick", CallerPool);
    }

    private void printthreadinfo(String information, BlockchainPool<TestTransaction> CallerPool) {
        System.out.println("/******************************************************************/");
        System.out.println(String.format("Event: %s", information));
        System.out.println("/******************************************************************/");
        System.out.println(String.format("Thread:%s(%d)", CallerPool.getName(), CallerPool.getId()));
        System.out.println("/******************************************************************/");
    }

    private void printlist(int i, List<TestTransaction> data) {
         System.out.println("/******************************************************************/");
        data.forEach(value->{
         StringBuilder b= new StringBuilder();
            for (int j = 0; j < i; j++) {
                b.append('\t');
            }
            b.append(value.toString());
            System.out.println(b.toString());
        });
        System.out.println("/******************************************************************/");
    }

}
