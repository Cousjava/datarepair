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

import java.io.PrintStream;
import java.util.HashMap;

public class WriteData implements Runnable{

	private HashMap<Integer, Person> people;
	private PrintStream writer;
	
	public WriteData(HashMap<Integer, Person> people, PrintStream writer){
		this.people = people;
		this.writer = writer;
	}
	
	public void writeData(){
		for (Person person : people.values()){
			writer.println(person.toString());
		}
	}

	@Override
	public void run() {
		writeData();
		
	}
}