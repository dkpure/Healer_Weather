package com.sjxz.moji_weather.weather;

import android.content.Context;

import com.sjxz.moji_weather.R;

/**
 * Created by dkpure on 17-3-27.
 */

public class DrawerFactory {

    public static BaseDrawer makeDrawerByType(Context context, DrawerType type) {
        switch (type) {
            case RAIN_D:
                return new RainDayDrawer(context, R.drawable.bg_moderate_rain_day);
            case FOG_D:
                return new FogDayDrawer(context, R.drawable.bg_fog_and_haze);
            case SNOW_D:
                return new SnowDayDrawer(context, R.drawable.bg_snow_day);
            case CLOUDY_D:
                return new CloudyDayDrawer(context, R.drawable.bg_cloud);
            case OVERCAST:
                return new OvercastDayDrawer(context, R.mipmap.bg_overcast_day);
            case CLEAR_N:
                return new SunnyNightDrawer(context, R.drawable.bg_sunny_night);
            case SNOW_N:
                return new SnowNightDrawer(context, R.drawable.bg_snow_night);
            case CLOUDY_N:
                return new CloudyNightDrawer(context, R.drawable.bg_cloud_night);
            case OVERCAST_N:
                return new OvercastNightDrawer(context, R.drawable.bg_overcast_night);
            case CLEAR_D:
            case DEFAULT:
            default:
                return new SunnyDrawer(context, R.mipmap.bg_sunny_day);
        }
    }
}
