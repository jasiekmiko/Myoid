package eu.miko.myoid;

import android.view.accessibility.AccessibilityNodeInfo;

import com.android.internal.util.Predicate;

class predicateClickable implements Predicate<AccessibilityNodeInfo> {
    @Override
    public boolean apply(AccessibilityNodeInfo accessibilityNodeInfo) {
        boolean clickable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            clickable = accessibilityNodeInfo.getActionList().contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
        } else {
            clickable = (accessibilityNodeInfo.getActions() & AccessibilityNodeInfo.ACTION_CLICK) > 0;
        }
        return clickable;
    }
}
