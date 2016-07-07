package com.ailk.util;

import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPUtil {

	private static String host;
	private static String port;
	private static String user;
	private static String password;
	
	public static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;
    public static final int DEFAULT_BLOCK_SIZE = 4 * 1024;
    
    static FTPClient client;
    static String workpath;
	
	
	
	public static boolean  mkdirs(String pathname) throws IOException{
		String[] folders = pathname.split("/");
		for(String folder : folders){
			if(folder.equals("")){
				continue;
			}
			if(!client.changeWorkingDirectory(folder)){
				if(!client.makeDirectory(folder)){
					return false;
				}
				if(!client.changeWorkingDirectory(folder)){
					return false;
				}
			}
		}
		return true;
	}
	
	
	
	public static FTPClient connect(String host, String port, String user, String password) throws IOException{
		FTPClient client = new FTPClient();
		client.connect(host);
		int reply = client.getReplyCode();
		
		if (!FTPReply.isPositiveCompletion(reply)) {
            throw new IOException("Server - " + host + " refused connection on port - " + port);
        } else if (client.login(user, password)) {
            client.setFileTransferMode(FTP.BLOCK_TRANSFER_MODE);
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.setBufferSize(DEFAULT_BUFFER_SIZE);
            client.setFileType(FTPClient.BINARY_FILE_TYPE); 
            client.enterLocalPassiveMode();  
        } else {
            throw new IOException("Login failed on server - " + host + ", port - " + port);
        }
		
		workpath = client.printWorkingDirectory();
		return client;
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		client = connect(host, port, user, password);
		//test();
	}
	
//	public static void test() throws IOException{
//		String p = "/home/etlcloud/maintain/escaping/version/escaping/bin/logs";
//		client.changeWorkingDirectory(p);
//		workpath = client.printWorkingDirectory();
//		//FTPFile f = new FTPFile("DREscaping_debug.log");
//		FTPFile[] fs= client.listFiles("DREscaping_debug.log");
//		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		for(FTPFile f : fs){
//			f.getTimestamp().setTimeZone(TimeZone.getTimeZone("+8"));
//			System.out.println(sf.format(f.getTimestamp().getTime()));
//		}
//		//System.out.println(f);
//	}
	
	
//	public static void download(String localFileDir) throws Exception{
//		//System.out.println(filePath);
//		client.changeWorkingDirectory("xiajs");
//		BufferedOutputStream output = null; 
//		File localFilePath = new File(localFileDir + File.separator  + fileName);  
//        output = new BufferedOutputStream(new FileOutputStream(localFilePath));  
//		client.retrieveFile(fileName, output);
//		//client.retrieveFileStream(remote);
//	}
}
