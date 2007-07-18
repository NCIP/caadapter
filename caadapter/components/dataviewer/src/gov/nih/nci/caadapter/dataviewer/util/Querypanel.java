package gov.nih.nci.caadapter.dataviewer.util;

import nickyb.sqleonardo.querybuilder.DiagramEntity;
import nickyb.sqleonardo.querybuilder.DiagramLoader;
import nickyb.sqleonardo.querybuilder.QueryBuilder;
import nickyb.sqleonardo.querybuilder.syntax.QueryTokens;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;

import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;

public class Querypanel extends JPanel
{

    MainDataViewerFrame _tf;

    private String schema;

    public ArrayList<DiagramLoader> _diagramLoaderList = new ArrayList<DiagramLoader>();

    public QueryBuilder get_queryBuilder()
    {
        return _queryBuilder;
    }

    QueryBuilder _queryBuilder;

    MainDataViewerFrame _controller;

    public String getSchema()
    {
        return schema;
    }

    public Querypanel(Connection con)
    {
        this.setLayout(new BorderLayout());
        _queryBuilder = new QueryBuilder(con);
        this.add(_queryBuilder, BorderLayout.CENTER);
    }

    public Querypanel(Connection con, MainDataViewerFrame tf)
    {
        this.setLayout(new BorderLayout());
        this._tf = tf;
        _queryBuilder = new QueryBuilder(con);
        this.add(_queryBuilder, BorderLayout.CENTER);
    }

    public Querypanel(Connection con, MainDataViewerFrame tf, String _schema)
    {
        this.setLayout(new BorderLayout());
        this._tf = tf;
        this.schema = _schema; 
        _queryBuilder = new QueryBuilder(con, schema, tf);
        
        this.add(_queryBuilder, BorderLayout.CENTER);
    }

    /*
    This method is called when the panel is already showing
     */
    public void fillPanel()
    {
        loadTables("hr", "Departments");
        loadTables("hr", "Locations");
        loadTables("hr", "JOBS");
        loadTables("hr", "Countries");
        loadTables("hr", "REGIONS");
        loadTables("hr", "EMPLOYEES");
    }

    public void loadTables(String schema, String tableName)
    {
        QueryTokens.Table token2 = new QueryTokens.Table(schema, tableName);
        DiagramLoader _diaLoad = new DiagramLoader(55, _queryBuilder, token2, true, _tf);
        _diagramLoaderList.add(_diaLoad);
        _diaLoad.show();
    }

    public boolean removeAllTables()
    {
        for (int i = 0; i < _diagramLoaderList.size(); i++)
        {
            Iterator _entity = _diagramLoaderList.get(i).getAllEntities().iterator();
            //get the querybuilder reference
            //now take tis entity and remove this in using query builder
            _queryBuilder.getDiagram().manager.closeFrame((DiagramEntity) _entity.next());
        }
        return true;
    }
}
