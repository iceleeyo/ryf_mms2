//检验查询条件数据合法性
 function checkData(){
	 var field = document.getElementById("field").value;
     var auth_type = document.getElementById("auth_type").value;
     var date = document.getElementById("date").value;
     if(field !='') {field = field.trim();}
        if(v(field) && field!=''){
            alert("输入项格式不对，请重新输入。");
            document.all.field.value='';
            document.all.field.focus();            
            return false;
        }    
    RiskmanageService.getBlackWhiteList(field,date,auth_type,callBack);  
}

var callBack = function(Page){
   dwr.util.removeAllRows("biaotou");
   var dtable = document.getElementById("biaotou");
   document.getElementById("cardList").style.display = '';
   if(Page.length>=1){
    for ( var i = 0; i <Page.length; i++) {
        CardAuth = Page[i];
        // 在table中新建一行
        var elTr = dtable.insertRow(-1);
        elTr.setAttribute("align","center");
        //创建序号
        var xuhaoTd = elTr.insertCell(-1);
       // xuhaoTd.setAttribute("align","center");
        xuhaoTd.innerHTML = i+1;
        
       var bankTd = elTr.insertCell(-1);
       bankTd.innerHTML = shadowAcc(CardAuth.field);
       //录入时间
       var dateTd = elTr.insertCell(-1);
       dateTd.innerHTML = CardAuth.date;
       //操作
       var caozuoTd = elTr.insertCell(-1);
       caozuoTd.innerHTML = "<input type=\"button\" onClick=\"return delList(" + CardAuth.id + " )\" value=\"删除\"> ";
    }
  } else {
		document.getElementById("biaotou").appendChild(creatNoRecordTr(4));
  }  
};

//删除黑名单
function delList(id){
    if (confirm("确认要删除该条记录？")){
        RiskmanageService.deleteList(id,function(msg){
            if(msg=='ok'){
                alert("删除成功");
                checkData();
            }else{
                alert(msg);
                }
            }
        )
    }
}

function v(data){
    return (!isFigure(data) && !isId(data));
}

//增加黑名单
function addList(){
    var field = document.getElementById("field").value;
    var auth_type = document.getElementById("auth_type").value;
    if(field !='') {field = field.trim();}
    if(field =='') {alert("必填项不能为空");return false;}
    if(v(field)){
     alert("输入项格式不对，请重新输入。");
     document.all.field.value='';
     document.all.field.focus();            
     return false;
    }
RiskmanageService.addList2(field,auth_type,function(msg){
       if(msg=='ok'){
                alert("添加成功");
                document.getElementById("field").value='';
                checkData();
         }else{
                alert(msg);
               }
            }
       )
}        