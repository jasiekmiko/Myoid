package eu.miko.myoid;

import android.view.WindowManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
    injects = {
        MyoidAccessibilityService.class,
        StatusActivity.class
    }
)
public class DaggerModule {
    private final MyoidAccessibilityService mas;
    private final WindowManager windowManager;

    public DaggerModule(MyoidAccessibilityService mas, WindowManager windowManager) {
        this.mas = mas;
        this.windowManager = windowManager;
    }

    @Provides @Singleton WindowManager provideWindowManager() { return windowManager;}
    @Provides MyoidAccessibilityService provideMAS() { return mas; }
    @Provides @Singleton IPerformer providePerformer(Performer performer) {return performer;}
    @Provides @Singleton IMyoHubManager provideIMyoHubManager(MyoHubManager mhm) {return mhm;}
}
