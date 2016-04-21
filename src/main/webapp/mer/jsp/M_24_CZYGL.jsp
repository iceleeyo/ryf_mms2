<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>操作员管理</title>
             <%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta http-equiv="cache-control" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
    	<script type='text/javascript' src='../../dwr/engine.js'></script>
	    <script type='text/javascript' src='../../dwr/util.js'></script>
	    <script type='text/javascript' src='../../dwr/interface/PageService.js?<%=rand%>'></script>
	    <script type='text/javascript' src='../../dwr/interface/MerMerchantService.js?<%=rand%>'></script>
	    <script type="text/javascript" src='../../public/js/ryt.js?<%=rand%>'></script>
	    <script type="text/javascript" src="../../public/js/md5.js"></script>
        <script type="text/javascript">
        function doAdd(mid,type){
            var mid = $("mid").value;
            if(!mid) return;
            var oper_id = $("Aoper_id").value;
            var oper_name = $("Aoper_name").value;
            var oper_tel = $("Aoper_tel").value;
            var oper_email = $("Aoper_email").value;
            var state = $("Astate").value;
            var oper_pass = $("Aoper_pass").value;
            var v_oper_pass = $("Av_oper_pass").value;
            if (checkMsg(oper_id,oper_name,oper_tel,oper_email,oper_pass,v_oper_pass)){
                 MerMerchantService.addOperInfo('minfo',state, oper_email, oper_tel, oper_name, hex_md5(oper_pass),oper_id, mid,mid,addback);
             } 
        }
        
        function addback(msg) {
            if(msg=='ok'){
                alert("增加成功！");
                init();
            }else if(msg=='false'){
                alert("操作员号已被占用，请重新输入");
            }else {
                alert (msg);        
            }
        }
        function editback(msg) {
            if(msg=='ok'){
                alert("修改成功！");
                jQuery("#wBox_close").click();
                init();
            }else{
                alert(msg);
            }
        }
        function doDel(i){
            if (confirm("确认删除此操作员！")){
            	var obj = m_opers[i];
                MerMerchantService.deleteOperInfo(obj.mid,obj.operId,callback);
             } 
        }
        function callback(msg){
            if(msg=="ok"){
                alert("删除成功！");
                init();
            }else{
                alert(msg);
            }
        }
        var m_opers = {};
        function init(){
        	$("edit_table_id").style.display = "none";
            var mid = $("mid").value;
            if(!mid || mid == '' ) return;
            MerMerchantService.getOpers4Object( mid,null,1,callBack2);
        }
        function callBack2(pageObj){

            dwr.util.removeAllRows("resultList");
            var i=0;
              	   	var cellFuncs = [
		                     function(obj) { return obj.mid; },
		                     function(obj) { return obj.operId; },     
		                     function(obj) { return obj.operName; },
		                     function(obj) { return obj.operTel; },
		                     function(obj) { return obj.operEmail; },
		                     function(obj) { return obj.regDate; },
		                     function(obj) { return  obj.state==0 ? '正常' : '关闭'; },
		                     function(obj) {
		                     i++; 
		                     m_opers[i]=obj; 
		                     var str="<input type=\"button\" value=\"修改\" onclick=\"edit("+i+")\">";
		                    // + "<input type=\"button\" value=\"删除\" onclick=\"doDel("+i+")\">";
		                     return str;}
                          ]
             paginationTable(pageObj,"resultList",cellFuncs,"","init");
        }

        function edit(i){
           // $("edit_table_id").style.display = "";
            var obj = m_opers[i];
            dwr.util.setValues({
            	v_operId:obj.operId,v_operName:obj.operName,
                v_operTel:obj.operTel,v_operEmail:obj.operEmail,
                v_state:obj.state
            });
          jQuery("#edit_table_id").wBox({title:"操作员信息修改",show:true});//显示box
        }

        function notEdit(){
             dwr.util.setValues({
                 v_operId:'',v_operName:'',v_operTel:'',v_operEmail:'', v_state:''
             });
             $("edit_table_id").style.display = "none";
            }

       function doEdit(){
	        var mid = $("mid").value;
	        if(!mid) return;
	        var oper_id = $("v_operId").value;
	        if(oper_id=='') return;
	        var oper_name = $("v_operName").value;
	        var oper_tel = $("v_operTel").value;
	        var oper_email = $("v_operEmail").value;
	        var state = $("v_state").value;
	        if (oper_name == '') {
	            alert("请填写操作员姓名");
	            return false;
	        }
	        if (oper_name.length > 20) {alert("操作员姓名不能超过20个字！");return false;}
	        if (oper_tel.length > 40) {alert("电话号码不能超过40位！");return false;}
	        if (oper_email.length > 40) {alert("Email地址长度不能超过40位！");return false;}
	        if (oper_email != '' && !isEmail(oper_email)) {alert("请输入正确的Email地址！"); return false;}
	        if (!isFigure(oper_tel)) {alert("电话号码只能是数字！"); return false;}
            MerMerchantService.editOperInfo(state, oper_email, oper_tel ,oper_name , oper_id ,mid,0,editback);
 
        }
function checkMsg (oper_id,oper_name,oper_tel,oper_email,oper_pass,v_oper_pass) {
	if (oper_name == '') {
		alert("请填写操作员姓名");
		return false;
	}
	if (oper_id == '') {
		alert("请填写操作员号！");
		return false;
	}
	if (!isNumber(oper_id)) {
		alert("操作员号请用数字！");
		return false;
	}
	if (oper_name.length > 20) {alert("操作员姓名不能超过20个字！");return false;}
	if (oper_tel.length > 40) {alert("电话号码不能超过40位！");return false;}
	if (oper_email.length > 40) {alert("Email地址长度不能超过40位！");return false;}
	if (oper_email != '' && !isEmail(oper_email)) {alert("请输入正确的Email地址！"); return false;}
	if (!isFigure(oper_tel)) {alert("电话号码只能是数字！"); return false;}
	if (oper_pass != null || v_oper_pass != null){
		if (oper_pass.trim() == '') {
			alert("密码不能为空或空格！");
			return false;
		}
		if (v_oper_pass == '') {
			alert("请确定密码！");
			return false;
		}
		if (oper_pass.length <8|| oper_pass.length > 15) {
			alert("密码为8-15位长度！");
			return false;
		}
		if (oper_pass != v_oper_pass) {
			alert("两次密码不一致！");
			return false;
		}
	}
	return true;
}
        
    </script>
  </head>
  
  <body onload="init();">
  
   <div class="style">
    <table class="tableBorder" >
      <tbody>
          <tr>
              <td class="title" colspan="4" >&nbsp;&nbsp;操作员管理(带<font color="red">*</font>的为必填项)
              </td>
          </tr>
          <tr>
              <td class="th1" align="right" width="20%"> &nbsp;  操作员号：</td>
              <td align="left">&nbsp;
                  <input type="text" id="Aoper_id"   onkeyup="inputFigure(this)"/><font color="red">*</font>
              </td>
              <td class="th1" align="right" width="20%"> &nbsp;  操作员姓名：</td>
              <td align="left">&nbsp;
                  <input type="text" id="Aoper_name"/><font color="red">*</font>
              </td>
          </tr>
          <tr>
          <td class="th1" align="right" width="20%"> &nbsp;密码： </td>
                    <td align="left">&nbsp;
                        <input type="password" id="Aoper_pass" maxlength="15"/>（8-15位长度）<font color="red">*</font>
                    </td>
                    <td class="th1" align="right" width="20%"> &nbsp;
                                                     密码确定：
                    </td>
                    <td align="left">&nbsp;
                        <input type="password" id="Av_oper_pass" maxlength="15"/>（8-15位长度）<font color="red">*</font>
                    </td>
                </tr>
                <tr>
                    <td class="th1" align="right" width="20%"> &nbsp;
                                                     操作员电话：
              </td>
                  <td align="left">&nbsp;
                      <input type="text" id="Aoper_tel" maxlength="20" style="width: 200px"/>
                  </td>
                  <td class="th1" align="right" width="20%"> &nbsp;
                                                   操作员Email：
                  </td>
                  <td align="left">&nbsp;
                      <input type="text" id="Aoper_email" maxlength="40" style="width: 300px"/>
                  </td>
              </tr>
              <tr>
                  <td class="th1" align="right" width="20%"> &nbsp;状态标志：</td>
                  <td align="left" colspan="3">&nbsp;
                      <select id="Astate" style="width: 80px">
                        <option value="0" >正常</option>
                        <option value="1" >关闭</option>                          
                      </select>
                        </td>
                        </tr>
                        <tr> 
                        <td colspan="4" height="30px"> 
                           <input type="hidden" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}"/>
                           <input type="button" style="width: 100px;height: 25px;margin-left: 300px" value="增加" onclick="doAdd()"/>
                        </td>
                    </tr>
                </tbody>
            </table>
            
            
    <table  class="tablelist tablelist2" >
        <thead>
          <tr>
            <th>商户号</th><th>操作员号</th><th>操作员名</th><th>操作员电话</th><th>操作员邮箱</th>
            <th>注册日期</th><th>状态标志</th><th>操作</th>
        </tr>
           </thead>
        
        <tbody id="resultList">
    </tbody>
    </table>
    
    <table class="tableBorder detailBox" style="display:none;height: 200px;width: 700px; " id="edit_table_id">
       <tbody>
           <tr>
           <td class="th1" align="right" > &nbsp;  操作员号：</td>
           <td align="left" >&nbsp;
            <input type="text" id="v_operId" disabled="disabled"/>
           </td>
             </tr>
           <tr>           
           <td class="th1" align="right"> &nbsp;  操作员姓名：</td>
           <td align="left">&nbsp;
               <input type="text" id="v_operName"/><font color="red">*</font>
           </td>
           </tr>
           <tr>
           <td class="th1" align="right"> &nbsp;
                                        操作员电话：
                        </td>
                        <td align="left">&nbsp;
                            <input type="text" id="v_operTel" maxlength="20" />
                        </td>
                  </tr>
           <tr>        
                        
                        <td class="th1" align="right"> &nbsp;
                                                         操作员Email：
                        </td>
                        <td align="left">&nbsp;
                            <input type="text" id="v_operEmail" maxlength="40" />
                        </td>
                    </tr>
                    <tr>
                        <td class="th1" align="right" > &nbsp;状态标志：</td>
                        <td align="left" >&nbsp;
                            <select id="v_state" style="width: 80px">
                              <option value="0" >正常</option>
                              <option value="1" >关闭</option>                          
                            </select>
                        </td>
                        </tr>
                        <tr> 
                        <td colspan="2" height="20px" align="center"> 
                           <input type="button" class="button" value="修改" onclick="doEdit()" />&nbsp;&nbsp;&nbsp;&nbsp;
                        <input type="button" class="wBox_close button" value="取消" />
                       
                        </td>
                    </tr>
                </tbody>
            </table>
   
 </div>
  </body>
</html>
