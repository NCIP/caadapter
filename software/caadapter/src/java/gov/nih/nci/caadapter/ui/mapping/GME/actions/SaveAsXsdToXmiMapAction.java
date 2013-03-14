/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
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
import gov.nih.nci.caadapter.ui.mapping.GME.XsdToXmiMappingReportPanel;
import gov.nih.nci.ncicb.xmiinout.domain.*;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLAssociationBean;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLAttributeBean;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLClassBean;

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
 *          revision    $Revision: 1.16 $
 *          date        $Date: 2009-07-14 16:36:40 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/GME/actions/SaveAsXsdToXmiMapAction.java,v 1.16 2009-07-14 16:36:40 wangeug Exp $";

    protected AbstractMappingPanel mappingPanel;
    private XsdToXmiMappingReportPanel holderPane;
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

            //holderPane.setSaveFile(file);
            setDefaultFile(file);
            mappingPanel.setSaveFile(file);
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

            //TODO: Clean XMI.Content Mappings
            xmiModelMeta.cleanClassObjectAnnotation();
            xmiModelMeta.cleanProjectObjectAnnotation();

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
                     ((UMLAttribute)umlObject).removeTaggedValue("NCI_GME_XML_LOC_REF");
                  }
                  else  if (umlObject instanceof UMLAssociation)
                  {
                     System.out.println("Removing assoc " + modelKey );
                     ((UMLAssociation)umlObject).removeTaggedValue("NCI_GME_TARGET_XML_LOC_REF");
                     ((UMLAssociation)umlObject).removeTaggedValue("NCI_GME_SOURCE_XML_LOC_REF");
                  }
                  else if (umlObject instanceof UMLPackage)
                  {
                      System.out.println("Removing package " + modelKey );
                      xmiModelMeta.cleanPackageTaggedValue((UMLPackage)umlObject,"NCI_GME_XML_NAMESPACE");
//                      ((UMLPackage)umlObject).removeTaggedValue("NCI_GME_XML_NAMESPACE");
                  }
                  else
                  {
                      System.out.println("SaveAsXsdToXmiMapAction.processSaveFile()... invalid uml object:"+umlObject.getClass());
                  }
            }
            System.out.println("[ / Removing annotations ]\n");

            Mapping mData = mappingManager.retrieveMappingData(true);
            List<Map> maps = mData.getMaps();

            List classList = new ArrayList();
            List packageList = new ArrayList();
            boolean projectAnnotation = false;

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
//                  String srcKey=srcAttr.getXPath();
                    String srcKey=srcAttr.getXmlPath();


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
                            trgtUmlBean.addTaggedValue( "NCI_GME_XML_LOC_REF", srcAttr.getName() );
                        else
                            trgtUmlBean.addTaggedValue( "NCI_GME_XML_LOC_REF", "@" + srcAttr.getName() );
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
                        //trgtClassBean.addTaggedValue("NCI_GME_XML_NAMESPACE", "Testing" );

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
                            trgtPkgBean.addTaggedValue("NCI_GME_XML_NAMESPACE", annoateGmeNamespace);
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

                    //find containing class
                    String className = (String) trgtAsscKey;
                    String classPath = className.substring(0, className.lastIndexOf("."));
                    String classRealName = classPath.substring(classPath.lastIndexOf(".")+1);
                    System.out.println("class: (" + classRealName + ")" );

                    if ( trgtUmlAsscBean ==null)
                    {
                        System.out.println("SaveAsXsdToXmiMapAction.processSaveFile()..umlBean is not found:"+trgtAsscKey);
                    }
                    else  if( trgtUmlAsscBean.getTaggedValue("ea_sourceName").getValue().equals(classRealName) )
                    {
//                      trgtUmlAsscBean.addTaggedValue("NCI_GME_TARGET_XML_LOC_REF",srcAssc.getRoleName());

//                      trgtUmlAsscBean.addTaggedValue("NCI_GME_SOURCE_XML_LOC_REF",srcAssc.getRoleName()+"/"+srcAssc.getParentXPath().substring(srcAssc.getParentXPath().lastIndexOf(".")+1 , srcAssc.getParentXPath().length() ));
                      trgtUmlAsscBean.addTaggedValue("NCI_GME_SOURCE_XML_LOC_REF",srcAssc.getRoleName()+"/"+srcAssc.getreturnTypeXPath().substring(srcAssc.getreturnTypeXPath().lastIndexOf(".")+1 , srcAssc.getreturnTypeXPath().length() ));
                    }
                    else  if( trgtUmlAsscBean.getTaggedValue("ea_targetName").getValue().equals(classRealName) )
                    {
//                      trgtUmlAsscBean.addTaggedValue("NCI_GME_SOURCE_XML_LOC_REF",srcAssc.getParentXPath());
//                        trgtUmlAsscBean.addTaggedValue("NCI_GME_TARGET_XML_LOC_REF",srcAssc.getRoleName()+"/" + srcAssc.getParentXPath().substring(srcAssc.getParentXPath().lastIndexOf(".")+1 , srcAssc.getParentXPath().length() ));
                        trgtUmlAsscBean.addTaggedValue("NCI_GME_TARGET_XML_LOC_REF",srcAssc.getRoleName()+"/"+srcAssc.getreturnTypeXPath().substring(srcAssc.getreturnTypeXPath().lastIndexOf(".")+1 , srcAssc.getreturnTypeXPath().length() ));
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
                            trgtPkgBean.addTaggedValue("NCI_GME_XML_NAMESPACE", annoateGmeNamespace);
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
                //Add Project Namespace
                if( projectAnnotation == false )
                {
                    String projectNamespace=xsdToXmi.getXsdModelMeta().getProjectNamespace();
                    xmiModelMeta.annotateProjectObject(projectNamespace);//srcGmeNameSpace);
                    projectAnnotation = true;
                }
            }

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
//              rptFile=DefaultSettings.getUserInputOfFileFromGUI(this.mappingPanel, Config.HL7_V3_MESSAGE_FILE_DEFAULT_EXTENSION, "Save Mapping Report As...", true, true);
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
 * HISTORY      : Revision 1.15  2008/10/20 16:33:10  phadkes
 * HISTORY      : GME changes for correctly referencing RoleName/Class fom xsd.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2008/10/09 20:47:31  phadkes
 * HISTORY      : change the order of GME tag from class/RoleName to RoleName/Class
 * HISTORY      :
 * HISTORY      : Revision 1.13  2008/06/26 20:17:39  phadkes
 * HISTORY      : Changes to GME tags.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2008/06/19 20:03:53  phadkes
 * HISTORY      : Changes to GME tags.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2008/06/12 17:36:06  phadkes
 * HISTORY      : Replaced to getXmlPath  instead of getXPath
 * HISTORY      :
 * HISTORY      : Revision 1.10  2008/06/09 19:54:05  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2008/03/12 13:59:05  wangeug
 * HISTORY      : annotate GME_XMLNamespace at project level
 * HISTORY      :
 * HISTORY      : Revision 1.8  2008/03/07 15:36:48  schroedn
 * HISTORY      : saving association
 * HISTORY      :
 * HISTORY      : Revision 1.7  2008/03/04 16:08:11  schroedn
 * HISTORY      : GME - creating annotated XMI file
 * HISTORY      :
 * HISTORY      : Revision 1.6  2008/02/28 19:12:30  wangeug
 * HISTORY      : load mapping from xsd to Xmi
 * HISTORY      :
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
