var zh_flag =null;
var w_state={};
var w_ptype={};
function init(){
//	if(zh_flag==null){
//			 MerZHService.getZH2(function(zh){
//				 dwr.util.addOptions("aid", zh);
//				zh_flag=zh;
//			 });
//		    }
	$("bdate").value = jsToday();
	$("edate").value = jsToday();
	$("trans_flow_").value=getParameter("trans_flow");
	$("oid").value=getParameter("oid");
	PageParam.initAdminStatePtype(function(list){
		
		  dwr.util.removeAllOptions("state");
		  dwr.util.addOptions("state", {"":"请选择…"});
		  dwr.util.addOptions("state",list[2]);
		  
		  w_state=list[2];
		  dwr.util.removeAllOptions("ptype");
		  dwr.util.addOptions("ptype", {"":"请选择…"});
		  dwr.util.addOptions("ptype",list[1]);
		  w_ptype=list[1];
	  });
	  queryMX_2(1); 
	  
}

//上传凭证
function queryMX_(pageNo){
	var aid=$("aid").value;
	var ptype=$("ptype").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
	var state=7;
//	var oid=$("oid").value;
	if(bdate=='') {
        alert("请选择起始日期！"); 
        return false; 
    }
    if(edate==''){
         alert("请选择结束日期！"); 
         return false; 
    }
   MerZHService.queryMX_(pageNo,aid,ptype,bdate,edate,state,callBack_);
}

//审批凭证
function queryMX_A_SPPZ(pageNo){
	var uid=$("mid").value;
	var ptype=$("ptype").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
	var state=7;
//	var oid=$("oid").value;
	if(bdate=='') {
        alert("请选择起始日期！"); 
        return false; 
    }
    if(edate==''){
         alert("请选择结束日期！"); 
         return false; 
    }
   MerZHService.queryMX_A_SPPZ(pageNo,uid,ptype,bdate,edate,state,callBack_A_SPPZ);
}

//查询账户上传凭证交易明细
function queryMX(pageNo){
	var aid=$("aid").value;
	var ptype=$("ptype").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
	var state=1;
	var oid=$("oid").value;
	if(bdate=='') {
      alert("请选择起始日期！"); 
      return false; 
  }
  if(edate==''){
       alert("请选择结束日期！"); 
       return false; 
  }
  MerZHService.queryMX(pageNo,aid,ptype,bdate,edate,state,oid,callBack);
}


//查询账户交易明细ZHJYMICX.jsp
function queryMX_2(pageNo){
	//var aid=$("aid").value;
	var ptype=$("ptype").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
	var state=$("state").value;
	var oid=$("oid").value;
	var trans_flow=$("trans_flow_").value;
	if(bdate=='') {
    alert("请选择起始日期！"); 
    return false; 
}
if(edate==''){
     alert("请选择结束日期！"); 
     return false; 
}
	MerZHService.queryZHJYMX_X(pageNo,ptype,bdate,edate,state,oid,trans_flow,callBack);
}

var currPage=1;
var callBack = function(pageObj){
	 $("mxTable").style.display="";
	   var cellFuncs = [
	                    function(obj) { return obj.uid; },
	                    function(obj) { return obj.aid; },
	                    function(obj) { return obj.aname; },
	                    function(obj) { return obj.oid; },
	                    function(obj){return obj.trans_flow;},
	                    function(obj) { return div100(obj.payAmt); },
	                    function(obj) { return div100(obj.transFee); },
	                   /* function(obj) { return div100(obj.trans_sum_payamt); },*/
	                /*    function(obj) { return obj.sysDate; },
	                    function(obj) { return getStringTime(obj.sysTime); },*/
	                    function(obj) { return w_state[obj.state]; },
	                    function(obj) {return w_ptype[obj.ptype]; },
	                    //function(obj) { return obj.toUid+"["+obj.toAid+"]"; },
//	                    function(obj) { return obj.toInfo; },
	                    function(obj) { return obj.sysDate+" "+getStringTime(obj.sysTime); },
//	                    function(obj) { return "<a href=\"#\" onclick=query4Remark('"+obj.oid+"'); class='box_detail'>详细说明</a>";; },
	                    function(obj) { var sysDate=obj.sysDate+" "+getStringTime(obj.sysTime);return "<a href=\"#\" onclick=\"queryOrderDetail('"+obj.uid+"','"+obj.aid+"','"+obj.aname+"','"+obj.oid+"','"+obj.trans_flow+"','"+div100(obj.transAmt)+"','"+div100(obj.transFee)+"','"+div100(obj.payAmt)+"','"+w_state[obj.state]+"','"+w_ptype[obj.ptype]+"','"+sysDate+"');\" class='box_detail'>订单详细</a>"; }
	                    
	                   // function(obj) { return obj.tseq; },
	                ];	
	                currPage=pageObj.pageNumber; 
	   			paginationTable(pageObj,"resultList",cellFuncs,"","queryMX_2");
	   			
  };
//查询账户交易明细
function queryMX_ShowDetail(pageNo,aid,oid,trans_flow){
	var ptype=$("ptype").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
	var state=1;
	if(aid==undefined){
		aid=document.getElementById("show_aid").value;
		oid=document.getElementById("show_oid").value;
		trans_flow=document.getElementById("show_transflow").value;
	}
  MerZHService.queryMX_showDetail(pageNo,aid,ptype,bdate,edate,state,oid,trans_flow,callBack_detail);
}


var callBack_detail = function(pageObj){
	 $("mxTable").style.display="";
	   var cellFuncs = [
	                    function(obj) { return obj.aid; },
	                    function(obj) { return obj.aname; },
	                    function(obj) { return obj.oid; },
	                    function(obj) { return div100(obj.transAmt); },
	                    function(obj) { return div100(obj.transFee); },
	                    function(obj) { return div100(obj.payAmt); },
	                    function(obj) { return obj.sysDate; },
//	                    function(obj) { return getStringTime(obj.sysTime); },
	                    function(obj) { return w_state[obj.state]; },
//	                    function(obj) { return w_ptype[obj.ptype]; },
	                    //function(obj) { return obj.toUid+"["+obj.toAid+"]"; },
	                    function(obj) { return obj.toInfo; },
	                    
	                    function(obj) { return getNormalTime(obj.initTime+""); }
	                    
	                   // function(obj) { return obj.tseq; },
	                ];	
	                currPage=pageObj.pageNumber; 
	   			paginationTable(pageObj,"resultList_",cellFuncs,"","queryMX_ShowDetail");
	   			
 };

var callBack_ = function(pageObj){
	 $("mxTable").style.display="";
	   var cellFuncs = [
	                    function(obj) { return obj.aid; },
	                    function(obj) { return obj.aname; },
	                    function(obj) { return div100(obj.trans_sum_amt); },
	                    function(obj) { return div100(obj.trans_sum_fee); },
	                    function(obj) { return div100(obj.trans_sum_payamt); },
	                    function(obj) { return obj.trans_flow; },//批次号
	                    function(obj){ return obj.count;},
	                    function(obj) { return obj.trans_proof; },//付款凭证
	                    function(obj) { return obj.sysDate; },
	                    function(obj) { return w_ptype[obj.ptype]; },
	                    function(obj){return (obj.state==0||obj.state==7)?'<a href="#" onclick="up_fun("'+obj.uid+'","'+obj.oid+'","'+obj.trans_flow+'")">上传凭证</a>':'<a>已上传</a>';},
	                    function(obj){return '<a href="#" onclick="show_detail("'+obj.aid+'","'+obj.oid+'","'+obj.trans_flow+'")">查看详细</a>';},
	                ];	
	                currPage=pageObj.pageNumber; 
	   			paginationTable(pageObj,"resultList",cellFuncs,"","queryMX_");
 };

var callBack_A_SPPZ = function(pageObj){
	 $("mxTable").style.display="";
	   var cellFuncs = [
	                    function(obj) { return obj.aid; },
	                    function(obj) { return obj.aname; },
	                    function(obj) { return div100(obj.trans_sum_amt); },
	                    function(obj) { return div100(obj.trans_sum_fee); },
	                    function(obj) { return div100(obj.trans_sum_payamt); },
	                    function(obj) { return obj.trans_flow; },//批次号
	                    function(obj){ return obj.count;},
	                    function(obj) { return obj.trans_proof; },//付款凭证
	                    function(obj) { return obj.sysDate; },
	                    function(obj) { return w_ptype[obj.ptype]; },
	                    function(obj){return obj.trans_proof==null?'<a>未上传</a>':'<a>已上传</a>';},
	                    function(obj){return '<a href=javascript:show_PIC("'+obj.oid+'","'+obj.trans_flow+'","'+obj.trans_proof+'",'+obj.ptype+');>'+obj.trans_proof+'</a>';},
	                ];	
	                currPage=pageObj.pageNumber; 
	   			paginationTable(pageObj,"resultList",cellFuncs,"","queryMX_");
};
	function up_fun(uid,oid,trans_flow){
//		$('uid').innerHTML=uid;
//		$('trans_flow').innerHTML=trans_flow;
		alert(uid);
		$("uid").innerHTML=uid;
		$("trans_flow_").innerHTML=trans_flow=="null"?"":trans_flow;
		$("trans_flow").value=trans_flow=="null"?"":trans_flow;
		$("oid").value=oid;
		jQuery("#hlogDetail").wBox({title:"上传付款凭证",show:true,iframeWH: {//iframe 设置高宽
            width: 1,
            height: 1
        }});//显示box
		
	}
	
	function show_detail(aid,oid,trans_flow){
		document.getElementById("show_aid").value=aid;
		document.getElementById("show_oid").value=oid;
		document.getElementById("show_transflow").value=trans_flow;
		queryMX_ShowDetail(1,aid,oid,trans_flow);
		jQuery("#hlogDetail_").wBox({title:"商户订单详细资料",show:true});//显示box
//		 MerZHService.queryMX_showDetail(1,aid,ptype,bdate,edate,state,oid,trans_flow,callBack_detail);
	}
	
	function uploadBatch(){
		var uid=$("uid").value;
		var oid=$("oid").value;
		var sj_money=$("sj_money").value;//实际充值金额
		var trans_flow=$("trans_flow").value;
		var file=dwr.util.getValue('batch');
		if(sj_money==''){alert("请输入实际充值金额");return ;}
		if($("batch").value==''){alert("请输入上传凭证图片");return ;}
		 loadingMessage("处理中，请稍候......");
		 MerZHService.uploadBatchPic(uid,trans_flow,oid,file,sj_money,function(res){
			 	if(res=="请上传正确的文件" || res=="文件格式不对" ||res=="上传失败"){
			 		alert(res);
			 		return ;
			 	}
			 	alert(res);
			 	queryMX_(1);
			 });
	}
	
	
	function show_PIC(oid,trans_flow,trans_proof,ptype){
		//var p;
		MerZHService.getPICPath(function(res){
	
			var PLPath=res.split(",")[1];
			var DBPath=res.split(",")[0];
			var picSrc;
			if(trans_flow=="null"){
				picSrc=DBPath+"/"+trans_proof;
			}else{
				picSrc=PLPath+"/"+trans_proof;
			}
			$("pz_oid").value=oid;
			$("pz_transflow").value=trans_flow;
			$("pz_ptype").value=ptype;
			MerZHService.getPICPath_(picSrc,trans_proof,function(res){
				jQuery("#img_pz").attr("src","../.."+res);
			});
		});
		
	
		jQuery("#hlogDetail_").wBox({title:"凭证审核",show:true});//显示box
	}
	
	
	function do_SHPZ(){
		var pz_oid=$("pz_oid").value;
		var pz_transflow=$("pz_transflow").value=="null"?"":$("pz_transflow").value;
		var pz_ptype=$("pz_ptype").value;
		MerZHService.do_SHPZ(pz_oid,pz_transflow,pz_ptype,function(res){
			  alert(res);
			  queryMX_A_SPPZ(1);
		});
	}

//下载查询结果

function downloadMX(){
//	var aid=$("aid").value;
	var ptype=$("ptype").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
	var state=$("state").value;
	var oid=$("oid").value;
	if(bdate=='') {
        alert("请选择起始日期！"); 
        return false; 
    }
    if(edate==''){
         alert("请选择结束日期！"); 
         return false; 
    }
    dwr.engine.setAsync(false);//把ajax调用设置为同步
    MerZHService.downloadMX(ptype,bdate,edate,state,oid,
    				function(data){dwr.engine.openInDownload(data);});
}


	//获取url中的参数
	function getParameter(param) {
		var query = window.location.search;
		var iLen = param.length;
		var iStart = query.indexOf(param);
		if (iStart == -1)
			return "";
		iStart += iLen + 1;
		var iEnd = query.indexOf("&", iStart);
		if (iEnd == -1)
			return query.substring(iStart);
	
		return query.substring(iStart, iEnd);
	}

	
	function query4ZH(){
		var uid=$("mid").value;
			 AdminZHService.getZHMapByUid(uid,function(zh){
				 dwr.util.removeAllOptions("aid");
				 dwr.util.addOptions("aid", {"":"请选择..."});
				 dwr.util.addOptions("aid", zh);
				zh_flag=zh;
			 });
	}
	
	
	/****
	 * 显示订单详细列表
	 * @param pageObj
	 * @returns
	 */
	var callBack_ZHJXMX = function(pageObj){
 
		   var cellFuncs = [
		                    function(obj){return obj.oid;},
		                    function(obj){return w_state[obj.state];},
		                    function(obj) { return w_ptype[obj.ptype]; },
		                    function(obj) {return obj.toBkNo; },
		                    function(obj) {return obj.toBkName; },
		                    function(obj) {return obj.toAccName; },
		                    function(obj) {return obj.toAccNo; },
		                    function(obj) { return obj.auditRemark; }//批次号
//		                    function(obj) { return obj.trans_proof; },//付款凭证
		                ];	
		                currPage=pageObj.pageNumber; 
		   			paginationTable(pageObj,"resultList_",cellFuncs,"","queryZHJXMX");
	};
	/***
	 * 查询账户交易明细
	 * @param oid
	 * @param trans_flow
	 */
	function queryZHJXMX(pageNo,oid,trans_flow){
		if(oid==undefined && trans_flow==undefined){
			oid=$("h_oid").value;
			trans_flow=$("h_trans_flow").value;
		}
		AdminZHService.queryZHJYMX(pageNo,oid,trans_flow,callBack_ZHJXMX);
	}
	/***
	 * 查询订单详细
	 * params -> 交易金额,交易手续费,实际金额
	 */
	function queryOrderDetail(uid,aid,aname,oid,trans_flow,trans_sum_amt,trans_sum_fee,trans_sum_payamt,state,ptype,sysDate){
		$("trans_flow").innerHTML=(trans_flow=='null' || trans_flow==null)?"":trans_flow;
		$("trans_uid").innerHTML=uid;
		$("trans_aname").innerHTML=aname;
		$("trans_aid").innerHTML=aid;
		$("trans_oid").innerHTML=oid;
		$("sys_date").innerHTML=sysDate;
		$("trans_amt").innerHTML=trans_sum_payamt+"元";
		$("trans_fee").innerHTML=trans_sum_fee+"元";
		/*$("pay_amt").innerHTML=trans_sum_payamt+"元";*/
		/*$("trans_state").innerHTML=state; */
		$("trans_ptype").innerHTML=ptype;
		$("h_oid").value=oid;
		$("h_trans_flow").value=trans_flow=='null'?"":trans_flow;
		jQuery("#hlogDetail_").wBox({title:"详细说明",show:true});//显示box
		queryZHJXMX(1,oid,trans_flow=='null'?"":trans_flow);
		
	}
