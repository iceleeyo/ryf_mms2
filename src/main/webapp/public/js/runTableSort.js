jQuery.noConflict();//$的控制权过渡给dwr，下面又在内部使用
(function($) { 
/** 表格排序：表格class=tablelist且有thead tr的表格排序，使用参见TableSort.js说明*********/
	$(function(){
		$(".tablelist thead tr").attr("role","head");
		$(".tablelist thead tr").children().not($("th[sort=false]")).attr({sort:"true",title:"点击可以进行排序！"});
		$(".tablelist").sorttable({ 
		            ascImgUrl: "../../public/images/bullet_arrow_up.png",
		            descImgUrl: "../../public/images/bullet_arrow_down.png",
		            ascImgSize: "8px",
		            descImgSize: "8px"
		});
/******* tip使用:<a tip='XXXXXXXXX' class='showTip'>xxx....</a> ****/
		$("body").append("<span id='tipDiv'></span>");
	    $(".showTip").live("mouseover",function(event){
	    	  var tip=$(this).attr("tip");
			   $("#tipDiv").text(tip)
			   $("#tipDiv").show();
			   
	    	var offset = $(this).offset();
	    	var position = $(this).position();
	    	 var clientWidth=document.body.clientWidth;
	    	 var objOffsetWidth=document.getElementById("tipDiv").offsetWidth;
			   if(event.clientX+objOffsetWidth>clientWidth){
				   $("#tipDiv").css({"left":offset.left-objOffsetWidth,"top":offset.top+20});
			   }else{
				   $("#tipDiv").css({"left":offset.left,"top":offset.top+20});
			   }
	    	/*
		      var tip=$(this).attr("tip");
		      var clientWidth=document.body.clientWidth;
		      var docWidth= document.documentElement.clientWidth;
			   $("#tipDiv").text(tip)
			   $("#tipDiv").show();
			   var objOffsetWidth=document.getElementById("tipDiv").offsetWidth;
			   if(event.clientX+objOffsetWidth>clientWidth){
				   $("#tipDiv").css({"left":event.clientX-objOffsetWidth,"top":event.clientY});
			   }else{
				   $("#tipDiv").css({"left":event.clientX,"top":event.clientY});
			   }
			   */
		}).live("mouseout",function(){
			$("#tipDiv").hide();
		}).live("click",function(){ 
			  var isSuccess=window.clipboardData.setData("Text",$(this).attr("tip"));
			  isSuccess?alert("复制成功！"):alert("复制失败！");
		  });
/*******
	    $(".tablelist").movedTh();
**/
   });
})(jQuery);