package com.solutionchallenge.factchecker.api.Member.entity;

public enum Tier {
    SEED("씨앗"),
    SPROUT("새싹"),
    SAPLING("묘목"),
    TREE("나무"),
    FOREST("숲");

    private final String tier;

    Tier(String tier) {
        this.tier = tier;
    }

    public String getTier(){return tier; }

    public static Tier getTier(String tier){
        for (Tier tier1 : Tier.values()){
            if (tier1.tier.equalsIgnoreCase(tier)){
                return tier1;
            }
        }
        return null;
    }

}