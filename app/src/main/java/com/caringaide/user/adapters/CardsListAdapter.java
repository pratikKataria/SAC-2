package com.caringaide.user.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.caringaide.user.R;
import com.caringaide.user.interfaces.RefreshListAdapterListener;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.model.Cards;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.CommonUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.caringaide.user.utils.CommonUtilities.getMaskedString;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.showAlertMsg;
import static com.caringaide.user.utils.CommonUtilities.toastShort;
import static com.caringaide.user.utils.card.CardValidator.cardImageMap;


public class CardsListAdapter extends ArrayAdapter<Cards>  {
    private List<Cards> cardsList;
    private LayoutInflater layoutInflater;
    private int cardResource;
    private ListView cardListView;
    RefreshListAdapterListener refreshListAdapterListener;

    public CardsListAdapter(Context context, int resource, List<Cards> objects,
                            ListView cardListView,RefreshListAdapterListener refreshListAdapterListener) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.cardsList = objects;
        this.cardResource  = resource;
        this.cardListView = cardListView;
        this.refreshListAdapterListener = refreshListAdapterListener;

    }

    @Override
    public Cards getItem(int position) {
        return cardsList.get(position);
    }

    @Override
    public int getCount() {
        return cardsList.size();
    }

    static class ViewHolder {
        ImageView viewCard;
        TextView cardNumberTxt;
        ImageButton deletecardBtn;

    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        View row =  convertView;
        if (row == null) {
            viewHolder = new ViewHolder();
            row = layoutInflater.inflate(cardResource, null);
            viewHolder.viewCard = row
                    .findViewById(R.id.ben_card_image);
            viewHolder.cardNumberTxt =  row
                    .findViewById(R.id.ben_card_number);
            viewHolder.deletecardBtn = row
                    .findViewById(R.id.delete_ben_card);
            if (cardsList.size()<2){
                viewHolder.deletecardBtn.setVisibility(View.GONE);
            }
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }
        final Cards beneficiaryCardData = getItem(position);
        if (beneficiaryCardData != null) {
            viewHolder.viewCard.setImageResource(cardImageMap.get(beneficiaryCardData.getCardCompany()));
            viewHolder.cardNumberTxt.setText(beneficiaryCardData.getCardNumber());
            viewHolder.deletecardBtn.setOnClickListener(new SingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    //code for deleting card
                    RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                        @Override
                        public void onResponse(RemoteResponse remoteResponse) {
                            handleCardsResponse(remoteResponse);
                        }
                    },getContext(),null);
                    UserServiceHandler.deleteBeneficiaryCard(beneficiaryCardData.getCardId(),requestParams);
                }
            });
        }else{

        }
        return row;
    }

    private void handleCardsResponse(RemoteResponse remoteResponse) {
        Context context = getContext();
        String customErrorMsg = context.getString(R.string.card_delete_failed);
        if (null == remoteResponse) {
            toastShort(customErrorMsg);
            return;
        } else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!isErrorsFromResponse(context, remoteResponse)) {
                    JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                    showAlertMsg(context.getString(R.string.app_name), context.getString(R.string.card_deleted_success));
                    refreshListAdapterListener.onDataRefresh();
                }else {
                    toastShort(customErrorMsg);
                }
            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }

}
