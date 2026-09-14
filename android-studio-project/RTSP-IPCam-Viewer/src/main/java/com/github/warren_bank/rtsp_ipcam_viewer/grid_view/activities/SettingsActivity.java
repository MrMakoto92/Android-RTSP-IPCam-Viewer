package com.github.warren_bank.rtsp_ipcam_viewer.grid_view.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.warren_bank.rtsp_ipcam_viewer.R;
import com.github.warren_bank.rtsp_ipcam_viewer.common.OnvifDiscovery;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private View viewAddCameras, viewEngineSettings;
    private RadioGroup radioGroupEngines;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_panel);

        prefs = getSharedPreferences("orion_settings", MODE_PRIVATE);

        viewAddCameras = findViewById(R.id.view_add_cameras);
        viewEngineSettings = findViewById(R.id.view_engine_settings);
        radioGroupEngines = findViewById(R.id.radio_group_engines);

        TextView title = findViewById(R.id.panel_title);
        String mode = getIntent().getStringExtra("MODE");
        if ("ENGINE".equals(mode)) {
            showEngineSection();
            if (title != null) title.setText("Ajustes Motor");
        } else {
            showAddSection();
            if (title != null) title.setText("Gestor Cámaras");
        }

        setupNavigation();
        setupEngineConfig();
        setupOnvifScan();
    }

    private void setupNavigation() {
        Button btnAdd = findViewById(R.id.btn_section_add);
        Button btnEngine = findViewById(R.id.btn_section_engine);
        Button btnBack = findViewById(R.id.btn_panel_back);

        if (btnAdd != null) btnAdd.setOnClickListener(v -> showAddSection());
        if (btnEngine != null) btnEngine.setOnClickListener(v -> showEngineSection());
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private void showAddSection() {
        viewAddCameras.setVisibility(View.VISIBLE);
        viewEngineSettings.setVisibility(View.GONE);
    }

    private void showEngineSection() {
        viewAddCameras.setVisibility(View.GONE);
        viewEngineSettings.setVisibility(View.VISIBLE);
    }

    private void setupEngineConfig() {
        String currentEngine = prefs.getString("player_engine", "EXOPLAYER");

        if ("LIBVLC".equals(currentEngine)) {
            ((RadioButton) findViewById(R.id.engine_libvlc)).setChecked(true);
        } else if ("FFMPEG".equals(currentEngine)) {
            ((RadioButton) findViewById(R.id.engine_ffmpeg)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.engine_exoplayer)).setChecked(true);
        }

        radioGroupEngines.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedEngine = "EXOPLAYER";
            if (checkedId == R.id.engine_libvlc) selectedEngine = "LIBVLC";
            if (checkedId == R.id.engine_ffmpeg) selectedEngine = "FFMPEG";

            prefs.edit().putString("player_engine", selectedEngine).apply();
            Toast.makeText(this, "Motor guardado: " + selectedEngine, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupOnvifScan() {
        Button btnScan = findViewById(R.id.btn_start_onvif_scan);
        if (btnScan != null) {
            btnScan.setOnClickListener(v -> {
                Toast.makeText(this, "Buscando dispositivos ONVIF en red...", Toast.LENGTH_SHORT).show();
                OnvifDiscovery.discoverDevices(this, new OnvifDiscovery.DiscoveryCallback() {
                    @Override
                    public void onDevicesFound(List<String> deviceUrls) {
                        Toast.makeText(SettingsActivity.this, "Cámaras encontradas: " + deviceUrls.size(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String errorMsg) {
                        Toast.makeText(SettingsActivity.this, "Error ONVIF: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }
}
