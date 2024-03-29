package com.example.try2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Build
import java.util.Locale
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    lateinit var speechText: EditText
    lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //입력창
        speechText = findViewById(R.id.speech_text)

        //음성전환 버튼 이벤트
        val speechBtn: Button = findViewById(R.id.speech_btn)
        speechBtn.setOnClickListener {

            val intent: Intent = Intent()
            intent.action = TextToSpeech.Engine.ACTION_CHECK_TTS_DATA
            activityResult.launch(intent)
        }

    }//onCreate

    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {

        //보이스가 있다면
        if (it.resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {

            //음성전환 준비
            textToSpeech = TextToSpeech(this, this)

        } else { //없다면 다운로드
            //데이터 다운로드
            val installIntent: Intent = Intent()
            installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
            startActivity(installIntent)
        }
    }

    //TextToSpeech 엔진 초기화시 호출되는 함수
    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            //언어값
            val languageStatus: Int = textToSpeech.setLanguage(Locale.KOREAN)

            //데이터 문제(데이터가 없거나 언어를 지원할 수 없다면)
            if (languageStatus == TextToSpeech.LANG_MISSING_DATA ||
                languageStatus == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                Toast.makeText(
                    this, "언어를 지원할 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            } else { //데이터 성공
                //입력값 변수에 담기
                val data: String = speechText.text.toString()

                var speechStatus: Int = 0

                //안드로이드 버전에 따른 조건(롤리팝보다 같거나 높다면
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    speechStatus = textToSpeech.speak(
                        data, TextToSpeech.QUEUE_FLUSH,
                        null, null
                    )
                } else {
                    speechStatus = textToSpeech.speak(
                        data, TextToSpeech.QUEUE_FLUSH,
                        null
                    )
                }


                if (speechStatus == TextToSpeech.ERROR) {
                    Toast.makeText(
                        this, "음성전환 에러입니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else { //실패
            Toast.makeText(
                this, "음성전환 엔진 에러입니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}