package com.alibaba.sprite.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Process;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.sprite.R;
import com.alibaba.sprite.SpriteClient;
import com.alibaba.sprite.audio.Saudioclient;
import com.alibaba.sprite.audio.Saudioserver;

public class MainActivity extends Activity {

    public static final int MENU_START_ID = Menu.FIRST;
    public static final int MENU_STOP_ID = Menu.FIRST + 1;
    public static final int MENU_EXIT_ID = Menu.FIRST + 2;

    protected Saudioserver m_player;
    protected Saudioclient m_recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public boolean onCreateOptionsMenu(Menu aMenu) {
        boolean res = super.onCreateOptionsMenu(aMenu);
        aMenu.add(0, MENU_START_ID, 0, "START");
        aMenu.add(0, MENU_STOP_ID, 0, "STOP");
        aMenu.add(0, MENU_EXIT_ID, 0, "EXIT");
        return res;
    }

    public boolean onOptionsItemSelected(MenuItem aMenuItem) {
        switch (aMenuItem.getItemId()) {
        case MENU_START_ID: {
            m_player = new Saudioserver();
            m_player.init((SpriteClient) getApplication());
            m_player.start();

            m_recorder = new Saudioclient();
            m_recorder.init((SpriteClient) getApplication());
            m_recorder.start();
            break;
        }
        case MENU_STOP_ID: {
            if (m_recorder != null) {
                m_recorder.free();
                m_recorder = null;
            }

            if (m_player != null) {
                m_player.free();
                m_player = null;
            }
            break;
        }
        case MENU_EXIT_ID: {
            Process.killProcess(Process.myPid());
            break;
        }
        default:
            break;
        }
        return super.onOptionsItemSelected(aMenuItem);
    }

}
