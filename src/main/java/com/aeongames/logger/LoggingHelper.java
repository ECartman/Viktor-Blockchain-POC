/*
  * 
  *  Copyright © 2016,2019 Eduardo Vindas Cordoba. All rights reserved.
  * 
  *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  *  THE SOFTWARE.
 */
package com.aeongames.logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * utility class that defines a few static global variables
 *
 * @author cartman
 */
public final class LoggingHelper {

    /**
     * private constructor, this class should never have instances
     *
     * @throws IllegalAccessException as this class cannot be instanced.
     */
    private LoggingHelper() throws IllegalAccessException {
        throw new IllegalAccessException("this Class should not be Instanciated");
    }
    /**
     * the folder where we prefer the logs to be stored at. relative to the
     * Runtime Folder
     */
    private static final Path LOG_FOLDER = Paths.get("Logs");

    /**
     * the Java.Util pattern to use for logs cycles if required.
     */
    public static final String LOG_FILE_PATTERN = "%g.log";

    public static final void EnsureLogFolderReady() throws IOException {
        if (!Files.exists(LOG_FOLDER) || !Files.isDirectory(LOG_FOLDER)) {
            Files.createDirectories(LOG_FOLDER);
        }
    }

    /**
     * builds a "default" File handler for the provided class.
     *
     * @param ClassName the class name to use on this file handler( used as part
     * of the filename as well.)
     * @return a newly created file handler.
     * @throws IOException
     */
    protected static FileHandler getDefaultFileHandler(String ClassName) throws IOException {
        FileHandler Fhandle = new FileHandler(String.format("%s/%s", LOG_FOLDER, ClassName) + LOG_FILE_PATTERN);
        Fhandle.setFormatter(new SimpleFormatter());
        return Fhandle;
    }

    private static final Logger DEFAULTLOGGER = Logger.getLogger("BlockChainLogger");
    private static final HashMap<String, Logger> REGLOGGERS = new HashMap<>();

    static {
        try {
            EnsureLogFolderReady();
        } catch (IOException ex) {
            Logger.getLogger(LoggingHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            //we might need to either add or removed STD out or STD err outputs. but no for the time begin
            DEFAULTLOGGER.addHandler(getDefaultFileHandler("BlockChainLogger"));
            DEFAULTLOGGER.setLevel(Level.ALL);
            REGLOGGERS.put("BlockChainLogger", DEFAULTLOGGER);
        } catch (IOException | SecurityException ex1) {
            ex1.printStackTrace(System.err);
        }
    }

    public static final Logger getAClassLogger(String classname) {
        if (REGLOGGERS.containsKey(classname)) {
            return REGLOGGERS.get(classname);
        } else {
            Logger tmplogger = Logger.getLogger(classname);
            try {
                //we might need to either add or removed STD out or STD err outputs. but no for the time begin
                tmplogger.addHandler(getDefaultFileHandler(String.format("BlockChain_%s", classname)));
                tmplogger.setLevel(Level.ALL);
            } catch (IOException | SecurityException ex1) {
                ex1.printStackTrace(System.err);
            }
            REGLOGGERS.put(classname, tmplogger);
            return tmplogger;
        }
    }

    public static String getCallerCallerClassName() {
        StackTraceElement[] stElements = null;
        try {
            stElements = Thread.currentThread().getStackTrace();
        } catch (SecurityException err) {
            //likely security related. we are on a limited enviroment?
        }
        if (stElements != null) {
            //so the stack trace SHOULD be parked at THIS class. or on the Thead stack call
            //so we need to dive until we find the last iteration of THIS or Thread Stack Trace. 
            for (StackTraceElement ste : stElements) {
                //ensure we get the caller class and not THIS class
                if (!ste.getClassName().equals(LoggingHelper.class.getName()) && ste.getClassName().indexOf(Thread.class.getName()) != 0) {
                    return ste.getClassName();
                }
            }
        }
        //not detected? WHY!?
        return null;
    }

    public static final Logger getClassLoggerForMe() {
        String caller = getCallerCallerClassName();
        if (caller != null) {
          return getAClassLogger(caller);
        }else{
          return getDefaultLogger();  
        }
    }

    public static final Logger getDefaultLogger() {
        return DEFAULTLOGGER;
    }
}
