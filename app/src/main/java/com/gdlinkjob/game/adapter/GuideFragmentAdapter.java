package com.gdlinkjob.game.adapter;

/**
 * Created by 97481 on 2016/10/11.
 */
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.gdlinkjob.game.views.IM.fragment.ContactFragment;

import io.rong.imkit.fragment.ConversationAddMemberFragment;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;


public class GuideFragmentAdapter extends FragmentPagerAdapter {

    private ConversationListFragment conversationFragment;

    private Fragment[] fragments = new Fragment[] {getConversationListFragment(),
            new ContactFragment() , new ConversationListFragment()};

    public GuideFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    private ConversationListFragment getConversationListFragment(){
        if(conversationFragment ==null){
            conversationFragment=new ConversationListFragment();
            Uri uri=Uri.parse("rong://com.gdlingjob.game").buildUpon()
                    .appendPath("conversationlist")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(),"false")
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(),"false")
                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(),"false")
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(),"false")
                    .build();
            conversationFragment.setUri(uri);
            return conversationFragment;
        }
        return conversationFragment;
    }
}