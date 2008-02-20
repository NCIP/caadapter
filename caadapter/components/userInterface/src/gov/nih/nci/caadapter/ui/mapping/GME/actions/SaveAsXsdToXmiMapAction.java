/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/GME/actions/SaveAsXsdToXmiMapAction.java,v 1.2 2008-02-20 15:24:38 schroedn Exp $
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

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.common.metadata.XmiModelMetadata;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
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
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLModelBean;

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
 * @author LAST UPDATE $Author: schroedn $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-02-20 15:24:38 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/GME/actions/SaveAsXsdToXmiMapAction.java,v 1.2 2008-02-20 15:24:38 schroedn Exp $";

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
		
        boolean oldChangeValue = mappingPanel.isChanged();

        try
		{
            XmiModelMetadata xmiModelMeta = xsdToXmi.getXmiModelMeta();
            Iterator  it=xmiModelMeta.getUmlHashMap().keySet().iterator();
            while(it.hasNext())
            {
                  String modelKey=(String)it.next();
                  System.out.println("XsdToXmiMappingPanel.actionPerformed() ..xmlPath:"+modelKey+"="+xmiModelMeta.findModelElementXmiId(modelKey));
            }

            //test annotation
            xmiModelMeta.cleanClassObjectAnnotation();
            for (int i=0;i<10;i++)
            {
                  xmiModelMeta.annotateClassObject("gemNameSpaceTest"+i, "packageModelElementIdTest"+i, "gmeXmlElementNameTest"+i, "classModelElementIdtest"+i);
            }
            xmiModelMeta.getHandler().save("myOut.xmi");

            //print all mappings
            System.out.println( "[ Current Mappings ]" );

            Mapping mData = mappingManager.retrieveMappingData(true);
            List<Map> maps = mData.getMaps();

            List classList = new ArrayList();
            List packageList = new ArrayList();

            for (int j=0; j < maps.size(); j++ )
            {
                Map tempMap = maps.get(j);
                System.out.println(tempMap.getClass().toString());                
                System.out.println("[target xpath: " + tempMap.getTargetMapElement().getMetaObject().getXmlPath() + " ]" );

                //TODO: Project-lvl
                UMLModelBean umlModel=(UMLModelBean) xsdToXmi.getXmiModelMeta().getUmlHashMap().get( "EA Model" );
                if ( umlModel != null )
                {
//                    if ( xsdToXmi.getXsdModelMeta().getProjectName() != null )
//                    {
//                        System.out.println("Adding GME Project TaggedValue");
//                        umlModel.addTaggedValue( "GME_XMLNamespaces", "gme://" + xsdToXmi.getXsdModelMeta().getProjectName() );
//                    }
                    // + "." + "contextName" + "/" + "version" + "/" + "packagePath" );
                }

            //TODO: Package-lvl
    //                UMLPackage umlPackage = (UMLPackage) xsdToXmi.getXmiModelMeta().getUmlHashMap().get( "EA Model" );
    //                if (umlPackage != null )
    //                {
    //                    System.out.println("Adding GME Package TaggedValue" );
    //                    umlPackage.addTaggedValue( "GME_XMLNamespace", "gme://" + xsdToXmi.getXsdModelMeta().getProjectName() );
    //                     // + "." + "contextName" + "/" + "version" + "/" + "packagePath" );
    //                }

                //TODO: Attribute-lvl
                if ( tempMap.getTargetMapElement().getMetaObject() instanceof AttributeMetadata)
                {
                    UMLAttribute umlAttribute = (UMLAttribute) xsdToXmi.getXmiModelMeta().getUmlHashMap().get( "EA Model." + tempMap.getTargetMapElement().getMetaObject().getXmlPath() );
                    if( umlAttribute != null)
                    {
                        //TODO: Class-lvl
                        String umlClassStr = tempMap.getTargetMapElement().getMetaObject().getXmlPath().substring( 0, tempMap.getTargetMapElement().getMetaObject().getXmlPath().lastIndexOf(".") );
                        System.out.println("umlClass Str :" + umlClassStr );
                        UMLClass umlClass = (UMLClass) xsdToXmi.getXmiModelMeta().getUmlHashMap().get( "EA Model." + umlClassStr );
                        if ( umlClass != null )
                        {
                            //Add to a list, check the list if ! contains add tagged value
                            if ( ! classList.contains( umlClassStr ) )
                            {
                                classList.add( umlClassStr );
                                System.out.println("Class Found (added) -> Part of: " + umlClassStr );
                                umlClass.addTaggedValue( "GME_XMLElement", umlClass.getName() );
                            }

                            //TODO: XMI.Content
                            //Add to xmi.content
                            String modelKey = xmiModelMeta.findModelElementXmiId( "EA Model." + umlClassStr );
                            System.out.println( "modelKey: " + modelKey );
                            if ( modelKey != null )
                            {
                                xmiModelMeta.annotateClassObject( "gme://caAdapterProject.caBIG/1.0/java.lang", modelKey, tempMap.getTargetMapElement().getMetaObject().getName(), modelKey );
                            }
                            
                            //TODO: Package-lvl
                            String umlPackageStr = tempMap.getTargetMapElement().getMetaObject().getXmlPath().substring( 0, umlClassStr.lastIndexOf(".") );
                            System.out.println("umlPackageStr :" + umlPackageStr );
                            UMLPackage umlPackage = (UMLPackage) xsdToXmi.getXmiModelMeta().getUmlHashMap().get( "EA Model" + umlPackageStr );
                            if( umlPackage != null)
                            {
                                if( ! packageList.contains( umlPackageStr))
                                {
                                    packageList.add( umlPackageStr );
                                    System.out.println("Package Found (added) -> Part of: " + umlPackageStr );
                                    umlPackage.addTaggedValue( "GME_XMLNamespace", umlPackage.getName() );
                                }
                            }
                        }

                        //Add the TaggedValue
                        System.out.println("Adding GME Attribute TaggedValue");
                        AttributeMetadata attr = (AttributeMetadata) tempMap.getSourceMapElement().getMetaObject();
                        if ( attr.isChildTag() )
                        {
                            umlAttribute.addTaggedValue( "GME_XMLLocReference", tempMap.getSourceMapElement().getMetaObject().getName() );
                        }
                        else 
                        {
                            umlAttribute.addTaggedValue( "GME_XMLLocReference", "@" + tempMap.getSourceMapElement().getMetaObject().getName() );
                        }
                    }
                }

                //TODO: Association-lvl
                if ( tempMap.getTargetMapElement().getMetaObject() instanceof AssociationMetadata )
                {
                    UMLAssociation umlAssoc = (UMLAssociation) xsdToXmi.getXmiModelMeta().getUmlHashMap().get( "EA Model." + tempMap.getTargetMapElement().getMetaObject().getXmlPath() );

                    if ( umlAssoc != null)
                    {
                        //TODO: Class-lvl
                        String umlClassStr = tempMap.getTargetMapElement().getMetaObject().getXmlPath().substring( 0, tempMap.getTargetMapElement().getMetaObject().getXmlPath().lastIndexOf(".") );
                        System.out.println("umlClass Str :" + umlClassStr );
                        UMLClass umlClass = (UMLClass) xsdToXmi.getXmiModelMeta().getUmlHashMap().get( "EA Model." + umlClassStr );
                        if ( umlClass != null )
                        {
                            //Add to a list, check the list if ! contains add tagged value
                            if ( ! classList.contains( umlClassStr ) )
                            {
                                classList.add( umlClassStr );
                                System.out.println("Class Found (added) -> Part of: " + umlClassStr );
                                umlClass.addTaggedValue( "GME_XMLElement", umlClass.getName() );
                            }

                             //TODO: Package-lvl
                            String umlPackageStr = tempMap.getTargetMapElement().getMetaObject().getXmlPath().substring( 0, umlClassStr.lastIndexOf(".") );
                            System.out.println( "umlPackageStr :" + umlPackageStr );
                            UMLPackage umlPackage = (UMLPackage) xsdToXmi.getXmiModelMeta().getUmlHashMap().get( "EA Model" + umlPackageStr );
                            if( umlPackage != null)
                            {
                                if( ! packageList.contains( umlPackageStr ) )
                                {
                                    packageList.add( umlPackageStr );
                                    System.out.println( "Package Found (added) -> Part of: " + umlPackageStr );
                                    umlPackage.addTaggedValue( "GME_XMLNamespace", umlPackage.getName() );
                                }
                            }
                        }
                        System.out.println( "Adding GME Assoc TaggedValue" );
                        umlAssoc.addTaggedValue( "GME_SourceXMLLocRef",  "" + tempMap.getSourceMapElement().getMetaObject().getXmlPath().substring( tempMap.getSourceMapElement().getMetaObject().getXmlPath().lastIndexOf(".") + 1, tempMap.getSourceMapElement().getMetaObject().getXmlPath().length() ) );                        
                        umlAssoc.addTaggedValue( "GME_TargetXMLLocRef",  "" + tempMap.getTargetMapElement().getMetaObject().getXmlPath().substring( tempMap.getTargetMapElement().getMetaObject().getXmlPath().lastIndexOf(".") + 1, tempMap.getTargetMapElement().getMetaObject().getXmlPath().length() ) );
                    }
                }

                //TODO: XMI.Content-lvl
                // ??
            }

            //NOT NEEDED: List all classes with Attributes or Associations
//            Iterator iterator=classList.iterator();
//            String string;
//            System.out.println("[ Class List ]");
//            while(iterator.hasNext()){
//                string=(String)iterator.next();
//                System.out.println(string);
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

//
            //try to notify affected panels
			postActionPerformed(mappingPanel);
		
            xsdToXmi.xsdToXmiGeneration(file.getAbsolutePath());
    		
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
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
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
