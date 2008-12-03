/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmps.ui.common;


/**
 * Define a list of constants that are used by menu and some of action definition.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2008-12-03 20:46:14 $
 */
public interface ActionConstants
{
	//The naming convention:
	//NEW_MAP_FILE is used as the key to the text, while the NEW_MAP_FILE_TXT is used as the action name;
	//this is because the action name in display may be the same but we really need to distinguish them in a map
	//between a new and an open command.
	String NEW_O2DB_MAP_FILE = "Object Model to Data Model Map Specification";
	String NEW_CSV2XMI_MAP_FILE = "New Csv Meta To Xmi Mapping";
    String NEW_XSD2XMI_MAP_FILE = "New Xsd To Xmi Mapping (GME)";   

	String OPEN_O2DB_MAP_FILE_TXT = "Object Model to Data Model Map Specification";
	String OPEN_CSV2SDTM_MAP_FILE_TXT = "CSV/Database to RDS Map Specification";
	String OPEN_O2DB_MAP_FILE = "Open " + OPEN_O2DB_MAP_FILE_TXT;
	String OPEN_CSV2XMI_MAP_FILE = "Open CSV Meta To Object/Data Model Mapping";
    String OPEN_XSD2XMI_MAP_FILE = "Open XSD Meta To XMI Mapping";

	String OPEN = "Open...";
	String CLOSE = "Close";
	String CLOSE_ALL = "Close All";
	String SAVE = "Save";
	String SAVE_AS = "Save As...";
	String EXIT = "Exit";
	String HELP_TOPIC = "HELP_TOPIC";

	String HELP = "Help - Contents and Index...";
	//String HELP_MANAGER = "Help Mgr";
	//String HELP_MANAGER2 = "Help Content Manager";   // For Tool Tip

	String GENERATE_REPORT = "Generate Report...";
	String VALIDATE = "Validate";
	String ANOTATE = "Anotate";
	String REFRESH = "Refresh";
	String NEW_MAP_FILE = "New Map file";
	String OPEN_MAP_FILE = "Open Map file";
}

/**
 * HISTORY : $Log: not supported by cvs2svn $
 * HISTORY : Revision 1.1  2008/11/04 15:58:57  linc
 * HISTORY : updated.
 * HISTORY :
 */
