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
package com.aeongames.blockchain.ui;

import com.aeongames.blockchain.base.BlockchainPool;
import com.aeongames.blockchain.base.PoolChangeListener;
import com.aeongames.blockchain.base.transactions.ITransaction;
import com.aeongames.expediente.FileRecord;
import com.aeongames.logger.LoggingHelper;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import javax.swing.SwingUtilities;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public abstract class blockAddedUIListener implements PoolChangeListener<FileRecord> {


    @Override
    public void NotifyAccepted(BlockchainPool<FileRecord> CallerPool, List<FileRecord> CommitedTransactions) {
        try {
            //this is almost 100% ensured to run on a non EDT therefore we need to be mindful of the threading.
            SwingUtilities.invokeAndWait(() -> {
                BlockAdded(CallerPool,CommitedTransactions);
            });
        } catch (InterruptedException | InvocationTargetException ex) {
             LoggingHelper.getAClassLogger("UI").log(Level.SEVERE, "Error Updating the UI", ex);
        }
    }

    @Override
    public void NotifyDoubleSpent(BlockchainPool<FileRecord> CallerPool, List<? extends ITransaction> DoubleSpentTransactions) {
        //ignore
    }

    @Override
    public void NotifyNotAccepted(BlockchainPool<FileRecord> CallerPool, List<FileRecord> Denylist) {
       //ignore
    }

    @Override
    public void NotifyInvalidSignature(BlockchainPool<FileRecord> CallerPool, List<FileRecord> AttemptedTransactions) {
        //ignore
    }

    @Override
    public void Tick(BlockchainPool<FileRecord> CallerPool) {
        //ignore
    }

    public abstract void BlockAdded(BlockchainPool<FileRecord> CallerPool, List<FileRecord> CommitedTransactions);
    
}
