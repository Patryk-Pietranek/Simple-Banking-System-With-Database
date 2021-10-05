package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class DBManager {

    private String url;
    private SQLiteDataSource dataSource;


    public DBManager() {
        this.url = "";
        this.dataSource = new SQLiteDataSource();
    }

    public void createDB(String fileName) {
        this.url = "jdbc:sqlite:" + fileName;
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                        "id INTEGER PRIMARY KEY," +
                        "number TEXT NOT NULL," +
                        "pin TEXT NOT NULL," +
                        "balance INTEGER DEFAULT 0)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addToDB(Account account) {
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                int i = statement.executeUpdate("INSERT INTO card (number, pin, balance) VALUES " +
                        "('" + account.getCardNumber() + "', '" + account.getPin() +
                        "', " + account.getBalance() + ")");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean validateLoggingData(String cardNumber, String pin) {
        dataSource.setUrl(url);
        boolean valid = false;
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                try (ResultSet account = statement.executeQuery("SELECT id FROM card WHERE number = " + cardNumber + " AND pin = " + pin)) {
                    if (account.next()) {
                        valid = true;
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return valid;
    }

    public void closeAccount(Account account) {
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            String delete = "DELETE FROM card WHERE number = ? AND pin = ?";
            try (PreparedStatement preparedStatement = con.prepareStatement(delete)) {
                preparedStatement.setString(1, account.getCardNumber());
                preparedStatement.setString(2, account.getPin());

                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMoney(int income, Account account) {
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            String updateBalance = "UPDATE card SET balance = balance + ? WHERE number = ? AND pin = ?";
            try (PreparedStatement preparedStatement = con.prepareStatement(updateBalance)) {
                preparedStatement.setInt(1, income);
                preparedStatement.setString(2, account.getCardNumber());
                preparedStatement.setString(3, account.getPin());

                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void transferMoney(String cardNumber, int money) {
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            String updateReciverBalance = "UPDATE card SET balance = balance + ? WHERE number = ?";
            try (PreparedStatement preparedStatement = con.prepareStatement(updateReciverBalance)) {
                preparedStatement.setInt(1, money);
                preparedStatement.setString(2, cardNumber);

                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean findCardNumber(String cardNumber) {
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            String select = "SELECT * from card WHERE number = ?";
            try (PreparedStatement preparedStatement = con.prepareStatement(select)) {
                preparedStatement.setString(1, cardNumber);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getAccountBalance(String cardNumber, String pin) {
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            String select = "SELECT balance from card WHERE number = ? AND pin = ?";
            try (PreparedStatement preparedStatement = con.prepareStatement(select)) {
                preparedStatement.setString(1, cardNumber);
                preparedStatement.setString(2, pin);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return Integer.parseInt(resultSet.getString(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}


