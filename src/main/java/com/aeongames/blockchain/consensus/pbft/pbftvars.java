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
package com.aeongames.blockchain.consensus.pbft;

/**
 * this interface provide nothing more than a few variables used on the 
 * client and server of the pbft.
 * TODO: 
 *  move this to configuration_files or other storage. 
 *  this is not done that way as I think this should be migrated to our own 
 *  pbft but time is not on our side. 
 * @author Eduardo <cartman@aeongames.com>
 */
interface pbftvars {
    public static final int TOLERANCE = 1;//this should be moved to configuration files. 
    public static final long TIMEOUT_MS = 500;
    public static final int REPLICA_COUNT = 3* TOLERANCE;//removed a +1 due it was off... 
    public static final String NAME="ViktorPBFT";
}
