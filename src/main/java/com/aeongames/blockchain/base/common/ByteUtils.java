/*
 * 
 *   Copyright © 2018 Eduardo Vindas Cordoba. All rights reserved.
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

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class ByteUtils {

    /**
     * reverse a byte array in place
     *
     * @param data
     * @return
     */
    public static byte[] reverse(byte[] data) {
        for (int i = 0, j = data.length - 1; i < data.length / 2; i++, j--) {
            data[i] ^= data[j];
            data[j] ^= data[i];
            data[i] ^= data[j];
        }
        return data;
    }

    public static String ByteArrayToString(final byte Array[]){
        Objects.requireNonNull(Array,"Invalid Byte Array");
        StringBuilder hexString = new StringBuilder(); // This will contain hash as hexidecimal
        for (byte byte_data:Array) {
            String hex = Integer.toHexString(0xff & byte_data);//to unsigned int
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    public static String ByteArrayToString(final ByteBuffer buffer){
        Objects.requireNonNull(buffer,"Invalid Byte Array");
        buffer.rewind();
        int num=buffer.remaining();
        byte values[]= new byte[num];
        buffer.get(values);
        buffer.rewind();
        return ByteArrayToString(values);
    }

    public static byte[] HexToBytes(String hex) {
        Objects.requireNonNull(hex, "the hex value cannot be null");
       final byte values[] = new byte[hex.length()/2];
        for (int i = 0; i < values.length; i++) {
            int index = i * 2;
            int j = Integer.parseUnsignedInt(hex.substring(index, index + 2), 16);
            values[i] = (byte) j;
        }
        return values;
    }
    
    //TODO: move to ByteUtils or merge both files. 
    /**
     * creates an array that contains the provided data.
     *
     * @param data the data that will be stored in the array
     * @return the array contained the value.
     */
    public static final ByteBuffer to_byte_buff(final byte data) {
        return ByteBuffer.allocate(1).put(data).asReadOnlyBuffer();
    }
    
    /**
     * WARNING the provided array MAY BE MUTABLE !!!
     * PLEASE if using for the Blockchain MAKE SURE no other processhave access to 
     * write or modify it. 
     * 
     * wraps the data provided into a byteBuffer. 
     * please not that the ByteBuffer does NOT copy the data. make sure the 
     * provided byte array is immutable. otherwise use to_safe_bytebuff
     * @param data
     * @return a ReadOnly ByteBuffer that wraps the provided data 
     */
    public static final ByteBuffer to_byte_buff(final byte[] data) {
        return ByteBuffer.allocate(data.length).put(data).asReadOnlyBuffer();
    }
    /**
     * returns a ReadOnly ByteBuffer that stores a <Strong>immutable COPY</strong> of the
     * provided array. please note that this might impact performance as it needs 
     * to do a memory copy of the provided data. 
     * @param data the data to copy and wrap into a ByteBuffer.
     * @return (immutable) ReadOnly ByteBuffer that contains a copy of the provided data.
     */
    public static final ByteBuffer to_safe_bytebuff(byte[] data){
        final byte[] immutablearray= Arrays.copyOf(data, data.length);
        return to_byte_buff(immutablearray);
    }

    /**
     * creates an array that contains the provided data.
     *
     * @param data the data that will be stored in the array
     * @return the array contained the value.
     */
    public static final ByteBuffer to_byte_buff(final short data) {
        return ByteBuffer.allocate(2).putShort(data).asReadOnlyBuffer();
    }

    /**
     * creates an array that contains the provided data.
     *
     * @param data the data that will be stored in the array
     * @return the array contained the value.
     */
    public static final ByteBuffer to_byte_buff(char data) {
        return ByteBuffer.allocate(2).putChar(data).asReadOnlyBuffer();
    }

    /**
     * creates an array that contains the provided data.
     *
     * @param data the data that will be stored in the array
     * @return the array contained the value.
     */
    public static final ByteBuffer to_byte_buff(int data) {
        return ByteBuffer.allocate(4).putInt(data).asReadOnlyBuffer();
    }

    /**
     * creates an array that contains the provided data.
     *
     * @param data the data that will be stored in the array
     * @return the array contained the value.
     */
    public static final ByteBuffer to_byte_buff(long data) {
        return ByteBuffer.allocate(8).putLong(data).asReadOnlyBuffer();
    }

    /**
     * creates an array that contains the provided data.
     *
     * @param data the data that will be stored in the array
     * @return the array contained the value.
     */
    public static final ByteBuffer to_byte_buff(float data) {
        return ByteBuffer.allocate(4).putFloat(data).asReadOnlyBuffer();
    }

    /**
     * creates an array that contains the provided data.
     *
     * @param data the data that will be stored in the array
     * @return the array contained the value.
     */
    public static final ByteBuffer to_byte_buff(double data) {
        return ByteBuffer.allocate(8).putDouble(data).asReadOnlyBuffer();
    }

    /**
     * creates an array that contains the provided data.
     *
     * @param data the data that will be stored in the array
     * @return the array contained the value.
     */
    public static final ByteBuffer to_byte_buff(boolean data) {
        return ByteBuffer.allocate(1).putDouble(data ? 0x01 : 0x00).asReadOnlyBuffer();
    }


}
