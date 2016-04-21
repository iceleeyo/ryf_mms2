 
        function init(){
            $("bdate").value= jsToday();
            $("edate").value= jsToday();
        }

        function queryAccount(pageNo) {
            
            var mid = dwr.util.getValue("mid");

            if(mid=='')  return false;
            var bdate = dwr.util.getValue("bdate");
            var edate = dwr.util.getValue("edate");
            if (bdate == '') {
                alert("请输入查询起始日期");
                return false;
            }
            if (edate == '') {
                alert("请输入查询结束日期");
                return false;
            }
            SettlementService.searchAccount(pageNo,mid,bdate,edate,callBack2);
        }
     var callBack2 = function(pageObj){
        document.getElementById("accountTable").style.display="";
        var cellFuncs = [
                         function(obj) { return obj.mid; },
                         function(obj) { return obj.liqType == 1 ? "全额结算" : "净额结算"; },
                         function(obj) { return obj.date; },
                         function(obj) { return getStringTime(obj.time); },
                         function(obj) { return h_account_type[obj.type]},
                         function(obj) { return obj.tseq},
                         function(obj) { return div100(obj.amount)},
                         function(obj) { return div100(obj.fee)},
                        function(obj) { return div100(obj.account)},
                        function(obj) { return div100(obj.balance)}
                     ]
              paginationTable(pageObj,"resultList",cellFuncs,'',"queryAccount");
       }     