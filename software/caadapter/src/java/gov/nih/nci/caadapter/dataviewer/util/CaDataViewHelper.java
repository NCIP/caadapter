/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dataviewer.util;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Collection;

/**
 * This class is an intermediary class which is called to process the
 * MAP file, with the SQL query which results in checking the right
 * check boxes in the data viewer frame
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.4 $
 *          $Date: 2008-06-09 19:53:50 $
 */
public class CaDataViewHelper {
    private MainDataViewerFrame viewerFrame = null;
    private String domainName = null;

    public CaDataViewHelper(MainDataViewerFrame viewerFrame, String domainName) {
        this.viewerFrame = viewerFrame;
        this.domainName = domainName;
    }

    public String processColumns(String SQLString, File mapFile) {
        QBParseMappingFile qb = new QBParseMappingFile(mapFile);
        return replaceNonMappedColumns(SQLString, getColumnsForDomain(domainName, qb.getHashTableTransform()));
    }

    private Hashtable getColumnsForDomain(String domainName, Hashtable result) {
        Hashtable _ret = new Hashtable();
        ArrayList columnSet = (ArrayList) result.get(domainName);
        String tempStr;
        String asTableName;
        EmptyStringTokenizer empt;
        for (int i = 0; i < columnSet.size(); i++) {
            tempStr = columnSet.get(i).toString();
            asTableName = tempStr.substring(tempStr.indexOf("~"));
            tempStr = tempStr.substring(0, tempStr.indexOf("~"));
            empt = new EmptyStringTokenizer(tempStr, ".");
            tempStr = empt.getTokenAt(1) + ".\"" + empt.getTokenAt(2) + "\"";
            _ret.put(tempStr, asTableName);
        }
        return _ret;
    }

    private String replaceNonMappedColumns(String SQLString, Hashtable mappedColumns) {
        //System.out.println("whio me "+SQLString);
       // System.out.println("mapped columns "+mappedColumns);
        if (!SQLString.equals("SELECT")) {
            String domain = getDomainName(mappedColumns);
            //System.out.println("daomin is "+domain);
            viewerFrame.getSqlListWODataViewer().put(domain, SQLString);
        }
        String append = SQLString.substring(SQLString.indexOf("FROM"));
        String chopSql = SQLString.substring(0, SQLString.indexOf("FROM"));
        String removeSelect = chopSql.substring(6);
        StringBuffer returnString = new StringBuffer();
        EmptyStringTokenizer empt = new EmptyStringTokenizer(removeSelect, ",");
        String check;
        EmptyStringTokenizer empt1;
        StringBuffer createSQLString = new StringBuffer();
        for (int i = 0; i < empt.countTokens(); i++) {
            check = empt.getTokenAt(i);
            empt1 = new EmptyStringTokenizer(check.trim(), " ");
            if (mappedColumns.containsKey(empt1.getTokenAt(0))) {
                createSQLString.append(empt1.getTokenAt(0));
                createSQLString.append(" AS ");
                createSQLString.append(empt1.getTokenAt(2));
                createSQLString.append(",");
            }
        }
        returnString.append("SELECT ");
        String formatReturnSQL = createSQLString.toString();
        int remove = formatReturnSQL.lastIndexOf(",");
        formatReturnSQL = formatReturnSQL.substring(0, remove);
        returnString.append(formatReturnSQL);
        returnString.append(" " + append);
        return returnString.toString();
    }

    private String getDomainName(Hashtable table){
        Collection collection = table.values();
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            Object o =  iterator.next();
            EmptyStringTokenizer emp = new EmptyStringTokenizer((String)o, "~");
            emp.nextToken();
            return emp.nextToken().toString().substring(0,2);
        }
        return null;
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2007/09/13 13:53:56  jayannah
 * Changes made to fix, window position, parameters during the launch of data viewer, handling of the toolbar buttons and to GEnerate the SQL when the user does not want to use the data viewer
 *
 * Revision 1.2  2007/08/16 18:53:55  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */