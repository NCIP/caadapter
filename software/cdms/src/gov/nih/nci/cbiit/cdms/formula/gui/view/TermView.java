/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.view;

import javax.swing.JComponent;

import gov.nih.nci.cbiit.cdms.formula.core.OperationType;
import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermType;

import java.awt.*;

import org.jgraph.graph.DefaultGraphCell;

public class TermView {
    public static final int VIEW_COMPONENT_HEIGHT=20;
    public static final int VIEW_CHARACTER_WEIDTH=7;
    public static int VIEW_COMPONENT_PADDING=2;
    public static int VIEW_SQUARE_ROOT_LEADING=15;
    private TermView parentView;
    private TermView firtTermView;
    private TermView secondTermView;
     private TermMeta term;
    private JComponent termOperatioinComponent;
    private JComponent termUiComponent;
    private JComponent startComponent;
    private JComponent endComponent;
    private int x, y, width, height;
    private java.util.List<Point> points = null;
    private DefaultGraphCell graphCell = null;

    public TermView(TermMeta meta, int locationX, int locationY)
    {
        term=meta;
        x=locationX;
        y=locationY;
        buildOperationComponent();

        processTermMeta(term);
        buildEndComponents();

     }

    public TermView getParentView() {
        return parentView;
    }

    public void setParentView(TermView parentView) {
        this.parentView = parentView;
    }

    public TermView getFirtTermView() {
        return firtTermView;
    }

    public int getHeight() {
        return height;
    }
    public TermView getSecondTermView() {
        return secondTermView;
    }

    public TermMeta getTerm() {
        return term;
    }

    public JComponent getTermOperatioinComponent() {
        return termOperatioinComponent;
    }

    public JComponent getTermUiComponent()
    {
        return termUiComponent;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getX() {
        return x;
    }

    public void setX(int xNew) {

        int xDif=xNew-x;
        x=xNew;
        //shift child term to new position
        if (getFirtTermView()!=null)
            getFirtTermView().setX(getFirtTermView().getX()+xDif);

        if (getSecondTermView()!=null)
            getSecondTermView().setX(getSecondTermView().getX()+xDif);
    }

    public int getY() {
        return y;
    }


    public void setY(int yNew) {
        int yDif=yNew-y;
        y=yNew;
        //shift child term to new position
        if (getFirtTermView()!=null)
            getFirtTermView().setY(getFirtTermView().getY()+yDif);

        if (getSecondTermView()!=null)
            getSecondTermView().setY(getSecondTermView().getY()+yDif);
    }

    public JComponent getStartComponent() {
        return startComponent;
    }

    public JComponent getEndComponent() {
        return endComponent;
    }

    private void processTermMeta(TermMeta meta)
    {
        if (meta==null)
            return;
        if (meta.getType().value().equals(TermType.UNKNOWN.value())
                ||meta.getType().value().equals(TermType.CONSTANT.value())
                ||(meta.getType().value().equals(TermType.VARIABLE.value())))
        {
            termUiComponent=new TermUiComponent(this);
            width=(int)termUiComponent.getBounds().getWidth();
            height= VIEW_COMPONENT_HEIGHT;
            return;
        }

        //build the view of the first term
        int x1=x, y1=y, x2=x, y2=y;
        switch (meta.getOperation())
        {
            case	DIVISION:
                y1=y;
                y2=y+VIEW_COMPONENT_HEIGHT;
                break;
            case LOGARITHM:
                y1=y+VIEW_COMPONENT_HEIGHT/3;
                x1=x+(int)termOperatioinComponent.getBounds().getWidth();
                break;
            case POWER:
                y2=y-VIEW_COMPONENT_HEIGHT/2;
                break;
            case SQUAREROOT:
                //shift the term 15 pixel right for the squareRoot symbol
                x=x+VIEW_SQUARE_ROOT_LEADING;
                x1=x;
                break;
            default:
                break;
        }

        firtTermView=new TermView(meta.getTerm().get(0),x1,y1);
        firtTermView.setParentView(this);
        x2=x1+firtTermView.getWidth();
        if (termOperatioinComponent!=null)
            x2=x2+(int)termOperatioinComponent.getWidth()+VIEW_COMPONENT_PADDING;

        if (meta.getOperation().equals(OperationType.DIVISION))
        {
            x2=x1;
        }
        else if (meta.getOperation().equals(OperationType.LOGARITHM))
        {
            x2=x1+20;
        }

        secondTermView=null;
        if (meta.getTerm().size()>1)
        {
            secondTermView=new TermView(meta.getTerm().get(1), x2, y2);
            secondTermView.setParentView(this);
            //set the size of the current view
            width=firtTermView.getWidth()+secondTermView.getWidth();
            height=Math.max(firtTermView.getY()+firtTermView.getHeight(),
                    secondTermView.getY()+secondTermView.getHeight())
                        -Math.min(firtTermView.getY(), secondTermView.getY());
        }
        else
        {
            width=firtTermView.getWidth();
            height=firtTermView.getHeight();
        }
        if (termOperatioinComponent!=null)
        {
            width=width+termOperatioinComponent.getWidth();
        }

        if (meta.getOperation().equals(OperationType.ADDITION)||
            meta.getOperation().equals(OperationType.SUBTRACTION)||
            meta.getOperation().equals(OperationType.MULTIPLICATION))
            adjustVerticalPosition();
        else if (meta.getOperation().equals(OperationType.DIVISION))
            adjustHorizontalPosition();
    }

    /**
     * Make sure the middle vertical line of the two terms vertical line
     */
    private void adjustHorizontalPosition()
    {
        int widthDif=Math.abs(getFirtTermView().getWidth()-getSecondTermView().getWidth());
        if (getFirtTermView().getWidth()<this.getSecondTermView().getWidth())
            getFirtTermView().setX(getFirtTermView().getX()+widthDif/2);
        else
            getSecondTermView().setX(getSecondTermView().getX()+widthDif/2);
    }
    /**
     * Make sure the middle horizontal line of the two terms and the operation symbol is on the
     * same level
     */
    private void adjustVerticalPosition()
    {
        if (getFirtTermView().getHeight()==getSecondTermView().getHeight())
            return;

        int absDif=Math.abs(getFirtTermView().getHeight()-getSecondTermView().getHeight());
        //do not adjust if the diffence of the heights of the two views
        //is less than the the height of one label
        if (absDif<VIEW_COMPONENT_HEIGHT)
            return;

        if (getFirtTermView().getHeight()<getSecondTermView().getHeight())
            getFirtTermView().setY(getFirtTermView().getY()+absDif/2);
        else
            getSecondTermView().setY(getSecondTermView().getY()+absDif/2);
    }

    private void buildOperationComponent()
    {
        if (term.getOperation()==null)
            return;
        switch(term.getOperation())
        {
            case  ADDITION:
                termOperatioinComponent=new TermUiComponent(
                         term.getOperation().toString());
                break;
            case  SUBTRACTION:
                termOperatioinComponent=new TermUiComponent(
                         term.getOperation().toString());
                break;
            case  MULTIPLICATION:
                termOperatioinComponent=new TermUiComponent(
                         term.getOperation().toString());
                break;
            case  EXPONENTIAL:
                termOperatioinComponent=new TermUiComponent(
                         term.getOperation().toString());
                break;
            case  LOGARITHM:
                termOperatioinComponent=new TermUiComponent(
                         term.getOperation().toString());
                break;
            default:
                break;
        }
        if (termOperatioinComponent!=null)
            ((TermUiComponent)termOperatioinComponent).setViewMeta(this);
    }

    private void buildEndComponents()
    {
        if (term.getOperation()==null)
            return;

        if (term.getName().equals("divisor")
                ||term.getName().equals("dividend"))
            return;

        if (term.getOperation().equals(OperationType.ADDITION)
                ||term.getOperation().equals(OperationType.SUBTRACTION))
        {
            startComponent=new TermUiComponent("(");
             endComponent=new TermUiComponent(")");
        }
        if (startComponent!=null)
        {
            width=width+startComponent.getWidth();
        }

        if (endComponent!=null)
            width=width+endComponent.getWidth();
    }

    public String toString()
    {
        StringBuffer rtnB=new StringBuffer();
        rtnB.append(term.toString()+"\n(width,height)=("+width+","+height+")");
        rtnB.append("(x,y)=("+this.x+","+y+")");
        return rtnB.toString();
    }

    public java.util.List<Point> getPoints()
    {
        return points;
    }
    public void setPoints(java.util.List<Point> points)
    {
        this.points = points;
    }
    public DefaultGraphCell getGraphCell()
    {
        return graphCell;
    }
    public void setGraphCell(DefaultGraphCell grCell)
    {
        graphCell = grCell;
    }
}
