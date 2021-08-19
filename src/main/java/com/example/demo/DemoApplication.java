package com.example.demo;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Random;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.jayway.jsonpath.JsonPath;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

@SpringBootApplication
public class DemoApplication {
	
	private static String address;
	public static MqttClient client;
	public static String mqttServerAndTopic;

	public static void main(String[] args) throws MqttException {
		// Start server
		ConfigurableApplicationContext ctx = SpringApplication.run(DemoApplication.class, args);
		
		// Get server infos from application.properties to receive mqtt-Server info
		String address = ctx.getEnvironment().getProperty("mqttserver.address"); // DummyServer-Adress
		String port = ctx.getEnvironment().getProperty("mqttserver.port");
		String serverinfo = ctx.getEnvironment().getProperty("mqttserver.serverinfo");

		// Send get request to receive server name and topic for MQTT Subscriber
		try {
			mqttServerAndTopic = sendingGetRequest(address+":"+port+"/"+serverinfo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Read MQTT Server and topic from JSON-String
		String mqttServer = JsonPath.read(mqttServerAndTopic, "$.mqttserver");
		String topic = JsonPath.read(mqttServerAndTopic, "$.topic");
		System.out.println("MQTT Server: " + mqttServer);
		System.out.println("Topic: " + topic);

		// Generate a random ID
		Random rnd = new Random();
		int ID = rnd.nextInt(999999);

		// New MQTT Client
		client = new MqttClient(mqttServer, "" + ID);

		// Connect MQTT Client to Callback
		MyCallback callback = new MyCallback();
		client.setCallback(callback);

		// Connect MQTT Client to Broker
		MqttConnectOptions mqOptions = new MqttConnectOptions();
		mqOptions.setCleanSession(true);
		client.connect(mqOptions);

		// Subscribe MQTT Client to the topic
		client.subscribe(topic);

	}

	private static String sendingGetRequest(String urlString) throws Exception {

		// Make connection to URL
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		// Set request method to "get"
		con.setRequestMethod("GET");

		// Add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");

		int responseCode = con.getResponseCode();
		System.out.println("Sending get request to: " + url);
		System.out.println("Response code: " + responseCode);

		// Reading response from input Stream
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String output;
		StringBuffer response = new StringBuffer();

		while ((output = in.readLine()) != null) {
			response.append(output);
		}
		in.close();

		// Printing result from response
		System.out.println("Response: " + response.toString());

		// Return response
		return response.toString();

	}

}
