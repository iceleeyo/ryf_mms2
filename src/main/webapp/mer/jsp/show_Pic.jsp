<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<table id="hlogDetail_PIC"  class="tableBorder detailBox2" style="display: none; ">
<tr><td>凭证：</td><td id="img"><img id="img_pz" width="200" height="200"><div id="img_pz1"></div></td></tr>
<tr><td>审核意见：</td><td><span id="option"></span></td></tr>
<tr><td>交易金额：</td><td><span id="trans_amt1"></span></td></tr>
<tr><td>实际充值金额：</td><td><span id="sj_money"></span></td></tr>
<!-- <tr><td colspan="2"><input type="button" value="返回" onclick="click_fun();" ></td></tr> -->

</table>
<input type="hidden" id="pz_oid">
<input type="hidden" id="pz_transflow">
<input type="hidden" id="pz_ptype">
<input type="hidden" id="pz_tr_amt">
<input type="hidden" id="pz_tr_fee">
<input type="hidden" id="pz_amt">

</body>
</html>