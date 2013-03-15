/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmts.ui.function;

import org.jgraph.JGraph;
import org.jgraph.graph.*;

import gov.nih.nci.cbiit.cmts.core.FunctionData;
import gov.nih.nci.cbiit.cmts.core.FunctionDef;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Map;

/**
 * This class defines a cutomized view implementation for functional box.
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-29 22:18:18 $
 */
public class FunctionBoxGraphCellView extends VertexView
{

	public FunctionBoxGraphCellView(Object cell)
	{
		super(cell);
	}
	
	/**
	 * Returns a renderer for the class.
	 */
	public CellViewRenderer getRenderer()
	{
		FunctionBoxCellRenderer fbRender=new FunctionBoxCellRenderer();
		return fbRender;
	}

	public GraphCellEditor getEditor()
	{
		return super.getEditor();    //To change body of overridden methods use File | Settings | File Templates.
	}
}

class FunctionBoxCellRenderer extends JPanel implements CellViewRenderer
{
	private static final ImageIcon imageIcon = new ImageIcon(DefaultSettings.getImage("functionIcon.gif"));

	private int GAP_INTERVAL = 2;
	private Color titleBackgroundColor = new Color(0, 158, 234);
	private Color titleSelectedBackgroundColor = new Color(0, 123, 183);
	private Color backgroundColor = Color.WHITE;//new Color(219, 219, 219);
	private Color backgroundSelectionColor = Color.WHITE;//new Color(230, 230, 230);//new Color(128, 191, 255);

	/**
	 * Cache the current graph for drawing.
	 */
	transient protected JGraph graph;

	/**
	 * Cache the current shape for drawing.
	 */
	transient protected VertexView view;

	/**
	 * Cached hasFocus and selected value.
	 */
	transient protected boolean hasFocus, selected, preview, opaque,
	childrenSelected;

	/**
	 * Cached default foreground and default background.
	 */
	transient protected Color defaultForeground, defaultBackground,
	bordercolor;

	/**
	 * Cached borderwidth.
	 */
	transient protected int borderWidth;

	/**
	 * Cached value of the double buffered state
	 */
	transient protected boolean isDoubleBuffered = false;

	transient protected Color gradientColor = null;

	/**
	 * Creates a non-resizable, non-closable, non-maximizable,
	 * non-iconifiable <code>JInternalFrame</code> with the specified title.
	 * Note that passing in a <code>null</code> <code>title</code> results in
	 * unspecified behavior and possibly an exception.
	 */
	public FunctionBoxCellRenderer()
	{
		defaultForeground = UIManager.getColor("Tree.textForeground");
		defaultBackground = UIManager.getColor("Tree.textBackground");
	}

	public JComponent getRendererComponent(JGraph graph, CellView view, boolean sel, boolean focus, boolean preview)
	{
		this.graph = graph;
		this.isDoubleBuffered = graph.isDoubleBuffered();
		this.hasFocus = focus;
		this.childrenSelected = graph.getSelectionModel().isChildrenSelected(view.getCell());
		this.selected = sel;
		this.preview = preview;

//		Log.logInfo(this, "view of type '" + view.getClass().getName() + "'");

		if (view instanceof VertexView)
		{
			this.view = (VertexView) view;
			setComponentOrientation(graph.getComponentOrientation());
			installAttributes(view);

			Object obj = view.getCell();
			if(obj instanceof FunctionBoxGraphCell)
			{
				JPanel mainPanel = new JPanel(new BorderLayout(GAP_INTERVAL, GAP_INTERVAL));
				if(selected)
				{
					mainPanel.setBackground(titleSelectedBackgroundColor);
				}
				else
				{
					mainPanel.setBackground(titleBackgroundColor);
				}
				FunctionBoxGraphCell functionCell = (FunctionBoxGraphCell) obj;
				//set up the function title
				String functionName = functionCell.getFunctionDef().getName().toString();
				JLabel label = new JLabel();
				label.setBorder(getDefaultBorder(1, 0, 0, 0));
				label.setBackground(titleBackgroundColor);
				label.setText(functionName);
				label.setIcon(imageIcon);
				mainPanel.add(label, BorderLayout.NORTH);
				if (functionCell.getInputElements().isEmpty())
					return renderFunctionWithoutInputPort(functionCell, mainPanel);
						
				return renderNormalFunction(functionCell, mainPanel);
			}
		}
		return this;
	}

	/**
	 * When this function is called, it is pre-determined that the given function is a constant function.
	 * @param function
	 * @param mainPanel
	 * @return this component
	 */
	private JComponent renderFunctionWithoutInputPort(FunctionBoxGraphCell function, JPanel mainPanel)
	{
		FunctionDef functionConstant = function.getFunctionDef();
		JTextArea area = new JTextArea(functionConstant.getName().toString());
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setEditable(false);
		area.setBackground(DefaultSettings.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		area.setBorder(getDefaultBorder(1, 0, 1, 0));
		JScrollPane scrollPane = new JScrollPane(area);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.setBorder(getDefaultBorder(1, 1, 1, 1));
		this.setLayout(new BorderLayout(GAP_INTERVAL, GAP_INTERVAL));
		this.add(mainPanel, BorderLayout.CENTER);

		return this;
	}

    private JComponent renderNormalFunction(FunctionBoxGraphCell function, JPanel mainPanel)
	{
		JPanel centerPanel = new JPanel(new GridLayout(1, 2));
		centerPanel.setBorder(getDefaultBorder(1, 0, 1, 0));
		JPanel leftPanel = new JPanel(new GridLayout(function.getInputElements().size(), 1, GAP_INTERVAL, GAP_INTERVAL));
		addParameters(leftPanel, function, true);
		JPanel rightPanel = new JPanel(new GridLayout(function.getOutputElements().size(), 1, GAP_INTERVAL, GAP_INTERVAL));
 		addParameters(rightPanel, function, false);
		addComponentToPanelByGridBagLayout(centerPanel, leftPanel, 0, 2, false);
		addComponentToPanelByGridBagLayout(centerPanel, rightPanel, 1, 2, false);

		mainPanel.add(centerPanel, BorderLayout.CENTER);
		int compSize = this.getComponentCount();
		if(compSize>0)
		{//clean up before moving forward
			for(int i=0; i<compSize; i++)
			{
				this.remove(i);
			}
		}
		mainPanel.setBorder(getDefaultBorder(1, 1, 1, 1));
		this.setLayout(new BorderLayout(GAP_INTERVAL, GAP_INTERVAL));
		this.add(mainPanel, BorderLayout.CENTER);
		return this;
	}


	private Border getDefaultBorder(int top, int left, int bottom, int right)
	{
		return BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK);
	}

	private void addComponentToPanelByGridBagLayout(JPanel parentPanel, JComponent component, int theChildIndex, int totalChildren, boolean verticalAligned)
	{
		parentPanel.setLayout(new GridBagLayout());
		Color localBackGroundColor = this.selected ? backgroundSelectionColor : backgroundColor;
		parentPanel.setBackground(localBackGroundColor);
		component.setBackground(localBackGroundColor);
		Insets insets = new Insets(0, 0, 0, 0);//GAP_INTERVAL, GAP_INTERVAL, GAP_INTERVAL, GAP_INTERVAL);
		GridBagConstraints cons = null;
		if(verticalAligned)
		{
			cons = new GridBagConstraints(0, theChildIndex, 1, 1, 1.0 / ((double) totalChildren), 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0);
		}
		else
		{
			cons = new GridBagConstraints(theChildIndex, 0, 1, 1, 1.0 / ((double) totalChildren), 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0);
		}
		parentPanel.add(component, cons);
	}

	private void addParameters(JPanel panel, FunctionBoxGraphCell function, boolean isInput)
	{
		FunctionDef FunctionDef = (FunctionDef) function.getFunctionDef();
		int size = FunctionDef.getData().size();
		java.util.List<FunctionData> paramList = FunctionDef.getData();
		Color localBackGroundColor = this.selected ? backgroundSelectionColor : backgroundColor;
		panel.setLayout(new GridBagLayout());
		panel.setBorder(isInput ? getDefaultBorder(0, 0, 0, 1) : getDefaultBorder(0, 1, 0, 0));
		panel.setBackground(localBackGroundColor);
		Insets insets = new Insets(GAP_INTERVAL, GAP_INTERVAL, GAP_INTERVAL, GAP_INTERVAL);
		for(int i=0; i<size; i++)
		{
			String name = null;
			FunctionData paramMeta = paramList.get(i);
			if (paramMeta.isInput()!=isInput)
				continue;
			name = paramMeta.getName();
			JLabel label = new JLabel();
//			if(i<size-1)
//			{
//				label.setBorder(getDefaultBorder(0, 0, 1, 0));
//			}
			label.setBackground(localBackGroundColor);
			label.setText(name);
			panel.add(label, new GridBagConstraints(0, i, 1, 1, 1.0 / ((double)size), 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0));
		}
	}

	/**
	 * Install the attributes of specified cell in this renderer instance. This
	 * means, retrieve every published key from the cells hashtable and set
	 * global variables or superclass properties accordingly.
	 *
	 * @param view the cell view to retrieve the attribute values from.
	 */
	protected void installAttributes(CellView view)
	{
		Map map = view.getAllAttributes();
//		setOpaque(GraphConstants.isOpaque(map));
		setBorder(GraphConstants.getBorder(map));
		bordercolor = GraphConstants.getBorderColor(map);
		borderWidth = Math.max(1, Math.round(GraphConstants.getLineWidth(map)));
		if (getBorder() == null && bordercolor != null)
			setBorder(BorderFactory.createLineBorder(bordercolor, borderWidth));
		Color foreground = GraphConstants.getForeground(map);
		setForeground((foreground != null) ? foreground : defaultForeground);
		Color background = GraphConstants.getBackground(map);
		setBackground((background != null) ? background : defaultBackground);
		Color gradientColor = GraphConstants.getGradientColor(map);
		setGradientColor(gradientColor);
		setFont(GraphConstants.getFont(map));
	}

	/**
	 * Paint the renderer. Overrides superclass paint to add specific painting.
	 */
	public void paint(Graphics g)
	{
		try
		{
//			if (gradientColor != null && !preview && isOpaque())
//			{
//				setOpaque(false);
//				Graphics2D g2d = (Graphics2D) g;
//				g2d.setPaint(new GradientPaint(0, 0, getBackground(),
//						getWidth(), getHeight(), gradientColor, true));
//				g2d.fillRect(0, 0, getWidth(), getHeight());
//			}
			super.paint(g);
			paintSelectionBorder(g);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Provided for subclassers to paint a selection border.
	 */
	protected void paintSelectionBorder(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		Stroke previousStroke = g2.getStroke();
		g2.setStroke(GraphConstants.SELECTION_STROKE);
		if (childrenSelected)
			g.setColor(graph.getGridColor());
		// FIXME: Repaint after selection change does not know about this
		else if (hasFocus && selected)
			g.setColor(graph.getLockedHandleColor());
		else if (selected)
			g.setColor(graph.getHighlightColor());
		if (childrenSelected || selected)
		{
			Dimension d = getSize();
			g.drawRect(0, 0, d.width - 1, d.height - 1);
		}
		g2.setStroke(previousStroke);
	}

	/**
	 * @return Returns the gradientColor.
	 */
	public Color getGradientColor()
	{
		return gradientColor;
	}

	/**
	 * @param gradientColor The gradientColor to set.
	 */
	public void setGradientColor(Color gradientColor)
	{
		this.gradientColor = gradientColor;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
