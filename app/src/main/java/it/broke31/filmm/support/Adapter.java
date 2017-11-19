package it.broke31.filmm.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.broke31.filmm.R;


public class Adapter extends ArrayAdapter<RowItem>  {
    private List<RowItem> textAndPhoto;
    private Context context;

    public Adapter(Context context, int resourceId, ArrayList<RowItem> textAndPhoto) {
        super(context, resourceId, textAndPhoto);
        this.textAndPhoto = textAndPhoto;
        this.context = context;
    }

    @Override
    public int getCount() {
        return textAndPhoto.size();
    }

    @Nullable
    @Override
    public RowItem getItem(int position) {
        return textAndPhoto.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        Holder holder;
        if (view == null) {
            holder = new Holder();
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adapter, null, true);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.setTextView((TextView) view.findViewById(R.id.textView1));
        holder.getTextView().setText(textAndPhoto.get(position).getTitle());
        holder.setImageView((ImageView) view.findViewById(R.id.imageView));
        holder.getImageView().setImageDrawable(textAndPhoto.get(position).getImageId().getDrawable());
        return view;
    }
    public void clearAll(){
        textAndPhoto.clear();
    }

}

class Holder {
    private TextView textView;
    private ImageView imageView;

    Holder() {
    }

    TextView getTextView() {
        return textView;
    }

    void setTextView(TextView textView) {
        this.textView = textView;
    }

    ImageView getImageView() {
        return imageView;
    }

    void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}






