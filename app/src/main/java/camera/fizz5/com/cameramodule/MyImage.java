package camera.fizz5.com.cameramodule;

/**
 * Created by sony on 17/10/15.
 */
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyImage {
    private String title, description, path,id,name;
    private String priority;
    public String timeStamp = new SimpleDateFormat("dd-MM-yyyy ").format(new Date());
    private long datetimeLong;
    private SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
    public MyImage(String title, String description,String name, String path,
                  String priority, long datetimeLong) {
        this.title = title;
        this.description = description;
        this.name=name;
        this.path = path;
        this.datetimeLong = datetimeLong;
        this.priority=priority;
        this.id = df.format(getDatetime().getTime());
    }
    public MyImage() {
    }
    /**
     * Gets title.
     *
     * @return Value of title.
     */
    public  String getPriority() {
        return priority;
    }
    public String getTitle() { return title; }

    public String getName() { return name; }
    /**
     * Gets datetime.
     *
     * @return Value of datetime.
     */
    public Calendar getDatetime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(datetimeLong);
        return cal;
    }
    /**
     * Sets new datetimeLong.
     *
     * @param datetimeLong New value of datetimeLong.
     */
    public void setDatetime(long datetimeLong) {
        this.datetimeLong = datetimeLong;
    }
    /**
     * Sets new datetime.
     *
     * @param datetime New value of datetime.
     */
    public void setDatetime(Calendar datetime) {
        this.datetimeLong = datetime.getTimeInMillis();
    }
    /**
     * Gets description.
     *
     * @return Value of description.
     */
    public String getDescription() { return description; }

    public void setPriority(String priority){
        this.priority=priority;
    }
    public void setTitle(String title) { this.title = title; }

    public void setName( String name ) { this.name = name; }
    /**
     * Gets datetimeLong.
     *
     * @return Value of datetimeLong.
     */
    public long getDatetimeLong() { return datetimeLong; }
    /**
     * Sets new description.
     *
     * @param description New value of description.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Sets new path.
     *
     * @param path New value of path.
     */
    public void setPath(String path) { this.path = path; }
    /**
     * Gets path.
     *
     * @return Value of path.
     */
    public String getPath() { return path; }

    @Override
    public String toString() {
        if (name == null /*|| name=="IMG_"+ timeStamp + ".jpg"*/ ) {
            return "Name: IMG_" + timeStamp + ".jpg\n" + df.format(getDatetime().getTime()) +
                    "\nReminder: " + description + "\nPriority: "+priority ;
        }
        else{
            return  "Name: " + name + "\n" + df.format(getDatetime().getTime()) +
                    "\nReminder: " + description+ "\nPriority: "+priority ;
        }
    }

}
