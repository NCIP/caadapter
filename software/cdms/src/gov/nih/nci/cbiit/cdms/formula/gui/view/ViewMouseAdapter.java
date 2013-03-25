/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.view;

import gov.nih.nci.cbiit.cdms.formula.core.*;
import gov.nih.nci.cbiit.cdms.formula.gui.EditTermWizard;
import gov.nih.nci.cbiit.cdms.formula.gui.FrameMain;
import gov.nih.nci.cbiit.cdms.formula.gui.action.EditTermAction;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.Edge;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;
import org.jgraph.JGraph;

public class ViewMouseAdapter extends MouseAdapter
{
    FormulaPanelWithJGraph panelJGraph = null;
    boolean isMouseEntered = false;

    public ViewMouseAdapter()
    {

    }
    public ViewMouseAdapter(FormulaPanelWithJGraph panel)
    {
        panelJGraph = panel;
    }
    @Override
    public void mouseClicked(MouseEvent e)
    {
        // TODO Auto-generated method stub
        super.mouseClicked(e);
        BaseMeta baseMeta=FrameMain.getSingletonInstance().getMainPanel().getCentralSplit().getControllMeta();
        if (!(baseMeta instanceof FormulaMeta))
            return;
        FormulaMeta formula=(FormulaMeta)baseMeta;

        TermView termView = null;
        DefaultGraphCell selectedGraphCell = null;

        if (panelJGraph == null)
        {
            TermUiComponent metaUi=(TermUiComponent)e.getSource();
            termView = metaUi.getViewMeta();
        }
        else
        {
            termView = panelJGraph.getSelectedTermView();
            Object ob = panelJGraph.getJGraph().getSelectionCell();
            if ((ob != null)||(ob instanceof DefaultGraphCell)) selectedGraphCell = (DefaultGraphCell) ob;
        }

        if (termView == null) return;

        if ((e.getClickCount()==1)&&(SwingUtilities.isLeftMouseButton(e)))
        {
            if (panelJGraph != null)
            {
                JGraph graph = panelJGraph.getJGraph();
                Object ob = graph.getSelectionCellAt(e.getPoint());
                if (ob == null)
                {
                    FrameMain frame=FrameMain.getSingletonInstance();
                    frame.getMainPanel().getRightSplitPanel().getPropertiePanel().updateProptiesDisplay(panelJGraph.getControlMeta());
                    System.out.println("ViewMouseAdapter.mouseClicked()....Single click: Not selected");
                }
                else
                {
                    System.out.println("ViewMouseAdapter.mouseClicked()..Single click:"+termView);
                }
            }
        }
        else if ((e.getClickCount()==2)&&(SwingUtilities.isLeftMouseButton(e)))
        {
            System.out.println("ViewMouseAdapter.mouseClicked()..double click:"+termView);
            //if (panelJGraph != null) panelJGraph.setNullToSelectedTermView();
            //do not allow user to edit an expression with double-click
            if(termView.getTerm().getType().equals(TermType.EXPRESSION))
                return;
            if (!formula.getStatus().equals(FormulaStatus.DRAFT))
            {
                Container parentC = e.getComponent().getParent();
                while ( !(parentC instanceof JScrollPane))
                {
                    parentC=parentC.getParent();
                }
                String errMsg="Edition is not allowed:"+formula.getName() +"... status:"+formula.getStatus().value();
                JOptionPane.showMessageDialog(parentC, errMsg, "Editing not allowed", JOptionPane.WARNING_MESSAGE);

                return;
            }
            FrameMain mainFrame=FrameMain.getSingletonInstance();
            EditTermWizard wizard = new EditTermWizard(mainFrame, termView.getTerm(),true);
            wizard.setLocation(mainFrame.getX()+mainFrame.getWidth()/4,
                       mainFrame.getY()+mainFrame.getHeight()/4);
               wizard.setSize((int)mainFrame.getSize().getWidth()/2,
                        (int)mainFrame.getSize().getHeight()/2);
               wizard.setVisible(true);
        }
        else if ((e.getClickCount()==1)&&(SwingUtilities.isRightMouseButton(e)))
        {
            System.out.println("Mouse clicked: RightMouseButton");
            Container parentC = e.getComponent().getParent();
            while ( !(parentC instanceof JScrollPane))
            {
                parentC=parentC.getParent();
            }

            JPopupMenu popupMenu = new JPopupMenu();
            EditTermAction editAction=new EditTermAction("Edit Term", EditTermAction.TYPE_EDIT);
            editAction.setTermView(termView);
            JMenuItem editItem=new JMenuItem(editAction);
            popupMenu.add(editItem);

            EditTermAction deleteAction=new EditTermAction("Delete Term", EditTermAction.TYPE_DELETE);
            deleteAction.setTermView(termView);
            JMenuItem deleteItem=new JMenuItem(deleteAction);
            popupMenu.add(deleteItem);
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
            if (formula.getStatus()!=FormulaStatus.DRAFT)
            {
                editAction.setEnabled(false);
                deleteAction.setEnabled(false);
            }
        }
        refreshJGraphPanel(termView, selectedGraphCell);
    }

    private boolean refreshJGraphPanel(TermView tView, DefaultGraphCell selectedGraphCell)
    {
        if ((panelJGraph == null)||(tView == null)) return false;

        DefaultGraphCell graphCell = tView.getGraphCell();
        if ((graphCell == null)||(!(graphCell instanceof Edge))) return false;

        Edge edge = (Edge) graphCell;

        int cTag = isSameEdgePoints(tView.getPoints(), GraphConstants.getPoints(edge.getAttributes()));
        if (cTag < 0) return false;

        if (cTag == 0)
        {
            java.util.List<DefaultGraphCell> list = panelJGraph.getGraphCellList();
            int seq = -1;
            for(int i=0;i<list.size();i++)
            {
                DefaultGraphCell cell = list.get(i);
                if (cell == selectedGraphCell) seq = i;
            }
            panelJGraph.refresh();
            panelJGraph.setNullToSelectedTermView();

            Object ob = panelJGraph.getJGraph().getSelectionCell();
            if ((ob == null)&&(selectedGraphCell != null)&&(seq >= 0))
            {
                panelJGraph.getJGraph().setSelectionCell(panelJGraph.getGraphCellList().get(seq));
            }

            return true;
        }
        return false;
    }

    private int isSameEdgePoints(java.util.List<Point> points, java.util.List<Point> mapPoints)
    {
        if ((points == null)||(points.size() == 0)) return -1;
        if ((mapPoints == null)||(mapPoints.size() == 0)) return -1;
        //System.out.println("DDDDD ***");
        int cTag = 1;
        for (int i=0;i<Math.max(mapPoints.size(), points.size());i++)
        {
            double mX = -99.99;
            double mY = -99.99;
            double bX = -99.99;
            double bY = -99.99;
            if (i < points.size())
            {
                Point2D p = points.get(i);
                bX =  p.getX();
                bY =  p.getY();
            }
            if (i < mapPoints.size())
            {
                Point2D p = mapPoints.get(i);
                mX =  p.getX();
                mY =  p.getY();
            }

            if (mX != bX) cTag = 0;
            if (mY != bY) cTag = 0;
            //System.out.println("  DDD Compare points("+i+")"+cTag+" X=" + mX+ ":" + bX + ", Y=" + mY + ":" + bY);
        }
        return cTag;
    }

    public void mouseReleased(MouseEvent e)
    {

        if (SwingUtilities.isRightMouseButton(e))
            System.out.println("DDD9 : mouseReleased(R), e.getClickCount()" + e.getClickCount());
        else if (SwingUtilities.isLeftMouseButton(e))
            System.out.println("DDD9 : mouseReleased(L), e.getClickCount()" + e.getClickCount());
        mouseClicked(e);
    }

    public void mouseExited(MouseEvent e)
    {
        System.out.println("DDD77 : mouseExited :: " + isMouseEntered);
        isMouseEntered = false;

        if (panelJGraph == null) return;
        BaseMeta bMeta = panelJGraph.getControlMeta();
        if (bMeta == null) return;
        FrameMain frame=FrameMain.getSingletonInstance();
        frame.getMainPanel().getRightSplitPanel().getPropertiePanel().updateProptiesDisplay(bMeta);
        //System.out.println("DDD77 : mouseExited");
    }
    public void mouseEntered(MouseEvent e)
    {
        System.out.println("DDD55 : mouseEntered :: " + isMouseEntered);
        if (isMouseEntered) return;

        isMouseEntered = true;
        if (panelJGraph == null) return;
        BaseMeta bMeta = panelJGraph.getControlMeta();
        if (bMeta == null) return;
        panelJGraph.refresh();

        //System.out.println("DDD55 : mouseEntered");
    }
}
