function convert_to_unicode(str)
{
	var array_one = new Array( 
	"ñ","Q+Z","sas","aa",")Z","ZZ","‘","’","“","”",
	
	"å",  "ƒ",  "„",   "…",   "†",   "‡",   "ˆ",   "‰",   "Š",   "‹", 
	
	"¶+",   "d+", "[+k","[+", "x+",  "T+",  "t+", "M+", "<+", "Q+", ";+", "j+", "u+",
	"Ùk", "Ù", "ä", "–", "—","é","™","=kk","f=k",  
	
	"à",   "á",    "â",   "ã",   "ºz",  "º",   "í", "{k", "{", "=",  "«",   
	"Nî",   "Vî",    "Bî",   "Mî",   "<î", "|", "K", "}",
	"J",   "Vª",   "Mª",  "<ªª",  "Nª",   "Ø",  "Ý", "nzZ",  "æ", "ç", "Á", "xz", "#", ":",
	
	"v‚","vks",  "vkS",  "vk",    "v",  "b±", "Ã",  "bZ",  "b",  "m",  "Å",  ",s",  ",",   "_",
	
	"ô",  "d", "Dk", "D", "[k", "[", "x","Xk", "X", "Ä", "?k", "?",   "³", 
	"pkS",  "p", "Pk", "P",  "N",  "t", "Tk", "T",  ">", "÷", "¥",
	
	"ê",  "ë",   "V",  "B",   "ì",   "ï", "M+", "<+", "M",  "<", ".k", ".",    
	"r",  "Rk", "R",   "Fk", "F",  ")", "n", "/k", "èk",  "/", "Ë", "è", "u", "Uk", "U",   
	
	"i",  "Ik", "I",   "Q",    "¶",  "c", "Ck",  "C",  "Hk",  "H", "e", "Ek",  "E",
	";",  "¸",   "j",    "y", "Yk",  "Y",  "G",  "o", "Ok", "O",
	"'k", "'",   "\"k",  "\"",  "l", "Lk",  "L",   "g", 
	
	"È", "z", 
	"Ì", "Í", "Î",  "Ï",  "Ñ",  "Ò",  "Ó",  "Ô",   "Ö",  "Ø",  "Ù","Ük", "Ü",
	
	"‚",    "ks",   "kS",   "k",  "h",    "q",   "w",   "`",    "s",    "S",
	"a",    "¡",    "%",     "W",  "•", "·", "∙", "·", "~j",  "~", "\\","+"," ः",
	"^", "*",  "Þ", "ß", "(", "¼", "½", "¿", "À", "¾", "A", "-", "&", "&", "Œ", "]","~ ","@")

	var array_two = new Array(
	"॰","QZ+","sa","a","र्द्ध","Z","\"","\"","'","'",
	
	"०",  "१",  "२",  "३",     "४",   "५",  "६",   "७",   "८",   "९",   
	
	"फ़्",  "क़",  "ख़", "ख़्",  "ग़", "ज़्", "ज़",  "ड़",  "ढ़",   "फ़",  "य़",  "ऱ",  "ऩ", 
	"त्त", "त्त्", "क्त",  "दृ",  "कृ","न्न","न्न्","=k","f=",
	
	"ह्न",  "ह्य",  "हृ",  "ह्म",  "ह्र",  "ह्",   "द्द",  "क्ष", "क्ष्", "त्र", "त्र्", 
	"छ्य",  "ट्य",  "ठ्य",  "ड्य",  "ढ्य", "द्य", "ज्ञ", "द्व",
	"श्र",  "ट्र",    "ड्र",    "ढ्र",    "छ्र",   "क्र",  "फ्र", "र्द्र",  "द्र",   "प्र", "प्र",  "ग्र", "रु",  "रू",
	
	"ऑ",   "ओ",  "औ",  "आ",   "अ", "ईं", "ई",  "ई",   "इ",  "उ",   "ऊ",  "ऐ",  "ए", "ऋ",
	
	"क्क", "क", "क", "क्", "ख", "ख्", "ग", "ग", "ग्", "घ", "घ", "घ्", "ङ",
	"चै",  "च", "च", "च्", "छ", "ज", "ज", "ज्",  "झ",  "झ्", "ञ",
	
	"ट्ट",   "ट्ठ",   "ट",   "ठ",   "ड्ड",   "ड्ढ",  "ड़", "ढ़", "ड",   "ढ", "ण", "ण्",   
	"त", "त", "त्", "थ", "थ्",  "द्ध",  "द", "ध", "ध", "ध्", "ध्", "ध्", "न", "न", "न्",    
	
	"प", "प", "प्",  "फ", "फ्",  "ब", "ब", "ब्",  "भ", "भ्",  "म",  "म", "म्",  
	"य", "य्",  "र", "ल", "ल", "ल्",  "ळ",  "व", "व", "व्",   
	"श", "श्",  "ष", "ष्", "स", "स", "स्", "ह", 
	
	"ीं", "्र",    
	"द्द", "ट्ट","ट्ठ","ड्ड","कृ","भ","्य","ड्ढ","झ्","क्र","त्त्","श","श्",
	
	"ॉ",  "ो",   "ौ",   "ा",   "ी",   "ु",   "ू",   "ृ",   "े",   "ै",
	"ं",   "ँ",   "ः",   "ॅ",  "ऽ", "ऽ", "ऽ", "ऽ", "्र",  "्", "?", "़",":",
	"‘",   "’",   "“",   "”",  ";",  "(",    ")",   "{",    "}",   "=", "।", ".", "-",  "µ", "॰", ",","् ","/")

	var array_one_length = array_one.length ;

	var modified_substring = document.getElementById(str).value  ;
    var text_size = document.getElementById(str).value.length ;
    var processed_text = '' ;
    var sthiti1 = 0 ;  var sthiti2 = 0 ;  var chale_chalo = 1 ;
    var max_text_size = 6000;

	while ( chale_chalo == 1 ) 
	{
		sthiti1 = sthiti2 ;
		if ( sthiti2 < ( text_size - max_text_size ) )  
		{ 
			sthiti2 +=  max_text_size ;
			while (document.getElementById(str).value.charAt ( sthiti2 ) != ' ') {sthiti2--;}
		} 
		else 
		{ sthiti2 = text_size  ;  chale_chalo = 0 }

		var modified_substring = document.getElementById(str).value.substring ( sthiti1, sthiti2 )  ;
		Replace_Symbols( ) ;
    	processed_text += modified_substring ;
    	document.getElementById(str).value = processed_text  ;
   	}

	function Replace_Symbols( )
	{
		if ( modified_substring != "" )
		{
			for ( input_symbol_idx = 0;   input_symbol_idx < array_one_length;    input_symbol_idx++ )
			{
				idx = 0  ;
				while (idx != -1 )
				{
					modified_substring = modified_substring.replace( array_one[ input_symbol_idx ] , array_two[input_symbol_idx] )
					idx = modified_substring.indexOf( array_one[input_symbol_idx] )
				}
			}
			modified_substring = modified_substring.replace( /±/g , "Zं" ) ;
			modified_substring = modified_substring.replace( /Æ/g , "र्f" ) ;
			var position_of_i = modified_substring.indexOf( "f" )
	
			while ( position_of_i != -1 )
			{
				var charecter_next_to_i = modified_substring.charAt( position_of_i + 1 )
				var charecter_to_be_replaced = "f" + charecter_next_to_i
				modified_substring = modified_substring.replace( charecter_to_be_replaced , charecter_next_to_i + "ि" ) 
				position_of_i = modified_substring.search( /f/ , position_of_i + 1 )
			}
	
			modified_substring = modified_substring.replace( /Ç/g , "fa" ) ;
			modified_substring = modified_substring.replace( /É/g , "र्fa" ) ;
			var position_of_i = modified_substring.indexOf( "fa" )
			
			while ( position_of_i != -1 )
			{
				var charecter_next_to_ip2 = modified_substring.charAt( position_of_i + 2 )
				var charecter_to_be_replaced = "fa" + charecter_next_to_ip2
				modified_substring = modified_substring.replace( charecter_to_be_replaced , charecter_next_to_ip2 + "िं" ) 
				position_of_i = modified_substring.search( /fa/ , position_of_i + 2 )
			}
		
			modified_substring = modified_substring.replace( /Ê/g , "ीZ" ) ; 
			var position_of_wrong_ee = modified_substring.indexOf( "ि्" ) 
			while ( position_of_wrong_ee != -1 )  //while-03
			{
				var consonent_next_to_wrong_ee = modified_substring.charAt( position_of_wrong_ee + 2 )
				var charecter_to_be_replaced = "ि्" + consonent_next_to_wrong_ee 
				modified_substring = modified_substring.replace( charecter_to_be_replaced , "्" + consonent_next_to_wrong_ee + "ि" ) 
				position_of_wrong_ee = modified_substring.search( /ि्/ , position_of_wrong_ee + 2 ) 
			} 
			set_of_matras = "अ आ इ ई उ ऊ ए ऐ ओ औ ा ि ी ु ू ृ े ै ो ौ ं : ँ ॅ" 
			var position_of_R = modified_substring.indexOf( "Z" )
	
			while ( position_of_R > 0 )
			{
				probable_position_of_half_r = position_of_R - 1 ;
				var charecter_at_probable_position_of_half_r = modified_substring.charAt( probable_position_of_half_r )
				while ( set_of_matras.match( charecter_at_probable_position_of_half_r ) != null )
				{
					probable_position_of_half_r = probable_position_of_half_r - 1 ;
					charecter_at_probable_position_of_half_r = modified_substring.charAt( probable_position_of_half_r ) ;
				}
				charecter_to_be_replaced = modified_substring.substr ( probable_position_of_half_r , ( position_of_R - probable_position_of_half_r ) ) ;
				new_replacement_string = "र्" + charecter_to_be_replaced ; 
				charecter_to_be_replaced = charecter_to_be_replaced + "Z" ;
				modified_substring = modified_substring.replace( charecter_to_be_replaced , new_replacement_string ) ;
				position_of_R = modified_substring.indexOf( "Z" ) ;
			}
		} 
	} 
} 