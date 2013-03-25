/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.mapping.V2V3;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.util.UUIDGenerator;
import gov.nih.nci.caadapter.hl7.v2v3.SgDataInfo;
import gov.nih.nci.caadapter.hl7.v2v3.V2V3CSVWriter;
import gov.nih.nci.caadapter.hl7.v2v3.XMLHelper;
import gov.nih.nci.caadapter.ui.hl7message.instanceGen.SCSIDChangerToXMLPath;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.knu.medinfo.hl7.v2tree.HL7MessageTreeException;

/**
 * This class takes in a HL7 message and creates a csv file and scs file. The
 * structure information is read from the V2.4 directory structure defined by
 * the user
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2 revision $Revision: 1.6 $ date $Date:
 *          2006/11/27 22:00:07 $
 */
public class MappingMain extends JFrame {
    private static final long serialVersionUID = 2485401639364434144L;

    /**
         * {@literal}
         */
    public static String _workingDir = null;

    /**
         * {@literal}
         */
    public SgDataInfo _sgDataInfo = null;

    /**
         * {@literal}
         */
    public XMLHelper _xmlHelper1 = null;

    private V2V3CSVWriter writer = null;

    private String _MSG_TYPE = null;

    private String _messageDescription = null;

    void showmessage(String m) {
	JOptionPane.showMessageDialog(this, m);
    }
    //For compatibility with the outputs of the advanced V2-V3 mapping function, this method is replaced by umkis.
    //This Changing will be restored after the test.
    //The original method with this name was changed to beginTransformationOriginal(....).
    public boolean beginTransformation(String dir, String filename, String csvLocation, String scsLocation, boolean xmlPathAsID) throws Exception
    {
        boolean resBoolean = true;
        File dirFile = new File(dir);
        if (!dirFile.exists()) throw new Exception("Not found this directory : " + dir);
        if (!dirFile.isDirectory()) throw new Exception("This is not a directory name. : " + dir);
        File dataDir = dirFile.getParentFile();
        String temp = (new File(FileUtil.getV2DataDirPath())).getName();
        String dataDirAbsolutePath = "";
        if (dataDir.getName().equals(temp)) dataDirAbsolutePath = dataDir.getAbsolutePath();
        else if (dirFile.getName().equals(temp)) dataDirAbsolutePath = dirFile.getAbsolutePath();
        else throw new Exception("This is not a HL7 V2 Data directory. : " + dir);
        try
        {
            V2Converter converter = new V2Converter(filename, dataDirAbsolutePath);
            converter.process(scsLocation, csvLocation, false, true, null, "", xmlPathAsID);
        }
        catch(HL7MessageTreeException he)
        {
            throw new Exception(he.getMessage());
        }
        catch(NullPointerException he)
        {
            throw new Exception(he.getMessage());
        }

        return resBoolean;
    }
    /***********************************************************************
         * @param dir
         * @return booleanVal
         * @throws Exception
         */
    public boolean beginTransformation(String dir, String filename, String csvLocation, String scsLocation) throws Exception {
	System.out.println("The file name which is passed is " + filename);
	ArrayList<String> _segNames = new ArrayList<String>();
	java.util.Set<String> sNames = new java.util.HashSet<String>();
	_sgDataInfo = new SgDataInfo();
	FileWriter fileWriter = null;
	_workingDir = dir;
	/**
         * Add smarts in here to identify the message type and the event type
         */
	BufferedReader inputTMP = new BufferedReader(new FileReader(filename));
	String lineTMP = null;
	EmptyStringTokenizer strTk = null;
	try {
	    while ((lineTMP = inputTMP.readLine()) != null) {
		String _segName = lineTMP.substring(0, 3).trim();
		if (_segName.equalsIgnoreCase("MSH")) {
		    strTk = new EmptyStringTokenizer(lineTMP.toString(), "|");
		    StringTokenizer _str = new StringTokenizer(strTk.getTokenAt(8), "^");
		    _MSG_TYPE = _str.nextToken().trim() + "_" + _str.nextToken().trim();
		}
		if (sNames.add(_segName)) {
		    _segNames.add(_segName);
		}
	    }
	} catch (Exception e1) {
	    // TODO Auto-generated catch block
	    // e1.printStackTrace();
	    System.out.println("empty line BAH");
	}
	try {
	    if (process(_segNames)) {
		if (createXMLFile(scsLocation)) {
		    System.out.println("\"" + scsLocation + ".SCS\"");
		    System.out.println("The save file name is " + csvLocation);
		    fileWriter = new FileWriter(csvLocation);
		    writer = new V2V3CSVWriter(fileWriter);
		    getContents(new File(filename));
		    writer.close();
		} else {
		    throw new Exception("Create XML Failed");
		}
	    } else {
		throw new Exception("Begin Transformation Error");
	    }
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    this.showmessage(e.getLocalizedMessage());
	    return false;
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    this.showmessage(e.getLocalizedMessage());
	    return false;
	}
	return true;
    }

    public boolean execute(String directoryName, String hl7v2FileName, String _csvFileLocation) throws Exception {
	MappingMain transFormFiles = new MappingMain();
	if (transFormFiles.beginTransformation(directoryName, hl7v2FileName, _csvFileLocation, null, true)) {
	    return true;
	} else {
	    return false;
	}
    }

    public boolean execute(String directoryName, String hl7v2FileName, String _csvFileLocation, String _scsFileLocation) throws Exception {
	MappingMain transFormFiles = new MappingMain();
	if (transFormFiles.beginTransformation(directoryName, hl7v2FileName, _csvFileLocation, _scsFileLocation, true)) {
	    return true;
	} else {
	    return false;
	}
    }

    public boolean process(ArrayList _segNames) {
	// ArrayList _segNames = new ArrayList(_segNamesSet);
	_xmlHelper1 = new XMLHelper();
	int _firstmarker = 0;
	_xmlHelper1.addBeginTag("csvMetadata", true); // the begin tag
	_xmlHelper1.addAttribute("uuid", UUIDGenerator.getUniqueString());
	_xmlHelper1.addAttribute("version", "1.2");
	_xmlHelper1.closeTag();
	_xmlHelper1.addBeginTag("segment", true);
	_xmlHelper1.addAttribute("name", _MSG_TYPE);
	_xmlHelper1.addAttribute("uuid", UUIDGenerator.getUniqueString());
	_xmlHelper1.closeTag();
	try {
	    _MSG_TYPE = _MSG_TYPE.trim();
	    BufferedReader input = new BufferedReader(new FileReader(_workingDir + "\\MessageStructure\\" + _MSG_TYPE + ".dat"));
	    String line = null;
	    String _structure = "";
	    java.util.ArrayList<String> ary = new java.util.ArrayList<String>();
	    EmptyStringTokenizer strTk;
	    while ((line = input.readLine()) != null) {
		strTk = new EmptyStringTokenizer(line.toString(), "\t");
		_structure = strTk.getTokenAt(6);
		_messageDescription = strTk.getTokenAt(9);
	    } // End of while loop;
	    try {
		java.util.Set<String> s = new java.util.HashSet<String>();
		EmptyStringTokenizer _getSegNames = new EmptyStringTokenizer(_structure, " ");
		String tempStr = "";
		while (_getSegNames.hasMoreTokens()) {
		    try {
			tempStr = _getSegNames.nextToken().toString().substring(0, 3);
			if (!(tempStr.charAt(0) == '!')) {
			    ary.add(tempStr.trim());
			}
		    } catch (Exception e) {
			/**
                         * This is a friendly exception; Okay to ignore
                         */
			continue;
		    }
		}
		String _tmp = ""; // declared outside the loop;
		// less-expensive
		// for (int i = 0; i < _segNames.size(); i++) {
		for (int i = 0; i < ary.size(); i++) {
		    try {
			if (s.add(ary.get(i).toString())) {
			    // String tmp = ary.get( i-1).toString();
			    _tmp = _sgDataInfo.getSegmentInfo(_workingDir + "/SegmentAttributeTable/" + ary.get(i).toString() + ".DAT", ary.get(i).toString());
			    if (_firstmarker != 0) {
				_xmlHelper1.addValue("\t");
				_xmlHelper1.addEndTag("segment");
			    }
			    _xmlHelper1.addValue("\t");
			    _xmlHelper1.addBeginTag("segment", true);
			    _xmlHelper1.addAttribute("name", ary.get(i).toString());
			    _xmlHelper1.addAttribute("uuid", UUIDGenerator.getUniqueString());
			    _xmlHelper1.closeTag();
			    _xmlHelper1.append(_tmp);
			    _xmlHelper1.addValue("\t");
			    _xmlHelper1.addValue("\t");
			    _xmlHelper1.addEndTag("segment");
			}
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		    _firstmarker++;
		}
	    } catch (Exception e) {
		e.printStackTrace();
		this.showmessage(e.getLocalizedMessage());
		System.out.println(" process1 :: 002 :: Message is " + e.getMessage());
		return false;
	    }
	} catch (Exception s) {
	    s.printStackTrace();
	    this.showmessage(s.getLocalizedMessage());
	    return false;
	}
	return true;
    }

    public String execute(String directoryName, String hl7v2FileName) throws Exception {
	MappingMain transFormFiles = new MappingMain();
	if (transFormFiles.beginTransformation(directoryName, hl7v2FileName, "", "", true)) {
	    String s2 = transFormFiles._MSG_TYPE + ".scs";
	    return (s2);
	} else {
	    this.showmessage("Please check the logs and try again");
	    return "Please check the logs and try again";
	}
    }

    /***********************************************************************
         * @param args
         **********************************************************************/
    public static void main(String args[]) {
	Long t1 = System.currentTimeMillis();
	MappingMain transFormFiles = new MappingMain();
	try {
	    if (transFormFiles.beginTransformation(args[0], args[1], args[2], args[3], true))
		System.out.println("\"" + transFormFiles._MSG_TYPE + ".CSV\" file created successfully");
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    transFormFiles.showmessage(e.getLocalizedMessage());
	}
	Long t2 = System.currentTimeMillis();
	System.out.println("The Time taken is " + (t2 - t1) / (1000F) + " sec(s)");
    }

    /***********************************************************************
         * This method creates the scs file
         *
         * @return boolean
         */
    public boolean process() throws Exception {
	_xmlHelper1 = new XMLHelper();
	int _firstmarker = 0;
	_xmlHelper1.addBeginTag("csvMetadata", true); // the begin tag
	_xmlHelper1.addAttribute("uuid", UUIDGenerator.getUniqueString());
	_xmlHelper1.addAttribute("version", "1.2");
	_xmlHelper1.closeTag();
	_xmlHelper1.addBeginTag("segment", true);
	_xmlHelper1.addAttribute("name", _MSG_TYPE);
	_xmlHelper1.addAttribute("uuid", UUIDGenerator.getUniqueString());
	_xmlHelper1.closeTag();
	try {
	    _MSG_TYPE = _MSG_TYPE.trim();
	    BufferedReader input = new BufferedReader(new FileReader(_workingDir + "\\MessageStructure\\" + _MSG_TYPE + ".dat"));
	    String line = null;
	    String _structure = "";
	    java.util.ArrayList<String> ary = new java.util.ArrayList<String>();
	    EmptyStringTokenizer strTk;
	    while ((line = input.readLine()) != null) {
		strTk = new EmptyStringTokenizer(line.toString(), "\t");
		_structure = strTk.getTokenAt(6);
		_messageDescription = strTk.getTokenAt(9);
	    } // End of while loop;
	    try {
		java.util.Set<String> s = new java.util.HashSet<String>();
		EmptyStringTokenizer _getSegNames = new EmptyStringTokenizer(_structure, " ");
		String tempStr = "";
		while (_getSegNames.hasMoreTokens()) {
		    try {
			tempStr = _getSegNames.nextToken().toString().substring(0, 3);
			if (!(tempStr.charAt(0) == '!')) {
			    ary.add(tempStr.trim());
			}
		    } catch (Exception e) {
			continue;
		    }
		}
		String _tmp = ""; // declared outside the loop;
		// less-expensive
		for (int i = 0; i < ary.size(); i++) {
		    try {
			if (s.add(ary.get(i).toString())) {
			    _tmp = _sgDataInfo.getSegmentInfo(_workingDir + "/SegmentAttributeTable/" + ary.get(i).toString() + ".DAT", null);
			    if (_firstmarker != 0) {
				// _xmlHelper1.addValue( "\t<field column=\"1\"
				// name=\"Message_Header\" uuid=\"" +
				// UUIDGenerator.getUniqueString() + "\"/>\n");
				// _xmlHelper1.addValue( "\t");
				// _xmlHelper1.addEndTag( "segment");
			    }
			    _xmlHelper1.addValue("\t");
			    _xmlHelper1.addBeginTag("segment", true);
			    _xmlHelper1.addAttribute("name", ary.get(i));
			    _xmlHelper1.addAttribute("uuid", UUIDGenerator.getUniqueString());
			    _xmlHelper1.closeTag();
			    _xmlHelper1.append(_tmp);
			    _xmlHelper1.addValue("\t");
			    _xmlHelper1.addValue("\t");
			    _xmlHelper1.addEndTag("segment");
			}
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		    _firstmarker++;
		}
	    } catch (Exception e) {
		e.printStackTrace();
		System.out.println(" process1 :: 002 :: Message is " + e.getMessage());
		return false;
	    }
	} catch (Exception s) {
	    s.printStackTrace();
	    this.showmessage(s.getLocalizedMessage());
	    return false;
	}
	return true;
    }

    /**
         * @param _segment
         * @return String
         * @throws Exception
         */
    public Vector<String> getSegmentData(String _segment) throws Exception {
	Vector<String> segmentInformation = new Vector<String>();
	String _segDataRet = _sgDataInfo.getSegmentStructure(_workingDir + "\\SegmentAttributeTable\\" + _segment + ".DAT");
	EmptyStringTokenizer smt = new EmptyStringTokenizer(_segDataRet, "\n");
	while (smt.hasMoreTokens()) {
	    segmentInformation.add(smt.nextToken());
	}
	segmentInformation.insertElementAt(_segment, 0);
	return segmentInformation;
    }

    /**
         * @return boolean
         */
    @SuppressWarnings("finally")
    public boolean createXMLFile(String scsFileName) {
	try {
	    // BufferedWriter out = new BufferedWriter( new FileWriter(
	    // _MSG_TYPE + ".scs"));
	    BufferedWriter out = new BufferedWriter(new FileWriter(scsFileName));
	    out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
	    out.write(_xmlHelper1.toString());
	    String _field1 = "\t<field column=\"1\" name=\"Message_Header\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>\n";
	    out.write("\t</segment>\n" + _field1 + "</segment>\n</csvMetadata>");
	    // out.write(_field1 + "</segment>\n</csvMetadata>");
	    out.close();
	} catch (Exception e) {
	    this.showmessage(e.getLocalizedMessage());
	    System.out.println(" createXMLFile :: Message is " + e.getMessage());
	    return false;
	} finally {
	    return true;
	}
    }

    /**
         * @param aFile
         * @return boolean
         * @throws Exception
         *                 Read the HL7 message file
         */
    public boolean getContents(File aFile) throws Exception {
	int _headMarker = 0; // to ensure the first line is written only once
	BufferedReader input = null;
	HashMap<String, String> _hmap = new HashMap<String, String>();
	BufferedReader readSegName = new BufferedReader(new FileReader(_workingDir + "\\DefinitionTable\\9901.DAT"));
	Vector<String> _hl7V2SegmentInfo = null;
	Vector<String> _hl7V2DataInfo = null;
	String segmentHeaderName = null;
	try {
	    String _line = null;
	    EmptyStringTokenizer _smrtTkn = null;
	    // Read the contents to a HashMap from 9901.DAT to show the
	    // segment description information
	    while ((_line = readSegName.readLine()) != null) {
		_smrtTkn = new EmptyStringTokenizer(_line.toString(), "\t");
		_hmap.put(_smrtTkn.getTokenAt(3), _smrtTkn.getTokenAt(4));
	    }
	    input = new BufferedReader(new FileReader(aFile));
	    String line = null; // not declared within while loop
	    EmptyStringTokenizer strTk = null; // not declared within
	    // while-loop
	    String _formatHeaderString = null; // not declared within
	    // while-loop
	    String _tmpStr = null; // not declared within while loop
	    while ((line = input.readLine()) != null) {
		_hl7V2SegmentInfo = new Vector<String>();
		_hl7V2DataInfo = new Vector<String>();
		if (line.length() > 0) {
		    _hl7V2SegmentInfo = getSegmentData(line.substring(0, 3).toString());
		} else {
		    continue;
		}
		strTk = new EmptyStringTokenizer(line, "|");
		/*
                 * Exhaust first token to make way for the segment header
                 * description
                 */
		segmentHeaderName = strTk.nextToken();
		while (strTk.hasMoreTokens()) {
		    _tmpStr = strTk.nextToken().toString();
		    if (!(line.substring(0, 3).equalsIgnoreCase("MSH"))) {
			_tmpStr = _tmpStr.replaceAll("&", "^");
		    }
		    _hl7V2DataInfo.add(_tmpStr);
		}
		if (_hl7V2DataInfo.size() > _hl7V2SegmentInfo.size()) {
		    this.showmessage("Problem with segment " + line.substring(0, 3) + "\n Please check the logs and try again");
		    System.out.println("Problem with segment " + line.substring(0, 3));
		    System.out.println("_hl7V2DataInfo " + _hl7V2DataInfo);
		    System.out.println("_hl7V2SegmentInfo " + _hl7V2SegmentInfo);
		}
		if (_hl7V2DataInfo.size() <= _hl7V2SegmentInfo.size()) {
		    if (_headMarker == 0) {
			if (_messageDescription.length() > 8) {
			    _hl7V2DataInfo.insertElementAt(_messageDescription.replaceAll("/", "_").substring(0, 7), 0);
			} else {
			    _hl7V2DataInfo.insertElementAt("No DEF", 0);
			}
			_hl7V2SegmentInfo.insertElementAt(_MSG_TYPE, 0);
			_hl7V2DataInfo.insertElementAt(_hmap.get(segmentHeaderName).replaceAll(" ", "_"), 1);
		    } else {
			_formatHeaderString = _hmap.get(segmentHeaderName);
			if (_formatHeaderString != null) {
			    _formatHeaderString = _formatHeaderString.replaceAll(" ", "_");
			    _formatHeaderString = _formatHeaderString.replaceAll("_-_", "_");
			    _hl7V2DataInfo.insertElementAt(_formatHeaderString, 0);
			} else {
			    _hl7V2DataInfo.insertElementAt("null", 0);
			}
		    }
		    if (line.substring(0, 3).toString().equalsIgnoreCase("MSH")) {
			_hl7V2DataInfo.insertElementAt("|", 2);
		    }
		    _headMarker++;
		    writeCSVFile(_hl7V2DataInfo, _hl7V2SegmentInfo);
		}
	    } // End of While loop for reading the HL7 message
	} catch (FileNotFoundException ex) {
	    ex.printStackTrace();
	    this.showmessage(ex.getLocalizedMessage());
	} catch (IOException ex) {
	    ex.printStackTrace();
	    this.showmessage(ex.getLocalizedMessage());
	} finally {
	    try {
		if (input != null) {
		    input.close();
		}
	    } catch (IOException ex) {
		ex.printStackTrace();
	    }
	}
	return true;
    }

    private void writeCSVFile(Vector<String> dataInfo, Vector<String> segmentInfo) throws Exception {
	String[] nextLine = null;
	EmptyStringTokenizer smt1 = null;
	EmptyStringTokenizer smt2 = null;
	String tmp = null;
	for (int i = 0; i < dataInfo.size(); i++) {
	    if (dataInfo.get(i).toString().length() > 0) {
		if ((!segmentInfo.get(1).equalsIgnoreCase("MSH")) && (dataInfo.get(i).toString().indexOf('~') > 0)) {
		    smt1 = new EmptyStringTokenizer(dataInfo.get(i).toString(), "~");
		    int k = i;
		    dataInfo.remove(k);
		    String repeatStr = segmentInfo.get(i);
		    segmentInfo.remove(k);
		    while (smt1.hasMoreTokens()) {
			tmp = smt1.nextToken();
			segmentInfo.insertElementAt(repeatStr, k);
			dataInfo.insertElementAt(tmp, k);
			k++;
		    }
		}
		if (!(segmentInfo.get(i).equalsIgnoreCase("MSH2_ST_ENCODING_CHARACTERS"))) {
		    smt2 = new EmptyStringTokenizer((String) dataInfo.get(i).toString(), "^");
		    nextLine = new String[smt2.countTokens() + 1];
		    nextLine[0] = (String) segmentInfo.get(i);
		    int u = 1;
		    while (smt2.hasMoreTokens()) {
			nextLine[u] = smt2.nextToken().toString();
			u++;
		    }
		} else {
		    nextLine = new String[] { (String) segmentInfo.get(i), (String) dataInfo.get(i) };
		}
		writer.writeNext(nextLine);
	    }
	}
    }
}
/**
 * HISTORY : $Log: not supported by cvs2svn $
 * HISTORY : Revision 1.5  2007/09/18 00:04:34  umkis
 * HISTORY : Temporary files will be automatically deleted when system exit.
 * HISTORY :
 * HISTORY : Revision 1.4  2007/09/18 00:03:41  umkis
 * HISTORY : Temporary files will be automatically deleted when system exit.
 * HISTORY :
 * HISTORY : Revision 1.3  2007/08/17 01:13:28  umkis
 * HISTORY : generated SCS file using xml path
 * HISTORY :
 * HISTORY : Revision 1.2  2007/07/12 16:44:39  umkis
 * HISTORY : no message
 * HISTORY :
 * HISTORY : Revision 1.1  2007/07/03 19:32:58  wangeug
 * HISTORY : initila loading
 * HISTORY :
 * HISTORY : Revision 1.7  2006/12/21 18:06:11  jayannah
 * HISTORY : Changed back to the original version
 * HISTORY :
 * HISTORY : Revision 1.6  2006/12/14 21:37:33  umkis
 * HISTORY : For compatibility with the outputs of the advanced V2-V3 mapping function, beginTransformation method is replaced by umkis. The original method with this name was changed to beginTransformationOriginal(....).
 * HISTORY :
 * HISTORY : Revision 1.5  2006/12/14 16:25:24  jayannah
 * HISTORY : fix for defect when empty lines are present in the hl7 message file
 * HISTORY : HISTORY : Revision 1.4 2006/11/27
 * 22:00:07 jayannah HISTORY : *** empty log message *** HISTORY : HISTORY :
 * Revision 1.2 2006/10/03 15:23:32 jayannah HISTORY : Changed the package names
 * HISTORY : HISTORY : Revision 1.1 2006/10/03 13:52:10 jayannah HISTORY :
 * Adding the file to CVS HISTORY :
 */
