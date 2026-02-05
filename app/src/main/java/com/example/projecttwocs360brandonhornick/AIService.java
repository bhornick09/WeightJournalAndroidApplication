package com.example.projecttwocs360brandonhornick;

import android.widget.TextView;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

public class AIService {
    private String prompt;
    private TextView resultText;
    OpenAIClient client = OpenAIOkHttpClient.builder().apiKey("").build(); // key removed for submission

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setResultText(TextView result) {
        this.resultText = result;
    }

    public void sendRequest() {
        // Note: This must be wrapped in a Thread or AsyncTask
        new Thread(() -> {
            try {
                ResponseCreateParams params = ResponseCreateParams.builder()
                        .input(prompt)
                        .model(ChatModel.GPT_3_5_TURBO) // Ensure model name is correct for your SDK
                        .build();

                Response response = client.responses().create(params);

                // Accessing the string content:
                // This exact syntax depends on your specific SDK version,
                // but it usually looks like this:
                String output = response.output().toString();

                // To update a TextView, you MUST move back to the Main Thread
                resultText.post(() -> resultText.setText(output));

            } catch (Exception e) {
                e.printStackTrace();
                resultText.post(() -> resultText.setText("Error: " + e.getMessage()));
            }
        }).start();
    }
}
