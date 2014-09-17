package com.seatunity.model;

/**
 * This class is used to parse the string got from a scanned-barcode into a
 * {@link BoardingPass} object
 * 
 * @author Sumon
 * 
 */
public class BoardingPassParser {
	String boarding_key;
	BoardingPass boardingPass;
	String stringform = "", firstname = "", lastname = "", PNR = "", travel_from = "", travel_to = "", carrier = "",
			flight_no = "", julian_date = "", compartment_code = "", seat = "", departure = "3 ", arrival = "9 ",
			travel_from_name = "", travel_to_name = "", carrier_name = "", travel_class = "";
	boolean deletestate = false;

	/**
	 * @param boarding_key
	 * @param codetype
	 */
	@SuppressWarnings("unused")
	public BoardingPassParser(String boarding_key, String codetype) {
		boarding_key = boarding_key.trim();
		this.boarding_key = boarding_key;
		stringform = boarding_key;
		String format_code = boarding_key.substring(0, 1);
		String num_of_leg_enoced = boarding_key.substring(1, 2);
		String name = boarding_key.substring(2, 21);
		String[] nameParts = name.split("/");
		if (nameParts.length <= 0)
			return;// Barcode in invalid boarding-pass format is received
		lastname = nameParts[0];
		firstname = nameParts[1];
		if (firstname.contains("MR")) {
			firstname = firstname.replace("MR", "");
			firstname = "MR" + " " + firstname;
		}
		PNR = boarding_key.substring(23, 29);
		travel_from = boarding_key.substring(30, 33);
		travel_to = boarding_key.substring(33, 36);
		carrier = boarding_key.substring(36, 38);
		flight_no = boarding_key.substring(39, 44);
		julian_date = boarding_key.substring(44, 47);
		compartment_code = boarding_key.substring(47, 48);
		seat = boarding_key.substring(48, 52);
		this.boardingPass = new BoardingPass(travel_class, stringform, firstname, lastname, PNR, travel_from,
				travel_to, carrier, flight_no, julian_date, compartment_code, seat, departure, arrival, codetype, "-1",
				travel_from_name, travel_to_name, carrier_name, deletestate);
	}

	/**
	 * @return boardingPass object
	 */
	public BoardingPass getBoardingpass() {
		// Log.e("checking", boardingPass.getStringform());
		// Log.e("checking", boardingPass.getFirstname());
		// Log.e("checking", boardingPass.getLastname());
		// Log.e("checking", boardingPass.getPNR());
		// Log.e("checking", boardingPass.getTravel_from());
		// Log.e("checking", boardingPass.getTravel_to());
		// Log.e("checking", boardingPass.getCarrier());
		// Log.e("checking", boardingPass.getFlight_no());
		// Log.e("checking", boardingPass.getJulian_date());
		// Log.e("checking", boardingPass.getCompartment_code());
		// Log.e("checking", boardingPass.getSeat());
		// Log.e("checking", boardingPass.getArrival());
		return this.boardingPass;
	}

}
