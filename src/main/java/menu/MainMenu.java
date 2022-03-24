package menu;

import java.util.Scanner;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);
    WelcomeCustomer welcomeCustomer = new WelcomeCustomer();
    ContractRenewal contractRenewal = new ContractRenewal();

    public void welcomeMenu() {
        System.out.println("Welcome Customer\n");
        System.out.println("Press 1: For activating a new contract");
        System.out.println("Press 2: For activating a second contract");
        System.out.println("Press 3: For renewing the contract");
        System.out.println("Press 4: For finding active contracts");
        System.out.println("Press 0: To exit the application");

        int selection;

        while (!scanner.hasNextInt()) scanner.next();
        {
            selection = scanner.nextInt();
            switch (selection) {
                case 1:
                    welcomeCustomer.activateContract();
                    welcomeMenu();
                    break;
                case 2:
                    welcomeCustomer.searchCustomer();
                    welcomeMenu();
                    break;
                case 3:
                    contractRenewal.contractRenewal();
                    welcomeMenu();
                    break;
                case 4:
                    contractRenewal.findActiveContracts();
                    welcomeMenu();
                    break;
                case 0:
                    System.exit(0);
                default:
                    System.out.println("Invalid Value, Please select a number from menu");
                    welcomeMenu();
                    break;

            }
        }
    }
}
