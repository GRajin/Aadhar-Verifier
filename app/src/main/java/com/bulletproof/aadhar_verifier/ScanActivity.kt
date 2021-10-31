package com.bulletproof.aadhar_verifier

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import org.json.JSONObject


class ScanActivity : AppCompatActivity() {

    private var mCodeScanner: CodeScanner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 123)
        } else {
            startScanning()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show()
                startScanning()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startScanning() {
        val scannerView = findViewById<CodeScannerView>(R.id.scanView)
        mCodeScanner = CodeScanner(this, scannerView)
        mCodeScanner?.decodeCallback = DecodeCallback { result ->
            runOnUiThread {
                try {
                    val obj = JSONObject(result.text)
                    if (obj.getString("name") == "") {
                        val intent = Intent(this, UnverifiedActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, VerifiedActivity::class.java)
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    val intent = Intent(this, UnverifiedActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        scannerView.setOnClickListener { mCodeScanner?.startPreview() }
    }

    override fun onResume() {
        super.onResume()
        if (mCodeScanner != null) {
            mCodeScanner!!.startPreview()
        }
    }

    override fun onPause() {
        if (mCodeScanner != null) {
            mCodeScanner!!.releaseResources()
        }
        super.onPause()
    }
}