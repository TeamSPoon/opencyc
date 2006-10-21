package org.opencyc.api;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.HashMap;

import org.opencyc.cycobject.ByteArray;
import org.opencyc.cycobject.CycAssertion;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycNart;
import org.opencyc.cycobject.CycObject;
import org.opencyc.cycobject.CycSymbol;
import org.opencyc.cycobject.CycVariable;
import org.opencyc.cycobject.Guid;
import org.opencyc.util.Log;


/**
 * A CFASL translating input stream.  All Java-native types which have logical sublisp equivalents
 * are translated automatically by this stream.  Classes implementing the CfaslTranslatingObject
 * interface are created using thier readObject() method.  Other CYC objects, such as
 * binding-lists and formulas, must be explicitly coerced using their static constructors.
 * 
 * @version $Id$
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class CfaslInputStream
  extends BufferedInputStream {
  /** No api trace. */
  public static final int API_TRACE_NONE = 0;

  /** Message-level api trace. */
  public static final int API_TRACE_MESSAGES = 1;

  /** Detailed api trace. */
  public static final int API_TRACE_DETAILED = 2;

  /** Parameter that, when true, causes a trace of the messages to and from the server. */
  public int trace = API_TRACE_NONE;

  /**
   * Parameter that when set true, causes CFASL object errors to be reported back as strings the
   * caller.
   */
  public boolean reportCfaslErrors = false;

  /** CFASL code */
  protected static final int CFASL_IMMEDIATE_FIXNUM_CUTOFF = 128;

  /** CFASL code */
  protected static final int CFASL_IMMEDIATE_FIXNUM_OFFSET = 256 - CFASL_IMMEDIATE_FIXNUM_CUTOFF;

  /** CFASL code */
  protected static final int CFASL_P_8BIT_INT = 0;

  /** CFASL code */
  protected static final int CFASL_N_8BIT_INT = 1;

  /** CFASL code */
  protected static final int CFASL_P_16BIT_INT = 2;

  /** CFASL code */
  protected static final int CFASL_N_16BIT_INT = 3;

  /** CFASL code */
  protected static final int CFASL_P_24BIT_INT = 4;

  /** CFASL code */
  protected static final int CFASL_N_24BIT_INT = 5;

  /** CFASL code */
  protected static final int CFASL_P_32BIT_INT = 6;

  /** DCFASL code */
  protected static final int CFASL_N_32BIT_INT = 7;

  /** DCFASL code */
  protected static final int CFASL_P_FLOAT = 8;

  /** CFASL code */
  protected static final int CFASL_N_FLOAT = 9;

  /** CFASL code */
  protected static final int CFASL_KEYWORD = 10;

  /** CFASL code */
  protected static final int CFASL_SYMBOL = 11;

  /** CFASL code */
  protected static final int CFASL_NIL = 12;

  /** CFASL code */
  protected static final int CFASL_LIST = 13;

  /** CFASL code */
  protected static final int CFASL_DOTTED = 17;

  /** CFASL code */
  protected static final int CFASL_VECTOR = 14;

  /** CFASL code */
  protected static final int CFASL_STRING = 15;

  /** CFASL code */
  protected static final int CFASL_CHARACTER = 16;

  /** CFASL code */
  protected static final int CFASL_HASHTABLE = 18;

  /** CFASL code */
  protected static final int CFASL_BTREE_LOW_HIGH = 19;

  /** CFASL code */
  protected static final int CFASL_BTREE_LOW = 20;

  /** CFASL code */
  protected static final int CFASL_BTREE_HIGH = 21;

  /** CFASL code */
  protected static final int CFASL_BTREE_LEAF = 22;

  /** CFASL code */
  protected static final int CFASL_P_BIGNUM = 23;

  /** CFASL code */
  protected static final int CFASL_N_BIGNUM = 24;

  /** CFASL code */
  protected static final int CFASL_GUID = 25;

  /** CFASL code */
  protected static final int CFASL_BYTE_VECTOR = 26;

  /** CFASL code */
  protected static final int CFASL_CONSTANT = 30;

  /** CFASL code */
  protected static final int CFASL_NART = 31;

  /** CFASL code */
  protected static final int CFASL_ASSERTION = 33;

  /** CFASL code */
  protected static final int CFASL_ASSERTION_SHELL = 34;

  /** CFASL code */
  protected static final int CFASL_ASSERTION_DEF = 35;

  /** CFASL code */
  protected static final int CFASL_SOURCE = 36;

  /** CFASL code */
  protected static final int CFASL_SOURCE_DEF = 37;

  /** CFASL code */
  protected static final int CFASL_AXIOM = 38;

  /** CFASL code */
  protected static final int CFASL_AXIOM_DEF = 39;

  /** CFASL code */
  protected static final int CFASL_VARIABLE = 40;

  /** CFASL code */
  protected static final int CFASL_INDEX = 41;

  /** CFASL code */
  protected static final int CFASL_SPECIAL_OBJECT = 50;

  /** CFASL code */
  protected static final int CFASL_EXTERNALIZATION = 51;

  /** CFASL code */
  protected static final int CFASL_UNICODE_CHAR = 52;

  /** CFASL code */
  protected static final int CFASL_UNICODE_STRING = 53;

  /** CFASL code */
  protected static final int CFASL_DICTIONARY = 64;

  /** CFASL code */
  protected static final int CFASL_SERVER_DEATH = -1;

  /** CFASL code */
  protected static final int DEFAULT_READ_LIMIT = 8192;
  static HashMap cfaslOpcodeDescriptions = null;

  /**
   * Initializes the opcode descriptions used in trace output.
   */
  protected void initializeOpcodeDescriptions() {
    cfaslOpcodeDescriptions = new HashMap();
    cfaslOpcodeDescriptions.put(new Integer(CFASL_IMMEDIATE_FIXNUM_CUTOFF), 
                                "CFASL_IMMEDIATE_FIXNUM_CUTOFF");
   
    cfaslOpcodeDescriptions.put(new Integer(CFASL_IMMEDIATE_FIXNUM_OFFSET), 
                                "CFASL_IMMEDIATE_FIXNUM_OFFSET");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_P_8BIT_INT), 
                                "CFASL_P_8BIT_INT");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_N_8BIT_INT), 
                                "CFASL_N_8BIT_INT");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_P_16BIT_INT), 
                                "CFASL_P_16BIT_INT");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_N_16BIT_INT), 
                                "CFASL_N_16BIT_INT");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_P_24BIT_INT), 
                                "CFASL_P_24BIT_INT");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_N_24BIT_INT), 
                                "CFASL_N_24BIT_INT");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_P_32BIT_INT), 
                                "CFASL_P_32BIT_INT");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_N_32BIT_INT), 
                                "CFASL_N_32BIT_INT");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_P_FLOAT), 
                                "CFASL_P_FLOAT");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_N_FLOAT), 
                                "CFASL_N_FLOAT");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_KEYWORD), 
                                "CFASL_KEYWORD");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_SYMBOL), 
                                "CFASL_SYMBOL");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_NIL), 
                                "CFASL_NIL");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_LIST), 
                                "CFASL_LIST");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_VECTOR), 
                                "CFASL_VECTOR");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_STRING), 
                                "CFASL_STRING");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_CHARACTER), 
                                "CFASL_CHARACTER");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_DOTTED), 
                                "CFASL_DOTTED");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_HASHTABLE), 
                                "CFASL_HASHTABLE");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_BTREE_LOW_HIGH), 
                                "CFASL_BTREE_LOW_HIGH");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_BTREE_LOW), 
                                "CFASL_BTREE_LOW");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_BTREE_HIGH), 
                                "CFASL_BTREE_HIGH");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_BTREE_LEAF), 
                                "CFASL_BTREE_LEAF");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_P_BIGNUM), 
                                "CFASL_P_BIGNUM");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_N_BIGNUM), 
                                "CFASL_N_BIGNUM");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_GUID), 
                                "CFASL_GUID");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_BYTE_VECTOR), 
                                "CFASL_BYTE_VECTOR");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_CONSTANT), 
                                "CFASL_CONSTANT");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_NART), 
                                "CFASL_NART");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_ASSERTION), 
                                "CFASL_ASSERTION");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_ASSERTION_SHELL), 
                                "CFASL_ASSERTION_SHELL");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_ASSERTION_DEF), 
                                "CFASL_ASSERTION_DEF");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_SOURCE), 
                                "CFASL_SOURCE");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_SOURCE_DEF), 
                                "CFASL_SOURCE_DEF");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_AXIOM), 
                                "CFASL_AXIOM");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_AXIOM_DEF), 
                                "CFASL_AXIOM_DEF");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_VARIABLE), 
                                "CFASL_VARIABLE");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_INDEX), 
                                "CFASL_INDEX");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_SPECIAL_OBJECT), 
                                "CFASL_SPECIAL_OBJECT");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_EXTERNALIZATION),
                                "CFASL_EXTERNALIZATION");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_UNICODE_CHAR), 
                                "CFASL_UNICODE_CHAR");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_UNICODE_STRING), 
                                "CFASL_UNICODE_STRING");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_DICTIONARY), 
                                "CFASL_DICTIONARY");
    
    cfaslOpcodeDescriptions.put(new Integer(CFASL_SERVER_DEATH), 
                                "CFASL_SERVER_DEATH");
  }

  /**
   * Creates a new CfaslInputStream to read data from the specified underlying input stream.
   * 
   * @param in the underlying input stream.
   */
  public CfaslInputStream(InputStream in) {
    super(in, DEFAULT_READ_LIMIT);
    initializeOpcodeDescriptions();

    if (Log.current == null) {
      Log.makeLog("cfasl.log");
    }
  }

  /**
   * Reads an Object from this CfaslInputStream.  Basic Java types are wrapped as appropriate (e.g.
   * ints become Integer objects).  New constants are missing name and GUID values and will be
   * completed by the caller to avoid recursion within the api call.
   * 
   * @return the object read from the binary OpenCyc input stream
   * 
   * @throws IOException if a communications error occurs
   */
  public Object readObject()
                    throws IOException {
    int cfaslOpcode = read();

    if (cfaslOpcode == CFASL_EXTERNALIZATION) {
      if (trace == API_TRACE_DETAILED) {
        Log.current.println("reading opcode = " + cfaslOpcode + " (" + 
                            cfaslOpcodeDescriptions.get(
                                  new Integer(cfaslOpcode)) + ")");
      }

      cfaslOpcode = read();
    }

    Object o = null;

    if (trace == API_TRACE_DETAILED) {
      Log.current.println("reading opcode = " + cfaslOpcode + " (" + 
                          cfaslOpcodeDescriptions.get(new Integer(
                                                            cfaslOpcode)) + ")");
    }

    if (cfaslOpcode >= CFASL_IMMEDIATE_FIXNUM_OFFSET) {
      o = new Integer(cfaslOpcode - CFASL_IMMEDIATE_FIXNUM_OFFSET);

      if (trace == API_TRACE_DETAILED) {
        Log.current.println("Reading Immediate Fixnum: " + o);
      }
    }
    else {
      switch (cfaslOpcode) {
      case CFASL_P_8BIT_INT:
        o = new Integer(readFixnumBody(1, 
                                       1));

        break;

      case CFASL_N_8BIT_INT:
        o = new Integer(readFixnumBody(1, 
                                       -1));

        break;

      case CFASL_P_16BIT_INT:
        o = new Integer(readFixnumBody(2, 
                                       1));

        break;

      case CFASL_N_16BIT_INT:
        o = new Integer(readFixnumBody(2, 
                                       -1));

        break;

      case CFASL_P_24BIT_INT:
        o = new Integer(readFixnumBody(3, 
                                       1));

        break;

      case CFASL_N_24BIT_INT:
        o = new Integer(readFixnumBody(3, 
                                       -1));

        break;

      case CFASL_P_32BIT_INT:
        o = new Integer(readFixnumBody(4, 
                                       1));

        break;

      case CFASL_N_32BIT_INT:
        o = new Integer(readFixnumBody(4, 
                                       -1));

        break;

      case CFASL_P_FLOAT:
        o = new Double(readFloatBody(1));

        break;

      case CFASL_N_FLOAT:
        o = new Double(readFloatBody(-1));

        break;

      case CFASL_P_BIGNUM:
        o = readBignumBody(1);

        break;

      case CFASL_N_BIGNUM:
        o = readBignumBody(-1);

        break;

      case CFASL_KEYWORD:

        // Keywords can be distinguished from Symbols by internal evidence
        o = readKeyword();

        break;

      case CFASL_SYMBOL:
        o = readSymbol();

        break;

      case CFASL_NIL:
        o = CycObjectFactory.nil;

        break;

      case CFASL_LIST:
        o = readCycList();

        break;

      case CFASL_DOTTED:
        o = readCons();

        break;

      case CFASL_VECTOR:
        return reportUnhandledCfaslOpcode(cfaslOpcode);

      case CFASL_STRING:

        int off = 0;
        int len = readInt();
        byte[] s = new byte[len];

        while (off < len) {
          off += read(s, 
                      off, 
                      len - off);
        }

        o = new String(s, 
                       "UTF-8");

        break;

      case CFASL_CHARACTER:
        o = new Character((char) read());

        break;

      case CFASL_HASHTABLE:
        return reportUnhandledCfaslOpcode(cfaslOpcode);

      case CFASL_BTREE_LOW_HIGH:
        return reportUnhandledCfaslOpcode(cfaslOpcode);

      case CFASL_BTREE_LOW:
        return reportUnhandledCfaslOpcode(cfaslOpcode);

      case CFASL_BTREE_HIGH:
        return reportUnhandledCfaslOpcode(cfaslOpcode);

      case CFASL_BTREE_LEAF:
        return reportUnhandledCfaslOpcode(cfaslOpcode);

      case CFASL_GUID:
        o = readGuid();
        break;

      case CFASL_UNICODE_STRING:
        o = readUnicodeString();
        break;

      case CFASL_UNICODE_CHAR:
        o = readUnicodeChar();
        break;

      case CFASL_BYTE_VECTOR:
        o = readByteArray();
      break;

      case CFASL_CONSTANT:
        o = readConstant();

        break;

      case CFASL_NART:
        o = readNart();

        break;

      case CFASL_ASSERTION:
        o = readAssertion();

        break;

      case CFASL_ASSERTION_SHELL:
        return reportUnhandledCfaslOpcode(cfaslOpcode);

      case CFASL_ASSERTION_DEF:
        return reportUnhandledCfaslOpcode(cfaslOpcode);

      case CFASL_SOURCE:
        return reportUnhandledCfaslOpcode(cfaslOpcode);

      case CFASL_SOURCE_DEF:
        return reportUnhandledCfaslOpcode(cfaslOpcode);

      case CFASL_AXIOM:
        return reportUnhandledCfaslOpcode(cfaslOpcode);

      case CFASL_AXIOM_DEF:
        return reportUnhandledCfaslOpcode(cfaslOpcode);

      case CFASL_VARIABLE:
        o = readVariable();

        break;

      case CFASL_INDEX:
        return reportUnhandledCfaslOpcode(cfaslOpcode);

      case CFASL_SPECIAL_OBJECT:
        return reportUnhandledCfaslOpcode(cfaslOpcode);

      case CFASL_DICTIONARY:
        return reportUnhandledCfaslOpcode(cfaslOpcode);

      case CFASL_SERVER_DEATH:
        return reportUnhandledCfaslOpcode(cfaslOpcode);

      default:
        return reportUnhandledCfaslOpcode(cfaslOpcode);
      }
    }

    if (trace == API_TRACE_DETAILED) {
      try {
        // If object o understands the safeToString method, then use it.
        Method safeToString = o.getClass().getMethod("safeToString", 
                                                     null);
        Log.current.println("readObject = " + safeToString.invoke(
                                                    o, 
                                                    null) + " (" + o.getClass() + ")");
      }
       catch (Exception e) {
        Log.current.println("readObject = " + o + " (" + o.getClass() + ")");
      }
    }

    return o;
  }

  /**
   * Reports the unhandled cfasl opcode or throws an exception.
   * 
   * @param cfaslOpcode the unhandled cfasl opcode
   * 
   * @return the unhandled cfasl opcode
   * 
   * @throws CfaslInputStreamClosedException if the socket connection is closed by the peer
   * @throws RuntimeException if the error is not logged and ignored
   */
  protected Object reportUnhandledCfaslOpcode(int cfaslOpcode) {
    String errorMessage;

    if (cfaslOpcode == -1) {
      throw new CfaslInputStreamClosedException("Cfasl connection closed by peer");
    }
    else {
      errorMessage = "Unknown cfasl opcode: " + cfaslOpcode;
    }

    if (reportCfaslErrors) {
      Log.current.println(errorMessage);

      return Integer.toString(cfaslOpcode);
    }
    else {
      //TODO create a new exception class for this case.
      throw new RuntimeException(errorMessage);
    }
  }

  /**
   * Reads an char from this CfaslInputStream.  If the next item on the stream is not a char, throw
   * an exception, and leave that object on the input stream.
   * 
   * @return the character read
   * 
   * @throws IOException if a communications error occurs
   * @throws RuntimeException if an unexpected cfasl opcode occurs
   */
  public char readChar()
                throws IOException {
    mark(DEFAULT_READ_LIMIT);

    int cfaslOpcode = read();

    if (cfaslOpcode == CFASL_CHARACTER) {
      return (char) read();
    }

    reset();
    throw new RuntimeException("Expected a char but received opCode=" + cfaslOpcode);
  }

  /**
   * Reads a double from this CfaslInputStream.  If the next item on the stream is not a double,
   * throw an exception, and leave that object on the input stream.
   * 
   * @return the double read
   * 
   * @throws IOException if a communications error occurs
   * @throws RuntimeException if an unexpected cfasl opcode occurs
   */
  public double readDouble()
                    throws IOException {
    mark(DEFAULT_READ_LIMIT);

    int cfaslOpcode = read();

    switch (cfaslOpcode) {
    case CFASL_P_FLOAT:
      return readFloatBody(1);

    case CFASL_N_FLOAT:
      return readFloatBody(-1);

    default:
      reset();
      throw new RuntimeException("Expected a double but received OpCode=" + cfaslOpcode);
    }
  }

  /**
   * Reads an int from this CfaslInputStream.  If the next item on the stream is not an int, throw
   * an exception, and leave that object on the input stream.  Bignum ints are not allowed.
   * 
   * @return the int read
   * 
   * @throws IOException if a communications error occurs
   * @throws RuntimeException if an unexpected cfasl opcode occurs
   */
  public int readInt()
              throws IOException {
    mark(DEFAULT_READ_LIMIT);

    int cfaslOpcode = read();

    if (cfaslOpcode >= CFASL_IMMEDIATE_FIXNUM_OFFSET) {
      return cfaslOpcode - CFASL_IMMEDIATE_FIXNUM_OFFSET;
    }
    else {
      switch (cfaslOpcode) {
      case CFASL_P_8BIT_INT:
        return readFixnumBody(1, 
                              1);

      case CFASL_N_8BIT_INT:
        return readFixnumBody(1, 
                              -1);

      case CFASL_P_16BIT_INT:
        return readFixnumBody(2, 
                              1);

      case CFASL_N_16BIT_INT:
        return readFixnumBody(2, 
                              -1);

      case CFASL_P_24BIT_INT:
        return readFixnumBody(3, 
                              1);

      case CFASL_N_24BIT_INT:
        return readFixnumBody(3, 
                              -1);

      case CFASL_P_32BIT_INT:
        return readFixnumBody(4, 
                              1);

      case CFASL_N_32BIT_INT:
        return readFixnumBody(4, 
                              -1);

      default:
        reset();
        throw new RuntimeException("Expected an int but received OpCode=" + cfaslOpcode);
      }
    }
  }

  /**
   * Reads the body of a CFASL Fixnum (everything but the opcode) from this CFASL input stream.
   * 
   * @param nBytes  The number of bytes to read
   * @param sign    The sign of the Fixnum (-1 or +1)
   * 
   * @return an int holding the CFASL Fixnum read in
   * 
   * @throws IOException if a communications error occurs
   * @throws ArithmeticException if nBytes > 4 or if the integer read in does not fit into a signed
   *         32 bit integer (i.e. the sign bit is being used for magnitude).
   */
  private int readFixnumBody(int nBytes, 
                             int sign)
                      throws IOException {
    if (trace == API_TRACE_DETAILED) {
      Log.current.println("readFixnumBody sign=" + sign + " length=" + nBytes);
    }

    if (nBytes > 4) {
      throw new ArithmeticException("Cannot fit " + nBytes + " bytes into an int");
    }

    int num = 0;

    for (int i = 0; i < nBytes; i++) {
      int j = read();

      if (trace == API_TRACE_DETAILED) {
        Log.current.println("\t" + j);
      }

      num |= (j << (8 * i));
    }

    // num should always be positive here.  Negatives indicate overflows.
    if (num < 0) {
      throw new ArithmeticException("Overflow: " + ((long) num & 0xFFFFFFFFL) + 
                                    " does not fit into an int");
    }

    return (sign * num);
  }

  /**
   * Reads the body of a CFASL Bignum (everything but the opcode) off of this CFASL input stream.
   * 
   * @param sign    The sign of the Bignum (-1 or +1)
   * 
   * @return a BigInteger holding the CFASL Bignum read in
   * 
   * @throws IOException if a communications error occurs
   */
  private BigInteger readBignumBody(int sign)
                             throws IOException {
    int length = readInt();

    if (trace == API_TRACE_DETAILED) {
      Log.current.println("readBignumBody sign=" + sign + " length=" + length);
    }

    byte[] b = new byte[length];

    for (int i = length - 1; i >= 0; i--) {
      int j = readInt();

      if (trace == API_TRACE_DETAILED) {
        Log.current.println("\t" + j);
      }

      b[i] = (byte) j;
    }

    return new BigInteger(sign, 
                          b);
  }

  /**
   * Reads the body of a CFASL Float (everything but the opcode) off of this CFASL input stream.
   * 
   * @param sign    The sign of the Float (-1 or +1)
   * 
   * @return a double holding the CFASL Float read in
   * 
   * @throws IOException if a communications error occurs
   * @throws ArithmeticException if significand cannot fit into a 64 bit signed long int
   */
  private double readFloatBody(int sign)
                        throws IOException {
    long signif;
    long exp;

    if (trace == API_TRACE_DETAILED) {
      Log.current.println("readFloatBody sign=" + sign);
    }

    Object obj = readObject();

    if (obj instanceof BigInteger) {
      BigInteger bi = (BigInteger) obj;

      if (bi.bitCount() < 64) {
        signif = bi.longValue();
      }
      else {
        throw new ArithmeticException("Overflow reading significand of float");
      }
    }
    else {
      signif = ((Number) obj).longValue();
    }

    exp = readInt();

    if (trace == API_TRACE_DETAILED) {
      Log.current.println("readFloatBody sign=" + sign + " signif=" + signif + " exp= " + exp);
    }

    return ((double) sign * (double) signif * Math.pow(
                                                    2.0, 
                                                    exp));
  }

  /**
   * Reads the body of a keyword Symbol from the CfaslInputStream.  The CFASL opcode has already
   * been read in at this point, so we only read in what follows.
   * 
   * @return the keyword <tt>CycSymbol</tt> read
   * 
   * @throws IOException if a communications error occurs
   */
  public CycSymbol readKeyword()
                        throws IOException {
    String keywordString = (String) readObject();

    if (!(keywordString.startsWith(":"))) {
      keywordString = ":" + keywordString;
    }

    return CycObjectFactory.makeCycSymbol(keywordString);
  }

  /**
   * Reads the body of a Symbol or EL variable from the CfaslInputStream.  The CFASL opcode has
   * already been read in at this point, so we only read in what follows.
   * 
   * @return the <tt>CycSymbol</tt> or EL <tt>CycVariable</tt>
   * 
   * @throws IOException if a communications error occurs
   */
  public Object readSymbol()
                    throws IOException {
    String name = (String) readObject();

    if (name.startsWith("?")) {
      return CycObjectFactory.makeCycVariable(name);
    }
    else {
      return CycObjectFactory.makeCycSymbol(name);
    }
  }

  /**
   * Reads the body of a Guid from the CfaslInputStream.  The CFASL opcode has already been read in
   * at this point, so we only read in what follows.
   * 
   * @return the <tt>Guid</tt> read
   * 
   * @throws IOException if a communications error occurs
   */
  public Guid readGuid()
                throws IOException {
    Guid guid = CycObjectFactory.makeGuid((String) readObject());

    if (trace == API_TRACE_DETAILED) {
      Log.current.println("readGuid: " + guid);
    }

    return guid;
  }

  /**
   * Reads the body of a Unicode Character from the CfaslInputStream.  
   * The CFASL opcode has already been read in
   * at this point, so we only read in what follows.
   * 
   * @return the <tt>Integer</tt> of the unicode value read
   * 
   * @throws IOException if a communications error occurs
   */
  public Integer readUnicodeChar()
                throws IOException {

    int off = 0;
    int len = readInt();
    byte[] s = new byte[len];
    

    while (off < len) {
      off += read(s, 
		  off, 
		  len - off);
    }

    String charString = new String(s, 
			       "UTF-8");
    int retval = (int)charString.charAt(0); 
    // NOTE: When we upgrade to java 1.5 change the above line to 
    //     int retval = charString.codePointAt(0);
    if (trace == API_TRACE_DETAILED) {
      Log.current.println("readUnicodeChar: 0x" + Integer.toHexString(retval));
    }

    return new Integer(retval);
  }

  /**
   * Reads the body of a Unicode String from the CfaslInputStream.  
   * The CFASL opcode has already been read in
   * at this point, so we only read in what follows.
   * 
   * @return the <tt>String</tt> read
   * 
   * @throws IOException if a communications error occurs
   */
  public String readUnicodeString()
                throws IOException {
    int off = 0;
    int len = readInt();
    byte[] s = new byte[len];
    

    while (off < len) {
      off += read(s, 
		  off, 
		  len - off);
    }

    String retval = new String(s, 
			       "UTF-8");
    if (trace == API_TRACE_DETAILED) {
      Log.current.println("readUnicodeString: " + retval);
    }

    return retval;
  }

  /**
   * Reads a byte vector from the CfaslInputStream. The CFASL opcode has already been read in at
   * this point, so we only read in what follows.
   * 
   * @return the <tt>ByteArray</tt> read
   * 
   * @throws IOException if a communications error occurs
   */
  public ByteArray readByteArray()
                          throws IOException {
    int off = 0;
    int len = readInt();
    byte[] bytes = new byte[len];

    while (off < len) {
      off += read(bytes, 
                  off, 
                  len - off);
    }

    return new ByteArray(bytes);
  }

  /**
   * Reads a list from the CfaslInputStream.  The CFASL opcode has already been read in at this
   * point, so we only read in what follows.
   * 
   * @return the <tt>CycList</tt> read
   * 
   * @throws IOException if a communications error occurs
   */
  public CycList readCycList()
                      throws IOException {
    int size = readInt();

    if (trace == API_TRACE_DETAILED) {
      Log.current.println("readCycList.size: " + size);
    }

    CycList cycList = new CycList();

    for (int i = 0; i < size; i++) {
      cycList.add(readObject());
    }

    if (trace == API_TRACE_DETAILED) {
      Log.current.println("readCycList.readObject: " + cycList.safeToString());
    }

    return cycList;
  }

  /**
   * Reads a dotted list from the CfaslInputStream.  The CFASL opcode has already been read in at
   * this point, so we only read in what follows.
   * 
   * @return the <tt>CycList</tt> read
   * 
   * @throws IOException if a communications error occurs
   */
  public CycList readCons()
                   throws IOException {
    int size = readInt();

    if (trace == API_TRACE_DETAILED) {
      Log.current.println("readCons.size: " + size);
    }

    CycList cycList = new CycList();

    //for (int i = 0; i < (size - 1); i++) {
    for (int i = 0; i < size; i++) {
      Object consObject = readObject();

      if (trace == API_TRACE_DETAILED) {
        if (consObject instanceof CycFort)
          Log.current.println("readCons.consObject: " + ((CycFort) consObject).safeToString());
        else
          Log.current.println("readCons.consObject: " + consObject);
      }

      cycList.add(consObject);
    }

    Object cdrObject = readObject();

    if (trace == API_TRACE_DETAILED) {
        try {
          // If element understands the safeToString method, then use it.
          final Method safeToString = cdrObject.getClass().getMethod("safeToString", null);
          Log.current.println("readCons.cdrObject: " + safeToString.invoke(cdrObject, null));
        } catch (Exception e) {
          Log.current.println("readCons.cdrObject: " + cdrObject.toString());
        }
    }
    cycList.setDottedElement(cdrObject);

    if (trace == API_TRACE_DETAILED) {
      Log.current.println("readCons.readCons: " + cycList.safeToString());
    }

    return cycList;
  }

  /**
   * Reads a constant from a CfaslInputStream.
   * 
   * @return an incomplete <tt>CycConstant</tt> having the input id or guid
   * 
   * @throws IOException if a communications error occurs
   * @throws RuntimeException if an unexpected constant id type occurs
   */
  public CycConstant readConstant()
                           throws IOException {
    CycConstant cycConstant = null;

    //cycConstant = new CycConstant();
    Object idObject = readObject();

    if (idObject instanceof Integer) {
      // deprecated legacy code support for SUIDs
      cycConstant = CycObjectFactory.getCycConstantCacheById((Integer) idObject);
      if (cycConstant == null) {
        cycConstant = new CycConstant();
        cycConstant.setId((Integer) idObject);
        CycObjectFactory.addCycConstantCacheById(cycConstant);
      }
    }
    else if (idObject instanceof Guid) {
      cycConstant = CycObjectFactory.getCycConstantCacheByGuid((Guid) idObject);
      if (cycConstant == null) {
        cycConstant = new CycConstant();
        cycConstant.setGuid((Guid) idObject);
        CycObjectFactory.addCycConstantCacheByGuid(cycConstant);
      }
    }
    else if ((idObject instanceof CycSymbol) && 
                 (idObject.equals(CycObjectFactory.makeCycSymbol(":FREE")))) {
      cycConstant = new CycConstant();
      cycConstant.setFree();
    }
    else {
      // Log.current.println("Unknown Constant ID type " + idObject + " (" + idObject.getClass() + ")");
      cycConstant = null;
    }

    if (trace == API_TRACE_DETAILED) {
      Log.current.println("readConstant: " + cycConstant.safeToString());
    }

    return cycConstant;
  }

  /**
   * Reads a variable from the CfaslInputStream.
   * 
   * @return an incomplete <tt>CycVariable</tt> having the input id
   * 
   * @throws IOException if a communications error occurs
   */
  public CycVariable readVariable()
                           throws IOException {
    CycVariable cycVariable = new CycVariable();
    cycVariable.hlVariableId = new Integer(readInt());

    if (trace == API_TRACE_DETAILED) {
      Log.current.println("readVariable: " + cycVariable.safeToString());
    }

    return cycVariable;
  }

  /**
   * Reads a NART from a CfaslInputStream.
   * 
   * @return a the CycNart having the input HL Formula or having the input id, or NIL if the nart is invalid
   * 
   * @throws IOException if a communications error occurs
   */
  public CycObject readNart()
                   throws IOException {
    CycNart cycNart = null;
    mark(10);

    int cfaslOpcode = read();

    if (cfaslOpcode == CFASL_LIST) {
      cycNart = new CycNart(readCycList());
    }
    else {
      if (trace == API_TRACE_DETAILED) {
        Log.current.println("readNart using id, cfasl opcode?: " + cfaslOpcode);
      }
      reset();
      cycNart = new CycNart();
      try {
        cycNart.setId(new Integer(readInt()));
      }
      catch (RuntimeException e) {
        if (cfaslOpcode == CFASL_NIL) {
          read();
          if (trace == API_TRACE_DETAILED)
            Log.current.println("readNart: invalid nart replaced with NIL");
          return CycObjectFactory.nil;
        }
        else
          throw new RuntimeException(e);
      }
    }

    if (trace == API_TRACE_DETAILED) {
      Log.current.println("readNart: " + cycNart.safeToString());
    }

    return cycNart;
  }

  /**
   * Reads an assertion from a CfaslInputStream.
   * 
   * @return an incomplete <tt>CycAssertion</tt> having the input id
   * 
   * @throws IOException if a communications error occurs
   */
  public CycAssertion readAssertion()
                             throws IOException {
    CycList formula = (CycList) readObject();
    CycObject mt = (CycObject) readObject();
    CycAssertion cycAssertion = new CycAssertion(formula, mt);
    if (trace == API_TRACE_DETAILED) {
      Log.current.println("readAssertion: " + cycAssertion.safeToString());
    }

    return cycAssertion;
  }
}
