/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/help/HelpViewerDocumentRenderer.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.help;

/*  Copyright 2002
    Kei G. Gauthier
    Suite 301
    77 Winsor Street
    Ludlow, MA  01056
*/
// {
/*  DocumentRenderer prints objects of type Document. Text attributes, including
    fonts, color, and small icons, will be rendered to a printed page.
    DocumentRenderer computes line breaks, paginates, and performs other
    formatting.

    An HTMLDocument is printed by sending it as an argument to the
    print(HTMLDocument) method. A PlainDocument is printed the same way. Other
    types of documents must be sent in a JEditorPane as an argument to the
    print(JEditorPane) method. Printing Documents in this way will automatically
    display a print dialog.

    As objects which implement the Printable Interface, instances of the
    DocumentRenderer class can also be used as the argument in the setPrintable
    method of the PrinterJob class. Instead of using the print() methods
    detailed above, a programmer may gain access to the formatting capabilities
    of this class without using its print dialog by creating an instance of
    DocumentRenderer and setting the document to be printed with the
    setDocument() or setJEditorPane(). The Document may then be printed by
    setting the instance of DocumentRenderer in any PrinterJob.
*/
/**
 * This class defines the document renderer.
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
 */
import gov.nih.nci.caadapter.ui.common.DocumentRenderer;

//import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;

public class HelpViewerDocumentRenderer extends DocumentRenderer
{
                                            //whether pages too wide to fit
                                              //on a page will be scaled.
    private HelpContentViewer helpViewer = null;

  public HelpViewerDocumentRenderer(PageFormat pF, JFrame mFrame, HelpContentViewer hViewer) {
    super(pF, mFrame);
    helpViewer = hViewer;
  }


/*  A protected method, printDialog(), displays the print dialog and initiates
    printing in response to user input.
*/
  protected void printDialog() {


      if (helpViewer == null)
      {
          super.printDialog();
      }
      else
      {
          boolean errSig = false;
          String erMsg = "";
          //helpViewer.setProtectFrameDispose(true);
          //helpViewer.setAlwaysOnTop(true);
          if (pJob.printDialog())
          {
              pJob.setPrintable(this, pFormat);
              try
              {
                  pJob.print();
                  mainFrame.setVisible(true);
                  helpViewer.setAlwaysOnTop(false);
                  //helpViewer.setVisible(true);
              }
              catch (PrinterException printerException)
              {
                  pageStartY = 0;
                  pageEndY = 0;
                  currentPage = -1;
                  errSig = true;
                  erMsg = printerException.getMessage();
                  //System.out.println("Error Printing Document");
              }
          }
          mainFrame.setVisible(true);
          helpViewer.setVisible(true);
          if (errSig)
          {
              helpViewer.setProtectFrameDispose(true);
              JOptionPane.showMessageDialog(helpViewer, erMsg,"Print Page Error!",JOptionPane.ERROR_MESSAGE);
          }
      }

  }

}