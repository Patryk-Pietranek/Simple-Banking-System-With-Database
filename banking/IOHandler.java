package banking;

import java.util.Scanner;

public class IOHandler {

    private final Scanner scanner = new Scanner(System.in);
    private boolean exit;
    private int command;
    private Account account;
    private DBManager dbManager;

    public IOHandler() {
        this.exit = false;
        this.command = -1;
        this.account = new Account();
        this.dbManager = new DBManager();
    }

    public void start(String fileName) {
        do {
            dbManager.createDB(fileName);
            commandsInfo();
            takeCommand();
            if (account.getLogged()) {
                checkCommandLogged();
            } else {
                checkCommandNotLogged();
            }
        } while (!exit);
    }

    private void commandsInfo() {
        if (!account.getLogged()) {
            System.out.println("1. Create an account\n" +
                    "2. Log into account\n" +
                    "0. Exit");
        } else {
            System.out.println("1. Balance\n" +
                    "2. Add income\n" +
                    "3. Do transfer\n" +
                    "4. Close account\n" +
                    "5. Log out\n" +
                    "0. Exit");
        }
    }

    private void takeCommand() {
        command = scanner.nextInt();
    }

    private void checkCommandLogged() {
        if (command == 1 && account.getLogged()) {
            System.out.println("Balance: " + account.getBalance());
        } else if (command == 2 && account.getLogged()) {
            System.out.println("Enter income:");
            int income = scanner.nextInt();
            account.addIncome(income, dbManager);
            System.out.println("Income was added!");
        } else if (command == 3 && account.getLogged()) {
            System.out.println("Transfer");
            System.out.println("Enter card number:");
            String cardNumber = scanner.next();
            boolean cardNumberLuhn = account.checkCardNumberLuhn(cardNumber);
            boolean cardNumberExists = dbManager.findCardNumber(cardNumber);
            if (!cardNumberLuhn) {
                System.out.println("Probably you made a mistake in the card number. Please try again!");
            } else if (!cardNumberExists) {
                System.out.println("Such a card does not exist.");
            } else {
                System.out.println("Enter how much money you want to transfer:");
                int transferMoneyAmount = scanner.nextInt();
                boolean enoughMoney = account.isTransferPossible(transferMoneyAmount);
                if (enoughMoney) {
                    account.transferMoney(cardNumber, transferMoneyAmount, dbManager);
                    System.out.println("Success!");
                } else {
                    System.out.println("Not enough money!");
                }
            }
        } else if (command == 4 && account.getLogged()) {
            dbManager.closeAccount(account);
            account.logOut();
            System.out.println("The account has been closed!");
        } else if (command == 5 && account.getLogged()) {
            System.out.println("You have successfully logged out!");
            account.logOut();
        } else if (command == 0) {
            System.out.println("Bye!");
            exit = true;
        }
    }

    private void checkCommandNotLogged() {
        if (command == 1 && !account.getLogged()) {
            account.createAccount(dbManager);
            System.out.println("Your card has been created\n" +
                    "Your card number:\n" +
                    account.getCardNumber() + "\n" +
                    "Your card PIN:\n" +
                    account.getPin());
        } else if (command == 2 && !account.getLogged()) {
            System.out.println("Enter your card number:");
            String inputCardNumber = scanner.next();
            System.out.println("Enter your PIN:");
            String inputPin = scanner.next();
            if (inputCardNumber.matches("\\d+") && inputPin.matches("\\d+")) {
                boolean verification = account.checkData(inputCardNumber, inputPin, dbManager);
                loggingOutput(verification);
            }
        } else if (command == 0) {
            System.out.println("Bye!");
            exit = true;
        }
    }

    private void loggingOutput(boolean verification) {
        if (verification) {
            System.out.println("You have successfully logged in");
        } else {
            System.out.println("Wrong card number or PIN!");
        }
    }

}
