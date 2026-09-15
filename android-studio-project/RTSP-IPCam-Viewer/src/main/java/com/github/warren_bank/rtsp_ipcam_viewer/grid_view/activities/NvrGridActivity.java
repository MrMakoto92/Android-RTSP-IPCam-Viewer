package com.github.warren_bank.rtsp_ipcam_viewer.grid_view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
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
    private List<String> fullMasterList;
    private List<String> activeGridList;
    private GridLayoutManager layoutManager;
    private int currentColumns = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_nvr);

        recyclerView = findViewById(R.id.camera_grid_recycler);

        fullMasterList = new ArrayList<>();
        activeGridList = new ArrayList<>();

        for (int i = 1; i <= 16; i++) {
            fullMasterList.add("CH " + String.format("%02d", i) + " - CANAL");
        }

        layoutManager = new GridLayoutManager(this, currentColumns);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NvrGridAdapter(this, activeGridList, position -> {
            Toast.makeText(NvrGridActivity.this, "Seleccionado: " + activeGridList.get(position), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        updateGridDisplay(currentColumns);
        setupUIButtons();
    }

    private void setupUIButtons() {
        ImageButton btnGridDialog = findViewById(R.id.btn_open_grid_dialog);
        ImageButton btnAddCam = findViewById(R.id.btn_nav_add_nvr);
        ImageButton btnSettings = findViewById(R.id.btn_nav_settings);

        if (btnGridDialog != null) {
            btnGridDialog.setOnClickListener(v -> showGridSelectionDialog());
        }

        // Abre la pantalla DEDICADA para añadir cámaras
        if (btnAddCam != null) {
            btnAddCam.setOnClickListener(v -> {
                Intent intent = new Intent(NvrGridActivity.this, AddCameraActivity.class);
                startActivity(intent);
            });
        }

        // Abre la pantalla DEDICADA exclusivamente a Ajustes de Motor
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                Intent intent = new Intent(NvrGridActivity.this, SettingsActivity.class);
                startActivity(intent);
            });
        }
    }

    private void showGridSelectionDialog() {
        String[] options = {"Rejilla 1x1 (1 Cámara)", "Rejilla 2x2 (4 Cámaras)", "Rejilla 3x3 (9 Cámaras)", "Rejilla 4x4 (16 Cámaras)"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Formato de Rejilla");
        builder.setItems(options, (dialog, which) -> {
            currentColumns = which + 1;
            updateGridDisplay(currentColumns);
        });
        builder.show();
    }

    private void updateGridDisplay(int columns) {
        int maxItems = columns * columns;
        activeGridList.clear();

        for (int i = 0; i < Math.min(maxItems, fullMasterList.size()); i++) {
            activeGridList.add(fullMasterList.get(i));
        }

        if (layoutManager != null && recyclerView != null) {
            layoutManager.setSpanCount(columns);
            adapter.notifyDataSetChanged();
            
            recyclerView.post(() -> {
                int availableHeight = recyclerView.getHeight() - recyclerView.getPaddingTop() - recyclerView.getPaddingBottom();
                adapter.setGridConfig(columns, availableHeight);
            });
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
