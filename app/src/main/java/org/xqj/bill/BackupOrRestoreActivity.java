package org.xqj.bill;

import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xqj.bill.model.BillItem;
import org.xqj.bill.model.ConsumptionType;
import org.xqj.bill.model.serializable.SerializableBillItem;
import org.xqj.bill.model.serializable.SerializableConsumptionType;
import org.xqj.bill.model.serializable.SerializeUtils;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_or_restore);

        ButterKnife.bind(this);

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
            int size = ois.readInt();
            for (int i = 0; i < size; i++) {
                types.add((SerializableConsumptionType) ois.readObject());
            }
            size = ois.readInt();
            Number number = realm.where(BillItem.class).max("id");
            int maxId = number == null ? 0 : number.intValue();
            for (int i = 0; i < size; i++) {
                SerializableBillItem item = (SerializableBillItem) ois.readObject();
                item.setId(maxId + i + 1);
                items.add(item);
            }
            ois.close();
            is.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            mStatusText.setText(R.string.restore_failed);
        }
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(SerializeUtils.toConsumptionTypeList(types));
        realm.copyToRealm(SerializeUtils.toBillItemList(items));
        realm.commitTransaction();

        Toast.makeText(this, R.string.restore_succeed, Toast.LENGTH_SHORT).show();
    }

    private void backup(File backupFile) {
        Realm realm = Realm.getInstance(this);
        try {
            FileOutputStream os = new FileOutputStream(backupFile);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            List<SerializableConsumptionType> types =
                    SerializeUtils.toSerializableConsumptionTypeList(realm.allObjects(ConsumptionType.class));
            oos.writeInt(types.size());
            oos.flush();
            for (SerializableConsumptionType type : types) {
                oos.writeObject(type);
            }
            List<SerializableBillItem> billResults =
                    SerializeUtils.toSerializableBillItemList(realm.allObjects(BillItem.class));
            oos.writeInt(billResults.size());
            oos.flush();
            for (SerializableBillItem item : billResults) {
                oos.writeObject(item);
            }
            oos.flush();
            oos.close();
            os.close();
            Calendar calendar = Calendar.getInstance();
            mStatusText.setText(String.format(
                    getString(R.string.backup_succeed_format), DateFormat.format("yyyy年MM月dd日 hh:mm", calendar)));
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
