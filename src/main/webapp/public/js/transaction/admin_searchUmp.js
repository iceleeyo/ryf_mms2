

function qiehuan(num){
          for(var id = 0;id<=2;id++){
              if(id==num){
                        document.getElementById("qh_con"+id).style.display="block";
                        document.getElementById("mynav"+id).className="nav_on";
               }
               else{
                        document.getElementById("qh_con"+id).style.display="none";
                        document.getElementById("mynav"+id).className="";
                }
            }
            document.getElementById("UMP").style.display = "none";
            document.getElementById("CMB").style.display = "none";
}
function beforeDown(){
    if (document.getElementById("downloadDate").value == ""){
        alert("您还没有选择日期!");
        return false;
        }
}
function beforeSearch(tseq,sysDate){
    if (tseq == ""){
        alert("您还没有输入电银流水号!");
        return false;
    }     
    if (!isNumber(tseq)){
        alert("电银流水号必须是数字!");
        return false;
    }
    if (sysDate == ""){
        alert("您还没有选择日期!");
        return false;
    }
    return true;
}
 
function results(tradedetail){
    
    // 取得html中表格对象
    var btable = document.getElementById("bodyTable");
    var htable = document.getElementById("headTable");
    var ump = document.getElementById("UMP");
    var cmb = document.getElementById("CMB");
    ump.style.display='';
    cmb.style.display='none';
    if (tradedetail.length == 1){
        btable.style.display='none';
        htable.style.display='';
        document.getElementById("code").innerHTML = "<font color='red'>" + tradedetail[0] + "</font>";
        document.getElementById("codeDiscribe").innerHTML = "<a href='./umpRetCode_info.html' target='codeFrame'><font color='blue'>错误返回码说明</font></a>";
    } else {
        btable.style.display='';
        htable.style.display='none';
        document.getElementById("tseq").innerHTML = tradedetail[0];//融易通流水号
        document.getElementById("sys_Date").innerHTML = tradedetail[1];//系统日期
        document.getElementById("payDate").innerHTML = tradedetail[2];//支付日期
        document.getElementById("amount").innerHTML = tradedetail[3];//交易金额
        document.getElementById("bank").innerHTML = tradedetail[4];//银行
        document.getElementById("tradeType").innerHTML = tradedetail[5];//交易类型
        document.getElementById("tradeState").innerHTML = tradedetail[6];//>交易状态
        document.getElementById("liqDate").innerHTML = tradedetail[7];//清算日期
        document.getElementById("bankCheck").innerHTML = tradedetail[8];//银行对账状态
        document.getElementById("merPri").innerHTML = tradedetail[9];//商户私有域
        }
}
function CMBSearch(){
 var tseq = document.getElementById("rytId").value;
 var sysDate = document.getElementById("sysDate").value;
 var operId = document.getElementById("operId").value;
 var pwd = document.getElementById("pwd").value;
 if(operId == ''){
    alert("操作员号不能为空!");
    return false;
 }
 if(pwd == ''){
    alert("操作员密码不能为空!");
    return false;
    }
 if (beforeSearch(tseq,sysDate)){
	 TransactionService.search(tseq,sysDate,operId,pwd,function(msg){
        var btable = document.getElementById("CMBbodyTable");
        var htable = document.getElementById("CMBheadTable");
        var ump = document.getElementById("UMP");
        var cmb = document.getElementById("CMB");
        ump.style.display='none';
        cmb.style.display='';
        if (msg.length == 1){
            btable.style.display='none';
            htable.style.display='';
            document.getElementById("CMBcode").innerHTML = "<font color='red'>" + msg[0] + "</font>";
        } else {
            btable.style.display='';
            htable.style.display='none';
            document.getElementById("CMBtseq").innerHTML = msg[0];//融易通流水号
            document.getElementById("CMBsysDate").innerHTML = msg[1];//系统日期
            document.getElementById("CMBpayDate").innerHTML = msg[2];//支付日期
            document.getElementById("CMBamount").innerHTML = msg[4];//交易金额
            document.getElementById("CMBbank").innerHTML = "招商银行";//银行
            document.getElementById("CMBtradeType").innerHTML = msg[3];//交易类型
            }
    });
 }

}