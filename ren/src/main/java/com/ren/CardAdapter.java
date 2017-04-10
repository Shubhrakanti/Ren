package com.ren;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jensdriller.libs.undobar.UndoBar;

import java.util.Collections;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {
    private List<Card> cards = Collections.emptyList();

    // Adapter's row layout style
    private final int layoutStyle;
    private LayoutInflater layoutInflater;

    private Context mContext;

    private CardAdapter.ClickListener clickListener; // Implement the interface

    public CardAdapter(Context context, int layoutStyle) {
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
        this.layoutStyle = layoutStyle;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Decide which custom row layout to use
        View view;
        switch( layoutStyle ) {
            case TabFragment.RECEIVED_TAB_INT:
                // The root the custom layout
                view = layoutInflater.inflate(R.layout.custom_row, parent, false);
                break;
            case TabFragment.SAVED_TAB_INT:
                // The root the custom layout
                view = layoutInflater.inflate(R.layout.custom_row, parent, false);
                break;
            /*case TabFragment.IGNORED_TAB_INT:
                view = layoutInflater.inflate(R.layout.ignored_row, parent, false );
                break;*/
            case TabFragment.HOME_TAB_INT:
                // The root the custom layout
                view = layoutInflater.inflate(R.layout.custom_row, parent, false);
                break;
            case TabFragment.MY_CARD_TAB_INT:
                // The root the custom layout
                view = layoutInflater.inflate(R.layout.custom_row, parent, false);
                break;
            default:
                // Guarantees we atleast have some layout to inflate.
                view = layoutInflater.inflate(R.layout.custom_row, parent, false);
        }
        // Inflate, then pass
        return new MyViewHolder(view);
        //return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // Build the item
        Card currentCard = cards.get(position);
        holder.name.setText(currentCard.getmName());
        if (currentCard.getmPhotoEncoded().equals("Default")) {
            //Log.e("DefaultPhoto", "YES");
            holder.photo.setImageResource(R.drawable.usericon);
        } else {
            holder.photo.setImageBitmap(currentCard.decodeBase64());
        }
        //Log.e("Gender", current.mGender);
        switch (currentCard.getmGender()) {
            case "UNKNOWN":
                holder.gender.setImageResource(0);
                break;
            case "MALE":
                holder.gender.setImageResource(R.drawable.male);
                break;
            case "FEMALE":
                holder.gender.setImageResource(R.drawable.female);
                break;
        }
        if (currentCard.getmOther() != null && !currentCard.getmOther().equals("")) {
            holder.about.setText(currentCard.getmOther());
        } else if (currentCard.getmEmail() != null && !currentCard.getmEmail().equals("")) {
            holder.about.setText(currentCard.getmEmail());
        } else if (currentCard.getmPhone() != null && !currentCard.getmPhone().equals("")) {
            holder.about.setText(currentCard.getmPhone());
        } else {
            holder.about.setText("");
        }
        //Log.e("For Card", currentCard.mName);
        //Log.e("For Card", holder.about.getText().toString());
        if( holder.starButton != null ) {
            if (currentCard.ismSaved()) {
                holder.starButton.setImageResource(R.drawable.star_filled);
            } else {
                holder.starButton.setImageResource(R.drawable.star_outline);
            }
        }
    }

    public void setCardList(List<Card> cardsList) {
        cards = cardsList;
        Collections.sort(cards);

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, UndoBar.Listener {
        TextView name;
        ImageView photo;
        ImageView gender;
        TextView about;
        ImageButton starButton,
                    ignoreButton;
//                    unignoreButton;
        View itemFrame;
        Card clickedCard;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.itemName);
            photo = (ImageView) itemView.findViewById(R.id.itemIcon);
            gender = (ImageView) itemView.findViewById(R.id.itemGender);

            about = (TextView) itemView.findViewById(R.id.itemAboutMeOrEmailOrPhone);
            starButton = (ImageButton) itemView.findViewById(R.id.starButton);
            ignoreButton = (ImageButton) itemView.findViewById(R.id.ignoreButton);
            itemFrame = itemView.findViewById(R.id.item_frame);
            if( starButton != null ) { starButton.setOnClickListener(this); }
            if( ignoreButton != null ) { ignoreButton.setOnClickListener(this); }
//            if( unignoreButton != null ) { unignoreButton.setOnClickListener(this); }
            itemFrame.setOnClickListener(this);
            about.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (starButton != null && view.getId() == starButton.getId()) {
                clickedCard = cards.get(getAdapterPosition());
                if (!clickedCard.ismSaved()) {
                    SyncService.addSaved(clickedCard);
                    SyncService.removeReceivedCard(clickedCard);
                    //Toast.makeText(view.getContext(), view.getResources().getString(R.string.card_saved), Toast.LENGTH_SHORT).show();
                } else {
                    SyncService.removeSaved(clickedCard);
                    SyncService.addNewCard( clickedCard );

                    new UndoBar.Builder((Activity) view.getContext())
                            .setMessage(view.getContext().getResources().getString(R.string.card_removed))
                            .setListener(this)
                            .show();
                }

            } else if( ignoreButton != null && view.getId() == ignoreButton.getId() ) {
                clickedCard = cards.get(getAdapterPosition());

                // Ignore confirmation
                AlertDialog.Builder ignoreConfirmationBuilder = new AlertDialog.Builder( view.getContext() );
                ignoreConfirmationBuilder.setTitle(R.string.ignore_dialog_title );
                ignoreConfirmationBuilder.setMessage( R.string.ignore_dialog_message );
                ignoreConfirmationBuilder.setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
                   @Override
                    public void onClick( DialogInterface dialog, int id ) {
                       SyncService.removeSaved(clickedCard);
                       SyncService.removeReceivedCard(clickedCard);

                       // Ignore card
                       SyncService.addIgnoredCard( clickedCard );
                   }
                });
                ignoreConfirmationBuilder.setNegativeButton( "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int id ) {
                        // Do nothing
                    }
                });

                AlertDialog ignoreConfirmationDialog = ignoreConfirmationBuilder.create();
                ignoreConfirmationDialog.show();
            }
//             else if( unignoreButton != null && view.getId() == unignoreButton.getId() ) {
//                clickedCard = cards.get( getAdapterPosition() );
//                SyncService.removeIgnoredCard( clickedCard );
//                SyncService.addNewCard( clickedCard );
//            }
            else {
                if (clickListener != null) {
                    // On card activity click update all saved users.
//                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( mContext );
//                    BackgroundConn bckConn = new BackgroundConn( mContext );
//                    bckConn.execute( BackgroundConn.OBTAIN_SAVED_USERS, sp.getString("Login uname", "") );

                    Card clickedCard = cards.get(getAdapterPosition());
                    clickListener.itemClicked(view, clickedCard);
                }
            }
        }

        @Override
        public void onHide() {

        }

        @Override
        public void onUndo(Parcelable parcelable) {
            SyncService.addSaved(clickedCard);
        }
    }

    public interface ClickListener {
        //public void itemClicked (View view, int position);
        void itemClicked(View view, Card card);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

}
