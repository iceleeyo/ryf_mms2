 
function init(){
	  $("sys_date_begin").value = jsToday();
	  $("sys_date_end").value = jsToday();
  }

  function queryElog(pageNo){
     var mid = $("mid").value;
     if(mid=='') return false;
     var oid = $("oid").value;
     var bdate = $("sys_date_begin").value;
     var edate = $("sys_date_end").value;
     if (bdate=='') {
       alert("请选择系统起始日期!"); 
       return false; 
     }
     if (edate=='') {
       alert("请选择系统结束日期!"); 
       return false; 
     }
     TransactionService.queryMerHlog(pageNo, mid, oid, bdate, edate,callBack2);
     
  }
  var callBack2 = function(pageObj){
	  	  $("elogTable").style.display="";
		  var cellFuncs = [
		                   function(obj) { return obj.mid; },
		                   function(obj) { return obj.mdate; },
		                   function(obj) { return obj.oid; },
		                   function(obj) { return div100(obj.amount); },
		                   function(obj) { return h_type[obj.type]; },
		                   function(obj) { return obj.sysDate+"&nbsp;"+getStringTime(obj.sysTime );}
		               ]
        paginationTable(pageObj,"resultList",cellFuncs,"","queryElog");
    }
  