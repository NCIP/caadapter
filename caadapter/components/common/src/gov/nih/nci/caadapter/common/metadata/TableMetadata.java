/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.common.metadata;
import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.SDKMetaData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @version 1.0
 * @created 11-Aug-2006 8:18:19 AM
 */
public class TableMetadata extends MetaObjectImpl implements SDKMetaData{

	private List<ColumnMetadata> columns = new ArrayList<ColumnMetadata>();
	/**
	 * This attribute indicates whether the table is a correlation table or
	 * not.
	 */
	private String name;
	private String type = "normal";
	private String xPath;
	private boolean hasDiscriminator = false;
	/**
	 *
	 * @param column
	 */
	public void addColumn(ColumnMetadata column){
		columns.add(column);
	}

	/**
	 *
	 * @param columnMetadata
	 */
	public boolean containsColumn(ColumnMetadata columnMetadata){
		boolean containsColumn = false;
		if (columns.contains(columnMetadata)) {
			containsColumn = true;
		}
		return containsColumn;
	}

	public List<ColumnMetadata> getColumns(){
		return columns;
	}

	public String getType(){
		return type;
	}

	public String getXPath(){
		return xPath;
	}

	public String getName(){
		return name;
	}
	/**
	 *
	 * @param columnMetadata
	 */
	public void removeColumn(ColumnMetadata columnMetadata){
        columns.remove(columnMetadata);
	}

	/**
	 *
	 * @param columns
	 */
	public void setColumns(List<ColumnMetadata> columns){
		this.columns = columns;
	}

	/**
	 *
	 * @param type
	 */
	public void setType(String type){
		this.type = type;
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

	public ColumnMetadata getPrimaryKeyColumn() {
		ColumnMetadata columnMetadata = new ColumnMetadata();
		Iterator i = columns.iterator();
		while (i.hasNext()) {
			try {
				ColumnMetadata col = (ColumnMetadata) i.next();
				if(col.isPrimaryKey()) {
					columnMetadata = col;
				}
			} catch (Exception e) {
				//LOGGER.fine(e.getMessage());
				e.printStackTrace();
			}
		}
		return columnMetadata;
	}

	/**
	 * @return the hasDiscriminator
	 */
	public boolean hasDiscriminator() {
		return hasDiscriminator;
	}

	/**
	 * @param hasDiscriminator the hasDiscriminator to set
	 */
	public void setHasDiscriminator(boolean hasDiscriminator) {
		this.hasDiscriminator = hasDiscriminator;
	}
}
