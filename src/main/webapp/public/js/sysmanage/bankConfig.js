 var gaterouteCache = {};
  function init(){
	  query4Begin(-1);
  }
  
   function query4Begin(type){
       var typeId = document.getElementById("typeId");
       typeId.value = type;
	   SysManageService.query4BankConfig(type,callback);    
   }
  
  function callback(gateList){
	  dwr.util.removeAllRows("gateRouteTable");
 	if (gateList.length == 0) {
 		var elTr =  $("gateRouteTable").insertRow(-1);
 		var elTd=elTr.insertCell(-1);
 		elTd.setAttribute("colspan",5)
 		elTd.innerHTML="没有符合条件的数据!"
        return;
     }
       // 取得html中表格对象
		var dtable = $("gateRouteTable");
		if (gateList.length > 0) {
			for (var i = 0; i < gateList.length; i++) {
				var gateRoute = gateList[i];

	                // 在table中新建一行
				var elTr = dtable.insertRow(-1);
				//elTr.setAttribute("align","center");
	                // 创建融易通网关列:
				var gateIdTd = elTr.insertCell(-1);
				gateIdTd.innerHTML = gateRoute.gate;
	                // 网关简称
				var gateNameTd = elTr.insertCell(-1);
				gateNameTd.innerHTML = gateRoute.gateDescShort;           
	                // 交易方式
				var transModeTd = elTr.insertCell(-1);
				transModeTd.innerHTML = h_gate_route_type[gateRoute.transMode];
				    // 开关
				var onoroffTd = elTr.insertCell(-1);
				onoroffTd.innerHTML = ( gateRoute.statFlag == 0 ? "正常" : "关闭" );
	                //详细
				var actionTd = elTr.insertCell(-1);
				var editBTN = "<input type=\"button\" value=\"打开配置\" onclick=\"editGate(" + i +");\"/>&nbsp;&nbsp;";				
				editBTN += "<input type=\"button\" value=\"修改\" onclick=\"detailGate(" + i + ");\"/>";			
				actionTd.innerHTML = editBTN;
	                // 放入缓存区备修改时用					
				gaterouteCache[i] = gateRoute;
			}
			
		}
	  }
	  	  
  var f = new GreyFrame('INLINEIFRAME');
	  f.setClassName("MyGreyFrame"); //指定框架的样式名称

	  function editGate(gate){
	    var theGate = gaterouteCache[gate];
	    var rytGate = theGate.gate;
	    SysManageService.getAuthorTypeByGate(rytGate,function(d){
         var authorType = document.getElementById("authorType");
         if(authorType != null){
            for( var i=0; i<d.length; i++ ){
              var theOptionAuthorType = document.createElement("option");
              theOptionAuthorType.innerHTML = h_author_type[d[i]];
              theOptionAuthorType.value = d[i];
               if(theGate.authorType == d[i]){
                  theOptionAuthorType.selected = true;
               }
               authorType.appendChild(theOptionAuthorType);
            }
          }
     });
	    var minfoHtml = "<div style='text-align: center;'><table class='tableBorder' >";
	    minfoHtml += "<tr><td align='center'>使&nbsp;&nbsp;用&nbsp;&nbsp;<select id='authorType'></select>&nbsp;接&nbsp;口&nbsp;</td></tr>";
	    minfoHtml += "<tr><td align='center'colspan='4'><input type='button' value='确定' onclick=\'editBank(" + rytGate+")\'></td></tr></table></div>";
	    f.openHtml('银行网关配置', minfoHtml, 500, 120);
   }
	 
   function editBank(rytGate){
	   var authorType = document.getElementById("authorType").value;
	   SysManageService.configGate(authorType,rytGate,callback4update);
   }
	
   function detailGate(gate){
	     theGate = gaterouteCache[gate];
	     var statFlag = theGate.statFlag;
	     var logoUrl = theGate.logoUrl;
	     var editHtml = "<div style='text-align: center;'><table class='tableBorder' >";
	     editHtml += "<tr><td align='right' width='40%'>开关配置：</td>";
	     editHtml += "<td align='left' with='60%'><select id = 'state'><option value='0'>正常</option><option value='1'>关闭</option></select></td></tr>";
	     editHtml += "<tr><td align='right' width='40%'>银行LOGO URL：</td><td align='left'><input type='text' name='logourl' id='logourl' value='"+logoUrl+"' size='30' maxlength='100'></td></tr>";
	     editHtml += "<tr><td align='center'colspan='2'><input type='button' value='确定' onclick=\'editUrl(" + gate + ")\'></td></tr></table></div>";
	     f.openHtml('银行网关修改', editHtml, 500, 120);
         document.getElementById("state").value = statFlag;
  }
  
  function editUrl(gate){
    theGate = gaterouteCache[gate];
    var statFlag = document.getElementById("state").value;
    var logoUrl = document.getElementById("logourl").value;
    theGate.statFlag =statFlag;
    theGate.logoUrl =logoUrl;
    SysManageService.updateGateUrl(theGate,callback4update);
 }
 function callback4update(msg){
    if(msg=='ok'){
       f.close();
       alert("修改成功！");
       var typeId = $("typeId").value;
       query4Begin(typeId);
    }else{
 	   alert(msg);
    }
 }
	
  function showListDiv(){
	  document.getElementById("chaxun").style.display = '';
	  document.getElementById("body").style.display = '';
   }
	
  function refreshGateList(){
		SysManageService.refreshGateList();
     alert("刷新成功!");
  }     