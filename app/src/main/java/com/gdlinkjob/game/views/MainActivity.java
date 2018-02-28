package com.gdlinkjob.game.views;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.gdlinkjob.game.App;
import com.gdlinkjob.game.R;
import com.gdlinkjob.game.adapter.GuideFragmentAdapter;
import com.gdlinkjob.game.base.BaseActivity;
import com.gdlinkjob.game.utils.ToastUtils;
import com.gdlinkjob.game.views.IM.ConversationActivity;
import com.gdlinkjob.game.views.IM.ConversationListActivity;
import com.gdlinkjob.game.views.IM.SubConversationListActivtiy;
import com.gdlinkjob.game.views.IM.fragment.ContactFragment;

import java.util.HashMap;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imkit.mention.MemberMentionedActivity;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class MainActivity extends BaseActivity {

    private ViewPager viewPager;
    private RadioButton main_tab_home, main_tab_catagory,main_tab_third;
    public GuideFragmentAdapter adapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        connect("t/BgMAz+wjeoX1hBIrXyQfpCikuRXqolGc1rEVIAYX5VrNWfLWgkvJOmloM4iiT6IyxdFLw4DAM=");
    }

    @Override
    protected void initBundleData() {

    }

    /**
     * <p>连接服务器，在整个应用程序全局，只需要调用一次，需在 {@link #init(Context)} 之后调用。</p>
     * <p>如果调用此接口遇到连接失败，SDK 会自动启动重连机制进行最多10次重连，分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后。
     * 在这之后如果仍没有连接成功，还会在当检测到设备网络状态变化时再次进行重连。</p>
     *
     * @param token    从服务端获取的用户身份令牌（Token）。
     * @param callback 连接回调。
     * @return RongIM  客户端核心类的实例。
     */
    private void connect(String token) {

        if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 *                  2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {
                    Log.d("LoginActivity", "--onTokenIncorrect" );
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.d("LoginActivity", "--onSuccess" + userid);
                    ToastUtils.showToast(getApplicationContext(),"登陆成功");
                    initView();
                    //startConversationList();
                    //finish();
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.d("LoginActivity", "--onFail" + errorCode);
                }
            });
        }
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        main_tab_home = (RadioButton) findViewById(R.id.main_tab_home);
        main_tab_catagory = (RadioButton) findViewById(R.id.main_tab_catagory);
        main_tab_third=(RadioButton) findViewById(R.id.main_tab_third);
    /*    main_tab_home.setOnClickListener(this);
        main_tab_catagory.setOnClickListener(this);
        main_tab_third.setOnClickListener(this);*/

        adapter = new GuideFragmentAdapter(
                getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    private void startConversationList() {
        Map<String, Boolean> map = new HashMap<>();
        map.put(Conversation.ConversationType.PRIVATE.getName(), false); // 会话列表需要显示私聊会话, 第二个参数 true 代表私聊会话需要聚合显示
        map.put(Conversation.ConversationType.GROUP.getName(), false);  // 会话列表需要显示群组会话, 第二个参数 false 代表群组会话不需要聚合显示

        RongIM.getInstance().startConversationList(this.getApplicationContext(), map);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
