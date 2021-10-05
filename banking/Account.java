package banking;

import java.util.Random;

public class Account {

    private String cardNumber;
    private String pin;
    private int balance;
    private boolean logged;

    public Account() {
        this.cardNumber = "";
        this.pin = "";
        this.balance = 0;
        this.logged = false;
    }

    public Account(String cardNumber, String pin, int balance, boolean logged) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.balance = balance;
        this.logged = logged;
    }

    public void createAccount(DBManager dbManager) {
        generatePin();
        generateCardNumber();
        dbManager.addToDB(this);
    }

    private void generatePin() {
        this.pin = "";
        Random random = new Random();
        int pinDigits = 4;
        for (int i = 0; i < pinDigits; i++) {
            this.pin += Integer.toString(random.nextInt(10));
        }
    }

    private void generateCardNumber() {
        Random random = new Random();
        int cardNumberDigits = 16;
        this.cardNumber = "400000";
        for (int i = cardNumber.length(); i < cardNumberDigits - 1; i++) {
            this.cardNumber += Integer.toString(random.nextInt(10));
        }
        this.cardNumber += generateCardNumberLuhn();
    }

    public void addIncome(int income, DBManager dbManager) {
        this.balance += income;
        dbManager.addMoney(income, this);
    }

    public void transferMoney(String cardNumber, int transferMoneyAmount, DBManager dbManager) {
        this.balance -= transferMoneyAmount;
        dbManager.addMoney(-transferMoneyAmount, this);
        dbManager.transferMoney(cardNumber, transferMoneyAmount);
    }


    public boolean checkCardNumberLuhn(String cardNumber) {
        int sum = 0;
        char lastDigit = cardNumber.charAt(cardNumber.length() - 1);
        for (int i = 0; i < cardNumber.length() - 1; i++) {
            int number = Character.getNumericValue(cardNumber.charAt(i));
            if ((i + 1) % 2 == 1) {
                number *= 2;
            }
            if (number > 9) {
                number -= 9;
            }
            sum += number;
        }
        sum += Character.getNumericValue(lastDigit);
        return sum % 10 == 0;
    }


    private char generateCardNumberLuhn() {
        int sum = 0;
        for (int i = 0; i < cardNumber.length(); i++) {
            int number = Character.getNumericValue(cardNumber.charAt(i));
            if ((i + 1) % 2 == 1) {
                number *= 2;
            }
            if (number > 9) {
                number -= 9;
            }
            sum += number;
        }
        return Character.forDigit(findCheckSum(sum), 10);
    }

    private int findCheckSum(int sum) {
        int x = 0;
        while (true) {
            int tempSum = sum;
            tempSum += x;
            if (tempSum % 10 == 0) {
                break;
            }
            x++;
        }
        return x;
    }

    public boolean checkData(String cardNumber, String pin, DBManager dbManager) {
        boolean found = dbManager.validateLoggingData(cardNumber, pin);
        if (found) {
            this.cardNumber = cardNumber;
            this.pin = pin;
            this.balance = dbManager.getAccountBalance(this.cardNumber, this.pin);
            logged = true;
        }
        return found;
    }

    public boolean isTransferPossible(int money) {
        return balance - money > 0;
    }

    public int getBalance() {
        return balance;
    }

    public boolean getLogged() {
        return logged;
    }

    public void logOut() {
        this.logged = false;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

}
