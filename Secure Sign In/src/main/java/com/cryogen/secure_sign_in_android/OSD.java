package com.cryogen.secure_sign_in_android;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class OSD extends AppCompatActivity
{
    //Instance Variables
    private char[] password;
    private String passwordString = "";
    //UI References
    EditText txtPassword;
    Button btnOK, btnCopy, btnDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osd);

        txtPassword = (EditText)findViewById(R.id.txtPassword);
        txtPassword.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        btnOK = (Button)findViewById(R.id.btnOK);
        btnCopy = (Button)findViewById(R.id.btnCopy);
        btnDisplay = (Button)findViewById(R.id.btnDisplay);

        password = SecureSignInMain.getPassword();
        for(char t : password)
            passwordString += '\u25cf';

        txtPassword.setText(passwordString);

        btnOK.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();//Go back to calling activity
            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(null, (new String(password)).substring(0, (new String(password)).indexOf(0)));
                clipboard.setPrimaryClip(clip);
                finish();
            }
        });

        btnDisplay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (txtPassword.getText().charAt(0) =='\u25cf')
                {
                    txtPassword.setText(new String(password));
                    btnDisplay.setText("Hide Password");
                } else
                {
                    txtPassword.setText(passwordString);
                    btnDisplay.setText("Reveal Password");
                }
            }
        });
    }

}
