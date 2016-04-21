 
document.write("<script type='text/javascript' src='../../public/js/ryt.js'></script>"); 
function tset(){
   var mid = document.getElementById("mid").value;
	 if(mid == ''){
	 	alert("请选择商户！");
	 	return false;
	 }
	 return true;
}

  //选择所有
function selectall(a){
   	var f = document.forms["bank_form"];
    if (a == 1){
        for (i=0;i<f.elements.length;i++){
            if(f.elements[i].id=="bank_id"){
             f.elements[i].checked = true;
            }
        }
     }
      if (a == 2){
        for (i=0;i<f.elements.length;i++){
            if(f.elements[i].id=="bank_id"){
             f.elements[i].checked = false;
            }
        }
     }
   
}
