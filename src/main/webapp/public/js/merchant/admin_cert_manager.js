function seachCertInfos(page) {
	var cert_name = document.getElementById("cert_name").value.trim();
	var cert_valid_period = document.getElementById("cert_valid_period").value;
	var mid = document.getElementById("mid").value.trim();
	var pageIndex = page == '' ? 1 : page;
	
	var obj={};
	obj["certName"] = cert_name;
	obj["mid"] = mid;
	obj["certValidPeriod"] = cert_valid_period;
	CertManagerService.queryCertInfo(obj, pageIndex, callback);
}
var callback = function(certInfoList) {
	document.getElementById("certInfoTable").style.display = "";
	if (certInfoList.length == 0) {
		document.getElementById("certInfoBody").appendChild(creatNoRecordTr(6));
		return false;
	}
	var cellFuncs = [ function(certInfo) {
						return certInfo.mid;
					}, function(certInfo) {
						return certInfo.subject;
					}, function(certInfo) {
						return certInfo.algorithm;
					}, function(certInfo) {
						return certInfo.notBeforeStr;
					}, function(certInfo) {
						return certInfo.notAfterStr;
					}, function(certInfo) {
						return certInfo.importTimeStr;
					}];
	paginationTable(certInfoList, "certInfoBody", cellFuncs, "", "seachCertInfos");
};

var f = new GreyFrame('INLINEIFRAME', 700, 400);
f.setClassName("MyGreyFrame");
function showFrame() {
	var win = "";
	win += "<table class=\"tablelist tablelist2\" >";
	win += "<tr><td class=\"th1\" style='text-align:right;'>商户号：</td><td style='text-align:left;'><input type='text' id='uploadMid' name='uploadMid' /></td></tr>";
	win += "<tr><td class=\"th1\" style='text-align:right;'>选择上传文件：</td><td style='text-align:left;'><input id='fileName' readonly='readonly' type='text'/><input onclick='chooseFile()' type='button' value= '浏览' style='width:80px;'><input onchange='showFileName()' id='uploadFile' type='file' style='display:none;' accept='.pfx,.der,.cer,.crt,.key,.pem'></td></tr>";
	win += "<tr><td colspan='2'><br/><input style='width:80px;' type='button' onclick='uploadCert()' value='上传'/></td></tr>";
	win += "</table>";
	f.openHtml('新增证书', win, 500, 130);
}

function chooseFile() {
	jQuery("#uploadFile").click();
}

function showFileName() {
	jQuery("#fileName").val(jQuery("#uploadFile").val());
}

function uploadCert() {
	var mid = dwr.util.getValue('uploadMid');
	var filePath = dwr.util.getValue('uploadFile');
	var fileName = jQuery("#uploadFile").val();
	if (fileName == '') {
		alert("请选择上传文件!");
		return;
	}
	
	CertManagerService.uploadCert(filePath, mid, uploadCallback);
}
function uploadCallback(msg) {
	alert(msg);
	f.close();
	seachCertInfos(1);
}