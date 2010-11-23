package gov.nih.nci.cbiit.cdms.formula.gui.listener;

import gov.nih.nci.cbiit.cdms.formula.gui.FormulaButtonPane;
import gov.nih.nci.cbiit.cdms.formula.gui.FormulaMainPanel;
import gov.nih.nci.cbiit.cdms.formula.gui.FrameMain;
import gov.nih.nci.cbiit.cdms.formula.gui.NodeContentElement;
import gov.nih.nci.cbiit.cdms.formula.gui.properties.PanelDefaultProperties;
import gov.nih.nci.cbiit.cdms.formula.core.BaseMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermType;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 19, 2010
 * Time: 12:04:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormulaButtonMouseListener implements MouseListener
{
    FormulaButtonPane parentButton;

    PanelDefaultProperties propertiePanel = null;
    BaseMeta beforeMeta = null;
    public FormulaButtonMouseListener(FormulaButtonPane pane)
    {
        parentButton = pane;
    }
    public void mouseClicked(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent ae)
    {
    }

    public void mouseReleased(MouseEvent ae)
    {
    }

    public void mouseEntered(MouseEvent ae)
    {
        //System.out.println("ww1");
        TermMeta termMeta = null;
        NodeContentElement element = parentButton.getNodeContentElement();
        if (element != null) termMeta = element.convertToMeta();
        if (termMeta == null)
        {
            termMeta = new TermMeta();
            FormulaButtonPane parentPane = parentButton.getParentPane();
            String name = null;
            if (parentPane != null) name = parentPane.getAssignedTermName(parentButton);
            if (name == null) name = "";
            termMeta.setType(TermType.UNKNOWN);
            termMeta.setName(name);
        }
        //System.out.println("ww2");
        try
        {
            propertiePanel = FrameMain.getSingletonInstance().getMainPanel().getRightSplitPanel().getPropertiePanel();
            beforeMeta = propertiePanel.getCurrentBaseMeta();

            if (beforeMeta instanceof FormulaMeta) {} //System.out.println(" FormulaMeta meta name");
            else beforeMeta = null;
        }
        catch(Exception ee)
        {
            beforeMeta = null;
        }

        if (beforeMeta == null)
        {
            try
            {
                //System.out.println(" not FormulaMeta meta name = ");
                FormulaMainPanel p = parentButton.getRootPanel();
                beforeMeta = p.getFormulaMeta();
            }
            catch(Exception ee1)
            {
                beforeMeta = null;
                propertiePanel = null;
                return;
            }
        }

        propertiePanel.updateProptiesDisplay(termMeta);
    }
    public void mouseExited(MouseEvent ae)
    {
        if (beforeMeta == null) return;
        if (propertiePanel == null) return;
        propertiePanel.updateProptiesDisplay(beforeMeta);
        beforeMeta = null;
        propertiePanel = null;
    }
}
