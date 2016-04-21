function doSave(){
	var antiPhishing = $("anti_phishing").value;
	if(antiPhishing != '' && antiPhishing.length > 20){
		alert("防伪信息最长20个字符");
		return;
	}
	
	AntiPhishingService.doSave(antiPhishing, function(msg){
		alert(msg);
	});
}