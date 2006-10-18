/*
 * QueryListener.java
 *
 * Created on August 10, 2004, 2:06 PM
 */

package org.opencyc.util.query;

import java.util.EventListener;

/**
 * @version $Id$
 * @author  mreimers
 */
public interface QueryListener extends EventListener {
  public void queryChanged(QueryChangeEvent e);
}
