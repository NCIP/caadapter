/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.common.metadata;

import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.SDKMetaData;

/**
 * @version 1.0
 * @created 11-Aug-2006 8:18:15 AM
 */
public class ColumnMetadata extends MetaObjectImpl implements SDKMetaData{

	private String datatype;
	private boolean isForeignKey;
	private boolean isPrimaryKey = true;
	private String name;
	private String xPath;
	private TableMetadata table;
	private String type; 
    public static String TYPE_ATTRIBUTE = "TYPE_ATTRIBUTE";  
    public static String TYPE_ASSOCIATION = "TYPE_ASSOCIATION";

	public ColumnMetadata(){
	}

	/**
	 * @param name
	 */
	public ColumnMetadata(String name) {
		super();
		// TODO Auto-generated constructor stub
		this.name = name;
	}

	/**
	 * @param name
	 * @param path
	 */
	public ColumnMetadata(String name, String path) {
		super();
		// TODO Auto-generated constructor stub
		this.name = name;
		xPath = path;
	}

	/**
	 * @param datatype
	 * @param name
	 * @param path
	 */
	public ColumnMetadata(String datatype, String name, String path) {
		super();
		// TODO Auto-generated constructor stub
		this.datatype = datatype;
		this.name = name;
		xPath = path;
	}

	/**
	 * @param datatype
	 * @param name
	 * @param path
	 * @param table
	 */
	public ColumnMetadata(String datatype, String name, String path, TableMetadata table) {
		super();
		// TODO Auto-generated constructor stub
		this.datatype = datatype;
		this.name = name;
		xPath = path;
		this.table = table;
	}

	/**
	 * @param datatype
	 * @param isForeignKey
	 * @param name
	 * @param path
	 * @param table
	 */
	public ColumnMetadata(String datatype, boolean isForeignKey, String name, String path, TableMetadata table) {
		super();
		// TODO Auto-generated constructor stub
		this.datatype = datatype;
		this.isForeignKey = isForeignKey;
		this.name = name;
		xPath = path;
		this.table = table;
	}

	/**
	 *
	 * @param xPath
	 * @param datatype
	 * @param name
	 * @param isPrimaryKey
	 * @param isForeignKey
	 */
	public ColumnMetadata(String xPath, String datatype, String name, boolean isPrimaryKey, boolean isForeignKey, TableMetadata table){
		this.xPath = xPath;
		this.datatype = datatype;
		this.name = name;
		this.isForeignKey = isForeignKey;
		this.isPrimaryKey = isPrimaryKey;
		this.table = table;
	}

	public String getDatatype(){
		return datatype;
	}

	public String getName(){
		return name;
	}

	public String getXPath(){
		return xPath;
	}

	public boolean isForeignKey(){
		return isForeignKey;
	}

	public boolean isPrimaryKey(){
		return isPrimaryKey;
	}

    public TableMetadata getTableMetadata() {
		return table;
	}
    
    public String getType(){
        return type;
    }
    
    public void setType(String type){
        this.type = type;   
    }
    
	/**
	 *
	 * @param datatype
	 */
	public void setDatatype(String datatype){
		this.datatype = datatype;
	}

	/**
	 *
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}

	public String toString() {
		return getName();
	}
	
	public String getTitle() {
		return getName();
	}

	/**
	 *
	 * @param xPath
	 */
	public void setXPath(String xPath){
		this.xPath = xPath;
	}

    /**
     *
     * @param table
     */
    public void setTableMetadata(TableMetadata table) {
		this.table = table;
	}

	/**
	 *
	 * @param isForeignKey
	 */
	 public void setIsForeignKey(boolean isForeignKey) {
		 this.isForeignKey = isForeignKey;
	 }
	/**
	 *
	 * @param isPrimaryKey
	 */
	 public void setIsPrimaryKey(boolean isPrimaryKey) {
		 this.isPrimaryKey = isPrimaryKey;
	 }
	 
	 public String getParentXPath() {
			String parentXPath;
			int attributeStartPoint = this.xPath.lastIndexOf(".");
			parentXPath = this.xPath.substring(0,attributeStartPoint);
			return parentXPath;
		}

}