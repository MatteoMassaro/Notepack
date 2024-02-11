package com.example.notepack.ui.notepack;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.notepack.MainActivity;
import com.example.notepack.R;
import com.example.notepack.databinding.CreateNotepackFragmentBinding;
import com.example.notepack.ui.category.CategoryAdapter;
import com.example.notepack.ui.category.Data;
import com.example.notepack.ui.database.DBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class CreateNotepackFragment extends Fragment {

    //Variabili
    private CreateNotepackFragmentBinding binding;
    private CategoryAdapter categoryAdapter;

    AdapterView.OnItemSelectedListener selectCategoryAdapter;
    Boolean saveNotepacksDetails, updateNotepacksDetails, saveNotepackItems, notepackTitleEmpty, newItemCreated, newItemDeleted, notepackStateRestored;
    Button saveBtn, createItemBtn;
    DBHelper DB;
    FloatingActionButton deleteItemFab;
    Handler handler;
    ImageView categoryImage;
    LinearLayout itemLinearLayout;
    Spinner selectCategorySpinner;
    String notepackTitle, notepackCategory, itemTitle;
    SwitchCompat switchTaken;
    TextInputEditText insertTitle, insertItemText;
    TextView itemsTakenText, text;
    TextWatcher titleTextWatcher, itemTextWatcher;
    Toolbar toolbar;
    View itemView;
    Toast toast;
    View.OnClickListener switchListener, deleteFabListener;

    int itemsTakenCounter, initializedItemsCunter, itemCategory, taken, notepacksCounter, notepackId;
    
    public static Boolean saved;
    public static int itemsCounter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = CreateNotepackFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Inizializzazione delle variabili
        itemLinearLayout = binding.itemLinearLayout;
        itemsTakenText = binding.itemsTakenText;
        insertTitle = binding.insertTitle;
        categoryImage = binding.categoryImage;
        saveBtn = binding.saveBtn;
        createItemBtn = binding.createItemBtn;
        saved = true;
        notepackTitleEmpty = true;
        handler = new Handler();
        DB = new DBHelper(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, null);
        text = (TextView) layout.findViewById(R.id.text);
        toast = new Toast(getContext());
        toast.setView(layout);
        List<Integer> itemsIds = new ArrayList<>();

        //Disattivazione del pulsante di salvataggio della Notepack
        saveBtn.setEnabled(false);
        saveBtn.setBackgroundColor(getResources().getColor(R.color.gray));

        //Settaggio del salvataggio della Notepack
        saveNotepack();

        //Settaggio della creazione degli items della Notepack
        createItem();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        //Rimozione del listener del titolo della Notepack
        insertTitle.removeTextChangedListener(titleTextWatcher);

        for(int i = 0; i < itemsCounter; i++) {
            //Inizializzazione variabili
            insertItemText = itemLinearLayout.getChildAt(i).findViewById(R.id.insertItemText);
            selectCategorySpinner = itemLinearLayout.getChildAt(i).findViewById(R.id.selectCategorySpinner);
            switchTaken = itemLinearLayout.getChildAt(i).findViewById(R.id.switchTaken);
            deleteItemFab = itemLinearLayout.getChildAt(i).findViewById(R.id.deleteItemBtn);

            //Rimozione del listener del testo di ciascun item della Notepack
            insertItemText.removeTextChangedListener(itemTextWatcher);

            //Rimozione del listener dello spinner di ciascun item della Notepack
            selectCategorySpinner.setOnItemSelectedListener(null);

            //Rimozione del listener dello switch di ciascun item
            switchTaken.setOnClickListener(null);

            //Rimozione del listener del pulsante di eliminazione di ciascun item
            deleteItemFab.setOnClickListener(null);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //Inizializzazione delle variabili
        toolbar = getActivity().findViewById(R.id.toolbar);
        newItemCreated = false;
        newItemDeleted = false;
        saveNotepackItems = false;
        saved = true;
        Cursor cursor1 = DB.getNotepacksId();


        //Settaggio dell'icona di navigazione della toolbar
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        //Settaggio della Notepack dal database in base allo stato salvato
        if(!MainActivity.newNotepackCreated) {
            Cursor cursor2 = DB.getNotepacksIdByTitle(MainActivity.animationText.getText().toString());
            if(cursor2 != null && cursor2.moveToFirst()) {
                notepackId = cursor2.getInt(0);
            }else{
                text.setText(getString(R.string.database_error));
                toast.show();
            }
            Cursor cursor3 = DB.getItemsNumberById(notepackId);
            if(cursor3 != null && cursor3.moveToFirst()) {
                itemsCounter = cursor3.getInt(0);
            }else{
                text.setText(getString(R.string.database_error));
                toast.show();
            }
            Cursor cursor4 = DB.getNotepacksDetailsItemsTakenById(notepackId);
            if(cursor4!= null && cursor4.moveToFirst()) {
                itemsTakenCounter = cursor4.getInt(0);
            }else{
                text.setText(getString(R.string.database_error));
                toast.show();
            }
            notepackTitleEmpty = false;
            notepackStateRestored = true;
            itemLinearLayout.removeAllViews();
            setNotepackDetailsFromDatabase();
        }else{
            if(cursor1 != null && cursor1.moveToLast()){
                notepacksCounter = cursor1.getInt(0) + 1;
            }else{
                notepacksCounter = 0;
                text.setText(getString(R.string.database_error));
                toast.show();
            }
            notepackId = notepacksCounter;
            itemsCounter = 0;
            itemsTakenCounter = 0;
            notepackStateRestored = false;
        }

        //Settaggio del testo degli items presi della Notepack
        itemsTakenText.setText(new StringBuilder().append(getString(R.string.items_taken_text1)).append(itemsTakenCounter).append(getString(R.string.items_taken_text2)).append(itemsCounter).toString());

        //Settaggio dell'immagine della categoria della Notepack
        switch (MainActivity.notepackCategory) {
            case "Vacanza":
                categoryImage.setImageResource(R.drawable.vacation);
                break;
            case "Viaggio di lavoro":
                categoryImage.setImageResource(R.drawable.business_trip);
                break;
            case "Viaggio d'istruzione":
                categoryImage.setImageResource(R.drawable.school_trip);
                break;
            case "Personalizzato":
                categoryImage.setImageResource(R.drawable.custom);
                break;
        }

        //Settaggio del listener del titolo della Notepack
        setTitleListener();

    }

    //Settaggio del listener del titolo della Notepack
    public void setTitleListener(){
        //Controlli sul titolo della Notepack per il salvataggio
        titleTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saved = true;
                notepackTitleEmpty = true;
                saveBtn.setEnabled(false);
                saveBtn.setBackgroundColor(getResources().getColor(R.color.gray));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!insertTitle.getText().toString().isEmpty() && !insertTitle.getText().toString().startsWith(" ") && !insertTitle.getText().toString().endsWith(" ")) {
                    saved = false;
                    notepackTitleEmpty = false;
                    saveBtn.setEnabled(true);
                    saveBtn.setBackgroundColor(getResources().getColor(R.color.lime_green));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Nessuna azione
            }
        };

        //Aggiunta del listener del titolo della Notepack
        insertTitle.addTextChangedListener(titleTextWatcher);

    }

    //Settaggio del listener del testo di ciascun item della Notepack
    public void setItemTextListener(){
        //Controlli sul testo dell'item per il salvataggio della Notepack
        itemTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saved = true;
                saveBtn.setEnabled(false);
                saveBtn.setBackgroundColor(getResources().getColor(R.color.gray));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!insertItemText.getText().toString().isEmpty() && !insertItemText.getText().toString().startsWith(" ") && !insertItemText.getText().toString().endsWith(" ") && !notepackTitleEmpty) {
                    saved = false;
                    saveBtn.setEnabled(true);
                    saveBtn.setBackgroundColor(getResources().getColor(R.color.lime_green));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Nessuna azione
            }
        };

        //Aggiunta del listener del testo di ciascun item della Notepack
        insertItemText.addTextChangedListener(itemTextWatcher);

    }

    //Settaggio del listener dello spinner di ciascun item della Notepack
    public void setCategorySpinnerListener(){
        //Controlli sulla categoria scelta dell'item per il salvataggio della Notepack
        selectCategoryAdapter = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                initializedItemsCunter++;
                view.findViewById(R.id.name).setVisibility(View.GONE);
                if(!insertItemText.getText().toString().isEmpty() && !insertItemText.getText().toString().startsWith(" ") && !insertItemText.getText().toString().endsWith(" ") && !notepackTitleEmpty && initializedItemsCunter > itemsCounter){
                    saved = false;
                    saveBtn.setEnabled(true);
                    saveBtn.setBackgroundColor(getResources().getColor(R.color.lime_green));
                    notepackStateRestored = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Nessuna azione
            }
        };

        //Settaggio della categoria di ciascun item della Notepack in base allo stato salvato
        if(newItemCreated) {
            selectCategorySpinner.setSelection(0, true);
        }else{
            selectCategorySpinner.setSelection(itemCategory, true);
        }

        selectCategorySpinner.getSelectedView().findViewById(R.id.name).setVisibility(View.GONE);

        //Aggiunta del listener dello spinner di ciascun item della Notepack
        selectCategorySpinner.setOnItemSelectedListener(selectCategoryAdapter);

    }

    //Settaggio del listener dello switch di ciascun item della Notepack
    public void setSwitchListener(){
        //Controlli sullo stato dello switch per il salvataggio della Notepack
        switchListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!insertItemText.getText().toString().isEmpty() && !insertItemText.getText().toString().startsWith(" ") && !insertItemText.getText().toString().endsWith(" ") && !notepackTitleEmpty){
                    saved = false;
                    saveBtn.setEnabled(true);
                    saveBtn.setBackgroundColor(getResources().getColor(R.color.lime_green));
                }
            }
        };

        //Aggiunta del listener dello switch di ciascun item della Notepack
        switchTaken.setOnClickListener(switchListener);

    }

    //Salvataggio della Notepack
    public void saveNotepack(){
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Inizializzazione delle variabili
                notepackTitle = insertTitle.getText().toString().substring(0,1).toUpperCase() + insertTitle.getText().toString().substring(1);
                notepackCategory = MainActivity.notepackCategory;
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast, null);
                TextView text = (TextView) layout.findViewById(R.id.text);
                Toast toast = new Toast(getContext());
                toast.setView(layout);
                itemsTakenCounter = 0;
                int i;

                //Salvataggio degli items della Notepack
                for(i=0; i < itemsCounter; i++){
                    //Inizializzazione variabili
                    selectCategorySpinner = itemLinearLayout.getChildAt(i).findViewById(R.id.selectCategorySpinner);
                    insertItemText = itemLinearLayout.getChildAt(i).findViewById(R.id.insertItemText);
                    switchTaken = itemLinearLayout.getChildAt(i).findViewById(R.id.switchTaken);
                    itemCategory = selectCategorySpinner.getSelectedItemPosition();
                    itemTitle = insertItemText.getText().toString();

                    Cursor cursor = DB.getItemsTitleById(notepackId);
                    cursor.moveToPosition(i);

                    if(switchTaken.isChecked()){
                        taken = 0;
                        itemsTakenCounter++;
                    }else{
                        taken = 1;
                    }

                    if(newItemCreated || newItemDeleted) {
                        saveNotepackItems = DB.saveNotepacksItems(i, notepackId, itemCategory, itemTitle, taken);
                        DB.updateItemsNumber(notepackId, itemsCounter);
                    }

                    if(!saveNotepackItems){
                        DB.updateNotepacksItems(i, notepackId, itemCategory, itemTitle, taken);
                        DB.updateItemsNumber(notepackId, itemsCounter);
                    }
                }

                //Settaggio del testo degli items presi della Notepack
                itemsTakenText.setText(new StringBuilder().append(getString(R.string.items_taken_text1)).append(itemsTakenCounter).append(getString(R.string.items_taken_text2)).append(itemsCounter).toString());

                if(itemsCounter == 0 && newItemDeleted){
                    DB.updateItemsNumber(notepackId, 0);
                }

                newItemCreated = false;
                saveNotepackItems = false;

                //Salvataggio e aggiornamento della Notepack
                if(MainActivity.newNotepackCreated) {
                    saveNotepacksDetails = DB.saveNotepacksDetails(notepackId, notepackTitle, notepackCategory, itemsCounter, itemsTakenCounter);
                    updateNotepacksDetails = false;
                }else{
                    updateNotepacksDetails = DB.updateNotepacksDetails(notepackId, notepackTitle, itemsTakenCounter);
                    saveNotepacksDetails = false;
                }

                //Visualizzazione messaggio di esito del salvataggio della Notepack
                if(saveNotepacksDetails){
                    text.setText(getString(R.string.notepack_saved));
                    toast.show();
                    saved = true;
                    saveBtn.setEnabled(false);
                    saveBtn.setBackgroundColor(getResources().getColor(R.color.gray));
                    MainActivity.newNotepackCreated = false;
                    DB.increaseNotepacksNumber();
                }else if(updateNotepacksDetails){
                    text.setText(getString(R.string.notepack_updated));
                    toast.show();
                    saved = true;
                    saveBtn.setEnabled(false);
                    saveBtn.setBackgroundColor(getResources().getColor(R.color.gray));
                }else{
                    text.setText(getString(R.string.existing_notepack));
                    toast.show();
                    saved = false;
                }
            }
        });

    }

    //Creazione degli items della Notepack
    public void createItem(){
        createItemBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InflateParams")
            @Override
            public void onClick(View view) {
                //Inizializzazione delle variabili
                newItemCreated = true;
                newItemDeleted = false;
                itemView = getLayoutInflater().inflate(R.layout.notepack_item, null);
                itemLinearLayout.addView(itemView, itemsCounter);
                insertItemText = itemView.findViewById(R.id.insertItemText);
                selectCategorySpinner = itemLinearLayout.getChildAt(itemsCounter).findViewById(R.id.selectCategorySpinner);
                categoryAdapter = new CategoryAdapter(getContext(), Data.getCategoryList());
                selectCategorySpinner.setAdapter(categoryAdapter);
                switchTaken = itemLinearLayout.getChildAt(itemsCounter).findViewById(R.id.switchTaken);
                deleteItemFab = itemLinearLayout.getChildAt(itemsCounter).findViewById(R.id.deleteItemBtn);
                itemsCounter++;

                //Disattivazione pulsante di salvataggio della Notepack
                saveBtn.setEnabled(false);
                saveBtn.setBackgroundColor(getResources().getColor(R.color.gray));

                //Settaggio del listener del testo di ciascun item della Notepack
                setItemTextListener();

                //Settaggio del listener dello spinner di ciascun item della Notepack
                setCategorySpinnerListener();

                //Settaggio del listener dello switch di ciascun item
                setSwitchListener();

                //Settaggio del listener del pulsante di rimozione dell'item della Notepack
                setDeleteFabListener();
            }
        });

    }

    //Settaggio del listener del pulsante di eliminazione di ciascun item
    public void setDeleteFabListener(){
        //Controlli sulla pressione del pulsante di eliminazione di ciascun item
        deleteFabListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animazione della rimozione dell'item
                View view1 = (View) view.getParent();
                view1.animate().translationX(-1000);
                handler.postDelayed(() -> itemLinearLayout.removeView((View) view.getParent()), 300);

                //Settaggio del messaggio di avvenuta eliminazione dell'item della Notepack
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast, null);
                TextView text = (TextView) layout.findViewById(R.id.text);
                text.setText(getString(R.string.item_deleted));
                Toast toast = new Toast(getContext());
                toast.setView(layout);
                toast.show();

                itemsCounter--;
                newItemDeleted = true;

                //Attivazione del pulsante di salvataggio della Notepack
                if(!newItemCreated) {
                    saved = false;
                    saveBtn.setEnabled(true);
                    saveBtn.setBackgroundColor(getResources().getColor(R.color.lime_green));
                }
            }
        };

        //Aggiunta del listener del pulsante di eliminazione di ciascun item
        deleteItemFab.setOnClickListener(deleteFabListener);
    }

    //Settaggio delle Notepacks dal database
    public void setNotepackDetailsFromDatabase(){
        //Inizializzazione delle variabili
        Cursor cursor1 = DB.getItemsCategoryById(notepackId);
        Cursor cursor2 = DB.getItemsTitleById(notepackId);
        Cursor cursor3 = DB.getNotepacksItemsTakenById(notepackId);

        int i;

        //Settaggio degli items della Notepack
        for(i = 0; i < itemsCounter; i++) {
            //Inizializzazione delle variabili
            itemView = getLayoutInflater().inflate(R.layout.notepack_item, null);
            itemLinearLayout.addView(itemView, i);
            insertItemText = itemLinearLayout.getChildAt(i).findViewById(R.id.insertItemText);
            selectCategorySpinner = itemLinearLayout.getChildAt(i).findViewById(R.id.selectCategorySpinner);
            categoryAdapter = new CategoryAdapter(getContext(), Data.getCategoryList());
            selectCategorySpinner.setAdapter(categoryAdapter);
            switchTaken = itemLinearLayout.getChildAt(i).findViewById(R.id.switchTaken);
            deleteItemFab = itemLinearLayout.getChildAt(i).findViewById(R.id.deleteItemBtn);
            Cursor cursor = DB.getItemsCategoryById(notepackId);
            Log.d("TAG", String.valueOf(notepackId));
            cursor.moveToPosition(i);
            itemCategory = cursor.getInt(0);

            if(cursor1.moveToPosition(i)){
                selectCategorySpinner.setSelection(cursor1.getInt(0));
            }

            if(cursor2.moveToPosition(i)){
                insertItemText.setText(cursor2.getString(0));
            }

            if(cursor3.moveToPosition(i)){
                switchTaken.setChecked(cursor3.getInt(0) == 0);
            }

            //Settaggio del listener del pulsante di rimozione dell'item
            if(itemsCounter > 0) {
                setDeleteFabListener();
            }

            //Settaggio del listener del testo di ciascun item della Notepack
            setItemTextListener();

            //Settaggio del listener dello spinner di ciascun item della Notepack
            setCategorySpinnerListener();

            //Settaggio del listener dello switch di ciascun item
            setSwitchListener();
        }

        //Settaggio del titolo della Notepack
        insertTitle.setText(MainActivity.animationText.getText().toString());
    }

}
