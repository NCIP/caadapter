/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.util;

import gov.nih.nci.caadapter.common.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * HL7 v3 Related utility class.
 *
 * @author OWNER: Eric Chen  Date: Jun 4, 2005
 * @author LAST UPDATE: $Author: linc $
 * @version $Revision: 1.24 $
 * @date $$Date: 2008-09-10 18:08:14 $
 * @since caAdapter v1.2
 */

public class CaadapterUtil {
	private static ArrayList<String> ACTIVATED_CAADAPTER_COMPONENTS =new ArrayList<String>();
	private static ArrayList<String> INLINETEXT_ATTRIBUTES =new ArrayList<String>();
	private static ArrayList<String> MANDATORY_SELECTED_ATTRIBUTES =new ArrayList<String>();
//	private static ArrayList<String> RESOURCE_REQUIRED =new ArrayList<String>();
	private static HashMap<String, ArrayList<String>> RESOURCE_MODULE_REQUIRED =new HashMap<String, ArrayList<String>>();
	private static HashMap prefs;
	private static HashMap appConfig;
	private static boolean authorizedUser=false;
	public static final String  LINEFEED_ENCODE="&#x0A;";//html format of UTF-8 unicode value of '\n'
	public static final String  CARTRIAGE_RETURN_ENCODE="&#x0D;";//html format of UTF-8 unicode value of '\r'
	public static String HL7_MIF_FILE_PATH;
	public static String getHL7_MIF_FILE_PATH() {
		return HL7_MIF_FILE_PATH;
	}

	static {
		//mkdir for logging
		File logDir=new File("log");
		if (!logDir.exists())
			logDir.mkdir();

        InputStream fi = null;
        appConfig=new HashMap();
        //load caadapter component types to run
        Properties properties=new Properties();
        try {

        	File srcFile=new  File("conf/caadapter-components.properties");
        	if (srcFile.exists())
        	{
        		fi =new FileInputStream(srcFile);
        	}
        	else
        		fi = CaadapterUtil.class.getClassLoader().getResource("caadapter-components.properties").openStream();
        	properties.load(fi);
            if (properties != null) {
            	//read the value for each component and add it into the ActivatedList
            	Enumeration propKeys=properties.keys();
            	while (propKeys.hasMoreElements())
            	{
            		String onePropKey=(String)propKeys.nextElement();
            		String onePropValue=(String)properties.getProperty(onePropKey);
            		appConfig.put(onePropKey, onePropValue);
            		if (onePropValue!=null&onePropValue.trim().equalsIgnoreCase("true"))
            		{
            			ACTIVATED_CAADAPTER_COMPONENTS.add(onePropKey);
            		}
            	}

            	//disable all components except MMS
    			{
    				ACTIVATED_CAADAPTER_COMPONENTS.clear();
    				ACTIVATED_CAADAPTER_COMPONENTS.add(Config.CAADAPTER_COMPONENT_MODEL_MAPPING_ACTIVATED);
    				ACTIVATED_CAADAPTER_COMPONENTS.add(Config.CAADAPTER_HELP_MENU_ACTIVATED);
    			}
             }
            //load MIF file path
            HL7_MIF_FILE_PATH=(String)properties.getProperty("caadapter.hl7.mif.path");

            //load datatypes require inlineText
            String inlineTextTypes=(String)properties.getProperty(Config.CAADAPTER_COMPONENT_HL7_SPECFICATION_ATTRIBUTE_INLINETEXT_REQUIRED);
            if (inlineTextTypes!=null)
            {
            	StringTokenizer tk=new StringTokenizer(inlineTextTypes, ",");
            	while(tk.hasMoreElements())
            		INLINETEXT_ATTRIBUTES.add((String)tk.nextElement());
            }

            String mandatorySelectedAttributes=(String)properties.getProperty(Config.CAADAPTER_COMPONENT_HL7_SPECFICATION_ATTRIBUTE_MANDATORY_SELECTED);
            if (mandatorySelectedAttributes!=null)
            {
            	StringTokenizer tk=new StringTokenizer(mandatorySelectedAttributes, ",");
            	while(tk.hasMoreElements())
            		MANDATORY_SELECTED_ATTRIBUTES.add((String)tk.nextElement());
            }
            fi.close();
            Properties rsrcProp=new Properties();
            fi = CaadapterUtil.class.getClassLoader().getResource("caadapter-resources.properties").openStream();
            rsrcProp.load(fi);
            readResourceRequired(rsrcProp);

        } catch (Exception ex) {
            Log.logException(CaadapterUtil.class, "caadapter-components.properties is not found", ex);
        } finally {
            if (fi != null) try {
                fi.close();
            } catch (IOException ignore) {
            }
        }
        readPreferencesMap();
    }


	private static void readResourceRequired(Properties prop)
	{
		//it is not required for webstart installation
		if (ACTIVATED_CAADAPTER_COMPONENTS.contains(Config.CAADAPTER_COMPONENT_WEBSTART_ACTIVATED))
			return;
		//always add common required
		addModuleResource(Config.CAADAPTER_COMMON_RESOURCE_REQUIRED,prop);
		//no additional resource is required except the common ones
		if (ACTIVATED_CAADAPTER_COMPONENTS.contains(Config.CAADAPTER_CSV_XMI_MENU_ACTIVATED))
			return;

		if (ACTIVATED_CAADAPTER_COMPONENTS.contains(Config.CAADAPTER_COMPONENT_HL7_TRANSFORMATION_ACTIVATED))
			addModuleResource(Config.CAADAPTER_HL7_TRANSFORMATION_RESOURCE_REQUIRED,prop);
		if (ACTIVATED_CAADAPTER_COMPONENTS.contains(Config.CAADAPTER_COMPONENT_HL7_V2V3_CONVERSION_ACTIVATED))
			addModuleResource(Config.CAADAPTER_HL7_V2V3_CONVERSION_RESOURCE_REQUIRED,prop);
		if (ACTIVATED_CAADAPTER_COMPONENTS.contains(Config.CAADAPTER_QUERYBUILDER_MENU_ACTIVATED))
			addModuleResource(Config.CAADAPTER_QUERYBUILDER_RESOURCE_REQUIRED,prop);

	}
	private static void addModuleResource(String moduleName, Properties prop)
	{
		ArrayList moduleRsc=RESOURCE_MODULE_REQUIRED.get(moduleName);
		if (moduleRsc==null)
			moduleRsc=new ArrayList<String>();
		String rsrcList=prop.getProperty(moduleName);
		StringTokenizer tk=new StringTokenizer(rsrcList, ",");
    	while(tk.hasMoreElements())
    	{
    		String nxtRsc=(String)tk.nextElement();
    		if (!moduleRsc.contains(nxtRsc))
    			moduleRsc.add(nxtRsc);
    	}
    	RESOURCE_MODULE_REQUIRED.put(moduleName, moduleRsc);
	}


    // getters.
    public static final ArrayList getInlineTextAttributes() {
        return INLINETEXT_ATTRIBUTES;
    }
    public static final ArrayList getAllActivatedComponents() {
        return ACTIVATED_CAADAPTER_COMPONENTS;
    }

    public static final ArrayList getMandatorySelectedAttributes() {
        return MANDATORY_SELECTED_ATTRIBUTES;
    }
    public static ArrayList getAllResourceRequired()
    {
    	ArrayList<String>rtnList=new ArrayList<String>();
    	Set<String> keySet=RESOURCE_MODULE_REQUIRED.keySet();
    	for(String keyValue:keySet)
    	{
    		ArrayList<String> modRsc=RESOURCE_MODULE_REQUIRED.get(keyValue);
    		for (String rsrc:modRsc)
        	{
        		if (!rtnList.contains(rsrc))
        			rtnList.add(rsrc);
        	}
    	}
    	return rtnList;
    }

    public static ArrayList<String> getModuleResourceMissed(String moduleName)
    {
    	ArrayList <String> missRsrc=new ArrayList<String>();
    	File libFile=new File("lib");
    	if (!libFile.exists()||!libFile.isDirectory())
    		return missRsrc;
    	String[] libRsrc=libFile.list();
    	List<String>  foundRsrc=Arrays.asList(libRsrc);
    	ArrayList<String> requiredRsc;
    	if (moduleName==null|moduleName.equals(""))
    		requiredRsc=getAllResourceRequired();
    	else
    		requiredRsc=RESOURCE_MODULE_REQUIRED.get(moduleName);

    	if (requiredRsc!=null)
	    	for (String rsrc:requiredRsc)
	    	{
	    		if (!foundRsrc.contains(rsrc))
	    			missRsrc.add(rsrc);
	    	}
    	//always include the common resource list
    	if (!moduleName.equals(Config.CAADAPTER_COMMON_RESOURCE_REQUIRED))
    	{
    		requiredRsc=RESOURCE_MODULE_REQUIRED.get(Config.CAADAPTER_COMMON_RESOURCE_REQUIRED);
    		if (requiredRsc!=null)
	    		for (String rsrc:requiredRsc)
	        	{
	        		if (!foundRsrc.contains(rsrc)
	        				&&!missRsrc.contains(rsrc))
	        			missRsrc.add(rsrc);
	        	}
    	}
    	return missRsrc;
    }

     /**
      * Move this method from GeneralUtilities
      * @param objectArray
      * @param separator
      * @return
      */
         public static String join(Object[] objectArray, String separator){
             if (objectArray == null) return null;
             String[] stringArray = new String[objectArray.length];

             for (int i = 0; i < objectArray.length; i++) {
                 stringArray[i] = "" + objectArray[i];
             }
             return join(stringArray, separator);
         }

         //copied the following method from javaSIG source code
         //decouple the dependence of "common" component from javaSIG
         /**
          * Joins a string array into one string with <code>separator</code>
          * between elements.
          *
          * @param as  array of tokens to join
          * @param separator  separator character
          * @return  the joined string
          */
         private static String join(String[] as, String separator)
         {
           if (as == null) return null;
           else if (as.length == 1) return as[0];
           else
           {
             StringBuffer sb = new StringBuffer();
             for (int i = 0; i < as.length; ++i)
             {
               if (i > 0) sb.append(separator);
               sb.append(as[i]);
             }
             return sb.toString();
           }
         }

         private static void readPreferencesMap()
         {   prefs=new HashMap();
             try
             {
                 FileInputStream f_out = new FileInputStream(System.getProperty("user.home") + "\\.caadapter");
                 ObjectInputStream obj_out = new ObjectInputStream(f_out);
                 prefs = (HashMap) obj_out.readObject();
                 //System.out.println(prefs);
             } catch (Exception e)
             {
                 e.printStackTrace();
             }
         }

         public static HashMap getCaAdapterPreferences()
         {
             return prefs;
         }

         public static void setCaAdapterPreferences(HashMap _prefs){
             prefs = _prefs;
         }

         /**
          * Read a preference value given its key as a string
          *
          * @param key -- The key value of a preference
          * @return A preference value as a string
          */
         public static String readPrefParams(String key) {
             HashMap prefs = CaadapterUtil.getCaAdapterPreferences();
             if (prefs == null)
                 return null;
             return (String) prefs.get(key);
         }

         public static void savePrefParams(String key, String value) {
             try {
                 if (CaadapterUtil.getCaAdapterPreferences() != null) {
                 	CaadapterUtil.getCaAdapterPreferences().put(key, value);
                 } else {
                     HashMap tempMap = new HashMap();
                     tempMap.put(key, value);
                     CaadapterUtil.setCaAdapterPreferences(tempMap);
                 }
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }

		public static boolean isAuthorizedUser() {
			return authorizedUser;
		}

		public static void setAuthorizedUser(boolean authorizedUser) {
			CaadapterUtil.authorizedUser = authorizedUser;
		}

		public static String findApplicationConfigValue(String configKey)
		{
			return (String)appConfig.get(configKey);
		}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.23  2008/09/09 18:29:44  wangeug
 * HISTORY      : support HL7 normative 2008, MIF format release 1
 * HISTORY      :
 * HISTORY      : Revision 1.20  2008/06/09 19:53:50  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.19  2008/03/12 20:33:29  wangeug
 * HISTORY      : annotate GME_XMLNamespace at project level
 * HISTORY      :
 * HISTORY      : Revision 1.18  2008/02/27 14:47:48  wangeug
 * HISTORY      : add new constants
 * HISTORY      :
 * HISTORY      : Revision 1.17  2007/12/20 18:10:19  wangeug
 * HISTORY      : validate resource required
 * HISTORY      :
 * HISTORY      : Revision 1.16  2007/10/12 16:12:16  wangeug
 * HISTORY      : avoid duplicated record of missed resource
 * HISTORY      :
 * HISTORY      : Revision 1.15  2007/10/04 18:08:27  wangeug
 * HISTORY      : verify resource based on module
 * HISTORY      :
 * HISTORY      : Revision 1.14  2007/09/19 20:30:59  wangeug
 * HISTORY      : read caadapter-commponts.properties
 * HISTORY      :
 * HISTORY      : Revision 1.13  2007/09/19 16:40:14  wangeug
 * HISTORY      : load property file from conf folder
 * HISTORY      :
 * HISTORY      : Revision 1.12  2007/09/19 13:54:29  wangeug
 * HISTORY      : show missing resources
 * HISTORY      :
 * HISTORY      : Revision 1.11  2007/09/11 18:54:25  wangeug
 * HISTORY      : create log file if not exist
 * HISTORY      :
 * HISTORY      : Revision 1.10  2007/09/07 19:25:48  wangeug
 * HISTORY      : relocate readPreference and savePreference methods
 * HISTORY      :
 * HISTORY      : Revision 1.9  2007/08/28 21:44:25  wangeug
 * HISTORY      : enable address/name datatype with pre-set attributes
 * HISTORY      :
 * HISTORY      : Revision 1.8  2007/08/24 21:13:54  wangeug
 * HISTORY      : clean code
 * HISTORY      :
 * HISTORY      : Revision 1.7  2007/08/15 17:54:23  wangeug
 * HISTORY      : remove property file "message-types.properties"
 * HISTORY      :
 * HISTORY      : Revision 1.6  2007/07/09 13:59:26  wangeug
 * HISTORY      : initila loading hl7 code without "clone"
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/06/12 14:58:20  wuye
 * HISTORY      : change the getsystemresouce as getresource.openinputstream
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/06/08 19:49:26  schroedn
 * HISTORY      : Edits to sync with new codebase and java webstart
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/06/07 15:32:12  schroedn
 * HISTORY      : Edits to sync with new codebase and java webstart
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/04/19 13:56:54  wangeug
 * HISTORY      : rename TransformationService to TransformationServiceCsvToHL7V3
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/08/02 18:44:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/06/02 22:18:50  chene
 * HISTORY      : 040002 improvement
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/05/03 21:26:43  chene
 * HISTORY      : Saved Point
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/01/12 22:20:23  chene
 * HISTORY      : caAdapter generic enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/04 18:12:57  giordanm
 * HISTORY      : remove some of the hard coded values from CaadapterUtil and TransformationServiceCsvToHL7V3 - extract some of that logic to the message-types.properties file.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 17:12:23  chene
 * HISTORY      : Messages types changed from from Code config to property files
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/30 16:12:55  chene
 * HISTORY      : Update JavaDoc
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/09/30 19:56:30  chene
 * HISTORY      : User Defined Structure Supported
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/09/08 19:37:04  chene
 * HISTORY      : Saved point
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/22 17:32:45  giordanm
 * HISTORY      : change the file attribute within BaseComponent from a String to a File,  this checkin also contains some refactor work to the FileUtil.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/07 16:40:44  chene
 * HISTORY      : Fixed message type error
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/06 21:46:12  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/05 16:25:46  jiangsc
 * HISTORY      : Added new Functionality.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/06/05 04:22:47  chene
 * HISTORY      : Firstcut of Meta Object Parser
 * HISTORY      :
 */
