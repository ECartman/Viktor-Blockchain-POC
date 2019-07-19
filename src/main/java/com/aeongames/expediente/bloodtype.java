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

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public enum bloodtype {
    On("O-"),
    Op("O+"),
    An("A-"),
    Ap("A+"),
    Bn("B-"),
    Bp("B+"),
    ABn("AB-"),
    ABp("AB+"),
    Unknown("Unknown");

    private final String BloodDescription;

    private bloodtype(String BloodT) {
        this.BloodDescription = BloodT;
    }

    @Override
    public String toString() {
        return BloodDescription;
    }

}
