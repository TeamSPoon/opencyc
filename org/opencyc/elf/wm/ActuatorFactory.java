package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.a.Actuator;
import org.opencyc.elf.a.ConsoleOutput;

import org.opencyc.elf.bg.planner.Resource;

import org.opencyc.elf.wm.ResourcePool;

//// External Imports

/**
 *  ActuatorFactory is designed to create actuators.  There is a singleton instance of actuator factory.
 *
 * <P>Copyright (c) 2003 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
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
 * @version $Id$
 */
public class ActuatorFactory {
  
  //// Constructors
  
  /** Creates a new instance of ActuatorFactory and stores it in the singleton instance. */
  public ActuatorFactory() {
    actuatorFactory = this;
  }
  
  //// Public Area
  
  /**
   * Gets the singleton actuator factory instance.
   *
   * @return the singleton actuator factory instance
   */
  public static ActuatorFactory getInstance () {
    return actuatorFactory;
  }
    
  /**
   * Populates the actuator pool.
   */
  public void populateActuatorPool() {
    ConsoleOutput consoleOutput = new ConsoleOutput(Actuator.CONSOLE_OUTPUT, 
                                                    ResourcePool.getInstance().getResource(Resource.CONSOLE));
    
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the singleton actuator factory instance
   */
  protected static ActuatorFactory actuatorFactory;
}
