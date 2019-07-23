package jack.jiang.clickcounter;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set default counter for the text box
        Button clickMeBtn = (Button) findViewById(R.id.click);
        clickMeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myClick(v); /* my method to call new intent or activity */
            }
        });
    }

    private int nbOfClick = 0;

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//// Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    public void myClick(View v) {
// Write your own code
        nbOfClick++;
        TextView txCounter = (TextView) findViewById(R.id.counter);
        txCounter.setText(Integer.toString(nbOfClick));
    }
}