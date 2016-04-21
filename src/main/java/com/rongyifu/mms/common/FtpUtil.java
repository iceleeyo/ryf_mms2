package com.rongyifu.mms.common;

import java.io.File;
import java.io.IOException;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPTransferType;

/**
 * FTP文件下载与上传
 * 
 * @author 阳耀峰
 * 
 */
public class FtpUtil {
	/** FTP主机IP地址 */
	private String ip;

	/** FTP端口号 */
	private int port;

	/** 用户名 */
	private String userName;

	/** 用户口令 */
	private String pwd;

	/** FTP客户端 */
	public FTPClient fc;

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip
	 *            the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the pwd
	 */
	public String getPwd() {
		return pwd;
	}

	/**
	 * @param pwd
	 *            the pwd to set
	 */
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public FtpUtil(String ip, int port, String userName, String pwd) {
		super();
		this.ip = ip;
		this.port = port;
		this.userName = userName;
		this.pwd = pwd;
	}

	public FtpUtil() {
		super();
	}

	/**
	 * 把指定远程目录文件下载到指定本地路径
	 * 
	 * @param remotePath
	 *            远程文件路径 如:"Data/Data1"根目录则直接写""
	 * @param localPath
	 *            下载到指定路径
	 * @return 返回下载结果 true成功 false失败
	 * @throws Exception
	 */
	public boolean downFileFromHost(String remotePath, String localPath,String fileName)
			throws Exception {
		boolean ret = false;
		boolean loginPass = false;
		try {
			loginPass = connect();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		try {
			if (loginPass) {
				fc.get(localPath + "/" + fileName, remotePath + "/" + fileName);
				ret = true;
			} else {
				ret = false;
			}
			return ret;
		} catch (Exception e) {
			throw new Exception("下载失败,请检查文件路径！");
		} finally {
			disconnect();
		}

	}
	/**
	 * 下载指定目录下的所有文件（不包括子目录）
	 * 
	 * @param remotePath
	 *            远程文件路径 如:"Data"
	 * @param localPath
	 *            下载到指定路径 如“Dowload”
	 * @return 返回下载结果 true成功 false失败
	 * @throws Exception
	 */
	public boolean downFileFromHost(String remotePath, String localPath)
			throws Exception {
		boolean ret = false;
		boolean loginPass = false;
		try { 
			loginPass = connect();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		try {
			if (loginPass) {
				String[]d=	fc.dir(remotePath);
				for (int i = 0; i < d.length; i++) {
					fc.get(localPath + "/" + d[i], remotePath+"/"+d[i]);
				}
				ret = true;
			} else {
				ret = false;
			}
			return ret;
		} catch (Exception e) {
			throw new Exception("下载失败,请检查文件路径！");
		} finally {
			disconnect();
		}

	}
	/**
	 * 指定远程路径下载文件
	 * 
	 * @param remotePath
	 *            远程文件路径 如:"Data/1.txt"根目录则直接写文件名
	 * @return 文件下载成功后的路径
	 * @throws Exception
	 */
	public String getDownFilePath(String remoteFile) throws Exception {
		boolean loginPass = false;
		String path = "";// 最终返回路径
		String localPath = new File("").getAbsolutePath()
				+ "\\src\\main\\webapp\\public\\temp\\";
		try {
			loginPass = connect();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		try {
			if (loginPass) {
				String filename = getFileName(remoteFile);
				path = localPath + filename;
				fc.get(path, remoteFile);
			}
			return path;
		} catch (Exception e) {
			throw new Exception("下载失败,请检查文件路径及文件名！");
		} finally {
			disconnect();
		}

	}

	/**
	 * 登录服务器
	 * 
	 * @return 返回登录结果 true成功 false失败
	 * @throws Exception
	 */
	private boolean connect() throws Exception {
		boolean ret = false;
		fc = new FTPClient();
		try {
			fc.setRemoteHost(this.ip);
			fc.setRemotePort(this.port);
			fc.setControlEncoding("UTF-8");
			fc.connect();
			fc.login(this.userName, this.pwd);
			fc.setConnectMode(FTPConnectMode.ACTIVE);
			fc.setType(FTPTransferType.BINARY);
			ret = true;
			return ret;
		} catch (Exception e) {
			throw new Exception("登录失败!");
		}
	}

	/**
	 * 释放连接
	 * 
	 * @return
	 */
	private boolean disconnect() {
		boolean ret = false;
		try {
			fc.quit();
			ret = true;
			return ret;
		} catch (Exception e) {
			ret = false;
			return ret;
		}

	}

	/**
	 * 得到文件名
	 * 
	 * @param filePathOrName
	 * @return
	 */
	private String getFileName(String filePathOrName) {
		if (filePathOrName.indexOf("/") > 0) {
			String []strs=filePathOrName.split("/");
			String fileName = strs[strs.length-1];
			if(fileName.equals("")){
				fileName=strs[strs.length-2];
			}
			return fileName;
		} else {
			return filePathOrName;
		}
	}

	// /**
	// * 上传本地文件到远程目录
	// * @param localPath
	// * @param fileName
	// * @param remotePath
	// * @return
	// * @throws Exception
	// */
	// public boolean uploadFile(String localPath, String fileName,
	// String remotePath) throws Exception {
	// boolean ret = false;
	// boolean loginPass=false;
	// try {
	// loginPass=connect();
	// } catch (Exception e) {
	// throw new Exception(e.getMessage());
	// }
	// if (loginPass) {
	// try {
	// fc.put(localPath + "/" + fileName, remotePath + "/" + fileName);
	// } catch (IOException e) {
	// throw new Exception("文件上传失败！");
	// } catch (FTPException e) {
	// throw new Exception("文件上传失败,请检查路径是否存在及是否拥有权限！");
	// }
	// finally {
	// disconnect();
	// }
	// ret = true;
	// } else {
	// ret = false;
	// }
	// return ret;
	// }

	/**
	 * 上传文件到FTP服务器（指定目录）
	 * 
	 * @param localFileName
	 *            本地文件路径（D:/Test.java）
	 * @param remoteFileName
	 *            远程文件名(Test1.java);
	 * @param remoteDirName
	 *            远程文件夹路径，不存在则会被创建("Test/test.java")
	 * @return
	 * @throws Exception
	 */
	public boolean uploadFileToDir(String localFileName, String remoteFileName,
			String remoteDirName) throws Exception {
		boolean loginPass = false;
		try {
			loginPass = connect();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		if (!loginPass) {
			return false;
		}
		if (remoteDirName != null && !remoteDirName.equals(""))
			createActiveDir(remoteDirName, true);
		try {
			fc.put(localFileName, remoteFileName);
		} catch (IOException e) {
			throw new Exception("读取文件失败！");
		} catch (FTPException e) {
			throw new Exception("上传文件失败！");
		}

		return true;
	}
	/**
	 * 上传文件到FTP服务器（指定目录）
	 * 
	 * @param localFileName
	 *            本地文件路径（D:/Test.java）
	 * @param remoteDirName
	 *            远程文件夹路径，不存在则会被创建("Test")
	 * @return
	 * @throws Exception
	 */
	public boolean uploadFileToDir(String localFileName,
			String remoteDirName) throws Exception {
		boolean loginPass = false;
		try {
			loginPass = connect();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		if (!loginPass) {
			return false;
		}
		if (remoteDirName != null && !remoteDirName.equals(""))
			createActiveDir(remoteDirName, true);
		try {
			fc.put(localFileName, getFileName(localFileName));
		} catch (IOException e) {
			throw new Exception("读取文件失败！");
		} catch (FTPException e) {
			throw new Exception("上传文件失败！");
		}

		return true;
	}
	/**
	 * 把指定目录下所有的文件上传到服务器指定路径上
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
				createActiveDir(ftpPath, true);
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						try {
							fc.put(files[i].getPath(), files[i].getName());
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
	 * 把指定目录下所有的文件上传到服务器根目录
	 * 
	 * @param folderName
	 *            文件夹名
	 * @return
	 * @throws Exception
	 */
	public boolean uploadAllFilesInFolder(String folderName) throws Exception {
		boolean flag = false;
		if (connect()) {
			File file = new File(folderName);
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						try {
							fc.put(files[i].getPath(), files[i].getName());
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
						if (dirs[i].indexOf(":") < 0
								&& !checkFolderIsExistAndChangeDir(currentDir
										+ dirs[i] + "/")) {
							if (!currentDir.equals(""))
								fc.chdir(currentDir);
							fc.mkdir(dirs[i]);
							fc.chdir(dirs[i]);
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new Exception("创建文件夹失败！");
					}
					currentDir += dirs[i] + "/";
				}
			} else {
				if (!checkFolderIsExistAndChangeDir(remoteDirName)) {
					fc.mkdir(dirs[dirs.length - 1]);
					fc.chdir(dirs[dirs.length - 1]);
				}
			}
		} catch (Exception e) {
			throw new Exception("创建文件夹失败！");
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
		boolean loginPass = false;
		try {
			loginPass = connect();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		try {
			if (loginPass && remoteDirName.endsWith("/")) {
				remoteDirName = remoteDirName.substring(0,
						remoteDirName.length() - 1);
			}
			fc.chdir(remoteDirName);
			result = true;
		} catch (Exception e) {
			return result;
		}
		return result;
	}

	/**
	 * 从FTP服务器指定目录删除文件
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public boolean deleteFile(String remoteFileName, String remoteDir)
			throws Exception {
		if (!connect()) {
			throw new Exception("登录服务器失败！");
		}
		if (!checkFolderIsExistAndChangeDir(remoteDir)) {
			throw new Exception("目录不存在！");
		}
		try {
			fc.delete(remoteFileName);
		} catch (Exception e) {
			throw new Exception("目录或文件不存在！");
		}
		return true;
	}

	/**
	 * 从FTP服务器根目录删除文件
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public boolean deleteFile(String remoteFileName) throws Exception {
		if (!connect()) {
			throw new Exception("登录服务器失败！");
		}
		try {
			fc.delete(remoteFileName);
		} catch (Exception e) {
			throw new Exception("文件删除失败！");
		}
		return true;
	}

	/**
	 * 删除文件夹(文件夹必须为空)
	 * @param localFileNameList
	 * @throws Exception
	 */
	public boolean deleteDir(String remoteDirName) throws Exception {
		if (!connect()) {
			throw new Exception("登录服务器失败！");
		}
		try {
			fc.rmdir(remoteDirName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("删除失败！");
		}
		return true;
	}
	
	/**
	 * 重命名文件
	 * @param remotePath 远程路径
	 * @param fileName 要修改的文件名
	 * @param newFileName 新的文件名
	 * @return
	 * @throws Exception
	 */
	public boolean renameFile(String remotePath,String fileName,String newFileName) throws Exception {
		if (!connect()) {
			throw new Exception("登录服务器失败！");
		}
		try {
			fc.rename(remotePath + "/" + fileName, remotePath
					+ "/" + newFileName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("修改失败！");
		}
		return true;
	}
	//=================================测试使用
//	public void test(String data) throws Exception{
//		if (!connect()) {
//			throw new Exception("登录服务器失败！");
//		}
//		try {
////			fc.chdir("yyf");
////			FTPFile[] files=fc.dirDetails("");
//			String[]d=	fc.dir(data);
//			for (int i = 0; i < d.length; i++) {
//				System.out.println("d["+i+"]:"+d[i]);
//			}
//			System.out.println(d.length);
//			System.out.println("files:"+fc.dir(data, true)[0]);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (FTPException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  
//	}
	
	
}
