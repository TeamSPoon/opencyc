/*
 * Justification.java
 *
 * Created on August 10, 2004, 2:06 PM
 */

package org.opencyc.util.query;

/**
 * @version $Id$
 * @author  mreimers
 */
public interface Justification {
  public QueryResultSet getQueryResultSet();
  public int getQueryResultSetIndex();
  public String toPrettyString();
}
