/*
 * 
 *   Copyright © ï¿½ 2019 Eduardo Vindas Cordoba. All rights reserved.
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
package com.aeongames.blockchain.consensus.pbft.client;

/**
 * TODO: consider migrating to ENUM
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public interface JSONTYPES {

    public static final String JSONOBJECTTYPE_REPLY = "REPLY";
    public static final String JSONOBJECTTYPE_REQUEST = "REQUEST";
    public static final String JSONOBJECTTYPE_PREPRE="PRE-PREPARE";
     public static final String JSONOBJECTTYPE_PRE="PREPARE";
     public static final String JSONOBJECTTYPE_COMMIT="COMMIT";
     public static final String JSONOBJECTTYPE_CHECK="CHECKPOINT";
     public static final String JSONOBJECTTYPE_VIEWCHNG="VIEW-CHANGE";
     public static final String JSONOBJECTTYPE_NEWVIEW="NEW-VIEW";
    
    
    public static final String JSONOBJECTTYPE = "type";
    public static final String JSONOBJECTVIEWNUM = "view-number";
    public static final String JSONOBJECTTIMESTAMP = "timestamp";
    public static final String JSONOBJECTREPLICAID = "replica-id";
    public static final String JSONOBJECTRESULT = "result";
    //public static final String JSONOBJECTBLOCKID = "Block-id";

    public static final String JSONBBLOCK = "Block";
    public static final String JSONOP = "operation";
    public static final String JSONOPTIME = "timestamp";
    public static final String JSONOPCLIENT= "client";

}
