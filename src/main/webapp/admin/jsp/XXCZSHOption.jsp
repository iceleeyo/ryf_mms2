<%@ page language="java" 
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>线下充值审核意见</title>
<link href="../../public/css/wbox.css" rel="stylesheet" type="text/css"/>
</head>
<body>
 <table id="hlogDetail_TX"  class="tableBorder detailBox4" style="display: none;height:10%">
  <tr>
  <td align="right" class="th1">线下充值审核意见：</td><td><textarea id="option"  style="resize:none"  rows="7" cols="40"></textarea></td>
  </tr>
  <tr><td colspan="2" align="center"><input type="button" class=" button"   value=" 提交 " onclick="solve_xxczsh();">&nbsp;&nbsp;<input type="button" id="back" value="返回"  class="wBox_close button"></td></tr>
       </table>
  	<input type="hidden" id="show_aid">
  	<input type="hidden" id="show_oid">
  	<input type="hidden" id="show_transflow">
  	<input type="hidden" id="flag" >
  	<input type="hidden" id="flag1" >


  </body>
</html>