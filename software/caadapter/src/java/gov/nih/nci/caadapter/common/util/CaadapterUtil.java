/*L
 * Copyright SAIC.
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
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * @author OWNER: Eugene Wang  Date: September 24, 2008
 * @author LAST UPDATE: $Author: altturbo $
 * @version $Revision: 1.3 $
 * @date $$Date: 2009-06-22 23:04:25 $
 * @since caAdapter v4.2
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
    private static String NAME_COMPONENT_PROPERTY_FILE = Config.COMPONENT_PROPERTY_FILE_NAME;
    private static String PATH_COMPONENT_PROPERTY_FILE = "conf" + File.separator + NAME_COMPONENT_PROPERTY_FILE;

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
            {
                String path = FileUtil.searchFile("conf/caadapter-components.properties");

                if (path != null) fi =new FileInputStream(path);
                else fi = CaadapterUtil.class.getClassLoader().getResource("caadapter-components.properties").openStream();
            }

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

    public static String getPathOfComponentPropertyFile()
	{
	    return PATH_COMPONENT_PROPERTY_FILE;
	}
	public static String getNameOfComponentPropertyFile()
	{
	    return NAME_COMPONENT_PROPERTY_FILE;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/12/19 22:24:58  umkis
 * HISTORY      : add getPathOfComponentPropertyFile() and getNameOfComponentPropertyFile()
 * HISTORY      :
 * HISTORY      : Revision 1.1  2008/09/24 15:16:51  wangeug
 * HISTORY      : Keep local copy of the CaadapterUtil.java
 * HISTORY      :
 *
 */