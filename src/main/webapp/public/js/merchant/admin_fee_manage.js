document.write("<script type='text/javascript' src='../../public/js/ryt.js'></script>"); 
//判断是否输入商户号     
function tset(){
      var mid = document.getElementById("mid").value;
	      if(mid==''){
	         alert("请输入商户号");
	         return false;
	      }
	      if(!isFigure(mid)){
            alert("商户号只能为整数！");
            return false; 
        }      
      }
//选择所有
	function selectall(a,type){
	    var f = document.forms[type+"_form"];
	    if (a == 1){
	        for (i=0;i<f.elements.length;i++){
	            if(f.elements[i].id=="bank_id"){
	             f.elements[i].checked = true;
	            }
	        }
	     }
	      if (a == 2){
	        for (i=0;i<f.elements.length;i++){
	            if(f.elements[i].id=="bank_id"){
	             f.elements[i].checked = false;
	            }
	        }
	     }
	}
    //提交
	function submit(mid,type){
	    if(mid=='') return false;
	    var yID = " ";
	    var nID = " ";
	    var yc = 0;
	    var nc = 0; 
	    var form1 = document.forms["yes_form"];
	    var form2 = document.forms["no_form"];
	    
	    if(undefined != form1){
		    for(var i=0;i<form1.length;i++)  {  
		        if(form1[i].checked   ==   true)  {  
		            yID += form1[i].value+",";
		            yc++;  
		        }  
		    }
	    }
		if(undefined != form2){ 
		    for(var j=0;j<form2.length;j++)  {  
		        if(form2[j].checked == true)  {
		            nID += form2[j].value+",";
		            nc++;  
		        }  
		    }
		}
	    if(yc+nc==0) {
	        alert("请选择要维护的网关");
	        return false;
	    }
	    yID = yID.substring(0,yID.length-1);
	    nID = nID.substring(0,nID.length-1);
	    var model = document.getElementById('base_model').value ; 
	    if(confirm("确定进行手续费维护？")){
	    	MerchantService.addCalcModel(mid,model,yID,nID,function(msg){
	            if(msg=='ok'){
	            	alert("维护成功！");
	                window.location.href="./fee_manage.jsp?search=yes&mid="+mid+"&userAuthIndex=38";
	            }else{
	                 alert("维护失败！");
	            }
	      });
	    }
	   
	}
   function checkForm(){
        var mid = document.getElementById('mid').value.trim();
        if(!isFigure(mid)){
            alert("商户号只能为整数！");
            return false; 
        }
   }

    function show(m){
    
        var obj = document.getElementById("showMethodID"); 
         if(m=="AMT"){
            obj.innerHTML = "公式：AMT*R<br>(固定交易费率，无上下限)其中R代表费率<br> 如费率为0.008<br>则计费公式为AMT*0.008";
            var obj = document.getElementById('base_model');
            if(obj != null ){
            	obj.value='AMT*0.008';
            }
        	return;
        }
        if(m=="MAX"){
            obj.innerHTML = "公式：MAX(X1,AMT*R)<br>(固定交易费率，有下限)其中X1代表下限，R代表费率<br>如费率为0.005，下限为0.2<br>则计费公式为MAX(0.2,AMT*0.005)";
            var obj = document.getElementById('base_model');
            if(obj != null ){
            	obj.value='MAX(0.2,AMT*0.005)';
            }
            return;
        }
        if(m=="MIN"){
            obj.innerHTML = "公式：MIN(X1,AMT*R)<br>(固定交易费率，有上限)其中X1代表上限，R代表费率<br>如费率为0.005，上限为50<br>则计费公式为MIN(50,AMT*0.005)";
        	 var obj = document.getElementById('base_model');
             if(obj != null ){
             	obj.value='MIN(50,AMT*0.005)';
             }
            return;
        }
        if(m=="MTM"){
            obj.innerHTML = "公式：MTM(x1,x2,AMT*R)<br>(固定交易费率，有下限和上限)其中x1,x2分别代表下限和上限，R代表费率<br>如费率为0.006，下限为0.8，上限为500<br>则计费公式为MTM(0.8,500,AMT*0.006)";
        	 var obj = document.getElementById('base_model');
             if(obj != null ){
             	obj.value='MTM(0.8,500,AMT*0.006)';
             }
            return;
        }
        if(m=='SGL'){
            obj.innerHTML = "公式：SGL(X)<br>(单笔固定费用)其中X代表单笔固定手续费<br>如单笔固定为2.5元<br>则计费公式为SGL(2.5)";
        	 var obj = document.getElementById('base_model');
             if(obj != null ){
             	obj.value='SGL(2.5)';
             }
            return;
        }
        if(m=="FLO"){
            obj.innerHTML = "公式：FLO(x1,R1,x2,R2,R3) (单笔浮动费率)其中x1,x2…..代表分界档，R1,R2,R3…..代表分界档内的费率。其中不包含上分界档如: < 500,费率为0.08,500-5000费率为0.06,5000-50000费率为0.05 ,50000-500000费率为0.03,>=5000000,费率为0.01 则计费公式为：FLO(500,0.08,5000,0.06,50000,0.05,500000,0.03,0.01)";
        	 var obj = document.getElementById('base_model');
             if(obj != null ){
             	obj.value='FLO(500,0.08,5000,0.06,50000,0.05,500000,0.03,0.01)';
             }
            return;
        }
        if(m=="FIX"){
            obj.innerHTML = "公式：FIX(x1,R1,x2,R2,R3) (分段固定手续费)其中x1,x2…..代表分界档<br>R1,R2,R3…..代表分界档内的固定手续费。其中不包含上分界档.如计费费率为:<20000,手续费为2,20000-50000,手续费为3,50000-100000,手续费为5,>=100000,费率为10<br>则计费公式为：FIX(20000,2,50000,3,100000,5,10)";
        	 var obj = document.getElementById('base_model');
             if(obj != null ){
             	obj.value='FIX(20000,2,50000,3,100000,5,10)';
             }
            return;
        }
        if(m=="INC"){
            obj.innerHTML = "公式：INC(X,N) <br>(增量计费)最低手续费为X，每增加N的金额，手续费就增加X<br>如：最低手续费为2.5元，交易金额每增加20000元，手续费就增加2.5元<br>则计费公式为INC(2.5，20000)";
        	 var obj = document.getElementById('base_model');
             if(obj != null ){
             	obj.value='INC(2.5,20000)';
             }
            return;
        }
        if(m=="IFBIG"){
           obj.innerHTML = "公式：IFBIG(x1,x2) (最低额计费)x1为最低计费额，x2为其它组合公式<br>只有在该额度以上才能计费，否则为零,在大于该额度以上，计费公式为X2.<br>如: 只有在5000以上才能计费，计费的规则为：每笔费率为0.008<br>则计费公式为IFBIG(5000, AMT*0.08)";
	       	 var obj = document.getElementById('base_model');
	         if(obj != null ){
	         	obj.value='IFBIG(5000, AMT*0.08)';
	         }
            return;
        }
    }
    
    function checkModel(o){
       
        var mobj = document.getElementById('base_model'); 
        if(mobj.value==''){
            alert('请填写手续费公式');
            return false;
        }
        
        var obj = document.getElementById('submitModelID'); 
        var feeModel = mobj.value.trimAll();
        document.getElementById('base_model').value = feeModel;
        //判断公式书写是否正确
        if(checkFeeModel(feeModel)){
            alert("计费公式输入正确!");
            mobj.disabled = true;
            o.value = "重置";
            o.onclick = function(){reset(this);}
            obj.disabled = false;
        }else{
        	alert("计费公式输入有误，请重新输入！");
        }
    } 
     function reset(o){
       o.value = "检查";
       o.onclick = function(){checkModel(this);}
       document.getElementById('base_model').disabled = false; 
       document.getElementById('submitModelID').disabled = true;     
    }    