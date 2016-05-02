package eu.miko.myoid;

import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ModeFromStateMap {
    private final Mouse mouse;
    private final Tapped tapped;
    private final OptionsDoorway optionsDoorway;
    private final Options options;
    private final Media media;
    private final MediaVolume mediaVolume;
    private final Mode baseMode;
    private final OptionsDoorwayFromMedia optionsDoorwayFromMedia;

    @Inject
    public ModeFromStateMap(Mouse mouse, Tapped tapped, OptionsDoorway optionsDoorway, OptionsDoorwayFromMedia optionsDoorwayFromMedia, Options options, Media media, MediaVolume mediaVolume, Mode baseMode) {
        this.mouse = mouse;
        this.tapped = tapped;
        this.optionsDoorway = optionsDoorway;
        this.options = options;
        this.media = media;
        this.mediaVolume = mediaVolume;
        this.baseMode = baseMode;
        this.optionsDoorwayFromMedia = optionsDoorwayFromMedia;
    }

    public Mode get(State state) {
        switch (state) {
            case MOUSE:
                return mouse;
            case TAPPED:
                return tapped;
            case OPTIONS_DOORWAY_FROM_MOUSE:
                return optionsDoorway;
            case OPTIONS_FROM_MOUSE:
                return options;
            case OPTIONS_DOORWAY_FROM_MEDIA:
                return optionsDoorwayFromMedia;
            case MEDIA:
                return media;
            case OPTIONS_FROM_MEDIA:
                return options;
            case MEIDA_VOLUME:
                return mediaVolume;
            default:
                Log.w(ModeFromStateMap.class.getName(), "Unimplemented mode is used.");
                return baseMode;
        }
    }
}
