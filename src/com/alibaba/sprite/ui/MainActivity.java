package com.alibaba.sprite.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Process;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.sprite.R;
import com.alibaba.sprite.SpriteClient;
import com.alibaba.sprite.audio.SpritePlayer;
import com.alibaba.sprite.audio.SpriteRecorder;

public class MainActivity extends Activity {

    public static final int MENU_START_ID = Menu.FIRST;
    public static final int MENU_STOP_ID = Menu.FIRST + 1;
    public static final int MENU_EXIT_ID = Menu.FIRST + 2;

    protected SpritePlayer player;
    protected SpriteRecorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        boolean res = super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_START_ID, 0, "START");
        menu.add(0, MENU_STOP_ID, 0, "STOP");
        menu.add(0, MENU_EXIT_ID, 0, "EXIT");
        return res;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_START_ID: {
            player = new SpritePlayer();
            player.init((SpriteClient) getApplication());
            player.start();

            recorder = new SpriteRecorder();
            recorder.init((SpriteClient) getApplication());
            recorder.start();
            break;
        }
        case MENU_STOP_ID: {
            if (recorder != null) {
                recorder.free();
                recorder = null;
            }

            if (player != null) {
                player.free();
                player = null;
            }
            break;
        }
        case MENU_EXIT_ID: {
            ((SpriteClient) getApplication()).free();
            Process.killProcess(Process.myPid());
            break;
        }
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

}
