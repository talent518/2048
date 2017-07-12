package net.yeah.talent518.abao2048;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private static final String TAG = "MainActivity";
    private static final int[][] blockResourceIds = {
            {R.id.block_0x0, R.id.block_0x1, R.id.block_0x2, R.id.block_0x3},
            {R.id.block_1x0, R.id.block_1x1, R.id.block_1x2, R.id.block_1x3},
            {R.id.block_2x0, R.id.block_2x1, R.id.block_2x2, R.id.block_2x3},
            {R.id.block_3x0, R.id.block_3x1, R.id.block_3x2, R.id.block_3x3}
    };
    Random rnd = new Random();
    GestureDetector mGestureDetector;
    private TextView[][] mBlockViews = new TextView[4][4];
    private int[][] mInts = {
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
    };
    private int mScore = 0;
    private TextView tvScore;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int y, x;
        for (y = 0; y < 4; y++) {
            for (x = 0; x < 4; x++) {
                mBlockViews[y][x] = (TextView) findViewById(blockResourceIds[y][x]);
                setBlock(y, x, "");
            }
        }

        initGame(false);

        mGestureDetector = new GestureDetector(this, this);
        mGestureDetector.setOnDoubleTapListener(this);

        view = findViewById(R.id.activity_main);
        view.setOnTouchListener(this);

        tvScore = (TextView) findViewById(R.id.score);
    }

    private void initGame(boolean isInitVariable) {
        int y, x;

        if (isInitVariable) {
            for (y = 0; y < 4; y++) {
                for (x = 0; x < 4; x++) {
                    mInts[y][x] = 0;
                    setBlock(y, x, "");
                }
            }

            mScore = 0;
            tvScore.setText("0");
        }

        y = rnd.nextInt(4);
        x = rnd.nextInt(4);
        mInts[y][x] = 2;
        setBlock(y, x, "2");
        playAnimation(y, x);

        int y2, x2;
        do {
            y2 = rnd.nextInt(4);
            x2 = rnd.nextInt(4);
        } while (y == y2 && x == x2);
        mInts[y2][y2] = 2;
        setBlock(y2, x2, "2");
        playAnimation(y2, x2);
    }

    private int getBlockNum(int y, int x) {
        try {
            String txt = mBlockViews[y][x].getText().toString();
            return Integer.parseInt(txt);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setBlock(int y, int x, String txt) {
        mBlockViews[y][x].setText(txt);

        int bgRes = R.drawable.bg_block;
        int n = getBlockNum(y, x);
        if (n > 0) {
            Field field;
            try {
                field = R.drawable.class.getDeclaredField("bg_block_" + n);
                bgRes = field.getInt(null);
            } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
                e.printStackTrace();
            }
            try {
                field = R.color.class.getDeclaredField("blockTextColor_" + n);
                mBlockViews[y][x].setTextColor(getResources().getColor(field.getInt(null)));
            } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
                e.printStackTrace();
            }
            try {
                field = R.dimen.class.getDeclaredField("text_size_" + Integer.toString(n).length() + "font");
                mBlockViews[y][x].setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(field.getInt(null)));
            } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mBlockViews[y][x].setBackgroundResource(bgRes);
    }

    private void playAnimation(int y, int x) {
        Animation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        mBlockViews[y][x].clearAnimation();
        mBlockViews[y][x].startAnimation(animation);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int mini_width = 120;
        int mini_speed = 0;
        float distance_right = e2.getX() - e1.getX();
        float distance_left = e1.getX() - e2.getX();
        float distance_down = e2.getY() - e1.getY();
        float distance_up = e1.getY() - e2.getY();
        int x, y, n, n2;
        boolean flag;
        boolean isMoved = false, isMovable = true;
        if (distance_right > mini_width && Math.abs(velocityX) > mini_speed) {
            Log.e(TAG, "onFling-" + "向右滑动: " + mIntsToString());

            int x2;
            for (y = 0; y < 4; y++) {
                x2 = 3;
                for (x = 2; x >= 0; x--) {
                    n = mInts[y][x];
                    if (n > 0) {
                        n2 = mInts[y][x2];
                        flag = true;
                        if (n == n2) {
                            n += n2;
                            mScore += n;
                        } else if (n2 > 0) {
                            x2--;
                            if (x2 <= x || mInts[y][x2] > 0) {
                                flag = false;
                                continue;
                            }
                        } else {
                            flag = false;
                        }
                        mInts[y][x] = 0;
                        mInts[y][x2] = n;
                        if (flag) {
                            x2--;
                        }
                        isMoved = true;
                    }
                }
            }
        } else if (distance_left > mini_width && Math.abs(velocityX) > mini_speed) {
            Log.e(TAG, "onFling-" + "向左滑动: " + mIntsToString());

            int x2;
            for (y = 0; y < 4; y++) {
                x2 = 0;
                for (x = 1; x < 4; x++) {
                    n = mInts[y][x];
                    if (n > 0) {
                        n2 = mInts[y][x2];
                        flag = true;
                        if (n == n2) {
                            n += n2;
                            mScore += n;
                        } else if (n2 > 0) {
                            x2++;
                            if (x2 >= x || mInts[y][x2] > 0) {
                                flag = false;
                                continue;
                            }
                        } else {
                            flag = false;
                        }
                        mInts[y][x] = 0;
                        mInts[y][x2] = n;
                        if (flag) {
                            x2++;
                        }
                        isMoved = true;
                    }
                }
            }
        } else if (distance_down > mini_width && Math.abs(velocityX) > mini_speed) {
            Log.e(TAG, "onFling-" + "向下滑动: " + mIntsToString());

            int y2;
            for (x = 0; x < 4; x++) {
                y2 = 3;
                for (y = 2; y >= 0; y--) {
                    n = mInts[y][x];
                    if (n > 0) {
                        n2 = mInts[y2][x];
                        flag = true;
                        if (n == n2) {
                            n += n2;
                            mScore += n;
                        } else if (n2 > 0) {
                            y2--;
                            if (y2 <= y || mInts[y2][x] > 0) {
                                flag = false;
                                continue;
                            }
                        } else {
                            flag = false;
                        }
                        mInts[y][x] = 0;
                        mInts[y2][x] = n;
                        if (flag) {
                            y2--;
                        }
                        isMoved = true;
                    }
                }
            }
        } else if (distance_up > mini_width && Math.abs(velocityX) > mini_speed) {
            Log.e(TAG, "onFling-" + "向上滑动: " + mIntsToString());

            int y2;
            for (x = 0; x < 4; x++) {
                y2 = 0;
                for (y = 1; y < 4; y++) {
                    n = mInts[y][x];
                    if (n > 0) {
                        n2 = mInts[y2][x];
                        flag = true;
                        if (n == n2) {
                            n += n2;
                            mScore += n;
                        } else if (n2 > 0) {
                            y2++;
                            if (y2 >= y || mInts[y2][x] > 0) {
                                flag = false;
                                continue;
                            }
                        } else {
                            flag = false;
                        }
                        mInts[y][x] = 0;
                        mInts[y2][x] = n;
                        if (flag) {
                            y2++;
                        }
                        isMoved = true;
                    }
                }
            }
        } else {
            isMovable = false;
        }

        if (isMovable) {
            Log.e(TAG, "Movable: " + mIntsToString());
        }

        if (isMoved) {
            ArrayList<Point> points = new ArrayList<Point>();
            for (y = 0; y < 4; y++) {
                for (x = 0; x < 4; x++) {
                    if (mInts[y][x] > 0) {
                        setBlock(y, x, Integer.toString(mInts[y][x]));
                    } else {
                        points.add(new Point(x, y));
                        setBlock(y, x, "");
                    }
                }
            }

            // TODO 游戏结束的判断还需完善！
            if (points.size() > 1) {
                Point p = points.get(rnd.nextInt(points.size()));

                mInts[p.y][p.x] = 2;
                setBlock(p.y, p.x, "2");
                playAnimation(p.y, p.x);

                tvScore.setText(Integer.toString(mScore));
                Log.e(TAG, p.toString());
                Log.e(TAG, "score = " + mScore);
            } else {
                Log.v(TAG, "Game Over");
                view.setOnTouchListener(null);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("游戏结束");
                builder.setMessage("你的得分：" + mScore);
                builder.setPositiveButton("新游戏", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.setOnTouchListener(MainActivity.this);
                        initGame(true);
                    }
                });
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
            }
        }

        if (isMovable) {
            Log.e(TAG, "Movable: " + mIntsToString());
            Log.v(TAG, "=============================");
        }

        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    private String mIntsToString() {
        StringBuffer sb = new StringBuffer();
        int y, x;
        for (y = 0; y < 4; y++) {
            sb.append("\t[");
            for (x = 0; x < 4; x++) {
                if (x > 0) {
                    sb.append(", ");
                }
                sb.append(mInts[y][x]);
            }
            sb.append("]" + (y == 3 ? "" : ",") + "\n");
        }
        return "[\n" + sb.toString() + "]";
    }
}