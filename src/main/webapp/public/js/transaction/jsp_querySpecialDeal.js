 window.onload=function(){
	 PageService.getGatesMap(function(gateMap){
		 h_gate=gateMap;
	 });
 }
 
function changeSearch(id) {
	   if (id == 'meroid') {
	        $("meroid").style.display = "";
	        $("bytseq").style.display = "none";
	        $("showTable").style.display = "none";
	        $("oid").value = "";
	        $("c_mdate").value = "";
	    } else if (id == 'bytseq') {
	        $("bytseq").style.display = "";
	        $("meroid").style.display = "none";
	        $("showTable").style.display = "none";
	        $("tseq").value = "";
	    } 			    
	}
	function queryOne(id){
		var mid = $("mid").value;
		if(mid=='') return;
		if (id == 'meroid') {
			var oid = $("oid").value;
			if (oid=='') {
                alert("请输入商户订单号!");
                return false;
            }
			var mdate = $("c_mdate").value; 
			 TransactionService.queryHlogByMer( mid,  mdate,  oid,callBack4Special);
        } else if (id == 'bytseq') {

        	var tseq = $("tseq").value;
        	if (tseq=='') {
                alert("请输入电银流水号!");
                return false;
            }
        	tseq = tseq.trim();
            if (!isNumber(tseq)) {
                alert("电银流水号必须是数字!");
                return false;
            }
            TransactionService.queryMerHlogByTseq(tseq,mid,callBack4Special);
        } 			
		
	}
	var callBack4Special = function (Hlog){
		 if(Hlog == null){
			$("showTable").style.display = "none";
           alert( "没有符合条件的查询记录!" );
		    return;
		 }
        $("showTable").style.display = "";
        $("v_mdate").innerHTML = Hlog.mdate;
        $("v_mid").innerHTML = Hlog.mid;
        $("v_oid").innerHTML = Hlog.oid;
        $("v_amount").innerHTML = div100 (Hlog.amount);
        $("v_type").innerHTML = h_type[Hlog.type];
        $("v_gateName").innerHTML = Hlog.gate==0 ? '' : h_gate[Hlog.gate];
        $("v_sys_date").innerHTML = Hlog.sysDate;
        $("v_init_sys_time").innerHTML = Hlog.initSysDate;
        $("v_tseq").innerHTML = Hlog.tseq;
        $("v_batch").innerHTML = Hlog.batch==0?"":Hlog.batch;
        $("v_fee_amt").innerHTML = div100(Hlog.feeAmt);
        $("v_tstat").innerHTML = h_tstat[Hlog.tstat];
        $("v_bk_flag").innerHTML = h_bk_flag[Hlog.bkFlag];
        $("v_refund_amt").innerHTML = div100(Hlog.refundAmt);
        $("v_bk_send").innerHTML = getStringTime(Hlog.bkSend);
        $("v_bk_recv").innerHTML = getStringTime(Hlog.bkRecv);
        $("v_bk_chk").innerHTML = h_bk_chk[Hlog.bkChk];
        $("v_bk_date").innerHTML = (Hlog.bkDate==null || Hlog.bkDate==0)?"&nbsp;":Hlog.bkDate;
        $("v_bk_seq1").innerHTML = Hlog.bk_seq1;
        $("v_bk_seq2").innerHTML = Hlog.bk_seq2;
        $("v_bk_resp").innerHTML = Hlog.bkResp;
	}	
				
		 