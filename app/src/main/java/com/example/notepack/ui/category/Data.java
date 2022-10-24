package com.example.notepack.ui.category;

import com.example.notepack.R;

import java.util.ArrayList;

public class Data {

    //Settaggio delle categorie degli items della Notepack
    public static ArrayList<Category> getCategoryList() {
        ArrayList<Category> categoryList = new ArrayList<>();

        Category none = new Category(R.string.none, R.drawable.none);
        categoryList.add(none);

        Category clothes = new Category(R.string.clothes, R.drawable.clothes);
        categoryList.add(clothes);

        Category shoes = new Category(R.string.shoes, R.drawable.shoes);
        categoryList.add(shoes);

        Category personalHygiene = new Category(R.string.personal_hygiene, R.drawable.personal_hygiene);
        categoryList.add(personalHygiene);

        Category foods = new Category(R.string.foods, R.drawable.foods);
        categoryList.add(foods);

        Category health = new Category(R.string.health, R.drawable.health);
        categoryList.add(health);

        Category finances = new Category(R.string.finances, R.drawable.finances);
        categoryList.add(finances);

        Category accessories = new Category(R.string.accessories, R.drawable.accessories);
        categoryList.add(accessories);

        Category electricDevices = new Category(R.string.electric_devices, R.drawable.electric_devices);
        categoryList.add(electricDevices);

        return categoryList;
    }

}
