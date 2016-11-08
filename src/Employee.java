import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.util.Scanner;

/**
 * Employee.java
 * @author Brad Huang
 * Nov.7, 2016
 * class for employees
 */
public abstract class Employee implements ReadWriteable{

	private String name;		// declare variables
	private String address;
	private String employeeID;
	private char gender;
	private double salary;

	private int hoursWorked;
    private int consecHourWorked;

	private File availabilityFile;
	private int[][] availability;

	private File workHourFile;
	private int[][] workHours = new int[7][24];

	/**
	 * @param name name of employee
	 * @param address address of employee
	 * @param employeeID ID of employee
	 * @param gender gender of employee
	 * @param salary salary(yearly or hourly) of employee
	 * @throws IOException when file not found
	 */
	public Employee(String name, String address, String employeeID, char gender, double salary) throws IOException{		// constructor with name, address, employeeID, gender, salary
		this.name = name;
		this.address = address;
		this.employeeID = employeeID;
		this.gender = gender;
		this.salary = salary;

		try {		// read availability file if have one
			this.readHours();
		} catch (IOException e){
			this.setAvailability();		// set up the availability if not
		}

		this.calcConsecutiveHours();	// calculate the available consecutive hours
		this.hoursWorked = 0;
	}

	/**
	 * return name of employee
	 * @return name of employee
	 */
	public String getName(){
		return name;
	}

	/**
	 * return addrees of empeloyee
	 * @return addrees of empeloyee
	 */
	public String getAddress(){
		return address;
	}

	/**
	 * return ID of employee
	 * @return ID of employee
	 */
	public String getEmployeeID(){
		return employeeID;
	}

	/**
	 * return gender of employee
	 * @return gender of employee
	 */
	public char getGender(){
		return gender;
	}

	/** 
	 * return salary of employee
	 * @return salary of employee
	 */
	public double getSalary(){
		return salary;
	}

	/**
	 * return availability of employee
	 * @return availability of employee
	 */
	public int[][] getAvailability(){
		return availability;
	}

	/**
	 * set or reset availability of employee
	 * @throws IOException when file not found
	 */
	private void setAvailability() throws IOException{		// set up the availability
		String[] daysInWeek = {"M", "T", "W", "R", "F", "S", "U"};
		Scanner keyIn = new Scanner(System.in);
		String line;
		this.availability = new int[7][24];
		int day = -1, startHour, endHour;
		boolean dayNotFound;

		System.out.println("\nPlease enter the employee's availibility in the following format:" +			// explain the format.
				"\n - Days: use \"M\", \"T\", \"W\", \"R\", \"F\", \"S\", \"U\" to represent weekdays" +	// either a letter representing the weekday
				"\n - Time: use integer hours with the format \"hh:00-hh:00\"" +							// or a time
				"\nPress enter after finishing one line. Enter \"0\" when finish.");

		do {
			line = keyIn.nextLine();
			if (!line.contains("-")){		// parse weekday
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
			} else {						// parse hour
				if (line.substring(2, 6).equals(":00-")){
					startHour = Integer.valueOf(line.substring(0, 2));
					endHour = Integer.valueOf(line.substring(6, 8));
					for (int i = startHour; i < endHour; i++){
						try {
							this.availability[day][i] = 1;		// set up hours
						} catch (ArrayIndexOutOfBoundsException e){
							System.out.println("Please enter a legitamate amount.");
							break;
						}
					}
				} else {
					System.out.println("The system does not recognize the last line. Please try again.");
				}
			}
		} while (!line.equals("0"));
		this.writeHours();		// write hour file
	}

	/**
	 * tweak the available array of the employee
	 * count the consecutive hours of employee and change to that from 1
	 */
	private void calcConsecutiveHours(){
        int pos, consecHr;
        for (int i = 0; i < 7; i++){
            for (int j = 0; j < 24; j++){
                if (this.availability[i][j] != 0){
                    pos = j;
                    do {
                        pos++;		// count the consecutive hours
                    } while ((pos != 24) && (this.availability[i][pos] != 0));
                    consecHr = pos - j;
                    for (int k = j; k < pos; k++){
                        this.availability[i][k] = consecHr;		// instead of 1, replace the availability array with the hour amount
                    }
                    j = pos;
                }
            }
        }
    }

	/**
	 * return hours worked
	 * @return hours worked
	 */
	public int getHoursWorked(){
		return hoursWorked;
	}

	/**
	 * add hours worked by one
	 */
	public void addHoursWorked(){
		this.hoursWorked++;
	}

	/**
	 * get work hour file
	 * @return work hour file
	 */
	public File getWorkHourFile(){
		return workHourFile;
	}

	/**
	 * set work hour file
	 * @param workHourFile work hour file
	 */
	public void setWorkHourFile(File workHourFile){
		this.workHourFile = workHourFile;
	}

	/**
	 * get work hours array
	 * @return work hours array
	 */
	public int[][] getWorkHours(){
		return workHours;
	}

    /**
     * get consecutive hour worked
     * @return consecutive hour worked
     */
    public int getConsecHourWorked(){
        return consecHourWorked;
    }

    /**
     * set the consecutive hours worked to 0
     */
    public void zeroConsecHourWorked(){
        this.consecHourWorked = 0;
    }

    /**
     * add the consecutive hours worked by 1
     */
    public void addConsecHourWorked(){
        this.consecHourWorked++;
    }

    /**
     * set the consecutive hours worked to -5
     */
    public void negConsecHourWorked() {
		this.consecHourWorked = -5;
    }

	/**
	 * set work hour to positive
	 * @param day the day of work hour array
	 * @param hour the hour of work hour array
	 */
	public void setWorkHour(int day, int hour){
		this.workHours[day][hour] = 1;
	}

	/**
	 * edit employee
	 * @return employee status
	 * @throws IOException when file not found
	 */
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
					case 1:		// change address
						System.out.print("Enter New Address: ");
						this.address = keyIn.nextLine();
						return 0;
					case 2:		// change employee ID
						System.out.print("Enter New Employee ID: ");
						this.employeeID = keyIn.nextLine();
						return 0;
					case 3:		// change type, done in Mac class
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
					case 4:		// change salary
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
					case 5:		// reset availability
						this.setAvailability();
						return 0;
					case 6:		// remove
						return -1;
					default:
						throw new NotBoundException();
				}
			} catch (Exception e){
				choice = -1;
				System.out.print("Please enter an option between 1 and 6.\n");
			}
		} while (choice < 1 && choice > 7);

		return 0;
	}

	/**
	 * list the information of an employee
	 */
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

	/**
	 * check if the employee is available(only for workers, overridden in the Manager class)
	 * @param day  
	 * @param hour
	 * @return
	 */
	public boolean isAvailable(int day, int hour){
		try {
			return this.availability[day][hour] != 0;
		} catch (ArrayIndexOutOfBoundsException e){
			return false;
		}
	}

	/**
	 * @throws IOException
	 */
	public void writeWorkerHourFile() throws IOException{
		String[] daysInWeek = {"Monday   ", "Tuesday  ", "Wednesday", "Thursday ", "Friday   ", "Saturday ", "Sunday   "};
		this.setWorkHourFile(new File("workerHourFiles/" + this.getName() + " schedule.txt"));
		PrintWriter workOut = new PrintWriter(this.workHourFile);
		int startHour, endHour, workHour;
		String line;
		boolean haveWork;

		workOut.println("******" + this.name + "'s Schedule******");
		
		if (this instanceof Worker){
			workOut.print("Worker");
		} else {
			workOut.print("Manager");
		}
		
		workOut.println(" \tEmployee ID: " + this.employeeID + "\n");		// print schedule

		for (int i = 0; i < 7; i++){
			haveWork = false;
			for (int j : this.workHours[i]){
				if (j == 1){
					haveWork = true;
					break;
				}
			}

			if (haveWork){      // print if only have work
				workOut.print(daysInWeek[i] + " ");
				startHour = 0;
				endHour = 0;
				while (endHour < 24){
					if (this.workHours[i][startHour] != this.workHours[i][endHour]){
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
						line += endHour + ":00 ";
						if (workHour == 1){
							line += "1 hr";
						} else {
							line += workHour + " hrs";
						}
						if (this.workHours[i][endHour - 1] != 0){
							workOut.print("   " + line);
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
				line += endHour + ":00 ";
				if (workHour == 1){
					line += "1 hr";
				} else {
					line += workHour + " hrs";
				}
				if (this.workHours[i][23] != 0){
					workOut.print("   " + line);
				}
				workOut.println();
			}
		}
		workOut.close();
	}

	/**
	 * read the availability file for employees
	 * @see ReadWriteable#readHours()
	 */
	@Override
	public void readHours() throws IOException{		// read availability hours
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

	/**
	 * write the availability file for employees
	 * @see ReadWriteable#writeHours()
	 */
	@Override
	public void writeHours() throws IOException{
		this.availabilityFile = new File("availabilityFiles/" + this.getName() + " availibility.txt");
		PrintWriter availOut = new PrintWriter(this.availabilityFile);

		int[][] rawData = this.availability;

		for (int[] rawRow : rawData){
			for (int i : rawRow){
				if (i != 0){
					availOut.print(1);
				} else {
					availOut.print(0);
				}
			}
			availOut.println();
		}

		availOut.close();
	}

}
