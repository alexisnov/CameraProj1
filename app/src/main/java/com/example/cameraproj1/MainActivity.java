package com.example.cameraproj1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private ImageView imageView;
    private Uri outputFileUri; //Путь к изображению
    private Switch aSwitch;//Переключатель

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        aSwitch = findViewById(R.id.switch1);
        //Проверка разрешения на запись
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionStatus != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        //Проверка разрешения на чтение
        permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionStatus != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }
    //Нажатие на картинку
    public void onClick(View v){
        if(aSwitch.isChecked()){
            saveFullImage();//Сохранение фотографии
        }else{
            getThumbnailPicture(); //Получение предпросмотра
        }

    }

    private void getThumbnailPicture() {//Получение предпросмотра
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            startActivityForResult(takePhotoIntent,REQUEST_TAKE_PHOTO);
        }catch (ActivityNotFoundException e){
            e.printStackTrace();
        }
    }

    private void saveFullImage() {//Сохранение фотографии
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String str = String.format("%d.jpg",System.currentTimeMillis()/1000);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),str);
        outputFileUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider",file);
        try{
            //takePhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //takePhotoIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileUri);
            startActivityForResult(takePhotoIntent,REQUEST_TAKE_PHOTO);
        }catch (ActivityNotFoundException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            //Проверка данных в ответе
            if(data!=null){
                if(data.hasExtra("data")){
                    //Фотография сделана
                    Bundle extras = data.getExtras();
                    Bitmap thumbnailBitmap = (Bitmap) extras.get("data");
                    imageView.setImageBitmap(thumbnailBitmap);
                }else{
                    //Сохранённое изображение
                    imageView.setImageURI(outputFileUri);
                }
            }

        }
    }
}