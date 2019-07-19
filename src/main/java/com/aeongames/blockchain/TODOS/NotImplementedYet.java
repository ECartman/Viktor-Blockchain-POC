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
package com.aeongames.blockchain.TODOS;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  use this annotation to document you are lazy and want something done on later date. 
 *  and if called/used you are held no responsibility OF ANY KIND
 * @author Eduardo <cartman@aeongames.com>
 */
@Target({
    ElementType.ANNOTATION_TYPE, 
    ElementType.METHOD, 
    ElementType.CONSTRUCTOR,
    ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotImplementedYet {
   boolean value() default true;
   String expectedReleaseAvail() default "Soon™";

   /**
    * tells if the signature might or not change.
    * as rule of thumb trust it will change regardless.
    * @return
    */
   boolean MightSignatureChange() default true;
   
}


