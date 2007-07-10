package gov.nih.nci.caadapter.common;

import gov.nih.nci.caadapter.common.util.Config;

public class Cardinality {

	public static String  ZERO_TO_ONE = Config.CARDINALITY_ZERO_TO_ONE;
	public static String  ZERO_TO_MANY = Config.CARDINALITY_ZERO_TO_MANY;
	public static String  ONE_TO_ONE = Config.CARDINALITY_ONE_TO_ONE;
	public static String  ONE_TO_MANY = Config.CARDINALITY_ZERO_TO_MANY;
	
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
