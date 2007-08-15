package gov.nih.nci.caadapter.dataviewer.util;

import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;
import nickyb.sqleonardo.querybuilder.DiagramLoader;
import nickyb.sqleonardo.querybuilder.QueryBuilder;
import nickyb.sqleonardo.querybuilder.syntax.QueryTokens;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;

public class Querypanel extends JPanel {

    private MainDataViewerFrame _tf;

    private String schema;

    private QueryBuilder _queryBuilder;

    private MainDataViewerFrame _controller;

    private String domain;

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

    public Querypanel(Connection con, MainDataViewerFrame tf, String _schema, String _domain) {
        this.setLayout(new BorderLayout());
        this._tf = tf;
        this.schema = _schema;
        this.domain = _domain;
        _queryBuilder = new QueryBuilder(con);
        this.add(_queryBuilder, BorderLayout.CENTER);

    }

    public void loadTables(String schema, String tableName) {
        QueryTokens.Table token2 = new QueryTokens.Table(schema, tableName);
        DiagramLoader.run(55, _queryBuilder, token2, true);
    }
}
