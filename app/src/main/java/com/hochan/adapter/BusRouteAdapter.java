package com.hochan.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.hochan.route.SchemeBusStep;
import com.hochan.util.AMapUtil;
import com.hochan.yueqiu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/5.
 */
public class BusRouteAdapter implements ExpandableListAdapter {

    private BusRouteResult mBusRouteResult;
    private Context mContext;
    private ArrayList<BusPath> mBusPaths;

    public BusRouteAdapter(Context context, BusRouteResult busRouteResult){
        this.mBusPaths = (ArrayList<BusPath>) busRouteResult.getPaths();
        this.mContext = context;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getGroupCount() {
        //Toast.makeText(mContext, "mBusPaths.size() "+mBusPaths.size(), Toast.LENGTH_LONG).show();
        return mBusPaths.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //Toast.makeText(mContext, "getGroup(groupPosition).size() "+getGroup(groupPosition).size(), Toast.LENGTH_LONG).show();
        return getGroup(groupPosition).size();
    }

    @Override
    public List<SchemeBusStep> getGroup(int groupPosition) {
        List<SchemeBusStep> busStepList = new ArrayList<SchemeBusStep>();
        SchemeBusStep start = new SchemeBusStep(null);
        start.setStart(true);
        busStepList.add(start);
        for (BusStep busStep : mBusPaths.get(groupPosition).getSteps()) {
            if (busStep.getWalk() != null) {
                SchemeBusStep walk = new SchemeBusStep(busStep);
                walk.setWalk(true);
                busStepList.add(walk);
            }
            if (busStep.getBusLine() != null) {
                SchemeBusStep bus = new SchemeBusStep(busStep);
                bus.setBus(true);
                busStepList.add(bus);
            }
        }
        SchemeBusStep end = new SchemeBusStep(null);
        end.setEnd(true);
        busStepList.add(end);
        return busStepList;
    }

    @Override
    public List<SchemeBusStep> getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        RouteGroupHolder holder = null;
        if (convertView == null) {
            holder = new RouteGroupHolder();
            convertView = View.inflate(mContext, R.layout.item_bus_result, null);
            holder.title = (TextView) convertView.findViewById(R.id.bus_path_title);
            holder.des = (TextView) convertView.findViewById(R.id.bus_path_des);
            convertView.setTag(holder);
        } else {
            holder = (RouteGroupHolder) convertView.getTag();
        }

        final BusPath item = mBusPaths.get(groupPosition);
        holder.title.setText(AMapUtil.getBusPathTitle(item));
        holder.des.setText(AMapUtil.getBusPathDes(item));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        RouteStepHolder holder = null;
        if (convertView == null) {
            holder = new RouteStepHolder();
            convertView = View.inflate(mContext, R.layout.item_bus_segment, null);
            holder.parent = (RelativeLayout) convertView
                    .findViewById(R.id.bus_item);
            holder.busLineName = (TextView) convertView
                    .findViewById(R.id.bus_line_name);
            holder.busDirIcon = (ImageView) convertView
                    .findViewById(R.id.bus_dir_icon);
            holder.busStationNum = (TextView) convertView
                    .findViewById(R.id.bus_station_num);
            holder.busExpandImage = (ImageView) convertView
                    .findViewById(R.id.bus_expand_image);
            holder.busDirUp = (ImageView) convertView
                    .findViewById(R.id.bus_dir_icon_up);
            holder.busDirDown = (ImageView) convertView
                    .findViewById(R.id.bus_dir_icon_down);
            holder.splitLine = (ImageView) convertView
                    .findViewById(R.id.bus_seg_split_line);
            holder.expandContent = (LinearLayout) convertView
                    .findViewById(R.id.expand_content);
            convertView.setTag(holder);
        } else {
            holder = (RouteStepHolder) convertView.getTag();
        }

        List<SchemeBusStep> busStepList = getGroup(groupPosition);
        final SchemeBusStep item = busStepList.get(childPosition);
        if (childPosition == 0) {
            holder.busDirIcon.setImageResource(R.drawable.dir_start);
            holder.busLineName.setText("出发");
            holder.busDirUp.setVisibility(View.INVISIBLE);
            holder.busDirDown.setVisibility(View.VISIBLE);
            holder.splitLine.setVisibility(View.GONE);
            holder.busStationNum.setVisibility(View.GONE);
            holder.busExpandImage.setVisibility(View.GONE);
            holder.parent.setOnClickListener(null);
            return convertView;
        } else if (childPosition == busStepList.size() - 1) {
            holder.busDirIcon.setImageResource(R.drawable.dir_end);
            holder.busLineName.setText("到达终点");
            holder.busDirUp.setVisibility(View.VISIBLE);
            holder.busDirDown.setVisibility(View.INVISIBLE);
            holder.busStationNum.setVisibility(View.INVISIBLE);
            holder.busExpandImage.setVisibility(View.INVISIBLE);
            holder.parent.setOnClickListener(null);
            return convertView;
        } else {
            if (item.isWalk() && item.getWalk() != null) {
                holder.busDirIcon.setImageResource(R.drawable.dir13);
                holder.busDirUp.setVisibility(View.VISIBLE);
                holder.busDirDown.setVisibility(View.VISIBLE);
                holder.busLineName.setText("步行"
                        + (int) item.getWalk().getDistance() + "米");
                holder.busStationNum.setVisibility(View.GONE);
                holder.busExpandImage.setVisibility(View.GONE);
                holder.parent.setOnClickListener(null);
                return convertView;
            }else if (item.isBus() && item.getBusLines().size() > 0) {
                holder.busDirIcon.setImageResource(R.drawable.dir14);
                holder.busDirUp.setVisibility(View.VISIBLE);
                holder.busDirDown.setVisibility(View.VISIBLE);
                holder.busLineName.setText(item.getBusLines().get(0).getBusLineName());
                //holder.busStationNum.setVisibility(View.VISIBLE);
                //holder.busStationNum
                //        .setText((item.getBusLines().get(0).getPassStationNum() + 1) + "站");
                //Toast.makeText(mContext, "站:"+(item.getBusLines().get(0).getPassStationNum()+1), Toast.LENGTH_LONG).show();
                //holder.busExpandImage.setVisibility(View.VISIBLE);
                //ArrowClick arrowClick = new ArrowClick(holder, item);
                //holder.parent.setTag(childPosition);
                //holder.parent.setOnClickListener(arrowClick);
                return convertView;
            }
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }

    class RouteGroupHolder{
        TextView title;
        TextView des;
    }

    class RouteStepHolder{
        public RelativeLayout parent;
        TextView busLineName;
        ImageView busDirIcon;
        TextView busStationNum;
        ImageView busExpandImage;
        ImageView busDirUp;
        ImageView busDirDown;
        ImageView splitLine;
        LinearLayout expandContent;
        boolean arrowExpend = false;
    }

    private class ArrowClick implements View.OnClickListener {
        private RouteStepHolder mHolder;
        private BusStep mItem;

        public ArrowClick(final RouteStepHolder holder, final BusStep item) {
            mHolder = holder;
            mItem = item;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int position = Integer.parseInt(String.valueOf(v.getTag()));
            //mItem = mBusStepList.get(position);
            if (mHolder.arrowExpend == false) {
                mHolder.arrowExpend = true;
                //mHolder.busExpandImage
                //        .setImageResource(R.drawable.up);
                addBusStation(mItem.getBusLine().getDepartureBusStation());
                for (BusStationItem station : mItem.getBusLine()
                        .getPassStations()) {
                    addBusStation(station);
                }
                addBusStation(mItem.getBusLine().getArrivalBusStation());

            } else {
                mHolder.arrowExpend = false;
                mHolder.busExpandImage
                        .setImageResource(R.drawable.down);
                mHolder.expandContent.removeAllViews();
            }

        }

        private void addBusStation(BusStationItem station) {
            LinearLayout ll = (LinearLayout) View.inflate(mContext,
                    R.layout.item_bus_segment_ex, null);
            TextView tv = (TextView) ll
                    .findViewById(R.id.bus_line_station_name);
            tv.setText(station.getBusStationName());
            mHolder.expandContent.addView(ll);
        }
    }
}
