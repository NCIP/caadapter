/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.hl7.v3csv;
/**
 * The class defines XML path element.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2008-10-24 19:35:04 $
 */
import java.util.ArrayList;

public class MessageElementXmlPath extends ArrayList<String> {
	private String rootName;

	public void removeLeaf(Object leaf)
	{
		if (lastIndexOf(leaf)==(size()-1))
			remove(size()-1);
		else
			System.out.println("MessageElementXmlPath.removeLeave() :"+leaf + " is not the last element in :"+ this);
	}
	
	public String getPathValue()
	{
		StringBuffer sb=new StringBuffer();
		for (String pathElement:this)
			sb.append(pathElement+".");		
		//remove the last "."
		return sb.toString().substring(0, sb.toString().length()-1);
	}
	
	public String toString()
	{
		return getPathValue();
	}

	public String getRootName() {
		if (super.size()>0)
			rootName=(String)super.get(0);
		return rootName;
	}

	public void setRootName(String rootName) {
		this.rootName = rootName;
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.4  2008/09/29 15:47:19  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */