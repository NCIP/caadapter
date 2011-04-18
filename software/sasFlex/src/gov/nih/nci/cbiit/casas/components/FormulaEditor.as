package gov.nih.nci.cbiit.casas.components
{
	

	import fl.core.UIComponent;
	
	import flash.display.MovieClip;
	import flash.events.KeyboardEvent;
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	import learnmath.mathml.components.MathMLEditor;
	import learnmath.mathml.formula.Constants;
	import learnmath.windows.ConfigManager;
	import learnmath.windows.apps.EditorApp;
	
	import mx.containers.Canvas;
	import mx.controls.Alert;
	import mx.controls.listClasses.BaseListData;
	import mx.controls.listClasses.IDropInListItemRenderer;
	import mx.controls.listClasses.IListItemRenderer;
	import mx.core.IDataRenderer;
	import mx.core.IFlexModuleFactory;
	import mx.core.IFontContextComponent;
	import mx.core.IUIComponent;
	import mx.managers.IFocusManagerComponent;
	import mx.events.FlexEvent;


	public class FormulaEditor extends MathMLEditor
		//extends Canvas implements IDataRenderer, IDropInListItemRenderer, IListItemRenderer, IFontContextComponent, IFocusManagerComponent 
	{
		private var _mathML:String = "";
		var _editorHeight:Number = 500;
//		private var _listData:BaseListData;
		var _editorWidth:Number = 800;
//		public var mainPannel:EditorApp;
		
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
			addEventListener(FlexEvent.CREATION_COMPLETE,onCreationComplete) ;
			return;
		}
	
		private function onCreationComplete(event : FlexEvent):void
		{
//			this.removeAllCh ildren();
//			this.invalidateDisplayList();
//			createCustomButtons();
			mainPannel.name= "<a href=\'http://ncicb.nci.nih.gov/\'>NCI Center for Biomedical Informatics & Information Technology - Scientific Algorithm Service</a>";
			var p1:XML =new XML("<math><mrow><mtext>default</mtext><mo selected='true'>&times;</mo><mtext>0123456789</mtext></mrow>" + "</math>")
			mainPannel.setMathML(p1);
		}
		

//		private function createCustomButtons() : void
//		{
//			super.createChildren();
//			mainPannel = null;
//			while (this.numChildren > 0)
//			{
//				
//				this.removeChildAt(0);
//			}
//			var _loc_1:* = new MovieClip();
//			mainPannel = new EditorApp(_loc_1, 0, 0, _editorWidth, _editorHeight, returnFocus);
//			mainPannel.draw();
//			mainPannel.setMathML(_mathML);
//			var _loc_2:* = new UIComponent();
//			addChild(_loc_2);
//			_loc_2.addChild(_loc_1);
//			var _loc_3:* = new Timer(100, 1);
//			_loc_3.addEventListener(TimerEvent.TIMER, redrawEditor);
//			_loc_3.start();
//			return;
//		}// end function
/**		
		override protected function measure() : void
		{
			measuredHeight = editorHeight;
			measuredMinHeight = editorHeight;
			measuredWidth = editorWidth;
			measuredMinWidth = editorWidth;
			return;
		}// end function
		
		public function get fontContext() : IFlexModuleFactory
		{
			return moduleFactory;
		}
		public function set fontContext(moduleFactory:IFlexModuleFactory) : void
		{
			this.moduleFactory = moduleFactory;
			return;
		}// end function

		public function get listData() : BaseListData
		{
			return _listData;
		}// end function
		public function set listData(value:BaseListData) : void
		{
			_listData = value;
			return;
		}// end function

		public function get mathML() : String
		{
			_mathML = mainPannel.getMathML();
			return _mathML;
		}// end function

		public function set mathML(mathML:String) : void
		{
			_mathML = mathML;
			if (mainPannel != null)
			{
				mainPannel.setMathML(_mathML);
			}
			return;
		}// end function

		public function get editorHeight() : Number
		{
			return _editorHeight;
		}// end function

		public function get editorWidth() : Number
		{
			return _editorWidth;
		}// end function

		public function set editorWidth(w:Number) : void
		{
			this._editorWidth = w;
			width = w;
			invalidateProperties();
			invalidateSize();
			invalidateDisplayList();
			return;
		}// end function

		public function set editorHeight(h:Number) : void
		{
			this._editorHeight = h;
			height = h;
			invalidateProperties();
			invalidateSize();
			invalidateDisplayList();
			return;
		}
*/	
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
/**	
		public function saveImageOnServer(type:String, compression:int, callbackName:Function) : void
		{
			mainPannel.saveImageOnServer(type, compression, callbackName);
			return;
		}// end function
		public function setConfiguration(key:String, value:String) : void
		{
			ConfigManager.setConfig(key, value);
			return;
		}// end function
		public function viewImageInBrowser(type:String, compression:int) : void
		{
			mainPannel.viewImageInBrowser(type, compression);
			return;
		}// end function
		public function processKey(event:KeyboardEvent) : void
		{
			if (mainPannel == null)
			{
				return;
			}
			mainPannel.processKey(event);
			return;
		}// end function

		public function returnFocus() : void
		{
			if (focusManager != null)
			{
				focusManager.setFocus(this);
			}
			return;
		}// end function
		
		public function getBase64Image(type:String, precission:int = 100) : String
		{
			return mainPannel.getBase64Image(type, precission);
		}// end function
 */
	}

}