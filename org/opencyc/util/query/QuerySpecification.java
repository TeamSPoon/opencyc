/*
 * Query.java
 *
 * Created on August 10, 2004, 2:04 PM
 */

package org.opencyc.util.query;

import java.util.Set;

/**
 * @version $Id$
 * @author  mreimers
 */
public interface QuerySpecification {
  
  public String getGloss();
  
  public Object getQuestion();
  
  public Set getConstraints();
  public Set getFilteredConstraints(Class constraintType);
  
  public Object clone();
}
