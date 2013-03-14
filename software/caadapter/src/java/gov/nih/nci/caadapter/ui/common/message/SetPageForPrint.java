/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common.message;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * This class defines a pageable implementation.
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */



public class SetPageForPrint implements Pageable
  {
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: SetPageForPrint.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/message/SetPageForPrint.java,v 1.2 2008-06-09 19:53:51 phadkes Exp $";

    private int mNumPagesX;
    private int mNumPagesY;
    private int mNumPages;
    private Printable mPainter;
    private PageFormat mFormat;
    /**
     * Create a java.awt.Pageable that will print
     *  a canvas over as many pages as are needed.
     * A SetPageForPrint can be passed to PrinterJob.setPageable.
     *
     * @param width The width, in 1/72nds of an inch,
     * of the vist's canvas.
     *
     * @param height The height, in 1/72nds of an inch,
     * of the SetPageForPrint's canvas.
     *
     * @param painter The object that will drawn the contents
     * of the canvas.
     *
     * @param format The description of the pages on to which
     * the canvas will be drawn.
     */
    public SetPageForPrint(float width, float height, Printable painter, PageFormat format)
      {
        setPrintable(painter);
        setPageFormat(format);
        setSize(width, height);
      }

    /**
     * Create a SetPageForPrint over a canvas whose width and height
     * are zero and whose Printable and PageFormat are null.
     */

    protected SetPageForPrint()
      {
      }

    /**
     * Set the object responsible for drawing the canvas.
     */

    protected void setPrintable(Printable painter)
      {
        mPainter = painter;
      }

    /**
     * Set the page format for the pages over which the
     * canvas will be drawn.
     */

    protected void setPageFormat(PageFormat pageFormat)
      {
        mFormat = pageFormat;
      }
    /**
     * Set the size of the canvas to be drawn.
     *
     * @param width The width, in 1/72nds of an inch, of
     * the vist's canvas.
     *
     * @param height The height, in 1/72nds of an inch, of
     * the SetPageForPrint's canvas.
     */

    protected void setSize(float width, float height)
      {
        mNumPagesX = (int) ((width + mFormat.getImageableWidth() - 1)/ mFormat.getImageableWidth());
        mNumPagesY = (int) ((height + mFormat.getImageableHeight() - 1)/ mFormat.getImageableHeight());
        mNumPages = mNumPagesX * mNumPagesY;
      }

    /**
     * Returns the number of pages over which the canvas
     * will be drawn.
     */

    public int getNumberOfPages()
      {
        return mNumPages;
      }

    protected PageFormat getPageFormat()
      {
        return mFormat;
      }

    /**
     * Returns the PageFormat of the page specified by
     * pageIndex. For a SetPageForPrint the PageFormat
     * is the same for all pages.
     *
     * @param pageIndex the zero based index of the page whose
     * PageFormat is being requested
     * @return the PageFormat describing the size and
     * orientation.
     * @exception IndexOutOfBoundsException
     * the Pageable  does not contain the requested
     * page.
     */
    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException
      {
        if (pageIndex >= mNumPages)
          {
            throw new IndexOutOfBoundsException();
          }
        return getPageFormat();
      }

    /**
     * Returns the <code>Printable</code> instance responsible for
     * rendering the page specified by <code>pageIndex</code>.
     * In a SetPageForPrint, all of the pages are drawn with the same
     * Printable. This method however creates
     * a Printable which calls the canvas's
     * Printable. This new Printable
     * is responsible for translating the coordinate system
     * so that the desired part of the canvas hits the page.
     *
     * The SetPageForPrint's pages cover the canvas by going left to
     * right and then top to bottom. In order to change this
     * behavior, override this method.
     *
     * @param pageIndex the zero based index of the page whose
     * Printable is being requested
     * @return the Printable that renders the page.
     * @exception IndexOutOfBoundsException
     * the Pageable does not contain the requested
     * page.
     */

    public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException
      {
        if (pageIndex >= mNumPages)
          {
            throw new IndexOutOfBoundsException();
          }
        double originX = (pageIndex % mNumPagesX) * mFormat.getImageableWidth();
        double originY = (pageIndex / mNumPagesX) * mFormat.getImageableHeight();
        Point2D.Double origin = new Point2D.Double(originX, originY);
        return new TranslatedPrintable(mPainter, origin);
      }

    /**
     * This inner class's sole responsibility is to translate
     * the coordinate system before invoking a canvas's
     * painter. The coordinate system is translated in order
     * to get the desired portion of a canvas to line up with
     * the top of a page.
     */

    public static final class TranslatedPrintable implements Printable
      {
        /**
         * The object that will draw the canvas.
         */
        private Printable mPainter;
        /**
         * The upper-left corner of the part of the canvas
         * that will be displayed on this page. This corner
         * is lined up with the upper-left of the imageable
         * area of the page.
         */
        private Point2D mOrigin;
        /**
         * Create a new Printable that will translate
         * the drawing done by painter on to the
         * imageable area of a page.
         *
         * @param painter The object responsible for drawing
         * the canvas
         *
         * @param origin The point in the canvas that will be
         * mapped to the upper-left corner of
         * the page's imageable area.
         */
        public TranslatedPrintable(Printable painter, Point2D origin)
          {
            mPainter = painter;
            mOrigin = origin;
          }
        /**
         * Prints the page at the specified index into the specified
         * {@link java.awt.Graphics} context in the specified
         * format. A PrinterJob calls the
         * Printableinterface to request that a page be
         * rendered into the context specified by
         * graphics. The format of the page to be drawn is
         * specified by pageFormat. The zero based index
         * of the requested page is specified by pageIndex.
         * If the requested page does not exist then this method returns
         * NO_SUCH_PAGE; otherwise PAGE_EXISTS is returned.
         * The Graphics class or subclass implements the
         * {@link java.awt.print.PrinterGraphics} interface to provide additional
         * information. If the Printable object
         * aborts the print job then it throws a {@link java.awt.print.PrinterException}.
         * @param graphics the context into which the page is drawn
         * @param pageFormat the size and orientation of the page being drawn
         * @param pageIndex the zero based index of the page to be drawn
         * @return PAGE_EXISTS if the page is rendered successfully
         * or NO_SUCH_PAGE if pageIndex specifies a
         * non-existent page.
         * @exception java.awt.print.PrinterException
         * thrown when the print job is terminated.
         */
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException
          {
            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(-mOrigin.getX(), -mOrigin.getY());
            mPainter.print(g2, pageFormat, 1);
            return PAGE_EXISTS;
          }
      }
  }


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/11/03 14:30:38  umkis
 * HISTORY      : code re-organize
 * HISTORY      :
 */
