document.write("<script type='text/javascript' src='../../public/js/ryt.js'></script>"); 	
var currPage=1;
function searchOpersInfo(pageNo){
   var mid = document.getElementById("mid").value.trim();
   var operName = document.getElementById("oper_name").value.trim();
   if(mid == '' && operName == ''){alert("至少输入一个查询条件");return false;}
   if(mid != '' && !isFigure(mid)){
         alert("商户号只能为整数"); 
         return false;
   }
   MerchantService.getOpers4Object(mid,operName,pageNo,callback);
}
var callback = function(operInfoList){
      document.getElementById("body4List").style.display="";
        var cellFuncs = [
                 function(OperInfo) { return OperInfo.mid; },
                 function(OperInfo) { return OperInfo.operId; },
                 function(OperInfo) { return OperInfo.operName; },
                 function(OperInfo) { return OperInfo.operTel; },
                 function(OperInfo) { return OperInfo.operEmail; },
                 function(OperInfo) { return OperInfo.regDate; },
                 function(OperInfo) { return OperInfo.state==0 ? '正常' : '关闭'; }
             ]; 
         currPage=operInfoList.pageNumber;    //设置当前页      
      paginationTable(operInfoList,"operInfoBody",cellFuncs,"","searchOpersInfo");
 }