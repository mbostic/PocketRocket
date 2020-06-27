package com.mbostic.gamble;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.mbostic.objects.Icon;
import com.mbostic.rocket.Assets;
import com.mbostic.rocket.RocketMain;
import com.mbostic.screens.MenuScreen;

public class Slot{

   public static final int //spodnja leva točka slota
            SLOT_Y_START = 577,
            SLOT_WIDTH = 896,//723
            SLOT_HEIGHT = (int)(SLOT_WIDTH*0.952f),
            SPIN_PRICE = 90;
    Color slotBGColor, selectPrizeColor;
    public SlotIcon[][] iconArray;
    final static int RESISTANCE = 140, STOP_VELOCITY = 220;

    float[] rowXVelocities, slotRowStartX;
    public Icon spin;
    BitmapFont font;
    public boolean allRowsStopped, prizeSelected, stopped;
    float selectPrizeVel, selectPrizePos, prizeSelectedAnimationTimer;
    public int selectedPrizeRow, leftEdge, slotXStart;
    GlyphLayout layout;
    final MenuScreen menuScreen;
    //float volume;
    //long soundId;

    public Slot(MenuScreen menuScreen){

        this.menuScreen = menuScreen;
        slotBGColor = RocketMain.newColor(211,205,194);
        selectPrizeColor = RocketMain.newColor(255,255,180,0.4f);
        iconArray = new SlotIcon[3][6];
        //začetne pozicije vrstic

        rowXVelocities = new float[]{0, 0, 0};

        font = Assets.instance.getFont(0.7f);

        layout = new GlyphLayout();

    }

    public void resize(){

        leftEdge = -813 - menuScreen.settingsYOffset - menuScreen.xCut;
        slotXStart = leftEdge + 41;

        slotRowStartX = new float[]{slotXStart, slotXStart, slotXStart};
        spin = new Icon(Assets.instance.slotButton, Assets.instance.slotButtonPressed,
               leftEdge + 122, 440, 100, 100);
    }

    public void render(float deltaTime, SpriteBatch batch){

        renderSlotWindow(batch);
        if(allRowsStopped && !stopped)
            renderSelectPrize(deltaTime, batch);

        batch.draw(Assets.instance.slot, leftEdge - 85, 400, SLOT_WIDTH, SLOT_HEIGHT);
        font.getData().setScale(0.8f);
        MenuScreen.drawCoins(batch, layout, font, leftEdge + RocketMain.WIDTH/2, 50 + menuScreen.bannerAdHeight, ""+MenuScreen.coins);
        MenuScreen.drawCoins(batch, layout, font, leftEdge + RocketMain.WIDTH*3/4+30, 460, ""+SPIN_PRICE);

        font.getData().setScale(0.7f);
        spin.render(batch);

        if(!allRowsStopped && !stopped)
            updateIcons(deltaTime);
    }

    private void renderSelectPrize(float deltaTime, SpriteBatch batch){

        if(!prizeSelected) {
            if (Math.abs(selectPrizeVel) > 20) {
                selectPrizeVel -= Math.signum(selectPrizeVel) * RESISTANCE * deltaTime;
                selectPrizePos += selectPrizeVel * deltaTime;
            //v kateri vrstici je?
                selectedPrizeRow = 0;

                if(selectPrizePos < SLOT_Y_START){
                    selectPrizeVel = -selectPrizeVel;
                    selectPrizePos = SLOT_Y_START + SlotIcon.HEIGHT;
                }
                else if (selectPrizePos > SLOT_Y_START + SlotIcon.HEIGHT * 3){
                    selectPrizeVel = -selectPrizeVel;
                    selectPrizePos = SLOT_Y_START + SlotIcon.HEIGHT * 2;
                    selectedPrizeRow = 2;
                }

                if(selectPrizePos > SLOT_Y_START + SlotIcon.HEIGHT
                        && selectPrizePos < SLOT_Y_START + SlotIcon.HEIGHT * 2){
                    selectedPrizeRow = 1;
                }
                else if(selectPrizePos > SLOT_Y_START + SlotIcon.HEIGHT * 2){
                    selectedPrizeRow = 2;
                }
            } else {
 //UNLOCK PRIZE
                prizeSelected = true;
                prizeSelectedAnimationTimer = 0;
                iconArray[selectedPrizeRow][3].getPrize();
                if(Assets.instance.soundOn){
                    Assets.instance.prizeSelected.play(0.2f);
                 //   Assets.instance.slotSound.stop();
                }
            }
            batch.setColor(selectPrizeColor);
        }
        else {
            prizeSelectedAnimationTimer += deltaTime;
            batch.setColor(selectPrizeColor.r, selectPrizeColor.g, selectPrizeColor.b,
                    0.48f*(float)Math.sin(Math.PI*prizeSelectedAnimationTimer*1.5f - 0.21f) + 0.5f);

        }

        MenuScreen.simpleDraw(batch, Assets.instance.whitePixel, slotXStart + SlotIcon.WIDTH * 2 ,
                SLOT_Y_START + selectedPrizeRow * SlotIcon.HEIGHT, SlotIcon.WIDTH, SlotIcon.HEIGHT);

        batch.setColor(Color.WHITE);

    }

    private void updateIcons(float deltaTime){

      //  volume -= 0.1f*deltaTime;
       // Assets.instance.slotSound.setVolume(soundId, volume);


        for(int i = 0; i < 3; i++){

            if(Math.abs(rowXVelocities[i]) > 10) {
                rowXVelocities[i] -=
                        Math.signum(rowXVelocities[i]) * RESISTANCE * deltaTime;

                slotRowStartX[i] += rowXVelocities[i] * deltaTime;
            }
            //desna smer
            if(slotRowStartX[i] > slotXStart){

                SlotIcon firstIcon = iconArray[i][iconArray[i].length - 1];

                for (int j = iconArray[i].length - 2; j >= 0; j--){
                    iconArray[i][j + 1] = iconArray[i][j];
                }
                iconArray[i][0] = firstIcon;
               // newIcon(i, 0);

                //ustavi, ce je hitrost premajhna
                if(rowXVelocities[i] < STOP_VELOCITY) {
                    rowXVelocities[i] = 0;
                    checkRowsStopped();
                }
                //vrsta se pomakne na zacetek
                slotRowStartX[i] = slotXStart - SlotIcon.WIDTH ;
            }
            //leva smer
            else if (slotRowStartX[i] < slotXStart - SlotIcon.WIDTH){

                if(rowXVelocities[i] > -STOP_VELOCITY) {
                    rowXVelocities[i] = 0;//morjo se ustavt tako da imajo odvečno ikono na levi
                    checkRowsStopped();
                }
                else {

                    SlotIcon lastIcon = iconArray[i][0];

                    for (int j = 1; j < iconArray[i].length; j++) {
                        iconArray[i][j - 1] = iconArray[i][j];
                    }
                    iconArray[i][iconArray[i].length - 1] = lastIcon;
                    //newIcon(i,iconArray[i].length - 1);

                    slotRowStartX[i] = slotXStart;
                }
            }
        }

    }

    private void checkRowsStopped(){
        boolean allRowsStopped = true;

        for(float rowVelocity : rowXVelocities){
            if (rowVelocity != 0){
                allRowsStopped = false;
                break;
            }
        }
        this.allRowsStopped = allRowsStopped;

        if(allRowsStopped){
            selectPrizeVel = randomVelocity()*0.7f;
            selectPrizePos = SLOT_Y_START + SlotIcon.HEIGHT;
        }
    }

    private float randomVelocity(){

            return MathUtils.randomSign()*MathUtils.random(700, 1200);
    }

    private void renderSlotWindow(SpriteBatch batch){
        //ozadje

        batch.setColor(slotBGColor);

        MenuScreen.simpleDraw(batch, Assets.instance.whitePixel, slotXStart,
                SLOT_Y_START , SlotIcon.WIDTH * 5 + 10, SlotIcon.HEIGHT * 3);

        batch.setColor(Color.WHITE);

        float  x, y = SLOT_Y_START;

        for(int row = 0; row < 3; row++, y += SlotIcon.HEIGHT){

            x =slotRowStartX[row];

            for(int col = 0; col < 6; col++, x += SlotIcon.WIDTH){

                iconArray[row][col].render(batch, x, y);
            }
        }
    }

    public void initIcons(){

        prizeSelected = allRowsStopped = false;
        stopped = true;

        for(int row = 0; row < 3; row++){

            for(int col = 0; col < 6; col++){

               newIcon(row, col);

            }
        }
    }

    private void newIcon(int row, int col){

        switch (GAMBLE.gamble()){

            case SKIN_COLOR:{
                iconArray[row][col] = new SkinColor();
                break;
            }
            case COINS:{
                iconArray[row][col] = new Coins(font);
                break;
            }
            case GAME_MODE:{
                iconArray[row][col] = new GameMode(font);
                break;
            }
            case SKIN: {
                iconArray[row][col] = new Skin();
                break;
            }
        }
    }
    public void gamble(){

        initIcons();
        MenuScreen.coins -= SPIN_PRICE;
        Assets.instance.prefs.writeInt("coins", MenuScreen.coins);

        stopped = false;

        rowXVelocities = new float[]{randomVelocity(), randomVelocity(), randomVelocity()};

        //Assets.instance.playCoinsSound();
        if(Assets.instance.soundOn){
            Assets.insertCoins.play();
           // Assets.insertCoins.setOnCompletionListener(this);
        }


    }

  /*  @Override
    public void onCompletion(Music music) {

        soundId = Assets.instance.slotSound.loop(0.5f);
        volume = 0.5f;
        music.setOnCompletionListener(null);
    }
*/
    private enum GAMBLE {

        SKIN_COLOR(60),
        COINS(30),
        GAME_MODE(20),
        SKIN(4);

        final int probability;

        GAMBLE(int probability){
            this.probability = probability;
        }

        public static GAMBLE gamble(){

            int[] arr = new int[values().length];
            for(int i = 0; i < arr.length; i++){
                arr[i] = values()[i].probability;
            }

            return values()[RocketMain.gamble(arr)];
        }
    }
}
