package gov.nih.nci.cbiit.cdms.formula.gui.view;

import javax.swing.JComponent;
import javax.swing.JLabel;

import gov.nih.nci.cbiit.cdms.formula.core.OperationType;
import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermType;

public class TermView {
	public static final int VIEW_COMPONENT_HEIGHT=25;
	public static final int VIEW_CHARACTER_WEIDTH=8;
	public static int VIEW_COMPONENT_PADDING=6;
	
	private TermView firtTermView;
	private TermView secondTermView;
 	private TermMeta term;
	private JComponent termOperatioinComponent;
	private JComponent termUiComponent;
	private int x, y, width, height;
	
	public TermView(TermMeta meta, int locationX, int locationY)
	{
		term=meta;	
		x=locationX;
		y=locationY;
		processTermMeta(term);
	}
	
	private void buildOperationComponent()
	{
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
			default: break;
		}
		if (termOperatioinComponent!=null)
			((TermUiComponent)termOperatioinComponent).setViewMeta(term);
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

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	private void processTermMeta(TermMeta meta)
	{
		if (meta==null)
			return;
		if (meta.getType().value().equals(TermType.UNKNOWN.value())
				||meta.getType().value().equals(TermType.CONSTANT.value())
				||(meta.getType().value().equals(TermType.VARIABLE.value())))
		{	
			termUiComponent=new TermUiComponent(term);
			width=(int)termUiComponent.getBounds().getWidth();
			height= VIEW_COMPONENT_HEIGHT;
		}
		else 
		{
			buildOperationComponent();
			//build the view of the first term
			int x1=x, y1=y, x2=x, y2=y;
			if (meta.getOperation().equals(OperationType.DIVISION))
			{
				y1=y;
				y2=y+VIEW_COMPONENT_HEIGHT;
			}
			else if (meta.getOperation().equals(OperationType.POWER))
			{	
				y2=y-VIEW_COMPONENT_HEIGHT/2;
			}
			else if (meta.getOperation().equals(OperationType.LOGARITHM))
			{	
				y1=y+VIEW_COMPONENT_HEIGHT/3;
				x1=x+(int)termOperatioinComponent.getBounds().getWidth();
			}
			firtTermView=new TermView(meta.getTerm().get(0),x1,y1);
			
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
				width=width+termOperatioinComponent.getWidth();
		}
	}
	
	public String toString()
	{
		StringBuffer rtnB=new StringBuffer();
		rtnB.append(term.toString()+"\n(width,height)=("+width+","+height+")");
		rtnB.append("(x,y)=("+this.x+","+y+")");
		return rtnB.toString();
	}
	
}
