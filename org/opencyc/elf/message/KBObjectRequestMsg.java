package org.opencyc.elf.message;

//// Internal Imports

//// External Imports

/**
 * Provides the container for the knowledge base object request message, that
 * is sent to the knowledge base.
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

public class KBObjectRequestMsg extends GenericMsg {
  
  //// Constructors
  
  /** Creates a new instance of KBObjectRequestMsg. */
    public KBObjectRequestMsg() {
  }
  
  //// Public Area
  
  /**
   * Gets the object for which data is requested.
   *
   * @return the object for which data is requested
   */
  public Object getObj () {
    return obj;
  }

  /**
   * Sets the object for which data is requested.
   *
   * @param obj the object for which data is requested
   */
  public void setObj (Object obj) {
    this.obj = obj;
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the object for which data is requested
   */  
  protected Object obj;
    
  //// Main
  
}
