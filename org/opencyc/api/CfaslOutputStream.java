package  org.opencyc.api;

import  java.io.*;
import  java.math.BigInteger;
import  java.util.*;
import  java.lang.reflect.*;
import  org.opencyc.cycobject.*;

/**
 * A CFASL translating buffered output stream.  All Java-native types which have logical
 * sublisp equivalents are translated automatically by this stream.  Classes
 * implementing the CfaslTranslatingObject interface are translated using their
 * writeObject() method.  Other CYC objects, such as binding-lists and formulas,
 * should be explicitly coerced before being sent, unless they inherit from
 * a class which can be translated automatically.
 *
 * @version $Id$
 * @author Christopher
 * @author Dan Lipofsky
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


public class CfaslOutputStream extends BufferedOutputStream {
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
     * Binary values for assembling CFASL messages.
     */
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
    protected static final int CFASL_VECTOR = 14;
    protected static final int CFASL_STRING = 15;
    protected static final int CFASL_CHARACTER = 16;
    protected static final int CFASL_DOTTED = 17;
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

    /**
     * Creates a new CfaslOutputStream to write data to the specified
     * underlying output stream with the default buffer size.
     *
     * @param out the underlying output stream.
     */
    public CfaslOutputStream (OutputStream out) {
        super(out);
    }

    /**
     * Creates a new CfaslOutputStream to write data to the specified
     * underlying output stream with the specified buffer size.
     *
     * @param out    the underlying output stream.
     * @param size   the buffer size.
     */
    public CfaslOutputStream (OutputStream out, int size) {
        super(out, size);
    }

    /**
     * Writes a boolean onto this CFASL output stream.
     * What is actually written is either the symbol T or NIL.
     *
     * @param v the boolean value to be written
     */
    public void writeBoolean (boolean v) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("writeBoolean = " + v);
        if (v)
            writeSymbol(CycObjectFactory.t);
        else
            writeSymbol(CycObjectFactory.nil);
    }

    /**
     * Writes a one byte character onto this CFASL output stream.
     * Crudely converts from Unicode to 8-bit ASCII.
     *
     * @param v the character to be written
     */
    public void writeChar (char v) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("writeChar = " + v);
        write(CFASL_CHARACTER);
        write(v);
    }

    /**
     * Writes a long integer to this CFASL output stream.  It may be written
     * as either a CFASL Fixnum or a CFASL Bignum, depending on its size.
     * For legacy reasons it is called writeInt instead of writeLong.
     *
     * @param the long integer to be written
     */
    public void writeInt (long v) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("writeInt = " + v);
        if (-2147483648L < v && v < 2147483648L)
            writeFixnum((int)v);
        else
            writeBignum(v);
    }

    /**
     * Writes an integer to this CFASL output stream as a Fixnum.
     * This method is protected because it does no size checking, so
     * the calling method must be wise as to what fits in a Fixnum.
     *
     * @param v the integer to be written
     */
    protected void writeFixnum (int v) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("* writeFixnum(long " + v + ")");
        int numBytes;
        if (v >= 0) {
            if (v < CFASL_IMMEDIATE_FIXNUM_CUTOFF) {
                // We have a special way of transmitting very small positive integers
                if (trace == API_TRACE_DETAILED)
                    System.out.println("Writing Immediate Fixnum: " + v);
                write((int)v + CFASL_IMMEDIATE_FIXNUM_OFFSET);
                numBytes = 0;
            }
            else if (v < 128) {                 // v < 2^7
                write(CFASL_P_8BIT_INT);
                numBytes = 1;
            }
            else if (v < 32768) {               // v < 2^15
                write(CFASL_P_16BIT_INT);
                numBytes = 2;
            }
            else if (v < 8388608) {             // v < 2^23
                write(CFASL_P_24BIT_INT);
                numBytes = 3;
            }
            else {              // v < 2^31 (implicit: nothing bigger should ever be passed in)
                write(CFASL_P_32BIT_INT);
                numBytes = 4;
            }
        }
        else {
            v = -v;
            if (v < 128) {      // v < 2^7
                write(CFASL_N_8BIT_INT);
                numBytes = 1;
            }
            else if (v < 32768) {               // v < 2^15
                write(CFASL_N_16BIT_INT);
                numBytes = 2;
            }
            else if (v < 8388608) {             // v < 2^23
                write(CFASL_N_24BIT_INT);
                numBytes = 3;
            }
            else {              // v < 2^31 (implicit: nothing bigger should ever be passed in)
                write(CFASL_N_32BIT_INT);
                numBytes = 4;
            }
        }
        // Transmit the bytes of the Fixnum in little-endian order (LSB first)
        for (int i = 0; i < numBytes; i++) {
            if (trace == API_TRACE_DETAILED)
                System.out.println("f\t" + ((v >>> (8*i)) & 0xFF));
            write(v >>> (8*i));
        }
    }

    /**
     * Writes a long integer to this CFASL output stream as a Bignum.
     * This method is protected because it does no size checking, so the
     * calling method must be wise as to whether Fixnum or Bignum is better.
     *
     * @param v the long integer to be written
     */
    protected void writeBignum (long v) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("* writeBignum(long " + v + ")");
        // Determine the sign, transmit the opcode, and take the absolute value
        if (v < 0) {
            write(CFASL_N_BIGNUM);
            v = -v;
        }
        else {
            write(CFASL_P_BIGNUM);
        }
        // Convert to an array of bytes in little-endian order (LSB at 0)
        int[] parts = new int[8];
        int numBytes = 0;
        while (v > 0) {
            parts[numBytes++] = (int)(v & 0x000000FF);
            v = v >>> 8;
        }
        // Transmit the size of the Bignum
        writeFixnum(numBytes);
        // Transmit the bytes of the Bignum in little-endian order (LSB first)
        for (int i = 0; i < numBytes; i++) {
            if (trace == API_TRACE_DETAILED)
                System.out.println("b\t" + parts[i]);
            // It sure seems dumb to send each byte as a fixnum instead of as
            // a raw byte.  But that is the way the CFASL protocol was written.
            writeFixnum(parts[i]);
        }
    }

    /**
     * Writes a BigInteger to this CFASL output stream as a CFASL
     * Bignum (unless it is small enough to be transmitted as a CFASL Fixnum,
     * in which case it is passed on to writeFixnum(long)).
     *
     * @param v the <tt>BigInteger</tt> to be written
     */
    public void writeBigInteger (BigInteger v) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("writeBigInteger = " + v);
        // If the absolute value of the BigInteger is less than 2^31, it can to be
        // transmitted as a CFASL Fixnum.  Why do we use v.abs().bitLength()
        // instead of just v.bitLength()?  There is exactly 1 case that is
        // different: -2^31 has a bitLength of 31 while 2^31 has a bitLength of 32.
        if (v.abs().bitLength() < 32) {
            writeFixnum(v.intValue());
            return;
        }
        // Determine the sign, transmit the opcode, and take the absolute value
        if (v.signum() < 0) {
            write(CFASL_N_BIGNUM);
            v = v.abs();
        }
        else {
            write(CFASL_P_BIGNUM);
        }
        // Convert the number to an array of bytes in big-endian order (MSB at 0)
        byte[] parts = v.toByteArray();
        // Transmit the size of the Bignum
        writeFixnum(parts.length);
        // Transmit the bytes of the Bignum in little-endian order (LSB first)
        for (int i = parts.length - 1; i >= 0; i--) {
            // System.out.println("b\t" + (parts[i] & 0x00FF));
            // It sure seems dumb to send each byte as a fixnum instead of as
            // a raw byte.  But that is the way the CFASL protocol was written.
            writeFixnum(parts[i] & 0x00FF);
        }
    }

    /**
     * Writes a double onto this CfaslOutputStream.  The double is encoded as
     * the sign (part of the opcode), significand, and exponent, such that
     * the original double can be reconstructed as sign * significand * 2^exp.
     * All parts are integers with the significand as small as possible.
     *
     * @param v the double value to be written
     */
    public void writeDouble (double v) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("writeDouble = " + v);
        if (Double.isNaN(v)) {
            throw  new RuntimeException("Tried to send a NaN floating-point");
        }
        else if (Double.isInfinite(v)) {
            throw  new RuntimeException("Tried to send an infinite floating-point");
        }
        else {
            if (v < 0.0) {
                write(CFASL_N_FLOAT);
                v = -v;
                // System.out.print("writeDouble sign=-1");
            }
            else {
                write(CFASL_P_FLOAT);
                // System.out.print("writeDouble sign=+1");
            }
            int exp = 0;
            double sig = v;
            while ((sig != 0.0) && (sig == Math.floor(sig))) {
                sig = sig/2.0;
                exp++;
            }
            while (sig != Math.floor(sig)) {
                sig = sig*2.0;
                exp--;
            }
            // System.out.println(" signif=" + (long)Math.floor(sig) + " exp=" + exp);
            writeInt((long)Math.floor(sig));
            writeInt(exp);
        }
    }

    /**
     * Writes a String to this CfaslOutputStream.
     *
     * @param s the string to be written
     */
    public void writeString (String s) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("writeString = \"" + s + "\"");
        write(CFASL_STRING);
        writeInt(s.length());
        write(s.getBytes());
    }

    /**
     * Writes a byte array to this CfaslOutputStream.
     *
     * @param bytes the byte array to be written.
     */
    public void writeByteArray (byte[] bytes) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("writeByteArray = \"" + bytes + "\"");
        write(CFASL_BYTE_VECTOR);
        writeInt(bytes.length);
        write(bytes);
    }

    /**
     * Writes a List of Objects to this CfaslOutputStream as a CFASL List.
     *
     * @param list the list of objects to be written
     */
    public void writeList (List list) throws IOException {
        if (list instanceof CycList && !((CycList)list).isProperList()) {
            writeDottedList((CycList)list);
            return;
        }
        if (trace == API_TRACE_DETAILED) {
            if (list instanceof CycList)
                System.out.println("writeList = " + ((CycList) list).safeToString() +
                                   "\n  of size " + list.size());
            else
                System.out.println("writeList = " + list +
                                   "\n  of size " + list.size());
        }
        write(CFASL_LIST);
        writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            writeObject(list.get(i));
        }
    }

    /**
     * Writes an improper (dotted) CycList of Objects to this CfaslOutputStream as a CFASL dotted list.
     *
     * @param improperList the list of objects to be written
     */
    public void writeDottedList (CycList dottedList) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("writeDottedList = " + dottedList.safeToString() +
                               "\n  proper elements size " + dottedList.size());
        write(CFASL_DOTTED);
        writeInt(dottedList.size());
        for (int i = 0; i < dottedList.size(); i++) {
            writeObject(dottedList.get(i));
        }
        Object dottedElement = dottedList.getDottedElement();
        if (trace == API_TRACE_DETAILED) {
            try {
                // If object dottedElement understands the safeToString method, then use it.
                Method safeToString = dottedElement.getClass().getMethod("safeToString", null);
                System.out.println("writeDottedList.cdr = " + safeToString.invoke(dottedElement, null));
            }
            catch (Exception e) {
                System.out.println("writeDottedList.cdr = " + dottedElement);
            }
        }
        writeObject(dottedElement);
    }

    /**
     * Writes an array of Objects to this CfaslOutputStream as a CFASL List.
     *
     * @param list the array of objects to be written
     */
    public void writeList (Object[] list) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("writeList(Array) = " + list + "\n  of size " + list.length);
        write(CFASL_LIST);
        writeInt(list.length);
        for (int i = 0; i < list.length; i++) {
            writeObject(list[i]);
        }
    }

    /**
     * Writes a <tt>Guid</tt> object to this CfaslOutputStream.
     *
     * @param guid the <tt>Guid</tt> to be written
     */
    public void writeGuid (Guid guid) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("writeGuid = " + guid);
        write(CFASL_GUID);
        writeString(guid.toString());
    }

    /**
     * Writes a <tt>CycSymbol</tt> object to this CfaslOutputStream.
     *
     * @param cycSymbol the <tt>CycSymbol</tt> to be written
     */
    public void writeSymbol (CycSymbol cycSymbol) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("writeSymbol = " + cycSymbol);
        if (cycSymbol.isKeyword()) {
            writeKeyword(cycSymbol);
            return;
        }
        if (cycSymbol.equals(CycObjectFactory.nil)) {
            if (trace == API_TRACE_DETAILED)
                System.out.println("writing CFASL_NIL");
            write(CFASL_NIL);
        }
        else {
            write(CFASL_SYMBOL);
            writeString(cycSymbol.toString().toUpperCase());
        }
    }

    /**
     * Writes a keyword symbol object to this CfaslOutputStream.
     *
     * @param cycSymbol the keyword to be written
     */
    public void writeKeyword (CycSymbol cycSymbol) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("writeKeyword = " + cycSymbol);
        write(CFASL_KEYWORD);
        writeString(cycSymbol.toString().toUpperCase());
    }

    /**
     * Writes a <tt>CycVariable</tt> object to this CfaslOutputStream.
     *
     * @param cycVariable the <tt>CycVariable</tt> to be written
     */
    public void writeVariable (CycVariable cycVariable) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("writeVariable = " + cycVariable.safeToString());
        //write(CFASL_VARIABLE);
        write(CFASL_SYMBOL);
        writeString(cycVariable.toString().toUpperCase());
    }

    /**
     * Writes a <tt>CycConstant</tt> object to this CfaslOutputStream.
     *
     * @param cycConstant the <tt>CycConstant</tt> to be written
     */
    public void writeConstant (CycConstant cycConstant) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("writeConstant = " + cycConstant.safeToString());
        write(CFASL_CONSTANT);
        writeInt(cycConstant.getId().intValue());
    }

    /**
     * Writes a <tt>CycNart</tt> object to this CfaslOutputStream.
     *
     * @param cycNart the <tt>CycNart</tt> to be written
     */
    public void writeNart (CycNart cycNart) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("writeNart = " + cycNart.safeToString());
        write(CFASL_NART);
        writeInt(cycNart.getId().intValue());
    }

    /**
     * Writes a <tt>CycAssertion</tt> object to this CfaslOutputStream.
     *
     * @param cycAssertion the <tt>CycAssertion</tt> to be written
     */
    public void writeAssertion (CycAssertion cycAssertion) throws IOException {
        if (trace == API_TRACE_DETAILED)
            System.out.println("writeAssertion = " + cycAssertion.safeToString());
        write(CFASL_ASSERTION);
        writeInt(cycAssertion.getId().intValue());
    }

    /**
     * Writes a generic object to this CfaslOutputStream.
     *
     * @param o the object to be written
     * @throws RuntimeException if the Object cannot be translated.
     */
    public void writeObject (Object o) throws IOException {
        if (trace == API_TRACE_DETAILED) {
            try {
                // If object o understands the safeToString method, then use it.
                Method safeToString = o.getClass().getMethod("safeToString", null);
                System.out.println("writeObject = " + safeToString.invoke(o, null) +
                                   " (" + o.getClass() + ")");
            }
            catch (Exception e) {
                System.out.println("writeObject = " + o + " (" + o.getClass() + ")");
            }
        }
        if (o instanceof Guid)
            writeGuid((Guid) o);
        else if (o instanceof CycSymbol)
            writeSymbol((CycSymbol) o);
        else if (o instanceof CycVariable)
            writeVariable((CycVariable) o);
        else if (o instanceof CycConstant)
            writeConstant((CycConstant) o);
        else if (o instanceof CycNart)
            writeNart((CycNart) o);
        else if (o instanceof CycAssertion)
            writeAssertion((CycAssertion) o);
        else if (o instanceof List)
            writeList((List)o);
        else if (o instanceof Boolean)
            writeBoolean(((Boolean) o).booleanValue());
        else if (o instanceof Character)
            writeChar(((Character) o).charValue());
        else if (o instanceof String)
            writeString((String) o);
        else if (o instanceof Double)
            writeDouble(((Double) o).doubleValue());
        else if (o instanceof Float)
            writeDouble(((Float) o).doubleValue());
        else if (o instanceof Long)
            writeInt(((Long) o).longValue());
        else if (o instanceof Integer)
            writeInt(((Integer) o).longValue());
        else if (o instanceof Short)
            writeInt(((Short) o).longValue());
        else if (o instanceof Byte)
            writeInt(((Byte) o).longValue());
        else if (o instanceof BigInteger)
            writeBigInteger((BigInteger) o);
        else if (o instanceof Object[])
            writeList((Object[]) o);
        else if (o instanceof ByteArray)
            writeByteArray(((ByteArray) o).byteArrayValue());
        else if (o instanceof byte[])
            writeByteArray((byte[]) o);
        else
            throw  new RuntimeException("No cfasl opcode for " + o);
    }
}



