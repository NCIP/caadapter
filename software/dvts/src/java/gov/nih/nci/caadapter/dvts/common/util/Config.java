/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.common.util;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 13, 2011
 * Time: 4:50:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class Config {

    	public static String PRODUCT_NAME = "caadapter-dvts";

	public static String CAADAPTER_VERSION = "V3.2";
	public static String CAADAPTER_BUILD_NUMBER = "caAdapter_3_2_Final";
    public static String JDK_VERSION = "1.5.0_06";
    public static String COPYRIGHT_YEARS = "2004-2006";
    public static String RIM_FILE = "./etc/Rim207.xml";
    public static String MIF_LOCATION = "./MIF/";
    public static String SCHEMA_LOCATION = "./schema/multicacheschemas/";
    public static String PORR_MT040001_XML_File = "./xml/PORR_MT040001.xml";

    public static String FUNCTION_DEFINITION_FILE_LOCATION = "./etc/core.fls";
    //public static String VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION = "./etc/vom.xsd";
    public static String VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION = "map/functions/vom.xsd";
    public static String MAP_COMPONENT_SOURCE_TYPE = "source";
    public static String MAP_COMPONENT_TARGET_TYPE = "target";
    public static String MAP_COMPONENT_FUNCTION_TYPE = "function";
	public static String MMS_PREFIX_DATAMODEL = "cadapter.mms.prefix.datamodel";
	public static String MMS_PREFIX_OBJECTMODEL = "cadapter.mms.prefix.objectmodel";

    public static String CSV_METADATA_FILE_DEFAULT_EXTENTION = ".scs";
    public static String CSV_DATA_FILE_DEFAULT_EXTENSTION = ".csv";
    public static String SOURCE_TREE_FILE_DEFAULT_EXTENTION = ".scs";
    public static String TARGET_TREE_FILE_DEFAULT_EXTENTION = ".h3s";
    public static String MAP_FILE_DEFAULT_EXTENTION = ".map";
    public static String HSM_META_DEFINITION_FILE_DEFAULT_EXTENSION = ".h3s";
    public static String FUNCTION_DEFINITION_FILE_DEFAULT_EXTENSION = ".fls";
    public static String HL7_V3_MESSAGE_FILE_DEFAULT_EXTENSION = ".xml";
    public static String DATABASE_META_FILE_DEFAULT_EXTENSION = ".dbm";
    public static String REPORT_FILE_DEFAULT_EXTENSION = ".xls";
    public static String VALIDATION_RESULT_SAVE_FILE_EXTENSION = ".txt";
    public static String TEMPORARY_FILE_EXTENSION = ".tmp";
    public static String VOCABULARY_MAPPING_DIR = "vom";
    public static String VOCABULARY_MAPPING_FILE_EXTENSION = "." + VOCABULARY_MAPPING_DIR;
    public static String VOCABULARY_MAP_FILE_VALUE_SEPARATOR = "=>";
    public static String TEMPORARY_FILE_PREFIX = "TEMPcaAdapter";
    public static String CAADAPTER_HOME_DIR_TAG = "{caAdapter_Home}";
    public static String VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR = "?@";
    public static String VOCABULARY_MAP_FILE_NAME_DOMAIN_WILD_CHARACTER = "*.*";
    public static String VOCABULARY_MAP_URL_SEARCH_DATA_INPUT_POINT_CHARACTER = "#";
    public static String VOCABULARY_MAP_URL_CONNECTION_TEST_DATA = "!TEST";
    public static String NULL_VALUE_MARK = "%%NULL%%";

    public static String OPEN_DIALOG_TITLE_FOR_CSV_METADATA_FILE = "Open CSV Specification...";
    public static String OPEN_DIALOG_TITLE_FOR_CSV_FILE = "Open CSV File...";
    public static String OPEN_DIALOG_TITLE_FOR_DEFAULT_SOURCE_FILE = "Open Source File...";
    public static String OPEN_DIALOG_TITLE_FOR_DEFAULT_TARGET_FILE = "Open Target File...";
    public static String OPEN_DIALOG_TITLE_FOR_DEFAULT_HL7_META_DEFINITION_FILE = "Open HL7 Metadata Definition (HMD) File...";
    public static String OPEN_DIALOG_TITLE_FOR_MAP_FILE = "Open Map File...";
    public static String OPEN_DIALOG_TITLE_FOR_HSM_FILE = "Open HL7 V3 Specification (H3S) File...";
    public static String OPEN_DIALOG_TITLE_FOR_HL7_V3_MESSAGE_FILE = "Open HL7 V3 Message File...";


//	public static String DEFAULT_FIELD_COLUMN_START_NUMBER" type="int" value="1";
//
    public static int FRAME_DEFAULT_WIDTH = 800;
    public static int FRAME_DEFAULT_HEIGHT = 600;
//
    public static int DEFAULT_DIVIDER_SIZE = 6;
//
//    public static String DEFAULT_READ_ONLY_BACK_GROUND_COLOR" type="Color" value="Color.LIGHT_GRAY";


    public static String DEFAULT_HELP_DATA_FILENAME = "help.dat";
    public static String DEFAULT_ABOUT_WINDOW_DATA_FILENAME = "aboutwin.html";
    public static String HELP_TEMPORARY_FILENAME_FIRST = "tempz89.html";
    public static String HELP_TEMPORARY_FILENAME_SECOND = "tempz90.html";


    public static String DEFAULT_SCREEN_IMAGE_FILENAME = "default_scr.gif";
    public static String ABOUT_WINDOW_BACKGROUND_IMAGE_FILENAME = "aboutwin.png";
    public static String SPLASH_WINDOW_IMAGE_FILENAME = "splash00.png";


    public static String COMMON_METADATA_DISPLAY_NAME = "Specification";
	public static String MAP_MODULE_NAME = "Map Specification";
	public static String CSV_MODULE_NAME = "CSV Specification";
	public static String HL7_V3_METADATA_MODULE_NAME = "HL7 V3 Specification";
	public static String HL7_V3_MESSAGE_MODULE_NAME = "HL7 V3 Message";
   public static String HL7_V3_TO_CSV_MODULE_NAME = "HL7 V3 To CSV";
    public static String FUNCTION_DEFINITION_DEFAULT_KIND = "core";
    public static String CSV_DEFINITION_DEFAULT_KIND = "scs";
    public static String HL7_V3_DEFINITION_DEFAULT_KIND = "HL7v3";
    public static String DATABASE_DEFINITION_DEFAULT_KIND = "dbs";


//    public static String DEFAULT_PAPER_SIZE_X_IN_INCH" type="double" value="8.5";
//    public static String DEFAULT_PAPER_SIZE_Y_IN_INCH" type="double" value="11.0";
//    public static String DEFAULT_PAPER_TOP_MARGIN_IN_INCH" type="double" value="0.75";
//    public static String DEFAULT_PAPER_BOTTOM_MARGIN_IN_INCH" type="double" value="0.5";
//    public static String DEFAULT_PAPER_LEFT_MARGIN_IN_INCH" type="double" value="0.75";
//    public static String DEFAULT_PAPER_RIGHT_MARGIN_IN_INCH" type="double" value="0.5";
//
//	public static String DEFAULT_TRANSFORMATION_SERVICE_THRESHOLD_FOR_USER_CONFIRMATION" type="long" value="120";
//	public static String DEFAULT_THRESHOLD_FOR_SECOND_VS_MINUTE" type="long" value="60";
//	public static String DEFAULT_SYNC_VS_ASYNC_THRESHOLD" type="long" value="10";
//
//
	public static String EVS_PRODUCTION_URL = "http://cabio.nci.nih.gov/cacore31/http/remoteService";
	public static String EVS_STAGING_URL = "http://cabio-stage.nci.nih.gov/cacore31/http/remoteService";
	public static String EVS_QA_URL = "http://cbioqa101.nci.nih.gov:49080/cacore31/http/remoteService";

	public static String EVS_DEFAULT_CONNECTION = "http://cabio-stage.nci.nih.gov/cacore31/http/remoteService";

    public static String SUFFIX_OF_CHOICE_CARDINALITY = "&lt;Choice&gt;";
    public static String CARDINALITY_ZERO_TO_ONE = "0..1";
    public static String CARDINALITY_ZERO_TO_MANY = "0..*";
    public static String CARDINALITY_ONE_TO_ONE = "1..1";
    public static String CARDINALITY_ONE_TO_MANY = "1..*";
    public static String CHOICE_CARDINALITY_ZERO_TO_ONE = "0..1&lt;Choice&gt;";
    public static String CHOICE_CARDINALITY_ZERO_TO_MANY = "0..*&lt;Choice&gt;";
    public static String CHOICE_CARDINALITY_ONE_TO_ONE = "1..1&lt;Choice&gt;";
    public static String CHOICE_CARDINALITY_ONE_TO_MANY = "1..*&lt;Choice&gt;";

    public static String DEFAULT_XPATH_LEVEL_DELIMITER = "\\";

    public static String LINKED_URL_FOR_SUPPORT = "http://ncicb.nci.nih.gov/NCICB/support";
    public static String LINKED_URL_FOR_MORE_INFORMATION = "http://ncicb.nci.nih.gov/NCICB/infrastructure/cacore_overview/caadapter";


    public static String COMPONENT_PROPERTY_FILE_NAME = "caadapter-components.properties";
    public static String V3_XSD_FILE_PATH_TAG = "v3xsdFilePath";
    public static String V3_XSD_MULTI_CACHE_SCHEMAS_DIRECTORY_NAME = "multicacheschemas";
    public static String V3_XSD_CORE_SCHEMAS_DIRECTORY_NAME = "coreschemas";

    public static String CONTEXT_LINK_ADDRESS_PROPERTY_FILE_NAME ="contextLinkAddress.properties";



    //public Config() { /* compiled code */ }
}