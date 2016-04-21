 function queryMsg(){
	        var beginDate = document.getElementById("v_sys_date_begin").value;
	        var endDate = document.getElementById("v_sys_date_end").value;
	        var notice = new Object();
	        notice.beginDate = beginDate;
		    notice.endDate =endDate;
	        MerMerchantService.getMessage(notice,callBack);
	      }
 	 
 	  var callBack = function(d){
 	      back();
 	     $("noticeTable").style.display="";
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
		            tseqTd.innerHTML = notice.beginDate;
		              //通知结束日
		            var tseqTd = elTr.insertCell(-1);
		            tseqTd.innerHTML =  notice.endDate;
		              //通知标题           
		            var nameTd = elTr.insertCell(-1);
		            nameTd.innerHTML = notice.title;
		              // 详细信息
		            var oidTd = elTr.insertCell(-1);
		            oidTd.innerHTML =  "<input type=\"button\" value=\"详细\" style=\"width: 60px;\" onclick=\"editORdetailNotice(" +notice.id + " );\" >";
		        }
		      } else {
		        	   var pageTr = dtable.insertRow(-1);
		        	    var pageTd = pageTr.insertCell(-1);
		        	    pageTd.setAttribute("colSpan",4);
		        	    pageTd.innerHTML="没有符合条件的记录！";
		        	    dtable.appendChild(pageTr);		    	   
		      }  
 	  }  
 	  
 	  function editORdetailNotice(id){
 	    document.getElementById("editORdetailNotice").style.display = "";
 	    document.getElementById("noticeTable").style.display = "none" ;
 	    MerMerchantService.getMessageById(id,callBack4one); 
 	  }
 	  
 	  var callBack4one = function(d){
 	     notice = d;
 	     dwr.util.setValues({noticeid : notice.id,msgtitle : notice.title , msgMeat : notice.content , sys_date_begin : notice.beginDate , sys_date_end :notice.endDate});
 	     };
 	     
 	  function back(){
 	    document.getElementById("editORdetailNotice").style.display = "none";
 	    document.getElementById("noticeTable").style.display = "" ;
 	  } 