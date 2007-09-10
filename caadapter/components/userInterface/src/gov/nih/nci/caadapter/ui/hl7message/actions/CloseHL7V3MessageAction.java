/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/actions/CloseHL7V3MessageAction.java,v 1.2 2007-09-10 16:40:35 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.hl7message.actions;

import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.actions.DefaultContextCloseAction;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;
import gov.nih.nci.caadapter.ui.hl7message.HL7MessagePanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * This class defines the close action of HL7 panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2007-09-10 16:40:35 $
 */
public class CloseHL7V3MessageAction extends DefaultContextCloseAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: CloseHL7V3MessageAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/actions/CloseHL7V3MessageAction.java,v 1.2 2007-09-10 16:40:35 wangeug Exp $";

	private transient HL7MessagePanel hl7Panel;

	public CloseHL7V3MessageAction(HL7MessagePanel hl7Panel)
	{
		this(COMMAND_NAME, hl7Panel);
	}

	public CloseHL7V3MessageAction(String name, HL7MessagePanel hl7Panel)
	{
		this(name, null, hl7Panel);
	}

	public CloseHL7V3MessageAction(String name, Icon icon, HL7MessagePanel hl7Panel)
	{
		super(name, icon, hl7Panel);
		this.hl7Panel = hl7Panel;
	}

	protected boolean postActionPerformed(ActionEvent e)
	{
		if (mainFrame != null)
		{
			//todo: should use some better way to call contextmanager
			ContextManager cm = ContextManager.getContextManager();
			cm.enableAction(ActionConstants.OPEN_HL7_V3_MESSAGE, true);
			cm.enableAction(ActionConstants.NEW_HL7_V3_MESSAGE, true);
		}
		hl7Panel.clearDataFromUI();
		return true;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/07/03 19:33:17  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/27 22:41:09  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 */
