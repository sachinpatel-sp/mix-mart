package com.mixmart.mixmart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static com.mixmart.mixmart.DeliveryActivity.SELECT_ADDRESS;
import static com.mixmart.mixmart.MyAccountFragment.MANAGE_ADDRESS;
import static com.mixmart.mixmart.MyAddressesActivity.refreshItem;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.ViewHolder> {

    private List<AddressesModel> addressesModelList;
    private int MODE;
    private int preSelectedPosition;
    public AddressesAdapter(List<AddressesModel> addressesModelList,int MODE) {
        this.addressesModelList = addressesModelList;
        this.MODE = MODE;
        preSelectedPosition = DBQueries.selectedAddress;
    }

    @NonNull
    @Override
    public AddressesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addresses_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressesAdapter.ViewHolder holder, int position) {
         String name = addressesModelList.get(position).getFullName();
        String address = addressesModelList.get(position).getAddress();
        String pin = addressesModelList.get(position).getPinCode();
        Boolean selected =addressesModelList.get(position).getSelected();
        holder.setData(name,address,pin,selected,position);
    }

    @Override
    public int getItemCount() {
        return addressesModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
       private TextView fullName;
        private TextView address;
        private TextView pinCode;
        private ImageView icon;
        private LinearLayout optionContainer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            pinCode = itemView.findViewById(R.id.pin_code);
            icon = itemView.findViewById(R.id.icon_view);
            optionContainer = itemView.findViewById(R.id.option_container);
        }

        private void setData(String name, String UserAddress, String userPinCode, Boolean selected, final int position){
            fullName.setText(name);
            address.setText(UserAddress);
            pinCode.setText(userPinCode);

            if(MODE == SELECT_ADDRESS){
                icon.setImageResource(R.drawable.ic_check);
                if(selected){
                    icon.setVisibility(View.VISIBLE);
                    preSelectedPosition = position;
                }else{
                    icon.setVisibility(View.GONE);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (preSelectedPosition != position) {
                            addressesModelList.get(position).setSelected(true);
                            addressesModelList.get(preSelectedPosition).setSelected(false);
                            refreshItem(position, preSelectedPosition);
                            preSelectedPosition = position;
                            DBQueries.selectedAddress = position;
                        }
                    }
                });
            }else if(MODE == MANAGE_ADDRESS){
                  optionContainer.setVisibility(View.GONE);
                  icon.setImageResource(R.drawable.ic_more_vertdp);
                  icon.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          optionContainer.setVisibility(View.VISIBLE);
                          refreshItem(preSelectedPosition,preSelectedPosition);
                          preSelectedPosition  = position;
                      }
                  });
                  itemView.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          refreshItem(preSelectedPosition,preSelectedPosition);
                          preSelectedPosition = -1;
                      }
                  });
            }
        }
    }
}
