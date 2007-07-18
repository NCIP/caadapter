package gov.nih.nci.caadapter.sdtm;

import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.data.CSVSegmentedFile;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.sdtm.util.CSVMapFileReader;
import gov.nih.nci.caadapter.sdtm.util.RDSHelper;
import gov.nih.nci.caadapter.ui.mapping.sdtm.SDTMMany2ManyMapping;
import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;

import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Jun 14, 2007
 * Time: 10:20:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class SDTMCsvTransformer
{

    /*
     globaldomainList has this as contents <String, ArrayList>
     DM=[DM.STUDYID, DM.DOMAIN, DM.USUBJID, DM.SUBJID, DM.RFSTDTC, DM.RFENDTC, DM.SITEID, DM.INVID, DM.BRTHDTC, DM.AGE, DM.AGEU, DM.SEX, DM.RACE, DM.ARMCD, DM.ARM, DM.COUNTRY, DM.DMDTC, DM.DMDY]
    */ Hashtable globaldomainList;

    LinkedList recordSetHashtable;

    ArrayList recordsSetForEntireCSV;

    CSVDataResult csvDataResult;
    public SDTMCsvTransformer(File mapFile, String _csvFileName, String _scsFileName, String _directoryLocation) throws Exception{
        new SDTMDomainsCSVTransformer(mapFile,_csvFileName,_scsFileName, _directoryLocation);
    }


    public SDTMCsvTransformer(File mapFile, String _csvFileName, String _scsFileName, boolean old) throws Exception
    {
        CSVMapFileReader csvMapFileReader = new CSVMapFileReader(mapFile);
        globaldomainList = RDSHelper.getAllFieldsForDomains(new File(RDSHelper.getDefineXMLNameFromMapFile(mapFile.getAbsolutePath())));
        Hashtable table = csvMapFileReader.getHashTableTransform();
        Enumeration enumeration = table.keys();
        ArrayList values;
        prepareCSVDataFromCSVDataFile(_csvFileName, _scsFileName);
        while (enumeration.hasMoreElements())
        {
            /*
              key is going to be 'DomainName'; the example is as below;
              DM=[\Source Tree\ORGS\ORGNM\1 : Name~DM.RFSTDTC,\Source Tree\ORGS\ORGAD\2 : Street_2~DM.DMDTC,\Source Tree\ORGS\1 : ORG_CODE~DM.ARM,\Source Tree\ORGS\ORGAD\3 : City~DM.DOMAIN,\Source Tree\ORGS\ORGID\2 : Extension~DM.BRTHDTC]
            */
            String key = (String) enumeration.nextElement();
            values = (ArrayList) table.get(key);
            //
            globaldomainList.get(key);
            processEachDomain(key, values);
        }
    }

    private void processEachDomain(String domainName, ArrayList mappedKey)
    {
        /*
          Create a txt file for each domain and do the meat of processing in this method
         */
        ArrayList arrayRow = null;
        HashSet checkForRepeats = null;
        // set this to null after each record
        ArrayList aCopyforEachRow = new ArrayList();
        int sizeOfDomain = new Integer(((ArrayList) globaldomainList.get(domainName)).size());
        for (int i = 0; i < recordsSetForEntireCSV.size(); i++)
        {
            checkForRepeats = new HashSet();
            //
            arrayRow = new ArrayList();
            for (int l = 0; l < sizeOfDomain; l++)
            {
                arrayRow.add(" ");
            }
            LinkedList _tempTable = (LinkedList) recordsSetForEntireCSV.get(i);
            LinkedList _tempTableRefined = new LinkedList();
            for (int a = 0; a < _tempTable.size(); a++)
            {
                String xpathCsvData = (String) _tempTable.get(a);
                if (checkForRepeats.contains(xpathCsvData.substring(0, xpathCsvData.indexOf("$"))))
                {
                    _tempTableRefined.add("R" + xpathCsvData);
                } else
                {
                    _tempTableRefined.add(xpathCsvData);
                    checkForRepeats.add(xpathCsvData.substring(0, xpathCsvData.indexOf("$")));
                }
            }
            for (int a = 0; a < _tempTableRefined.size(); a++)
            {
                //
                //start marking 'repeats' to the csvxpathdata begin
                //start marking 'repeats' to the csvxpathdata end
                //
                String xpathCsvData = (String) _tempTableRefined.get(a);
                // for one csvxpathdata , compare with complete map file for all the mapped values
                for (int j = 0; j < mappedKey.size(); j++)
                {
                    String _tmpStr = (String) mappedKey.get(j);
                    String _one = _tmpStr.substring(0, _tmpStr.indexOf("~"));
                    EmptyStringTokenizer emt = new EmptyStringTokenizer(_one, "\\");
                    String _two = emt.deleteTokenAt(emt.countTokens() - 1, true);
                    _one = emt.toStringAndRemoveLastSlash();
                    //System.out.println("comparing " + _one + "  and " + xpathCsvData);
                    String trimDollarSign = xpathCsvData.substring(0, xpathCsvData.indexOf("$"));
                    if (_one.equalsIgnoreCase(trimDollarSign))
                    {
                        //System.out.println("\t\twe have a hit " + xpathCsvData + " value are " + _tempTableRefined.get(xpathCsvData));
                        // System.out.println("============================");
                        //System.out.println("\t\tsdtm domain is "+ _tmpStr.substring(_tmpStr.indexOf("~")+1) );
                        EmptyStringTokenizer empt = new EmptyStringTokenizer(xpathCsvData.substring(xpathCsvData.indexOf("$") + 1, xpathCsvData.length()), ",");
                        //System.out.println("\t\tthe source value will be "+empt.getTokenAt(new Integer(_two.substring(0,1)).intValue()-1));
                        int position = new Integer(((ArrayList) globaldomainList.get(domainName)).indexOf(_tmpStr.substring(_tmpStr.indexOf("~") + 1))) + 1;
                        //System.out.println("\t\tthe position at target will be "+position);
                        arrayRow.remove(position - 1);
                        arrayRow.add(position - 1, empt.getTokenAt(new Integer(_two.substring(0, 1)).intValue() - 1));
                        //System.out.println("============================");
                    }
                }
                if (xpathCsvData.startsWith("R"))
                {
                    CSVSegment csvSegment = getRecord(a);
                    System.out.println("");
                }
            }
            System.out.println(arrayRow);
        }
    }

    private void prepareCSVDataFromCSVDataFile(String _csvFileName, String _scsFileName)
    {
        recordsSetForEntireCSV = new ArrayList();
        // get the csv data from the file
        String lineTMP;
        int firstMarker = 0;
        ArrayList _csvData = new ArrayList();
        String startRecord = null;
        CSVPanel csvPanel = new CSVPanel();
        File csvFile = new File(_csvFileName);
        csvPanel.setSaveFile(new File(_scsFileName), true);
        CSVMeta rootMeta = csvPanel.getCSVMeta(false);
        SDTMMany2ManyMapping segmentedCSVParser = new SDTMMany2ManyMapping();
        csvDataResult = segmentedCSVParser.parse(csvFile, rootMeta);
        List csvDATA = segmentedCSVParser.returnCsvMapData1;
        csvDATA.add(csvDATA.get(0));
        processDataRecords();
        // insert code
        //List csvDATA = SDTMMany2ManyMapping.getHashMapData(_csvFileName, _scsFileName);
        for (Iterator it = csvDATA.iterator(); it.hasNext();)
        {
            lineTMP = (String) it.next();
            // System.out.println( lineTMP);
            String _lineTMP = lineTMP.substring(0, lineTMP.indexOf('^'));
            if (firstMarker == 0)
            {
                startRecord = _lineTMP;
            }
            if (_lineTMP.toString().equalsIgnoreCase(startRecord) && firstMarker != 0)
            {
                // _csvData.add( lineTMP);
                //processRecordController(_csvData, map);
                System.out.println("_csvData " + _csvData);
                recordSetHashtable = new LinkedList();
                for (int i = 0; i < _csvData.size(); i++)
                {
                    String _tempStr = (String) _csvData.get(i);
                    StringTokenizer str = new StringTokenizer(_tempStr, "^");
                    recordSetHashtable.add(str.nextToken() + "$" + str.nextToken());
                }
                recordsSetForEntireCSV.add(recordSetHashtable);
                _csvData.clear();
                _csvData.add(lineTMP);
            } else
            {
                _csvData.add(lineTMP);
            }
            firstMarker++;
        }
    }

    private CSVSegment getRecord(int recordNumber)
    {
        List resultList = csvDataResult.getCsvSegmentedFile().getLogicalRecords();
        for (int i = 0; i < resultList.size(); i++)
        {
            CSVSegment object = (CSVSegment) resultList.get(i);
            if (recordNumber == i)
            {
                return object;
            }
        }
        return null;
    }

    private void processDataRecords()
    {
        CSVSegmentedFile csvSegmentedFile = csvDataResult.getCsvSegmentedFile();
        List recordsinCSVFile = csvSegmentedFile.getLogicalRecords();
        // for (int k = 0; k < recordsinCSVFile.size(); k++)
        for (int k = 0; k < 1; k++)
        {
            CSVSegment csvSegment = (CSVSegment) recordsinCSVFile.get(k);
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++ RECORD NUMBER");
            /*
                1.) Process each segment to seek out the if they have child elements
                2.) Process those child segments for any fields which may have been mapped
             */
            processSegment(csvSegment, true);
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++ RECORD NUMBER");
        }
    }

    private void processSegment(CSVSegment csvSegment, boolean newway)
    {
        while (csvSegment.getChildSegments().size() > 0)
        {
            List childSegments = csvSegment.getChildSegments();
            for (int segments = 0; segments < childSegments.size(); segments++)
            {
                csvSegment = (CSVSegment) childSegments.get(segments);
                // process each segment for fields which may be mapped
                processSegmentFields(csvSegment);
            }
        }
    }

    private void processSegmentFields(CSVSegment csvSegment)
    {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + getParentasXPath(csvSegment));
        if (csvSegment.getFields().size() > 0)
        {
            List fieldsForEachSegment = csvSegment.getFields();
            for (int fields = 0; fields < fieldsForEachSegment.size(); fields++)
            {
                CSVField fieldObject = (CSVField) fieldsForEachSegment.get(fields);
                /*
                 * 1.Check if each field (getMetaObject.toString()) this is mapped to a sdtm domain name;
                 * 2.If it is mapped then get the value of that field and add to the array for that row
                 */
                System.out.println("\t\tfields are " + fieldObject.getMetaObject() + "  col is " + fieldObject.getColumn() + " value is " + fieldObject.getValue());
            }
        }
    }

    private void processSegment(CSVSegment csvSegment)
    {
        if (csvSegment.getChildSegments().size() > 0)
        {
            /*
                check if this name is equal to the previous name; if it is then it is a repeating record
             */
            List childSegments = csvSegment.getChildSegments();
            for (int segments = 0; segments < childSegments.size(); segments++)
            {
                CSVSegment segmentObject = (CSVSegment) childSegments.get(segments);
                //processSegmentFields(segmentObject);
                if (segmentObject.getChildSegments().size() > 0)
                {
                    List moreChildSegments = segmentObject.getChildSegments();
                    for (int moreSegments = 0; moreSegments < moreChildSegments.size(); moreSegments++)
                    {
                        CSVSegment object = (CSVSegment) moreChildSegments.get(moreSegments);
                        processSegment(object);
                    }
                }
            }
        }
    }

    private String getParentasXPath(CSVSegment csvsegment)
    {
        ArrayList list = new ArrayList();
        StringBuffer sb = new StringBuffer();
        try
        {
            while (true)
            {
                list.add(csvsegment.getMetaObject());
                csvsegment = csvsegment.getParentSegment();
                if (csvsegment.getParentSegment() == null)
                {
                    list.add(csvsegment.getMetaObject());
                    break;
                }
            }
        } catch (Exception e)
        {
            //System.out.println(e.getMessage());
        }
        for (int l = 1; l < list.size() + 1; l++)
        {
            try
            {
                int sizeNow = list.size() - l;
                sb.append("\\" + list.get(sizeNow));
            } catch (Exception ed)
            {
                // ed.printStackTrace();
            }
        }
        // _sourceDataAsXPath.append("\\" + _tmpStr);
        return "\\Source tree" + sb.toString();
        //return stack.toString();
    }

    public static void main(String args[]) throws Exception
    {
        //new SDTMCsvTransformer(new File("d:\\d2.map"));
    }
}
