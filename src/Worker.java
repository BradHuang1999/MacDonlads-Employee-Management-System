import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Worker extends Employee{

	public Worker(String name, String address, String employeeID, char gender, double salary) throws IOException{
		super(name, address, employeeID, gender, salary);
	}

	@Override
	public void writeWorkerHourFile() throws IOException{
        String[] daysInWeek = {"Monday   ", "Tuesday  ", "Wednesday", "Thursday ", "Friday   ", "Saturday ", "Sunday   "};
        this.setWorkHourFile(new File("workerHourFiles/" + this.getName() + " schedule.txt"));
        PrintWriter demandOut = new PrintWriter(this.getWorkHourFile());
        int startHour, endHour, workHour;
        String line;
        boolean haveWork;

        demandOut.println("******" + this.getName() + "'s Schedule******\nWorker \tEmployee ID: " + this.getEmployeeID() + "\n");

        for (int i = 0; i < 7; i++){
            haveWork = false;
            for (int j : this.getWorkHours()[i]){
                if (j == 1){
                    haveWork = true;
                    break;
                }
            }

            if (haveWork){
                demandOut.print(daysInWeek[i] + " ");
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
                        workHour = endHour - startHour;
                        startHour = endHour;
                        line += endHour + ":00 " + workHour;
                        if (this.getWorkHours()[i][endHour - 1] != 0){
                            demandOut.print("   " + line);
                        }
                    }
                    endHour++;
                }
                workHour = endHour - startHour;
                line = "";
                if (startHour < 10){
                    line += "0";
                }
                line += startHour + ":00-";
                line += endHour + ":00 " + workHour;
                if (this.getWorkHours()[i][23] != 0){
                    demandOut.print("   " + line);
                }
                demandOut.println();
            }
        }

		demandOut.println("Hours Worked: " + this.getHoursWorked() + "\t\tWage for the Week: " + ((double)this.getHoursWorked() * this.getSalary()));

		demandOut.close();
	}
}
