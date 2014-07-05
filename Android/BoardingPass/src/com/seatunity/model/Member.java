package com.seatunity.model;

public class Member {
  String email,password,firstname,lastname,gender,live_in,age,profession,seating_pref,name,type,content;
  public String getContent(){
	  return this.content;
  }
  //String email,password,confirmpassword,firstname,lastname,livein,age,profession,seating;
  
  public Member(String email,String password,String firstname,String lastname,String gender,String live_in,String age,
		  String profession,String seating_pref){
	  this.email=email;
	  this.password=password;
	  this.firstname=firstname;
	  this.lastname=lastname;
	  this.gender=gender;
	  this.live_in=live_in;
	  this.age=age;
	  this.profession=profession;
	  this.seating_pref=seating_pref;
	  
  }
  public Member(String email,String password,String firstname,String lastname,String gender,String live_in,String age,
		  String profession,String seating_pref,String name,String type,String content){
	  this.email=email;
	  this.password=password;
	  this.firstname=firstname;
	  this.lastname=lastname;
	  this.gender=gender;
	  this.live_in=live_in;
	  this.age=age;
	  this.profession=profession;
	  this.seating_pref=seating_pref;
	  this.name=name;
	  this.type=type;
	  this.content=content;
	}
  public void setContent(String content){
	this.content=content;  
  }
  public String getType(){
	  return this.type;
  }
  public void setType(String type){
	this.type=type;  
  }
  public String getName(){
	  return this.name;
  }
  public void setName(String name){
	this.name=name;  
  }
  public String getSeating_pref(){
	  return this.seating_pref;
  }
  public void setSeating_pref(String seating_pref){
	this.seating_pref=seating_pref;  
  }
  public String getProfession(){
	  return this.profession;
  }
  public void setProfession(String profession){
	this.profession=profession;  
  }
  public String getAge(){
	  return this.age;
  }
  public void setAge( String age){
	this.age=age;  
  }
  public String getLive_in(){
	  return this.live_in;
  }
  public void setLive_in( String live_in){
	this.live_in=live_in;  
  }
  public String getGender(){
	  return this.gender;
  }
  public void setGender( String gender){
	this.gender=gender;  
  }
  public String getLastname(){
	  return this.lastname;
  }
  public void setLastname( String lastname){
	this.lastname=lastname;  
  }
  public String getFirstname(){
	  return this.firstname;
  }
  public void setFirstname( String firstname){
	this.firstname=firstname;  
  }
  public String getPassword(){
	  return this.password;
  }
  public void setPassword( String password){
	this.password=password;  
  }
  public String getEmail(){
	  return this.email;
  }
  public void setEmail( String email){
	this.email=email;  
  }
}
