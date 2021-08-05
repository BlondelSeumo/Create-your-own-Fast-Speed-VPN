package fast.pi.vpn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anchorfree.partner.api.response.AvailableCountries;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import fast.pi.vpn.R;
import fast.pi.vpn.adapter.RegionListAdapter;
import fast.pi.vpn.dialog.CountryData;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;

import static fast.pi.vpn.utils.Constant.BUNDLE;
import static fast.pi.vpn.utils.Constant.COUNTRY_DATA;

public class ChooseServerActivity extends AppCompatActivity {

    @BindView(R.id.regions_recycler_view)
    RecyclerView regionsRecyclerView;

    @BindView(R.id.regions_progress)
    ProgressBar regionsProgressBar;

    Toolbar toolbar;
    private RegionListAdapter regionAdapter;
    private RegionChooserInterface regionChooserInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_server);
        ButterKnife.bind(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        regionChooserInterface = new RegionChooserInterface() {
            @Override
            public void onRegionSelected(CountryData item) {
                if (!item.isPro()) {
                    Intent intent = new Intent();
                    Bundle args = new Bundle();
                    Gson gson = new Gson();
                    String json = gson.toJson(item);

                    args.putString(COUNTRY_DATA, json);
                    intent.putExtra(BUNDLE, args);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Intent intent = new Intent(ChooseServerActivity.this, PremiumActivity.class);
                    startActivity(intent);
                }
            }
        };

        regionsRecyclerView.setHasFixedSize(true);
        regionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        regionAdapter = new RegionListAdapter(new RegionListAdapter.RegionListAdapterInterface() {
            @Override
            public void onCountrySelected(CountryData item) {
                regionChooserInterface.onRegionSelected(item);
            }
        }, ChooseServerActivity.this);
        regionsRecyclerView.setAdapter(regionAdapter);
        loadServers();
    }

    private void loadServers() {
        showProgress();
        UnifiedSDK.getInstance().getBackend().countries(new Callback<AvailableCountries>() {
            @Override
            public void success(@NonNull final AvailableCountries countries) {
                hideProress();
                regionAdapter.setRegions(countries.getCountries());
            }

            @Override
            public void failure(@NonNull VpnException e) {
                hideProress();
            }
        });
    }

    private void showProgress() {
        regionsProgressBar.setVisibility(View.VISIBLE);
        regionsRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void hideProress() {
        regionsProgressBar.setVisibility(View.GONE);
        regionsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public interface RegionChooserInterface {
        void onRegionSelected(CountryData item);
    }
}
