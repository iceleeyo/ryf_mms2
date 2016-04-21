//根据选择的商户显示他的操作员
 		function showOper(){
 	       MerMerchantService.showOPers(showOperCallback);
 	   }
 	    var opermap=null;
 		function showOperCallback(dataMap){
 				opermap=dataMap;
 				$("queryMenuAuthButtn").disabled="none";//按钮失效	
 				 dwr.util.removeAllOptions("oper_id");
 				 dwr.util.addOptions("oper_id",{"":"请选择..."});
 			     dwr.util.addOptions("oper_id",dataMap);
 			
 		}
 		function enableButton(){
 					$("queryMenuAuthButtn").disabled = "";
 					if($("perButtonID"))$("perButtonID").disabled = "";
 					//$("minfo_abbrev").innerHTML=$("mid").value;
 					$("oper_name").innerHTML=opermap[$("oper_id").value];	
 		}
 		function validatChoose(){
 			var operId=$("oper_id").value;
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
 		var zTreeObj;
 		setting = {
 			check: {
	 			chkStyle:"checkbox",
	 			enable: true
 			}
 		};
 		
 		$("#queryMenuAuthButtn").click(function (){//点击查询事件	
			$("#authTable").show();
			$("#oper_auth").show();
			 if(!validatChoose())return;
			 MerMerchantService.searchOperAuth($("#oper_id").val(),function(menuList){
 				var zTreeObj1 = $.fn.zTree.init($("#tree"), setting, menuList);
 	 	 	 });
 		});
 		$("#confirmButton").click(function(){//点击确认分配事件
 			 if(!validatChoose())return;
 	 		var treeObj = $.fn.zTree.getZTreeObj("tree");
 	 		var nodes = treeObj.getCheckedNodes(true);
 	 		var nodes_nc = treeObj.getCheckedNodes(false);
 	 		var menu_nc=getModifyAuthStr("",nodes_nc);
 	 		var menu=getModifyAuthStr("",nodes);
 			if (menu == ""){
 		    	var sure = confirm("没有分配权限是否继续？");
 				if (sure == false) {return false;}
 		    }
 			var operid=$("#oper_id").val();
 			MerMerchantService.addMenu(operid,menu,menu_nc,function(msg){alert(msg);});
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
