/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.common.metadata;
import gov.nih.nci.caadapter.common.MetaObjectImpl;
import java.util.ArrayList;
import java.util.List;
/**
 * @version 1.0
 * @created 11-Aug-2006 8:18:15 AM
 */
public class DatabaseMetadata extends MetaObjectImpl{

	private List<TableMetadata> tables = new ArrayList<TableMetadata>();

	/**
	 *
	 * @param tables
	 */
	public DatabaseMetadata(List<TableMetadata> tables){
		this.tables = tables;
	}

	public DatabaseMetadata(){
	}

	/**
	 *
	 * @param table
	 */
	public void addTable(TableMetadata table){
		tables.add(table);
	}

	/**
	 *
	 * @param table
	 */
	public boolean containsTable(TableMetadata table){
		boolean containsTable = false;
		if (tables.contains(table)) {
			containsTable = true;
		}
		return containsTable;
	}

	/**
	 *
	 * @param table
	 */
	public void removeTable(TableMetadata table){
		tables.remove(table);
	}

}