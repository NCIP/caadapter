/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.test;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import gov.nih.nci.cbiit.cdms.formula.gui.view.TermUiComponent;

import javax.swing.JLabel;
import javax.xml.bind.JAXBException;

import org.junit.Test;

public class FormatTest {

	/**
	 * Format a number store
	 * @throws JAXBException 
	 */
	@Test
	public void testNumberFormat()
	{
		 Locale[] locales = NumberFormat.getAvailableLocales();
		 double myNumber = -1111234.567;
		 NumberFormat form;
		 for (int j=0; j<4; ++j) {
		     System.out.println("FORMAT");
		     for (int i = 0; i < locales.length; ++i) {
		         if (locales[i].getCountry().length() == 0) {
		            continue; // Skip language-only locales
		         }
		         System.out.print(locales[i].getDisplayName());
		         switch (j) {
		         case 0:
		             form = NumberFormat.getInstance(locales[i]); 
		             form.setMaximumFractionDigits(2);
		             break;
		         case 1:
		             form = NumberFormat.getIntegerInstance(locales[i]); break;
		         case 2:
		             form = NumberFormat.getCurrencyInstance(locales[i]); break;
		         default:
		             form = NumberFormat.getPercentInstance(locales[i]); break;
		         }
		         if (form instanceof DecimalFormat) {
		             System.out.print(": " + ((DecimalFormat) form).toPattern());
		         }
		         System.out.print(" -> j="+j+"..->" + form.format(myNumber));
		         try {
		             System.out.println(" -> " + form.parse(form.format(myNumber)));
		         } catch (ParseException e) {}
		     }
		 }
	}
}
