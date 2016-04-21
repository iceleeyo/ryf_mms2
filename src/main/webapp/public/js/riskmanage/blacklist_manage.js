 //检验查询条件数据合法性
var pageNo=1;
 function checkData(){
	 queryForPage(1);
   }
 function queryForPage(page){
	 pageNo = page;
	 var field = document.getElementById("field").value.trim();
     var auth_type = document.getElementById("auth_type").value;
     var date = document.getElementById("date").value;
     var fieldType = document.getElementById("fieldType").value;
        if(v(field) && field!=''){
            alert("输入项格式不对，请重新输入。");
            document.all.field.value='';
            document.all.field.focus();            
            return false;
        }    
    RiskmanageService.getBlackWhiteList(pageNo,fieldType,field,date,auth_type,callBack);  
 }

   function v(data){
   	return (!isFigure(data) && !isId(data));
  }

  var callBack = function(pageObj){
	  var incrNo = (pageNo-1)*15+1;
      dwr.util.removeAllRows("biaotou");
      document.getElementById("cardList").style.display = '';
	  if(pageObj==null){
		  document.getElementById("biaotou").appendChild(creatNoRecordTr(4));
		  return;
	   }  
    var cellFuncs = [
                     function(obj) { return incrNo++; },
                     function(obj) { return obj.fieldType==3?shadowPhone(obj.field):shadowAcc(obj.field); },
                     function(obj) { return obj.date; },
                     function(obj) { return "<input type=\"button\" onClick=\"return delList(" + obj.id + " )\" value=\"删除\"> ";}
                 ];
  	paginationTable(pageObj,"biaotou",cellFuncs,"","queryForPage");
          	
  };
   
   //删除黑(白)名单
   function delList(id){
       if (confirm("确认要删除该条记录？")){
    	      flush=false;//刷新标志
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
   //增加黑(白)名单
   function addList(){
       var field = document.getElementById("field").value;
       var auth_type = document.getElementById("auth_type").value;
       var fieldType = document.getElementById("fieldType").value;
       if(field !='') {field = field.trim();}
       if(field =='') {alert("必填项不能为空");return false;}
       if(fieldType ==0) {
    	   alert("卡类型不能为空，请选择");return false;
    	}else if(fieldType==1){//!isFigure(data) && !isId(data)
    	   if(!isFigure(field)){
    		   alert("卡号格式不正确！");
    		   return false;
    		}
       }else if(fieldType==2){
    	   if(!isId(field)){
    		   alert("身份证格式不正确！");
    		   return false;
    	   }
       }else{
    	   if(!isMobel(field)){
    		   alert("手机号格式不正确！");
    		   return false;
    	   }
       }
    flush=false;//刷新标志
   RiskmanageService.addList2(field,fieldType,auth_type,function(msg){
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
  //刷新黑(白)名单的ETS
   function refreshCardAuth(){
	   flush=true;//刷新标志
	   CommonService.refreshCardAuth(function(msg){
		   if(msg==true) alert('刷新成功');
		   else alert('刷新失败');
	  })
	   
   }
   
   var f = new GreyFrame('INLINEIFRAME',700,400);
      f.setClassName("MyGreyFrame"); 
      function showFrame(){
   		var win = ""; 
   		win += "<table class=\"tablelist tablelist2\" >";
   		win += "<tr><td style='text-align:right;'>选择上传文件：</td><td style='text-align:left;'><input id='fileName' readonly='readonly' type='text'/><input onclick='chooseFile()' type='button' value= '浏览'><input onchange='showFileName()' id='xlsfile' type='file' style='display:none;' accept='.xls,.xlsx'></td></tr>"
   		win += "<tr><td colspan='2'><input style='width:80px;' type='button' onclick='batchAddBlackList()' value='上传'/></td></tr>";
   	    win += "</table>";
   	    f.openHtml('批量导入', win, 500, 100);
   	}

      function chooseFile(){
   	   jQuery("#xlsfile").click();
      }

      function showFileName(){
   	   jQuery("#fileName").val(jQuery("#xlsfile").val());
      }
      
      function downloadTemplate(){
    	  RiskmanageService.downloadTemplate(function(data){
      		dwr.engine.openInDownload(data);
    	  });
      }

      function batchAddBlackList(){
        var filePath = dwr.util.getValue('xlsfile');
        var fileName = jQuery("#xlsfile").val();
        var appendix = fileName.substring(fileName.lastIndexOf("."));
        if (fileName == '') {
           alert("请选择上传文件!");
           return ;
        }else if(chkSuffix(fileName)==false){
       	 alert("文件格式错误！仅支持xls、xlsx");
       	 return;
        }
        RiskmanageService.batchAddBlackList(filePath,callback);
      }
      function callback(msg){
   	   alert(msg);
   	   f.close();
   	   checkData();
      }
      
      function batchAddWhiteList(){
    	  var month = jQuery("#month").val();
    	  if(!month){
    		  alert("请选择导入时间！");
    		  return;
    	  }
    	  RiskmanageService.batchAddWhiteList(month,callback1);
      }
      
      function callback1(msg){
    	  alert(msg);
    	  checkData();
      }
      
   var flush=true;//配置后判断是否有刷新
   window.onbeforeunload = function(e){
		  if(!flush) return '你添加或删除了名单，还没有刷新！';
    }
    //新增上传文件格式验证
    function chkSuffix(file){
        var reg=/(.xls|.xlsx)$/
        return reg.test(file);
    }
   