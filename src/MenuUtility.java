import java.util.Scanner;

public class MenuUtility {
    public static void displayMenu(String[] menuOptions, String menuTitle){
        System.out.println("\n********************************************");
        System.out.println(menuTitle);
        System.out.println("********************************************");
        System.out.println("Please choose an option:");
        for (String option: menuOptions){
            System.out.println(option);
        }
        System.out.print("> ");
    }

    public static int getMenuChoice(int numOptions) {
        Scanner keyboard = new Scanner(System.in);
        int choice = keyboard.nextInt();
        while (choice < 0 || choice >= numOptions){
            System.out.printf("❌ That's not a valid choice. Please enter a number between 0 and %d.\n", numOptions - 1);
            System.out.print("> ");
            choice = keyboard.nextInt();
        }
        return choice;
    }
}