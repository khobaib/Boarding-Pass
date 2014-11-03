package com.seatunity.model;

import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Model-class to hold the user-credential values. The server response
 * correxponding this class is: {@code  <br> {<br>
 * "success": "true", <br>
 * "token": "gobbledygook", <br>
 * "language": "en", <br>
 * "firstname": "Jane", <br>
 * "lastname": "Shepherd", <br>
 * "gender": "unspecified", <br>
 * "live_in": "Normandy", <br>
 * "age": "122", <br>
 * "profession": "Citadel Council Spectre", <br>
 * "status": "my status", <br>
 * "seating_pref": "small_talk", <br>
 * "image_url": "path/to/profile_pic.jpg" <br>
 * } <br>
 * }
 * @author Sumon
 * 
 */
public class UserCred {

	private String email = "";
	private String password = "";
	private String token;
	private String language;
	private String firstname;
	private String lastname;
	private String gender;
	private String live_in;
	private String age;
	private String profession;
	private String seating_pref = "";
	private String image_url;
	private String some_about_you = "";
	private String status = "";

	/**
	 * Parses a server-sent {@link JSONObject} to a {@link UserCred } object
	 * using {@code GSON} library
	 * 
	 * @param userObj
	 * @return
	 */
	public static UserCred parseUserCred(JSONObject userObj) {
		UserCred userCred = new UserCred();

		GsonBuilder gsonb = new GsonBuilder();
		Gson gson = gsonb.create();
		if (userObj != null) {
			String jsonString = userObj.toString();
			userCred = gson.fromJson(jsonString, UserCred.class);
			if (userCred.some_about_you == null) {
				userCred.setSomethinAbout("");
			}
		}
		return userCred;
	}

	/**
	 * This constructor does nothing special
	 * @param cred
	 */
	public UserCred(UserCred cred) {

	}

	/**
	 * This empty constructor does nothing special
	 * 
	 */
	public UserCred() {

	}

	/**
	 * Explicit constructor
	 * 
	 * @param email
	 * @param password
	 * @param token
	 * @param language
	 * @param firstname
	 * @param lastname
	 * @param gender
	 * @param live_in
	 * @param age
	 * @param profession
	 * @param seating_pref
	 * @param image_url
	 * @param some_about_you
	 * @param status
	 */
	public UserCred(String email, String password, String token, String language, String firstname, String lastname,
			String gender, String live_in, String age, String profession, String seating_pref, String image_url,
			String some_about_you, String status) {
		this.email = email;
		this.password = password;
		this.token = token;
		this.language = language;
		this.firstname = firstname;
		this.lastname = lastname;
		this.gender = gender;
		this.live_in = live_in;
		this.age = age;
		this.profession = profession;
		this.seating_pref = seating_pref;
		this.image_url = image_url;
		this.status = status;
		this.some_about_you = some_about_you;

	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSomethinAbout() {
		return this.some_about_you;
	}

	public void setSomethinAbout(String some_about_you) {
		this.some_about_you = some_about_you;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setSeating_pref(String seating_pref) {
		this.seating_pref = seating_pref;
	}

	public String getSeating_pref() {
		return seating_pref;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getProfession() {
		return profession;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getAge() {
		return age;
	}

	public void setLive_in(String live_in) {
		this.live_in = live_in;
	}

	public String getLive_in() {
		return live_in;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getGender() {
		return gender;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserCred [email=" + email + ", password=" + password + ", token=" + token + ", language=" + language
				+ ", firstname=" + firstname + ", lastname=" + lastname + ", gender=" + gender + ", live_in=" + live_in
				+ ", age=" + age + ", profession=" + profession + ", seating_pref=" + seating_pref + ", image_url="
				+ image_url + ", some_about_you=" + some_about_you + ", status=" + status + "]";
	}

}
