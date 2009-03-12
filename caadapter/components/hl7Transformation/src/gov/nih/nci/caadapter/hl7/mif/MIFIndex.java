/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.hl7.mif;

/**
 * The class defines an object containing the index of HL7 message MIF.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.5 $
 *          date        $Date: 2009-03-12 15:00:46 $
 */

import java.io.Serializable;
import java.util.TreeSet;
import java.util.Set;
import java.util.Enumeration;
import java.util.Hashtable;

public class MIFIndex implements Serializable {

	private TreeSet<String> messageCategory =new TreeSet<String>();
	private Hashtable <String, String> mifNames=new Hashtable<String, String>();
	private String copyrightYears;
	private String schemaPath;
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
	public String getSchemaPath() {
		return schemaPath;
	}

	/**
	 * @param schemaPath the schemaPath to set
	 */
	public void setSchemaPath(String schemaPath) {
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
	
	public String findMIFFileName(String messageType)
	{
		return (String)mifNames.get(messageType);
	}
	@Override public String toString()
	{
		return this.getNormativeDescription();
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.4  2008/09/29 15:44:40  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */