package com.example.btlealumnos2021;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import com.example.btlealumnos2021.R;

public class ButtonGroup implements View.OnClickListener {

    private Button[] btn = new Button[3];
    private Button btn_unfocus;
    private int[] btn_id = {R.id.btn0, R.id.btn1, R.id.btn2};

    public void crearBotones(){
        for(int i = 0; i < btn.length; i++){
            //btn[i] = (Button) findViewById(btn_id[i]);
            btn[i].setBackgroundColor(Color.rgb(207, 207, 207));
            btn[i].setOnClickListener(this);
        }

        btn_unfocus = btn[0];
    }

    @Override
    public void onClick(View v) {
        //setForcus(btn_unfocus, (Button) findViewById(v.getId()));
        //Or use switch
        switch (v.getId()){
            case R.id.btn0 :
                setFocus(btn_unfocus, btn[0]);
                break;

            case R.id.btn1 :
                setFocus(btn_unfocus, btn[1]);
                break;

            case R.id.btn2 :
                setFocus(btn_unfocus, btn[2]);
                break;
        }
    }

    private void setFocus(Button btn_unfocus, Button btn_focus){
        btn_unfocus.setTextColor(Color.rgb(49, 50, 51));
        btn_unfocus.setBackgroundColor(Color.rgb(207, 207, 207));
        btn_focus.setTextColor(Color.rgb(255, 255, 255));
        btn_focus.setBackgroundColor(Color.rgb(3, 106, 150));
        this.btn_unfocus = btn_focus;
    }

}
