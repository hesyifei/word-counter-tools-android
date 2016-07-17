package com.arefly.wordcounter;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Map<String, String> unitStringData = new HashMap<>();

    private Toolbar topToolbar;
    private EditText mainEditText;

    private Button wordBtn;
    private Button charBtn;
    private Button sentBtn;
    private Button paraBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 設定「軟鍵盤出現後自動Resize界面」
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        // 獲取頂部Toolbar
        topToolbar = (Toolbar) findViewById(R.id.id_toolbar_top);

        setSupportActionBar(topToolbar);



        unitStringData.put("Word.Singular", getString(R.string.unit_word_singular));
        unitStringData.put("Word.Plural", getString(R.string.unit_word_plural));
        unitStringData.put("Word.Short.Singular", getString(R.string.unit_short_word_singular));
        unitStringData.put("Word.Short.Plural", getString(R.string.unit_short_word_plural));

        unitStringData.put("Character.Singular", getString(R.string.unit_character_singular));
        unitStringData.put("Character.Plural", getString(R.string.unit_character_plural));
        unitStringData.put("Character.Short.Singular", getString(R.string.unit_short_character_singular));
        unitStringData.put("Character.Short.Plural", getString(R.string.unit_short_character_plural));

        unitStringData.put("Paragraph.Singular", getString(R.string.unit_paragraph_singular));
        unitStringData.put("Paragraph.Plural", getString(R.string.unit_paragraph_plural));
        unitStringData.put("Paragraph.Short.Singular", getString(R.string.unit_short_paragraph_singular));
        unitStringData.put("Paragraph.Short.Plural", getString(R.string.unit_short_paragraph_plural));

        unitStringData.put("Sentence.Singular", getString(R.string.unit_sentence_singular));
        unitStringData.put("Sentence.Plural", getString(R.string.unit_sentence_plural));

        unitStringData.put("Sentence.Short.Singular", getString(R.string.unit_short_sentence_singular));
        unitStringData.put("Sentence.Short.Plural", getString(R.string.unit_short_sentence_plural));



        wordBtn = (Button) findViewById(R.id.id_button_word);
        charBtn = (Button) findViewById(R.id.id_button_char);
        sentBtn = (Button) findViewById(R.id.id_button_sent);
        paraBtn = (Button) findViewById(R.id.id_button_para);

        wordBtn.setText(getCountString("", "Word"));
        charBtn.setText(getCountString("", "Character"));
        sentBtn.setText(getCountString("", "Sentence"));
        paraBtn.setText(getCountString("", "Paragraph"));


        mainEditText = (EditText) findViewById(R.id.id_main_edit_text);

        mainEditText.addTextChangedListener(new TextWatcher() {

            private CountWordsAsyncTask asyncTask;

            @Override
            public void afterTextChanged(final Editable s) {
                //Log.e(TAG, "afterTextChanged");

                String text = s.toString().trim();

                if(asyncTask != null) {
                    asyncTask.cancel(true);
                    //Log.e(TAG, "noyes"+asyncTask.getStatus());
                }

                asyncTask = (CountWordsAsyncTask) new CountWordsAsyncTask().execute(text);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


    }


    // 當物理按鈕按下並鬆開(up)後
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if(event.getAction() == KeyEvent.ACTION_UP){
            switch(keyCode) {
                case KeyEvent.KEYCODE_MENU:
                    Log.i(TAG, "Menu Button Clicked");
                    // 直接顯示頂部Toolbar的Menu
                    topToolbar.showOverflowMenu();
                    // 不顯示默認Menu Panel
                    return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_count:
                Log.i(TAG, "Count Action Clicked");

                showCountResultAlert(mainEditText.getText().toString());

                return true;
            case R.id.action_clear:
                Log.i(TAG, "Clear Action Clicked");

                AlertDialog.Builder clearContentBuilder = new AlertDialog.Builder(MainActivity.this);
                clearContentBuilder.setTitle(getString(R.string.alert_before_clear_title));
                clearContentBuilder.setMessage(getString(R.string.alert_before_clear_message));
                clearContentBuilder.setCancelable(true);

                clearContentBuilder.setPositiveButton(
                        getString(R.string.alert_before_clear_button_clear),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mainEditText.setText("");
                                dialog.cancel();
                            }
                        });
                clearContentBuilder.setNegativeButton(
                        getString(R.string.alert_before_clear_button_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog clearContentAlert = clearContentBuilder.create();
                clearContentAlert.show();

                return true;
            case R.id.action_about:
                Log.i(TAG, "About Action Clicked");
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                // http://stackoverflow.com/a/10960720/2603230
                aboutIntent.putExtra( AboutActivity.EXTRA_SHOW_FRAGMENT, AboutActivity.AboutFragment.class.getName() );
                aboutIntent.putExtra( AboutActivity.EXTRA_NO_HEADERS, true );
                startActivity(aboutIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }




    public void hideKeyboard() {
        // Check if no view has focus:
        View currentFocusView = this.getCurrentFocus();
        // 隱藏軟鍵盤
        if (currentFocusView != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocusView.getWindowToken(), 0);
        }
    }

    public void countResultButtonAction(View view) {
        showCountResultAlert(mainEditText.getText().toString());
    }

    public void showCountResultAlert(String text) {
        AlertDialog.Builder countResultBuilder = new AlertDialog.Builder(MainActivity.this);
        countResultBuilder.setMessage("Write your message here.\n123\n456\n789");
        countResultBuilder.setCancelable(true);

        countResultBuilder.setNeutralButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog countResultAlert = countResultBuilder.create();
        countResultAlert.show();
    }


    public String getCountString(String inputString, String type) {
        int count = getCount(inputString, type);
        String unitString = (count == 1) ? unitStringData.get(type+".Short.Singular") : unitStringData.get(type+".Short.Plural");
        return count+" "+unitString;
    }

    public int getCount(String inputString, String type) {
        int returnInt;

        switch(type){
            case "Word":
                returnInt = wordCount(inputString);
                break;
            case "Character":
                returnInt = characterCount(inputString);
                break;
            case "Sentence":
                returnInt = sentenceCount(inputString);
                break;
            case "Paragraph":
                returnInt = paragraphCount(inputString);
                break;
            default:
                returnInt = 0;
        }

        return returnInt;
    }


    class CountWordsAsyncTask extends AsyncTask<String, Void, Map<String, String>> {
        @Override
        protected Map<String, String> doInBackground(String... inputParas) {
            String text = inputParas[0];

            Map<String, String> map = new HashMap<>();
            map.put("Word", getCountString(text, "Word"));
            map.put("Character", getCountString(text, "Character"));
            map.put("Sentence", getCountString(text, "Sentence"));
            map.put("Paragraph", getCountString(text, "Paragraph"));

            return map;
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            wordBtn.setText(result.get("Word"));
            charBtn.setText(result.get("Character"));
            sentBtn.setText(result.get("Sentence"));
            paraBtn.setText(result.get("Paragraph"));
        }

    }


    public int wordCount(String inputString) {

        // 代碼邏輯來自於iOS版本的字數統計工具

        int counts = 0;
        String lines[] = inputString.replaceAll("\\p{P}", "").split("\\r?\\n");
        String joinedString = TextUtils.join(" ", lines);
        //Log.e(TAG, "NO:"+joinedString);
        String words[] = joinedString.split("\\s+");
        //Log.e(TAG, "YES:"+TextUtils.join(",", words));
        for (String eachWord: words) {
            eachWord = eachWord.trim();

            if (TextUtils.isEmpty(eachWord)) {
                continue;
            }
            if (isCJK(eachWord)) {

                // http://stackoverflow.com/a/1675826/2603230
                Set<Character.UnicodeBlock> chineseUnicodeBlocks = new HashSet<Character.UnicodeBlock>() {{
                    add(Character.UnicodeBlock.CJK_COMPATIBILITY);
                    add(Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS);
                    add(Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS);
                    add(Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT);
                    add(Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT);
                    add(Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION);
                    add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
                    add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A);
                    add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B);
                    add(Character.UnicodeBlock.KANGXI_RADICALS);
                    add(Character.UnicodeBlock.IDEOGRAPHIC_DESCRIPTION_CHARACTERS);
                }};


                List<String> chineseCharResultList = new ArrayList<>();

                for (char c : eachWord.toCharArray()) {
                    if (chineseUnicodeBlocks.contains(Character.UnicodeBlock.of(c))) {
                        //Log.e(TAG, c + " is chinese");
                        chineseCharResultList.add(String.valueOf(c));
                    }
                }

                List<String> eachWordCharList = new ArrayList<>(Arrays.asList(eachWord.split("")));
                eachWordCharList.remove(0);


                /*Log.e(TAG, eachWordCharList.get(0));
                Log.e(TAG, chineseCharResultList.get(0));
                Log.e(TAG, ""+eachWordCharList.get(0).equals(chineseCharResultList.get(0)));

                Log.e(TAG, eachWordCharList.get(eachWordCharList.size()-1));
                Log.e(TAG, chineseCharResultList.get(chineseCharResultList.size()-1));*/

                if (!eachWordCharList.get(0).equals(chineseCharResultList.get(0))) {
                    counts += 1;
                    //Log.e(TAG, "0 FALSE");
                }

                if (!eachWordCharList.get(eachWordCharList.size()-1).equals(chineseCharResultList.get(chineseCharResultList.size()-1))) {
                    counts += 1;
                    //Log.e(TAG, "last FALSE");
                }

                counts += chineseCharResultList.size();
                continue;
            }

            counts += 1;

        }

        return counts;

    }


    public int characterCount(String inputString) {

        // 代碼邏輯來自於iOS版本的字數統計工具

        int characterCounts = 0;

        String stringToBeCount = inputString.replace(" ", "").replace("\n", "");

        return stringToBeCount.length();
    }

    public int sentenceCount(String inputString) {

        // 因iOS版本使用的是iOS自帶函數,需要重新使用regex來計算句數


        // http://stackoverflow.com/a/34247755/2603230
        String sents[] = inputString.replaceAll("((?<=[.?!;…])\\s+|(?<=[。！？；…])\\s*)(?=[\\p{L}\\p{N}])", "[*-SENTENCE-*]").split("(\\[\\*-SENTENCE-\\*\\])");
        for (int i = 0; i < sents.length; i++) {
            sents[i] = sents[i].trim();
        }

        //Log.e(TAG, Arrays.toString(sents));

        List<String> sentsList = new ArrayList<>(Arrays.asList(sents));
        sentsList.removeAll(Collections.singleton(null));
        sentsList.removeAll(Collections.singleton(""));

        //Log.e(TAG, ""+sentsList.size());

        return sentsList.size();

    }

    public int paragraphCount(String inputString) {
        // 代碼邏輯來自於iOS版本的字數統計工具

        String paras[] = inputString.split("\\r?\\n");

        List<String> parasList = new ArrayList<>();

        for (String eachPara: paras) {
            eachPara = eachPara.trim();

            if (TextUtils.isEmpty(eachPara)) {
                continue;
            }

            parasList.add(eachPara);
        }

        return parasList.size();
    }


    public static boolean isCJK(String str){
        int length = str.length();
        for (int i = 0; i < length; i++){
            char ch = str.charAt(i);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
            if (Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(block)||
                    Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS.equals(block)||
                    Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A.equals(block)){
                return true;
            }
        }
        return false;
    }



}
