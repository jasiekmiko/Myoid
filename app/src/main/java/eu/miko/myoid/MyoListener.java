package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;

public class MyoListener extends AbstractDeviceListener {

    private final MyoidAccessibilityService mService;

    public MyoListener(MyoidAccessibilityService service) {
        mService = service;
    }

    @Override
    public void onConnect(Myo myo, long timestamp) {
        shortToast("Myo Connected");
    }

    @Override
    public void onDisconnect(Myo myo, long timestamp) {
        shortToast("Myo Disconnected");
    }

    @Override
    public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
        float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
        float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
        float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));
        // Adjust roll and pitch for the orientation of the Myo on the arm.
        if (myo.getXDirection() == XDirection.TOWARD_ELBOW) {
            roll *= -1;
            pitch *= -1;
        }

        mService.moveCursor((int)roll, (int)pitch);

    }

    @Override
    public void onPose(Myo myo, long timestamp, Pose pose) {
        switch (pose) {
            case FIST:
                //mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                doFistAction();
                break;
            case WAVE_IN:
                mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                break;
            case WAVE_OUT:
                //mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                doWaveOutAction();
                break;
            case FINGERS_SPREAD:
                mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
        }
        shortToast("Pose: " + pose);
    }

    private void doWaveOutAction() {
        AccessibilityNodeInfo root = mService.getRootInActiveWindow();
        int x = mService.cursorParams.x;
        int y = mService.cursorParams.y;
        AccessibilityNodeInfo pointedAt = findChildAt(root, x, y);
        if (pointedAt != null) {
            if(pointedAt.isClickable()) pointedAt.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        else shortToast("pointedAt null");
    }

    private AccessibilityNodeInfo findChildAt(AccessibilityNodeInfo nodeInfo, int x, int y) {
        if (nodeInfo == null) return null;
        Rect bounds = new Rect();
        nodeInfo.getBoundsInScreen(bounds);
        int childCount = nodeInfo.getChildCount();
        if (!bounds.contains(x,y)) return null;
        else if (childCount == 0) return nodeInfo;

        int childIndex = 0;
        while (childIndex < childCount) {
            AccessibilityNodeInfo result = findChildAt(nodeInfo.getChild(childIndex), x, y);
            if (result != null) return result;
            childIndex += 1;
        }
        return nodeInfo;

    }

    private void doFistAction() {
        AccessibilityNodeInfo root = mService.getRootInActiveWindow();
        AccessibilityNodeInfo scrollableView = findScrollableView(root);
        if (scrollableView != null) scrollableView.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        else shortToast("scrollableView null");
    }

    private AccessibilityNodeInfo findScrollableView(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) return null;
        if (nodeInfo.isScrollable()) return nodeInfo;
        else if (nodeInfo.getChildCount() > 0) {
            int childCount = nodeInfo.getChildCount();
            int childIndex = 0;
            AccessibilityNodeInfo result = null;
            while (result == null && childIndex < childCount) {
                result = findScrollableView(nodeInfo.getChild(childIndex));
                childIndex += 1;
            }
            return result;
        }
        else return null;
    }

    private void shortToast(String text) {
        Toast.makeText(mService, text, Toast.LENGTH_SHORT).show();
    }
}
