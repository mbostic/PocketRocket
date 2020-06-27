package com.mbostic.rocket;

import com.badlogic.gdx.math.MathUtils;

public enum GAME_MODE{

    TRAINING("Tutorial", 0, 0, 0),
    EASY("Easy", 0, 0, 0.5f),
    THROUGH_WALL("Through Wall", 0, 0, 0.9f),
    CLASSIC("Classic", 0, 0, 1),
    RANDOM_CLOUDS_OFFSET("Random Clouds", 200, 40, 1.1f),
    ACCELERATING("Accelerating", 500, 30, 1.2f),
    HIGH_SPEED("High Speed", 1000, 20, 1.4f),
    MOVING_CLOUDS("Moving Clouds", 2000, 10, 1.8f);

    public final String name;
    public final int price,
            probability;
    public final float coinsFactor;
    public boolean unlocked;

    GAME_MODE(String mode, int price, int probability, float coinsFactor){
        name = mode;
        this.price = price;
        this.probability = probability;
        this.coinsFactor = coinsFactor;
        if (!Assets.instance.prefs.has(name)) {
            Assets.instance.prefs.writeInt(name, 0);
            if(!(name.equals("Classic") || name.equals("Through Wall"))
                    || name.equals("Tutorial") || name.equals("Easy"))
                Assets.instance.prefs.writeBoolean(name+"unlocked", false);
        }
        updateUnlocked();
    }

    public static GAME_MODE gamble(){

        int probabilitySum = 0;

        for(GAME_MODE mode : vals){
            probabilitySum += mode.probability;
        }

        int rand = MathUtils.random(probabilitySum);

        probabilitySum = 0;

        GAME_MODE ret = CLASSIC;

        for(GAME_MODE mode : vals){

            probabilitySum += mode.probability;
            if(rand < probabilitySum) {
                ret = mode;
                break;
            }
        }
        return ret;
    }

    public static GAME_MODE[] vals = values();

    public GAME_MODE next() {
        return vals[(this.ordinal()+1) % vals.length];
    }
    public GAME_MODE previous()
    {
        int i =this.ordinal()-1;
        if(i<0)
            i=vals.length-1;

        return vals[i];
    }

    public void updateUnlocked(){

        unlocked = !(name.equals("Classic") || name.equals("Through Wall")
                || name.equals("Tutorial") || name.equals("Easy")) ?
                Assets.instance.prefs.readBoolean(name+"unlocked") : true;

    }

    public int getBestScore(){
        return Assets.instance.prefs.readInt(name);
    }

    public void setBestScore(int val) {
        Assets.instance.prefs.writeInt(name, val);
    }

    public boolean isUnlocked(){
        return unlocked;
    }

    public void unlock(){Assets.instance.prefs.writeBoolean(name+"unlocked", true);}

}
