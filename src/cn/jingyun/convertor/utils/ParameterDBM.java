package cn.jingyun.convertor.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import cn.jingyun.convertor.ffmpeg.FFmpegArguments;

public class ParameterDBM
{
	private static final String ParameterTableName = "ParameterTable";
	private static final String FFmpegArgumentsTableName = "FFmpegArgumentsTable";
	private static final ParameterDBM instance = new ParameterDBM();

	private Connection connection = null;

	public static synchronized ParameterDBM getInstance()
	{
		return instance;
	}
	
	public static String finishPathEnd(String path)
	{
		String ret = path;
		if (ret.contains("/")) {
			if (ret.charAt(ret.length() - 1) != '/')
				ret += "/";
		} else { 
			if (ret.charAt(ret.length() - 1) != '\\')
				ret += "\\";
		}
		
		return ret;
	}

	private ParameterDBM()
	{
		try {
			Class.forName("org.sqlite.JDBC");
			
			String path = System.getProperty("user.home");
			path = finishPathEnd(finishPathEnd(path) + ".Trans");
			File file = new File(path);
			if (!(file.exists()))
				file.mkdir();
			
			// create a database connection
			connection = DriverManager
					.getConnection("jdbc:sqlite:" + path +
							"TransParameterDBM.dbm");
			if (!hasTableAtDB(connection, ParameterTableName))
				createParameterTable();
			if (!hasTableAtDB(connection, FFmpegArgumentsTableName))
				createArgumentsTable();
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable
	{
		close();
	}

	// Close connection
	public void close()
	{
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (SQLException e) {
			// connection close failed.
			System.err.println(e);
		}
	}

	// Check the table's name is 'tblName' whether it exists!
	public static boolean hasTableAtDB(Connection conn, String tblName)
			throws SQLException
	{
		boolean ret = false;

		if (conn == null || tblName == null)
			return ret;

		PreparedStatement stat = conn
				.prepareStatement("SELECT COUNT(*) FROM sqlite_master WHERE name = ?");
		stat.setString(1, tblName);
		ResultSet resultSet = stat.executeQuery();
		if (resultSet.next() && resultSet.getInt(1) == 1)
			ret = true;
		resultSet.close();
		stat.close();

		return ret;
	}

	// ////////////////////////////////////////////////////////////////
	// // TODO Operate ParameterTable Methods ////////
	// ////////////////////////////////////////////////////////////////

	// Create ParameterTable
	private void createParameterTable() throws SQLException
	{
		if (connection == null)
			return;

		Statement stat = connection.createStatement();
		stat.executeUpdate("CREATE TABLE "
				+ ParameterTableName
				+ "(ParameterName VARCHAR(32) NOT NULL PRIMARY KEY, Value VARCHAR(128))");
		stat.close();
		if (hasTableAtDB(connection, ParameterTableName))
			System.out.println("Create Table Successful");
		else
			System.out.println("Create Table Failed");
	}

	// Get Parameter's value from 'ParameterTable'
	// return value is 'null' while the 'name' isn't exist!
	public String getParameter(String name)
	{
		String ret = null;

		if (connection == null)
			return ret;

		try {
			PreparedStatement stat = connection
					.prepareStatement("SELECT Value FROM " + ParameterTableName
							+ " WHERE ParameterName = ?");
			stat.setString(1, name);
			ResultSet resultSet = stat.executeQuery();
			if (resultSet.next())
				ret = resultSet.getString(1);
			resultSet.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	// Set Parameter
	// Insert item while name is not exist!
	// Update item while name exist;
	public boolean setParameter(String name, String value)
	{
		boolean ret = false;
		if (connection == null)
			return ret;

		String tblValue = getParameter(name);
		if (tblValue == null) {
			try {
				PreparedStatement stat = connection
						.prepareStatement("INSERT INTO " + ParameterTableName
								+ " VALUES(?, ?)");
				stat.setString(1, name);
				stat.setString(2, value);
				ret = stat.execute();
				stat.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (!tblValue.equals(value)) {
			try {
				PreparedStatement stat = connection.prepareStatement("UPDATE "
						+ ParameterTableName
						+ " SET Value = ? WHERE ParameterName = ?");
				stat.setString(1, value);
				stat.setString(2, name);
				ret = stat.execute();
				stat.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	// Remove Parameter
	// name: Parameter's name;
	public boolean removeParameter(String name)
	{
		boolean ret = false;
		if (connection == null)
			return ret;

		String tblValue = getParameter(name);
		if (tblValue != null) {
			try {
				PreparedStatement stat = connection
						.prepareStatement("DELETE FROM " + ParameterTableName + " WHERE ParameterName = ?");
				stat.setString(1, name);
				ret = stat.execute();
				stat.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	// ////////////////////////////////////////////////////////////////
	// // TODO Operate ParameterTable Methods ////////
	// ////////////////////////////////////////////////////////////////

	// Create ParameterTable
	private void createArgumentsTable() throws SQLException
	{
		// TODO Auto-generated method stub
		if (connection == null)
			return;

		Statement stat = connection.createStatement();
		stat.executeUpdate("CREATE TABLE "
				+ FFmpegArgumentsTableName
				+ "(Name VARCHAR(32) NOT NULL PRIMARY KEY, Arguments VARCHAR(128) NOT NULL, FileExtension VARCHAR(32) NOT NULL)");
		stat.close();
		if (hasTableAtDB(connection, FFmpegArgumentsTableName))
			System.out.println("Create Table Successful");
		else
			System.out.println("Create Table Failed");
	}
	
	//Get all arguments's information From FFmpegArgumentsTable
	public void loadAllFFmpegArguments(List<FFmpegArguments> list)
	{
		if (connection == null || list == null)
			return;
		
		list.clear();
		
		Statement stat = null;
		try {
			stat = connection.createStatement();
			ResultSet rs = stat.executeQuery(
					"SELECT Name, Arguments, FileExtension FROM " 
					+ FFmpegArgumentsTableName);
			while (rs.next())
				list.add(new FFmpegArguments(rs.getString(1), rs.getString(2), rs.getString(3)));
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (stat != null)
				try {
					stat.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	//Insert Arguments information to table
	public boolean addFFmpegArgument(FFmpegArguments arguments)
	{
		boolean ret = false;
		if (connection == null 
			|| arguments == null || arguments.getName() == null || arguments.getArguments() == null || arguments.getFileExtension() == null
			|| hasFFmpegArgumentsName(arguments.getName()))
			return ret;
		
		try {
			PreparedStatement stat = connection.prepareStatement("INSERT INTO " + FFmpegArgumentsTableName + " VALUES(?, ?, ?)");
			stat.setString(1, arguments.getName());
			stat.setString(2, arguments.getArguments());
			stat.setString(3, arguments.getFileExtension());
			stat.execute();
			stat.close();
			if (hasFFmpegArgumentsName(arguments.getName()))
				ret = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	//Delete FFmpeg Arguments With it's name
	public boolean deleteFFmpegArgumentName(String name)
	{
		boolean ret = false;
		if (connection == null || name == null || !hasFFmpegArgumentsName(name))
			return ret;
		
		try {
			PreparedStatement stat = connection.prepareStatement("DELETE FROM " + FFmpegArgumentsTableName + " WHERE Name=?");
			stat.setString(1, name);
			ret = stat.execute();
			stat.close();
			if (!hasFFmpegArgumentsName(name))
				ret = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	//Update FFmpeg Arguments with it's name
	public boolean updateFFmpegArgumentsName(String name, String arguments, String extension)
	{
		boolean ret = false;
		if (connection == null || name == null || !hasFFmpegArgumentsName(name))
			return ret;
		try {
			PreparedStatement stat = connection.prepareStatement("UPDATE " + FFmpegArgumentsTableName + " SET Arguments=?, FileExtension=? WHERE Name=?");
			stat.setString(1, arguments);
			stat.setString(2, extension);
			stat.setString(3, name);
			stat.execute();
			stat.close();
			
			stat = connection.prepareStatement("SELECT COUNT(*) FROM " + FFmpegArgumentsTableName + " WHERE Name=? AND Arguments=? AND FileExtension=?");
			stat.setString(1, name);
			stat.setString(2, arguments);
			stat.setString(3, extension);
			ResultSet rs = stat.executeQuery();
			if (rs.next() && rs.getInt(1) == 1)
				ret = true;
			
			rs.close();
			stat.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	//Verify FFmpeg arguments if it exist.
	private boolean hasFFmpegArgumentsName(String name)
	{
		boolean ret = false;
		if (connection == null || name == null)
			return ret;
		
		PreparedStatement pstat = null;
		try {
			pstat = connection.prepareStatement("SELECT COUNT(*) FROM " + FFmpegArgumentsTableName + " WHERE Name = ?");
			pstat.setString(1, name);
			ResultSet rs = pstat.executeQuery();
			if (rs.next() && rs.getInt(1) == 1)
				ret = true;
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (pstat != null)
				try {
					pstat.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return ret;
	}
}
