package eu.miko.myoid;

public interface IconBehaviour {

    class Wifi implements IconBehaviour {
        private int onIcon = R.drawable.ic_signal_wifi_4_bar_24dp;
        private int offIcon = R.drawable.ic_signal_wifi_off_24dp;
    }

    class Torch implements IconBehaviour {
    }

    class Mute implements IconBehaviour {
    }

    class Gps implements IconBehaviour {
    }
}

