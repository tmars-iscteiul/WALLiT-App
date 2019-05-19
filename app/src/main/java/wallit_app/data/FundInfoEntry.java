package wallit_app.data;

import java.io.Serializable;
import java.util.Date;

public class FundInfoEntry implements Serializable	{

    private static final long serialVersionUID = 4L;

    private Date date;
    private double value;

    public FundInfoEntry(Date date, double value)	{
        this.date = date;
        this.value = value;
    }

    // TODO Add a constructor that generates date from a string.
    // TODO See how we can store a variable of type java.util.Date on a JSON (If we can just store it and load it afterwards directly)

    // Constructor to add a value, setting the associated date to today
    public FundInfoEntry(double value)	{
        this.date = new Date();
        this.value = value;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public void setValue(double value) {
        this.value = value;
    }
    public Date getDate() {
        return date;
    }
    public double getValue() {
        return value;
    }
}
