package edu.khai.csn.abondar.passwordmanager;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;

import edu.khai.csn.abondar.passwordmanager.Model.Entities.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.User;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private DBHelper db;
    Password password;
    ArrayList<Password> arrayList;
    User user;
    Cryptography cryptography;
    String encryptedPass;

    @Before
    public void setUp() {
        cryptography = new Cryptography("passwordmanager1");
        try{encryptedPass = cryptography.encrypt("qwerty");}catch(Exception e){}
        db = new DBHelper(InstrumentationRegistry.getTargetContext());
        //password = new Password("qwerty1",
        //        "service", "lex", "some", 1,new Date(), new Date());
        //arrayList = new ArrayList<>();
        //arrayList.add(password);
        //user = new User(1, "lex", "lexich",
        //        "a@email.com", "qwerty", arrayList);
        //db.addUser(user, password);
    }

    @After
    public void finish() {
        db.close();
    }

    @Test
    public void preCond() {
        assertNotNull(db);
    }

    @Test
    public void getUserTest() {
        assertTrue(db.getUser("lex", encryptedPass));
    }
}
