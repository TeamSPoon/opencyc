package  org.opencyc.api;

import  java.io.*;
import  java.math.BigInteger;
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
    protected static final int CFASL_SERVER_DEATH = -1;
    protected static final int DEFAULT_READ_LIMIT = 1024;
    protected CycConnection cycConnection;

    /**
     * Creates a new CfaslInputStream to read data from the
     * specified underlying input stream.
     *
     * @param in the underlying input stream.
     * @param cycConnection the parent <tt>CycConnection</tt> object
     */
    public CfaslInputStream (InputStream in, CycConnection cycConnection) {
        super(in, DEFAULT_READ_LIMIT);
        this.cycConnection = cycConnection;
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
        if (cycConnection.trace)
            System.out.println("opcode = " + cfaslOpcode);
        if (cfaslOpcode >= CFASL_IMMEDIATE_FIXNUM_OFFSET) {
            o = new Integer(cfaslOpcode - CFASL_IMMEDIATE_FIXNUM_OFFSET);
            if (cycConnection.trace)
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
                    o = null;
                    break;
                case CFASL_LIST:
                    o = readCycList();
                    break;
                case CFASL_DOTTED:
                    o = readCons();
                    break;
                case CFASL_VECTOR:
                    throw  new RuntimeException("CFASL opcode " + cfaslOpcode + " is not supported");
                case CFASL_STRING:
                    int off = 0;
                    int len = readInt();
                    byte s[] = new byte[len];
                    while (off < len) {
                        off += read(s, off, len - off);
                    }
                    o = new String(s);
                    break;
                case CFASL_CHARACTER:
                    o = new Character((char)read());
                    break;
                case CFASL_HASHTABLE:
                    throw  new RuntimeException("CFASL opcode " + cfaslOpcode + " is not supported");
                case CFASL_BTREE_LOW_HIGH:
                    throw  new RuntimeException("CFASL opcode " + cfaslOpcode + " is not supported");
                case CFASL_BTREE_LOW:
                    throw  new RuntimeException("CFASL opcode " + cfaslOpcode + " is not supported");
                case CFASL_BTREE_HIGH:
                    throw  new RuntimeException("CFASL opcode " + cfaslOpcode + " is not supported");
                case CFASL_BTREE_LEAF:
                    throw  new RuntimeException("CFASL opcode " + cfaslOpcode + " is not supported");
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
                    throw  new RuntimeException("CFASL opcode " + cfaslOpcode + " is not supported");
                case CFASL_ASSERTION_DEF:
                    throw  new RuntimeException("CFASL opcode " + cfaslOpcode + " is not supported");
                case CFASL_SOURCE:
                    throw  new RuntimeException("CFASL opcode " + cfaslOpcode + " is not supported");
                case CFASL_SOURCE_DEF:
                    throw  new RuntimeException("CFASL opcode " + cfaslOpcode + " is not supported");
                case CFASL_AXIOM:
                    throw  new RuntimeException("CFASL opcode " + cfaslOpcode + " is not supported");
                case CFASL_AXIOM_DEF:
                    throw  new RuntimeException("CFASL opcode " + cfaslOpcode + " is not supported");
                case CFASL_VARIABLE:
                    throw  new RuntimeException("CFASL opcode " + cfaslOpcode + " is not supported");
                case CFASL_INDEX:
                    throw  new RuntimeException("CFASL opcode " + cfaslOpcode + " is not supported");
                case CFASL_SPECIAL_OBJECT:
                    throw  new RuntimeException("CFASL opcode " + cfaslOpcode + " is not supported");
                case CFASL_SERVER_DEATH:
                    throw  new IOException("CFASL server closed connection.");
                default:
                    throw  new RuntimeException("Unknown cfasl opcode: " + cfaslOpcode);
            }
        }
        if (cycConnection.trace)
            if (o == null)
                System.out.println("readObject = nil/null");
            else
                System.out.println("readObject = " + o + " (" + o.getClass() + ")");
        return  o;
    }

    /*
     * The methods below read Java primative types without wrapping them
     * in Objects.  Some, like readInt, may be called a lot, so the savings
     * from not having to allocate heap space may be large.  This justifies
     * the extra maintainence burden caused by code repetition.
     */
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
        if (cycConnection.trace)
            System.out.println("readFixnumBody sign=" + sign + " length=" + nBytes);
        if (nBytes > 4)
            throw  new ArithmeticException("Cannot fit " + nBytes + " bytes into an int");
        int num = 0;
        for (int i = 0; i < nBytes; i++) {
            int j = read();
            if (cycConnection.trace)
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
        if (cycConnection.trace)
            System.out.println("readBignumBody sign=" + sign + " length=" + length);
        byte b[] = new byte[length];
        for (int i = length - 1; i >= 0; i--) {
            int j = readInt();
            if (cycConnection.trace)
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
        if (cycConnection.trace)
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
        if (cycConnection.trace)
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
        return  CycSymbol.makeCycSymbol((String)readObject());
    }

    /**
     * Reads the body of a Symbol from the CfaslInputStream.  The CFASL opcode
     * has already been read in at this point, so we only read in what follows.
     *
     * @return the <tt>CycSymbol</tt> read
     */
    public CycSymbol readSymbol () throws IOException {
        return  CycSymbol.makeCycSymbol((String)readObject());
    }

    /**
     * Reads a list from the CfaslInputStream.  The CFASL opcode
     * has already been read in at this point, so we only read in what follows.
     *
     * @return the <tt>CycList</tt> read
     */
    public CycList readCycList () throws IOException {
        int size = readInt();
        CycList cycList = new CycList();
        for (int i = 0; i < size; i++) {
            cycList.add(readObject());
        }
        if (cycConnection.trace)
            System.out.println("CycList.readObject: " + cycList);
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
        CycList cycList = new CycList();
        for (int i = 0; i < (size - 1); i++)
            cycList.add(readObject());
        cycList.setDottedElement(readObject());
        return  cycList;
    }

    /**
     * Reads a constant from a CfaslInputStream.
     *
     * @return an incomplete <tt>CycConstant</tt> having the input id
     */
    public CycConstant readConstant () throws IOException {
        return  new CycConstant(readInt());
    }

    /**
     * Reads a NART from a CfaslInputStream.
     *
     * @return an incomplete <tt>CycConstant</tt> having the input id
     */
    public CycNart readNart () throws IOException {
        return  new CycNart(readInt());
    }

    /**
     * Reads an assertion from a CfaslInputStream.
     *
     * @return an incomplete <tt>CycAssertion</tt> having the input id
     */
    public CycAssertion readAssertion () throws IOException {
        return  new CycAssertion(new Integer(readInt()));
    }
}



