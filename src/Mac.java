import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Mac implements ReadWriteable{

	private int[][] employeeDemand;        // declare variables
	private String demandFileName, employeeFileName;
	private File demandFile;
	private File employeeFile;

	public ArrayList<Employee> employees = new ArrayList<Employee>();
	private ArrayList<Employee>[][] weeklyAvailable;
	private Employee[][][] weeklySchedule = new Employee[7][24][];

	private final int MID_HOUR = 5;        // declare a mid-hour. twice the number is the hour cap for an employee

	public Mac(String demandFileName, String employeeFileName) throws IOException{        // constructor
		this.demandFileName = demandFileName;
		this.employeeFileName = employeeFileName;

		System.out.println();
		try {
			this.readEmployeeFile();
			System.out.println("Employee file loaded.");
		} catch (FileNotFoundException e){
			System.out.println("Employee file not found. Please check.");
			System.exit(404);
		} catch (Exception e){
			System.out.println("Employee file wrong format. Please check.");
			System.exit(1);
		}

		try {
			this.readHours();
			System.out.println("Demand file loaded.");
		} catch (FileNotFoundException e){
			System.out.println("Demand file not found. Please check.");
			System.exit(404);
		} catch (Exception e){
			System.out.println("Demand file wrong format. Please check.");
			System.exit(1);
		}
	}

	public void addEmployee() throws IOException{
		System.out.println("\n******Add an Employee******\nEnter employee profile: ");

		Scanner keyIn = new Scanner(System.in);
		String type, name, address, employeeID;
		char gender;
		double salary;


		do {
			System.out.print("Employee Type: ");
			type = keyIn.nextLine().toLowerCase();
			if (!(type.equals("manager") || type.equals("worker"))){
				System.out.println("The system does not recognize the last line. Please try again.");
			}
		} while (!(type.equals("manager") || type.equals("worker")));

		System.out.print("Employee Name: ");
		name = keyIn.nextLine();

		System.out.print("Address: ");
		address = keyIn.nextLine();

		System.out.print("Employee ID: ");
		employeeID = keyIn.nextLine();

		System.out.print("Sex: ");
		gender = keyIn.nextLine().charAt(0);

		if (type.equals("manager")){
			System.out.print("Annual Salary: ");
			salary = keyIn.nextDouble();
			employees.add(new Manager(name, address, employeeID, gender, salary));        // add employee
		} else {
			System.out.print("Hourly Wage: ");
			salary = keyIn.nextDouble();
			employees.add(new Worker(name, address, employeeID, gender, salary));
		}
	}

	public void editEmployee() throws IOException{
		System.out.print("\n******Edit Employee******\nEnter Employee Name or ID: ");

		Scanner keyIn = new Scanner(System.in);
		String line;
		Employee employee;

		line = keyIn.nextLine();

		for (int i = 0; i < this.employees.size(); i++){
			employee = this.employees.get(i);
			if (line.equals(employee.getName()) || line.equals(employee.getEmployeeID())){
				int editStatus = employee.edit();

				if (editStatus == 50){                // change employee type from worker to manager
					employees.add(new Manager(employee.getName(), employee.getAddress(), employee.getEmployeeID(), employee.getGender(), employee.getSalary()));
					employees.remove(i);
				} else if (editStatus == 51){        // change employee type from manager to worker
					employees.add(new Worker(employee.getName(), employee.getAddress(), employee.getEmployeeID(), employee.getGender(), employee.getSalary()));
					employees.remove(i);
				} else if (editStatus == -1){        // remove employee
					employees.remove(i);
				}

				break;
			}
		}
	}

	public void listEmployees(){
		System.out.println("\n******Employee Information******");

		this.sortEmployeeBySalary();
		for (int i = 0; i < this.employees.size(); i++){
			this.employees.get(i).listInformation();    // list all information of employee
		}
	}

	public void schedule() throws IOException{
		System.out.print("\n******Scheduling Information******");

		Employee employee;
		int startHour, endHour;
		boolean neg;
		String[] daysInWeek = {"Monday   ", "Tuesday  ", "Wednesday", "Thursday ", "Friday   ", "Saturday ", "Sunday   "};
		this.weeklyAvailable = new ArrayList[7][24];

		File weeklySchedule = new File("weeklySchedule.txt");
		PrintWriter weeklyScheduleOut = new PrintWriter(weeklySchedule);

		weeklyScheduleOut.print("******Weekly Schedule******");

		for (int i = 0; i < 7; i++){
			for (int k = 0; k < this.employees.size(); k++){        // zero consecutive hour at the beginning of the day
				employee = this.employees.get(k);
				employee.zeroConsecHourWorked();
			}
			for (int j = 0; j < 24; j++){
				this.weeklyAvailable[i][j] = new ArrayList<Employee>();        // set up an arraylist of available employees at the timepoint
				for (int k = 0; k < this.employees.size(); k++){        // add employees to that arraylist
					employee = this.employees.get(k);
					if (employee.isAvailable(i, j)){
						this.weeklyAvailable[i][j].add(employee);
					}
				}
				this.sortEmployeeByConsecHours(i, j);        // sort the employees
				this.weeklySchedule[i][j] = new Employee[this.employeeDemand[i][j]];
				for (int n = 0; n < this.employeeDemand[i][j]; n++){        // convert the arraylist into array
					try {
						employee = this.weeklyAvailable[i][j].get(n);
						this.weeklySchedule[i][j][n] = employee;
						employee.setWorkHour(i, j);        // modificaitons on the employee
						employee.addHoursWorked();
						employee.addConsecHourWorked();
					} catch (IndexOutOfBoundsException e){
						break;
					}
				}
				this.sortEmployeeByName(i, j);        // sort the employees in the array
				if (j != 0){        // put the employees who stop works for the day at the end of the pool
					for (int k = 0; k < this.employeeDemand[i][j - 1]; k++){
						employee = this.weeklySchedule[i][j - 1][k];
						if (employee != null){
							neg = true;
							for (int n = 0; n < this.employeeDemand[i][j]; n++){
								if (this.weeklySchedule[i][j][n] == employee){
									neg = false;
									break;
								}
							}
							if (neg){
								employee.negConsecHourWorked();
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < 7; i++){        // display employees
			startHour = 0;
			endHour = 0;
			while (endHour < 24){
				if (!Arrays.equals(this.weeklySchedule[i][startHour], this.weeklySchedule[i][endHour])){
					if (this.employeeDemand[i][startHour] != 0){
						System.out.print("\n" + daysInWeek[i] + "   ");        // combine same employees into one print
						weeklyScheduleOut.print("\n" + daysInWeek[i] + "   ");
						if (startHour < 10){
							System.out.print("0");
							weeklyScheduleOut.print("0");
						}
						System.out.print(startHour + ":00-");
						weeklyScheduleOut.print(startHour + ":00-");
						if (endHour < 10){
							System.out.print("0");
							weeklyScheduleOut.print("0");
						}
						System.out.print(endHour + ":00 ");
						weeklyScheduleOut.print(endHour + ":00 ");
						for (int n = 0; n < this.employeeDemand[i][startHour]; n++){
							try {
								System.out.print(this.weeklySchedule[i][startHour][n].getName());
								weeklyScheduleOut.print(this.weeklySchedule[i][startHour][n].getName());
								if ((n != this.employeeDemand[i][startHour] - 1) && (n != this.weeklyAvailable[i][startHour].size() - 1)){
									System.out.print(", ");
									weeklyScheduleOut.print(", ");
								}
							} catch (NullPointerException e){
								this.needMoreEmployees(i, startHour, (this.employeeDemand[i][startHour] - n));        // need more employees, give recommendations
								weeklyScheduleOut.print(" (Still need " + (this.employeeDemand[i][startHour] - n) + " employees)");
								break;
							}
						}
					}
					startHour = endHour;
				}
				endHour++;
			}
			if (this.employeeDemand[i][startHour] != 0){		// single out the last one
				System.out.print("\n" + daysInWeek[i] + "   " + startHour + ":00-" + endHour + ":00 ");
				weeklyScheduleOut.print("\n" + daysInWeek[i] + "   " + startHour + ":00-" + endHour + ":00 ");
				for (int n = 0; n < this.employeeDemand[i][startHour]; n++){
					try {
						System.out.print(this.weeklySchedule[i][startHour][n].getName());
						weeklyScheduleOut.print(this.weeklySchedule[i][startHour][n].getName());
						if ((n != this.employeeDemand[i][startHour] - 1) && (n != this.weeklyAvailable[i][startHour].size() - 1)){
							System.out.print(", ");
							weeklyScheduleOut.print(", ");
						}
					} catch (NullPointerException e){
						this.needMoreEmployees(i, startHour, (this.employeeDemand[i][startHour] - n));        // need more employees, give recommendations
						weeklyScheduleOut.print(" (Still need " + (this.employeeDemand[i][startHour] - n) + " employees)");
						break;
					}
				}
			}
		}
		System.out.println();
		weeklyScheduleOut.close();
	}

	public void displayWeeklySchedule() throws IOException{		// display schedules
		System.out.println("\n******Generate Employee Weekly Schedules******");

		this.sortEmployeeByID();

		for (int i = 0; i < this.employees.size(); i++){
			try {
				this.employees.get(i).writeWorkerHourFile();        // generate employees' work hours
				System.out.println(this.employees.get(i).getName() + "'s weekly schedule generated.");
			} catch (NullPointerException e){
				System.out.println("\nNot scheduled yet. Please schedule first.");        // if not scheduled, warn user
				break;
			}
		}
	}

	public void quit() throws IOException{
		this.writeEmployeeFile();        // write files
		this.writeHours();
		System.exit(0);        // quit
	}

	private void needMoreEmployees(int day, int hour, int moreEmployeesNeeded){        // give recommendation if need more employee
		Employee employee;
		ArrayList<Employee> recommendList = new ArrayList<Employee>();
		ArrayList<Employee> employeeCopy = new ArrayList<Employee>(this.employees);
		int employeeRecommended = 0, searchDegree = 0;

		for (int i = 0; i < this.weeklySchedule[day][hour].length; i++){
			employee = this.weeklySchedule[day][hour][i];
			employeeCopy.remove(employee);
		}

		System.out.print("\n  ******Still need " + moreEmployeesNeeded + " employees.");

		while ((employeeRecommended < (moreEmployeesNeeded + 1)) && (searchDegree < 7)){        // search for employees
			searchDegree++;
			for (int i = 0; i < employeeCopy.size(); i++){
				employee = employeeCopy.get(i);
				if ((employee.isAvailable(day + searchDegree, hour)) || (employee.isAvailable(day - searchDegree, hour)) || (employee.isAvailable(day, hour + searchDegree)) || (employee.isAvailable(day, hour - searchDegree))){
					recommendList.add(employee);        // search for employees available at closer timing, and add
					employeeCopy.remove(employee);
					employeeRecommended++;
				}
			}
		}

		if (!recommendList.isEmpty()){
			System.out.print(" Try call " + recommendList.get(0).getName());
			for (int i = 1; i < recommendList.size() - 1; i++){
				System.out.print(", " + recommendList.get(i).getName());
			}
			System.out.print(" and " + recommendList.get(recommendList.size() - 1).getName() + ".");
		}

		if (employeeRecommended < moreEmployeesNeeded){
			System.out.print(" Hire more people.");
		}
	}

	private void sortEmployeeByName(int day, int hour){        // sort employees in the weekly schedule by name
		Employee employeeBuffer;
		for (int i = 0; i < this.weeklySchedule[day][hour].length - 1; i++){
			for (int j = 0; j < this.weeklySchedule[day][hour].length - i - 1; j++){
				try {
					if ((this.weeklySchedule[day][hour][j].getName().compareTo(this.weeklySchedule[day][hour][j + 1].getName())) > 0){
						employeeBuffer = this.weeklySchedule[day][hour][j];
						this.weeklySchedule[day][hour][j] = this.weeklySchedule[day][hour][j + 1];
						this.weeklySchedule[day][hour][j + 1] = employeeBuffer;
					}
				} catch (NullPointerException e){
				}
			}
		}
	}

	private void sortEmployeeByConsecHours(int day, int hour){        // sort employee in the weekly available by consecutive hours
		ArrayList<Employee> employeesTemp = this.weeklyAvailable[day][hour];
		ArrayList<Manager> managers = new ArrayList<Manager>();
		ArrayList<Worker> workers = new ArrayList<Worker>();

		for (int i = 0; i < employeesTemp.size() - 1; i++){        // first sort by salary
			for (int j = 0; j < employeesTemp.size() - i - 1; j++){
				if (employeesTemp.get(j).getSalary() > employeesTemp.get(j + 1).getSalary()){
					employeesTemp.add(j, employeesTemp.get(j + 1));
					employeesTemp.remove(j + 2);
				}
			}
		}

		while (!employeesTemp.isEmpty()){        // then sort by manager and worker
			if (employeesTemp.get(0) instanceof Manager){
				managers.add((Manager)employeesTemp.get(0));
			} else if (employeesTemp.get(0) instanceof Worker){
				workers.add((Worker)employeesTemp.get(0));
			}
			employeesTemp.remove(0);
		}

		for (int i = 0; i < managers.size() - 1; i++){
			for (int j = 0; j < managers.size() - i - 1; j++){
				if (managers.get(j).getAvailability()[day][hour] < managers.get(j + 1).getAvailability()[day][hour]){
					managers.add(j, managers.get(j + 1));
					managers.remove(j + 2);
				}
			}
		}

		for (int i = 0; i < managers.size(); i++){
			employeesTemp.add(managers.get(i));
		}

		for (int i = 0; i < workers.size() - 1; i++){
			for (int j = 0; j < workers.size() - i - 1; j++){
				if (workers.get(j).getAvailability()[day][hour] < workers.get(j + 1).getAvailability()[day][hour]){
					workers.add(j, workers.get(j + 1));
					workers.remove(j + 2);
				}
			}
		}

		for (int i = 0; i < workers.size(); i++){
			employeesTemp.add(workers.get(i));
		}

		for (int i = 0; i < employeesTemp.size() - 1; i++){        // lastly sort by consecutive hours
			for (int j = 0; j < employeesTemp.size() - i - 1; j++){
				if (Math.abs(employeesTemp.get(j).getConsecHourWorked() - this.MID_HOUR) > Math.abs(employeesTemp.get(j + 1).getConsecHourWorked() - this.MID_HOUR)){
					employeesTemp.add(j, employeesTemp.get(j + 1));
					employeesTemp.remove(j + 2);
				}
			}
		}
	}

	private void sortEmployeeBySalary(){        // sort employee in the employees arraylist by salary
		int size = this.employees.size();
		for (int i = 0; i < size - 1; i++){
			for (int j = 0; j < size - i - 1; j++){
				if (this.employees.get(j).getSalary() < this.employees.get(j + 1).getSalary()){
					this.employees.add(j, this.employees.get(j + 1));
					this.employees.remove(j + 2);
				}
			}
		}
	}

	private void sortEmployeeByID(){            // sort employee in the employees arraylist by ID
		int size = this.employees.size();
		for (int i = 0; i < size - 1; i++){
			for (int j = 0; j < size - i - 1; j++){
				if ((this.employees.get(j).getEmployeeID().compareTo(this.employees.get(j + 1).getEmployeeID())) > 0){
					this.employees.add(j, this.employees.get(j + 1));
					this.employees.remove(j + 2);
				}
			}
		}
	}

	private void readEmployeeFile() throws IOException{        // read the employee file, add employees in the arraylist
		employeeFile = new File(employeeFileName);
		Scanner employeeIn = new Scanner(this.employeeFile);
		String line, type, name, address, employeeID;
		char gender;
		double salary;

		while (employeeIn.hasNextLine()){
			line = employeeIn.nextLine();
			type = line.substring(0, line.indexOf("$"));
			line = line.substring(line.indexOf("$") + 1);

			name = line.substring(0, line.indexOf("$"));
			line = line.substring(line.indexOf("$") + 1);

			address = line.substring(0, line.indexOf("$"));
			line = line.substring(line.indexOf("$") + 1);

			employeeID = line.substring(0, line.indexOf("$"));
			line = line.substring(line.indexOf("$") + 1);

			gender = line.charAt(0);
			line = line.substring(line.indexOf("$") + 1);

			salary = Double.valueOf(line);

			if (type.equals("manager")){
				employees.add(new Manager(name, address, employeeID, gender, salary));
			} else {
				employees.add(new Worker(name, address, employeeID, gender, salary));
			}
		}
	}

	private void writeEmployeeFile() throws IOException{        // write employees in the file
		PrintWriter employeeOut = new PrintWriter(this.employeeFile);
		String line;
		Employee employee;

		for (int i = 0; i < this.employees.size(); i++){
			employee = this.employees.get(i);
			if (employee instanceof Manager){
				line = "manager$";
			} else {
				line = "worker$";
			}
			line += employee.getName() + "$" + employee.getAddress() + "$" + employee.getEmployeeID() + "$" + employee.getGender() + "$" + employee.getSalary();
			employeeOut.println(line);
		}

		employeeOut.close();
	}

	@Override
	public void readHours() throws IOException{        // read the employee demand
		demandFile = new File(demandFileName);
		Scanner demandIn = new Scanner(this.demandFile);
		String line;
		this.employeeDemand = new int[7][24];
		int day = -1, startHour, endHour, hourPoint;

		while (demandIn.hasNextLine()){
			line = demandIn.nextLine();
			if (!line.contains("-")){
				day++;
			} else {
				startHour = Integer.valueOf(line.substring(0, 2));
				endHour = Integer.valueOf(line.substring(6, 8));
				hourPoint = Integer.valueOf(line.substring(12));
				for (int i = startHour; i < endHour; i++){
					this.employeeDemand[day][i] = hourPoint;
				}
			}
		}
	}

	@Override
	public void writeHours() throws IOException{        // write the demand file in a refined format
		String[] daysInWeek = {"M", "T", "W", "R", "F", "S", "U"};
		PrintWriter demandOut = new PrintWriter(this.demandFile);
		int startHour, endHour;
		String line;

		for (int i = 0; i < 7; i++){
			demandOut.println(daysInWeek[i]);
			startHour = 0;
			endHour = 0;
			while (endHour < 24){
				if (this.employeeDemand[i][startHour] != this.employeeDemand[i][endHour]){
					line = "";
					if (startHour < 10){
						line += "0";
					}
					line += startHour + ":00-";
					if (endHour < 10){
						line += "0";
					}
					line += endHour + ":00 " + this.employeeDemand[i][startHour];
					demandOut.println(line);
					startHour = endHour;
				}
				endHour++;
			}
			line = startHour + ":00-" + endHour + ":00 " + this.employeeDemand[i][startHour];
			demandOut.println(line);
		}
		demandOut.close();
	}

}
