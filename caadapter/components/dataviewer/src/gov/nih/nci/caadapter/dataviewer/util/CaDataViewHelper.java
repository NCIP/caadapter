package gov.nih.nci.caadapter.dataviewer.util;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;

import java.util.Hashtable;
import java.util.ArrayList;
import java.io.File;

/**
 *
 * This class is an intermediary class which is called to process the
 * MAP file, with the SQL query which results in checking the right
 * check boxes in the data viewer frame
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: jayannah $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.2 $
 *          $Date: 2007-08-16 18:53:55 $
 */
public class CaDataViewHelper {

   public String processColumns(String domainName, String SQLString, File mapFile ) {
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
        returnString.append("Select ");      
        String formatReturnSQL = createSQLString.toString();
        int remove = formatReturnSQL.lastIndexOf(",");
        formatReturnSQL = formatReturnSQL.substring(0, remove);
        returnString.append(formatReturnSQL);
        returnString.append(" " + append);
        return returnString.toString();
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 */