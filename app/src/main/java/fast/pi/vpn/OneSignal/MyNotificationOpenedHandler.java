package fast.pi.vpn.OneSignal;

import android.content.Intent;
import android.widget.Toast;

import fast.pi.vpn.MainApplication;
import fast.pi.vpn.activity.MainActivity;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String activityToBeOpened;
        if (data != null) {
            activityToBeOpened = data.optString("activityTobeOpened", null);
            if (activityToBeOpened.equals("MainActivty")) {
                Intent intent = new Intent(MainApplication.getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                MainApplication.getContext().startActivity(intent);
            } else {
                Intent intent = new Intent(MainApplication.getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                MainApplication.getContext().startActivity(intent);
            }
        }
        if (actionType == OSNotificationAction.ActionType.ActionTaken) {
            if (result.action.actionID.equals("ActionOne")) {
                Toast toast = Toast.makeText(MainApplication.getContext(), "ActionOne button was pressed", Toast.LENGTH_LONG);
                toast.show();
            } else if (result.action.actionID.equals("ActionTwo")) {
                Toast toast = Toast.makeText(MainApplication.getContext(), "ActionTwo button was pressed", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
