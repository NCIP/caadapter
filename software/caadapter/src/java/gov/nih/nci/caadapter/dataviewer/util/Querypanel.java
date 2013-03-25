/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dataviewer.util;

import nickyb.sqleonardo.querybuilder.DiagramLoader;
import nickyb.sqleonardo.querybuilder.QueryBuilder;
import nickyb.sqleonardo.querybuilder.syntax.QueryTokens;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * This class helps during transformation
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.5 $
 *          $Date: 2008-06-09 19:53:50 $
 */
public class Querypanel extends JPanel {
    private String schema=null;
    private QueryBuilder _queryBuilder=null;
    public ArrayList<DiagramLoader> _diagramLoaderList = new ArrayList<DiagramLoader>();

    public QueryBuilder get_queryBuilder() {
        return _queryBuilder;
    }

    public String getSchema() {
        return schema;
    }

    public Querypanel(Connection con) {
        this.setLayout(new BorderLayout());
        _queryBuilder = new QueryBuilder(con);
        this.add(_queryBuilder, BorderLayout.CENTER);
    }

    public Querypanel(Connection con, String _schema) {
        this.setLayout(new BorderLayout());
        this.schema = _schema;
        _queryBuilder = new QueryBuilder(con);
        this.add(_queryBuilder, BorderLayout.CENTER);
    }

    public void loadTables(String schema, String tableName) {
        QueryTokens.Table token2 = new QueryTokens.Table(schema, tableName);
        DiagramLoader.run(55, _queryBuilder, token2, true);
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2007/08/16 18:53:55  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
