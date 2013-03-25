/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.transformation;

import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the Utility class to process a CSV data object
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.8 $
 *          date        $Date: 2009-01-14 21:01:57 $
 */
public class MapProcssorCSVUtil {
    public List<CSVSegment> findCSVSegment(CSVSegment csvSegment, String targetXmlPath)
    {
    	List<CSVSegment> csvSegments = new ArrayList<CSVSegment>();
    	if (targetXmlPath==null)
    		return csvSegments;
    	if (csvSegment.getXmlPath().equals(targetXmlPath)) {
    		csvSegments.add(csvSegment);
    		return csvSegments;
    	}
    	if (csvSegment.getXmlPath().contains(targetXmlPath)) {
    		CSVSegment current = csvSegment.getParentSegment();
    		while (true) {
    			if (current.getXmlPath().equals(targetXmlPath)) {
    	    		csvSegments.add(current);
    	    		return csvSegments;
    			}
    			current = current.getParentSegment();
    			if (current == null) {
    				System.out.println("Error");
    				break;
    			}
    		}
    	}

    	if (targetXmlPath.contains(csvSegment.getXmlPath())) {
			CSVSegment current = csvSegment;
			ArrayList<CSVSegment> parentHolder = new ArrayList<CSVSegment>();
			parentHolder.add(current);
    		while (true) {
    			boolean canStop = true;
    			if (parentHolder == null) break;
    			ArrayList<CSVSegment> childHolder = new ArrayList<CSVSegment>();
    			for(CSVSegment csvS:parentHolder)
    			{
    				current = csvS;
    				if (current.getChildSegments() == null || current.getChildSegments().size()==0) break;

    				for(CSVSegment childSegment:current.getChildSegments()) {
    					if (childSegment.getXmlPath().equals(targetXmlPath)) {
    						csvSegments.add(childSegment);
    					}
    					else {
    						if (targetXmlPath.contains(childSegment.getXmlPath())) {
    							childHolder.add(childSegment);
    							canStop=false;
    						}
    					}
    				}
    			}
    			parentHolder.clear();
    			parentHolder = childHolder;
				if (canStop) break;
    		}
    	}

    	return csvSegments;
    }
/**
 * Find CSVField from a CSVSegment
 * @param csvSegment
 * @param targetFieldXmlPath
 * @return CSVField found or null
 */
    public CSVField findCSVField(CSVSegment csvSegment, String targetFieldXmlPath)
    {
    	String targetSegmentXmlPath = targetFieldXmlPath.substring(0,targetFieldXmlPath.lastIndexOf('.'));
    	CSVSegment current = csvSegment;
    	while (true) {
            if (current == null)
            	return null;
            String currentSegmentXmlPath = current.getXmlPath();

            if (targetSegmentXmlPath.equals(currentSegmentXmlPath))
            {
            	//the CSVSegment is the holder of the target CSVField
    			for(CSVField csvField:current.getFields()) {
    				if (csvField.getXmlPath().equals(targetFieldXmlPath))
    					return csvField;
    			}
    			//the target CSV field is not set as the holder CSVSegment
    			//was created (V2 message parser)

    			//return here since the targetFieldXmlPath is definitely the path
    			//of a CSVField, so it does not need to search the child CSVSegment
    			//of the current CSVSegment
    			return null;
    		}
            else if (targetSegmentXmlPath.contains(csvSegment.getXmlPath()))
            {
            	//curent Segment is parent of the original Segment
            	for (CSVSegment childSeg:current.getChildSegments())
            	{
            		CSVField chldField=findCSVField(childSeg,targetFieldXmlPath );
            		if (chldField!=null)
            			return chldField;
            	}
            }
    		current = current.getParentSegment();
    		if (current == null)
    		{
    			System.out.println("Error");
    			System.out.println("csvSegment="+csvSegment.getXmlPath());
    			System.out.println("target="+targetFieldXmlPath);
    	    	return null;
    		}
    	}
    }

    public CSVField findCSVField(List<CSVSegment> csvSegmentList, String targetXmlPath) {

    	String targetSegmentXmlPath = targetXmlPath.substring(0,targetXmlPath.lastIndexOf('.'));

    	for (CSVSegment csvSegment: csvSegmentList) {
    		if (csvSegment==null)
    			continue;
        	CSVSegment currentParent = csvSegment.getParentSegment();
    		while (true) {
    			if (currentParent.getXmlPath().equals(targetSegmentXmlPath)) {
    				for(CSVField csvField:currentParent.getFields()) {
    					if (csvField.getXmlPath().equals(targetXmlPath))
    						return csvField;
    				}
    			}
    			currentParent = currentParent.getParentSegment();
    			if (currentParent == null) {
    				break;
    			}
    		}
    	}

    	return null;
    	// Error should be thrown
    }
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.7  2009/01/14 19:31:57  wangeug
 * HISTORY :terminate loop if the target CSV field is not set as the holder CSVSegment which was created (V2 message parser). return null  since the targetFieldXmlPath is definitely the path of a CSVField, so it does not need to search the child CSVSegment of the current CSVSegment
 * HISTORY :
 * HISTORY :Revision 1.6  2008/12/08 19:06:06  wangeug
 * HISTORY :Search a CSVField from root element with top-down algrithm: V2 message XML
 * HISTORY :
 * HISTORY :Revision 1.5  2008/12/04 20:41:37  wangeug
 * HISTORY :support nullFlavor
 * HISTORY :
 * HISTORY :Revision 1.4  2008/09/29 15:40:38  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */