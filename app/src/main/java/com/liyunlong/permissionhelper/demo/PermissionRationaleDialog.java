package com.liyunlong.permissionhelper.demo;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyunlong
 * @date 2017/7/28 14:46
 */
public class PermissionRationaleDialog extends Dialog implements View.OnClickListener {

    private TextView tvTitle;
    private TextView tvDesc;
    private GridView gvPermission;
    private Button btnNext;
    private Callback callback;
    private PermissionAdapter adapter;

    public PermissionRationaleDialog(@NonNull Context context) {
        this(context, 0);
    }

    public PermissionRationaleDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initContentlayout();
    }

    private void initContentlayout() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.layout_permission_prerequest_dialog, null);
        tvTitle = (TextView) rootView.findViewById(R.id.permission_title);
        tvDesc = (TextView) rootView.findViewById(R.id.permission_desc);
        gvPermission = (GridView) rootView.findViewById(R.id.permission_grid);
        btnNext = (Button) rootView.findViewById(R.id.permission_next);
        btnNext.setOnClickListener(this);
        this.setContentView(rootView);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
    }

    public void setTitle(@Nullable CharSequence title) {
        tvTitle.setText(title);
    }

    public void setDesc(@Nullable CharSequence title) {
        tvDesc.setText(title);
    }

    public void setNext(@Nullable CharSequence next) {
        btnNext.setText(next);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setPermissions(List<String> list) {
        if (list == null) {
            list = new ArrayList<>(0);
        }
        if (list.size() > 2) {
            gvPermission.setNumColumns(3);
        } else if (list.size() > 1) {
            gvPermission.setNumColumns(2);
        } else {
            gvPermission.setNumColumns(1);
        }
        if (adapter == null) {
            adapter = new PermissionAdapter(list);
            gvPermission.setAdapter(adapter);
        } else {
            adapter.refresh(list);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.permission_next:
                dismiss();
                if (callback != null) {
                    callback.callback();
                }
                break;
        }
    }

    public interface Callback {

        void callback();

    }

    private class PermissionAdapter extends BaseAdapter {

        private List<String> list;

        PermissionAdapter(List<String> list) {
            this.list = list;
        }

        void refresh(List<String> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public String getItem(int position) {
            return list == null ? null : list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PermissionViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.layout_item_permission, parent, false);
                holder = new PermissionViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (PermissionViewHolder) convertView.getTag();
            }
            String permission = getItem(position);
            if (permission != null) {
                holder.ivIcon.setImageResource(PermissionConfig.getIcon(permission));
                holder.tvDesc.setText(PermissionConfig.getDesc(permission));
            }
            return convertView;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        private class PermissionViewHolder {

            private final ImageView ivIcon;
            private final TextView tvDesc;

            PermissionViewHolder(View itemView) {
                ivIcon = (ImageView) itemView.findViewById(R.id.permission_icon);
                tvDesc = (TextView) itemView.findViewById(R.id.permission_desc);
            }
        }
    }
}
