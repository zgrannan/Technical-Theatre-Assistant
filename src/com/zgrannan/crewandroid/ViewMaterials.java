package com.zgrannan.crewandroid;

import pl.polidea.sectionedlist.R;
import pl.polidea.sectionedlist.SectionListAdapter;
import pl.polidea.sectionedlist.SectionListItem;
import pl.polidea.sectionedlist.SectionListView;

import com.zgrannan.crewandroid.Consumables.Material;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * This class allows the user to view information about the materials available.
 * 
 * @author Zack Grannan
 * @version 0.97
 * 
 */
public class ViewMaterials extends Activity {
	
	private class StandardArrayAdapter extends ArrayAdapter<SectionListItem> {

        private final SectionListItem[] items;

        public StandardArrayAdapter(final Context context,
                final int textViewResourceId, final SectionListItem[] items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(final int position, final View convertView,
                final ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                final LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.material_list_view, null);
            }
            final SectionListItem currentItem = items[position];
            if (currentItem != null) {
                final TextView textView = (TextView) view
                        .findViewById(R.id.material_text_view);
                if (textView != null) {
                    textView.setText(currentItem.item.toString());
                }
                textView.setOnClickListener(new OnClickListener(){

    				@Override
    				public void onClick(View v) {
    					((Material)currentItem.item).showDialog(ViewMaterials.this);
    					
    				}
                });
            
            }
            return view;
        }
    }
	
    private StandardArrayAdapter arrayAdapter;

    private SectionListAdapter sectionAdapter;

    private SectionListView listView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		final Activity activity = this;
		super.onCreate(savedInstanceState);
		
		SectionListItem[] materials = new SectionListItem[Consumables.woodstickForManage.length + Consumables.sheetForManage.length];
		for (int i = 0; i < Consumables.woodstickForManage.length; i ++){
			materials[i] = new SectionListItem(Consumables.woodstickForManage[i],getString(R.string.frame_materials));
		}
		for (int i = 0; i < Consumables.sheetForManage.length; i++){
			materials[i + Consumables.woodstickForManage.length] = new SectionListItem(Consumables.sheetForManage[i],getString(R.string.lid_materials));
		}
		 setContentView(R.layout.materials);
	    arrayAdapter = new StandardArrayAdapter(this, R.id.material_text_view,
	                materials);
	    sectionAdapter = new SectionListAdapter(getLayoutInflater(),
	                arrayAdapter);
	    listView = (SectionListView) findViewById(getResources().getIdentifier(
	                "section_list_view", "id",
	                this.getClass().getPackage().getName()));
	    
	    
	    listView.setAdapter(sectionAdapter);
	    
	}
}
