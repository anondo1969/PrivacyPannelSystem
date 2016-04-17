package app.android.tanzi.com.privacypannel3;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Entu on 3/24/2016.
 */
public class PermissionWarningServiceReceiver extends BroadcastReceiver {

    boolean ProtectionEnable = false;
    final Context context = AppContext.getContext();
    final DatabaseHandler db = new DatabaseHandler(context);
    ArrayList <String> notifications = new ArrayList<String>();
   // StringBuilder sb = new StringBuilder();

    @Override
    public void onReceive(Context context, Intent intent) {

        //detecting foreground apps
        new AsyncTask<Void, Void, List<ProcessManager.Process>>() {

            long startTime;

            @Override
            protected List<ProcessManager.Process> doInBackground(Void... params) {
                startTime = System.currentTimeMillis();
                return ProcessManager.getRunningApps();
            }

            @Override
            protected void onPostExecute(List<ProcessManager.Process> processes) {
                // StringBuilder sb = new StringBuilder();
                // sb.append("Execution time: ").append(System.currentTimeMillis() - startTime).append("ms\n");
                //   sb.append("Attention: Following Apps are using your location permissions\n");

                //এই লিসট এর আসলে কোন আইটেমটা সবার নিচে সেইটা দেখতে হবে।
                for (ProcessManager.Process process : processes) {

                    //check whether it is a system app or not
                    //-------------------------------START---------------------------------------------------------------------

//                    Log.d("Foreground Process: ", "starts");

                    ApplicationInfoController applicationInfo = db.getApplicationInfo(process.uid);


                    //check whether it is a system app or not
                    //if system process id is of a system app then it will be null or 0
                    if(applicationInfo.getUID()!=0) {
                        //sb.append('\n').append(process.name);
                        //sb.append('\n').append(process.uid);

//                        String log = "Id: "+applicationInfo.getId()+" ,UID: " + applicationInfo.getUID()
//                                +" ,Name: " + applicationInfo.getAppName() + " ,Package Name: " + applicationInfo.getPackageName()
//                                + " ,Permission Checked: " + applicationInfo.getPermissionChecked();
//                        // Writing Contacts to log
//                        Log.d("Foreground Process: ", log);

                        //Check gps permissions

                        List<PermissionInfoController> packageSpecificPermissionInfo = db.
                                getPackageSpecificPermissionInfo(applicationInfo.getUID());

                        for (PermissionInfoController apps : packageSpecificPermissionInfo) {
                            //a list should be maintained

                            //নিচের দুই লাইন লাগবে না, আসলে চেকবক্স দিয়ে চেক করলেই হবে।
                          //  if(apps.getPermissions().equals("android.permission.ACCESS_COARSE_LOCATION")
                            //        ||apps.getPermissions().equals("android.permission.ACCESS_FINE_LOCATION") ){

                                if(applicationInfo.getPermissionChecked().equals("1")) {// 0 is for testing only, it will be 1
                                    ProtectionEnable = true;
                                    notifications.add(applicationInfo.getAppName());
                                    //sb.append('\n').append(applicationInfo.getPackageName());
                                }

                                //sb.append('\n').append(applicationInfo.getAppName());
                                //sb.append('\n').append(applicationInfo.getPackageName());

                                break;
                            //}

                        }

                    }
                }

//                Log.d("ProtectionEnable: ", Boolean.toString(ProtectionEnable));

                // Intent service = new Intent(AppContext.getContext(), FakeGPSOnService.class);

                //------------------------------------------------------------------------------------------------------

                if (ProtectionEnable) {

                    //we have to show a warning dialogue here
                    //Log.d("locationWarning ", "GPS Permission");
                    // Toast.makeText(AppContext.getContext(), "GPS Permission", Toast.LENGTH_LONG).show();
                    //Toast.makeText(AppContext.getContext(), "Active Apps: " + sb.toString(), Toast.LENGTH_LONG).show();

                    //----------------------------------------
                    /*/dialogue
                    AlertDialog.Builder builder = new AlertDialog.Builder(AppContext.getContext());
                    builder.setMessage(sb.toString())//app names
                            .setCancelable(false)
                            .setTitle("Attention: Following Apps are using your location permissions!!!")
                            .setPositiveButton("OK",null);
                    AlertDialog alert = builder.create();
                    alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    alert.show();
                    */

                    //notification to warn user
                    //এখানে নতুন একটিভিটি ক্লাসের নাম আসবে।
                    //Intent intent = new Intent(AppContext.getContext(), RiskyAppsViewActivity.class);
                    Intent intent = new Intent(AppContext.getContext(), ApplicationSelectedPermissionActivity.class);
                    PendingIntent pIntent = PendingIntent.getActivity(AppContext.getContext(), (int) System.currentTimeMillis(), intent, 0);

                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                    // Sets a title for the Inbox in expanded layout
                    inboxStyle.setBigContentTitle("Suspicious Application Detected!");
                    inboxStyle.setSummaryText("Touch to change their Permissions");

                    // Moves namess into the expanded layout
                    //only keep unique values

                    Set<String> uniqueAppNames = new HashSet<String>(notifications);
                    String[] appNamesArray = new String[uniqueAppNames.size()];
                    uniqueAppNames.toArray(appNamesArray);

                    for (int i=0; i < uniqueAppNames.size(); i++) {

                        //inboxStyle.addLine(notifications.get(i));
                        inboxStyle.addLine(appNamesArray[i]);
                    }

                    NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(AppContext.getContext())
                            .setSmallIcon(R.drawable.notification)
                            .setContentTitle("Suspicious Application Detected!")
                            .setContentText("Touch to change their Permissions")
                            .setAutoCancel(true)
                            .setContentIntent(pIntent)
                            .setStyle(inboxStyle)
                            .setTicker("Suspicious Application Detected!")
                            .setWhen(0);

                    // Issue the notification here.
                    NotificationManager notificationManager =
                            (NotificationManager) AppContext.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    // mId allows you to update the notification later on.
                    int mId = 1001;
                    notificationManager.notify(mId, mBuilder.build());


                }
                //------------------------------------------------------------------------------------------------/


                //printDialog(ProtectionEnable, sb.toString());
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //------------------------------------------------------------------------------------------------
        db.close();

    }

}