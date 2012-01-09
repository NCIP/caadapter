package gov.nih.nci.cbiit.cmts.ui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JApplet;
import javax.swing.KeyStroke;

import edu.stanford.ejalbert.BrowserLauncher;
import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;

public class HelpViewAction extends AbstractContextAction {
	private static final String COMMAND_NAME = ActionConstants.HELP;
	private static final Character COMMAND_MNEMONIC = new Character('H');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false);
//	private static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("Help16.gif"));
//	private static final String TOOL_TIP_DESCRIPTION = ActionConstants.HELP;

	private JApplet mainApplet;
	public HelpViewAction(JApplet container) {
		super(COMMAND_NAME);
		this.setMnemonic(COMMAND_MNEMONIC);
		mainApplet=container;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean doAction(ActionEvent e) throws Exception {
		// TODO Auto-generated method stub

		BrowserLauncher brLauncher = new BrowserLauncher();
		String location = mainApplet.getCodeBase()+"/help/CMTS_User_Guide_Document.pdf";
		System.out.println("HelpViewAction.doAction()..:clicked:"+location);
		brLauncher.openURLinBrowser(location);
		return true;
	}

	@Override
	protected Component getAssociatedUIComponent() {
		// TODO Auto-generated method stub
		return mainApplet;
	}

}
