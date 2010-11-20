package gov.nih.nci.cbiit.cdms.formula.gui.listener;

import gov.nih.nci.cbiit.cdms.formula.gui.FormulaButtonPane;
import gov.nih.nci.cbiit.cdms.formula.gui.FormulaMainPanel;
import gov.nih.nci.cbiit.cdms.formula.gui.FrameMain;
import gov.nih.nci.cbiit.cdms.formula.gui.NodeContentElement;
import gov.nih.nci.cbiit.cdms.formula.gui.properties.PanelDefaultProperties;
import gov.nih.nci.cbiit.cdms.formula.core.BaseMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;

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
        NodeContentElement element = parentButton.getNodeContentElement();
        if (element == null) return;

        //System.out.println("TTTT4 element " + element.toString() + " : " + element.convertToMeta());
        TermMeta termMeta = element.convertToMeta();
        if (termMeta == null) return;

        try
        {
            propertiePanel = FrameMain.getSingletonInstance().getMainPanel().getRightSplitPanel().getPropertiePanel();
            beforeMeta = propertiePanel.getCurrentBaseMeta();
        }
        catch(Exception ee)
        {
            beforeMeta = null;
            propertiePanel = null;
            return;
        }

        propertiePanel.updateProptiesDisplay(element.convertToMeta());

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
