import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Mac implements ReadWriteable{
	
	private int[][] employeeDemand;
	private File demandFile = new File("demand.txt");
    private File employeeFile = new File("employees.txt");

    public ArrayList<Employee> employees = new ArrayList();
	private ArrayList<Employee>[][] weeklyAvailable;
	private Employee[][][] weeklySchedule = new Employee[7][24][];

	public Mac() throws IOException{
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
        Scanner keyIn = new Scanner(System.in);
        String type, name, address, employeeID;
        char gender;
        double salary;

        System.out.println("\n******Add an Employee******\nEnter employee profile: ");

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
        Scanner keyIn = new Scanner(System.in);
        String line;
        Employee employee;

        System.out.print("\n******Edit Employee******\nEnter Employee Name or ID: ");
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
		this.sortEmployeeBySalary();
        for (int i = 0; i < this.employees.size(); i++){
            this.employees.get(i).listInformation();
        }
	}

	public void schedule(){
		// TODO
	}

	public void displayWeeklySchedule() throws IOException{
        this.sortEmployeeByID();
        for (int i = 0; i < this.employees.size(); i++){
            try {
                this.employees.get(i).writeWorkerHourFile();
            } catch (NullPointerException e){
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
