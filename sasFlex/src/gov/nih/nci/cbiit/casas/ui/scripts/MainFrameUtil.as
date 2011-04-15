// ActionScript file
import learnmath.mathml.formula.MathML;
import learnmath.windows.apps.EditorApp;

import mx.collections.ICollectionView;
import mx.collections.XMLListCollection;
import mx.controls.Alert;
import mx.controls.Menu;
import mx.events.ListEvent;
//Display tree node
private function treeMathLabel(item:Object):String {
	var node:XML = XML(item);
	if( node.localName() == "math" )
	{
		if (node.attribute('title').length()>0)
			return	node.@title;
	}
	else if (node.attribute('name').length()>0)
		return node.@name;
	
	return node.name();
	
}

// handle tree selection event		 
private function tree_itemClick(tree:Tree, evt:ListEvent):void {
	var item:Object = Tree(evt.currentTarget).selectedItem;
	editorId.mainPannel.setMathML(item+"");
	textareaId.text = editorId.mathML;
	if (tree.dataDescriptor.isBranch(item)) {
		tree.expandItem(item, !tree.isItemOpen(item), true);
	}
}

// Show popup menu TO-BE-DONE
private function createAndShow():void
{
	var myMenu:Menu = new Menu();
	myMenu.dataProvider = menuData;
	myMenu.labelField = "@label";
	myMenu.showRoot = false;
	// calling display() here has no result, because the data provider
	// has been set but the underlying NativeMenu hasn't been created yet.
	//			myMenu.show(this.stage, 10, 10);
	Alert.show(commonStoreXML, "menitem");
	
}

protected function getMathML(event:MouseEvent):void
{
	textareaId.text = editorId.mathML
}
protected function setMathML(event:MouseEvent):void
{
	editorId.mathML = textareaId.text;
}

protected function saveImageToServer(event:MouseEvent):void{
	Alert.show("saveImageOnServer", "callBackName");
	editorId.saveImageOnServer("JPG", 100, callbackName);
}

protected function callbackName(name:String):void{
	textareaId.text = "Image url: " + name;
}
protected function viewImageInBrowser(event:MouseEvent):void{
	editorId.viewImageInBrowser("JPG", 100);
}