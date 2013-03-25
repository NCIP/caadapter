/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.function;

import gov.nih.nci.caadapter.common.MetaException;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.function.meta.GroupMeta;
import gov.nih.nci.caadapter.common.function.meta.ParameterMeta;
import gov.nih.nci.caadapter.common.util.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the controller class of Middle Panel JGraph implementation.
 * <p/>
 * The MiddlePanelJGraphController class will deal with real implementation of some of actions
 * to modify (mainly CRUD) upon graph, and mainly focuses on drag-and-drop and handlings of repaint of graph, for example.
 * MiddlePanelMarqueeHandler will help handle key and mouse driven events such as display pop menus, etc.
 *
 * @author OWNER: Jayfus Doswell
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:53:49 $
 */
public class FunctionManager
{
    private static final FunctionManager instance = new FunctionManager();
    private List<FunctionMeta> lstFunctionMeta = null;   //Stores a functionMataImpl object
    private List<GroupMeta> lstGroupMeta = null;

	public static final FunctionManager getInstance()
	{
		return instance;
	}

    //Build the FunctionMetaImpl object
    private FunctionManager()
    {
        FunctionParser fp = null;                   //Stores a FunctionParser object.
        File fleFunctionSpec = null;                //Stores a Function Specification from a file.
        Reader flrFunctionSpecReader = null;    //Stores a Functional Spec as a character streamn.

        fp = new FunctionParser();                  //Instantiate a FunctionParser object.

        try
        {
            //Create new Function Specification file.
//        	URL licenseURL= ClassLoader.getSystemResource(Config.FUNCTION_DEFINITION_FILE_LOCATION);
//        	String filePath=licenseURL.getFile();
//        	System.out.println("function spec file Path:"+filePath);
//            fleFunctionSpec = new File(filePath);
//            flrFunctionSpecReader = new FileReader(fleFunctionSpec);
        	flrFunctionSpecReader=new InputStreamReader( Thread.currentThread().getContextClassLoader().getResourceAsStream(Config.FUNCTION_DEFINITION_FILE_LOCATION) );
        }
        //@todo send exceptions into log files.

        //Catch if the file is not found
//        catch (FileNotFoundException eFileNotFound)
//        {
//            eFileNotFound.printStackTrace();
//        }

        //Catch is file parameter is empty.
        catch (NullPointerException eNullPointer)
        {
            eNullPointer.printStackTrace();
        }

        try
        {
            //Parse Function specification file and transfer data to a GroupMetaImpl object.
            lstGroupMeta = fp.parse(flrFunctionSpecReader, Config.FUNCTION_DEFINITION_DEFAULT_KIND);

            //if (((lstGroupMeta.size())<0) || (lstGroupMeta == null)) throw new FunctionException("Group is empty", null);

            //Validate the FunctionMetaImpl object
            //@todo Create a FunctionValidate class with other validation rules.
           /*
            for (int i=0;i<lstFunctionMeta.size(); i++)
            {
                if(lstFunctionMeta.get(i).getSizeOfDefinedInput()==0)
                {
                     System.out.println("The Input for this function has not yet been defined");
                }
                if(lstFunctionMeta.get(i).getSizeOfDefinedOutput()==0)
                {
                     System.out.println("The Output for this function has not yet been defined");
                }

            }
             */

        }

        catch (MetaException eMeta)
        {
            eMeta.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    /**
     * Returns a GroupMeta Object containing a complete set of Function Groups.
     *
     * @return A GroupMeta object list
     */

    public List<GroupMeta> getGroupList()
    {
        return lstGroupMeta;
    }

       /**
         * Returns a GroupMeta Object containing a Function Group that matches the strGroupSearchName.
         *
         * @return A GroupMeta object
         */

        public GroupMeta getGroupByName(String strGroupSearchName)
        {
           GroupMeta objGroupMeta = null;  //Stores the mathing groupMeta object

           //Search through the list of GroupMeta objects and find a match.
           for (int i=0;i<lstGroupMeta.size();++i)
           {
               String strGroupName = lstGroupMeta.get(i).getGroupName();
               if (strGroupName.equalsIgnoreCase(strGroupSearchName))
               {
                   objGroupMeta = lstGroupMeta.get(i);
                   break;  //Found GroupName
               }

           }
           return objGroupMeta;
       }



    /**
     * Find a FunctionMeta object based on kind, group and name.
     *
     * @param kind  For example "core" as defined in Config.FUNCTION_DEFINITION_DEFAULT_KIND
     * @param strGroup For example "string"
     * @param strFunction For example "concat"
     * @return A FunctionMeta object
     */
    public FunctionMeta getFunctionMeta(String kind, String strGroup, String strFunction) //throws FunctionException
    {
        List<FunctionMeta> lstFunctionMetas = null;
        FunctionMeta objSelectedFunctionMeta = null;

        // right now "core" is the only one supported - TODO : this needs to be fixed.

        //Find matching group name.
        for (int i=0;i<lstGroupMeta.size();++i)
        {
            String strGroupName = lstGroupMeta.get(i).getGroupName();
            if (strGroupName.equalsIgnoreCase(strGroup))
            {
                lstFunctionMetas = lstGroupMeta.get(i).getFunctionList();

                for (int j=0;j<lstFunctionMetas.size();j++)
                {
                    String strFunctionName = lstFunctionMetas.get(j).getFunctionName();
                    if (strFunctionName.equalsIgnoreCase(strFunction))
                    {
                        objSelectedFunctionMeta = lstFunctionMetas.get(j);
                        break;
                        //if(lstFunctionMetas.size()<1)throw new FunctionException("function could not be found : " + name, null);
                        //if(lstFunctionMetas.size()>1)throw new FunctionException("more than one function found for : " + name, null)

                    }

                }
            }

        }
        return objSelectedFunctionMeta;
    }



    /**
        * Get a FunctionMeta list based on a group name.
        *
        * @param strGroupSearchName Group Name to search
        * @return A FunctionMeta object
        */

    //Get the FunctionMetaImpl object containing a list of function definitions.
    public List<FunctionMeta> getFunctionList(String strGroupSearchName)
    {
        List<FunctionMeta> lstFunctionMetas = null;

        //Get Functionlist from the mathing GroupName.
        for (int i=0;i<lstGroupMeta.size();++i)
        {
             String strGroupName = lstGroupMeta.get(i).getGroupName();
             if (strGroupName.equalsIgnoreCase(strGroupSearchName))
             {
                 lstFunctionMetas = lstGroupMeta.get(i).getFunctionList();
                 break;
             }
         }
         return lstFunctionMetas;
    }

    //Get the FunctionMetaImpl object containing a set of function definitions with the same name.
    public List<FunctionMeta> getFunctionByName(String strFunctionSearchName)
    {
        ArrayList lstFunctionMetaByName = null;  //Stores list of FunctionMetaImpl objects of the same name.
        String strFunctionName = null;           //Stores the current function name of the FunctionMeta object.

        lstFunctionMetaByName = new ArrayList();
        List<FunctionMeta> lstFunctionMeta = null;

        //Search through the list of groups
        for(int i=0;i<lstGroupMeta.size();i++)
        {
          lstFunctionMeta = lstGroupMeta.get(i).getFunctionList();

            //Search through the list of FunctionMetaImpl objects for the name that matches what's stored in strFunctionSearchName.
            for (int j=0;j<lstFunctionMeta.size();j++)
            {
              strFunctionName = lstFunctionMeta.get(j).getFunctionName();
              if(strFunctionName.equalsIgnoreCase(strFunctionSearchName))
              {
                  lstFunctionMetaByName.add(lstFunctionMeta.get(j));
              }
          }
        }
          return lstFunctionMetaByName;   //return a FunctionMetaImpl object containing the desired subset
    }


   public List<ParameterMeta> getFunctionMappingInputList(FunctionMeta fmtSelectFunctionMeta)
   {
       List<ParameterMeta> lstInputParameterMeta = null;  //Stores a list of input ParameterMeta objects.
       lstInputParameterMeta = fmtSelectFunctionMeta.getInputDefinitionList();  //Get the inputlist from a FunctionMetaImpl object.
       return lstInputParameterMeta;
   }


   public List<ParameterMeta> getFunctionMappingOutputList(FunctionMeta fmtSelectFunctionMeta)
   {
       List<ParameterMeta> lstOutputParameterMeta = null;
       lstOutputParameterMeta = fmtSelectFunctionMeta.getOuputDefinitionList() ;  //Get the outputlist from a FunctionMetaImpl object.
       return lstOutputParameterMeta;
   }

   //getNumberofInputs
   //getNumberofOutputs

    //Checks the list of user input and output and compares it against the function specification file.
   public boolean isFunctionMappingComplete(FunctionMeta functionmeta, List userInputList, List userOutputList)
    {
        //Make separate calls to isFunctionMappingInputComplete and isFunctionMappingOuputComplete
        return isFunctionMappingInputComplete(functionmeta, userInputList) && isFunctionMappingOutputComplete(functionmeta, userOutputList);
    }

	/**
	 * Return true if the user's inputs complete the function required inputs.
	 * @param functionmeta
	 * @param userInputList
	 * @return
	 */
    public boolean isFunctionMappingInputComplete (FunctionMeta functionmeta, List userInputList)
    {
		List<ParameterMeta> listParameterMeta = functionmeta.getInputDefinitionList();
		return isListSizeEquals(listParameterMeta, userInputList);
    }


    /**
	 * Return true if the user's outputs complete the function required outputs.
	 * @param functionmeta
	 * @param userOutputList
	 * @return
	 */
    public boolean isFunctionMappingOutputComplete (FunctionMeta functionmeta, List userOutputList)
    {
		List<ParameterMeta> listParameterMeta = functionmeta.getOuputDefinitionList();
		return isListSizeEquals(listParameterMeta, userOutputList);
    }

	private boolean isListSizeEquals(List defList, List userList)
	{
		int defListSize = defList==null ? 0 : defList.size();
		int userListSize = userList==null ? 0 : userList.size();
		return defListSize==userListSize;
	}

	public String getKind()
	{
		String kind = Config.FUNCTION_DEFINITION_DEFAULT_KIND;
		if(lstFunctionMeta!=null)
		{
			kind = ((FunctionMeta) lstFunctionMeta.get(0)).getKind();
		}
		return kind;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/06/07 15:32:06  schroedn
 * HISTORY      : Edits to sync with new codebase and java webstart
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/08/02 18:44:20  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/01/03 18:56:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/07/27 21:52:54  doswellj
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/07/18 20:30:01  chene
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/07/18 19:58:42  doswellj
 * HISTORY      : Added Property class
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/07/14 22:24:40  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/07/14 20:48:54  jiangsc
 * HISTORY      : Removed the hard-coded values and defined them in Config class.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/07/13 18:25:19  jiangsc
 * HISTORY      : 1) Changed parameter reference from FunctionMetaImpl to FunctionMeta;
 * HISTORY      : 2) Fully implemented isFunctionMappingComplete(), isFunctionMappingInputComplete(), and isFunctionMappingOutputComplete() methods in FunctionManager class.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/07/13 17:50:21  jiangsc
 * HISTORY      : Make FunctionBoxManager a singleton.
 * HISTORY      :
 */
