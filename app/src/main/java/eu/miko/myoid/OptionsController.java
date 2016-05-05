package eu.miko.myoid;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class OptionsController {
    private static final int THRESHOLD = 50;
    private final WindowManager windowManager;
    private final Point screenSize = new Point();
    private View optionsWindow;
    private ImageView pointer;
    private IconSet currentSet = IconSet.MAIN;
    private double circleRadius;
    private Point circleCenter;
    WindowManager.LayoutParams optionsLayoutParams;
    private WindowManager.LayoutParams pointerParams;
    Boolean graphicsInitialized = false;
    private final MyoidAccessibilityService mas;
    private int iconRadius;
    private int pointerSize = 69;

    @Inject
    public OptionsController(WindowManager windowManager, MyoidAccessibilityService mas) {
        this.windowManager = windowManager;
        this.mas = mas;
        windowManager.getDefaultDisplay().getSize(screenSize);
        calculateUiSizes();
    }

    private void calculateUiSizes() {
        iconRadius = (int) (min(screenSize.x, screenSize.y) * 1.0/12);
        circleRadius = min(screenSize.x, screenSize.y) * 1.0/3;
        circleCenter = new Point(screenSize.x/2, screenSize.y/2);
    }

    public void displayOptions() {
        if (!graphicsInitialized) {
            initializeGraphics();
            graphicsInitialized = true;
        }
        optionsWindow.setVisibility(View.VISIBLE);
        showIconSet(IconSet.MAIN);
        pointer.setVisibility(View.VISIBLE);
    }

    private void initializeGraphics() {
        initOptionsWindow();
        initIcons();
        initPointer();
    }

    public void dismissOptions() {
        optionsWindow.setVisibility(View.GONE);
        hideCurrentSet();
        hidePointer();
    }

    private void hidePointer() {
        pointer.setVisibility(View.GONE);
    }

    void initOptionsWindow() {
        optionsWindow = new OptionsWindow(mas, circleRadius + iconRadius);
        optionsWindow.setVisibility(View.GONE);
        optionsWindow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismissOptions();
                    return true;
                }
                return false;
            }
        });
        optionsLayoutParams = new WindowManager.LayoutParams(
                screenSize.x, screenSize.y,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        optionsLayoutParams.gravity = Gravity.TOP | Gravity.START;
        windowManager.addView(optionsWindow, optionsLayoutParams);
    }

    private void initPointer() {
        pointer = new ImageView(mas);
        pointer.setImageResource(R.drawable.cursor_pan);
        pointerParams = new WindowManager.LayoutParams(
            pointerSize,
            pointerSize,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
        windowManager.addView(pointer, pointerParams);
        pointerParams.gravity = Gravity.TOP | Gravity.LEFT;
        resetPointerToCenter();
    }

    void resetPointerToCenter() {
        pointerParams.x = circleCenter.x;
        pointerParams.y = circleCenter.y;
        windowManager.updateViewLayout(pointer, pointerParams);
    }

    public Icon movePointerByAndChooseIconIfHit(int x, int y) {
        if (graphicsInitialized) {
            int newX = pointerParams.x + x;
            int newY = pointerParams.y + y;
            if (!exitsBoundary(newX, newY)){
                pointerParams.x = newX;
                pointerParams.y = newY;
                windowManager.updateViewLayout(pointer, pointerParams);
            }
            return checkIfAndReturnHitIcon();
        }
        return null;
    }

    private boolean exitsBoundary(int newX, int newY) {
        Point newPos = getPointerCenter(newX, newY);
        double dist = distance(newPos, circleCenter);
        return dist > circleRadius*1.1;
    }

    private Point getPointerCenter() {
        return getPointerCenter(pointerParams.x, pointerParams.y);
    }

    @NonNull
    private Point getPointerCenter(int newX, int newY) {
        int pointerRadius = pointerSize/2;
        return new Point(newX + pointerRadius, newY + pointerRadius);
    }

    void showIconSet(IconSet iconSet) {
        resetPointerToCenter();
        hideCurrentSet();
        currentSet = iconSet;
        showCurrentSet();
    }

    private void showCurrentSet() {
        for (Map.Entry entry: currentSet.getEntries()) {
            Icon icon = (Icon) entry.getKey();
            ImageView view = (ImageView) entry.getValue();
            view.setImageDrawable(getIconImage(icon));
            view.setVisibility(View.VISIBLE);
        }
    }

    private void hideCurrentSet() {
        for (View view : currentSet.getViews()) {
            view.setVisibility(View.GONE);
        }
    }

    private Icon checkIfAndReturnHitIcon() {
        Point myPos = getPointerCenter();
        for (Icon icon : currentSet.getIcons()) {
            View view = currentSet.getView(icon);
            Point iconPos = pointFromView(view);
            double dist = distance(myPos, iconPos);
            if (dist < THRESHOLD) return icon;
        }
        return null;
    }

    @NonNull
    private Point pointFromView(View view) {
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
        int viewRadius = min(lp.width, lp.height)/2;
        return new Point(lp.x + viewRadius, lp.y + viewRadius);
    }

    private double distance(Point p1, Point p2) {
        return sqrt(pow(p2.x - p1.x, 2) + pow(p2.y - p1.y, 2));
    }

    void initIcons() {
        int i = 0;
        for (MainIcon icon : MainIcon.values()) {
            ImageView iconView = initializeIconInCircle(icon, i, MainIcon.values().length);
            IconSet.MAIN.addView(icon, iconView);
            i++;
        }
        i = 0;
        for (NavIcon icon : NavIcon.values()) {
            ImageView iconView = initializeIconInCircle(icon, i+1, 4);
            IconSet.NAV.addView(icon, iconView);
            i++;
        }
        i = 0;
        for (QsIcon icon : QsIcon.values()) {
            ImageView iconView = initializeIconInCircle(icon, i, QsIcon.values().length);
            IconSet.QS.addView(icon, iconView);
            i++;
        }
    }

    @NonNull
    private ImageView initializeIconInCircle(Icon icon, int i, int nIcons) {
        ImageView iconView = createIconImageView(icon);
        WindowManager.LayoutParams params = createIconLayoutParams(i, nIcons);
        windowManager.addView(iconView, params);
        return iconView;
    }

    @NonNull
    private WindowManager.LayoutParams createIconLayoutParams(int i, int nIcons) {
        Rect pos = calculateIconPositions(i, nIcons);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                iconRadius*2, iconRadius*2, //size
                pos.left, pos.top,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.LEFT;
        return params;
    }

    @NonNull
    private ImageView createIconImageView(Icon icon) {
        ImageView iconView = new ImageView(mas);
        Drawable iconImage = getIconImage(icon);
        iconView.setImageDrawable(iconImage);
        iconView.setVisibility(View.GONE);
        return iconView;
    }

    private Drawable getIconImage(Icon icon) {
        Drawable iconImage;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iconImage = mas.getResources().getDrawable(icon.getIconImage(), null);
            assert iconImage != null;
            iconImage.setTint(Color.WHITE);
        } else {
            //noinspection deprecation
            iconImage = mas.getResources().getDrawable(icon.getIconImage());
        }
        return iconImage;
    }

    private Rect calculateIconPositions(int index, int nIcons) {
        double angle = (2.0/nIcons)*index;
        double dx = circleRadius*sin(angle * PI);
        double dy = -circleRadius*cos(angle * PI);
        Point center = new Point(circleCenter);
        center.offset((int) dx, (int) dy);

        int left = center.x - iconRadius;
        int top = center.y - iconRadius;
        int right = center.x + iconRadius;
        int bottom = center.y + iconRadius;

        return new Rect(left, top, right, bottom);
    }

    public boolean goBack() {
        if (currentSet == IconSet.MAIN) return true;
        showIconSet(IconSet.MAIN);
        return false;
    }

    public void changePointerImage(int resource) {
        pointer.setImageResource(resource);
    }

    public void resetScreen() {
        resetPointerToCenter();
        showCurrentSet();
    }

    enum IconSet {
        MAIN,
        NAV,
        QS;

        private HashMap<Icon, View> views;

        IconSet() {
            views = new HashMap<>();
        }

        public Collection<View> getViews() {
            return views.values();
        }

        public Set<Icon> getIcons() {
            return views.keySet();
        }

        public void addView(Icon icon, View view) {
            views.put(icon, view);
        }

        public View getView(Icon icon) {
            return views.get(icon);
        }

        public Set<Map.Entry<Icon, View>> getEntries() {
            return views.entrySet();
        }
    }

    interface Icon {
        int getIconImage();
    }

    enum MainIcon implements Icon {
        SEARCH(R.drawable.ic_search_24dp),
        QS(R.drawable.ic_settings_24dp),
        NAV(R.drawable.ic_navigation_24dp),
        MEDIA_MOUSE {
            @Override
            public int getIconImage() {
                return Options.mouseOrMedia == State.MOUSE ? R.drawable.ic_headset_24dp : R.drawable.ic_mouse_24dp;
            }
        };

        private int iconImage;

        MainIcon(int iconImage) {
            this.iconImage = iconImage;
        }

        MainIcon() {

        }

        public int getIconImage() {
                return iconImage;
            }
        }


    enum NavIcon implements Icon {
        RECENT(R.drawable.ic_nav_recent_24dp),
        HOME(R.drawable.ic_nav_home_24dp),
        BACK(R.drawable.ic_nav_back_24dp);

        private int iconImage;

        NavIcon(int iconImage) {
            this.iconImage = iconImage;
        }

        public int getIconImage() {
            return iconImage;
        }
    }

    enum QsIcon implements Icon {
        WIFI {
            @Override
            public int getIconImage() {
                try {
                    int wifiOn = Settings.Global.getInt(getContentResolver(), Settings.Global.WIFI_ON);
                    return wifiOn != 0 ? R.drawable.ic_signal_wifi_4_bar_24dp : R.drawable.ic_signal_wifi_off_24dp;
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                return R.mipmap.ic_launcher;
            }
        },
        TORCH {
            @Override
            public int getIconImage() {
                    return Options.torchOn ? R.drawable.ic_torch_on_24dp : R.drawable.ic_torch_off_24dp;
            }
        },
        RINGER {
            @Override
            public int getIconImage() {
                AudioManager audioManager = (AudioManager) MyoidAccessibilityService.getMyoidService().getSystemService(Context.AUDIO_SERVICE);
                switch (audioManager.getRingerMode()) {
                    case AudioManager.RINGER_MODE_NORMAL:
                        return R.drawable.ic_notifications_black_24dp;
                    case AudioManager.RINGER_MODE_SILENT:
                        return R.drawable.ic_notifications_off_24dp;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        return R.drawable.ic_vibration_24dp;
                    default:
                        return R.mipmap.ic_launcher;
                }
            }
        },
        ORIENTATION {
            @Override
            public int getIconImage() {
                boolean rotationLocked = true;
                try {
                    int rotationLockedSetting = Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
                    rotationLocked = rotationLockedSetting == 0;
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                return rotationLocked ? R.drawable.ic_screen_lock_portrait_24dp : R.drawable.ic_screen_rotation_24dp;
            }
        };

        private static ContentResolver getContentResolver() {
            Context mas = MyoidAccessibilityService.getMyoidService();
            return mas.getContentResolver();
        }
    }
}