import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Manager extends Employee {

	private static final int HOUR_CAP = 40;

	public Manager(String name, String address, String employeeID, char gender, double salary) throws IOException{		// constructor, nothing special
		super(name, address, employeeID, gender, salary);
	}

	
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
