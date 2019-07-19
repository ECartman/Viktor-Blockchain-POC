/*
 * 
 *   Copyright © ï¿½ 2019 Eduardo Vindas Cordoba. All rights reserved.
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

import com.aeongames.blockchain.base.transactions.ITransaction;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
class TransactionHelper {

    public final static String Print(final List<? extends ITransaction> listprint) {
        StringBuilder buff = new StringBuilder("\n\t{");
        buff.append(listprint.stream().map(Object::toString).collect(Collectors.joining("},\n{")));
        buff.append("\n}");
        return buff.toString();
    }
}
