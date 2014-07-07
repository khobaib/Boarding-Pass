package com.seatunity.boardingpass.adapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.seatunity.boardingpass.NavDrawerItem;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.UserCred;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
 
public class AdapterForSettings extends BaseAdapter {
     String month;
     String day;
    private Context context;
    private ArrayList<String>list;
    UserCred userCred;
    
     
    public AdapterForSettings(Context context,ArrayList<String>list, UserCred userCred){
        this.context = context;
        this.list=list;
        this.userCred=userCred;
        
       

    }
 
    @Override
    public int getCount() {
        return list.size();
    }
 
    @Override
    public Object getItem(int position) {       
        return list.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
    private class ViewHolder {
		
		TextView tv_seeting_criteria;
		TextView tv_info;
	}
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	LayoutInflater mInflater;
    	 ViewHolder holder = new ViewHolder();
    	 if (convertView == null) {
            mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.setting_inflate, null);
            
            holder.tv_seeting_criteria=(TextView) convertView.findViewById(R.id.tv_seeting_criteria);
            holder.tv_info=(TextView) convertView.findViewById(R.id.tv_info);
           convertView.setTag(holder);
            
        }
        else {
			holder = (ViewHolder) convertView.getTag();
		}
    	 if(position==0){
    		 holder.tv_info.setText(userCred.getLive_in());
    	 }
    	 else if(position==1){
    		 holder.tv_info.setText(userCred.getAge());
    	 }
    	 else if(position==2){
    		 holder.tv_info.setText(userCred.getGender());
    	 }
    	 else if(position==3){
    		 holder.tv_info.setText(userCred.getProfession());
    	 }
    	 else if(position==4){
    		 holder.tv_info.setText(userCred.getSeating_pref());
    	 }
    	 else if(position==5){
    		 holder.tv_info.setText(userCred.getSomethinAbout());
    	 }
    	 else if(position==5){
    		 holder.tv_info.setText(userCred.getSomethinAbout());
    	 }
    	 else {
    		 holder.tv_info.setText("");
    	 }
    	 
    	 
    	 holder.tv_seeting_criteria.setText(list.get(position));
        return convertView;
    }
    
 
}
