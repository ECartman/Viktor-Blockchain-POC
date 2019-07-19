/*
 *  Copyright © 2008-2013,2019 Eduardo Vindas Cordoba. All rights reserved.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.aeongames.edi.utils.visual;

import com.aeongames.logger.LoggingHelper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;

/**
 * this class was original written around... 2010 or so... this class is a
 * simple wrapper for error that happened on non UI code and needs to be send or
 * notified up to UI code. this class is unaware of its location on life.
 * meaning that it is likely to start at a thread that does NOT reside on the
 * EDT but needs to go to the EDT. therefore be mindful you are likely to send
 * this via a event dispatcher.
 *
 * @author Eduardo Jose Vindas Cordoba <cartman@aeongames.com>
 */
public class ErrorTransport {

    private String ErrorTittle;
    private String ErrorMessage;
    private Throwable error;

    public ErrorTransport(Throwable error) {
        if (error != null) {
            ErrorTittle = "Error on Execution";
            ErrorMessage = error.getMessage();
            this.error = error;
        } else {
            throw new IllegalArgumentException("the error cannot be null");
        }
    }

    public ErrorTransport(String title, String Message, Throwable err) {
        if ((title != null && !title.equals(""))) {
            ErrorTittle = title;
        } else {
            ErrorTittle = "Error on Execution";
        }
        error = err;
        if (Message != null) {
            ErrorMessage = Message;
        } else if (error != null) {
            ErrorMessage = error.getMessage();
        } else {
            ErrorMessage = "Error on the Application, details are not provided.";
        }
    }

    public ErrorTransport(String Message, Throwable err) {
        if (err != null) {
            ErrorTittle = "Error on Execution";
            if (Message != null) {
                ErrorMessage = Message;
            } else {
                ErrorMessage = error.getMessage();
            }
            this.error = err;
        } else {
            throw new IllegalArgumentException("the error cannot be null");
        }
    }

    /**
     * @return the ErrorTittle
     */
    public String getErrorTittle() {
        return ErrorTittle;
    }

    /**
     * @return the ErrorMessage
     */
    public String getErrorMessage() {
        return ErrorMessage;
    }

    public String getErrorStack() {
         String ErrorStack = "Stack is not available for this error";
        if (error != null) {           
            try (StringWriter writer = new StringWriter();
                 PrintWriter out = new PrintWriter(writer, true)) {
                    error.printStackTrace(out);
                    out.flush();
                    ErrorStack = writer.toString();
            } catch (IOException ex) {
                LoggingHelper.getDefaultLogger().log(Level.SEVERE, null, ex);
            }
        }
        return ErrorStack;
    }
}
