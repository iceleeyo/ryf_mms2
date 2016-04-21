
      function init(){
          $("bdate").value=jsToday();$("edate").value=jsToday();
          dwr.util.addOptions("tstat", h_tstat);
          var mid = $("mid").value;
          PageService.getMerOpersMap(mid,function(map){
              dwr.util.addOptions("operid", map);
          });
          
      }
      function initDate(){
    	  if($("table").value=='tlog'){
    		  $("bdate").value=jsToday();
    		  $("edate").value=jsToday();
    		  $("bdate").disabled = true;
    		  $("edate").disabled = true;  
           }else{
        	   $("bdate").value = '';
               $("edate").value= '';
               $("bdate").disabled = false;
               $("edate").disabled = true;  
           }
      }

        function confirmPhonePay(){
            //var ordId = $("ordId").value.trim();
            var transAmt = $("transAmt").value.trim();
            var phoneNo = $("phoneNumber").value.trim();
            var payTimePeriod = $("payTimePeriod").value.trim();
            if(transAmt == ''){
                alert("请输入交易金额！");
                return false;
            }
            if(!isNumber(transAmt)){
                alert("交易金额格式错误!");
                return false;
            }
            if(phoneNo == ''){
                alert("请输入手机号！");
                return false;
            }
            if(!isMobel(phoneNo)){
            	alert("手机号格式错误！");
                return false;
            }
            var uid = $("uid").value;
            var mid = $("mid").value;
            TransactionService.doPhonePay( mid, transAmt, phoneNo, payTimePeriod, uid,function(msg){
        	   if(msg == 'ok'){
                   alert("发送订单支付短信成功!");
                  // $("ordId").value='';
                   $("transAmt").value='';
                   $("phoneNumber").value='';
                   $("payTimePeriod").value=30;
                   return false;
               }else{
                   alert(msg);
               }
           }); 
        }
        var mid,table,tstat,bdate,edate,operid;
        function judgeCondition(){
             mid = $("mid").value;
            if(mid=='') return ;
             table = $("table").value;
             tstat = $("tstat").value;
             bdate = $("bdate").value;
             edate = $("edate").value;
             operid = $("operid").value;
            return true;
        }
        //手机支付查询
		 function queryPhonePay(pageNo){
				if(!judgeCondition())return;
	            TransactionService.queryPhonePay( pageNo, table, mid, tstat, bdate, edate, operid,callBack2);
	     }
		 //手机支付下载
	    function downloadMobilePay(){
	        	if(!judgeCondition())return;
	            dwr.engine.setAsync(false);//把ajax调用设置为同步
	            TransactionService.downloadPhonePay(table, mid, tstat, bdate, edate, operid,
	            		   function(data){dwr.engine.openInDownload(data);});
	    }
         var callBack2 = function(pageObj){	 
        	 $("phonePayTable").style.display="";
        	 dwr.util.removeAllRows("resultList");
        	  if(pageObj==null){
           	   document.getElementById("resultList").appendChild(creatNoRecordTr(11));
       		   return;
       		   }
	        var cellFuncs = [
	                         function(obj) { return obj.tseq; },
	                         function(obj) { return obj.mid; },
	                         function(obj) { return obj.oid; },
	                         function(obj) { return div100(obj.amount); },
	                         function(obj) { return h_tstat[obj.tstat]; },
	                         function(obj) { return obj.sysDate;},
	                         function(obj) { return getStringTime(obj.sysTime);},
	                         function(obj) { return obj.mobileNo;},
	                         function(obj) { return obj.operId; },
	                         function(obj) { return obj.transPeriod; }
	                     ];
	        	 
	              str = "<span style='float:left;color:blue'>&nbsp;&nbsp;交易总金额：<font color='red'><b>" + div100(pageObj.sumResult["amtSum"])+"</b></font>  元</span>";
	              paginationTable(pageObj,"resultList",cellFuncs,str,"queryPhonePay");
	          }
         
/*
	        function sendMerPayUrl(tseq,mid,oid,transPeriod,mobileNo){
	        	TransactionService.reSendPayUrl(tseq,mid,oid,transPeriod,mobileNo,function(msg){
	                if(msg.indexOf('ok')!=-1){
	                    alert("发送成功!");
	                   // document.getElementById('t_'+tseq).innerHTML = getStringTime(parseInt(msg.split(',')[1]))
	                }else{
	                    alert(msg)
	                }
	            }); 
	        }
  */