package com.example.fangfei;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.models.sort.SortingTypes;
import droidninja.filepicker.utils.ContentUriUtils;
import droidninja.filepicker.views.FilePickerConst;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks;

public class MainActivity extends AppCompatActivity implements PermissionCallbacks {
    private int MAX_ATTACHMENT_COUNT = 10;
    public static final int RC_FILE_PICKER_PERM = 321;
    private GridView grid_view;
    private ArrayList data_list;
    private int[] to;
    private Object[] icon = {R.drawable.excel, R.drawable.word, R.drawable.ppt, R.drawable.txt, R.drawable.pdf, R.drawable.more};
    private String[] iconName = {"XLS", "DOC", "PPT", "TXT", "PDF", "更多"};
    private String[] from;
    private Toolbar toolbar;
    private String[] provinceNames;
    private int[] bgColor;
    private ProvinceAdapter mProvinceAdapter;
    private GridViewAdapter gridViewAdapter;
    private ArrayList<Uri> photoPaths = new ArrayList<>();
    private ArrayList<Uri> docPaths = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
        // 延伸显示区域到刘海
        from = new String[]{"image", "text"};
        data_list = new ArrayList<Map<String, Object>>();
        to = new int[]{R.id.image, R.id.text};
//
        setContentView(R.layout.activity_main);
        initView();

        setOnClickListener();

    }

    private void setOnClickListener() {
        grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //选取手机中的excel
                        pickDocClicked();

                        break;
                    case 1:

                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        Toast.makeText(getApplicationContext(), "敬请期待", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    private void pickDocClicked() {

        if (EasyPermissions.hasPermissions(this, FilePickerConst.PERMISSIONS_FILE_PICKER)) {
            onPickDoc();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_doc_picker),
                    RC_FILE_PICKER_PERM, FilePickerConst.PERMISSIONS_FILE_PICKER);
        }
    }

    private void onPickDoc() {
        String[] zips = {"zip", "rar"};
        String[] pdfs = {"pdf"};
        int maxCount = MAX_ATTACHMENT_COUNT - photoPaths.size();
        if ((docPaths.size() + photoPaths.size()) == MAX_ATTACHMENT_COUNT) {
            Toast.makeText(this, "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items",
                    Toast.LENGTH_SHORT).show();
        } else {
            FilePickerBuilder.getInstance()
                    .setMaxCount(maxCount)
                    .setSelectedFiles(docPaths)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .setActivityTitle("Please select doc")
//                    .addFileSupport("ZIP", zips)
//                    .addFileSupport("PDF", pdfs, R.drawable.pdf)
                    .setViewPagePosition(3)
                    .enableDocSupport(true)
                    .enableSelectAll(true)
                    .sortDocumentsBy(SortingTypes.name)
                    .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .pickFile(this);
        }
    }

    private void initView() {
        getData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           Toolbar toolbar = findViewById(R.id.top);
            toolbar.setTitle(getString(R.string.app_name));
        }
        grid_view = findViewById(R.id.grid_view);
        gridViewAdapter = new GridViewAdapter(this, data_list, R.layout.item, from, to);
        grid_view.setAdapter(gridViewAdapter);
    }

    public List<Map<String, Object>> getData() {
        //cion和iconName的长度是相同的，这里任选其一都可以
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }
        return data_list;
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                    if (dataList != null) {
                        docPaths = new ArrayList<>();
                        docPaths.addAll(dataList);
                    }
                }
                break;
        }

        addThemToView(photoPaths, docPaths);
    }

    private void addThemToView(ArrayList<Uri> imagePaths, ArrayList<Uri> docPaths) {
        ArrayList<Uri> filePaths = new ArrayList<>();
        if (imagePaths != null) filePaths.addAll(imagePaths);

        if (docPaths != null) filePaths.addAll(docPaths);

        final RecyclerView recyclerView = findViewById(R.id.recyclerview);

        if (recyclerView != null) {
            StaggeredGridLayoutManager layoutManager =
                    new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
            layoutManager.setGapStrategy(
                    StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            recyclerView.setLayoutManager(layoutManager);

            ImageAdapter imageAdapter = new ImageAdapter(this, filePaths, new ImageAdapter.ImageAdapterListener() {
                @Override
                public void onItemClick(Uri uri) {
                    try {
                        //make sure to use this getFilePath method from worker thread
                        String path = ContentUriUtils.INSTANCE.getFilePath(recyclerView.getContext(), uri);
                        if (path != null) {
                            Toast.makeText(recyclerView.getContext(), path, Toast.LENGTH_SHORT).show();
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            });

            recyclerView.setAdapter(imageAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }
}

