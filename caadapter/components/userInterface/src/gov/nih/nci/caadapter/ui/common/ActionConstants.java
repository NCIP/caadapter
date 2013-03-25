/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common;

import gov.nih.nci.caadapter.common.util.Config;

/**
 * Define a list of constants that are used by menu and some of action definition.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.9 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public interface ActionConstants
{
	//The naming convention:
	//NEW_MAP_FILE is used as the key to the text, while the NEW_MAP_FILE_TXT is used as the action name;
	//this is because the action name in display may be the same but we really need to distinguish them in a map
	//between a new and an open command.
	String NEW_MAP_FILE_TXT = "CSV to HL7 v3 " + Config.MAP_MODULE_NAME;
	String NEW_MAP_FILE = "New " + NEW_MAP_FILE_TXT;
	String NEW_CSV_SPEC_TXT = Config.CSV_MODULE_NAME;
	String NEW_CSV_SPEC = "New " + NEW_CSV_SPEC_TXT;
	String NEW_HL7_V3_MESSAGE_TXT = Config.HL7_V3_MESSAGE_MODULE_NAME;
	String NEW_HL7_V3_TO_CSV= "New "+Config.HL7_V3_TO_CSV_MODULE_NAME;
	String NEW_HL7_V3_MESSAGE = "New " + NEW_HL7_V3_MESSAGE_TXT;
	String NEW_HSM_FILE_TXT = Config.HL7_V3_METADATA_MODULE_NAME;
	String NEW_HSM_FILE = "New " + NEW_HSM_FILE_TXT;
	String NEW_O2DB_MAP_FILE = "Object Model to Data Model Map Specification";
	String NEW_CSV2XMI_MAP_FILE = "New Csv Meta To Xmi Mapping";
    String NEW_XSD2XMI_MAP_FILE = "New Xsd To Xmi Mapping (GME)";

    String OPEN_MAP_FILE_TXT = NEW_MAP_FILE_TXT;
	String OPEN_MAP_FILE = "Open HL7 v2 to HL7 v3 " + OPEN_MAP_FILE_TXT;
	String OPEN_O2DB_MAP_FILE_TXT = "Object Model to Data Model Map Specification";
	String OPEN_CSV2SDTM_MAP_FILE_TXT = "CSV/Database to RDS Map Specification";
	String OPEN_O2DB_MAP_FILE = "Open " + OPEN_O2DB_MAP_FILE_TXT;
	String OPEN_CSV2XMI_MAP_FILE = "Open CSV Meta To Object/Data Model Mapping";
    String OPEN_XSD2XMI_MAP_FILE = "Open XSD Meta To XMI Mapping";
    String OPEN_CSV_SPEC_TXT = NEW_CSV_SPEC_TXT;
	String OPEN_CSV_SPEC = "Open " + OPEN_CSV_SPEC_TXT;
	String OPEN_HL7_V3_MESSAGE_TXT = NEW_HL7_V3_MESSAGE_TXT;
	String OPEN_HL7_V3_MESSAGE = "Open " + OPEN_HL7_V3_MESSAGE_TXT;
	String OPEN_HSM_FILE_TXT = NEW_HSM_FILE_TXT;
	String OPEN_HSM_FILE = "Open " + OPEN_HSM_FILE_TXT +"(.h3s)";
	String OPEN_XML_HSM_FILE = "Open " + OPEN_HSM_FILE_TXT +"(.xml)";

	String OPEN = "Open...";
	String CLOSE = "Close";
	String CLOSE_ALL = "Close All";
	String SAVE = "Save";
	String SAVE_AS = "Save As...";
	String EXIT = "Exit";
	String ABOUT = "About " + Config.PRODUCT_NAME + "...";
	String HELP_TOPIC = "HELP_TOPIC";

	String HELP = "Help - Contents and Index...";
	//String HELP_MANAGER = "Help Mgr";
	//String HELP_MANAGER2 = "Help Content Manager";   // For Tool Tip

	String GENERATE_REPORT = "Generate Report...";
	String VALIDATE = "Validate";
	String ANOTATE = "Anotate";
	String REFRESH = "Refresh";
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.8  2008/02/04 15:08:49  schroedn
 * HISTORY      : XSD to XMI Mapping - GME initial
 * HISTORY      :
 * HISTORY      : Revision 1.7  2007/12/13 15:28:14  wangeug
 * HISTORY      : support both data model and object model
 * HISTORY      :
 * HISTORY      : Revision 1.6  2007/11/29 16:47:22  wangeug
 * HISTORY      : create CSV_TO_XMI mapping module
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/08/30 19:55:33  jayannah
 * HISTORY      : changed the verbage
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/08/13 15:22:08  wangeug
 * HISTORY      : add new constants :open_dialog_tile_hsm_xml
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/08/08 21:13:47  jayannah
 * HISTORY      : Changed the verbage from SDTM to RDS
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/06/12 15:57:07  wangeug
 * HISTORY      : enable new module: HL7 V3 to CSV Transformation
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.28  2006/12/06 17:54:08  wuye
 * HISTORY      : Updated menu for object model to data model mapping menu
 * HISTORY      :
 * HISTORY      : Revision 1.27  2006/11/27 20:39:44  jayannah
 * HISTORY      : Changes to fix the reported bugs
 * HISTORY      :
 * HISTORY      : Revision 1.26  2006/11/10 14:51:59  wuye
 * HISTORY      : Change DB to Database
 * HISTORY      :
 * HISTORY      : Revision 1.25  2006/11/07 15:00:17  jayannah
 * HISTORY      : added a new constant for the open SDTM map specification
 * HISTORY      :
 * HISTORY      : Revision 1.24  2006/10/30 16:30:15  wuye
 * HISTORY      : Modified the Menu structure
 * HISTORY      :
 * HISTORY      : Revision 1.23  2006/10/23 16:23:30  wuye
 * HISTORY      : Add constant for O-2-DB mapping
 * HISTORY      :
 * HISTORY      : Revision 1.22  2006/09/26 15:57:49  wuye
 * HISTORY      : Update with new menu actions
 * HISTORY      :
 * HISTORY      : Revision 1.21  2006/08/02 18:44:20  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.20  2006/01/25 16:45:48  chene
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.19  2006/01/25 16:33:46  chene
 * HISTORY      : Change the Help tooltip
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/01/03 18:26:16  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/01 20:03:39  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/11/16 16:29:42  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/11/15 19:39:47  jiangsc
 * HISTORY      : Changed HL7 to caAdapter
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/10/26 17:21:09  umkis
 * HISTORY      : #156
 * HISTORY      : a)Help manager Menu is droped down
 * HISTORY      : b) Change the "About HL7SDK..." option to "About caAdapter..."
 * HISTORY      : c) Change the "Help..." option to "Contents and Index..."
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/12 17:21:55  jiangsc
 * HISTORY      : Constant value changed.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/10/04 15:30:03  jiangsc
 * HISTORY      : Added VALIDATE constant.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/09/29 21:13:56  jiangsc
 * HISTORY      : Added Generate Report action support
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/09/12 21:45:04  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/09/07 22:27:37  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/02 22:23:09  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/27 22:41:16  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/27 13:57:42  jiangsc
 * HISTORY      : Added the first round of HSMPanel.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/15 18:58:28  jiangsc
 * HISTORY      : 1) Reconstucted Menu bars;
 * HISTORY      : 2) Integrated FunctionPane to display property;
 * HISTORY      : 3) Enabled drag and drop functions to mapping panel.
 * HISTORY      :
 * HISTORY      : Add The Constant of HELP_MANAGER by Kisung 09/12/05
 * HISTORY      : Add The Constant of HELP_MANAGER2 by Kisung 09/12/05
 */
