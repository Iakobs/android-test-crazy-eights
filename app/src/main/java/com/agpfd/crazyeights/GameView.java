package com.agpfd.crazyeights;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author <a mailto="jacobibanez@jacobibanez.com">Jacob Ibáñez Sánchez</a>
 * @since 22/07/2016
 */
public class GameView extends View {

    private final int CARD_W_SCALE = 8;

    private Context myContext;
    private List<Card> deck = new ArrayList<>();
    private List<Card> myHand = new ArrayList<>();
    private List<Card> oppHand = new ArrayList<>();
    private List<Card> discardPile = new ArrayList<>();
    private Paint blackPaint;
    private Bitmap cardBack;
    private Bitmap nextCardButton;
    private int scaledCardW;
    private int scaledCardH;
    private int screenW;
    private int screenH;
    private int myScore;
    private int oppScore;
    private int movingCardIdx = -1;
    private int movingX;
    private int movingY;
    private int xOffset;
    private int yOffset;
    private int validRank = 8;
    private int validSuit = 0;
    private float scale;
    private boolean myTurn;
    private ComputerPlayer computerPlayer = new ComputerPlayer();


    public GameView(Context context) {
        super(context);
        myContext = context;
        scale = myContext.getResources().getDisplayMetrics().density;
        xOffset = (int) (30 * scale);
        yOffset = (int) (90 * scale);
        blackPaint = new Paint();
        blackPaint.setAntiAlias(true);
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStyle(Paint.Style.STROKE);
        blackPaint.setTextAlign(Paint.Align.LEFT);
        blackPaint.setTextSize(scale * 15);
    }

    private void initCards() {
        for (int i = 0; i < 4; i++) {
            for (int j = 102; j < 115; j++) {
                int tempId = j + (i * 100);
                Card tempCard = new Card(tempId);
                int resourceId = getResources().getIdentifier("card" + tempId,
                        "drawable", myContext.getPackageName());
                Bitmap tempBitmap = BitmapFactory.decodeResource(myContext.getResources(),
                        resourceId);
                scaledCardW = (screenW / CARD_W_SCALE);
                scaledCardH = (int) (scaledCardW * 1.28);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(tempBitmap,
                        scaledCardW, scaledCardH, false);
                tempCard.setBmp(scaledBitmap);
                deck.add(tempCard);
            }
        }
    }

    private void drawCard(List<Card> handToDraw) {
        handToDraw.add(0, deck.get(0));
        deck.remove(0);
        if (deck.isEmpty()) {
            for (int i = discardPile.size() - 1; i > 0; i--) {
                deck.add(discardPile.get(i));
                discardPile.remove(i);
                Collections.shuffle(deck, new Random());
            }
        }
    }

    private void dealCards() {
        Collections.shuffle(deck, new Random());
        for (int i = 0; i < 7; i++) {
            drawCard(myHand);
            drawCard(oppHand);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawText(myContext.getResources().getString(R.string.computer_score) +
                " " + Integer.toString(oppScore), 10, blackPaint.getTextSize() + 10, blackPaint);
        canvas.drawText(myContext.getResources().getString(R.string.my_score) +
                        " " + Integer.toString(myScore), 10, screenH - blackPaint.getTextSize() - 10,
                blackPaint);
        for (int i = 0; i < oppHand.size(); i++) {
            canvas.drawBitmap(cardBack, i * (scale * 5),
                    blackPaint.getTextSize() + (50 * scale), null);
        }
        canvas.drawBitmap(cardBack, (screenW / 2) - cardBack.getWidth() - 10,
                (screenH / 2) - (cardBack.getHeight() / 2), null);
        if (!discardPile.isEmpty()) {
            canvas.drawBitmap(discardPile.get(0).getBmp(), (screenW / 2) + 10,
                    (screenH / 2) - (cardBack.getHeight() / 2), null);
        }
        if (myHand.size() > 7) {
            canvas.drawBitmap(nextCardButton,
                    screenW - nextCardButton.getWidth() - (30 * scale),
                    screenH - nextCardButton.getHeight() - scaledCardH - (90 * scale),
                    null);
        }
        for (int i = 0; i < myHand.size(); i++) {
            if (i == movingCardIdx) {
                canvas.drawBitmap(myHand.get(i).getBmp(), movingX, movingY, null);
            } else {
                if (i < 7) {
                    canvas.drawBitmap(myHand.get(i).getBmp(), i * (scaledCardW + 5),
                            screenH - scaledCardH - blackPaint.getTextSize() - (50 * scale), null);
                }
            }
        }
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenH = h;
        screenW = w;
        initCards();
        dealCards();
        drawCard(discardPile);
        Bitmap tempBitmap = BitmapFactory.decodeResource(myContext.getResources(),
                R.drawable.card_back);
        cardBack = Bitmap.createScaledBitmap(tempBitmap, scaledCardW, scaledCardH, false);
        validRank = discardPile.get(0).getRank();
        validSuit = discardPile.get(0).getSuit();
        nextCardButton = BitmapFactory.decodeResource(myContext.getResources(),
                R.drawable.arrow_next);
        myTurn = new Random().nextBoolean();
        if (!myTurn) makeComputerPlay();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();
        int X = (int) event.getX();
        int Y = (int) event.getY();
        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:
                if (myTurn) {
                    for (int i = 0; i < 7; i++) {
                        if (X > i * (scaledCardW + 5) &&
                                X < i * (scaledCardW + 5) + scaledCardW &&
                                Y > screenH - scaledCardH - blackPaint.getTextSize() - (50 * scale)) {
                            movingCardIdx = i;
                            movingX = X - xOffset;
                            movingY = Y - yOffset;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                movingX = X - xOffset;
                movingY = Y - yOffset;
                break;
            case MotionEvent.ACTION_UP:
                if (movingCardIdx > -1 &&
                        X > (screenW / 2) - (100 * scale) &&
                        X < (screenW / 2) + (100 * scale) &&
                        Y > (screenH / 2) - (100 * scale) &&
                        Y < (screenH / 2) + (100 * scale) &&
                        (myHand.get(movingCardIdx).getRank() == 8 ||
                                myHand.get(movingCardIdx).getRank() == validRank ||
                                myHand.get(movingCardIdx).getSuit() == validSuit)) {
                    validRank = myHand.get(movingCardIdx).getRank();
                    validSuit = myHand.get(movingCardIdx).getSuit();
                    discardPile.add(0, myHand.get(movingCardIdx));
                    myHand.remove(movingCardIdx);
                    if (myHand.isEmpty()) {
                        //@TODO: 26/07/2016 Handle end of hand
                    } else {
                        if (validRank == 8) {
                            showChooseSuitsDialog();
                        } else {
                            myTurn = false;
                            makeComputerPlay();
                        }
                    }
                }
                if (movingCardIdx == -1 && myTurn &&
                        X > (screenW / 2) - (100 * scale) &&
                        X < (screenW / 2) + (100 * scale) &&
                        Y > (screenH / 2) - (100 * scale) &&
                        Y < (screenH / 2) + (100 * scale)) {
                    if (checkForValidDraw()) {
                        drawCard(myHand);
                    } else {
                        Toast.makeText(myContext, myContext.getString(R.string.valid_play),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                if (myHand.size() > 7 &&
                        X > screenW - nextCardButton.getWidth() - (30 * scale) &&
                        Y > screenH - nextCardButton.getHeight() - scaledCardH - (90 * scale) &&
                        Y < screenH - nextCardButton.getHeight() - scaledCardH - (60 * scale)) {
                    Collections.rotate(myHand, 1);
                }
                movingCardIdx = -1;
                break;
        }
        invalidate();
        return true;
    }

    private void showChooseSuitsDialog() {
        final Dialog chooseSuitDialog = new Dialog(myContext);
        chooseSuitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        chooseSuitDialog.setContentView(R.layout.choose_suit_dialog);
        final Spinner suitSpinner = (Spinner) chooseSuitDialog.findViewById(R.id.suitSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(myContext,
                R.array.suits, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suitSpinner.setAdapter(adapter);
        Button okButton = (Button) chooseSuitDialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validSuit = (suitSpinner.getSelectedItemPosition() + 1) * 100;
                String suitText = "";
                if (validSuit == 100) {
                    suitText = myContext.getResources().getString(R.string.diamonds);
                } else if (validSuit == 200) {
                    suitText = myContext.getResources().getString(R.string.clubs);
                } else if (validSuit == 300) {
                    suitText = myContext.getResources().getString(R.string.hearts);
                } else if (validSuit == 400) {
                    suitText = myContext.getResources().getString(R.string.spades);
                }
                chooseSuitDialog.dismiss();
                Toast.makeText(myContext,
                        myContext.getResources().getString(R.string.you_choose) + " " + suitText,
                        Toast.LENGTH_SHORT).show();
                myTurn = false;
                makeComputerPlay();
            }
        });
        chooseSuitDialog.show();
    }

    private boolean checkForValidDraw() {
        boolean canDraw = true;
        for (int i = 0; i < myHand.size(); i++) {
            int tempId = myHand.get(i).getId();
            int tempRank = myHand.get(i).getRank();
            int tempSuit = myHand.get(i).getSuit();
            if (validSuit == tempSuit || validRank == tempRank ||
                    tempId == 108 || tempId == 208 || tempId == 308 || tempId == 408) {
                canDraw = false;
            }
        }
        return canDraw;
    }

    private void makeComputerPlay() {
        int tempPlay = 0;
        while (tempPlay == 0) {
            tempPlay = computerPlayer.makePlay(oppHand, validSuit, validRank);
            if (tempPlay == 0) drawCard(oppHand);
        }
        if (tempPlay == 108 || tempPlay == 208 || tempPlay == 308 || tempPlay == 408) {
            validRank = 8;
            validSuit = computerPlayer.chooseSuit(oppHand);
            String suitText = "";
            if (validSuit == 100) {
                suitText = myContext.getResources().getString(R.string.diamonds);
            } else if (validSuit == 200) {
                suitText = myContext.getResources().getString(R.string.clubs);
            } else if (validSuit == 300) {
                suitText = myContext.getResources().getString(R.string.hearts);
            } else if (validSuit == 400) {
                suitText = myContext.getResources().getString(R.string.spades);
            }
            Toast.makeText(myContext,
                    myContext.getResources().getString(R.string.you_choose) + " " + suitText,
                    Toast.LENGTH_SHORT).show();
        } else {
            validSuit = Math.round((tempPlay / 100) * 100);
            validRank = tempPlay - validSuit;
        }
        for (int i = 0; i < oppHand.size(); i++) {
            Card tempCard = oppHand.get(i);
            if (tempPlay == tempCard.getId()) {
                discardPile.add(0, oppHand.get(i));
                oppHand.remove(i);
            }
        }
        myTurn = true;
    }
}