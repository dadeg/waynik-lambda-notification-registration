package com.amazonaws.lambda.waynik.notifications.registration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.dbutils.DbUtils;
import org.json.simple.JSONObject;

public class FirebaseTokenModel {
	
	private String table = "firebase_tokens";
	
	private String ID = "id";
	private String USER_ID = "user_id";
	private String TOKEN = "device_registration_token";
	private String CREATED_AT = "created_at";
    
	private String[] fields = {
    	ID,
        USER_ID,
        TOKEN,
        CREATED_AT
    };
    
	public int create(JSONObject data) throws Exception
	{
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultingKey = null;
        try {
        	ArrayList<String> params = new ArrayList<String>();
        
	        String fieldsString = "";
	        String questionMarks = "";
	        String comma = "";
	
	        for (String field : fields) {
	            if (data.containsKey(field)) {
	                params.add((String)data.get(field));
	                fieldsString += comma + "`" + field + "`";
	                questionMarks += comma + "?";
	                comma = ",";
	            }
	        }
	
	        String sql = "INSERT INTO `" + table + "` (" + fieldsString + ") VALUES (" + questionMarks + ")";
	
	        connection = MysqlConnector.getConnection();
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			int i = 1;
	        for (String param : params) {
				statement.setString(i, param);
				i++;
			}
	        int rowsAffected = statement.executeUpdate();
	        
	        if (rowsAffected == 0) {
	        	throw new SQLException("Creating token failed");
	        }
	        
	        resultingKey = statement.getGeneratedKeys();
	        int createdId = 0;
	        if (resultingKey.next())
	        {
	            createdId = resultingKey.getInt(1);
	        }
	        return createdId;
        } catch (Exception e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(resultingKey);
			DbUtils.closeQuietly(statement);
			DbUtils.closeQuietly(connection);
		}
	}
}
