package org.opencyc.elf.wm;

//// Internal Imports
import org.opencyc.elf.a.DirectActuator;

import org.opencyc.elf.bg.planner.Schedule;

import org.opencyc.elf.bg.predicate.PredicateExpression;
import org.opencyc.elf.bg.predicate.True;

import org.opencyc.elf.bg.taskframe.Action;
import org.opencyc.elf.bg.taskframe.Command;

import org.opencyc.elf.s.DirectSensor;

//// External Imports
import java.util.ArrayList;
import java.util.List;

/** ScheduleFactory populates the schedule libary.  There is a singleton instance.
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
public class ScheduleFactory {
  
  //// Constructors
  
  /** Creates a new instance of ScheduleFactory. */
  public ScheduleFactory() {
  }
  
  //// Public Area
  
  /** Gets the schedule factory singleton instance.
   *
   * @return the schedule factory singleton instance
   */
  public static ScheduleFactory getInstance() {
    return scheduleFactory;
  }
  
  /** Populates the schedule library. */
  public void populateScheduleLibrary () {
    // converse with user
    PredicateExpression predicateExpression = new PredicateExpression(new True()); 
    List plannedCommands = new ArrayList();
    plannedCommands.add(ActionLibrary.getInstance().getAction(Action.CONSOLE_PROMPTED_INPUT));
    List plannedTimeMilliseconds = new ArrayList();
    plannedTimeMilliseconds.add(new Integer(300000));
    Schedule schedule = new Schedule(predicateExpression, 
                                     plannedCommands, 
                                     plannedTimeMilliseconds, 
                                     DirectActuator.CONSOLE_OUTPUT, 
                                     DirectSensor.CONSOLE_INPUT);
    ScheduleLibrary.getInstance().addSchedule(Action.CONSOLE_PROMPTED_INPUT, schedule);
  }
    
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** the schedule factory singleton instance */
  protected static ScheduleFactory scheduleFactory;
  
  //// Main
  
}
