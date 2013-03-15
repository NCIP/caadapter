/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.mif;
import java.io.Serializable;
import java.util.TreeSet;
import java.util.Set;
import java.util.Enumeration;
import java.util.Hashtable;

public class MIFIndex implements Serializable {

	private TreeSet<String> messageCategory =new TreeSet<String>();
	private Hashtable <String, String> mifNames=new Hashtable<String, String>();

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
}
