package my.bank;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

/**
 * Servlet implementation class Admin, this is for DEBUG purposes only
 */
@WebServlet("/Admin")
public class Admin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection db2Conn;
	private static Statement stmt;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Admin() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");

		JSONObject jsonResult = new JSONObject();
		JSONArray resultList = new JSONArray();

		String table = request.getParameter("action");

		try {
      //TODO use prepared statements
			String query = "SELECT * FROM BALANCE";

			if (table.equals("balance")) {
				query = "SELECT * FROM BALANCE";
			} else if (table.equals("customer")) {
				query = "SELECT * FROM CUSTOMER";
			} else if (table.equals("pin")) {
				query = "SELECT * FROM PIN";
			} else if (table.equals("trans")) {
				query = "SELECT * FROM TRANS";
			} else if (table.equals("zbank")) {
				query = "SELECT * FROM ZBANK.ZIPCODE";
			} else if (table.equals("delete")) {
				String accountToDelete = request.getParameter("account");
				int result = 0;
				System.out.println("Deleting account from balance " + accountToDelete);
				query = "DELETE FROM BALANCE WHERE ACCT = " + accountToDelete;
				result = stmt.executeUpdate(query);
				
				System.out.println("Deleting account from customer " + accountToDelete);
				query = "DELETE FROM CUSTOMER WHERE ACCT = '" + accountToDelete + "'";
				result = stmt.executeUpdate(query);
				
				System.out.println("Deleting account from pin " + accountToDelete);
				query = "DELETE FROM PIN WHERE ACCT = " + accountToDelete;
				result = stmt.executeUpdate(query);
				
				System.out.println("Deleting account from trans " + accountToDelete);
				query = "DELETE FROM TRANS WHERE ACCT = " + accountToDelete;
				result = stmt.executeUpdate(query);
				return;
			}

			ResultSet result;
			result = stmt.executeQuery(query);
			ResultSetMetaData resultMetaData = result.getMetaData();

			JSONArray list = new JSONArray();
			for (int i = 1; i <= resultMetaData.getColumnCount(); i++) {
				list.add(resultMetaData.getColumnLabel(i));
			}
			resultList.add(list);

			list = new JSONArray();
			while (result.next()) {
				for (int e = 1; e <= resultMetaData.getColumnCount(); e++) {
					list.add(result.getString(e));
				}
				resultList.add(list);
				list = new JSONArray();
			}

			jsonResult.put("result", resultList);

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		out.print(jsonResult);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	public void init(ServletConfig config) throws ServletException {
  /** 
   * Removed for security purposes
   */
	}

	public void destroy() {
    /**
     * Removed
     * /
	}

}
