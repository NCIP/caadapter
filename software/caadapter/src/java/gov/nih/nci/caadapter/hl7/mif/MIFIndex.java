/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.hl7.mif;

/**
 * The class defines an object containing the index of HL7 message MIF.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.13 $
 *          date        $Date: 2009-05-06 15:27:48 $
 */

import java.io.File;
import java.io.Serializable;
import java.util.TreeSet;
import java.util.Set;
import java.util.Enumeration;
import java.util.Hashtable;

public class MIFIndex implements Serializable, Comparable<MIFIndex> {

	private TreeSet<String> messageCategory =new TreeSet<String>();
	private Hashtable <String, String> mifNames=new Hashtable<String, String>();
	private String copyrightYears;
	private String schemaPath;
	private String mifPath;
	public final static String DEFAULT_COPYRIGHT_YEAR="2008";
	/**
	 * @return the mifPath
	 */
	public String getMifPath()
    {
		 return mifPath;
    }

	/**
	 * @param mifPath the mifPath to set
	 */
	public void setMifPath(String mifPath)
    {
        this.mifPath = mifPath;
	}
	private String normativeDescription;

	/**
	 * @return the normativeDescription
	 */
	public String getNormativeDescription() {
		return normativeDescription;
	}

	/**
	 * @param normativeDescription the normativeDescription to set
	 */
	public void setNormativeDescription(String normativeDescription) {
		this.normativeDescription = normativeDescription;
	}

	/**
	 * @return the schemaPath
	 */
	public String getSchemaPath()
    {
         return schemaPath;
    }

	/**
	 * @param schemaPath the schemaPath to set
	 */
	public void setSchemaPath(String schemaPath)
    {
		this.schemaPath = schemaPath;
    }

	/**
	 * @return the copyrightYears
	 */
	public String getCopyrightYears() {
		return copyrightYears;
	}

	/**
	 * @param copyrightYears the copyrightYears to set
	 */
	public void setCopyrightYears(String copyrightYears) {
		this.copyrightYears = copyrightYears;
	}

	public void addMessageType(String mifFileName)
	{
		Enumeration mifEnum=mifNames.elements();
		while (mifEnum.hasMoreElements())
		{
			String existingMif=(String)mifEnum.nextElement();
			if (existingMif.equalsIgnoreCase(mifFileName))
				return ;
		}
		//only the _MT MIFs are implemented artificates
		//the _HD MIFs "hierachicalmessage defineitionthat serialize the
		//elements in RMMI
		if (mifFileName.indexOf("_HD")>-1)
			return;
		if (mifFileName.indexOf(".mif")<0)
			return;

		//new MIF file name being added
		String msgType=mifFileName.substring(0, mifFileName.indexOf(".mif"));
		//remove "UVxx" from the message type string
		if (msgType.indexOf("UV")>-1)
			msgType=msgType.substring(0, msgType.indexOf("UV"));

		String msgCat=msgType.substring(0,7);
		if (!messageCategory.contains(msgCat))
			messageCategory.add(msgCat);

		mifNames.put(msgType, mifFileName);
	}

	public Set<String> getMessageCategory()
	{
		return messageCategory;
	}


	public Set fingMessageTypesWithCategory(String msgCat)
	{
		TreeSet<String> rtnSet=new TreeSet<String>();
		Enumeration messageTypes=mifNames.keys();
		while (messageTypes.hasMoreElements())
		{
			String messageType=(String)messageTypes.nextElement();
			if (messageType.startsWith(msgCat))
				rtnSet.add(messageType);
		}
		return rtnSet;
	}

	public String findSpecificationHome()
	{
		String rtnSt=null;
		System.out.println("MIFIndex.findSpecificationHome()..mifPath:"+ getMifPath());

		if (this.getMifPath()!=null)
		{
			File mifFile=new File(this.getMifPath());
			if (mifFile.exists())
				rtnSt= mifFile.getParent();
			else
				rtnSt=getMifPath().substring(0, getMifPath().lastIndexOf("/"));
		}
		System.out.println("MIFIndex.findSpecificationHome()..mifHome:"+rtnSt);
		return rtnSt;
	}
	public String findMIFFileName(String messageType)
	{
		return (String)mifNames.get(messageType);
	}
	@Override public String toString()
	{
		return this.getNormativeDescription();
	}
	public int compareTo(MIFIndex index) {
		// TODO Auto-generated method stub
		String myCompareKey=getCopyrightYears();
		if (myCompareKey==null||myCompareKey.equals(""));
			myCompareKey=getNormativeDescription();

		String otherCompareKey=index.getCopyrightYears();
		if (otherCompareKey==null||otherCompareKey.equals(""));
			otherCompareKey=index.getNormativeDescription();
		return (myCompareKey.compareToIgnoreCase(otherCompareKey));
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.12  2009/04/17 14:14:45  wangeug
 * HISTORY :Roll back changes regarding MifPath and SchemaPath
 * HISTORY :
 * HISTORY :Revision 1.11  2009/04/02 19:17:08  altturbo
 * HISTORY :Protect from 'resource not found error' in case of 'dist' directory
 * HISTORY :
 * HISTORY :Revision 1.10  2009/04/02 04:22:33  altturbo
 * HISTORY :in case of 'dist' directory.
 * HISTORY :
 * HISTORY :Revision 1.9  2009/04/02 03:12:22  altturbo
 * HISTORY :For protecting FileNotFoundException in case of 'dist' directory and can process in case of schema directory.
 * HISTORY :
 * HISTORY :Revision 1.8  2009/03/18 15:50:36  wangeug
 * HISTORY :enable wesstart to support multiple normatives
 * HISTORY :
 * HISTORY :Revision 1.7  2009/03/13 16:28:50  wangeug
 * HISTORY :support multiple HL& normatives: set default as 2008
 * HISTORY :
 * HISTORY :Revision 1.6  2009/03/13 14:51:17  wangeug
 * HISTORY :support multiple HL& normatives
 * HISTORY :
 * HISTORY :Revision 1.5  2009/03/12 15:00:46  wangeug
 * HISTORY :support multiple HL& normatives
 * HISTORY :
 * HISTORY :Revision 1.4  2008/09/29 15:44:40  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */