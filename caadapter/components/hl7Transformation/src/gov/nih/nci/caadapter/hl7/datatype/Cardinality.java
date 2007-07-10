package gov.nih.nci.caadapter.hl7.datatype;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;

import java.util.HashMap;


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
			if (getMinimum() == 1)
				rtnString = Config.CARDINALITY_ZERO_TO_ONE;
		}
		return rtnString;
	}
  /*
  public String toString()
  {
    return String.valueOf(min_) + ".." +
      (max_ == UNBOUNDED ? "*" : String.valueOf(max_));
  }
  */
}

/*
import gov.nih.nci.caadapter.common.util.Config;

public class Cardinality {

	public static String  ZERO_TO_ONE = Config.CARDINALITY_ZERO_TO_ONE;
	public static String  ZERO_TO_MANY = Config.CARDINALITY_ZERO_TO_MANY;
	public static String  ONE_TO_ONE = Config.CARDINALITY_ONE_TO_ONE;
	public static String  ONE_TO_MANY = Config.CARDINALITY_ONE_TO_MANY;

	private int minimum=0;
	private int maximum=0;

	public Cardinality(int min, int max)
	{
		minimum=min;
		maximum=max;
	}

	public int getMaximum() {
		return maximum;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	public int getMinimum() {
		return minimum;
	}

	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}

	public String toString()
	{
		String rtnString=ZERO_TO_MANY;
		if (getMinimum()>0)
		{
			if (getMaximum()==1)
				rtnString=ONE_TO_ONE;
			else
				rtnString=ONE_TO_MANY;
		}
		else
		{
			if (getMinimum()==1)
				rtnString=ZERO_TO_ONE;
		}
		return rtnString;
	}
}
*/