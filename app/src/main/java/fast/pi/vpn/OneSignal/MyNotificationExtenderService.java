package fast.pi.vpn.OneSignal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;

import fast.pi.vpn.MainApplication;
import fast.pi.vpn.R;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;

import java.math.BigInteger;

public class MyNotificationExtenderService extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        OverrideSettings overrideSettings = new OverrideSettings();
        overrideSettings.extender = new NotificationCompat.Extender() {
            @Override
            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
                Bitmap icon = BitmapFactory.decodeResource(MainApplication.getContext().getResources(),R.mipmap.ic_launcher);
                builder.setLargeIcon(icon);
                return builder.setColor(new BigInteger("FF0000FF", 16).intValue());
            }
        };
        OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
        return true;
    }
}
