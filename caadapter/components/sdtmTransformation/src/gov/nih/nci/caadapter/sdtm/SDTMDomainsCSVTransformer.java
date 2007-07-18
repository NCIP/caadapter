package gov.nih.nci.caadapter.sdtm;

import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.data.CSVSegmentedFile;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.sdtm.util.RDSHelper;
import gov.nih.nci.caadapter.sdtm.util.RDSMapResult;
import gov.nih.nci.caadapter.ui.mapping.sdtm.SDTMMany2ManyMapping;
import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Jun 19, 2007
 * Time: 12:31:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class SDTMDomainsCSVTransformer
{

    String directoryLocation;

    Hashtable globaldomainList;

    //Very useful provides the Object to be process on
    CSVDataResult csvDataResult;

    //The entire data file
    ArrayList recordsSetForEntireCSV;

    //This object reads the map file and creates <key, Arraylist> / <domainName, mappedSourceXpath~SDTMKey as arrayList>
    Hashtable hashTableTransform;

    //This objects just checks if the preceding record is equal to the record in process; since the csv data files are notoriously sequential.
    Stack checkForRepeats = new Stack();

    //This object stores the result of the transformation keyed by domain name and data with arraylist
    Hashtable resultHashTableData = new Hashtable();

    /*
      This object maintains a list of repeating record meaning it will have the segment object and can be traversed all the way
      to the parent for values; so the structure for the repeating records are maintained; this list is per record basis
    */ ArrayList repeatSegments;

    public SDTMDomainsCSVTransformer(File mapFile, String _csvFileName, String _scsFileName, String _directoryLocation) throws Exception
    {
        //new RDSTransformer( mapFile,_csvFileName,_scsFileName,_directoryLocation);
//        directoryLocation = _directoryLocation;
//        CSVMapFileReader csvMapFileReader = new CSVMapFileReader(mapFile);
//        globaldomainList = RDSHelper.getAllFieldsForDomains(new File(RDSHelper.getDefineXMLNameFromMapFile(mapFile.getAbsolutePath())));
//        hashTableTransform = csvMapFileReader.getHashTableTransform();
//        prepareCSVDataFromCSVDataFile(_csvFileName, _scsFileName);
    }

    private void prepareCSVDataFromCSVDataFile(String _csvFileName, String _scsFileName)
    {
        recordsSetForEntireCSV = new ArrayList();
        CSVPanel csvPanel = new CSVPanel();
        File csvFile = new File(_csvFileName);
        csvPanel.setSaveFile(new File(_scsFileName), true);
        CSVMeta rootMeta = csvPanel.getCSVMeta(false);
        SDTMMany2ManyMapping segmentedCSVParser = new SDTMMany2ManyMapping();
        csvDataResult = segmentedCSVParser.parse(csvFile, rootMeta);
        List csvDATA = segmentedCSVParser.returnCsvMapData1;
        csvDATA.add(csvDATA.get(0));
        //for each domain process the data records so it is easier to manage
        Enumeration en = hashTableTransform.keys();
        processDataRecords();
        if (dithu.size() > 0)
        {
            for (int d = 0; d < dithu.size(); d++)
            {
                String domainNameResult = ((RDSMapResult) dithu.get(d)).getDomainName().toString();
                if (finalResultTable.containsKey(domainNameResult))
                {
                    ((ArrayList) finalResultTable.get(domainNameResult)).add(((RDSMapResult) dithu.get(d)).getArrayList());
                } else
                {
                    ArrayList resArray = new ArrayList();
                    resArray.add(((RDSMapResult) dithu.get(d)).getArrayList());
                    finalResultTable.put(domainNameResult, resArray);
                }
            }
        }
        processResult();
    }

    private void processResult()
    {
        Enumeration domains = finalResultTable.keys();
        while (domains.hasMoreElements())
        {
            String domain = (String) domains.nextElement();
            ArrayList resArray = (ArrayList) finalResultTable.get(domain);
            writeFile(domain, resArray);
        }
    }

    private void processDataRecords()
    {
        CSVSegmentedFile csvSegmentedFile = csvDataResult.getCsvSegmentedFile();
        List recordsinCSVFile = csvSegmentedFile.getLogicalRecords();
        for (int k = 0; k < recordsinCSVFile.size(); k++)
        //for (int k = 0; k < 1; k++)// uncomment this and remove the above line
        {
            processOneRecordTest(recordsinCSVFile, k);
        }
    }

    private void processOneRecordTest(List recordsinCSVFile, int recordNumber){
        ArrayList retAry = getSegmentTrailWithoutChildPre((CSVSegment) recordsinCSVFile.get(recordNumber));
        System.out.println("");
    }

    private void processOneRecord(List recordsinCSVFile, int recordNumber)
    {
        CSVSegment csvSegment = (CSVSegment) recordsinCSVFile.get(recordNumber);
        /*
           1.) Process each segment to seek out the if they have child elements
           2.) Process those child segments for any fields which may have been mapped
           3.) Add the segments which have the mappings to the arraylist; In the end we will hava list of csvsegment OBJECT who have been mapped
        */
        if (csvSegment.getParentSegment() == null)
        {
            if (isFieldsMapped(csvSegment))
            {
                segmentsWithmappings.add(csvSegment);
            }
        }
        getSegmentTrailWithoutChild(csvSegment);
    }

    Hashtable finalResultTable = new Hashtable();

    private String isFieldMapped(String field)
    {
        Enumeration enum1 = hashTableTransform.keys();
        while (enum1.hasMoreElements())
        {
            String domainName = (String) enum1.nextElement();
            ArrayList array = (ArrayList) hashTableTransform.get(domainName);
            for (int i = 0; i < array.size(); i++)
            {
                String _tempStr = array.get(i).toString();
                String _temStr2 = _tempStr.substring(0, _tempStr.indexOf("~"));
                //System.out.println("comparing " + field + "  and  " + _temStr2);
                if (_temStr2.equals(field))
                {
                    // System.out.println("\t\t\tReturning " + _tempStr.substring(_tempStr.indexOf("~") + 1, _tempStr.length()));
                    return _tempStr.substring(_tempStr.indexOf("~") + 1, _tempStr.length());
                }
            }
        }
        return null;
    }

    ArrayList segmentsWithmappings = new ArrayList();

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void getSegmentTrailWithoutChild(CSVSegment csvSegment)
    {
        List list = csvSegment.getChildSegments();
        for (int i = 0; i < list.size(); i++)
        {
            CSVSegment csvChildSegment = (CSVSegment) list.get(i);
            // check if csvChildSegment has fields which are mapped
            if (isFieldsMapped(csvChildSegment))
            {
                segmentsWithmappings.add(csvChildSegment);
            }
            //continue seeking the childsegments who may still have the mapping adding it to the collection
            getSegmentTrailWithoutChild(csvChildSegment);
        }
    }

    private boolean isFieldsMapped(CSVSegment csvSegment)
    {
        boolean retVal = false;
        //This Xpath will be used to prefix all the fields
        String xpathPrefix = RDSHelper.getParentasXPath(csvSegment);
        if (csvSegment.getFields().size() > 0)
        {
            List fieldsForEachSegment = csvSegment.getFields();
            for (int fields = 0; fields < fieldsForEachSegment.size(); fields++)
            {
                CSVField fieldObject = (CSVField) fieldsForEachSegment.get(fields);
                String mappedValue = isFieldMapped(xpathPrefix + "\\" + fieldObject.getMetaObject().toString());
                if (mappedValue != null)
                {
                    //harsha invoke RDS map result from here and add to the array List as an object to be processed finally!!
                    //dithu.add(new RDSMapResult(globaldomainList, mappedValue, xpathPrefix + "\\" + fieldObject.getMetaObject().toString(), fieldObject.getValue(), csvSegment, hashTableTransform));
                    return true;
                }
            }
        }
        return retVal;
    }

    ArrayList dithu = new ArrayList();

    private void writeFile(String domainName, ArrayList resultData)
    {
        try
        {
            ArrayList domainHeader = (ArrayList) globaldomainList.get(domainName);
            //create a file with domain name
            FileWriter fstream = new FileWriter(directoryLocation + "\\" + domainName + ".csv");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(domainHeader.toString().substring(1, domainHeader.toString().indexOf(']')));
            for (int i = 0; i < resultData.size(); i++)
            {
                try
                {
                    ArrayList writeToList = (ArrayList) resultData.get(i);
                    String tempStr = writeToList.toString();
                    String resultStr = tempStr.substring(1, tempStr.indexOf(']'));
                    out.write("\n" + resultStr);
                } catch (IOException e)
                {
                    e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
                    System.out.println("continuing to next loop " + e.getMessage());
                }
            }
            out.close();
        } catch (IOException e)
        {
            e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
        }
    }
      ArrayList listary = new ArrayList();
    /////////////////////////////////////////////////////////////////////////////////////////////////////////// take 2
    private ArrayList getSegmentTrailWithoutChildPre(CSVSegment csvSegment)
    {

        // start with the current segments
        String xpathPrefix = RDSHelper.getParentasXPath(csvSegment);
        if (csvSegment.getFields().size() > 0)
        {
            List fieldsForEachSegment = csvSegment.getFields();
            for (int fields = 0; fields < fieldsForEachSegment.size(); fields++)
            {
                CSVField fieldObject = (CSVField) fieldsForEachSegment.get(fields);
                String mappedValue = isFieldMapped(xpathPrefix + "\\" + fieldObject.getMetaObject().toString());
                if (mappedValue != null)
                {
                    //dithu.add(new RDSMapResult(globaldomainList, mappedValue, xpathPrefix + "\\" + fieldObject.getMetaObject().toString(), fieldObject.getValue(), csvSegment, hashTableTransform));
                    //listary.add(new RDSMapResult(globaldomainList, mappedValue, xpathPrefix + "\\" + fieldObject.getMetaObject().toString(), fieldObject.getValue(), csvSegment, hashTableTransform));
                }
            }
        }
        // continue for the child segments
        List list = csvSegment.getChildSegments();
        for (int i = 0; i < list.size(); i++)
        {
            CSVSegment csvChildSegment = (CSVSegment) list.get(i);
            //continue seeking the childsegments who may still have the mapping adding it to the collection
            getListOfFarthestMappedField(csvChildSegment, listary);
            if (csvChildSegment.getChildSegments().size() > 0)
                getSegmentTrailWithoutChildPre(csvChildSegment);
        }
        return listary;
    }

    private ArrayList getListOfFarthestMappedField(CSVSegment csvSegment, ArrayList list)
    {
        //This Xpath will be used to prefix all the fields
        String xpathPrefix = RDSHelper.getParentasXPath(csvSegment);
        if (csvSegment.getFields().size() > 0)
        {
            List fieldsForEachSegment = csvSegment.getFields();
            for (int fields = 0; fields < fieldsForEachSegment.size(); fields++)
            {
                CSVField fieldObject = (CSVField) fieldsForEachSegment.get(fields);
                String mappedValue = isFieldMapped(xpathPrefix + "\\" + fieldObject.getMetaObject().toString());
                if (mappedValue != null)
                {
                    //dithu.add(new RDSMapResult(globaldomainList, mappedValue, xpathPrefix + "\\" + fieldObject.getMetaObject().toString(), fieldObject.getValue(), csvSegment, hashTableTransform));
                    //list.add(new RDSMapResult(globaldomainList, mappedValue, xpathPrefix + "\\" + fieldObject.getMetaObject().toString(), fieldObject.getValue(), csvSegment, hashTableTransform));
                }
            }
        }
        return list;
    }
}
