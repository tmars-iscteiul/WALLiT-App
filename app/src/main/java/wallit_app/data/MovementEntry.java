package wallit_app.data;

import java.io.Serializable;

public class MovementEntry implements Serializable	{

    /**
     *
     */
    private static final long serialVersionUID = 2L;

    // Should we use string for the date?
    // Are we assuming the date is delivered already sorted by date (please yes)?
    // What's the most efficient way to transmit the date?
    private String date;
    private double amount;
    private double balance;

    public MovementEntry(String date, double amount, double balance)	{
        this.date = date;
        this.amount = amount;
        this.balance = balance;
    }

    // Assuming args always has 3 entries
    public MovementEntry(String args[])	{
        this.date = args[0];
        this.amount = Double.parseDouble(args[1]);
        this.balance = Double.parseDouble(args[2]);
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalance() {
        return balance;
    }

}
