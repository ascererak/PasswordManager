package edu.khai.csn.abondar.passwordmanager;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import edu.khai.csn.abondar.passwordmanager.Model.Cryptography;
import edu.khai.csn.abondar.passwordmanager.Model.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.User;
import edu.khai.csn.abondar.passwordmanager.Presenter.RecyclerAdapter;

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

    @Test
    public void getUserOverloadedTest() {
        assertTrue(db.getUser("lex3", "danon@gmail.com", 1));
    }

    @Test
    public void encryptTest(){
        String expected = encryptedPass;//"TOiKk+Nsr7SAnkH+Lbuh/Q==";
        String tested = "qwerty";
        String actual = "";

        try {
            actual = cryptography.encrypt(tested);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(expected, actual);
    }

    @Test
    public void decryptTest() {
        String expected = "qwerty";
        String tested = encryptedPass;//"TOiKk+Nsr7SAnkH+Lbuh/Q==";
        String actual = "";

        try{
            actual = cryptography.decrypt(tested);
        }
        catch (Exception e){}

        assertEquals(expected, actual);
    }

    @Test
    public void getUserByEmailTest() {
        String actual = db.getUserByEmail("danon@gmail.com");

        assertEquals(encryptedPass, actual);
    }

    @Test
    public void getUserByUsernameTest() {
        String expected = "Alexey Bondar";
        User user = db.getUser("lex3");
        String actual = user.getName();

        assertEquals(expected, actual);
    }

    @Test
    public void getUserByUsernameTestPassword() {
        String expected = encryptedPass;
        User user = db.getUser("lex3");
        String actual = user.getMasterPassword();

        assertEquals(expected, actual);
    }

    @Test
    public void getUserByUsernameTestUsername() {
        String expected = "lex3";
        User user = db.getUser("lex3");
        String actual = user.getUsername();

        assertEquals(expected, actual);
    }

    @Test
    public void getUserByUsernameTestEmail() {
        String expected = "danon@gmail.com";
        User user = db.getUser("lex3");
        String actual = user.getEmail();

        assertEquals(expected, actual);
    }

    @Test
    public void getItemCountTest(){
        ArrayList<Password> list = new ArrayList<>(2);
        RecyclerAdapter adapter = new RecyclerAdapter(list, InstrumentationRegistry.getTargetContext());
        int actual = adapter.getItemCount();
        int expected = list.size();

        assertEquals(expected, actual);
    }
}
