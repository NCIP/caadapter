/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common.csv.meta;

import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;

import java.util.List;

/**
 * Interface for segment metadata (contained within a csv file).
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.6 $
 * @date        $Date: 2008-06-09 19:53:49 $
 */

public interface CSVSegmentMeta extends MetaObject{
    //parents and children.
    public CSVSegmentMeta getParent();
    public void setParent(CSVSegmentMeta newParentSegmentMeta);
    public List<CSVFieldMeta> getFields();
    public List<CSVSegmentMeta> getChildSegments();
    //convenience.
    public void addField(CSVFieldMeta f);
    public void addSegment(CSVSegmentMeta s);

	//mutable
	public void removeAllFields();
	public void setFields(List<CSVFieldMeta> newFieldMetaList);

	public boolean removeField(CSVFieldMeta field);
	public boolean removeSegment(CSVSegmentMeta segment);

    public String getCardinalityWithString();
    public void setCardinalityWithString(String type) throws IllegalArgumentException;
    public CardinalityType getCardinalityType();
    public void setCardinalityType(CardinalityType type);

    public boolean isChoiceSegment();
    public boolean isChoiceMemberSegment();
    public int getMaxCardinality();
    public int getMinCardinality();
}

