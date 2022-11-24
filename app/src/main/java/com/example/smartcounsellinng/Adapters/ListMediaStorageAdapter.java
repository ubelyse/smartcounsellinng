package com.example.smartcounsellinng.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.smartcounsellinng.Models.Message;
import com.example.smartcounsellinng.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

public class ListMediaStorageAdapter extends BaseAdapter {

    Context context;
    int layout;
    List<Message> listImages;

    public ListMediaStorageAdapter(Context context, int layout, List<Message> listNameImages){
        this.context = context;
        this.layout = layout;
        this.listImages = listNameImages;
    }

    public static class ViewHolder{
        ImageView imageListStorage;
    }

    @Override
    public int getCount() {
        return listImages.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewRow = convertView;
        if(viewRow == null){ // The technique of loading view 1 item and the following items using the available view tag
            viewRow = inflater.inflate(layout,parent,false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.imageListStorage = (ImageView) viewRow.findViewById(R.id.imageViewInListStorage);
            viewRow.setTag(viewHolder); // Only initialize 1 time for the item friend in the list
        }

        ViewHolder viewHolder = (ViewHolder)viewRow.getTag();
        getImageStorage(viewHolder.imageListStorage, listImages.get(position).getUidSender(),
                listImages.get(position).getUidReceiver(),listImages.get(position).getContent());

        return viewRow;
    }

    public void getImageStorage(final ImageView imageView, String senderUid, String receiverUid, String nameImage){
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(senderUid).child(receiverUid).child(nameImage);
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "be_images");
            if (!root.exists()) {
                root.mkdirs();
            }

            final File gpxfile = new File(root, nameImage);

            if(gpxfile.exists()){
                Bitmap bmp = BitmapFactory.decodeFile(gpxfile.getAbsolutePath());
                imageView.setImageBitmap(bmp);
            }
            else{
                ref.getFile(gpxfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bmp = BitmapFactory.decodeFile(gpxfile.getAbsolutePath());
                        imageView.setImageBitmap(bmp);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        imageView.setImageResource(R.drawable.not_found_image);
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    }
                });
            }
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }
}
