//订单导入 
function importOrder(){
    var filePath = dwr.util.getValue('file');
    var fileName = jQuery("#file").val();
    var type = jQuery("#type").val();
//    var appendix = fileName.substring(fileName.lastIndexOf("."));
    if (fileName == '') {
        alert("请选择上传文件!");
        return ;
    //}else if((appendix!=".csv"&&type != 3)||(appendix!=".txt"&&type == 3)){
    }else if(chkAppendix(fileName)==false){
	   	alert("文件格式错误!仅支持txt、csv、xls、xlsx");
	   	return;
    }
    UploadHlogService.uploadHlog(filePath,fileName,type,function(msg){
    	alert(msg);
    });
  }

//验证上送文件格式
function chkAppendix(appendix){
    var reg=/(.txt|.csv|.xls|.xlsx)$/;
    return reg.test(appendix);
}