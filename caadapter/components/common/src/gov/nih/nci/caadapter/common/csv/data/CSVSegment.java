/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common.csv.data;

import gov.nih.nci.caadapter.common.DataObject;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;

import java.util.List;

/**
 * Interface for a segment that is contained within segmented csv data file.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.4 $
 * @date        $Date: 2008-06-09 19:53:49 $
 */

public interface CSVSegment extends DataObject{
    public List<CSVField> getFields();
    public List<CSVSegment> getChildSegments();
    public void addChildSegment(CSVSegment segment);
    public CSVSegment getParentSegment();
    public void setParentSegment(CSVSegment segment);
    public CardinalityType getCardinalityType();
    public void setCardinalityType(CardinalityType type);
    public String getCardinalityWithString();
    public boolean isChoiceSegment();
    public void setCardinalityWithString(String type) throws IllegalArgumentException;
    public int getMaxCardinality();
    public int getMinCardinality();
}
/*
public interface CSVSegment extends DataObject{
    public List<CSVField> getFields();
    public List<CSVSegment> getChildSegments();
    public void addChildSegment(CSVSegment segment);
    public CSVSegment getParentSegment();
    public void setParentSegment(CSVSegment segment);    
	//segment cardinality
	public String getCardinality();
	public void setCardinality(String newValue);

}
*/
