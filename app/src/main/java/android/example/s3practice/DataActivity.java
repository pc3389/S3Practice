package android.example.s3practice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Post;
import com.amplifyframework.datastore.generated.model.PostStatus;

public class DataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        Button postButton;
        Button queryButton;

        postButton = findViewById(R.id.button_post);
        queryButton = findViewById(R.id.button_query);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Post post = Post.builder()
                        .title("My First Post")
                        .status(PostStatus.PUBLISHED)
                        .rating(10)
                        .content("Hello All")
                        .image("S3")
                        .build();

                Amplify.DataStore.save(post,
                        saved -> Log.i("MyAmplifyApp", "Saved a post."),
                        failure -> Log.e("MyAmplifyApp", "Save failed.", failure)
                );
            }
        });

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Amplify.DataStore.query(Post.class,
                        queryMatches -> {
                            if (queryMatches.hasNext()) {
                                Log.i("MyAmplifyApp", "Successful query, found posts.");
                            } else {
                                Log.i("MyAmplifyApp", "Successful query, but no posts.");
                            }
                        },
                        error -> Log.e("MyAmplifyApp",  "Error retrieving posts", error)
                );
            }
        });

    }
}