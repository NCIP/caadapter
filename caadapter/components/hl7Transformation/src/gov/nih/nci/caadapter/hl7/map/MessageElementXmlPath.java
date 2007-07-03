package gov.nih.nci.caadapter.hl7.map;

import java.util.ArrayList;

public class MessageElementXmlPath extends ArrayList<String> {
	

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
}
