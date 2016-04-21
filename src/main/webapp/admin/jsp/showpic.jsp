<%@ page language="java" import="java.util.*,java.io.*" 
    contentType="image/jpg;charset=gb2312"%>
    
   <%

    String path = (String)(request.getParameter("path"));
    response.reset();
        System.out.println("path:"+path);
       // FileOutputStream output=new FileOutputStream("D:/a.jpg",false);
    ServletOutputStream output = response.getOutputStream();
    InputStream in = new FileInputStream(path);
    byte tmp[] = new byte[1024*1024];

    int i=0;
    while ((i = in.read(tmp)) != -1) {
        output.write(tmp, 0, i);
    }
    in.close();
    output.flush(); //强制清出缓冲区                
    output.close();
   
%> 

