/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/GME/actions/SaveAsXsdToXmiMapAction.java,v 1.6 2008-02-28 19:12:30 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.mapping.GME.actions;

import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.metadata.*;
import gov.nih.nci.caadapter.hl7.map.Mapping;
import gov.nih.nci.caadapter.hl7.map.Map;
import gov.nih.nci.caadapter.hl7.map.impl.MapBuilderImpl;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.MappableNode;
import gov.nih.nci.caadapter.ui.common.actions.DefaultSaveAsAction;
import gov.nih.nci.caadapter.ui.common.jgraph.MappingDataManager;
import gov.nih.nci.caadapter.ui.common.tree.MappingBaseTree;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.GME.XsdToXmiMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.GME.XsdToXmiMappingReporter;
import gov.nih.nci.caadapter.ui.mapping.catrend.CsvToXmiMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.catrend.CsvToXmiMappingReporter;
import gov.nih.nci.caadapter.mms.generator.XMIGenerator;
import gov.nih.nci.ncicb.xmiinout.domain.*;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLAssociationBean;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLAttributeBean;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLClassBean;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLModelBean;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLPackageBean;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class defines a concrete "Save As" action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.6 $
 *          date        $Date: 2008-02-28 19:12:30 $
 */
public class SaveAsXsdToXmiMapAction extends DefaultSaveAsAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: SaveAsXsdToXmiMapAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/GME/actions/SaveAsXsdToXmiMapAction.java,v 1.6 2008-02-28 19:12:30 wangeug Exp $";

	protected AbstractMappingPanel mappingPanel;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public SaveAsXsdToXmiMapAction(AbstractMappingPanel mappingPanel)
	{
		this(COMMAND_NAME, mappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public SaveAsXsdToXmiMapAction(String name, AbstractMappingPanel mappingPanel)
	{
		this(name, null, mappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public SaveAsXsdToXmiMapAction(String name, Icon icon, AbstractMappingPanel mappingPanel)
	{
		super(name, icon, null);
		this.mappingPanel = mappingPanel;
	}

	protected boolean doAction(ActionEvent e) throws Exception
	{
		if(this.mappingPanel!=null)
		{
			if(!mappingPanel.isSourceTreePopulated() || !mappingPanel.isTargetTreePopulated())
			{
				String msg = "Enter both source and target information before saving the map specification.";
				JOptionPane.showMessageDialog(mappingPanel, msg, "Error", JOptionPane.ERROR_MESSAGE);
				setSuccessfullyPerformed(false);
				return false;
			}
		}
		
		//Select file name and type 
		File file = DefaultSettings.getUserInputOfFileFromGUI(this.mappingPanel, Config.TAGGED_MAP_FILE_DEFAULT_EXTENTION, "Save As...", true, true);
		if (file != null)
		{
			setSuccessfullyPerformed(processSaveFile(file));
		}

		return isSuccessfullyPerformed();
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	protected boolean processSaveFile(File file) throws Exception
	{		
		preActionPerformed(mappingPanel);

        MappingDataManager mappingManager = mappingPanel.getMappingDataManager();
		Mapping mappingData = mappingManager.retrieveMappingData(true);

        MapBuilderImpl builder = new MapBuilderImpl();
        FileOutputStream fw = null;
		BufferedOutputStream bw = null;

        XsdToXmiMappingPanel xsdToXmi = (XsdToXmiMappingPanel)mappingPanel;
		mappingData.setMappingType(xsdToXmi.getMappingTarget());
        XmiModelMetadata xmiModelMeta = xsdToXmi.getXmiModelMeta();

        boolean oldChangeValue = mappingPanel.isChanged();

        try
		{
            System.out.println("[ Removing annotations ]");

            //Clean XMI.Content Mappings
            xmiModelMeta.cleanClassObjectAnnotation();
            
            Iterator xsdBeanIt = xsdToXmi.getXmiModelMeta().getUmlHashMap().keySet().iterator();//xsdModelMeta.getAttributeMap().keySet().iterator();
            while (xsdBeanIt.hasNext())
            {
                  String modelKey=(String)xsdBeanIt.next();
                
                  //find the attribute
                  Object umlObject = (Object) xsdToXmi.getXmiModelMeta().getUmlHashMap().get(modelKey );

                  //remove the taggedValue
                  if(umlObject ==null)
                  {
                	  System.out.println("SaveAsXsdToXmiMapAction.processSaveFile()..uml object is missing:"+modelKey);
                  }
                  else  if (umlObject instanceof UMLAttribute)
                  {
                     System.out.println("Removing attr " + modelKey );
                     ((UMLAttribute)umlObject).removeTaggedValue("GME_XMLLocReference");
                  }
                  else  if (umlObject instanceof UMLAssociation)
                  {
                     System.out.println("Removing assoc " + modelKey );
                     ((UMLAssociation)umlObject).removeTaggedValue("GME_TargetXMLLocRef");
                     ((UMLAssociation)umlObject).removeTaggedValue("GME_SourceXMLLocRef");
                  }
                  else if (umlObject instanceof UMLPackage)
                  {
                      System.out.println("Removing package " + modelKey );
                      xmiModelMeta.cleanPackageTaggedValue((UMLPackage)umlObject,"GME_XMLNamespace");
//                      ((UMLPackage)umlObject).removeTaggedValue("GME_XMLNamespace");
                  }
                  else
                	  System.out.println("SaveAsXsdToXmiMapAction.processSaveFile()... invalid uml object:"+umlObject.getClass());
            }
            System.out.println("[ / Removing annotations ]\n");

            Mapping mData = mappingManager.retrieveMappingData(true);
            List<Map> maps = mData.getMaps();

            List classList = new ArrayList();
            List packageList = new ArrayList();

            for (int j=0; j < maps.size(); j++ )
            {
                Map tempMap = maps.get(j);
                System.out.println( tempMap.getClass().toString() );
                System.out.println( "[target xpath: " + tempMap.getTargetMapElement().getMetaObject().getXmlPath() + " ]" );

                MetaObject srcMeta=tempMap.getSourceMapElement().getMetaObject();
                MetaObject trgtMeta =tempMap.getTargetMapElement().getMetaObject();
                if (trgtMeta==null)
                {
                	System.out.println("SaveAsXsdToXmiMapAction.processSaveFile()...invalidate mapping:"+tempMap.getXmlPath());
                }
                else if (trgtMeta instanceof AttributeMetadata)
                {
                	//annotate attribute
                	AttributeMetadata srcAttr=(AttributeMetadata)srcMeta;
                	AttributeMetadata trgtAttr=(AttributeMetadata)trgtMeta;
                	String srcKey=srcAttr.getXPath();

                    //add "EA Model" infront of the xmlPath
                	String trgtKey=xsdToXmi.getXmiModelMeta().getHandler().getModel().getName()+"."+ trgtAttr.getXmlPath();
                	System.out.println("SaveAsXsdToXmiMapAction.processSaveFile()..annotate source:"+srcKey);
                	System.out.println("SaveAsXsdToXmiMapAction.processSaveFile()..annotate target:"+trgtKey);
        	
                	UMLAttributeBean trgtUmlBean=(UMLAttributeBean)xsdToXmi.getXmiModelMeta().getUmlHashMap().get(trgtKey);
                    if ( trgtUmlBean ==null)
                    {
                    	System.out.println("SaveAsXsdToXmiMapAction.processSaveFile()..umlBean is not found:"+trgtKey);
                    }
                    else
                    {
                    	if(srcAttr.isChildTag())
                    		trgtUmlBean.addTaggedValue( "GME_XMLLocReference", srcAttr.getName() );
                    	else
                    		trgtUmlBean.addTaggedValue( "GME_XMLLocReference", "@" + srcAttr.getName() );
                    }
                    
                    //find the class and package of the this attribute
                    //remove attribute name from xmlPath
                    String classPathKey = trgtKey.substring( 0,trgtKey.lastIndexOf(".") );
                    if (!classList.contains(classPathKey))
                    {
                    	classList.add(classPathKey);

                        //annotate the class at its first attribute
                    	UMLClassBean trgtClassBean=(UMLClassBean)xsdToXmi.getXmiModelMeta().getUmlHashMap().get(classPathKey);
                    	String modelElmentId=trgtClassBean.getJDomElement().getAttributeValue("xmi.id");

                        //annotate at the class lvl
                        //trgtClassBean.addTaggedValue("GME_XMLNamespace", "Testing" );

                        //remove class name from xmlPath
                    	String umlPackageKey=classPathKey.substring(0, classPathKey.lastIndexOf("."));
                    	UMLPackageBean trgtPkgBean=(UMLPackageBean)xsdToXmi.getXmiModelMeta().getUmlHashMap().get(umlPackageKey);

                        //look the name space of source package
                    	String srcClassPath=srcKey.substring(0, srcKey.lastIndexOf("."));
                    	String srcPkgPath=srcClassPath.substring(0,srcClassPath.lastIndexOf("."));
                    	String srcGmeNameSpace=xsdToXmi.getXsdModelMeta().findPackageNamespace(srcPkgPath);
                    	String annoateGmeNamespace=srcGmeNameSpace+"/"+srcPkgPath;

                        //annotate package
                        if( !packageList.contains(trgtPkgBean.getName()))
                        {
                            packageList.add(trgtPkgBean.getName());
                            trgtPkgBean.addTaggedValue("GME_XMLNamespace", annoateGmeNamespace);
                        }

                        //annoate class to add tag at XMI level (xmi.content)
                    	String srcClassName=srcClassPath.substring(srcClassPath.lastIndexOf(".")+1);
                    	xsdToXmi.getXmiModelMeta().annotateClassObject(annoateGmeNamespace, modelElmentId, srcClassName, modelElmentId);
                    }                	
                }
                else if (trgtMeta instanceof AssociationMetadata)
                {
                	//annotate association
                	AssociationMetadata srcAssc=(AssociationMetadata)srcMeta;
                	AssociationMetadata trgtAssc=(AssociationMetadata)trgtMeta;
                    String srcAsscKey=srcAssc.getXPath();

                    //add "EA Model" infront of the xmlPath
                	String trgtAsscKey=xsdToXmi.getXmiModelMeta().getHandler().getModel().getName()+"."+ trgtAssc.getXmlPath();
                	UMLAssociationBean trgtUmlAsscBean=(UMLAssociationBean)xsdToXmi.getXmiModelMeta().getUmlHashMap().get(trgtAsscKey);
                    if ( trgtUmlAsscBean ==null)
                    {
                    	System.out.println("SaveAsXsdToXmiMapAction.processSaveFile()..umlBean is not found:"+trgtAsscKey);
                    }
                    else
                    {
                    	trgtUmlAsscBean.addTaggedValue("GME_TargetXMLLocRef",srcAssc.getRoleName());
                    	trgtUmlAsscBean.addTaggedValue("GME_SourceXMLLocRef",srcAssc.getParentXPath());
                    }

                    //find the class and package of the this attribute
                    //remove attribute name from xmlPath
                    String classPathKey = trgtAsscKey.substring( 0,trgtAsscKey.lastIndexOf(".") );
                    if (!classList.contains(classPathKey))
                    {
                    	classList.add(classPathKey);

                        //annotate the class at its first attribute
                    	UMLClassBean trgtClassBean=(UMLClassBean)xsdToXmi.getXmiModelMeta().getUmlHashMap().get(classPathKey);
                    	String modelElmentId=trgtClassBean.getJDomElement().getAttributeValue("xmi.id");

                        //remove class name from xmlPath
                    	String umlPackageKey=classPathKey.substring(0, classPathKey.lastIndexOf("."));
                    	UMLPackageBean trgtPkgBean=(UMLPackageBean)xsdToXmi.getXmiModelMeta().getUmlHashMap().get(umlPackageKey);

                        //look the name space of source package
                    	String srcClassPath=srcAsscKey.substring(0, srcAsscKey.lastIndexOf("."));
                    	String srcPkgPath=srcClassPath.substring(0,srcClassPath.lastIndexOf("."));
                    	String srcGmeNameSpace=xsdToXmi.getXsdModelMeta().findPackageNamespace(srcPkgPath);
                    	String annoateGmeNamespace=srcGmeNameSpace+"/"+srcPkgPath;

                        //annotate package
                        if( !packageList.contains(trgtPkgBean.getName()))
                        {
                            packageList.add(trgtPkgBean.getName());
                            trgtPkgBean.addTaggedValue("GME_XMLNamespace", annoateGmeNamespace);
                        }

                        //annoate class to add tag at XMI level
                    	String srcClassName=srcClassPath.substring(srcClassPath.lastIndexOf(".")+1);
                    	xsdToXmi.getXmiModelMeta().annotateClassObject(annoateGmeNamespace, modelElmentId, srcClassName, modelElmentId);
                    }
                }
                else
                {
                    System.out.println("SaveAsXsdToXmiMapAction.processSaveFile()... invalidate targetMetadata:"+trgtMeta.getName());
                }
            }
                        
//                //TODO: Project-lvl
//                umlModel =( UMLModelBean) xsdToXmi.getXmiModelMeta().getUmlHashMap().get( "EA Model" );
//                if ( umlModel != null )
//                {
//                    if ( xsdToXmi.getXsdModelMeta() != null )
//                    {
//                       System.out.println( "[ Adding GME Project Name: " + xsdToXmi.getXsdModelMeta().getProjectName() + " ]\n");
//                       umlModel.addTaggedValue( "GME_XMLNamespaces", xsdToXmi.getXsdModelMeta().getProjectName() );
//                    }
//                }
//
//                //TODO: Attribute-lvl
//                if ( tempMap.getTargetMapElement().getMetaObject() instanceof AttributeMetadata )
//                {
//                    System.out.println("** Searching for attr: " + tempMap.getTargetMapElement().getMetaObject().getXmlPath() );
//                    umlAttribute = (UMLAttribute) xsdToXmi.getXmiModelMeta().getUmlHashMap().get( tempMap.getTargetMapElement().getMetaObject().getXmlPath() );
//                    if( umlAttribute != null)
//                    {
//                        //TODO: Class-lvl
//                        System.out.println("[ Class-lvl ]");
//                        String umlClassStr = tempMap.getTargetMapElement().getMetaObject().getXmlPath();//.substring( 0, tempMap.getTargetMapElement().getMetaObject().getXmlPath().lastIndexOf(".") );
//                        System.out.println("umlClass Str :" + umlClassStr );
//                        UMLClass umlClass = (UMLClass) xsdToXmi.getXmiModelMeta().getUmlHashMap().get( umlClassStr);
//                        if ( umlClass != null )
//                        {
//                            //Add to a list, check the list if ! contains add tagged value
//                            if ( ! classList.contains( umlClassStr ) )
//                            {
//                                classList.add( umlClassStr );
//                                System.out.println("Class Found (added) -> Part of: " + umlClass.getName() );
//                                //TODO: PROBLEM WITH CLASS ELEMENT! Being placed in xmi.content
//                                umlClass.addTaggedValue( "GME_XMLElement_12345", umlClass.getName() );
//                            }
//                            System.out.println("[ / Class-lvl ]\n");
//
//                            //TODO: XMI.Content
//                            System.out.println("[ Xmi.Content ]");
//                            String classModelKey = xmiModelMeta.findModelElementXmiId( "EA Model." + umlClassStr );
//                            System.out.println( "Class: " + umlClassStr );
//                            System.out.println( "classModelKey:  " + classModelKey );
//
//                            String packageModelKey = xmiModelMeta.findModelElementXmiId( "EA Model." + umlClassStr.substring( 0, umlClassStr.lastIndexOf(".") ) );
//                            System.out.println("Package: " + umlClassStr.substring( 0, umlClassStr.lastIndexOf(".") ));
//                            System.out.println("packageModelKey: " + packageModelKey);
//                            System.out.println("[ / Xmi.Content ]\n");
//                            
//                            if ( classModelKey != null )
//                            {
//                                if ( xsdToXmi.getXsdModelMeta() != null )
//                                {
//                                    xmiModelMeta.annotateClassObject( xsdToXmi.getXsdModelMeta().getProjectName(), packageModelKey, tempMap.getTargetMapElement().getMetaObject().getName(), classModelKey );
//                                    //public void annotateClassObject(String gmeXmlNamespace, String packageModelElementId, String gmeXmlElementName,String classModelElementId)
//                                }
//                            }
//                            
//                            //TODO: Package-lvl
//                            System.out.println("[ Package-lvl ]");
//                            String umlPackageStr = tempMap.getTargetMapElement().getMetaObject().getXmlPath().substring( 0, umlClassStr.lastIndexOf(".") );
//                            System.out.println("umlPackageStr :" + umlPackageStr );
//
//                            UMLPackage umlPackage = (UMLPackage) xsdToXmi.getXmiModelMeta().getUmlHashMap().get( "EA Model." + umlPackageStr );
//                            if( umlPackage != null)
//                            {
//                                if( ! packageList.contains( umlPackageStr) )
//                                {
//                                    packageList.add( umlPackageStr );
//                                    System.out.println("Package Found (added) -> Part of: " + umlPackageStr );
//
//                                    //TODO: Get GMENamespace for the XSD Class here
//                                    String clsGME = xsdModelMeta.findPackageNamespace( umlPackageStr );
//                                    System.out.println("GME Namespace: " + clsGME );                                    
//                                    umlPackage.addTaggedValue( "GME_XMLNamespace", clsGME );
//                                }
//                            }
//                            System.out.println("[ / Package-lvl ]\n");
//                        }
//
//                        //Add the TaggedValue
//                        System.out.println("[ Adding GME Attribute TaggedValue ]\n");
//                        AttributeMetadata attr = (AttributeMetadata) tempMap.getSourceMapElement().getMetaObject();
//                        if ( attr.isChildTag() )
//                        {
//                            umlAttribute.addTaggedValue( "GME_XMLLocReference", tempMap.getSourceMapElement().getMetaObject().getName() );
//                        }
//                        else 
//                        {
//                            umlAttribute.addTaggedValue( "GME_XMLLocReference", "@" + tempMap.getSourceMapElement().getMetaObject().getName() );
//                        }
//                    }
//                }

//                //TODO: Association-lvl
//                if ( tempMap.getTargetMapElement().getMetaObject() instanceof AssociationMetadata )
//                {
//                    UMLAssociation umlAssoc = (UMLAssociation) xsdToXmi.getXmiModelMeta().getUmlHashMap().get( "EA Model." + tempMap.getTargetMapElement().getMetaObject().getXmlPath() );
//
//                    if ( umlAssoc != null)
//                    {
//                        //TODO: Class-lvl
//                         System.out.println("[ Class-lvl ]");
//                        String umlClassStr = tempMap.getTargetMapElement().getMetaObject().getXmlPath().substring( 0, tempMap.getTargetMapElement().getMetaObject().getXmlPath().lastIndexOf(".") );
//                        System.out.println("umlClass Str :" + umlClassStr );
//                        UMLClass umlClass = (UMLClass) xsdToXmi.getXmiModelMeta().getUmlHashMap().get( "EA Model." + umlClassStr );
//                        if ( umlClass != null )
//                        {
//                            //Add to a list, check the list if ! contains add tagged value
//                            if ( ! classList.contains( umlClassStr ) )
//                            {
//                                classList.add( umlClassStr );
//                                System.out.println("Class Found (added) -> Part of: " + umlClassStr );
//                                umlClass.addTaggedValue( "GME_XMLElement_12345", umlClass.getName() );
//                            }
//                             System.out.println("[ / Class-lvl ]\n");
//
//                            //TODO: XMI.Content-lvl
//                            System.out.println("[ Xmi.Content ]");
//                            String classModelKey = xmiModelMeta.findModelElementXmiId( "EA Model." + umlClassStr );
//                            System.out.println( "Class: " + umlClassStr );
//                            System.out.println( "classModelKey:  " + classModelKey );
//
//                            String packageModelKey = xmiModelMeta.findModelElementXmiId( "EA Model." + umlClassStr.substring( 0, umlClassStr.lastIndexOf(".") ) );
//                            System.out.println("Package: " + umlClassStr.substring( 0, umlClassStr.lastIndexOf(".") ));
//                            System.out.println("packageModelKey: " + packageModelKey);
//                            System.out.println("[ / Xmi.Content ]\n");
//
//                            if ( classModelKey != null )
//                            {
//                                if ( xsdToXmi.getXsdModelMeta() != null )
//                                {
//                                    xmiModelMeta.annotateClassObject( xsdToXmi.getXsdModelMeta().getProjectName(), packageModelKey, tempMap.getTargetMapElement().getMetaObject().getName(), classModelKey );
//                                    //public void annotateClassObject(String gmeXmlNamespace, String packageModelElementId, String gmeXmlElementName,String classModelElementId)
//                                }
//                            }
//
//                            //TODO: Package-lvl
//                             System.out.println("[ Package-lvl ]");
//                            String umlPackageStr = tempMap.getTargetMapElement().getMetaObject().getXmlPath().substring( 0, umlClassStr.lastIndexOf(".") );
//                            System.out.println( "umlPackageStr :" + umlPackageStr );
//                            UMLPackage umlPackage = (UMLPackage) xsdToXmi.getXmiModelMeta().getUmlHashMap().get( "EA Model." + umlPackageStr );
//                            if( umlPackage != null)
//                            {
//                                if( ! packageList.contains( umlPackageStr ) )
//                                {
//                                    packageList.add( umlPackageStr );
//                                    System.out.println( "Package Found (added) -> Part of: " + umlPackageStr );
//                                    umlPackage.addTaggedValue( "GME_XMLNamespace", umlPackage.getName() );
//                                }
//                            }
//                             System.out.println("[ / Package-lvl ]\n");
//                        }
//
//                        System.out.println( "[ Adding GME Assoc TaggedValue ]\n" );
//                        umlAssoc.addTaggedValue( "GME_SourceXMLLocRef",  "" + tempMap.getSourceMapElement().getMetaObject().getXmlPath().substring( tempMap.getSourceMapElement().getMetaObject().getXmlPath().lastIndexOf(".") + 1, tempMap.getSourceMapElement().getMetaObject().getXmlPath().length() ) );                        
//                        umlAssoc.addTaggedValue( "GME_TargetXMLLocRef",  "" + tempMap.getTargetMapElement().getMetaObject().getXmlPath().substring( tempMap.getTargetMapElement().getMetaObject().getXmlPath().lastIndexOf(".") + 1, tempMap.getTargetMapElement().getMetaObject().getXmlPath().length() ) );
//                    }
//                }
//            }

            //Save xmi to disk
            xsdToXmi.getXmiModelMeta().getHandler().save( file.getAbsolutePath() );

            if (!GeneralUtilities.areEqual(defaultFile, file))
			{//not equal, change it.
				removeFileUsageListener(defaultFile, mappingPanel);
				defaultFile = file;
			}

			//clear the change flag.
			mappingPanel.setChanged(false);

            //try to notify affected panels
			postActionPerformed(mappingPanel);
            //xsdToXmi.xsdToXmiGeneration(file.getAbsolutePath());
    		
            //generate mapping report:
            File rptFile =null;
            if (xsdToXmi.getReportPanel()!=null)
            	rptFile=xsdToXmi.getReportPanel().getSaveFile();
            
            if (rptFile!=null)
            {
//            	rptFile=DefaultSettings.getUserInputOfFileFromGUI(this.mappingPanel, Config.HL7_V3_MESSAGE_FILE_DEFAULT_EXTENSION, "Save Mapping Report As...", true, true);
	            //regenerate reportfile if it is available
            	MappingBaseTree mappingBaseTree=(MappingBaseTree)xsdToXmi.getSourceTree();
	            XsdToXmiMappingReporter reporter=new XsdToXmiMappingReporter((MappableNode)mappingBaseTree.getRootTreeNode(), XsdToXmiMappingReporter.REPORT_UNMAPPED);
	            reporter.setSourceFileName(xsdToXmi.getSourceFileName());
	            reporter.setTargetFileName(xsdToXmi.getTargetFileName());
	            reporter.generateReportFile(rptFile);
            }
            JOptionPane.showMessageDialog(mappingPanel.getParent(), "Mapping data has been saved successfully.", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
			return true;

		}
		catch(Throwable e)
		{
			//restore the change value since something occurred and believe the save process is aborted.
			mappingPanel.setChanged(oldChangeValue);
			//rethrow the exeception
			e.printStackTrace();
			throw new Exception(e);
		}
		finally
		{
			try
			{
				//close buffered writer will automatically close enclosed file writer.
				if(bw!=null)
				{
					bw.close();
					mappingPanel.setSaveFile(file);
				}
			}
			catch(Throwable e)
			{//intentionally ignored.
			}
		}
	}

    public void getPackages( UMLPackage pkg )
    {
//        for( UMLTaggedValue tagValue : pkg.getTaggedValues() )
//        {
//                if( tagValue.getName().contains( "GME_XMLNamespace" ))
//                {
//                    System.out.println("Removing pkg GME_XMLNamespace (pkg)");
//                    pkg.removeTaggedValue( "GME_XMLNamespace" );
//                }
//        }

        for ( UMLClass clazz : pkg.getClasses() )
        {
            System.out.println("class.name: " + clazz.getName());

            for( UMLTaggedValue tagValue : clazz.getTaggedValues() )
            {
                if( tagValue.getName().contains( "GME_XMLElement" ))
                {
                    System.out.println("Removing pkg GME_XMLElement (class)");
                    clazz.removeTaggedValue( "GME_XMLElement" );
                }
            }

            for( UMLAttribute att : clazz.getAttributes() )
            {
                System.out.println("attr.name: " + att.getName() );
                for( UMLTaggedValue tagValue : att.getTaggedValues() )
                {
                    if( tagValue.getName().contains( "GME_XMLLocReference" ))
                    {
                        System.out.println("Removing pkg GME_XMLLocReference (attr)");
                        att.removeTaggedValue( "GME_XMLLocReference" );
                    }
                }
            }
            for( UMLAssociation assc : clazz.getAssociations())
            {
                for( UMLTaggedValue tagValue : assc.getTaggedValues() )
                {
                    if( tagValue.getName().contains( "GME_SourceXMLLocRef" ))
                    {
                        System.out.println("Removing pkg GME_SourceXMLLocRef (assoc)");
                        assc.removeTaggedValue( "GME_SourceXMLLocRef" );
                    }
                    if( tagValue.getName().contains( "GME_TargetXMLLocRef" ))
                    {
                        System.out.println("Removing pkg GME_TargetXMLLocRef (assoc)");
                        assc.removeTaggedValue( "GME_TargetXMLLocRef" );
                    }
                }
            }
        }

        for ( UMLPackage pkg2 : pkg.getPackages() )
        {
            getPackages( pkg2 );
        }
    }

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.5  2008/02/25 20:15:32  schroedn
 * HISTORY      : *** empty log message ***
 * HISTORY      :
 * HISTORY      : Revision 1.4  2008/02/21 15:39:31  wangeug
 * HISTORY      : annotate XMI to support GME
 * HISTORY      :
 * HISTORY      : Revision 1.3  2008/02/20 21:46:06  schroedn
 * HISTORY      : *** empty log message ***
 * HISTORY      :
 * HISTORY      : Revision 1.2  2008/02/20 15:24:38  schroedn
 * HISTORY      : Annotations
 * HISTORY      :
 * HISTORY      : Revision 1.1  2008/02/04 15:10:34  schroedn
 * HISTORY      : XSD to XMI Mapping - GME initial load
 * HISTORY      :
 * HISTORY      : Revision 1.7  2007/12/14 16:01:36  wangeug
 * HISTORY      : do not force to save mapping report
 * HISTORY      :
 * HISTORY      : Revision 1.6  2007/12/12 19:54:07  wangeug
 * HISTORY      : support both data model and object model
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/12/07 16:06:15  wangeug
 * HISTORY      : support both data model and object model
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/12/06 16:16:28  schroedn
 * HISTORY      : Annotate XMI file in csv to xmi
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/11/30 20:57:53  schroedn
 * HISTORY      : CSV to XMI mapping
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/11/30 14:40:33  wangeug
 * HISTORY      : create CSV_TO_XMI mapping module
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/11/29 16:47:52  wangeug
 * HISTORY      : create CSV_TO_XMI mapping module
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/09/20 16:40:14  schroedn
 * HISTORY      : License text
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/06/07 19:02:24  schroedn
 * HISTORY      : Edits to sync with new codebase and java webstart
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/09/26 15:48:30  wuye
 * HISTORY      : New actions for object - 2 db mapping
 * HISTORY      :
 * HISTORY      : Revision 1.29  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.28  2006/06/13 18:12:13  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.27  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.26  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.25  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.24  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.23  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.22  2005/12/01 20:03:39  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/10/26 16:22:10  jiangsc
 * HISTORY      : Face lift to provide better error report.
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/10/18 13:35:26  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/10/13 17:37:41  jiangsc
 * HISTORY      : Enhanced UI reporting on exceptions.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/10/10 20:49:01  jiangsc
 * HISTORY      : Enhanced dialog operation.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/09/30 20:44:06  jiangsc
 * HISTORY      : Minor update - corrected wording
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/09/30 20:28:09  jiangsc
 * HISTORY      : Minor update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/08/30 20:48:17  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/08/11 22:10:32  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/05 20:35:53  jiangsc
 * HISTORY      : 0)Implemented field sequencing on CSVPanel but needs further rework;
 * HISTORY      : 1)Removed (Yes/No) for questions;
 * HISTORY      : 2)Removed double-checking after Save-As;
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/07/27 22:41:11  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 */
