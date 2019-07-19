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
import com.aeongames.expediente.Expediente;
import com.aeongames.expediente.FileRecord;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class PoolFileRecordTerminalListener implements PoolChangeListener<FileRecord> {
    private final Expediente<FileRecord> myExpediente;
    public PoolFileRecordTerminalListener(Expediente<FileRecord> myExpediente) {
        this.myExpediente=myExpediente;
    }

    @Override
    public synchronized void NotifyAccepted(BlockchainPool<FileRecord> CallerPool, List<FileRecord> CommitedTransactions) {
        printthreadinfo("NotifyAccepted", CallerPool);
        printlist(1, CommitedTransactions);
        myExpediente.commit(new ArrayList<>(CommitedTransactions));
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized void NotifyDoubleSpent(BlockchainPool<FileRecord> CallerPool, List<? extends ITransaction> DoubleSpentTransactions) {
        printthreadinfo("NotifyDoubleSpent", CallerPool);
        printlist(1, (List<FileRecord>) DoubleSpentTransactions);
        myExpediente.commit(new ArrayList<>((List<FileRecord>)DoubleSpentTransactions));
    }

    @Override
    public synchronized void NotifyNotAccepted(BlockchainPool<FileRecord> CallerPool, List<FileRecord> AttemptedTransactions) {
        printthreadinfo("NotifyNotAccepted", CallerPool);
        printlist(1, AttemptedTransactions);
    }

    @Override
    public synchronized void NotifyInvalidSignature(BlockchainPool<FileRecord> CallerPool, List<FileRecord> AttemptedTransactions) {
        printthreadinfo("NotifyInvalidSignature", CallerPool);
        printlist(1, AttemptedTransactions);
    }

    @Override
    public synchronized void Tick(BlockchainPool<FileRecord> CallerPool) {
        printthreadinfo("Tick", CallerPool);
    }

    private void printthreadinfo(String information, BlockchainPool<FileRecord> CallerPool) {
        System.out.println("/******************************************************************/");
        System.out.println(String.format("Event: %s", information));
        System.out.println("/******************************************************************/");
        System.out.println(String.format("Thread:%s(%d)", CallerPool.getName(), CallerPool.getId()));
        System.out.println("/******************************************************************/");
    }

    private void printlist(int i, List<FileRecord> data) {
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
