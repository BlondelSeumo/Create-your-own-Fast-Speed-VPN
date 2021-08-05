package fast.pi.vpn.activity;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import fast.pi.vpn.BuildConfig;
import fast.pi.vpn.R;

public class TermsToUseActivity extends AppCompatActivity {

    Toolbar toolbar;
    WebView termsToUse_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_to_use);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        termsToUse_view = findViewById(R.id.terms_to_use_view);
        //load Terms to use HTML document
        termsToUse_view.loadUrl(BuildConfig.TERMS_TO_USE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
