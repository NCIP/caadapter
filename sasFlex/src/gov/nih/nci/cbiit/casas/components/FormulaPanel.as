package gov.nih.nci.cbiit.casas.components
{
	import flash.display.MovieClip;
	
	import learnmath.windows.apps.EditorApp;
	
	import mx.controls.Alert;
	public class FormulaPanel extends EditorApp
	{
		public function FormulaPanel(arg0:MovieClip, arg1:int, arg2:int, arg3:int, arg4:int, arg5:Function)
		{
			super(arg0, arg1, arg2, arg3, arg4, arg5);
			name= "<a href=\'http://ncicb.nci.nih.gov/\'>NCI Center for Bioinformatics &amp; Information Technology - Scientific Algorithm Service</a>";
			return;
		}
		
		override public function openAction()
		{
//			var _loc_1:* = String(ExternalInterface.call("getMathMLFromJavascript"));
//			if (_loc_1 != null)
//			{
//			}
//			if (_loc_1.length != 0)
//			{
//			}
//			if (_loc_1 == "null")
//			{
//				return;
//			}
//			this.editor.newFormula();
//			this.editor.insert(_loc_1, this.lastStyle);
			Alert.show("open is cliked...","Scientific Algorithm Service:openAction()");
			super.openAction();
			return;
		}// end function
		
		override public function saveAction()
		{
//			var _loc_1:* = this.convToMathML(this.editor.getMathMLString());
//			ExternalInterface.call("saveMathMLToJavascript", _loc_1);
//			this.isSaved = true;
			Alert.show("save is cliked...","Scientific Algorithm Service:saveAction()");
			super.saveAction();
			return;
		}// end function
	}
}