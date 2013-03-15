/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**

The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location:



 */
package gov.nih.nci.caadapter.dataviewer.util;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * This class helps during transformation
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.4 $
 *          $Date: 2008-06-09 19:53:50 $
 */
public class QBTransformHelper {
    Hashtable sqlColumnNames=null;
    HashMap resultSetSQL=null;

    public QBTransformHelper() {
    }

    public QBTransformHelper(Hashtable sqlTable) {
        sqlColumnNames = new Hashtable();
        Enumeration enum1 = sqlTable.keys();
        while (enum1.hasMoreElements()) {
            String o = (String) enum1.nextElement();
            process(o, (String) sqlTable.get(o));
        }
    }

    public Hashtable getSqlColumnNames() {
        return sqlColumnNames;
    }

    private void process(String key, String sql) {
        resultSetSQL = new HashMap();
        //SELECT CAHL7_MESSAGE_DATA."ID" AS CAHL7_MESSAGE_DATA_ID, CAHL7_MESSAGE_TYPE_FIELD."FIELD_ID" AS CAHL7_MESSAGE_TYPE_FIELD_FIELD, CAHL7_MESSAGE_TYPE."MESSAGE_TYPE_NAME" AS CAHL7_MESSAGE_TYPE_MESSAGE_TYP, CAHL7_MESSAGE_TYPE."UPDATE_DATE" AS CAHL7_MESSAGE_TYPE_UPDATE_DATE, CAHL7_MESSAGE_INSTANCE."MESSAGE_TYPE_ID" AS CAHL7_MESSAGE_INSTANCE_MESSAGE, CAHL7_MESSAGE_INSTANCE."MESSAGE_TIMESTAMP" AS CAHL7_MESSAGE_INSTANCE_MESSAGE
        //FROM "CAADAPTER"."CAHL7_MESSAGE_TYPE_FIELD" CAHL7_MESSAGE_TYPE_FIELD INNER JOIN "CAADAPTER"."CAHL7_MESSAGE_DATA" CAHL7_MESSAGE_DATA ON CAHL7_MESSAGE_TYPE_FIELD."FIELD_ID" = CAHL7_MESSAGE_DATA."FIELD_ID" INNER JOIN "CAADAPTER"."CAHL7_MESSAGE_TYPE" CAHL7_MESSAGE_TYPE ON CAHL7_MESSAGE_DATA."MESSAGE_TYPE_ID" = CAHL7_MESSAGE_TYPE."MESSAGE_TYPE_ID" INNER JOIN "CAADAPTER"."CAHL7_MESSAGE_INSTANCE" CAHL7_MESSAGE_INSTANCE ON CAHL7_MESSAGE_DATA."MESSAGE_INSTANCE_ID" = CAHL7_MESSAGE_INSTANCE."MESSAGE_INSTANCE_ID" AND CAHL7_MESSAGE_TYPE."MESSAGE_TYPE_ID" = CAHL7_MESSAGE_INSTANCE."MESSAGE_TYPE_ID" AND CAHL7_MESSAGE_INSTANCE."CREATION_DATE" = CAHL7_MESSAGE_TYPE."CREATION_DATE"
        String chopSql = sql.substring(0, sql.indexOf("FROM"));
        String removeSelect = chopSql.substring(6);
        EmptyStringTokenizer empt = new EmptyStringTokenizer(removeSelect.trim(), " ");
        if (!isEven(empt.countTokens())) {
            //System.out.println(" +------------------------------SQL NOT [EVEN] THERE IS A PROBLEM");
        }
        String _value;
        String _key = "";
        for (int i = 0; i < empt.countTokens(); i++) {
            if (empt.getTokenAt(i).equalsIgnoreCase("AS")) {
                _value = empt.getTokenAt(i + 1);
                _key = _key.replace("\"", "");
                _key = _key.replace(".", "_");
                _value = _value.replace(",", "");
                resultSetSQL.put(_key, _value);
            } else {
                _key = empt.getTokenAt(i);
            }
        }
        sqlColumnNames.put(key, resultSetSQL);
    }

    public static boolean isEven(int intValue) {
        return ((intValue & 1) == 0);
    }

    public static void main(String args[]) {
        String test = "SELECT CAHL7_MESSAGE_DATA.\"ID\" AS CAHL7_MESSAGE_DATA_ID, CAHL7_MESSAGE_TYPE_FIELD.\"FIELD_ID\" S CAHL7_MESSAGE_TYPE_FIELD_FIELD, CAHL7_MESSAGE_TYPE.\"MESSAGE_TYPE_NAME\" AS CAHL7_MESSAGE_TYPE_MESSAGE_TYP, CAHL7_MESSAGE_TYPE.\"UPDATE_DATE\" AS CAHL7_MESSAGE_TYPE_UPDATE_DATE, CAHL7_MESSAGE_INSTANCE.\"MESSAGE_TYPE_ID\" AS CAHL7_MESSAGE_INSTANCE_MESSAGE, CAHL7_MESSAGE_INSTANCE.\"MESSAGE_TIMESTAMP\" AS CAHL7_MESSAGE_INSTANCE_MESSAGE FROM \"CAADAPTER\".\"CAHL7_MESSAGE_TYPE_FIELD\" CAHL7_MESSAGE_TYPE_FIELD INNER JOIN \"CAADAPTER\".\"CAHL7_MESSAGE_DATA\" CAHL7_MESSAGE_DATA ON CAHL7_MESSAGE_TYPE_FIELD.\"FIELD_ID\" = CAHL7_MESSAGE_DATA";
        QBTransformHelper test1 = new QBTransformHelper();
        test1.sqlColumnNames = new Hashtable();
        test1.process("", test);
        System.out.println("" + test1.resultSetSQL);
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2007/08/29 20:42:38  jayannah
 * removed a comment
 *
 * Revision 1.2  2007/08/16 18:53:55  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
