package project.test.mild;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class Help extends AppCompatActivity {
    WebView webview;
    Button back;
    int user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        //Initiate variable dan koneksi dengan database
        back = (Button) findViewById(R.id.buttonBackHelp);
        Intent intent = getIntent();
        user = intent.getIntExtra("user", 1);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainBody.class);
                i.putExtra("user", user);
                startActivity(i);
                finish();
            }
        });
    }
}
