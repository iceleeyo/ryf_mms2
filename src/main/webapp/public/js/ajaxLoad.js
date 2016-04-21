//图片加载中...    
function useLoadingImage(imageSrc,top,right) { 
	  var loadingImage; 
	  if (top) loadTop = top;
	  else loadTop = "350px";
	  var loadRight;
	  if (right) loadRight = right;
	  else loadRight = "600px";
	  
	  if (imageSrc) loadingImage = imageSrc;     
	  else loadingImage = "ajax-loader.gif";     
	  dwr.engine.setPreHook(function() {     
	    var disabledImageZone = $('disabledImageZone');     
	    if (!disabledImageZone) { 
		    disabledImageZone = document.createElement('div');
		    	disabledImageZone.setAttribute('id', 'disabledImageZone');
	    	var imgBgDiv = document.createElement('div');
	    		imgBgDiv.setAttribute('id', 'imgBgDiv');
	    		imgBgDiv.style.cursor='wait';
		    var imageZone = document.createElement('img');
		    	imageZone.setAttribute('id', 'imageZone');
	  	        imageZone.setAttribute('src',imageSrc);
	  	        imageZone.style.top =loadTop;     
	  	        imageZone.style.right =loadRight;
	  	    disabledImageZone.appendChild(imgBgDiv);
	  	    disabledImageZone.appendChild(imageZone);
	  	    document.body.appendChild(disabledImageZone);
	    }     
	    else {     
	      $('imageZone').src = imageSrc;     
	      disabledImageZone.style.visibility = 'visible';     
	    }     
	  });     
	  dwr.engine.setPostHook(function() {     
	    $('disabledImageZone').style.visibility = 'hidden';     
	  });     
}