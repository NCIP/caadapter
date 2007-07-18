package gov.nih.nci.caadapter.ui.mapping.sdtm;

import com.Ostermiller.util.StringTokenizer;
import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.sdtm.*;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.tree.DefaultTargetTreeNode;
import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class SDTMNewTransformer
{

    String saveSDTMFile;

    BufferedWriter out = null;

    ArrayList order1 = new ArrayList();

    ArrayList order2 = new ArrayList();

    Hashtable tempTable;

    public static void main(String[] art) throws Exception
    {
        try
        {
            new SDTMNewTransformer().processCSVDATA(art);
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public SDTMNewTransformer(String _mapFileName, String _csvFileName, String _scsFileName, AbstractMainFrame _mainFrame, String _saveSDTMFileName) throws Exception{      
       new SDTMCsvTransformer(new File(_mapFileName), _csvFileName, _scsFileName, _saveSDTMFileName);
    }

    public SDTMNewTransformer(String _mapFileName, String _csvFileName, String _scsFileName, AbstractMainFrame _mainFrame, String _saveSDTMFileName, boolean newway) throws Exception
    {
        saveSDTMFile = _saveSDTMFileName;
        try
        {
            if (_saveSDTMFileName.endsWith("txt"))
            {
                out = new BufferedWriter(new FileWriter(_saveSDTMFileName));
            } else
            {
                _saveSDTMFileName = _saveSDTMFileName + ".txt";
                out = new BufferedWriter(new FileWriter(_saveSDTMFileName));
            }
            tempTable = getAllFieldsForDomains(new File(getDefineXMLNameFromMapFile(_mapFileName)));
            processCSVDATA(_mapFileName, _csvFileName, _scsFileName, _mainFrame, _saveSDTMFileName);
            SDTMMany2ManyMapping.clearReturnDataList();
            out.flush();
            out.close();
            JOptionPane.showMessageDialog(_mainFrame, "Created the file \"" + _saveSDTMFileName + "\" successfully");
        } catch (Exception outerException)
        {
            JOptionPane.showMessageDialog(_mainFrame, "Created the file \"" + outerException.getLocalizedMessage() + "\" successfully");
        } finally
        {
            if (out != null)
                out.close();
        }
    }

    public SDTMNewTransformer()
    {
        // TODO Auto-generated constructor stub
    }

    public boolean processCSVDATA(String _mapFileName, String _csvFileName, String _scsFileName, AbstractMainFrame _mainFrame, String _saveSDTMFileName) throws Exception
    {
        System.out.println(SDTMRecordStructure.printCols());
        order1.add(SDTMRecordStructure.printCols());
        out.write(SDTMRecordStructure.printCols() + "\n");
        ArrayList _csvData = new ArrayList();
        String lineTMP = null;
        EmptyStringTokenizer strTk = null;
        int firstMarker = 0;
        String startRecord = null;
        HashMap map = GetMappings.returnMappings(_mapFileName);
        // insert code
        CSVPanel csvPanel = new CSVPanel();
        File csvFile = new File(_csvFileName);
        ValidatorResults validatorResults = csvPanel.setSaveFile(new File(_scsFileName), true);
        ValidatorResults validatorResults2 = new ValidatorResults();
        CSVMeta rootMeta = csvPanel.getCSVMeta(false);
        SDTMMany2ManyMapping segmentedCSVParser = new SDTMMany2ManyMapping();
        CSVDataResult result = segmentedCSVParser.parse(csvFile, rootMeta);
        List csvDATA = segmentedCSVParser.returnCsvMapData1;
        csvDATA.add(csvDATA.get(0));
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
                processRecordController(_csvData, map);
                _csvData.clear();
                _csvData.add(lineTMP);
            } else
            {
                _csvData.add(lineTMP);
            }
            firstMarker++;
        }
        return true;
    }

    // New code begin
    public void processRecordController(ArrayList csvData, HashMap map) throws Exception
    {
        SDTMRecordStructure _newRec = new SDTMRecordStructure();
        for (int a = 0; a < csvData.size(); a++)
        {
            StringTokenizer str = new StringTokenizer(csvData.get(a).toString(), "^");
            String key = str.nextToken();
            String value = str.nextToken();
            if (map.containsKey(key))
            {
                // step 1: map the obvious mapped source
                String mapVal = (String) map.get(key).toString();
                //createSDTMRecUsingArrayList(mapVal, value);
                createSDTMRec(_newRec, mapVal, value);
                // step 2: String tokenize the key by '\\' and check for
                // mappings if it exists then put the value in sdtmrec instance
                // step 3: spit the sdtmrec to the txt file
            }
            if (key.startsWith("R"))
            {
                // System.out.println ( "the repeat record is "+key);
                createRepeatRecord(key, value, map, csvData);
            }
        }// end for stmt
        if (_newRec.hasAtLeastOneValue(_newRec))
        {
            System.out.println(_newRec.toString());
            order1.add(_newRec.toString());
            out.write(_newRec.toString() + "\n");
        }
    }

    public void createRepeatRecord(String strVal, String val, HashMap map, ArrayList csvData) throws Exception
    {
        SDTMRecordStructure repeatRecord = new SDTMRecordStructure();
        String key = strVal.substring(1);
        try
        {
            createSDTMRec(repeatRecord, map.get(key).toString(), val);
        } catch (Exception e1)
        {
        }
        if (repeatRecord.hasAtLeastOneValue(repeatRecord))
        {
            ArrayList keyList = returnKeyList(key.toString());
            for (int k = 0; k < keyList.size(); k++)
            {
                String keyFromList = (String) keyList.get(k);
                LinkedHashMap tempMap = convertArraylistToHashMap(csvData);
                try
                {
                    if (map.containsKey(keyFromList))
                    {
                        String one = map.get(keyFromList).toString();
                        String two = tempMap.get(keyFromList).toString();
                        createSDTMRec(repeatRecord, one, two);
                    }
                } catch (Exception e)
                {
                }
            }
            if (repeatRecord.hasAtLeastOneValue(repeatRecord))
            {
                System.out.println(repeatRecord.toString());
                order2.add(repeatRecord.toString());
                out.write(repeatRecord.toString() + "\n");
            }
        }
    }

    public ArrayList returnKeyList(String s)
    {
        // String s = "\\Source Tree\\INVESTEVN\\PERTAINSTOXX2_A\\PROCEDUREEVENT_A1\\IDENTIFIEDDEVICE040005_A13\\IDENTIFIEDDEVICE_A131\\INVENTORYITEM_A1311\\MANUFACTUREDDEVICEMODEL_A13111\\ASREGULATEDPRODUCT_A131111\\REGULATORYAUTHORITY_A1311111";
        ArrayList a = new ArrayList();
        EmptyStringTokenizer e = new EmptyStringTokenizer(s, "\\");
        int d = e.countTokens();
        while (e.hasMoreTokens())
        {
            e.deleteTokenAt(d - 1);
            String tmp = e.toString();
            try
            {
                int pos = tmp.lastIndexOf("\\");
                // System.out.println( tmp.substring( 0, pos));
                a.add(tmp.substring(0, pos));
            } catch (Exception es)
            {
            }
            d--;
        }
        return a;
    }

    HashSet domainSet = new HashSet();

    public void createSDTMRecUsingArrayList(String mapVal, String csvVal)
    {
        /**
         * prepare the csvVal by stringtokenizing and ready to be called by position
         */
        EmptyStringTokenizer emtVal = new EmptyStringTokenizer(csvVal, ",");
        /**
         * for each token in the map val get the position and call the cvsVal value and set the sdtm record
         */
        EmptyStringTokenizer emtMap = new EmptyStringTokenizer(mapVal, ",");
        while (emtMap.hasMoreTokens())
        {
            StringTokenizer str = new StringTokenizer(emtMap.nextToken(), "?");
            int position = new Integer(str.nextToken().substring(0, 1));
            //int position1 = (new Integer(((ArrayList) tempTable.get(domainName)).indexOf(emt.nextToken().toString())));
            //String key = str.nextToken().toString().replace('.', '_');
            String _tmpVal = str.nextToken().toString();
            String domainName = _tmpVal.substring(0, _tmpVal.indexOf("."));
            int position1 = new Integer(((ArrayList) tempTable.get(domainName)).indexOf(_tmpVal)) + 1;
            String val = emtVal.getTokenAt(position - 1);
            //setSDTMRecord(rec, key, val);
            System.out.println("");
        }
    }

    public void createSDTMRec(SDTMRecordStructure rec, String mapVal, String csvVal)
    {
        /**
         * prepare the csvVal by stringtokenizing and ready to be called by position
         */
        EmptyStringTokenizer emtVal = new EmptyStringTokenizer(csvVal, ",");
        /**
         * for each token in the map val get the position and call the cvsVal value and set the sdtm record
         */
        EmptyStringTokenizer emtMap = new EmptyStringTokenizer(mapVal, ",");
        while (emtMap.hasMoreTokens())
        {
            StringTokenizer str = new StringTokenizer(emtMap.nextToken(), "?");
            int position = new Integer(str.nextToken().substring(0, 1));
            // setSDTMRecord(SDTMRecord sdtmRec, String sdtmKey, String dataKey)
            String key = str.nextToken().toString().replace('.', '_');
            String val = emtVal.getTokenAt(position - 1);
            setSDTMRecord(rec, key, val);
            // System.out.println(rec.toString());
        }
    }

    public ArrayList getKeysList(String key)
    {
        ArrayList retKeysList = new ArrayList();
        StringTokenizer keys = new StringTokenizer(key, ",");
        while (keys.hasMoreTokens())
        {
        }
        return retKeysList;
    }

    public LinkedHashMap convertArraylistToHashMap(ArrayList csvDataRecords)
    {
        // HashMap _ret = new HashMap();
        // HashMap _mv = new HashMap();
        LinkedHashMap _mv = new LinkedHashMap();
        for (int i = 0; i < csvDataRecords.size(); i++)
        {
            EmptyStringTokenizer emt = new EmptyStringTokenizer(csvDataRecords.get(i).toString(), "^");
            String key = emt.nextToken();
            String val = emt.nextToken();
            _mv.put(key, val);
            // if (key.startsWith( "R"))
            // System.out.println( "+++++++++++++++++++++++++++ "+key );
            // System.out.println( key + " = " + val);
        }
        return _mv;
    }

    // New code end
    public SDTMRecordStructure setSDTMRecord(SDTMRecordStructure sdtmRec, String sdtmKey, String dataKey)
    {
        if (sdtmKey.startsWith("STUDYID"))
        {
            sdtmRec.setSTUDYID(dataKey);
        } else if (sdtmKey.startsWith("DOMAIN"))
        {
            sdtmRec.setDOMAIN(dataKey);
        } else if (sdtmKey.startsWith("USUBJID"))
        {
            sdtmRec.setUSUBJID(dataKey);
        } else if (sdtmKey.startsWith("SUBJID"))
        {
            sdtmRec.setSUBJID(dataKey);
        } else if (sdtmKey.startsWith("DM_RFSTDTC"))
        {
            sdtmRec.setDM_RFSTDTC(dataKey);
        } else if (sdtmKey.startsWith("DM_RFENDTC"))
        {
            sdtmRec.setDM_RFENDTC(dataKey);
        } else if (sdtmKey.startsWith("DM_SITEID"))
        {
            sdtmRec.setDM_SITEID(dataKey);
        } else if (sdtmKey.startsWith("DM_INVID"))
        {
            sdtmRec.setDM_INVID(dataKey);
        } else if (sdtmKey.startsWith("DM_BRTHDTC"))
        {
            sdtmRec.setDM_BRTHDTC(dataKey);
        } else if (sdtmKey.startsWith("DM_AGE"))
        {
            sdtmRec.setDM_AGE(dataKey);
        } else if (sdtmKey.startsWith("DM_AGEU"))
        {
            sdtmRec.setDM_AGEU(dataKey);
        } else if (sdtmKey.startsWith("DM_SEX"))
        {
            sdtmRec.setDM_SEX(dataKey);
        } else if (sdtmKey.startsWith("DM_RACE"))
        {
            sdtmRec.setDM_RACE(dataKey);
        } else if (sdtmKey.startsWith("DM_ARMCD"))
        {
            sdtmRec.setDM_ARMCD(dataKey);
        } else if (sdtmKey.startsWith("DM_ARM"))
        {
            sdtmRec.setDM_ARM(dataKey);
        } else if (sdtmKey.startsWith("DM_COUNTRY"))
        {
            sdtmRec.setDM_COUNTRY(dataKey);
        } else if (sdtmKey.startsWith("DM_DMDTC"))
        {
            sdtmRec.setDM_DM_TC(dataKey);
        } else if (sdtmKey.startsWith("DM_DMDY"))
        {
            sdtmRec.setDM_DMDY(dataKey);
        }
        return sdtmRec;
    }

    public static Hashtable getAllFieldsForDomains(File SDTMXmlFile)
    {
        ParseSDTMXMLFile _parseSDTMFile = new ParseSDTMXMLFile(SDTMXmlFile.getAbsolutePath().toString());
        ArrayList _retArray = _parseSDTMFile.getSDTMStructure();
        Hashtable domainFieldsList = new Hashtable();
        DefaultMutableTreeNode pNode = null;
        String domainString = "";
        ArrayList fieldsString = null;
        String _tempHolder;
        domainFieldsList = new Hashtable();
        for (int k = 0; k < _retArray.size(); k++)
        {
            if (_retArray.get(k).toString().startsWith("KEY"))
            {
                if (fieldsString != null)
                    domainFieldsList.put(domainString, fieldsString);
                fieldsString = new ArrayList();
                EmptyStringTokenizer _str = new EmptyStringTokenizer(_retArray.get(k).toString(), ",");
                pNode = new DefaultMutableTreeNode(_str.getTokenAt(1).substring(0, 2));
                domainString = pNode.toString();
            } else
            {
                EmptyStringTokenizer _str = new EmptyStringTokenizer(_retArray.get(k).toString(), ",");
                _tempHolder = _str.getTokenAt(1);
                pNode.add(new DefaultTargetTreeNode(new SDTMMetadata(pNode.toString(), _tempHolder, _str.getTokenAt(2), _str.getTokenAt(3), _str.getTokenAt(4))));
                if (!_tempHolder.startsWith(domainString))
                    _tempHolder = domainString + "." + _tempHolder;
                fieldsString.add(_tempHolder.substring(0, _tempHolder.indexOf('&')));
            }
        }
        return domainFieldsList;
    }

    public static String getDefineXMLNameFromMapFile(String mapFile) throws Exception
    {
        String defineXMLName = "";
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new File(mapFile));
        //System.out.println( "Root element of the doc is " + doc.getDocumentElement().getNodeName());
        NodeList linkNodeList = doc.getElementsByTagName("components");
        int totalPersons = linkNodeList.getLength();
        //System.out.println( "Total no of links are : " + totalPersons);
        // System.out.println( defineXMLList.toString());
        for (int s = 0; s < linkNodeList.getLength(); s++)
        {
            Node node = linkNodeList.item(s);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element firstCompElement = (Element) node;
                NodeList targetNode = firstCompElement.getElementsByTagName("component");
                Element targetName1 = (Element) targetNode.item(0);
                targetName1.getAttribute("location").toString();
                Element targetName2 = (Element) targetNode.item(1);
                if (targetName2.getAttribute("kind").toString().equalsIgnoreCase("XML"))
                {
                    defineXMLName = targetName2.getAttribute("location").toString();
                }
            }
        }
        return defineXMLName;
    }

    public void processCSVDATA(String[] args) throws Exception
    {
        //		System.out.println(SDTMRecordStructure.printCols());
        //		order1.add(SDTMRecordStructure.printCols());
        //		out.write(SDTMRecordStructure.printCols());
        //		ArrayList _csvData = new ArrayList();
        //		String lineTMP = null;
        //		EmptyStringTokenizer strTk = null;
        //		int firstMarker = 0;
        //		String startRecord = null;
        //		HashMap map = GetMappings.returnMappings("d:\\" + args[0] + ".map");
        //		//List csvDATA = SDTMMany2ManyMapping.getHashMapData("d:\\" + args[0] + ".csv", "d:\\" + args[0] + ".scs");
        //		List csvDATA =
        //		for (Iterator it = csvDATA.iterator(); it.hasNext();) {
        //			lineTMP = (String) it.next();
        //			// System.out.println( lineTMP);
        //			String _lineTMP = lineTMP.substring(0, lineTMP.indexOf('^'));
        //			if (firstMarker == 0) {
        //				startRecord = _lineTMP;
        //			}
        //			if (_lineTMP.toString().equalsIgnoreCase(startRecord) && firstMarker != 0) {
        //				// _csvData.add( lineTMP);
        //				processRecordController(_csvData, map);
        //				_csvData.clear();
        //				_csvData.add(lineTMP);
        //			} else {
        //				_csvData.add(lineTMP);
        //			}
        //			firstMarker++;
        //		}
    }
}