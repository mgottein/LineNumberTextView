package mgottein.me.sample;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import me.mgottein.LineNumberEditText;


public class MainActivity extends ActionBarActivity implements SettingsFragment.SettingsListener {

    private LineNumberEditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (LineNumberEditText) findViewById(R.id.demo);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //see http://developer.android.com/reference/android/app/DialogFragment.html#BasicDialog
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            SettingsFragment settings = SettingsFragment.newInstance(editText.getGravity(), editText.doLineNumbersHugLine(),
                    editText.layoutLineNumbersOnLeft());
            settings.show(ft, "dialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setGravity(int gravity) {
        editText.setGravity(gravity);
    }

    @Override
    public void setShouldLineNumbersHugLine(boolean hugLine) {
        editText.doLineNumbersHugLine(hugLine);
    }

    @Override
    public void layoutLineLumbersOnLeft(boolean layoutOnLeft) {
        editText.layoutLineNumbersOnLeft(layoutOnLeft);
    }
}
