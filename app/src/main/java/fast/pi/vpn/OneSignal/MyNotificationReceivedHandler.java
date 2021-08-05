package fast.pi.vpn.OneSignal;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class MyNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
    @Override
    public void notificationReceived(OSNotification notification) {
        JSONObject data = notification.payload.additionalData;
        String customKey;
        if (data != null) {
            customKey = data.optString("customKey", null);
        }
    }
}
