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
import java.util.List;
import java.util.Map;
import java.lang.Boolean;
import java.util.Random;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

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
	
/**
*Takes a url and returns it in a string of no more than 256k bytes
*
*@params fetchUrl
*@return urlString
*
*@throws IOException
*/	

	public String fetchUrlToString(URL fetchUrl) throws IOException {
		
		URLConnection connection = fetchUrl.openConnection();
		connection.connect();
		InputStream stream = connection.getInputStream();
		String urlString = new String();
		String urlContentType = new String();
		Boolean isMatched = false;
		
		
		//make sure the file is an html file
		String htmlText = "text/html";
		urlContentType = connection.getContentType();
		isMatched = urlContentType.contains(htmlText);
		

		if(isMatched == true){		
			//Get the stream and make a string
		
			
			byte[] buffer = new byte[1024];
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream(256000);
			int sizeOfStream = 0;

			while(stream.read() != -1 && sizeOfStream <= 256000){
				int lengthOfBuffer = stream.read(buffer);
				outByteStream.write(buffer, 0, lengthOfBuffer);
				sizeOfStream = sizeOfStream + outByteStream.size();
			}
				
			urlString = outByteStream.toString("UTF-8");
			return urlString;
		}else{
			System.out.println("\nNot Processing File...File of wrong type.\n");
			System.out.println("Content Type: " + urlContentType);
			System.out.println("Comparison String: " + htmlText);
			System.out.println("Match is " + isMatched + "\n");
			return null;
		}
	
	}

/**
*Writes the formatted contents of the html file to a folder.
*
*@param url
*@param directory
*@return titleOfUrl
*
*@throws IOException
*/
	
	public String fetchUrlToFile(URL url, String directory) throws IOException {
			 

		String unformattedUrlText = new String();
		URL workingUrl = url;
		String titleOfUrl = new String();
		String userDirectory = new String(directory);
		int fileNumber = 0;
		
		//get the string format of the url and remove spaces		
		unformattedUrlText = fetchUrlToString(url);
		


		if (unformattedUrlText == null){
			return null;
		}else{

			System.out.println("Processing URL");

			
			String formattedUrlText = unformattedUrlText.replaceAll("\\s+", " ");

			//look for the title tags and retrieve the title
			Pattern p = Pattern.compile("<title>(.*?)</title>");
			Matcher m = p.matcher(formattedUrlText);
			
			if(m.find()){
				titleOfUrl = m.group(1);
				System.out.println("Title of Document: " + titleOfUrl);
			}else{
				System.out.println("Well that didnt work.");
				titleOfUrl = null;
			}

			String titleToFile = new String();
			
			String titleToFileTemp = titleOfUrl.replaceAll("\\s+", "");

			if (titleToFileTemp.length() > 4){
				titleToFile = titleToFileTemp.substring(0,3);
			}else{
				titleToFile = titleToFileTemp;
			}		
		
			//because we don't care what the filename is			
			Random generator = new Random();
			int randomInt = generator.nextInt(1000) +1;
			String writePath = userDirectory + File.separator;
			String fileName = titleToFile + randomInt + ".html";

			System.out.println("Writing to: " + randomInt + writePath + fileName);
			

			//write file to folder
			File dir = new File(writePath);
			File f = new File(dir, fileName);
			f.createNewFile();

			FileWriter writeUrlToFile = new FileWriter(f);
			writeUrlToFile.write(formattedUrlText);
			writeUrlToFile.flush();
			writeUrlToFile.close();
	
			
			
			return titleOfUrl;
			
		}	
		
	}
	
	
/**
*Runs the crawler.  Adds a url to a queue and strips any http link from that page, adding them to the queue.
*Every 5 seconds it repeats using the url from the top of the queue.
*
*@param rootUrl
*@param userDirectory
*
*@throws java.io.UnsupportedEncodingException
*@throws java.io.IOException
*@throws java.io.InterruptedException
*/
	public void run(URL rootUrl, String userDirectory) throws UnsupportedEncodingException, IOException, InterruptedException{
		
		TagTokenizer tokenizer = new TagTokenizer();		
		Queue<URL> urls = new LinkedList<URL>();		
		HashSet<URL> seen = new HashSet<URL>();
		String dir = new String(userDirectory);
		String urlFileText = new String();
		String urlToAdd = new String();
		
		int iBeenLooped = 0;
		
		urls.add(rootUrl);
		seen.add(rootUrl);

		//add a loop that will do the next part for 10 files added to batman folder

		while(iBeenLooped < 10){		

			URL operatingUrl = urls.poll();
			if(operatingUrl != null){
				System.out.println("\nThe current url is: " + operatingUrl);
			}else{
				System.out.println("Something went wrong.");
			}
			
			String currUrlTitle = new String(fetchUrlToFile(operatingUrl, dir));
			iBeenLooped++;
			if(currUrlTitle != null){
				String tempTextBucket = new String(fetchUrlToString(operatingUrl));
				String currUrlText = tempTextBucket.replaceAll("\\s+", " ");
			
				Document urlDocument = new Document(currUrlTitle, currUrlText);
				tokenizer.tokenize(urlDocument);

				String baseUrlProtocol = new String(operatingUrl.getProtocol());				
				String baseUrlHost = new String(operatingUrl.getHost());
				String baseOfUrlToAdd = new String(baseUrlProtocol+ "://" + baseUrlHost);

				//search anchor tags for href
				for (Tag tag : urlDocument.tags){
					if (tag.name.equals("a") && tag.attributes.containsKey("href")){

						String relativeUrlText = new String(tag.toString());						

						Pattern p = Pattern.compile("href=\"(.*?)\"");
						Matcher m = p.matcher(relativeUrlText);
			
						if(m.find()){
							urlFileText = m.group(1);
						}else{
							System.out.println("No Match, No Fire");
							urlFileText = null;
						}
						//exclude links to same page
						p = Pattern.compile("#");
						m = p.matcher(urlFileText);
						if(m.find()){
							System.out.println("Skipping Entry...");

							Thread.sleep(100);
						//add to queue and seen						
						}else{
							urlToAdd = baseOfUrlToAdd + urlFileText;
							
							URL nextIntoQueue = new URL(urlToAdd);
							if (seen.add(nextIntoQueue)){
								urls.add(nextIntoQueue);
								System.out.println("Adding " + urlToAdd + " to queue");
							}else{
								System.out.println(urlToAdd + " is already in the Queue.");
							}
							
							Thread.sleep(100);
						}
					}
				}
				
			}

			Thread.sleep(5000);
		}
	}

/**
*Creates a Folder to store the pages that are crawled.
*
*@param root
*/
	
	public void setFolder(String root){
		
		String folderName = root;
		
		File file = new File(folderName);
		if(file.mkdir()){
			System.out.println("\nNew folder for downloads created.");
			
		}else{
			System.out.println(" No destination folder created.");
		}
		
	}
	
}
