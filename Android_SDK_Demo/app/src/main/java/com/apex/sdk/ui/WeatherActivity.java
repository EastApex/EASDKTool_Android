package com.apex.sdk.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.callback.WeatherCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleWeather;
import com.apex.sdk.R;
import com.apex.sdk.dialog.WaitingDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WeatherActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.weather)
    RecyclerView weatherView;
    @BindView(R.id.submit)
    AppCompatButton submitButton;
    private EABleWeather eaBleWeather;
    private AdapterWeather adapterWeather;
    @BindView(R.id.temperature_unit)
    TextView unitText;
    @BindView(R.id.current_temperature)
    TextView currentText;
    @BindView(R.id.place)
    TextView placeText;
    Calendar calendar;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x40) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                eaBleWeather = (EABleWeather) msg.obj;
                EABleWeather.TemperatureUnit temperatureUnit = eaBleWeather.getTemperatureUnit();
                if (temperatureUnit != null) {
                    if (temperatureUnit == EABleWeather.TemperatureUnit.centigrade) {
                        unitText.setText(getString(R.string.temperature_unit_centigrade));
                    } else {
                        unitText.setText(getString(R.string.temperature_unit_fahrenheit));
                    }
                }
                currentText.setText(eaBleWeather.getCurrent_temperature() + "");
                placeText.setText(eaBleWeather.getPlace());
                if (eaBleWeather.getS_day() == null || eaBleWeather.getS_day().isEmpty()) {
                    Toast.makeText(WeatherActivity.this, getString(R.string.no_weather), Toast.LENGTH_SHORT).show();
                } else {
                    adapterWeather.notifyDataSetChanged();
                }

            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(WeatherActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                EABleWeather.TemperatureUnit temperatureUnit = eaBleWeather.getTemperatureUnit();
                if (temperatureUnit != null) {
                    if (temperatureUnit == EABleWeather.TemperatureUnit.centigrade) {
                        unitText.setText(getString(R.string.temperature_unit_centigrade));
                    } else {
                        unitText.setText(getString(R.string.temperature_unit_fahrenheit));
                    }
                }
                currentText.setText(eaBleWeather.getCurrent_temperature() + "");
                placeText.setText(eaBleWeather.getPlace());
                adapterWeather.notifyDataSetChanged();
            } else if (msg.what == 43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(WeatherActivity.this, getString(R.string.add_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        unbinder = ButterKnife.bind(this);
        toolbar.setNavigationIcon(R.mipmap.exit_page);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WeatherActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        weatherView.setLayoutManager(linearLayoutManager);
        adapterWeather = new AdapterWeather();
        weatherView.setAdapter(adapterWeather);
        EABleConnectState state = EABleManager.getInstance().getDeviceConnectState();
        if (state == EABleConnectState.STATE_CONNECTED) {
            if (waitingDialog == null) {
                waitingDialog = new WaitingDialog(WeatherActivity.this);
            }
            waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.weather, new WeatherCallback() {
                @Override
                public void weatherInfo(EABleWeather eaBleWeather) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        message.what = 0x40;
                        message.obj = eaBleWeather;
                        mHandler.sendMessage(message);
                    }
                }

                @Override
                public void mutualFail(int errorCode) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x41);
                    }
                }
            });
        }
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    if (eaBleWeather == null) {
                        eaBleWeather = new EABleWeather();
                        eaBleWeather.setPlace("jiaxiang");
                        eaBleWeather.setTemperatureUnit(EABleWeather.TemperatureUnit.centigrade);
                        eaBleWeather.setCurrent_temperature(20);
                        List<EABleWeather.EABleWeatherItem> weatherItemList = new ArrayList<>();
                        eaBleWeather.setS_day(weatherItemList);
                    }
                    List<EABleWeather.EABleWeatherItem> weatherItemList = eaBleWeather.getS_day();
                    eaBleWeather.setPlace("jiaxiang");
                    eaBleWeather.setCurrent_temperature(20);
                    if (weatherItemList == null) {
                        weatherItemList = new ArrayList<>();
                        eaBleWeather.setS_day(weatherItemList);
                    }
                    EABleWeather.EABleWeatherItem eaBleWeatherItem = new EABleWeather.EABleWeatherItem();
                    eaBleWeatherItem.setAir_humidity(80);
                    eaBleWeatherItem.setCloudiness(20);
                    eaBleWeatherItem.setE_air(EABleWeather.AirQuality.good);
                    eaBleWeatherItem.setE_day_type(EABleWeather.WeatherType.clear);
                    eaBleWeatherItem.setE_moon(EABleWeather.Moon.new_moon);
                    eaBleWeatherItem.setE_night_type(EABleWeather.WeatherType.clear);
                    eaBleWeatherItem.setE_rays(EABleWeather.RaysLevel.medium);
                    eaBleWeatherItem.setMax_temperature(26);
                    eaBleWeatherItem.setMin_temperature(15);
                    eaBleWeatherItem.setMax_wind_power(3);
                    eaBleWeatherItem.setMin_wind_power(1);
                    eaBleWeatherItem.setSunrise_timestamp(1646431200L);
                    eaBleWeatherItem.setSunset_timestamp(1646476200L);
                    weatherItemList.add(eaBleWeatherItem);
                    if (waitingDialog == null) {
                        waitingDialog = new WaitingDialog(WeatherActivity.this);
                    }
                    if (!waitingDialog.isShowing()) {
                        waitingDialog.show();
                    }
                    EABleManager.getInstance().setWeather(eaBleWeather, new GeneralCallback() {
                        @Override
                        public void result(boolean success,int reason) {
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x42);
                            }
                        }

                        @Override
                        public void mutualFail(int errorCode) {
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x43);
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (waitingDialog != null) {
            waitingDialog.dismiss();
            waitingDialog = null;
        }
        adapterWeather = null;

        super.onDestroy();
    }

    class AdapterWeather extends RecyclerView.Adapter<AdapterWeather.HoldView> {
        @NonNull
        @Override
        public HoldView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.adapter_weather_recyclerview, parent, false);
            return new HoldView(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull HoldView holder, int position) {
            if (eaBleWeather == null || eaBleWeather.getS_day() == null || eaBleWeather.getS_day().isEmpty()) {
                return;
            }
            EABleWeather.EABleWeatherItem weatherItem = eaBleWeather.getS_day().get(position);
            EABleWeather.WeatherType dayType = weatherItem.getE_day_type();
            if (dayType != null) {
                if (dayType == EABleWeather.WeatherType.clear) {
                    holder.dayText.setText(getString(R.string.weather_type_clear));

                } else if (dayType == EABleWeather.WeatherType.cloudy) {
                    holder.dayText.setText(getString(R.string.weather_type_cloudy));
                } else if (dayType == EABleWeather.WeatherType.gloomy) {
                    holder.dayText.setText(getString(R.string.weather_type_gloomy));
                } else if (dayType == EABleWeather.WeatherType.drizzle) {
                    holder.dayText.setText(getString(R.string.weather_type_drizzle));
                } else if (dayType == EABleWeather.WeatherType.moderate_rain) {
                    holder.dayText.setText(getString(R.string.weather_type_moderate_rain));
                } else if (dayType == EABleWeather.WeatherType.thunderstorm) {
                    holder.dayText.setText(getString(R.string.weather_type_thunderstorm));
                } else if (dayType == EABleWeather.WeatherType.heavy_rain) {
                    holder.dayText.setText(getString(R.string.weather_type_heavy_rain));
                } else if (dayType == EABleWeather.WeatherType.sleet) {
                    holder.dayText.setText(getString(R.string.weather_type_heavy_sleet));
                } else if (dayType == EABleWeather.WeatherType.light_snow) {
                    holder.dayText.setText(getString(R.string.weather_type_light_snow));
                } else if (dayType == EABleWeather.WeatherType.moderate_snow) {
                    holder.dayText.setText(getString(R.string.weather_type_moderate_snow));
                } else if (dayType == EABleWeather.WeatherType.heavy_snow) {
                    holder.dayText.setText(getString(R.string.weather_type_heavy_snow));
                }
            }
            EABleWeather.WeatherType nightType = weatherItem.getE_night_type();
            if (nightType != null) {
                if (nightType == EABleWeather.WeatherType.clear) {
                    holder.nightText.setText(getString(R.string.weather_type_clear));
                } else if (nightType == EABleWeather.WeatherType.cloudy) {
                    holder.nightText.setText(getString(R.string.weather_type_cloudy));
                } else if (nightType == EABleWeather.WeatherType.gloomy) {
                    holder.nightText.setText(getString(R.string.weather_type_gloomy));
                } else if (nightType == EABleWeather.WeatherType.drizzle) {
                    holder.nightText.setText(getString(R.string.weather_type_drizzle));
                } else if (nightType == EABleWeather.WeatherType.moderate_rain) {
                    holder.nightText.setText(getString(R.string.weather_type_moderate_rain));
                } else if (nightType == EABleWeather.WeatherType.thunderstorm) {
                    holder.nightText.setText(getString(R.string.weather_type_thunderstorm));
                } else if (nightType == EABleWeather.WeatherType.heavy_rain) {
                    holder.nightText.setText(getString(R.string.weather_type_heavy_rain));
                } else if (nightType == EABleWeather.WeatherType.sleet) {
                    holder.nightText.setText(getString(R.string.weather_type_heavy_sleet));
                } else if (nightType == EABleWeather.WeatherType.light_snow) {
                    holder.nightText.setText(getString(R.string.weather_type_light_snow));
                } else if (nightType == EABleWeather.WeatherType.moderate_snow) {
                    holder.nightText.setText(getString(R.string.weather_type_moderate_snow));
                } else if (nightType == EABleWeather.WeatherType.heavy_snow) {
                    holder.nightText.setText(getString(R.string.weather_type_heavy_snow));
                }
            }
            EABleWeather.AirQuality airQuality = weatherItem.getE_air();
            if (airQuality != null) {
                if (airQuality == EABleWeather.AirQuality.excellent) {
                    holder.qualityText.setText(getString(R.string.weather_air_excellent));
                } else if (airQuality == EABleWeather.AirQuality.good) {
                    holder.qualityText.setText(getString(R.string.weather_air_good));
                } else if (airQuality == EABleWeather.AirQuality.bad) {
                    holder.qualityText.setText(getString(R.string.weather_air_bad));
                }
            }
            EABleWeather.RaysLevel raysLevel = weatherItem.getE_rays();
            if (raysLevel != null) {
                if (raysLevel == EABleWeather.RaysLevel.weak) {
                    holder.uvText.setText(getString(R.string.weather_rays_weak));
                } else if (raysLevel == EABleWeather.RaysLevel.medium) {
                    holder.uvText.setText(getString(R.string.weather_rays_medium));
                } else if (raysLevel == EABleWeather.RaysLevel.strong) {
                    holder.uvText.setText(getString(R.string.weather_rays_strong));
                } else if (raysLevel == EABleWeather.RaysLevel.very_strong) {
                    holder.uvText.setText(getString(R.string.weather_rays_very_strong));
                } else if (raysLevel == EABleWeather.RaysLevel.super_strong) {
                    holder.uvText.setText(getString(R.string.weather_rays_super_strong));
                }
            }
            EABleWeather.Moon moon = weatherItem.getE_moon();
            if (moon != null) {
                if (moon == EABleWeather.Moon.new_moon) {
                    holder.moonText.setText(getString(R.string.weather_moon_new_moon));
                } else if (moon == EABleWeather.Moon.waxing_crescent_moon) {
                    holder.moonText.setText(getString(R.string.weather_moon_waxing_crescent_moon));
                } else if (moon == EABleWeather.Moon.quarter_moon) {
                    holder.moonText.setText(getString(R.string.weather_moon_quarter_moon));
                } else if (moon == EABleWeather.Moon.half_moon_1) {
                    holder.moonText.setText(getString(R.string.weather_moon_half_moon_1));
                } else if (moon == EABleWeather.Moon.waxing_gibbous_moon) {
                    holder.moonText.setText(getString(R.string.weather_moon_waxing_gibbous_moon));
                } else if (moon == EABleWeather.Moon.full_moon) {
                    holder.moonText.setText(getString(R.string.weather_moon_full_moon));
                } else if (moon == EABleWeather.Moon.waning_gibbous_moon) {
                    holder.moonText.setText(getString(R.string.weather_moon_waning_gibbous_moon));
                } else if (moon == EABleWeather.Moon.half_moon_2) {
                    holder.moonText.setText(getString(R.string.weather_moon_half_moon_2));
                } else if (moon == EABleWeather.Moon.last_quarter_moon) {
                    holder.moonText.setText(getString(R.string.weather_moon_last_quarter_moon));
                } else if (moon == EABleWeather.Moon.waning_crescent_moon) {
                    holder.moonText.setText(getString(R.string.weather_moon_waning_crescent_moon));
                }
            }
            holder.minTempText.setText(weatherItem.getMin_temperature() + "");
            holder.maxTempText.setText(weatherItem.getMax_temperature() + "");
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(weatherItem.getSunrise_timestamp() * 1000);
            holder.sunriseText.setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE));
            calendar.clear();
            calendar.setTimeInMillis(weatherItem.getSunset_timestamp() * 1000);
            Log.e(TAG, "日落时间:" + weatherItem.getSunrise_timestamp() * 1000);
            holder.sunsetText.setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE));
            holder.minWindText.setText(weatherItem.getMin_wind_power() + "");
            holder.maxWindText.setText(weatherItem.getMax_wind_power() + "");
            holder.humidityText.setText(weatherItem.getAir_humidity() + "");
            holder.cloudinessText.setText(weatherItem.getCloudiness() + "");


        }

        @Override
        public int getItemCount() {
            return eaBleWeather == null ? 0 : (eaBleWeather.getS_day() == null ? 0 : eaBleWeather.getS_day().size());
        }

        class HoldView extends RecyclerView.ViewHolder {
            @BindView(R.id.daytime_weather)
            TextView dayText;
            @BindView(R.id.night_weather)
            TextView nightText;
            @BindView(R.id.min_temperature)
            TextView minTempText;
            @BindView(R.id.max_temperature)
            TextView maxTempText;
            @BindView(R.id.sunrise_time)
            TextView sunriseText;
            @BindView(R.id.sunset_time)
            TextView sunsetText;
            @BindView(R.id.air_quality)
            TextView qualityText;
            @BindView(R.id.min_wind_force)
            TextView minWindText;
            @BindView(R.id.max_wind_force)
            TextView maxWindText;
            @BindView(R.id.uv_intensity)
            TextView uvText;
            @BindView(R.id.air_humidity)
            TextView humidityText;
            @BindView(R.id.moon)
            TextView moonText;
            @BindView(R.id.cloudiness)
            TextView cloudinessText;


            public HoldView(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
