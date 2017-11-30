package org.xqj.shandiandai.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.xqj.shandiandai.R;
import org.xqj.shandiandai.utils.Constants;
import org.xqj.shandiandai.utils.DeviceUtil;
import org.xqj.shandiandai.utils.GetPathFromUri4kitkat;
import org.xqj.shandiandai.utils.PopupWindowUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.shenfan.updateapp.UpdateService;

public class HtmlActivity extends AppCompatActivity {


    @Bind(org.xqj.shandiandai.R.id.webView)
    WebView webView;
    @Bind(org.xqj.shandiandai.R.id.bar)
    ProgressBar bar;
    public static void launch(Context context) {
        context.startActivity(new Intent(context, HtmlActivity.class));
    }

    private ValueCallback mFilePathCallback;
    private File vFile;
    private Uri origUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);
        ButterKnife.bind(this);
        CheckInternet();
    }

    private void CheckInternet() {
            ConnectivityManager con= (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
            boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
            if(wifi|internet){
                //执行相关操作
                getDate();

            }else{
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("网络异常，请检查网络")
                     .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                         @Override
                         public void onClick(SweetAlertDialog sweetAlertDialog) {
                                finish();
                         }
                     })
                        .show();


            }
    }

    private void getDate() {
        String html = getIntent().getStringExtra("url");
        if(html!=null){
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.setWebChromeClient(new WebChromeClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setAppCachePath(getCacheDir().getPath());
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setSupportZoom(false);
            if (Build.VERSION.SDK_INT >= 21) {
                webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }

            /**
             * 设置获取位置
             */
            //启用数据库
            webView.getSettings().setDatabaseEnabled(true);
            //设置定位的数据库路径
            String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
            webView.getSettings().setGeolocationDatabasePath(dir);
            //启用地理定位
            webView.getSettings().setGeolocationEnabled(true);
            //开启DomStorage缓存
            webView.getSettings().setDomStorageEnabled(true);



            webView.loadUrl(html);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return false;
                }
            });
            webView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if (keyEvent.getAction() == keyEvent.ACTION_DOWN) {

                        if (i == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                            webView.goBack();
                            return true;
                        }
                    }
                    return false;
                }
            });

            //h5下载
            webView.setDownloadListener(new DownloadListener() {

                @Override
                public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype,
                                            long contentLength) {
                    if (url.endsWith(".apk")) {//判断是否是.apk结尾的文件路径
                        if (DeviceUtil.isWifiAvailable(HtmlActivity.this)) {
                            UpdateService.Builder.create(url).build(HtmlActivity.this);
                        } else {
                            final AlertDialog alertDialog = new AlertDialog.Builder(HtmlActivity.this,R.style.CustomDialog).create();
                            alertDialog.setCancelable(false);
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();
                            Window window = alertDialog.getWindow();
                            window.setContentView(R.layout.integral_exchange_tips1);
                            TextView tv1 = (TextView) window.findViewById(R.id.integral_exchange_tips1_tv);
                            tv1.setText("亲，您现在是非wifi状态下，确定要下载吗？");
                            RelativeLayout rl2 = (RelativeLayout) window.findViewById(R.id.integral_exchange_tips1_rl1);
                            RelativeLayout rl3 = (RelativeLayout) window.findViewById(R.id.integral_exchange_tips1_rl2);
                            rl2.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            });
                            rl3.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    UpdateService.Builder.create(url).build(HtmlActivity.this);
                                    alertDialog.dismiss();
                                }
                            });
                        }

                    }
                }
            });





            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    // TODO 自动生成的方法存根

                    if (newProgress == 100) {
                        bar.setVisibility(View.GONE);//加载完网页进度条消失
                    } else {
                        bar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                        bar.setProgress(newProgress);//设置进度值
                    }
                }


                //获取位置
                @Override
                public void onGeolocationPermissionsShowPrompt(String origin,GeolocationPermissions.Callback callback) {
                    callback.invoke(origin, true, false);
                    super.onGeolocationPermissionsShowPrompt(origin, callback);
                }

                /**
                 * h5打开相机或相册
                 */

                //5.0+
                @Override
                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                    try {
                        showMyDialog();
                        mFilePathCallback = filePathCallback;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }


                // Andorid 4.1+
                public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                    try {
                        showMyDialog();
                        mFilePathCallback = uploadFile;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Andorid 3.0 +
                public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType) {
                    try {
                        showMyDialog();
                        mFilePathCallback = uploadFile;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Android 3.0
                public void openFileChooser(ValueCallback<Uri> uploadFile) {
                    try {
                        showMyDialog();
                        mFilePathCallback = uploadFile;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        }



    private void showMyDialog() {
        View rootView=getLayoutInflater().inflate(R.layout.activity_main,null);
        PopupWindowUtil.showPopupWindow(this,rootView,"相机","文件","取消",
                new PopupWindowUtil.onPupupWindowOnClickListener() {
                    @Override
                    public void onFirstButtonClick() {
                        int flag1 = ActivityCompat.checkSelfPermission(HtmlActivity.this, Manifest.permission.CAMERA);
                        int flag2 = ActivityCompat.checkSelfPermission(HtmlActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (PackageManager.PERMISSION_GRANTED != flag1||PackageManager.PERMISSION_GRANTED != flag2) {
                            ActivityCompat.requestPermissions(HtmlActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_CAMERA_PERMISSION);
                            cancelFilePathCallback();
                        }else {
                            takeForPicture();
                        }
                    }

                    @Override
                    public void onSecondButtonClick() {
                        int flag2 = ActivityCompat.checkSelfPermission(HtmlActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (PackageManager.PERMISSION_GRANTED != flag2) {
                            ActivityCompat.requestPermissions(HtmlActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_READ_EXTERNAL_PERMISSION);
                            cancelFilePathCallback();
                        }else {
                            takeForPhoto();
                        }
                    }

                    @Override
                    public void onCancleButtonClick() {
                        cancelFilePathCallback();
                    }
                });
    }

    /**
     * 调用相册
     */
    private void takeForPhoto() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"), Constants.REQUEST_CODE_PICK_PHOTO);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),Constants.REQUEST_CODE_PICK_PHOTO);
        }
    }


    /**
     * 调用相机
     */
    private void takeForPicture() {

        try {
            String storageState = Environment.getExternalStorageState();
            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                vFile = new File(Environment.getExternalStorageDirectory().getPath()
                        + "/xianjindai/");//图片位置
                if (!vFile.exists()) {
                    vFile.mkdirs();
                }
            } else {
                Toast.makeText(HtmlActivity.this, "未挂载sdcard", Toast.LENGTH_LONG).show();
                return;
            }

            String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new
                    Date()) + ".jpg";

            Uri uri = Uri.fromFile(new File(vFile, fileName));

            //拍照所存路径
            origUri = uri;

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (Build.VERSION.SDK_INT > 23) {//7.0及以上
//            Uri contentUri = getUriForFile(MainActivity.this, "com.xinhe.crame", picturefile);
//            grantUriPermission(getPackageName(),contentUri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
//        } else {//7.0以下
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picturefile));
//        }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, origUri);
            startActivityForResult(intent, Constants.REQUEST_CODE_TAKE_PICETURE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void cancelFilePathCallback() {
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
            mFilePathCallback = null;
        }
    }

    private void takePhotoResult(int resultCode, Intent data) {
        if (mFilePathCallback != null){
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result != null) {
                String path = GetPathFromUri4kitkat.getPath(this, result);
                Uri uri = Uri.fromFile(new File(path));
                if (Build.VERSION.SDK_INT > 18) {
                    mFilePathCallback.onReceiveValue(new Uri[]{uri});
                } else {
                    mFilePathCallback.onReceiveValue(uri);
                }

            }else {
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
        }
    }

    private void takePictureResult(int resultCode) {
        if (mFilePathCallback != null) {
            if (resultCode == RESULT_OK) {

                if (Build.VERSION.SDK_INT > 18) {
                    mFilePathCallback.onReceiveValue(new Uri[]{origUri});
                } else {
                    mFilePathCallback.onReceiveValue(origUri);
                }
            } else {
                //点击了file按钮，必须有一个返回值，否则会卡死
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 105:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "拍照权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 105:
                takePictureResult(resultCode);
                break;

            case 106:
                takePhotoResult(resultCode, data);

                break;
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }


}
