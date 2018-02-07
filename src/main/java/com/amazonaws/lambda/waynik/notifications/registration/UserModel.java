package com.amazonaws.lambda.waynik.notifications.registration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;

import java.sql.PreparedStatement;

class UserModel {
	public User getByEmail(String email, String token) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			
			connection = MysqlConnector.getConnection();
			
			statement = connection.prepareStatement("select u.id from users u INNER JOIN user_custom_fields c ON c.user_id = u.id AND c.attribute = 'apiToken' AND c.value = ? where u.email = ? LIMIT 1;");
			statement.setString(1, token);
			statement.setString(2, email);
			resultSet = statement.executeQuery();
			
	        while (resultSet.next()) {
				int userId = resultSet.getInt("id");
				
				User user = new User(userId);
				
				return user;
	        }
	        
	        throw new Exception("Invalid user credentials");
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(statement);
			DbUtils.closeQuietly(connection);
		}
	}
}
