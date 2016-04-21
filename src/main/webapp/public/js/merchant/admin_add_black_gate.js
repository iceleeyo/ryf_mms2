 document.write("<script type='text/javascript' src='../../public/js/ryt.js'></script>");
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

function tset(){    
   var singleMid = document.getElementById("mid").value;
     if(singleMid == ''){
        alert("请选择商户！");
        return false;
     }  
}