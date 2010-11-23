package gov.nih.nci.cbiit.cdms.formula.gui.view;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JLabel;

import gov.nih.nci.cbiit.cdms.formula.core.OperationType;
import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermType;

public class TermView {
	public static int VIEW_COMPONENT_HEIGHT=15;
	public static int VIEW_COMPONENT_PADDING=5;
	
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
				termOperatioinComponent=new JLabel(
						 term.getOperation().toString(), JLabel.CENTER );
				break;
			case  SUBTRACTION:
				termOperatioinComponent=new JLabel(
						 term.getOperation().toString(), JLabel.CENTER  );
				break;
			case  MULTIPLICATION:
				termOperatioinComponent=new JLabel(
						 term.getOperation().toString(), JLabel.CENTER  );
				break;
			case  EXPONENTIAL:
				termOperatioinComponent=new JLabel(
						 term.getOperation().toString(), JLabel.CENTER  );
				break;
			default: break;
		}
		if (termOperatioinComponent!=null)
			termOperatioinComponent.setBounds(new Rectangle(new Dimension(25,25)));
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
		if (meta.getType().value().equals(TermType.UNKNOWN.value()))
		{	
			termUiComponent=new JLabel(meta.getName());
			width=meta.getName().length()*5+VIEW_COMPONENT_PADDING;
			height= VIEW_COMPONENT_HEIGHT;
		}
		else if (meta.getType().value().equals(TermType.CONSTANT.value())
				||(meta.getType().value().equals(TermType.VARIABLE.value())))
		{
			
			termUiComponent=new JLabel(meta.getValue());
			width=meta.getValue().length()*5+VIEW_COMPONENT_PADDING;
			System.out.println("TermView.processTermMeta()...width:"+width);
			height= VIEW_COMPONENT_HEIGHT;
		}
		else 
		{
			//build the view of the first term
			firtTermView=new TermView(meta.getTerm().get(0),x,y);
			int xTerm2=x+firtTermView.getWidth()+VIEW_COMPONENT_PADDING;
			int yTerm2=y;
			if (meta.getOperation().equals(OperationType.DIVISION))
			{
				yTerm2=y+VIEW_COMPONENT_HEIGHT*2;
				xTerm2=x+(firtTermView.getWidth()+VIEW_COMPONENT_PADDING)/2;
			}
			else if (meta.getOperation().equals(OperationType.POWER))
				yTerm2=yTerm2-VIEW_COMPONENT_HEIGHT;
			buildOperationComponent();
			if (termOperatioinComponent!=null)
				xTerm2=xTerm2+(int)termOperatioinComponent.getWidth()+VIEW_COMPONENT_PADDING;
			secondTermView=null;
			if (meta.getTerm().size()>1)
			{
				secondTermView=new TermView(meta.getTerm().get(1), xTerm2, yTerm2);
			
				//set the size of the current view
				width=firtTermView.getWidth()+VIEW_COMPONENT_PADDING
					+secondTermView.getWidth()+VIEW_COMPONENT_PADDING;
				height=Math.max(firtTermView.getY()+firtTermView.getHeight(),
						secondTermView.getY()+secondTermView.getHeight())
							-Math.min(firtTermView.getY(), secondTermView.getY());
			}
			else
			{
				width=firtTermView.getWidth()+VIEW_COMPONENT_PADDING;
				height=firtTermView.getHeight();
			}
			if (termOperatioinComponent!=null)
				width=width+termOperatioinComponent.getWidth()+VIEW_COMPONENT_PADDING;
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
