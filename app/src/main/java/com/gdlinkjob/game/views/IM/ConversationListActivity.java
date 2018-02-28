package com.gdlinkjob.game.views.IM;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.gdlinkjob.game.R;

import io.rong.imkit.mention.MemberMentionedActivity;

/**
 *
 * @author hwhong
 * @创建日期 2018/1/27/ 0027 下午 03:49
 */
public class ConversationListActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversationlist);
    }
}
