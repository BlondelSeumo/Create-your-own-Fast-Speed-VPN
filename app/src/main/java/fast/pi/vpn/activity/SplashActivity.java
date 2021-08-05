package fast.pi.vpn.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fast.pi.vpn.BuildConfig;
import fast.pi.vpn.Preference;
import fast.pi.vpn.R;
import fast.pi.vpn.utils.NetworkState;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static fast.pi.vpn.utils.Constant.APP_IN_PURCHASE_KEY;
import static fast.pi.vpn.utils.Constant.MONTHLY_SUB;
import static fast.pi.vpn.utils.Constant.SIX_MONTHS_SUB;
import static fast.pi.vpn.utils.Constant.YEARLY_SUB;

public class SplashActivity extends AppCompatActivity {

    Preference preference;
    private static final int MY_IGNORE_OPTIMIZATION_REQUEST = 135;
    private int flag = 0;
    private static final int MY_AUTO_START_PERMISSION = 150;

    private static final Intent[] AUTO_START_INTENTS = {
            new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
            new Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT),
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity" : "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.FakeActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupmanager.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startupmanager.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startsettings")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupapp.startupmanager")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupmanager.startupActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.startupapp.startupmanager")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.privacypermissionsentry.PermissionTopActivity.Startupmanager")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.privacypermissionsentry.PermissionTopActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.FakeActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(Uri.parse("mobilemanager://function/entry/AutoStart")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.autostart.AutoStartActivity")),
            new Intent().setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.security.SHOW_APPSEC")).addCategory(Intent.CATEGORY_DEFAULT).putExtra("packageName", BuildConfig.APPLICATION_ID),
            new Intent().setComponent(new ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
            new Intent().setComponent(new ComponentName("com.dewav.dwappmanager", "com.dewav.dwappmanager.memory.SmartClearupWhiteList")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"))};

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView version = findViewById(R.id.Version);
        version.setText(getString(R.string.version) + packageInfo.versionName);

        preference = new Preference(SplashActivity.this);
        preference.setStringpreference(APP_IN_PURCHASE_KEY, BuildConfig.IN_APPKEY);
        preference.setStringpreference(MONTHLY_SUB, BuildConfig.MONTHLY);
        preference.setStringpreference(SIX_MONTHS_SUB, BuildConfig.SIX_MONTH);
        preference.setStringpreference(YEARLY_SUB, BuildConfig.YEARLY);
        CalltheBetterypermission();
    }

    private void callMainAppData() {
        if (NetworkState.isOnline(this)) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent myIntent = new Intent(getApplicationContext(), StepActivity.class);
                    startActivity(myIntent);
                    finish();
                }
            }, 3000);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.network_error))
                    .setMessage(getString(R.string.network_error_message))
                    .setNegativeButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    onBackPressed();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    // region Permission 29/03/2020
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void CalltheBetterypermission() {
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
        if (!isIgnoringBatteryOptimizations) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, MY_IGNORE_OPTIMIZATION_REQUEST);
        } else {
            callMainAppData();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_IGNORE_OPTIMIZATION_REQUEST) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
            if (isIgnoringBatteryOptimizations) {
                autostartPermission();
            } else {
                CalltheBetterypermission();
            }
        } else if (requestCode == MY_AUTO_START_PERMISSION) {
            callMainAppData();
        }
    }

    private void autostart(final Intent intent) {
        final AlertDialog autostartAlertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("Allow " + getString(R.string.app_name) + " to start Background Service");
        builder.setMessage(getString(R.string.app_name) + " requires to be enabled to run in Background properly");
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.Open_setting, null);
        autostartAlertDialog = builder.create();
        autostartAlertDialog.show();
        Button positive = autostartAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 31;
                startActivityForResult(intent, MY_AUTO_START_PERMISSION);
                autostartAlertDialog.dismiss();
            }
        });
    }

    private void autostartPermission() {
        for (final Intent intent : AUTO_START_INTENTS) {
            flag++;
            if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                autostart(intent);
                break;
            }
        }
        if (flag == 31) {
            callMainAppData();
        }
    }
    //endregion
}
