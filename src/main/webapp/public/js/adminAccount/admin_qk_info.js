//公共代码
        function init(){
        	initGateChannel1();
            
        }  
        var mid,phoneNo,cardNo,bdate,edate, abbrev,type;
        function judgeCondition(){
             mid = $("mid").value;
             phoneNo = $("phone_no").value;
             cardNo = $("card_no").value;
             bdate = $("beginDate").value;
             edate = $("endDate").value;
             abbrev = $("abbrev").value;
             type = $("type").value;
             if(bdate=="")
            	 {
            	 	alert("请选择开始日期!");
            	 	return false;
            	 }
             if(edate=="")
        	 {
            	 alert("请选择结束日期!");
            	 return false;
        	 }
             if (mid !=    "") {
            	 mid = mid.trim();
                 if (!isNumber(mid)) {
                     alert("商户号必须是数字!");
                     $("mid").value = '';
                     $("mid").focus();
                     return false;
                  }
             }
             if (phoneNo !=    "") {
            	 phoneNo = phoneNo.trim();
                 if (!isNumber(phoneNo)) {
                     alert("电话号码必须是数字!");
                     $("phone_no").value = '';
                     $("phone_no").focus();
                     return false;
                  }
             }
             if (cardNo !=    "") {
            	 cardNo = cardNo.trim();
                 if (!isNumber(cardNo)) {
                     alert("卡号必须是数字!");
                     $("card_no").value = '';
                     $("card_no").focus();
                     return false;
                  }
             }
            return true;
        }
        //快捷用户信息查询
        function queryQkCardInfos(pageNo){
        	if(!judgeCondition())return;
        	QkRiskService.queryQkCardInfos( pageNo,  mid,  phoneNo,  cardNo, bdate, edate,abbrev,type,callBack2);
        	
        }
      //快捷用户信息查询下载
        function downloadQk(){
        	if(!judgeCondition())return;
            dwr.engine.setAsync(false);//把ajax调用设置为同步
            QkRiskService.downloadQk(mid,phoneNo,cardNo, bdate,edate,abbrev,type,
            			function(data){dwr.engine.openInDownload(data);});
        }
       //查询的回调函数
       var callBack2 = function(pageObj){ 
    	   $("merTodayTable").style.display="";
    	   dwr.util.removeAllRows("resultList");
    	   if(pageObj==null){
        	   document.getElementById("resultList").appendChild(creatNoRecordTr(10));
    		   return;
    	   }  
	        var cellFuncs = [
	                         //function(obj) { return "<a href=\"#\" onclick=query4Detail('"+obj.authCode+"'); class='box_detail'>"+obj.mid+"</a>"; },
	                         function(obj) { return obj.mid; },
	                         function(obj) {return createTip(8,obj.userId) ; },
	                         function(obj) {return obj.cardName; },
	                         function(obj) {return obj.pidNo; },
	                         function(obj) {return h_gate1[obj.gateId]; },
	                         function(obj) {return obj.cardNo; },
	                         function(obj) {return obj.phoneNo ; },
	                         function(obj) { return obj.bindTimeStr; },
	                         function(obj) {return obj.abbrev; },
	                         function(obj) {return obj.dataSource=="0"?"融易付":"账户" ; }
	                        
	                         //function(obj) { return obj.error_msg==''? createTip(8,obj.errorCode):createTip(8,obj.error_msg);}
	                     ];

//	               str ="<span class='pageSum'>&nbsp;交易总金额：<font color='red'><b>" + div100(pageObj.sumResult["amtSum"])
//	              		     +"</b></font>  元</span> <span class='pageSum'>&nbsp;&nbsp;系统手续费总金额：<font color='red'><b>"+ div100(pageObj.sumResult["sysAmtFeeSum"])+"</b></font>  元</span>";	             
	              paginationTable(pageObj,"resultList",cellFuncs,"","queryQkCardInfos");
          };
     // 查询详细信息
        /*function query4Detail(authCode) {
        	QkRiskService.queryQKByCode(authCode,function(QkCardInfo){ 
               $("v_mid").innerHTML = QkCardInfo.mid;
               $("v_abbrev").innerHTML = QkCardInfo.abbrev;
               $("v_userId").innerHTML = QkCardInfo.userId;
               $("v_cardName").innerHTML = QkCardInfo.cardName;
              // alert(QkCardInfo.pidNo);
               $("v_pidNo").innerHTML = QkCardInfo.pidNo;
               $("v_gateId").innerHTML = h_gate1[QkCardInfo.gateId];
               $("v_phoneNo").innerHTML = QkCardInfo.phoneNo;
               //alert(QkCardInfo.regiTimeStr);
               $("v_bindTime").innerHTML = QkCardInfo.regiTimeStr;
               $("v_cardNo").innerHTML = QkCardInfo.cardNo;
              jQuery("#hlogDetail").wBox({title:"快捷支付用户详细资料",show:true});//显示box
            });
           
        }*/
  
