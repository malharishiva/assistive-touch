package com.assistivetouch.app

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.assistivetouch.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var audioManager: AudioManager

    companion object {
        private const val PREFS_NAME = "assistive_prefs"
        private const val KEY_FLOATING_ENABLED = "key_floating_enabled"
        private const val KEY_NOTIFICATION_ENABLED = "key_notification_enabled"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        setupPermissionsUI()
        setupToggles()
        setupActionButtons()
        setupVolumeSliders()
    }

    override fun onResume() {
        super.onResume()
        updatePermissionStates()
        updateVolumeSliders()
    }

    private fun setupPermissionsUI() {
        // Overlay Permission
        binding.btnOverlayPermission.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Overlay permission already granted", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Accessibility Service Permission
        binding.btnAccessibilityPermission.setOnClickListener {
            if (!ActionAccessibilityService.isAccessibilityServiceEnabled(this)) {
                Toast.makeText(this, "Find 'Assistive Power & Volume Service' and turn it ON", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Accessibility Service is already ON", Toast.LENGTH_SHORT).show()
            }
        }

        // Device Admin Permission (For locking screen)
        binding.btnAdminPermission.setOnClickListener {
            if (!DeviceAdminManager.isActive(this)) {
                DeviceAdminManager.requestAdmin(this)
            } else {
                Toast.makeText(this, "Screen Lock Admin is already active", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePermissionStates() {
        // 1. Overlay
        val hasOverlay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
        if (hasOverlay) {
            binding.btnOverlayPermission.text = "✓ Granted"
            binding.btnOverlayPermission.setTextColor(Color.parseColor("#43A047"))
            binding.btnOverlayPermission.isEnabled = false
        } else {
            binding.btnOverlayPermission.text = "Enable"
            binding.btnOverlayPermission.isEnabled = true
        }

        // 2. Accessibility
        val hasAccessibility = ActionAccessibilityService.isAccessibilityServiceEnabled(this)
        if (hasAccessibility) {
            binding.btnAccessibilityPermission.text = "✓ Enabled"
            binding.btnAccessibilityPermission.setTextColor(Color.parseColor("#43A047"))
            binding.btnAccessibilityPermission.isEnabled = false
        } else {
            binding.btnAccessibilityPermission.text = "Enable"
            binding.btnAccessibilityPermission.isEnabled = true
        }

        // 3. Admin
        val hasAdmin = DeviceAdminManager.isActive(this)
        if (hasAdmin) {
            binding.btnAdminPermission.text = "✓ Active"
            binding.btnAdminPermission.setTextColor(Color.parseColor("#43A047"))
            binding.btnAdminPermission.isEnabled = false
        } else {
            binding.btnAdminPermission.text = "Enable"
            binding.btnAdminPermission.isEnabled = true
        }
    }

    private fun setupToggles() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Floating widget toggle
        val floatingOn = prefs.getBoolean(KEY_FLOATING_ENABLED, false)
        binding.switchFloatingWidget.isChecked = floatingOn
        binding.switchFloatingWidget.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                    binding.switchFloatingWidget.isChecked = false
                    Toast.makeText(this, "Please grant Floating Overlay permission first", Toast.LENGTH_SHORT).show()
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                    startActivity(intent)
                    return@setOnCheckedChangeListener
                }
                FloatingWidgetService.start(this)
                prefs.edit().putBoolean(KEY_FLOATING_ENABLED, true).apply()
            } else {
                FloatingWidgetService.stop(this)
                prefs.edit().putBoolean(KEY_FLOATING_ENABLED, false).apply()
            }
        }

        // Notification toggle
        val notificationOn = prefs.getBoolean(KEY_NOTIFICATION_ENABLED, false)
        binding.switchNotification.isChecked = notificationOn
        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ControlNotificationService.start(this)
                prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, true).apply()
            } else {
                ControlNotificationService.stop(this)
                prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, false).apply()
            }
        }
    }

    private fun setupActionButtons() {
        binding.btnVolUp.setOnClickListener {
            ActionHelper.volumeUp(this)
            updateVolumeSliders()
        }

        binding.btnVolDown.setOnClickListener {
            ActionHelper.volumeDown(this)
            updateVolumeSliders()
        }

        binding.btnLockScreen.setOnClickListener {
            ActionHelper.lockScreen(this)
        }

        binding.btnPowerMenu.setOnClickListener {
            ActionHelper.openPowerMenu(this)
        }
    }

    private fun setupVolumeSliders() {
        // Media
        val maxMedia = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        binding.seekBarMedia.max = maxMedia
        binding.seekBarMedia.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        binding.seekBarMedia.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Ring
        val maxRing = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
        binding.seekBarRing.max = maxRing
        binding.seekBarRing.progress = audioManager.getStreamVolume(AudioManager.STREAM_RING)
        binding.seekBarRing.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) audioManager.setStreamVolume(AudioManager.STREAM_RING, progress, 0)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Call
        val maxCall = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
        binding.seekBarCall.max = maxCall
        binding.seekBarCall.progress = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
        binding.seekBarCall.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, progress, 0)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateVolumeSliders() {
        binding.seekBarMedia.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        binding.seekBarRing.progress = audioManager.getStreamVolume(AudioManager.STREAM_RING)
        binding.seekBarCall.progress = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
    }
}
