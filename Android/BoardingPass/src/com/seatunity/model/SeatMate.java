package com.seatunity.model;

public class SeatMate {
	String id,name,gender,age,profession,live_in,seating_pref,some_about_you,status,seat,travel_class,image_url;
	
	public void setImage_url(String image_url){
		this.image_url=image_url;
	}
	public String getImage_url(){
		return this.image_url;
	}
	
	public void setTravel_class(String travel_class){
		this.travel_class=travel_class;
	}
	public String getTravel_class(){
		return this.travel_class;
	}
	
	public void setStatus(String status){
		this.status=status;
	}
	public String getStatus(){
		return this.status;
	}
	public void setSome_about_you(String some_about_you){
		this.some_about_you=some_about_you;
	}
	public String getSome_about_you(){
		return this.some_about_you;
	}
	public void setSeating_pref(String seating_pref){
		this.seating_pref=seating_pref;
	}
	public String getSeating_pref(){
		return this.seating_pref;
	}
	public void setLive_in(String live_in){
		this.live_in=live_in;
	}
	public String getLive_in(){
		return this.live_in;
	}
	
	public void setId(String id){
		this.id=id;
	}
	public String getId(){
		return this.id;
	}
	public void setName(String name){
		this.name=name;
	}
	public String getName(){
		return this.name;
	}
	public void setAge(String age){
		this.age=age;
	}
	public String getAge(){
		return this.age;
	}
	public void setProfession(String profession){
		this.profession=profession;
	}
	public String getProfession(){
		return this.profession;
	} 
	public void setSeat(String seat){
		this.seat=seat;
	}
	public String getSeat(){
		return this.seat;
	}
	
}
