package com.example.notepack.ui.notepack;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.notepack.MainActivity;
import com.example.notepack.R;
import com.example.notepack.databinding.NotepackFragmentBinding;
import com.example.notepack.ui.database.DBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NotepackFragment extends Fragment {

    //Variabili
    private NotepackFragmentBinding binding;

    FloatingActionButton fab, deleteNotepackFab;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    LinearLayout notepackLinearLayout;
    TextView notepackTitle, itemsTaken;
    ImageView notepackImage;
    Animation circleExplosion, textExplosion, circleImplosion, textImplosion;
    Handler handler;
    DBHelper DB;

    int notepacksCounter, itemsCounter, itemsTakenCounter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = NotepackFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Inizializzazione delle variabili
        handler = new Handler();
        DB = new DBHelper(getContext());

        //Settaggio delle animazioni
        circleExplosion = AnimationUtils.loadAnimation(getContext(), R.anim.circle_explosion_animation);
        textExplosion = AnimationUtils.loadAnimation(getContext(), R.anim.text_explosion_animation);
        circleImplosion = AnimationUtils.loadAnimation(getContext(), R.anim.circle_implosion_animation);
        textImplosion = AnimationUtils.loadAnimation(getContext(), R.anim.text_implosion_animation);
        circleExplosion.setDuration(1000);
        circleImplosion.setDuration(1000);
        textExplosion.setDuration(600);
        textImplosion.setDuration(600);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Inizializzazione delle variabili
        fab = getActivity().findViewById(R.id.fab);
        toolbar = getActivity().findViewById(R.id.toolbar);
        drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        notepackLinearLayout = getActivity().findViewById(R.id.notepackLinearLayout);
        fab.show();
        Cursor cursor = DB.getNotepacksNumber();
        cursor.moveToFirst();
        notepacksCounter = cursor.getInt(0);

        //Controlli sulla toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //Settaggio delle Notepacks dal database
        if(notepacksCounter != 0){
            notepackLinearLayout.removeAllViews();
            setNotepacksFromDatabase();
        }

    }

    @Override
    public void onDestroyView () {
        super.onDestroyView();
        binding = null;
    }

    //Settaggio delle Notepacks dal database
    public void setNotepacksFromDatabase(){
        for(int i = 0; i < notepacksCounter; i++) {
            //Inizializzazione delle variabili
            View notepackView = getLayoutInflater().inflate(R.layout.cardview, null);
            notepackLinearLayout.addView(notepackView);
            notepackImage = notepackLinearLayout.getChildAt(i).findViewById(R.id.notepackImage);
            notepackTitle = notepackLinearLayout.getChildAt(i).findViewById(R.id.notepackTitle);
            itemsTaken = notepackLinearLayout.getChildAt(i).findViewById(R.id.cardviewItemsTaken);
            Cursor cursor2 = null;
            Cursor cursor3 = null;
            Cursor cursor4 = DB.getNotepacksItemsTaken(i);
            Cursor cursor5 = DB.getItemsNumber(i);
            cursor4.moveToFirst();
            cursor5.moveToFirst();

            //Controllo sull'ordinamento delle Notepacks
            if(MainActivity.orderNotepacksById) {
                cursor2 = DB.getNotepacksTitleOrderedById();
                cursor3 = DB.getNotepacksCategoryOrderedById();
            }else if(MainActivity.orderNotepacksByCategory){
                cursor2 = DB.getNotepacksTitleOrderedByCategory();
                cursor3 = DB.getNotepacksCategoryOrderedByCategory();
            }else if(MainActivity.orderNotepacksByTitle){
                cursor2 = DB.getNotepacksTitleOrderedByTitle();
                cursor3 = DB.getNotepacksCategoryOrderedByTitle();
            }

            itemsTakenCounter = cursor4.getInt(0);
            itemsCounter = cursor5.getInt(0);

            //Settaggio del testo degli items presi della Notepack
            itemsTaken.setText(new StringBuilder().append(getString(R.string.items_taken_text)).append(itemsTakenCounter).append(getString(R.string.items_taken_text2)).append(itemsCounter).toString());

            //Settaggio del titolo della Notepack
            if(cursor2.moveToPosition(i)){
                notepackTitle.setText(cursor2.getString(0));
            }

            //Settaggio dell'immagine della categoria della Notepack
            if(cursor3.moveToPosition(i)){
                if (cursor3.getString(0).equals("Vacanza")) {
                    notepackImage.setImageResource(R.drawable.vacation);
                }else if(cursor3.getString(0).equals("Viaggio di lavoro")){
                    notepackImage.setImageResource(R.drawable.business_trip);
                }else if(cursor3.getString(0).equals("Viaggio d'istruzione")){
                    notepackImage.setImageResource(R.drawable.school_trip);
                }else if(cursor3.getString(0).equals("Personalizzato")){
                    notepackImage.setImageResource(R.drawable.custom);
                }
            }

            //Settaggio della rimozione della Notepack
            manageNotepack(i, cursor2, cursor3);
        }

    }

    //Rimozione della Notepack
    public void manageNotepack(int i, Cursor cursor1, Cursor cursor2){
        notepackLinearLayout.getChildAt(i).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Inizializzazione delle variabili
                deleteNotepackFab = view.findViewById(R.id.deleteNotepackBtn);
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast, null);
                TextView text = (TextView) layout.findViewById(R.id.text);
                Toast toast = new Toast(getContext());
                toast.setView(layout);

                //Controlli sulla pressione del pulsante di rimozione della Notepack
                if(deleteNotepackFab.getVisibility() == View.INVISIBLE) {
                    deleteNotepackFab.setVisibility(View.VISIBLE);
                    deleteNotepackFab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            View view1 = (View) view.getParent();
                            if (DB.deleteNotepacksDetails(i)) {
                                text.setText(getString(R.string.notepack_deleted));
                                toast.show();
                                DB.decreaseNotepacksNumber();
                                view1.animate().translationX(-1000);
                                handler.postDelayed(() -> binding.notepackLinearLayout.removeView((View) view.getParent().getParent()), 300);
                            } else {
                                text.setText(getString(R.string.notepack_deleting_error));
                                toast.show();
                            }
                            Cursor cursor = DB.getNotepacksNumber();
                            cursor.moveToFirst();
                            notepacksCounter = cursor.getInt(0);
                            if(notepacksCounter == 0){
                                View addNotepackHint = getLayoutInflater().inflate(R.layout.add_notepack_text, null);
                                handler.postDelayed(() -> notepackLinearLayout.addView(addNotepackHint), 300);
                            }
                        }
                    });
                }else if(deleteNotepackFab.getVisibility() == View.VISIBLE){
                    deleteNotepackFab.setVisibility(View.INVISIBLE);
                }

                return true;
            }
        });

        //Visualizzazione della Notepack
        notepackLinearLayout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Inizializzazione delle variabili
                deleteNotepackFab = view.findViewById(R.id.deleteNotepackBtn);

                if(deleteNotepackFab.getVisibility() == View.INVISIBLE) {
                    MainActivity.goToNotepackFragmentCheck = true;
                    MainActivity.newNotepackCreated = false;
                    MainActivity.circleNotepack.startAnimation(circleExplosion);
                    MainActivity.animationText.setText(cursor1.getString(0));
                    MainActivity.notepackCategory = cursor2.getString(0);
                    handler.postDelayed(() -> MainActivity.animationText.setVisibility(View.VISIBLE), 400);
                    handler.postDelayed(() -> MainActivity.animationText.startAnimation(textExplosion), 400);
                    handler.postDelayed(() -> MainActivity.animationText.startAnimation(textImplosion), 1200);
                    handler.postDelayed(() -> MainActivity.circleNotepack.startAnimation(circleImplosion), 1000);
                    MainActivity.fab.hide();
                    MainActivity.fabVacation.hide();
                    MainActivity.fabBusinessTrip.hide();
                    MainActivity.fabSchoolTrip.hide();
                    MainActivity.fabCustom.hide();
                    handler.postDelayed(() -> MainActivity.item.setVisible(false), 1000);
                    handler.postDelayed(() -> MainActivity.createNotepack.setVisibility(View.GONE), 1000);
                    handler.postDelayed(() -> Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_create_notepack), 1000);
                    handler.postDelayed(() -> MainActivity.animationText.setVisibility(View.GONE), 1200);
                }
            }
        });
    }
}