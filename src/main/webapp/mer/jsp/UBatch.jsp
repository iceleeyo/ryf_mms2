<%@ page language="java" 
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
  	<table id="hlogDetail" class="tableBorder detailBox2" style="display: none;" >
  		<tr>
  	<td  class="th1" >商户号</td>
  	<td  id="uid_"></td>
  	</tr>
  		<tr>
  	<td  class="th1" >批次号</td>
  	<td id="trans_flow_"></td>
  	<input type="hidden" id="uid"/><input type="hidden" id="trans_flow" value=""/><input type="hidden" id="oid"/>
  	</tr>
  	<tr>
  	<td class=th1>充值金额</td>
  	<td><input type="text" id="sj_money_">元</td>
  	</tr>
  	<tr>
  	<td  class="th1" >上传凭证</td>
  	<td id="bathId">
  	</td>
  	</tr>
  	</table>
  	<form action="#" method="post" id="testForm">
  	</form>

  </body>
</html>