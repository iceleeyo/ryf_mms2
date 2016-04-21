//var chk = document.getElementsByName("mid");
//var available = "";
//function selectall(flag){
//    for (i = 0; i < chk.length; i ++){
//         chk[i].checked = flag;
//    }
//} 
//var layer;
///**
// * 检查商户是否完成对账
// */
//function chkeckMinfo(){
//    var mids = "";
//    var date = document.getElementById("exp_date").value;
//    for (i = 0; i < chk.length; i ++){
//         if (chk[i].checked ) {
//            mids += chk[i].value + ",";
//        }
//    }
//    if(mids == "") {
//        alert("您还没有选择商户进行结算!");
//        return false;
//    }
//    layer = getOverlay("begin","校验商户是否完成对账,请稍后。。。");
//    SettlementService.checkMinfo(mids,date,callback);
//}
//function callback (msg) {
//   available = msg.split(";")[0];
//   var invalid1 = msg.split(";")[1];//不能发起结算的商户(商户号,原因_商户号,原因..)
//   var invalid2 = msg.split(";")[2];//不能发起结算的商户(商户号，商户号..)
//   layer.remove();
//   if (invalid1 != ""){
//       alert(invalid2 + "这些商户不满足结算条件(可能是没有结算的订单或者订单没有对账!)");
//       var idReasonList= invalid1.split("_");
//       for(j = 0; j < idReasonList.length-1; j++){
//           var idReason = idReasonList[j];
//           var id = idReason.split(",")[0];
//           var reasonCode = idReason.split(",")[1];
//           document.getElementById(id).checked = false;
//           var obj =   document.getElementById("n_"+id);
//           obj.innerHTML = obj.innerHTML+"<font color='red'>(" + decodeReason(reasonCode) +")</font>";
//       }
//   }
//   if (available != "" && confirm("请发起结算!")){
//	   beginSettle(document.getElementById("exp_date").value,available);
//       }
//   document.getElementById("allSelect").disabled="true";
//   for (i = 0; i < chk.length; i ++){
//       chk[i].disabled = "true";
//   }
//}
///**
// * 对原因解码
// * @param n 原因编码 
// * @return 返回原因
// */
//function decodeReason(n){
//    var reason = "";
//    switch(n){
//        case "1": reason = "没有可以结算的订单!";break;
//        case "2": reason = "没有对账!";break;
//        }
//        return reason;
//}
///**
// * 根据结算发起的截止日期和非结算周期内商户的商户号罗列商户信息
// * @param date 结算发起的截止日期
// * @param mids 非结算周期内商户的商户号
// * @return
// */
//function getMinfos(date,mids){
//	SettlementService.getSettleMinfos(date,mids,function(minfoBack){
//    	var count = 0;
//    	var id,name,tableRow,tableCell;
//    	dwr.util.removeAllRows("settleMinfo");
//    	var dtable = document.getElementById("settleMinfo");
//     
//        for(var k in minfoBack){
//        	if(count % 4 == 0){
//        		tableRow = dtable.insertRow(-1);
//        	}
//        	if (minfoBack[k] == "N/A"){
//        		id = "<input type='checkbox' disabled/>" + k;
//        		name = "<font color='red'>此商户不存在!</font>";
//        	} else {
//        		id = "<input id='" + k+ "' type='checkbox' name='mid' value='" + k+ "'/>" + k;
//        		name = minfoBack[k];
//        	}
//        	tableCell = tableRow.insertCell(-1)
//        	tableCell.innerHTML =id;
//        	tableCell = tableRow.insertCell(-1);
//        	tableCell.innerHTML = name;
//        	tableCell.id = "n_" + k;
//            count ++;
//        }
//    });
//}
///**
// * 对完成对账操作的商户发起结算
// * @return
// */
//function beginSettle(date,mids){
//    if(available == "") {
//	    alert("您还没有选择商户进行结算!");
//	    return false;
//	}
//    layer = getOverlay("begin","结算中,请稍后。。。");
//    SettlementService.beginSettlement(date,mids,function(msgBack){
//		layer.remove();
//		document.getElementById("settleMids").innerHTML = mids;
//		var msg = msgBack.split(";")[0];
//		if (msg.indexOf(";") != -1){
//			msg += "<br/>其中" + msg.split(";")[1] + "商户因余额不足或不满足结算满足额度没有发起结算!";
//		}
//		document.getElementById("result").style.display='';
//		document.getElementById("begin").style.display='none';
//        document.getElementById("msg").innerHTML = msg;
//	});
//}

