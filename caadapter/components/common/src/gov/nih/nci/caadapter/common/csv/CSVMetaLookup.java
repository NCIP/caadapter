/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.csv;

import gov.nih.nci.caadapter.common.MetaException;
import gov.nih.nci.caadapter.common.MetaLookup;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * Provides quick access when doing csv metadata lookups.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.3 $
 * @date        $Date: 2008-06-09 19:53:49 $
 */

public class CSVMetaLookup implements MetaLookup{
	private static final String LOGID = "$RCSfile: CSVMetaLookup.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/CSVMetaLookup.java,v 1.3 2008-06-09 19:53:49 phadkes Exp $";

	private CSVMeta meta;
	private Hashtable<String, MetaObject> table = new Hashtable<String, MetaObject>();

	public CSVMetaLookup(CSVMeta meta) throws MetaException{
		this.meta = meta;
		initTable();
	}

	private void initTable(){
		initSegment(meta.getRootSegment());
	}

	private void initSegment(CSVSegmentMeta segmentMeta){
		table.put(segmentMeta.getXmlPath(),segmentMeta);
		// put the segments in the hash.
		List<CSVSegmentMeta>childSegments = segmentMeta.getChildSegments();
		for (int i = 0; i < childSegments.size(); i++) {
			initSegment(childSegments.get(i));
		}
		// put the fields in the hash.
		List<CSVFieldMeta> fields = segmentMeta.getFields();
		for (int i = 0; i < fields.size(); i++) {
			CSVFieldMeta csvFieldMeta =  fields.get(i);
			table.put(csvFieldMeta.getXmlPath(),csvFieldMeta);
		}
	}

	public MetaObject lookup(String uuid){
		return table.get(uuid);
	}

	/**
	 * Return all key elements.
	 * @return all key elements.
	 */
	public Set getAllKeys()
	{
		Set keySet = new HashSet(table.keySet());
		return keySet;
	}
}
