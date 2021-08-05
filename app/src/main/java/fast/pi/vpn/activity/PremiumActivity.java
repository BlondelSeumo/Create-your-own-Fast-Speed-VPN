package fast.pi.vpn.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import fast.pi.vpn.InAppPurchase.IabBroadcastReceiver;
import fast.pi.vpn.InAppPurchase.IabHelper;
import fast.pi.vpn.InAppPurchase.IabResult;
import fast.pi.vpn.InAppPurchase.Inventory;
import fast.pi.vpn.InAppPurchase.Purchase;
import fast.pi.vpn.Preference;
import fast.pi.vpn.R;

import java.util.ArrayList;
import java.util.List;

import static fast.pi.vpn.utils.Constant.APP_IN_PURCHASE_KEY;
import static fast.pi.vpn.utils.Constant.INAPPSKUUNIT;
import static fast.pi.vpn.utils.Constant.MONTHLY_SUB;
import static fast.pi.vpn.utils.Constant.MONTHLY_SUB_COST;
import static fast.pi.vpn.utils.Constant.PRIMIUM_STATE;
import static fast.pi.vpn.utils.Constant.PURCHASETIME;
import static fast.pi.vpn.utils.Constant.SIX_MONTHS_SUB;
import static fast.pi.vpn.utils.Constant.SIX_MONTHS_SUB_COST;
import static fast.pi.vpn.utils.Constant.YEARLY_SUB;
import static fast.pi.vpn.utils.Constant.YEARLY_SUB_COST;

public class PremiumActivity extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener {

    static final int RC_REQUEST = 10001;
    private static final String TAG = "PremiumActivity";
    public String SKU_DELAROY_MONTHLY;
    public String SKU_DELAROY_SIXMONTH;
    public String SKU_DELAROY_YEARLY;
    public String base64EncodedPublicKey;
    LinearLayout one_month, six_months, twelve_months;
    TextView one_month_sub_cost, six_months_sub_cost, one_year_sub_cost;
    IabHelper mHelper;
    IabBroadcastReceiver mBroadcastReceiver;
    boolean mSubscribedToDelaroy = false;
    String mDelaroySku = "";
    boolean mAutoRenewEnabled = false;
    String mSelectedSubscriptionPeriod = "";
    private Preference preference;

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                unlockdata();
                return;
            }

            // First find out which subscription is auto renewing
            Purchase delaroyMonthly = inventory.getPurchase(SKU_DELAROY_MONTHLY);
            Purchase delaroySixMonth = inventory.getPurchase(SKU_DELAROY_SIXMONTH);
            Purchase delaroyYearly = inventory.getPurchase(SKU_DELAROY_YEARLY);
            if (delaroyMonthly != null && delaroyMonthly.isAutoRenewing()) {
                mDelaroySku = SKU_DELAROY_MONTHLY;
                mAutoRenewEnabled = true;
            } else if (delaroySixMonth != null && delaroySixMonth.isAutoRenewing()) {
                mDelaroySku = SKU_DELAROY_SIXMONTH;
                mAutoRenewEnabled = true;
            } else if (delaroyYearly != null && delaroyYearly.isAutoRenewing()) {
                mDelaroySku = SKU_DELAROY_YEARLY;
                mAutoRenewEnabled = true;
            } else {
                mDelaroySku = "";
                mAutoRenewEnabled = false;
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            mSubscribedToDelaroy = (delaroyMonthly != null && verifyDeveloperPayload(delaroyMonthly))
                    || (delaroySixMonth != null && verifyDeveloperPayload(delaroySixMonth))
                    || (delaroyYearly != null && verifyDeveloperPayload(delaroyYearly));

            if (mDelaroySku != "") {
                preference.setStringpreference(INAPPSKUUNIT, mDelaroySku);
                preference.setLongpreference(PURCHASETIME, inventory.getPurchase(mDelaroySku).getPurchaseTime());
            }
            unlockdata();
        }
    };
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");

                return;
            }


            if (purchase.getSku().equals(SKU_DELAROY_MONTHLY)
                    || purchase.getSku().equals(SKU_DELAROY_SIXMONTH)
                    || purchase.getSku().equals(SKU_DELAROY_YEARLY)) {
                preference.setStringpreference(INAPPSKUUNIT, purchase.getSku());
                preference.setLongpreference(PURCHASETIME, purchase.getPurchaseTime());
                unlock();
                alert("Thank you for subscribing to Delaroy!");
                mSubscribedToDelaroy = true;
                mAutoRenewEnabled = purchase.isAutoRenewing();
                mDelaroySku = purchase.getSku();

            }
        }

    };
    private BillingProcessor bp;

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_premium);

        preference = new Preference(PremiumActivity.this);
        one_month = findViewById(R.id.one_month_layout);
        six_months = findViewById(R.id.six_months_layout);
        twelve_months = findViewById(R.id.twelve_months_layout);
        one_month_sub_cost = findViewById(R.id.one_month_sub_cost);
        six_months_sub_cost = findViewById(R.id.six_months_sub_cost);
        one_year_sub_cost = findViewById(R.id.one_year_sub_cost);

        SKU_DELAROY_MONTHLY = preference.getStringpreference(MONTHLY_SUB, SKU_DELAROY_MONTHLY);
        SKU_DELAROY_SIXMONTH = preference.getStringpreference(SIX_MONTHS_SUB, SKU_DELAROY_SIXMONTH);
        SKU_DELAROY_YEARLY = preference.getStringpreference(YEARLY_SUB, SKU_DELAROY_YEARLY);
        base64EncodedPublicKey = preference.getStringpreference(APP_IN_PURCHASE_KEY, base64EncodedPublicKey);

        bp = new BillingProcessor(this, base64EncodedPublicKey, null, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
//                showToast("onProductPurchased: " + productId);
            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
//                showToast("onBillingError: " + Integer.toString(errorCode));
            }

            @Override
            public void onBillingInitialized() {
//                showToast("onBillingInitialized");
                SkuDetails subs = bp.getSubscriptionListingDetails(SKU_DELAROY_MONTHLY);
                SkuDetails subs1 = bp.getSubscriptionListingDetails(SKU_DELAROY_SIXMONTH);
                SkuDetails subs2 = bp.getSubscriptionListingDetails(SKU_DELAROY_YEARLY);

                if (subs == null) {
                    showToast("Failed to load subscription details");
                } else {
                    preference.setStringpreference(MONTHLY_SUB_COST, subs.priceText);
                    preference.setStringpreference(SIX_MONTHS_SUB_COST, subs1.priceText);
                    preference.setStringpreference(YEARLY_SUB_COST, subs2.priceText);
                }
            }

            @Override
            public void onPurchaseHistoryRestored() {
//                showToast("onPurchaseHistoryRestored");
                for (String sku : bp.listOwnedProducts())
                    Log.d("LOG_TAG", "Owned Managed Product: " + sku);
                for (String sku : bp.listOwnedSubscriptions())
                    Log.d("LOG_TAG", "Owned Subscription: " + sku);
//                updateTextViews();
            }
        });
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {

                if (!result.isSuccess()) {
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                if (mHelper == null) return;

                mBroadcastReceiver = new IabBroadcastReceiver(PremiumActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });

        one_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mHelper.subscriptionsSupported()) {
                    complain("Subscriptions not supported on your device yet. Sorry!");
                    return;
                }
                mSelectedSubscriptionPeriod = SKU_DELAROY_MONTHLY;
                PurchaseFlow();
            }
        });
        six_months.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mHelper.subscriptionsSupported()) {
                    complain("Subscriptions not supported on your device yet. Sorry!");
                    return;
                }
                mSelectedSubscriptionPeriod = SKU_DELAROY_SIXMONTH;
                PurchaseFlow();
            }
        });
        twelve_months.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mHelper.subscriptionsSupported()) {
                    complain("Subscriptions not supported on your device yet. Sorry!");
                    return;
                }
                mSelectedSubscriptionPeriod = SKU_DELAROY_YEARLY;
                PurchaseFlow();
            }
        });
        one_month_sub_cost.setText(preference.getStringpreference(MONTHLY_SUB_COST));
        six_months_sub_cost.setText(preference.getStringpreference(SIX_MONTHS_SUB_COST));
        one_year_sub_cost.setText(preference.getStringpreference(YEARLY_SUB_COST));
    }

    private void PurchaseFlow() {
        String payload = "";
        List<String> oldSkus = null;
        if (!TextUtils.isEmpty(mDelaroySku)
                && !mDelaroySku.equals(mSelectedSubscriptionPeriod)) {
            // The user currently has a valid subscription, any purchase action is going to
            // replace that subscription
            oldSkus = new ArrayList<String>();
            oldSkus.add(mDelaroySku);
        }
        try {
            mHelper.launchPurchaseFlow(this, mSelectedSubscriptionPeriod, IabHelper.ITEM_TYPE_SUBS,
                    oldSkus, RC_REQUEST, mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
        }
    }

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }

    void complain(String message) {
        alert("Error: " + message);
    }

    void alert(String message) {
        android.app.AlertDialog.Builder bld = new android.app.AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }

    private void unlockdata() {
        if (mSubscribedToDelaroy) {
            unlock();
        } else {
            preference.setBooleanpreference(PRIMIUM_STATE, false);
        }
    }

    public void unlock() {
        preference.setBooleanpreference(PRIMIUM_STATE, true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mHelper == null) {
            return;
        }
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
        }
    }
}
