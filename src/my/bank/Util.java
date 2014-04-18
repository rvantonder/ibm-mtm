package my.bank;

import java.util.HashMap;

public class Util {

	private static HashMap<String, String> otpLookup = new HashMap<String, String>();

	public static boolean validateAccount(String account) {
		if (account.matches("[0-9]+") && account.length() == 12) {
			return true;
		}
		return false;
	}

	public static boolean validateStatus(String status) {
		if (status.equals("A") || status.equals("I")) {
			return true;
		}
		return false;
	}

	public static void discardOTP(String account) {
		System.out.println("Discarding otp: " + account);
		otpLookup.put(account, "");
	}

	public static void setOTP(String account, String otp) {
		System.out.println("OTP set to: " + account + " " + otp);
		otpLookup.put(account, otp);
	}

	public static boolean validateOTP(String account, String otp) {
		System.out.println("Validating otp: " + account + " " + otp);
		boolean result = false;
		try {
			result= otpLookup.get(account).equals(otp);
		} catch (Exception e) {
			
		}
		return result;
		
	}
}
