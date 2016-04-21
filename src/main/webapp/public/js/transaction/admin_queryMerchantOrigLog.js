//公共代码

  function init(){
      $("sys_date_begin").value = jsToday();
      $("sys_date_end").value = jsToday();
      initGateChannel();
      dwr.util.addOptions("mstate", m_mstate);
      initMinfos();
  }
  function queryElog(pageNo){
     var mid = $("mid").value;
     var oid = $("oid").value;
     var bdate = $("sys_date_begin").value;
     var edate = $("sys_date_end").value;
     var mstate=$("mstate").value;
     if (bdate=='') {
       alert("请选择系统起始日期!"); 
       return false; 
     }
     if (edate=='') {
       alert("请选择系统结束日期!"); 
       return false; 
     }
     TransactionService.queryMerHlog(pageNo, mid, oid, bdate, edate,mstate,callBack2);
  }
  var callBack2 = function(pageObj){
	  $("elogTable").style.display="";
	  dwr.util.removeAllRows("resultList");
  var cellFuncs = [
                   function(obj) { return obj.mid; },
                   function(obj) { return m_minfos[obj.mid]; },
                   function(obj) { return formatDate(obj.mdate); },
                   function(obj) { return obj.oid; },
                   function(obj) { return div100(obj.amount); },
                   function(obj) { return h_type[obj.type]; },
                   function(obj) { return formatDate(obj.sysDate)+"&nbsp;"+getStringTime(obj.sysTime );},
                   function(obj) { return h_gate[obj.gateId];},
               ];
        paginationTable(pageObj,"resultList",cellFuncs,"","queryElog");
    };
   