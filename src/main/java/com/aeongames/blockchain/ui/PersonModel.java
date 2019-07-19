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

import com.aeongames.expediente.Person;
import java.util.ArrayList;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class PersonModel extends AbstractListModel<Person> implements ComboBoxModel<Person>,ListDataListener {

    private int ComboSelecteditem = -1;
    private final ArrayList<Person> PersonList;

    PersonModel(ArrayList<Person> Pers) {
        this.PersonList = Pers;
    }

    @Override
    public int getSize() {
        if (PersonList != null) {
            return PersonList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Person getElementAt(int index) {
        if (PersonList != null) {
            return PersonList.get(index);
        } else {
            return null;
        }
    }

    ArrayList<Person> getthelist() {
        return PersonList;
    }

    void donotify() {
        this.fireContentsChanged(this, 0, PersonList.size());
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if (anItem != null) {
            if (PersonList != null) {
                ComboSelecteditem =PersonList.indexOf(anItem);
            }
        }else{
            ComboSelecteditem=-1;
        }
    }

    @Override
    public Object getSelectedItem() {
       if(ComboSelecteditem>=0){
          return PersonList.get(ComboSelecteditem);
       }
       return null;
    }
    
    public PersonModel getcopy(){
        PersonModel tmp = new PersonModel(PersonList);
        this.addListDataListener(tmp);
        return tmp;
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
       donotify();
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
    }
}
