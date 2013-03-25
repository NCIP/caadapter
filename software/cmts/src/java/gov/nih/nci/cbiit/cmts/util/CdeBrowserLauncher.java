/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.util;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

public class CdeBrowserLauncher {
	public static String CDE_SITE="https://cdebrowser.nci.nih.gov/CDEBrowser/search?elementDetails=9&FirstTimer=0&PageId=ElementDetailsGroup";

	private static BrowserLauncher browserLauncher;
	

	public static void BrowseCDE(String publicId, String version) 
	{
		if (browserLauncher==null)
		{
			try {
				browserLauncher=new BrowserLauncher();
			} catch (BrowserLaunchingInitializingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedOperatingSystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String brwsURL=CDE_SITE+"&publicId="+publicId+"&version="+version;
		browserLauncher.openURLinBrowser(brwsURL);
	}
}
