import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.util.Scanner;

public abstract class Employee implements ReadWriteable{

	private String name;
	private String address;
	private String employeeID;
	private char gender;
	private double salary;

	private int hourWorked;

	private File availabilityFile;
	private int[][] availability;

	private File workHourFile;
	private int[][] workHours;

	public Employee(String name, String address, String employeeID, char gender, double salary) throws IOException{
		this.name = name;
		this.address = address;
		this.employeeID = employeeID;
		this.gender = gender;
		this.salary = salary;

		try {
			this.readHours();
		} catch (IOException e){
			this.setAvailability();
		}

		this.hourWorked = 0;
	}

	public String getName(){
		return name;
	}

	public String getAddress(){
		return address;
	}

	public String getEmployeeID(){
		return employeeID;
	}

	public char getGender(){
		return gender;
	}

	public double getSalary(){
		return salary;
	}

	public int[][] getAvailability(){
		return availability;
	}

	public void setAvailability() throws IOException{
		String[] daysInWeek = {"M", "T", "W", "R", "F", "S", "U"};
		Scanner keyIn = new Scanner(System.in);
		String line;
		this.availability = new int[7][24];
		int day = -1, startHour, endHour;
		boolean dayNotFound;

		System.out.println("\nPlease enter the employee's availibility in the following format:" +
				"\n - Days: use \"M\", \"T\", \"W\", \"R\", \"F\", \"S\", \"U\" to represent weekdays" +
				"\n - Time: use integer hours with the format \"hh:00-hh:00\"" +
				"\nPress enter after finishing one line. Enter \"0\" when finish.");

		do {
			line = keyIn.nextLine();
			if (!line.contains("-")){
				dayNotFound = true;
				line = line.toUpperCase();
				for (int i = 0; i < 7; i++){
					if (line.equals(daysInWeek[i])){
						day = i;
						dayNotFound = false;
						break;
					}
				}
				if (dayNotFound && !line.equals("0")){
					System.out.println("The system does not recognize the last line. Please try again.");
				}
			} else {
				if (line.substring(2, 6).equals(":00-")){
					startHour = Integer.valueOf(line.substring(0, 2));
					endHour = Integer.valueOf(line.substring(6, 8));
					for (int i = startHour; i < endHour; i++){
						this.availability[day][i] = 1;
					}
				} else {
					System.out.println("The system does not recognize the last line. Please try again.");
				}
			}
		} while (!line.equals("0"));
		this.writeHours();
	}

	public int getHourWorked(){
		return hourWorked;
	}

	public void addHourWorked(){
		this.hourWorked++;
	}

	public File getWorkHourFile(){
		return workHourFile;
	}

	public void setWorkHourFile(File workHourFile){
		this.workHourFile = workHourFile;
	}

	public int[][] getWorkHours(){
		return workHours;
	}

	public void setWorkHours(int[][] workHours){
		this.workHours = workHours;
	}

	public int edit() throws IOException{
		Scanner keyIn = new Scanner(System.in);
		int choice;

		System.out.println("Employee Information: ");
		this.listInformation();

		do {
			System.out.println("Edit options:\n - 1. Change Address\n - 2. Change Employee ID\n - 3. Change Employee Type\n - 4. Change Salary\n - 5. Reset Availability\n - 6. Remove Employee");

			try {
				choice = Integer.valueOf(keyIn.nextLine());


				switch (choice){
					case 1:
						System.out.print("Enter New Address: ");
						this.address = keyIn.nextLine();
						return 0;
					case 2:
						System.out.print("Enter New Employee ID: ");
						this.employeeID = keyIn.nextLine();
						return 0;
					case 3:
						System.out.print("Enter New Type: ");
						String type = keyIn.nextLine().toLowerCase();
						if ((type.equals("manager") && this instanceof Worker)){
							boolean check;
							do {
								System.out.print("Enter New Salary per Year: ");
								try {
									this.salary = Double.valueOf(keyIn.nextLine());
									check = true;
								} catch (Exception e){
									System.out.println("Please enter a ligitamate amount.");
									check = false;
								}
							} while (!check);
							return 50;
						} else if (type.equals("worker") && this instanceof Manager){
							boolean check;
							do {
								System.out.print("Enter New Wage per hour: ");
								try {
									this.salary = Double.valueOf(keyIn.nextLine());
									check = true;
								} catch (Exception e){
									System.out.println("Please enter a ligitamate amount.");
									check = false;
								}
							} while (!check);
							return 51;
						} else {
							return 0;
						}
					case 4:
						boolean check;
						do {
							System.out.print("Enter New Salary: ");
							try {
								this.salary = Double.valueOf(keyIn.nextLine());
								check = true;
							} catch (Exception e){
								System.out.println("Please enter a ligitamate amount.");
								check = false;
							}
						} while (!check);
						return 0;
					case 5:
						this.setAvailability();
						return 0;
					case 6:
						return -1;
					default:
						throw new NotBoundException();
				}
			} catch (Exception e){
				choice = -1;
				System.out.print("Please enter an option between 1 and 7.\n");
			}
		} while (choice < 1 && choice > 7);

		return 0;
	}

	public void listInformation(){
		System.out.print(this.name + "\t\t");

		if (this instanceof Manager){
			System.out.print("Manager\t\t");
		} else if (this instanceof Worker){
			System.out.print("Worker\t\t");
		}

		System.out.print(this.employeeID + "\t\t$" + this.salary);

		if (this instanceof Manager){
			System.out.print(" per year\t\t");
		} else if (this instanceof Worker){
			System.out.print(" per hour\t\t");
		}

		System.out.println(this.address);
	}

	public boolean isAvailable(int day, int hour){
		return this.availability[day][hour] == 1;
	}

	public abstract void writeWorkerHourFile() throws IOException;

	@Override
	public void readHours() throws IOException{
		this.availabilityFile = new File("availabilityFiles/" + this.getName() + " availibility.txt");
		Scanner availIn = new Scanner(this.availabilityFile);
		String line;

		this.availability = new int[7][24];
		for (int i = 0; i < 7; i++){
			line = availIn.nextLine();
			for (int j = 0; j < 24; j++){
				this.availability[i][j] = Integer.parseInt(String.valueOf(line.charAt(j)));
			}
		}

		availIn.close();
	}

	@Override
	public void writeHours() throws IOException{
		this.availabilityFile = new File("availabilityFiles/" + this.getName() + " availibility.txt");
		PrintWriter availOut = new PrintWriter(this.availabilityFile);

		int[][] rawData = this.getAvailability();

		for (int[] rawRow : rawData){
			for (int i : rawRow){
				availOut.print(i);
			}
			availOut.println();
		}

		availOut.close();
	}

}
