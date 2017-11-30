package org.xqj.shandiandai;

import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xqj.shandiandai.model.BillItem;
import org.xqj.shandiandai.model.ConsumptionType;
import org.xqj.shandiandai.model.serializable.SerializableBillItem;
import org.xqj.shandiandai.model.serializable.SerializableConsumptionType;
import org.xqj.shandiandai.model.serializable.SerializeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * @author Chaos
 *         2016/01/14.
 */
public class BackupOrRestoreActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String BACKUP_DIR = "PersonalBill";
    private static final String BACKUP_FILE = "bill.backup";

    @Bind(R.id.status) TextView mStatusText;
    @Bind(R.id.action_backup) Button mBackupBtn;
    @Bind(R.id.action_restore) Button mRestoreBtn;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if ("粉红".equals(PreferenceManager.getDefaultSharedPreferences(this).getString("default_theme", "粉红"))) {
            setTheme(R.style.AppTheme_Pink_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_or_restore);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mBackupBtn.setOnClickListener(this);
        mRestoreBtn.setOnClickListener(this);

        long preBackupTime = PreferenceManager.getDefaultSharedPreferences(this)
                .getLong(PreferenceKeys.KEY_LAST_BACKUP_TIME, -1);
        Calendar calendar = Calendar.getInstance();
        if (preBackupTime != -1) {
            calendar.setTimeInMillis(preBackupTime);
        }
        mStatusText.setText(String.format(
                getString(R.string.backup_succeed_format), DateFormat.format("yyyy年MM月dd日 hh:mm", calendar)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        File dir = new File(Environment.getExternalStorageDirectory(), BACKUP_DIR);
        File backupFile = new File(dir, BACKUP_FILE);

        if (mRestoreBtn == v) {
            if (dir.exists() && backupFile.exists()) {
                restore(backupFile);
            } else {
                mStatusText.setText(R.string.restore_failed);
            }
        } else if (mBackupBtn == v) {

            if (!dir.exists()) {
                dir.mkdirs();
            }

            if (backupFile.exists()) {
                backupFile.delete();
            }

            try {
                backupFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                mStatusText.setText(R.string.backup_failed);
                return;
            }

            backup(backupFile);
        }
    }

    private void restore(File backupFile) {
        List<SerializableConsumptionType> types = new ArrayList<>();
        List<SerializableBillItem> items = new ArrayList<>();
        Realm realm = Realm.getInstance(this);
        try {
            FileInputStream is = new FileInputStream(backupFile);
            ObjectInputStream ois = new ObjectInputStream(is);

            // read the size of types
            int size = ois.readInt();
            // read consumption types
            for (int i = 0; i < size; i++) {
                types.add((SerializableConsumptionType) ois.readObject());
            }

            // read the size of bills
            size = ois.readInt();
            // get current max id of bill in local database
            Number number = realm.where(BillItem.class).max("id");
            int maxId = number == null ? 0 : number.intValue();

            // read bills
            for (int i = 0; i < size; i++) {
                SerializableBillItem item = (SerializableBillItem) ois.readObject();
                // set bill's id
                item.setId(maxId + i + 1);
                items.add(item);
            }

            // release resources
            ois.close();
            is.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            mStatusText.setText(R.string.restore_failed);
        }

        // save restore's data to local database
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(SerializeUtils.toConsumptionTypeList(types));
        realm.copyToRealm(SerializeUtils.toBillItemList(items));
        realm.commitTransaction();

        // save the timestamp of successful restore
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putLong(PreferenceKeys.KEY_LAST_RESTORE_TIME, System.currentTimeMillis())
                .apply();
        Toast.makeText(this, R.string.restore_succeed, Toast.LENGTH_SHORT).show();
    }

    private void backup(File backupFile) {
        Realm realm = Realm.getInstance(this);
        try {
            FileOutputStream os = new FileOutputStream(backupFile);
            ObjectOutputStream oos = new ObjectOutputStream(os);

            // get all consumption types in local database
            List<SerializableConsumptionType> types =
                    SerializeUtils.toSerializableConsumptionTypeList(realm.allObjects(ConsumptionType.class));

            // write the size of types
            oos.writeInt(types.size());
            oos.flush();
            // write consumption types to file
            for (SerializableConsumptionType type : types) {
                oos.writeObject(type);
            }
            oos.flush();

            // get all bills in local database
            List<SerializableBillItem> billResults =
                    SerializeUtils.toSerializableBillItemList(realm.allObjects(BillItem.class));

            // write bills to file
            oos.writeInt(billResults.size());
            oos.flush();
            // write the size of bills
            for (SerializableBillItem item : billResults) {
                oos.writeObject(item);
            }
            oos.flush();

            // release resources
            oos.close();
            os.close();

            Calendar calendar = Calendar.getInstance();
            mStatusText.setText(String.format(
                    getString(R.string.backup_succeed_format), DateFormat.format("yyyy年MM月dd日 hh:mm", calendar)));
            // save the timestamp of successful backup
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putLong(PreferenceKeys.KEY_LAST_BACKUP_TIME, calendar.getTimeInMillis())
                    .apply();
            Toast.makeText(this, R.string.backup_succeed, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            mStatusText.setText(R.string.backup_failed);
        }
    }
}
