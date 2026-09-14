package com.github.warren_bank.rtsp_ipcam_viewer.grid_view.activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.warren_bank.rtsp_ipcam_viewer.R;
import com.github.warren_bank.rtsp_ipcam_viewer.common.OnvifDiscovery;
import com.github.warren_bank.rtsp_ipcam_viewer.grid_view.adapters.NvrGridAdapter;
import java.util.ArrayList;
import java.util.List;

public class NvrGridActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NvrGridAdapter adapter;
    private List<String> cameraList;
    private GridLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_nvr);

        recyclerView = findViewById(R.id.camera_grid_recycler);

        // Lista de demostración inicial
        cameraList = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            cameraList.add("CH " + String.format("%02d", i) + " - ESPERANDO ONVIF");
        }

        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NvrGridAdapter(this, cameraList, position -> {
            Toast.makeText(NvrGridActivity.this, "Cámara seleccionada: " + cameraList.get(position), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        setupGridButtons();
    }

    private void setupGridButtons() {
        Button btn1 = findViewById(R.id.btn_grid_1);
        Button btn4 = findViewById(R.id.btn_grid_4);
        Button btn9 = findViewById(R.id.btn_grid_9);
        Button btn16 = findViewById(R.id.btn_grid_16);
        Button btnOnvif = findViewById(R.id.btn_onvif_scan);

        if (btn1 != null) btn1.setOnClickListener(v -> setGridColumns(1));
        if (btn4 != null) btn4.setOnClickListener(v -> setGridColumns(2));
        if (btn9 != null) btn9.setOnClickListener(v -> setGridColumns(3));
        if (btn16 != null) btn16.setOnClickListener(v -> setGridColumns(4));

        if (btnOnvif != null) {
            btnOnvif.setOnClickListener(v -> executeOnvifScan());
        }
    }

    private void executeOnvifScan() {
        Toast.makeText(this, "Escaneando red local en busca de cámaras ONVIF...", Toast.LENGTH_SHORT).show();
        
        OnvifDiscovery.discoverDevices(this, new OnvifDiscovery.DiscoveryCallback() {
            @Override
            public void onDevicesFound(List<String> deviceIps) {
                if (deviceIps.isEmpty()) {
                    Toast.makeText(NvrGridActivity.this, "No se encontraron dispositivos ONVIF en la red.", Toast.LENGTH_LONG).show();
                } else {
                    cameraList.clear();
                    for (int i = 0; i < deviceIps.size(); i++) {
                        cameraList.add("ONVIF CAM - " + deviceIps.get(i));
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(NvrGridActivity.this, "¡Se encontraron " + deviceIps.size() + " cámaras ONVIF!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String errorMsg) {
                Toast.makeText(NvrGridActivity.this, "Error en escaneo ONVIF: " + errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setGridColumns(int columns) {
        if (layoutManager != null) {
            layoutManager.setSpanCount(columns);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Modo de Rejilla: " + columns + "x" + columns, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
