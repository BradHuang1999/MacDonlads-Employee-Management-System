import java.io.IOException;

/**
 * Manager.java
 * @author Brad Huang
 * Nov.7, 2016
 * class for manager employees
 */
public class Manager extends Employee {

	private static final int HOUR_CAP = 40;

	/**
	 * @param name
	 * @param address
	 * @param employeeID
	 * @param gender
	 * @param salary
	 * @throws IOException
	 */
	public Manager(String name, String address, String employeeID, char gender, double salary) throws IOException{		// constructor, nothing special
		super(name, address, employeeID, gender, salary);
	}

	
	/**
	 * @see Employee#isAvailable(int, int)
	 */
	@Override
	public boolean isAvailable(int day, int hour){
		if (this.getHoursWorked() < HOUR_CAP){
			try {
				return this.getAvailability()[day][hour] != 0;
			} catch (ArrayIndexOutOfBoundsException e){
				return false;
			}
		} else {
			return false;
		}
	}
}
