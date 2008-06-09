/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.ui.specification.csv;

import java.util.ArrayList;
import java.util.List;

public class CSVPanelConstants {
	private static ArrayList<String> SEGMENT_CARDINALITY_SELECTION=new ArrayList<String>();
//	initialize the cardinality selection list
	static {
		SEGMENT_CARDINALITY_SELECTION.add("0..1");
		SEGMENT_CARDINALITY_SELECTION.add("0..*");
		SEGMENT_CARDINALITY_SELECTION.add("1..1");
		SEGMENT_CARDINALITY_SELECTION.add("1..*");
	}
	
	public static List<String> getSegmentCardinalitySelection()
	{
		return SEGMENT_CARDINALITY_SELECTION;
	}
}
