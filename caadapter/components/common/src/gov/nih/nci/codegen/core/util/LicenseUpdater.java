
package gov.nih.nci.codegen.core.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.*;


/**
 * <!-- LICENSE_TEXT_START -->
The caAdapter Software License, Version 3.2
Copyright Notice.

Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 

1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 

2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:


"This product includes software developed by the SAIC and the National Cancer Institute."


If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 

3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 

4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 

5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * @author caBIO Team
 * @version 1.0
 */
public class LicenseUpdater {

	private static Logger logger = Logger.getLogger(LicenseUpdater.class);

	private String _beginPattern;

	private String _endPattern;

	private String _outputDirName;

	private String _text;

	private String _logFileName;

	private Collection _files = new ArrayList();

	private FileWriter _log;

	/**
	 *
	 */
	public LicenseUpdater() {
		super();

	}

	public void setBeginPattern(String p_beginPattern) {
		_beginPattern = p_beginPattern;
	}

	public String getBeginPattern() {
		return _beginPattern;
	}

	public void setEndPattern(String p_endPattern) {
		_endPattern = p_endPattern;
	}

	public String getEndPattern() {
		return _endPattern;
	}

	public void setOutputDirName(String p_outputDirName) {
		_outputDirName = p_outputDirName;
	}

	public String getOutputDirName() {
		return _outputDirName;
	}

	public void setLogFileName(String p_logFileName) {
		_logFileName = p_logFileName;
	}

	public String getLogFileName() {
		return _logFileName;
	}

	public Collection getFiles() {
		return _files;
	}

	public void setText(String p_text) {
		_text = p_text;
	}

	public String getText() {
		return _text;
	}

	public void validate() throws IllegalStateException {
		if (_beginPattern == null) {
			logger.error("beginPattern not set");
			throw new IllegalStateException("beginPattern not set");
		}
		if (_endPattern == null) {
			logger.error("endPattern not set");
			throw new IllegalStateException("endPattern not set");
		}
		if (_text == null) {
			logger.error("text not set");
			throw new IllegalStateException("text not set");
		}
	}

	private void init() throws Exception {
		validate();
		if (_logFileName != null) {
			File f = new File(_logFileName);
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			_log = new FileWriter(new File(_logFileName));
		}
	}

	public void run() throws Exception {
		init();
		log("Processing " + _files.size() + " files...");
		for (Iterator i = _files.iterator(); i.hasNext();) {
			File f = (File) i.next();
			int beginLineNum = getLineNumber(f, getBeginPattern());
			int endLineNum = getLineNumber(f, getEndPattern());
			if (beginLineNum < 0) {
				log("No begin pattern: " + f.getAbsolutePath());
			} else if (endLineNum < 0) {
				log("No end pattern: " + f.getAbsolutePath());
			} else {
				File outFile = getOutFile(f, getOutputDirName());
				insertText(f, outFile, beginLineNum, endLineNum, getText());
			}
		}
		log("...done");
	}

	/**
	 * @param f
	 * @param outputDirName
	 * @return
	 */
	private File getOutFile(File f, String outputDirName) {
		File outFile = null;
		if (outputDirName == null) {
			outFile = f;
		} else {
			String currDir = (new File(".")).getAbsolutePath();
			String relDir = f.getParentFile().getAbsolutePath().substring(
					currDir.length() - 1);
			outFile = new File(outputDirName + "/" + relDir + "/" + f.getName());
			if (!outFile.getParentFile().exists()) {
				outFile.getParentFile().mkdirs();
			}
		}
		return outFile;
	}

	/**
	 * @param endPattern
	 * @return
	 */
	private int getLineNumber(File inFile, String pattern) {
		boolean found = false;
		int lineNum = 1;
		try {
			BufferedReader br = new BufferedReader(new FileReader(inFile));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.indexOf(pattern) > -1) {
					found = true;
					break;
				}
				lineNum++;
			}
		} catch (Exception ex) {
			logger.error("Error reading file" + ex.getMessage());
			throw new RuntimeException("Error reading file", ex);
		}
		if (!found) {
			lineNum = -1;
		}
		return lineNum;
	}

	/**
	 * @param f
	 * @param beginPattern
	 * @param endPattern
	 * @param text
	 */
	private void insertText(File inFile, File outFile, int beginLineNum,
			int endLineNum, String text) {
		StringBuffer sb = new StringBuffer();
		StringBuffer upToInc = getUpToIncluding(inFile, beginLineNum);
		StringBuffer afterInc = getAfterIncluding(inFile, endLineNum);

		sb.append(upToInc);
		sb.append(text);
		if(!text.endsWith("\n")){
			sb.append("\n");
		}
		sb.append(afterInc);
		try {
			FileWriter out = new FileWriter(outFile);
			out.write(sb.toString());
			out.flush();
			out.close();
		} catch (Exception ex) {
			logger.error("Error writing " + ex.getMessage());
			throw new RuntimeException("Error writing", ex);
		}
	}

	/**
	 * @param inFile
	 * @param endLineNum
	 * @return
	 */
	private StringBuffer getAfterIncluding(File inFile, int endLineNum) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new FileReader(inFile));
			String line = null;
			int lineNum = 1;
			while ((line = br.readLine()) != null) {
				if (lineNum >= endLineNum) {
					sb.append(line);
					sb.append("\n");
				}
				lineNum++;
			}
		} catch (Exception ex) {
			logger.error("Error reading file " + ex.getMessage());
			throw new RuntimeException("Error reading file", ex);
		}
		return sb;
	}

	/**
	 * @param inFile
	 * @param beginLineNum
	 * @return
	 */
	private StringBuffer getUpToIncluding(File inFile, int beginLineNum) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new FileReader(inFile));
			String line = null;
			int lineNum = 1;
			while ((line = br.readLine()) != null && lineNum <= beginLineNum) {
				sb.append(line);
				sb.append("\n");
				lineNum++;
			}
		} catch (Exception ex) {
			logger.error("Error reading file "+ ex.getMessage());
			throw new RuntimeException("Error reading file", ex);
		}
		return sb;
	}

	private void log(String msg) {
		if (_log != null) {
			try {
				_log.write(msg + "\n");
				_log.flush();
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}

		} else {

		}
	}

	public void finalize() {
		if (_log != null) {
			try {
				_log.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			LicenseUpdater lu = new LicenseUpdater();
			lu.setBeginPattern(args[0]);
			lu.setEndPattern(args[1]);
			lu.setOutputDirName(args[2]);
			lu.setText(args[3]);
			lu.setLogFileName(args[4]);
			lu.getFiles().add(new File(args[5]));
			lu.run();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
