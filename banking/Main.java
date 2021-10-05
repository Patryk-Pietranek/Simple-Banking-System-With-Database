package banking;

public class Main {
    public static void main(String[] args) {
        IOHandler ioHandler = new IOHandler();
        ioHandler.start(args[1]);
    }
}