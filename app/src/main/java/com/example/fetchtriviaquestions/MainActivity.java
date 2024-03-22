package com.example.fetchtriviaquestions;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getTranslationModel();
        getTriviaQuestions();
    }
    private void getTranslationModel() {
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.HEBREW)
                        .build();
        Translator englishHebrewTranslator =
                Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        englishHebrewTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        v -> {
                            Toast.makeText(MainActivity.this, "Model downloaded", Toast.LENGTH_SHORT).show();
                            // Model downloaded successfully. Okay to start translating.
                        })
                .addOnFailureListener(
                        e -> {
                            Toast.makeText(MainActivity.this, "Model cannot be downloaded", Toast.LENGTH_SHORT).show();

                            // Model couldn’t be downloaded or other internal error.
                        });
    }
    private void getTriviaQuestions() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://opentdb.com/api.php?amount=1")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();


                    // Parse the JSON response into Question objects
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(myResponse);

                    JSONArray results = null;
                    results = jsonObject.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject questionObject = results.getJSONObject(i);
                            String questionText = questionObject.getString("question");
                            String correctAnswer = questionObject.getString("correct_answer");
                            JSONArray incorrectAnswersArray = questionObject.getJSONArray("incorrect_answers");
                            List<String> incorrectAnswers = new ArrayList<>();
                            for (int j = 0; j < incorrectAnswersArray.length(); j++) {
                                incorrectAnswers.add(incorrectAnswersArray.getString(j));
                            }
                            QuestionData question = new QuestionData(questionText, correctAnswer, incorrectAnswers);
                            // Add the question to your data structure
                            showQuestionOnScreen(question);

                            //translateQuestion(question);
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


        }
    }
});
    }



    private void translateQuestion(String data, View v) {
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.HEBREW)
                        .build();
        Translator englishTranslator =
                Translation.getClient(options);

        englishTranslator.translate(data)
                .addOnSuccessListener(
                        translatedText -> {
                            // Translation successful
                            // Use the translated text
                            if(v instanceof TextView)
                                ((TextView)v).setText(translatedText);
                            else
                                ((Button)v).setText(translatedText);
                        })
                .addOnFailureListener(
                        e -> {
                            // Error occurred in translation
                        });
    }

    private void showQuestionOnScreen(QuestionData question) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView questionTextView = findViewById(R.id.question_text_view);
             //   questionTextView.setText(question.getQuestion());
                translateQuestion(question.getQuestion(), questionTextView);
                LinearLayout answersLayout = findViewById(R.id.answers_layout);
                for (String answer : question.getAllAnswers()) {
                    Button answerButton = new Button(MainActivity.this);
                   // answerButton.setText(answer);
                    // show the translated text on the button
                    // keep original as TAG
                    translateQuestion(answer, answerButton);
                    answerButton.setTag(answer);
                    answerButton.setOnClickListener(v -> {
                    //    String selectedAnswer = ((Button) v).getText().toString();
                           String selectedAnswer = ((Button) v).getTag().toString();

                        if(selectedAnswer.equals(question.getCorrectAnswer())) {
                            // Correct answer
                            ((Button) v).setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                        } else {
                            // Incorrect answer
                            ((Button) v).setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                        }
                        // Check if the answer is correct and update the UI accordingly
                    });
                    answersLayout.addView(answerButton);
                }
            }
        });

    }
}