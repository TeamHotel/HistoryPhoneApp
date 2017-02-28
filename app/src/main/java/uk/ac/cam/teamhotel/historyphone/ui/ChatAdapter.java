package uk.ac.cam.teamhotel.historyphone.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;
import uk.ac.cam.teamhotel.historyphone.artifact.ArtifactCache;
import uk.ac.cam.teamhotel.historyphone.database.ChatMessage;
import uk.ac.cam.teamhotel.historyphone.utils.StoreBitmapUtility;
import uk.ac.cam.teamhotel.historyphone.utils.TimeStampHelper;

public class ChatAdapter extends ArrayAdapter<ChatMessage> {

    public ChatAdapter(Context context, List<ChatMessage> messages) {
        // Resource ID = 0 as custom Adapter already handles this
        super(context, 0, messages);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        // Get the data item for this position.
        ChatMessage chatMessage = getItem(position);

        // Get the type of the layout
        int type = getItemViewType(position);

        // Check if an existing view is being reused, otherwise inflate the view.
        if (view == null) {
            if(type ==0) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_message_reply, parent, false);
            }else{
                view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_message_user, parent, false);
            }
        }

        // Lookup view for data population.
        ImageView id_image = (ImageView) view.findViewById(R.id.id_image);
        TextView message_text = (TextView) view.findViewById(R.id.chat_message);
        TextView timestamp = (TextView) view.findViewById(R.id.timestamp);

        // Populate the data into the template view using the data object.
        {
            // Set parameters of artifact tile.
            message_text.setText(chatMessage.getMessage_text());
            timestamp.setText(TimeStampHelper.formatTimeStamp(chatMessage.getTimestamp()));
            if(type ==0){

                // Format artifact image.
                Bitmap image = StoreBitmapUtility.loadImageFromStorage(chatMessage.getUuid(), getContext().getApplicationContext());
                if(image != null) {
                    id_image.setImageBitmap(image);
                }else {
                    // Otherwise, fall back on the launcher icon.
                    id_image.setImageBitmap(BitmapFactory.decodeResource(view.getResources(),
                            R.mipmap.ic_launcher));
                }

            }else{
                //TODO: add a different user image maybe
                id_image.setImageBitmap(BitmapFactory.decodeResource(view.getResources(), R.mipmap.ic_launcher));
            }

        }

        // Return the completed view to render on screen.
        return view;
    }

    // Override this method so that we can have two custom list_item_nearby layout files
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    // Override this method so that we can have two custom list_item_nearby layout files
    @Override
    public int getItemViewType(int position) {
        // Get corresponding data item
        ChatMessage chatMessage = getItem(position);

        // Return the type of the layout, depending on whether the message is a sent or received.
        if(chatMessage.isFrom_user() == 0){
            return 0;
        }else{
            return 1;
        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
