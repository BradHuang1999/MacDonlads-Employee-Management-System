import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Worker extends Employee{

	public Worker(String name, String address, String employeeID, char gender, double salary) throws IOException{
		super(name, address, employeeID, gender, salary);
	}

	@Override
	public void writeWorkerHourFile() throws IOException{
		String[] daysInWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
		this.setWorkHourFile(new File("workerHourFiles/" + this.getName() + " schedule.txt"));
		PrintWriter demandOut = new PrintWriter(this.getWorkHourFile());
		int startHour, endHour;
		String line;
		boolean haveWork;

		demandOut.println("******" + this.getName() + "'s Schedule******\nEmployee ID: " + this.getEmployeeID() + "\n");

		for (int i = 0; i < 7; i++){
			haveWork = false;
			for (int j: this.getWorkHours()[i]){
				if (j == 1){
					haveWork = true;
					break;
				}
			}

			if (haveWork){
				demandOut.print(daysInWeek[i] + "\t");
				startHour = 0;
				endHour = 0;
				while (endHour < 24){
					if (this.getWorkHours()[i][startHour] != this.getWorkHours()[i][endHour]){
						line = "";
						if (startHour < 10){
							line += "0";
						}
						line += startHour + ":00-";
						if (endHour < 10){
							line += "0";
						}
						line += endHour + ":00 " + this.getWorkHours()[i][startHour];
						demandOut.print("\t" + line);
						startHour = endHour;
					}
					endHour++;
				}
				line = startHour + ":00-" + endHour + ":00 " + this.getWorkHours()[i][startHour];
				demandOut.println(line);
			}
		}

		demandOut.println("Hours Worked: " + this.getHoursWorked() + "\t\tWage for the Week: " + ((double)this.getHoursWorked() * this.getSalary()));

		demandOut.close();
	}
}
