package fish.payara.jonathan.datarepair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import org.apache.commons.cli.*;

/**
 * @author jonathan
 *
 */
public class DataRepair {

	private ArrayList<String> files;
	private String write = "output.txt";
	private String conflictWrite = "conflicts.txt";
	
	private boolean verbose = false;
	
	private String[] cmdargs;
	private Options options;	
	private ArrayList<Person> allPersons;
	private HashMap<Integer, Person> people;
	private HashMap<Integer, Person> conflicts;
	
	public static void main(String[] args){
		DataRepair dr = new DataRepair(args);
		dr.run();		
	}
	
	@SuppressWarnings({ "deprecation", "static-access" })
	public DataRepair(String[] args){
		cmdargs = args;
		options = new Options();
		options.addOption(OptionBuilder.withLongOpt("file").withDescription("The files to be processed")
		                                .hasArgs().withArgName("FILE").create('f'));
		 options.addOption(OptionBuilder.withLongOpt("verbose").withDescription("Display verbose output")
		                                .create('v'));
		 options.addOption(OptionBuilder.withLongOpt("help").withDescription("Display this help text").create('h'));
		 options.addOption(OptionBuilder.withLongOpt("output").withDescription("The location of the file to be outputted")
				 .hasArg().withArgName("FILE").create("o"));
		 allPersons = new ArrayList<Person>();
		 people = new HashMap<Integer, Person>();
		 conflicts = new HashMap<Integer, Person>();
		 files = new ArrayList<String>();
		 files.add("resources/file1.txt");
		 files.add("resources/file2.txt");
		 files.add("resources/file3.txt");
	}
	
	
	public void run(){
		 setup();
		 readFiles();
		 simplify();
		 writeData();
	}
	
	public void setup(){
		CommandLineParser clp = new DefaultParser();
		CommandLine cmd;
		try {
			 
			cmd = clp.parse(options, cmdargs);			
			if (cmd.hasOption("h")){
				displayHelp();
				System.exit(0);
			}
			
			if (cmd.hasOption("v")){
				verbose = true;
				System.out.println("Verbose output turned on");				
			}
			
			if (cmd.hasOption("f")){
				String filesIn[] = cmd.getOptionValues("f");
				//Uses files set from the command line to replace defaults.
				//Replaces from back of defaults
				for (String fileName : filesIn){
					File file = new File(fileName);
					if (!file.exists()){
						System.out.println(fileName + "does not exist");
						continue;
					}
					files.add(fileName);
				}
			}
			if (cmd.hasOption("o")){
				write = cmd.getOptionValue("o");
			}
		} catch (UnrecognizedOptionException e){
			System.out.println("Invalid option - " + e.getOption());
			displayHelp();
			System.exit(1);
			
		} catch (ParseException e) {
			System.out.println("Error reading commands from terminal");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void readFiles(){
		System.out.println("Reading files...");
		ExecutorService pool = Executors.newFixedThreadPool(3);
		Set<Future<ArrayList<Person>>> personsReturned = new HashSet<Future<ArrayList<Person>>>();;
		for (String fileName: files){
			try {
				Callable<ArrayList<Person>> callable = new Parser(fileName);
				System.out.println("Reading file... " + fileName);
				Future<ArrayList<Person>> future = pool.submit(callable);
				personsReturned.add(future);
			} catch (FileNotFoundException e) {
				System.out.println("File not found - " + fileName);
			} catch (IOException e){//I believe this code can be reached if there is some other file error
				System.out.println("Error reading file " + fileName);
				if (verbose){
					e.printStackTrace();
				}
			}
		}
		
		
		for (Future<ArrayList<Person>> filePersons : personsReturned){
			try {
				allPersons.addAll(filePersons.get());
			} catch (InterruptedException e) {
				System.out.println("Error occured in file reader");
				if (verbose){
					e.printStackTrace();
				}
			} catch (ExecutionException e) {
				System.out.println("Error occured in reading files");
				if (verbose){
					e.printStackTrace();
				}
			}
		}
		pool.shutdown();
		System.out.println("Finished reading in files");
	}
	
	public void simplify(){
		for (Person person : allPersons){
			if (person == null){
				continue;
			}
			if (people.putIfAbsent(person.getId(), person)!= null){
				try {
					Person merged = merge(people.get(person.getId()), person);
					people.put(person.getId(), merged);
				} catch (MergeConflictException e) {
					System.out.println("Error merging person");
					conflicts.put(e.getPerson1().getId(), e.getPerson1());
					conflicts.put(e.getPerson2().getId(), e.getPerson2());
					if (verbose){
						e.printStackTrace();
					}
				}
				
			}
		}
		
	}
	
	/**
	 * Merges two Person objects together. It throws an exception if there is non-identical
	 * non-null data in the same field
	 * @param person1
	 * @param person2
	 * @return
	 * @throws MergeConflictException
	 */
	public Person merge(Person person1, Person person2) throws MergeConflictException {
		Person result = new Person();
		if (person1.getId() != person2.getId()){
			throw new MergeConflictException(person1, person2, "ID");
		}
		result.setId(person1.getId());
		
		if (person1.getName() != null){
			if (person2.getName() != null && !person1.getName().equals(person2.getName())){
				throw new MergeConflictException(person1, person2, "Name");
			}
			result.setName(person1.getName());
		} else if (person2.getName() != null){
			result.setName(person2.getName());
		}
		
		if (person1.getJobTitle() != null){
			if (person2.getJobTitle() != null && !person1.getJobTitle().equals(person2.getJobTitle())){
				throw new MergeConflictException(person1, person2, "Job Title");
			}
			result.setJobTitle(person1.getJobTitle());
		} else if (person2.getJobTitle() != null){
			result.setJobTitle(person2.getJobTitle());
		}
		
		if (person1.getDob() != null){
			if (person2.getDob() != null && !person1.getDob().equals(person2.getDob())){
				throw new MergeConflictException(person1, person2, "DOB");
			}
			result.setDob(person1.getDob());
		} else if (person2.getDob() != null){
			result.setDob(person2.getDob());
		}
		if (person1.getAppearance() != null){
			if (person2.getAppearance() != null && !person1.getAppearance().equals(person2.getAppearance())){
				try {
					result.setAppearance(mergeAppearance(person1.getAppearance(), person2.getAppearance()));
				} catch (MergeConflictException e){
					e.setP1(person1);
					e.setP2(person2);
					throw e;
				}
			}
			result.setAppearance(person1.getAppearance());
		} else if (person2.getAppearance() != null){
			result.setAppearance(person2.getAppearance());
		}
		
		if (person1.getPhone() != null){
			if (person2.getPhone() != null && !person1.getPhone().equals(person2.getPhone())){
				throw new MergeConflictException(person1, person2, "Phone");
			}
			result.setPhone(person1.getPhone());
		} else if (person2.getPhone() != null){
			result.setPhone(person2.getPhone());
		}
		
		return result;
	}
	
	public Appearance mergeAppearance(Appearance apr1, Appearance apr2) throws MergeConflictException{
		Appearance result = new Appearance();
		if (apr1.getHeight()!=null){
			if (apr2.getHeight() != null && !apr1.getHeight().equals(apr2.getHeight())){
				throw new MergeConflictException("Appearance - Height");
			}
			result.setHeight(apr1.getHeight());
		} else if (apr2.getHeight() != null){
			result.setHeight(apr2.getHeight());
		}
		
		if (apr1.getHairColour()!=null){
			if (apr2.getHairColour() != null && !apr1.getHairColour().equals(apr2.getHairColour())){
				throw new MergeConflictException("Appearance - Hair Colour");
			}
			result.setHairColour(apr1.getHairColour());
		} else if (apr2.getHairColour() != null){
			result.setHairColour(apr2.getHairColour());
		}
		
		if (apr1.getEyeColour()!=null){
			if (apr2.getEyeColour() != null && !apr1.getEyeColour().equals(apr2.getEyeColour())){
				throw new MergeConflictException("Appearance - Eye Colour");
			}
			result.setEyeColour(apr1.getEyeColour());
		} else if (apr2.getEyeColour() != null){
			result.setEyeColour(apr2.getEyeColour());
		}
		
		if (apr1.getGender()!=null){
			if (apr2.getGender() != null && !apr1.getGender().equals(apr2.getGender())){
				throw new MergeConflictException("Appearance - Gender");
			}
			result.setGender(apr1.getGender());
		} else if (apr2.getGender() != null){
			result.setGender(apr2.getGender());
		}
		return result;
	}
	
	
	public void writeData(){
		System.out.println("Writing data to file");
		try {
			WriteData fileWriter = new WriteData(people, new PrintStream(new File(write)));
			fileWriter.run();
			if (verbose){
				WriteData consoleWriter = new WriteData(people, System.out);
				consoleWriter.run();
			}
		} catch (IOException e) {
			System.out.println("Error writing data");
			if (verbose){
				e.printStackTrace();
			}
		}
		if (!conflicts.isEmpty()){
		System.out.println("Writing merge conflicts to file");
		try {
			WriteData fileWriter = new WriteData(conflicts, new PrintStream(new File(conflictWrite)));
			fileWriter.run();
			if (verbose){
				WriteData consoleWriter = new WriteData(conflicts, System.out);
				consoleWriter.run();
			}
		} catch (IOException e) {
			System.out.println("Error writing data");
			if (verbose){
				e.printStackTrace();
			}
		}
		}
		System.out.println("Finished writing files");
	}
	
	public void displayHelp(){
		 HelpFormatter help = new HelpFormatter();
		 String usage = "java -jar daterepair.jar [-h] [-v] [-f <FILE> ... ] [-o <FILE>] [-t FILETYPE]";
		 help.printHelp(usage, options);
	}
	
	
}
