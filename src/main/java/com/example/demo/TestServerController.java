package com.example.demo;

import java.awt.PageAttributes.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.apache.commons.io.IOUtils;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

@RestController
public class TestServerController {

	// get the messages from a specific time range from the database
	@GetMapping("/jsonData")
	public List<MyMessage> jsonData(String d1, String d2, String topic) { // example date in correct format:
																			// 2021-06-15T14:09:24.002+00:00 // instead
																			// of "+", sometimes only %2B works
		MongoClient myMongoClient = ConnectionManager.getConnection();

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

	
	@GetMapping("/imageData")
	public @ResponseBody byte[] imageData(String filename) throws IOException {
		MongoClient myMongoClient = ConnectionManager.getConnection();
		
		// Use the mqtt database and collection of received messages
		DB mydb = myMongoClient.getDB("mqtt_db");

		String bucket = "Image";
		
		GridFS gfsPhoto = new GridFS(mydb, bucket);
		
		// get image file by its file name
        GridFSDBFile imageForOutput = gfsPhoto.findOne(filename);
        
        InputStream img = imageForOutput.getInputStream();
		

		return IOUtils.toByteArray(img);
	}
	
	// Show index.html in browser
	@RequestMapping("/")
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("index.html");
		return modelAndView;
	}

	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}

}
