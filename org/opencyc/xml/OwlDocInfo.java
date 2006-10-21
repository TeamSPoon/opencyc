/* $Id$
 *
 * Copyright (c) 2003 - 2005 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.xml;

//// Internal Imports

//// External Imports

/**
 * <P>OwlDocInfo is designed to contain the information about an OWL document to be imported into Cyc.
 *
 * <P>Copyright (c) 2004 - 2005 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
 * @author reed
 * @date January 30, 2004, 3:30 PM
 * @version $Id$
 */
public final class OwlDocInfo {
  
  //// Constructors
  
  /**
   * Constructs a new OwlDocInfo object
   *
   * @param owlPath the path to the OWL docuement - as a URI
   * @parma nickname the ontology nickname
   * @param characterEncoding the character encoding scheme of the input OWL
   * document or null if default ASCII encoding
   * @param importMtName the microtheory name into which the non-definitional assertions are
   * placed
   * @param importGenlMtName the genlMt name for the import mt
   */
  public OwlDocInfo(final String owlPath,
                    final String nickname,
                    final String characterEncoding,
                    final String importMtName,
                    final String importGenlMtName) {
    this.owlPath = owlPath;
    this.nickname = nickname;
    this.characterEncoding = characterEncoding;
    this.importMtName = importMtName;
    this.importGenlMtName = importGenlMtName;
  }
  
  //// Public Area
  
  /** Returns the owl document path.
   *
   * @return the owl document path
   */
  public String getOwlPath() {
    return owlPath;
  }
  
  /** Returns the nickname.
   *
   * @return the nickname
   */
  public String getNickname() {
    return nickname;
  }
  
  /** Returns the character encoding scheme of the input OWL
   * document.
   *
   * @return the the character encoding scheme of the input OWL
   * document
   */
  public String getCharacterEncoding() {
    return characterEncoding;
  }
  
  /** Returns the microtheory name into which OWL content is imported.
   *
   * @return the microtheory name into which OWL content is imported
   */
  public String getImportMtName() {
    return importMtName;
  }
  
  /** Returns the genlMt name.
   *
   * @return the genlMt name
   */
  public String getImportGenlMtName() {
    return importGenlMtName;
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  /** path (url) to the OWL document */
  final private String owlPath;
  
  /** ontology nickiname */
  final private  String nickname;
  
  /** character encoding (e.g. UTF-8) */
  final private  String characterEncoding;
  
  /** the microtheory name into which OWL content is imported */
  final private  String importMtName;
  
  /** the genlMt name */
  final private  String importGenlMtName;
  
  //// Main
  
}

