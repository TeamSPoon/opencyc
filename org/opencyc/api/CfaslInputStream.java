package  org.opencyc.api;

import  java.io.*;
import  java.math.BigInteger;
import  java.util.HashMap;
import  java.lang.reflect.*;
import  org.opencyc.cycobject.*;

/**
 * A CFASL translating input stream.  All Java-native types which have logical
 * sublisp equivalents are translated automatically by this stream.  Classes
 * implementing the CfaslTranslatingObject interface are created using thier
 * readObject() method.  Other CYC objects, such as binding-lists and formulas,
 * must be explicitly coerced using their static constructors.
 *
 * @version $Id$
 * @author Stephen L. Reed
 *
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
 */
public class CfaslInputStream extends BufferedInputStream {

    /**
     * No api trace.
     */
    public static final int API_TRACE_NONE = 0;

    /**
     * Message-level api trace.
     */
    public static final int API_TRACE_MESSAGES = 1;

    /**
     * Detailed api trace.
     */
    public static final int API_TRACE_DETAILED = 2;

    /**
     * Parameter that, when true, causes a trace of the messages to and from the server.
     */
    public int trace = API_TRACE_NONE;

    /**
     * Parameter that when set true, causes CFASL object errors to be reported back as strings
     * the caller.
     */
    public boolean reportCfaslErrors = false;

    protected static final int CFASL_IMMEDIATE_FIXNUM_CUTOFF = 128;
    protected static final int CFASL_IMMEDIATE_FIXNUM_OFFSET = 256 - CFASL_IMMEDIATE_FIXNUM_CUTOFF;
    protected static final int CFASL_P_8BIT_INT = 0;
    protected static final int CFASL_N_8BIT_INT = 1;
    protected static final int CFASL_P_16BIT_INT = 2;
    protected static final int CFASL_N_16BIT_INT = 3;
    protected static final int CFASL_P_24BIT_INT = 4;
    protected static final int CFASL_N_24BIT_INT = 5;
    protected static final int CFASL_P_32BIT_INT = 6;
    protected static final int CFASL_N_32BIT_INT = 7;
    protected static final int CFASL_P_FLOAT = 8;
    protected static final int CFASL_N_FLOAT = 9;
    protected static final int CFASL_KEYWORD = 10;
    protected static final int CFASL_SYMBOL = 11;
    protected static final int CFASL_NIL = 12;
    protected static final int CFASL_LIST = 13;
    protected static final int CFASL_DOTTED = 17;
    protected static final int CFASL_VECTOR = 14;
    protected static final int CFASL_STRING = 15;
    protected static final int CFASL_CHARACTER = 16;
    protected static final int CFASL_HASHTABLE = 18;
    protected static final int CFASL_BTREE_LOW_HIGH = 19;
    protected static final int CFASL_BTREE_LOW = 20;
    protected static final int CFASL_BTREE_HIGH = 21;
    protected static final int CFASL_BTREE_LEAF = 22;
    protected static final int CFASL_P_BIGNUM = 23;
    protected static final int CFASL_N_BIGNUM = 24;
    protected static final int CFASL_GUID = 25;
    protected static final int CFASL_BYTE_VECTOR = 26;
    protected static final int CFASL_CONSTANT = 30;
    protected static final int CFASL_NART = 31;
    protected static final int CFASL_ASSERTION = 33;
    protected static final int CFASL_ASSERTION_SHELL = 34;
    protected static final int CFASL_ASSERTION_DEF = 35;
    protected static final int CFASL_SOURCE = 36;
    protected static final int CFASL_SOURCE_DEF = 37;
    protected static final int CFASL_AXIOM = 38;
    protected static final int CFASL_AXIOM_DEF = 39;
    protected static final int CFASL_VARIABLE = 40;
    protected static final int CFASL_INDEX = 41;
    protected static final int CFASL_SPECIAL_OBJECT = 50;
    protected static final int CFASL_DICTIONARY = 64;
    protected static final int CFASL_SERVER_DEATH = -1;
    protected static final int DEFAULT_READ_LIMIT = 1024;

    static HashMap cfaslOpcodeDescriptions = null;

    /**
     * Initializes the opcode descriptions used in trace output.
     */
    protected void initializeOpcodeDescriptions() {
        cfaslOpcodeDescriptions = new HashMap();
        cfaslOpcodeDescriptions.put(new Integer(128), "CFASL_IMMEDIATE_FIXNUM_CUTOFF");
        cfaslOpcodeDescriptions.put(new Integer(256 - CFASL_IMMEDIATE_FIXNUM_CUTOFF),
                                    "CFASL_IMMEDIATE_FIXNUM_OFFSET");
        cfaslOpcodeDescriptions.put(new Integer(0), "CFASL_P_8BIT_INT");
        cfaslOpcodeDescriptions.put(new Integer(1), "CFASL_N_8BIT_INT");
        cfaslOpcodeDescriptions.put(new Integer(2), "CFASL_P_16BIT_INT");
        cfaslOpcodeDescriptions.put(new Integer(3), "CFASL_N_16BIT_INT");
        cfaslOpcodeDescriptions.put(new Integer(4), "CFASL_P_24BIT_INT");
        cfaslOpcodeDescriptions.put(new Integer(5), "CFASL_N_24BIT_INT");
        cfaslOpcodeDescriptions.put(new Integer(6), "CFASL_P_32BIT_INT");
        cfaslOpcodeDescriptions.put(new Integer(7), "CFASL_N_32BIT_INT");
        cfaslOpcodeDescriptions.put(new Integer(8), "CFASL_P_FLOAT");
        cfaslOpcodeDescriptions.put(new Integer(9), "CFASL_N_FLOAT");
        cfaslOpcodeDescriptions.put(new Integer(10), "CFASL_KEYWORD");
        cfaslOpcodeDescriptions.put(new Integer(11), "CFASL_SYMBOL");
        cfaslOpcodeDescriptions.put(new Integer(12), "CFASL_NIL");
        cfaslOpcodeDescriptions.put(new Integer(13), "CFASL_LIST");
        cfaslOpcodeDescriptions.put(new Integer(14), "CFASL_VECTOR");
        cfaslOpcodeDescriptions.put(new Integer(15), "CFASL_STRING");
        cfaslOpcodeDescriptions.put(new Integer(16), "CFASL_CHARACTER");
        cfaslOpcodeDescriptions.put(new Integer(17), "CFASL_DOTTED");
        cfaslOpcodeDescriptions.put(new Integer(18), "CFASL_HASHTABLE");
        cfaslOpcodeDescriptions.put(new Integer(19), "CFASL_BTREE_LOW_HIGH");
        cfaslOpcodeDescriptions.put(new Integer(20), "CFASL_BTREE_LOW");
        cfaslOpcodeDescriptions.put(new Integer(21), "CFASL_BTREE_HIGH");
        cfaslOpcodeDescriptions.put(new Integer(22), "CFASL_BTREE_LEAF");
        cfaslOpcodeDescriptions.put(new Integer(23), "CFASL_P_BIGNUM");
        cfaslOpcodeDescriptions.put(new Integer(24), "CFASL_N_BIGNUM");
        cfaslOpcodeDescriptions.put(new Integer(25), "CFASL_GUID");
        cfaslOpcodeDescriptions.put(new Integer(26), "CFASL_BYTE_VECTOR");
        cfaslOpcodeDescriptions.put(new Integer(30), "CFASL_CONSTANT");
        cfaslOpcodeDescriptions.put(new Integer(31), "CFASL_NART");
        cfaslOpcodeDescriptions.put(new Integer(33), "CFASL_ASSERTION");
        cfaslOpcodeDescriptions.put(new Integer(34), "CFASL_ASSERTION_SHELL");
        cfaslOpcodeDescriptions.put(new Integer(35), "CFASL_ASSERTION_DEF");
        cfaslOpcodeDescriptions.put(new Integer(36), "CFASL_SOURCE");
        cfaslOpcodeDescriptions.put(new Integer(37), "CFASL_SOURCE_DEF");
        cfaslOpcodeDescriptions.put(new Integer(38), "CFASL_AXIOM");
        cfaslOpcodeDescriptions.put(new Integer(39), "CFASL_AXIOM_DEF");
        cfaslOpcodeDescriptions.put(new Integer(40), "CFASL_VARIABLE");
        cfaslOpcodeDescriptions.put(new Integer(41), "CFASL_INDEX");
        cfaslOpcodeDescriptions.put(new Integer(50), "CFASL_SPECIAL_OBJECT");
        cfaslOpcodeDescriptions.put(new Integer(64), "CFASL_DICTIONARY");
        cfaslOpcodeDescriptions.put(new Integer(-1), "CFASL_SERVER_DEATH");
    }

    /**
     * Creates a new CfaslInputStream to read data from the
     * specified underlying input stream.
     *
     * @param in the underlying input stream.
     */
    public CfaslInputStream (InputStream in) {
        super(in, DEFAULT_READ_LIMIT);
        initializeOpcodeDescriptions();
    }

    /**
     * Reads an Object from this CfaslInputStream.  Basic Java types are
     * wrapped as appropriate (e.g. ints become Integer objects).  New constants are missing
     * name and GUID values and will be completed by the caller to avoid recursion within the
     * api call.
     *
     * @return the object read from the binary OpenCyc input stream
     */
    public Object readObject () throws IOException {
        int cfaslOpcode = read();
        Object o = null;
        if (trace == API_TRACE_DETAILED)
            System.out.println("reading opcode = " + cfaslOpcode + " (" +
                               cfaslOpcodeDescriptions.get(new Integer(cfaslOpcode)) +")");
        if (cfaslOpcode >= CFASL_IMMEDIATE_FIXNUM_OFFSET) {
            o = new Integer(cfaslOpcode - CFASL_IMMEDIATE_FIXNUM_OFFSET);
            if (trace == API_TRACE_DETAILED)
                System.out.println("Reading Immediate Fixnum: " + o);
        }
        else {
            switch (cfaslOpcode) {
                case CFASL_P_8BIT_INT:
                    o = new Integer(readFixnumBody(1, 1));
                    break;
                case CFASL_N_8BIT_INT:
                    o = new Integer(readFixnumBody(1, -1));
                    break;
                case CFASL_P_16BIT_INT:
                    o = new Integer(readFixnumBody(2, 1));
                    break;
                case CFASL_N_16BIT_INT:
                    o = new Integer(readFixnumBody(2, -1));
                    break;
                case CFASL_P_24BIT_INT:
                    o = new Integer(readFixnumBody(3, 1));
                    break;
                case CFASL_N_24BIT_INT:
                    o = new Integer(readFixnumBody(3, -1));
                    break;
                case CFASL_P_32BIT_INT:
                    o = new Integer(readFixnumBody(4, 1));
                    break;
                case CFASL_N_32BIT_INT:
                    o = new Integer(readFixnumBody(4, -1));
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
                    byte s[] = new byte[len];
                    while (off < len) {
                        off += read(s, off, len - off);
                    }
                    o = new String(s);
                    /**
                     * Enable when/if UTF-8 is supported by Cyc
                     */
                    //o = new String(s, "UTF-8");
                    break;
                case CFASL_CHARACTER:
                    o = new Character((char)read());
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
                Method safeToString = o.getClass().getMethod("safeToString", null);
                System.out.println("readObject = " + safeToString.invoke(o, null) +
                                   " (" + o.getClass() + ")");
            }
            catch (Exception e) {
                System.out.println("readObject = " + o + " (" + o.getClass() + ")");
            }
        }
        return  o;
    }

    /**
     * Reports the unhandled cfasl opcode or throws an exception.
     */
    protected Object reportUnhandledCfaslOpcode (int cfaslOpcode) {
        String errorMessage;
        if (cfaslOpcode == -1)
            errorMessage = "Cyc server closed the connection";
        else
            errorMessage = "Unknown cfasl opcode: " + cfaslOpcode;
        if (reportCfaslErrors) {
            System.out.println(errorMessage);
            return Integer.toString(cfaslOpcode);
        }
        else
            throw  new RuntimeException(errorMessage);
    }

    /**
     * Reads an char from this CfaslInputStream.  If the next item
     * on the stream is not a char, throw an exception, and leave
     * that object on the input stream.
     *
     * @return the character read
     */
    public char readChar () throws IOException {
        mark(DEFAULT_READ_LIMIT);
        int cfaslOpcode = read();
        if (cfaslOpcode == CFASL_CHARACTER)
            return  (char)read();
        reset();
        throw  new RuntimeException("Expected a char but received opCode=" + cfaslOpcode);
    }

    /**
     * Reads a double from this CfaslInputStream.  If the next item
     * on the stream is not a double, throw an exception, and leave
     * that object on the input stream.
     *
     * @return the double read
     */
    public double readDouble () throws IOException {
        mark(DEFAULT_READ_LIMIT);
        int cfaslOpcode = read();
        switch (cfaslOpcode) {
            case CFASL_P_FLOAT:
                return  readFloatBody(1);
            case CFASL_N_FLOAT:
                return  readFloatBody(-1);
            default:
                reset();
                throw  new RuntimeException("Expected a double but received OpCode=" + cfaslOpcode);
        }
    }

    /**
     * Reads an int from this CfaslInputStream.  If the next item
     * on the stream is not an int, throw an exception, and leave
     * that object on the input stream.  Bignum ints are not allowed.
     *
     * @return the int read
     */
    public int readInt () throws IOException {
        mark(DEFAULT_READ_LIMIT);
        int cfaslOpcode = read();
        if (cfaslOpcode >= CFASL_IMMEDIATE_FIXNUM_OFFSET) {
            return  cfaslOpcode - CFASL_IMMEDIATE_FIXNUM_OFFSET;
        }
        else {
            switch (cfaslOpcode) {
                case CFASL_P_8BIT_INT:
                    return  readFixnumBody(1, 1);
                case CFASL_N_8BIT_INT:
                    return  readFixnumBody(1, -1);
                case CFASL_P_16BIT_INT:
                    return  readFixnumBody(2, 1);
                case CFASL_N_16BIT_INT:
                    return  readFixnumBody(2, -1);
                case CFASL_P_24BIT_INT:
                    return  readFixnumBody(3, 1);
                case CFASL_N_24BIT_INT:
                    return  readFixnumBody(3, -1);
                case CFASL_P_32BIT_INT:
                    return  readFixnumBody(4, 1);
                case CFASL_N_32BIT_INT:
                    return  readFixnumBody(4, -1);
                default:
                    reset();
                    throw  new RuntimeException("Expected an int but received OpCode=" + cfaslOpcode);
            }
        }
    }

    /**
     * Reads the body of a CFASL Fixnum (everything but the opcode)
     * from this CFASL input stream.
     *
     * @param nBytes  The number of bytes to read
     * @param sign    The sign of the Fixnum (-1 or +1)
     * @return an int holding the CFASL Fixnum read in
     * @throws ArithmeticException if nBytes > 4 or if the integer read in does
     *         not fit into a signed 32 bit integer (i.e. the sign bit is being
     *         used for magnitude).
     */
    private int readFixnumBody (int nBytes, int sign) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("readFixnumBody sign=" + sign + " length=" + nBytes);
        if (nBytes > 4)
            throw  new ArithmeticException("Cannot fit " + nBytes + " bytes into an int");
        int num = 0;
        for (int i = 0; i < nBytes; i++) {
            int j = read();
            if (trace == API_TRACE_DETAILED)
                System.out.println("\t" + j);
            num |= (j << (8*i));
        }
        // num should always be positive here.  Negatives indicate overflows.
        if (num < 0)
            throw  new ArithmeticException("Overflow: " + ((long)num & 0xFFFFFFFFL) + " does not fit into an int");
        return  (sign*num);
    }

    /**
     * Reads the body of a CFASL Bignum (everything but the opcode)
     * off of this CFASL input stream.
     *
     * @param sign    The sign of the Bignum (-1 or +1)
     * @return a BigInteger holding the CFASL Bignum read in
     */
    private BigInteger readBignumBody (int sign) throws IOException {
        int length = readInt();
        if (trace == API_TRACE_DETAILED)
            System.out.println("readBignumBody sign=" + sign + " length=" + length);
        byte b[] = new byte[length];
        for (int i = length - 1; i >= 0; i--) {
            int j = readInt();
            if (trace == API_TRACE_DETAILED)
                System.out.println("\t" + j);
            b[i] = (byte)j;
        }
        return  new BigInteger(sign, b);
    }

    /**
     * Reads the body of a CFASL Float (everything but the opcode)
     * off of this CFASL input stream.
     *
     * @param sign    The sign of the Float (-1 or +1)
     * @return a double holding the CFASL Float read in
     * @throws ArithmeticException if significand cannot fit into a 64 bit signed long int
     */
    private double readFloatBody (int sign) throws IOException {
        long signif, exp;
        if (trace == API_TRACE_DETAILED)
            System.out.println("readFloatBody sign=" + sign);
        Object obj = readObject();
        if (obj instanceof BigInteger) {
            BigInteger bi = (BigInteger)obj;
            if (bi.bitCount() < 64)
                signif = bi.longValue();
            else
                throw  new ArithmeticException("Overflow reading significand of float");
        }
        else
            signif = ((Number)obj).longValue();
        exp = readInt();
        if (trace == API_TRACE_DETAILED)
            System.out.println("readFloatBody sign=" + sign + " signif=" + signif + " exp= " + exp);
        return  ((double)sign*(double)signif*Math.pow(2.0, exp));
    }

    /**
     * Reads the body of a keyword Symbol from the CfaslInputStream.  The CFASL opcode
     * has already been read in at this point, so we only read in what follows.
     *
     * @return the keyword <tt>CycSymbol</tt> read
     */
    public CycSymbol readKeyword () throws IOException {
        return  CycObjectFactory.makeCycSymbol(":" + (String)readObject());
    }

    /**
     * Reads the body of a Symbol or EL variable from the CfaslInputStream.  The CFASL opcode
     * has already been read in at this point, so we only read in what follows.
     *
     * @return the <tt>CycSymbol</tt> or EL <tt>CycVariable</tt>
     */
    public Object readSymbol () throws IOException {
        String name = (String) readObject();
        if (name.startsWith("?"))
            return  CycObjectFactory.makeCycVariable(name);
        else
            return  CycObjectFactory.makeCycSymbol(name);
    }

    /**
     * Reads the body of a Guid from the CfaslInputStream.  The CFASL opcode
     * has already been read in at this point, so we only read in what follows.
     *
     * @return the <tt>Guid</tt> read
     */
    public Guid readGuid () throws IOException {
        Guid guid = CycObjectFactory.makeGuid((String)readObject());
        if (trace == API_TRACE_DETAILED)
            System.out.println("readGuid: " + guid);
        return guid;
    }

  /**
   * Reads a byte vector from the CfaslInputStream. The CFASL opcode
   * has already been read in at this point, so we only read in what follows.
   *
   * @return the <tt>ByteArray</tt> read
   */
  public ByteArray readByteArray() throws IOException {
    int off = 0;
    int len = readInt();
    byte[] bytes = new byte[len];
    while (off < len) {
      off += read(bytes, off, len - off);
    }
    return new ByteArray(bytes);
  }

    /**
     * Reads a list from the CfaslInputStream.  The CFASL opcode
     * has already been read in at this point, so we only read in what follows.
     *
     * @return the <tt>CycList</tt> read
     */
    public CycList readCycList () throws IOException {
        int size = readInt();
        if (trace == API_TRACE_DETAILED)
            System.out.println("readCycList.size: " + size);
        CycList cycList = new CycList();
        for (int i = 0; i < size; i++) {
            cycList.add(readObject());
        }
        if (trace == API_TRACE_DETAILED)
            System.out.println("readCycList.readObject: " + cycList.safeToString());
        return  cycList;
    }

    /**
     * Reads a dotted list from the CfaslInputStream.  The CFASL opcode
     * has already been read in at this point, so we only read in what follows.
     *
     * @return the <tt>CycList</tt> read
     */
    public CycList readCons () throws IOException {
        int size = readInt();
        if (trace == API_TRACE_DETAILED)
            System.out.println("readCons.size: " + size);
        CycList cycList = new CycList();
        //for (int i = 0; i < (size - 1); i++) {
        for (int i = 0; i < size; i++) {
            Object consObject = readObject();
            if (trace == API_TRACE_DETAILED)
                System.out.println("readCons.consObject: " + consObject);
            cycList.add(consObject);
        }
        Object cdrObject = readObject();
        if (trace == API_TRACE_DETAILED)
            System.out.println("readCons.cdrObject: " + cdrObject);
        cycList.setDottedElement(cdrObject);
        if (trace == API_TRACE_DETAILED)
            System.out.println("readCons.readCons: " + cycList);
        return  cycList;
    }

    /**
     * Reads a constant from a CfaslInputStream.
     *
     * @return an incomplete <tt>CycConstant</tt> having the input id
     */
    public CycConstant readConstant () throws IOException {
        CycConstant cycConstant = new CycConstant();
        cycConstant.setId(new Integer(readInt()));
        if (trace == API_TRACE_DETAILED)
            System.out.println("readConstant: " + cycConstant.safeToString());
        return  cycConstant;
    }

    /**
     * Reads a variable from the CfaslInputStream.
     *
     * @return an incomplete <tt>CycVariable</tt> having the input id
     */
    public CycVariable readVariable () throws IOException {
        CycVariable cycVariable = new CycVariable();
        cycVariable.id = new Integer(readInt());
        if (trace == API_TRACE_DETAILED)
            System.out.println("readVariable: " + cycVariable.safeToString());
        return  cycVariable;
    }

    /**
     * Reads a NART from a CfaslInputStream.
     *
     * @return an incomplete <tt>CycConstant</tt> having the input id
     */
    public CycNart readNart () throws IOException {
        CycNart cycNart = new CycNart();
        cycNart.setId(new Integer(readInt()));
        if (trace == API_TRACE_DETAILED)
            System.out.println("readNart: " + cycNart.safeToString());
        return  cycNart;
    }

    /**
     * Reads an assertion from a CfaslInputStream.
     *
     * @return an incomplete <tt>CycAssertion</tt> having the input id
     */
    public CycAssertion readAssertion () throws IOException {
        CycAssertion cycAssertion = new CycAssertion(new Integer(readInt()));
        if (trace == API_TRACE_DETAILED)
            System.out.println("readAssertion: " + cycAssertion.safeToString());
        return cycAssertion;
    }

}







