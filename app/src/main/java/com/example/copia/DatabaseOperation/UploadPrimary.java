package com.example.copia.DatabaseOperation;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class UploadPrimary
{
    boolean finished = false;
    boolean successful = false;
    public ParseObject client_upload(HashMap<String, String> clientData, ArrayList<String> tags)
    {
        finished = false;
        successful = false;
        final ParseObject query = new ParseObject("Client");
        query.put("Representative", clientData.get("Representative"));
        query.put("Position", clientData.get("Position"));
        query.put("Company", clientData.get("Company"));
        query.put("Industry", clientData.get("Industry"));
        query.put("Type", clientData.get("Type"));
        query.put("Tags", new JSONArray(tags));
        query.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                    successful = true;
                finished= true;
            }
        });
        while(finished == false)
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return query;
    }

    public ParseObject suppliers_upload(HashMap<String, String> suppliersData, ArrayList<String> tags)
    {
        finished = false;
        successful = false;
        final ParseObject query = new ParseObject("Suppliers");
        query.put("Representative", suppliersData.get("Representative"));
        query.put("Position", suppliersData.get("Position"));
        query.put("Company_Name", suppliersData.get("Company_Name"));
        query.put("Brand", suppliersData.get("Brand"));
        query.put("Industry", suppliersData.get("Industry"));
        query.put("Type", suppliersData.get("Type"));
        query.put("Tags", new JSONArray(tags));
        query.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                    successful = true;
                finished= true;
            }
        });
        while(finished == false)
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return query;
    }
}
