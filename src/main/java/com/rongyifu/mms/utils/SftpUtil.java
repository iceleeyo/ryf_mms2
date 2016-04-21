package com.rongyifu.mms.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
* 利用JSch包实现SFTP下载、上传文件
* @ClassName: FtpUtil
* @Description: TODO(这里用一句话描述这个类的作用)
* @param ip 主机IP
* @param user 主机登陆用户名
* @param psw  主机登陆密码
* @param port 主机ssh2登陆端口，如果取默认值(默认值22)，传-1
* @param privateKey 密钥文件路径
* @param passphrase 密钥的密码
*/ 
public class SftpUtil {
	
	public static void main(String[] args) throws Exception {
		SftpUtil util = null;
		try {
			//util = new SftpUtil("192.168.30.229", "ceb", "sc086k791p", 9822, null,null);
			util = new SftpUtil("192.168.20.47", "sftpzf", null, 9822, "C:/Users/shdy/Desktop/电银/ftp/sftpzf_rsa",null);
			util.connect();
			System.out.println(util.pwd());
			util.cd("sftpsh/nongfengwang");
			//util.createActiveDir("ftp",false);
			
			System.out.println(util.pwd());
			//util.uploadFile("test.txt", "C:/Users/shdy/Desktop/test.txt");
			
			util.downloadFile("test.txt", "d:/qqqq.txt");
		} finally {
			if (util != null) {
				util.close();
			}
		}
	}
	
	private String ip;
	private String user;
	private String psw;
	private int port;
	private String privateKey;
	private String passphrase;
	private Session session;
    private Channel channel;
    private ChannelSftp sftp;
	
    public SftpUtil(String ip, String user, String psw ,int port ,String privateKey ,String passphrase){
		this.ip= ip;
		this.user = user;
		this.psw = psw;
		this.port = port;
		this.privateKey = privateKey;
		this.passphrase = passphrase;
	}
    public String pwd() throws SftpException{
    	return sftp.pwd();
    }
    
    /**
	 * 把指定目录下所有的文件上传到服务器指定路径上（不包含子目录下文件）
	 * 
	 * @param folderName
	 *            文件夹名
	 * @param ftpPath
	 *            远程服务器路径，如路径不存在则会被创建
	 * @return
	 * @throws Exception
	 */
	public boolean uploadAllFilesInFolder(String folderName, String ftpPath)
			throws Exception {
		boolean flag = false;
		if (connect()) {
			File file = new File(folderName);
			if (file.isDirectory()) {
				String currRemontPath = ftpPath + file.getName();
				if (!ftpPath.endsWith("/")) {
					currRemontPath = ftpPath + "/" + file.getName();
				}
				createActiveDir(currRemontPath, true);
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						try {
							sftp.put(files[i].getPath(), files[i].getName());
						} catch (Exception ee) {
							throw new Exception("文件" + files[i].getName()
									+ "上传失败！");
						}
					}
				}
				flag = true;
			} else {
				throw new Exception(folderName + " 不是文件夹!");
			}
		} else {
			throw new Exception("登录服务器失败！");
		}
		return flag;
	}
    
    /**
	 * FTP服务器-创建目录
	 * 
	 * @param remoteDirName
	 *            需要创建的目录名
	 * @param isMultiple
	 *            是否创建多级目录
	 * @return
	 * @throws Exception
	 */
	public boolean createActiveDir(String remoteDirName, boolean isMultiple)
			throws Exception {
		try {
			String[] dirs = remoteDirName.split("/");
			// 根据不同的分隔符解析目录路径
			if (dirs == null || dirs.length == 0) {
				dirs = remoteDirName.split("//");
			}
			if (dirs == null || dirs.length == 0) {
				return false;
			}
			// 循环创建路径中包含的目录
			if (isMultiple) {
				String currentDir = "";
				for (int i = 0; i < dirs.length; i++) {
					try {
						if (dirs[i].indexOf(":") < 0 && !"".equals(dirs[i])
								&& !checkFolderIsExistAndChangeDir(currentDir + dirs[i] + "/")) {
//							if (!currentDir.equals("")) {
//								sftp.cd(currentDir);
//							}
							sftp.mkdir(dirs[i]);
							sftp.cd(dirs[i]);
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new Exception("创建文件夹失败！");
					}
					currentDir += dirs[i] + "/";
				}
			} else {
				if (!checkFolderIsExistAndChangeDir(remoteDirName)) {
					sftp.mkdir(dirs[dirs.length - 1]);
					sftp.cd(dirs[dirs.length - 1]);
				}
			}
		} catch (Exception e) {
			throw new Exception("创建文件夹失败！");
		}
		return true;
	}
	
	/**
	* 删除文件或文件夹
	* @param fileName 当前目录的文件或文件夹的名称
	*/ 
	public boolean remove(String fileName) throws Exception{
		if (!isConnected()) {
			return false;
		}
		if(!isDir(fileName)){
			sftp.rm(fileName);//如果是文件 直接删除
			System.out.println("remove file: " + fileName + ".");
		}else{
			System.out.println(fileName + " is a directory.");
			Vector<LsEntry> v = lsEntry(fileName);
			sftp.cd(fileName);
			System.out.println("enter dir " + fileName + ".");
			for (LsEntry file : v) {
				if(!isDir(file.getFilename())) {
					sftp.rm(file.getFilename());
					System.out.println("remove file: " + file.getFilename() + ".");
				} else {
					remove(file.getFilename());
				}
			}
			sftp.cd("..");
			sftp.rmdir(fileName);
			System.out.println("remove empty dir: " + fileName + ".");
		}
		return true;
	}
    
    /**
    * @param fileName
    * 判断fileName 名称对应的是不是一个目录
     * @throws Exception 
    */ 
    public boolean isDir(String fileName){
    	boolean flag = false;
    	try {
			if(!isConnected()){
				return flag;
			}
			sftp.cd(fileName);
			flag = true;
			sftp.cd("..");//返回上级别目录
		} catch (Exception e) {
		}
    	return flag;
    }
	
	private boolean isConnected() throws Exception{
		if (!sftp.isConnected()) {
			if(!connect()){
				return false;
			}
		}
		return true;
	}

	/**
	 * 检查FTP服务器上是否存在指定的文件夹并跳转到指定目录
	 * 
	 * @param remoteDirName
	 *            远程目录名
	 * @return boolean
	 * @throws Exception
	 * 
	 */
	public boolean checkFolderIsExistAndChangeDir(String remoteDirName)
			throws Exception {
		boolean result = false;
		try {
			if (remoteDirName.endsWith("/")) {
				remoteDirName = remoteDirName.substring(0,
						remoteDirName.length() - 1);
			}
			sftp.cd(remoteDirName);
			result = true;
		} catch (Exception e) {
			return result;
		}
		return result;
	}
    
    public String cd(String dir) throws SftpException{
    	sftp.cd(dir);
    	return sftp.pwd();
    }
    
    public void ls(String path) {
    	try {
    		Vector v = sftp.ls(path);
	        for(int i=0;i<v.size();i++){
	           LsEntry entry = (LsEntry) v.get(i);
	           if (!".".equals(entry.getFilename()) && !"..".equals(entry.getFilename())) {
				System.out.println(entry.getLongname());
			}
	        }
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
    }
    
    public Vector<LsEntry> lsEntry(String path) {
    	Vector<LsEntry> vct = new Vector<LsEntry>();
    	try {
//        	ChannelSftp.LsEntry lsEntry = (LsEntry) v.get(i);
    		Vector v = sftp.ls(path);
    		for (Object obj : v) {
				LsEntry entry = (LsEntry) obj;
				if(!".".equals(entry.getFilename()) && !"..".equals(entry.getFilename())){
					vct.add(entry);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new Vector<LsEntry>();
		}
    	return vct;
    }
    
    public boolean uploadFile(String remoteFile,File localFile) throws SftpException, IOException{
    	boolean flag = false;
	    OutputStream outstream = null;
	    InputStream instream =null;
    	try {
			//以下代码实现从本地上传一个文件到服务器，如果要实现下载，对换以下流就可以了
			outstream = sftp.put(remoteFile);
			instream = new FileInputStream(localFile);
			byte b[] = new byte[1024];
			int n;
			while ((n = instream.read(b)) != -1) {
				outstream.write(b, 0, n);
			}
			outstream.flush();
			flag = true;
		} finally{
			outstream.close();
			instream.close();
		}
    	return flag;
    }
    
    public boolean uploadFile(String remoteFile,String localFile) throws SftpException, IOException{
    	boolean flag = false;
	    OutputStream outstream = null;
	    InputStream instream =null;
    	try {
			//以下代码实现从本地上传一个文件到服务器，如果要实现下载，对换以下流就可以了
			outstream = sftp.put(remoteFile);
			instream = new FileInputStream(new File(localFile));
			byte b[] = new byte[1024];
			int n;
			while ((n = instream.read(b)) != -1) {
				outstream.write(b, 0, n);
			}
			outstream.flush();
			flag = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			outstream.close();
			instream.close();
		}
    	return flag;
    }
    
    public boolean downloadFile(String remoteFile,String localFile) throws SftpException, IOException{
    	boolean flag = false;
	    OutputStream outstream = null;
	    InputStream instream =null;
    	try {
			//以下代码实现从服务器下载一个文件，如果要实现上传，对换以下流就可以了
    		instream = sftp.get(remoteFile);
    		outstream = new FileOutputStream(new File(localFile));
			byte b[] = new byte[1024];
			int n;
			while ((n = instream.read(b)) != -1) {
				outstream.write(b, 0, n);
			}
			outstream.flush();
			flag = true;
		} finally{
			outstream.close();
			instream.close();
		}
    	return flag;
    }
    
	/**
	* @Title: connect 
	* @Description: TODO(连接ftp服务器)
	* @param @throws Exception    设定文件
	* @return flag  
	* @throws
	*/ 
	public boolean connect() throws Exception{
		boolean flag = false;
	    JSch jsch = new JSch();
	    //设置密钥和密码
	    if (privateKey != null && !"".equals(privateKey) && !"null".equals(privateKey)) {
	        if (passphrase != null && !"".equals(passphrase) && !"null".equals(passphrase)) {
	            //设置带口令的密钥
	            jsch.addIdentity(privateKey, passphrase);
	        } else {
	            //设置不带口令的密钥
	            jsch.addIdentity(privateKey);
	        }
	    }
	    if(port <=0){
	        //连接服务器，采用默认端口
	        session = jsch.getSession(user, ip);
	    }else{
	        //采用指定的端口连接服务器
	        session = jsch.getSession(user, ip ,port);
	    }
	    //如果服务器连接不上，则抛出异常
	    if (session == null) {
	        throw new Exception("session is null");
	    }
	    //设置登陆主机的密码
	    session.setPassword(psw);//设置密码  
	    //设置第一次登陆的时候提示，可选值：(ask | yes | no)
	    session.setConfig("StrictHostKeyChecking", "no");
	    //设置登陆超时时间  
	    session.connect(20000);
	    try {
	        //创建sftp通信通道
	        channel = (Channel) session.openChannel("sftp");
	        channel.connect(1000);
	        sftp = (ChannelSftp) channel;
	        flag = true;
	    } catch (Exception e) {
	        e.printStackTrace();
	    } 
	    return flag;
	}
	
	public void close(){
		if(null!=channel){
	        channel.disconnect();
	        channel = null;
	    }
		if(null != session){
			session.disconnect();
			session = null;
		}
	}
}
