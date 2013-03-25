/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





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
