package gov.nih.nci.caadapter.ui.common.resource;

import gov.nih.nci.caadapter.hl7.mif.v1.BuildResourceUtil;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BuildHL7ResourceAction extends AbstractContextAction {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final String COMMAND_BUILD_V3 = "Build HL7 V3 Resource";
    public static final String COMMAND_BUILD_V2 = "Build HL7 V2 Resource";
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
    		new BuildHL7ResourceDialog(mainFrame,getName(),true, resorcePath).show();

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

