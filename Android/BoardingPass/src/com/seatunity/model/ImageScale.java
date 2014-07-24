package com.seatunity.model;

import java.io.File;
import java.io.FileInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class ImageScale 
{


	public Bitmap decodeImagetoUpload(String imagePath)
	 {  
	     Bitmap bitmap=null;

	     try
	     {

	         File file=new File(imagePath);
	         BitmapFactory.Options o = new BitmapFactory.Options();
	         o.inJustDecodeBounds = true;

	         BitmapFactory.decodeStream(new FileInputStream(file),null,o);
	         final int REQUIRED_SIZE=300;
	         int width_tmp=o.outWidth, height_tmp=o.outHeight;

	         int scale=1;
	         while(true)
	         {
	             if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
	             break;
	             width_tmp/=2;
	             height_tmp=width_tmp;
	             scale*=2;  
	         }  

	         BitmapFactory.Options options=new BitmapFactory.Options();

	         options.inSampleSize=scale;
	         bitmap=BitmapFactory.decodeStream(new FileInputStream(file), null, options);
	         Log.e("size",""+bitmap.getWidth()+bitmap.getHeight());

	     }  
	     catch(Exception e) 
	     {  
	         bitmap = null;
	     }      
	     return bitmap; 
	 }
	
 public Bitmap decodeImage(String imagePath)
 {  
     Bitmap bitmap=null;

     try
     {

         File file=new File(imagePath);
         BitmapFactory.Options o = new BitmapFactory.Options();
         o.inJustDecodeBounds = true;

         BitmapFactory.decodeStream(new FileInputStream(file),null,o);
         final int REQUIRED_SIZE=200;
         int width_tmp=o.outWidth, height_tmp=o.outHeight;

         int scale=1;
         while(true)
         {
             if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
             break;
             width_tmp/=2;
             height_tmp=width_tmp;
             scale*=2;  
         }  

         BitmapFactory.Options options=new BitmapFactory.Options();

         options.inSampleSize=scale;
         bitmap=BitmapFactory.decodeStream(new FileInputStream(file), null, options);
         Log.e("size",""+bitmap.getWidth()+bitmap.getHeight());

     }  
     catch(Exception e) 
     {  
         bitmap = null;
     }      
     return bitmap; 
 }


 public Bitmap decodeImageForProfile(String imagePath)
 {  
     Bitmap bitmap=null;

     try
     {

         File file=new File(imagePath);
         BitmapFactory.Options o = new BitmapFactory.Options();
         o.inJustDecodeBounds = true;

         BitmapFactory.decodeStream(new FileInputStream(file),null,o);
         final int REQUIRED_SIZE=100;
         int width_tmp=o.outWidth, height_tmp=o.outHeight;

         int scale=1;
         while(true)
         {
             if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
             break;
             width_tmp/=2;
             height_tmp=width_tmp;
             scale*=2;  
         }  

         BitmapFactory.Options options=new BitmapFactory.Options();

         options.inSampleSize=scale;
         bitmap=BitmapFactory.decodeStream(new FileInputStream(file), null, options);
         Log.e("size",""+bitmap.getWidth()+bitmap.getHeight());

     }  
     catch(Exception e) 
     {  
         bitmap = null;
     }      
     return bitmap; 
 }

 
 public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) 
 {

    Bitmap resizedBitmap = null;
    try
    {
        if(bm!=null)
        {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // create a matrix for the manipulation
            Matrix matrix = new Matrix();
            // resize the bit map
            matrix.postScale(scaleWidth, scaleHeight);
            // recreate the new Bitmap
            resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);

        }
    }
    catch(Exception e)
    {
        resizedBitmap = null;
    }

    return resizedBitmap;
} 

}