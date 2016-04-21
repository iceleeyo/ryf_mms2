function initOption(){
		    dwr.util.addOptions("tstat", h_tstat);
		    dwr.util.addOptions("mstate", m_mstate);
            initMinfos();
	}
	var mid,bdate,edate,tstat,tseq,cardType,cardVal,payAmount,mstate,begintrantAmt,endtrantAmt;
	function judgeInput(){//查询条件的判断
	     mid=$("mid").value;
	     bdate=$("bdate").value;
	     edate= $("edate").value;
	     tstat= $("tstat").value;
	     tseq= $("tseq").value;
	     cardType=$("cardType").value; 
	     cardVal= $("cardVal").value;
	     payAmount= $("payAmount").value;
	     mstate=$("mstate").value;
	     begintrantAmt=$("begintrantAmt").value;
         endtrantAmt=$("endtrantAmt").value;
         if (begintrantAmt !=    "") {
         	begintrantAmt = begintrantAmt.trim();
             if (!isPlusInteger(begintrantAmt)) {
                 alert("金额必须是正整数!");
                 $("begintrantAmt").value = '';
                 $("begintrantAmt").focus();
                 return false;
              }
         }
         if (endtrantAmt !=    "") {
         	endtrantAmt = endtrantAmt.trim();
             if (!isPlusInteger(endtrantAmt)) {
                 alert("金额必须是正整数!");
                 $("endtrantAmt").value = '';
                 $("endtrantAmt").focus();
                 return false;
              }
         }
	    if(bdate==""||edate==""){
	       alert("请输入起始日期！");
	       return false;
	    }
	    if(tseq!=""&&!isNumber(tseq)){
	        alert("电银流水号必须是数字!");
	       $("tseq").value="";
	       return false;
	    }
	    if(payAmount!=""&&!isFigure(payAmount)){
	        alert("金额必须是正整数!");
	       $("payAmount").value="";
	       return false;
	    }
	    return true;
	}
	//下载
	function downloadCardPay(){
	  	  if(!judgeInput())return;
	      dwr.engine.setAsync(false);//把ajax调用设置为同步
	      QueryCreditCardResultService.downloadCreditCardPay(mid,bdate,edate,tstat,tseq,cardType,cardVal,payAmount*100,mstate,begintrantAmt,endtrantAmt,
		  				function(data){dwr.engine.openInDownload(data);});
	 }
	  //查询
	function showCreditCardResult(pageNo){
		if(!judgeInput())return;
		QueryCreditCardResultService.queryCreditCardResult(pageNo,mid,bdate,edate,tstat,tseq,cardType,cardVal,payAmount*100,mstate,begintrantAmt,endtrantAmt,callback);
	   
	    function callback(mlogList){
	        dwr.util.removeAllRows("resultList");
	        $("creditCardTable").style.display="";
	       if(mlogList==null||mlogList.length==0){
    	      $("resultList").appendChild(creatNoRecordTr(9));
 	           return false;
   		   }
   		     var cellFuncs = [
                  function(Mlog) { return Mlog.tseq; },
                  function(Mlog) { return formatDate(Mlog.sysDate); },
                  function(Mlog) { return Mlog.mid; },
                  function(Mlog) { return m_minfos[Mlog.mid]; },
                  function(Mlog) { return div100(Mlog.payAmount); },
                  function(Mlog) { return h_tstat[Mlog.tstat] },
                  function(Mlog) { return shadowAcc(Mlog.payCard); },
                  function(Mlog) { return Mlog.mobileNo=="null"?"无":shadowPhone(Mlog.mobileNo);},
                  function(Mlog){ return  shadowAcc(Mlog.payId)}       
              ];               
           var totalAmoutStr="<span class='pageSum'>&nbsp;&nbsp;交易金额总计：<span style='color:red'>"+div100(mlogList.sumResult.amtSum)+"</span>&nbsp;元</span>";
           paginationTable(mlogList,"resultList",cellFuncs,totalAmoutStr,"showCreditCardResult");
	    }
	} 