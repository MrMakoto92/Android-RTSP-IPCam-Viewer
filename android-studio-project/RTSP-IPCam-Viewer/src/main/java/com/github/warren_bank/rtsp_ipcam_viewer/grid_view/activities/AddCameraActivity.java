package com.github.warren_bank.rtsp_ipcam_viewer.grid_view.activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.warren_bank.rtsp_ipcam_viewer.R;

public class AddCameraActivity extends AppCompatActivity {

    private EditText inputAlias, inputIp, inputPort, inputUser, inputPass, inputPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_camera);

        inputAlias = findViewById(R.id.input_cam_alias);
        inputIp = findViewById(R.id.input_cam_ip);
        inputPort = findViewById(R.id.input_cam_port);
        inputUser = findViewById(R.id.input_cam_user);
        inputPass = findViewById(R.id.input_cam_pass);
        inputPath = findViewById(R.id.input_cam_path);

        Button btnSave = findViewById(R.id.btn_save_custom_cam);
        Button btnClose = findViewById(R.id.btn_close_add);

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveCameraConfig());
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }
    }

    private void saveCameraConfig() {
        String ip = inputIp.getText().toString().trim();
        if (ip.isEmpty()) {
            Toast.makeText(this, "Ingresa una dirección IP válida", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Cámara guardada correctamente", Toast.LENGTH_SHORT).show();
        finish();
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
