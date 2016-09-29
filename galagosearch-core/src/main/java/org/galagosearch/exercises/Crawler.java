/* 
 * 
 * Name:  Jeffrey Turner
 * 
 * 
 */

package org.galagosearch.exercises;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.galagosearch.core.parse.Document;
import org.galagosearch.core.parse.Tag;
import org.galagosearch.core.parse.TagTokenizer;


//import sun.net.www.URLConnection;

/* 
 * A simple single threaded web crawler
 * does not parse robots.txt
 * 
 * @author Jeffrey Turner
 */

public class Crawler{
	private String root;
	
	public URL getBaseUrl(Document document, URL fetchUrl){
		try{
			for (Tag tag : document.tags){
				if (tag.name.equals("base") && tag.attributes.containsKey("href")){
					return new URL(tag.attributes.get("href"));
				}
			}
		}catch(MalformedURLException e){
			return fetchUrl;
		}
		return fetchUrl;
		
	}
	
	public String fetchUrlToString(URL fetchUrl) throws IOException {
		
		URLConnection connection = fetchUrl.openConnection();
		connection.connect();
		InputStream stream = connection.getInputStream();
		
		
		
	return " ";	
	}
	
	public String fetcchUrlToFile(URL url) throws IOException {
		
	return " ";	
		
	}
	
	public void run(URL rootUrl) throws UnsupportedEncodingException, IOException, InterruptedException{
		
		TagTokenizer tokenizer = new TagTokenizer();
		
		Queue<URL> urls = new LinkedList<URL>();
		
		HashSet<URL> seen = new HashSet<URL>();
		
		
		
		Thread.sleep(5000);
		
	}
	
	public void setFolder(String root){
		
		String folderName = root;
		//String rootDirectory = "/home/jturner/galagosearch-1.04";
		//String downloadDirectory = rootDirectory + File.separator + root;
		
		File file = new File(folderName);
		if(file.mkdir()){
			System.out.println("New folder for downloads created.");
			//System.out.println(downloadDirectory);
		}else{
			System.out.println(" No destination folder created.");
		};
		
	}
	
}