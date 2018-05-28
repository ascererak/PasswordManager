package edu.khai.csn.abondar.passwordmanager.Model;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

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

import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.User;

/**
 * Created by Alexey Bondar on 28-May-18.
 */

public class XmlOperations {

    private User mUser;
    private ArrayList<Password> mPasswords;
    private String mDirectory;
    private Context mContext;
    private String mPath;
    private ArrayList<Password> passToImport;

    public void writeToXml() {
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

            IOHelper.writeToFile(mContext, mDirectory + "/passwords.xml", result);
        } catch (Exception e) {
            Log.e("Exception", "Xml didn't write well");
        }
    }

    public void parseXml() {
        XmlPullParserFactory factory;
        FileInputStream fis = null;
        passToImport = new ArrayList<>();
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            File file = new File(mPath);
            fis = new FileInputStream(file);
            xpp.setInput(fis, null);
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
                    passToImport.add(password);
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
    }

    public void setmUser(User mUser) {
        this.mUser = mUser;
    }

    public void setmPasswords(ArrayList<Password> mPasswords) {
        this.mPasswords = mPasswords;
    }

    public void setmDirectory(String mDirectory) {
        this.mDirectory = mDirectory;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public ArrayList<Password> getPassToImport() {
        return passToImport;
    }
}
