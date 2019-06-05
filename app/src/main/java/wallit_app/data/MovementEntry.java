package wallit_app.data;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A data class, containing an amount and a balance associated with a date object.
 * <p>
 * If the amount is positive, it corresponds to a deposit operation, if it's negative, it corresponds to a withdraw operation.
 * @see MovementEntryChunk
 * @author skner
 *
 */
public class MovementEntry implements Serializable	{

    private static final long serialVersionUID = 2L;

    private Date date;
    private double amount;
    private double balance;

    public MovementEntry(Date date, double amount, double balance)	{
        this.date = date;
        this.amount = amount;
        this.balance = balance;
    }

    public MovementEntry(String[] args)	{
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(args[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.amount = Double.parseDouble(args[1]);
        this.balance = Double.parseDouble(args[2]);
    }

    public MovementEntry(double amount, double balance)	{
        this.date = new Date();
        this.amount = amount;
        this.balance = balance;
    }

    public Date getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalance() {
        return balance;
    }

}

