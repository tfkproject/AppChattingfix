package com.example.zart.appchattingfix;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.zart.appchattingfix.model.ProfilUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private static final String TAG = "Profile";
    private static final int REQUEST_CODE = 999;
    public final int SELECT_FILE = 1;
    public final int REQUEST_CAMERA = 0;
    private Intent intent, CropIntent;
    private Bitmap bitmap, decoded;
    private Uri fileUri;
    private CircleImageView imageView;
    private DatabaseReference mDatabase;
    private EditText nama;

    int bitmap_size = 40; // image quality 1 - 100;
    int max_resolution_image = 800;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nama = findViewById(R.id.nama);
        Button Masuk = findViewById(R.id.masuk);
        ImageButton camera = findViewById(R.id.camera);
        imageView = findViewById(R.id.foto);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        this.registerForContextMenu(camera);

        Masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = getIntent().getExtras();
                String no_HP = bundle.getString("nohp");
                writeNewUser(no_HP,nama.getText().toString(),fileUri.getLastPathSegment());

            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: opening dialog to choose new photo");
                selectImage();
            }
        });

        getPermissions();
    }

    private void selectImage() {

        izinakses();
        imageView.setImageResource(0);
        final CharSequence[] items = {"Kamera", "Galeri", "Batal"};

        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        builder.setTitle("Pilih Foto");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Kamera")) {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQUEST_CAMERA);

                } else if (items[item].equals("Galeri")) {
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/");
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_FILE);
                } else if (items[item].equals("Batal")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    // Untuk menampilkan bitmap pada ImageView
    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        imageView.setImageBitmap(decoded);
    }


    // Untuk resize bitmap
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(getApplicationContext(), "Permission is denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    private void izinakses() {
        Log.d(TAG, "izinakses");
        String[] izinakses = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                izinakses[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                izinakses[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                izinakses[2]) == PackageManager.PERMISSION_GRANTED){
        }else{
            ActivityCompat.requestPermissions(Profile.this, izinakses, REQUEST_CODE);
        }
    }


    private void CropImage() {

        try {
            CropIntent = new Intent("com.android.camera.action.crop");
            CropIntent.setDataAndType(fileUri, "image/*");

            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputx", 180);
            CropIntent.putExtra("outputy", 180);
            CropIntent.putExtra("aspectx", 3);
            CropIntent.putExtra("aspecty", 4);
            CropIntent.putExtra("scaleUpIfNeeded", "true");
            CropIntent.putExtra("return-data", "true");

            startActivityForResult(CropIntent, REQUEST_CODE);
        } catch (ActivityNotFoundException ex) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("onActivityResult", "requestCode " + requestCode + ", resultCode " + resultCode +", data " + data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                try {
                    Log.e(TAG,""+ fileUri.getPath().toString());

                    bitmap = BitmapFactory.decodeFile(fileUri.getPath());
                    setToImageView(getResizedBitmap(bitmap, max_resolution_image));
                    Log.i("bitmap",""+bitmap.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE && data != null && data.getData() != null) {
                try {
                    // mengambil gambar dari Gallery
                    fileUri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(Profile.this.getContentResolver(), fileUri);
                    setToImageView(getResizedBitmap(bitmap, max_resolution_image));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AppChat");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
            if (!mediaStorageDir.mkdirs()) {
                Log.e("Monitoring", "Oops! Failed create Monitoring directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_AppChat_" + timeStamp + ".jpg");

        Log.e("mediafile",""+mediaFile.toString());

        return mediaFile;
    }

    private void writeNewUser(String userId, String name, String foto) {
        ProfilUser user = new ProfilUser(userId , userId, name, foto);

        final FirebaseUser userDB = FirebaseAuth.getInstance().getCurrentUser();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu");
        progressDialog.show();

        if (user != null){
            mDatabase.child("Users").child(userDB.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Intent il = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(il);
                    progressDialog.dismiss();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Log.e("mDataBase", "" + e);
                }
            });
        }else {
            Toast.makeText(getApplicationContext(),"Harap lengkapi data!",Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
            Log.e("tod", "main");
        }
    }

}



