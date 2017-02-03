/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fish.payara.jonathan.datarepair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.naming.directory.InvalidAttributesException;

public class Parser implements Callable<ArrayList<Person>>{

	private BufferedReader in;
	private DateTimeFormatter dateFormat;
	
	public Parser(String file) throws FileNotFoundException{
		in = new BufferedReader(new FileReader(file));
		dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	}
	
	public Person readLine() throws IOException, InvalidAttributesException{
		String line = in.readLine();
		if (line==null){
			return null;
		}
		Person person = new Person();
		String[] fields = line.split(", ");
		for (String field : fields){
			String fieldName = field.split(": ")[0];
			switch (fieldName){
			case "ID":
				person.setId(Integer.parseInt(field.split(": ")[1]));
				break;
			case "Name":
				person.setName(field.split(": ")[1]);
				break;
			case "Job Title":
				person.setJobTitle(field.split(": ")[1]);
				break;
			case "DOB":
				LocalDate dob = LocalDate.parse(field.split(": ")[1], dateFormat);
				person.setDob(dob);
				break;
			case "Appearance":
				person.setAppearance(parseAppearance(field.split(": ",2)[1]));
				break;
			case "Phone Number":
				person.setPhone(field.split(": ")[1]);
				break;
			default:
				throw new InvalidAttributesException(fieldName);	
					
			}
		}
		
		
		return person;
	}
	
	private Appearance parseAppearance(String in) throws InvalidAttributesException{
		Appearance appearance = new Appearance();
		String[] parts = in.split("; ");
		for (String part : parts){
			String[] partName = part.split(": ");
			
			switch (partName[0]){
			case "Height":
				appearance.setHeight(partName[1]); break;
			case "Hair Colour":
				appearance.setHairColour(partName[1]); break;
			case "Eye Colour":
				appearance.setEyeColour(partName[1]); break;
			case "Gender":
				appearance.setGender(partName[1]);break;
			default: 
				throw new InvalidAttributesException(partName[0]);	
			}
		}
		return appearance;
	}
	
	public ArrayList<Person> readFile() throws IOException {
		ArrayList<Person> persons = new ArrayList<Person>();
		Person person = null;
		try {
			person = readLine();
			
		} catch (InvalidAttributesException e) {
			System.out.println("Invalid attributes - recieved " + e.getMessage());
			person = new Person();//just to make sure that the while loop starts if there is an error on the first line
		}
		while (person != null){	
			persons.add(person);
			try {				
				person = readLine();
			} catch (InvalidAttributesException e) {
				System.out.println("Invalid attributes - recieved " + e.getMessage());
			}
		}
		return persons;
	
}

	@Override
	public ArrayList<Person> call() throws IOException {
		return readFile();
	}
	
}