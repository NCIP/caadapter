package gov.nih.nci.cbiit.casas.components
{
	

	import fl.core.UIComponent;
	
	import flash.display.MovieClip;
	import flash.events.KeyboardEvent;
	import flash.utils.Timer;
	
	import learnmath.mathml.components.MathMLEditor;
	import learnmath.mathml.formula.Constants;
	import learnmath.windows.ConfigManager;
	
	import mx.controls.Alert;
	import mx.core.IUIComponent;
	
	import flash.events.TimerEvent;

	public class FormulaEditor extends MathMLEditor 
	{
		public function FormulaEditor()
		{
			//duplicate MathMLEditor Constructor
			ConfigManager.urlFonts="./";
			Constants.setUrlFonts("./");
//			ConfigManager.setConfig("disableSave","false");
//			ConfigManager.setConfig("disableOpen","false");
			ConfigManager.disableOpen=new Boolean(false);
			ConfigManager.disableSave=new Boolean(false);
			addEventListener(KeyboardEvent.KEY_DOWN, processKey);
//			mainPannel.name= "<a href=\'http://ncicb.nci.nih.gov/\'>NCI Center for Biomedical Informatics & Information Technology - Scientific Algorithm Service</a>";
//			mainPannel.draw();
			return;
		}
		
//		override protected function createChildren():void
//		{
//			Alert.show("before created by super", mainPannel+"");
//			super.createChildren();
//			Alert.show("created by super", mainPannel+"");
//			mainPannel=null;
//			while (this.numChildren>0)
//			{
//				this.removeChildAt(0);
//			}
//			var _loc_1:*=new MovieClip();
//			var editorW:Number=800;
//			var editorH:Number=500;
//
//			mainPannel = new FormulaPanel(_loc_1, 0,0,editorW, editorH, returnFocus);
//			mainPannel.draw();
//			mainPannel.setMathML("..xx");
//			Alert.show(mainPannel+"", mainPannel.getMathML());
//			var _loc_2:*=new UIComponent();
//			addChild(_loc_2);
//			
//			var _loc_3:*=new Timer(100,1);
//			_loc_3.addEventListenr(TimerEvent.TIMER, redrawEditor);
//			_loc_3.start();
//			return;
//		}
		
		//override private method
		private function redrawEditor(event:TimerEvent):void
		{
			mainPannel.draw();
			if (focusManager!=null)
			{
				focusManager.setFocus(this);
			}
			return;
		}
	}

}