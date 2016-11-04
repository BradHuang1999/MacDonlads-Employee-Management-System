import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Mac implements ReadWriteable{

	private int[][] employeeDemand;
	private String demandFileName, employeeFileName;
	private File demandFile;
	private File employeeFile;

	public ArrayList<Employee> employees = new ArrayList<Employee>();
	private ArrayList<Employee>[][] weeklyAvailable;
	private Employee[][][] weeklySchedule = new Employee[7][24][];

	private final int MID_HOUR = 5;

	public Mac(String demandFileName, String employeeFileName) throws IOException{
		this.demandFileName = demandFileName;
		this.employeeFileName = employeeFileName;

		System.out.println();
		try {
			this.readEmployeeFile();
			System.out.println("Employee file loaded.");
		} catch (FileNotFoundException e) {
			System.out.println("Employee file not found. Please check.");
			System.exit(404);
		} catch (Exception e) {
			System.out.println("Employee file wrong format. Please check.");
			System.exit(1);
		}

		try {
			this.readHours();
			System.out.println("Demand file loaded.");
		} catch (FileNotFoundException e) {
			System.out.println("Demand file not found. Please check.");
			System.exit(404);
		} catch (Exception e) {
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
				System.out.print("The system does not recognize the last line. Please try again.");
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
			employees.add(new Manager(name, address, employeeID, gender, salary));
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

				if (editStatus == 50){
					employees.add(new Manager(employee.getName(), employee.getAddress(), employee.getEmployeeID(), employee.getGender(), employee.getSalary()));
					employees.remove(i);
				} else if (editStatus == 51){
					employees.add(new Worker(employee.getName(), employee.getAddress(), employee.getEmployeeID(), employee.getGender(), employee.getSalary()));
					employees.remove(i);
				} else if (editStatus == -1){
					employees.remove(i);
				}

				break;
			}
		}
	}

	public void listEmployees(){
		System.out.println("\n******Employee Information******\n");

		this.sortEmployeeBySalary();
		for (int i = 0; i < this.employees.size(); i++){
			this.employees.get(i).listInformation();
		}
	}

	public void schedule(){
		System.out.print("\n******Scheduling Information******\n");

		Employee employee;
		String line;
		boolean zero;
		String[] daysInWeek = {"Monday   ", "Tuesday  ", "Wednesday", "Thursday ", "Friday   ", "Saturday ", "Sunday   "};
		this.weeklyAvailable = new ArrayList[7][24];

		for (int i = 0; i < 7; i++){
			for (int j = 0; j < 24; j++){
				this.weeklyAvailable[i][j] = new ArrayList<Employee>();
				for (int k = 0; k < this.employees.size(); k++){
					employee = this.employees.get(k);
					if (employee.isAvailable(i, j)){
						this.weeklyAvailable[i][j].add(employee);
					}
					zero = j == 0;
					if (!zero){
						zero = true;
						for (int m = 0; m < this.employeeDemand[i][j - 1]; m++){
							if (this.weeklySchedule[i][j - 1][m].equals(employee)){
								zero = false;
								break;
							}
						}
					}
					if (zero && employee.getConsecHourWorked() > 0){
						employee.zeroConsecHourWorked();
					}
				}
				this.sortEmployeeByConsecHours(i, j);
				this.weeklySchedule[i][j] = new Employee[this.employeeDemand[i][j]];
				for (int n = 0; n < this.employeeDemand[i][j]; n++){
					try{
						employee = this.weeklyAvailable[i][j].get(n);
						this.weeklySchedule[i][j][n] = employee;
						employee.setWorkHour(i, j);
						employee.addHoursWorked();
						employee.addConsecHourWorked();
//					employee.checkConsecHourWorked();
					} catch (IndexOutOfBoundsException e){
						break;
					}
				}
			}
		}
		
		for (int i = 0; i < 7; i++){
			for (int j = 0; j < 24; j++){
				if (this.employeeDemand[i][j] != 0){
					line = "\n" + daysInWeek[i] + " ";
					if (j < 10){
						line += "0";
					}
					line += j + ":00-";
					if (j < 9){
						line += "0";
					}
					line += (j + 1) + ":00 ";
					System.out.print(line + "  \t");

					for (int n = 0; n < this.employeeDemand[i][j]; n++){
						try{
							System.out.print(this.weeklySchedule[i][j][n].getName());
							if ((n != this.employeeDemand[i][j] - 1) && (n != this.weeklyAvailable[i][j].size() - 1)){
								System.out.print(", ");
							}
						} catch (NullPointerException e){
							this.needMoreEmployees(i, j, (this.employeeDemand[i][j] - n));
							break;
						}
					}
				}

//				if (this.employeeDemand[i][j] != 0){
//					line = "\n" + daysInWeek[i] + " ";
//					if (j < 10){
//						line += "0";
//					}
//					line += j + ":00-";
//					if (j < 9){
//						line += "0";
//					}
//					line += (j + 1) + ":00 ";
//					System.out.print(line + "  \t");
//
//					for (int n = 0; n < this.weeklyAvailable[i][j].size(); n++){
//						System.out.print(this.weeklyAvailable[i][j].get(n).getName() + this.weeklyAvailable[i][j].get(n).getConsecHourWorked());
//						if (n != this.weeklyAvailable[i][j].size() - 1){
//							System.out.print(", ");
//						}
//					}
//				}
			}
		}

		System.out.println();
	}

	public void displayWeeklySchedule() throws IOException{
		System.out.println("\n******Generate Employee Weekly Schedules******\n");

		this.sortEmployeeByID();

		for (int i = 0; i < this.employees.size(); i++){
			try {
				this.employees.get(i).writeWorkerHourFile();
				System.out.println(this.employees.get(i).getName() + "'s weekly schedule generated.");
			}
			catch (NullPointerException e){
				System.out.println("\nNot scheduled yet. Please schedule first.");
				break;
			}
		}
	}

	public void quit() throws IOException{
		this.writeEmployeeFile();
		this.writeHours();
		System.exit(0);
	}
	
	private void needMoreEmployees(int day, int hour, int moreEmployeesNeeded){
		System.out.print("\n******Still need " + moreEmployeesNeeded + " employees. ");
	}

	private void sortEmployeeByConsecHours(int day, int hour){
		ArrayList<Employee> employeesTemp = this.weeklyAvailable[day][hour];
		ArrayList<Manager> managers = new ArrayList<Manager>();
		ArrayList<Worker> workers = new ArrayList<Worker>();
		
		for (int i = 0; i < employeesTemp.size() - 1; i++){
			for (int j = 0; j < employeesTemp.size() - i - 1; j++){
				if (employeesTemp.get(j).getSalary() > employeesTemp.get(j + 1).getSalary()){
					employeesTemp.add(j, employeesTemp.get(j + 1));
					employeesTemp.remove(j + 2);
				}
			}
		}

		while(!employeesTemp.isEmpty()){
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

		for (int i = 0; i < employeesTemp.size() - 1; i++){
			for (int j = 0; j < employeesTemp.size() - i - 1; j++){
				if (Math.abs(employeesTemp.get(j).getConsecHourWorked() - this.MID_HOUR) > Math.abs(employeesTemp.get(j + 1).getConsecHourWorked() - this.MID_HOUR)){
					employeesTemp.add(j, employeesTemp.get(j + 1));
					employeesTemp.remove(j + 2);
				}
			}
		}
	}

	private void sortEmployeeBySalary(){
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

	private void sortEmployeeByID(){
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

	private void readEmployeeFile() throws IOException{
		employeeFile = new File(employeeFileName);
		Scanner employeeIn = new Scanner (this.employeeFile);
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

	private void writeEmployeeFile() throws IOException{
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
	public void readHours() throws IOException{
		demandFile = new File(demandFileName);
		Scanner demandIn = new Scanner (this.demandFile);
		String line;
		this.employeeDemand = new int[7][24];
		int day = -1, startHour, endHour, hourPoint;

		while (demandIn.hasNextLine()){
			line = demandIn.nextLine();
			if (!line.contains("-")){
				day++;
			} else {
				startHour = Integer.valueOf(line.substring(0,2));
				endHour = Integer.valueOf(line.substring(6,8));
				hourPoint = Integer.valueOf(line.substring(12));
				for (int i = startHour; i < endHour; i++){
					this.employeeDemand[day][i] = hourPoint;
				}
			}
		}
	}

	@Override
	public void writeHours() throws IOException{
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
