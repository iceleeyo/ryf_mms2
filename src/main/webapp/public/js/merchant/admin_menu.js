var flag2=0; //0 为管理员权限分配  判断不能为自己赋权  1 为商户操作员赋权   不做判断
//function showTab(flag){
//	if(flag==1){
//		flag2=0;
//		$("authTable").style.display="none";
//		$("oper_auth").style.display="none";
//		jQuery("#shmid").val("");
//		dwr.util.removeAllOptions("mid_operid");
//		dwr.util.addOptions("mid_operid",{"":"请选择..."});
// 		document.getElementById("confirmButtons").style.display="none";
// 		document.getElementById("confirmButton").style.display="block";
//	}else if(flag==2){
//		//allMids();
//		flag2=1;
//		$("authTable").style.display="none";
//		$("oper_auth").style.display="none";
//		jQuery("#oper_id").val("");
// 		document.getElementById("confirmButton").style.display="none";
// 		document.getElementById("confirmButtons").style.display="block";
//	}
//}
	//所有商户
//	function allMids(){
//		MerchantService.selectallMid(function (data){
//			dwr.util.removeAllOptions("smid");
//			dwr.util.addOptions("smid",{"":"请选择..."});
//			dwr.util.addOptions("smid",data);
//		});
//		
//		
//		
//	}
		//根据选择的商户显示他的操作员
 		function showOper(){
        	var mid = $("mid").value;
    		 if(mid&&!isFigure(mid)){
  	     	   alert("商户号只能是整数!");
  	             return false;
  	         }
    		if(mid){
    			 MerchantService.getImportentMsgByMid(mid, function(minfo) {
    		  	  		if (minfo == null) {
    		  	  			alert("商户不存在!");
    		  	  			dwr.util.removeAllOptions("oper_id");
    		  	  			dwr.util.addOptions("oper_id",{"":"请选择..."});
    		  	  			return false;
    		  	  		}
    		       	     MerchantService.showOPers(mid,"",showOperCallback);
		        	});
    		}else{
  	  			dwr.util.removeAllOptions("oper_id");
  	  			dwr.util.addOptions("oper_id",{"":"请选择..."});
    			MerchantService.showOPers(mid,"",showOperCallback);
    		}
    		if($('queryMenuAuthButtn')){
    			enableButton();
    		}
 	   }
 	    var opermap=null;
 		function showOperCallback(dataMap){
 				opermap=dataMap;
// 				$("queryMenuAuthButtn").disabled="none";//按钮失效	
 				 dwr.util.removeAllOptions("oper_id");
 				 dwr.util.addOptions("oper_id",{"":"请选择..."});
 			     dwr.util.addOptions("oper_id",dataMap);
 		}
 		function enableButton(){
 					var operId = $('oper_id').value;
 					if(operId){
 						$("queryMenuAuthButtn").disabled = "";
	 					if($("perButtonID"))$("perButtonID").disabled = "";
 					}else{
 						$("queryMenuAuthButtn").disabled = "true";
	 					if($("perButtonID"))$("perButtonID").disabled = "true";
 					}
 					$("authTable").style.display='none';
 					$("oper_auth").style.display='none';
					$("tree").innerHTML='';
 					$("buttonAuth_tree").innerHTML='';
 		}
 		//商户权限修改中商户号改变时
 		function midsChange(value){
 			var mid=$("smid").value;
 			if(document.getElementById("shmid")) document.getElementById("shmid").value= value;
 			if(mid==""){
 				dwr.util.removeAllOptions("mid_operid");
 				dwr.util.addOptions("mid_operid",{"":"请选择..."});
 				return;
 			}
 			allopermap(mid);
 			
 		}
 		
 		function validatChoose(){
 			var mid=$("mid").value;
 			var operId=$("oper_id").value;
 			if(mid==""){
 				alert("请选择要查询的商户！");
 				return false;
 			}
 			if(operId==""){
 				alert("请选择要查询的操作员");
 				return false;
 			}
 			return true;
 		}
//引入jQuery，转成$	
(function($){
 	$(function(){
 	   //ztree的使用参照http://www.ztree.me/v3/api.php
// 		var zTreeObj;
 		setting = {
 			check: {
	 			chkStyle:"checkbox",
	 			enable: true
 			}
 		};
 		
 		$("#queryMenuAuthButtn").click(function (){//点击查询事件	
 			 MerchantService.searchOperAuth($("#mid").val(),$("#oper_id").val(),function(menuList){
 				$.fn.zTree.init($("#tree"), setting, menuList);
 	 	 	 });
 	 	 	 MerchantService.searchButtonAuth($("#mid").val(),$("#oper_id").val(),function(btBeanList){
 				$.fn.zTree.init($("#buttonAuth_tree"), setting, btBeanList);
 	 	 	 });
			$("#operId").html($("#oper_id").val());
			$("#oper_name").html(opermap[$("#oper_id").val()]);
			$("#minfo_abbrev").html($("#mid").val());
			$("#authTable").show();
			$("#oper_auth").show();
 		});
 		
 		$("#confirmButton").click(function(){//点击确认分配事件
 			if(!confirm("确认提交申请？")){
 				return;
 			}	
 			var treeObj = $.fn.zTree.getZTreeObj("tree");
 	 		var nodes = treeObj.getCheckedNodes(true);
 	 		var treeObj2 = $.fn.zTree.getZTreeObj("buttonAuth_tree");
 	 		var nodes2 = treeObj2.getCheckedNodes(true);
 	 		var menu=getModifyAuthStr("",nodes);
 	 			menu=getModifyAuthStr(menu,nodes2);
 			if (menu == ""){
 		    	var sure = confirm("没有分配权限是否继续？");
 				if (sure == false) {return false;}
 		    }
 			var operid=$("#oper_id").val();
 			var mid=$("#mid").val();
 		    MerchantService.addMenuApply(mid,operid,menu,function(msg){alert(msg);});
 		});
 		
 		//组装权限id
 		function getModifyAuthStr(authStr,nodes){
 			for(var index in nodes){
 				var obj=nodes[index];
 				authStr+=obj.id+",";
 	 			if(obj.children!=null&&obj.children!=undefined){
 	 				getModifyAuthStr(obj.children);
 	 			}
 			}
 			return authStr;
 		}
 		
 	});
})(jQuery); 

//分页查询申请
	function showOperApply(pageNo){
		$("authTable").style.display = 'none';
		var mid = $("mid").value;
		var operId = $("oper_id").value;
		MerchantService.showOperApply(mid,operId,pageNo,showAppCallBack);
	}
	
	function showAppCallBack(pageObj){ 
	   $("applyTable").style.display="";
	   dwr.util.removeAllRows("resultList");
	   if(pageObj==null){
 	   document.getElementById("resultList").appendChild(creatNoRecordTr(13));
		   return;
	   }  
     var cellFuncs = [
              function(obj) { return obj.mid; },
              function(obj) { return obj.mName; },
              function(obj) { return obj.operId; },
              function(obj) { return obj.operName; },
              function(obj) { return '<a href="#" class="detailsBtn">详细</a>';}
          ];
     	paginationTable(pageObj,"resultList",cellFuncs,"","showOperApply");
	}

function Obj2str(o) {
    if (o == undefined) {
        return "";
    }
    var r = [];
    if (typeof o == "string") return "\"" + o.replace(/([\"\\])/g, "\\$1").replace(/(\n)/g, "\\n").replace(/(\r)/g, "\\r").replace(/(\t)/g, "\\t") + "\"";
    if (typeof o == "object") {
        if (!o.sort) {
            for (var i in o)
                r.push("\"" + i + "\":" + Obj2str(o[i]));
            if (!!document.all && !/^\n?function\s*toString\(\)\s*\{\n?\s*\[native code\]\n?\s*\}\n?\s*$/.test(o.toString)) {
                r.push("toString:" + o.toString.toString());
            }
            r = "{" + r.join() + "}\n";
        } else {
            for (var i = 0; i < o.length; i++)
                r.push(Obj2str(o[i]));
            r = "[" + r.join() + "]\n";
        }
        return r;
    }
    return o.toString().replace(/\"\:/g, '":""');
}
//----------------------
//引入jQuery，转成$	
(function($){
 	$(function(){
 	   //ztree的使用参照http://www.ztree.me/v3/api.php
// 		var zTreeObj;
 		var allAuthStr="30,";
 		setting = {
 			check: {
	 			chkStyle:"checkbox",
	 			enable: true
 			}
 		};
 		//审核权限申请 提交
 		$(".btn").live('click',function(){
 			var choice = this.value;
 			var status = 0;
 			if(choice == "审核通过"){
 				status = 1;
 			}
 			var treeObj = $.fn.zTree.getZTreeObj("tree");
 	 		var nodes = treeObj.getCheckedNodes(true);
 	 		var treeObj2 = $.fn.zTree.getZTreeObj("buttonAuth_tree");
 	 		var nodes2 = treeObj2.getCheckedNodes(true);
 	 		var menu=getModifyAuthStr("",nodes);
 	 			menu=getModifyAuthStr(menu,nodes2);
 			if (menu == ""){
 		    	var sure = confirm("没有分配权限是否继续？");
 				if (sure == false) {return false;}
 		    }

 			var mid = $("#currentMid").val();
 			var operId = $("#currentOperId").val();
 			MerchantService.doCheckAuthApply(menu,mid,operId,status,function(msg){
 				alert(msg);
 			});
 		});
 		//审核权限申请 详情
 		$(".detailsBtn").live('click',function(){
 			var tds = this.parentElement.parentElement.children;
 			var mid = tds[0].innerHTML;
 			var operId = tds[2].innerHTML;
 			var operName = tds[3].innerHTML;
 			$("#currentMid").val(mid);
 			$("#currentOperId").val(operId);
			MerchantService.searchOperAuthsApply(mid,operId,function(menuList){
				allnodes(1,menuList);
				$.fn.zTree.init($("#tree"), setting, menuList);
	 	 	});
	 	 	MerchantService.searchButtonAuthApply(mid,operId,function(btBeanList){
	 	 		allnodes(2,btBeanList);
				$.fn.zTree.init($("#buttonAuth_tree"), setting, btBeanList);
	 	 	});
			$("#operId").html(operId);
			$("#oper_name").html(operName);
			$("#authTable").show();
			$("#oper_auth").show();
 		});
 		
 		$("#queryMenuAuthButtns").click(function (){//点击查询事件	
			 $("#authTable").show();
			 $("#oper_auth").show();
 			 MerchantService.searchOperAuths($("#shmid").val(),$("#mid_operid").val(),function(menuList){
 				allnodes(1,menuList);
 				$.fn.zTree.init($("#tree"), setting, menuList);
 	 	 	 });
 	 	 	  MerchantService.searchButtonAuth($("#shmid").val(),$("#mid_operid").val(),function(btBeanList){
 	 	 		allnodes(2,btBeanList);
 				$.fn.zTree.init($("#buttonAuth_tree"), setting, btBeanList);
 	 	 	 });
 	 	 	
 		});
 		$("#confirmButtons").click(function(){//点击确认分配事件
// 			if(!validatChoose())return;
 	 		var treeObj = $.fn.zTree.getZTreeObj("tree");
 	 		var nodes = treeObj.getCheckedNodes(true);
 	 		
 	 		var treeObj2 = $.fn.zTree.getZTreeObj("buttonAuth_tree");
 	 		var nodes2 = treeObj2.getCheckedNodes(true);
 	 		
 	 		var menu=getModifyAuthStr("",nodes);
 	 		menu=getModifyAuthStr(menu,nodes2);
 			if (menu == ""){
 		    	var sure = confirm("没有分配权限是否继续？");
 				if (sure == false) {return false;}
 		    }
 			var operid=$("#mid_operid").val();
 			var mids=$("#shmid").val();
 		    MerchantService.addMerMenus(allAuthStr,mids,operid,menu,function(msg){alert(msg);});
 		});
 		
 		//组装权限id
 		function getModifyAuthStr(authStr,nodes){
 			for(var index in nodes){
 				var obj=nodes[index];
 				authStr+=obj.id+",";
 	 			if(obj.children!=null&&obj.children!=undefined){
 	 				getModifyAuthStr(obj.children);
 	 			}
 			}
 			return authStr;
 		}
 		function allnodes(flag,btBeanList){
 			if(flag==1){
 				for ( var int = 0; int < btBeanList[0].children.length; int++) {
 	 				allAuthStr=allAuthStr+btBeanList[0].children[int].id+",";
 				}
 			}
 			if(flag==2){
 				for ( var i = 0; i < btBeanList.length; i++) {
 					allAuthStr=allAuthStr+btBeanList[i].id+",";
 				}
 			}
 			
 		}

 	});
})(jQuery);

//商户权限按钮的
	function enableButtons(){
		
		if($("mid_operid").value==""){
			$("queryMenuAuthButtns").disabled = "disabled";
			return false;
		}
		$("queryMenuAuthButtns").disabled = "";
		$("minfo_abbrev").innerHTML=$("shmid").value;
		$("oper_name").innerHTML=$("mid_operid").value;	
	}
	function checkInputThis(obj){
		 if(obj && !isFigure(obj.value)){
		    	obj.value='';
		    }else{
		    	if($("shmid").value==1){
					$("queryMenuAuthButtns").disabled = "disabled";
					dwr.util.removeAllOptions("mid_operid");
					dwr.util.addOptions("mid_operid",{"":"请选择..."});
					$("authTable").style.display="none";
					$("oper_auth").style.display="none";
					return false;
				}
//		    	dwr.util.setValue('smid',obj.value);
		    	allopermap(obj.value);
		    }
		 
	}
	function allopermap(mid){
		MerchantService.getAllopersMap(mid,function (dataMap){
				dwr.util.removeAllOptions("mid_operid");
				dwr.util.addOptions("mid_operid",{"":"请选择..."});
				dwr.util.addOptions("mid_operid",dataMap);
			});
	}