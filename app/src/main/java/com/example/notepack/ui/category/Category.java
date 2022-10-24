package com.example.notepack.ui.category;

public class Category {

    private final int name, image;

    public Category(int categoryName, int categoryImage){
        name = categoryName;
        image = categoryImage;
    }

    public int getName(){
        return name;
    }

    public int getImage(){
        return image;
    }

}
