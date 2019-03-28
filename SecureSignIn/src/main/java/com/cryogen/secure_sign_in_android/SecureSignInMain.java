package com.cryogen.secure_sign_in_android;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import java.util.LinkedList;

/**
 * A screen to encrypt/complicate password.
 */
public class SecureSignInMain extends AppCompatActivity //implements LoaderCallbacks<Cursor>
{
	//Instance Variables
	private char[] password;
	private char[] key;
	private static char[] finalPassword;
	private int limit;
	//UI References
	Button btnEncryptPassword;
	EditText pswPassword, pswKey;
	Switch switchCompact;

	/**
	 * Default Constructor
	 */
	public SecureSignInMain()
	{
		password = null;
		key = null;
		finalPassword = null;
		limit = 32;

	}

	/**
	 * Get the user's password
	 * @return user's chosen password
	 */
	public static char[] getPassword()
    {
        return finalPassword;
    }

	/**
	 * Method to encrypt the password
	 * Based on Viginere's Cipher Algorithm, modified by Zander
	 * @param userPassword the password to be encrypted
	 * @param key          the key used to encrypt the password
	 * @return the encrypted password
	*/
	public static char[] encrypt(char[] userPassword, char[] key, int limit)
	{
		try
		{
			char[] systemPassword = new char[userPassword.length + 1];
			char[] finalPassword = new char[userPassword.length * 2 + 1];
			int keyIndex = 0;
			int i = 0;
			int ii = 0;
			int temp;
			int specCharCount = 0;
			int pos = 0;
			char[] specChars = new char[finalPassword.length];

			for (char t : userPassword) {
				if (t >= 65 && t <= 90) { //Encrypting Uppercase Characters
					temp = t - 65 + (key[keyIndex] - 65);
					if (temp < 0)
						temp += 26;
					if (temp <= 0)
						temp += 26;

					systemPassword[i++] = (char) (65 + (temp % 26));
					if (++keyIndex == key.length)
						keyIndex = 0;
				} else if (t >= 97 && t <= 122) { //Encrypting Lower Case Characters
					temp = t - 97 + (key[keyIndex] - 97);
					if (temp < 0)
						temp += 26;
					if (temp < 0)
						temp += 26;

					systemPassword[i++] = (char) (97 + (temp % 26));
					if (++keyIndex == key.length)
						keyIndex = 0;
				} else { //Encrypting Special Characters
					specChars[ii++] = (char) (pos + 65);
					specChars[ii++] = t;
					specCharCount++;
				}
				pos++;
			}
			i = 0;
			finalPassword[i++] = (char) (specCharCount == 0 ? 65 : (--specCharCount + 65)); //Encrypting Amount of Special Characters in Password
			for (char t = specChars[0]; t != 0; i++, t = specChars[i - 1]) //Encrypting Special Characters & Positions of Special Characters
				finalPassword[i] = t;
			ii = i;
			for (char t = systemPassword[0]; t != 0; i++, t = systemPassword[i - ii]) //Encrypting Password
				finalPassword[i] = t;

			if (i > 32)
				Toast.makeText(null, "Password is greater than 32 characters", Toast.LENGTH_LONG).show();

			int length = 0;
			for (int x = 0; finalPassword[x] != '\0'; x++)
				length++;
			char[] cipherPassword = new char[length];
			for (int xi = 0; xi < length; xi++)
				cipherPassword[xi] = finalPassword[xi];

			//Shuffle Password
			LinkedList<Character> evens = new LinkedList<>();
			LinkedList<Character>odds = new LinkedList<>();
			for (int iii = 0; iii < cipherPassword.length; iii++)
				if((int)cipherPassword[iii] % 2 == 0)
					evens.addLast(cipherPassword[iii]);
				else
					odds.addFirst(cipherPassword[iii]);
			int iv = 0;
			while (!evens.isEmpty() || !odds.isEmpty()) {
				if (!odds.isEmpty()) {
					cipherPassword[iv++] = odds.getFirst();
					odds.removeFirst();
				}
				if(!evens.isEmpty()) {
					cipherPassword[iv++] = evens.getFirst();
					evens.removeFirst();
				}
			}

			//encrypt special chars further
			for (int v = 0; v < cipherPassword.length; v++)
				if ((int)cipherPassword[v] <= 47)
					cipherPassword[v] += 10;
				else if ((int)cipherPassword[v] > 47 && (int)cipherPassword[v] < 64)
					cipherPassword[v] -= 5;
				else if ((int)cipherPassword[v] > 90 && (int)cipherPassword[v] <= 96)
					if (cipherPassword.length % 2 == 0)
						cipherPassword[v] += 2;
					else
						cipherPassword[v] -= 2;

			//Replacing unloved characters
			for (int vi = 0; vi < cipherPassword.length; vi++)
				if ((int)cipherPassword[vi] == 34)
					cipherPassword[vi] = 123;
				else if ((int)cipherPassword[vi] == 38)
					cipherPassword[vi] = 124;
				else if ((int)cipherPassword[vi] == 60)
					cipherPassword[vi] = 125;
				else if ((int)cipherPassword[vi] == 62)
					cipherPassword[vi] = 126;

			//Limitations
			char[] cipherPasswordLimited = new char[limit];
			for (int vii = 0; vii < cipherPassword.length && vii < limit; vii++)
				cipherPasswordLimited[vii] = cipherPassword[vii];

			return cipherPasswordLimited;
		} catch (Exception ex)
		{
			Toast.makeText(null, "Failed to Encrypt Password", Toast.LENGTH_LONG).show();

			return null;
		}
	}

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_sign_in_main);
        // Set up the main form.
        pswPassword = (EditText) findViewById(R.id.pswPassword);
        pswPassword.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        pswPassword.setFocusable(true);
        pswPassword.requestFocus();
        pswKey = (EditText) findViewById(R.id.pswKey);
        pswKey.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        btnEncryptPassword = (Button) findViewById(R.id.btnEncryptPassword);
        btnEncryptPassword.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                encryptPassword();
            }
        });
        switchCompact = (Switch) findViewById(R.id.switch_Compact);
    }


	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	*/
	private void encryptPassword()
	{
		try
		{
			View focusView = null;
			boolean cancel = false;

			pswPassword.setError(null);
			pswKey.setError(null);

			if (pswPassword.getText().toString().length() == 0) {
				pswPassword.setError("Please Enter a Password");
				focusView = pswPassword;
				cancel = true;
			}

			password = pswPassword.getText().toString().toCharArray();

			if (pswKey.getText().toString().length() == 0) {
				pswKey.setError("Please Enter a Key");
				focusView = pswKey;
				cancel = true;
			}

			if (cancel) {
				focusView.requestFocus();
			} else {
				key = pswKey.getText().toString().toCharArray();
				if (switchCompact.isChecked())
					limit = 12;
				else
					limit = 32;
				finalPassword = encrypt(password, key, limit);
				if (finalPassword == null)
					throw new EncryptionException("Error Occurred During Encryption Process");
				Intent intent = new Intent(SecureSignInMain.this, OSD.class);
				startActivity(intent);
				Thread deletePassword = new Thread() {
					public void run()
					{
						try
						{
							Thread.sleep(10000);
							ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
							ClipData clip = ClipData.newPlainText(null, "");
							clipboard.setPrimaryClip(clip);
						}
						catch(InterruptedException v)
						{
							v.printStackTrace();
						}
					}
				};
				deletePassword.start();
			}
		}
		catch (Exception ex)
		{
			Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
}

