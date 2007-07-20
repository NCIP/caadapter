package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.dataviewer.util.GetConnectionSingleton;
import gov.nih.nci.caadapter.dataviewer.util.QBParseMappingFile;
import gov.nih.nci.caadapter.sdtm.ParseSDTMXMLFile;
import gov.nih.nci.caadapter.sdtm.SDTMMetadata;
import gov.nih.nci.caadapter.sdtm.util.CSVMapFileReader;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.CaadapterFileFilter;
import gov.nih.nci.caadapter.ui.common.tree.DefaultTargetTreeNode;
import gov.nih.nci.caadapter.ui.mapping.sdtm.Database2SDTMMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.sdtm.RDSFixedLenghtInput;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.prefs.Preferences;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: May 11, 2007
 * Time: 2:26:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class QBTransformAction
{

    JFileChooser directoryLoc, saveXLSLocation;

    File directory;

    private Connection con;

    Preferences prefs;

    HashMap fixedLengthRecords;

    boolean fixedLengthIndicator = false;

    public QBTransformAction(AbstractMainFrame _mainFrame, Database2SDTMMappingPanel mappingPanel, Connection _con, Preferences _prefs) throws Exception
    {
        //this(_mainFrame, mappingPanel, "");
        this.con = _con;
        directoryLoc = new JFileChooser(System.getProperty("user.dir"));
        directoryLoc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = directoryLoc.showOpenDialog(_mainFrame);
        this.prefs = _prefs;
        if (prefs.get("FIXED_LENGTH_VAR", "NULL").equalsIgnoreCase("Fixed"))
        {
            fixedLengthIndicator = true;
            CSVMapFileReader csvMapFileReader = new CSVMapFileReader(new File(mappingPanel.getSaveFile().getAbsolutePath()));
            //Prepare the list here and keep it ready so that number of blanks corresponding to the
            //value set by the user will be applied appropriately
            RDSFixedLenghtInput rdsFixedLenghtInput = new RDSFixedLenghtInput(_mainFrame, csvMapFileReader.getTargetKeyList());
            fixedLengthRecords = rdsFixedLenghtInput.getUserValues();
        }
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            directory = directoryLoc.getSelectedFile();
            try
            {
                processTransform1(mappingPanel.getSaveFile().getAbsolutePath(), mappingPanel.getDefineXMLLocation(), directory.getAbsolutePath().toString());
            } catch (Exception e)
            {
                throw e;
            } finally
            {
                GetConnectionSingleton.closeConnection();
            }
        }
    }

    public QBTransformAction(AbstractMainFrame _mainFrame, Database2SDTMMappingPanel mappingPanel, String newway)
    {
        CaadapterFileFilter filter = new CaadapterFileFilter();
        filter.addExtension("xls");
        filter.setDescription("xls");
        saveXLSLocation = new JFileChooser(System.getProperty("user.dir"));
        saveXLSLocation.setFileFilter(filter);
        int returnVal = saveXLSLocation.showSaveDialog(_mainFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            String xlsFileName = saveXLSLocation.getSelectedFile().toString();
            if (!xlsFileName.endsWith(".xls"))
            {
                xlsFileName = xlsFileName + ".xls";
            }
            System.out.println(xlsFileName);
            try
            {
                processTransform1(mappingPanel.getSaveFile().getAbsolutePath(), mappingPanel.getDefineXMLLocation(), directory.getAbsolutePath().toString());
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) throws Exception
    {
        // new QBTransformAction().processTransform1("c:\\d2.map", "c:\\define.xml", "c:\\save.xls");
    }

    public static Connection getConnection() throws Exception
    {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "hr", "hr");
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

    public void processTransform1(String savedMapFile, String defineXML, String xlsFile) throws Exception
    {
        try
        {
            QBParseMappingFile qb = new QBParseMappingFile(new File(savedMapFile));
            Hashtable tempTable = getAllFieldsForDomains(new File(defineXML));
            Iterator _iter = qb.getHashSQLfromMappings().keySet().iterator();
            while (_iter.hasNext())
            {
                //get the domain name
                String domainName = _iter.next().toString();
                //create a file with domain name
                FileWriter fstream = new FileWriter(xlsFile + "\\" + domainName + ".csv");
                BufferedWriter out = new BufferedWriter(fstream);
                //get the query and fire it
                String query = qb.getHashSQLfromMappings().get(domainName);
                ResultSet rs = con.createStatement().executeQuery(query);
                //get all the columns for the 'domainName'
                ArrayList columns = qb.getHashTableTransform().get(domainName);
                out.write(tempTable.get(domainName).toString().substring(1, tempTable.get(domainName).toString().indexOf(']')));
                ArrayList domainHeader = (ArrayList) tempTable.get(domainName);
                //compute the number of commas for each mapped columnvalue and set if the retrieved value is
                int rsIncrementer = 1;
                while (rs.next())
                {// each row begins here
                    try
                    {
                        // if(rsIncrementer == 5000)
                        //     break;
                        int sizeOfEachRow = ((ArrayList) tempTable.get(domainName)).size();
                        ArrayList _tempArray = new ArrayList(sizeOfEachRow + 1);
                        for (int j = 0; j < (sizeOfEachRow + 1); j++)
                        {
                            _tempArray.add(" ");//add empty buffer
                        }
                        for (int i = 0; i < columns.size(); i++)
                        {
                            EmptyStringTokenizer emt = new EmptyStringTokenizer(columns.get(i).toString(), "~");
                            EmptyStringTokenizer getColumn = new EmptyStringTokenizer(emt.nextToken(), ".");
                            String _dataStr = rs.getString(getColumn.getTokenAt(1) + "_" + getColumn.getTokenAt(2));
                            String empt1 = emt.nextToken().toString();
                            int position = (new Integer(((ArrayList) tempTable.get(domainName)).indexOf(empt1)));
                            if (fixedLengthRecords.containsKey(empt1))
                            {
                                try
                                {
                                    _tempArray = implementFixedRec(position, _dataStr, _tempArray, new Integer(fixedLengthRecords.get(empt1).toString()).intValue());
                                } catch (Exception e)
                                {
                                    String runTimePrp = System.getProperty("debug", "false");
                                    if (new Boolean(runTimePrp))
                                        System.out.println("Problem for target field \"" + empt1 + "\" at position \"" + position + "\" and date was \"" + _dataStr + "\"");
                                }
                            } else
                            {
                                _tempArray.remove(position);
                                try
                                {
                                    _tempArray.add(position, _dataStr);
                                } catch (Exception e)
                                {
                                    System.out.println("error at " + position);
                                }
                            }
                        }
                        out.write("\n" + _tempArray.toString().substring(1, _tempArray.toString().indexOf(']')));
                        rsIncrementer++;
                    } catch (Exception e)
                    {
                        e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
                        continue;
                    }
                }// result set
                out.close();
            }
        } catch (SQLException e)
        {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private ArrayList implementFixedRec(int position, String srcData, ArrayList _tempArray, int fixedsize) throws Exception
    {
        StringBuffer _setSize;
        int finalSize = fixedsize - srcData.length();
        _setSize = new StringBuffer();
        _setSize.append(srcData);
        for (int i = 0; i < finalSize; i++)
        {
            _setSize.append(" ");
        }
        _tempArray.add(position - 1, _setSize.toString());
        return _tempArray;
    }
}
