/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/functions/FunctionBoxView.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.common.functions;

import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.function.meta.ParameterMeta;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.hl7.map.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import org.jgraph.JGraph;
import org.jgraph.graph.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Map;

/**
 * This class defines a cutomized view implementation for functional box.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
 */
public class FunctionBoxView extends VertexView
{
//	public static transient FunctionBoxCellRenderer renderer = new FunctionBoxCellRenderer();

	public FunctionBoxView()
	{
	}

	public FunctionBoxView(Object cell)
	{
		super(cell);
	}
	
	/**
	 * Returns a renderer for the class.
	 */
	public CellViewRenderer getRenderer()
	{
//		return super.getRenderer();
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
//		setBackground(backgroundColor);
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
			if(obj instanceof FunctionBoxCell)
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
				FunctionBoxCell functionCell = (FunctionBoxCell) obj;
				Object userObj = functionCell.getUserObject();
				if(!(userObj instanceof FunctionBoxMutableViewInterface))
				{
					System.err.println("User Object of FunctionBoxCell is not of type '" + FunctionBoxMutableViewInterface.class.getName() + "'");
					return null;
				}
				FunctionBoxMutableViewInterface function = (FunctionBoxMutableViewInterface) userObj;
				//set up the function title
				String functionName = function.getName();
//			Log.logInfo(this, "Function Name in FunctionBoxCellRenderer.getRendererComponent(): '" + functionName + "'");
				JLabel label = new JLabel();
				label.setBorder(getDefaultBorder(1, 0, 0, 0));
				label.setBackground(titleBackgroundColor);
				label.setText(functionName);
				label.setIcon(imageIcon);
				mainPanel.add(label, BorderLayout.NORTH);

				if(function.getFunctionConstant()!=null)
				{
					return renderConstantFunction(function, mainPanel);
				}
                else if(function.getFunctionVocabularyMapping()!=null)
				{
					return renderVocabularyMappingFunction((function), mainPanel);
				}
                else
				{
					return renderNormalFunction(function, mainPanel);
				}
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
	private JComponent renderConstantFunction(FunctionBoxMutableViewInterface function, JPanel mainPanel)
	{
		FunctionConstant functionConstant = function.getFunctionConstant();
		JTextArea area = new JTextArea(functionConstant.getValue());
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setEditable(false);
		area.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		area.setBorder(getDefaultBorder(1, 0, 1, 0));
		JScrollPane scrollPane = new JScrollPane(area);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.setBorder(getDefaultBorder(1, 1, 1, 1));
		this.setLayout(new BorderLayout(GAP_INTERVAL, GAP_INTERVAL));
		this.add(mainPanel, BorderLayout.CENTER);

		return this;
	}

    private JComponent renderVocabularyMappingFunction(FunctionBoxMutableViewInterface function, JPanel mainPanel)
	{
		FunctionVocabularyMapping functionVocabularyMapping = function.getFunctionVocabularyMapping();
		JTextArea area = new JTextArea(functionVocabularyMapping.getValue());
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setEditable(false);
		area.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		area.setBorder(getDefaultBorder(1, 0, 1, 0));
		JScrollPane scrollPane = new JScrollPane(area);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.setBorder(getDefaultBorder(1, 1, 1, 1));
		this.setLayout(new BorderLayout(GAP_INTERVAL, GAP_INTERVAL));
		this.add(mainPanel, BorderLayout.CENTER);

		return this;
	}

    private JComponent renderNormalFunction(FunctionBoxMutableViewInterface function, JPanel mainPanel)
	{
		JPanel centerPanel = new JPanel(new GridLayout(1, 2));
		centerPanel.setBorder(getDefaultBorder(1, 0, 1, 0));

//				JPanel leftOuterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, GAP_INTERVAL, GAP_INTERVAL));
		JPanel leftPanel = new JPanel(new GridLayout(function.getTotalNumberOfDefinedInputs(), 1, GAP_INTERVAL, GAP_INTERVAL));
//				leftPanel.setBackground(backgroundColor);
		addParameters(leftPanel, function, true);
//			leftOuterPanel.add(leftPanel);
//			addComponentToPanelByGridBagLayout(leftOuterPanel, leftPanel, 0, 1, true);
//				leftOuterPanel.setBorder(getDefaultBorder(0, 0, 0, 0));
//				leftOuterPanel.setBackground(backgroundColor);

//				JPanel rightOuterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, GAP_INTERVAL, GAP_INTERVAL));
		JPanel rightPanel = new JPanel(new GridLayout(function.getTotalNumberOfDefinedOutputs(), 1, GAP_INTERVAL, GAP_INTERVAL));
//				rightPanel.setBackground(backgroundColor);
		addParameters(rightPanel, function, false);
//			rightOuterPanel.add(rightPanel);
//			addComponentToPanelByGridBagLayout(rightOuterPanel, rightPanel, 0, 1, true);
//				rightOuterPanel.setBorder(getDefaultBorder(0, 1, 0, 0));
//				rightOuterPanel.setBackground(backgroundColor);

		//to leave some space between edge and display text.
//			centerPanel.add(leftPanel);
//			centerPanel.add(rightPanel);
//			centerPanel.add(leftOuterPanel);
//			centerPanel.add(rightOuterPanel);

		addComponentToPanelByGridBagLayout(centerPanel, leftPanel, 0, 2, false);
		addComponentToPanelByGridBagLayout(centerPanel, rightPanel, 1, 2, false);

		mainPanel.add(centerPanel, BorderLayout.CENTER);
//			mainPanel.add(getSpaceFiller(), BorderLayout.EAST);
//			mainPanel.add(getSpaceFiller(), BorderLayout.WEST);
//			this.getViewport().setView(mainPanel);
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
//			Insets insets = new Insets(5, 5, 5, 5);
		return this;
	}

//	private JComponent getSpaceFiller()
//	{
//		JComponent comp = new JPanel();
//		Dimension dim = new Dimension(5, 5);
//		comp.setPreferredSize(dim);
//		return comp;
//	}

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

	private void addParameters(JPanel panel, FunctionBoxMutableViewInterface function, boolean isInput)
	{
		FunctionMeta functionMeta = (FunctionMeta) function.getFunctionMeta();
		int size = isInput ? functionMeta.getSizeOfDefinedInput() : functionMeta.getSizeOfDefinedOutput();
		java.util.List<ParameterMeta> paramList = isInput ? functionMeta.getInputDefinitionList() : functionMeta.getOuputDefinitionList();
		Color localBackGroundColor = this.selected ? backgroundSelectionColor : backgroundColor;
		panel.setLayout(new GridBagLayout());
		panel.setBorder(isInput ? getDefaultBorder(0, 0, 0, 1) : getDefaultBorder(0, 1, 0, 0));
		panel.setBackground(localBackGroundColor);
		Insets insets = new Insets(GAP_INTERVAL, GAP_INTERVAL, GAP_INTERVAL, GAP_INTERVAL);
		for(int i=0; i<size; i++)
		{
			String name = null;
			ParameterMeta paramMeta = paramList.get(i);
			name = paramMeta.getParameterName();
//			isInput ? "value_" : "result_";
//			name += i;
			JLabel label = new JLabel();
			if(i<size-1)
			{
				label.setBorder(getDefaultBorder(0, 0, 1, 0));
			}
//			Font oldFont = label.getFont();
//			Log.logInfo(this, "old Font is '" + oldFont + "'");
//			label.setFont(new Font("customized", Font.PLAIN, 11));
			label.setBackground(localBackGroundColor);
			label.setText(name);
			panel.add(label, new GridBagConstraints(0, i, 1, 1, 1.0 / ((double)size), 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0));
//			panel.add(label);
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
			// JDK Bug: Zero length string passed to TextLayout constructor
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
 * HISTORY      : Revision 1.14  2006/09/06 18:27:10  umkis
 * HISTORY      : The new implement of Vocabulary Mapping function.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/25 15:17:08  jiangsc
 * HISTORY      : Added description.
 * HISTORY      :
 */
