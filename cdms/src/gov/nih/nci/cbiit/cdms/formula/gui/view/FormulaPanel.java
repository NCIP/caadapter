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
	private JLabel nameLabel;
	private TermView termView;
	
	public FormulaPanel(BaseMeta formula)
	{
		super();
		this.setSize(350,250);
		controlMeta=formula;
		if (controlMeta==null)
			return;
		if (controlMeta instanceof FormulaMeta)
			initFormulaUI((FormulaMeta) controlMeta);
		else
		{	
			nameLabel=new JLabel("Formula Store:"+controlMeta.getName());
			nameLabel.setLocation(this.getX()+this.getWidth()/2,
					this.getY()+this.getHeight()/2-25);
			this.add(nameLabel);
		}
	}
	
	private void drawDivider(TermView view, int x0, int y0, Graphics g)
	{
		g.drawLine(x0, y0, x0+view.getWidth(), y0);
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
		if (f.getExpression().getDescription()!=null)
			lbText=lbText+f.getExpression().getDescription();
		
		lbText=lbText+" = ";
		nameLabel=new JLabel(lbText);
		add(nameLabel);
		termView=new TermView(f.getExpression(), 50, -5);
		ViewMouseAdapter mouseListener=new ViewMouseAdapter();
		processViewComponents(termView, mouseListener);
	}
	
	@Override
	protected void paintComponent(Graphics arg0) {
		// TODO Auto-generated method stub
		super.paintComponent(arg0);	
		//this.location(x,y)=(0,0)
		if (nameLabel==null)
			return;
		nameLabel.setLocation(50, getHeight()/2-25);
		if (termView==null)
			return;
		
		int x0=50+nameLabel.getWidth();
		int y0=this.getHeight()/2-termView.getHeight()/2-TermView.VIEW_COMPONENT_HEIGHT/2;
		positionTermUiComponent(termView, x0, y0, arg0);
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
		positionTermUiComponent(view.getFirtTermView(), x0, y0, g);
		if (view.getTermOperatioinComponent()!=null)
		{
			view.getTermOperatioinComponent().setLocation(x0+view.getX()+view.getFirtTermView().getWidth(), 
						y0+view.getFirtTermView().getY());
		}
		
		if (view.getTerm().getOperation()==OperationType.DIVISION)
			drawDivider(view,x0, y0+TermView.VIEW_COMPONENT_HEIGHT, g);
		else if (view.getTerm().getOperation()==OperationType.SQUAREROOT)
			drawSquareRoot(view, x0, y0, g);
		
		positionTermUiComponent(view.getSecondTermView(), x0, y0, g);
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
		System.out.println("FormulaPanel.processViewComponents()...:"+view);
		if (view.getTermUiComponent()!=null)
		{
			add(view.getTermUiComponent());
			view.getTermUiComponent().addMouseListener(listener);
			return;
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
