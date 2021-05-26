package com.steinel_it.stundenplanhof;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.steinel_it.stundenplanhof.data_manager.DozentParseDownloadManager;
import com.steinel_it.stundenplanhof.interfaces.HandleDozentTaskInterface;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class DozentActivity extends AppCompatActivity implements HandleDozentTaskInterface {

    private ArrayList<String> titelList;
    private ArrayList<String> contentList;
    private String dozent;
    private String phone;
    private String mail;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dozent);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.dozentInfo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState != null) {
            titelList = savedInstanceState.getStringArrayList("titelList");
            contentList = savedInstanceState.getStringArrayList("contentList");
            phone = savedInstanceState.getString("phone");
            mail = savedInstanceState.getString("mail");
            image = bitmapToByteArray(savedInstanceState.getByteArray("imageBytes"));
        } else {
            Bundle extras = getIntent().getExtras();
            dozent = extras.getString(MainActivity.EXTRA_MESSAGE_DOZENT);
        }
        setupContent();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList("titelList", titelList);
        savedInstanceState.putStringArrayList("contentList", contentList);
        savedInstanceState.putString("phone", phone);
        savedInstanceState.putString("mail", mail);
        savedInstanceState.putByteArray("imageBytes", getImageAsByteArray());
    }

    private byte[] getImageAsByteArray() {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        return outStream.toByteArray();
    }

    private Bitmap bitmapToByteArray(byte[] imageBytes) {
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    private void setupContent() {
        if (titelList == null || contentList == null) {
            DozentParseDownloadManager dozentParseDownloadManager = new DozentParseDownloadManager(this);
            dozentParseDownloadManager.getDozent(dozent);
        } else if (titelList.isEmpty() || contentList.isEmpty()) {
            Group groupNothingToSee = findViewById(R.id.groupDozentNothingToSee);
            Group loadingGroup = findViewById(R.id.groupLoadingDozent);
            loadingGroup.setVisibility(View.GONE);
            groupNothingToSee.setVisibility(View.VISIBLE);
        } else {
            TextView textViewName = findViewById(R.id.textViewDozentName);
            TextView textViewTopContent = findViewById(R.id.textViewDozentTopContent);
            textViewName.setText(titelList.get(0));
            textViewTopContent.setText(contentList.get(0));
            ImageView imageViewDozent = findViewById(R.id.imageViewDozent);
            if (image != null) {
                imageViewDozent.setImageBitmap(image);
            } else {
                imageViewDozent.setVisibility(View.GONE);
            }
            LinearLayout linearLayoutDozentContent = findViewById(R.id.linearLayoutDozentContent);
            for (int i = 1; i < titelList.size(); i++) {
                TextView textViewTitle = new TextView(this);
                textViewTitle.setText(titelList.get(i));
                textViewTitle.setTextSize(20);
                textViewTitle.setTypeface(null, Typeface.BOLD);
                TextView textViewText = new TextView(this);
                if (i == 1) {
                    textViewText.setText(contentList.get(i));
                } else { //Manueller Abstand nach 1, weil Website nicht konstant ist
                    textViewText.setText(String.format("%s%s",contentList.get(i),System.lineSeparator()));
                }
                textViewText.setTextSize(16);
                linearLayoutDozentContent.addView(textViewTitle);
                linearLayoutDozentContent.addView(textViewText);
            }
            //Set VISIBLE to Main Content and set GONE to Loading Screen
            ScrollView scrollView = findViewById(R.id.scrollViewDozent);
            Group loadingGroup = findViewById(R.id.groupLoadingDozent);
            Group fabDozent = findViewById(R.id.groupFABDozent);
            scrollView.setVisibility(View.VISIBLE);
            loadingGroup.setVisibility(View.GONE);
            fabDozent.setVisibility(View.VISIBLE);
        }
    }

    public void onClickFAB(View view) {
        int id = view.getId();
        if (id == R.id.floatingActionButtonDozentMail) {
            if (mail != null) {
                Intent intentMail = new Intent(Intent.ACTION_SENDTO);
                intentMail.setData(Uri.parse("mailto:"));
                intentMail.putExtra(Intent.EXTRA_EMAIL, new String[]{mail});
                try {
                    startActivity(Intent.createChooser(intentMail, getString(R.string.chooseEMailService)));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, getString(R.string.noEMailServiceFound), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.EMailNotFound), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.floatingActionButtonDozentCall) {
            if (phone != null) {
                Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                try {
                    startActivity(Intent.createChooser(intentCall, getString(R.string.choosePhoneService)));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, getString(R.string.noPhoneServiceFound), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.PhoneNotFound), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onTaskFinished(ArrayList<String> titel, ArrayList<String> contentList, Bitmap image) {
        this.titelList = titel;
        this.contentList = contentList;
        if (!contentList.isEmpty()) {
            this.phone = contentList.get(0).substring(contentList.get(0).indexOf("Fon: ") + 4, contentList.get(0).indexOf("Fax: ")).replace("(0) ", "").replace(" / ", "");
            this.mail = contentList.get(0).substring(contentList.get(0).indexOf("E-Mail: ") + 7);
            this.image = image;
        }
        setupContent();
    }
}