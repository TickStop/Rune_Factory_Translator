
package com.tickstop.runefactorytranslator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.tickstop.runefactorytranslator.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TranslationStateEntryChangeListener {

    private TranslationState translationState;

    private Context context;

    Spinner spinner_Select;
    private EditText originalText;
    private EditText translatedInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        context = getApplicationContext();

        spinner_Select = findViewById(R.id.spinner_Select);
        originalText = findViewById(R.id.multiLineText_Original);
        translatedInput = findViewById(R.id.multiLineText_Translation);

        translationState = new TranslationState();
    }

    private void setListeners() {
        translationState.removeListener(this);
        translationState.addListener(this);
        EditText searchInput = findViewById(R.id.editText_Search);

        Button button_Search = findViewById(R.id.button_Search);
        Button button_Save = findViewById(R.id.button_Save);
        Button button_Previous = findViewById(R.id.button_previous);
        Button button_Next = findViewById(R.id.button_next);

        spinner_Select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                translationState.selectEntry(adapterView.getSelectedItemPosition());
                view.refreshDrawableState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        searchInput.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                button_Search.performClick();
                return  true;
            }
            return false;
        });

        originalText.setLongClickable(false);
        originalText.setTextIsSelectable(false);
        button_Search.setOnClickListener(listener -> translationState.selectEntryContaining(searchInput.getText().toString()));
        button_Save.setOnClickListener(listener -> translationState.setEntryAtCurrentIndex(translatedInput.getText().toString()));
        button_Previous.setOnClickListener(listener -> translationState.selectEntry(translationState.getSelectedEntryIndex() - 1));
        button_Next.setOnClickListener(listener -> translationState.selectEntry(translationState.getSelectedEntryIndex() + 1));
    }

    private final ActivityResultLauncher<Intent> importOriginal = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_CANCELED)
                    return;

                if (result.getData() != null) {
                    TableData originalData = loadTableFromUri(result.getData().getData());
                    Toast.makeText(context, "Imported original file", Toast.LENGTH_LONG);
                    translationState.removeListener(this);
                    String path = result.getData().getData().getPath();
                    int lastPathSegmentIndex = path.lastIndexOf("/") + 1;
                    String fileName = path.substring(lastPathSegmentIndex);
                    translationState = new TranslationState(originalData, fileName);
                    translationState.addListener(this);
                    String[] entryIndexArray = new String[translationState.numberOfEntries];
                    for (int i = 0; i < translationState.numberOfEntries; i++) {
                        entryIndexArray[i] = String.format(Locale.GERMANY, "%d", i);
                    }
                    spinner_Select.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, entryIndexArray));
                    setListeners();
                }
            }
    );

    private final ActivityResultLauncher<Intent> importTranslation = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    translationState = new TranslationState();
                    return;
                }

                if (result.getData() != null) {
                    TableData translatedData = loadTableFromUri(result.getData().getData());
                    if (translatedData == null)
                        return;
                    translationState.setTranslation(translatedData);
                    translationState.selectEntry(0);
                    updateUIDisplay(translationState);
                }
            }
    );

    private TableData loadTableFromUri(Uri uri) {
        byte[] loadedData = loadFile(uri);
        if (loadedData.length == 0) {
            Toast.makeText(context, "The selected file was not of type TEXT", Toast.LENGTH_SHORT).show();
            return null;
        }
        return new TableData(loadedData);
    }

    private final ActivityResultLauncher<String> exportFile = registerForActivityResult(
            new ActivityResultContracts.CreateDocument(),
            result ->  {
                if (result != null) {
                    saveFile(result);
                    Toast.makeText(context, "Saved file to " + result.getLastPathSegment(), Toast.LENGTH_LONG).show();
                }
            }
    );

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private Intent getFileLoadIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_import_original) {
            Intent intent = getFileLoadIntent();
            importOriginal.launch(intent);
        }
        else if (id == R.id.action_import_translation) {
            Intent intent = getFileLoadIntent();
            importTranslation.launch(intent);
        }
        else if (id == R.id.action_export) {
            exportFile.launch(translationState.OriginalFileName);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /** Updates the UI to show the currently selected entry */
    private void updateUIDisplay(TranslationState state) {
        spinner_Select.setSelection(state.getSelectedEntryIndex());
        String[] entries = state.getTableEntry();
        originalText.setText(entries[0]);
        translatedInput.setText(entries[1]);
    }

    /** Loads a text file
     * @return the contents as a byte array */
    private byte[] loadFile(Uri filePath) {
        byte[] allBytes = new byte[0];
        try (InputStream inputStream = getContentResolver().openInputStream(filePath)) {
            allBytes = FileSaveManager.importBinaryTextFile(inputStream);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return allBytes;
    }

    /** Saves the current TranslationState to a file */
    private void saveFile(Uri filePath) {
        File file = new File(filePath.getPath());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (OutputStream outputStream = getContentResolver().openOutputStream(filePath, "wt")) {
            FileSaveManager.exportBinaryTextFile(outputStream, translationState);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Execute(TranslationState state) {
        updateUIDisplay(state);
    }

    @Override
    public void onBackPressed() {
        // Disallow closing the app with the return button because this deletes all made changes
        Toast.makeText(context, "Can't close app using return", Toast.LENGTH_SHORT).show();
    }
}