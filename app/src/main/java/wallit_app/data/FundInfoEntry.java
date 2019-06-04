package wallit_app.data;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FundInfoEntry implements Serializable	{

    private static final long serialVersionUID = 4L;

    private Date date;
    private double value;

    public FundInfoEntry(Date date, double value)	{
        this.date = date;
        this.value = value;
    }

    // Constructor to initialize an object, with a String for the date
    public FundInfoEntry(String date, double value)	{
        try {
            this.date = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.value = value;
    }

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
