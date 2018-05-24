package edu.khai.csn.abondar.passwordmanager.View;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.khai.csn.abondar.passwordmanager.Model.Cryptography;
import edu.khai.csn.abondar.passwordmanager.Model.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.IOHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.User;
import edu.khai.csn.abondar.passwordmanager.R;

public class SettingsActivity extends AppCompatActivity implements DirectoryChooserFragment.OnFragmentInteractionListener {

    private FrameLayout mImport;
    private FrameLayout mExport;
    private ArrayList<Password> mPasswords;
    private User mUser;
    private TextView mName;
    private TextView mUsername;
    private TextView mEmail;
    private EditText mMasterPass;
    private FrameLayout btnChangeMaster;
    private DBHelper db;
    private Cryptography crypto;
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mImport = findViewById(R.id.btnImport);
        mExport = findViewById(R.id.btnExport);
        mToolBar = findViewById(R.id.navAction);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mName = findViewById(R.id.tvName);
        mUsername = findViewById(R.id.tvUsernameSettings);
        mEmail = findViewById(R.id.tvEmailSettings);
        mMasterPass = findViewById(R.id.editMasterPass);
        btnChangeMaster = findViewById(R.id.btnChangeMaster);
        db = new DBHelper(this);
        crypto = new Cryptography("passwordmanager1");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPasswords = (ArrayList<Password>) extras.getSerializable("passwords");
            mUser = (User) extras.getSerializable("user");
        }
        mName.setText("Name: " + mUser.getName());
        mUsername.setText("Username: " + mUser.getUsername());
        mEmail.setText("Email: " + mUser.getEmail());
        try {
            mMasterPass.setText(crypto.decrypt(mUser.getMasterPassword()));
        } catch (Exception e) {
        }
        btnChangeMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = "";
                try {
                    String str = mMasterPass.getText().toString().trim();
                    pass = crypto.encrypt(str);
                } catch (Exception e) {
                }
                db.updateMaster(mUser, pass);
                Toast.makeText(getApplication(), "Master Password has been changed", Toast.LENGTH_LONG).show();
            }
        });


        DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("DialogSample")
                .build();
        mDialog = DirectoryChooserFragment.newInstance(config);
        mExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToXml();
            }
        });
        mImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readFromXml();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            setResult(789, new Intent());
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //@Override
    //protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    super.onActivityResult(requestCode, resultCode, data);
//
    //    if (requestCode == 0) {
    //        if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
    //            writeToXml(data
    //                    .getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
    //        } else {
    //            // Nothing selected
    //        }
    //    }
    //}
    private DirectoryChooserFragment mDialog;

    private void writeToXml() {
        mDialog.show(getFragmentManager(), null);

    }

    private void writeToXml(String dir) {
        XmlSerializer serializer = Xml.newSerializer();

        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "passwords");
            serializer.attribute("", "owner", mUser.getUsername());
            for (Password password : mPasswords) {
                serializer.startTag("", "details");
                serializer.attribute("", "id", String.valueOf(password.getId()));

                serializer.startTag("", "service");
                serializer.text(password.getServiceName());
                serializer.endTag("", "service");

                serializer.startTag("", "username");
                serializer.text(password.getUserName());
                serializer.endTag("", "username");

                serializer.startTag("", "password");
                serializer.text(password.getPassword());
                serializer.endTag("", "password");

                serializer.startTag("", "addInfo");
                serializer.text(password.getAdditionalInformation());
                serializer.endTag("", "addInfo");

                serializer.startTag("", "creationDate");
                serializer.text(password.getCreationDate().toString());
                serializer.endTag("", "creationDate");

                serializer.startTag("", "modifyingDate");
                serializer.text(password.getModifyingDate().toString());
                serializer.endTag("", "modifyingDate");

                serializer.endTag("", "details");
            }
            serializer.endTag("", "passwords");
            serializer.endDocument();
            String result = writer.toString();

            IOHelper.writeToFile(this, dir + "/passwords.xml", result);
        } catch (Exception e) {
            Log.e("Exception", "Xml didn't write well");
        }
    }

    private void readFromXml() {

        Intent intent = new Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            //String str = data.getDataString();


            Uri selectedfile = data.getData(); //The uri with the location of the file
            String str = getPathFromUri(this, selectedfile);
            parseXml(str);
        }
    }

    private void parseXml(String filepath) {
        XmlPullParserFactory factory;
        FileInputStream fis = null;
        ArrayList<Password> _passwords = new ArrayList<>();
        try {
            StringBuilder sb = new StringBuilder();
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            File file = new File(filepath);
            //String str = file.getAbsolutePath();
            fis = new FileInputStream(file);
            xpp.setInput(fis, null);
            //xpp.setInput(new ByteArrayInputStream(xmlString.getBytes()),null);
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                Password password = new Password();
                while (eventType != XmlPullParser.END_TAG && xpp.getName() != "details") {

                    if (eventType == XmlPullParser.START_TAG) {
                        switch (xpp.getName()) {
                            case "service":
                                xpp.next();
                                password.setServiceName(xpp.getText());
                                xpp.next();
                                break;
                            case "username":
                                xpp.next();
                                password.setUserName(xpp.getText());
                                xpp.next();
                                break;
                            case "password":
                                xpp.next();
                                password.setPassword(xpp.getText());
                                xpp.next();
                                break;
                            case "addInfo":
                                xpp.next();
                                password.setAdditionalInformation(xpp.getText());
                                xpp.next();
                                break;
                            case "creationDate":
                                xpp.next();
                                String dtStart = xpp.getText();
                                Date date = new Date();
                                SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
                                try {
                                    date = format.parse(dtStart);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                password.setCreationDate(date);
                                xpp.next();
                                break;
                            case "modifyingDate":
                                xpp.next();
                                String dtStartMod = xpp.getText();
                                Date dateMod = new Date();
                                SimpleDateFormat formatMod = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
                                try {
                                    dateMod = formatMod.parse(dtStartMod);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                password.setModifyingDate(dateMod);
                                xpp.next();
                                break;
                            default:
                                break;
                        }
                    }
                    eventType = xpp.next();
                }
                eventType = xpp.next();
                if (password.getServiceName() != null)
                    _passwords.add(password);
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        addImported(_passwords);
    }

    private void addImported(ArrayList<Password> _passwords) {
        int i = mPasswords.size();
        for (Password p : _passwords) {
            int j = 0;
            for(; j < i; j++) {
                if (mPasswords.get(j).getServiceName().equals(p.getServiceName()) &&
                        mPasswords.get(j).getUserName().equals(p.getUserName())) {
                    break;
                }
            }
            if(i == j)
                mPasswords.add(p);
        }

        Bundle extras = new Bundle();
        extras.putSerializable("imported", mPasswords);
        Intent intent = new Intent();
        intent.putExtras(extras);
        setResult(1101, intent);
        finish();
    }

    @Override
    public void onSelectDirectory(@NonNull String path) {
        writeToXml(path);
        mDialog.dismiss();
    }

    @Override
    public void onCancelChooser() {
        mDialog.dismiss();
    }

    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
