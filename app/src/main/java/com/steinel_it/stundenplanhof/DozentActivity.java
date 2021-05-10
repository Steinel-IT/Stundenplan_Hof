package com.steinel_it.stundenplanhof;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class DozentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dozent);
        getSupportActionBar().setTitle("Dozenteninformation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        //TODO: Content setzen
    }

    public void onClickFAB(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButtonDozentMail:
                System.out.println("Mail");
                String mail = "187@gmail.com";
                Intent intentMail = new Intent(Intent.ACTION_SENDTO);
                intentMail.setData(Uri.parse("mailto:"));
                intentMail.putExtra(Intent.EXTRA_EMAIL, new String[]{mail});
                try {
                    startActivity(Intent.createChooser(intentMail, "Wähle einen E-Mail Service:"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Kein E-Mail Service gefunden", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.floatingActionButtonDozentCall:
                System.out.println("Call");
                String phone = "+34666777888";
                Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                try {
                    startActivity(Intent.createChooser(intentCall, "Wähle einen Telefon Service:"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Kein Telefon Service gefunden", Toast.LENGTH_LONG).show();
                }
        }
    }
}