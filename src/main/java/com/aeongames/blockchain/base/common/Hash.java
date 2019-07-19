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
package com.aeongames.blockchain.base.common;

import static com.aeongames.logger.LoggingHelper.getDefaultLogger;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class Hash {
    /**
     * the Algorithm used for the Hash Calculation. this is variable and can
     * change. HOWEVER note that a system that has serialized values HAS already
     * committed to a particular Algorithm and computation un the future might
     * fail. therefore this value MIGHT require
     */
    private static final String HASH_ALGORITHM = "SHA-256";
    /**
     * the size of the hash data. in bytes therefore the size in bits of the
     * stored value is equivalent to HASHSIZE * 8
     */
    public static final int HASHSIZE = 32;
    public static final Hash INVALID = Hash.createFromSafeArray(new byte[HASHSIZE]);
    public static final Hash HASHOFZERO =Hash.of(new byte[0]);//--> 0 hash or e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
    /**
     * this hash bytes
     */
    private final byte[] bytes;

    /**
     * Stores a copy of the Hash value as it is provided. the Hash value needs
     * to be of 32 bytes as this chain will use on 256 bits hash
     *
     * WARNING: this constructor DOES NOT calculate the hash the param should be
     * the hashed value
     *
     * @param hash
     */
    protected Hash(final byte[] hash) {
        Objects.requireNonNull(hash, "the hash value cannot be null");
        if (hash.length != HASHSIZE) {
            throw new IllegalArgumentException("Hash is required to be 256 bit ");
        }
        // we need to ensure the hash we have is "immutable!" therefore lets use a copy
        //from whatever is provided 
        this.bytes =  Arrays.copyOf(hash,HASHSIZE);
    }

    /**
     * this constructor is intended to store an already digested hash value that
     * is stored on a ByteBuffer.
     *
     * @param hash the already calculated hash.
     */
    protected Hash(final ByteBuffer hash) {
        Objects.requireNonNull(hash, "the hash value cannot be null");
        if (hash.remaining() != HASHSIZE) {
            throw new IllegalArgumentException("Hash is required to be 256 bit ");
        }
        // we need to ensure the hash we have is "immutable!" therefore lets use a copy
        //from whatever is provided 
        this.bytes = new byte[HASHSIZE];
        hash.get(bytes);
    }

    /**
     * load a Hash that is stored on a HexString. NOTE the hex value must be
     * contiguous
     *
     * @param hex the hex string
     */
    public Hash(String hex) {
        Objects.requireNonNull(hex, "the hash value cannot be null");
        this.bytes = new byte[HASHSIZE];
        if (hex.length() != HASHSIZE * 2) {
            throw new IllegalArgumentException("the hash string cannot be less or more than 64 characters. ");
        }
        for (int i = 0; i < HASHSIZE; i++) {
            int index = i * 2;
            int j = Integer.parseUnsignedInt(hex.substring(index, index + 2), 16);
            bytes[i] = (byte) j;
        }
    }

    /**
     * WARNING this is An unsafe constructor of a Hash from a byte array.Unsafe
     * it keeps a reference to it.this is intended for internal code that Knows
     * what is doing and wants a faster result and less computation.
     *
     * @param hash - a 32 byte hash
     * @param caller - the caller of this constructor used to document into the
     * logger ignored if the called is this class.
     */
    protected Hash(byte[] hash, Class<?> caller) {
        Objects.requireNonNull(hash, "the hash value cannot be null");
        if (hash.length != HASHSIZE) {
            throw new IllegalArgumentException("Hash is required to be 256 bit ");
        }
        bytes = hash;
            StringBuilder trace_string = new StringBuilder();
            for (StackTraceElement traceElement : Thread.currentThread().getStackTrace()) {
                trace_string.append("\nat ").append(traceElement);
            }
            getDefaultLogger().log(Level.FINE, String.format("Unsafe Hash Constructor has been called, stack:\n%s ", trace_string));
    }

    /**
     * returns a ByteBuffer that contains the Hash value.note the returned
     * ByteBuffer is Read only.
     *
     * @return a read only ByteBuffer that contains the hash bytes
     */
    public ByteBuffer getHash() {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer = buffer.asReadOnlyBuffer();
        buffer.rewind();
        return buffer;
    }

    /**
     * WARNING this constructor is Designed to be fast HOWEVER it is
     * <strong>unsafe</strong>
     * due the fact that the byte array in parameter will be used as is. IF the
     * Hash would be used afterwards it MIGHT to bugs or problems due shared
     * mutable data. The only reason this method is allowed to be called is due
     * performace reasons
     *
     * @param hash please avoid using outside this context.
     * @return a new Hash that shares internal representation with the parameter
     * byte array.
     */
    private static Hash createFromSafeArray(byte[] hash) {
        return new Hash(hash, Hash.class);
    }

    /**
     * creates a new Hash from the provided ByteBuffer.
     *
     * @param data the data to use to calculate the hash.
     * @return a new instance of the Hash.
     */
    public static Hash hash(ByteBuffer data) {
        try {
            MessageDigest a = MessageDigest.getInstance(HASH_ALGORITHM);
            a.update(data);
            return Hash.createFromSafeArray(a.digest());
        } catch (NoSuchAlgorithmException e) {
            String message = String.format("UNABLE TO GENERATE A HASH, THE JVM CANNOT READ OR PROCESS ALGORITHM %s", HASH_ALGORITHM);
            getDefaultLogger().log(Level.SEVERE, message);
            throw new RuntimeException(message, e);
        }
    }
    
    public static Hash of(ByteBuffer data) {
        return hash(data);
    }

    
    /**
     * Merge 2 or more hashes used on block or Merkle hash calculation
     * CALLER must ensure that ALL values are non null if null if meet this function 
     * will throw null pointer
     * @param list
     * @return HASH_ALGORITHM(HASH_ALGORITHM(a||b||...||n))
     * @throws NullPointerException if any argument is null
     * @throws RuntimeException if hash underline function is not supported 
     */
    public static Hash merge(Hash ...list) {
        if(list!=null&& list.length>1){//lenght needs to be at least 2
        try {
            //here we call this method every time as it just load the hash method from the underline API
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            //Applies sha256 to our input, 
            for (Hash hash : list) {
                /*posible null pointer. but its fine if this value is null 
                this is a corrupted hash! */
            digest.update(hash.bytes);                
            }
            return Hash.createFromSafeArray(digest.digest(digest.digest()));
        } catch (NoSuchAlgorithmException e) {
            String message = String.format("UNABLE TO GENERATE A HASH, THE JVM CANNOT READ OR PROCESS ALGORITHM %s", HASH_ALGORITHM);
            getDefaultLogger().log(Level.SEVERE, message);
            throw new RuntimeException(message, e);
        }
        }else{
            throw new IllegalArgumentException("not enought Hashes provided");
        }
        
    }

    /**
     * HASH_ALGORITHM of arbitrary data
     *
     * @param data arbitrary data
     * @param offset start hashing at this offset (0 starts)
     * @param len hash length number of bytes
     * @return HASH_ALGORITHM(data)
     */
    public static Hash hash(byte[] data, int offset, int len) {
        try {
            MessageDigest a = MessageDigest.getInstance(HASH_ALGORITHM);
            a.update(data, offset, len);
            return Hash.createFromSafeArray(a.digest());
        } catch (NoSuchAlgorithmException e) {
            String message = String.format("UNABLE TO GENERATE A HASH, THE JVM CANNOT READ OR PROCESS ALGORITHM %s", HASH_ALGORITHM);
            getDefaultLogger().log(Level.SEVERE, message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Create a Hash with double SHA256 hash of arbitrary data
     *
     * @param data arbitrary data
     * @return a Hash initialized with SHA256(SHA256(data))
     */
    public static Hash of(byte[] data) {
        return hash(data, 0, data.length);
    }
    
    public static Hash hash(byte[] data) {
       return of(data);
    }

    /**
     * UUID created from the first 128 bits of HASH_ALGORITHM
     *
     * @return String
     */
    public String toUuidString() {
        String result = String.join("-", contentAsHex(0, 4), contentAsHex(4, 6),
                contentAsHex(6, 8), contentAsHex(8, 10),
                contentAsHex(10, 16));
        return result.toLowerCase();
    }

    /**
     * creates a copy of this hash underline byte array.
     *
     * @return a copy of the internal Hash value
     */
    public byte[] toByteArray() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    /**
     * Convert a Hash into a big positive integer. used for calculations on
     * arbitrary size math.
     *
     * @return Hash as big positive integer
     */
    public BigInteger toBigInteger() {
        return new BigInteger(1, toByteArray());
    }

    /**
     * returns the Hex representation of the Hash value.
     *
     * @return a Hex representation of the Hash value.
     */
    @Override
    public String toString() {
        StringBuilder hexString = new StringBuilder(); // This will contain hash as hexidecimal
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xff & bytes[i]);//to unsigned int 
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * CAUTION since this is a 32 bit result (integer)  it might be vulnerable! 
     * PLEASE AVOID LIKE THE PLAGE  ! 
     * please use the equals instead.
     * @return 
     */
     @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Arrays.hashCode(this.bytes);
        return hash;
    }

    @Override
    public boolean equals(Object other) {
        if (Objects.nonNull(other)) {
            if (this == other) {
                return true;
            } else if (getClass() != other.getClass()) {
                return false;
            } else {
                return Arrays.equals(bytes, ((Hash) other).bytes);
            }
        }
        return false;

    }



    private String contentAsHex(int From, int To) {
        if (To - From < 0) {
            throw new IllegalArgumentException(From + " > " + To);
        }
        if (bytes.length < To) {
            throw new ArrayIndexOutOfBoundsException("Cannot Calculate outside of Array size");
        }
        StringBuilder hexString = new StringBuilder(); // This will contain hash as hexidecimal
        for (int index = From; index < To; index++) {
            String hex = Integer.toHexString(0xff & bytes[index]);//to unsigned int 
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
