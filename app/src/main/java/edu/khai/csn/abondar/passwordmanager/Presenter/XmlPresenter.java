package edu.khai.csn.abondar.passwordmanager.Presenter;

import edu.khai.csn.abondar.passwordmanager.Model.XmlOperations;
import edu.khai.csn.abondar.passwordmanager.View.XmlView;

/**
 * Created by Alexey Bondar on 28-May-18.
 */

public class XmlPresenter {
    XmlView view;
    XmlOperations model = new XmlOperations();

    public XmlPresenter(XmlView view) {
        this.view = view;
    }

    public void writeToXml(){
        model.setmPasswords(view.getmPasswords());
        model.setmUser(view.getmUser());
        model.setmDirectory(view.getmDirectory());
        model.setmContext(view.getmContext());
        model.writeToXml();
    }

    public void parseXml() {
        model.setmPath(view.getmPath());
        model.parseXml();
        view.setPassToImport(model.getPassToImport());
    }
}
