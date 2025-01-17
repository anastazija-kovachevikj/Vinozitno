package finki.nichk.tablet.screens.child;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import finki.nichk.R;

public class ConnectActivity extends AppCompatActivity {

    private ImageView targetImage;
    private List<Integer> branchImages;
    private int targetImageRes;
    private TextView resultTextView;
    private ImageView firstPlate, secondPlate, thirdPlate, forthPlate;
    private ImageView beeReaction;
    private int currentRound = 1; // current round
    private final int totalRounds = 5; // number of rounds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_connecting); //tablet_connecting_main_menu

        targetImage = findViewById(R.id.targetImage);
        //beeReaction = findViewById(R.id.bee_reaction);
        //resultTextView = findViewById(R.id.resultTextView);
        ImageButton backButton = findViewById(R.id.back_button);

        buttonTouchListener(backButton, () -> {
            Intent intent = new Intent(this, ChildActivity.class);
            startActivity(intent);
        });

//        firstPlate = findViewById(R.id.firstPlate);
//        secondPlate = findViewById(R.id.secondPlate);
//        thirdPlate = findViewById(R.id.thirdtPlate);
//        forthPlate = findViewById(R.id.forthPlate);

        // Load dish images
        branchImages = new ArrayList<>();
        branchImages.add(R.drawable.fruit_lemon);
        branchImages.add(R.drawable.fruit_pear);
        branchImages.add(R.drawable.watermelon);
        branchImages.add(R.drawable.sb);
        branchImages.add(R.drawable.orange);
        branchImages.add(R.drawable.grape);
        branchImages.add(R.drawable.fruit_banana);
        branchImages.add(R.drawable.apple);
        branchImages.add(R.drawable.pineapple);
        branchImages.add(R.drawable.cherry);

        // listener for the target image
        targetImage.setOnDragListener(new TargetDragListener());

        //startNewRound(); // first round

        MediaPlayer mediaPlayer = MediaPlayer.create(ConnectActivity.this, R.raw.instructions);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void buttonTouchListener(ImageButton button, Runnable onClickAction) {
        button.setOnClickListener(v -> onClickAction.run());

        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // transparent when pressed
                    v.setAlpha(0.5f);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // back to normal released or canceled
                    v.setAlpha(1.0f);
                    break;
            }
            return false;
        });
    }

    private void animateFruits() {
        // load animation
        final Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom);

        // delay between each animation
        long delayBetweenPlates = 0;

        secondPlate.setVisibility(View.VISIBLE);
        secondPlate.startAnimation(zoomIn);

        thirdPlate.postDelayed(() -> {
            thirdPlate.setVisibility(View.VISIBLE);
            thirdPlate.startAnimation(zoomIn);
        }, delayBetweenPlates);

        firstPlate.postDelayed(() -> {
            firstPlate.setVisibility(View.VISIBLE);
            firstPlate.startAnimation(zoomIn);
        }, 2 * delayBetweenPlates);

        forthPlate.postDelayed(() -> {
            forthPlate.setVisibility(View.VISIBLE);
            forthPlate.startAnimation(zoomIn);
        }, 3 * delayBetweenPlates);
    }

    private void animateReaction(boolean isCorrect) {
        ImageView react = findViewById(R.id.bee_reaction);

        if (isCorrect) {
            react.setImageResource(R.drawable.hearts);
            //playCorrectAnswerSound(); // Play correct sound

        } else {
            react.setImageResource(R.drawable.wrong);
            //playTryAgainSound(); // Play "try again" sound
        }

        react.setVisibility(View.VISIBLE);

        Animation reactAnimation = AnimationUtils.loadAnimation(this, R.anim.float_away); // Reuse the same animation
        react.startAnimation(reactAnimation);

        // Optionally, reset the heart image after the animation finishes (if needed for future rounds)
        reactAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Reset the heart image back to the default after the animation ends
                react.setImageResource(R.drawable.hearts);  // Default heart image
                react.setVisibility(View.INVISIBLE); // Optionally hide it again after the animation
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        react.startAnimation(reactAnimation);
    }

    private void animateHeart(ImageView heart) {
        Animation floatAwayAnimation = AnimationUtils.loadAnimation(this, R.anim.float_away);
        floatAwayAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                heart.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                heart.setVisibility(View.GONE); // hide the heart after the animation ends
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        heart.startAnimation(floatAwayAnimation);
    }

    private void animateStars() {
        ImageView[] stars = {
                findViewById(R.id.star1),
                findViewById(R.id.star2),
                findViewById(R.id.star3),
                findViewById(R.id.star4),
                findViewById(R.id.star5),
                findViewById(R.id.star6)
        };

        int delay = 500; // delay for each star

        for (int i = 0; i < stars.length; i++) {
            ImageView star = stars[i];

            // Use a Handler with the main Looper to delay the start of each star's animation
            int finalI = i;
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                star.setVisibility(View.VISIBLE);

                // Get screen width
                int screenWidth = getResources().getDisplayMetrics().widthPixels;

                // Set the X translation to a random value between 0 and screen width
                float randomXTranslation = new Random().nextFloat() * screenWidth - 200; // Random float between 0 and width
                star.setTranslationX(randomXTranslation);

                Animation fallingStarsAnimation = AnimationUtils.loadAnimation(this, R.anim.star_fall);
                fallingStarsAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        star.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        star.setVisibility(View.INVISIBLE); // hide

                        if (finalI == stars.length - 1) { // on the last star go back to menu
                            Intent intent = new Intent(ConnectActivity.this, ChildActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                star.startAnimation(fallingStarsAnimation); // start animation

            }, delay * i); // increase delay for each star
        }

    }


    private void startNewRound() {
        Collections.shuffle(branchImages, new Random());  // shuffle

        // set images to plates
        firstPlate.setImageResource(branchImages.get(0));
        secondPlate.setImageResource(branchImages.get(1));
        thirdPlate.setImageResource(branchImages.get(2));
        forthPlate.setImageResource(branchImages.get(3));

        animateFruits();

        // random target image
        List<Integer> choices = new ArrayList<>();
        choices.add(branchImages.get(0));
        choices.add(branchImages.get(1));
        choices.add(branchImages.get(2));
        choices.add(branchImages.get(3));

        Collections.shuffle(choices, new Random());
        targetImageRes = choices.get(0); // target image resource
        targetImage.setImageResource(targetImageRes);

        resetPlates();

        setPlateDragListener(firstPlate, branchImages.get(0));
        setPlateDragListener(secondPlate, branchImages.get(1));
        setPlateDragListener(thirdPlate, branchImages.get(2));
        setPlateDragListener(forthPlate, branchImages.get(3));
    }

    private void resetPlates() {

        firstPlate.setEnabled(true);
        secondPlate.setEnabled(true);
        thirdPlate.setEnabled(true);
        forthPlate.setEnabled(true);

        firstPlate.setAlpha(1.0f);
        secondPlate.setAlpha(1.0f);
        thirdPlate.setAlpha(1.0f);
        forthPlate.setAlpha(1.0f);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setPlateDragListener(ImageView plate, int imageRes) {
        plate.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && v.isEnabled()) {

                ClipData.Item item = new ClipData.Item(String.valueOf(imageRes));
                ClipData dragData = new ClipData(
                        String.valueOf(imageRes),
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                        item
                );

                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(dragData, shadowBuilder, v, 0);

                return true;
            }
            return false;
        });
    }

    private void playSoundForTarget(int targetImageRes) {
        String soundName = getResources().getResourceEntryName(targetImageRes); // name from drawable
        int soundResId = getResources().getIdentifier(soundName, "raw", getPackageName());

        if (soundResId != 0) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, soundResId);
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer.start(); // play sound
        }
    }


    private class TargetDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();

            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    return true;

                case DragEvent.ACTION_DROP:
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    int draggedImageRes = Integer.parseInt(item.getText().toString());

                    if (draggedImageRes == targetImageRes) {
                        //resultTextView.setText("Correct!");
                        animateReaction(true);
                        playSoundForTarget(targetImageRes);
                        currentRound++;

                        if (currentRound > totalRounds) {
                            animateStars();
                        } else {
                            startNewRound();
                        }

                    } else {
                        // sound
                        MediaPlayer mediaPlayer = MediaPlayer.create(ConnectActivity.this, R.raw.try_again);
                        if (mediaPlayer != null) {
                            mediaPlayer.start();
                            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
                        }

                        animateReaction(false);

                        //resultTextView.setText("Incorrect!");
                        View draggedView = (View) event.getLocalState();
                        draggedView.setEnabled(false); // disable plate
                        draggedView.setAlpha(0.5f);
                    }
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    return true;

                default:
                    break;
            }

            return false;
        }
    }
}
