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

public class ViewMouseAdapter extends MouseAdapter
{
    FormulaPanelWithJGraph panelJGraph = null;

    public ViewMouseAdapter()
    {

    }
    public ViewMouseAdapter(FormulaPanelWithJGraph panel)
    {
        panelJGraph = panel;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        super.mouseClicked(e);
        BaseMeta baseMeta=FrameMain.getSingletonInstance().getMainPanel().getCentralSplit().getControllMeta();
        if (!(baseMeta instanceof FormulaMeta))
            return;
        FormulaMeta formula=(FormulaMeta)baseMeta;
        TermView termView = null;
        if (panelJGraph == null)
        {
            TermUiComponent metaUi=(TermUiComponent)e.getSource();
            termView = metaUi.getViewMeta();
        }
        else termView = panelJGraph.getSelectedTermView();

        if (termView == null) return;
        if (e.getClickCount()==1)
        {
            System.out.println("ViewMouseAdapter.mouseClicked()..Single click:"+termView);
        }
        if (e.getClickCount()==2)
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
        else if (SwingUtilities.isRightMouseButton(e))
        {
            System.out.println("Mouse clicked: RightMouseButton");
            Container parentC = e.getComponent().getParent();
            while ( !(parentC instanceof JScrollPane))
            {
                parentC=parentC.getParent();
            }
            //if (panelJGraph != null) panelJGraph.setNullToSelectedTermView();
            // Create PopupMenu for the Cell
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
        refreshJGraphPanel(termView);

    }

    private boolean refreshJGraphPanel(TermView tView)
    {
        if (panelJGraph == null) return false;
        if (tView == null) return false;
        DefaultGraphCell graphCell = tView.getGraphCell();
        if ((graphCell == null)||(!(graphCell instanceof Edge))) return false;

        Edge edge = (Edge) graphCell;

        java.util.List<Point> points = tView.getPoints();
        if ((points == null)||(points.size() == 0)) return false;

        AttributeMap map = edge.getAttributes();
        java.util.List mapPoints = GraphConstants.getPoints(map);
        if ((mapPoints == null)||(mapPoints.size() == 0)) return false;

        boolean cTag = true;
        for (int i=0;i<Math.max(mapPoints.size(), points.size());i++)
        {
            double mX = -99.99;
            double mY = -99.99;
            double bX = -99.99;
            double bY = -99.99;
            if (i < points.size())
            {
                Point p = points.get(i);
                bX =  p.getX();
                bY =  p.getY();
            }
            if (i < mapPoints.size())
            {
                Point2D p = (Point2D) mapPoints.get(i);
                mX =  p.getX();
                mY =  p.getY();
            }

            if (mX != bX) cTag = false;
            if (mY != bY) cTag = false;
            //System.out.println("  DDD Compare points("+i+")"+cTag+" map=" + mX+ ":" + mY + ", View=" + bX + ":" + bY);
        }

        if (((points.size() != mapPoints.size()))||(!cTag))
        {
            panelJGraph.refresh();
            panelJGraph.setNullToSelectedTermView();
            return true;
        }
        return false;
    }

    public void mouseReleased(MouseEvent e)
    {

        if (SwingUtilities.isRightMouseButton(e))
            System.out.println("DDD9 : mouseReleased(R), e.getClickCount()" + e.getClickCount());
        else if (SwingUtilities.isLeftMouseButton(e))
            System.out.println("DDD9 : mouseReleased(L), e.getClickCount()" + e.getClickCount());
        mouseClicked(e);
    }
//    public void mouseReleased(MouseEvent e)
//    //public void mouseDragged(MouseEvent e)
//    {
        //refreshJGraphPanel(panelJGraph.getSelectedTermView());
//        if (panelJGraph != null)
//        {
//            System.out.println("DDD9 : mouseReleased, e.getClickCount()=" + e.getClickCount());
//            panelJGraph.refresh();
//
//        }
//    }
}
