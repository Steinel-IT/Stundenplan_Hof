package com.steinel_it.stundenplanhof;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.steinel_it.stundenplanhof.data_manager.DozentParseDownloadManager;
import com.steinel_it.stundenplanhof.interfaces.HandleDozentTaskInterface;

import java.util.ArrayList;

public class DozentActivity extends AppCompatActivity implements HandleDozentTaskInterface {

    DozentParseDownloadManager dozentParseDownloadManager;

    ArrayList<String> titelList;
    ArrayList<String> contentList;
    String dozent;
    String phone;
    String mail;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dozent);
        getSupportActionBar().setTitle("Dozenteninformation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        dozent = extras.getString(MainActivity.EXTRA_MESSAGE_DOZENT);
        setupContent();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupContent() {
        if (titelList == null || contentList == null) {
            dozentParseDownloadManager = new DozentParseDownloadManager(this);
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
                if(i == 1) {
                    textViewText.setText(contentList.get(i));
                } else { //Manueller Abstand nach 1, weil Website nicht konstant ist
                    StringBuilder sb = new StringBuilder(contentList.get(i));
                    sb.append(System.lineSeparator());
                    textViewText.setText(sb.toString());
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
                    startActivity(Intent.createChooser(intentMail, "Wähle einen E-Mail Service:"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Kein E-Mail Service gefunden", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "E-Mail Adresse nicht vorhanden", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.floatingActionButtonDozentCall) {
            if (phone != null) {
                Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                try {
                    startActivity(Intent.createChooser(intentCall, "Wähle einen Telefon Service:"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Kein Telefon Service gefunden", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Telefon Nummer nicht vorhanden", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onTaskFinished(ArrayList<String> titel, ArrayList<String> result, Bitmap image) {
        this.titelList = titel;
        this.contentList = result;
        if (!result.isEmpty()) {
            this.phone = result.get(0).substring(result.get(0).indexOf("Fon: ") + 4, result.get(0).indexOf("Fax: ")).replace("(0) ", "").replace(" / ", "");
            this.mail = result.get(0).substring(result.get(0).indexOf("E-Mail: ") + 7);
            this.image = image;
        }
        setupContent();
    }
}