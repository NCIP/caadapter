/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/v2v3/SgDataInfo.java,v 1.1 2007-07-03 18:24:28 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 3.2
 * Copyright Notice.
 *
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 *
 *
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 *
 *
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear.
 *
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software.
 *
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick.
 *
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.v2v3;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.common.util.UUIDGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

/**
 * This class has the utility methods MappingMain calls to obtain the SEGMENT/FIELD information during the
 * processing and creating a V2 Message tree
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v3.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-07-03 18:24:28 $
 */
public class SgDataInfo {
    static File _aFile;

    /**
         * @param args
         * @throws Exception
         */
    public static void main(String[] args) throws Exception {
	System.out.println(new SgDataInfo().getSegmentStructure("MSH.dat"));
    }

    /**
         * @param _fileName
         * @return segmentInfo
         * @throws Exception
         */
    public String getSegmentInfo(String _fileName, String name) throws Exception {
	StringBuffer _retArray = new StringBuffer();
	String _token1 = "";
	String _token2 = "";
	String _token3 = "";
	String _token4 = "";
	int _marker1 = 0;
	BufferedReader input = new BufferedReader(new FileReader(_fileName));
	String line = null;
	String _subComponentValHolder = null;
	int _d = 1;
	EmptyStringTokenizer smt = null;
	_retArray.append("\t<field column=\"1\" name=\"" + name  + "_Message_Header\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>\n");
	while ((line = input.readLine()) != null) {
	    smt = new EmptyStringTokenizer(line, "\t");

		_token1 = smt.getTokenAt(0); // 1;header
		_token2 = smt.getTokenAt(1); // 2 segment
		_token3 = smt.getTokenAt(2); // 3 component
		_token4 = smt.getTokenAt(3);// 4 sub component
		if (new Integer(_token4).intValue() == 0) {
		    _subComponentValHolder = _token4;
		}
		String _token7 = smt.getTokenAt(6); // 7 data type
		String _tmp14 = smt.getTokenAt(13); // 14
		if (_tmp14 != null && _tmp14.length() > 0) {
		    if (_tmp14.indexOf(" - ") > 0) {
			_tmp14 = _tmp14.replaceAll(" - ", "_");
		    }
		    if (_tmp14.indexOf(" ") > 0) {
			_tmp14 = _tmp14.replaceAll("-", "_");
		    }
		    if (_tmp14.indexOf("/") > 0) {
			_tmp14 = _tmp14.replaceAll("/", "_");
		    }
		    if (_tmp14.indexOf("`") > 0) {
			_tmp14 = _tmp14.replaceAll("`", "");
		    }
		    if (_tmp14.indexOf(".") > 0) {
			_tmp14 = _tmp14.replaceAll(".", "");
		    }
		    if (_tmp14.indexOf("&") > 0) {
			_tmp14 = _tmp14.replaceAll("&", "");
		    }
		    if (_tmp14.indexOf(",") > 0) {
			_tmp14 = _tmp14.replaceAll(",", "");
		    }
		    if (_tmp14.indexOf("(") > 0) {
			_tmp14 = _tmp14.substring(0, _tmp14.indexOf("("));
		    }
		    if (_tmp14.indexOf("#") > 0) {
			_tmp14 = _tmp14.substring(0, _tmp14.indexOf("#"));
		    }
		    if (_tmp14.indexOf("+") > 0) {
			_tmp14 = _tmp14.substring(0, _tmp14.indexOf("+"));
		    }
		    if (_tmp14.indexOf("*") > 0) {
			_tmp14 = _tmp14.substring(0, _tmp14.indexOf("*"));
		    }
		    if (_marker1 == 0) {
			String s1 = "\t\t<segment name=\"" + _token1 + _token2 + "_" + _token7 + "_" + _tmp14.replaceAll(" ", "_").toUpperCase() + "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\">\n";
			if (s1.indexOf("___") > 0) {
			    s1 = s1.replaceAll("___", "_");
			}
			_retArray.append(s1);
		    } else {
			_d = 1;
			String s1 = "\t\t</segment>\n\t\t<segment name=\"" + _token1 + _token2 + "_" + _token7 + "_" + _tmp14.replaceAll(" ", "_").toUpperCase() + "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\">\n";
			_retArray.append(s1);
		    }
		    if (_token3.equalsIgnoreCase("-1") && _token4.equalsIgnoreCase("-1")) {
			String s1 = "\t\t\t<field column=\"1\" name=\"" + _tmp14.replaceAll(" ", "_") + "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>\n";
			if (s1.indexOf("___") > 0) {
			    s1 = s1.replaceAll("___", "_");
			}
			_retArray.append(s1);
		    }
		}

		String _token16 = smt.getTokenAt(15); // 16
		if (_token16 != null && _token16.length() > 0) {
		    if (_token16.indexOf("/") > 0) {
			_token16 = _token16.replaceAll("/", "_");
		    }
		    if (_token16.indexOf("(") > 0) {
			_token16 = _token16.substring(0, (_token16.indexOf("(") - 1));
		    }
		    if (_token16.indexOf("&") > 0) {
			_token16 = _token16.substring(0, (_token16.indexOf("(") - 1));
		    }
		    if (_token16.indexOf("`") > 0) {
			_token16 = _token16.replaceAll("`", "");
		    }
		    if (_token16.indexOf("&") > 0) {
			_token16 = _token16.replaceAll("&", "");
		    }
		    if (_token16.indexOf(",") > 0) {
			_token16 = _token16.replaceAll(",", "");
		    }
		    if (_token16.indexOf("(") > 0) {
			_token16 = _token16.substring(0, _token16.indexOf("("));
		    }
		    if (_token16.indexOf("#") > 0) {
			_token16 = _token16.substring(0, _token16.indexOf("#"));
		    }
		    if (_token16.indexOf("*") > 0) {
			_token16 = _token16.substring(0, _token16.indexOf("*"));
		    }
		    if (_token16.indexOf("+") > 0) {
			_token16 = _token16.substring(0, _token16.indexOf("+"));
		    }
		    if ((new Integer(_token3).intValue() > 0) && (new Integer(_token4).intValue() == -1)) {
			String s = "\t\t\t<field column=\"" + _d + "\" name=\"C" + _token3 + "_" + _token7 + "_" + _token16.replaceAll(" ", "_") + "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>\n";
			if (s.indexOf("___") > 0) {
			    s = s.replaceAll("___", "_");
			}
			_d++;
			_retArray.append(s);
		    }
		    if (new Integer(_token4).intValue() == 0) {
			_subComponentValHolder = "\"C" +_token3 + "_" + _token7 + "_" + _token16.replaceAll(" ", "_");
		    }
		    if ((new Integer(_token3).intValue() > 0) && (new Integer(_token4).intValue() > 0)) {
			String s = "\t\t\t<field column=\"" + _d + "\" name =" + _subComponentValHolder + "_" + "S" + _token4 + "_" + _token7 + "_" + _token16.replaceAll(" ", "_") + "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>\n";
			_d++;
			if (s.indexOf("___") > 0) {
			    s = s.replaceAll("___", "_");
			}
			_retArray.append(s);
		    }
		}

	    _marker1++;
	}
	return _retArray.toString();
    }

    /**
         * @param _fileName
         * @return segmentStructure
         * @throws Exception
         */
    public String getSegmentStructure(String _fileName) throws Exception {
	StringBuffer _retArray = new StringBuffer();
	BufferedReader input = new BufferedReader(new FileReader(_fileName));
	String line = null;
	String _token1 = "";
	String _token2 = "";

	while ((line = input.readLine()) != null) {
	    EmptyStringTokenizer smt = new EmptyStringTokenizer(line, "\t");
		_token1 = smt.getTokenAt(0); // 1 header
		_token2 = smt.getTokenAt(1); // 2 segment
		String _token7 = smt.getTokenAt(6); // 7 data type
		String _tmp14 = smt.getTokenAt(13); // 14
		if (_tmp14 != null && _tmp14.length() > 0) {
		    if (_tmp14.indexOf(" - ") > 0) {
			_tmp14 = _tmp14.replaceAll(" - ", "_");
		    }
		    if (_tmp14.indexOf(" ") > 0) {
			_tmp14 = _tmp14.replaceAll("-", "_");
		    }
		    if (_tmp14.indexOf("/") > 0) {
			_tmp14 = _tmp14.replaceAll("/", "_");
		    }
		    if (_tmp14.indexOf("`") > 0) {
			_tmp14 = _tmp14.replaceAll("`", "");
		    }
		    if (_tmp14.indexOf(".") > 0) {
			_tmp14 = _tmp14.replaceAll(".", "");
		    }
		    if (_tmp14.indexOf("&") > 0) {
			_tmp14 = _tmp14.replaceAll("&", "");
		    }
		    if (_tmp14.indexOf(",") > 0) {
			_tmp14 = _tmp14.replaceAll(",", "");
		    }
		    if (_tmp14.indexOf("(") > 0) {
			_tmp14 = _tmp14.substring(0, _tmp14.indexOf("("));
		    }
		    if (_tmp14.indexOf("#") > 0) {
			_tmp14 = _tmp14.replaceAll("#", "");
		    }
		    if (_tmp14.indexOf("*") > 0) {
			_tmp14 = _tmp14.substring(0, _tmp14.indexOf("*"));
		    }
		    if (_tmp14.indexOf("+") > 0) {
			_tmp14 = _tmp14.substring(0, _tmp14.indexOf("+"));
		    }
		    String _tmpStr1 = _token1 + _token2 + "_" + _token7 + "_" + _tmp14.replaceAll(" ", "_").toUpperCase() + "\n";
		    if (_tmpStr1.indexOf("___") > 0) {
			_tmpStr1 = _tmpStr1.replaceAll("___", "_");
		    }
		    _retArray.append(_tmpStr1);
		}
	}
	return _retArray.toString();
    }
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2006/11/27 21:54:28  jayannah
 * HISTORY      : *** empty log message ***
 * HISTORY      :
 * HISTORY      : Revision 1.2  2006/10/03 15:14:29  jayannah
 * HISTORY      : changed the package names
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/10/03 14:59:57  jayannah
 * HISTORY      : Created the files
 * HISTORY      :
 */