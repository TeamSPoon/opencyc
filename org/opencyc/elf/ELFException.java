package org.opencyc.elf;

//// Internal Imports

//// External Imports

/**
 * ELFException contains the ELF exception.
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
public class ELFException extends RuntimeException {
  
  //// Constructors
  
  /** 
   * Constructs a new runtime exception with null as its detail message. The cause is 
   * not initialized, and may subsequently be initialized by a call to 
   * Throwable.initCause(java.lang.Throwable). 
   */
  public ELFException() {
    super();
  }
  
  /** 
   * Constructs a new runtime exception with the specified detail message. The cause is 
   * not initialized, and may subsequently be initialized by a call to 
   * Throwable.initCause(java.lang.Throwable). 
   *
   * @param message the detail message. The detail message is saved for later retrieval by 
   * the Throwable.getMessage() method
   */
  public ELFException(String message) {
    super(message);
  }
  
  /**
   * Constructs a new runtime exception with the specified detail message and cause.  Note that 
   * the detail message associated with cause is not automatically incorporated in this runtime 
   * exception's detail message. 
   *
   * @param message the detail message (which is saved for later retrieval by the 
   * Throwable.getMessage() method)
   * @param cause the cause (which is saved for later retrieval by the 
   * Throwable.getCause() method). (A null value is permitted, and indicates that the cause is 
   * nonexistent or unknown.)
   */
  public ELFException(String message, Throwable cause) {
    super(message, cause);
  }
  
  
  //// Public Area
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
}
