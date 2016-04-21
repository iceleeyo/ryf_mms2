<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<div> 
      <div id="table3" style="display: none;width: 300px;height: 140px;"> 
       <table class="tableBorder" >
				<tbody>
					<tr>
						<td class="th1" align="right" width="30%">
							&nbsp; 交易金额：
						</td>
						<td width="70%" align="left">
							<span id="sumAmt"></span>
							<input type="hidden" id="hidden_type"  value=""/>
						</td></tr>
						<tr>
						<td class="th1" align="right" width="30%">
							&nbsp; 手续费：
						</td>
						<td width="70%" align="left">
							<span id="fee_amt"></span>
							<input type="hidden" id="hidden_type"  value=""/>
						</td></tr>
						 <tr>
			                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">开户银行：</td>
			                <td align="left"  >
			             	<span id="bankName"></span>&nbsp;&nbsp;
			             	 </td> 
			            </tr>
			            <tr>
			                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">银行账户：</td>
			                <td align="left"  >
			             	 <span id="bankNo"></span>&nbsp;&nbsp;
			             	 </td> 
			            </tr>
						<tr>
						<td class="th1" align="right" width="30%">
							&nbsp; 密码：
						</td>
						<td width="70%" align="left">
							<input type="password" id="pwd"  size="20"/>
							<input type="hidden" id="hidden_type"  value=""/>
						</td>
						
					</tr>
					<tr>
					    
						<td colspan="2" align="center">
							<input type="button" value="确  定" onclick="editTX();" class="button"/>&nbsp;&nbsp;
							<input type="button" value="返 回" onclick="fanhui();"  class="wBox_close button"/>
						</td>
					</tr>
				</tbody>
			</table> </div></div>
</body>
</html>