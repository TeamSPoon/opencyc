package org.opencyc.elf.message;

//// Internal Imports

import org.opencyc.elf.Status;

//// External Imports

/**
 * Provides the status message that is sent from the job assigner to the higher-level
 * executor.
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
public class JobAssignmentStatus extends GenericMsg {
  
  //// Constructors
  
  /** Creates a new instance of JobAssignmentStatus. */
  public JobAssignmentStatus() {
  }
  
  //// Public Area

  /**
   * Gets the job assigner status
   *
   * @return the job assigner status
   */
  public Status getStatus () {
    return status;
  }

  /**
   * Sets the job assigner status
   *
   * @param status the job assigner status
   */
  public void setStatus (Status status) {
    this.status = status;
  }

  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the job assigner status
   */
  protected Status status;
  
  //// Main
  
}
