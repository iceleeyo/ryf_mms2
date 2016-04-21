/**
 * 数字 转换成大写的中文
 * @param numberValue
 * @return
 */
function   atoc(numberValue){  
          var   numberValue=new   String(Math.round(numberValue*100));   //   数字金额  
          var   chineseValue="";                     //   转换后的汉字金额  
          var   String1   =   "零壹贰叁肆伍陆柒捌玖";               //   汉字数字  
          var   String2   =   "万仟佰拾亿仟佰拾万仟佰拾元角分";           //   对应单位  
          var   len=numberValue.length;                   //   numberValue   的字符串长度  
          var   Ch1;                           //   数字的汉语读法  
          var   Ch2;                           //   数字位的汉字读法  
          var   nZero=0;                         //   用来计算连续的零值的个数  
          var   String3;                         //   指定位置的数值  
          if(len>15){  
                  alert("超出计算范围");  
                  return   "";  
          }  
          if   (numberValue==0){  
                  chineseValue   =   "零元整";  
                  return   chineseValue;  
          }  
   
          String2   =   String2.substr(String2.length-len,   len);       //   取出对应位数的STRING2的值  
          for(var   i=0;   i<len;   i++){  
                  String3   =   parseInt(numberValue.substr(i,   1),10);       //   取出需转换的某一位的值  
                  if   (   i   !=   (len   -   3)   &&   i   !=   (len   -   7)   &&   i   !=   (len   -   11)   &&   i   !=(len   -   15)   ){  
                          if   (   String3   ==   0   ){  
                                  Ch1   =   "";  
                                  Ch2   =   "";  
                                  nZero   =   nZero   +   1;  
                          }  
                          else   if   (   String3   !=   0   &&   nZero   !=   0   ){  
                                  Ch1   =   "零"   +   String1.substr(String3,   1);  
                                  Ch2   =   String2.substr(i,   1);  
                                  nZero   =   0;  
                          }  
                          else{  
                                  Ch1   =   String1.substr(String3,   1);  
                                  Ch2   =   String2.substr(i,   1);  
                                  nZero   =   0;  
                          }  
                  }  
                  else{                             //   该位是万亿，亿，万，元位等关键位  
                          if(   String3   !=   0   &&   nZero   !=   0   ){  
                                  Ch1   =   "零"   +   String1.substr(String3,   1);  
                                  Ch2   =   String2.substr(i,   1);  
                                  nZero   =   0;  
                          }  
                          else   if   (   String3   !=   0   &&   nZero   ==   0   ){  
                                  Ch1   =   String1.substr(String3,   1);  
                                  Ch2   =   String2.substr(i,   1);  
                                  nZero   =   0;  
                          }  
                          else   if(   String3   ==   0   &&   nZero   >=   3   ){  
                                  Ch1   =   "";  
                                  Ch2   =   "";  
                                  nZero   =   nZero   +   1;  
                          }  
                          else{  
                                  Ch1   =   "";  
                                  Ch2   =   String2.substr(i,   1);  
                                  nZero   =   nZero   +   1;  
                          }  
                          if(   i   ==   (len   -   11)   ||   i   ==   (len   -   3)){         //   如果该位是亿位或元位，则必须写上  
                                  Ch2   =   String2.substr(i,   1);  
                          }  
                  }  
                  chineseValue   =   chineseValue   +   Ch1   +   Ch2;  
          }  
   
          if   (   String3   ==   0   ){                       //   最后一位（分）为0时，加上“整”  
                  chineseValue   =   chineseValue   +   "整";  
          }  
   
          return   chineseValue;  
  }   
/**
 * 银行账户 每6位 用空格隔开
 * @param accNo
 * @return
 */
function subAccNo(accNo){
	 if(accNo.length<=12){
		 throw new Error("账户长度错误！");
		 return;
	 }
	var acc1= accNo.substring(0,6);
	var acc2= accNo.substring(6,12);
	var accEnd= accNo.substring(12,accNo.length);
	 return acc1+" "+acc2+" "+accEnd
}
/**
 * 金额 自动加逗号
 * @param num
 * @return
 */
function formatNumber(num){ 
	if(!/^(\+|-)?(\d+)(\.\d+)?$/.test(num)){return num;} 
	var a = RegExp.$1, b = RegExp.$2, c = RegExp.$3; 
	var re = new RegExp().compile("(\\d)(\\d{3})(,|$)"); 
	while(re.test(b)) b = b.replace(re,"$1,$2$3"); 
	return a +""+ b +""+ c; 
}