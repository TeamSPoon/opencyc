/* $Id$
 *
 * Copyright (c) 2003 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.elf.message;

/// Internal Imports

//// External Imports

/**
 * Provides the container for the prediction request messsage that is sent from the plan
 * selector and exectors to the predictor when they require a predicted input.
 * 
 * @version $Id$
 * @author Stephen L. Reed  
 * <p>Copyright 2001 Cycorp, Inc., license is open source GNU LGPL.
 * <p><a href="http://www.opencyc.org/license.txt">the license</a>
 * <p><a href="http://www.opencyc.org">www.opencyc.org</a>
 * <p><a href="http://www.sourceforge.net/projects/opencyc">OpenCyc at SourceForge</a>
 * <p>
 * THIS SOFTWARE AND KNOWLEDGE BASE CONTENT ARE PROVIDED ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE OPENCYC
 * ORGANIZATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE AND KNOWLEDGE
 * BASE CONTENT, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class PredictionRequestMsg extends GenericMsg {
  
  //// Constructors
  
  /** Creates a new instance of PredictionRequestMsg. */
  public PredictionRequestMsg() {
  }
  
  //// Public Area
  
  /**
   * Gets the object for which prediction data is requested
   *
   * @return the object for which prediction data is requested
   */
  public Object getObj () {
    return obj;
  }

  /**
   * Sets the object for which prediction data is requested.
   *
   * @param obj the object for which prediction data is requested
   */
  public void setObj (Object obj) {
    this.obj = obj;
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the object for which prediction data is requested
   */  
  protected Object obj;
  
  
  //// Main
  
}
