import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Worker extends Employee{

	public Worker(String name, String address, String employeeID, char gender, double salary) throws IOException{		// constructor, nothing special
		super(name, address, employeeID, gender, salary);
	}

	@Override
	public void writeWorkerHourFile() throws IOException{		// write the worker hour file
        super.writeWorkerHourFile();

        PrintWriter workOutAppend = new PrintWriter(new FileWriter(this.getWorkHourFile(), true));
        workOutAppend.println("\nHours Worked: " + this.getHoursWorked() + "\t\tWage for the Week: " + ((double)this.getHoursWorked() * this.getSalary()));       // append the salary earned
        workOutAppend.close();
    }
}
