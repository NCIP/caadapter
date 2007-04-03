/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/message/PagePrintPrepare.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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

import gov.nih.nci.caadapter.common.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * This class defines the preparation class for page print.
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
 */


public class PagePrintPrepare extends SetPageForPrint implements Printable
{
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: PagePrintPrepare.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/message/PagePrintPrepare.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $";

    private static final boolean SYMMETRIC_SCALING = true;
    private static final boolean ASYMMETRIC_SCALING = false;
    private double mScaleX;
    private double mScaleY;
    /**
     * The Swing component to print.
     */
    private JComponent mComponent;
    /**
     * Create a Pageable that can print a
     * Swing JComponent over multiple pages.
     *
     * @param c The swing JComponent to be printed.
     *
     * @param format The size of the pages over which
     * the componenent will be printed.
     */
    public PagePrintPrepare(JComponent c, PageFormat format)
      {
        setPageFormat(format);
        setPrintable(this);
        setComponent(c);
        /* Tell the Vista we subclassed the size of the canvas.
         */
        Rectangle componentBounds = c.getBounds(null);
        setSize(componentBounds.width, componentBounds.height);
        setScale(1, 1);
      }

  protected void setComponent(JComponent c)
    {
      mComponent = c;
    }
  public void resetPageFormat(PageFormat format)
    {
      setPageFormat(format);
      JComponent c = mComponent;
      Rectangle componentBounds = c.getBounds(null);
      setSize(componentBounds.width, componentBounds.height);
      setScale(1, 1);
    }

  public void setScale(double scaleX, double scaleY)
    {
      mScaleX = scaleX;
      mScaleY = scaleY;
    }
  public void scaleToFitX()
    {
      PageFormat format = getPageFormat();
      Rectangle componentBounds = mComponent.getBounds(null);
      double scaleX = format.getImageableWidth() /componentBounds.width;
      double scaleY = scaleX;
      if (scaleX < 1)
        {
          setSize( (float) format.getImageableWidth(),
          (float) (componentBounds.height * scaleY));
          setScale(scaleX, scaleY);
        }
    }

  public void scaleToFitY()
    {
      PageFormat format = getPageFormat();
      Rectangle componentBounds = mComponent.getBounds(null);
      double scaleY = format.getImageableHeight() /componentBounds.height;
      double scaleX = scaleY;
      if (scaleY < 1)
        {
          setSize( (float) (componentBounds.width * scaleX),(float) format.getImageableHeight());
          setScale(scaleX, scaleY);
        }
    }

  public void scaleToFit(boolean useSymmetricScaling)
    {
      PageFormat format = getPageFormat();
      Rectangle componentBounds = mComponent.getBounds(null);
      double scaleX = format.getImageableWidth() /componentBounds.width;
      double scaleY = format.getImageableHeight() /componentBounds.height;
      Log.logInfo(this, "Scale: " + scaleX + " " + scaleY);
      if (scaleX < 1 || scaleY < 1)
        {
          if (useSymmetricScaling)
            {
              if (scaleX < scaleY)
                {
                  scaleY = scaleX;
                }
              else
                {
                  scaleX = scaleY;
                }
            }
          setSize( (float) (componentBounds.width * scaleX), (float) (componentBounds.height * scaleY) );
          setScale(scaleX, scaleY);
        }
    }

  public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException
    {
      Graphics2D g2 = (Graphics2D) graphics;
      g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      Rectangle componentBounds = mComponent.getBounds(null);
      g2.translate(-componentBounds.x, -componentBounds.y);
      g2.scale(mScaleX, mScaleY);
      boolean wasBuffered = mComponent.isDoubleBuffered();
      mComponent.paint(g2);
      mComponent.setDoubleBuffered(wasBuffered);
      //System.out.print("Here is print!!!" + PAGE_EXISTS + " : " + pageIndex + " : " + -componentBounds.x + " : "+  -componentBounds.y);
      //System.out.println(" : " + componentBounds.x + " : "+ componentBounds.y);

      return PAGE_EXISTS;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.12  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/09 19:12:48  umkis
 * HISTORY      : Define default variables for printing paper size and margins
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/03 14:30:38  umkis
 * HISTORY      : code re-organize
 * HISTORY      :
 */
