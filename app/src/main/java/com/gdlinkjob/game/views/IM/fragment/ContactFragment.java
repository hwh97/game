package com.gdlinkjob.game.views.IM.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gdlinkjob.game.R;
import com.gdlinkjob.game.base.BaseFragment;
import com.gdlinkjob.game.views.IM.Entity.ContactEntity;
import com.gdlinkjob.game.views.IM.Entity.MenuEntity;
import com.gdlinkjob.game.views.IM.Entity.UserEntity;
import com.gdlinkjob.game.views.IM.IndexStickyViewDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.ittiger.indexlist.IndexStickyView;
import cn.ittiger.indexlist.adapter.IndexHeaderFooterAdapter;
import cn.ittiger.indexlist.adapter.IndexStickyViewAdapter;
import cn.ittiger.indexlist.entity.BaseEntity;
import cn.ittiger.indexlist.listener.OnItemClickListener;
import cn.ittiger.indexlist.listener.OnItemLongClickListener;
import io.rong.imkit.RongIM;

/**
 *
 * @author hwhong
 * @创建日期 2018/1/29/ 0029 下午 03:33
 */

public class ContactFragment extends BaseFragment implements OnItemClickListener<ContactEntity>, OnItemLongClickListener<ContactEntity> {

    IndexStickyView mIndexStickyView;
    MyIndexStickyViewAdapter mAdapter;
    private Context mContext;
    IndexHeaderFooterAdapter<UserEntity> mFavAdapter;
    IndexHeaderFooterAdapter<MenuEntity> mMenuAdapter;
    IndexHeaderFooterAdapter<UserEntity> mNormalAdapter;
    IndexHeaderFooterAdapter mBannerAdapter;
    IndexHeaderFooterAdapter<UserEntity> mFooterAdapter;
    IndexHeaderFooterAdapter mFooterBannerAdapter;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.contact_fragment);
        mIndexStickyView=getViewById(R.id.indexStickyView);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mContext = this.getContext();

        mAdapter = new MyIndexStickyViewAdapter(initDatas());
        mIndexStickyView.setAdapter(mAdapter);
        mIndexStickyView.addItemDecoration(new IndexStickyViewDecoration(getContext()));

        //自定义添加头部收藏信息
        mFavAdapter = new IndexHeaderFooterAdapter<UserEntity>("☆", "我的收藏", initFavDatas()) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

                View view = LayoutInflater.from(mContext).inflate(R.layout.indexsticky_item_contact, parent, false);
                return new ContentViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, UserEntity itemData) {

                ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
                contentViewHolder.mMobile.setText(itemData.getMobile());
                contentViewHolder.mName.setText(itemData.getName());
            }
        };
        mFavAdapter.setOnItemClickListener(new OnItemClickListener<UserEntity>() {
            @Override
            public void onItemClick(View childView, int position, UserEntity item) {
                Toast.makeText(mContext, "点击：" + item.getName() + ",位置：" + position, Toast.LENGTH_SHORT).show();
            }
        });
        mIndexStickyView.addIndexHeaderAdapter(mFavAdapter);

        //添加自定义头部菜单项
        mMenuAdapter = new IndexHeaderFooterAdapter<MenuEntity>("↑", null, initMenuDatas()) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

                View view = LayoutInflater.from(mContext).inflate(R.layout.indexsticky_item_contact, parent, false);
                return new ContentViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, MenuEntity itemData) {

                ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
                contentViewHolder.mMobile.setVisibility(View.GONE);
                contentViewHolder.mName.setText(itemData.getMenuTitle());
                contentViewHolder.mAvatar.setImageResource(itemData.getMenuIconRes());
            }
        };
        mMenuAdapter.setOnItemLongClickListener(new OnItemLongClickListener<MenuEntity>() {
            @Override
            public  void onItemLongClick(View childView, int position, MenuEntity item) {
                Toast.makeText(mContext, "长按：" + item.getMenuTitle() + ",位置：" + position, Toast.LENGTH_SHORT).show();
            }
        });
        mIndexStickyView.addIndexHeaderAdapter(mMenuAdapter);

        //添加一个长度为1的数据来作为普通视图的数据源
        UserEntity[] entitys = {new UserEntity("数据绑定普通HeaderView", "13312345654")};
        mNormalAdapter = new IndexHeaderFooterAdapter<UserEntity>(null, null, Arrays.asList(entitys)) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

                View view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
                return new MyViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, UserEntity itemData) {

                ((MyViewHolder)holder).mTextView.setText(itemData.getName());
            }

            class MyViewHolder extends RecyclerView.ViewHolder {
                TextView mTextView;

                public MyViewHolder(View itemView) {
                    super(itemView);
                    mTextView = (TextView) itemView;
                }
            }
        };
        mNormalAdapter.setOnItemClickListener(new OnItemClickListener<UserEntity>() {
            @Override
            public void onItemClick(View childView, int position, UserEntity item) {
                /**
                 * 启动单聊界面。
                 *
                 * @param context      应用上下文。
                 * @param targetUserId 要与之聊天的用户 Id。
                 * @param title        聊天的标题，开发者需要在聊天界面通过 intent.getData().getQueryParameter("title")
                 *                     获取该值, 再手动设置为聊天界面的标题。
                 */
                RongIM.getInstance().startPrivateChat(getActivity(), "1", "标题");
                Toast.makeText(mContext, "普通Header视图点击：" + item.getName() + ",位置：" + position, Toast.LENGTH_SHORT).show();
            }
        });
        mIndexStickyView.addIndexHeaderAdapter(mNormalAdapter);

      /*  //自定义添加一个图片作为头部普通视图
        mBannerAdapter = new IndexHeaderFooterAdapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

                View view = LayoutInflater.from(mContext).inflate(R.layout.indexsticky_header_contact_banner, parent, false);
                ImageViewVH vh = new ImageViewVH(view);
                vh.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "头部普通图片视图点击", Toast.LENGTH_SHORT).show();
                    }
                });
                return vh;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, BaseEntity itemData) {

            }

            class ImageViewVH extends RecyclerView.ViewHolder {
                ImageView img;

                public ImageViewVH(View itemView) {
                    super(itemView);
                    img = (ImageView) itemView.findViewById(R.id.img);
                }
            }
        };
        mIndexStickyView.addIndexHeaderAdapter(mBannerAdapter);*/


        //添加一个底部自定义列表
        mFooterAdapter = new IndexHeaderFooterAdapter<UserEntity>("$", "Footer", initFavDatas()) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

                View view = LayoutInflater.from(mContext).inflate(R.layout.indexsticky_item_contact, parent, false);
                return new ContentViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, UserEntity itemData) {
                ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
                contentViewHolder.mMobile.setText(itemData.getMobile());
                contentViewHolder.mName.setText(itemData.getName());
            }
        };
        mFooterAdapter.setOnItemClickListener(new OnItemClickListener<UserEntity>() {
            @Override
            public void onItemClick(View childView, int position, UserEntity item) {
                Toast.makeText(mContext, "点击Footer：" + item.getName() + ",位置：" + position, Toast.LENGTH_SHORT).show();
            }
        });
        mIndexStickyView.addIndexFooterAdapter(mFooterAdapter);

    /*    //Footer Banner
        mFooterBannerAdapter = new IndexHeaderFooterAdapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

                View view = LayoutInflater.from(mContext).inflate(R.layout.indexsticky_header_contact_banner, parent, false);
                ImageViewVH vh = new ImageViewVH(view);
                vh.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "Footer图片视图点击", Toast.LENGTH_SHORT).show();
                    }
                });
                return vh;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, BaseEntity itemData) {

            }

            class ImageViewVH extends RecyclerView.ViewHolder {
                ImageView img;

                public ImageViewVH(View itemView) {
                    super(itemView);
                    img = (ImageView) itemView.findViewById(R.id.img);
                }
            }
        };
        mIndexStickyView.addIndexFooterAdapter(mFooterBannerAdapter);
*/

        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);

    }

    @Override
    protected void lazyLoad() {

    }

    class MyIndexStickyViewAdapter extends IndexStickyViewAdapter<ContactEntity> {

        public MyIndexStickyViewAdapter(List<ContactEntity> list) {

            super(list);
        }

        @Override
        public RecyclerView.ViewHolder onCreateIndexViewHolder(ViewGroup parent) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.indexsticky_item_index, parent, false);
            return new IndexViewHolder(view);
        }

        @Override
        public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.indexsticky_item_contact, parent, false);
            return new ContentViewHolder(view);
        }

        @Override
        public void onBindIndexViewHolder(RecyclerView.ViewHolder holder, int position, String indexName) {

            IndexViewHolder indexViewHolder = (IndexViewHolder) holder;
            indexViewHolder.mTextView.setText(indexName);
        }

        @Override
        public void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position, ContactEntity itemData) {

            ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
            contentViewHolder.mMobile.setText(itemData.getMobile());
            contentViewHolder.mName.setText(itemData.getName());
        }
    }

    class IndexViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public IndexViewHolder(View itemView) {

            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_index);
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mMobile;
        ImageView mAvatar;

        public ContentViewHolder(View itemView) {

            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mMobile = (TextView) itemView.findViewById(R.id.tv_mobile);
            mAvatar = (ImageView) itemView.findViewById(R.id.img_avatar);
        }
    }

    private List<ContactEntity> initDatas() {

        List<ContactEntity> list = new ArrayList<>();
        // 初始化数据
        List<String> contactStrings = Arrays.asList(getResources().getStringArray(R.array.contact_array));
        List<String> mobileStrings = Arrays.asList(getResources().getStringArray(R.array.mobile_array));
        for (int i = 0; i < contactStrings.size(); i++) {
            ContactEntity contactEntity = new ContactEntity(contactStrings.get(i), mobileStrings.get(i));
            list.add(contactEntity);
        }
        return list;
    }

    private List<UserEntity> initFavDatas() {
        List<UserEntity> list = new ArrayList<>();
        list.add(new UserEntity("张三", "13298449923"));
        list.add(new UserEntity("李四", "13298449923"));
        return list;
    }

    private List<MenuEntity> initMenuDatas() {
        List<MenuEntity> list = new ArrayList<>();
        list.add(new MenuEntity("圣诞组", R.drawable.angel));
        list.add(new MenuEntity("雪人组", R.drawable.angel));
        list.add(new MenuEntity("天使组", R.drawable.angel));
        list.add(new MenuEntity("铃铛组", R.drawable.angel));
        return list;
    }

    @Override
    public void onItemClick(View childView, int position, ContactEntity item) {

        Toast.makeText(mContext, "点击" + item.getName() + ",位置：" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View childView, int position, ContactEntity item) {

        Toast.makeText(mContext, "长按：" + item.getName() + ",位置：" + position, Toast.LENGTH_SHORT).show();
    }

}
