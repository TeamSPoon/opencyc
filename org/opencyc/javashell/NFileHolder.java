package org.opencyc.javashell;

import java.io.File;

/** utility class that associates an <code>Object</code> with a <code>File</code>.
*/
public class NFileHolder {

    protected Object object;
    protected File file;
    protected long date = 0l;
    /** constuctor to create the association between a <code>File</code> and an <code>Object</code>.
     * @param file the <code>File</code> that needs the association.
     * @param object the <code>Object</code> that is to be associated with the <code>File</code>.
     */
    public NFileHolder(File file, Object object) {
	this.object = object;
	this.file = file;
	date = file.lastModified();
    }
    /** retrieves the <code>File</code> that forms half of this association.
     * @return the <code>File</code> that is half of the association.
     */
    public File getFile() {
	return file;
    }
    /** retrieves the <code>Object</code> that forms half of this association.
     * @return the <code>Object</code> that is half of the association.
     */
    public Object getObject() {
	return object;
    }
    /** retrieves the timestamp at which the association between a <code>File</code> and an <code>Object</code> was made.
     * @return the timestamp at which the association between a <code>File</code> and an <code>Object</code> was made as a <code>long</code>.
     */
    public long date() {
	return date;
    }
}
