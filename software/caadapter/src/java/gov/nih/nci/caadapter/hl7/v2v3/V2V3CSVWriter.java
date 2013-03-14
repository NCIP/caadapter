/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.v2v3;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * This class provides creates CSV file with the input String array and each
 * array item separated by comma implementations
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2 revision $Revision: 1.2 $ date $Date:
 *          2006/10/03 15:14:29 $
 */
public class V2V3CSVWriter {
	private Writer rawWriter;

	private PrintWriter pw;

	private char separator;

	private char quotechar;

	private String lineEnd;

	/** The character used for escaping quotes. */
	public static final char ESCAPE_CHARACTER = '"';

	/** The default separator to use if none is supplied to the constructor. */
	// public static final char DEFAULT_SEPARATOR = ',';
	public static final char DEFAULT_SEPARATOR = ',';

	/**
	 * The default quote character to use if none is supplied to the
	 * constructor.
	 */
	public static final char DEFAULT_QUOTE_CHARACTER = '"';

	/** The quote constant to use when you wish to suppress all quoting. */
	public static final char NO_QUOTE_CHARACTER = '\u0000';

	/** Default line terminator uses platform encoding. */
	public static final String DEFAULT_LINE_END = "\n";

	/**
	 * Constructs CSVWriter using a comma for the separator.
	 *
	 * @param writer
	 *            the writer to an underlying CSV source.
	 */
	public V2V3CSVWriter(Writer writer) {
		this(writer, DEFAULT_SEPARATOR);
	}

	public V2V3CSVWriter(Writer writer, char separator) {
		this(writer, separator, DEFAULT_QUOTE_CHARACTER);
	}

	/**
	 * Constructs CSVWriter with supplied separator and quote char.
	 *
	 * @param writer
	 *            the writer to an underlying CSV source.
	 * @param separator
	 *            the delimiter to use for separating entries
	 * @param quotechar
	 *            the character to use for quoted elements
	 */
	public V2V3CSVWriter(Writer writer, char separator, char quotechar) {
		this(writer, separator, quotechar, "\n");
	}

	/**
	 * Constructs CSVWriter with supplied separator and quote char.
	 *
	 * @param writer
	 *            the writer to an underlying CSV source.
	 * @param separator
	 *            the delimiter to use for separating entries
	 * @param quotechar
	 *            the character to use for quoted elements
	 * @param lineEnd
	 *            the line feed terminator to use
	 */
	public V2V3CSVWriter(Writer writer, char separator, char quotechar, String lineEnd) {
		this.rawWriter = writer;
		this.pw = new PrintWriter(writer);
		this.separator = separator;
		this.quotechar = quotechar;
		this.lineEnd = lineEnd;
	}

	/**
	 * Writes the next line to the file.
	 *
	 * @param nextLine
	 *            a string array with each comma-separated element as a separate
	 *            entry.
	 */
	public void writeNext(String[] nextLine) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < nextLine.length; i++) {
			if (i != 0) {
				sb.append(separator);
			}
			String nextElement = nextLine[i];
			if (nextElement == null)
				continue;
			if (quotechar != NO_QUOTE_CHARACTER)
				sb.append(quotechar);
			for (int j = 0; j < nextElement.length(); j++) {
				char nextChar = nextElement.charAt(j);
				if (nextChar == quotechar) {
					sb.append(ESCAPE_CHARACTER).append(nextChar);
				} else if (nextChar == ESCAPE_CHARACTER) {
					sb.append(ESCAPE_CHARACTER).append(nextChar);
				} else {
					sb.append(nextChar);
				}
			}
			if (quotechar != NO_QUOTE_CHARACTER)
				sb.append(quotechar);
		}
		//sb.append("\n");
		System.out.println( sb.toString());
		pw.write(sb.toString()+"\r\n");
	}

	/**
	 * Close the underlying stream writer flushing any buffered content.
	 *
	 * @throws IOException
	 *             if bad things happen
	 *
	 */
	public void close() throws IOException {
		pw.flush();
		pw.close();
		rawWriter.close();
	}
}
/**
 * HISTORY : $Log: not supported by cvs2svn $
 * HISTORY : Revision 1.1  2007/07/03 18:24:28  wangeug
 * HISTORY : initila loading
 * HISTORY :
 * HISTORY : Revision 1.3  2006/12/12 22:42:22  jayannah
 * HISTORY : fixed the next line in the notepad
 * HISTORY : HISTORY : Revision 1.2 2006/10/03
 * 15:14:29 jayannah HISTORY : changed the package names HISTORY : HISTORY :
 * Revision 1.1 2006/10/03 14:59:57 jayannah HISTORY : Created the files HISTORY :
 *
 */
