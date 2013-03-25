/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.constants;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 15, 2010
 * Time: 9:59:31 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ActionConstants
{
	//The naming convention:
	//NEW_MAP_FILE is used as the key to the text, while the NEW_MAP_FILE_TXT is used as the action name;
	//this is because the action name in display may be the same but we really need to distinguish them in a map
	//between a new and an open command.

	String OPEN = "Open...";
	String CLOSE = "Close";
	String CLOSE_ALL = "Close All";
	String SAVE = "Save";
	String SAVE_AS = "Save As...";
	String EXIT = "Exit";
	String HELP_TOPIC = "HELP_TOPIC";

    String CALCULATE = "Calculate";

    String HELP = "Help - Contents and Index...";
	String GENERATE_REPORT = "Generate Report...";
	String VALIDATE = "Validate";
	String ANOTATE = "Anotate";
	String REFRESH = "Refresh";
	String NEW_MAP_FILE = "Transformation Mapping";
	String OPEN_MAP_FILE = "Open Transformation Mapping";
	String NEW_XML_Transformation = "XML to XML Transformation";
	String NEW_CSV_Transformation = "CSV to XML Transformation";
	String NEW_HL7_V2_Transformation = "HL7 v2 to XML Transformation";
	String NEW_XML_CDA_Transformation = "XML to CDA Transformation";
	String NEW_CSV_CDA_Transformation = "CSV to CDA Transformation";
	String NEW_HL7_V2_CDA_Transformation = "HL7 v2 to CDA Transformation";
    String FORMULA_FILE_EXTENSION = ".xml";
    String TEMPORARY_FILE_PREFIX = "TEMPcaAdapter";
    String TEMPORARY_FILE_EXTENSION = ".tmp";
    String CAADAPTER_HOME_DIR_TAG = "{caAdapter_Home}";

}
