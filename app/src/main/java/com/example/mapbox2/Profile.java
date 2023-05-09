package com.example.mapbox2;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mapbox2.databinding.ActivityProfileBinding;
import com.example.mapbox2.models.Driver;
import com.example.mapbox2.responses.ShowUserResponse;
import com.example.mapbox2.responses.UpdateResponse;
import com.example.mapbox2.storage.SharedPreferencesManager;
import com.example.mapbox2.viewmodels.AuthViewModel;
import com.example.mapbox2.viewmodels.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import stream.customalert.CustomAlertDialogue;

public class Profile extends AppCompatActivity {
    private static final String TAG = "Profile-Activity";
    private ActivityProfileBinding profileBinding;
    ProgressDialog progressDialog;
    private UserViewModel userViewModel;
    private String token;
    private String name;
    private String mobileNumber;
    private String email;
    private String licenseNumber;
    private String image_path;
    // Activity Result Launcher for image
    ActivityResultLauncher<Object> cropActivityResultLauncher;
    ActivityResultContract<Object, Uri> activityResultContract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        userViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(UserViewModel.class);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Information");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        token = SharedPreferencesManager.getInstance(this).getToken();
        Log.d("token",token);
        /** Load User Information **/
            loadUserInformation();


        /** When User Click On Save Button **/
        profileBinding.saveUpdate.setOnClickListener(view -> {
            updateUserInformation();
        });

        /** logging out from app **/
        profileBinding.logOut.setOnClickListener(view -> {
            showDialog("Log Out","Do you want to log out?");
        });

        /** When User Click To Choose An Image **/
        profileBinding.profileImage.setOnClickListener(view -> {
            cropActivityResultLauncher.launch(null);
        });
        activityResultContract = new ActivityResultContract<Object, Uri>() {
            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, Object input) {
                /** createIntent() — accepts input data and creates an intent, which will be later launched by calling launch() **/
                return CropImage.activity()
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1, 1)
                        .getIntent(getApplicationContext());
            }

            @Override
            public Uri parseResult(int resultCode, @Nullable Intent intent) {
                /** parseResult() — is responsible for returning the result, handling resultCode, and parsing the data. **/
                if (CropImage.getActivityResult(intent) != null) {
                    return CropImage.getActivityResult(intent).getUri();
                }
                return null;
            }
        };

        cropActivityResultLauncher = registerForActivityResult(activityResultContract, result -> {
            if (result != null) {
                uploadImage(result);
                profileBinding.profileImage.setImageURI(result);
            }
        });
    }

    private void loadUserInformation(){
        /*
        profileBinding.nameText.setText(SharedPreferencesManager.getInstance(this).getUser().getName());
        profileBinding.editEmail.setText(SharedPreferencesManager.getInstance(this).getUser().getEmail());
        profileBinding.editMobile.setText(SharedPreferencesManager.getInstance(this).getUser().getMobileNumber());
        profileBinding.editLicesne.setText(SharedPreferencesManager.getInstance(this).getUser().getLicenseNumber());
        profileBinding.code.setText(SharedPreferencesManager.getInstance(this).getUser().getSchoolId()+"");
        profileBinding.idText.setText("ID:"+SharedPreferencesManager.getInstance(this).getUser().getId());
        if(SharedPreferencesManager.getInstance(this).getUser().getImagePath()!=null){
            image_path = SharedPreferencesManager.getInstance(this).getUser().getImagePath();
            Picasso.get().load(SharedPreferencesManager.getInstance(this).getUser().getImagePath()).into(profileBinding.profileImage);
        }*/
        progressDialog.setTitle("Updating Is Preparing");
        progressDialog.setMessage("Please Wait....");
        progressDialog.show();
        userViewModel.getUserInfo("Bearer "+SharedPreferencesManager.getInstance(Profile.this).getToken()).observe(Profile.this, new Observer<ShowUserResponse>() {
            @Override
            public void onChanged(ShowUserResponse showUserResponse) {
                progressDialog.dismiss();
                Toast.makeText(Profile.this, showUserResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if(showUserResponse!=null && showUserResponse.isSuccess()){
                    Picasso.get().load(showUserResponse.getData().getImagePath()).into(profileBinding.profileImage);
                    profileBinding.nameText.setText(showUserResponse.getData().getName());
                    profileBinding.editEmail.setText(showUserResponse.getData().getEmail());
                    profileBinding.editMobile.setText(showUserResponse.getData().getMobileNumber());
                    profileBinding.editLicesne.setText(showUserResponse.getData().getLicenseNumber());
                    profileBinding.code.setText(showUserResponse.getData().getSchoolCode()+"");
                    profileBinding.idText.setText("ID:"+showUserResponse.getData().getId());
                }else if(showUserResponse!=null && !showUserResponse.isSuccess()){
                    showDialog2("Sorry",showUserResponse.getMessage());
                }
            }
        });

    }

    private void showDialog2(String title, String message) {
        CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(Profile.this)
                .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                .setTitle(title)
                .setMessage(message)
                .setNegativeText("OK")
                .setNegativeColor(R.color.purple_200)
                .setNegativeTypeface(Typeface.DEFAULT_BOLD)
                .setOnNegativeClicked(new CustomAlertDialogue.OnNegativeClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setDecorView(getWindow().getDecorView())
                .build();
        alert.show();
    }

    private void showDialog(String title,String message) {
        CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(Profile.this)
                .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveText("Confirm")
                .setPositiveColor(R.color.negative)
                .setPositiveTypeface(Typeface.DEFAULT_BOLD)
                .setOnPositiveClicked(new CustomAlertDialogue.OnPositiveClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        dialog.dismiss();
                        SharedPreferencesManager.getInstance(Profile.this)
                                .clear();
                        startActivity(new Intent(Profile.this, LogIn.class));
                        finish();
                        Toast.makeText(Profile.this, "You Logged Out", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeText("Cancel")
                .setNegativeColor(R.color.positive)
                .setOnNegativeClicked(new CustomAlertDialogue.OnNegativeClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setDecorView(getWindow().getDecorView())
                .build();
        alert.show();
    }

    private void updateUserInformation(){
        progressDialog.setTitle("Updating Is Preparing");
        progressDialog.setMessage("Please Wait....");
        progressDialog.show();
        name = profileBinding.nameText.getText().toString();
        email = profileBinding.editEmail.getText().toString();
        mobileNumber = profileBinding.editMobile.getText().toString();
        licenseNumber = profileBinding.editLicesne.getText().toString();
        if(name.isEmpty() || name.trim().isEmpty()){
            Toast.makeText(this, "Please fill empty fields!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }else if(email.isEmpty() || email.trim().isEmpty()){
            Toast.makeText(this, "Please fill empty fields!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }else if(mobileNumber.isEmpty() || mobileNumber.trim().isEmpty()){
            Toast.makeText(this, "Please fill empty fields!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }else if(licenseNumber.isEmpty() || licenseNumber.trim().isEmpty()){
            Toast.makeText(this, "Please fill empty fields!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }else if(image_path.isEmpty()){
            Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        userViewModel.updateUser("Bearer "+token,name,email,licenseNumber,mobileNumber,image_path).observe(this, new Observer<UpdateResponse>() {
            @Override
            public void onChanged(UpdateResponse updateResponse) {
                progressDialog.dismiss();
                if(updateResponse.isSuccess() && updateResponse.getData()!=null)
                {
                    Driver driver = new Driver(
                            updateResponse.getData().getSchoolId(),
                            updateResponse.getData().getMobileNumber(),
                            updateResponse.getData().getName(),
                            updateResponse.getData().getTripId(),
                            updateResponse.getData().getLicenseNumber(),
                            updateResponse.getData().getId(),
                            updateResponse.getData().getEmail(),
                            updateResponse.getData().getImagePath()
                    );
                    SharedPreferencesManager.getInstance(getBaseContext()).saveUser(driver);
                }
                Log.d(TAG,updateResponse.getMessage());
                Toast.makeText(Profile.this, updateResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Uploading Image **/
    private void uploadImage(Uri imageURI) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("drivers_images");
        progressDialog.setTitle("Updating Image");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        storageRef.child(String.valueOf(SharedPreferencesManager.getInstance(Profile.this).getUser().getId())).putFile(imageURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(Profile.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        // get upload url
                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                image_path = String.valueOf(task.getResult());
                                progressDialog.dismiss();
                                Log.d(TAG, image_path);

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.getMessage());
                        progressDialog.dismiss();
                    }
                });

    }
}