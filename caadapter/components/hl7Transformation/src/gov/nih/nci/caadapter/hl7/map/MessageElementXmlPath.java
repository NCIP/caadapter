/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.map;

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
