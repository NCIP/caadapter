/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.common;
/**
 * @author OWNER: $Author: phadkes $
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.7 $
 * @date       $Date: 2008-09-24 19:51:48 $
*/

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;

public class Cardinality
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

  private boolean isChoice = false;

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
  public Cardinality(int min, int max)
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
    public Cardinality(String s)
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
 * Looks up cardinality by its string representation.
 *
 * @param type  the string to look up
 * @throws IllegalArgumentException  if the string cannot be looked up
 */
  public Cardinality(CardinalityType type)
  {
      if (type == null)
      {
          throw new IllegalArgumentException("Null Cardinality Type");
      }

      try
      {
          setCardinalityValue(type.toString());
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
        int idx = s.indexOf(Config.SUFFIX_OF_CHOICE_CARDINALITY);

        if (idx < 0)
        {
            isChoice = false;
        }
        else
        {
            s = s.substring(0, idx).trim();
            isChoice = true;
        }
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
   * Gets the boolean value whether this cardinalityu is choice or not.
   *
   * @return isChoice value;
   */
  public boolean isChoice()
  {
    return isChoice;
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
      if (!((i == 0)||(i == 1)))
      {
          throw new IllegalArgumentException("Invalid Minimum Cardinality value : " + i);
      }
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
      if (i <= 0)
      {
          throw new IllegalArgumentException("Invalid Minimum Cardinality value : " + i);
      }
      else if (i == 1) max_ = 1;
      else max_ = UNBOUNDED;
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
        if (isChoice) rtnString = rtnString + " " + Config.SUFFIX_OF_CHOICE_CARDINALITY;
        return rtnString;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
