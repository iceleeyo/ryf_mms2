function toAdd() {

	dwr.engine.setAsync(true);  
    var html = '<tr><td class="th1" align="right">商户号：</td><td>';

  /*  for(var key in mapArr2[0]){
		html += '<option value="'+key+'">'+mapArr2[0][key]+'</option>';
		
	}*/
    html += '&nbsp;<input type="text" maxlength="6" id="addmid" name="mid" onblur="getMinfoName()"/>&nbsp;<font color="red">*</font>&nbsp;</td></tr>';
    //联行行号
    html += '<tr><td class="th1" align="right">商户名称：</td><td><input name="name1" id ="addname" type="text" maxlength="12"/>&nbsp;<font color="red">*</font>&nbsp;</td></tr>';
    //联行名称
    html += '<tr><td class="th1" align="right">推送IP：</td><td><input name="ip" type="text" maxlength="20"/>&nbsp;<font color="red">*</font>&nbsp;</td></tr>';
    
    html += '<tr><td colspan="2" align="center"><input type="button" value="确认" onclick="add();"/>&nbsp;&nbsp;<input type="button" value="取消" onclick="jQuery(\'#wBox_close\').click();"/></td></tr>';
	jQuery("#pupUpTable").html(html);
	jQuery("#pupUpTable").wBox({title : "&nbsp;&nbsp;新增同步商户",show : true});//显示弹出层
	
	
}

function add(){
	//查找wbox弹出的隐藏层中的元素 需要在#wBox下查找
	var minfoNotice = {};
	minfoNotice.mid = jQuery('#wBox #pupUpTable input[name=mid]').val().trim();
	minfoNotice.name = jQuery('#wBox #pupUpTable input[name=name1]').val();
	minfoNotice.ip = jQuery('#wBox #pupUpTable input[name=ip]').val().trim();
	if(minfoNotice.mid == ""){
		alert("商户号不能为空");
		return;
	}
	
	/*if (isFigure(bankNoInfo.bkNo) && bankNoInfo.bkNo.length == 12){
		
	}
	else{
		alert("联行号只能是12位的数字！");
		return false;
	}*/
	
	if(minfoNotice.name == ""){
		alert("商户名称不能为空");
		return;
	}
	if(minfoNotice.ip == ""){
		alert("推送ip不能为空");
		return;
	}
	SettlementNoticeService.addNotice(minfoNotice,function(count){
		if(count == 1){
			alert("新增成功");
			jQuery("#wBox_close").click();//关闭弹出层
			query(1);
		}else{
			alert("新增失败");
		}
	});
}

function init(){
	
      $("beginDate").value = jsToday();
      $("endDate").value = jsToday();
}


var query_cash = {};
var currentPage = 1;
function checkdata(date_b, date_e) {
	
	if (!judgeDate(date_b, date_e)) {
		alert("开始日期应先于结束日期");
		return false;
	}

	return true;
}


function query(p){ 
	
	var date_b = document.getElementById("beginDate").value;
	var date_e = document.getElementById("endDate").value;
	var name = document.getElementById("name").value;
	var mid = document.getElementById("mid").value;
	
	 if(mid !='' && !isFigure(mid)){
			alert("商户号只能是整数!");
			$("mid").value = '';
			return false; 
 	 }
	
	
	if (checkdata(date_b,date_e)) {
		SettlementNoticeService.getMinfoNotices(p, date_b, date_e, mid,  name,resultData);
	}
}


//var chxCount = 0;
var resultData = function (flbPage) {
	 $("detailResultList").style.display="";
	 dwr.util.removeAllRows("bodyTable");
	if (flbPage.pageTotle != 0) {
		var cellFuncs = [
		                 function(obj) { return obj.mid; },
		                 function(obj) { return  obj.name; },
		                 function(obj) { return obj.ip; },
		                 function(obj) { return obj.createDate; },
		                 function(obj) {
		                	 return "<input class=\"button\" type=\"button\"  onclick =toModify('"+obj.id+"'); value=\"修改\"/><input class=\"button\" type=\"button\"  onclick = delById('"+obj.id+"') value=\"删除\"/>";
		                  	                      
		                 }
		              ];
		
		paginationTable(flbPage,"bodyTable",cellFuncs,"","query"); 
		
	}else {
		document.getElementById("bodyTable").appendChild(creatNoRecordTr(6));
	}
};


function delById(id){
	if(confirm("确认删除？")){
		SettlementNoticeService.delById(id,function(count){
			if(count == 1){
				alert("删除成功");
				query(1);
			}else{
				alert("删除失败");
			}
		});
	}
}

function toModify(id) {
	
	SettlementNoticeService.queryById(id,function(mt){
		
		
	    var html = '<tr><td class="th1" align="right">商户号：</td><td><input type="text" maxlength="20" id="umid" value="'+mt.mid+'"/>&nbsp;<font color="red">*</font>&nbsp;</td></tr>';
	  
	    html += ' <tr><td><input type="hidden" name="id" value="'+mt.id+'"/></td></tr>';
	    //商户名称
	    html += '<tr><td class="th1" align="right">商户名称：</td><td><input name="name" id ="uname" type="text" value="'+mt.name+'" maxlength="20"/>&nbsp;<font color="red">*</font>&nbsp;</td></tr>';
	    //推送ip
	    html += '<tr><td class="th1" align="right">推送ip：</td><td><input name="ip" type="text" value="'+mt.ip+'" />&nbsp;<font color="red">*</font>&nbsp;</td></tr>';
	    
	    html += '<tr><td colspan="2" align="center"><input type="button" value="确认" onclick="modify();"/>&nbsp;&nbsp;<input type="button" value="取消" onclick="jQuery(\'#wBox_close\').click();"/></td></tr>';
		jQuery("#pupUpTable").html(html);
		jQuery("#pupUpTable").wBox({title : "&nbsp;&nbsp;修改同步商户",show : true});//显示弹出层
		jQuery("#uname").prop("disabled",true);
		jQuery("#umid").prop("disabled",true);
	});
}

function modify(){
	var minfoNotice = {};
	minfoNotice.mid = jQuery('#wBox #pupUpTable input[name=mid]').val();
	minfoNotice.name = jQuery('#wBox #pupUpTable input[name=name1]').val();
	minfoNotice.ip = jQuery('#wBox #pupUpTable input[name=ip]').val();
	minfoNotice.id = jQuery('#wBox #pupUpTable input[name=id]').val();
	if(minfoNotice.ip == ""){
		alert("推送ip不能为空");
		return;
	}
	
	SettlementNoticeService.update(minfoNotice,function(count){
		if(count == 1){
			alert("更新成功");
			jQuery("#wBox_close").click();//关闭弹出层
			query(1);
		}else{
			alert("更新失败");
		}
	});
}

function getMinfoName(){
	var mid = $("addmid").value;
	SettlementNoticeService.getMinfoName(mid,function(name){
	document.getElementById("addname").value=name;
	});
}




