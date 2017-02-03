package fish.payara.jonathan.datarepair;

public class MergeConflictException extends Exception {

	private Person p1;
	private Person p2;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5130737589837509720L;

	public MergeConflictException(){
		
	}
	
	public MergeConflictException(String string) {
		super(string);
	}
	
	public MergeConflictException(Person p1, Person p2){
		super();
		this.p1 = p1;
		this.p2 = p2;
	}

	public MergeConflictException(Person p1, Person p2, String message){
		super(message);
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public Person getPerson1(){
		return p1;
	}
	
	public Person getPerson2(){
		return p2;
	}

	public void setP1(Person p1) {
		this.p1 = p1;
	}

	public void setP2(Person p2) {
		this.p2 = p2;
                
               
                
	}
	
}
