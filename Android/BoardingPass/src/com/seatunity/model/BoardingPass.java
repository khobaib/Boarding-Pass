package com.seatunity.model;

public class BoardingPass {
	String stringform,firstname,lastname,PNR,travel_from,travel_to,carrier,flight_no,julian_date,compartment_code,
	seat,departure,arrival,codetype,travel_from_name,travel_to_name,carrier_name,travel_class="";
	String id="-1";
	boolean deletestate=false;
	public BoardingPass(String travel_class, String stringform,String firstname,String lastname,String PNR,String travel_from,
			String travel_to,String carrier,String flight_no,String julian_date,String compartment_code,
			String seat,String departure,String arrival,String codetype,String id,String travel_from_name,
			String travel_to_name,String carrier_name,boolean deletestate ){
		this.travel_class=travel_class;
		this.deletestate=deletestate;
		this.carrier_name=carrier_name;
		this.travel_from_name=travel_from_name;
		this.travel_to_name=travel_to_name;
		this.codetype=codetype;
		this.stringform=stringform;
		this.firstname=firstname;
		this.lastname=lastname;
		this.PNR=PNR;
		this.travel_from=travel_from;
		this.travel_to=travel_to;
		this.carrier=carrier=carrier;
		this.flight_no=flight_no;
		this.julian_date=julian_date;
		this.compartment_code=compartment_code;
		this.seat=seat;
		this.departure=departure;
		this.arrival=arrival;
		this.id=id;
		
	}
	
	public String getTravel_class(){
		return this.travel_class;
	}
	public void setTravel_class(String travel_class){
		this.travel_class=travel_class;
	}
	
	
	
	
	public void setDeletestate(boolean deletestate){
		this.deletestate=deletestate;
	}
	public boolean getDeletestate(){
		return this.deletestate;
	}
	public void setTravel_from_name(String travel_from_name){
		this.travel_from_name=travel_from_name;
	}
	public String getTravel_from_name(){
		return this.travel_from_name;
	}
	public void setTravel_to_name(String travel_to_name){
		this.travel_to_name=travel_to_name;
	}
	public String getTravel_to_name(){
		return this.travel_to_name;
	}
	public void setCarrier_name(String carrier_name){
		this.carrier_name=carrier_name;
	}
	public String getCarrier_name(){
		return this.carrier_name;
	}

	public void setCodetype(String codetype){
		this.codetype=codetype;
	}
	public String getCodetype(){
		return this.codetype;
	}
	
	public void setId(String id){
		this.id=id;
	}
	public String getId(){
		return this.id;
	}
	
	public void setStringform(String stringform){
		this.stringform=stringform;
	}
	public String getStringform(){
		return this.stringform;
	}
	public void setFirstname(String firstname){
		this.firstname=firstname;
	}
	public String getFirstname(){
		return this.firstname;
	}
	public void setLastname(String lastname){
		this.lastname=lastname;
	}
	public String getLastname(){
		return this.lastname;
	}
	public void setPNR(String PNR){
		this.PNR=PNR;
	}
	public String getPNR(){
		return this.PNR;
	}
	public void setTravel_from(String travel_from){
		this.travel_from=travel_from;
	}
	public String getTravel_from(){
		return this.travel_from;
	}
	public void setTravel_to(String travel_to){
		this.travel_to=travel_to;
	}
	public String getTravel_to(){
		return this.travel_to;
	}
	public void setCarrier(String carrier){
		this.carrier=carrier;
	}
	public String getCarrier(){
		return this.carrier;
	}
	public void setFlight_no(String flight_no){
		this.flight_no=flight_no;
	}
	public String getFlight_no(){
		return this.flight_no;
	}
	public void setJulian_date(String julian_date){
		this.julian_date=julian_date;
	}
	public String getJulian_date(){
		return this.julian_date;
	}
	public void setCompartment_code(String compartment_code){
		this.compartment_code=compartment_code;
	}
	public String getCompartment_code(){
		return this.compartment_code;
	}
	public void setSeat(String seat){
		this.seat=seat;
	}
	public String getSeat(){
		return this.seat;
	}
	
	
	
	public void setDeparture(String departure){
		this.departure=departure;
	}
	public String getDeparture(){
		return this.departure;
	}
	public void setArrival(String arrival){
		this.arrival=arrival;
	}
	public String getArrival(){
		return this.arrival;
	}

	/*"success": "true",
"version": "1",
"bpdata": {
    "stringform": "M1HELDT/UWEMR         EYWX9ZS LWOMUCLH 2551 075M024A0008 355>2180OO3075BOS 022052227001 262202331497901  LH                     *30601001205",
    "firstname": "Uwe",
    "lastname": "Heldt",
    "PNR": "YWX9ZS",
    "travel_from": "LWO",
    "travel_to": "MUC",
    "carrier": "LH",
    "flight_no": "2551",
    "julian_date": "075",
    "compartment_code": "M",
    "seat": "24A",
    "departure": "20:00",
    "arrival": "21:10",
}*/
}
