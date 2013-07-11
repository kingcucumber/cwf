package com.demo.rest.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MyDataSource {

	private static String url = "jdbc:mysql://localhost:3306/cwf";
	private static String user = "root";
	private static String password = "root";

	private static int initCount = 5;
	private static int maxCount = 10;
	private int currentCount = 0;

	private static MyDataSource myDataSource = new MyDataSource();
	private Log logger = LogFactory.getLog(MyDataSource.class);
	
	private LinkedList<Connection> connectionsPool = new LinkedList<Connection>();

	public MyDataSource() {
		try {
			logger.info("create connection pool !!");
			for (int i = 0; i < initCount; i++) {
				this.connectionsPool.addLast(this.createConnection());
				this.currentCount++;
			}
		} catch (SQLException e) {
			throw new ExceptionInInitializerError();
		}
	}

	public Connection getConnection() throws Exception {
		synchronized (connectionsPool) {
			logger.info("get connection from datasource!");
			
			if (this.connectionsPool.size() > 0)
				return this.connectionsPool.removeFirst();
			if (this.currentCount < maxCount) {
				this.currentCount++;
				return this.createConnection();
			}
			throw new SQLException("too many connections!");
		}
	}

	public void free(Connection conn) {
		logger.info("release the connection to the connection pool!");
		
		this.connectionsPool.addLast(conn);
	}

	private Connection createConnection() throws SQLException {
		logger.info("create connections !!");
		
		return DriverManager.getConnection(url, user, password);
	}
}