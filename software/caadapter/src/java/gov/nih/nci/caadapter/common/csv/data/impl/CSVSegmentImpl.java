/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.common.csv.data.impl;

import gov.nih.nci.caadapter.common.DataObjectImpl;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;
import gov.nih.nci.caadapter.castor.csv.meta.impl.C_segment;

import java.util.ArrayList;

import gov.nih.nci.caadapter.common.Cardinality;

/**
 * Implementation of a segment that is contained within segmented csv data file.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.7 $
 * @date        $Date: 2008-09-24 20:00:10 $
 */

public class CSVSegmentImpl extends DataObjectImpl implements CSVSegment{
    private static final String LOGID = "$RCSfile: CSVSegmentImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/data/impl/CSVSegmentImpl.java,v 1.7 2008-09-24 20:00:10 phadkes Exp $";

    public ArrayList<CSVField> fields = new ArrayList<CSVField>();
    public ArrayList<CSVSegment> childSegments = new ArrayList<CSVSegment>();
    public CSVSegment parentSegment = null;
    public CardinalityType cardinality = (new C_segment()).getCardinality();
    // constructors
    public CSVSegmentImpl(CSVSegmentMeta metaObject) {
        super(metaObject);
    }

    // getters and setters
    public ArrayList<CSVField> getFields() {
        return fields;
    }

    public void setFields(ArrayList<CSVField> fields) {
        this.fields = fields;
    }

    public ArrayList<CSVSegment> getChildSegments() {
        return childSegments;
    }

    public void setChildSegments(ArrayList<CSVSegment> childSegments) {
        this.childSegments = childSegments;
    }

    public CSVSegment getParentSegment() {
        return parentSegment;
    }

    public void setParentSegment(CSVSegment parentSegment) {
        this.parentSegment = parentSegment;
    }

    // convenience method.
    public void addChildSegment(CSVSegment segment){
        this.childSegments.add(segment);
    }

    public String toString()
    {
        return getName();
    }

    public CardinalityType getCardinalityType()
    {
        return cardinality;
    }
    public void setCardinalityType(CardinalityType type)
    {
        cardinality = type;
    }
    public String getCardinalityWithString()
    {
        return cardinality.toString().substring(0, (new C_segment()).getCardinality().toString().length());
    }
    public boolean isChoiceSegment()
    {
        return cardinality.toString().endsWith(Config.SUFFIX_OF_CHOICE_CARDINALITY);
    }
    public void setCardinalityWithString(String type) throws IllegalArgumentException
    {
        cardinality = CardinalityType.valueOf(type);
    }
    public int getMaxCardinality()
    {
        Cardinality _cardinality = null;
        try
        {
            _cardinality = new Cardinality(getCardinalityWithString());
        }
        catch(IllegalArgumentException ie) {}
        return _cardinality.getMaximum();
    }
    public int getMinCardinality()
    {
        Cardinality _cardinality = null;
        try
        {
            _cardinality = new Cardinality(getCardinalityWithString());
        }
        catch(IllegalArgumentException ie) {}
        return _cardinality.getMinimum();
    }
}

/*
    public ArrayList<CSVField> fields = new ArrayList<CSVField>();
    public ArrayList<CSVSegment> childSegments = new ArrayList<CSVSegment>();
    public CSVSegment parentSegment = null;
    private String cardinality;
    // constructors
    public CSVSegmentImpl(CSVSegmentMeta metaObject) {
        super(metaObject);
    }

    // getters and setters
    public ArrayList<CSVField> getFields() {
        return fields;
    }

    public void setFields(ArrayList<CSVField> fields) {
        this.fields = fields;
    }

    public ArrayList<CSVSegment> getChildSegments() {
        return childSegments;
    }

    public void setChildSegments(ArrayList<CSVSegment> childSegments) {
        this.childSegments = childSegments;
    }

    public CSVSegment getParentSegment() {
        return parentSegment;
    }

    public void setParentSegment(CSVSegment parentSegment) {
        this.parentSegment = parentSegment;
    }

    // convenience method.
    public void addChildSegment(CSVSegment segment){
        this.childSegments.add(segment);
    }

    public String toString()
    {
        return getName();
    }

	public String getCardinality() {
		return cardinality;
	}

	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}

}
*/

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
