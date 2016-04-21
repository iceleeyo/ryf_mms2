
var a = [{name:'-1',text:'全部 ...'}];
function init1(){
	//dwr.util.removeAllOptions("gate");
	//dwr.util.addOptions("gate", a,'name','text',{escapeHtml:false});
	//PageService.getGatesMap(function(map){h_gate = map; dwr.util.addOptions("gate", h_gate);});
	//RypCommon.getHashMer(function(map) {minfoMap = map;});
	//dwr.util.addOptions("type", a,'name','text',{escapeHtml:false});
	//dwr.util.addOptions("type", h_type);
	initMinfos();
	dwr.util.addOptions("mstate", m_mstate);
	
}

//var pageObj={};
//根据条件查询(第一次查询)
function queryBaseIncome() {
    var mid = $("mid").value.trim();
    var date_b = $("date_begin").value.trim();
    var date_e = $("date_end").value.trim();
    var mstate=$("mstate").value.trim();
    //var gate = $("gate").value;
    //var type = $("type").value;
    if (mid!='' && !isFigure(mid)){
    	 alert("商户号只能是整数!");$("mid").value='';
        return false;
    }
    if (date_b == ''){
        alert("请选择查询起始日期!");
        return false;
    }   
    if (date_e==''){
        alert("请选择查询结束日期!");
        return false;
    }
    	//pageObj = {"loadtype":"Income","mid":mid ,"beginDate":date_b,"endDate":date_e};
       SettlementService.searchIncome(mid, date_b, date_e,mstate, callIncomeList);  
     
       //提取用户列表的回设函数：balanceList中放的是minfo对象
	 function callIncomeList(IncomeList) {
		 $("IncomeList").style.display = '';

			    var cellFuncs = [
			    			 function(obj) { return obj.liqDate;},
		                     function(obj) { return obj.mid;},
		                     function(obj) { return m_minfos[obj.mid]; },
		                    // function(obj) { return document.getElementById("gate").value== -1 ? "全部" : h_gate[obj.gate]; },
		                     //function(obj) { return h_type[obj.type]; },
		                     function(obj) { return div100(obj.amount); },
		                     function(obj) { return div100(obj.refAmt);},
		                     function(obj) { return div100(obj.feeAmt); },
		                     function(obj) { return div100(obj.refFee); },		                     
		                     function(obj) { return div100(obj.bankFee);},
		                     function(obj) { return div100(obj.bkRefFee); },
		                     function(obj) { return div100(obj.income); }
		                 ];
		  dwr.util.removeAllRows("IncomeTable");
		 if(IncomeList==null||IncomeList.length==0){
			 document.getElementById("IncomeTable").appendChild(creatNoRecordTr(cellFuncs.length));
			 return;
		 }
		   var str = "<span  style='float:left'>&nbsp;&nbsp;&nbsp;<input type='button' name='download' id='download' value='导出Excel' onclick=\"downLoadInCome();\"></span>";
		   //paginationTable(IncomeList,"IncomeTable",cellFuncs,str,"queryBaseIncome");
		  
		   dwr.util.addRows("IncomeTable",IncomeList,cellFuncs,null);
		    //分页行
		     
	}
}
function downLoadInCome(){
	
	     var mid = $("mid").value.trim();
	    var date_b = $("date_begin").value.trim();
	    var date_e = $("date_end").value.trim();
	    //var gate = $("gate").value;
	    //var type = $("type").value;
	    var mstate=$("mstate").value.trim();
	    if (mid!='' && !isFigure(mid)){
	    	 alert("商户号只能是整数!");$("mid").value='';
	        return false;
	    }
	    if (date_b == ''){
	        alert("请选择查询起始日期!");
	        return false;
	    }   
	    if (date_e==''){
	        alert("请选择查询结束日期!");
	        return false;
	    }
	
	
	    dwr.engine.setAsync(false);//把ajax调用设置为同步
		SettlementService.downloadIncomeSheet(mid,date_b,date_e,mstate,function(data){dwr.engine.openInDownload(data);});
}

