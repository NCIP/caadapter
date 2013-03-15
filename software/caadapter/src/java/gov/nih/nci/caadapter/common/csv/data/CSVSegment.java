/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





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
 * @version    $Revision: 1.5 $
 * @date        $Date: 2008-09-24 20:36:54 $
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

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
