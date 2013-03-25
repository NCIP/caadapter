/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.function;
/**
 * This class parses the Functions.
 * @author OWNER: $Author: phadkes $
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.6 $
 * @date       $Date: 2008-09-25 18:57:45 $
*/

import gov.nih.nci.caadapter.castor.function.impl.C_datapoint;
import gov.nih.nci.caadapter.castor.function.impl.C_function;
import gov.nih.nci.caadapter.castor.function.impl.C_functions;
import gov.nih.nci.caadapter.castor.function.impl.C_group;
import gov.nih.nci.caadapter.castor.function.impl.C_inputs;
import gov.nih.nci.caadapter.castor.function.impl.C_outputs;
import gov.nih.nci.caadapter.common.MetaException;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.function.meta.GroupMeta;
import gov.nih.nci.caadapter.common.function.meta.ParameterMeta;
import gov.nih.nci.caadapter.common.function.meta.impl.*;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class FunctionParser
{
    private String kind;
    //Parse a Function specification file

    public List<GroupMeta> parse(Reader metafile, String kind) throws MetaException
     {
       this.kind = kind;                          //Stores the file name (e.g., core) containing function specification
       C_functions c_functionsmeta = null;        //Stores castor object equivallent for functionmeta data.
       List<GroupMeta> lstGroupMeta = null;       //Stores a list of group meta objects.

       try
       {
           //Get the root castor object

           c_functionsmeta = (C_functions) C_functions.unmarshalC_functions(metafile);

           //Build a GroupMetaImpl object.
           lstGroupMeta = processGroup(c_functionsmeta.getC_group());
        }

        catch (MarshalException e)
        {
            throw new MetaException(e.getMessage(), e);

        }
        catch (ValidationException e)
        {
            throw new MetaException(e.getMessage(), e);
        }
        return lstGroupMeta;
    }

    private ArrayList processGroup(C_group[] c_group)
    {
        C_group c_groupobject = null;
        ArrayList alstFunctions = null;
        ArrayList alstGroups = null;
        GroupMetaImpl groupmeta = null;

         alstGroups = new ArrayList();
        //Evaluate the list of function groups defined in castor objects
        for (int i=0;i<c_group.length;i++)
        {
            //Reference individual function group.
            c_groupobject = c_group[i];

            //Instantiate new GroupMeta object and define its attributes from corresponding Castor objects
            groupmeta = new GroupMetaImpl();
            groupmeta.setGroupName(c_groupobject.getName());
            groupmeta.setXmlPath(c_groupobject.getXmlPath());
            //System.out.println("9-1 : " + groupmeta.getXmlPath() + ", " + c_groupobject.getXmlPath());
            //Build function list within the Group
            alstFunctions = new ArrayList();
            alstFunctions = processFunction(c_group[i]);
            groupmeta.setFunctionList(alstFunctions);

            //Build list of GroupsMeta objects
            alstGroups.add(groupmeta);

        }

        return alstGroups;
    }


    private ArrayList processFunction(C_group c_group) {

        FunctionMetaImpl functionMeta = null;     //Stores individual FunctionMetaImpl object
        ArrayList<FunctionMeta> alstFunctionMeta = new ArrayList<FunctionMeta>();       //Stores a list of  FunctionMetaImpl objects

        C_function[] c_functionlist = c_group.getC_function();    //Get castor representation of the function object

        //Make a FunctionMetaImpl object containing a list of function definitions.
        for (int i=0; i< c_functionlist.length; i++)
        {
            functionMeta = new FunctionMetaImpl();      //Stores a FunctionMetaImpl object
            C_function c_function = c_functionlist[i];  //Get function definition object from castor.
            functionMeta.setXmlPath(c_function.getXmlPath());
            //System.out.println("9-2 : " + functionMeta.getXmlPath() + ", " + c_function.getXmlPath());
            functionMeta.setGroupName(c_group.getName());
            functionMeta.setKind(kind);
            functionMeta.setFunctionName(c_function.getName());  //Set the function name.
            if(c_function.getC_implementation()!=null){
                functionMeta.setImplementationClass(c_function.getC_implementation().getClassname().toString());  //Set the implementation class
                functionMeta.setImplementationMethod(c_function.getC_implementation().getMethod()); //Set the implementation mood.
            }
            functionMeta.setInputDefinitionList(processInputList(functionMeta, c_function.getC_inputs()));  //Set the Input Definition List
            functionMeta.setOutputDefinitionList(processOutputList(functionMeta, c_function.getC_outputs())); //Set the Output Definition List.
            alstFunctionMeta.add(functionMeta); //Add a FunctionMetaImpl object to the FunctionMetaImpl object list.
        }

        return alstFunctionMeta;
    }

    private ArrayList<ParameterMeta> processInputList(FunctionMeta parentFunction, C_inputs c_input)
    {
        ParameterMetaImpl parameterMeta = null;                   //Stores a ParameterMetaImpl object.
        ArrayList<ParameterMeta> alstParameterMeta = new ArrayList<ParameterMeta>();            //Stores a list of ParameterMetaImpl objects.

        if(c_input==null)return alstParameterMeta;

        //Get castor representation of the c_input list object
        C_datapoint[] c_datapointlist = c_input.getC_datapoint();

        //Transfer data from the castor inputlist to the equivalent ParameterMetaImpl object list.
        for (int i=0; i< c_datapointlist.length; i++)
        {
            parameterMeta = new ParameterMetaImpl(parentFunction);                    //Create ParameterMetaImpl object.
            C_datapoint c_datapoint = c_datapointlist[i];               //Get castor inputlist.
            parameterMeta.setParameterType(c_datapoint.getDatatype());  //Set input parameter datatype.
            parameterMeta.setParameterName(c_datapoint.getName());      //Set input parameter name.
            parameterMeta.setParameterPosition(c_datapoint.getPos());   //Set input parameter position.
            parameterMeta.setXmlPath(c_datapoint.getXmlPath());      //Set input parameter UUID.
            parameterMeta.setIsInput(true);
            alstParameterMeta.add(parameterMeta);
        }

        return alstParameterMeta;
    }


    private ArrayList processOutputList(FunctionMeta parentFunction, C_outputs c_output)
    {
        ParameterMetaImpl parameterMeta = null;                   //Stores individual FunctionMetaImpl object
        ArrayList lstParameterMeta = new ArrayList();            //Stores a list of C_inputlist objects

        C_datapoint[] c_datapointlist = c_output.getC_datapoint();    //Get castor representation of the c_output list object
        for (int i=0; i< c_datapointlist.length; i++)
        {
            parameterMeta = new ParameterMetaImpl(parentFunction);  //Stores individual FunctionMetaImpl object
            C_datapoint c_datapoint = c_datapointlist[i];
            parameterMeta.setParameterType(c_datapoint.getDatatype());
            parameterMeta.setParameterName(c_datapoint.getName());
            parameterMeta.setParameterPosition(c_datapoint.getPos());
            parameterMeta.setXmlPath(c_datapoint.getXmlPath());
            parameterMeta.setIsInput(false);
            lstParameterMeta.add(parameterMeta);
        }

        return lstParameterMeta;
    }

	public String getKind()
	{
		return kind;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/



