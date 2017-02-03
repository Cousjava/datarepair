/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fish.payara.jonathan.datarepair;

/**
 *
 * @author jonathan
 */

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Person implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8549068751063504242L;	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	private int id;
	private String name;
	private String jobTitle;
	private LocalDate dob;
	private Appearance appearance;
	private String phone;
	
	public Person(){
		
	}
	
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getJobTitle() {
		return jobTitle;
	}
	public LocalDate getDob() {
		return dob;
	}
	public Appearance getAppearance() {
		return appearance;
	}
	public String getPhone() {
		return phone;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public void setDob(LocalDate dob) {
		this.dob = dob;
	}
	public void setAppearance(Appearance appearance) {
		this.appearance = appearance;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Override
	public String toString(){
		String line = "ID: " + id;
		if (name!= null){
			line += ", Name: " + name;
		}
		if (jobTitle != null){
			line += ", Job Title: " + jobTitle;
		}
		if (dob != null){
			line += ", DOB: " + dob.format(formatter);
		}
		if (appearance != null){
			line += ", Appearance: " + appearance.toString();
		}
		if (phone != null){
			line += ", Phone Number: " + phone;
		}
		return line;
	}
	
}
