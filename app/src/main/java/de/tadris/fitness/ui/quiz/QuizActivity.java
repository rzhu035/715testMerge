package de.tadris.fitness.ui.quiz;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.tadris.fitness.R;
import de.tadris.fitness.client.BirdClassifierApiClient;
import de.tadris.fitness.converter.ScientificNameConverter;
import de.tadris.fitness.generator.Generator;
import de.tadris.fitness.model.BirdData;
import de.tadris.fitness.ui.FitoTrackActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends FitoTrackActivity {
    private ImageView imageView;

    private Button backToWorkoutButton;
    private int imageValue;

    private ProgressBar mProgressBar;
    private MyCountDownTimer myCountDownTimer;

    private TextView countDownText;

    private Button choiceOne;
    private Button choiceTwo;
    private Button choiceThree;

    private TextView quizResult;

    private int finalQuizScore;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);

        backToWorkoutButton = findViewById(R.id.back_to_workout);
        setBackToWorkoutButtonEvent(backToWorkoutButton);

        imageView = findViewById(R.id.quiz_imageView);
        renderQuizImage(imageView);

        mProgressBar = findViewById(R.id.quiz_countdown);
        countDownText = findViewById(R.id.sec_remains);

        myCountDownTimer = new MyCountDownTimer(60000, 1000);
        myCountDownTimer.start();

        choiceOne = findViewById(R.id.choice_1);
        choiceTwo = findViewById(R.id.choice_2);
        choiceThree = findViewById(R.id.choice_3);

        quizResult = findViewById(R.id.quizResult);
        quizResult.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
        quizResult.setVisibility(View.INVISIBLE);
    }

    private void renderQuizImage(ImageView imageView){
        byte[] byteArray = getIntent().getByteArrayExtra("quiz_image");
        Bitmap quizImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        // Convert byteArray to File to send to API Request
        File file = new File(this.getCacheDir(), "image.jpg");
        try {
            file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(byteArray);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Call API will probably make another function later
        //-----------------------------------------------------------------------------------------
        BirdClassifierApiClient apiClient = new BirdClassifierApiClient();
        Call<List<BirdData>> callPredictions = apiClient.getPredictions(file);
        callPredictions.enqueue(new Callback <List<BirdData>> () {

            @Override
            public void onResponse(Call < List<BirdData> > callPredictions, Response< List<BirdData>  > response) {
                Log.v("Upload", "success");
                List<BirdData> responseBody = response.body();

                ScientificNameConverter converter = new ScientificNameConverter();
                
                choiceOne.setText(converter.convertToCommonName(responseBody.get(0).getScientificName()));
                choiceTwo.setText(converter.convertToCommonName(responseBody.get(1).getScientificName()));
                choiceThree.setText(converter.convertToCommonName(responseBody.get(3).getScientificName()));

                // pass correct choice (1, 2, 3) here to bind answer event to the button
                // TODO: pass the choice which has the highest probability here.
                bindAnswerEventToChoices(1);
            }
            @Override
            public void onFailure(Call < List<BirdData>  > call, Throwable t) {
                Log.e("Upload", t.getMessage());
                Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_LONG).show();
            }

        });
        //-----------------------------------------------------------------------------------------

        imageView.setImageBitmap(quizImage);
    }

    private void setBackToWorkoutButtonEvent(Button backToWorkoutButton){
        backToWorkoutButton.setOnClickListener(v->{
            finish();
            onBackPressed();
        });
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (millisUntilFinished / 1000);
            mProgressBar.setProgress(mProgressBar.getMax()-progress);
            countDownText.setText("seconds remaining: " + progress);
        }

        @Override
        public void onFinish() {
            finish();
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    public void answerCorrect(Button choice){
        choice.setOnClickListener(v->{
            quizResult.setVisibility(View.VISIBLE);
            quizResult.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            );
            calculateQuizScore(true);
            quizResult.setText("Correct!\nScore: " + this.finalQuizScore);
            mProgressBar.setVisibility(View.INVISIBLE);
            countDownText.setVisibility(View.INVISIBLE);
        });
    }

    public void answerWrong(Button choice){
        choice.setOnClickListener(v->{
            quizResult.setVisibility(View.VISIBLE);
            quizResult.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            );
            calculateQuizScore(false);
            quizResult.setText("Wrong answer!\nScore: " + this.finalQuizScore);
            mProgressBar.setVisibility(View.INVISIBLE);
            countDownText.setVisibility(View.INVISIBLE);
        });
    }

    public void bindAnswerEventToChoices(int correctChoice){
        switch(correctChoice) {
            case 1:
                answerCorrect(choiceOne);
                answerWrong(choiceTwo);
                answerWrong(choiceThree);
                break;
            case 2:
                answerWrong(choiceOne);
                answerCorrect(choiceTwo);
                answerWrong(choiceThree);
                break;
            case 3:
                answerWrong(choiceOne);
                answerWrong(choiceTwo);
                answerCorrect(choiceThree);
                break;
            default:
                // fall through
                Log.v("BindAnswerEvent", "bind answer event to choice button failed!");
                break;
        }
    }
    public void calculateQuizScore(boolean isAnswerCorrect){
        //TODO: implement the actual score calculating formula for quiz
        if(isAnswerCorrect){
            this.finalQuizScore = 100;
        } else {
            this.finalQuizScore = 50;
        }
    }
}
