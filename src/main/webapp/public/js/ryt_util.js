//trim空格
String.prototype.trim = function() {
	return this.replace(/^( |[\s　])+|( |[\s　])+$/g, "");
};
//用正则表达式将字符中所有空格都用空字符串替代。
String.prototype.trimAll = function(){
	return this.replace(/\ /g, "");
};
/**
 * 根据 数组中Object的id 取得object
 * idName：id的属性名 idVal:id属性值
 */

function getObjById(objArr,idName,idVal){
	for(var i=0;i<objArr.length;i++){
		var obj=objArr[i];
		if(obj[idName]==idVal){
			return obj;
		}
	}
	return {};
}	
//返回金额 ￥￥.￥￥形式
function div100(x){
 var f_x = parseFloat(x);
 if (isNaN(f_x)){
    return '';
 }
 var f_x = Math.round(x)/100;
 var s_x = f_x.toString();
 var pos_decimal = s_x.indexOf('.');
 if (pos_decimal < 0){
    pos_decimal = s_x.length;
    s_x += '.';
 }
 while (s_x.length <= pos_decimal + 2){
    s_x += '0';
 }
 return s_x;
}
/**
 * 创建没有记录的提示
 * @param spanNum
 * @return
 */
function creatNoRecordTr(spanNum){
	   if(spanNum=="")spanNum=15;
	   var  trElement = document.createElement("tr");
       var tdElement = document.createElement("td");
       tdElement.colSpan = spanNum;
       tdElement.innerHTML = "没有符合条件的查询记录!";
       trElement.appendChild(tdElement);
       return trElement;
}
/**********表单对象，判断、处理********************/
//判断值为null返回“”否则return value
function isEmpty(objVal){
	if(objVal==null||objVal==0){
		return "";
	}
	return objVal;
}  
//根据name获取选中的checkbox的值，return array;
function getValuesByName(checkName){
    var checkIds=document.getElementsByName(checkName);
    var valArr=new Array();
    var j=0;
    for(var i=0;i<checkIds.length;i++){
		if(checkIds[i].checked){
    	    valArr[j]=checkIds[i].value;
    	    ++j;
    	}
	}
	return valArr;
  }
//根据id自动封装表单对象
function wrapObj(formId,attrNameFun){
  var obj={};
   var elements=document.getElementById(formId).elements;
   for(var i=0;i<elements.length;i++){
     var nodeName=elements[i].nodeName;
     var nodeType=elements[i].getAttribute("type");
     if((nodeName=="INPUT"&&(nodeType==null||nodeType=="text"||nodeType=="hidden"||
     		nodeType=="radio"||nodeType=="checkbox"))||nodeName=="SELECT"||nodeName=="TEXTAREA"){
        var objName=elements[i].getAttribute("id");
         if(attrNameFun!=undefined)objName=attrNameFun(objName);//对objName的属性做处理的函数；
         objName=reversionChar(objName,1);//_的转换
         obj[objName]=elements[i].value;
     }
   }
   return obj;
}
 
//0为不转换，1(_[a-z])转成[A-Z] 2为 大写字母转成(_[a-z])
function reversionChar(str,flag){
  var reg1=/_[a-z]{1}/gi; 
  var reg2=/[A-Z]{1}/g;
  if(flag==1){
  	 str = str.replace(reg1,function($1){ 
	    return $1.substr(1).toLocaleUpperCase();
	 }); 
  }else if(flag==2){
  	 str=str.replace(reg2,function($1){ 
	    return "_"+$1.toLowerCase();
	 });
  }
  return str;
}
//不为空的判断，参数，如：obj={"name":"商户全名"}
function judgeBlankByIds(obj){
	for(var attr in obj){
	   var attrVal=document.getElementById(attr).value;
	   if(attrVal==""){
	      alert(obj[attr]+"不能为空！");
	      return false;
	   }
	}
	return true;
}
//验证不为空和正整数的 函数{gate:{name:"银行","integer":true,"notNull":true}}
function validateObj(obj,doObjFun){
	for(var attr in obj){
	   var validateObjVal=document.getElementById(attr).value.trim();
	   var attrObj=obj[attr];
	   for(var attrName in attrObj){
	   		var alertStr=attrObj.name==undefined?attr:attrObj.name;
	   		if(attrName=="notNull"&&attrObj[attrName]==true&&validateObjVal==""){
	   			alert(alertStr+"不能为空，请输入后再提交！");
	   			return false;
	   		}else if(attrName=="integer"&&attrObj[attrName]==true&&!isInteger(validateObjVal)){
	   			alert(alertStr+"必须为正整数！");
	   			return false;
	   		}else{
	   			if(doObjFun!=undefined) doObjFun(attr);
	   		}
	   }
	}
	return true;
}
/**********通用选择条目（反选-全选）--onclick=CheckOthers(this.form)********************/
//全选
var isCheckAll=false;
function checkAll(checkName){
	 var checkArr=document.getElementsByName(checkName);
   for (var i=0;i<checkArr.length;i++){
	   if(isCheckAll){
		   checkArr[i].checked=false;
	   }else{
		   checkArr[i].checked=true;
	   }
   }
   isCheckAll=isCheckAll?false:true;
}
//反选2
function checkReverseByName(idsName){
   var ids=document.getElementsByName(idsName);
   for(var i=0;i<ids.length;i++){
	   if(ids[i].checked){
		   ids[i].checked=false;
	   }else{
		   ids[i].checked=true;
	   } 
   }
}
/*********************************常规的验证函数**********************************/

//是否有字母和数字
function isCharAndNum1(num){
	var pattern=/[A-Za-z].*[0-9]|[0-9].*[A-Za-z]/;
	if(pattern.test(num)){
	return true;
}else{
	return false;
}
}
	
//是否有字母和数字和特殊字符
function isCharAndNum(num){
	//var pattern=/[A-Za-z].*[0-9]|[0-9].*[A-Za-z]/;
//	if(pattern.test(num)){
//	return true;
//}else{
//	return false;
//}
//}
	 if(null!=/[a-z]+/ig.exec(num)
			 &&null!=/\d+/ig.exec(num)
			 &&null!=/[,`_\[\]!@#$%^&*\(\)\.<>?\'\":;|{}~\-+=\\]+/ig.exec(num)){
		    return true;
	 }else{
		 return false;
		 } 
	 }
function isWordOrNumber(num){
	var pattern=/^[M][a-zA-Z0-9]+$/;  
	if(pattern.test(num)){
		return true;
	}else{
		return false;
	}
}
//金额格式，0-2位小数
function isMoney(num){
	//var pattern=/^[1-9][0-9]*(\.[0-9]{1,2})?$/;
	var pattern=/^(([1-9][0-9]*)|0)(\.[0-9]{1,2})?$/;
	if(pattern.test(num)){
		return true;
	}else{
		return false;
	}
}
//是否为两位小数1.00
function isTwoPointNum(num){
	var pattern=/^\d{1,}\.\d{2}$/;  
	if(pattern.test(num)){
		return true;
	}else{
		return false;
	}
}

//是否为数字
function isNumber(oNum) {
	if (!oNum)
		return false;
	var strP = /^\d+(\.\d+)?$/;
	if (!strP.test(oNum))
		return false;
	try {
		if (parseFloat(oNum) != oNum)
			return false;
	} catch (ex) {
		return false;
	}
	return true;
}
//是否为Email地址
function isEmail(data) {
	var reg = new RegExp("@[^\ ]+[\.]{1}[0-9a-zA-Z]+[\.]?[0-9a-zA-Z]+$");
	return reg.test(data);
}
//是否为整数
function isFigure(data) {
	var reg = /^\d*$/;
	return reg.test(data);
}
//是否为正整数   
function isInteger(data){   
     var reg = /^[1-9]+[0-9]*]*$/;
     return reg.test(data);
}
//金额是否为正整数   
function isPlusInteger(data){   
     var reg = /^[0-9]+[0-9]*]*$/;
     return reg.test(data);
}
//银行卡号校验（不能以0开头）
function isAccNo(data){
	var reg = /^[1-9]{1}[0-9]{9,29}$/;
    return reg.test(data);
}

//检验座机
function isTel(data) {
	//var reg = /^(\d{3}-\d{8}|\d{4}-\d{7}|\d{4}-\d{8}|\d{7}|\d{8})$/;
	//var reg = /^((0?1[358]\d{9})|((0(10|2[1-3]|[3-9]\d{2}))?[1-9]\d{6,7}))?$/;
	var reg = /^([0-9]{3,4}-)?[0-9]{7,8}$/;
	return reg.test(data);
}
//检验移动电话
function isMobel(value){ 
	//var reg = /^(13|14|15|17|18)\d{9}$/;
	var reg = /^(13[0-9]|15[0-9]|17[0-9]|18[0-9]|14[0-9])[0-9]{8}$/;
	return reg.test(value);
}
//检验身份证号
function isId(data){
	   var reg = /^(\d{15}|\d{17}[\dxX])$/;
	   return reg.test(data);
}

//验证字母和数字的组合 （字母在前 必须有数字）
function isNumEng(num){
	var reg = /^([a-zA-Z0-9]+)$/;
	return reg.test(num);
}
// 验证IP是否合法
function checkIp(ip) {
	var re = /^(\d+)\.(\d+)\.(\d+)\.(\d+)$/;// 正则表达式
	if (re.test(ip)) {
		if (RegExp.$1 < 256 && RegExp.$2 < 256 && RegExp.$3 < 256
				&& RegExp.$4 < 256)
			return true;
	}
	return false;
}

// 验证手机号是否合法
function checkPhone(v) {
	var a = /^((\(\d{3}\))|(\d{3}\-))?13\d{9}|15\d{9}|18\d{9}$/;
	if (v.length != 11 || !a.test(v)) {
		return false;
	} else {
		return true;
	}
}
// 校验(国内)邮政编码
function isPostalCode(s) {
	//var pattern = /^[0-9]{6}$/;
	var pattern = /^[1-9]\d{5}(?!\d)?$/;
	if (!pattern.exec(s)) {
		return false;
	}
	return true;
}

// 验证网址
function checkURL(URL) {
	var str = URL;
	// 下面的代码中应用了转义字符"\"输出一个字符"/"
	var Expression = /http(s)?:\/\/([\w-]+\.)+[\w-]+(\/[\w- .\/?%&=]*)?/;
	var objExp = new RegExp(Expression);
	if (objExp.test(str) == true) {
		return true;
	} else {
		return false;
	}
}

//ip的验证
function getStringIP(ip) {
	var fip = parseInt(ip / 16777216);
	var sip = parseInt((ip % 16777216) / 65536);
	var tip = parseInt((ip % 65536) / 256);
	var lip = parseInt(ip % 256);
	return fip + "." + sip + "." + tip + "." + lip;
}

//特殊字符验证[。~!@#$%\^\+\*&\\\/\?\|:\.<>{}()';="]
function examine(that) {
    var reg = /[。~!@#$%\^\+\*&\\\/\?\|:\.<>{}()';="]/;
    if(reg.test(that)){
    	 //alert("请不要输入特殊字符");
	     return false;
	  }else{	  
		  return true;
	  }
}

//ip地址校验
function examine1(that) {
	var reg=/((25[0-5]|2[0-4]\d|1?\d?\d)\.){3}(25[0-5]|2[0-4]\d|1?\d?\d)/;
    if(reg.test(that)){
	     return false;
	  }else{	  
		  return true;
	  }
}



//中文验证
function chinese(that) {
	var reg=/[\u4E00-\u9FA5\uF900-\uFA2D]/;
    if(reg.test(that)){
	     return true;
	  }else{	  
		  return false;
	  }
}
//判断是否有javascript的非法字符
function checkIllegalChar(str){
	  var reg = /^.*(\%3C|\<)(?!\s).*|.*(\%3E|\>).*|(.*script.*)|(.*onerror.*)$/;	 //
	  if(reg.test(str.toLowerCase())){
	     alert("请不要输入<,>,script等非法字符!");
	     return false;
	  }else{	  
      return true;
 }
}
/*********************************日期和时间函数**********************************/
//	20110823153157转换成20110823 15:31:57 
function getNormalTime(longTime){
	if(longTime=="null"||longTime=="")return "";
	var normalDate=longTime.substring(0,8);
	var normalTime=longTime.substring(8,10)+":"+longTime.substring(10,12)+":"+longTime.substring(12,14);
	return normalDate+" "+normalTime;
}
//返回字符串时间
function  getStringTime(nowtime) {
	var  hour = parseInt( (nowtime % 86400) / 3600 );
	var  min = parseInt( (nowtime % 3600) / 60 );
	var  second =parseInt( nowtime % 60 );
	return hour + ":" + (min < 10 ? "0" + min : min) + ":" + (second < 10 ? "0" + second : second);
}
//返回日期YYYYMMDD形式的日期
function jsToday(){
    var tmpDate = new Date();
    var day = tmpDate.getDate();
    var month= tmpDate.getMonth() + 1 ;
    var year= tmpDate.getFullYear();
    month = ((month < 10) ? "0" : "") + month;
    day = ((day < 10) ? "0" : "") + day;
    return parseInt(year + month + day) ;
}

//比较日期大小
function judgeDate(date_begin, date_end) {
	var date_begin_b;
	var date_end_e;
	if (date_begin != undefined && date_end != undefined) {
		date_begin_b = date_begin.replace(/-/g, '');
		date_end_e = date_end.replace(/-/g, '');
		if (date_begin_b > date_end_e && date_end_e != '') {
			return false;
		}
	}
	return true;
}

//计算两个日期的间隔天数
function DateDiff(sDate1, sDate2) { // sDate1和sDate2是2002-12-18格式
	var aDate, oDate1, oDate2, iDays;
	aDate = sDate1.split("-");
	oDate1 = new Date(aDate[0], aDate[1] - 1, aDate[2]);
	aDate = sDate2.split("-");
	oDate2 = new Date(aDate[0], aDate[1] - 1, aDate[2]);
	iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60 / 24); // 把相差的毫秒数转换为天数
	return iDays;
}

//处理手机号 前3+****+后4
function shadowPhone(Phone){
    return Phone
//    if(Phone==undefined){
//        return "";
//    }else{
//        var len=Phone.length;
//        if(4>len&&len>0){
//            return Phone+"******"+Phone;
//        }else if(len==0 || ""==Phone){
//            return "";
//        }
//        var head=Phone.substr(0,3);
//        var end=Phone.substr(len-4);
//        return head+"****"+end;
//    }
}
//处理账户号 前6+******+后4
function shadowAcc(Acc){
    return Acc;
//    if(Acc==undefined){
//        return "";
//    }else{
//        var len=Acc.length;
//        if(6>len&&len>0){
//             return Acc+"******"+Acc;
//        }else if(len==0 || ""==Acc){
//             return "";
//        }
//        var head=Acc.substr(0,6);//前6
//        var end=Acc.substr(len-4);
//        return head+"******"+end;
//    }
}


/********************************匹配计费公式正则表达式****************************** */
//验证计费公式
function checkFeeModel(feeModel) {
	var subFee = feeModel.substring(0, 5);
	if (subFee == 'IFBIG') {
		if (!chkIFBIG(feeModel))
			return false;
		var sub_feeModel = feeModel.substring(feeModel.indexOf(",")+1,feeModel.length-1);
		return checkFee(sub_feeModel);
	} else {
		return checkFee(feeModel);
	}
}

//验证除IFBIG(X1,X2)之外的计费公式
function checkFee(feeModel) {
	if (!chkAMT(feeModel) && !chkMAX(feeModel) && !chkMIN(feeModel)
			&& !chkMTM(feeModel) && !chkSGL(feeModel) && !chkFIX(feeModel)
			&& !chkINC(feeModel) && !chkFLO(feeModel) && !chkIPSMAX(feeModel)
			&& !chkBKFEE(feeModel) &&!chkFROMBKFILE(feeModel)) {
		return false;
	}
	return true;
}
//验证FROMBKFILE
function chkFROMBKFILE(feeModel){
	return feeModel=='FROMBKFILE';
}
//验证BKFEE(X,D,R,I)计费公式
function chkBKFEE(feeModel){
	//var reg=/^BKFEE$/;
	var reg=/^(BKFEE)([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\,([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\,([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\,[1-9]\)$/;
	return reg.test(feeModel);
}

//验证AMT*R计费公式
function chkAMT(feeModel) {
	var reg = /^AMT\*([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)$/;
	return reg.test(feeModel);
}
//验证MAX(X1,AMT*R)计费公式
function chkMAX(feeModel) {
	var reg = /^MAX\(([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\,AMT\*([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\)$/;
	return reg.test(feeModel);
}
//验证MIN(X1,AMT*R)计费公式
function chkMIN(feeModel) {
	var reg = /^MIN\(([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\,AMT\*([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\)$/;
	return reg.test(feeModel);
}
//验证MTM(X1,,X2,AMT*R)计费公式
function chkMTM(feeModel) {
	var reg = /^MTM\(([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\,([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\,AMT\*([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\)$/;
	return reg.test(feeModel);
}
//验证SGL(X)计费公式
function chkSGL(feeModel) {
	var reg = /^SGL\(([1-9]\d*\.?\d*|0|0\.\d*[1-9]\d*)\)$/;
	return reg.test(feeModel);
}
//验证INC(X,N)计费公式
function chkINC(feeModel) {
	var reg = /^INC\(([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\,([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\)$/;
	return reg.test(feeModel);
}
//验证FLO(x1,R1,x2,R2,R3)计费公式
function chkFLO(feeModel) {
	var reg = /^FLO\((([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\,([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\,){1,}([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\)$/;
	return reg.test(feeModel);
}
//验证FIX(x1,R1,x2,R2,R3)计费公式
function chkFIX(feeModel) {
	var reg = /^FIX\((([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\,([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\,){1,}([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\)$/;
	return reg.test(feeModel);
}
//验证IFBIG(x1,x2)计费公式(验证公式除X2部分外的其余部分是否匹配)
function chkIFBIG(feeModel) {
	var reg = /^IFBIG\(([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\,([\s\S]*)\)$/;
	return reg.test(feeModel);
}
//验证IPSMAX(X1,AMT*R)计费公式 环迅专用
function chkIPSMAX(feeModel) {
	var reg = /^IPSMAX\(([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\,AMT\*([1-9]\d*\.?\d*|0\.\d*[1-9]\d*)\)$/;
	return reg.test(feeModel);
}

