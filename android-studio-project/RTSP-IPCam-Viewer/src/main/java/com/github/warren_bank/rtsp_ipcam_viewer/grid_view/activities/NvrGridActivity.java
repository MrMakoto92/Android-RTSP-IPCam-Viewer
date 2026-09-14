package com.github.warren_bank.rtsp_ipcam_viewer.grid_view.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private int currentColumns = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_nvr);

        recyclerView = findViewById(R.id.camera_grid_recycler);

        cameraList = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            cameraList.add("CH " + String.format("%02d", i) + " - CANAL");
        }

        layoutManager = new GridLayoutManager(this, currentColumns);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NvrGridAdapter(this, cameraList, position -> {
            Toast.makeText(NvrGridActivity.this, "Cámara seleccionada: " + cameraList.get(position), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        // Recalcular tamaño exacto cuando el RecyclerView termine de renderizarse
        recyclerView.post(() -> updateGridDimensions(currentColumns));

        setupUIButtons();
    }

    private void setupUIButtons() {
        ImageButton btnGridDialog = findViewById(R.id.btn_open_grid_dialog);
        ImageButton btnAddCam = findViewById(R.id.btn_nav_add_nvr);

        if (btnGridDialog != null) {
            btnGridDialog.setOnClickListener(v -> showGridSelectionDialog());
        }

        if (btnAddCam != null) {
            btnAddCam.setOnClickListener(v -> showAddCameraDialog());
        }
    }

    private void showGridSelectionDialog() {
        String[] options = {"Rejilla 1x1", "Rejilla 2x2", "Rejilla 3x3", "Rejilla 4x4"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Rejilla");
        builder.setItems(options, (dialog, which) -> {
            currentColumns = which + 1;
            updateGridDimensions(currentColumns);
        });
        builder.show();
    }

    private void updateGridDimensions(int columns) {
        if (layoutManager != null && recyclerView != null) {
            layoutManager.setSpanCount(columns);
            int availableHeight = recyclerView.getHeight() - recyclerView.getPaddingTop() - recyclerView.getPaddingBottom();
            adapter.setGridConfig(columns, availableHeight);
        }
    }

    private void showAddCameraDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_camera, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        Button btnScan = view.findViewById(R.id.btn_dialog_onvif_search);
        Button btnSave = view.findViewById(R.id.btn_save_camera);
        EditText inputUrl = view.findViewById(R.id.input_stream_url);

        btnScan.setOnClickListener(v -> {
            dialog.dismiss();
            executeOnvifScan();
        });

        btnSave.setOnClickListener(v -> {
            String url = inputUrl.getText().toString().trim();
            if (!url.isEmpty()) {
                cameraList.add(0, "MANUAL - " + url);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Cámara agregada", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void executeOnvifScan() {
        Toast.makeText(this, "Escaneando dispositivos ONVIF...", Toast.LENGTH_SHORT).show();
        OnvifDiscovery.discoverDevices(this, new OnvifDiscovery.DiscoveryCallback() {
            @Override
            public void onDevicesFound(List<String> deviceIps) {
                if (!deviceIps.isEmpty()) {
                    cameraList.clear();
                    for (String ip : deviceIps) {
                        cameraList.add("ONVIF CAM - " + ip);
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(NvrGridActivity.this, "Se encontraron " + deviceIps.size() + " cámaras", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NvrGridActivity.this, "No se encontraron cámaras ONVIF", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMsg) {
                Toast.makeText(NvrGridActivity.this, "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
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
