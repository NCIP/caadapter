/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.mapping.catrend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.TextSerializer;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.ui.common.MappableNode;

/**
 * Define the reporting class to list mapped or mapped element from a s mapping source tree
 * @author wangeug
 *
 */
public class CsvToXmiMappingReporter {
	public static String REPORT_MAPPED="REPORT_MAPPED";
	public static String REPORT_UNMAPPED="REPORT_UNMAPPED";
	private String reportType=REPORT_MAPPED;

	private MappableNode treeNode;
	private String sourceFileName;
	private String targetFileName;

	private ArrayList<String> reportElments=new ArrayList<String>();

	/**
	 * Constructor with the default reporting type to report mapped elements
	 * @param nodeToReport
	 */
	public CsvToXmiMappingReporter(MappableNode nodeToReport)
	{
		this(nodeToReport,null);
	}
	/**
	 * Constructor given reporting type
	 * @param nodeToReport
	 * @param type
	 */
	public CsvToXmiMappingReporter(MappableNode nodeToReport, String type)
	{
		treeNode=nodeToReport;
		if (type!=null&&!type.equals(""))
			reportType=type;
		verifyMapping();
	}

	private void verifyMapping()
	{
		reportElments.clear();
		if (treeNode==null)
			reportElments.add("Invalid reporting node: tree node has not been set");
		boolean checkMapped=true;
		if (reportType.equalsIgnoreCase(REPORT_UNMAPPED))
			checkMapped=false;
		else if (!reportType.equalsIgnoreCase(REPORT_MAPPED))
		{
			reportElments.add("Invalid reporting type:"+reportType);
			reportElments.add("The correct reporting type is :"+REPORT_MAPPED+"|"+REPORT_UNMAPPED);
			return;
		}
		recursivelyVerifyNode(treeNode, checkMapped);
	}

	private void recursivelyVerifyNode(MappableNode node, boolean checkType)
	{
		if (!(node instanceof DefaultMutableTreeNode ))
		{
			reportElments.add("Invalid tree node type:"+node.getClass().getName());
			return;
		}
		DefaultMutableTreeNode mutableNode=(DefaultMutableTreeNode)node;
		if (node.isMapped()==checkType)
		{
			if (mutableNode.isLeaf())
			{
				MetaObject metaObj=(MetaObject)mutableNode.getUserObject();
//				reportElments.add( metaObj.getXmlPath());
				reportElments.add(((MetaObjectImpl)metaObj).getXPath());//.getXmlPath());
			}
		}
		for (int i=0;i<mutableNode.getChildCount(); i++)
		{
			MappableNode childNode=(MappableNode)mutableNode.getChildAt(i);
			recursivelyVerifyNode(childNode, checkType);
		}
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
		verifyMapping();
	}
	public MappableNode getTreeNode() {
		return treeNode;
	}
	public void reload()
	{
		this.verifyMapping();
	}
	public void setTreeNode(MappableNode treeNode) {
		this.treeNode = treeNode;
		this.verifyMapping();
	}
	/**
	 * Generate the reporting with the given path
	 * @param filePath
	 */
	public void generateReportFile(File file)
	{
		Document rptDom=createReportDocument();
		printToFile( rptDom, file);
	}

	public String getReportAsString()
	{
		Document rptDom=createReportDocument();
		try
		{
			OutputFormat format = new OutputFormat(rptDom);
			format.setIndenting(true);

			//convert XML document into a string
			StringWriter sw = new StringWriter();
			XMLSerializer stSerializer=new XMLSerializer(sw,format);
			stSerializer.serialize(rptDom);

			return sw.toString();
		} catch(IOException ie) {
		    ie.printStackTrace();
		}
		return "";
	}
	/**
	 * Create an XML document based on mapping verification result
	 * @return
	 */
	private Document createReportDocument()
	{
		Document dom=null;
		DocumentBuilderFactory dbf = DocumentBuilderFactoryImpl.newInstance();
		try
		{
			//get an instance of builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//create an instance of DOM
			dom = db.newDocument();
			Element  rootElm=dom.createElement("unmapped");
			rootElm.setAttribute("type", reportType);
			dom.appendChild(rootElm);

			Element components=dom.createElement("components");
			Element srcNode=dom.createElement("component");
			srcNode.setAttribute("kind", "scs");
			String srcFilePath = getSourceFileName();
            if (srcFilePath.startsWith(FileUtil.getWorkingDirPath()))
            	srcFilePath = srcFilePath.replace(FileUtil.getWorkingDirPath(), Config.CAADAPTER_HOME_DIR_TAG);

			srcNode.setAttribute("location", srcFilePath);
			srcNode.setAttribute("type", "source");
			components.appendChild(srcNode);

			Element trgtNode=dom.createElement("component");
			trgtNode.setAttribute("kind", "xmi");
			String tgrtFilePath =getTargetFileName();
            if (tgrtFilePath.startsWith(FileUtil.getWorkingDirPath()))
            	tgrtFilePath = tgrtFilePath.replace(FileUtil.getWorkingDirPath(), Config.CAADAPTER_HOME_DIR_TAG);

			trgtNode.setAttribute("location", tgrtFilePath);
			trgtNode.setAttribute("type", "target");
			components.appendChild(trgtNode);

			rootElm.appendChild(components);

			Element unmappedFields=dom.createElement("fields");
			for(String oneItem:reportElments)
			{
				Element rptItem=dom.createElement("field");
				rptItem.setAttribute("kind", "scs");
				rptItem.setAttribute("xmlPath", oneItem);
				unmappedFields.appendChild(rptItem);
			}
			rootElm.appendChild(unmappedFields);
		}
		catch(ParserConfigurationException  e)
		{
			e.printStackTrace();
		}
		return dom;
	}

	/**
	 * This method uses Xerces specific classes
	 * prints the XML document to file.
     */
	private void printToFile(Document dom, File file){
		try
		{
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);

			//to generate output to console use this serializer
//			XMLSerializer serializer = new XMLSerializer(System.out, format);

			//to generate a file output use fileoutputstream instead of system.out
			XMLSerializer serializer = new XMLSerializer(
			new FileOutputStream(file), format);
			serializer.serialize(dom);

		} catch(IOException ie) {
		    ie.printStackTrace();
		}
	}
	public String getSourceFileName() {
		return sourceFileName;
	}
	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}
	public String getTargetFileName() {
		return targetFileName;
	}
	public void setTargetFileName(String targetFileName) {
		this.targetFileName = targetFileName;
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
**/