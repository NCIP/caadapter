/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.hl7;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Jan 21, 2009
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-01-23 18:20:17 $
 * @since caAdapter v4.2
 */

public class TransformationSourceSelectionDialog extends JDialog
{
	private String sourceDatatype;
	public static String SOURCE_TYPE_CSV="CSV Data File";
	public static String SOURCE_TYPE_V2="HL7 V2 Message";

	 public TransformationSourceSelectionDialog(JFrame callingFrame)
	 {
	        super(callingFrame, "Message Transformation Source", true);
	        //
	        JComponent panel2 = new TransformationSourceSelectionPanel(this);
	        add(panel2);
	        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	        setSize(320, 180);
	        setResizable(false);
	        setLocation(400, 300);
	        setVisible(true);
	 }
		/**
		 * @return the sourceDatatype
		 */
		public String getSourceDatatype() {
			return sourceDatatype;
		}

		/**
		 * @param sourceDatatype the sourceDatatype to set
		 */
		public void setSourceDatatype(String sourceDatatype) {
			this.sourceDatatype = sourceDatatype;
		}

}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/