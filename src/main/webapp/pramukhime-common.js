if(document.getElementById('message-body') != null) {
	  pramukhIME.addLanguage(PramukhIndic);
	    pramukhIME.enable("form:message-body", document.getElementById('form:message-body'));
	    pramukhIME.onLanguageChange(scriptChangeCallback);
	    var lang = (getCookie('pramukhime_language', 'pramukhindic:hindi')).split(':');
	    pramukhIME.setLanguage(lang[1], lang[0]);
	   
	    document.getElementById('form:message-body').focus();
}




function check()
{

	    pramukhIME.addLanguage(PramukhIndic);
	    pramukhIME.enable("form:message-body", document.getElementById('form:message-body'));
	    pramukhIME.onLanguageChange(scriptChangeCallback);
	    var lang = (getCookie('pramukhime_language', 'pramukhindic:hindi')).split(':');
	    pramukhIME.setLanguage(lang[1], lang[0]);
	   
	    document.getElementById('form:message-body').focus();

}

function check1()
{
	
   pramukhIME.addLanguage(PramukhIndic);
   pramukhIME.enable("message-body", document.getElementById('message-body'));
   pramukhIME.onLanguageChange(scriptChangeCallback);
   document.getElementById('message-body').focus();

}

function setCookie(c_name, value, expdays) {
	
	alert("setcoockies");
    var expdate = new Date();
    expdate.setDate(expdate.getDate() + expdays);
    var c_value = escape(value) + ((expdays == null) ? "" : "; expires=" + expdate.toUTCString());
    document.cookie = c_name + "=" + c_value;
}
function getCookie(c_name, defaultvalue) {
    var i, x, y, cookies = document.cookie.split(";");
    for (i = 0; i < cookies.length; i++) {
        x = cookies[i].substr(0, cookies[i].indexOf("="));
        y = cookies[i].substr(cookies[i].indexOf("=") + 1);
        x = x.replace(/^\s+|\s+$/g, "");
        if (x == c_name) {
            return unescape(y);
        }
    }
    return defaultvalue;
}
// This functions adds option to drop down list
function addOption(elemid, text, value, selected) {
    var elem = document.getElementById(elemid);
    var newopt = document.createElement('option');
    newopt.text = text;
    newopt.value = value;
    newopt.selected = !(!selected);
    elem.add(newopt);
}

// Callback function which gets called when user presses F9 key.
function scriptChangeCallback(lang, kb, context) {
    if(document.getElementById('drpLanguage') != null) {
        var i, dd = document.getElementById('drpLanguage');

        for (i = 0; i < dd.options.length; i++) {
            if (dd.options[i].value == kb + ':' + lang) {
                dd.options[i].selected = true;
            }
        }
        setCookie('pramukhime_language', kb + ':' + lang, 10);
    }
}
// Changing the language by selecting from dropdown list
function changeLanguage(Language) {

	

	
    if(Language==false)
 	{
    	
     newLanguage = "english";
       	
 	}
 else
 	{
 	   newLanguage="pramukhindic:hindi"
 	}
  
	 
 var lang1 = newLanguage.split(':');
 pramukhIME.setLanguage(lang1[1], lang1[0]);
 pramukhIME.addLanguage(PramukhIndic);
 pramukhIME.enable("form:message-body", document.getElementById('form:message-body'));
 pramukhIME.onLanguageChange(scriptChangeCallback);
 var lang = (getCookie('pramukhime_language', newLanguage)).split(':');
 pramukhIME.setLanguage(lang[1], lang[0]);

 document.getElementById('form:message-body').focus();

 
}


function changeLanguage1(Language) {

	

	
    if(Language==false)
 	{
    	
     newLanguage = "english";
       	
 	}
 else
 	{
 	   newLanguage="pramukhindic:hindi"
 	}
  
	 
 var lang1 = newLanguage.split(':');
 pramukhIME.setLanguage(lang1[1], lang1[0]);
 pramukhIME.addLanguage(PramukhIndic);
 pramukhIME.enable("form:descripation", document.getElementById('form:descripation'));
 pramukhIME.onLanguageChange(scriptChangeCallback);
 var lang = (getCookie('pramukhime_language', newLanguage)).split(':');
 pramukhIME.setLanguage(lang[1], lang[0]);

 document.getElementById('form:descripation').focus();

 
}



function changeLanguage2(Language,id)
{
	

		if(Language==false)
	 	{
	    	
	     newLanguage = "english";
	       	
	 	}
		else
	 	{
	 	   newLanguage="pramukhindic:hindi"
	 	}
	  
		 
	 var lang1 = newLanguage.split(':');
	 pramukhIME.setLanguage(lang1[1], lang1[0]);
	 pramukhIME.addLanguage(PramukhIndic);
	 pramukhIME.enable(id, document.getElementById(id));
	 pramukhIME.onLanguageChange(scriptChangeCallback);
	 var lang = (getCookie('pramukhime_language', newLanguage)).split(':');
	 pramukhIME.setLanguage(lang[1], lang[0]);

	 document.getElementById(id).focus();
	

}

function changeLanguage3(checklanguage,Language,id)
{
    var languagetest= document.getElementById(checklanguage).value;
	if(languagetest=='english')
		{
		
		}
	else
		{
		if(Language==false)
	 	{
	    	
	     newLanguage = "english";
	       	
	 	}
		else
	 	{
	 	   newLanguage="pramukhindic:hindi"
	 	}
	  
		 
	 var lang1 = newLanguage.split(':');
	 pramukhIME.setLanguage(lang1[1], lang1[0]);
	 pramukhIME.addLanguage(PramukhIndic);
	 pramukhIME.enable(id, document.getElementById(id));
	 pramukhIME.onLanguageChange(scriptChangeCallback);
	 var lang = (getCookie('pramukhime_language', newLanguage)).split(':');
	 pramukhIME.setLanguage(lang[1], lang[0]);

	 document.getElementById(id).focus();
		}
    
}