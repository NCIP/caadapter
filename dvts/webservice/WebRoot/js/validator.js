function checkReqdFields()	
	{
		var errors = "";
		var reqFields = new Array('title', 'description', 'protocolDocument','organ','irbDate','primaryEndPoint','secondaryEndPoint','primarySite','participatingSites','sporePiName','studyPiName','type','phase','agents','startDate','endDate','regimen');
		
		if(document.forms[0].title.value == "")
			errors += "Fill in the title\n";
	
		if(document.forms[0].description.value == "")
			errors += "Fill in the description\n";
		if(document.forms[0].accessbility.value == "")
			errors += "Select the Accessibility\n";
		//if(document.forms[0].organSite.value == "")
			//errors += "Select an OrganSite\n";			
		if(document.forms[0].irbDate.value == "")
			errors += "Fill in the IRB Date\n";
		if(document.forms[0].primaryEndpoints.value == "")
			errors += "Fill in the Primary Endpoint\n";
		if(document.forms[0].secondaryEndpoints.value == "")
			errors += "Fill in the Secondary Endpoint\n";
	//	if(document.forms[0].performanceSite.value == "")
		//	errors += "Select the Primary performance Site\n";
		//if(document.forms[0].participatingSites.value == "")
			//errors += "Select the Participating Sites\n";
		//if(document.forms[0].sporePiName.value == "")
			//errors += "Select the Center Director\n";
		if(document.forms[0].studyPI.value == "")
			errors += "Select the Study PI\n";
		//if(document.forms[0].clinApps.value ==	 "")
		//	errors += "Select the type of trial\n";		
			
		if(document.forms[0].trialPhase.value == "")
			errors += "Select the Trial Phase\n";
		if(document.forms[0].agents.value == "")
			errors += "Select the Agents\n";
		if(document.forms[0].startDate.value ==	 "")
			errors += "Fill in the Start Date\n";					
		
		if(document.forms[0].endDate.value == "")
			errors += "Fill in the End Date\n";
		if(document.forms[0].treatmentSchedule.value == "")
			errors += "Fill in the Dosage and Regimen\n";
		
		//make sure accruals are numbers
/*		var t = 'no';
  for(i=35; i<53; i++)
 	{
 		var field = document.forms[0].elements[i].value;
		
 		if (field != ""){
			var val = parseInt(field);
 			var newval = ""+val; 
			if (newval != field) {
				t = 'yes';
    			//alert("You must give numeric values");
				document.forms[0].elements[i].value="";
 			}
		}
 	} //close for
	if(t == 'yes')
	 {
		errors+="Accruals must be numeric values\n";
	 }	
*/	 
		if(errors == "")
			return true;
		else
		{
		 	var errorString = "Please correct the following:\n\n" + errors;
		 	alert(errorString);
		 	return false;
		}
	}

function selectAll(tbox)
{
  var i;
  for (i = 0; i < tbox.options.length; i++)
  {
    var no = new Option();

    no.value  = tbox.options[i].value;
    no.text = tbox.options[i].text;
    no.selected = true;
    tbox.options[i] = no;
  }
}


function isLegal(txt) {
var invalids = "!@#$%^&*()~`,'<>/?;:\|+={}[] "
for(i=0; i<invalids.length; i++) {
	if(txt.indexOf(invalids.charAt(i)) >= 0 ) {
		return false;
	}
}
return true;
}

//get the file name (all chars after the last \) and check for legal chars
function validateIt()
{
//alert(navigator.platform);

var platformz = navigator.platform.substring(0,3);
//alert(platformz);

fullName = new String(document.forms[0].protocolDocument.value);
//alert(fullName);
if (platformz == "Win")
	var os = "\\";
else
	var os = "/";
var t = fullName.lastIndexOf(os);
//alert(t);
var fname = fullName.substring(t+1);
//alert(fname);

if(isLegal(fname)) {return true;}
else {
	alert("Your file contains illegal characters. \n Please rename your file and do not include:\n" +
	"!@#$%^&*()~`,'<>/?;:\|+={}[] or a space\n");
	return false;
}
}
