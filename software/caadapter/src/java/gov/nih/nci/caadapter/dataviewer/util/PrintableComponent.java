/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.dataviewer.util;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * This wrapper class encapsulates a Component and allows it to be printed
 * using the Java 2 printing API.
 */
public class PrintableComponent implements Printable
{

    // The component to be printed.
    Component c;

    /**
     * Create a PrintableComponent wrapper around a Component
     */
    public PrintableComponent(Component c)
    {
        this.c = c;
    }

    /**
     * This method is not part of the Printable interface.  It is a method
     * that sets up the PrinterJob and initiates the printing.
     */
    public void print() throws PrinterException
    {
        // Get the PrinterJob object
        PrinterJob job = PrinterJob.getPrinterJob();

        // Get the default page format, then allow the user to modify it
        PageFormat format = job.defaultPage();
        format.setOrientation(PageFormat.PORTRAIT);
        // Tell the PrinterJob what to print
        job.setPrintable(this, format);
        // Ask the user to confirm, and then begin the printing process
        if (job.printDialog())
            job.print();
    }

    /**
     * This is the "callback" method that the PrinterJob will invoke.
     * This method is defined by the Printable interface.
     */
    public int print(Graphics g, PageFormat format, int pagenum)
    {
        // The PrinterJob will keep trying to print pages until we return
        // this value to tell it that it has reached the end.
        if (pagenum > 0)
            return Printable.NO_SUCH_PAGE;
        // We're passed a Graphics object, but it can always be cast to Graphics2D
        Graphics2D g2 = (Graphics2D) g;
        // Use the top and left margins specified in the PageFormat Note
        // that the PageFormat methods are poorly named.  They specify
        // margins, not the actual imageable area of the printer.
        g2.translate(format.getImageableX(), format.getImageableY());
        // Tell the component to draw itself to the printer by passing in
        // the Graphics2D object.  This will not work well if the component
        // has double-buffering enabled.
        c.paint(g2);
        // Return this constant to tell the PrinterJob that we printed the page.
        return Printable.PAGE_EXISTS;
    }
}
