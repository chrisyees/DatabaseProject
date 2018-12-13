/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class MechanicShop{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public MechanicShop(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + MechanicShop.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		MechanicShop esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new MechanicShop (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. AddCustomer");
				System.out.println("2. AddMechanic");
				System.out.println("3. AddCar");
				System.out.println("4. InsertServiceRequest");
				System.out.println("5. CloseServiceRequest");
				System.out.println("6. ListCustomersWithBillLessThan100");
				System.out.println("7. ListCustomersWithMoreThan20Cars");
				System.out.println("8. ListCarsBefore1995With50000Milles");
				System.out.println("9. ListKCarsWithTheMostServices");
				System.out.println("10. ListCustomersInDescendingOrderOfTheirTotalBill");
				System.out.println("11. < EXIT");
				
				/*
				 * FOLLOW THE SPECIFICATION IN THE PROJECT DESCRIPTION
				 */
				switch (readChoice()){
					case 1: AddCustomer(esql); break;
					case 2: AddMechanic(esql); break;
					case 3: AddCar(esql); break;
					case 4: InsertServiceRequest(esql); break;
					case 5: CloseServiceRequest(esql); break;
					case 6: ListCustomersWithBillLessThan100(esql); break;
					case 7: ListCustomersWithMoreThan20Cars(esql); break;
					case 8: ListCarsBefore1995With50000Milles(esql); break;
					case 9: ListKCarsWithTheMostServices(esql); break;
					case 10: ListCustomersInDescendingOrderOfTheirTotalBill(esql); break;
					case 11: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice
	
	public static void AddCustomer(MechanicShop esql){//1
		// id INTEGER NOT NULL,
		// fname CHAR(32) NOT NULL,
		// lname CHAR(32) NOT NULL,
		// phone CHAR(13) NOT NULL,
		// address CHAR(256) NOT NULL,
		// PRIMARY KEY (id)
		try{
			String query = "INSERT INTO Customer (fname, lname, phone, address) VALUES (";
			
			System.out.print("\tEnter First Name: ");
			String input = in.readLine();
			while (input.length() == 0 || input.length() > 32 ) {
				System.out.print("\tName Invalid.\n");
				System.out.print("\tEnter First Name: ");
				input = in.readLine();
			}
			query += "'" + input + "', ";
			
			System.out.print("\tEnter Last Name: ");
			input = in.readLine();
			while (input.length() == 0 || input.length() > 32 ) {
				System.out.print("\tName Invalid.\n");
				System.out.print("\tEnter Last Name: ");
				input = in.readLine();
			}
			query += "'" + input + "', ";
			
			System.out.print("\tEnter Phone Number (###)###-####: ");
			input = in.readLine();
			while (input.length() == 0 || input.length() > 13 || !input.matches("\\(\\d{3})\\d{3}-\\d{4}")) {
				System.out.print("\tInvalid Phone Number.\n");
				System.out.print("\tEnter Phone Number: ");
				input = in.readLine();
			}
			query += "'" + input + "', ";
			
			System.out.print("\tEnter Address: ");
			input = in.readLine();
			while (input.length() == 0 || input.length() > 256) {
				System.out.print("\tInvalid Address.\n");
				System.out.print("\tEnter Address: ");
				input = in.readLine();
			}
			query += "'" + input + "');";
			
			esql.executeUpdate(query);
			int rowCount = esql.executeQueryAndPrintResult("SELECT * FROM Customer WHERE id>499");
			System.out.println ("total row(s): " + rowCount);
      	}catch(Exception e){
			System.err.println (e.getMessage());
		}
	}
	
	public static void AddMechanic(MechanicShop esql){//2
		
	}
	
	public static void AddCar(MechanicShop esql){//3
		// vin VARCHAR(16) NOT NULL,
		// make VARCHAR(32) NOT NULL,
		// model VARCHAR(32) NOT NULL,
		// year _YEAR NOT NULL,
		// PRIMARY KEY (vin)
		try{
			String query = "INSERT INTO Car (vin, make, model, year) VALUES (";
			
			System.out.print("\tEnter VIN: ");
			String vin = in.readLine();
			while (input.length() != 16 || esql.executeQuery("SELECT vin FROM Car WHERE vin=" + input + ";") < 1) {
				System.out.print("\tInvalid VIN.\n");
				System.out.print("\tEnter VIN: ");
				input = in.readLine();
			}
			query += "'" + input + "', ";
			
			System.out.print("\tEnter Make: ");
			String input = in.readLine();
			while (input.length() == 0 || input.length() > 32 ) {
				System.out.print("\tInvalid Make.\n");
				System.out.print("\tEnter Make: ");
				input = in.readLine();
			}
			query += "'" + input + "', ";

			System.out.print("\tEnter Model: ");
			input = in.readLine();
			while (input.length() == 0 || input.length() > 32 ) {
				System.out.print("\tInvalid Model.\n");
				System.out.print("\tEnter Model: ");
				input = in.readLine();
			}
			query += "'" + input + "', ";

			System.out.print("\tEnter Year: ");
			input = in.readLine();
			while (input.length() == 0 || !isInteger(input) || Integer.parseInt(input) < 0) {
				System.out.print("\tInvalid Year.\n");
				System.out.print("\tEnter Year: ");
				input = in.readLine();
			}
			query += "'" + input + "');";

			esql.executeUpdate(query);
			int rowCount = esql.executeQueryAndPrintResult("SELECT * FROM Car WHERE vin='" + vin + "'");
			System.out.println ("total row(s): " + rowCount);
      	}catch(Exception e){
			System.err.println (e.getMessage());
      	}
	}
	
	public static void InsertServiceRequest(MechanicShop esql){//4

	}
	
	public static void CloseServiceRequest(MechanicShop esql) throws Exception{//5
		// This function will allow you to complete an existing service request. Given a service
		// request number and an employee id, the client application should verify the information
		// provided and attempt to create a closing request record. You should make sure to check
		// for the validity of the provided inputs (i.e. does the mechanic exist, does the request exist,
		// valid closing date after request date, etc.)  

		// CREATE TABLE Closed_Request
		// (
		// 	wid INTEGER NOT NULL,
		// 	rid INTEGER NOT NULL,
		// 	mid INTEGER NOT NULL,
		// 	date DATE NOT NULL,
		// 	comment TEXT,
		// 	bill _PINTEGER NOT NULL,
		// 	PRIMARY KEY (wid),
		// 	FOREIGN KEY (rid) REFERENCES Service_Request(rid),
		// 	FOREIGN KEY (mid) REFERENCES Mechanic(id)
		// );
		try{
			String query = "INSERT INTO Closed_Request (rid, mid, date, comment, bill) VALUES (";

			System.out.print("\tEnter Service Request ID: ");
			String input = in.readLine();
			
			// Get rid input and check if valid
			while (input.length() == 0 || !isInteger(input) || Integer.parseInt(input) < 0 || esql.executeQuery("SELECT rid FROM Service_Request WHERE rid=" + input + ";") < 1) {
				System.out.print("\tInvalid Service Request ID.\n");
				System.out.print("\tEnter Service Request ID: ");
				input = in.readLine();
			}
			query += "'" + input + "', ";

			System.out.print("\tEnter Mechanic ID: ");
			input = in.readLine();
			while (input.length() == 0 || !isInteger(input) || Integer.parseInt(input) < 0) {
				System.out.print("\tInvalid Mechanic ID.\n");
				System.out.print("\tEnter Mechanic ID: ");
				input = in.readLine();
			}
			query += "'" + input + "', ";

			System.out.print("\tEnter Date: ");
			input = in.readLine();
			query += "'" + input + "',);";

			System.out.print("\tEnter Comment: ");
			input = in.readLine();
			query += "'" + input + "',);";
			
			System.out.print("\tEnter Bill: ");
			input = in.readLine();
			while (input.length() == 0 || !isInteger(input) || Integer.parseInt(input) < 0) {
				System.out.print("\tInvalid Bill.\n");
				System.out.print("\tEnter Bill: ");
				input = in.readLine();
			}
			query += "'" + input + "');";
			
			esql.executeUpdate(query);
			int rowCount = esql.executeQueryAndPrintResult("SELECT * FROM Closed_Request WHERE wid>499");
			System.out.println ("total row(s): " + rowCount);

		}catch(Exception e){
			System.err.println (e.getMessage());
		}
	}
	
	public static void ListCustomersWithBillLessThan100(MechanicShop esql){//6

	}
	
	public static void ListCustomersWithMoreThan20Cars(MechanicShop esql){//7
		try{
			String query = "SELECT C.fname, C.lname FROM Customer C, Owns O WHERE C.id = O.customer_id GROUP BY C.id HAVING COUNT(O.customer_id) > 20";
			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println ("total row(s): " + rowCount);
		}catch(Exception e){
			System.err.println (e.getMessage());
		}
	}
	
	public static void ListCarsBefore1995With50000Milles(MechanicShop esql){//8
		
	}
	
	public static void ListKCarsWithTheMostServices(MechanicShop esql){//9
		try{
			String query = "SELECT C.make, C.model, COUNT(S.rid) AS numS FROM Car C, Service_Request S WHERE C.vin = S.car_vin GROUP BY C.vin ORDER BY numS DESC LIMIT ";
			System.out.print("\tEnter Number of Cars: ");
			String input = in.readLine();
			while (!isInteger(input) || Integer.parseInt(input) < 0) {
				System.out.print("\tInvalid Number.\n");
				System.out.print("\tEnter Enter Number of Cars: ");
				input = in.readLine();
			}
			query += input + ";";

			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println ("total row(s): " + rowCount);
		}catch(Exception e){
			System.err.println (e.getMessage());
		}
	}
	
	public static void ListCustomersInDescendingOrderOfTheirTotalBill(MechanicShop esql){//9
		//
		
	}
	
	public static boolean isInteger(String s) {
		
		try{
			Integer.parseInt(s);
			return true;
		}
		catch (NumberFormatException ex){
			return false;
		}
		
	}
}
