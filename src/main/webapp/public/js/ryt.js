
document.write("<script type='text/javascript' src='../../public/js/ryt_util.js?v=2.5'></script>");
document.write("<script type='text/javascript' src='../../public/js/jquery-1.6.min.js'></script>");
document.write("<script type='text/javascript' src='../../public/js/TableSort.js'></script>");
document.write("<script type='text/javascript' src='../../public/js/wbox.js?v=2.5'></script>");
document.write("<script type='text/javascript' src='../../public/js/runTableSort.js?"+Math.random()+"'></script>");

//dwr的异常信息
dwr.engine.setErrorHandler(function errh(msg, exc) {
	  //alert("异常类型:" +exc.javaClassName+ " \n错误信息: " + msg);
	  alert(" \n错误信息: " + msg);
});

//dwr.engine.setAsync(false);//把ajax调用设置为同步
var h_tstat = {0:'初始状态',1:'待支付',2:'交易成功',3:'交易失败',4:'请求银行失败',5:'撤销'};
//var h_type = {0:'初始化',1:'网银支付',2:'消费充值卡支付',3:'信用卡支付',6:'语音/快捷支付',8:'手机WAP支付',10:'POS支付',11:'代付业务'};
var h_type = {0:'初始化',1:'个人网银支付',7:'企业网银支付',2:'消费充值卡支付',3:'信用卡支付',6:'语音支付',18:'快捷支付',8:'手机WAP支付',10:'POS支付',13:'B2B充值',15:'对私代扣',5:'增值业务'};
/*所以交易类型 h_type_all*/
var h_type_all={0:'初始化',1:'个人网银支付',2:'消费充值卡支付',3:'信用卡支付',4:'退款交易',5:'增值业务',6:'语音支付',7:'企业网银支付',8:'手机WAP支付',10:'POS支付',11:'对私代付',12:'对公代付',13:'B2B充值',14:'线下充值',15:'对私代扣',16:'对公提现',17:'对私提现',18:'快捷支付'};

var h_settle_type = {0:'初始化',1:'网银支付',2:'消费充值卡支付',3:'信用卡支付',4:'退款交易',5:'增值业务',6:'语音支付',7:'企业网银支付',8:'手机WAP支付',10:'POS支付',18:'快捷支付'};
//var h_author_type = { 0:"直属银行",1:"汇付支付",2:"环迅支付",3:"高阳捷迅",4:"快钱支付"};
//var h_merge_bk={10001:"交通银行",90000: "招商银行(WAP)",90001:"交通银行(WAP)",90002:"建设银行(WAP)",
 //       90003:"工商银行(WAP)",1:"汇付信用卡",2:"环迅",3:"高阳捷迅",4:"汇付网银"};


//此处修改需要修改AppParam 中 h_merge_bk_1的值
//var h_merge_bk_1={10001:"交通银行",10500:"农业银行",90000: "招商银行(WAP)",90001:"交通银行(WAP)",90002:"建设银行(WAP)",
//        90003:"工商银行(WAP)",90004:"中国银行(WAP)",1:"hf",2:"hx",3:"gy",4:"kq",5:"jh"};//单笔查询时用到


//var h_merge_bk={10001:"交通银行",10500:"农业银行",90000: "招商银行(WAP)",90001:"交通银行(WAP)",90002:"建设银行(WAP)",
//        90003:"工商银行(WAP)",90004:"中国银行(WAP)",0:"hf网银",1:"hf信用卡",2:"hx",3:"gy",4:"kq",5:"jh"};


//var h_author_type = { 0:"yh",1:"hf",2:"hx",3:"gy",4:"kq",5:"jh"};
var h_bk_chk = {0: "初始状态（未对帐）",1: "勾对成功，双方均成功",2: "勾对失败，双方金额不符",3: "勾对失败，银行为可疑交易"};
var h_bk_flag = {0:"初始状态",1:"正常"};
var h_account_type = {1: "支付",2: "退款",3: "结算",4: "手工增加",5: "手工减少"};
var h_liq_period = {1:"每天清算一次",2: "每周清算一次",3:"每周清算两次",4:"每月清算一次",5:"其它"};
var h_liq_type = {1: "全额",2: "净额"};
var h_stat_flag = {0: "正常",1: "关闭"};
var h_admin_refund_tstat = {1:"商户已提交",2:"退款完成",3:"操作成功",4:"操作失败",5:"商户未确认",6:"撤销退款",7:"商户撤销"};
//运行经办用
var h_admin_refund_tstat_yxjb = {1:"商户已提交",2:"退款完成",6:"撤销退款"};
//退款审核
var h_admin_refund_tstat_sh = {'2 or stat = 3 or stat = 4 ':'全部',2:"未操作",3:"操作成功",4:"操作失败"};
//var h_mer_refund_tstat = {5:"商户未确认",1:"商户已提交",7:"商户撤销",2:"退款成功",6:"退款撤销",3:"操作成功",4:"操作失败"}
var h_mer_refund_tstat = {5:"商户申请退款",1:"商户确认退款",7:"商户撤销退款",2:"系统退款成功",6:"系统退款失败",3:"系统退款成功",4:"系统退款失败"};
var h_mer_refund_tstat_ = {5:"商户申请退款",1:"商户确认退款",7:"商户撤销退款",'2 or stat=3 ':"退款成功",'6 or stat=4 ' :"退款失败"};
var h_mer_Onlinerefund_tstat={0:"待处理",1:"处理中",2:"联机退款成功",3:"联机退款失败",4:"请求银行失败"};
var cmb_b2e_area={
		10:"北京",12:"离岸分行",13:"总行离岸中心",20:"广州",21:"上海",22:"天津",23:"重庆",24:"沈阳",25:"南京",27:"武汉",28:"成都",29:"西安",35:"太原",37:"郑州",
		38:"石家庄",41:"大连",43:"长春",45:"哈尔滨",47:"呼和浩特",51:"银川",52:"苏州",53:"青岛",54:"宁波",55:"合肥",56:"济南",57:"杭州",59:"福州",69:"东莞",
		71:"南宁",73:"长沙",75:"深圳",77:"佛山",79:"南昌",85:"贵阳",87:"昆明",91:"乌鲁木齐",92:"厦门",93:"兰州",97:"香港",03:"总行会计部",01:"总行"};
//gateRoute type
var h_gate_route_type = {
		1:"个人网银支付",
		2:"消费充值卡支付",
		3:"信用卡支付",		
		5:"增值业务",
		6:"语音支付",
		7:"企业网银支付",
		8:"手机WAP支付",		
		10:"POS支付",
		11:"对私代付",
		12:"对公代付",
		13:"B2B充值",
		//14:"线下充值",
		15:"对私代扣",
		16:"对公提现",
		17:"对私提现",
		18:"快捷支付"
};
//商户信息
var m_mstate={0:"正常",1:"关闭"};
//账户信息
var z_state={0:"生成",1:"正常",2:"关闭"};
var z_payFlag={0:"关闭",1:"正常"};
var z_ptype={0:"充值",1:"提现",2:"提现撤销",3:"付款",4:"收款"};
var zz_state={0:"订单生成",1:"交易处理中",2:"交易成功",3:"交易失败",4:"订单取消"};
var tr_type={0:"充值",1:"提现",2:"提现撤销",3:"付款",4:"收款",10:"支付",11:"支付手续费",12:"退款",13:"退款退回手续费",14:"调增",15:"调减",16:"结算"};
var rec_pay={0:"增加",1:"减少"};
var merTradeType={100:"航空机票",101:"酒店/旅游",102:"服务/缴费",103:"综合商城",104:"金融/保险",105:"虚拟/游戏",106:"医药/保健",107:"教育/招生",108:"交友/咨询",109:"其他"};
var z_tate={0:"初始",1:"注册成功",2:"注销"};
var z_id_type={15:"居民身份证"};
var bk_b2b={20000:"交通银行(企)",20001:"中国银行(企)"};
var bk_c_b2b={40000:"交行超级网银",40001:"中国银行(银企)",40002:"交通银行(银企)",40003:"招商银行(银企)"};
var z_ctype={0:"企业",1:"个人"};
/**代付交易查询**/
var df_type={11:'对私代付',12:'对公代付',17:'对私提现',16:'对公提现'};
var pstates={1:'经办成功',2:'经办失败',3:'审核成功',4:'审核失败',5:'凭证审核成功',6:'凭证审核失败'};

//备份金管理
var trans_type_map={0:"差错退款",1:"资金归集",2:"应收商户手续费调增",3:"应收商户手续费调减"};
var trans_state_map={0:"订单生成",1:"订单处理中",2:"交易成功",3:"交易失败"};
var mer_category_map={0:"RYF商户",1:"VAS商户",2:"POS商户",3:"POS代理商"};
var a = [{name:'',text:'全部'}];

//代扣证件类型
var dk_ID_types={"01":"身份证","02":"军官证","03":"护照","04":"户口簿","05":"回乡证","06":"其他"};

//商户类别
var m_category = {0 : "RYF商户", 1 : "VAS商户", 2 : "POS商户"};


//pos交易类型
var pos_type = {
		"2":"消费",
		"18":"消费撤销",
		"20":"退货",
		"26":"消费冲正",
		"28":"消费撤销冲正"/*
		"52":"预授权",
		"56":"预授权完成",
		"58":"预授权完成撤销",
		"54":"预授权撤销",
		"76":"预授权冲正",
		"80":"预授权完成冲正",
		"78":"预授权撤销冲正",
		"82":"预授权完成撤销冲正",
		"110":"电子现金消费",
		"20":"电子现金退货同退货"*/
			};

//================================

//if(typeof(PageService)!="undefined"){
//	PageService.getGatesMap({callback:function(map){
//		h_gate = map;
//	},async:false});
//}
var h_gate = {};//初始化网关
var gate_route_map={};  //替换以前的h_author_type
function initGateChannel(){
	PageService.getGateChannelMap(function(mapArr){
		h_gate=mapArr[0];//select gate,gate_name from ryt_gate
		gate_route_map=mapArr[1];//select gid,name from gate_route order by gid";
		if($("gate")){
			dwr.util.addOptions("gate", h_gate);
		}
		if($("gateRouteId")){
			dwr.util.addOptions("gateRouteId", gate_route_map);
		}
	});
}


//所有出款交易银行
var h_gate2 = {};//初始化网关
var gate_route_map2={};  //替换以前的h_author_type
function initGateChannel2(obj){
	PageService.getDFGateChannelMapByType(obj,function(mapArr){
		h_gate2=mapArr[0];
		gate_route_map2=mapArr[1];
		if($("gate")){
			dwr.util.addOptions("gate", h_gate2);
		}
		if($("gateId")){
			dwr.util.addOptions("gateId", h_gate2);
		}
		if($("gateRouteId")){
			dwr.util.addOptions("gateRouteId", gate_route_map2);
		}
	});
}

//所有出款交易银行
var h_gate3 = {};//初始化网关
var gate_route_map3={};  //替换以前的h_author_type
function initGateChannel3(obj){
	PageService.getDFGateChannelMapByType3(obj,function(mapArr){
		h_gate3=mapArr[0];
		gate_route_map3=mapArr[1];
		if($("gate")){
			dwr.util.addOptions("gate", h_gate3);
		}
		if($("gateId")){
			dwr.util.addOptions("gateId", h_gate3);
		}
		if($("gateRouteId")){
			dwr.util.addOptions("gateRouteId", gate_route_map3);
		}
	});
}

var h_gate1 = {};//初始化网关
var gate_route_map1={}; 
function initGateChannel1(){
	PageService.getGateChannelMap1(function(mapArr){
		h_gate1=mapArr[0];
		gate_route_map1=mapArr[1];
		if($("gate")){
			dwr.util.addOptions("gate", h_gate1);
		}
		if($("gateRouteId")){
			dwr.util.addOptions("gateRouteId", gate_route_map1);
		}
	});
}


//---onload时初始化商户options--
var m_minfos={};
function initMinfos(){ 
    PageService.getMinfosMap(function(m){
    	m_minfos=m;
    	if($("smid")){
    		dwr.util.addOptions("smid", m);
    	}
    });
}

//================================
//修改mid时 改变商户name
function checkMidInput(obj){
    if(obj && !isFigure(obj.value)){
    	obj.value='';
    }else{
    	dwr.util.setValue('smid',obj.value);
    }
}
//改变商户name时 改变显示的商户号mid
function initMidInput(value){
	 if(document.getElementById("mid")) document.getElementById("mid").value= value;
}

//输入不为数字则置空input
function inputFigure(obj){
	if(obj && !isFigure(obj.value)){
    	obj.value='';
    }
}
/**产生提示的tip
 *maxlength:出现tip的长度，str：显示的字符串
 */
function createTip(maxlength,str){
	   if(str==""||str==null||str==undefined){
		   return "";
	   }else if(str.length<=maxlength){
	       return str;
	   } else {
		   return "<a href=\"#\" tip='"+str+"' class='showTip'>"+str.substring(0,maxlength)+"...</a>";
	   }
}

//session超时服务器端调用此函数
function logOut(){
		//alert("操作超时,请重新登陆!");
		location.reload();
}

//图片加载中...   
function useLoadingImage(imageSrc,top,right) { 
	  var loadingImage; 
	  if (top) loadTop = top;
	  else loadTop = "120px";
	  var loadRight;
	  if (right) loadRight = right;
	  else loadRight = "600px";
	  
	  if (imageSrc) loadingImage = imageSrc;     
	  else loadingImage = "ajax-loader.gif";     
	  dwr.engine.setPreHook(function() {     
	    var disabledImageZone = $('disabledImageZone');     
	    if (!disabledImageZone) { 
		    disabledImageZone = document.createElement('div');
		    	disabledImageZone.setAttribute('id', 'disabledImageZone');
	    	var imgBgDiv = document.createElement('div');
	    		imgBgDiv.setAttribute('id', 'imgBgDiv');
		    var imageZone = document.createElement('img');
		    	imageZone.setAttribute('id', 'imageZone');
	  	        imageZone.setAttribute('src',imageSrc);
	  	        imageZone.style.top =loadTop;     
	  	        imageZone.style.right =loadRight;
	  	    disabledImageZone.appendChild(imgBgDiv);
	  	    disabledImageZone.appendChild(imageZone);
	  	    document.body.appendChild(disabledImageZone);
	    }     
	    else {     
	      $('imageZone').src = imageSrc;     
	      disabledImageZone.style.visibility = 'visible';     
	    }     
	  });     
	  dwr.engine.setPostHook(function() {     
	    $('disabledImageZone').style.visibility = 'hidden';     
	  });     
}
//加载中...
function loadingMessage(message,top,right) {
  var loadingMessage;
  if (message) loadingMessage = message;
  else loadingMessage = "Loading...";
  var loadTop;
  if (top) loadTop = top;
  else loadTop = "80px";
  var loadRight;
  if (right) loadRight = right;
  else loadRight = "600px";
  dwr.engine.setPreHook(function() {
    var disabledZone = $('disabledZone');
    if (!disabledZone) {
      disabledZone = document.createElement('div');
      disabledZone.setAttribute('id', 'disabledZone');
      disabledZone.style.position = "absolute";
      disabledZone.style.left = "0px";
      disabledZone.style.top = "0px";
      disabledZone.style.width = "100%";
      disabledZone.style.height = "100%";
      if(!+[1,]){//这是ie浏览器
          disabledZone.style.filter = "alpha(opacity=40);";
          disabledZone.style.background = "white";
      }
      document.body.appendChild(disabledZone);
      var messageZone = document.createElement('div');
      messageZone.setAttribute('id', 'messageZone');
      messageZone.style.position = "absolute";
      messageZone.style.top = loadTop;
      messageZone.style.right = loadRight;
      messageZone.style.background = "red";
      messageZone.style.color = "white";
      messageZone.style.zIndex = "1000";
      messageZone.style.fontFamily = "Arial,Helvetica,sans-serif";
      messageZone.style.padding = "4px";
      disabledZone.appendChild(messageZone);
      var text = document.createTextNode(loadingMessage);
      messageZone.appendChild(text);
    }
    else {
      $('messageZone').innerHTML = loadingMessage;
      disabledZone.style.visibility = 'visible';
    }
  });

  dwr.engine.setPostHook(function() {
    $('disabledZone').style.visibility = 'hidden';
  });
};
//加上全局的屏蔽
useLoadingImage("../../public/images/wbox/loading.gif");
/**
 * pageObj 页面对象,
 * dataTableId 显示数据的tableId 或者 tbodyId
 * cellFuncs  列函数数组
 * footLeftStr 分页行左边显示的字符 自定义
 * searchFF 查询的方法
 */
function paginationTable(pageObj,dataTableId,cellFuncs,footLeftStr,searchFF){
	
	var pageTable = $(dataTableId);
	if(!pageTable){
  	  return;
    }
	if(!footLeftStr){
	  footLeftStr = "";
	}
    //清空表单数据
    dwr.util.removeAllRows(dataTableId);
    if(!pageObj) return;
    if(pageObj.pageItems.length==0){
  	  var pageTr = pageTable.insertRow(-1);
      var pageTd = pageTr.insertCell(-1);
      pageTd.setAttribute("colSpan",cellFuncs.length);
      pageTd.setAttribute("align","center");
      pageTd.innerHTML='没有符合条件的记录';
      return;
    }
    dwr.util.addRows(dataTableId,pageObj.pageItems,cellFuncs,{escapeHtml:false});
    //分页行
    var pageTr = pageTable.insertRow(-1);
    var pageTd = pageTr.insertCell(-1);
    pageTd.setAttribute("colSpan",cellFuncs.length);
    pageTd.setAttribute("height","30px");
    //排序时要设置role="bottom"底部才不会参与排序
    pageTr.setAttribute("role","bottom");

    var str = "<span  style='float:left'>"+footLeftStr+"</span>";
    str += "<span style='float:right;display:inline-block'>共<font color='blue'>" + pageObj.pageTotle+ "</font>条，";
    str += "第<font color='blue'>" + pageObj.pageNumber + "/" + pageObj.pagesAvailable + "</font>页,";
    str += "每页显示<font color='blue'>"+ pageObj.pageSize + "</font>条&nbsp;&nbsp;&nbsp;"; 
    str += "&nbsp;<input type= \"button\" value=\" 首页 \"" + (pageObj.pageNumber == 1 ? "disabled>" : "onclick=\""+searchFF+"("+1+")\">" );
    str += "&nbsp;<input type= \"button\" value=\"上一页\"" + (pageObj.pageNumber > 1 ? "onclick=\""+searchFF+"("+ (pageObj.pageNumber-1) +")\">" : "disabled>")  ;
    str += "&nbsp;<input type= \"button\" value=\"下一页\"" + (pageObj.pageNumber < pageObj.pagesAvailable ? ("onclick=\""+searchFF+"("+(pageObj.pageNumber+1) +")\">") : "disabled>" );
    str += "&nbsp;<input type= \"button\" value=\" 尾页 \"" + (pageObj.pagesAvailable == pageObj.pageNumber ? "disabled>" : "onclick=\""+searchFF+"("+(pageObj.pagesAvailable) +")\">") ;
    str = str + "&nbsp;跳转到: <select id=\"pageChoose\" "+(pageObj.pageTotle<=pageObj.pageSize ? "disabled" : "")+" onchange=\""+searchFF+"(this.value)\">";
    for(var j = 1;j<= pageObj.pagesAvailable ; j++){
      str = str + "<option value=\""+j+"\""+(j == pageObj.pageNumber ? "selected" : "")+" >"+j+"</option>";
    } 
    str = str+"</select>&nbsp;</span>"; 
    pageTd.innerHTML =str;
}
/**
 * 是否有按钮权限
 */
function hasButtonAuth(codes){
	var payways=document.getElementsByName("payway");
	if(codes.length=payways.length){
		MerchantService.hasButtonAuth(codes,function(data){
			for ( var i = 0; i < data.length; i++) {
				if(data[i]){
					payways[i].style.display="";
				}
			}
		});
		
	}
}
function formatDate(date){
	var dates="";
	if(date!=null&&date!=undefined){
		dates=date.toString();
	}
	if(dates.length==8){
	  var year = dates.substring(0, 4);
	  var month = dates.substring(4, 6);
	  var day = dates.substring(6,8);
	  return year+"-"+month+"-"+day;
	}
	return "";
}

function formatDate1(date){
	var dates="";
	if(date!=null&&date!=undefined){
		dates=date.toString();
	}
	if(dates.length==14){
	  var year = dates.substring(0, 4);
	  var month = dates.substring(4, 6);
	  var day = dates.substring(6,8);
	  var hour = dates.substring(8,10);
	  var min = dates.substring(10,12);
	  var sec = dates.substring(12,14);
	  return year+"-"+month+"-"+day+" "+hour+":"+min+":"+sec;
	}
	return "";
}
