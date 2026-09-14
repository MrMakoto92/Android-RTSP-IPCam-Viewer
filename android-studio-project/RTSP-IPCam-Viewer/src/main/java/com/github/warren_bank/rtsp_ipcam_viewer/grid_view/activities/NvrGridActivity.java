package com.github.warren_bank.rtsp_ipcam_viewer.grid_view.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.warren_bank.rtsp_ipcam_viewer.R;
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

        // Recalcular tamaño exacto de celda al renderizar el RecyclerView
        recyclerView.post(() -> updateGridDimensions(currentColumns));

        setupUIButtons();
    }

    private void setupUIButtons() {
        ImageButton btnGridDialog = findViewById(R.id.btn_open_grid_dialog);
        ImageButton btnAddCam = findViewById(R.id.btn_nav_add_nvr);
        ImageButton btnSettings = findViewById(R.id.btn_nav_settings);

        if (btnGridDialog != null) {
            btnGridDialog.setOnClickListener(v -> showGridSelectionDialog());
        }

        // Abre el panel completo tipo tablet para Agregar Cámaras (+)
        if (btnAddCam != null) {
            btnAddCam.setOnClickListener(v -> {
                Intent intent = new Intent(NvrGridActivity.this, SettingsActivity.class);
                intent.putExtra("MODE", "ADD");
                startActivity(intent);
            });
        }

        // Abre el panel completo tipo tablet en la sección del Motor Multimedia
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                Intent intent = new Intent(NvrGridActivity.this, SettingsActivity.class);
                intent.putExtra("MODE", "ENGINE");
                startActivity(intent);
            });
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
