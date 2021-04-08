package com.caringaide.user.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.caringaide.user.R;
import com.caringaide.user.activities.AddCardActivity;
import com.caringaide.user.activities.BookNowActivity;
import com.caringaide.user.activities.UserHomeActivity;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.fragments.FavoriteBuddyListFragment;
import com.caringaide.user.fragments.ListBeneficiaryFragment;
import com.caringaide.user.interfaces.ChangeFragmentListener;
import com.caringaide.user.interfaces.RefreshListAdapterListener;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.model.Beneficiary;
import com.caringaide.user.model.Cards;
import com.caringaide.user.model.Zipcodes;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.PatternUtil;
import com.caringaide.user.utils.SharedPrefsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.caringaide.user.utils.BuddyConstants.ADDRESS_MIN_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.FULL_NAME_MAX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.FULL_NAME_MIN_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.MOBILE_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.REQUIREMENT_MIN_LENGTH;
import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.calculateAge;
import static com.caringaide.user.utils.CommonUtilities.capitalize;
import static com.caringaide.user.utils.CommonUtilities.getDateAsString;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;
import static com.caringaide.user.utils.CommonUtilities.notifyWithVibration;
import static com.caringaide.user.utils.CommonUtilities.setToCamelCase;
import static com.caringaide.user.utils.CommonUtilities.showAlertMsg;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

public class ListBeneficiaryAdapter extends ArrayAdapter<Beneficiary> {

    private static final String TAG = "ListBenAdapter";
    private Context context;
    private int resource;
    private ArrayList<Beneficiary> beneficiaryList;
    private Map<String, String> benTypeMap = new HashMap<>();
    private Fragment fragment;
    private RefreshListAdapterListener refreshListAdapterListener;
    private ChangeFragmentListener changeListener;
    private LayoutInflater inflater;

    public ListBeneficiaryAdapter(Context context, int resource, List<Beneficiary> objects, Map<String, String> benTypes,
                                  ListView beneficiaryListView, ListBeneficiaryFragment bookNowFragment,
                                  RefreshListAdapterListener listener, ChangeFragmentListener changeListeenr) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.benTypeMap = benTypes;
        beneficiaryList = (ArrayList<Beneficiary>) objects;
        this.resource = resource;
        this.fragment = bookNowFragment;
        this.refreshListAdapterListener = listener;
        this.changeListener = changeListeenr;

    }

    @Override
    public Beneficiary getItem(int position) {
        return beneficiaryList.get(position);
    }

    @Override
    public int getCount() {
        return beneficiaryList.size();
    }

    static class ViewHolder {
        ImageView beneficiaryGenderview;
        TextView benNameTextView, benRequirements, benRelation, benZipcode, benDob, noCardTextView;//serviceTextView,
        ImageButton editBeneficiaryBtn, deleteBeneficiaryBtn;
        Button bookNowBtn;
        TextView chooseBuddyBtn;
        LinearLayout cardViewConatiner;
        Button showCardBtn;

    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        View row = convertView;
        if (row == null) {
            viewHolder = new ViewHolder();
            row = inflater.inflate(resource, null);
            viewHolder.beneficiaryGenderview = row
                    .findViewById(R.id.ben_gender_avatar);
            viewHolder.editBeneficiaryBtn = row
                    .findViewById(R.id.edit_beneficiary_button);
            viewHolder.editBeneficiaryBtn.setTag(position);
            viewHolder.deleteBeneficiaryBtn = row
                    .findViewById(R.id.delete_beneficiary);
            viewHolder.deleteBeneficiaryBtn.setTag(position);
            viewHolder.benNameTextView = row
                    .findViewById(R.id.list_beneficiary_name);
            viewHolder.benDob = row
                    .findViewById(R.id.ben_dob_result);
            viewHolder.benZipcode = row
                    .findViewById(R.id.ben_zipcode_result);
            viewHolder.benRelation = row
                    .findViewById(R.id.ben_relation_result);
            viewHolder.benRequirements = row
                    .findViewById(R.id.ben_requirement_result);
            viewHolder.bookNowBtn = row
                    .findViewById(R.id.ben_book_service_btn);
            viewHolder.bookNowBtn.setTag(position);
            viewHolder.cardViewConatiner = row
                    .findViewById(R.id.card_view_container);
            viewHolder.noCardTextView = row
                    .findViewById(R.id.info_no_payment);
            viewHolder.chooseBuddyBtn = row
                    .findViewById(R.id.ben_list_choose_buddy_btn);
            viewHolder.showCardBtn = row
                    .findViewById(R.id.btn_show_card_detail);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }

        Animation leftAnimation = AnimationUtils.loadAnimation(context, R.anim.animate_rigth_in);
        leftAnimation.setDuration(1000);
        final Beneficiary beneficiaryData = getItem(position);
        viewHolder.cardViewConatiner.setVisibility(View.VISIBLE);
        viewHolder.benDob.setText(getDateAsString(beneficiaryData.getBenDob()).concat(" (")
                .concat(String.valueOf(calculateAge(getDateAsString(beneficiaryData.getBenDob())))).concat(")"));
        viewHolder.benZipcode.setText(beneficiaryData.getBenZipcode());
        viewHolder.benRequirements.setText(beneficiaryData.getBenComments());
        String relationWithUser = beneficiaryData.getBenRelation().isEmpty() ? benTypeMap.get(beneficiaryData.getBenType()) :
                beneficiaryData.getBenRelation();
        viewHolder.benRelation.setText(relationWithUser);
            viewHolder.benNameTextView.setText(setToCamelCase(beneficiaryData.getBenNickName()));
            viewHolder.deleteBeneficiaryBtn.setVisibility(View.VISIBLE);
        String benGender = beneficiaryData.getBenGender();
        if (null != benGender) {
            viewHolder.beneficiaryGenderview.setImageResource(CommonUtilities.getGenderImage(beneficiaryData.getBenGender()));
        }
        viewHolder.editBeneficiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditBeneficiaryDialog(beneficiaryData);
            }
        });
        viewHolder.deleteBeneficiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertAndAction(fragment.getActivity(), context.getString(R.string.delete_beneficiary_label),
                        context.getString(R.string.ask_delete_beneficiary), context.getString(R.string.ok), context.getString(R.string.cancel),
                        new AlertAction() {
                            @Override
                            public void positiveAction() {
                                RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                                    @Override
                                    public void onResponse(RemoteResponse remoteResponse) {
                                        handleDeletebeneficiaryData(remoteResponse,position);

                                    }
                                }, getContext(), null);
                                UserServiceHandler.deleteBeneficiary(beneficiaryData.getBenId(), requestParams);
                            }

                            @Override
                            public void negativeAction() {

                            }
                        });
            }
        });
        viewHolder.bookNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go book service
                Intent intent = new Intent(fragment.getActivity(), BookNowActivity.class);
                CommonUtilities.selectedBenId = beneficiaryData.getBenId();
                CommonUtilities.selectedserviceId = beneficiaryData.getBenServiceId();
                intent.putExtra("user_name", beneficiaryData.getBenNickName());
                fragment.startActivity(intent);
            }
        });
        if (!beneficiaryData.isHaveCard()) {
            viewHolder.noCardTextView.setAnimation(leftAnimation);
            viewHolder.noCardTextView.setVisibility(View.VISIBLE);
            viewHolder.bookNowBtn.setEnabled(false);
        } else {
            viewHolder.noCardTextView.setVisibility(View.GONE);
            viewHolder.bookNowBtn.setEnabled(true);
        }
        viewHolder.chooseBuddyBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //show favorite buddies
                CommonUtilities.selectedBenId = beneficiaryData.getBenId();
                FavoriteBuddyListFragment favoriteBuddyListFragment = new FavoriteBuddyListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("choose_context","listBeneficiaryFragment");
                favoriteBuddyListFragment.setArguments(bundle);
                changeListener.changeToTargetFragment(favoriteBuddyListFragment);
            }
        });
        viewHolder.showCardBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (!beneficiaryData.isHaveCard()) {
                    Intent intent = new Intent(fragment.getActivity(), AddCardActivity.class);
                    intent.putExtra("ben_id",beneficiaryData.getBenId());
                    fragment.startActivity(intent);
                }else {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BuddyApp.getCurrentActivity());
                    builder.setCustomTitle(View.inflate(fragment.getActivity(), R.layout.custom_header_layout_white, null));
                    builder.setView(View.inflate(fragment.getActivity(), R.layout.card_list_view_layout, null));
                    //builder.setMessage(message);
                    builder.setCancelable(false);
                    builder.setNegativeButton(fragment.getString(R.string.done), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }
                    });

                    final android.app.AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    ListView cardDetailListView = alertDialog.findViewById(R.id.card_listview_layout);
                    Button addCardButton = alertDialog.findViewById(R.id.add_new_card_btn);

                    //show cards. If 2 cards are there, add card button will be invisible
               /* View view = editBenDialog.findViewById(R.id.card_adapter_view);
                showCardDetailsLayout.setVisibility(View.VISIBLE);*/
                    ArrayList<Cards> cardList = beneficiaryData.getCardsList();
                    Log.d(TAG, "manageCard onClick: " + cardList.size());
                    if (cardList.size() > 0) {
//                    view.setVisibility(View.VISIBLE);
                        for (int i = 0; i < cardList.size(); i++) {
                            CardsListAdapter cardListAdapter = new CardsListAdapter(getContext(), R.layout.view_card_adapter_layout, cardList,
                                    cardDetailListView, new RefreshListAdapterListener() {
                                @Override
                                public void onDataRefresh() {
                                    alertDialog.dismiss();
                                    refreshListAdapterListener.onDataRefresh();
                                }
                            });
                            cardDetailListView.setAdapter(cardListAdapter);
                        }
                        if (cardList.size() == 2) {
                            addCardButton.setVisibility(View.GONE);
                        }
                    }
                    addCardButton.setOnClickListener(new SingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(fragment.getActivity(), AddCardActivity.class);
                            intent.putExtra("ben_id", beneficiaryData.getBenId());
                            fragment.startActivity(intent);
                        }
                    });
                }
            }
        });
        return row;
    }

    /**
     * edit client laert
     * @param beneficiary client details object
     */
    private void showEditBeneficiaryDialog(final Beneficiary beneficiary) {
//        final Zipcodes zipcodeData = SharedPrefsManager.getInstance().getZipcodeDetails(beneficiary.getBenZipcode());
        ArrayList<Zipcodes> zipcodesArrayList = SharedPrefsManager.getInstance().getZipcodeList(beneficiary.getBenZipcode());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(context.getString(R.string.edit_beneficiary_label));
        builder.setView(R.layout.edit_beneficiary_layout);
        final AlertDialog editBenDialog = builder.create();
        editBenDialog.setCancelable(false);
        editBenDialog.show();
        final EditText editBenName = editBenDialog.findViewById(R.id.edit_benif_name);
        final EditText editBenMobile = editBenDialog.findViewById(R.id.edit_benif_mobile);
        final EditText editBenDob = editBenDialog.findViewById(R.id.edit_benif_dob);
        final EditText editBenAddress = editBenDialog.findViewById(R.id.edit_benif_address);
        final Spinner editBenCity = editBenDialog.findViewById(R.id.edit_benif_city);
        final EditText editBenState = editBenDialog.findViewById(R.id.edit_benif_state);
        final EditText editBenZipcode = editBenDialog.findViewById(R.id.edit_benif_zipcode);
        final EditText editBenRequirements = editBenDialog.findViewById(R.id.edit_benif_requirements);
        final LinearLayout showCardDetailsLayout = editBenDialog.findViewById(R.id.list_buddy_card_layout);
        final Button addCardBtn = editBenDialog.findViewById(R.id.add_new_card_btn);
        final Button showCardBtn = editBenDialog.findViewById(R.id.btn_show_card_detail1);
        final ListView cardDetailListView = editBenDialog.findViewById(R.id.card_listview_layout);
        final TextView zipAvailableTextView = editBenDialog.findViewById(R.id.edit_client_area_serviceability);
//        cardDetailListView.setVisibility(View.GONE);
        editBenName.setText(beneficiary.getBenFullName());
        editBenMobile.setText(beneficiary.getBenMobile());
        editBenAddress.setText(beneficiary.getBenAddress());
        editBenDob.setText(getDateAsString(beneficiary.getBenDob()));
        if (zipcodesArrayList.size()>0) {
            editBenState.setText(zipcodesArrayList.get(0).getStateName());
            editBenZipcode.setText(zipcodesArrayList.get(0).getZipcode());
        }else{
            editBenState.setText("");
            editBenZipcode.setText("");

        }
        //populate city to the spinner
        final ArrayList<String> cityList = new ArrayList();
        for (Zipcodes zip : zipcodesArrayList) {
            if (beneficiary.getBenZipcode().equals(zip.getZipcode())) {
                cityList.add(zip.getCityName());
            }
        }
        ArrayAdapter<String> benCityAdapter = new ArrayAdapter<>(fragment.getActivity(),
                android.R.layout.simple_spinner_item, cityList);
        benCityAdapter.notifyDataSetChanged();
        editBenCity.setAdapter(benCityAdapter);
        editBenCity.setSelected(true);
        if (zipcodesArrayList.size()>0){
            for (int i=0;i<zipcodesArrayList.size();i++){
                Zipcodes zip= zipcodesArrayList.get(i);
                if (zip.getCityId().equals(beneficiary.getBenCityId())){
                    int spinnerPosition = benCityAdapter.getPosition(zip.getCityName());
                    editBenCity.setSelection(spinnerPosition);
                }
            }
        }

        addCardBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                editBenDialog.dismiss();
                Intent intent = new Intent(fragment.getActivity(), AddCardActivity.class);
                intent.putExtra("ben_id",beneficiary.getBenId());
                fragment.startActivity(intent);
               /* AddCardFragment cardFragment = new AddCardFragment();
                Bundle bundle = new Bundle();
                bundle.putString("ben_id",beneficiary.getBenId());
                cardFragment.setArguments(bundle);
                changeListener.changeToTargetFragment(cardFragment);*/
            }
        });
        showCardBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //show cards. If 2 cards are there, add card button will be invisible
                View view = editBenDialog.findViewById(R.id.card_adapter_view);
                showCardDetailsLayout.setVisibility(View.VISIBLE);
                ArrayList<Cards> cardList = beneficiary.getCardsList();
                Log.d(TAG, "manageCard onClick: "+cardList.size());
                if (cardList.size()>0){
                    view.setVisibility(View.VISIBLE);
                    for (int i = 0; i < cardList.size(); i++) {
                        CardsListAdapter cardListAdapter = new CardsListAdapter(getContext(), R.layout.view_card_adapter_layout, cardList,
                                cardDetailListView,refreshListAdapterListener);
                        cardDetailListView.setAdapter(cardListAdapter);
                    }
                    if (cardList.size()==2){
                        addCardBtn.setVisibility(View.GONE);
                    }
                }
            }
        });
        editBenZipcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

//                zipAvailable.setText("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isZipAvaialable = false;
                ArrayList<String> cityListForZip = new ArrayList<String>();
                ArrayAdapter<String> benCityAdapter = new ArrayAdapter<String>(fragment.getActivity(),
                        android.R.layout.simple_spinner_item, cityListForZip);
                benCityAdapter.notifyDataSetChanged();

                if (s.toString().trim().length() > 4 && s.toString().trim().length() < 7) {
                    ArrayList<Zipcodes> zipcodes = SharedPrefsManager.getInstance().getZipcodeList(s.toString().trim());
                    String zipcode = s.toString().trim();
                    for (Zipcodes zip : zipcodes) {
                        if (null == zip.getCityName()) {
                            showIsZipAvailable(false,zipAvailableTextView);
                            return;
                        }
                        cityListForZip.add(zip.getCityName());
                    }
                    for (Zipcodes zip : zipcodes) {
                        if (zipcode.equals(zip.getZipcode())) {
                            isZipAvaialable = true;
                            editBenState.setText(zip.getStateName());
                            editBenState.setError(null);
                            benCityAdapter.notifyDataSetChanged();
                            editBenCity.setAdapter(benCityAdapter);
                            editBenCity.setSelected(true);
                        }else{
                            isZipAvaialable = false;
                            editBenState.setText("");
                            cityList.clear();
                            cityListForZip.clear();
                            benCityAdapter.notifyDataSetChanged();
                        }
                    }//end for loop

                } else {
                    isZipAvaialable = false;
                    cityList.clear();
                    cityListForZip.clear();
                    benCityAdapter.notifyDataSetChanged();
                    editBenState.setText("");
                }
                showIsZipAvailable(isZipAvaialable,zipAvailableTextView);
            }
        });
        editBenRequirements.setText(beneficiary.getBenComments());
        Button cancelEditBtn = editBenDialog.findViewById(R.id.cancel_edit_ben);
        if (cancelEditBtn != null) {
            cancelEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editBenDialog.dismiss();
                }
            });
        }
        Button proceedReqBtn = editBenDialog.findViewById(R.id.submit_edit_ben);
        if (proceedReqBtn != null) {
            proceedReqBtn.setOnClickListener(new SingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    verifyAndSubmitBeneficaryData(editBenDialog, beneficiary.getBenId(),
                            editBenName, editBenMobile, editBenAddress,
                            editBenZipcode, editBenRequirements, editBenDob,
                            editBenCity, editBenState);
                }
            });
        }

    }

    /**
     * show the response of zipcode availability check
     * @param isZipAvaialable
     * @param isZipResponseTextView
     */
    private void showIsZipAvailable(boolean isZipAvaialable, TextView isZipResponseTextView){
        if (isZipAvaialable){
            isZipResponseTextView.setText(context.getString(R.string.servicable_area));
            isZipResponseTextView.setTextColor(context.getResources().getColor(R.color.text_green));

        }else {
            isZipResponseTextView.setText(context.getString(R.string.no_service_area));
            isZipResponseTextView.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
    }

    /**
     * validate and submit details to edit client
     * @param editBenDialog the alert dialog for  dismissing the dialog after response
     * @param benId the id of client
     * @param editBenName client name
     * @param editBenMobile client mobile
     * @param editBenAddress client address
     * @param editBenZipcode client zipcode
     * @param editBenRequirements client requirements
     * @param editBenDob client dob
     * @param editBenCity client city
     * @param editBenState client state
     */
    private void verifyAndSubmitBeneficaryData(final AlertDialog editBenDialog, final String benId, final EditText editBenName,
                                               final EditText editBenMobile, final EditText editBenAddress, final EditText editBenZipcode,
                                               final EditText editBenRequirements, final EditText editBenDob, Spinner editBenCity, EditText editBenState) {
        final Context context = getContext();
        String cityId = "";
        String stateId = "";
        if (editBenName.getText().toString().isEmpty()) {
            editBenName.setError(context.getString(R.string.ask_valid_fullname));
        } else if (editBenName.getText().toString().length()<FULL_NAME_MIN_LENGTH ||
                editBenName.getText().toString().length()>FULL_NAME_MAX_LENGTH ) {
            editBenName.setError(context.getString(R.string.ask_fullname_length)
                    .concat(String.valueOf(FULL_NAME_MAX_LENGTH)).concat(context.getString(R.string.and_label))
            .concat(String.valueOf(FULL_NAME_MAX_LENGTH)));
            notifyWithVibration(context);
        } else if (!CommonUtilities.checkStringData(editBenName)) {
//            editBenName.setError(context.getString(R.string.ask_valid_fullname));
            notifyWithVibration(context);
        } else if (!editBenName.getText().toString().matches(PatternUtil.fullNameRegex)) {
            editBenName.setError(context.getString(R.string.ask_valid_fullname));notifyWithVibration(context);
        } else if (editBenMobile.getText().toString().isEmpty()) {
            editBenMobile.setError(context.getString(R.string.ask_mobile));notifyWithVibration(context);
        } else if (editBenMobile.getText().toString().length()< MOBILE_LENGTH ||
                editBenMobile.getText().toString().length()< MOBILE_LENGTH) {
            editBenMobile.setError(context.getString(R.string.ask_mobile));notifyWithVibration(context);
        } else if (!editBenMobile.getText().toString().matches(PatternUtil.mobileWithCntryCodeRegex)) {
            editBenMobile.setError(context.getString(R.string.ask_mobile));notifyWithVibration(context);
        } else if (editBenAddress.getText().toString().isEmpty()||
                editBenAddress.getText().toString().length()<ADDRESS_MIN_LENGTH) {
            editBenAddress.setError(context.getString(R.string.ask_address));notifyWithVibration(context);
        } else if (!editBenAddress.getText().toString().matches(PatternUtil.address_pattern)) {
            editBenAddress.setError(context.getString(R.string.ask_address));notifyWithVibration(context);
        } else if (editBenZipcode.getText().toString().isEmpty()) {
            editBenZipcode.setError(context.getString(R.string.ask_zip));notifyWithVibration(context);
        } else if (!editBenZipcode.getText().toString().matches(PatternUtil.zipcode_pattern)) {
            editBenZipcode.setError(context.getString(R.string.ask_zip));notifyWithVibration(context);
        } else if (editBenCity.getSelectedItem().toString().isEmpty()) {
           toastShort(context.getString(R.string.ask_zip_for_city));notifyWithVibration(context);
        } else if (editBenState.getText().toString().isEmpty()) {
            editBenState.setError(context.getString(R.string.ask_zip_for_state));notifyWithVibration(context);
        }else if (!editBenRequirements.getText().toString().isEmpty()&&!editBenRequirements.getText().toString().matches(PatternUtil.additionalInfoRegex)){
            editBenRequirements.requestFocus();
            editBenRequirements.setError(context.getString(R.string.ask_requirements));notifyWithVibration(context);
        } else {
            String zipcode = editBenZipcode.getText().toString();
            ArrayList<Zipcodes> zipcodes = SharedPrefsManager.getInstance().getZipcodeList(zipcode);
            for (Zipcodes zipcodeData:zipcodes){
                //bcoz same zipcode can have multiple cities
//                final Zipcodes zipcodeData = SharedPrefsManager.getInstance().getZipcodeDetails(editBenZipcode.getText().toString());
                if (null==zipcodeData.getStateId()||null==zipcodeData.getCityId()){
                    editBenZipcode.setError(context.getString(R.string.ask_zip));notifyWithVibration(context);
                    return;
                }else{
                    if (editBenCity.getSelectedItem().toString().equals(zipcodeData.getCityName())){
                        cityId = zipcodeData.getCityId();
                        stateId = zipcodeData.getStateId();
                    }
                }
            }
            String finalCityId = cityId;//effectively final variable
            String finalStateId = stateId;//effectively final variable
            alertAndAction(fragment.getActivity(), context.getString(R.string.confirm_ben_edit_title),
                    context.getString(R.string.confirm_ben_edit_value), context.getString(R.string.ok),
                    context.getString(R.string.cancel), new AlertAction() {
                        @Override
                        public void positiveAction() {
                            String requirements = editBenRequirements.getText().toString().isEmpty()?
                                    context.getString(R.string.no_data):editBenRequirements.getText().toString();
                            RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                                @Override
                                public void onResponse(RemoteResponse remoteResponse) {
                                    handleEditBenResponse(remoteResponse, editBenDialog);
                                }
                            }, context, null);
                            Map<String, String> params = new HashMap<>();
                            params.put("nickname", editBenName.getText().toString());
                            params.put("mobile", editBenMobile.getText().toString());
                            params.put("address", editBenAddress.getText().toString());
                            params.put("zipcode", editBenZipcode.getText().toString());
                            params.put("city_id", finalCityId);
                            params.put("state_id", finalStateId);
                            params.put("dob", editBenDob.getText().toString().trim());
                            params.put("comments", requirements);
                            requestParams.setRequestParams(params);
                            UserServiceHandler.editBeneficiaries(benId, requestParams);
                        }

                        @Override
                        public void negativeAction() {

                        }
                    });
        }
    }

    /**
     * remote response after edit client
     * @param remoteResponse the servr response
     * @param editDialog the alert dialog for  dismissing the dialog after response
     */
    private void handleEditBenResponse(RemoteResponse remoteResponse, AlertDialog editDialog) {
        String customErrorMsg = context.getString(R.string.edit_buddies_failed);
        if (null == remoteResponse) {
            toastShort(context.getString(R.string.edit_buddies_failed));
            return;
        } else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!isErrorsFromResponse(context, remoteResponse)) {
                    JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
//                    toastLong(context.getString(R.string.edit_buddies_success));
                    showAlertMsg(context.getString(R.string.edit_buddies),context.getString(R.string.edit_buddies_success));
                    editDialog.dismiss();
                    refreshListAdapterListener.onDataRefresh();
                }
            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }

    /**
     * remote response of delete client remote call
     * @param remoteResponse
     * @param position
     */
    private void handleDeletebeneficiaryData(RemoteResponse remoteResponse, int position) {
        String customErrorMsg = context.getString(R.string.remove_benef_failed);
        if (null == remoteResponse) {
            toastShort(customErrorMsg);
        } else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!isErrorsFromResponse(context, remoteResponse)) {
                    JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                    beneficiaryList.remove(position);
                    if (beneficiaryList.size()==0){

                        alertAndAction(fragment.getActivity(), context.getString(R.string.app_name),
                                context.getString(R.string.remove_benef_success), context.getString(R.string.ok), null,
                                new AlertAction() {
                                    @Override
                                    public void positiveAction() {
                                        moveToHomeActivity();
                                    }

                                    @Override
                                    public void negativeAction() {

                                    }
                                });
                    }else{

                        showAlertMsg(context.getString(R.string.app_name), context.getString(R.string.remove_benef_success));
                    }
                    refreshListAdapterListener.onDataRefresh();
                }
            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }

    //choose buddy
/*    private void showBuddyList(){
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        builder.setTitle(context.getString(R.string.choose_buddy_txt));
        builder.setView(R.layout.choose_buddy_list_layout);
        final AlertDialog chooseBuddyDialog = builder.create();
        chooseBuddyDialog.setCancelable(false);
        chooseBuddyDialog.show();
        final LinearLayout chooseBuddyLayout = chooseBuddyDialog.findViewById(R.id.select_buddy_premium);
        final CardView listBuddy1;
        final CardView listBuddy2;
        final CardView listBuddy3;
        final CardView addNewBuddy;
        final TextView buddyName1;
        final TextView buddyName2;
        final TextView buddyName3;
        final TextView buddyPhone1;
        final TextView buddyPhone2;
        final TextView buddyPhone3;

    }*/
}
