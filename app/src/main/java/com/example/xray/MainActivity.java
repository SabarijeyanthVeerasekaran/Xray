package com.example.xray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xray.retrofit.model.FileInfo;
import com.example.xray.retrofit.remote.ApiUtils;
import com.example.xray.retrofit.remote.FileService;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

   //String s="http://192.168.1.5";
    private WebView web;

    private Button selectbtn,uploadbtn;
    private ImageView d_diseasesimage;
    private TextView diseasStatus;
    private Bitmap bitmap;
    private String imagepath;
    private ProgressBar progressBar;
    private Uri imageUri;
 //   private StorageReference storageReference;
    private FileService fileService;
    private String message;
    private static  int count=0;
   // private StorageTask storageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        d_diseasesimage=(ImageView)findViewById(R.id.d_diseasesimage);
        selectbtn=(Button)findViewById(R.id.selectbtn);
        uploadbtn=(Button)findViewById(R.id.uploadbtn);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        diseasStatus=(TextView)findViewById(R.id.diseasesstatus);

        fileService= ApiUtils.getFileServices();
        //fileService= ApiUtils.getFileServices();
        selectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadbtn.setEnabled(true);
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,0);
            }
        });
        //storageReference= FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getUid());
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(1);

                final File file=new File(imagepath);
                final RequestBody requestBody=RequestBody.create(MediaType.parse("multipart/form-data"),file);
                MultipartBody.Part body=MultipartBody.Part.createFormData("file",file.getName(),requestBody);

                Call<FileInfo> call=fileService.upload(body);
                call.enqueue(new Callback<FileInfo>() {
                    @Override
                    public void onResponse(Call<FileInfo> call, Response<FileInfo> response) {
                        FileInfo fileInfo=response.body();
                        if(response.isSuccessful())
                        {

                            Toast.makeText(MainActivity.this,""+ response.toString(),Toast.LENGTH_LONG).show();


                        }
                        if(!response.isSuccessful())
                        {

                            Toast.makeText(MainActivity.this,"No Diseases Detected",Toast.LENGTH_LONG).show();


                        }
                    }

                    @Override
                    public void onFailure(Call<FileInfo> call, Throwable t) {
                        //message=t.getMessage().trim().substring(34);
                        //diseasStatus.setText("The Plant was affected by"+message);
                        Toast.makeText(MainActivity.this,"Error "+t.toString(),Toast.LENGTH_LONG).show();
                    }
                });
                /*if(storageTask!=null && storageTask.isInProgress())
                    Toast.makeText(getActivity(),"Uploading in progess...",Toast.LENGTH_SHORT).show();
                else
                    uploadImage();


                 */
            }
        });

       }
    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
   /* private void uploadImage() {
        if(imageUri!=null)
        {
            StorageReference fileReference=storageReference.child("uploads/"+System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));
            FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid()).child("uploads/").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    count=(int)dataSnapshot.getChildrenCount();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            storageTask=fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(),"Uploaded Sucessful",Toast.LENGTH_SHORT).show();

                            Upload uploadImage=new Upload(message,taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                            FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid())
                                    .child("uploads/").child(String.valueOf(count)).setValue(uploadImage);

                        }


                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int)progress);
                            //progressStatus.setText((int) progress);
                        }
                    });
        }
        else {
            Toast.makeText(MainActivity.this,"No file selected",Toast.LENGTH_SHORT).show();
        }
    }

    */

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(MainActivity.this, "Unable to choose file", Toast.LENGTH_LONG).show();
                return;
            }
            imageUri = data.getData();
            imagepath = getPathFromUri(imageUri);
            /*try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            d_diseasesimage.setImageBitmap(bitmap);


             */
            //Picasso.with(load(imageUri).into(d_diseasesimage);
        }
    }
    private String getPathFromUri(Uri uri)
    {
        String[] projection={MediaStore.Images.Media.DATA};
        CursorLoader loader=new CursorLoader(getApplicationContext(),uri,projection,null,null,null);
        Cursor cursor=loader.loadInBackground();
        int column_index=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result=cursor.getString(column_index);
        cursor.close();
        return result;
    }
   /* public void onResume() {
        super.onResume();
        AppCompatActivity appCompatActivity=(AppCompatActivity)getActivity();
        ActionBar actionBar=appCompatActivity.getSupportActionBar();
        actionBar.setTitle(R.string.diseasesprediction);
    }

    */

}
//diseasesfragment.DiseasesFragment