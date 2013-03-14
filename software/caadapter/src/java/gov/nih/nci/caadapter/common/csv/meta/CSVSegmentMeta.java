/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
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
 * @version    $Revision: 1.7 $
 * @date        $Date: 2008-09-24 20:42:38 $
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


/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
