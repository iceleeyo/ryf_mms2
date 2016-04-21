 document.write("<script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>");
 document.write("<script type='text/javascript' src='../../public/js/ryt.js'></script>");
 function init(){
 	PageService.getGatesMap(function(map){
		h_gate = map;
		dwr.util.addOptions("gate", h_gate);
	});
     //统计周期      
     var b = [{name:'day',text:'按天统计'},{name:'month',text:'按月统计'},{name:'season',text:'按季度统计'},{name:'year',text:'按年统计'}];
     dwr.util.removeAllOptions("timetype");
     dwr.util.addOptions("timetype", b,'name','text',{escapeHtml:false});
 }

 function queryMerCollect(pageNo){
 	var mid = $("mid").value;
 	if(mid=='') return;
     var date_b = $("bdate").value;
     var date_e = $("edate").value; 
     if (date_b=='') {
        alert("请选择系统起始日期!"); 
        return false; 
     }
     if (date_e=='') {
         alert("请选择系统结束日期!"); 
         return false; 
      }
     var p = {};
     p.mid = mid;
     p.gate = $("gate").value;
     p.bdate = date_b;
     p.edate = date_e;
     p.timetype = $("timetype").value;
     
     PageService.paginationQuery(pageNo,'MERCOLLECT',p,callBack2)
     
 }
 var callBack2 = function(pageObj){
	 var cellFuncs = [
	                  function(obj) { 
	                 	 if($("timetype").value == 'season' ){
	                 		 return (obj.sysDate+"").substring(0,4)+ "第" + (obj.sysDate+"").substring(4,5)+"季度";
	                        }else{
	                     	   return obj.sysDate;
	                        }
	                  },
	                  function(obj) { return obj.mid; },
	                  function(obj) { return div100( (obj.paySucAmt - obj.cnlSucAmt) ); },
	                  function(obj) { return (obj.paySucCnt + obj.payFaiCnt); },
	                  function(obj) { return obj.paySucCnt;},
	                  function(obj) { return obj.payFaiCnt; },
	                  function(obj) { return div100(obj.paySucAmt); },
	                  function(obj) { return div100(obj.payFaiAmt); },
	                  function(obj) { return obj.cnlFaiCnt; },
	                  function(obj) { return div100(obj.cnlSucAmt);}
	              ]
 	$("merCollectTable").style.display="";
       paginationTable(pageObj,"resultList",cellFuncs,'',"queryMerCollect");
   }

