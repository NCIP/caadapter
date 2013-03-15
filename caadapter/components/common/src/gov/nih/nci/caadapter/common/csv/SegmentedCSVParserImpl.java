/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common.csv;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.data.impl.CSVFieldImpl;
import gov.nih.nci.caadapter.common.csv.data.impl.CSVSegmentImpl;
import gov.nih.nci.caadapter.common.csv.data.impl.CSVSegmentedFileImpl;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.common.validation.CSVMetaValidator;
//import gov.nih.nci.caadapter.hl7.validation.CSVMetaValidator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

/**
 * Parses csv datafiles that are based on a certain csv meta specification.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: linc $
 * @version $Revision: 1.4.2.1 $
 * @date $Date: 2008-05-23 15:48:34 $
 * @since caAdapter v1.2
 */

public class SegmentedCSVParserImpl {
    private static final String LOGID = "$RCSfile: SegmentedCSVParserImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/SegmentedCSVParserImpl.java,v 1.4.2.1 2008-05-23 15:48:34 linc Exp $";
    private static final SegmentedCSVParserImpl singleton = new SegmentedCSVParserImpl();

    public static CSVDataResult parse(File dataFile, File metaFile) throws ApplicationException
    {
        // parse the metafile
        CSVMeta meta = null;
        try {
            CSVMetaParserImpl parser = new CSVMetaParserImpl();
            CSVMetaResult csvMetaResult = parser.parse(new FileReader(metaFile));
            meta = csvMetaResult.getCsvMeta();
        } catch (FileNotFoundException e) {
            Log.logException(singleton, e);
            Message msg = MessageResources.getMessage("GEN0", new Object[]{e.getMessage()});
            ValidatorResults validatorResults =new ValidatorResults();
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            return new CSVDataResult(new CSVSegmentedFileImpl(),validatorResults);
        }
        return parse(dataFile, meta);
    }

    public static CSVDataResult parse(InputStream dataStream, File metaFile) throws ApplicationException{
        // parse the metafile
        CSVMeta meta = null;
        try {
            CSVMetaParserImpl parser = new CSVMetaParserImpl();
            CSVMetaResult csvMetaResult = parser.parse(new FileReader(metaFile));
            meta = csvMetaResult.getCsvMeta();
        } catch (FileNotFoundException e) {
            Log.logException(singleton, e);
            Message msg = MessageResources.getMessage("GEN0", new Object[]{e.getMessage()});
            ValidatorResults validatorResults =new ValidatorResults();
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            return new CSVDataResult(new CSVSegmentedFileImpl(),validatorResults);
        }
        return parse(dataStream, meta);
    }
    public static CSVDataResult parse(String dataString, File metaFile) throws ApplicationException{
        // parse the metafile
        CSVMeta meta = null;
        try {
            CSVMetaParserImpl parser = new CSVMetaParserImpl();
            CSVMetaResult csvMetaResult = parser.parse(new FileReader(metaFile));
            meta = csvMetaResult.getCsvMeta();
        } catch (FileNotFoundException e) {
            Log.logException(singleton, e);
            Message msg = MessageResources.getMessage("GEN0", new Object[]{e.getMessage()});
            ValidatorResults validatorResults =new ValidatorResults();
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            return new CSVDataResult(new CSVSegmentedFileImpl(),validatorResults);
        }
        return parse(dataString, meta);
    }

    public static CSVDataResult parse(File dataFile, CSVMeta csvMeta) throws ApplicationException{
        ValidatorResults validatorResults = new ValidatorResults();
        CSVSegmentedFileImpl segmentedFile = new CSVSegmentedFileImpl();
        CSVDataResult csvDataResult = new CSVDataResult(segmentedFile, validatorResults);
    	try {
    		String[][] data = CsvCache.getCsv(dataFile.getPath());
            return parse(data,csvMeta);
    	} catch (IOException e) {
          Log.logException(singleton, e);
          Message msg = MessageResources.getMessage("GEN0", new Object[]{e.getMessage()});
          validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
          csvDataResult.setValidatorResults(validatorResults);
          return csvDataResult;
        }
    }

    public static CSVDataResult parse(String dataString, CSVMeta csvMeta) throws ApplicationException{
        ValidatorResults validatorResults = new ValidatorResults();
        CSVSegmentedFileImpl segmentedFile = new CSVSegmentedFileImpl();
        CSVDataResult csvDataResult = new CSVDataResult(segmentedFile, validatorResults);
    	try {
    		String[][] data = CsvCache.getCsvFromString(dataString);
    		return parse(data,csvMeta);
    	} catch (IOException e) {
            Log.logException(singleton, e);
            Message msg = MessageResources.getMessage("GEN0", new Object[]{e.getMessage()});
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            csvDataResult.setValidatorResults(validatorResults);
            return csvDataResult;
          }
    }

    public static CSVDataResult parse(InputStream dataStream, CSVMeta csvMeta) throws ApplicationException{
        ValidatorResults validatorResults = new ValidatorResults();
        CSVSegmentedFileImpl segmentedFile = new CSVSegmentedFileImpl();
        CSVDataResult csvDataResult = new CSVDataResult(segmentedFile, validatorResults);
    	try {
    		String[][] data = CsvCache.getCsvFromInputStream(dataStream);
    		return parse(data,csvMeta);
    	} catch (IOException e) {
            Log.logException(singleton, e);
            Message msg = MessageResources.getMessage("GEN0", new Object[]{e.getMessage()});
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            csvDataResult.setValidatorResults(validatorResults);
            return csvDataResult;
          }
    }

    public static CSVDataResult parse(String[][] data, CSVMeta csvMeta) throws ApplicationException
    {
        ValidatorResults validatorResults = new ValidatorResults();
        CSVSegmentedFileImpl segmentedFile = new CSVSegmentedFileImpl();
        CSVDataResult csvDataResult = new CSVDataResult(segmentedFile, validatorResults);

        CSVSegmentMeta rootSegmentMeta = csvMeta.getRootSegment();

        java.util.List<String> segmentNameList = new java.util.ArrayList<String>();

        // load the data. --> has been moved out
        	//            String[][] data = CsvCache.getCsv(dataFile.getPath());

            // traverse + populate objects.
            ArrayList<CSVSegment> logicalRecords = new ArrayList<CSVSegment>();
            CSVSegmentMeta currentSegmentMeta = rootSegmentMeta;
            CSVSegment currentSegment = null;
            CSVSegment rootSegment = null;
            Stack<CSVSegment> segmentStack = new Stack<CSVSegment>();

            for (int i = 0; i < data.length; i++)
            {
                String[] row = data[i];
                String segmentName = row[0];
                //System.out.println("CCCSS : " + segmentName);
                segmentNameList.add(segmentName);
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
                if (segmentName.equalsIgnoreCase(rootSegmentMeta.getName()))
                {
                    // if there is something on the stack - add it to the logical records.
                    // empty the stack (prepare a new logical record)
                    if (segmentStack.size() != 0)
                    {
                        logicalRecords.add(rootSegment);
                        segmentStack = new Stack<CSVSegment>();
                    }
                    rootSegment = currentSegment;
                    segmentStack.push(rootSegment);
                }
                else
                {

                    // we have a child segment.
                    // we need to find the parent + assign it appropriately.
                    CSVSegmentMeta parentSegmentMeta = currentSegmentMeta.getParent();
                    if (currentSegmentMeta.isChoiceMemberSegment()) parentSegmentMeta = parentSegmentMeta.getParent();
                    String parentSegmentMetaName = parentSegmentMeta.getName();
                    // the stack should contain the parent
                    boolean found = false;
                    while (!found)
                    {
                        // check the top of the stack.
                        CSVSegment segment = null;

				        if(!segmentStack.isEmpty())
						{
							segment = segmentStack.peek();
						}
						else
						{//if nothing is found before the stack is empty, return
                            Message msg = MessageResources.getMessage("CSV4", new Object[]{segmentName, parentSegmentMetaName, i});
                            //System.out.println("***CCCSS : " + segmentName + " : " + parentSegmentMetaName);
                            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
							return csvDataResult;
                        }

                        // if found...
						String segmentNameLocal = segment==null? null : segment.getName();
						if (parentSegmentMetaName.equalsIgnoreCase(segmentNameLocal))
                        {
                            found = true;
                            // assign it to the sement that's on the top + add it.
                            segment.addChildSegment(currentSegment);
                            currentSegment.setParentSegment(segment);
                            segmentStack.push(currentSegment);
                        }
                        else
                        {
                            // otherwise, check the next one..
							if(!segmentStack.isEmpty())
							{
								segmentStack.pop();
							}
						}
                    }//end of while (!found)

                } // end of else
            } // end of for

        CSVMetaValidator validator = new CSVMetaValidator(csvMeta);
        ValidatorResults valResults = new ValidatorResults();
		//Check Cardinality for CSV Data
//		valResults.addValidatorResults(validator.ScmRule9(data));
        valResults.addValidatorResults(validator.ScmRule10(data));

        if (valResults.getAllMessages().size() > 0)
        {
            validatorResults.addValidatorResults(valResults);
            if (!validatorResults.isValid()) return csvDataResult;
            //return csvDataResult;
        }

            // there are no more rows.. add the final logical record.
            logicalRecords.add(rootSegment);

            segmentedFile.setLogicalRecords(logicalRecords);
            //System.out.println("done!");

        return csvDataResult;
    }


    public static CSVSegmentMeta findCSVSegmentMeta(CSVSegmentMeta rootsegment, String segmentname) {
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

    private static CSVSegment createSegment(CSVSegmentMeta meta, String[] data, ValidatorResults validatorResults) {
        CSVSegmentImpl segment = new CSVSegmentImpl(meta);
        segment.setXmlPath(meta.getXmlPath());
        segment.setCardinalityType(meta.getCardinalityType());
        // check for validation rule #3
        int metaFields = meta.getFields().size();
        int dataFields = data.length - 1;
        if (dataFields > metaFields) {
            Message msg = MessageResources.getMessage("CSV3", new Object[]{meta.getName(), dataFields, metaFields});
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
        }
        //setup the fields.
        ArrayList<CSVField> fields = new ArrayList<CSVField>();
        List<CSVFieldMeta> fieldMeta = meta.getFields();
        for (int i = 0; i < fieldMeta.size(); i++) {
            CSVFieldMeta csvFieldMeta = fieldMeta.get(i);
            CSVFieldImpl field = new CSVFieldImpl(csvFieldMeta);
            field.setColumn(csvFieldMeta.getColumn());
            field.setXmlPath(csvFieldMeta.getXmlPath());
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
}


/*
    public static CSVDataResult parse(File dataFile, File metaFile) {
        // parse the metafile
        CSVMeta meta = null;
        try {
            CSVMetaParserImpl parser = new CSVMetaParserImpl();
            CSVMetaResult csvMetaResult = parser.parse(new FileReader(metaFile));
            meta = csvMetaResult.getCsvMeta();
        } catch (FileNotFoundException e) {
            Log.logException(singleton, e);
            Message msg = MessageResources.getMessage("GEN0", new Object[]{e.getMessage()});
            ValidatorResults validatorResults =new ValidatorResults();
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            return new CSVDataResult(new CSVSegmentedFileImpl(),validatorResults);
        }
        return parse(dataFile, meta);
    }

    public static CSVDataResult parse(InputStream dataStream, File metaFile) {
        // parse the metafile
        CSVMeta meta = null;
        try {
            CSVMetaParserImpl parser = new CSVMetaParserImpl();
            CSVMetaResult csvMetaResult = parser.parse(new FileReader(metaFile));
            meta = csvMetaResult.getCsvMeta();
        } catch (FileNotFoundException e) {
            Log.logException(singleton, e);
            Message msg = MessageResources.getMessage("GEN0", new Object[]{e.getMessage()});
            ValidatorResults validatorResults =new ValidatorResults();
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            return new CSVDataResult(new CSVSegmentedFileImpl(),validatorResults);
        }
        return parse(dataStream, meta);
    }
    public static CSVDataResult parse(String dataString, File metaFile) {
        // parse the metafile
        CSVMeta meta = null;
        try {
            CSVMetaParserImpl parser = new CSVMetaParserImpl();
            CSVMetaResult csvMetaResult = parser.parse(new FileReader(metaFile));
            meta = csvMetaResult.getCsvMeta();
        } catch (FileNotFoundException e) {
            Log.logException(singleton, e);
            Message msg = MessageResources.getMessage("GEN0", new Object[]{e.getMessage()});
            ValidatorResults validatorResults =new ValidatorResults();
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            return new CSVDataResult(new CSVSegmentedFileImpl(),validatorResults);
        }
        return parse(dataString, meta);
    }


    public static CSVDataResult parse(File dataFile, CSVMeta csvMeta) {
        ValidatorResults validatorResults = new ValidatorResults();
        CSVSegmentedFileImpl segmentedFile = new CSVSegmentedFileImpl();
        CSVDataResult csvDataResult = new CSVDataResult(segmentedFile, validatorResults);
    	try {
    		String[][] data = CsvCache.getCsv(dataFile.getPath());
        	return parse(data,csvMeta);
    	} catch (IOException e) {
          Log.logException(singleton, e);
          Message msg = MessageResources.getMessage("GEN0", new Object[]{e.getMessage()});
          validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
          csvDataResult.setValidatorResults(validatorResults);
          return csvDataResult;
        }
    }

    public static CSVDataResult parse(String dataString, CSVMeta csvMeta) {
        ValidatorResults validatorResults = new ValidatorResults();
        CSVSegmentedFileImpl segmentedFile = new CSVSegmentedFileImpl();
        CSVDataResult csvDataResult = new CSVDataResult(segmentedFile, validatorResults);
    	try {
    		String[][] data = CsvCache.getCsvFromString(dataString);
    		return parse(data,csvMeta);
    	} catch (IOException e) {
            Log.logException(singleton, e);
            Message msg = MessageResources.getMessage("GEN0", new Object[]{e.getMessage()});
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            csvDataResult.setValidatorResults(validatorResults);
            return csvDataResult;
          }
    }

    public static CSVDataResult parse(InputStream dataStream, CSVMeta csvMeta) {
        ValidatorResults validatorResults = new ValidatorResults();
        CSVSegmentedFileImpl segmentedFile = new CSVSegmentedFileImpl();
        CSVDataResult csvDataResult = new CSVDataResult(segmentedFile, validatorResults);
    	try {
    		String[][] data = CsvCache.getCsvFromInputStream(dataStream);
    		return parse(data,csvMeta);
    	} catch (IOException e) {
            Log.logException(singleton, e);
            Message msg = MessageResources.getMessage("GEN0", new Object[]{e.getMessage()});
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            csvDataResult.setValidatorResults(validatorResults);
            return csvDataResult;
          }
    }

    public static CSVDataResult parse(String[][] data, CSVMeta csvMeta) {
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
							if(!segmentStack.isEmpty())
							{
								segment = segmentStack.peek();
							}
							else
							{//if nothing is found before the stack is empty, return
								Message msg = MessageResources.getMessage("CSV4", new Object[]{segmentName, parentSegmentMetaName, i});
								validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
								return csvDataResult;
							}
						} catch (EmptyStackException e) {
                            Log.logException(singleton, e);
                            Message msg = MessageResources.getMessage("CSV4", new Object[]{segmentName, parentSegmentMetaName, i});
                            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
                            return csvDataResult;
                        }
                        // if found...
						String segmentNameLocal = segment==null? null : segment.getName();
						if (parentSegmentMetaName.equalsIgnoreCase(segmentNameLocal)) {
                            found = true;
                            // assign it to the sement that's on the top + add it.
                            segment.addChildSegment(currentSegment);
                            currentSegment.setParentSegment(segment);
                            segmentStack.push(currentSegment);
                        } else {
                            // otherwise, check the next one..
							if(!segmentStack.isEmpty())
							{
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

    public static CSVSegmentMeta findCSVSegmentMeta(CSVSegmentMeta rootsegment, String segmentname) {
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

    private static CSVSegment createSegment(CSVSegmentMeta meta, String[] data, ValidatorResults validatorResults) {
        CSVSegmentImpl segment = new CSVSegmentImpl(meta);
        segment.setUUID(meta.getUUID());
        // check for validation rule #3
        int metaFields = meta.getFields().size();
        int dataFields = data.length - 1;
        if (dataFields > metaFields) {
            Message msg = MessageResources.getMessage("CSV3", new Object[]{meta.getName(), dataFields, metaFields});
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
        }
        //setup the fields.
        ArrayList<CSVField> fields = new ArrayList<CSVField>();
        List<CSVFieldMeta> fieldMeta = meta.getFields();
        for (int i = 0; i < fieldMeta.size(); i++) {
            CSVFieldMeta csvFieldMeta = fieldMeta.get(i);
            CSVFieldImpl field = new CSVFieldImpl(csvFieldMeta);
            field.setColumn(csvFieldMeta.getColumn());
            field.setUUID(csvFieldMeta.getUUID());
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
}
*/