package org.vanillaworld.MySQLInventories.Backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class Compression {


	
	public static byte[] compress(byte[] content){
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        try{
	            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
	            gzipOutputStream.write(content);
	            gzipOutputStream.close();
	        } catch(IOException e){
	            throw new RuntimeException(e);
	        }
	        return byteArrayOutputStream.toByteArray();
	    }

	    public static byte[] decompress(byte[] contentBytes){	        
	        java.io.ByteArrayInputStream bytein = new java.io.ByteArrayInputStream(contentBytes);
	        java.util.zip.GZIPInputStream gzin = null;
			try {
				gzin = new java.util.zip.GZIPInputStream(bytein);
			} catch (IOException e) {
				e.printStackTrace();
			}
	        java.io.ByteArrayOutputStream byteout = new java.io.ByteArrayOutputStream();

	        int res = 0;
	        byte buf[] = new byte[1024];
	        while (res >= 0) {
	            try {
					res = gzin.read(buf, 0, buf.length);
				} catch (IOException e) {
					e.printStackTrace();
				}
	            if (res > 0) {
	                byteout.write(buf, 0, res);
	            }
	        }
	        return byteout.toByteArray();
	    }
	
	

	
	
}
