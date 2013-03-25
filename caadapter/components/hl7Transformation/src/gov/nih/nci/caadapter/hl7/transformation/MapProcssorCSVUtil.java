/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.transformation;

import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;

import java.util.ArrayList;
import java.util.List;

public class MapProcssorCSVUtil {
    public List<CSVSegment> findCSVSegment(CSVSegment csvSegment, String targetXmlPath) {
//    	System.out.println("CSVSegment "+csvSegment.getXmlPath() + "-->target"+targetXmlPath);
    	List<CSVSegment> csvSegments = new ArrayList<CSVSegment>();
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
    				/*
    				 * TODO throw error
    				 */
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
//  					System.out.println("ChildSegment" + childSegment.getXmlPath());
    					if (childSegment.getXmlPath().equals(targetXmlPath)) {
    						csvSegments.add(childSegment);
    					}
    					else {
    						if (targetXmlPath.contains(childSegment.getXmlPath())) {
    							childHolder.add(childSegment);
    							canStop=false;
//    							break;
    						}
    					}
    				}
    			}
    			parentHolder.clear();
    			parentHolder = childHolder;
				if (canStop) break;
    		}
    	}
//    	System.out.println(csvSegments.size());
    	return csvSegments;
    }
/*
    private CSVField findCSVField(CSVSegment csvSegment, String targetXmlPath) {
    	String targetSegmentXmlPath = targetXmlPath.substring(0,targetXmlPath.lastIndexOf('.'));
//    	CSVSegment current = csvSegment.getParentSegment();
    	CSVSegment current = csvSegment;
    	while (true) {
    		if (current.getXmlPath().equals(targetSegmentXmlPath)) {
    			for(CSVField csvField:current.getFields()) {
    				if (csvField.getXmlPath().equals(targetXmlPath)) return csvField;
    			}
    		}
    		current = current.getParentSegment();
    		if (current == null) {
    			System.out.println("Error");
    			System.out.println("csvSegment="+csvSegment.getXmlPath());
    			System.out.println("target="+targetXmlPath);
    	    	return null;  /*
    			/*
    			 * TODO throw error
    			 */
/*    		}
    	}
    }
*/
        public CSVField findCSVField(CSVSegment csvSegment, String targetXmlPath) {
    	String targetSegmentXmlPath = targetXmlPath.substring(0,targetXmlPath.lastIndexOf('.'));
//    	CSVSegment current = csvSegment.getParentSegment();
    	CSVSegment current = csvSegment;
    	while (true) {
            //System.out.println("CCC1 : " +  current + " : " + csvSegment.getName() + " : " + targetXmlPath);
            if (current == null) return null;
            String str = current.getXmlPath();
            //System.out.println("CCC : " +  current.getName() + str);
            if (str.equals(targetSegmentXmlPath)) {
    			for(CSVField csvField:current.getFields()) {
    				if (csvField.getXmlPath().equals(targetXmlPath)) return csvField;
    			}
    		}
    		current = current.getParentSegment();
    		if (current == null) {
    			System.out.println("Error");
    			System.out.println("csvSegment="+csvSegment.getXmlPath());
    			System.out.println("target="+targetXmlPath);
    	    	return null;
    			/*
    			 * TODO throw error
    			 */
    		}
    	}

    }

    public CSVField findCSVField(List<CSVSegment> csvSegments, String targetXmlPath) {

    	String targetSegmentXmlPath = targetXmlPath.substring(0,targetXmlPath.lastIndexOf('.'));

    	for (CSVSegment csvSegment: csvSegments) {
        	CSVSegment current = csvSegment.getParentSegment();
    		while (true) {
    			if (current.getXmlPath().equals(targetSegmentXmlPath)) {
    				for(CSVField csvField:current.getFields()) {
    					if (csvField.getXmlPath().equals(targetXmlPath)) return csvField;
    				}
    			}
    			current = current.getParentSegment();
    			if (current == null) {
    				break;
    			}
    		}
    	}

    	return null;
    	// Error should be thrown
    }

}
