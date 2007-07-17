/**
 * $Header: /share/content/gforge/caadapter/caadapter/demo/gov/nih/nci/caadapter/hl7/demo/LaunchUI.java,v 1.2 2007-07-17 16:29:39 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 *	The caAdapter Software License, Version 1.0
 *
 *	Copyright 2001 SAIC. This software was developed in conjunction with the National Cancer
 *	Institute, and so to the extent government employees are co-authors, any rights in such works
 *	shall be subject to Title 17 of the United States Code, section 105.
 *
 *	Redistribution and use in source and binary forms, with or without modification, are permitted
 *	provided that the following conditions are met:
 *
 *	1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *	and the disclaimer of Article 3, below.  Redistributions in binary form must reproduce the above
 *	copyright notice, this list of conditions and the following disclaimer in the documentation and/or
 *	other materials provided with the distribution.
 *
 *	2.  The end-user documentation included with the redistribution, if any, must include the
 *	following acknowledgment:
 *
 *	"This product includes software developed by the SAIC and the National Cancer
 *	Institute."
 *
 *	If no such end-user documentation is to be included, this acknowledgment shall appear in the
 *	software itself, wherever such third-party acknowledgments normally appear.
 *
 *	3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or
 *	promote products derived from this software.
 *
 *	4. This license does not authorize the incorporation of this software into any proprietary
 *	programs.  This license does not authorize the recipient to use any trademarks owned by either
 *	NCI or SAIC-Frederick.
 *
 *
 *	5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *	WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *	DISCLAIMED.  IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR
 *	THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *	EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *	PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *	PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 *	OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *	NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  MODULENAME   : $Workfile: $
 *
 * ********************************************************************
 */
package gov.nih.nci.caadapter.hl7.demo;

import java.io.IOException;
import java.util.Enumeration;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.main.MainFrame;

/**
 * A tiny driver which will launch the HL7SDK Swing Mapping Tool.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.2 $
 * @since caAdapter v1.2
 */
public class LaunchUI {
    private static final String LOGID = "$RCSfile: LaunchUI.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/demo/gov/nih/nci/caadapter/hl7/demo/LaunchUI.java,v 1.2 2007-07-17 16:29:39 wangeug Exp $";

    public static void main(String[] args)
    {

//    	System.out.println("java.class.path:"+System.getProperty("java.class.path"));
//  		java.net.URL url= Thread.currentThread().getClass().getResource("/mif/COCT_MT150003UV03.mif");
//  		String urlSt=url.getFile();
//  		urlSt=urlSt.substring(6,urlSt.indexOf(".zip")+4);
//  		System.out.println("MyTest.main()..URL:"+urlSt);
//  		try {
//			java.util.zip.ZipFile zipFile=new java.util.zip.ZipFile(urlSt);
//			Enumeration em = zipFile.entries();
//			for (Enumeration em1 = zipFile.entries(); em1.hasMoreElements();){
//				System.out.println(em1.nextElement());
//			}
//  		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	try
        {
            DefaultSettings.installAll();
            new MainFrame().launch();
//            AbstractMainFrame mainFrame = new MainFrame();
//            mainFrame.launch();
            //DefaultSettings.centerWindow(mainFrame);    // defect# 255, for mainframe center aligning. inserted by umkis 01/30/06
        }
        catch (Throwable t)
        {
            Log.logException(new Object(), t);
        }
        //DefaultSettings.installAll();
        //MainFrame m = new MainFrame();
        //m.launch();
    }
}
