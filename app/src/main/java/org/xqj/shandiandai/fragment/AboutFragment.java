package org.xqj.shandiandai.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import me.shenfan.updateapp.UpdateService;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {
    public static String NAME = "NAME";
    private static final String APP_URL = "http://bingo.shoujiweidai.com/apk/CashLoan.apk";

    @Bind(org.xqj.shandiandai.R.id.layout)
    ImageView layout;


    public static AboutFragment newInstance(String text) {
        Bundle args = new Bundle();
        args.putString(NAME, text);
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(org.xqj.shandiandai.R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);
        setListener();
        return view;
    }




    private void setListener() {
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getActivity())
                        .items(org.xqj.shandiandai.R.array.items)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), org.xqj.shandiandai.R.drawable.erweima);
                                        saveImage(bitmap);
                                        bitmap.recycle();
                                        break;
                                    case 1:
                                        TextNet();
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });


    }

    /**
     * 网络相关检测
     */
    private void TextNet() {
        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        if (wifi | internet) {
            //执行相关操作
            downloaderApp();
            Toasty.info(getActivity(), "开始下载", Toast.LENGTH_SHORT, true).show();
        } else {
            Toasty.error(getActivity(), "网络异常", Toast.LENGTH_SHORT, true).show();
        }
    }

    private void downloaderApp() {
        UpdateService.Builder.create(APP_URL)
                .setStoreDir("store")
                .setIsSendBroadcast(true)
                .setIcoResId(org.xqj.shandiandai.R.drawable.xianjindai)
                .setIcoSmallResId(android.R.drawable.ic_notification_overlay)
                .build(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    private void saveImage(Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "SchoolPicture");
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fos != null) {
            Toasty.success(getActivity(), "保存成功请到相册查看!", Toast.LENGTH_SHORT, true).show();

        }
        // 最后通知图库更新
        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));
    }
}
