/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.sdtm;

import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Jun 22, 2007
 * Time: 2:52:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class RDSMapResult {
    String domainName=null;
    String targetFieldName=null;
    String sourceFieldName=null;
    String sourceDataValue=null;
    CSVSegment currentCSVSegment=null;
    Hashtable globalDomainList=null;
    Hashtable hashTableTransform=null;
    ArrayList rowData=null;
    HashMap fixedLength=null;
    HashMap prefs=null;

    public RDSMapResult(String _domainName, Hashtable _globaldomainList, String _targetFieldName, String _sourceFieldName, String _sourceDataValue, CSVSegment _currentCSVSegment, Hashtable _hashTableTransform, ArrayList _rowData, HashMap fixedLengthRecs) {
        this.targetFieldName = _targetFieldName;
        this.sourceFieldName = _sourceFieldName;
        this.sourceDataValue = _sourceDataValue;
        this.currentCSVSegment = _currentCSVSegment;
        this.hashTableTransform = _hashTableTransform;
        this.globalDomainList = _globaldomainList;
        this.domainName = _domainName;
        this.rowData = _rowData;
        this.fixedLength = fixedLengthRecs;
        createResultDataArray();
    }

    public String getDomainName() {
        return domainName;
    }

    public ArrayList getArrayList() {
        return rowData;
    }

    public void createResultDataArray() {
        if (targetFieldName.substring(0, 2).equalsIgnoreCase(domainName)) {
            int position = new Integer(((ArrayList) globalDomainList.get(domainName)).indexOf(targetFieldName)) + 1;
            rowData.remove(position - 1);
            //this is where the data is inserted; now check if this has some fixed length requirements.
            if (fixedLength != null) {
                implementFixedRec(position, sourceDataValue);
            } else {
                rowData.add(position - 1, sourceDataValue);
            }
            //now check for relational records upwards this particular field may have
            checkSegment(currentCSVSegment);
        } else {
            System.out.println("unusual combination " + targetFieldName.substring(0, 2) + " and " + domainName);
        }
    }

    private void checkSegment(CSVSegment csvSegment) {
        if (csvSegment.getParentSegment() != null) {
            csvSegment = csvSegment.getParentSegment();
            areFieldsMapped(csvSegment);
            checkSegment(csvSegment);
        }
    }

    private void implementFixedRec(int position, String srcData) {
        if (fixedLength.containsKey(targetFieldName)) {
            StringBuffer _setSize;
            int fixedsize = new Integer(fixedLength.get(targetFieldName).toString()).intValue();
            int finalSize = fixedsize - srcData.length();
            _setSize = new StringBuffer();
            _setSize.append(srcData);
            for (int i = 0; i < finalSize; i++) {
                _setSize.append(" ");
            }
            rowData.add(position - 1, _setSize.toString());
        } else {
            rowData.add(position - 1, srcData);
        }
    }

    private void areFieldsMapped(CSVSegment csvSegment) {
        //This Xpath will be used to prefix all the fields
        String xpathPrefix = RDSHelper.getParentasXPath(csvSegment);
        if (csvSegment.getFields().size() > 0) {
            List fieldsForEachSegment = csvSegment.getFields();
            for (int fields = 0; fields < fieldsForEachSegment.size(); fields++) {
                CSVField fieldObject = (CSVField) fieldsForEachSegment.get(fields);
                String mappedValue = isFieldMapped(xpathPrefix + "\\" + fieldObject.getMetaObject().toString());
                if (mappedValue != null && (mappedValue.substring(0, 2).equalsIgnoreCase(targetFieldName.substring(0, 2)))) {
                    int position = new Integer(((ArrayList) globalDomainList.get(targetFieldName.substring(0, 2))).indexOf(mappedValue)) + 1;
                    rowData.remove(position - 1);
                    if (fixedLength != null) {
                        implementFixedRec(position, fieldObject.getValue());
                    } else {
                        rowData.add(position - 1, fieldObject.getValue());
                    }
                }
            }
        }
    }

    private String isFieldMapped(String field) {
        Enumeration enum1 = hashTableTransform.keys();
        while (enum1.hasMoreElements()) {
            String domainName = (String) enum1.nextElement();
            ArrayList array = (ArrayList) hashTableTransform.get(domainName);
            for (int i = 0; i < array.size(); i++) {
                String _tempStr = array.get(i).toString();
                String _temStr2 = _tempStr.substring(0, _tempStr.indexOf("~"));
                //System.out.println("comparing " + field + "  and  " + _temStr2);
                if (_temStr2.equals(field)) {
                    //System.out.println("\t\t\tReturning " + _tempStr.substring(_tempStr.indexOf("~") + 1, _tempStr.length()));
                    return _tempStr.substring(_tempStr.indexOf("~") + 1, _tempStr.length());
                }
            }
        }
        return null;
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2007/08/16 19:39:45  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 *
 */
