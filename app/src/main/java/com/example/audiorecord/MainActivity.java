package com.example.audiorecord;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import kotlin.math.MathKt;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1; // ここで定義


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestAudioPermission();

        AudioRecordSample audioRecordSample = new AudioRecordSample();
        audioRecordSample.startRecording();
    }

    public class AudioRecordSample {

        private final int samplingRate = 44100;
        private final int frameRate = 10;
        private final int oneFrameDataCount = samplingRate / frameRate;
        private final int oneFrameSizeInByte = oneFrameDataCount * 2;

        private int audioBufferSizeInByte;

        public void startRecording() {
            audioBufferSizeInByte = Math.max(oneFrameSizeInByte * 10,
                    AudioRecord.getMinBufferSize(samplingRate,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT));


            AudioRecord audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    samplingRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    audioBufferSizeInByte);


            audioRecord.setPositionNotificationPeriod(oneFrameDataCount);
            audioRecord.setNotificationMarkerPosition(40000);

            short[] audioDataArray = new short[oneFrameDataCount];

            audioRecord.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener() {
                @Override
                public void onPeriodicNotification(AudioRecord recorder) {
                    recorder.read(audioDataArray, 0, oneFrameDataCount);
                    Log.v("AudioRecord", "onPeriodicNotification size=" + audioDataArray.length);
                    // 好きに処理する
                }

                @Override
                public void onMarkerReached(AudioRecord recorder) {
                    recorder.read(audioDataArray, 0, oneFrameDataCount);
                    Log.v("AudioRecord", "onMarkerReached size=" + audioDataArray.length);
                    // 好きに処理する
                }
            });

            audioRecord.startRecording();
        }
    }

    // RECORD_AUDIO アクセス許可をリクエストするメソッド
    private void requestAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // アクセス許可が付与されていない場合、リクエストする
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_CODE);
        }
    }

}

