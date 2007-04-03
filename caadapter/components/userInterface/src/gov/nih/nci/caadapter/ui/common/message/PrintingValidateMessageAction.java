/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/message/PrintingValidateMessageAction.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.common.message;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.validation.ValidationMessageUtils;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.DocumentRenderer;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextInsensitiveAction;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
 */
public class PrintingValidateMessageAction extends AbstractContextInsensitiveAction
{
     /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: PrintingValidateMessageAction.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/message/PrintingValidateMessageAction.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $";


    private static final String COMMAND_NAME = "Print";


    protected transient ValidationMessagePane validationMessagePane = null;
    protected JEditorPane editorPane = null;
    //protected JTextArea editorPane = null;
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    public PrintingValidateMessageAction(ValidationMessagePane valPanel)
    {
      this(COMMAND_NAME, valPanel);
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a default icon.
     */
    public PrintingValidateMessageAction(String name, ValidationMessagePane valPanel)
    {
        super(name, null);
        this.validationMessagePane = valPanel;
    }

    /**
     * Invoked when an action occurs.
     */
    public boolean doAction(ActionEvent e) throws Exception
    {
        ValidatorResults results = validationMessagePane.getValidatorResults();
        String messages = "<html><head><title>Print Validation Results</title></head><body>"
                        + "<font size='2' face='Times New Roman'>" + ValidationMessageUtils.generateStatMessage(results)
                        + "<br><br><br>"+ (results.toString()).replace("\n", "<br>")
                        + "</font></body></html>";
        setSuccessfullyPerformed(processPrintFile(results.toString(), ""));
        return isSuccessfullyPerformed();
    }

    private boolean processPrintFile(String messages, String dummy) throws Exception
    {
        Paper paper = new Paper();
        paper.setSize(Config.DEFAULT_PAPER_SIZE_X_IN_INCH*72, Config.DEFAULT_PAPER_SIZE_Y_IN_INCH*72);
        paper.setImageableArea(Config.DEFAULT_PAPER_TOP_MARGIN_IN_INCH*72,Config.DEFAULT_PAPER_LEFT_MARGIN_IN_INCH*72,
                  (Config.DEFAULT_PAPER_SIZE_X_IN_INCH - Config.DEFAULT_PAPER_LEFT_MARGIN_IN_INCH - Config.DEFAULT_PAPER_RIGHT_MARGIN_IN_INCH - 0.25)*72,
                  (Config.DEFAULT_PAPER_SIZE_Y_IN_INCH - Config.DEFAULT_PAPER_TOP_MARGIN_IN_INCH - Config.DEFAULT_PAPER_BOTTOM_MARGIN_IN_INCH - 0.25)*72);
        PageFormat page = new PageFormat();
        page.setPaper(paper);
        DocumentRenderer documentRenderer1 = new DocumentRenderer(page);
        PlainDocument pDocument = new PlainDocument();
        pDocument.insertString(0, messages, null);
        documentRenderer1.print(pDocument);
        return true;
    }
//    protected boolean processPrintFile(String messages) throws Exception
//    {
//        JFrame frame = new JFrame();
//
//        try
//          {
//            editorPane = new JEditorPane("text/html", "<html><head><title>help</title></head><body><font color='blue'>Start</font></body></html>");
//          }
//        catch (Exception e1)
//          {
//            JOptionPane.showMessageDialog(validationMessagePane,"Validation Results message print editing Failure!!\r\n"
//                                         + e1.getMessage(),"Saving Error!",JOptionPane.ERROR_MESSAGE);
//            return false;
//          }
//
//        String dirSeparater = File.separator;
//        String commonPath = FileUtil.getWorkingDirPath() + dirSeparater + "etc" + dirSeparater;
//        String commonURIPath = "file:///";
//        for(int i=0;i<commonPath.length();i++)
//          {
//            String achar = commonPath.substring(i, i+1);
//            if (achar.equals(dirSeparater)) commonURIPath = commonURIPath + "/";
//            else commonURIPath = commonURIPath + achar;
//          }
//        //JScrollPane js2 = new JScrollPane(mainView);
//        FileWriter fw = null;
//        String displayFileName = commonPath+Config.HELP_TEMPORARY_FILENAME_FIRST;
//        try
//          {
//            fw = new FileWriter(displayFileName);
//            fw.write(messages);
//            fw.close();
//          }
//        catch(IOException ie)
//          {
//            JOptionPane.showMessageDialog(validationMessagePane,"Validation Results message File writing Failure!!\r\n"
//                                        + displayFileName + "\r\n" + ie.getMessage(),"File Writing Error!",JOptionPane.ERROR_MESSAGE);
//            return false;
//          }
//
//
//      frame.setLayout(new BorderLayout());
//      frame.setTitle("Validation Result");
//      //JScrollPane js2 = new JScrollPane(editorPane = new JTextArea());
//      JScrollPane js2 = new JScrollPane(editorPane);
//      js2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//
//      frame.add(js2, BorderLayout.CENTER);
//      //Font font = new Font("Times New Roman", Font.PLAIN, 10);
//      //editorPane.setFont(font);
//      //editorPane.
//      editorPane.setText(messages);
//      frame.setSize(350,200);
//      frame.setVisible(true);
//      DefaultSettings.centerWindow(frame);
//
//
//      try
//          {
//            editorPane.setPage(new URL("file:///" + displayFileName));
//          }
//        catch(IOException ie)
//          {
//            //editorPane.setText("<html><head><title>help</title></head><body><font color='blue'>Source File not Found : "+displayFileName+"</font></body></html>");
//            JOptionPane.showMessageDialog(validationMessagePane,"Displaying File fiding Failure!!\r\n"
//                                        + displayFileName + "\r\n" + ie.getMessage(),"File not found Error!",JOptionPane.ERROR_MESSAGE);
//            return false;
//          }
//
//        PrinterJob pj = PrinterJob.getPrinterJob();
//        Paper paper = new Paper();
//        paper.setSize(Config.DEFAULT_PAPER_SIZE_X_IN_INCH*72, Config.DEFAULT_PAPER_SIZE_Y_IN_INCH*72);
//        paper.setImageableArea(Config.DEFAULT_PAPER_TOP_MARGIN_IN_INCH*72,Config.DEFAULT_PAPER_LEFT_MARGIN_IN_INCH*72,
//                  (Config.DEFAULT_PAPER_SIZE_X_IN_INCH - Config.DEFAULT_PAPER_LEFT_MARGIN_IN_INCH - Config.DEFAULT_PAPER_RIGHT_MARGIN_IN_INCH - 0.25)*72,
//                  (Config.DEFAULT_PAPER_SIZE_Y_IN_INCH - Config.DEFAULT_PAPER_TOP_MARGIN_IN_INCH - Config.DEFAULT_PAPER_BOTTOM_MARGIN_IN_INCH - 0.25)*72);
//        PageFormat page = new PageFormat();
//        page.setPaper(paper);
//        page.setOrientation(PageFormat.LANDSCAPE);
//
//        Component comp = validationMessagePane;
//        while(true)
//        {
//          if (comp == null) break;
//          else if (comp instanceof JFrame)
//          {
//              JFrame fm = (JFrame) comp;
//              fm.setVisible(true);
//              break;
//          }
//          else if (comp instanceof JDialog)
//          {
//              JDialog fm = (JDialog) comp;
//              fm.setVisible(true);
//              break;
//          }
//          comp = comp.getParent();
//        }
//        PageFormat page1 = null;
//        PagePrintPrepare vista = new PagePrintPrepare(editorPane, page1 = pj.pageDialog(page));
//        if (page1.equals(page))
//        {
//            frame.dispose();
//            return true;
//        }
//
//        pj.setPageable(vista);
//
//        try
//          {
//            if (pj.printDialog())
//              {
//                pj.print();
//              }
//          }
//        catch (PrinterException pe)
//          {
//              JOptionPane.showMessageDialog(validationMessagePane,"Validation Results message Printing Failure!!\r\n"
//                                         + pe.getMessage(),"Printing Error!",JOptionPane.ERROR_MESSAGE);
//              frame.dispose();
//              return false;
//          }
//       frame.dispose();
//       return true;
//    }
  protected Component getAssociatedUIComponent()
    {
        return validationMessagePane;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.14  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/05 16:44:14  umkis
 * HISTORY      : Printing support class change into DocumentRenderer
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/19 16:37:15  jiangsc
 * HISTORY      : Created dumpAllValidatorResultsToFile() function to temporarily dump validator results to files.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/14 21:37:20  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 15:39:38  umkis
 * HISTORY      : defect# 232
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/15 16:18:37  umkis
 * HISTORY      : default page set up
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/11/09 19:12:33  umkis
 * HISTORY      : Define default variables for printing paper size and margins
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/11/02 21:10:26  umkis
 * HISTORY      : defect# 172
 * HISTORY      :
 */
