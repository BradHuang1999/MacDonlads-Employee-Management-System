import java.io.IOException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IOException{
		Mac mac = new Mac();
		Scanner keyIn = new Scanner(System.in);
		int choice;

		System.out.println("MacDonlads Employee Management System");

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
						System.out.print("Please enter an option between 1 and 6.\n");
						break;
				}
			} catch (Exception e){
				choice = -1;
				System.out.print("Please enter an option between 1 and 6.\n");
			}

		} while(choice != 6);
	}

}
