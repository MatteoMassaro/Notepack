package com.example.notepack.ui.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "Notepacks.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        //Creazione delle tabelle del database
        DB.execSQL("create table NotepacksDetails(id INT primary key, title TEXT unique, category TEXT, itemsTaken INT)");
        DB.execSQL("create table NotepacksCounter(number INT primary key)");
        DB.execSQL("create table ItemsCounter(notepackId INT primary key, number INT)");
        DB.execSQL("create table NotepacksItems(itemId INT, notepackId INT, category INT, title TEXT, taken INT, primary key (itemId, notepackId))");

        //Inizializzazione del contatore delle Notepacks
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", 0);
        DB.insert("NotepacksCounter", null, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        //Eliminazione delle tabelle per l'aggiornamento
        DB.execSQL("drop table if exists NotepacksDetails");
        DB.execSQL("drop table if exists NotepacksCounter");
        DB.execSQL("drop table if exists ItemsCounter");
        DB.execSQL("drop table if exists NotepacksItems");
    }

    //Salvataggio delle informazioni delle Notepacks
    public boolean saveNotepacksDetails(int id, String title, String category, int itemsTaken){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("title", title);
        contentValues.put("category", category);
        contentValues.put("itemsTaken", itemsTaken);
        long result = DB.insert("NotepacksDetails", null, contentValues);
        return result != -1;
    }

    //Aggiornamento delle informazioni delle Notepacks
    public boolean updateNotepacksDetails(int id, String title, int itemsTaken){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("itemsTaken", itemsTaken);
        long result = DB.update("NotepacksDetails", contentValues, "id =?", new String[]{String.valueOf(id)});
        return result != -1;
    }

    //Rimozione delle informazioni delle Notepacks
    public boolean deleteNotepacksDetails(int id){
        SQLiteDatabase DB = this.getWritableDatabase();
        long result = DB.delete("NotepacksDetails", "id =?", new String[]{String.valueOf(id)});
        long result1 = DB.delete("NotepacksItems", "notepackId =?",  new String[]{String.valueOf(id)});
        long result2 = DB.delete("ItemsCounter", "notepackId =?",  new String[]{String.valueOf(id)});
        return result != -1 && result1 != -1 && result2 != -1;
    }

    //Salvataggio del numero di items delle Notepacks
    public void saveItemsNumber(int notepackId, int number){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("notepackId", notepackId);
        contentValues.put("number", number);
        DB.insertWithOnConflict("ItemsCounter", null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    //Aggiornamento del numero di items delle Notepacks
    public void updateItemsNumber(int notepackId, int number){
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("update ItemsCounter set number =" + number + " where notepackId =" + notepackId);
    }

    //Salvataggio delle informazioni degli items delle Notepacks
    public boolean saveNotepacksItems(int itemId, int notepackId, int category, String title, int taken){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("itemId", itemId);
        contentValues.put("notepackId", notepackId);
        contentValues.put("title", title);
        contentValues.put("category", category);
        contentValues.put("taken", taken);
        long result = DB.insertWithOnConflict("NotepacksItems", null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        return result != -1;
    }

    //Aggiornamento delle informazioni degli items delle Notepacks
    public void updateNotepacksItems(int itemId, int notepackId, int category, String title, int taken){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("itemId", itemId);
        contentValues.put("notepackId", notepackId);
        contentValues.put("title", title);
        contentValues.put("category", category);
        contentValues.put("taken", taken);
        DB.update("NotepacksItems", contentValues, "itemId =? and notepackid=?", new String[]{String.valueOf(itemId), String.valueOf(notepackId)});
    }

    //Incremento del numero delle Notepacks
    public void increaseNotepacksNumber(){
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("update NotepacksCounter set number = number + 1");
    }

    //Decremento del numero delle Notepacks
    public void decreaseNotepacksNumber(){
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("update NotepacksCounter set number = number - 1");
    }

    //Recupero del numero di Notepacks
    public Cursor getNotepacksNumber(){
        SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("select number from NotepacksCounter",null);
    }

    //Recupero dell'id delle Notepacks
    public Cursor getNotepacksId(String title){
        SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("select id from NotepacksDetails where title =?", new String[]{title});
    }

    //Recupero dei titoli delle Notepacks ordinati per id
    public Cursor getNotepacksTitleOrderedById(){
        SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("select title from NotepacksDetails Order By id",null);
    }

    //Recupero delle categorie delle Notepacks ordinate per id
    public Cursor getNotepacksCategoryOrderedById(){
        SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("select category from NotepacksDetails Order By id",null);
    }

    //Recupero dei titoli delle Notepacks ordinati per categoria
    public Cursor getNotepacksTitleOrderedByCategory(){
        SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("select title from NotepacksDetails Order By category",null);
    }

    //Recupero delle categorie delle Notepacks ordinate per categoria
    public Cursor getNotepacksCategoryOrderedByCategory(){
        SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("select category from NotepacksDetails Order By category",null);
    }

    //Recupero dei titoli delle Notepacks ordinati per titolo
    public Cursor getNotepacksTitleOrderedByTitle(){
        SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("select title from NotepacksDetails Order By title",null);
    }

    //Recupero delle categorie delle Notepacks ordinate per titolo
    public Cursor getNotepacksCategoryOrderedByTitle(){
        SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("select category from NotepacksDetails Order By title",null);
    }

    //Recupero degli items presi delle Notepacks
    public Cursor getNotepacksItemsTaken(int notepackID){
        SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("select itemsTaken from NotepacksDetails where id=?",new String[]{String.valueOf(notepackID)});
    }

    //Recupero del numero di items delle Notepacks
    public Cursor getItemsNumber(int notepackId){
        SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("select number from ItemsCounter where notepackId =" + notepackId,null);
    }

    //Recupero delle categorie degli items
    public Cursor getItemsCategory(int notepackId){
        SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("select category from NotepacksItems where notepackId =" + notepackId + " Order By notepackId, itemId",null);
    }

    //Recupero dei titoli degli items
    public Cursor getItemsTitle(int notepackId){
        SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("select title from NotepacksItems where notepackId =" + notepackId + " Order By notepackId, itemId",null);
    }

    //Recupero degli stati degli switch degli items
    public Cursor getItemsTaken(int notepackId){
        SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("select taken from NotepacksItems where notepackId =" + notepackId + " Order By notepackId, itemId",null);
    }

}
