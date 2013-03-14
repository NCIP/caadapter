/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
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
 * @author LAST UPDATE $Author: linc $
 * @since caAdapter v1.2
 * @version    $Revision: 1.3 $
 * @date       $Date: 2008-09-26 20:35:27 $
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
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */
