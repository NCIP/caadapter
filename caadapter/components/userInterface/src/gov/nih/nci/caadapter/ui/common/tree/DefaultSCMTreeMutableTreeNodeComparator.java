/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.tree;

import gov.nih.nci.caadapter.ui.common.nodeloader.CSVFieldMetaColumnNumberComparator;

import java.util.Comparator;

/**
 * This class defines a comparator class to facilitate the comparison among DefaultSCMTreeMutableTreeNodes.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:52 $
 */
public class DefaultSCMTreeMutableTreeNodeComparator implements Comparator
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: DefaultSCMTreeMutableTreeNodeComparator.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/DefaultSCMTreeMutableTreeNodeComparator.java,v 1.2 2008-06-09 19:53:52 phadkes Exp $";

	private CSVFieldMetaColumnNumberComparator fieldNumberComparator = new CSVFieldMetaColumnNumberComparator();

	/**
	 * Compares its two arguments for order.  Returns a negative integer,
	 * zero, or a positive integer as the first argument is less than, equal
	 * to, or greater than the second.<p>
	 * <p/>
	 * The implementor must ensure that <tt>sgn(compare(x, y)) ==
	 * -sgn(compare(y, x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
	 * implies that <tt>compare(x, y)</tt> must throw an exception if and only
	 * if <tt>compare(y, x)</tt> throws an exception.)<p>
	 * <p/>
	 * The implementor must also ensure that the relation is transitive:
	 * <tt>((compare(x, y)&gt;0) &amp;&amp; (compare(y, z)&gt;0))</tt> implies
	 * <tt>compare(x, z)&gt;0</tt>.<p>
	 * <p/>
	 * Finally, the implementer must ensure that <tt>compare(x, y)==0</tt>
	 * implies that <tt>sgn(compare(x, z))==sgn(compare(y, z))</tt> for all
	 * <tt>z</tt>.<p>
	 * <p/>
	 * It is generally the case, but <i>not</i> strictly required that
	 * <tt>(compare(x, y)==0) == (x.equals(y))</tt>.  Generally speaking,
	 * any comparator that violates this condition should clearly indicate
	 * this fact.  The recommended language is "Note: this comparator
	 * imposes orderings that are inconsistent with equals."
	 *
	 * @param o1 the first object to be compared.
	 * @param o2 the second object to be compared.
	 * @return a negative integer, zero, or a positive integer as the
	 *         first argument is less than, equal to, or greater than the
	 *         second.
	 * @throws ClassCastException if the arguments' types prevent them from
	 *                            being compared by this Comparator.
	 */
	public int compare(Object o1, Object o2)
	{
		if(!(o1 instanceof DefaultSCMTreeMutableTreeNode))
		{
			throw new IllegalArgumentException("Object o is not of type '" + DefaultSCMTreeMutableTreeNode.class.getName() + "'. It is '" + (o1==null? "null" : o1.getClass().getName()) + "'.");
		}
		if(!(o2 instanceof DefaultSCMTreeMutableTreeNode))
		{
			throw new IllegalArgumentException("Object o1 is not of type '" + DefaultSCMTreeMutableTreeNode.class.getName() + "'. It is '" + (o2==null? "null" : o2.getClass().getName()) + "'.");
		}
		DefaultSCMTreeMutableTreeNode node1 = (DefaultSCMTreeMutableTreeNode) o1;
		DefaultSCMTreeMutableTreeNode node2 = (DefaultSCMTreeMutableTreeNode) o2;

		return fieldNumberComparator.compare(node1.getUserObject(), node2.getUserObject());
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/19 18:54:38  jiangsc
 * HISTORY      : Added reshuffle functionality.
 * HISTORY      :
 */
