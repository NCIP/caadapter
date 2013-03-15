/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.mif;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;

public class MIFCardinality
{

  //-------------------------------------------------------------------------
  /** Minimum cardinality. */
  private int min_;
  /** Maximum cardinality; -1 corresponds to *
   *
   * GS: I changed that to UNBOUNDED (Integer.MAX_VALUE) because otherwise
   * it is quite unintuitive when I want to test if(card.getMax() > 1).
   */
  private int max_;

  public static final int UNBOUNDED = Integer.MAX_VALUE;

  //-------------------------------------------------------------------------
  /**
   * Constructors is called only from create() to ensure identity equality.
   *
   * @param min  minimum cardinality value
   * @param max  maximum cardinality value
   *             If this value is two or more, the maximum value will be set as UNBOUNDED.
   * @throws IllegalArgumentException  if these values are invalid for cardinality
   */
  public MIFCardinality(int min, int max)
  {
      try
      {
          setMinimum(min);
          setMaximum(max);
      }
      catch(IllegalArgumentException ie)
      {
          throw new IllegalArgumentException("min=" + min + ", max=" + max);
      }
  }

  //-------------------------------------------------------------------------
  /**
   * Looks up cardinality by its string representation.
   *
   * @param s  the string to look up
   * @throws IllegalArgumentException  if the string cannot be looked up
   */
    public MIFCardinality(String s)
    {
        try
        {
            setCardinalityValue(s);
        }
        catch(IllegalArgumentException ie)
        {
            throw ie;
        }
    }

  //-------------------------------------------------------------------------
  /**
   * Setup Cardinality Value (minimum and maximum.
   *
   * @param s  the string to look up
   * @throws IllegalArgumentException  if the string cannot be looked up
   */
    private void setCardinalityValue(String s) throws IllegalArgumentException
    {
        if ((s == null)||(s.trim().equals("")))
        {
            throw new IllegalArgumentException("Null Cardinality String");
        }
        s = s.trim();
        if (s.equals(Config.CARDINALITY_ONE_TO_ONE))
        {
            min_ = 1;
            max_ = 1;
        }
        else if (s.equals(Config.CARDINALITY_ONE_TO_MANY))
        {
            min_ = 1;
            max_ = UNBOUNDED;
        }
        else if (s.equals(Config.CARDINALITY_ZERO_TO_ONE))
        {
            min_ = 0;
            max_ = 1;
        }
        else if (s.equals(Config.CARDINALITY_ZERO_TO_MANY))
        {
            min_ = 0;
            max_ = UNBOUNDED;
        }
        else
        {
            throw new IllegalArgumentException("Illegal cardinality: " + s);
        }
    }

  //-------------------------------------------------------------------------
  /**
   * Gets the minimum cardinality.
   *
   * @return  the minimum
   */
  public int getMinimum()
  {
    return min_;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the maximum cardinality.
   *
   * @return  the maximum; UNBOUNDED means "*". UNBOUNDED happens to
   * be the Integer.MAX_VALUE, but who will ever distinguish cardinalities
   * of that magnitude. Noone will.
   */
  public int getMaximum()
  {
    return max_;
  }

  //-------------------------------------------------------------------------
  /**
   * Set the minimum cardinality.
   *
   * @param i  minimum cardinality
   * @throws IllegalArgumentException  if minimum cardinality is invalid
   */
  public void setMinimum(int i) throws IllegalArgumentException
  {
      min_ = i;
  }

  //-------------------------------------------------------------------------
  /**
   * Set the maximum cardinality.
   * If input parameter value is two or more, the maximum value will be set as UNBOUNDED.
   *
   * @param i  maximum cardinality
   * @throws IllegalArgumentException  if maximum cardinality is invalid
   */
  public void setMaximum(int i) throws IllegalArgumentException
  {
     max_=i;
  }
  //-------------------------------------------------------------------------
  /**
   *  Converts cardinality to its string representation.
   *
   * @return the string representation
   * @throws IllegalArgumentException  on internal error
   */
    public String toString()
	{
		String rtnString = Config.CARDINALITY_ZERO_TO_MANY;
		if (getMinimum()>0)
		{
			if (getMaximum()==1)
				rtnString = Config.CARDINALITY_ONE_TO_ONE;
			else
				rtnString = Config.CARDINALITY_ONE_TO_MANY;
		}
		else
		{
			if (getMaximum() == 1)
				rtnString = Config.CARDINALITY_ZERO_TO_ONE;
		}
		return rtnString;
	}
}
