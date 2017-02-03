/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fish.payara.jonathan.datarepair;

import java.util.regex.Pattern;

public class Appearance {

	private String height;
	private String hairColour;
	private String eyeColour;
	private String gender;
	
	public Appearance(){
		
	}
	
	public String getHeight() {
		return height;
	}
	public String getHairColour() {
		return hairColour;
	}
	public String getEyeColour() {
		return eyeColour;
	}
	public String getGender() {
		return gender;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public void setHairColour(String hairColour) {
		this.hairColour = hairColour;
	}
	public void setEyeColour(String eyeColour) {
		this.eyeColour = eyeColour;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	@Override
	public String toString(){
		String line = "";
		if (height != null){
			line += "Height: " + height + "; ";
		}
		if (hairColour != null){
			line += "Hair Colour: " + hairColour + "; ";
		}
		if (eyeColour != null){
			line += "Eye Colour: " + eyeColour + "; ";
		}
		if (gender != null){
			line += "Gender: " + gender;
		}
		Pattern p = Pattern.compile("; $");
		line = p.matcher(line).replaceAll("");
		
		return line;
	}
	
}
