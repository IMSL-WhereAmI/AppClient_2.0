package com.ng.anthony.mininavigationdrawer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Anthony on 16-01-25.
 */
public class MasterFragment extends ListFragment {

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_master, container);

        setListAdapter(new MenuListAdapter(R.layout.row_menu_action_item, getActivity(), MenuActionItem.values()));

//        FragmentManager manager = getFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//
//        MapFragment2 mapFragment2 = new MapFragment2();
//        transaction.replace(R.id.map_frame, mapFragment2, "mapFragment2");
//        ((MainActivity) getActivity()).setMapIndex("02");
//        transaction.commit();

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if(position == MenuActionItem.ITEM1.ordinal()) {
            MapFragment1 mapFragment1 = new MapFragment1();
            transaction.replace(R.id.map_frame, mapFragment1, "mapFragment1");
            ((MainActivity) getActivity()).setMapIndex("01");

            Log.d("TAG", "onListItemClick: "+"MapFragment1");
        }else if (position == MenuActionItem.ITEM2.ordinal()) {
            MapFragment2 mapFragment2 = new MapFragment2();
            transaction.replace(R.id.map_frame, mapFragment2, "mapFragment2");
            Log.d("TAG", "onListItemClick: "+"MapFragment2");
            ((MainActivity) getActivity()).setMapIndex("02");
        }

        String tCode = ((MainActivity) getActivity()).gettCode();
        ((MainActivity) getActivity()).stopCollector(tCode);
        ((MainActivity) getActivity()).changemap();

        transaction.commit();

    }

    //    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //定义一个数组
//        List<String> data = new ArrayList<String>();
//        for (int i = 0; i < 30; i++) {
//            data.add("smyh" + i);
//        }
//        //将数组加到ArrayAdapter当中
//        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, data);
//        //绑定适配器时，必须通过ListFragment.setListAdapter()接口，而不是ListView.setAdapter()或其它方法
//        setListAdapter(adapter);
//    }
}
