package org.opencyc.cycobject;


/*****************************************************************************
 * This class implements a Cyc Fort (First Order Reified Term.
 *
 * @version $Id$
 * @author
 *      Stefano Bertolo<BR>
 *
 * Copyright 2001 OpenCyc.org, license is open source GNU LGPL.<p>
 * <a href="http://www.opencyc.org">www.opencyc.org</a>
 * <a href="http://www.sourceforge.net/projects/opencyc">OpenCyc at SourceForge</a>
 *****************************************************************************/

import java.io.Serializable;
//import org.opencyc.xml.XMLPrintWriter;

public abstract class CycFort implements Serializable {


  /**
   * Return a cyclified string representation of the OpenCyc FORT.
   * Embedded constants are prefixed with ""#$".
   *
   * @return a cyclified <tt>String</tt>.
   */
  public abstract String cyclify();

  /**
   * Prints the XML representation of the CycFort to an <tt>XMLPrintWriter</tt>
   */
/*
  public abstract void toXML (XMLPrintWriter xml_writer, int indent, boolean relative);
*/
}

