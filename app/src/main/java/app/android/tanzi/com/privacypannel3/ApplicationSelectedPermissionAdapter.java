package app.android.tanzi.com.privacypannel3;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by আনন্দ on 4/17/2016.
 */
public class ApplicationSelectedPermissionAdapter extends ArrayAdapter {
    String[] application_permission_text;
    Boolean[] selectedPosition2;
    ArrayList<Boolean> checkValueList;

    public ApplicationSelectedPermissionAdapter(Context context, String[] text,ArrayList<Boolean> checkBoxSelect) {
        super(context,R.layout.application_permission_item, text);
        this.application_permission_text = text;
        this.checkValueList = checkBoxSelect;
        this.selectedPosition2 = new Boolean[checkValueList.size()];
        int i=-1;
        //Log.d("TanziLogg", Integer.toString(l));
        for (Boolean app : checkValueList) {
            i++;
            selectedPosition2[i] = app;

        }

        this.getContext();
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.location_list_item, null, true);
        TextView textView1 = (TextView) row.findViewById(R.id.location_list_text_view);
        final CheckBox checkbox = (CheckBox) row.findViewById(R.id.location_list_checkBox);

        textView1.setText(application_permission_text[position]);

        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), application_permission_text[position], Toast.LENGTH_SHORT).show();
                //redirect to the page here.

                Intent intent = new Intent(AppContext.getContext(), SpecialPermissionApplicationNamesActivity.class);
                intent.putExtra("PName", application_permission_text[position]);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //based on item add info to intent
                AppContext.getContext().startActivity(intent);
            }
        });

        if(selectedPosition2[position]) checkbox.setChecked(true);
        else checkbox.setChecked(false);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Object item = getItem(position);
                Boolean status;
                status = checkbox.isChecked();

                checkValueList.set(position,status);

 //               String status1= Boolean.toString(status);
                String str = (String) item;
                if(str.equals("Geolocation")) {
                    Toast.makeText(getContext(), "To enable warning for Geolocation, " +
                            "please go" +
                            "to Location Accuracy-->VIEW RISKY APPS\n" +
                            "and Select specific apps", Toast.LENGTH_LONG).show();
                }

                if(selectedPosition2[position]==true){
                    selectedPosition2[position]=false;}
                else {selectedPosition2[position]= true;}
            }
        });

        return row;
    }

    public Boolean [] saveValues(){

        Boolean[] myArray = new Boolean[checkValueList.size()];
        //*****************************
        checkValueList.toArray(myArray);

        return myArray;
    }
}
