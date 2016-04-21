	function queryMsg(){
		 var beginDate = document.getElementById("v_sys_date_begin").value;
	     var endDate = document.getElementById("v_sys_date_end").value;
	     var notice = new Object();
	     notice.beginDate = beginDate;
		    notice.endDate =endDate;
		    document.getElementById("detailNotice").style.display="";
	     SysManageService.getMessage(notice,callBack);
   }	 
   var callBack = function(d){
	     back();
	     dwr.util.removeAllRows("biaotou");
     // 取得html中表格对象  
     var dtable = document.getElementById("biaotou");
     if(d.length>=1){
	        for ( var i = 0; i <d.length; i++) {
	            notice = d[i];
	              // 在table中新建一行
	            var elTr = dtable.insertRow(-1);
	            elTr.setAttribute("align","center");
	              // 通知开始日
	            var tseqTd = elTr.insertCell(-1);
	            tseqTd.innerHTML = notice.beginDate == null ? "" : notice.beginDate ;
	              //通知结束日
	            var tseqTd = elTr.insertCell(-1);
	            tseqTd.innerHTML =  notice.endDate == null ? "" : notice.endDate;
	              //通知标题           
	            var nameTd = elTr.insertCell(-1);
	            nameTd.innerHTML = notice.title;
	              // 修改编辑
	            var mdateTd = elTr.insertCell(-1); 
	            mdateTd.innerHTML =  "<input type=\"button\" value=\"修改\"  onclick=\"editORdetailNotice(" +notice.id + ",'edit');\" >";
	              // 详细信息
	            var oidTd = elTr.insertCell(-1);
	            oidTd.innerHTML =  "<input type=\"button\" value=\"详细\"  onclick=\"editORdetailNotice(" +notice.id + ",'detail');\" >";
	              //删除信息
	            var deleteTd = elTr.insertCell(-1);
	            deleteTd.innerHTML =  "<input type=\"button\" value=\"删除\"  onclick=\"deleteNotice(" +notice.id + ");\" >";
	        }
	      } else {
	         dwr.util.removeAllRows("biaotou");
          alert("没有符合条件的记录！");
	     }  
	  }    
	  
	  //删除
	  function deleteNotice(id){
	   if (confirm("是否确定进行删除通知信息操作?")) {
	 	    SysManageService.deleteNotice(id,function(msg){
	 	       if(msg == 'ok'){
	 	         alert("删除成功");
	 	         queryMsg();
	 	       }else{
	 	         alert("删除失败");
	 	       }
	 	    });
	    }
	  }
	  
	  //修改 详细
	  function editORdetailNotice(id,type){
	    document.getElementById("editORdetailNotice").style.display = "";
	    document.getElementById("detailNotice").style.display = "none" ;
	    if(type == 'detail'){
	      document.getElementById("edit").style.display  = "none";
	    }else{
	     document.getElementById("edit").style.display  = "";
	    }
	    SysManageService.getMessageById(id,callBack4one); 
	  }
	  
	  var callBack4one = function(d){
	     notice = d;
	     dwr.util.setValues({noticeid : notice.id,msgtitle : notice.title , msgMeat : notice.content , sys_date_begin : notice.beginDate , sys_date_end :notice.endDate});
	     var  a = document.getElementById("msgMeat");
	     isMaxLen(a,'intro_info');
	     };
	     
	  function back(){
	    document.getElementById("editORdetailNotice").style.display = "none";
	    document.getElementById("detailNotice").style.display = "" ;
	    document.getElementById("edit").disabled = false ;
	  } 
	  
	  //修改
	 function addMsg(){
		var id =  document.getElementById("noticeid").value;
	    var msgtitle = document.getElementById("msgtitle").value;
	    var msgMeat = document.getElementById("msgMeat").value;
	    var beginDate = document.getElementById("sys_date_begin").value.trim();
	    var endDate = document.getElementById("sys_date_end").value.trim();
	    var notice = new Object();
	    if(msgtitle.trim() == ''){alert("通知消息标题不能为空");return false;} 
	    if(msgMeat.trim() == ''){alert("通知消息内容不能为空");return false;} 
	    if(!checkIllegalChar(msgtitle)) return false;
	    if(!checkIllegalChar(msgMeat))return false;	
	    if(beginDate == ''){alert("请输入有效起始时间");return false;}
	    if(endDate == ''){alert("请输入有效终止时间");return false;}
	    notice.id = id;
	    notice.beginDate = beginDate;
	    notice.endDate =endDate;
	    notice.title = msgtitle;
	    notice.content = msgMeat;
	    notice.operId = operId;
	    SysManageService.addOrEditMessage(notice,'edit',callBack4update);
	 } 
	 
	 var callBack4update = function(msg){
	   if(msg == "ok"){
	      alert("修改成功");
	      queryMsg();
	   }else{
	     alert("修改失败");
	   }
	 };
	 
	 //控制字数
	function isMaxLen(o,info){  
		var Restlen = 0;
		var curlen= o.value.length;
		var nMaxLen=o.getAttribute? parseInt(o.getAttribute("maxlength")):"";
		if(o.getAttribute && o.value.length>nMaxLen){
			o.value=o.value.substring(0,nMaxLen)
		}else{
			Restlen=nMaxLen - curlen;
			var rest=document.getElementById(info);
			document.getElementById(info).value=Restlen;
		}
		return Restlen;
	 }        