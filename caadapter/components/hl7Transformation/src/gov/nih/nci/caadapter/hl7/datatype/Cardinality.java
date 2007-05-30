package gov.nih.nci.caadapter.hl7.datatype;

public class Cardinality {

	public static String  ZERO_TO_ONE="0..1";
	public static String  ZERO_TO_MANY="0..*";
	public static String  ONE_TO_ONE="1..1";
	public static String  ONE_TO_MANY="1..*";
	
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
