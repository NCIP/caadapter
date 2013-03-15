/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.common.csv.data.impl;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.data.CSVField;
import java.util.*;
/**
 * CSVSegmentExtension
 * @author OWNER: $Author: phadkes $
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.4 $
 * @date       $Date: 2008-09-24 20:00:11 $
*/
public class CSVSegmentExtension extends CSVSegmentImpl {

	public CSVSegmentExtension (CSVSegmentMeta meta)
	{
		super(meta);
	}
	/**
	 * Find all child segment with the given name
	 * @param childName
	 * @return
	 */
	public List<CSVSegment> getChildSegmentsByName(String childName)
	{
		ArrayList<CSVSegment> rtnList=new ArrayList<CSVSegment>();
		for(CSVSegment childSeg:getChildSegments())
			if (childSeg.getName().equals(childName))
				rtnList.add(childSeg);
		return rtnList;
	}
	/**
	 * Find the child field with the given name
	 * @param childName
	 * @return
	 */
	public CSVField getFieldByName(String childName)
	{
		for(CSVField childField:this.getFields())
			if (childField.getName().equals(childName))//.equalsIgnoreCase(childName))//.equals(childName))
				return childField;

		return null;
	}

	/**
	 * Creates a new child segement and inserts it after the last
	 * segement with the same name
	 * @param newChildSeg
	 */
	public void attachDuplicateChildSegment(CSVSegment newChildSeg)
	{
		newChildSeg.setParentSegment(this);
		int childSegSite=0;
		ArrayList<CSVSegment> childSegList=this.getChildSegments();

		for (int i=0;i<childSegList.size();i++)
		{
			CSVSegment childSeg= (CSVSegment)childSegList.get(i);
			if (newChildSeg.getName().equals(childSeg.getName()))
				childSegSite=i;
		}
		this.getChildSegments().add(childSegSite+1, newChildSeg);
	}

	/**
	 * Express the segment as a string
	 */
	public String toString()
	{
		StringBuffer sb=new StringBuffer();
		sb.append(this.getName()+"__"+this.getXmlPath()+":");
		for (CSVField field:this.getFields())
		{
			sb.append(","+field.getName()+":"+field.getValue());
		}
		return sb.toString();
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
