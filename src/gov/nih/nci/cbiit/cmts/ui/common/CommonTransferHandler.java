package gov.nih.nci.cbiit.cmps.ui.common;

import javax.swing.JComponent;
import javax.swing.TransferHandler;


public class CommonTransferHandler extends TransferHandler {

	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
	 */
	@Override
	public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
	}

}
