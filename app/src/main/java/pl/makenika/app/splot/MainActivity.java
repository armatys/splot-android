package pl.makenika.app.splot;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.util.Map;

import pl.makenika.splot.SplotEngine;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SplotEngine splotEngine = new SplotEngine(this);
        try {
            Pair<Boolean, String> result = splotEngine.loadLuaModule("appmain");
            if (Boolean.FALSE == result.first) {
                Log.d("MainActivity", result.second);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Test test = new Test(this);
        final Pair<Double, byte[]> calc = test.calculate(22.0);
        Log.d(TAG, "calculate: " + calc.first + "; " + new String(calc.second));

        test.callme();

        final Test.PTcomplexFn1 pTcomplexFn1 = test.new PTcomplexFn1(false);
        pTcomplexFn1.setVal(25.0);
        final Test.RTcomplexFn1 rTcomplexFn1 = test.complexFn(pTcomplexFn1);
        rTcomplexFn1.clear();
        rTcomplexFn1.put(123.0, "Hej!".getBytes());
        for (Map.Entry<Double, byte[]> entry : rTcomplexFn1.entrySet()) {
            Log.d(TAG, "rTcomplexFn1: " + entry.getKey() + ": " + new String(entry.getValue()));
        }
        Log.d(TAG, "rTcomplexFn1 size: " + rTcomplexFn1.size());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
