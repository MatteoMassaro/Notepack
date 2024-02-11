package com.example.notepack;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.notepack.databinding.ActivityMainBinding;
import com.example.notepack.ui.notepack.CreateNotepackFragment;
import com.example.notepack.ui.notepack.NotepackFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //Variabili
    private AppBarConfiguration mAppBarConfiguration;

    Animation circleExplosion, textExplosion, circleImplosion, textImplosion;
    CardView cardViewVacation, cardViewBusinessTrip, cardViewSchoolTrip, cardViewCustom, cardViewItalian, cardViewEnglish, cardViewFrench, cardViewSpanish, cardViewGerman, cardViewInstagram, cardviewEmail, cardViewWebsite;
    DrawerLayout drawerLayout;
    Handler handler;
    LinearLayout notepackLinearLayout;
    Menu drawerMenu;
    NavController navController;
    NavigationView navigationView;
    Toolbar toolbar;
    View circleVacation, circleBusinessTrip, circleSchoolTrip, circleCustom;

    ImageView imageCheckItalian, imageCheckEnglish, imageCheckFrench, imageCheckSpanish, imageCheckGerman;

    int fabVisibilityCounter;

    public static Boolean goToNotepackFragmentCheck, newNotepackCreated, orderNotepacksById, orderNotepacksByCategory, orderNotepacksByTitle;
    public static FloatingActionButton fab, fabVacation, fabBusinessTrip, fabSchoolTrip, fabCustom;
    public static MenuItem item;
    public static String notepackCategory;
    public static TextView animationText, createNotepack;
    public static View circleNotepack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.notepack.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Inizializzazione delle variabili
        drawerLayout = findViewById(R.id.drawer_layout);
        notepackLinearLayout = findViewById(R.id.notepackLinearLayout);
        navigationView = findViewById(R.id.nav_view);
        createNotepack = findViewById(R.id.createNotepack);
        circleNotepack = binding.appBarMain.circleNotepack;
        animationText = binding.appBarMain.categoryText;
        fab = binding.appBarMain.fab;
        fabVacation = binding.appBarMain.fabVacation;
        fabBusinessTrip = binding.appBarMain.fabBusinessTrip;
        fabSchoolTrip = binding.appBarMain.fabSchoolTrip;
        fabCustom = binding.appBarMain.fabCustom;
        circleVacation = binding.appBarMain.circleVacation;
        circleBusinessTrip = binding.appBarMain.circleBusinessTrip;
        circleSchoolTrip = binding.appBarMain.circleSchoolTrip;
        circleCustom = binding.appBarMain.circleCustom;
        toolbar = binding.appBarMain.toolbar;
        handler = new Handler();
        fabVisibilityCounter = 0;
        goToNotepackFragmentCheck = false;
        orderNotepacksById = true;
        orderNotepacksByCategory = false;
        orderNotepacksByTitle = false;

        //Settaggio delle animazioni
        circleExplosion = AnimationUtils.loadAnimation(this, R.anim.circle_explosion_animation);
        textExplosion = AnimationUtils.loadAnimation(this, R.anim.text_explosion_animation);
        circleImplosion = AnimationUtils.loadAnimation(this, R.anim.circle_implosion_animation);
        textImplosion = AnimationUtils.loadAnimation(this, R.anim.text_implosion_animation);
        circleExplosion.setDuration(1000);
        circleImplosion.setDuration(1000);
        textExplosion.setDuration(600);
        textImplosion.setDuration(600);

        //Settaggio della toolbar
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.toolbar.setTitleTextAppearance(this,R.style.robotoSlabTextAppearance);

        //Inizializzazione del navigation drawer
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_create_notepack, R.id.nav_my_notepacks, R.id.nav_info).setOpenableLayout(drawer).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        drawerMenu = navigationView.getMenu();

        //Settaggio del navigation drawer
        setNavigationDrawer();

        //Settaggio della lingua salvata
        setSavedLanguage();

        //Settaggio dell'animazione dei floating action buttons
        animateFABS();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inizializzazione dell'item del menu
        getMenuInflater().inflate(R.menu.main, menu);
        item = menu.findItem(R.id.order_by);

        //Controllo del click dell'item del menu
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //Settaggio del menu popup per l'ordinamento delle Notepacks
                setPopupMenu();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        //Reindirizzamento alla schermata precedente
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        //Inizializzazione delle variabili
        fabVisibilityCounter = 0;
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);

        //Controllo del salvataggio della Notepack
        if (navHostFragment != null) {
            if(navHostFragment.getChildFragmentManager().getBackStackEntryCount() > 0) {
                if(goToNotepackFragmentCheck && !CreateNotepackFragment.saved) {
                    //Settaggio della finestra di avviso del salvataggio della Notepack
                    setAlertDialog();
                }else{
                    navController.navigateUp();
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            }else{
                super.onBackPressed();
            }
        }

        //Chiusura del drawer menu
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

    }

    //Settaggio dell'animazione dei floating action buttons
    public void animateFABS(){
        //Animazione del FAB principale
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fabVisibilityCounter == 0) {
                    fabVacation.show();
                    fabBusinessTrip.show();
                    fabSchoolTrip.show();
                    fabCustom.show();
                    fabVisibilityCounter++;
                }else{
                    fabVacation.hide();
                    fabBusinessTrip.hide();
                    fabSchoolTrip.hide();
                    fabCustom.hide();
                    fabVisibilityCounter--;
                }

                //Animazione del FAB Vacanza
                fabVacation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToNotepackFragmentCheck = true;
                        newNotepackCreated = true;
                        circleVacation.startAnimation(circleExplosion);
                        animationText.setText(getString(R.string.vacation_notepack));
                        notepackCategory = "Vacanza";
                        handler.postDelayed(() -> animationText.setVisibility(View.VISIBLE), 400);
                        handler.postDelayed(() -> animationText.startAnimation(textExplosion), 400);
                        handler.postDelayed(() -> animationText.startAnimation(textImplosion), 1200);
                        handler.postDelayed(() -> circleVacation.startAnimation(circleImplosion), 1000);
                        fab.hide();
                        fabVacation.hide();
                        fabBusinessTrip.hide();
                        fabSchoolTrip.hide();
                        fabCustom.hide();
                        handler.postDelayed(() -> item.setVisible(false), 1000);
                        handler.postDelayed(() -> createNotepack.setVisibility(View.GONE), 1000);
                        handler.postDelayed(() -> Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_create_notepack), 1000);
                        handler.postDelayed(() -> animationText.setVisibility(View.GONE), 1200);
                    }
                });

                //Animazione del FAB Viaggio di lavoro
                fabBusinessTrip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToNotepackFragmentCheck = true;
                        newNotepackCreated = true;
                        circleBusinessTrip.startAnimation(circleExplosion);
                        animationText.setText(getString(R.string.business_trip_notepack));
                        notepackCategory = "Viaggio di lavoro";
                        handler.postDelayed(() -> animationText.setVisibility(View.VISIBLE), 400);
                        handler.postDelayed(() -> animationText.startAnimation(textExplosion), 400);
                        handler.postDelayed(() -> animationText.startAnimation(textImplosion), 1200);
                        handler.postDelayed(() -> circleBusinessTrip.startAnimation(circleImplosion), 1000);
                        fab.hide();
                        fabVacation.hide();
                        fabBusinessTrip.hide();
                        fabSchoolTrip.hide();
                        fabCustom.hide();
                        handler.postDelayed(() -> item.setVisible(false), 1000);
                        handler.postDelayed(() -> createNotepack.setVisibility(View.GONE), 1000);
                        handler.postDelayed(() -> Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_create_notepack), 1000);
                        handler.postDelayed(() -> animationText.setVisibility(View.GONE), 1200);
                    }
                });

                //Animazione del FAB Viaggio d'istruzione
                fabSchoolTrip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToNotepackFragmentCheck = true;
                        newNotepackCreated = true;
                        circleSchoolTrip.startAnimation(circleExplosion);
                        animationText.setText(getString(R.string.school_trip_notepack));
                        notepackCategory = "Viaggio d'istruzione";
                        handler.postDelayed(() -> animationText.setVisibility(View.VISIBLE), 400);
                        handler.postDelayed(() -> animationText.startAnimation(textExplosion), 400);
                        handler.postDelayed(() -> animationText.startAnimation(textImplosion), 1200);
                        handler.postDelayed(() -> circleSchoolTrip.startAnimation(circleImplosion), 1000);
                        fab.hide();
                        fabVacation.hide();
                        fabBusinessTrip.hide();
                        fabSchoolTrip.hide();
                        fabCustom.hide();
                        handler.postDelayed(() -> item.setVisible(false), 1000);
                        handler.postDelayed(() -> createNotepack.setVisibility(View.GONE), 1000);
                        handler.postDelayed(() -> Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_create_notepack), 1000);
                        handler.postDelayed(() -> animationText.setVisibility(View.GONE), 1200);
                    }
                });

                //Animazione del FAB personalizzato
                fabCustom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToNotepackFragmentCheck = true;
                        newNotepackCreated = true;
                        circleCustom.startAnimation(circleExplosion);
                        animationText.setText(getString(R.string.custom_notepack));
                        notepackCategory = "Personalizzato";
                        handler.postDelayed(() -> animationText.setVisibility(View.VISIBLE), 400);
                        handler.postDelayed(() -> animationText.startAnimation(textExplosion), 400);
                        handler.postDelayed(() -> animationText.startAnimation(textImplosion), 1200);
                        handler.postDelayed(() -> circleCustom.startAnimation(circleImplosion), 1000);
                        fab.hide();
                        fabVacation.hide();
                        fabBusinessTrip.hide();
                        fabSchoolTrip.hide();
                        fabCustom.hide();
                        handler.postDelayed(() -> item.setVisible(false), 1000);
                        handler.postDelayed(() -> createNotepack.setVisibility(View.GONE), 1000);
                        handler.postDelayed(() -> Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_create_notepack), 1000);
                        handler.postDelayed(() -> animationText.setVisibility(View.GONE), 1200);
                    }
                });
            }
        });

    }

    //Settaggio del navigation drawer
    public void setNavigationDrawer(){
        //Controlli sulla pressione degli item del navigation drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_my_notepacks: {
                        goToNotepackFragmentCheck = false;
                        Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_my_notepacks);

                        //Chiusura del drawer menu
                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        }

                        fab.show();
                        fabVisibilityCounter = 0;

                        navigationView.getMenu().getItem(0).setChecked(true);

                        break;
                    }
                    case R.id.nav_create_notepack: {
                        goToNotepackFragmentCheck = true;

                        //Settaggio della finestra per la creazione della Notepack
                        setCreateNotepackDialogMenu();

                        //Chiusura del drawer menu
                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        }

                        navigationView.getMenu().getItem(0).setChecked(true);

                        break;

                    }case R.id.nav_language: {
                        goToNotepackFragmentCheck = true;

                        //Settaggio della finestra per il settaggio della lingua
                        setLanguageDialogMenu();

                        //Chiusura del drawer menu
                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        }

                        navigationView.getMenu().getItem(0).setChecked(true);

                        break;

                    }case R.id.nav_contacts: {
                        goToNotepackFragmentCheck = true;

                        //Settaggio della finestra dei contatti
                        setContactsDialogMenu();

                        //Chiusura del drawer menu
                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        }

                        navigationView.getMenu().getItem(0).setChecked(true);

                        break;

                    }
                    case R.id.nav_info: {
                        goToNotepackFragmentCheck = true;

                        //Settaggio della finestra delle info dell'applicazione
                        setInfoDialogMenu();

                        //Chiusura del drawer menu
                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        }

                        navigationView.getMenu().getItem(0).setChecked(true);

                        break;
                    }
                }
                return true;
            }
        });

    }

    //Settaggio del menu popup per l'ordinamento delle Notepacks
    @SuppressLint("RestrictedApi")
    public void setPopupMenu(){
        //Inizializzazione delle variabili
        NotepackFragment notepackFragment = new NotepackFragment();
        MenuInflater inflater = new MenuInflater(this);
        @SuppressLint("RestrictedApi") MenuBuilder menuBuilder = new MenuBuilder(this);
        @SuppressLint("RestrictedApi") MenuPopupHelper menuPopupHelper = new MenuPopupHelper(MainActivity.this, menuBuilder, toolbar.findViewById(R.id.order_by));
        menuPopupHelper.setForceShowIcon(true);
        inflater.inflate(R.menu.order_popup_menu, menuBuilder);

        //Visualizzazione dell'icona di selezione in base all'item selezionato del menu popup
        if (orderNotepacksByCategory) {
            menuBuilder.getItem(0).setIcon(R.drawable.ic_baseline_check_24);
        } else if (orderNotepacksById) {
            menuBuilder.getItem(1).setIcon(R.drawable.ic_baseline_check_24);
        } else if (orderNotepacksByTitle) {
            menuBuilder.getItem(2).setIcon(R.drawable.ic_baseline_check_24);
        }

        //Controlli sull'item selezionato del menu popup
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.order_by_category:
                        orderNotepacksById = false;
                        orderNotepacksByCategory = true;
                        orderNotepacksByTitle = false;

                        notepackFragment.setNotepacksFromDatabase();
                        Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_my_notepacks);

                        break;

                    case R.id.order_by_date:
                        orderNotepacksById = true;
                        orderNotepacksByCategory = false;
                        orderNotepacksByTitle = false;

                        notepackFragment.setNotepacksFromDatabase();
                        Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_my_notepacks);

                        break;

                    case R.id.order_by_title:
                        orderNotepacksById = false;
                        orderNotepacksByCategory = false;
                        orderNotepacksByTitle = true;

                        notepackFragment.setNotepacksFromDatabase();
                        Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_my_notepacks);

                        break;
                }
                return false;
            }

            @Override
            public void onMenuModeChange(@NonNull MenuBuilder menu) {
                //Nessuna azione
            }
        });

        //Visualizzazione del menu popup per l'ordinamento delle Notepacks
        menuPopupHelper.show();

    }

    //Settaggio della finestra per la creazione della Notepack
    public void setCreateNotepackDialogMenu(){
        //Inizializzazione delle variabili
        Dialog menuDialog = new Dialog(this);
        menuDialog.setContentView(R.layout.create_notepack_dialog);
        menuDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        menuDialog.show();
        cardViewVacation = menuDialog.findViewById(R.id.cardviewVacation);
        cardViewBusinessTrip = menuDialog.findViewById(R.id.cardviewBusinessTrip);
        cardViewSchoolTrip = menuDialog.findViewById(R.id.cardviewSchoolTrip);
        cardViewCustom = menuDialog.findViewById(R.id.cardviewCustom);

        //Animazione sulla selezione della Notepack di tipo Vacanza
        cardViewVacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNotepackFragmentCheck = true;
                newNotepackCreated = true;
                circleNotepack.startAnimation(circleExplosion);
                animationText.setText(getString(R.string.vacation_notepack));
                notepackCategory = "Vacanza";
                handler.postDelayed(() -> animationText.setVisibility(View.VISIBLE), 400);
                handler.postDelayed(() -> animationText.startAnimation(textExplosion), 400);
                handler.postDelayed(() -> animationText.startAnimation(textImplosion), 1200);
                handler.postDelayed(() -> circleNotepack.startAnimation(circleImplosion), 1000);
                fab.hide();
                fabVacation.hide();
                fabBusinessTrip.hide();
                fabSchoolTrip.hide();
                fabCustom.hide();
                handler.postDelayed(() -> menuDialog.hide(), 100);
                handler.postDelayed(() -> createNotepack.setVisibility(View.GONE), 1000);
                handler.postDelayed(() -> Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_create_notepack), 1000);
                handler.postDelayed(() -> animationText.setVisibility(View.GONE), 1200);
            }
        });

        //Animazione sulla selezione della Notepack di tipo Viaggio di lavoro
        cardViewBusinessTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNotepackFragmentCheck = true;
                newNotepackCreated = true;
                circleNotepack.startAnimation(circleExplosion);
                animationText.setText(getString(R.string.business_trip_notepack));
                notepackCategory = "Viaggio di lavoro";
                handler.postDelayed(() -> animationText.setVisibility(View.VISIBLE), 400);
                handler.postDelayed(() -> animationText.startAnimation(textExplosion), 400);
                handler.postDelayed(() -> animationText.startAnimation(textImplosion), 1200);
                handler.postDelayed(() -> circleNotepack.startAnimation(circleImplosion), 1000);
                fab.hide();
                fabVacation.hide();
                fabBusinessTrip.hide();
                fabSchoolTrip.hide();
                fabCustom.hide();
                handler.postDelayed(() -> menuDialog.hide(), 100);
                handler.postDelayed(() -> createNotepack.setVisibility(View.GONE), 1000);
                handler.postDelayed(() -> Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_create_notepack), 1000);
                handler.postDelayed(() -> animationText.setVisibility(View.GONE), 1200);
            }
        });

        //Animazione sulla selezione della Notepack di tipo Viaggio d'istruzione
        cardViewSchoolTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNotepackFragmentCheck = true;
                newNotepackCreated = true;
                circleNotepack.startAnimation(circleExplosion);
                animationText.setText(getString(R.string.school_trip_notepack));
                notepackCategory = "Viaggio d'istruzione";
                handler.postDelayed(() -> animationText.setVisibility(View.VISIBLE), 400);
                handler.postDelayed(() -> animationText.startAnimation(textExplosion), 400);
                handler.postDelayed(() -> animationText.startAnimation(textImplosion), 1200);
                handler.postDelayed(() -> circleNotepack.startAnimation(circleImplosion), 1000);
                fab.hide();
                fabVacation.hide();
                fabBusinessTrip.hide();
                fabSchoolTrip.hide();
                fabCustom.hide();
                handler.postDelayed(() -> menuDialog.hide(), 100);
                handler.postDelayed(() -> createNotepack.setVisibility(View.GONE), 1000);
                handler.postDelayed(() -> Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_create_notepack), 1000);
                handler.postDelayed(() -> animationText.setVisibility(View.GONE), 1200);
            }
        });

        //Animazione sulla selezione della Notepack di tipo Personalizzato
        cardViewCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNotepackFragmentCheck = true;
                newNotepackCreated = true;
                circleNotepack.startAnimation(circleExplosion);
                animationText.setText(getString(R.string.custom_notepack));
                notepackCategory = "Personalizzato";
                handler.postDelayed(() -> animationText.setVisibility(View.VISIBLE), 400);
                handler.postDelayed(() -> animationText.startAnimation(textExplosion), 400);
                handler.postDelayed(() -> animationText.startAnimation(textImplosion), 1200);
                handler.postDelayed(() -> circleNotepack.startAnimation(circleImplosion), 1000);
                fab.hide();
                fabVacation.hide();
                fabBusinessTrip.hide();
                fabSchoolTrip.hide();
                fabCustom.hide();
                handler.postDelayed(() -> menuDialog.hide(), 100);
                handler.postDelayed(() -> createNotepack.setVisibility(View.GONE), 1000);
                handler.postDelayed(() -> Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_create_notepack), 1000);
                handler.postDelayed(() -> animationText.setVisibility(View.GONE), 1200);
            }
        });

    }

    //Settaggio della finestra per il settaggio della lingua
    public void setLanguageDialogMenu(){
        //Inizializzazione delle variabili
        Dialog menuDialog = new Dialog(this);
        menuDialog.setContentView(R.layout.language_dialog);
        menuDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        menuDialog.show();
        SharedPreferences sharedPreferences = getSharedPreferences("Selected language", MODE_PRIVATE);
        String languageCode = sharedPreferences.getString("Language","it");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        cardViewItalian = menuDialog.findViewById(R.id.cardviewItalian);
        cardViewEnglish = menuDialog.findViewById(R.id.cardviewEnglish);
        cardViewFrench = menuDialog.findViewById(R.id.cardviewFrench);
        cardViewSpanish = menuDialog.findViewById(R.id.cardviewSpanish);
        cardViewGerman = menuDialog.findViewById(R.id.cardviewGerman);
        imageCheckItalian = menuDialog.findViewById(R.id.imageCheckItalian);
        imageCheckEnglish = menuDialog.findViewById(R.id.imageCheckEnglish);
        imageCheckFrench = menuDialog.findViewById(R.id.imageCheckFrench);
        imageCheckSpanish = menuDialog.findViewById(R.id.imageCheckSpanish);
        imageCheckGerman = menuDialog.findViewById(R.id.imageCheckGerman);

        switch(languageCode){
            case "it":
                imageCheckItalian.setVisibility(View.VISIBLE);
                break;
            case "en":
                imageCheckEnglish.setVisibility(View.VISIBLE);
                break;
            case "fr":
                imageCheckFrench.setVisibility(View.VISIBLE);
                break;
            case "es":
                imageCheckSpanish.setVisibility(View.VISIBLE);
                break;
            case "de":
                imageCheckGerman.setVisibility(View.VISIBLE);
                break;
        }

        //Settaggio della lingua italiana
        cardViewItalian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("Language", "it");
                editor.apply();
                setLanguage("it");
                Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_my_notepacks);
                menuDialog.hide();
                imageCheckItalian.setVisibility(View.VISIBLE);
                imageCheckEnglish.setVisibility(View.GONE);
                imageCheckFrench.setVisibility(View.GONE);
                imageCheckSpanish.setVisibility(View.GONE);
                imageCheckGerman.setVisibility(View.GONE);
            }
        });

        //Settaggio della lingua inglese
        cardViewEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("Language", "en");
                editor.apply();
                setLanguage("en");
                Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_my_notepacks);
                menuDialog.hide();
                imageCheckItalian.setVisibility(View.GONE);
                imageCheckEnglish.setVisibility(View.VISIBLE);
                imageCheckFrench.setVisibility(View.GONE);
                imageCheckSpanish.setVisibility(View.GONE);
                imageCheckGerman.setVisibility(View.GONE);
            }
        });

        //Settaggio della lingua francese
        cardViewFrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("Language", "fr");
                editor.apply();
                setLanguage("fr");
                Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_my_notepacks);
                menuDialog.hide();
                imageCheckItalian.setVisibility(View.GONE);
                imageCheckEnglish.setVisibility(View.GONE);
                imageCheckFrench.setVisibility(View.VISIBLE);
                imageCheckSpanish.setVisibility(View.GONE);
                imageCheckGerman.setVisibility(View.GONE);
            }
        });

        //Settaggio della lingua spagnola
        cardViewSpanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("Language", "es");
                editor.apply();
                setLanguage("es");
                Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_my_notepacks);
                menuDialog.hide();
                imageCheckItalian.setVisibility(View.GONE);
                imageCheckEnglish.setVisibility(View.GONE);
                imageCheckFrench.setVisibility(View.GONE);
                imageCheckSpanish.setVisibility(View.VISIBLE);
                imageCheckGerman.setVisibility(View.GONE);
            }
        });

        //Settaggio della lingua tedesca
        cardViewGerman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("Language", "de");
                editor.apply();
                setLanguage("de");
                Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_my_notepacks);
                menuDialog.hide();
                imageCheckItalian.setVisibility(View.GONE);
                imageCheckEnglish.setVisibility(View.GONE);
                imageCheckFrench.setVisibility(View.GONE);
                imageCheckSpanish.setVisibility(View.GONE);
                imageCheckGerman.setVisibility(View.VISIBLE);
            }
        });

    }

    //Settaggio della finestra dei contatti
    public void setContactsDialogMenu(){
        //Inizializzazione delle variabili
        Dialog menuDialog = new Dialog(this);
        menuDialog.setContentView(R.layout.contacts_dialog);
        menuDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        menuDialog.show();
        cardViewInstagram = menuDialog.findViewById(R.id.cardviewInstagram);
        cardviewEmail = menuDialog.findViewById(R.id.cardviewEmail);
        cardViewWebsite = menuDialog.findViewById(R.id.cardviewWebsite);

        //Settaggio del collegamento a Instagram
        cardViewInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri profile = Uri.parse("http://instagram.com/_u/matteo__massaro");
                Intent instagramLink = new Intent(Intent.ACTION_VIEW, profile);

                instagramLink.setPackage("com.instagram.android");

                try {
                    startActivity(instagramLink);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/matteo__massaro")));
                }
                menuDialog.hide();
            }
        });

        //Settaggio del collegamento all'email
        cardviewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SENDTO);
                String subject = "massaromatteo21@gmail.com";
                Uri data = Uri.parse("mailto:" + subject);
                email.setData(data);
                startActivity(email);
                menuDialog.hide();
            }
        });

        //Settaggio del collegamento al sito web
        cardViewWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent website = new Intent(Intent.ACTION_VIEW);
                Uri link = Uri.parse("https://matteomassaro.altervista.org/");
                website.setData(link);
                startActivity(website);
                menuDialog.hide();
            }
        });

    }

    //Settaggio della finestra dei contatti
    public void setInfoDialogMenu(){
        //Inizializzazione delle variabili
        Dialog menuDialog = new Dialog(this);
        menuDialog.setContentView(R.layout.info_dialog);
        menuDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        menuDialog.show();

    }

    //Settaggio della lingua in base alla scelta dell'utente
    public void setLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.activity_main_drawer);

    }

    //Settaggio della lingua salvata
    public void setSavedLanguage(){
        SharedPreferences sharedPreferences = getSharedPreferences("Selected language", MODE_PRIVATE);
        String languageCode = sharedPreferences.getString("Language","it");
        setLanguage(languageCode);
        Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_my_notepacks);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.activity_main_drawer);
    }

    //Settaggio della finestra di avviso del salvataggio della Notepack
    public void setAlertDialog(){
        //Inizializzazione delle variabili
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.warning_dialog, findViewById(R.id.layoutDialogContainer));
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText(R.string.warningTitle);
        ((TextView) view.findViewById(R.id.textMessage)).setText(R.string.warningMessage);
        ((Button) view.findViewById(R.id.buttonYes)).setText(R.string.yes);
        ((Button) view.findViewById(R.id.buttonNo)).setText(R.string.no);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_baseline_warning_24);

        //Creazione della finestra di avviso del salvataggio della Notepack
        final AlertDialog alertDialog = builder.create();

        //Controlli sulla pressione del pulsante di conferma dell'uscita dalla Notepack
        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNotepackFragmentCheck = false;
                alertDialog.dismiss();
                navController.navigateUp();
                navigationView.getMenu().getItem(0).setChecked(true);
            }
        });

        ////Controlli sulla pressione del pulsante di annullamento dell'uscita dalla Notepack
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        //Visualizzazione della finestra di avviso del salvataggio della Notepack
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

}
