package com.example.demo;

import java.awt.PageAttributes.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.apache.commons.io.IOUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

@RestController
public class TestServerController {

	// get the messages of a certain topic from a specific time range from the database
	@GetMapping("/jsonData")
	public List<MyMessage> jsonData(String d1, String d2, String topic) {
		
		// Build new connection to MongoDB
		MongoClient myMongoClient = ConnectionManager.getConnection();
		
		// Parse the input dates from Strings to datatype Date
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

		Date date1 = new Date();
		Date date2 = new Date();

		try {
			date1 = format.parse(d1);
			date2 = format.parse(d2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Use the mqtt database and collection of received messages
		DB mydb = myMongoClient.getDB("mqtt_db");
		DBCollection mycoll = mydb.getCollection("received_messages");

		// get a list of all messages in the specific date range
		List<MyMessage> myMessages = ConnectionManager.readFromDBtoJSON(date1, date2, mycoll, topic);

		return myMessages;
	}
	
	// Neue Methode einmal zum ausprobieren!
	@GetMapping("/images")
	public @ResponseBody byte[] images(String d1, String d2) throws IOException {
		MongoClient myMongoClient = ConnectionManager.getConnection();
		
		DB mydb = myMongoClient.getDB("mqtt_db");

		String bucket = "Image";
		
		GridFS gfsPhoto = new GridFS(mydb, bucket);
		
		
		
		//GridFSDBFile imageForOutput = gfsPhoto.findOne(filename);
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

		Date startDate = new Date();
		Date endDate = new Date();

		try {
			startDate = format.parse("2021-08-10T18:42");
			endDate = format.parse("2021-08-10T18:43");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        BasicDBObject getQuery = new BasicDBObject();
        getQuery.append("uploadDate", new BasicDBObject("$gte", startDate).append("$lte", endDate));
        DBCursor cursor = gfsPhoto.getFileList(getQuery);
	    while (cursor.hasNext()) {
	        System.out.println(cursor.next());
	    }
        
	    
        List<GridFSDBFile> bla = gfsPhoto.find(getQuery);
        
        Vector<InputStream> inputStreams = new Vector<InputStream>();
        for(int i=0; i<bla.size(); i++) {
        	InputStream img = bla.get(i).getInputStream();
        	inputStreams.add(img);
        }
        
        Enumeration<InputStream> enu = inputStreams.elements();
        SequenceInputStream sis = new SequenceInputStream(enu);

		return IOUtils.toByteArray(sis);
	}
	
	// Get the filenames of all images in the database between 2 dates
	@GetMapping("/FileNames")
	public List<MyImage> FileNames(String d1, String d2){
		
		// Build new connection to MongoDB
		MongoClient myMongoClient = ConnectionManager.getConnection();
		
		// Parse Strings to Dates
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

		Date date1 = new Date();
		Date date2 = new Date();

		try {
			date1 = format.parse(d1);
			date2 = format.parse(d2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Use the mqtt database and collection of Image.files
		DB mydb = myMongoClient.getDB("mqtt_db");
		DBCollection mycoll = mydb.getCollection("Image.files");

		// get a list of all image names in the specific date range
		List<MyImage> filenames = ConnectionManager.readImageNames(date1, date2, mycoll);
		
		return filenames;
	}
	
	// Get an image as byte array by its filename
	@GetMapping("/imageData")
	public @ResponseBody byte[] imageData(String filename) throws IOException {
		
		// Build new connection to MongoDB
		MongoClient myMongoClient = ConnectionManager.getConnection();
		
		DB mydb = myMongoClient.getDB("mqtt_db");

		String bucket = "Image";
		
		// Use GridFS to search images in MongoDB
		GridFS gfsPhoto = new GridFS(mydb, bucket);
		
		// get image file by its filename
        GridFSDBFile imageForOutput = gfsPhoto.findOne(filename);
        
        // Cast the GridFSDBFile to InputStream
        InputStream img = imageForOutput.getInputStream();
        
        // Return InputStream as byte[]
		return IOUtils.toByteArray(img);
	}
	
	// Show index.html in browser
	@RequestMapping("/")
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("index.html");
		return modelAndView;
	}
}
