package com.solutionchallenge.factchecker.api.Youtube.entity;

public enum Category {
    RELATED("related"),
    LATEST("latest");

    private final String category;

    Category(String category) {
        this.category = category;
    }

    public String getCategory(){return category; }

    public static com.solutionchallenge.factchecker.api.Youtube.entity.Category getCategory(String category){
        for (com.solutionchallenge.factchecker.api.Youtube.entity.Category category1 : com.solutionchallenge.factchecker.api.Youtube.entity.Category.values()){
            if (category1.category.equalsIgnoreCase(category)){
                return category1;
            }
        }
        return null;
    }

}
