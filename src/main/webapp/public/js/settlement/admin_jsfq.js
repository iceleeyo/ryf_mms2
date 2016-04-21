/**
 * @param o
 */
var toDate;
var mid;//查询用商户号
var type;//类型 0周期内 1 周期外
var category;//商户类型
var categories = ["RYF商户","VAS商户","POS商户","POS代理商"];
var resons = {1:'商户没有可以结算的订单',2:'结算的订单没有对账',3:'余额不足',4:'未达到结算的最低金额',5:'结算冻结',6:'结算金额小于零',7:'其他',8:'商户有未完成的退款'};
var frame = new GreyFrame('detailsFrame',700,400);
function toggleAll(o){
	var status = o.checked;
	var checkboxes=document.getElementsByName("check");
	for(var i = 0;i<checkboxes.length;i++){
		checkboxes[i].checked=status;
	}
}

jQuery(function(){
	jQuery(":checkbox[name='check']").live('click', function() {
		var status = this.checked?"true":"false";
		var checkboxes=document.getElementsByName("check");
		for(var i = 0;i<checkboxes.length;i++){
			var status1 = checkboxes[i].checked?"true":"false";
			if(status == status1){
				continue;
			}else{
				if(!this.checked){
					jQuery("#toggleAll").attr("checked",this.checked);
				}
				return;
			}
		}
		jQuery("#toggleAll").attr("checked",this.checked);
	});
});
function showLiqInfoByType(){
	toDate=$("to_date").value;
	if(!toDate){
		alert("请输入截至日期");
		return;
	}
	jQuery("#toggleAll").attr("checked",false);
	LiqService.showLiqInfoByType(toDate,showLiqInfoByTypeCallback);
}
function showLiqInfoByTypeCallback(map){
	var html1 = '';
	var html2 = '';
	jQuery(".generated").each(function(){
		jQuery(this).remove();
		});
	for(var i = 0; i < 4; i++){
		html1+='<tr class="generated"><td style="text-align: left;padding-left: 20px;"><input type="checkbox" name="check" value = "'+i+'"/>'+categories[i]+
		'</td><td>'+map[i]["inCount"]+'</td><td><a href="#" onclick="liqByCategory('+i+');">结算</a><a style="padding-left: 10px;" href="#" onclick="setCondition(0,'+i+',1)">详情</a></td></tr>';
		html2+='<tr class="generated"><td>'+categories[i]+'</td><td>'+(map[i]["outCount"])+'</td><td><a href="#" onclick="setCondition(1,'+i+',1)">详情</a></td></tr>';
	}
	jQuery("#toAppend1").after(html1);
	jQuery("#toAppend2").after(html2);
	jQuery(".search").show();
}
frame.setClassName("MyGreyFrame");
function setCondition(type1,category1){
	type = type1;
	category = category1;
	mid = jQuery("#mid").val("");
	showDetails(1);
}

function showDetails(pageNo){
	mid = jQuery("#mid").val();
	LiqService.showLiqDetails(mid,type,category,toDate,pageNo,showLiqDetailsCallBack);
}
function showLiqDetailsCallBack(pageObj){
	var table = '<p style="padding:0 10px;"><span style="font-size:12px;">商户号：</span>&nbsp;&nbsp;<input type="text" id="mid"/>&nbsp;&nbsp;<button onclick="showDetails(1)">搜索</button></p><table width="100%" class="table"><tr">'+
				'<th align="center">商户号</th><th align="center">商户简称</th><th align="center">上次结算日期</th>'+
				'<th align="center">操作</th></tr><tbody id="resultList"></tbody></table>';
	var title = type==0?"<b>满足结算发起详情</b>":"<b>非结算周期商户详情</b>";
	
	frame.openHtml(title, table, 600, 392);
	if(pageObj==null){
	   document.getElementById("resultList").appendChild(creatNoRecordTr(3));
	   return;
    }
    var cellFuncs = [
                   function(obj) { return obj.mid; },
                   function(obj) { return obj.name; },
                   function(obj) { return obj.lastLiqDate; },
                   function(obj) { 
                	   if(type==1){
                    	   var hasAuth = '1'== authStr.charAt(190);
                    	   return hasAuth?'<a href="#" onclick="startLiqByMid('+obj.mid+')">结算</a>':'<font color="silver">结算</font>';
                	   }else{
                    	   return '<a href="#" onclick="startLiqByMid('+obj.mid+')">结算</a>';
                	   }
            	   }
               ];
	paginationTable(pageObj,"resultList",cellFuncs,"","showDetails");
	jQuery("#mid").val(mid);
}

/**
 * 批量结算
 * @param category 商户类型
 */
function liqByCategory(category){
	if(!confirm("确认结算？")){
		return;
	}
	var cateArray = new Array();
	if(category!=undefined){
		cateArray.push(category);
	}else{
		var checkedBoxes = jQuery(":checkbox[name='check'][checked=true]");
		if(checkedBoxes.length == 0){
			alert("请选择要结算的商户类别");
			return;
		}
		jQuery(checkedBoxes).each(function(){
			cateArray.push(this.value);
		 });
	}
	LiqService.startBatchLiq(cateArray,toDate,batchLiqCallBack);
}

function batchLiqCallBack(msg){
	alert(msg);
}

function startLiqByMid(mid){
	if(!confirm("确认结算？")){
		return;
	}
	LiqService.startLiqByMid(mid,toDate,startLiqByMidCallBack);
}
function startLiqByMidCallBack(code){
	if(code == 0 ){
		alert("结算发起成功");
	}else{
		alert("结算发起失败，原因："+resons[code]);
	}
}
