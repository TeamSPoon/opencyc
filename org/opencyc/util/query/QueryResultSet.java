/*
 * ResultSet.java
 *
 * Created on August 10, 2004, 2:04 PM
 */

package org.opencyc.util.query;

import java.util.Date;
import java.util.Iterator;

/**
 * @version $Id$
 * @author  mreimers
 */
public interface QueryResultSet {
  public Iterator getResultSetIterator();
  public Query getQuery();
  public Date getTimeStamp();
  public void addQueryResult(Object queryResult);
  public Justification getJustificationForIndex(int i);
  
}
