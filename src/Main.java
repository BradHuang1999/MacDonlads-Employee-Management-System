import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IOException{
		Mac mac = null;
		Scanner keyIn = new Scanner(System.in);
		int choice;

		System.out.println("MacDonlads Employee Management System");

		do {
			System.out.println("\nChoose files to upload:\n - 1. Test Case 1 Demand & Employees\n - 2. Test Case 2 Demand & Employees\n - 3. Test Case 3 Demand & Employees\n - 4. Brad's Demand & Employees");

			try {
				choice = Integer.valueOf(keyIn.nextLine());

				switch (choice){
					case 1:
						mac = new Mac("demandFiles/TestCase1Scehdule.txt", "employeeFiles/TestCase1Employees.txt");
						break;
					case 2:
						mac = new Mac("demandFiles/TestCase2Scehdule.txt", "employeeFiles/TestCase2Employees.txt");
						break;
					case 3:
						mac = new Mac("demandFiles/TestCase3Scehdule.txt", "employeeFiles/TestCase3Employees.txt");
						break;
					case 4:
						mac = new Mac("demandFiles/demand.txt", "employeeFiles/employees.txt");
						break;
					default:
						throw new NotBoundException();
				}
			}
			catch (Exception e){
				choice = -1;
				System.out.println("Please enter an option between 1 and 4.");
			}

		} while(choice == -1);

		do {
			System.out.println("\nChoose an option:\n - 1. Add Employee\n - 2. Edit or Remove Employee\n - 3. List all Employees\n - 4. Schedule Employees\n - 5. Display Employee Weekly Schedule/Pay\n - 6. Quit");

			try {
				choice = Integer.valueOf(keyIn.nextLine());

				switch (choice){
					case 1:
						mac.addEmployee();
						break;
					case 2:
						mac.editEmployee();
						break;
					case 3:
						mac.listEmployees();
						break;
					case 4:
						mac.schedule();
						break;
					case 5:
						mac.displayWeeklySchedule();
						break;
					case 6:
						mac.quit();
					default:
						throw new NotBoundException();
				}
			}
			catch (Exception e){
				choice = -1;
				System.out.println("Please enter an option between 1 and 6.");
			}

		} while(choice != 6);
	}

}
