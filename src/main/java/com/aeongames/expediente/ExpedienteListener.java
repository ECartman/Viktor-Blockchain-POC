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
package com.aeongames.expediente;

import com.aeongames.blockchain.TODOS.NotImplementedYet;
import com.aeongames.blockchain.TODOS.SubjectToChange;
import java.util.EventListener;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public interface ExpedienteListener extends EventListener{
    /**
     * the called class needs to be mindful that the Object that call this class 
     * will hold a lock(re-entrant one tho) and NEEDS to avoid doing actions that MIGHT meet a deadlock 
     * in short DO NOT call the Expediente back or do changes to Expediente from 
     * an event on a diferent thread otherwise will cause a deadlock! 
     * @param exp the afected Expediente
     * @param type the type of event 
     * @param affectedRecords the affected records. 
     */
    @NotImplementedYet
    @SubjectToChange
    void ExpedienteChanged(Expediente exp, ExpActionType type,Record ...affectedRecords);
}


