package com.amazonaws.lambda.waynik.notifications.registration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class NotificationsRegistration implements RequestStreamHandler {
	JSONParser parser = new JSONParser();
	LambdaLogger logger;
    
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        logger = context.getLogger();
        logger.log("Loading Java Lambda handler of ProxyWithStream");

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONObject responseJson = new JSONObject();

        String responseCode = "200";

        try {
            JSONObject event = (JSONObject)parser.parse(reader);
            
            if (event.get("queryStringParameters") == null) {
             throw new Exception("no query params sent");
            }
            
            JSONObject requestData = (JSONObject)event.get("queryStringParameters");
            String securityToken = (String)requestData.get("security_token");
            String email = (String)requestData.get("email");
            String deviceToken = (String)requestData.get("device_token");
            
            UserModel userModel = new UserModel();
            User user = userModel.getByEmail(email, securityToken);
    		
            requestData.put("user_id", Integer.toString(user.getId()));
            requestData.put("device_registration_token", deviceToken);
            
            FirebaseTokenModel tokenModel = new FirebaseTokenModel();
            int tokenId = tokenModel.create(requestData);
         
            JSONObject responseBody = new JSONObject();
            responseBody.put("message", "data received successfully");
            
            JSONObject headerJson = new JSONObject();
            // headerJson.put("x-custom-response-header", "my custom response header value");

            responseJson.put("statusCode", responseCode);
            responseJson.put("headers", headerJson);
            responseJson.put("body", responseBody.toString());  

      

        } catch(Exception ex) {
            responseJson.put("statusCode", "400");
            responseJson.put("exception", ex);
            responseJson.put("stackTrace", Utils.getStackTrace(ex));
        }

        logger.log(responseJson.toJSONString());
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(responseJson.toJSONString());  
        writer.close();
    }

}