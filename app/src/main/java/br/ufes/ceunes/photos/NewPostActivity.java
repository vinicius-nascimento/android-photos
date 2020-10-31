package br.ufes.ceunes.photos;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class NewPostActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private StorageReference storageReference, imageStorageReference;
    private DatabaseReference databaseReference;

    private Uri filePath;

    private Double height, width;
    private boolean isCompletelyFilled;
    private Long time;

    private ImageView imageView;
    private TextInputLayout textInputLayout;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        imageView = findViewById(R.id.img_new_post);
        textInputLayout = findViewById(R.id.desc_new_post);
        button = findViewById(R.id.pub_new_post);

        database = DatabaseConfig.getDatabase();
        databaseReference = database.getReference("posts");
        storageReference = FirebaseStorage.getInstance().getReference();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isCompletelyFilled = true;

                if (filePath == null) {
                    Toast.makeText(getApplicationContext(), "Select a image", Toast.LENGTH_LONG).show();
                    isCompletelyFilled = false;
                }

                if (textInputLayout.getEditText().getText().toString().isEmpty()) {
                    isCompletelyFilled = false;
                    textInputLayout.setError("Required field");
                    textInputLayout.setErrorEnabled(true);
                } else textInputLayout.setErrorEnabled(false);

                if (isCompletelyFilled) {
                    Toast.makeText(getApplicationContext(), "Publishing post", Toast.LENGTH_LONG).show();
                    time = System.currentTimeMillis();
                    imageStorageReference = storageReference.child("posts/" + time + "." + getFileExtension(filePath));
                    imageStorageReference.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imageStorageReference.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Post post = new Post(
                                                            uri.toString(),
                                                            height.toString(),
                                                            width.toString(),
                                                            textInputLayout.getEditText().getText().toString(),
                                                            time
                                                    );
                                                    databaseReference.push().setValue(post)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(getApplicationContext(), "Post published successfully", Toast.LENGTH_LONG).show();
                                                                    finish();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getApplicationContext(), "Connection failure", Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Connection failure", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Connection failure", Toast.LENGTH_LONG).show();
                                }
                            });
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 234 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Glide.with(getApplicationContext()).load(filePath).into(imageView);
                height = (double) bitmap.getHeight();
                width = (double) bitmap.getWidth();
                imageView.setBackground(null);
                double proporcao = (height / width);
                imageView.getLayoutParams().height = (int) Math.round(proporcao * imageView.getLayoutParams().width);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to load image", Toast.LENGTH_LONG).show();
                filePath = null;
            }
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select a image"), 234);
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
