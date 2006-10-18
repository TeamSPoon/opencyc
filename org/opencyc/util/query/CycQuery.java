/*
 * CycQuery.java
 *
 * Created on August 11, 2004, 10:48 AM
 */

package org.opencyc.util.query;

import org.opencyc.inference.InferenceStatus;
/**
 * @version $Id$
 * @author  mreimers
 */
public interface CycQuery extends Query {
  
  public InferenceStatus getInferenceStatus();
}
