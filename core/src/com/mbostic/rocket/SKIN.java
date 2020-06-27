package com.mbostic.rocket;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public enum SKIN{

    DEFAULT("Default", "atlases/default.atlas", 42, false, 0, 40,
            RocketMain.newColor(140, 190, 255),
            RocketMain.newColor(240, 240, 240)),

    RETRO_ROCKET_1("Retro rocket 1", "atlases/retro1.atlas", 35, true, 200, 30,
            RocketMain.newColor(168, 248, 255),
            RocketMain.newColor(127, 127, 127)),

    PENCIL("Pencil", "atlases/pencil.atlas", 0, false, 500, 30,
            RocketMain.newColor(255,246,213),
            RocketMain.newColor(255,204,43));

    public final String name;
    public final boolean isPixelated;
    public final String location;
    public final int price, probability,
            fireHeight;

    public final Color bgColor;
    public Color currColor;
    public Color nextColor;
    public Color previousColor;
    public boolean unlocked;

    SKIN(String name, String location, int fireHeight, boolean isPixelated, int price,
         int probability, Color bgColor, Color defaultSkinColor){
        this.name = name;
        this.fireHeight = fireHeight;
        this.location = location;
        this.price = price;
        this.isPixelated = isPixelated;
        this.bgColor = bgColor;
        this.probability = probability;


        if (!Assets.instance.prefs.has("numColors"+name)) {//kolok barv ma skin
            Assets.instance.prefs.writeInt("numColors"+name, 0);
        }
        if (!Assets.instance.prefs.has("currColor"+name)) {//kolok barv ma skin
            Assets.instance.prefs.writeInt("currColor"+name, 0);
        }

        //	if (!Assets.instance.prefs.has(name+0)) {
        Assets.instance.prefs.writeString(name+0, defaultSkinColor.toString());
        //}

        updateColor();

        //is skin unlocked?
        if (!Assets.instance.prefs.has(name) && !name.equals("Default")) {
            Assets.instance.prefs.writeBoolean(name, false);
        }
        updateUnlocked();
    }

    public static SKIN[] vals = values();

    public boolean isUnlocked(){
        return unlocked;
    }

    public void updateUnlocked(){
        unlocked = !name.equals("Default") ? Assets.instance.prefs.readBoolean(name) : true;
    }

    public void nextColor(){

        int currColorN = Assets.instance.prefs.readInt("currColor"+name);
        int numColors = Assets.instance.prefs.readInt("numColors"+name);

        int nextColorN = currColorN + 1;
        if (nextColorN > numColors)
            nextColorN = 0;

        Assets.instance.prefs.writeInt("currColor"+name, nextColorN);

        updateColor();
    }

    public void prevColor(){

        int currColorN = Assets.instance.prefs.readInt("currColor"+name);
        int numColors = Assets.instance.prefs.readInt("numColors"+name);

        int prevColorN = currColorN - 1;
        if (prevColorN < 0)
            prevColorN = numColors;

        Assets.instance.prefs.writeInt("currColor"+name, prevColorN);

        updateColor();
    }

    public void unlock(){Assets.instance.prefs.writeBoolean(name, true);}

    public void updateColor(){

        int currColorN = Assets.instance.prefs.readInt("currColor"+name);
        int numColors = Assets.instance.prefs.readInt("numColors"+name);

        int nextColorN = currColorN + 1;
        if (nextColorN > numColors)
            nextColorN = 0;

        int prevColorN = currColorN - 1;
        if (prevColorN < 0)
            prevColorN = numColors;

        currColor = new Color(Color.valueOf(Assets.instance.prefs.readString(name + currColorN)));
        nextColor = new Color(Color.valueOf(Assets.instance.prefs.readString(name + nextColorN)));
        previousColor = new Color(Color.valueOf(Assets.instance.prefs.readString(name + prevColorN)));
    }


    public SKIN next()
    {
        return vals[(this.ordinal()+1) % vals.length];
    }

    public static SKIN gamble(){

        int probabilitySum = 0;

        for(SKIN skin : vals){
            probabilitySum += skin.probability;
        }

        int rand = MathUtils.random(probabilitySum);

        probabilitySum = 0;

        SKIN ret = DEFAULT;

        for(SKIN skin : vals){

            probabilitySum += skin.probability;
            if(rand < probabilitySum) {
                ret = skin;
                break;
            }
        }

        return ret;
    }

    public void saveNewColor(Color color){

        int numColors = Assets.instance.prefs.readInt("numColors"+name);
        Assets.instance.prefs.writeInt("numColors"+name, ++numColors);
        Assets.instance.prefs.writeString(name+numColors, color.toString());
        updateColor();

    }

    public static Color randomColor(){

        float a;

        if(Math.random() < 0.85)
            a = 1;
        else
            a = MathUtils.random(0.2f, 0.7f);

        return new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), a);
    }

    public SKIN previous(){
        int i =this.ordinal()-1;
        if(i<0)
            i=vals.length-1;

        return vals[i];
    }


}
