/*
 *
 * Copyright © 2008-2012,2019 Eduardo Vindas Cordoba. All rights reserved.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.aeongames.edi.utils.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * TODO: this is an OLD code I wrote for a HP Project, this needs review and migration. (last changed 2012)
 *
 * this class is intended to save and load data from a properties file the
 * property file is a text file that use a pattern:
 * <br/>
 * variable=value
 * <br/>
 * ...
 * <br/>
 * #comment ...
 * <br/>
 * and so on... it is useful to read user editable settings . this class has
 * also been adapted to use a XML schema implemented by sun now oracle...
 *
 * @author Eduardo Vindas C
 * @version 2.0
 * @since 0.4
 */
public class properties_File {
    private final Path FilePath;
    private final String ResourcePath;
    private final Source FromWhere;
    private final java.util.Properties configFile = new java.util.Properties();
    public enum Source {
        FILE,
        RESOURCE,
        BOTH;
    }

    public properties_File(){
        throw new IllegalCallerException("illegal instance creation");
    }

    public properties_File(Path FileSource) throws java.io.IOException {
        this(Source.FILE,Optional.of(Objects.requireNonNull(FileSource,"A valid Path is Required")),Optional.empty());
    }

    public properties_File(String InternalSource) throws java.io.IOException {
        this(Source.RESOURCE,Optional.empty(),Optional.of(Objects.requireNonNull(InternalSource,"A valid String is Required")));
    }

    public properties_File(Path FileSource,String InternalSource) throws java.io.IOException {
        this(Source.BOTH,Optional.of(Objects.requireNonNull(FileSource,"A valid Path is Required")),Optional.of(Objects.requireNonNull(InternalSource,"A valid String is Required")));
    }

    public properties_File(Source infoSource, Optional<Path> FileSource,Optional<String> InternalSource) throws java.io.IOException {
        FromWhere=Objects.requireNonNull(infoSource);
        FileSource=Objects.requireNonNullElse(FileSource,Optional.empty());
        if(FromWhere==Source.FILE&&FileSource.isEmpty()) {
            throw new FileSystemNotFoundException("invalid File");
        }else{
            FilePath= FileSource.orElse(null);
        }
        InternalSource=Objects.requireNonNullElse(InternalSource,Optional.empty());
        if(FromWhere==Source.RESOURCE&&InternalSource.isEmpty()) {
            throw new FileSystemNotFoundException("invalid File");
        }else{
            ResourcePath= InternalSource.orElse(null);
        }
        if(FromWhere==Source.BOTH&&ResourcePath==null&& FilePath==null){
            throw new NullPointerException("we need at least one source of information");
        }
        if(ResourcePath!=null){
            loadwithin(ResourcePath,this.getClass(),true,false);
        }
        if(FilePath!=null){
            LoadSettings(FilePath,true,false);
        }
    }



    /**
     * Loads And if specified Concatenates all the settings already loaded.
     * please be aware that this will rewrite settings that have not been saved
     * if settings with the same name exists. also if Concat is set to false all
     * new settings will be dropped.
     *
     * @param file the File location to read.
     * @param concat if concatenate the values.
     * @param XML if a XML file will be loaded or a prop file
     * @throws java.io.IOException
     */
    private synchronized void LoadSettings(Path file, boolean concat, boolean XML) throws java.io.IOException {
        if (Files.exists(file) && Files.isRegularFile(file)) {
            if (!concat) {
                configFile.clear();
            }
            try(InputStream resource= Files.newInputStream(file, StandardOpenOption.READ)) {//--> autoclose resource.
                if (!XML) {
                    configFile.load(resource);
                } else {
                    configFile.loadFromXML(resource);
                }
            }
        } else {
            throw new FileNotFoundException("File Does not Exist or is A Directory");
        }
    }


    /**
     *
     * @param within the internal resource to read
     * @param classfrom the class calling to read
     * @param concat if concatenate the values or load from 0 and remove all new
     * variables and changes
     * @param XML if the read file is XML or props file
     * @throws java.io.IOException
     */
    public final synchronized void loadwithin(String within, Class<?> classfrom, boolean concat, boolean XML) throws java.io.IOException {
        if (!concat) {
            configFile.clear();
        }
        InputStream resource = classfrom.getResourceAsStream(within);
        if (resource != null) {
            if (!XML) {
                try(resource) {
                    configFile.load(resource);
                }
            } else {
                try(resource) {
                    configFile.loadFromXML(resource);
                }
            }
        } else {
            throw new IOException("Cannot Load The Resource");
        }
    }

    public synchronized Set<Object> getKeyset() {
        if (configFile != null) {
            return configFile.keySet();
        } else {
            return null;
        }
    }

    /**
     * reads and gather a property, if it is not found or the property is null
     * returns null
     *
     * @param KeyName they property name
     * @return
     */
    public synchronized String getProperty(String KeyName) {
        if (configFile != null) {
            return configFile.getProperty(KeyName);
        } else {
            return null;
        }
    }

    /**
     * sets the specific value to the provided property, if the property had a
     * previous value this method will return that value if none or an error is
     * found will return null;
     *
     * @param KeyName the property to set
     * @param Value the value of this property
     * @return a previous stored value under this property or null if none or
     * error Error can be caused due the OBject is corrupted or key is null
     */
    public synchronized Object Setproperty(String KeyName, String Value) {
        if (configFile != null && KeyName != null) {
            Object oldv = getProperty(KeyName);
            if (oldv != null && oldv.equals(Value)) {
                //no change needed
            } else {
                oldv = configFile.setProperty(KeyName, Value);
            }
            return oldv;
        } else {
            return null;
        }
    }

    public synchronized void clearMemory() {
        configFile.clear();
    }

}
