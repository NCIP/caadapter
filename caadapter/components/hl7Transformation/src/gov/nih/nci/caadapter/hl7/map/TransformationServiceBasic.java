package gov.nih.nci.caadapter.hl7.map;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.util.GeneralTask;
import gov.nih.nci.caadapter.common.util.Stats;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.impl.MapParserImpl;

import java.io.File;
import java.io.FileReader;
import java.util.List;

public abstract class TransformationServiceBasic {
		protected Stats statistics = null;
	
	  protected boolean inputStringFlag = false;
//	  protected boolean isInputStream = false;
	  protected String sourceString = "";
	  protected File mapFile = null;
	  protected File sourceFile = null;
	  protected File specFile = null;
	  protected Mapping mapping = null;
	  protected ValidatorResults prepareValidatorResults = new ValidatorResults();
	  protected boolean preparedFlag = false;
	  
	  public Stats getStatistics()
	    {
	        return statistics;
	    }

	    /**
	     * Return the estimated amount of time needed, in terms of seconds.
	     *
	     * @return the estimated amount of time needed, in terms of seconds.
	     */

	    public abstract TransformationResult getEstimate() throws Exception;
	    public abstract List<TransformationResult> process(GeneralTask task);

	    protected MappingResult parseMapfile() throws Exception
	    {
	        MapParserImpl parser = new MapParserImpl();
	        long begintime = System.currentTimeMillis();
	        MappingResult mappingResult = parser.parse(mapFile.getParent(), new FileReader(mapFile));
	        statistics.mapParseTime += System.currentTimeMillis() - begintime;
	        return mappingResult;
	    }
	    
		/**
		 * Return true if continue; otherwise, return false.
		 * @param task
		 * @param toNotifyObject could be null.
		 * @return true if continue; otherwise, return false.
		 */
	    protected boolean notifyAndCheckTaskStatus(GeneralTask task, Object toNotifyObject)
		{
			boolean toProceed = true;
			if(task!=null)
			{
				if(task.isCanceled() || task.isDone())
				{
					toProceed = false;
				}
				else
				{//just to notify so that the task receiver could update the UI information, if necessary
					task.addTaskResult(toNotifyObject);
				}
			}
			return toProceed;
		}
	    
	    protected TransformationResult handleException(Exception e) //List<TransformationResult> v3messageResults
	    {
			String errorMessage = e.getMessage();
	        if ((errorMessage == null) || errorMessage.equalsIgnoreCase("null"))
	        {
	            errorMessage = "";
	        }
	        Message msg = MessageResources.getMessage("GEN0", new Object[]{errorMessage});
	        ValidatorResult validatorResult = new ValidatorResult(ValidatorResult.Level.FATAL, msg);
	        ValidatorResults vrs = new ValidatorResults();
	        vrs.addValidatorResult(validatorResult);
	        TransformationResult oneResult = new TransformationResult(MessageResources.getMessage("TRF2", new Object[]{}).toString(),
	            vrs);
	        Log.logException(this, e);
			return oneResult;
		}

}
