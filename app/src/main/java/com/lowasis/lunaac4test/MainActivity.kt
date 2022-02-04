package com.lowasis.lunaac4test

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPresentation
import android.media.AudioTrack
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var mAudioThread: AudioThread? = null
    private var mAudioTrack: AudioTrack? = null
    private var ac4ByteArray: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // read ac4 file
        val inputStream = resources.openRawResource(R.raw.ac4raw)
        ac4ByteArray = ByteArray(inputStream.available())
        inputStream.read(ac4ByteArray)
        inputStream.close()
        // audio track setting
        mAudioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                    .setFlags(0)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(48000)
                    .setChannelMask(12)
                    .setEncoding(AudioFormat.ENCODING_AC4)
                    .build()
            )
            .setTransferMode(AudioTrack.MODE_STREAM)
            .setBufferSizeInBytes(336)
            .build()
        // Presentation ID 10 chinese
        // Presentation ID 11 spanish
        // Presentation ID 12 english
        mAudioTrack?.setPresentation(AudioPresentation.Builder(12).build())
        mAudioTrack?.play()

        // start audio
        mAudioThread = AudioThread()
        mAudioThread?.start()
    }

    override fun onStop() {
        super.onStop()
        mAudioThread?.release()
        mAudioTrack?.stop()
        mAudioTrack?.release()
        mAudioTrack = null
    }

    inner class AudioThread : Thread() {
        var bRun = true
        override fun run() {
            var pos = 0
            while (bRun) {
                val ret = mAudioTrack?.write(ac4ByteArray!!, pos, 4096) ?: break
                if (ret <= 0) {
                    pos = 0
                } else {
                    pos += ret
                }
            }
        }

        fun release() {
            bRun = false
        }
    }
}