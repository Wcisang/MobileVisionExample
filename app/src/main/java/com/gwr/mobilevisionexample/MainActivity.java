package com.gwr.mobilevisionexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gwr.mobilevisionexample.barcode.BarCodeActivity;
import com.gwr.mobilevisionexample.face.FaceActivity;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listview;
    private String[] classes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (ListView) findViewById(R.id.list);
        classes = new String[] {"Bar Code Reader","Face Detector"};
        listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,classes));
        listview.setOnItemClickListener(this);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String classe = (String) parent.getItemAtPosition(position);
        Intent it = null;
        switch (classe){
            case "Bar Code Reader":
                it = new Intent(MainActivity.this,BarCodeActivity.class);
                break;
            case "Face Detector":
                it = new Intent(MainActivity.this,FaceActivity.class);
                break;
        }

        if(it != null)
            startActivity(it);
    }
}
