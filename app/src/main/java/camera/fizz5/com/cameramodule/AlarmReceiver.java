package camera.fizz5.com.cameramodule;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
public class AlarmReceiver extends BroadcastReceiver {
    int NotiId = 123;
    PendingIntent resultPendingIntent, deletePendingIntent;
    private NotificationManager myGoalNotifyMgr;
    NotificationCompat.Builder gBuilder;
    private SQLiteDatabase database;
    private DBhelper dbHelper;
    private int currentYear, currentMonth, currentDay;
    public String timeStamp = new SimpleDateFormat("dd-MM-yyyy hh:mm").format(new Date());
    public String fname = "IMG_" + timeStamp + ".jpg";
    public int count1,count2;
    private MainActivity ma;
    public int flag=0;
    ArrayList<Integer> mon = new ArrayList<Integer>(Arrays.asList(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31));
    ArrayList<String> monStr = new ArrayList<String>(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"));
    int calMonthDay(int m, int y) {//calMonthDay(month,year)
        int x = 0, c;
        for (c = 1; c < m; c++) {// Jan to less than the month 'm' as 'm' we are not taking the the whole days of that month
            if (c == 2) {//if Feb
                if (y % 4 == 0)//checks if year is leap or not
                    x += 29;
                else
                    x += 28;
            } else
                x += mon.get(c - 1);
        }
        return (x);
    }
    //calculates no. of months from current month & year to goal month & year
    int calDateMonth(int mC, int yC, int mG, int yG) {//(current-month, current-year, goal-month, goal-year)
        int x = 0, i, countM = 0;
        if (yC <= yG) {
            for (i = yC; i < yG; i++)
                countM += 12;
        }
        countM -= mC;
        countM += mG;
        return (countM);
    }
    //calculates no. of weeks from current month & year to goal month & year
    int calDateWeek(int mC, int yC, int mG, int yG) {
        int x = 0, i, countW = 0;
        if (yC <= yG) {
            for (i = yC; i < yG; i++)
                countW += 52;
        }
        countW -= mC;
        countW += mG;
        countW *= 4;
        return (countW);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        dbHelper = new DBhelper(context);
        database = dbHelper.getWritableDatabase();
        int IDcnt = 0;
        ArrayList<String> MyImageDescription = new ArrayList<String>();
        ArrayList<Integer> MyImageCount = new ArrayList<Integer>();
        ArrayList<String> MyImageTitle = new ArrayList<String>();
        ArrayList<Integer> MyImageID = new ArrayList<Integer>();
        ArrayList<String> MyImagePriority = new ArrayList<String>();
        ArrayList<String> UpdatedName = new ArrayList<String>();
        MyImageCount.clear();
        MyImageDescription.clear();
        MyImageID.clear();
        MyImageTitle.clear();
        MyImagePriority.clear();
        UpdatedName.clear();
        Cursor cursor = database.query(DBhelper.TABLE_NAME, null, null, null, null, null, DBhelper.COLUMN_DATETIME + " DESC");
        if (cursor.moveToFirst()) {
            do {
                IDcnt++;
                //MyImage MyImage = cursorToMyImage(cursor);
                String dateDesc = cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_DESCRIPTION));
                String upImageName= cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_NAME));
                String ImageTitle=cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_TITLE));
                /*Toast.makeText(context," "+ImageTitle,Toast.LENGTH_SHORT).show();*/
                try {
                    if(dateDesc!=null){
                        if(upImageName!=null) {
                            String[] DummyGoalDate = dateDesc.split("-");
                            String[] DummyTitle = ImageTitle.split("/");
                            String nameTitle= DummyTitle[6];
                            int goalYear = Integer.parseInt(DummyGoalDate[2]), goalMonth = Integer.parseInt(DummyGoalDate[1]), goalDay = Integer.parseInt(DummyGoalDate[0]);
                            count1 = -1;
                            count2 = 1;
                            Calendar c = Calendar.getInstance();
                            int curYear = c.get(Calendar.YEAR), curMonth = c.get(Calendar.MONTH) + 1, curDay = c.get(Calendar.DAY_OF_MONTH);
                            //Fetches the date and Time from system, hence not used
                            if (curYear <= goalYear || (goalYear == curYear && goalMonth >= curMonth) || (goalYear == curYear && goalMonth == curMonth && goalDay >= curDay)) {
                                count1 = 0;
                                int i;
                                for (i = curYear; i < goalYear; i++) {
                                    if (i % 4 == 0) {
                                        count1 += 366;//Leap year
                                    } else {
                                        count1 += 365;// Non leap year
                                    }
                                }
                                //calculating the no of days left from current date to goal date
                                count1 -= calMonthDay(curMonth, curYear);
                                count1 -= curDay;
                                count1 += calMonthDay(goalMonth, goalYear);
                                count1 += goalDay;
                                if (count1 < 0) {
                                    count1 *= -1;
                                }
                                if (count1 <= 2 && count1 >= 0) {// if difference betn goal & current date is less than 2
                                    MyImageID.add(IDcnt);// for ID
                                    MyImageCount.add(count1);// for days left
                                    MyImageDescription.add(cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_DESCRIPTION)));// for description
                                    MyImageTitle.add(nameTitle);
                                    UpdatedName.add(cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_NAME)));// for title

                                }
                                //cursor.moveToNext();
                                MyImagePriority.add(cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_PRIORITY)));// for priority

                                if (MyImagePriority.get(0).equals("OFF")) {

                                    int x = goalMonth + 3;
                                    int y = goalYear;
                                    Toast.makeText(context, " Initial" + x, Toast.LENGTH_SHORT).show();
                                    if (x > 12) {
                                        x = x - 12;
                                        y++;
                                        Toast.makeText(context, " year " + y, Toast.LENGTH_SHORT).show();
                                        Toast.makeText(context, " curmonth " + curMonth, Toast.LENGTH_SHORT).show();
                                        if ((curMonth == x) || (curYear == y)) {
                                            MyImageID.add(IDcnt);// for ID
                                            MyImageDescription.add(cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_DESCRIPTION)));// for description
                                            MyImageTitle.add(nameTitle);
                                            UpdatedName.add(cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_NAME)));// for title
                                            flag = 1;
                                        }
                                    } else {
                                        if (/*(curDay==goalDay + 90 || curDay==goalDay + 91) && */(curMonth == x)) {

                                            MyImageID.add(IDcnt);// for ID
                                            MyImageDescription.add(cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_DESCRIPTION)));// for description
                                            MyImageTitle.add(nameTitle);
                                            UpdatedName.add(cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_NAME)));// for title
                                            flag = 1;
                                        }
                                    }
                                }
                            } else {
                                MyImagePriority.add(cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_PRIORITY)));// for priority

                                if (MyImagePriority.get(0).equals("OFF")) {
                                    int x = goalMonth + 3;
                                    int y = goalYear;
                                    Toast.makeText(context, " Initial" + x, Toast.LENGTH_SHORT).show();
                                    if (x > 12) {
                                        x = x - 12;
                                        Toast.makeText(context, " year " + y, Toast.LENGTH_SHORT).show();
                                        Toast.makeText(context, " curmonth " + curMonth, Toast.LENGTH_SHORT).show();
                                        if ((curMonth == x)) {
                                            MyImageID.add(IDcnt);// for ID
                                            MyImageDescription.add(cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_DESCRIPTION)));// for description
                                            MyImageTitle.add(nameTitle);
                                            UpdatedName.add(cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_NAME)));// for title
                                            flag = 1;
                                        }
                                    }
                                }
                            }
                        }
                        else{
                            String[] DummyGoalDate = dateDesc.split("-");
                            String[] DummyTitle = ImageTitle.split("/");
                            String nameTitle= DummyTitle[6];
                            int goalYear = Integer.parseInt(DummyGoalDate[2]), goalMonth = Integer.parseInt(DummyGoalDate[1]), goalDay = Integer.parseInt(DummyGoalDate[0]);
                            count1 = -1;
                            count2 = 1;
                            Calendar c = Calendar.getInstance();
                            int curYear = c.get(Calendar.YEAR), curMonth = c.get(Calendar.MONTH) + 1, curDay = c.get(Calendar.DAY_OF_MONTH);
                            //Fetches the date and Time from system, hence not used
                            if (curYear <= goalYear || (goalYear == curYear && goalMonth >= curMonth) || (goalYear == curYear && goalMonth == curMonth && goalDay >= curDay)) {
                                count1 = 0;
                                int i;
                                for (i = curYear; i < goalYear; i++) {
                                    if (i % 4 == 0) {
                                        count1 += 366;//Leap year
                                    } else {
                                        count1 += 365;// Non leap year
                                    }
                                }
                                //calculating the no of days left from current date to goal date
                                count1 -= calMonthDay(curMonth, curYear);
                                count1 -= curDay;
                                count1 += calMonthDay(goalMonth, goalYear);
                                count1 += goalDay;
                                if (count1 < 0) {
                                    count1 *= -1;
                                }
                                if (count1 <= 2 && count1 >= 0) {// if difference betn goal & current date is less than 2
                                    MyImageID.add(IDcnt);// for ID
                                    MyImageCount.add(count1);// for days left
                                    MyImageDescription.add(cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_DESCRIPTION)));// for description
                                    MyImageTitle.add(nameTitle);// for title

                                }
                                //cursor.moveToNext();
                                MyImagePriority.add(cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_PRIORITY)));// for priority

                                if (MyImagePriority.get(0).equals("OFF")) {

                                    int x = goalMonth + 3;
                                    int y = goalYear;
                                    Toast.makeText(context, " Initial" + x, Toast.LENGTH_SHORT).show();
                                    if (x > 12) {
                                        x = x - 12;
                                        y++;
                                        Toast.makeText(context, " year " + y, Toast.LENGTH_SHORT).show();
                                        Toast.makeText(context, " curmonth " + curMonth, Toast.LENGTH_SHORT).show();
                                        if ((curMonth == x) || (curYear == y)) {
                                            MyImageID.add(IDcnt);// for ID
                                            MyImageDescription.add(cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_DESCRIPTION)));// for description
                                            MyImageTitle.add(nameTitle);// for title
                                            flag = 1;
                                        }
                                    } else {
                                        if (/*(curDay==goalDay + 90 || curDay==goalDay + 91) && */(curMonth == x)) {

                                            MyImageID.add(IDcnt);// for ID
                                            MyImageDescription.add(cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_DESCRIPTION)));// for description
                                            MyImageTitle.add(nameTitle);// for title
                                            flag = 1;
                                        }
                                    }
                                }
                            } else {
                                MyImagePriority.add(cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_PRIORITY)));// for priority

                                if (MyImagePriority.get(0).equals("OFF")) {
                                    int x = goalMonth + 3;
                                    int y = goalYear;
                                    Toast.makeText(context, " Initial" + x, Toast.LENGTH_SHORT).show();
                                    if (x > 12) {
                                        x = x - 12;
                                        Toast.makeText(context, " year " + y, Toast.LENGTH_SHORT).show();
                                        Toast.makeText(context, " curmonth " + curMonth, Toast.LENGTH_SHORT).show();
                                        if ((curMonth == x)) {
                                            MyImageID.add(IDcnt);// for ID
                                            MyImageDescription.add(cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_DESCRIPTION)));// for description
                                            MyImageTitle.add(nameTitle);// for title
                                            flag = 1;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }catch(ArrayIndexOutOfBoundsException ae){
                    ae.printStackTrace();
                }
            } while (cursor.moveToNext());
            cursor.close();
            if(MyImageDescription.size() > 0 && flag==1) {
                    myGoalNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Intent delIntent = new Intent(context, MainActivity.class);
                    delIntent.setAction("Del");
                    int ID = MyImageID.get(0);
                    delIntent.putExtra("DEL", ID);
                    delIntent.putExtra("update", true);
                    delIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    Intent resultIntent = new Intent(context, MainActivity.class);
                    //resultIntent.putExtra("ID", mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.KEY_ID)));
                    resultIntent.putExtra("update", true);
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    PendingIntent resultPendingIntent1 = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    PendingIntent delPendingIntent = PendingIntent.getActivity(context, 0, delIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    if (MyImageDescription.size() == 1) {
                        int cnt = MyImageDescription.size();
                        if(UpdatedName.size()>0) {
                            gBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Fizz")
                                    .setContentText("The reminder date for " + UpdatedName.get(0) + " has exceeded the 3 months limit,press the delete button to delete the image")
                                    .addAction(R.drawable.ic_action_discard, "Delete", delPendingIntent);
                            gBuilder.setContentIntent(delPendingIntent);
                            String summary = String.valueOf(cnt) + " goals";
                            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                            inboxStyle.setSummaryText(summary);
                            gBuilder.setPriority(Notification.PRIORITY_HIGH);// [-2,2]->[PRIORITY_MIN,PRIORITY_MAX]
                            gBuilder.setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).
                                    setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).
                                    setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}).setLights(Color.WHITE, 0, 1);
                            gBuilder.setAutoCancel(true);
                            myGoalNotifyMgr.notify(MyImageID.get(0), gBuilder.build());
                        }
                        else{
                            gBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Fizz")
                                    .setContentText("The reminder date for " + MyImageTitle.get(0) + " has exceeded the 3 months limit,press the delete button to delete the image")
                                    .addAction(R.drawable.ic_action_discard, "Delete", delPendingIntent);
                            gBuilder.setContentIntent(delPendingIntent);
                            String summary = String.valueOf(cnt) + " goals";
                            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                            inboxStyle.setSummaryText(summary);
                            gBuilder.setPriority(Notification.PRIORITY_HIGH);// [-2,2]->[PRIORITY_MIN,PRIORITY_MAX]
                            gBuilder.setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).
                                    setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).
                                    setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}).setLights(Color.WHITE, 0, 1);
                            gBuilder.setAutoCancel(true);
                            myGoalNotifyMgr.notify(MyImageID.get(0), gBuilder.build());
                        }
                        //myGoalNotifyMgr.cancel(MyImageID.get(0));
                    } else if (MyImageDescription.size() > 1) {
                        int cnt = MyImageDescription.size();
                        gBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Fizz")
                                .setContentText("The deadline dates for the below images have exceeded the 3 month limit, press the notification if u wish to delete them");
                        gBuilder.setContentIntent(resultPendingIntent1);
                        String summary = String.valueOf(cnt) + " goals";
                        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                        if (cnt <= 3)
                            for (int i = 0; i < cnt; i++) {
                                try {
                                    if (UpdatedName.get(i) != null) {
                                        inboxStyle.addLine(MyImageID.get(i) + ". " + UpdatedName.get(i));
                                    }
                                }catch(IndexOutOfBoundsException ae){
                                    inboxStyle.addLine(MyImageID.get(i) + ". " + MyImageTitle.get(i));
                                }
                            }
                        else
                            for (int i = 0; i < 3; i++) {
                                try {
                                    if (UpdatedName.get(i) != null) {
                                        inboxStyle.addLine(MyImageID.get(i) + ". " + UpdatedName.get(i));
                                    }
                                } catch (IndexOutOfBoundsException ae) {
                                    inboxStyle.addLine(MyImageID.get(i) + ". " + MyImageTitle.get(i));
                                }
                            }
                        inboxStyle.setSummaryText(summary);
                        gBuilder.setStyle(inboxStyle);
                        gBuilder.setPriority(Notification.PRIORITY_HIGH);// [-2,2]->[PRIORITY_MIN,PRIORITY_MAX]
                        gBuilder.setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).
                                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).
                                setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}).setLights(Color.WHITE, 0, 1);
                        gBuilder.setAutoCancel(true);
                        myGoalNotifyMgr.notify(10, gBuilder.build());
                        //myGoalNotifyMgr.cancel(10);
                    }
                }
            else if(MyImageDescription.size() > 0) {
                    myGoalNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Intent notificationIntent = new Intent(context, MainActivity.class);
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Intent resultIntent = new Intent(context, MainActivity.class);
                    //resultIntent.putExtra("ID", mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.KEY_ID)));
                    resultIntent.putExtra("update", true);
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //Opening Image in Gallery View
                    intent = new Intent(Intent.ACTION_VIEW);
                    //intent.setAction(Intent.ACTION_VIEW);
                    //intent.putExtra("IMAGE", (new Gson()).toJson(image));
                    intent.setDataAndType(Uri.parse("file://" + MyImageTitle.get(0)), "image/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // startActivity(intent);
                    Intent deleteIntent = new Intent(context, MainActivity.class);
                    deleteIntent.setAction("Delete");
                    int ID = MyImageID.get(0);
                    deleteIntent.putExtra("ID", ID);
                    deleteIntent.putExtra("update", true);
                    //deleteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    deleteIntent.addFlags(/*Intent.FLAG_ACTIVITY_SINGLE_TOP |*/ Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent backIntent = new Intent(context, MainActivity.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //startActivity(resultIntent);
                    // Because clicking the notification opens a new ("special") activity, there's
                    // no need to create an artificial back stack.
                    PendingIntent resultPendingIntent1 = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    PendingIntent resPendingIntent = PendingIntent.getActivities(context, 0, new Intent[]{backIntent, intent}, PendingIntent.FLAG_CANCEL_CURRENT);
                    //PendingIntent resPendingIntent = PendingIntent.getActivity(context, 0,backIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    deletePendingIntent = PendingIntent.getActivity(context, 0, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    if (MyImageDescription.size() == 1) {
                        if(UpdatedName.size()>0) {
                            int cnt = MyImageDescription.size();
                            gBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Fizz")
                                    .setContentText("Reminder Set: " + UpdatedName.get(0))
                                    .addAction(R.drawable.ic_action_about, "View", resPendingIntent)
                                    .addAction(R.drawable.ic_action_discard, "Delete", deletePendingIntent);
                            gBuilder.setContentIntent(resultPendingIntent1);
                            //gBuilder.setContentIntent(deletePendingIntent);
                            String summary = String.valueOf(cnt) + " goals";
                            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                            inboxStyle.setSummaryText(summary);
                            gBuilder.setStyle(new NotificationCompat.InboxStyle().setBigContentTitle(UpdatedName.get(0)).
                                    addLine(MyImageID.get(0) + ". " + UpdatedName.get(0) + " - " + String.valueOf(MyImageCount.get(0)) + " days left"));
                            gBuilder.setPriority(Notification.PRIORITY_HIGH);// [-2,2]->[PRIORITY_MIN,PRIORITY_MAX]
                            gBuilder.setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).
                                    setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).
                                    setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}).setLights(Color.WHITE, 0, 1);
                            gBuilder.setAutoCancel(true);
                            myGoalNotifyMgr.notify(MyImageID.get(0), gBuilder.build());
                        }
                        else{
                            int cnt = MyImageDescription.size();
                            gBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Fizz")
                                    .setContentText("Reminder Set: " + MyImageTitle.get(0))
                                    .addAction(R.drawable.ic_action_about, "View", resPendingIntent)
                                    .addAction(R.drawable.ic_action_discard, "Delete", deletePendingIntent);
                            gBuilder.setContentIntent(resultPendingIntent1);
                            //gBuilder.setContentIntent(deletePendingIntent);
                            String summary = String.valueOf(cnt) + " goals";
                            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                            inboxStyle.setSummaryText(summary);
                            gBuilder.setStyle(new NotificationCompat.InboxStyle().setBigContentTitle(MyImageTitle.get(0)).
                                    addLine(MyImageID.get(0) + ". " + MyImageTitle.get(0) + " - " + String.valueOf(MyImageCount.get(0)) + " days left"));
                            gBuilder.setPriority(Notification.PRIORITY_HIGH);// [-2,2]->[PRIORITY_MIN,PRIORITY_MAX]
                            gBuilder.setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).
                                    setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).
                                    setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}).setLights(Color.WHITE, 0, 1);
                            gBuilder.setAutoCancel(true);
                            myGoalNotifyMgr.notify(MyImageID.get(0), gBuilder.build());
                        }
                        //myGoalNotifyMgr.cancel(MyImageID.get(0));
                    } else if (MyImageDescription.size() > 1) {
                        int cnt = MyImageDescription.size();
                        gBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Fizz")
                                .setContentText("Reminder Set: "/* + fname*/);
                        //.addAction(R.drawable.ic_action_about, "View", resPendingIntent);
                        //.addAction(R.drawable.ic_action_discard, "Delete", deletePendingIntent);
                        gBuilder.setContentIntent(resultPendingIntent1);
                        //gBuilder.setContentIntent(deletePendingIntent);
                        String summary = String.valueOf(cnt) + " goals";
                        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                        if (cnt <= 3)
                            for (int i = 0; i < cnt; i++) {
                                try {
                                    if (UpdatedName.get(i)!=null) {
                                        inboxStyle.addLine(MyImageID.get(i) + ". " + UpdatedName.get(i) + " - " + String.valueOf(MyImageCount.get(i)) + " days left");
                                    }
                                }catch(IndexOutOfBoundsException ae){
                                    inboxStyle.addLine(MyImageID.get(i) + ". " + MyImageTitle.get(i) + " - " + String.valueOf(MyImageCount.get(i)) + " days left");
                                }
                            }
                                else
                            for (int i = 0; i < 3; i++){
                                try {
                                    if (UpdatedName.get(i)!=null) {
                                        inboxStyle.addLine(MyImageID.get(i) + ". " + UpdatedName.get(i) + " - " + String.valueOf(MyImageCount.get(i)) + " days left");
                                    }
                                }catch(IndexOutOfBoundsException ae){
                                    inboxStyle.addLine(MyImageID.get(i) + ". " + MyImageTitle.get(i) + " - " + String.valueOf(MyImageCount.get(i)) + " days left");
                                }
                            }
                        inboxStyle.setSummaryText(summary);
                        gBuilder.setStyle(inboxStyle);
                        gBuilder.setPriority(Notification.PRIORITY_HIGH);// [-2,2]->[PRIORITY_MIN,PRIORITY_MAX]
                        gBuilder.setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).
                                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).
                                setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}).setLights(Color.WHITE, 0, 1);
                        gBuilder.setAutoCancel(true);
                        myGoalNotifyMgr.notify(10, gBuilder.build());
                        //myGoalNotifyMgr.cancel(10);

                    //}
                }
            }
        }
    }
}
