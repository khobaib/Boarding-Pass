package com.seatunity.boardingpass.adapter;
import java.util.ArrayList;

import com.seatunity.boardingpass.NavDrawerItem;
import com.seatunity.boardingpass.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
 
public class NavDrawerListAdapter extends BaseAdapter {
     
    private Context context;
    private ArrayList<String> item;
     
    public NavDrawerListAdapter(Context context){
        this.context = context;
        item=new ArrayList<String>();
        item.add("1");
        item.add("2");
        item.add("3");

    }
 
    @Override
    public int getCount() {
        return item.size();
    }
 
    @Override
    public Object getItem(int position) {       
        return item.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	LayoutInflater mInflater;
        if (convertView == null) {
            mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }
          
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView user_name = (TextView) convertView.findViewById(R.id.user_name);
        TextView email = (TextView) convertView.findViewById(R.id.email);
        LinearLayout le_unameandemail_holder=(LinearLayout) convertView.findViewById(R.id.le_unameandemail_holder);
        TextView item = (TextView) convertView.findViewById(R.id.item);

        
        if(position==0){
        	item.setVisibility(View.GONE);
        	imgIcon.setVisibility(View.VISIBLE);
        	le_unameandemail_holder.setVisibility(View.VISIBLE);
        	 
            imgIcon.setImageResource(R.drawable.ic_contact_picture);    
        	user_name.setText(context.getResources().getText(R.string.txt_user_name));
        	email.setText(context.getResources().getText(R.string.txt_email_addess));
        }
        else{
        	imgIcon.setVisibility(View.GONE);
        	le_unameandemail_holder.setVisibility(View.GONE);
        	item.setVisibility(View.VISIBLE);

        	 if(position==1){
        		 item.setText(context.getResources().getText(R.string.txt_myaccounts));
             }
             else{
        		 item.setText(context.getResources().getText(R.string.txt_past_boarding_passes));

             }
        	
        }
       
             
        return convertView;
    }
 
}