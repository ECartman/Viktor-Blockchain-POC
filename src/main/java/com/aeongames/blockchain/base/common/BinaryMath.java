/*
 *
 *   Copyright © ï¿½ 2019 Eduardo Vindas Cordoba. All rights reserved.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 *
 */
package com.aeongames.blockchain.base.common;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public final class BinaryMath {

    /**
     * returns the next possible power of 2 for a 32 bit number up to 2^31
     * http://graphics.stanford.edu/~seander/bithacks.html#RoundUpPowerOf2
     * calculates
     *
     * @param value the value that we need to know the next two to the power of
     * X
     * @return the next power of two that is bigger than the provided value.
     * returns a Long so it can be used within the scope of all possible numeric
     * results however the only power of 2 that requires Long is 2^31
     */
    public static final long round_next_power_two(final int value) {
        //avoid loss of precision
        long calculatedRound = getUnsignedInteger(value);
        calculatedRound--;
        calculatedRound |= calculatedRound >>> 1;
        calculatedRound |= calculatedRound >>> 2;
        calculatedRound |= calculatedRound >>> 4;
        calculatedRound |= calculatedRound >>> 8;
        calculatedRound |= calculatedRound >>> 16;
        calculatedRound++;
        calculatedRound += (calculatedRound == 0b0) ? 1 : 0; // handle v == 0 edge case
        return calculatedRound;
    }

    /**
     * tell us if the UNSIGNED BITS of an {@code int} is a Power of two in
     * relation of its bits therefore all positive power of two AND also
     * {@code 0x80000000} (a negative number) but a unsigned power of 2 will be
     * valid power of 2 (as the provided value is handle as IF were unsigned
     * integer)
     *
     * @param value the unsigned integer to evaluate
     * @return {@code true} if the bits represent a power of 2 number, false
     * otherwise (works only for Unsigned values, with the exception of
     * 0x80000000 that is a negative number (however a power of 2 if seen as
     * unsigned integer.
     */
    public static final boolean is_power_of_two(int value) {
        return (value != 0 && value != 1) && ((value & (value - 1)) == 0);
    }

    /**
     * Converts the argument to a {@code long} by an unsigned conversion. so for
     * instance a 0x80000000 instead of converting to a negative number it
     * becomes a Positive number as the bit 32 is flipped and is not used as
     * sign.
     *
     * @param Raw_flipped_bytes the Unsigned bits that needs to become a
     * meaningful value
     * @return a Long value that stores the Unsigned value that represent the
     * provided bit data.
     * @see Integer#toUnsignedLong(int)
     */
    public static final long getUnsignedInteger(int Raw_flipped_bytes) {
        return Integer.toUnsignedLong(Raw_flipped_bytes);
    }

    /**
     * transfer the first 32 bits of a 64 bit long variable into a integer. note
     * that this conversion is NOT CASTING what is doing is parsing the bits
     * from a long to the integer up to 0xffffffff note that Java does not
     * understand unsigned values therefore 0xffffffff is interpret as -1 if you
     * need a numeric representation of a integer use Long
     * {@code}getUnsignedInteger
     *
     * @param unsigned_Integer the Long parameter that contains at 32 meaningful
     * bits
     * @return a integer that stores from 0x0 up to 0xffffffff (-1);
     */
    public static int ConvertLongToIntBitwise(long unsigned_Integer) {
        // from the bit 32(exclusive) and up to bit 64 are ignored
        // the first part gathers the first 31 bits and cast it to integer, then the result is OR to the last but (if the bit 32 is flipped or not
        return ((int) (unsigned_Integer & 0x7fffffff)) | ((unsigned_Integer & 0x80000000L) == 0x80000000L ? 0x80000000 : 0b0);
    }

    /**
     * returns the result of 2 to the power of the {@code power} this function
     * can return 2<sup>63</sup> as a positive value. beyond that is not
     * functional as the primitive cannot store such value.
     *
     * @param power the power to elevate 2. note however any value beyond 63
     * will return -1;
     * @return to the power of the {@code power} Unless power>63
     * @throws ArithmeticException if power is larger than 63
     */
    public static final long twoPower(int power) {
        if (power > 63) {
            throw new ArithmeticException("a Power Beyond the bounds of Possibilities");
        }
        return 0b01L << power;
    }

    /**
     * returns true if the value is divisible against two
     *
     * @param value the value to check if divisible against two
     * @return true if the value is divisible against two false otherwise
     */
    public static final boolean istwodivisible(long value) {
        return value % 2 == 0;
    }

    /**
     * returns true if the Unsigned binary value is divisible against two for
     * unsigned values this is faster than the modulo operator. see
     * <a href="https://en.wikipedia.org/wiki/Modulo_operation#Performance%20issues">Modulo
     * Operator Wiki</a>
     *
     * @param UnsignedValue the value to check
     * @return true if the Unsigned binary value is divisible against two
     */
    public static final boolean fasteristwodivisible(long UnsignedValue) {
        return (UnsignedValue & 0b1L) == 0;
    }

    /**
     * this Function is created with the intention with to calculate the max
     * depth (or the amount if levels) a tree will need in order to store the
     * provided amount regardless if it is or not a Perfect sized tree(binary)
     * (a power of 2)
     *
     * @param Value the amount of values to include into the tree
     * @return the amount of levels needed to store a provided value.
     */
    public static final int GetTreeDepthNeeded(int Value) {
        double Result = 0;
        if (Value > 0) {
            Result = Math.log((double) Value) / Math.log(2.0d);
        }
        return (int) Math.ceil(Result);
    }

    
}
