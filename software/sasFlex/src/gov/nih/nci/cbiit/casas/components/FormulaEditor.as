package gov.nih.nci.cbiit.casas.components
{
	

//	import fl.core.UIComponent;
//	import flash.display.MovieClip;
//	import flash.events.TimerEvent;
//	import flash.utils.Timer;
	import flash.events.KeyboardEvent;

//	import mx.containers.Canvas;
//	import mx.controls.Alert;
//	import mx.controls.listClasses.BaseListData;
//	import mx.controls.listClasses.IDropInListItemRenderer;
//	import mx.controls.listClasses.IListItemRenderer;
//	import mx.core.IDataRenderer;
//	import mx.core.IFlexModuleFactory;
//	import mx.core.IFontContextComponent;
//	import mx.core.IUIComponent;
//	import mx.managers.IFocusManagerComponent;
	import mx.events.FlexEvent;
	
	import learnmath.mathml.components.MathMLEditor;
	import learnmath.mathml.formula.Constants;
	import learnmath.windows.ConfigManager;


	public class FormulaEditor extends MathMLEditor 
	{	
		public function FormulaEditor()
		{

			super();
			//super does not set the followng properteis correctly, reset them
			ConfigManager.disableOpen=new Boolean(false);
			ConfigManager.disableSave=new Boolean(false);
			addEventListener(FlexEvent.CREATION_COMPLETE,onCreationComplete) ;
			return;
		}
	
		/**
		 * Reset formual editor formula after creation complete
		 */
		private function onCreationComplete(event : FlexEvent):void
		{	
			var _loc_1:* =mainPannel.parent;// new MovieClip();
			var _editorHeight:Number = 500;
			var _editorWidth:Number = 800;
			//use local version of FormulaEditor panel
			mainPannel = new FormulaPanel(_loc_1, 0, 0, _editorWidth, _editorHeight, returnFocus);
			var p1:XML =new XML("<math><mrow><mtext>default</mtext><mo selected='true'>&times;</mo><mtext>0123456789</mtext></mrow>" + "</math>")
			mainPannel.setMathML(p1);
			mainPannel.draw();			
			return;
		}
	}
}