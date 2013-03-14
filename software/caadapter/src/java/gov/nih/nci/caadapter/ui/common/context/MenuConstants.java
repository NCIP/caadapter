/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */
package gov.nih.nci.caadapter.ui.common.context;

/**
 * This class defines a list of menu names that will be referenced across the system.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public interface MenuConstants
{
	// the menu names.
	String FILE_MENU_NAME = "File";
	String NEW_MENU_NAME = "New";
	String OPEN_MENU_NAME = "Open";
	String REPORT_MENU_NAME = "Report";
	String HELP_MENU_NAME = "Help";
	String TOOLBAR_MENU_NAME = "ToolBar";
	//Module Name
	String CSV_TO_HL7V3="CSV_TO_HL7V3";
	String CSV_SPEC="CSV_SPEC";
    String XSD_SPEC="XSD_SPEC";
    String HSM_FILE="HSM_FILE";
	String HL7_V3_MESSAGE="HL7_V3_MESSAGE";
	String HL7_V2_TO_V3="HL7_V2_TO_V3";
	String SDTM_TO_HL7V3="SDTM_TO_HL7V3";
	String DB_TO_SDTM="DB_TO_SDTM";
	String DB_TO_OBJECT="DB_TO_OBJECT";
	String CSV_TO_XMI="CSV_TO_XMI";
    String XSD_TO_XMI="XSD_TO_XMI";

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2008/02/04 15:09:07  schroedn
 * HISTORY      : XSD to XMI Mapping - GME initial
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/11/30 14:38:57  wangeug
 * HISTORY      : create CSV_TO_XMI mapping module
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/08/02 18:44:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/09/29 21:20:00  jiangsc
 * HISTORY      : Added Generate Report action support
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/04 22:22:27  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/15 18:58:50  jiangsc
 * HISTORY      : 1) Reconstucted Menu bars;
 * HISTORY      : 2) Integrated FunctionPane to display property;
 * HISTORY      : 3) Enabled drag and drop functions to mapping panel.
 * HISTORY      :
 */
