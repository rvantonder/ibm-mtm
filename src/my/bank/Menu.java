package my.bank;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.annotation.Resource;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

/**
 * Servlet implementation class Menu
 */
@WebServlet("/Menu")
public class Menu extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Resource(name = "jdbc/MyDataSource")
	private DataSource ds1;
	private Connection con = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Menu() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String action = request.getParameter("action");
		String account = request.getParameter("account");
		String result = "";
		String amount = "";
		Double newBalance;

		HttpSession session = request.getSession(true);

		if (request.getSession().getAttribute("auth") == null
				|| request.getSession().getAttribute("account") == null) {
			response.sendRedirect("/Bank42/atm/atm.html");
			return;
		}

		System.out.println("auth attribute value: "
				+ request.getSession().getAttribute("auth"));
		System.out.println("account attribute value: "
				+ request.getSession().getAttribute("account"));

		String authAttribute = (String) request.getSession().getAttribute(
				"auth");
		String accountAttribute = (String) request.getSession().getAttribute(
				"account");
		if (!authAttribute.equals("AUTHED")
				|| !accountAttribute.equals(account)) {
			response.sendRedirect("/Bank42/atm/atm.html");
			return;
		}

		try {
			Double balance = getBalance(account);
			Boolean accountActive = getStatus(account);
			if (action == null) {
				out.print("Invalid request");
				response.sendRedirect("/Bank42/atm/atm.html");
				return;
			}
			if (action.equals("check")) {
				result = String.format("Balance = $%.2f\n", balance);
				out.print(result);
			} else if (action.equals("withdrawal")) {
				if (accountActive) {
					amount = request.getParameter("amount");
					newBalance = doWithdrawal(account, amount, balance);
			
					if ( (double) newBalance == (double) balance) {
						// Withdrawal amount was too big, cancel
						result = String.format(
								"Cancelled! Withdrawal amount too large!\nBalance = $%.2f\n",
								newBalance);
					} else {
						result = String.format(
								"Withdrawal complete.\nNew balance = $%.2f\n",
								newBalance);
					}
				} else {
					result = "Cannot withdraw, account not active!";
				}
				out.print(result);
			} else if (action.equals("deposit")) {
				if (accountActive) {
					amount = request.getParameter("amount");
					newBalance = doDeposit(account, amount, balance);
					result = String.format(
							"Deposit complete.\nNew balance = $%.2f\n",
							newBalance);
				} else {
					result = "Cannot deposit, account not active!";
				}
				out.print(result);
			} else if (action.equals("transactions")) {
				JSONObject jsonResult = new JSONObject();
				if (accountActive) {
					jsonResult.put("result", getTransactions(account));
				} else {
					jsonResult.put("result",
							"Cannot display transactions, account not active!");
				}
				System.out.println("Returning: " + jsonResult);
				out.print(jsonResult);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	private JSONArray getTransactions(String account) throws SQLException {
		JSONArray resultList = new JSONArray();

    // TODO use prepared statements
		String query = "SELECT * FROM TRANS WHERE ACCT = '" + account + "' ORDER BY TIME_START DESC";
		System.out.println("Query: " + query);
		ResultSet result;
		try {

			con = ds1.getConnection();
			Statement stmt = null;
			stmt = con.createStatement();
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
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		return resultList;
	}

	private double getBalance(String account) throws SQLException {
    // TODO use prepared statements
		String query = "SELECT balance FROM BALANCE WHERE ACCT = " + account;
		System.out.println("GetBalance Query: " + query);
		ResultSet result;
		double r = 0.0;
		try {
			con = ds1.getConnection();
			Statement stmt = null;
			stmt = con.createStatement();
			result = stmt.executeQuery(query);
			result.next();
			r = Double.parseDouble(result.getString("balance"));
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return r;
	}

	private Boolean getStatus(String account) throws SQLException {
    // TODO use prepared statements
		String query = "SELECT STATUS FROM CUSTOMER WHERE ACCT = '" + account
				+ "'";
		String status = "";
		ResultSet result;
		try {

			con = ds1.getConnection();
			Statement stmt = null;
			stmt = con.createStatement();
			result = stmt.executeQuery(query);
			result.next();

			status = result.getString("status").toUpperCase();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		if (status.equals("A")) {
			return true;
		} else {
			return false;
		}
	}

	private double doWithdrawal(String account, String amount, Double balance)
			throws SQLException {
		double initialBalance = balance;
		double withdrawalAmount = Double.parseDouble(amount);
		double newBalance = initialBalance - withdrawalAmount;

		if (newBalance < 0) {
			return balance;
		}

		try {
			con = ds1.getConnection();
			Statement stmt = null;
			stmt = con.createStatement();

			// new entry in the transaction table with start time
      // TODO use prepared statements
			String newTransaction = String.format(
					"INSERT INTO TRANS (ACCT,AMOUNT,TRNTYPE,TIME_START)"
							+ "  VALUES ('%s', %.2f, 'W', CURRENT TIMESTAMP)",
					account, withdrawalAmount);
			stmt.executeUpdate(newTransaction);
			// update the balance table
      // TODO use prepared statements
			String chgBalance = String.format(
					"UPDATE BALANCE SET BALANCE = %.2f WHERE ACCT = %s",
					newBalance, account);
			stmt.executeUpdate(chgBalance);
			// close out transaction
      // TODO use prepared statements
			String cloTransaction = String.format(
					"UPDATE TRANS SET TIME_END = CURRENT TIMESTAMP"
							+ " WHERE ACCT = %s AND TIME_END IS NULL", account);
			stmt.executeUpdate(cloTransaction);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return newBalance;
	}

	private Double doDeposit(String account, String amount, Double balance)
			throws SQLException {
		double initialBalance = balance;
		double depositAmount = Double.parseDouble(amount);
		double newBalance = initialBalance + depositAmount;

		try {

			con = ds1.getConnection();
			Statement stmt = null;
			stmt = con.createStatement();
			// new entry in the transaction table with start time
      // TODO use prepared statements
			String newTransaction = String.format(
					"INSERT INTO TRANS (ACCT,AMOUNT,TRNTYPE,TIME_START)"
							+ "  VALUES ('%s', %.2f, 'D', CURRENT TIMESTAMP)",
					account, depositAmount);
			stmt.executeUpdate(newTransaction);
			// update the balance table
      // TODO use prepared statements
			String chgBalance = String.format(
					"UPDATE BALANCE SET BALANCE = %.2f WHERE ACCT = %s",
					newBalance, account);
			stmt.executeUpdate(chgBalance);
			// close out transaction
      // TODO use prepared statements
			String cloTransaction = String.format(
					"UPDATE TRANS SET TIME_END = CURRENT TIMESTAMP"
							+ " WHERE ACCT = %s AND TIME_END IS NULL", account);
			stmt.executeUpdate(cloTransaction);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return newBalance;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String account = (String) request.getSession().getAttribute("account");

		System.out.println("Account name is: " + account);

		if (request.getSession().getAttribute("auth") == null
				|| request.getSession().getAttribute("account") == null) {
			response.sendError(403);
			return;
		}

		out.print(account);
	}

}
