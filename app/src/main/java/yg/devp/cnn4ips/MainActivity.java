package yg.devp.cnn4ips;

import android.app.Activity;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import yg.devp.util.RequestHttpURLConnection;
import yg.devp.util.Useful;

import static yg.devp.util.Useful.LOG_COMM_SERVER;

/* 2018-11-10 04:00 LBS system design */

public class MainActivity extends BLEActivity {

    private static EditText edit_main_cell;
    private EditText edit_main_set;
    private Button btn_main_query;
    private Button btn_main_learn;
    private Button btn_main_reset;
    private static TextView textv_main_message;
    private static LinearLayout message_LinearLayout;
    private static Activity mainActivityContext;
    private static ScrollView scrollView;
    private static TextView tv_main_accuracy;  // 정확도 표기

    private static ImageView imageCell1;
    private static ImageView imageCell2;
    private static ImageView imageCell3;
    private static ImageView imageCell4;


    private boolean toggle = false; // false:off, true:on
    private String cellNumber = null;
    private static int urlSendCount = 0; // 쿼리전송시 전송한 URL 갯수
    private static int correctCount = 0; // 맞은 횟수 카운트
    private static String accuracyPercent = null; // 정확도 확률(%)
    private static int responseCell = 0; // 응답으로 온 셀번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initScreen() {
        edit_main_cell = findViewById(R.id.edit_main_cell);
        edit_main_set = findViewById(R.id.edit_main_set);
        btn_main_reset = findViewById(R.id.btn_main_reset);
        btn_main_query = findViewById(R.id.btn_main_query);
        btn_main_learn = findViewById(R.id.btn_main_learn);
        message_LinearLayout = findViewById(R.id.message_LinearLayout);
        scrollView = findViewById(R.id.ScrollView);
        tv_main_accuracy = findViewById(R.id.tv_main_accuracy);

        imageCell1 = findViewById(R.id.image_cell1);
        imageCell2 = findViewById(R.id.image_cell2);
        imageCell3 = findViewById(R.id.image_cell3);
        imageCell4 = findViewById(R.id.image_cell4);

        imageCell1.setVisibility(View.INVISIBLE);
        imageCell2.setVisibility(View.INVISIBLE);
        imageCell3.setVisibility(View.INVISIBLE);
        imageCell4.setVisibility(View.INVISIBLE);

        btn_main_reset.setOnClickListener(clickListener);
        btn_main_query.setOnClickListener(clickListener);
        btn_main_learn.setOnClickListener(clickListener);

        mainActivityContext = this;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btn_main_reset) {
                urlSendCount = 0; // url 전송갯수 초기화
                correctCount = 0; // 정확도 갯수 초기화
                tv_main_accuracy.setText("0%");

                if ((message_LinearLayout).getChildCount() > 0)
                    (message_LinearLayout).removeAllViews();
            } else if (v == btn_main_query) {
                if (toggle) {
                    Toast.makeText(MainActivity.this, "Query : Off", Toast.LENGTH_SHORT).show();
                    toggle = false;
                } else {
                    Toast.makeText(MainActivity.this, "Query : On", Toast.LENGTH_SHORT).show();
                    toggle = true;
                    setButtonType(1); // 1 : query button
                }
                scanLeDevice(toggle);
            }  else if (v == btn_main_learn) {
                Toast.makeText(MainActivity.this, "Learning Start", Toast.LENGTH_SHORT).show();
                int setNumberForLearning = Integer.parseInt(edit_main_set.getText().toString());
                String learnUrl = Useful.URL_LEARN + setNumberForLearning + "/";
                new CNN4IPSNetworkTask(learnUrl, null).execute();
            }
        }
    };

    public static void AutoScrollBottom() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    // 응답하는 셀번호에 따라 실내지도에 빨간점 표시
    public static void SelectImageCell(int cellNumber){
        if(cellNumber == 1){
            imageCell1.setVisibility(View.VISIBLE);
            imageCell2.setVisibility(View.INVISIBLE);
            imageCell3.setVisibility(View.INVISIBLE);
            imageCell4.setVisibility(View.INVISIBLE);
        } else if(cellNumber == 2){
            imageCell1.setVisibility(View.INVISIBLE);
            imageCell2.setVisibility(View.VISIBLE);
            imageCell3.setVisibility(View.INVISIBLE);
            imageCell4.setVisibility(View.INVISIBLE);
        } else if(cellNumber == 3){
            imageCell1.setVisibility(View.INVISIBLE);
            imageCell2.setVisibility(View.INVISIBLE);
            imageCell3.setVisibility(View.VISIBLE);
            imageCell4.setVisibility(View.INVISIBLE);
        } else if(cellNumber == 4){
            imageCell1.setVisibility(View.INVISIBLE);
            imageCell2.setVisibility(View.INVISIBLE);
            imageCell3.setVisibility(View.INVISIBLE);
            imageCell4.setVisibility(View.VISIBLE);
        }
    }


    public static class CNN4IPSNetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public CNN4IPSNetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return parser(result);
        }

        @Override
        protected void onPostExecute(String aString) {
            super.onPostExecute(aString);

            //save버튼을 눌렀을때만 카운트횟수와 'Success:save' 문구 출력
            if (getSetNumberForLearning() != 0 && getButtonType() == 2) {
                int num = getSetNumberForLearning() - getSetNumber();
                Log.i("setNumberLearning", "setNumberLearning:" + getSetNumberForLearning());
                Log.i("setNumber", "setNumber:" + getSetNumber());

                textv_main_message = new TextView(mainActivityContext);
                textv_main_message.setText(String.format("%05d:%s\n", num, aString));
                message_LinearLayout.addView(textv_main_message);
                AutoScrollBottom();
            } else {
                Log.i("setNumberLearning", "setNumberLearning:" + getSetNumberForLearning());
                if (getButtonType() == 1) {
                    //query버튼 누를시 정확도 표기를 목적으로함

                    urlSendCount++;

                    responseCell = Integer.parseInt(aString);
                    int cellNumber = Integer.parseInt(edit_main_cell.getText().toString());

                    if (responseCell == cellNumber) {
                        correctCount++;
                    }

                    double value = (double) correctCount / (double) urlSendCount;

                    accuracyPercent = String.valueOf(Double.parseDouble(String.format("%.2f", value)) * 100);
                    //정확도 표기
                    Log.i(LOG_COMM_SERVER, "accuracy:" + accuracyPercent + "%");
                    Log.i(LOG_COMM_SERVER, "urlSendCount:" + urlSendCount);
                    Log.i(LOG_COMM_SERVER, "correctCount:" + correctCount);
                    Log.i(LOG_COMM_SERVER, "responseCell:" + responseCell);
                    Log.i(LOG_COMM_SERVER, "cellNumber:" + cellNumber);
                    tv_main_accuracy.setText(accuracyPercent + "%");
                }

                textv_main_message = new TextView(mainActivityContext);
                textv_main_message.setText(aString + "\n");
                message_LinearLayout.addView(textv_main_message);
                AutoScrollBottom();

                SelectImageCell(responseCell);
            }
        }

        private String parser(String aString) {
            String parse = aString;
            Log.d("TAG", "Web: " + parse);

            return parse;
        }
    }
}
