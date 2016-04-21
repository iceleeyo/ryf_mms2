
//缓存当前查询出来的要退款的对象，免得后台再次查询----71行
window.onload=function(){
	CommonService.getMerGatesMap($("mid").value,function(map){
		dwr.util.addOptions("gate", map);
		h_gate=map;
	});
	initMinfos();
}
    var thisRefundOB = new Object(); 
    function query4OneHlog2(chooseid){
        if (chooseid == 2) {
            var oid = document.getElementById("oid").value;
            var c_mdate = document.getElementById("c_mdate").value;
            var mid = document.getElementById("mid").value;
            if (mid == '') { 
                alert("请输入商户号");
                return false;
            }
            if (oid == '') { 
                alert("请输入商户订单号");
                return false;
            }
            if(!isNumEng(oid.trim())){
                alert("请输入正确的订单号格式!");
                document.all.oid.value = '';
                document.all.oid.focus();
                return false;
            }
            
            RefundmentService.getRefundOBByMer(oid,mid,c_mdate,mid,call4RefundApply2)  
            
        } else  {
            var tseq = document.getElementById("tseq").value;
            if(tseq !='') {tseq = tseq.trim();}
            if (tseq == '') {
                alert("请输入电银流水号");
                return false;
            }
            if (!isFigure(tseq)) {
                alert("电银流水号必须是数字!");
                return false;
            }
            RefundmentService.getRefundOBByTseq(tseq,call4RefundApply2);  
        } 
    }

    var call4RefundApply2 = function (RefundOB){
        document.getElementById("detail").style.display = RefundOB == null ? "none" : "";
        document.getElementById("detailResultList").style.display = RefundOB == null ? "none" : "";
        if(RefundOB == null ){
           document.getElementById("refundTable").style.display = "none"; 
           alert("没有符合条件的记录，请确认输入项是否有误!");
           return;
        }
        thisRefundOB = RefundOB;
        //RefundOB.canRefund为true时标示能够退款，为false标示不能够退款
        //不能够退款时候隐藏refundTable
        document.getElementById("refundTable").style.display = RefundOB.canRefund ? "" : "none";
        document.getElementById("toCheck").disabled = !RefundOB.canRefund;
        document.getElementById("sureRefund").disabled = !RefundOB.canRefund;
        if(RefundOB.canRefund){
            document.getElementById("toCheck").checked = true;
        }
        //清空input中 的 值
        document.getElementById("refundAmount").value ="";
        document.getElementById("refundReason2").value ="";
        document.getElementById("refundState").innerHTML = "&nbsp;"+RefundOB.notRefuntReason;
        //赋 值
        dwr.util.setValues({v_gate:h_gate[RefundOB.gate],v_name:m_minfos[RefundOB.mid],v_tseq:RefundOB.tseq,v_oid:RefundOB.oid,v_mdate:RefundOB.mdate,v_fee_amt:div100(RefundOB.feeAmt)});  
        dwr.util.setValues({v_refund_amt:div100(RefundOB.refundAmt),v_keshenqing:div100( RefundOB.amount-RefundOB.refundAmt),v_amount:div100(RefundOB.amount)});
        
        document.getElementById("intro_info").value = 100;
    }

    function confirmRefund2() {

        var refSmount = document.getElementById("refundAmount").value;
        var canValue = document.getElementById("can").firstChild.data; 
        var v_keshenqing = document.getElementById("v_keshenqing").value;
        if (!document.getElementById("toCheck").checked) {
            alert("请选中是否可退款!");
            return;
        }else if(v_keshenqing*100 < refSmount*100){
            alert("可退款金额不足，请重新输入退款金额！");
            return;
        }else if(refSmount == ''){
            alert("请输入退款金额!");
            return;
        }else if(refSmount == '0' || refSmount*100 == '0'){
            alert("退款金额不能为0，请重新输入！");
            return;
        }else if(!isNumber(refSmount)){
           alert("请输入正确的金额格式!");        
           return;
        }else if(parseFloat(refSmount)>parseFloat(canValue)){
           alert("本条交易可退余额不足");
           return;
        }else if(thisRefundOB.midRefundFlag==0){
        	alert("此商户不允许退款！");
        	return;
        }
        if (window.confirm("确认提交退款吗?")) {
          var refundReason = document.getElementById("refundReason2").value;//.firstChild == null? "":document.getElementById("refundReason2").firstChild.data; 
          thisRefundOB.applyRrefundAmount = refSmount;
          thisRefundOB.refundReason = refundReason;
          RefundmentService.applyRefund(thisRefundOB,function a(msg){
              if (msg != undefined) {
                  alert(msg);
                  if(msg.indexOf('退款申请成功') != -1){
                      document.getElementById("detailResultList").style.display = "none";
                      document.getElementById("refundTable").style.display = "none";
                      document.getElementById("oid").value = "";
                      document.getElementById("tseq").value = ""; 
                      document.getElementById("refundAmount").value = "";
                     // document.getElementById("refundReason").value ="";
                      document.getElementById("refundReason2").value ="";
                      document.getElementById("can").firstChild.data = ""; 
                 }
              }

          }) 
        }
    }
  //两种申请退款方式
    function query4RefundApply(){
       //初始化开始默认为融易通流水号查询
        bank();//银行
        mer();//商户
    }    
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
    function qiehuan(num){
        document.getElementById("detail").style.display="none";
        if(num != 2){
          document.getElementById("batchResult").style.display="none";
        }
        for(var id = 0;id<2;id++){
            if(id==num)
            {
                document.getElementById("qh_con"+id).style.display="block";
                document.getElementById("mynav"+id).className="nav_on";
            }
            else
            {
                document.getElementById("qh_con"+id).style.display="none";
                document.getElementById("mynav"+id).className="";
            }
        }
                 
    }
    function checkPath(){
        var path = document.getElementById("RefTXTfileid").value;
        if (path == '') {
            alert("请选择文件!");
            return false;
        }
        if ((path.toLocaleLowerCase().indexOf("." + "txt") == -1)) {
            alert("请选择TXT文件!");
        return false;
        }
        return true;
    } 
               