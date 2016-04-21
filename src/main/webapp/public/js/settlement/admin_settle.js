
 /**
  * 上传文件对账ccb和icbc
  * @param fileId
  * @return
  */      
 function doSettle(fileId){
            if (checkFormat(fileId, 'txt')) {
                $("submitid").disabled = true;
                $("submitid_fh").disabled = true;
                $("submitid").value = "对账中,请稍后。。。";
                $("form1").submit();               
            }
            return false;
}      
       
/**
 * 交行对账
 * @param path
 * @return
 */
function checkBank(path) {
	var settleDate = document.getElementById("settleDate").value;
	if (settleDate == "") {
		alert("请选择日期!");
		return false;
	}
	getOverlay("settle", "对账中,请稍后。。。");
	return true;
}

function checkCCB(){
	if (checkFormat("CCBfileid", "txt")) {
		getOverlay("settle", "对账中,请稍后。。。");
		return true;
	}
	return false;
	
}

/**
 * IPS对账
 * @return
 */
function checkIPS() {
	if (checkFormat("IPSfileid", "csv")) {
		getOverlay("settle", "对账中,请稍后。。。");
		return true;
	}
	return false;
}

/**
 * PNR对账
 * @return
 */
function checkPnr() {
	if (checkFormat("PnrTXTfileid", "txt")) {
		getOverlay("settle", "对账中,请稍后。。。");
		return true;
	}
	return false;
}

/**
 * UMP对账
 * UMP有两种对账方式 上传文件对账 和 按日期对账
 * @return
 */
function checkUmp(id) {
	var flag = false;
	//if(id == 'dateBtn'){
		//var beginDate = document.getElementById("Umpbegin").value;
		//var endDate = document.getElementById("Umpend").value;
		//if (checkDate(beginDate, endDate)) {//判断日期的有效性,日期控件已经做了约束此步可以省略
			//flag = true;
			//因为form设置属性enctype="multipart/form-data" 无法将表单中的数据放到request中只能手动拼接
			//document.forms["form4"].action = document.forms["form4"].action + "&Umpbegin=" + beginDate +"&Umpend=" + endDate + "&way=date";
		//}
	//}
	if(id == 'fileBtn'){
		if(checkFormat("UmpTXTfileid", "txt")){
			flag = true;
			document.forms["form4"].action = document.forms["form4"].action + "&way=file";
		}
	}
	if(flag){
		getOverlay("settle", "对账中,请稍后。。。");
	}
	return flag;
}
/**
 * CMB对账
 * @return
 */
function checkCMB(){
	var beginDate = document.getElementById("CMBbegin").value;
	var endDate = document.getElementById("CMBend").value;
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
	if (checkDate(beginDate, endDate)) {
		getOverlay("settle", "对账中,请稍后。。。");
		return true;
	}
	return false;
}
/**
 * 对日期进行校验
 * @param beginDate  开始日期
 * @param endDate    结束日期
 * @return
 */
function checkDate(beginDate,endDate){
	if (beginDate == "") {
		alert("请选择起始时间!");
		return false;
	}
	if (endDate == "") {
		alert("请选择结束时间!");
		return false;
	}
	if (!judgeDate(beginDate, endDate)) {
		alert("开始日期应先于结束日期");
		return false;
	}
	return true;
}
/**
 * 校验上传文件格式
 * @param path 上传文件的全路径
 * @param suffix  应该匹配的后缀
 * @return true or false
 */
function checkFormat(path, suffix) {
	var filePath = document.getElementById(path).value;
	if (filePath == '') {
		alert("请选择文件!");
		return false;
	}
	if ((filePath.toLocaleLowerCase().indexOf("." + suffix) == -1)) {
		alert("请选择" + suffix + "文件!");
		return false;
	}
	return true;
}
/**
 * 切换div
 * @param num  div的编号
 * @return
 */
function switchDiv(num){
	document.getElementById("result").style.display="none";
	selectDiv(num);
}
function selectDiv(num) {
	for ( var id = 0; id < 5; id++) {
		if (id == num) {
			document.getElementById("qh_con" + id).style.display = "block";
			document.getElementById("mynav" + id).className = "nav_on";
		} else {
			document.getElementById("qh_con" + id).style.display = "none";
			document.getElementById("mynav" + id).className = "";
		}
	}
}
/**
 * 联动优势两种对账方式间切换
 * @param id Umpbegin or UmpTXTfileid
 * @return
 */
function umpSwitch(id){
	var dateFrm = document.getElementById("Umpbegin");
	var fileFrm = document.getElementById("UmpTXTfileid");
	var dateBtn = document.getElementById("dateBtn");
	var fileBtn = document.getElementById("fileBtn");
	if (id == 'Umpbegin'){//按日期对账则清空上传路径,并把fileBtn置为disable
		fileFrm.value = "";
		fileBtn.disabled = true;
		dateBtn.disabled = false;
	}
	if (id == 'UmpTXTfileid'){//按文件对账则清空日期,并把dateBtn置为disable
		dateFrm.value = "";
		document.getElementById("Umpend").value = "";
		dateBtn.disabled = true;
		fileBtn.disabled = false;
	}
	
}
