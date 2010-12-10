package gov.nih.nci.cbiit.cdms.formula.gui.view;

import javax.swing.JComponent;

import gov.nih.nci.cbiit.cdms.formula.core.OperationType;
import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermType;

public class TermView {
	public static final int VIEW_COMPONENT_HEIGHT=25;
	public static final int VIEW_CHARACTER_WEIDTH=7;
	public static int VIEW_COMPONENT_PADDING=2;
	public static int VIEW_SQUARE_ROOT_LEADING=15;
	private TermView parentView;
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
		//shift the term 15 pixel right for the squareRoot symbol
		if (meta.getOperation()!=null
				&&meta.getOperation().equals(OperationType.SQUAREROOT))
			x=x+VIEW_SQUARE_ROOT_LEADING;
		processTermMeta(term);
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

	
	public void setY(int y) {
		this.y = y;

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
 
		buildOperationComponent();
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
			width=width+termOperatioinComponent.getWidth();
		
		//add parenthesis
		if (meta.getOperation().equals(OperationType.ADDITION)
				||meta.getOperation().equals(OperationType.SUBTRACTION))
		{
			TermUiComponent mostLeft=(TermUiComponent)findMostLeftParenthesis(getFirtTermView());
			mostLeft.setText("("+mostLeft.getText());
			
			TermUiComponent rightLeft=(TermUiComponent)findMostRightParenthesis(getSecondTermView());
			rightLeft.setText(rightLeft.getText()+")");
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
			((TermUiComponent)termOperatioinComponent).setViewMeta(this);
	}

	private  JComponent findMostRightParenthesis(TermView view)
	{
		if (view.getTermUiComponent()!=null)
			return view.getTermUiComponent();
		if (view.getSecondTermView()!=null)
		{
			if (view.getTerm().getOperation().equals(OperationType.POWER)
					||view.getTerm().getOperation().equals(OperationType.LOGARITHM))
				return findMostRightParenthesis(view.getFirtTermView());
		
			return findMostRightParenthesis(view.getSecondTermView());
		}
		return findMostRightParenthesis(view.getFirtTermView());
	}

	private   JComponent findMostLeftParenthesis(TermView view)
	{
		if (view.getTermUiComponent()!=null)
			return view.getTermUiComponent();
		return findMostLeftParenthesis(view.getFirtTermView());
	}

	public String toString()
	{
		StringBuffer rtnB=new StringBuffer();
		rtnB.append(term.toString()+"\n(width,height)=("+width+","+height+")");
		rtnB.append("(x,y)=("+this.x+","+y+")");
		return rtnB.toString();
	}
	
}
