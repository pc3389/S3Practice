package android.example.s3practice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StorageItem;
import com.amplifyframework.storage.options.StorageUploadFileOptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        uploadFile();
//        downloadFile();
        listFile();
    }

    private void uploadFile() {
        File exampleFile = new File(getApplicationContext().getFilesDir(), "ExampleKey");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(exampleFile));
            writer.append("Example file contents");
            writer.close();
        } catch (Exception exception) {
            Log.e("MyAmplifyApp", "Upload failed", exception);
        }

        Amplify.Storage.uploadFile(
                "ExampleKey",
                exampleFile,
                StorageUploadFileOptions.defaultInstance(),
                progress -> Log.i("MyAmplifyApp", "Fraction completed: " + progress.getFractionCompleted()),
                result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
                storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
        );
    }

    private void downloadFile() {
        Amplify.Storage.downloadFile(
                "ExampleKey",
                new File(getApplicationContext().getFilesDir() + "/download.txt"),
                result -> Log.i("MyAmplifyApp", "Successfully downloaded: " + result.getFile().getName() + getApplicationContext().getFilesDir()),
                error -> Log.e("MyAmplifyApp",  "Download Failure", error)
        );
    }

    private void listFile() {
        Amplify.Storage.list(
                "/",
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