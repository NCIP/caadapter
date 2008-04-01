/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/function/FunctionParser.java,v 1.4 2008-04-01 21:49:44 umkis Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common.function;

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



