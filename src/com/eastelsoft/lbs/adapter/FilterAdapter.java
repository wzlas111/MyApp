package com.eastelsoft.lbs.adapter;

import java.util.ArrayList;  
import java.util.List;  
  
import android.content.Context;  
import android.util.Log;
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;  
import android.widget.Filter;  
import android.widget.Filterable;  
import android.widget.TextView;  
  /*
   * @author zhengyuhui
   * 检索过滤器，暂时先不用了，先不要删除，万一以后要用
   */
public class FilterAdapter<T> extends BaseAdapter implements Filterable {  
	public static final String TAG="FilterAdapter";
    private List<T> mOriginalValues;
    private List<T> mObjects;
    private final Object mLock = new Object();
    private int mResource;
    private int mDropDownResource;
    private Context mContext = null;
    private MyFilter mFilter = null;
    private LayoutInflater mInflater = null;
  
    public FilterAdapter(Context context, int textViewResourceId,
            List<T> objects) {
        init(context, textViewResourceId, objects);
    }  
  
    private void init(Context context, int resource, List<T> objects) {  
    	Log.e("FilterAdapter","构造器启动");
        mContext = context;  
        mInflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        mResource = mDropDownResource = resource;  
        mObjects = new ArrayList<T>(objects);  
        mFilter = new MyFilter();  
    }  
  
    public void add(T object) {  
        if (mOriginalValues != null) {  
            synchronized (mLock) {  
            	Log.i(TAG, "Lock---->add");
                mOriginalValues.add(object);  
            }  
        } else {  
        	Log.i(TAG, "unLock---->add");
            mObjects.add(object);  
        }  
    }  
  
    public void insert(T object, int index) {  
        if (mOriginalValues != null) {  
            synchronized (mLock) {
            	Log.i(TAG, "Lock---->insert");
                mOriginalValues.add(index, object);  
            }  
        } else {  
        	Log.i(TAG, "unLock---->insert");
            mObjects.add(index, object);  
        }  
    }  
  
    public void remove(T object) {  
        if (mOriginalValues != null) {  
            synchronized (mLock) {  
            	Log.i(TAG, "Lock---->remove");
                mOriginalValues.remove(object);  
            }  
        } else {  
        	Log.i(TAG, "unLock---->remove");
            mObjects.remove(object);  
        }  
    }  
  
    public void clear() {  
        if (mOriginalValues != null) {  
            synchronized (mLock) {  
            	Log.i(TAG, "Lock---->clear");
                mOriginalValues.clear();  
            }  
        } else {  
        	Log.i(TAG, "unLock---->clear");
            mObjects.clear();  
        }  
    }  
  
    public Context getContext() {  
        return mContext;  
    }  
  
    @Override  
    public int getCount() {  
        return mObjects.size();  
    }  
  
    @Override  
    public T getItem(int position) {  
        return mObjects.get(position);  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        return createViewFromResource(position, convertView, parent, mResource);  
    }  
  
    private View createViewFromResource(int position, View convertView,  
            ViewGroup parent, int resource) {  
        View view;  
        TextView text;  
  
        if (convertView == null) {  
            view = mInflater.inflate(resource, parent, false);  
        } else {  
            view = convertView;  
        }  
  
        try {  
            text = (TextView) view;  
        } catch (ClassCastException e) {  
            throw new IllegalStateException(  
                    "ArrayAdapter requires the resource ID to be a TextView", e);  
        }  
  
        T item = getItem(position);  
        if (item instanceof CharSequence) {  
            text.setText((CharSequence) item);  
        } else {  
            text.setText(item.toString());  
        }  
  
        return view;  
    }  
  
    @Override  
    public Filter getFilter() {  
        return mFilter;  
    }  
  
    public void setDropDownViewResource(int resource) {  
        this.mDropDownResource = resource;  
    }  
  
    @Override  
    public View getDropDownView(int position, View convertView, ViewGroup parent) {  
        return createViewFromResource(position, convertView, parent,  
                mDropDownResource);  
    }  
  
    private class MyFilter extends Filter {
    	public MyFilter(){
    		Log.e("MyFilter","过滤器构造启动");
    	}
        @Override  
        protected FilterResults performFiltering(CharSequence constraint) {
        	Log.e("匹配的值是",constraint+"");
            FilterResults results = new FilterResults();  
            if (mOriginalValues == null) {  
                synchronized (mLock) {
                	Log.i(TAG, "mOriginalValues==null");
                    mOriginalValues = new ArrayList<T>(mObjects);  
                }
            }
            int count = mOriginalValues.size();  
            ArrayList<T> values = new ArrayList<T>();  
  
            for (int i = 0; i < count; i++){  
                T value = mOriginalValues.get(i);  
                String valueText = value.toString();  
                if (null != valueText && null != constraint  
                        && valueText.contains(constraint)) { 
                	Log.i(TAG, "所需添加的值"+value.toString());
                    values.add(value);  
                }
            }
            results.values = values;  
            results.count = values.size();
            Log.i("TAG", "FilterResults.......,reulstvalues:"+values+"resultsCount"+values.size());
            return results;  
        }  
  
        @Override  
        protected void publishResults(CharSequence constraint,
                FilterResults results) {
        	Log.e("publishResults","publishResults");
            mObjects = (List<T>) results.values;
            if (results.count > 0) {
            	for(T f:mObjects){
            		Log.e("返回的值是", f+"");
            	}
                notifyDataSetChanged();
            } else {
            	Log.e("返回的值是", "空值");
                notifyDataSetInvalidated(); 
            }  
        }  
    }  
}  