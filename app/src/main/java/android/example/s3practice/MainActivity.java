package android.example.s3practice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StorageAccessLevel;
import com.amplifyframework.storage.StorageItem;
import com.amplifyframework.storage.options.StorageListOptions;
import com.amplifyframework.storage.options.StorageUploadFileOptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button downloadButton;
    Button uploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadButton = findViewById(R.id.downloadbutton);
        uploadButton = findViewById(R.id.uploadButton);
        uploadFile();
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile();
            }
        });

        listFile();
        cognito();
    }

    private void cognito() {

        EditText emailEditText;
        EditText passwordEditText;
        EditText confirmationEditText;
        Button signUpButton;
        Button signOutButton;
        Button signInButton;
        Button checkButton;
        Button credButton;
        Button dataButton;

        emailEditText = findViewById(R.id.editTextEmailAddress);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmationEditText = findViewById(R.id.editTextConfirmation);
        signUpButton = findViewById(R.id.button2);
        signOutButton = findViewById(R.id.signoutbutton);
        signInButton = findViewById(R.id.signinbutton);
        checkButton = findViewById(R.id.checkbutton);
        credButton = findViewById(R.id.credButton);
        dataButton = findViewById(R.id.button_data);

        Amplify.Auth.fetchAuthSession(
                result -> Log.i("MyAmplifyApp", result.toString()),
                error -> Log.e("MyAmplifyApp", error.toString())
        );

        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DataActivity.class);
                startActivity(intent);
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Amplify.Auth.signUp(
                        emailEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), emailEditText.getText().toString()).build(),
                        result -> Log.i("MyAmplifyApp", "Result: " + result.toString()),
                        error -> Log.e("MyAmplifyApp", "Sign up failed", error)
                );
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Amplify.Auth.confirmSignUp(
                        emailEditText.getText().toString(),
                        confirmationEditText.getText().toString(),
                        result -> Log.i("MyAmplifyApp", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete"),
                        error -> Log.e("MyAmplifyApp", error.toString())
                );
            }
        });


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Amplify.Auth.getCurrentUser() == null) {
                    Amplify.Auth.signIn(
                            emailEditText.getText().toString(),
                            passwordEditText.getText().toString(),
                            result -> {
                                Log.i("MyAmplifyApp", result.isSignInComplete() ? "Sign in succeeded  " + Amplify.Auth.getCurrentUser() : "Sign in not complete");
                            },
                            error -> Log.e("MyAmplifyApp", error.toString())
                    );
                } else {
                    Log.i("MyAmplifyApp", "Another user is logged in");
                }

            }
        });


        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Amplify.Auth.signOut(
                        () -> Log.i("MyAmplifyApp", "Signed out successfully"),
                        error -> Log.e("MyAmplifyApp", error.toString())
                );
            }
        });

        credButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Amplify.Auth.fetchAuthSession(
                        result -> {
                            AWSCognitoAuthSession cognitoAuthSession = (AWSCognitoAuthSession) result;
                            switch (cognitoAuthSession.getIdentityId().getType()) {
                                case SUCCESS:
                                    Log.i("MyAmplifyApp", "IdentityId: " + cognitoAuthSession.getIdentityId().getValue() + "   " + Amplify.Auth.getCurrentUser());
                                    break;
                                case FAILURE:
                                    Log.i("MyAmplifyApp", "IdentityId not present because: " + cognitoAuthSession.getIdentityId().getError().toString());
                            }
                        },
                        error -> Log.e("MyAmplifyApp", error.toString())
                );
            }
        });
    }

    private void uploadFile() {
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File exampleFile = new File(getApplicationContext().getFilesDir(), "ExampleKey12.txt");

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(exampleFile));
                    writer.append("Example file contents");
                    writer.close();
                } catch (Exception exception) {
                    Log.e("MyAmplifyApp", "Upload failed", exception);
                }

                StorageUploadFileOptions options =
                        StorageUploadFileOptions.builder()
                                .accessLevel(StorageAccessLevel.PRIVATE)
                                .build();

                Amplify.Storage.uploadFile(
                        "ExampleKey12",
                        exampleFile,
                        options,
                        result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
                        error -> Log.e("MyAmplifyApp", "Upload failed", error)
                );
            }
        });

    }

    private void downloadFile() {
        Amplify.Storage.downloadFile(
                "ExampleKey",
                new File(getApplicationContext().getFilesDir() + "/download.txt"),
                result -> {
                    Log.i("MyAmplifyApp", "Successfully downloaded: " + result.getFile().getName() + getApplicationContext().getFilesDir());
                    Log.i("MyAmplifyApp", "Successfully downloaded: " + readFile(result.getFile()));
                },
                error -> Log.e("MyAmplifyApp", "Download Failure", error)
        );
        listFile();
    }

    private String readFile(File file) {


        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }

        return text.toString();
    }

    private void listFile() {
        StorageListOptions options = StorageListOptions.builder()
                .accessLevel(StorageAccessLevel.PRIVATE)
                .build();
        Amplify.Storage.list(
                "",
                options,
                result -> {
                    for (StorageItem item : result.getItems()) {
                        Log.i("MyAmplifyApp", "Item: " + item.getKey());
                    }
                },
                error -> Log.e("MyAmplifyApp", "List failure", error)
        );

    }

    private void removeFile() {
        Amplify.Storage.remove(
                "ExampleKey",
                result -> Log.i("MyAmplifyApp", "Successfully removed: " + result.getKey()),
                error -> Log.e("MyAmplifyApp", "Remove failure", error)
        );
    }
}