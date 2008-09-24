/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.ui.common.resource;
/** BuildsHL7ResourceAction for HL7 UI
 * @author OWNER: $Author: phadkes $
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2    
 * @version    $Revision: 1.14 $
 * @date       $Date: 2008-09-24 17:55:15 $
*/

import gov.nih.nci.caadapter.hl7.mif.v1.BuildResourceUtil;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.main.HL7AuthorizationDialog;
import gov.nih.nci.caadapter.ui.mapping.V2V3.V2MetaCollectorDialog;
import gov.nih.nci.caadapter.ui.mapping.V2V3.V2MetaBasicInstallDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BuildHL7ResourceAction extends AbstractContextAction {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final String COMMAND_BUILD_V3 = "Load HL7 v3 Normative Edition Processable Artifacts";
    public static final String COMMAND_BUILD_V2 = "Load HL7 v2 Processable Artifacts";
    public static final String COMMAND_BUILD_V2_CORE ="Load HL7 v2 Core Artifacts";
    public static final String COMMAND_BUILD_V_MESSAGE = "Load HL7 v2 Message Artifacts";
 
    private AbstractMainFrame mainFrame;
   
    private static final String RESOURCE_NAME_V3="resource.zip";
    private static final String RESOURCE_NAME_V2="resourceV2.zip";


    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a default icon.
     */
    public BuildHL7ResourceAction(String name, AbstractMainFrame mainFrame) {
        this(name, null, mainFrame);
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public BuildHL7ResourceAction(String name, Icon icon, AbstractMainFrame mainFrame) {
        super(name, icon);
        this.mainFrame = mainFrame;
        setActionCommandType(DESKTOP_ACTION_TYPE);
    }

    /**
     * The abstract function that descendant classes must be overridden to
     * provide customsized handling.
     *
     * @param e
     * @return true if the action is finished successfully; otherwise,
     *         return false.
     */
    protected boolean doAction(ActionEvent e) throws Exception {
 
    	String resourceName="";
    	if (getName().equals(COMMAND_BUILD_V2))
    		resourceName=RESOURCE_NAME_V2;
    	else if (getName().equals(COMMAND_BUILD_V3))
    		resourceName=RESOURCE_NAME_V3;
    	
    	String resorcePath=BuildResourceUtil.findResourceFile(resourceName);
    	boolean toContinue=false;
    	
    	if (resorcePath==null||resorcePath.equals(""))
    	{
    		toContinue=true;
    		resorcePath=System.getProperty("user.dir")+java.io.File.separator
    		+"lib"+java.io.File.separator
    		+resourceName;
    	}
    	else
    	{
    		String msgConfirm="Your resource is found :"+resorcePath+".\nIt will replaced if you continue ?";
    		
    		int userReply=JOptionPane.showConfirmDialog(mainFrame, msgConfirm, getName(),JOptionPane.YES_OPTION);
    		if (userReply==JOptionPane.YES_OPTION)
    			toContinue=true;
    	}
    	if (toContinue)
    	{
    		if (getName().equals( COMMAND_BUILD_V3))
    			new BuildHL7ResourceDialog(mainFrame,getName(),true, resorcePath).setVisible(true);
    		else if (getName().equals( COMMAND_BUILD_V2))
    		{
//		        V2MetaCollectorDialog dialog = new V2MetaCollectorDialog(mainFrame);
    			HL7AuthorizationDialog dialog=	new HL7AuthorizationDialog (mainFrame,"Notice: Loading HL7 V2 Specification"
    					,HL7AuthorizationDialog.HL7_V2_WARNING_CONTEXT_FILE_PATH);
 
    		}
            else if (getName().equals( COMMAND_BUILD_V2_CORE))
    		{
    			V2MetaBasicInstallDialog dialog = new V2MetaBasicInstallDialog(mainFrame); 
    			DefaultSettings.centerWindow(dialog);
    			//dialog.setViewOnly(true);
		        dialog.setVisible(true);

    		}
            else if (getName().equals( COMMAND_BUILD_V_MESSAGE))
    		{
                V2MetaCollectorDialog dialog = new V2MetaCollectorDialog(mainFrame);
//    			    			HL7AuthorizationDialog dialog=	new HL7AuthorizationDialog (mainFrame,"Notice: Loading HL7 V3 Specification"
//    					,HL7AuthorizationDialog.HL7_V2_WARNING_CONTEXT_FILE_PATH);
    			DefaultSettings.centerWindow(dialog);
    			//dialog.setViewOnly(true);
		        dialog.setVisible(true);

    		}
        }

        return true;
        
    }

    /**
     * Return the associated UI component.
     *
     * @return the associated UI component.
     */
    protected Component getAssociatedUIComponent() {
        return mainFrame;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
