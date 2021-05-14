package com.steinel_it.stundenplanhof;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.steinel_it.stundenplanhof.data_manager.DozentParseDownloadManager;
import com.steinel_it.stundenplanhof.interfaces.HandleDozentTaskInterface;

import java.util.ArrayList;

public class DozentActivity extends AppCompatActivity implements HandleDozentTaskInterface {

    DozentParseDownloadManager dozentParseDownloadManager;

    ArrayList<String> titelList = new ArrayList<>();
    ArrayList<String> contentList = new ArrayList<>();
    String dozent;
    String phone;
    String mail;

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
        dozentParseDownloadManager = new DozentParseDownloadManager(this);
        dozentParseDownloadManager.getDozent(dozent);
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
    public void onTaskFinished( ArrayList<String> titel, ArrayList<String> result, String image) {
        this.titelList = titel;
        this.contentList = result;
        ScrollView scrollView = findViewById(R.id.scrollViewDozent);
        Group loadingGroup = findViewById(R.id.groupLoadingDozent);
        Group fabDozent = findViewById(R.id.groupFABDozent);
        scrollView.setVisibility(View.VISIBLE);
        loadingGroup.setVisibility(View.GONE);
        fabDozent.setVisibility(View.VISIBLE);
    }
}