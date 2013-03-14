/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.view;
import java.awt.Graphics;
import java.awt.Point;

import gov.nih.nci.cbiit.cdms.formula.core.BaseMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.core.OperationType;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FormulaPanel extends JPanel{

	private BaseMeta controlMeta;
	private JComponent nameLabel;
	private TermView termView;
	
	public FormulaPanel(BaseMeta formula)
	{
		super();
		controlMeta=formula;
		if (controlMeta==null)
			return;
		if (controlMeta instanceof FormulaMeta)
			initFormulaUI((FormulaMeta) controlMeta);
		else
		{	
			nameLabel=new JLabel("Formula Store:"+controlMeta.getName());
			nameLabel.setLocation(this.getX()+this.getWidth()/2,
					this.getY()+this.getHeight()/2);
			add(nameLabel);
		}
	}

	private void drawSquareRoot(TermView view, int x0, int y0, Graphics g)
	{
		//            p2-----------------p1
		//			  *
		//           *
		//          *
		//   p4*  *
		//    * **
		// p5*   * p3
		Point p1=new Point(x0+view.getWidth()-5, y0-10);
		Point p2=new Point(x0, y0-10);
		Point p3=new Point(x0-5, y0+view.getHeight()-10);
		Point p4=new Point(x0-10, y0+view.getHeight()-15);
		Point p5=new Point(x0-15, y0+view.getHeight()-10);
		g.drawLine(p1.x,p1.y, p2.x, p2.y);
		g.drawLine(p2.x,p2.y, p3.x, p3.y);
		g.drawLine(p3.x,p3.y, p4.x, p4.y);
		g.drawLine(p4.x,p4.y, p5.x, p5.y);		
	}

	private void initFormulaUI(FormulaMeta f)
	{
		String lbText="No formula is selected";
		if (f!=null)
			lbText=f.getName();
		if (f.getExpression().getUnit()!=null
				&&f.getExpression().getUnit().trim().length()>0)
			lbText=lbText+"("+f.getExpression().getUnit()+")";
		
		lbText=lbText+" = ";
		nameLabel=new TermUiComponent(lbText);
		add(nameLabel);
		termView=new TermView(f.getExpression(), 0, 0);
		ViewMouseAdapter mouseListener=new ViewMouseAdapter();
		processViewComponents(termView, mouseListener);
	}
	
	@Override
	protected void paintComponent(Graphics arg0) {
		// TODO Auto-generated method stub
		super.paintComponent(arg0);	
		if (nameLabel==null)
			return;
  
		nameLabel.setLocation(TermView.VIEW_COMPONENT_PADDING*4, getHeight()/2);
		if (termView==null)
			return;
		
		int x0=TermView.VIEW_COMPONENT_PADDING*2
			+ nameLabel.getX()+(int)nameLabel.getBounds().getWidth();
		//y0 is the middle of the left label
		int y0=this.getHeight()/2+TermView.VIEW_COMPONENT_HEIGHT/2; 
		positionTermUiComponent(termView, x0, calculateViewY(termView,y0), arg0);
	}
	
	private int calculateViewY(TermView view, int yStart)
	{
		int y=yStart;
		if (view.getTerm()==null)
			return y;
		if (view.getTerm().getOperation()==null)
			return y;
		switch(view.getTerm().getOperation())
		{
		case DIVISION://make the divider line at bottom of the dividend
			y=yStart-view.getSecondTermView().getY();
			break;
		case SQUAREROOT: //make the centers of square root and left label  at the same level 
			y=yStart-view.getHeight()/2;
			break;
		default	: //make the view start is the bottom of left label
			y=yStart-TermView.VIEW_COMPONENT_HEIGHT/2;
			break;
		}
		return y;
	}
	private void positionTermUiComponent(TermView view, int x0, int y0, Graphics g)
	{
		if (view==null)
			return;
		if (view.getTermUiComponent()!=null)
		{
			JComponent viewUi=view.getTermUiComponent();
			viewUi.setLocation(x0+view.getX(), y0+view.getY());
		}
		if (view.getStartComponent()!=null)
		{
			//set vertical position of the starting and ending parenthesis at middle of term
			view.getStartComponent().setLocation(x0+view.getX(), 
					y0+view.getFirtTermView().getY()
					+(view.getFirtTermView().getHeight()-TermView.VIEW_COMPONENT_HEIGHT)/2);
			int endY=y0+view.getSecondTermView().getY()
				+(view.getSecondTermView().getHeight()-TermView.VIEW_COMPONENT_HEIGHT)/2;
			
			view.getEndComponent().setLocation(x0 + view.getX()+view.getWidth()
					-view.getEndComponent().getWidth(),endY);
			x0=x0+view.getStartComponent().getWidth();
		}
			
		positionTermUiComponent(view.getFirtTermView(), x0, y0, g);
		if (view.getTermOperatioinComponent()!=null)
		{
			if(view.getTerm().getOperation().equals(OperationType.LOGARITHM))
				view.getTermOperatioinComponent().setLocation(x0+view.getX(),
						y0+view.getSecondTermView().getY());
			else
				view.getTermOperatioinComponent().setLocation(x0+view.getX()+view.getFirtTermView().getWidth(), 
						y0+view.getFirtTermView().getY()
						+(view.getFirtTermView().getHeight()-TermView.VIEW_COMPONENT_HEIGHT)/2);
		}
		
		if (view.getTerm().getOperation()==OperationType.DIVISION)
		{
			int yDivider=y0+view.getY()+view.getFirtTermView().getHeight();
			g.drawLine(x0+view.getX(),yDivider, x0+view.getX()+view.getWidth(), yDivider);
		}
		else if (view.getTerm().getOperation()==OperationType.SQUAREROOT)
			drawSquareRoot(view, x0+view.getX(), y0+view.getY(), g);
		int x2=x0;
		int y2=y0;
		if (view.getTerm()!=null&&view.getTerm().getOperation()!=null&&view.getTerm().getOperation().equals(OperationType.DIVISION))
			y2=y0+TermView.VIEW_COMPONENT_HEIGHT/2;
		positionTermUiComponent(view.getSecondTermView(), x2, y2, g);
	}
	
	/**
	 * use the the mouse listener instance for all TermView UI components
	 * @param view
	 * @param listener
	 */
	private void processViewComponents(TermView view, ViewMouseAdapter listener)
	{
		if (view==null)
			return;
		if (view.getTermUiComponent()!=null)
		{
			add(view.getTermUiComponent());
			view.getTermUiComponent().addMouseListener(listener);
			return;
		}
		if (view.getStartComponent()!=null)
		{
			add(view.getStartComponent());
			add(view.getEndComponent());
		}
		processViewComponents(view.getFirtTermView(), listener);		
		if (view.getTermOperatioinComponent()!=null)
		{
			add(view.getTermOperatioinComponent());
			view.getTermOperatioinComponent().addMouseListener(listener);
		}
		processViewComponents(view.getSecondTermView(), listener);
	}
}
