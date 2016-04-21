// 引入jQuery，转成$
(function($) {
	$(function() {
		// ztree的使用参照http://www.ztree.me/v3/api.php
		// var zTreeObj;
		var allAuthStr = "30,";
		setting = {
			check : {
				chkStyle : "checkbox",
				enable : true
			}
		};

		$("#queryMenuAuthButtns").click(
				function() {// 点击查询事件
					$("#authTable").show();
					$("#oper_auth").show();
					MerchantService.searchOperAuths($("#shmid").val(), $(
							"#mid_operid").val(), function(menuList) {
						allnodes(1, menuList);
						$.fn.zTree.init($("#tree"), setting, menuList);
					});
				});
		$("#confirmButtons").click(
				function() {// 点击确认分配事件
					var treeObj = $.fn.zTree.getZTreeObj("tree");
					var nodes = treeObj.getCheckedNodes(true);

					var menu = getModifyAuthStr("", nodes);
					if (menu == "") {
						var sure = confirm("没有分配权限是否继续？");
						if (sure == false) {
							return false;
						}
					}
					var operid = $("#mid_operid").val();
					var mids = $("#shmid").val();
					MerchantService.addMerMenus(allAuthStr, mids, operid, menu,
							function(msg) {
								alert(msg);
							});
				});

		// 组装权限id
		function getModifyAuthStr(authStr, nodes) {
			for ( var index in nodes) {
				var obj = nodes[index];
				authStr += obj.id + ",";
				if (obj.children != null && obj.children != undefined) {
					getModifyAuthStr(obj.children);
				}
			}
			return authStr;
		}
		function allnodes(flag, btBeanList) {
			if (flag == 1) {
				for ( var int = 0; int < btBeanList[0].children.length; int++) {
					allAuthStr = allAuthStr + btBeanList[0].children[int].id
							+ ",";
				}
			}
			if (flag == 2) {
				for ( var i = 0; i < btBeanList.length; i++) {
					allAuthStr = allAuthStr + btBeanList[i].id + ",";
				}
			}

		}

	});
})(jQuery);

// 商户权限按钮的
function enableButtons() {

	if ($("mid_operid").value == "") {
		$("queryMenuAuthButtns").disabled = "disabled";
		return false;
	}
	$("queryMenuAuthButtns").disabled = "";
	$("minfo_abbrev").innerHTML = $("shmid").value;
	$("oper_name").innerHTML = $("mid_operid").value;
}
function checkInputThis(obj) {
	if (obj && !isFigure(obj.value)) {
		obj.value = '';
	} else {
		if ($("shmid").value == 1) {
			$("queryMenuAuthButtns").disabled = "disabled";
			dwr.util.removeAllOptions("mid_operid");
			dwr.util.addOptions("mid_operid", {
				"" : "请选择..."
			});
			$("authTable").style.display = "none";
			$("oper_auth").style.display = "none";
			return false;
		}
		allopermap(obj.value);
	}
}
function allopermap(mid) {
	MerchantService.getAllopersMap(mid, function(dataMap) {
		dwr.util.removeAllOptions("mid_operid");
		dwr.util.addOptions("mid_operid", {
			"" : "请选择..."
		});
		dwr.util.addOptions("mid_operid", dataMap);
	});
}