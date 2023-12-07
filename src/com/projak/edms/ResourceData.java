package com.projak.edms;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/*
 * @author Bharathkumar Reddy Chitteti - Projak Infotech Private Limited, Mumbai
 * @version 1.0.0
 */

public class ResourceData {
	
	@SuppressWarnings("finally")
	public static ResourceBundle getResourceBundle() {
		 ResourceBundle rsbundle = null;
		 FileInputStream fis = null;
		try {
			fis= new FileInputStream("D:\\EDMS\\ErrorDescription\\configurations\\config.properties");
			rsbundle = new PropertyResourceBundle(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			System.out.println("Property File Not Found "+e.fillInStackTrace());
		} catch (IOException e) {
			System.out.println("Property File Not Found "+e.fillInStackTrace());
		}
		catch(Exception e){
			e.printStackTrace();
			
			System.out.println(e.fillInStackTrace());
		}
		finally{
			return rsbundle;
		}
	  }
	
	public static void main (String [] args){
		String dictionaryFile = ResourceData.getResourceBundle().getString("dictionaryPath");
		System.out.println(dictionaryFile);
	}
}
