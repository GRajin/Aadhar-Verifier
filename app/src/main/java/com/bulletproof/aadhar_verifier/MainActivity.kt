package com.bulletproof.aadhar_verifier

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import org.json.JSONObject

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSave = findViewById<MaterialButton>(R.id.btnScan)
        btnSave.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        if(NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            processIntent(intent)
        }
    }

    private fun processIntent(intent: Intent) {
        intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMsgs ->
            (rawMsgs[0] as NdefMessage).apply {
                try {
                    val obj = JSONObject(String(records[0].payload))
                    if(obj.getString("name") == "") {
                        val i = Intent(this@MainActivity, UnverifiedActivity::class.java)
                        startActivity(i)
                    } else {
                        val i = Intent(this@MainActivity, VerifiedActivity::class.java)
                        startActivity(i)
                    }
                } catch(e: Exception) {
                    val i = Intent(this@MainActivity, UnverifiedActivity::class.java)
                    startActivity(i)
                }
            }
        }
    }
}