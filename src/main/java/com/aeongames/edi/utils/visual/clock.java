/*
 * 
 * Copyright © 2008-2012 Eduardo Vindas Cordoba. All rights reserved.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/**
 * clock.java
 *
 * Created on 07/05/2010, 05:17:20 PM
 */
package com.aeongames.edi.utils.visual;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Calendar;

/**
 *a multi use swing enable clock/Timer this can be used to track a amount X of time
 * for example to track how long a person takes to determine what this class do
 * also we can use this class to track and determine when a X amount of minutes has pass or
 * how much % of a required time has past.
 * this class implements methods that makes this class a bean
 * meaning it will notify if required via a property change each tick in order to print the time it has pass
 * the output format of this clock is XX:XX:XX
 * @author Eduardo Vindas C <eduardo.vindas@hp.com>
 */
public class clock implements ActionListener, Serializable {

    public final static String Update_Property = "CLOCK_UPDATE";
    public final static String CountDowndone_Property = "COUNT_DONE";
    public final static String CountDown_Property = "COUNT_DOWN";
    private PropertyChangeSupport propertySupport;
    private java.text.DecimalFormat formater = new java.text.DecimalFormat("00");
    /**the amount o miliseconds for start and end time. */
    private long start, end;
    /**delay to be used.*/
    private int DELAY, countdownminutes = 0;
    /**default delay ~1000ms that is equal to ~1 seconds ("~"=somewhat,close to)*/
    public static final int DEFDELAY = 1000;
    private javax.swing.Timer tim;
    private long milis = 0L;

    /**
     * this constructor will create a new instance for a clock note that this method will use the default delay
     * that is equal to 1000 miliseconds approximately
     */
    public clock() {
        end = -1;
        DELAY = DEFDELAY;
        CreateTimer(DELAY);

    }

    /**
     * this constructor will create a new instance for a clock note that this method will usedelay provided a integer that represent a X amount of miliseconds
     *@param int delay
     */
    public clock(int delay) {
        end = -1;
        if (delay > 0) {
            DELAY = delay;
        } else {
            DELAY = DEFDELAY;
        }
        CreateTimer(DELAY);
    }

    /**
     * this constructor will create a new instance for a clock note that this method will usedelay provided a integer that represent a X amount of miliseconds
     *@param int delay
     */
    public clock(int delay, int countdown) {
        if (countdown > 0) {
            countdownminutes = countdown;
        }
        end = -1;
        DELAY = delay;
        CreateTimer(DELAY);
    }

    /**
     * this methos will start the timer
     */
    public void start() {
        if (tim != null) {
            if (tim.isRunning()) {
                throw new java.lang.IllegalThreadStateException("the timer is alredy running");
            } else {
                start = System.currentTimeMillis();
                tim.start();
            }
        } else {
            throw new java.lang.NullPointerException("The Timer cannot be null!!!");
        }
    }

    /**
     * tells if the clock is running
     */
    public boolean isrunning() {
        if (tim != null) {
            return tim.isRunning();
        } else {
            return false;
        }

    }

    /**
     * this methos will stop the timer and sent the end time
     */
    public void stop() {
        if (tim != null) {
            end = System.currentTimeMillis();
            tim.stop();
            propertySupport.firePropertyChange("STOP", null, providePrintableTimeString(end));
        } else {
            throw new java.lang.NullPointerException("The Timer cannot be null!!!");
        }
    }

    /**
     * this method is designed to clear the values and destroy the values leaving the clock ready for reuse
     * or atleast that is the idea
     */
    public void clear_and_new() {
        start = 0;
        end = -1;
        tim = null;
        PropertyChangeListener[] temp_listeners = propertySupport.getPropertyChangeListeners();
        for (int cont = 0; cont < temp_listeners.length; cont++) {
            propertySupport.removePropertyChangeListener(temp_listeners[cont]);
        }
        CreateTimer(DELAY);
        for (int cont = 0; cont < temp_listeners.length; cont++) {
            propertySupport.addPropertyChangeListener(temp_listeners[cont]);
        }
        temp_listeners = null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        milis = System.currentTimeMillis();
        propertySupport.firePropertyChange(Update_Property, null, providePrintableTimeString(milis));
        propertySupport.firePropertyChange(CountDown_Property, null, providePrintableCountdown(countdownminutes, milis));
    }

    /**
     * provides the value for the delay that is begin used o this clock
     * @return the delay
     */
    public int getdelay() {
        return DELAY;
    }

    /**
     * returns true of the delay is the default delay.
     * false otherwise
     * @return  DELAY == DEFDELAY
     */
    public boolean isDefaultDelay() {
        return DELAY == DEFDELAY;
    }

    private void CreateTimer(int DELAY) {
        propertySupport = new java.beans.PropertyChangeSupport(this);
        tim = new javax.swing.Timer(DELAY, this);
        tim.setInitialDelay(0);
        tim.setRepeats(true);
    }

    /**
     * provides the value of the time that has been recorded or has run on this timer
     * note that in order to this method to work the timer is required to be stop
     * @return the end time munis the start time
     */
    public long gettime() {
        if (tim != null && !tim.isRunning()) {
            return end - start;
        } else {
            return -1;
        }
    }

    private String providePrintableTimeString(long time_tocalculate) {
        long valueToShow = time_tocalculate - start;
        double seconds = Math.floor(valueToShow / 1000);
        double minutes = Math.floor(seconds / 60);
        double hours = Math.floor(minutes / 60);
        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 24;
        StringBuffer toreturn = formater.format(hours, new StringBuffer(), new java.text.FieldPosition(0));
        toreturn.append(':');
        toreturn = formater.format(minutes, toreturn, new java.text.FieldPosition(0));
        toreturn.append(':');
        toreturn = formater.format(seconds, toreturn, new java.text.FieldPosition(0));
        return toreturn.toString();
    }

    private String providePrintableCountdown(int ammountMinutes, long time_tocalculate) {
        if (ammountMinutes > 0) {
            long valueToShow = ((long) ammountMinutes) * 60000 - (time_tocalculate - start);
            if (valueToShow > 0L) {
                double seconds = Math.floor(valueToShow / 1000);
                double minutes = Math.floor(seconds / 60);
                double hours = Math.floor(minutes / 60);
                seconds = seconds % 60;
                minutes = minutes % 60;
                hours = hours % 24;
                StringBuffer toreturn = formater.format(hours, new StringBuffer(), new java.text.FieldPosition(0));
                toreturn.append(':');
                toreturn = formater.format(minutes, toreturn, new java.text.FieldPosition(0));
                toreturn.append(':');
                toreturn = formater.format(seconds, toreturn, new java.text.FieldPosition(0));
                return toreturn.toString();
            } else {
                propertySupport.firePropertyChange(CountDowndone_Property, null, "00:00:00");
                return "00:00:00";
            }
        } else {
            return "00:00:00";
        }
    }

    /**
     * provides a printable string in order to be show on a laber or whateve use it can have.
     * Warning if the timer is currently running or has not yet start will return a Null value
     * @return the Printable string. 
     */
    public String printableTime() {
        if (end != -1) {
            return providePrintableTimeString(end);
        } else {
            return null;
        }
    }

    /**
     * this method allow to add listeners to this object in order to be notifiend on the changes or to be updated when the clock runs.
     * for further references pleae review PropertyChangeSupport addPropertyChangeListener method
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    /**
     * this method will allow to remove the listener send on the paramenter so ti can start to listen to another event or case whatever apply here.
     * please review: PropertyChangeSupport removePropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    /**
     * this method will calculate the % of completition of a X amount of minutes
     * NOTE this will be calculated agains the total amount that has been run so far.
     * @param integer of the desired minutes to be calculated again the run  time
     * @return % of the calculation of the run time agains the minutes seind as parameter. if the run time is mayor than the paramenter minutes 100% is ALWAYS return
     * also note it return a Double value if you want a integer use CalculateCompleteint
     */
    public int CalculateComplete(int minutes) {
        long valueToShow = System.currentTimeMillis() - start;
        if ((minutes * 60000) > valueToShow) {
            return (int) (valueToShow * 100 / (minutes * 60000));
        } else {
            return 100;
        }
    }

    /**
     * returns a calendar with the date/time when the clock started.
     */
    public final java.util.Calendar getstartdatetime() {
        Calendar calend = Calendar.getInstance();
        calend.setTimeInMillis(start);
        Calendar.getInstance();
        return calend;
    }
}
