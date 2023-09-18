package de.tadris.fitness.generator;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Generator {

    public String randomGenerateImages() {
        File folder = new File(android.os.Environment.getExternalStorageDirectory()+ "/birdsimages/");

        File[] files = folder.listFiles();

        Random rand = new Random();

        File choosedFile = files[rand.nextInt(files.length)];

        return choosedFile.getName();
    }

    public ArrayList<String> generateQuiz(String answer){

        File folder = new File(android.os.Environment.getExternalStorageDirectory()+ "/birdsimages/");

        File[] files = folder.listFiles();

        ArrayList<String> fileNames = new ArrayList<>();

        for (File file : files) {
            String name = FilenameUtils.removeExtension(file.getName());
            if (!name.equals(answer)){
                fileNames.add(FilenameUtils.removeExtension(file.getName()));
            }
        }

        Random rand = new Random();

        ArrayList<String> quiz = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            int index = rand.nextInt(fileNames.size());
            String choiceName = fileNames.get(index);
            quiz.add(choiceName);
            fileNames.remove(index);
        }

        quiz.add(answer);

        Collections.shuffle(quiz, new Random());

        return quiz;
    }
}
