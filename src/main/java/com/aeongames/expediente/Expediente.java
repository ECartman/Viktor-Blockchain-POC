/*
 * 
 *   Copyright � � 2019 Eduardo Vindas Cordoba. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.stream.Stream;

/**
 * the name "Expediente" is the best to use english does not seem to have a
 * great way to translate this concept for instance a medical record is not the
 * same a medical record might refer to a single entry on a book of medical
 * records an "Expediente" is not singular to "Medical" but is more general
 * information. and can apply to other scenarios. now for this PoC this will
 * apply a example in medical records however the idea is for this to be general
 * enough so can be used on other kinds of records.
 *
 * a possible translation COULD be portfolio refer to
 * https://en.wikipedia.org/wiki/Portfolio
 *
 * TODO: add Listening or callbacks to Pool managers IF implemented.
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class Expediente<T extends Record> {

    /**
     * the owner or patient of this record;
     */
    private final Person Owner;

    /**
     * the Transactions or Records of changes done to this portfolio. this is
     * used to compile the portfolio information. and the medical (or other)
     * records of "things" done.
     */
    private final ArrayList<T> Records;

    /**
     * a list of listeners that will be notified of changes on this portfolio.
     */
    private final ArrayList<ExpedienteListener> ExpListeners = new ArrayList<>();

    /**
     * the Transactions or Records of changes done to this portfolio. that ARE
     * NOT added to the chain.
     *
     * if we were to Persist the list the Uncommited values DO NOT persist.
     */
    private final transient ArrayList<T> uncommitedRecords = new ArrayList<>();

    /*
    @Deprecated
    public static final <K extends Record> Expediente<K> CreateNewExpediente(Person Owner, Person Requester)
            throws IllegalAccessException {
        //new k(Record.CreateGenesisforExpediente(Owner,Requester), null, null);
       throw new IllegalAccessException("Removed");
    }
     */
    /**
     * illegal constructor. this is defined due Reflection vulnerability.
     */
    private Expediente() {
        throw new IllegalArgumentException("This class require information");
    }

    /**
     * to be used by Loaders from databases. Please ensure this is not used to
     * create records that are not commit as they will not be added.
     *
     * @param Owner
     * @param Records
     */
    public Expediente(Person Owner, ArrayList<T> Records) {
        this.Owner = Objects.requireNonNull(Owner, "the Owner MUST be non null");
        if (!Objects.requireNonNull(Records, "The records cannot be a null value").isEmpty()) {
            this.Records = Records;
        } else {
            throw new IllegalArgumentException("the Records cannot be a Empty list");
        }
    }

    /**
     * used when creating a new Portfolio
     *
     * @param Owner
     * @param firstRecord
     */
    public Expediente(Person Owner, T firstRecord) {
        this.Owner = Owner;
        this.Records = new ArrayList<>();
        //Records.add(firstRecord);
        uncommitedRecords.add(firstRecord);
    }

    /**
     * @return the Owner
     */
    public Person getOwner() {
        return Owner;
    }

    /**
     * @return a copy of the Records.
     */
    // @SuppressWarnings("unchecked")
    public synchronized ArrayList<T> getRecords() {
        //return (ArrayList<T>) Records.clone();
        //shallow copy
        return new ArrayList<>(Records);
    }

    //@SuppressWarnings("unchecked")
    public synchronized ArrayList<T> getUncommitedRecords() {
        //return (ArrayList<T>) uncommitedRecords.clone();
        return new ArrayList<>(uncommitedRecords);
    }

    /**
     * updates the records for this portfolio if there are commited records that
     * belong to this porfolio are added into the commit records and removed
     * from the uncommit.
     *
     * @param commitedRecords
     */
    public synchronized void commit(List<T> commitedRecords) {
        Stream<T> strm = commitedRecords.stream().filter((Recordi) -> (uncommitedRecords.contains(Recordi)))
                .filter((Recordi) -> (uncommitedRecords.remove(Recordi)));
        strm.forEachOrdered((Recordi) -> {
            Records.add(Recordi);
        });
        ExpListeners.forEach((listener) -> listener.ExpedienteChanged(this, ExpActionType.RecordsCommited, strm.toArray(Record[]::new)));
    }

    public SimpleImmutableEntry<Boolean, String> CheckRecord(T newRecord) {
        if (Records.contains(newRecord)) {
            return new SimpleImmutableEntry<>(false, "already on the chain");
        } else if (uncommitedRecords.contains(newRecord)) {
            return new SimpleImmutableEntry<>(false, "already on the waitList");
        } else {
            return new SimpleImmutableEntry<>(true, "ready");
        }
    }

    public synchronized SimpleImmutableEntry<Boolean, String> addRecord(T newRecord) {
        if (Records.contains(newRecord)) {
            return new SimpleImmutableEntry<>(false, "already on the chain");
        } else if (uncommitedRecords.contains(newRecord)) {
            return new SimpleImmutableEntry<>(false, "already on the waitList");
        } else {
            if (uncommitedRecords.add(newRecord)) {
                ExpListeners.forEach((listener) -> listener.ExpedienteChanged(this, ExpActionType.Recordsqueued, newRecord));
                return new SimpleImmutableEntry<>(true, "Sucess");
            } else {
                return new SimpleImmutableEntry<>(false, "Error Adding to the Chain");
            }
        }
    }

    public synchronized boolean addListener(ExpedienteListener newlistener) {
        return ExpListeners.add(Objects.requireNonNull(newlistener, "Listener cannot be null"));
    }

    public synchronized boolean RemoveListener(ExpedienteListener removal) {
        if (Objects.nonNull(removal)) {
            return ExpListeners.remove(removal);
        }
        return false;
    }

    public synchronized boolean hasuncommitedRedords() {
        return !uncommitedRecords.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("Expediente->%s", Owner.toString());
    }

}
