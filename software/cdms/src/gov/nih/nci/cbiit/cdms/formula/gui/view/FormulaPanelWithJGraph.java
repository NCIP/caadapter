/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.view;

import gov.nih.nci.cbiit.cdms.formula.core.*;
import gov.nih.nci.cbiit.cdms.formula.gui.SplitCentralPane;
import gov.nih.nci.cbiit.cdms.formula.gui.FrameMain;

import javax.swing.*;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.graph.*;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Jan 7, 2011
 * Time: 3:53:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormulaPanelWithJGraph extends JPanel
{
    private BaseMeta controlMeta;
    private JComponent nameLabel;

    //JScrollPane mainP;
    JGraph graph = null;

    private int INSET = 3;
    private double SHRINK_RATE = 0.9;
    //private Hashtable<TermMeta, DefaultGraphCell> hashTbl = new Hashtable<TermMeta, DefaultGraphCell>();

    //private Hashtable<DefaultGraphCell, TermMeta> expressionHashTbl = new Hashtable<DefaultGraphCell, TermMeta>();
    //private Hashtable<DefaultGraphCell, TermMeta> nonExpressionHashTbl = new Hashtable<DefaultGraphCell, TermMeta>();
    private Hashtable<DefaultGraphCell, TermView> hashTbl = null;

    private TermView selectedTermView = null;

    private java.util.List<DefaultGraphCell> graphCellList = null;

    //private FormulaMeta fomulaMeta = null;

    private SplitCentralPane parentPane;

    private Dimension preferredDim = new Dimension(1,1);
    private ViewMouseAdapter mouseAdapter = null;


    public FormulaPanelWithJGraph(BaseMeta formula, SplitCentralPane parentPane, Dimension preferredDim)
    {
        super();
        this.parentPane = parentPane;
        controlMeta=formula;
        this.preferredDim = preferredDim;
        refresh();
//        if (controlMeta==null)
//            return;
//        if (controlMeta instanceof FormulaMeta)
//        {
//            System.out.println("DDDD 99  getX=" + this.getX()+ ", getWidh="+this.getWidth()/2 + ", getY=" + this.getY() + ", getHeight=" +this.getHeight()/2);
//            refresh();
//        }
//        else
//        {
//            nameLabel=new JLabel("Formula Store:"+controlMeta.getName());
//            nameLabel.setLocation(this.getX()+this.getWidth()/2,
//                    this.getY()+this.getHeight()/2);
//            add(nameLabel);
//        }
    }
    protected void refresh()
    {
        if ((controlMeta != null)&&(controlMeta instanceof FormulaMeta))
        {
            refresh((FormulaMeta) controlMeta);
        }
        else refresh(null);
    }
    protected void refresh(FormulaMeta f)
    {
        hashTbl = new Hashtable<DefaultGraphCell, TermView>();
        graphCellList = new ArrayList<DefaultGraphCell>();

        if (graph != null) this.remove(graph);

        initFormulaUI(f);
        this.setLayout(new BorderLayout());
        this.add(graph, BorderLayout.CENTER);

        if ((parentPane != null)&&(parentPane.getScrollPane() != null)) parentPane.getScrollPane().validate();
    }

    private void initFormulaUI(FormulaMeta f)
    {
        String lbText="No formula is selected";
        if (f!=null)
        {
            lbText=f.getName();
            if (f.getExpression().getUnit()!=null
                    &&f.getExpression().getUnit().trim().length()>0)
                lbText=lbText+"("+f.getExpression().getUnit()+")";
            lbText=lbText+" = ";
        }

        GraphModel model = new DefaultGraphModel();
        GraphLayoutCache view = new GraphLayoutCache(model, new DefaultCellViewFactory());

        graph = new JGraph(model, view);

        int initialFontSize = 22;
        int initialXPoint = 20;
        DefaultGraphCell baseCell = new DefaultGraphCell(lbText);

        Font font = new Font("DialogInput", Font.ITALIC + Font.BOLD, initialFontSize);

        Rectangle2D rec = font.getStringBounds(lbText, new FontRenderContext(null, true, true));
        double width = rec.getWidth();
        double height = rec.getHeight();

        double preferredHieght = 0.0;
        if (preferredDim != null) preferredHieght = preferredDim.getHeight();

        double cHeight = this.getBounds().getHeight();
        if (cHeight == 0.0) cHeight = preferredHieght;

        double x = initialXPoint;
        double y = cHeight / 2;

        if (f == null)
        {
            if (y < (height / 2) + INSET) y = (height / 2) + INSET + 30;
            setJGraphCell(null, baseCell, font, x, y - (height / 2), width, height, Color.GRAY);
            graph.getGraphLayoutCache().insert(new DefaultGraphCell[] {baseCell} );
            graph.setEditable(false);
            return;
        }

        double[] dbls = estimateAllocSize(f.getExpression(), initialFontSize, false);

        if (y < (dbls[1] / 2) + INSET) y = (dbls[1] / 2) + INSET + 30;

        setJGraphCell(null, baseCell, font, x, y - (height / 2), width, height, Color.green);

        drawTerm(f.getExpression(), null, x + width + INSET, y, initialFontSize, false, false);

        DefaultGraphCell[] insertedCells = new DefaultGraphCell[graphCellList.size()];
        for (int i=0;i<graphCellList.size();i++)
        {
            DefaultGraphCell c = graphCellList.get(i);
            insertedCells[i] = c;
        }
        graph.getGraphLayoutCache().insert(insertedCells);

//        graph.getModel().addGraphModelListener
//        (
//            new GraphModelListener()
//            {
//                public void graphChanged(GraphModelEvent e)
//                {
//                    System.out.println("DDDD 888 graphChanged(GraphModelEvent e)");
//                }
//            }
//        );

        graph.addGraphSelectionListener
        (
            new GraphSelectionListener()
            {
                public void valueChanged(GraphSelectionEvent e)
                {
                    Object o = e.getCell();
                    if ((o == null)||(!(o instanceof DefaultGraphCell))) return;

                    DefaultGraphCell d = (DefaultGraphCell) o;

                    TermView tView = hashTbl.get(d);
                    if (tView == null)
                    {
                        selectedTermView = null;
                        if ((controlMeta != null)&&(controlMeta instanceof FormulaMeta))
                        {
                            //System.out.println("DDDD7-1 TermView is NULL");
                            FrameMain frame=FrameMain.getSingletonInstance();
                            frame.getMainPanel().getRightSplitPanel().getPropertiePanel().updateProptiesDisplay(controlMeta);
                        }
                    }
                    else
                    {
                        selectedTermView = tView;
                        //System.out.println("DDDD7 TermView=" + tView.getTerm().toString());
                        FrameMain frame=FrameMain.getSingletonInstance();
                        frame.getMainPanel().getRightSplitPanel().getPropertiePanel().updateProptiesDisplay(selectedTermView.getTerm());
                    }
                }
            }
        );

        if (mouseAdapter == null) mouseAdapter = new ViewMouseAdapter(this);
        graph.addMouseListener(mouseAdapter);

        graph.setEditable(false);
        //return new JScrollPane(graph);

    }

    private double[] drawTerm(TermMeta meta, TermView parentView, double xPosition, double yPosition, int fontSize, boolean withParenthesis, boolean isEstimate)
    {
        TermType termType = meta.getType();
        TermView tView = new TermView(meta, (int) xPosition, (int) yPosition);
        if (parentView != null) tView.setParentView(parentView);
        Rectangle2D rec = null;
        if (termType == TermType.CONSTANT)
        {
            DefaultGraphCell baseCell = new DefaultGraphCell(meta.getValue());
            Font font = new Font(Font.DIALOG_INPUT, Font.BOLD, fontSize);
            rec = font.getStringBounds(meta.getValue(), new FontRenderContext(null, true, true));
            if (!isEstimate)
            {
                setJGraphCell(tView, baseCell, font, xPosition, yPosition-(rec.getHeight() / 2), rec.getWidth(), rec.getHeight(), Color.cyan);
                hashTbl.put(baseCell, tView);
            }
        }
        else if (termType == TermType.VARIABLE)
        {
            DefaultGraphCell baseCell = new DefaultGraphCell(meta.getValue());
            Font font = new Font(Font.DIALOG_INPUT, Font.ITALIC + Font.BOLD, fontSize);
            rec = font.getStringBounds(meta.getValue(), new FontRenderContext(null, true, true));
            if (!isEstimate)
            {
                setJGraphCell(tView, baseCell, font, xPosition, yPosition-(rec.getHeight() / 2), rec.getWidth(), rec.getHeight(), Color.cyan);
                hashTbl.put(baseCell, tView);
            }
        }
        else if (termType == TermType.UNKNOWN)
        {
            DefaultGraphCell baseCell = new DefaultGraphCell("UNKNOWN");
            Font font = new Font(Font.DIALOG_INPUT, Font.BOLD, fontSize);
            rec = font.getStringBounds("UNKNOWN", new FontRenderContext(null, true, true));
            if (!isEstimate)
            {
                setJGraphCell(tView, baseCell, font, xPosition, yPosition-(rec.getHeight() / 2), rec.getWidth(), rec.getHeight(), Color.gray);
                hashTbl.put(baseCell, tView);
            }
        }


        if (termType != TermType.EXPRESSION)
        {
            return new double[] {rec.getWidth(), rec.getHeight(), rec.getHeight() / 2, 0.0};
        }

        OperationType operType = meta.getOperation();
        double numberOfSubParenthesis = 0.0;
        double[] parenthesisPositionData = null;

        while (withParenthesis)
        {
            if ((operType == OperationType.POWER)||
                (operType == OperationType.LOGARITHM))
            {
                withParenthesis = false;
                break;
            }

            Font font = new Font(Font.DIALOG_INPUT, Font.BOLD, fontSize);
            rec = font.getStringBounds("(", new FontRenderContext(null, true, true));
            parenthesisPositionData = new double[] {xPosition, yPosition - (rec.getHeight() / 2), rec.getWidth(), rec.getHeight()};
            xPosition = xPosition + rec.getWidth();
            break;
        }

        java.util.List<TermMeta> termList = meta.getTerm();

        double[] sizeFormer = null;
        double[] sizeLater = null;
        double xEndPosition = 0.0;
        double yEndPosition = 0.0;
        double yCentralPosition = 0.0;

        if ((operType == OperationType.MULTIPLICATION)||
            (operType == OperationType.SUBTRACTION)||
            (operType == OperationType.ADDITION))
        {
            sizeFormer = estimateAllocSize(termList.get(0), fontSize, (termList.get(0).getType() == TermType.EXPRESSION));
            sizeLater = estimateAllocSize(termList.get(1), fontSize, (termList.get(1).getType() == TermType.EXPRESSION));

            numberOfSubParenthesis = Math.max(sizeFormer[3], sizeLater[3]);

            DefaultGraphCell baseCell = new DefaultGraphCell(operType.toString().trim());
            Font font = new Font(Font.DIALOG_INPUT, Font.BOLD, fontSize);
            rec = font.getStringBounds(operType.toString().trim(), new FontRenderContext(null, true, true));

            if (!isEstimate)
            {
                setJGraphCell(tView, baseCell, font, xPosition + sizeFormer[0] + INSET, yPosition-(rec.getHeight() / 4), rec.getWidth(), (fontSize/10)*9, Color.red);
                hashTbl.put(baseCell, tView);
                //expressionHashTbl.put(baseCell, meta);
                drawTerm(termList.get(0), tView, xPosition , yPosition, fontSize, (termList.get(0).getType() == TermType.EXPRESSION), false);
                drawTerm(termList.get(1), tView, xPosition + sizeFormer[0] + (INSET*2) + rec.getWidth() , yPosition, fontSize , (termList.get(1).getType() == TermType.EXPRESSION), false);
            }
            xEndPosition = xPosition + sizeFormer[0] + (INSET*2) + sizeLater[0] + rec.getWidth();
            yEndPosition = yPosition + Math.max(sizeFormer[1], sizeLater[1]);
        }
        if (operType == OperationType.DIVISION)
        {
            sizeFormer = estimateAllocSize(termList.get(0), fontSize, false);
            sizeLater = estimateAllocSize(termList.get(1), fontSize, false);

            //numberOfSubParenthesis = Math.max(sizeFormer[2], sizeLater[2]);

            double width = Math.max(sizeFormer[0] + (2*INSET), sizeLater[0]) + (2*INSET);
            double height = sizeFormer[1] + sizeLater[1] + (2*INSET);


            if (!isEstimate)
            {
                DefaultEdge edge = new DefaultEdge();

                java.util.List<Point> points = new ArrayList<Point>();
                points.add(new Point((int) xPosition, (int)yPosition));
                points.add(new Point((int)(xPosition + width), (int)yPosition));

                setJGraphEdge(tView, edge, points);

                hashTbl.put(edge, tView);
                //expressionHashTbl.put(edge, meta);

                double yPos1 = yPosition - INSET - (sizeFormer[1] - sizeFormer[2]);
                double yPos2 = yPosition + INSET + sizeLater[2];

                drawTerm(termList.get(0), tView, xPosition + INSET + (width / 2) - (sizeFormer[0] / 2) , yPos1, fontSize, false, false);
                drawTerm(termList.get(1), tView, xPosition + INSET + (width / 2) - (sizeLater[0] / 2) , yPos2, fontSize, false, false);
            }
            xEndPosition = xPosition + width;
            yEndPosition = yPosition + height;
            yCentralPosition = sizeFormer[1] + INSET;
        }
        if (operType == OperationType.POWER)
        {
            sizeFormer = estimateAllocSize(termList.get(0), fontSize, (termList.get(0).getType() == TermType.EXPRESSION));
            sizeLater = estimateAllocSize(termList.get(1), (int) (fontSize * SHRINK_RATE), (termList.get(1).getType() == TermType.EXPRESSION));

            //numberOfSubParenthesis = Math.max(sizeFormer[2], sizeLater[2]);

            DefaultGraphCell baseCell = new DefaultGraphCell(operType.toString().trim());
            Font font = new Font(Font.DIALOG_INPUT, Font.BOLD, (fontSize / 2));//(int) (fontSize * SHRINK_RATE * SHRINK_RATE));
            rec = font.getStringBounds(operType.toString().trim(), new FontRenderContext(null, true, true));

            if (!isEstimate)
            {
                setJGraphCell(tView, baseCell, font, xPosition + sizeFormer[0], yPosition - (sizeLater[1] / 2), rec.getWidth(), rec.getHeight(), Color.red);
                hashTbl.put(baseCell, tView);
                //expressionHashTbl.put(baseCell, meta);
                //nonExpressionHashTbl.put(baseCell, meta);
                //drawTerm(termList.get(0), xPosition, yPosition + (sizeLater[1] / 2), fontSize);
                drawTerm(termList.get(0), tView, xPosition, yPosition, fontSize, (termList.get(0).getType() == TermType.EXPRESSION), false);
                drawTerm(termList.get(1), tView, xPosition + rec.getWidth() + sizeFormer[0], yPosition - sizeLater[1], (int) (fontSize * SHRINK_RATE), (termList.get(1).getType() == TermType.EXPRESSION), false);
            }
            xEndPosition = rec.getWidth() + xPosition + sizeFormer[0] + sizeLater[0];
            yEndPosition = yPosition + sizeFormer[1] + sizeLater[1];
            if (sizeFormer[2] >= (sizeLater[1]*1.5))
            {
                yEndPosition = yPosition + sizeFormer[1];
                yCentralPosition = sizeFormer[2];
            }
            else
            {
                yEndPosition = yPosition + (sizeLater[1]*1.5) + (sizeFormer[1] - sizeFormer[2]);
                yCentralPosition = yPosition + (sizeLater[1]*1.5);
            }

        }
        if (operType == OperationType.SQUAREROOT)
        {
            sizeFormer = estimateAllocSize(termList.get(0), fontSize, false);

            //numberOfSubParenthesis = sizeFormer[2];

            double width = sizeFormer[0] + (INSET * 4);
            double height = sizeFormer[1] + INSET;

            DefaultEdge edge = new DefaultEdge();

            if (!isEstimate)
            {
                java.util.List<Point> points = new ArrayList<Point>();
                points.add(new Point((int) xPosition, (int)(yPosition + (height / 2) - (INSET*2))));
                points.add(new Point((int)(xPosition + INSET*2), (int)(yPosition + (height / 2))));
                points.add(new Point((int) (xPosition + (INSET*4)), (int)(yPosition - (height / 2))));
                points.add(new Point((int)(xPosition + width), (int)(yPosition - (height / 2))));

                setJGraphEdge(tView, edge, points);
                hashTbl.put(edge, tView);
                //expressionHashTbl.put(edge, meta);

                drawTerm(termList.get(0), tView, xPosition + (4*INSET) , yPosition + INSET, fontSize, false, false);
            }
            xEndPosition = xPosition + width;
            yEndPosition = yPosition + height;
            yCentralPosition = (sizeFormer[1] / 2) + INSET;
        }
        if (operType == OperationType.LOGARITHM)
        {
            sizeFormer = estimateAllocSize(termList.get(0), (int) (fontSize * SHRINK_RATE), (termList.get(0).getType() == TermType.EXPRESSION));
            sizeLater = estimateAllocSize(termList.get(1), fontSize, (termList.get(1).getType() == TermType.EXPRESSION));

            //numberOfSubParenthesis = Math.max(sizeFormer[2], sizeLater[2]);

            DefaultGraphCell baseCell = new DefaultGraphCell("log");
            Font font = new Font(Font.DIALOG_INPUT, Font.BOLD, (int)(fontSize * 1.1));
            rec = font.getStringBounds("log", new FontRenderContext(null, true, true));

            //double width = rec.getWidth() + sizeFormer[0] + sizeLater[0] + 2*INSET;
            //double height = fontSize + ((fontSize * SHRINK_RATE) / 2);

            if (!isEstimate)
            {
                setJGraphCell(tView, baseCell, font, xPosition, yPosition - (rec.getHeight() / 2), rec.getWidth(), rec.getHeight(), Color.red);
                hashTbl.put(baseCell, tView);
                //expressionHashTbl.put(baseCell, meta);

                drawTerm(termList.get(0), tView, xPosition + rec.getWidth(), yPosition + (sizeFormer[1] / 2), (int) (fontSize * SHRINK_RATE), (termList.get(0).getType() == TermType.EXPRESSION), false);
                drawTerm(termList.get(1), tView, xPosition + rec.getWidth() + sizeFormer[0] + INSET, yPosition, fontSize, (termList.get(1).getType() == TermType.EXPRESSION), false);
            }
            xEndPosition = xPosition + rec.getWidth() + sizeFormer[0] + INSET + sizeLater[0];
            yEndPosition = yPosition + (sizeFormer[1] / 2) + sizeLater[1];
            yCentralPosition = yPosition + (sizeLater[1] / 2);
        }

        if (withParenthesis)
        {
            String openB = "[";
            String closeB = "]";
            Color color = Color.pink;
            if (numberOfSubParenthesis == 0.0)
            {
                openB = "(";
                closeB = ")";
                color = Color.magenta;
            }
            else if (numberOfSubParenthesis == 1.0)
            {
                openB = "{";
                closeB = "}";
                color = Color.orange;
            }
            DefaultGraphCell baseCell1 = new DefaultGraphCell(openB);
            Font font = new Font(Font.DIALOG_INPUT, Font.BOLD, fontSize);

            DefaultGraphCell baseCell2 = new DefaultGraphCell(closeB);

            rec = font.getStringBounds(openB, new FontRenderContext(null, true, true));
            xEndPosition= xEndPosition + INSET;
            if (!isEstimate)
            {
                setJGraphCell(null, baseCell1, font, parenthesisPositionData[0], parenthesisPositionData[1], parenthesisPositionData[2], parenthesisPositionData[3], color);
                setJGraphCell(null, baseCell2, font, xEndPosition, yPosition-(rec.getHeight() / 2), rec.getWidth(), rec.getHeight(), color);
            }
            xEndPosition = xEndPosition + rec.getWidth();
            numberOfSubParenthesis++;
        }
        if (yCentralPosition == 0.0) yCentralPosition = yEndPosition / 2;
        return new double[] {xEndPosition, yEndPosition, yCentralPosition, numberOfSubParenthesis};
    }

    private double[] estimateAllocSize(TermMeta meta, int fontSize, boolean withParenthesis)
    {
        return drawTerm(meta, null, 0.0, 0.0, fontSize, withParenthesis, true);
    }

    private void setJGraphCell(TermView termView, DefaultGraphCell baseCell, Font font, double x, double y, double width, double height, Color color)
    {
        GraphConstants.setBounds(baseCell.getAttributes(), new Rectangle2D.Double(x,y,width,height));

        GraphConstants.setFont(baseCell.getAttributes(), font);

        GraphConstants.setGradientColor(baseCell.getAttributes(), color);
        GraphConstants.setOpaque(baseCell.getAttributes(), true);

        GraphConstants.setEditable(baseCell.getAttributes(), false);
        GraphConstants.setMoveable(baseCell.getAttributes(), false);
        GraphConstants.setResize(baseCell.getAttributes(), false);
        GraphConstants.setSizeable(baseCell.getAttributes(), false);

        graphCellList.add(baseCell);
        if (termView != null) termView.setGraphCell(baseCell);
        //return baseCell;
    }

    private void setJGraphEdge(TermView termView, Edge edge, java.util.List<Point> points)
    {
        GraphConstants.setPoints(edge.getAttributes(), points);
        GraphConstants.setLineBegin(edge.getAttributes(), GraphConstants.ARROW_NONE);
        GraphConstants.setEditable(edge.getAttributes(), false);
        GraphConstants.setMoveable(edge.getAttributes(), false);
        GraphConstants.setResize(edge.getAttributes(), false);
        GraphConstants.setSizeable(edge.getAttributes(), false);

        graphCellList.add((DefaultGraphCell) edge);

        if (termView != null)
        {
            termView.setGraphCell((DefaultGraphCell) edge);
            termView.setPoints(points);
        }
            //return baseCell;
    }
    public JGraph getJGraph()
    {
        return graph;
    }
    public TermView getSelectedTermView()
    {
        return selectedTermView;
    }
    public void setNullToSelectedTermView()
    {
        selectedTermView = null;
    }
    public BaseMeta getControlMeta()
    {
        return controlMeta;
    }
    public java.util.List<DefaultGraphCell> getGraphCellList()
    {
        return graphCellList;
    }
    public Dimension getDimension()
    {
        int width = (int) this.getBounds().getWidth();
        int height = (int) this.getBounds().getHeight();

        if ((width * height) > 0) return new Dimension(width, height);
        return preferredDim;
    }
}
