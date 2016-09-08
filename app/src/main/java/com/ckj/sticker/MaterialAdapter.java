package com.ckj.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author chenkaijian
 * 贴图素材适配器
 */
public class MaterialAdapter extends BaseAdapter {
        //  上下文对象
        private Context context;
        //  素材集合
        private ArrayList<HashMap<String, String>> materialList;

        MaterialAdapter(Context context, ArrayList<HashMap<String, String>> materialList) {
            this.context = context;
            this.materialList = materialList;
        }

        public int getCount() {
            return materialList.size();
        }

        public Object getItem(int item) {
            return item;
        }

        public long getItemId(int id) {
            return id;
        }

        //创建View方法
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.material_grid_item,null);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            File dir = context.getExternalFilesDir(null);

            Bitmap bitmap = BitmapFactory.decodeFile(dir.getAbsolutePath()+"/decorate/"+materialList.get(position).get("thumbnailname"));
            holder.img.setImageBitmap(bitmap);
            return convertView;
        }

        public final class ViewHolder {
            public ImageView img;
        }
}
