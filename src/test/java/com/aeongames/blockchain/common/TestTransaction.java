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

import com.aeongames.blockchain.base.transactions.ITransaction;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class TestTransaction implements ITransaction{
    
    private static int count = 0;
    private final byte[] privatedata;
    private final int id;

    public TestTransaction(byte[] data) {
        privatedata = data;
        id = count++;
    }

    @Override
    public byte[] getTransactionBytes() {
        return privatedata;
    }

    @Override
    public String toString() {
        return String.format("Transaction %s, data: %s", id, new String(privatedata, Charset.defaultCharset()));
    }

    @Override
    public ITransaction requestclone() {
        //deep clone
        return new TestTransaction(Arrays.copyOf(privatedata, privatedata.length));
    }

    @Override
    public boolean canCertificateConfirm() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public X509Certificate[] getConfirmationCertificates() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ByteBuffer getSignedTransaction(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
