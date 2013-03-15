/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.sdtm;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.csv.CsvCache;
import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.data.impl.CSVFieldImpl;
import gov.nih.nci.caadapter.common.csv.data.impl.CSVSegmentImpl;
import gov.nih.nci.caadapter.common.csv.data.impl.CSVSegmentedFileImpl;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This class preprocess the CSV data for transformation
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.6 $
 *          $Date: 2008-06-09 19:54:06 $
 */
public class SDTMMany2ManyMapping {
    // MultiValueMap returnCsvMapData = new MultiValueMap();
    // LinkedHashMap returnCsvMapData.get( key); = new LinkedHashMap();
    public List<String> returnCsvMapData1=null;
    private HashSet checkRepeats=null;

    public CSVDataResult parse(File dataFile, CSVMeta csvMeta) {
        ValidatorResults validatorResults = new ValidatorResults();
        CSVSegmentedFileImpl segmentedFile = new CSVSegmentedFileImpl();
        CSVDataResult csvDataResult = new CSVDataResult(segmentedFile, validatorResults);
        try {
            String[][] data = CsvCache.getCsv(dataFile.getPath());
            return parse(data, csvMeta);
        } catch (IOException e) {
            Log.logException(this, e);
            Message msg = MessageResources.getMessage("GEN0", new Object[]{e.getMessage()});
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            csvDataResult.setValidatorResults(validatorResults);
            return csvDataResult;
        }
    }

    public CSVDataResult parse(String[][] data, CSVMeta csvMeta) {
        checkRepeats = new HashSet();
        returnCsvMapData1 = new LinkedList<String>();
        ValidatorResults validatorResults = new ValidatorResults();
        CSVSegmentedFileImpl segmentedFile = new CSVSegmentedFileImpl();
        CSVDataResult csvDataResult = new CSVDataResult(segmentedFile, validatorResults);
        CSVSegmentMeta rootSegmentMeta = csvMeta.getRootSegment();
        // load the data. --> has been moved out
        // String[][] data = CsvCache.getCsv(dataFile.getPath());
        // traverse + populate objects.
        ArrayList<CSVSegment> logicalRecords = new ArrayList<CSVSegment>();
        CSVSegmentMeta currentSegmentMeta = rootSegmentMeta;
        CSVSegment currentSegment = null;
        CSVSegment rootSegment = null;
        Stack<CSVSegment> segmentStack = new Stack<CSVSegment>();
        // test code begin -harsha
        ArrayList _myAry = new ArrayList();
        // harsha end
        for (int i = 0; i < data.length; i++) {
            String[] row = data[i];
            String segmentName = row[0];
            // find the metadata for this row.
            currentSegmentMeta = findCSVSegmentMeta(rootSegmentMeta, segmentName);
            if (currentSegmentMeta == null) {
                Message msg = MessageResources.getMessage("CSV2", new Object[]{segmentName});
                validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
                return csvDataResult;
            }
            // create the segment for this row.
            currentSegment = createSegment(currentSegmentMeta, row, validatorResults);
            // now we need to assign it to the hierarchy.
            // if it's a root segment it needs to be a new logical record.
            if (segmentName.equalsIgnoreCase(rootSegmentMeta.getName())) {
                // if there is something on the stack - add it to the logical records.
                // empty the stack (prepare a new logical record)
                if (segmentStack.size() != 0) {
                    logicalRecords.add(rootSegment);
                    segmentStack = new Stack<CSVSegment>();
                }
                rootSegment = currentSegment;
                segmentStack.push(rootSegment);
                // harsha
                StringBuffer _val = new StringBuffer();
                for (int k = 0; k < data[i].length; k++) {
                    _val.append("," + data[i][k]);
                }
                EmptyStringTokenizer emp = new EmptyStringTokenizer(_val.toString().substring(1), ",");
                emp.deleteTokenAt(0);
                // remove the leading '\'
                int d = emp.toString().lastIndexOf('\\');
                String finalStr = emp.toSameString().substring(0, d);
                returnCsvMapData1.add("\\Source Tree\\" + rootSegment + "^" + finalStr);
                // _myAry.add( "\\Source Tree\\" + rootSegment + "=" + finalStr);
                // harsha
            } else {
                // we have a child segment.
                // we need to find the parent + assign it appropriately.
                CSVSegmentMeta parentSegmentMeta = currentSegmentMeta.getParent();
                String parentSegmentMetaName = parentSegmentMeta.getName();
                // the stack should contain the parent
                boolean found = false;
                while (!found) {
                    // check the top of the stack.
                    CSVSegment segment = null;
                    try {
                        if (!segmentStack.isEmpty()) {
                            segment = segmentStack.peek();
                        } else {// if nothing is found before the stack is empty, return
                            Message msg = MessageResources.getMessage("CSV4", new Object[]{segmentName, parentSegmentMetaName, i});
                            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
                            return csvDataResult;
                        }
                    } catch (EmptyStackException e) {
                        Log.logException(this, e);
                        Message msg = MessageResources.getMessage("CSV4", new Object[]{segmentName, parentSegmentMetaName, i});
                        validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
                        return csvDataResult;
                    }
                    // if found...
                    String segmentNameLocal = segment == null ? null : segment.getName();
                    if (parentSegmentMetaName.equalsIgnoreCase(segmentNameLocal)) {
                        found = true;
                        // assign it to the sement that's on the top + add it.
                        segment.addChildSegment(currentSegment);
                        currentSegment.setParentSegment(segment);
                        segmentStack.push(currentSegment);
                    } else {
                        // otherwise, check the next one..
                        if (!segmentStack.isEmpty()) {
                            segmentStack.pop();
                        }
                    }
                    // harsha
                    StringBuffer _put = new StringBuffer();
                    for (int j = 0; j < segmentStack.size(); j++) {
                        _put.append("\\" + segmentStack.get(j));
                    }
                    StringBuffer _val = new StringBuffer();
                    for (int k = 0; k < data[i].length; k++) {
                        _val.append("," + data[i][k]);
                    }
                    String first = _put.toString();
                    String second = _val.toString().substring(1);
                    EmptyStringTokenizer str1 = new EmptyStringTokenizer(first, "\\");
                    String _first = str1.getTokenAt(str1.countTokens() - 1).toString();
                    EmptyStringTokenizer str2 = new EmptyStringTokenizer(second, ",");
                    String _second = str2.getTokenAt(0).toString();
                    if (_first.equalsIgnoreCase(_second)) {
                        int a = _val.toString().substring(1).indexOf(',');
                        String forCompare = returnCsvMapData1.get(returnCsvMapData1.size() - 1);
                        String clean = forCompare.substring(0, forCompare.indexOf('^'));
                        if (checkRepeats.contains("\\Source Tree" + _put.toString())) {
                            returnCsvMapData1.add("\\Source Tree" + _put.toString() + "^" + _val.toString().substring(a + 2));
                        } else {
                            returnCsvMapData1.add("\\Source Tree" + _put.toString() + "^" + _val.toString().substring(a + 2));
                            checkRepeats.add("\\Source Tree" + _put.toString());
                        }
                        //                        if (clean.trim().equalsIgnoreCase("\\Source Tree" + _put.toString()) | clean.trim().equalsIgnoreCase("R\\Source Tree" + _put.toString()))
                        //							returnCsvMapData1.add("R\\Source Tree" + _put.toString() + "^" + _val.toString().substring(a + 2));
                        //						else
                        //							returnCsvMapData1.add("\\Source Tree" + _put.toString() + "^" + _val.toString().substring(a + 2));
                    }
                    // _myAry.add(_val.toString().substring( 0));
                    // harsha
                }// end of while (!found)
            }
        }
        // there are no more rows.. add the final logical record.
        logicalRecords.add(rootSegment);
        // System.err.println( "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        // System.err.println( "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        // Set e = returnCsvMapData.keySet();
        // System.out.println ( returnCsvMapData);
        // Iterate over the keys in the map
        // java.util.Iterator it = returnCsvMapData.keySet().iterator();
        // while (it.hasNext()) {
        // // Get key
        // Object key = it.next();
        // System.out.println( key + "=" +returnCsvMapData.get( key));
        // List list = (List) returnCsvMapData.get( key);
        // Iterator iterator = list.listIterator();
        // while (iterator.hasNext()) {
        // System.out.println( " " + iterator.next());
        // //iterator.next();
        // }
        // }
        // For a set or list
        // for (Iterator it = returnCsvMapData1.iterator(); it.hasNext();) {
        // Object element = it.next();
        // System.out.println( element);
        // }
        segmentedFile.setLogicalRecords(logicalRecords);
        // System.out.println("done!");
        return csvDataResult;
    }

    public CSVSegmentMeta findCSVSegmentMeta(CSVSegmentMeta rootsegment, String segmentname) {
        if (segmentname.equalsIgnoreCase(rootsegment.getName())) {
            return rootsegment;
        } else {
            List<CSVSegmentMeta> csvSegmentMetas = rootsegment.getChildSegments();
            for (int i = 0; i < csvSegmentMetas.size(); i++) {
                CSVSegmentMeta csvSegmentMeta = csvSegmentMetas.get(i);
                CSVSegmentMeta foundsegment = findCSVSegmentMeta(csvSegmentMeta, segmentname);
                if (foundsegment != null) {
                    return foundsegment;
                }
            }
        }
        return null;
    }

    private CSVSegment createSegment(CSVSegmentMeta meta, String[] data, ValidatorResults validatorResults) {
        CSVSegmentImpl segment = new CSVSegmentImpl(meta);
        //segment.setUUID(meta.getUUID());
        // check for validation rule #3
        int metaFields = meta.getFields().size();
        int dataFields = data.length - 1;
        if (dataFields > metaFields) {
            Message msg = MessageResources.getMessage("CSV3", new Object[]{meta.getName(), dataFields, metaFields});
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
        }
        // setup the fields.
        ArrayList<CSVField> fields = new ArrayList<CSVField>();
        List<CSVFieldMeta> fieldMeta = meta.getFields();
        for (int i = 0; i < fieldMeta.size(); i++) {
            CSVFieldMeta csvFieldMeta = fieldMeta.get(i);
            CSVFieldImpl field = new CSVFieldImpl(csvFieldMeta);
            field.setColumn(csvFieldMeta.getColumn());
            // field.setUUID(csvFieldMeta.getUUID());
            try {
                field.setValue(data[csvFieldMeta.getColumn()]);
            } catch (Exception e) {
                field.setValue("");
            }
            fields.add(field);
        }
        segment.setFields(fields);
        return segment;
    }

    public List getHashMapData(String csv, String scs) {
        //CSVPanel csvPanel = new CSVPanel();
        //File csvFile = new File(csv);
        //ValidatorResults validatorResults = csvPanel.setSaveFile(new File(scs), true);
        //ValidatorResults validatorResults2 = new ValidatorResults();
        //CSVMeta rootMeta = csvPanel.getCSVMeta(false);
        returnCsvMapData1.add(returnCsvMapData1.get(0));
        return returnCsvMapData1;
    }

    public static void clearReturnDataList() {
        //returnCsvMapData1.clear();
    }

    public static void main(String[] args) {
        //System.out.println(SDTMMany2ManyMapping.getHashMapData("d:\\aa.csv", "d:\\aa.scs"));
        // s.processList();
    }

    public CSVDataResult parseorig(String[][] data, CSVMeta csvMeta) {
        ValidatorResults validatorResults = new ValidatorResults();
        CSVSegmentedFileImpl segmentedFile = new CSVSegmentedFileImpl();
        CSVDataResult csvDataResult = new CSVDataResult(segmentedFile, validatorResults);
        CSVSegmentMeta rootSegmentMeta = csvMeta.getRootSegment();
        // load the data. --> has been moved out
        //            String[][] data = CsvCache.getCsv(dataFile.getPath());
        // traverse + populate objects.
        ArrayList<CSVSegment> logicalRecords = new ArrayList<CSVSegment>();
        CSVSegmentMeta currentSegmentMeta = rootSegmentMeta;
        CSVSegment currentSegment = null;
        CSVSegment rootSegment = null;
        Stack<CSVSegment> segmentStack = new Stack<CSVSegment>();
        for (int i = 0; i < data.length; i++) {
            String[] row = data[i];
            String segmentName = row[0];
            //find the metadata for this row.
            currentSegmentMeta = findCSVSegmentMeta(rootSegmentMeta, segmentName);
            if (currentSegmentMeta == null) {
                Message msg = MessageResources.getMessage("CSV2", new Object[]{segmentName});
                validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
                return csvDataResult;
            }
            // create the segment for this row.
            currentSegment = createSegment(currentSegmentMeta, row, validatorResults);
            //  now we need to assign it to the hierarchy.
            // if it's a root segment it needs to be a new logical record.
            if (segmentName.equalsIgnoreCase(rootSegmentMeta.getName())) {
                // if there is something on the stack - add it to the logical records.
                // empty the stack (prepare a new logical record)
                if (segmentStack.size() != 0) {
                    logicalRecords.add(rootSegment);
                    segmentStack = new Stack<CSVSegment>();
                }
                rootSegment = currentSegment;
                segmentStack.push(rootSegment);
            } else {
                // we have a child segment.
                // we need to find the parent + assign it appropriately.
                CSVSegmentMeta parentSegmentMeta = currentSegmentMeta.getParent();
                String parentSegmentMetaName = parentSegmentMeta.getName();
                // the stack should contain the parent
                boolean found = false;
                while (!found) {
                    // check the top of the stack.
                    CSVSegment segment = null;
                    try {
                        if (!segmentStack.isEmpty()) {
                            segment = segmentStack.peek();
                        } else {//if nothing is found before the stack is empty, return
                            Message msg = MessageResources.getMessage("CSV4", new Object[]{segmentName, parentSegmentMetaName, i});
                            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
                            return csvDataResult;
                        }
                    } catch (EmptyStackException e) {
                        Log.logException(this, e);
                        Message msg = MessageResources.getMessage("CSV4", new Object[]{segmentName, parentSegmentMetaName, i});
                        validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
                        return csvDataResult;
                    }
                    // if found...
                    String segmentNameLocal = segment == null ? null : segment.getName();
                    if (parentSegmentMetaName.equalsIgnoreCase(segmentNameLocal)) {
                        found = true;
                        // assign it to the sement that's on the top + add it.
                        segment.addChildSegment(currentSegment);
                        currentSegment.setParentSegment(segment);
                        segmentStack.push(currentSegment);
                    } else {
                        // otherwise, check the next one..
                        if (!segmentStack.isEmpty()) {
                            segmentStack.pop();
                        }
                    }
                }//end of while (!found)
            }
        }
        // there are no more rows.. add the final logical record.
        logicalRecords.add(rootSegment);
        segmentedFile.setLogicalRecords(logicalRecords);
        //System.out.println("done!");
        return csvDataResult;
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2007/08/16 19:39:45  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */