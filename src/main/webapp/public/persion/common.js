//************************************************************************
// 用 户 名：上海环迅电子商务有限公司
// 系 统 名：IPS4.0系统
// 子系统名：isp4公共JScript文件 20081120
// 作 成 者：mary.lou
// 改 版 日：11/20/2008
// 改版内容：新建
//************************************************************************


//判断输入参数是否为保留2位小数 mary.lou 20081120
//true匹配成功,false匹配失败
function CheckIsNum2(argValue)
{
    try{
        var strExp=/^(0|[1-9]\d*)(\.\d{1,2})?$/;
        return strExp.test(argValue);
    }catch(e){return false;}
}
//邮政编码判断
function CheckZipCodeOrMobile(argValue,argLen)
{
try{
    if(!CheckIsNum2(argValue)) {return false;}
    if(argValue.length!=argLen){ return false;}
    return true;
    }catch(e){return false;}
}
//判断输入项是否为指定长度的正整数
function CheckIntLen(argValue,argLen)
{
try{
    if(argValue.length!=argLen){return false;}
    var strExp=/^\d*$/;
    return strExp.test(argValue);
}catch(e){return false;}
}
//动态加载js文件 mary.lou 20081120
//argSrc要加载的JS文件,包括路径
function LoadJsFile(argSrc)
{
    try{
        var oHead = document.getElementsByTagName('HEAD').item(0); 
        var oScript= document.createElement("script"); 
        oScript.type = "text/javascript"; 
        oScript.src=argSrc;
        oHead.appendChild( oScript); 
    }catch(e){}
}
//使下拉列表框的某项值选中 mary.lou 20081120
//argObj下拉列表对象,argText将要匹配的Text值
function GetSelectTextChecked(argObj,argText)
{
    try{
        if(argObj==null || argObj==undefined || argObj.options.length<=0){return false;}
        for(var i=0;i<argObj.options.length;i++){
            var outText="";
            if(getBrowserVersion()){outText=argObj.options[i].outerText;}
            else{outText=argObj.options[i].textContent;}
            if(outText==argText){
                argObj.options[i].selected=true;
                break;
            }
        }
    }catch(e){}
}
//弹出ModalPopupExtender对应的信息 mary.lou 20081120
//argMessage要弹出的信息
function PopupMessage(argMessage)
{
//debugger;
    try{
        var Popup_Msg = document.getElementById("Popup_Msg");
        Popup_Msg.innerHTML =SplitErrMessageAboutPopup(argMessage);
        var modalPopupBehavior = $find('programmaticModalPopupBehavior');
        modalPopupBehavior.show();
    }catch(e){}
}
//对弹出的错误信息加以处理,源于测试部非要只显示一条记录.mary.lou 20081223
function SplitErrMessageAboutPopup(argErr)
{
    var strErr=argErr;
    try{
        var arrSource=argErr.split('</br>');
        if(arrSource.length>0){
            if(arrSource[0]!=""){strErr=arrSource[0];}
            else{strErr=arrSource[1];}
        }
    }catch(e){}
    return strErr;
}
//隐藏汽泡式弹出窗口上的关闭按钮 mary.lou 20081204
function HidePopupCloseButton()
{
    var obj=document.getElementById("CancelButton");
    obj.style.display="none";
}
//手动控制弹出ModalPopupExtender的关闭 mary.lou 20081204
//argMessage要弹出的信息
function HidePopupMessage()
{
    try{
        var Popup_Msg = document.getElementById("Popup_Msg");
        Popup_Msg.innerHTML ="";
        var modalPopupBehavior = $find('programmaticModalPopupBehavior');
        modalPopupBehavior.hide();
    }catch(e){}
}
//电话判断函数，允许“数字”、“*”、“-”、“(”、”)“  
//true表示是电话号码  
function CheckIsTel(argValue)
{
    try{
        var trueChar = "()-*1234567890";     
        if(argValue.length<7 || argValue.length>25){     
            return false;     
        }     
        for(var i=0;i<argValue.length;i++){
            var c = argValue.charAt(i);//字符串str中的字符     
            if(trueChar.indexOf(c) == -1) return false;     
        }     
        return true; 
    }catch(e){return false;}
    
}
//Email验证
function CheckEmail(argValue)
{
    try{
        var strExp= /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
        return strExp.test(argValue);
    }catch(e){return false;}
}
//生成Hidden元素用以进行表单提交.
//argName为hidden的name属性值.
//argValue为hidden的value值.
function getElementByHidden(argForm,argName,argValue)
{
    var obj=null;
    try{
        obj=document.createElement("input");
        obj.type="hidden";
        obj.name=argName;
        obj.value=argValue;
        argForm.appendChild(obj);
    }catch(e){}
    return obj;
}
//获取当前的浏览器 mary.lou 20081210
//返回一个对象,然后有若干个属性,以方便以后扩展
function getBrowserVersion()
{
    var Browser={};
    Browser.agt=navigator.userAgent.toLowerCase();
    Browser.isW3C = document.getElementById ? true:false;
    Browser.isIE = ((Browser.agt.indexOf("msie") != -1) && (Browser.agt.indexOf("opera") == -1) && (Browser.agt.indexOf("omniweb") == -1));
    Browser.isNS6 = Browser.isW3C && (navigator.appName=="Netscape") ;
    Browser.isOpera = Browser.agt.indexOf("opera") != -1;
    Browser.isGecko = Browser.agt.indexOf("gecko") != -1;
    return Browser.isIE;
}

// 无提示消息关闭窗口
function IEClose() 
{ 
    var ua = navigator.userAgent;
    var ie = navigator.appName == "Microsoft Internet Explorer" ? true : false;
    if (ie) 
    { 
        var IEversion = parseFloat(ua.substring(ua.indexOf("MSIE ")+5,ua.indexOf(";",ua.indexOf("MSIE ")))) 
        if (IEversion < 5.5) 
        { 
            var str = '<object id=noTipClose classid="clsid:ADB880A6-D8FF-11CF-9377-00AA003B7A11">';
            str += '<param name="Command" value="Close"></object>'; 
            document.body.insertAdjacentHTML("beforeEnd", str); 
            document.all.noTipClose.Click(); 
        } 
        else 
        { 
            window.opener = null; 
            window.open("","_self");
            window.close(); 
        } 
    } 
    else 
    { 
        window.close() 
    } 
}
//去除左右两边空格 mary.lou 20090113
function Trim(argValue)
{
    try{
        return argValue.replace(/^\s*|\s*$/g,"")    
    }
    catch(e){
        return argValue;
    }
}
//当备注内容很多时,点击显示的一个div.为onmouseover事件
function fnDef()
{ 
    var oPopup = window.createPopup();
    oPopup.document.body.innerHTML = "<DIV STYLE=\"background:#ffffcc; border:1px solid black; padding:4px;font-family:verdana; font-size:70%;color=#111; width:250px;word-break:break-all;WORD-WRAP: break-word;filter:progid:DXImageTransform.Microsoft.Gradient(GradientType=0,StartColorStr='#0099FF', EndColorStr='#00FFFF';\" onmouseup='document.execCommand(\"Copy\",\"false\",null);'>"+changeSpHtml(event.srcElement.title)+"</div>";
    var popupBody = oPopup.document.body;
    oPopup.show(0, 0, 0, 0);
    var realHeight = popupBody.scrollHeight;
    oPopup.hide();
    oPopup.show(0, 15 , 250, realHeight, event.srcElement);
}
function changeSpHtml(htmlvalue) 
{ 
    reg1 = /\</g;
    reg2 = /\>/g;
    str=htmlvalue;
    str = htmlvalue.replace(new RegExp("&lt;","gm"),"括号左");
    str = str.replace(new RegExp("&gt;","gm"),"括号右");
    str = str.replace(new RegExp("<","gm"),"&lt;");
    str = str.replace(new RegExp("括号右","gm"),"&amp;gt;");
    str = str.replace(new RegExp("括号左","gm"),"&amp;lt;");
    return str;
}
//分页查询控件AspNetPager在数据源绑定时生成的js,点"go"时会调用,但当GridView放在UpdatePanel中,就无法自动生成.此时手动生成放在页面中
function ANP_checkInput(bid,mv){var el=document.getElementById(bid);var r=new RegExp("^\\s*(\\d+)\\s*$");
if(r.test(el.value)){if(RegExp.$1<1||RegExp.$1>mv){alert("页索引超出范围！");el.focus();el.select();return 

false;}
return true;}alert("页索引不是有效的数值！");el.focus();el.select();return false;}
function ANP_keydown(e,btnId){
var kcode;
if(window.event){kcode=e.keyCode;}
else if(e.which){kcode=e.which;}
var validKey=(kcode==8||kcode==46||kcode==37||kcode==39||(kcode>=48&&kcode<=57)||(kcode>=96&&kcode<=105));
if(!validKey){
if(kcode==13) document.getElementById(btnId).click();
if(e.preventDefault) e.preventDefault();else{event.returnValue=false};
}
}
//动态生成Form元素，并进行Post方式的表单提交.
function getFormElement(argActionurl)
{
//debugger;
    try{
        
        objForm = document.createElement("form");
        document.body.appendChild(objForm);
        if(objForm==undefined || objForm==null) {return false;}
        var keyTest = getElementByHidden(objForm,"keyTest","");
        objForm.action = argActionurl;
        objForm.target = "_blank";
        objForm.method = "post";
        objForm.submit();
        }catch(e){}
}
//绑定select控件的值
//argID 待绑定的select对象的ID
//objJSon Json类型的值
//argHeader 第一项要显示的标识
//argSelectedCode 选中项
//注：cs中要将value,text分别命名:value,text;value,text
function setValueToSelect(argID,objJSon,argHeader,argSelectedCode)
{
//debugger;
try{
        var obj=document.getElementById(argID);
        if (obj==undefined || obj==null  ){return false;}
        obj.options.length=0;
        var index=0;
        if(argHeader!=""){obj[0]=new Option("=="+argHeader+"==","");index=1;}
        var arrData=objJSon.Result.split(';');
        var text="";var id="";
        for(var i=0;i<arrData.length;i++){
            id=arrData[i].split(',')[0];
            text=arrData[i].split(',')[1];
            obj[i+index]=new Option(text,id);
            if(id==argSelectedCode){obj[i+index].selected=true;}
        }
        obj.onchange(); //有时此对象有此事件需要触发
    }catch(e){}
}